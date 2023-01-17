package org.openlca.io.xls.process;

import org.openlca.core.model.RootEntity;

final class Util {
	private Util () {
	}

	static String pathOf(RootEntity e) {
		return e != null && e.category != null
			? e.category.toPath()
			: null;
	}
}
