package org.openlca.io.xls.process;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openlca.core.database.IDatabase;
import org.openlca.core.model.Category;
import org.openlca.core.model.Flow;
import org.openlca.core.model.Location;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.Process;
import org.openlca.core.model.ProcessDocumentation;
import org.openlca.core.model.Source;
import org.openlca.core.model.store.InMemoryStore;

import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;

public class BaseTest {

	private final IDatabase db = Tests.db();
	private Process origin;
	private Process synced;

	@Before
	public void setup() throws IOException {
		var store = InMemoryStore.create();
		var mass = Tests.createMass(store);
		var p = Flow.product("p", mass);

		origin = Process.of("P", p);
		var doc = origin.documentation = new ProcessDocumentation();
		var root = Category.of("some", ModelType.PROCESS);
		origin.category = Category.childOf(root, "category");

		var location = Location.of("Aruba", "AW");
		origin.location = location;
		origin.documentation.geography = "about geography";

		var source1 = Source.of("Source 1");
		var source2 = Source.of("Source 2");
		doc.sources.add(source1);
		doc.sources.add(source2);
		doc.publication = source1;

		doc.dataCollectionPeriod = "dataCollectionPeriod";
		doc.completeness = "completeness";
		doc.inventoryMethod = "inventoryMethod";
		doc.dataTreatment = "dataTreatment";

		// write and read
		store.insert(source1, source2, p, location, origin);
		var file = Files.createTempFile("_olca_", ".xlsx");
		XlsProcessWriter.of(store)
			.write(origin, file.toFile());
		synced = XlsProcessReader.of(db)
			.sync(file.toFile())
			.orElseThrow();
		// System.out.println(file);
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
		var qref = synced.quantitativeReference;
		assertEquals(1, qref.amount, 1e-17);
		assertEquals("p", qref.flow.name);
		assertEquals("kg", qref.unit.name);
		assertEquals("Mass", qref.flowPropertyFactor.flowProperty.name);
	}

	@Test
	public void testGeography() {
		var loc = synced.location;
		assertNotNull(loc);
		assertEquals("Aruba", loc.name);
		assertEquals("AW", loc.code);
		assertEquals("about geography", synced.documentation.geography);
	}

	@Test
	public void testSources() {
		var sources = synced.documentation.sources
			.stream()
			.map(s -> s.name)
			.toList();
		assertTrue(sources.contains("Source 1"));
		assertTrue(sources.contains("Source 2"));
	}
}
