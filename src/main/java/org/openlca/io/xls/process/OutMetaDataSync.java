package org.openlca.io.xls.process;

import java.util.Date;

import org.openlca.core.model.Process;
import org.openlca.core.model.doc.ProcessDoc;
import org.openlca.core.model.ProcessType;
import org.openlca.core.model.Version;

class OutMetaDataSync {

	private final OutConfig config;
	private final Process process;
	private final ProcessDoc doc;

	private OutMetaDataSync(OutConfig config) {
		this.config = config;
		this.process = config.process();
		this.doc = process.documentation == null
			? new ProcessDoc()
			: process.documentation;
	}

	static void sync(OutConfig config) {
		new OutMetaDataSync(config).sync();
	}

	private void sync() {
		writeInfoSheet();
		writeDocSheet();
	}

	private void writeInfoSheet() {
		var sheet = config.createSheet(Tab.GENERAL_INFO)
			.withColumnWidths(2, 40);

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

	private void writeDocSheet() {
		var sheet = config.createSheet(Tab.DOCUMENTATION)
			.withColumnWidths(2, 40);

		sheet.next(Section.LCI_METHOD)
			.next(Field.PROCESS_TYPE, process.processType == ProcessType.LCI_RESULT
				? "LCI result"
				: "Unit process")
			.next(Field.LCI_METHOD, doc.inventoryMethod)
			.next(Field.MODELING_CONSTANTS, doc.modelingConstants)
			.next();

		sheet.next(Section.DATA_SOURCE_INFO)
			.next(Field.DATA_COMPLETENESS, doc.dataCompleteness)
			.next(Field.DATA_SELECTION, doc.dataSelection)
			.next(Field.DATA_TREATMENT, doc.dataTreatment)
			.next(Field.SAMPLING_PROCEDURE, doc.samplingProcedure)
			.next(Field.DATA_COLLECTION_PERIOD, doc.dataCollectionPeriod)
			.next(Field.USE_ADVICE, doc.useAdvice)
			.next();

		// flow completeness
		sheet.next(Section.COMPLETENESS);
		doc.flowCompleteness.each(
			(key, val) -> sheet.next(row -> row.next(key).next(val)));
		sheet.next();

		/*
		TODO write review sections
		sheet.next(Section.REVIEW)
			.next(Field.REVIEWER, doc.reviewer)
			.next(Field.REVIEW_DETAILS, doc.reviewDetails)
			.next();
    */

		sheet.next(Section.SOURCES);
		for (var source : doc.sources) {
			sheet.next(source);
		}

		sheet.next()
			.next(Section.ADMINISTRATIVE_INFO)
			.next(Field.PROJECT, doc.project)
			.next(Field.INTENDED_APPLICATION, doc.intendedApplication)
			.next(Field.DATA_SET_OWNER, doc.dataOwner)
			.next(Field.DATA_GENERATOR, doc.dataGenerator)
			.next(Field.DATA_DOCUMENTOR, doc.dataDocumentor)
			.next(Field.PUBLICATION, doc.publication)
			.next(Field.CREATION_DATE, doc.creationDate)
			.next(Field.COPYRIGHT, doc.copyright)
			.next(Field.ACCESS_RESTRICTIONS, doc.accessRestrictions);
	}


}
