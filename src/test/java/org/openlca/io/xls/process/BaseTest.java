package org.openlca.io.xls.process;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openlca.core.database.IDatabase;
import org.openlca.core.model.Category;
import org.openlca.core.model.Flow;
import org.openlca.core.model.FlowProperty;
import org.openlca.core.model.Location;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.Process;
import org.openlca.core.model.ProcessDocumentation;
import org.openlca.core.model.UnitGroup;
import org.openlca.core.model.store.InMemoryStore;

import java.io.IOException;
import java.nio.file.Files;

public class BaseTest {

	private final IDatabase db = Tests.db();
	private Process origin;
	private Process synced;

	@Before
	public void setup() throws IOException {
		var units = UnitGroup.of("Mass units", "kg");
		var mass = FlowProperty.of("Mass", units);
		var p = Flow.product("p", mass);

		origin = Process.of("P", p);
		origin.documentation = new ProcessDocumentation();
		var root = Category.of("some", ModelType.PROCESS);
		origin.category = Category.childOf(root, "category");

		var location = Location.of("Aruba", "AW");
		origin.location = location;
		origin.documentation.geography = "about geography";

		// write and read
		var store = InMemoryStore.create();
		store.insert(units, mass, p, location, origin);
		var file = Files.createTempFile("_olca_", ".xlsx");
		XlsProcessWriter.of(store)
			.write(origin, file.toFile());
		synced = XlsProcessReader.of(db)
			.sync(file.toFile())
			.orElseThrow();
		Files.delete(file);
	}

	@After
	public void cleanup() {
		db.clear();
	}

	@Test
	public void testBasicMetaData() {
		assertEquals(origin.refId, synced.refId);
		assertEquals(origin.name, synced.name);
		assertEquals(origin.lastChange, synced.lastChange);
		assertEquals(origin.category.toPath(), "some/category");
	}

	@Test
	public void testQuantRef() {
		assertEquals("p", synced.quantitativeReference.flow.name);
	}

	@Test
	public void testGeography() {
		var loc = synced.location;
		assertNotNull(loc);
		assertEquals("Aruba", loc.name);
		assertEquals("AW", loc.code);
		assertEquals("about geography", synced.documentation.geography);
	}
}
