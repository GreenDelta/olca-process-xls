package org.openlca.io.xls.process;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openlca.core.database.IDatabase;
import org.openlca.core.model.Category;
import org.openlca.core.model.Flow;
import org.openlca.core.model.FlowProperty;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.Process;
import org.openlca.core.model.UnitGroup;
import org.openlca.core.model.store.InMemoryStore;

import java.io.File;

public class BaseTest {

	private final IDatabase db = Tests.db();
	private final InMemoryStore store = InMemoryStore.create();
	private Process process;

	@Before
	public void setup() {
		var units = UnitGroup.of("Mass units", "kg");
		var mass = FlowProperty.of("Mass", units);
		var p = Flow.product("p", mass);
		process = Process.of("P", p);
		var root = Category.of("some", ModelType.PROCESS);
		process.category = Category.childOf(root, "category");
		store.insert(units, mass, p, process);
	}

	@After
	public void cleanup() {
		db.clear();
		store.clear();
	}

	@Test
	public void testBasicMetaData() {
		var file = new File("target/process.xlsx");
		XlsProcessWriter.of(store).write(process, file);
		var copy = XlsProcessReader.of(db)
			.sync(file)
			.orElseThrow();
		assertEquals(process.refId, copy.refId);
		assertEquals(process.name, copy.name);
		assertEquals(process.lastChange, copy.lastChange);
		assertEquals(process.category.toPath(), "some/category");
	}
}
