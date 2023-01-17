package org.openlca.io.xls.process;

import org.junit.Test;
import org.openlca.core.model.Flow;
import org.openlca.core.model.FlowProperty;
import org.openlca.core.model.Process;
import org.openlca.core.model.UnitGroup;
import org.openlca.core.model.store.InMemoryStore;

import java.io.File;

public class XlsProcessWriterTest {

	@Test
	public void testWrite() throws Exception {
		var store = InMemoryStore.create();
		var units = UnitGroup.of("Mass units", "kg");
		var mass = FlowProperty.of("Mass", units);
		var p = Flow.product("p", mass);
		var process = Process.of("P", p);
		store.insert(units, mass, p, process);
		var file = new File("target/process.xlsx");
		new XlsProcessWriter(store).write(process, file);
	}
}
