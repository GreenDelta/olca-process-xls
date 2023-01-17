package org.openlca.io.xls.process;

import org.apache.poi.ss.usermodel.Workbook;
import org.openlca.core.model.Process;
import org.openlca.core.model.ProcessDocumentation;
import org.openlca.core.model.RootEntity;
import org.openlca.core.model.Version;
import org.openlca.core.model.store.EntityStore;

import java.util.Collections;
import java.util.Date;
import java.util.List;

class WorkbookWriter {

	final Workbook wb;
	final Styles styles;

	private final EntityStore db;
	private final Process process;
	private final List<EntitySheetWriter> entitySheets;

	WorkbookWriter(Workbook wb, EntityStore db, Process process) {
		this.wb = wb;
		this.styles = Styles.of(wb);
		this.db = db;
		this.process = process;
		entitySheets = Collections.emptyList();
	}

	void write() {
		writeInfoSheet();
	}

	void visit(RootEntity e) {
		if (e == null)
			return;
		for (var sheet : entitySheets) {
			sheet.visit(e);
		}
	}

	private void writeInfoSheet() {
		var sheet = new SheetWriter(Tab.GENERAL_INFO, this);

		sheet.next(Section.GENERAL_INFO)
			.next(Field.UUID, process.refId)
			.next(Field.NAME, process.name)
			.next(Field.CATEGORY, Util.pathOf(process))
			.next(Field.DESCRIPTION, process.description)
			.next(Field.VERSION, Version.asString(process.version))
			.next(Field.LAST_CHANGE, process.lastChange > 0
				? new Date(process.lastChange)
				: null)
			.next(Field.TAGS, process.tags)
			.next();

		var doc = process.documentation != null
			? process.documentation
			: new ProcessDocumentation();

		sheet.next(Section.TIME)
			.next(Field.VALID_FROM, doc.validFrom)
			.next(Field.VALID_UNTIL, doc.validUntil)
			.next(Field.DESCRIPTION, doc.time)
			.next();

		sheet.next(Section.GEOGRAPHY)
			.next(Field.LOCATION, process.location)
			.next(Field.DESCRIPTION, doc.geography)
			.next();

		sheet.next(Section.TECHNOLOGY)
			.next(Field.DESCRIPTION, doc.technology)
			.next();

		sheet.next(Section.DATA_QUALITY)
			.next(Field.PROCESS_SCHEMA, process.dqSystem)
			.next(Field.DATA_QUALITY_ENTRY, process.dqEntry)
			.next(Field.FLOW_SCHEMA, process.exchangeDqSystem)
			.next(Field.SOCIAL_SCHEMA, process.socialDqSystem)
			.next();
	}

}
