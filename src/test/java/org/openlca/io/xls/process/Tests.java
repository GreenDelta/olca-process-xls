package org.openlca.io.xls.process;

import org.openlca.core.database.Derby;
import org.openlca.core.database.IDatabase;

class Tests {

	private static IDatabase db = Derby.createInMemory();

	static IDatabase db() {
		return db;
	}

}



