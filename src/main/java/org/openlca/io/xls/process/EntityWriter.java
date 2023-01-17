package org.openlca.io.xls.process;

import org.openlca.core.model.RefEntity;
import org.openlca.core.model.RootEntity;
import org.openlca.util.Strings;

import java.util.Collection;
import java.util.List;

interface EntityWriter {

	void flush();

	void visit(RootEntity entity);

}
