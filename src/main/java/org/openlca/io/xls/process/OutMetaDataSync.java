package org.openlca.io.xls.process;

import java.util.Date;

import org.openlca.core.model.Process;
import org.openlca.core.model.ProcessDocumentation;
import org.openlca.core.model.ProcessType;
import org.openlca.core.model.Version;

class OutMetaDataSync {

	private final OutConfig config;
	private final Process process;
	private final ProcessDocumentation doc;

	private OutMetaDataSync(OutConfig config) {
		this.config = config;
		this.process = config.process();
		this.doc = process.documentation == null
			? new ProcessDocumentation()
			: process.documentation;
	}

	static void sync(OutConfig config) {
		new OutMetaDataSync(config).sync();
	}

	private void sync() {
		writeInfoSheet();
		writeModellingSheet();
		writeAdminSheet();
	}

	private void writeInfoSheet() {
		var sheet = config.createSheet(Tab.GENERAL_INFO);

		sheet.next(Section.GENERAL_INFO)
			.next(Field.UUID, process.refId)
			.next(Field.NAME, process.name)
			.next(Field.CATEGORY, Out.pathOf(process))
			.next(Field.DESCRIPTION, process.description)
			.next(Field.VERSION, Version.asString(process.version))
			.next(Field.LAST_CHANGE, process.lastChange > 0
				? new Date(process.lastChange)
				: null)
			.next(Field.TAGS, process.tags)
			.next();

		sheet.next(Section.TIME)
			.next(Field.VALID_FROM, doc.validFrom)
			.next(Field.VALID_UNTIL, doc.validUntil)
			.next(Field.DESCRIPTION, doc.time)
			.next();

		sheet.next(Section.GEOGRAPHY)
			.next(Field.LOCATION, process.location)
			.next(Field.DESCRIPTION, doc.geography)
			.next();

		sheet.next(Section.TECHNOLOGY)
			.next(Field.DESCRIPTION, doc.technology)
			.next();

		sheet.next(Section.DATA_QUALITY)
			.next(Field.PROCESS_SCHEMA, process.dqSystem)
			.next(Field.DATA_QUALITY_ENTRY, process.dqEntry)
			.next(Field.FLOW_SCHEMA, process.exchangeDqSystem)
			.next(Field.SOCIAL_SCHEMA, process.socialDqSystem)
			.next();
	}

	private void writeAdminSheet() {
		var sheet = config.createSheet(Tab.ADMINISTRATIVE_INFORMATION);
		sheet.next(Section.ADMINISTRATIVE_INFO)
			.next(Field.INTENDED_APPLICATION, doc.intendedApplication)
			.next(Field.DATA_SET_OWNER, doc.dataSetOwner)
			.next(Field.DATA_SET_GENERATOR, doc.dataGenerator)
			.next(Field.DATA_SET_DOCUMENTOR, doc.dataDocumentor)
			.next(Field.PUBLICATION, doc.publication)
			.next(Field.ACCESS_AND_USE_RESTRICTIONS, doc.restrictions)
			.next(Field.PROJECT, doc.project)
			.next(Field.CREATION_DATE, doc.creationDate)
			.next(Field.COPYRIGHT, doc.copyright);
	}

	private void writeModellingSheet() {
		var sheet = config.createSheet(Tab.MODELING_AND_VALIDATION);

		sheet.next(Section.MODELING_AND_VALIDATION)
			.next(Field.PROCESS_TYPE, process.processType == ProcessType.LCI_RESULT
				? "LCI result"
				: "Unit process")
			.next(Field.LCI_METHOD, doc.inventoryMethod)
			.next(Field.MODELING_CONSTANTS, doc.modelingConstants)
			.next(Field.DATA_COMPLETENESS, doc.completeness)
			.next(Field.DATA_SELECTION, doc.dataSelection)
			.next(Field.DATA_TREATMENT, doc.dataTreatment)
			.next();

		sheet.next(Section.DATA_SOURCE_INFO)
			.next(Field.SAMPLING_PROCEDURE, doc.sampling)
			.next(Field.DATA_COLLECTION_PERIOD, doc.dataCollectionPeriod)
			.next();

		sheet.next(Section.REVIEW)
			.next(Field.REVIEWER, doc.reviewer)
			.next(Field.REVIEW_DETAILS, doc.reviewDetails)
			.next();

		sheet.next(Section.SOURCES);
		for (var source : doc.sources) {
			sheet.next(source);
		}
	}

}
