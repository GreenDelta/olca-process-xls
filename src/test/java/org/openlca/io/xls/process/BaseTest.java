package org.openlca.io.xls.process;

import static org.junit.Assert.*;

import org.junit.Test;
import org.openlca.core.database.IDatabase;
import org.openlca.core.model.Flow;
import org.openlca.core.model.FlowProperty;
import org.openlca.core.model.Process;
import org.openlca.core.model.UnitGroup;
import org.openlca.core.model.store.InMemoryStore;

import java.io.File;

public class BaseTest {

	private final IDatabase db = Tests.db();

	@Test
	public void testBasicMetaData() {
		var store = InMemoryStore.create();
		var units = UnitGroup.of("Mass units", "kg");
		var mass = FlowProperty.of("Mass", units);
		var p = Flow.product("p", mass);
		var process = Process.of("P", p);
		store.insert(units, mass, p, process);
		var file = new File("target/process.xlsx");
		XlsProcessWriter.of(store).write(process, file);
		var copy = XlsProcessReader.of(db)
			.sync(file)
			.orElseThrow();
		assertEquals(process.refId, copy.refId);
		assertEquals(process.name, copy.name);
		assertEquals(process.lastChange, copy.lastChange);
	}
}
