package org.openlca.io.xls.process;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

class RowReader implements CellReader {

	private final Row row;
	private final FieldMap fields;

	private RowReader(Row row, FieldMap fields) {
		this.row = row;
		this.fields = fields;
	}

	static RowReader of(Row row, FieldMap fields) {
		return new RowReader(row, fields);
	}

	int getRowNum() {
		return row.getRowNum();
	}

	@Override
	public Cell cellOf(Field field) {
		var col = fields.posOf(field);
		return col.isPresent()
				? In.cell(row, col.getAsInt())
				: null;
	}
}
