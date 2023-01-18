package org.openlca.io.xls.process;

import org.openlca.core.model.RefEntity;
import org.openlca.core.model.RootEntity;
import org.openlca.util.Strings;

import java.util.Collection;
import java.util.List;

final class Out {

	private Out() {
	}

	static String pathOf(RootEntity e) {
		return e != null && e.category != null
			? e.category.toPath()
			: null;
	}

	static <T extends RefEntity> List<T> sort(Collection<T> set) {
		return set.stream().sorted((e1, e2) -> {
			if (e1 == null && e2 == null)
				return 0;
			if (e1 == null)
				return -1;
			if (e2 == null)
				return 1;
			int c = Strings.compare(e1.name, e2.name);
			if (c != 0)
				return c;
			if (e1 instanceof RootEntity re1 && e2 instanceof RootEntity re2) {
				var c1 = Out.pathOf(re1);
				var c2 = Out.pathOf(re2);
				return Strings.compare(c1, c2);
			}
			return 0;
		}).toList();
	}
}
