package org.openlca.io.xls.process;

import org.openlca.core.model.RootEntity;

interface EntitySheetWriter {

	void flush();

	void visit(RootEntity entity);

}
