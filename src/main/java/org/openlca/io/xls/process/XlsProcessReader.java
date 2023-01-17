package org.openlca.io.xls.process;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openlca.core.database.IDatabase;
import org.openlca.core.io.ImportLog;
import org.openlca.core.model.Process;
import org.openlca.core.model.Version;
import org.openlca.core.model.descriptors.ProcessDescriptor;
import org.openlca.util.Strings;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class XlsProcessReader {

	private final IDatabase db;
	private final ImportLog log;
	private boolean skipUpdates;

	public XlsProcessReader(IDatabase db) {
		this.db = Objects.requireNonNull(db);
		this.log = new ImportLog();
	}

	public ImportLog log() {
		return log;
	}

	public XlsProcessReader skipUpdates(boolean b) {
		this.skipUpdates = b;
		return this;
	}

	public Optional<Process> sync(File file) throws IOException {
		try (var wb = WorkbookFactory.create(file)) {
			var info = readInfo(wb);
			if (info == null) {
				log.error("invalid workbook: " + file.getName());
				return Optional.empty();
			}
			var process = db.get(Process.class, info.refId);
			if (process != null) {
				if (skipUpdates)
					return Optional.of(process);
			} else {
				process = new Process();
			}
		}
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

	private SheetReader getSheet(Workbook wb, Tab tab) {
		var sheet = wb.getSheet(tab.label());
		return sheet != null
			? new SheetReader(sheet)
			: null;
	}
}
