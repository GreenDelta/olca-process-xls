package org.openlca.io.xls.process;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openlca.core.model.Process;
import org.openlca.core.model.descriptors.ProcessDescriptor;
import org.openlca.core.model.store.EntityStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class XlsProcessWriter {

	private final EntityStore db;

	private XlsProcessWriter(EntityStore db) {
		this.db = db;
	}

	public static XlsProcessWriter of(EntityStore db) {
		return new XlsProcessWriter(db);
	}

	public void write(ProcessDescriptor d, File file) {
		if (d == null || file == null)
			return;
		var process = db.get(Process.class, d.id);
		if (process == null)
			return;
		write(process, file);
	}

	public void write(Process process, File file) {
		if (process == null || file == null)
			return;
		try (var wb = new XSSFWorkbook();
				 var out = new FileOutputStream(file)) {
			var writer = new WorkbookWriter(wb, db, process);
			writer.write();
			wb.write(out);
		} catch (IOException e) {
			throw new RuntimeException(
				"failed to write process " + process + " to file " + file, e);
		}
	}
}
