package org.openlca.io.xls.process;

import org.openlca.core.model.Location;
import org.openlca.core.model.RootEntity;

import java.util.HashSet;
import java.util.Set;

class LocationWriter implements EntityWriter {

	private final WorkbookWriter wb;
	private final Set<Location> locations = new HashSet<>();

	LocationWriter(WorkbookWriter wb) {
		this.wb = wb;
	}

	@Override
	public void visit(RootEntity entity) {
		if (entity instanceof Location location) {
			locations.add(location);
		}
	}

	@Override
	public void flush() {
		if (locations.isEmpty())
			return;
		var cursor = wb.createSheet(Tab.LOCATIONS)
			.withColumnWidths(5, 25);
		cursor.header(
			Field.UUID,
			Field.CODE,
			Field.NAME,
			Field.CATEGORY,
			Field.DESCRIPTION,
			Field.LATITUDE,
			Field.LONGITUDE,
			Field.LAST_CHANGE,
			Field.VERSION
		);

		for (var location : Util.sort(locations)) {
			cursor.next(row -> row.next(location.refId)
				.next(location.code)
				.next(location.name)
				.next(Util.pathOf(location))
				.next(location.description)
				.next(location.latitude)
				.next(location.longitude)
				.nextAsDate(location.lastChange)
				.nextAsVersion(location.version));
		}
	}
}
