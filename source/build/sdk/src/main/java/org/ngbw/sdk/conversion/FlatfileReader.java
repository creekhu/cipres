/*
 * FlatfileReader.java
 */
package org.ngbw.sdk.conversion;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ngbw.sdk.api.conversion.ConversionRegistry;
import org.ngbw.sdk.api.conversion.RecordFilter;
import org.ngbw.sdk.api.core.CoreRegistry;
import org.ngbw.sdk.api.core.GenericDataRecordCollection;
import org.ngbw.sdk.common.util.ValidationResult;
import org.ngbw.sdk.core.shared.IndexedDataRecord;
import org.ngbw.sdk.core.shared.SourceDocumentBean;
import org.ngbw.sdk.core.shared.SourceDocumentType;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.core.types.RecordType;
import org.ngbw.sdk.database.SourceDocument;


/**
 * 
 * @author Roland H. Niedner
 * 
 */
public class FlatfileReader extends BaseSourceDocumentReader {

	private Map<SourceDocumentType, FlatfileParser> parsers = new HashMap<SourceDocumentType, FlatfileParser>();

	/**
	 * @param conversionRegistry
	 */
	public FlatfileReader(ConversionRegistry conversionRegistry) {
		super(conversionRegistry);
		init();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ngbw.sdk.conversion.BaseSourceDocumentReader#read(org.ngbw.sdk.api.core.SourceDocument)
	 */
	@Override
	public GenericDataRecordCollection<IndexedDataRecord> read(SourceDocument document) throws IOException, SQLException, ParseException {
		if (document == null)
			throw new NullPointerException("SourceDocument must not be null!");
		SourceDocumentType documentType = document.getType();
		String data = new String(document.getData());
		GenericDataRecordCollection<IndexedDataRecord> drc = this.getDataRecordCollection(document);
		DataFormat dataFormat = document.getDataFormat();
		BufferedReader br = new BufferedReader(new StringReader(data));
		RecordFilter filter = conversionRegistry.getRecordFilter(dataFormat);
		filter.setInput(br);
		int index = 0;
		if (filter.hasNext() == false)
			throw new RuntimeException("RecordFilter cannot retrive any records - invalid SourceDocumentType!");
		while (filter.hasNext()) {
			String recordSource = filter.next();
			SourceDocument recordDoc = new SourceDocumentBean(documentType,
					recordSource.getBytes());
			recordDoc.setValidated();
			drc.add(readSingle(index, recordDoc));
			index++;
		}
		filter.close();
		return drc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ngbw.sdk.conversion.BaseSourceDocumentReader#readSingle(org.ngbw.sdk.api.core.SourceDocument)
	 */
	@Override
	public IndexedDataRecord readSingle(SourceDocument document) throws IOException, SQLException, ParseException {
		if (document == null)
			throw new NullPointerException("SourceDocument must not be null!");
		if (document.isValidated() == false) {
			DataFormat dataFormat = document.getDataFormat();
			BufferedReader br = new BufferedReader(new StringReader(new String(document.getData())));
			RecordFilter filter = conversionRegistry.getRecordFilter(dataFormat);
			filter.setInput(br);
			if (filter.hasNext() == false)
				throw new RuntimeException("RecordFilter cannot retrive any records - invalid SourceDocumentType!");
			String recordSource = filter.next();
			SourceDocument recordDoc = new SourceDocumentBean(document.getType(),
					recordSource.getBytes());
			recordDoc.setValidated();
			if (filter.hasNext())
				throw new RuntimeException("RecordFilter retrived more than 1 record - use read() method!");
			return readSingle(0, recordDoc);
		} else {
			return readSingle(0, document);
		}
	}

	private IndexedDataRecord readSingle(int index, SourceDocument document) throws IOException, SQLException, ParseException {
		FlatfileParser parser = getFlatfileParser(document.getType());
		String data = new String(document.getData());
		DataFormat dataFormat = document.getDataFormat();
		RecordType recordType = conversionRegistry.getCoreRegistry().getRecordType(document.getEntityType(), 
				document.getDataType());
		return parser.parse(index, recordType, dataFormat, data);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.ngbw.sdk.conversion.BaseSourceDocumentReader#validate(org.ngbw.sdk.api.core.SourceDocument)
	 */
	@Override
	public ValidationResult validate(SourceDocument document) {
		if (document == null)
			throw new NullPointerException("SourceDocument must not be null!");
		ValidationResult result = new ValidationResult();
		try {
			read(document);
		} catch (Exception e) {
			result.addError(e.getMessage());
		}
		return result;
	}

	private FlatfileParser getFlatfileParser(SourceDocumentType type) {
		if (parsers.containsKey(type) == false) {
			CoreRegistry coreRegistry = conversionRegistry.getCoreRegistry();
			RecordType recordType = coreRegistry.getRecordType(type.getEntityType(), type.getDataType());
			Set<RecordFieldType> fields = coreRegistry.getRecordFields(recordType);
			FlatfileParser parser = new FlatfileParser(fields);
			parsers.put(type, parser);
		}
		return parsers.get(type);
	}

	private void init() {
		// register formats that can be read and
		// this reader can read what the FlatfileParser can parse
		Map<DataFormat, Set<RecordType>> map = FlatfileParser.canParse();
		for (DataFormat dataFormat : map.keySet()) {
			Set<RecordType> recordTypes = map.get(dataFormat);
			for (RecordType recordType : recordTypes)
				srcTypes.add(getSourceDocumentType(recordType, dataFormat));
		}
	}
}
