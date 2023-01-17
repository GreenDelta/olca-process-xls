package org.openlca.io.xls.process;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.Date;

class SheetWriter {

	private final Sheet sheet;
	private final Styles styles;

	private int cursor;

	SheetWriter(Sheet sheet, Styles styles) {
		this.sheet = sheet;
		this.styles = styles;
	}

	SheetWriter header(String header) {
		cell(cursor, 0, header).setCellStyle(styles.bold());
		cursor++;
		return this;
	}

	SheetWriter next(String header, String value) {
		cell(cursor, 0, header).setCellStyle(styles.pairLabel());
		cell(cursor, 1, value).setCellStyle(styles.pairValue());
		cursor++;
		return this;
	}

	SheetWriter next(String header, Date date) {
		cell(cursor, 0, header).setCellStyle(styles.pairLabel());
		if (date != null) {
			var c = cell(cursor, 1);
			c.setCellValue(date);
			c.setCellStyle(styles.date());
		}
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
