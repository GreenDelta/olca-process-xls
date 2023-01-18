package org.openlca.io.xls.process;

import org.apache.poi.ss.usermodel.Workbook;
import org.openlca.core.database.IDatabase;
import org.openlca.core.model.Process;

record InConfig(
	XlsProcessReader reader,
	Workbook wb,
	Process process,
	EntityIndex index,
	IDatabase db) {

	SheetReader getSheet(Tab tab) {
		var sheet = wb.getSheet(tab.label());
		return sheet != null
			? new SheetReader(sheet)
			: null;
	}
}
