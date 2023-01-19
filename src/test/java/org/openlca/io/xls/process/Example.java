package org.openlca.io.xls.process;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

import org.openlca.core.DataDir;
import org.openlca.core.database.ProcessDao;

public class Example {

	public static void main(String[] args) {
		try (var db = DataDir.get().openDatabase("ei39_cutoff")) {
			var ps = new ProcessDao(db).getDescriptors();
			var rand = ThreadLocalRandom.current();
			var i = rand.nextInt(ps.size());
			XlsProcessWriter.of(db)
				.write(ps.get(i), new File("target/example.xlsx"));
		}
	}
}
