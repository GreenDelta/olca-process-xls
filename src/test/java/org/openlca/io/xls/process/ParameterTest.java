package org.openlca.io.xls.process;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openlca.core.database.IDatabase;
import org.openlca.core.math.SystemCalculator;
import org.openlca.core.model.CalculationSetup;
import org.openlca.core.model.Flow;
import org.openlca.core.model.Parameter;
import org.openlca.core.model.ParameterRedef;
import org.openlca.core.model.Process;
import org.openlca.core.model.store.InMemoryStore;

import java.util.List;

public class ParameterTest {

	private final IDatabase db = Tests.db();
	private Process synced;

	@Before
	public void setup() {
		var store = InMemoryStore.create();
		var mass = Tests.createMass(store);
		var p = Flow.product("p", mass);
		var e = Flow.elementary("e", mass);
		var process = Process.of("P", p);
		process.output(e, 1).formula = "localDep";

		// parameters
		var globalInp = Parameter.global("globalInp", 1);
		var globalDep = Parameter.global("globalDep", "2 * globalInp");
		process.parameters.addAll(List.of(
			Parameter.process("localInp", 0.25),
			Parameter.process("localDep", "globalDep * localInp")));

		store.insert(p, e, process, globalInp, globalDep);
		synced = Tests.syncWithDb(process, store);
	}

	@After
	public void cleanup() {
		db.clear();
	}

	@Test
	public void testCalculation() {
		var globalInp = db.getForName(Parameter.class, "globalInp");
		var setup = CalculationSetup.of(synced)
			.withParameters(List.of(ParameterRedef.of(globalInp, 84)));
		var result = new SystemCalculator(db).calculate(setup);
		var enviFlow = result.enviIndex().at(0);
		assertEquals(42, result.getTotalFlowValueOf(enviFlow), 1e-17);
	}

}
