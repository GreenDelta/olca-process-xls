package org.openlca.io.xls.process;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openlca.core.database.IDatabase;
import org.openlca.core.io.ImportLog;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.Process;
import org.openlca.core.model.ProcessDocumentation;
import org.openlca.core.model.Version;
import org.openlca.core.model.descriptors.ProcessDescriptor;
import org.openlca.jsonld.input.UpdateMode;
import org.openlca.util.Strings;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class XlsProcessReader {

	private final IDatabase db;
	private final ImportLog log;
	private UpdateMode updates = UpdateMode.NEVER;

	private XlsProcessReader(IDatabase db) {
		this.db = Objects.requireNonNull(db);
		this.log = new ImportLog();
	}

	public static XlsProcessReader of(IDatabase db) {
		return new XlsProcessReader(db);
	}

	public ImportLog log() {
		return log;
	}

	public XlsProcessReader withUpdates(UpdateMode mode) {
		if (mode != null) {
			this.updates = mode;
		}
		return this;
	}

	public Optional<Process> sync(File file) {
		try (var wb = WorkbookFactory.create(file)) {
			var info = readInfo(wb);
			if (info == null) {
				log.error("invalid workbook: " + file.getName());
				return Optional.empty();
			}
			var process = db.get(Process.class, info.refId);
			if (process != null) {
				if (skipUpdate(process, info))
					return Optional.of(process);
			} else {
				process = new Process();
			}

			// update info fields
			process.refId = info.refId;
			process.version = info.version;
			process.name = info.name;
			process.lastChange = info.lastChange;
			if (process.documentation == null) {
				process.documentation = new ProcessDocumentation();
			}

			// sync sheets
			syncGeneralInfo(wb, process);

			var synced = process.id == 0
				? db.insert(process)
				: db.update(process);
			return Optional.of(synced);
		} catch (IOException e) {
			throw new RuntimeException(
				"failed to read process from file: " + file);
		}
	}

	private boolean skipUpdate(Process current, ProcessDescriptor next) {
		return switch (updates) {
			case NEVER -> true;
			case ALWAYS -> false;
			case IF_NEWER -> current.version != next.version
				? current.version >= next.version
				: current.lastChange >= next.lastChange;
		};
	}

	private ProcessDescriptor readInfo(Workbook wb) {
		var sheet = getSheet(wb, Tab.GENERAL_INFO);
		if (sheet == null)
			return null;
		var info = sheet.read(Section.GENERAL_INFO);
		var refId = info.str(Field.UUID);
		if (Strings.nullOrEmpty(refId))
			return null;
		var d = new ProcessDescriptor();
		d.refId = refId;
		d.name = info.str(Field.NAME);
		d.version = Version.fromString(info.str(Field.VERSION)).getValue();
		var lastChange = info.date(Field.LAST_CHANGE);
		d.lastChange = lastChange != null
			? lastChange.getTime()
			: 0;
		return d;
	}

	private void syncGeneralInfo(Workbook wb, Process process) {
		var sheet = getSheet(wb, Tab.GENERAL_INFO);
		if (sheet == null)
			return;

		var info = sheet.read(Section.GENERAL_INFO);
		if (info != null) {
			process.category = info.syncCategory(db, ModelType.PROCESS);
			process.description = info.str(Field.DESCRIPTION);
			process.tags = info.str(Field.TAGS);
		} else {
			process.category = null;
			process.description = null;
			process.tags = null;
		}

		var doc = process.documentation;

		var time = sheet.read(Section.TIME);
		if (time != null) {
			doc.validFrom = info.date(Field.VALID_FROM);
			doc.validUntil = info.date(Field.VALID_UNTIL);
			doc.time = info.str(Field.DESCRIPTION);
		} else {
			doc.validFrom = null;
			doc.validUntil = null;
			doc.time = null;
		}

		var geo = sheet.read(Section.GEOGRAPHY);
		if (geo != null) {
			// TODO location
			doc.geography = geo.str(Field.DESCRIPTION);
		} else {
			process.location = null;
			doc.geography = null;
		}

		var dqs = sheet.read(Section.DATA_QUALITY);
		if (dqs != null) {
			// TODO data quality schemes
			process.dqEntry = dqs.str(Field.DATA_QUALITY_ENTRY);
		} else {
			process.dqSystem = null;
			process.dqEntry = null;
			process.exchangeDqSystem = null;
			process.socialDqSystem = null;
		}
	}

	private SheetReader getSheet(Workbook wb, Tab tab) {
		var sheet = wb.getSheet(tab.label());
		return sheet != null
			? new SheetReader(sheet)
			: null;
	}
}
