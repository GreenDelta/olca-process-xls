package org.openlca.io.xls.process;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openlca.core.database.IDatabase;
import org.openlca.core.model.Actor;
import org.openlca.core.model.Flow;
import org.openlca.core.model.FlowProperty;
import org.openlca.core.model.Process;
import org.openlca.core.model.Source;
import org.openlca.core.model.UnitGroup;
import org.openlca.core.model.doc.ProcessDoc;
import org.openlca.core.model.doc.Review;
import org.openlca.core.model.doc.ReviewScope;

public class DocTest {

	private final IDatabase db = Tests.db();
	private Process process;
	private ProcessDoc doc;
	private Actor actor;
	private Source source;

	@Before
	public void setup() {
		var units = UnitGroup.of("Mass units", "kg");
		var mass = FlowProperty.of("Mass", units);
		var p = Flow.product("p", mass);
		actor = Actor.of("actor");
		source = Source.of("source");
		db.insert(units, mass, p);
		process = Process.of("P", p);
		doc = process.documentation = new ProcessDoc();
	}

	@After
	public void cleanup() {
		var qref = process.quantitativeReference;
		var mass = qref.flowPropertyFactor.flowProperty;
		var units = mass.unitGroup;
		db.delete(qref.flow, mass, units, actor, source);
	}

	@Test
	public void testFlowCompleteness() {
		var a = doc.flowCompleteness;
		a.put("Product flows", "All present");
		a.put("Climate change", "Some missing");
		var synced = Tests.syncWithDb(process, db);
		var copy = synced.documentation.flowCompleteness;
		assertEquals("All present", copy.get("Product flows"));
		assertEquals("Some missing", copy.get("Climate change"));
		db.delete(synced);
	}

	@Test
	public void testReviews() {
		for (int i = 0; i < 3; i++) {
			var rev = new Review();
			doc.reviews.add(rev);
			rev.type = "some review method";
			rev.details = "details " + i;
			rev.report = source;
			rev.reviewers.add(actor);

			var docScope = new ReviewScope("Documentation");
			docScope.methods.add("Reading");
			docScope.methods.add("Spell checking");
			rev.scopes.put(docScope);

			var calScope = new ReviewScope("Flow amounts");
			calScope.methods.add("Mass balance");
			calScope.methods.add("Energy balance");
			rev.scopes.put(calScope);

			rev.assessment.put("Documentation", "Very good");
			rev.assessment.put("Data quality", "Good");
		}

		var synced = Tests.syncWithDb(process, db);

		for (int i = 0; i < 3; i++) {
			var rev = synced.documentation.reviews.get(i);
			assertEquals("some review method", rev.type);
			assertEquals("details " + i, rev.details);
			assertEquals(source, rev.report);
			assertEquals(actor, rev.reviewers.get(1));

			var docScope = rev.scopes.get("Documentation");
			assertTrue(docScope.methods.contains("Reading"));
			assertTrue(docScope.methods.contains("Spell checking"));

			var calScope = rev.scopes.get("Flow amounts");
			assertTrue(calScope.methods.contains("Mass balance"));
			assertTrue(calScope.methods.contains("Energy balance"));

			assertEquals("Very good", rev.assessment.get("Documentation"));
			assertEquals("Good", rev.assessment.get("Data quality"));
		}

		db.delete(synced);
	}

}
