package org.openlca.io.xls.process;

import org.openlca.core.database.Derby;
import org.openlca.core.database.IDatabase;
import org.openlca.core.model.FlowProperty;
import org.openlca.core.model.Process;
import org.openlca.core.model.Unit;
import org.openlca.core.model.UnitGroup;
import org.openlca.core.model.store.EntityStore;

import java.io.IOException;
import java.nio.file.Files;

class Tests {

	private static final IDatabase db = Derby.createInMemory();

	static IDatabase db() {
		return db;
	}

	/**
	 * A utility method to create the flow property 'Mass' with a corresponding
	 * unit group that has the units 'kg' and 'g'.
	 */
	static FlowProperty createMass(EntityStore db) {
		var group = UnitGroup.of("Units of mass", "kg");
		group.units.add(Unit.of("g", 0.001));
		var mass = FlowProperty.of("Mass", group);
		db.insert(group, mass);
		return mass;
	}

	static Process syncWithDb(Process process, EntityStore store) {
		try {
			var file = Files.createTempFile("_olca_", ".xlsx").toFile();
			XlsProcessWriter.of(store).write(process, file);
			var synced = XlsProcessReader.of(db()).sync(file).orElseThrow();
			Files.delete(file.toPath());
			return synced;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}



