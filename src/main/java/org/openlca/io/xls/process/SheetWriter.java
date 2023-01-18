package org.openlca.io.xls.process;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.openlca.core.model.RootEntity;
import org.openlca.core.model.Version;

import java.util.Date;
import java.util.function.Consumer;

class SheetWriter {

	private final Sheet sheet;
	private final Styles styles;
	private final OutConfig wb;

	private int cursor;

	SheetWriter(Sheet sheet, OutConfig wb) {
		this.wb = wb;
		this.sheet = sheet;
		this.styles = wb.styles();
	}

	SheetWriter withColumnWidths(int count, int width) {
		for (int i = 0; i < count; i++) {
			sheet.setColumnWidth(i, width * 256);
		}
		return this;
	}

	void header(Field... fields) {
		for (var i = 0; i < fields.length; i++) {
			cell(cursor, i, fields[i].label())
				.setCellStyle(styles.bold());
		}
		cursor++;
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

	void next(Consumer<RowWriter> fn) {
		fn.accept(new RowWriter(this, row(cursor)));
		cursor++;
	}

	void next() {
		cursor++;
	}

	private Cell cell(int row, int col, String val) {
		return cell(row(row), col, val);
	}

	private Cell cell(Row row, int col, String val) {
		var cell = cell(row, col);
		if (val != null) {
			cell.setCellValue(val);
		}
		return cell;
	}

	private Cell cell(Row row, int col, double val) {
		var cell = cell(row, col);
		cell.setCellValue(val);
		return cell;
	}

	private Cell cellAsDate(Row row, int col, long val) {
		var cell = cell(row, col);
		if (val > 0) {
			var date = new Date(val);
			cell.setCellValue(date);
		}
		return cell;
	}

	private Cell cell(int row, int col) {
		return cell(row(row), col);
	}

	private Cell cell(Row row, int col) {
		var cell = row.getCell(col);
		return cell == null
			? row.createCell(col)
			: cell;
	}

	private Row row(int row) {
		var r = sheet.getRow(row);
		return r == null
			? sheet.createRow(row)
			: r;
	}

	static class RowWriter {

		private final SheetWriter sheet;
		private final Row row;
		private int col = 0;

		private RowWriter(SheetWriter sheet, Row row) {
			this.sheet = sheet;
			this.row = row;
		}

		RowWriter next(String value) {
			sheet.cell(row, col, value);
			col++;
			return this;
		}

		RowWriter next(double value) {
			sheet.cell(row, col, value);
			col++;
			return this;
		}

		RowWriter nextAsDate(long value) {
			sheet.cellAsDate(row, col, value);
			col++;
			return this;
		}

		RowWriter nextAsVersion(long value) {
			var v = new Version(value).toString();
			return next(v);
		}

	}
}
