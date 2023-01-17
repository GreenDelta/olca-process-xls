package org.openlca.io.xls.process;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.openlca.core.model.RootEntity;

import java.util.Date;

class SheetWriter {

	private final Sheet sheet;
	private final Styles styles;
	private final WorkbookWriter wb;

	private int cursor;

	SheetWriter(Tab tab, WorkbookWriter wb) {
		this.wb = wb;
		this.sheet = wb.wb.createSheet(tab.label());
		this.styles = wb.styles;
	}

	SheetWriter next(Section section) {
		cell(cursor, 0, section.header()).setCellStyle(styles.bold());
		cursor++;
		return this;
	}

	SheetWriter next(Field field, String value) {
		cell(cursor, 0, field.label()).setCellStyle(styles.pairLabel());
		cell(cursor, 1, value).setCellStyle(styles.pairValue());
		cursor++;
		return this;
	}

	SheetWriter next(Field field, Date date) {
		cell(cursor, 0, field.label()).setCellStyle(styles.pairLabel());
		if (date != null) {
			var c = cell(cursor, 1);
			c.setCellValue(date);
			c.setCellStyle(styles.date());
		}
		cursor++;
		return this;
	}

	SheetWriter next(Field field, RootEntity e) {
		cell(cursor, 0, field.label()).setCellStyle(styles.pairLabel());
		if (e != null) {
			wb.visit(e);
			cell(cursor, 1, e.name).setCellStyle(styles.pairValue());
		}
		cursor++;
		return this;
	}

	SheetWriter next(RootEntity e) {
		if (e == null)
			return this;
		wb.visit(e);
		cell(cursor, 0, e.name);
		cursor++;
		return this;
	}

	void next() {
		cursor++;
	}

	private Cell cell(int row, int col, String val) {
		var cell = cell(row, col);
		if (val != null) {
			cell.setCellValue(val);
		}
		return cell;
	}

	private Cell cell(int row, int col) {
		var r = row(row);
		var cell = r.getCell(col);
		return cell == null
			? r.createCell(col)
			: cell;
	}

	private Row row(int row) {
		var r = sheet.getRow(row);
		return r == null
			? sheet.createRow(row)
			: r;
	}
}
