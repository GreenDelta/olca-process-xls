package org.openlca.io.xls.process;

import org.apache.poi.ss.usermodel.Workbook;
import org.openlca.core.model.Process;
import org.openlca.core.model.Version;
import org.openlca.core.model.store.EntityStore;

import java.util.Date;

class WbWriter {

	private final Workbook wb;
	private final Styles styles;
	private final EntityStore db;
	private final Process process;

	WbWriter(Workbook wb, EntityStore db, Process process) {
		this.wb = wb;
		this.styles = Styles.of(wb);
		this.db = db;
		this.process = process;
	}

	void write() {
		writeInfoSheet();
	}

	private void writeInfoSheet() {
		var sheet = createSheet(Tab.GENERAL_INFORMATION);
		sheet.header("General information")
			.next("UUID", process.refId)
			.next("Name", process.name)
			.next("Category", Util.pathOf(process))
			.next("Description", process.description)
			.next("Version", Version.asString(process.version))
			.next("Last change", process.lastChange > 0
				? new Date(process.lastChange)
				: null)
			.next("Tags", process.tags)
			.next();
	}

	SheetWriter createSheet(Tab tab) {
		var sheet = wb.createSheet(tab.label());
		return new SheetWriter(sheet, styles);
	}

}
