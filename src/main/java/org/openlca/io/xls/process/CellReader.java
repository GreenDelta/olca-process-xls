package org.openlca.io.xls.process;

import org.apache.poi.ss.usermodel.Cell;
import org.openlca.core.database.CategoryDao;
import org.openlca.core.database.IDatabase;
import org.openlca.core.model.Category;
import org.openlca.core.model.ModelType;
import org.openlca.util.Strings;

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

	default Category syncCategory(IDatabase db, ModelType type) {
		var path = str(Field.CATEGORY);
		if (Strings.nullOrEmpty(path))
			return null;
		var parts = path.split("/");
		return CategoryDao.sync(db, type, parts);
	}

	Cell cellOf(Field field);

}