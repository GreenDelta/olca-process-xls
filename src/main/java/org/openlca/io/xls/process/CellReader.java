package org.openlca.io.xls.process;

import org.apache.poi.ss.usermodel.Cell;

import java.util.Date;

interface CellReader {

	default String str(Field field) {
		return In.stringOf(cellOf(field));
	}

	default Date date(Field field) {
		return In.dateOf(cellOf(field));
	}

	default boolean bool(Field field) {
		return In.booleanOf(cellOf(field));
	}

	Cell cellOf(Field field);

}
