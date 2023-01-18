package org.openlca.io.xls.process;

import java.io.IOException;
import java.nio.file.Files;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openlca.core.database.IDatabase;
import org.openlca.core.model.Category;
import org.openlca.core.model.DQSystem;
import org.openlca.core.model.Flow;
import org.openlca.core.model.FlowProperty;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.Process;
import org.openlca.core.model.ProcessDocumentation;
import org.openlca.core.model.UnitGroup;
import org.openlca.core.model.Version;
import org.openlca.jsonld.input.UpdateMode;

import static org.junit.Assert.*;

public class UpdateTest {

	private final IDatabase db = Tests.db();
	private Process process;

	@Before
	public void setup() {
		var units = UnitGroup.of("Mass units", "kg");
		var mass = FlowProperty.of("Mass", units);
		var p = Flow.product("p", mass);

		process = Process.of("P", p);
		process.documentation = new ProcessDocumentation();
		var root = Category.of("some", ModelType.PROCESS);
		process.category = Category.childOf(root, "category");
	}

	@After
	public void teardown() {
		db.clear();
	}

	@Test
	public void testUpdateDqSchema() {

		var schema = db.insert(DQSystem.of("dq-schema"));
		process.dqSystem = schema;
		process.exchangeDqSystem = schema;
		process.socialDqSystem = schema;

		var v = new Version(process.version);
		v.incMinor();
		process.version = v.getValue();

		var synced = sync(process, UpdateMode.IF_NEWER);
		assertNotSame(process, synced);
		assertEquals(synced.dqSystem, schema);
		assertEquals(synced.exchangeDqSystem, schema);
		assertEquals(synced.socialDqSystem, schema);
	}

	private Process sync(Process process, UpdateMode mode) {
		try {
			var file = Files.createTempFile("_olca_", ".xlsx").toFile();
			XlsProcessWriter.of(db).write(process, file);
			var synced = XlsProcessReader.of(db)
				.withUpdates(mode)
				.sync(file)
				.orElseThrow();
			Files.delete(file.toPath());
			return synced;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
