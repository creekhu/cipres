/*
 * PdbFinderParser.java
 */
package org.ngbw.sdk.conversion;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.api.conversion.ConversionRegistry;
import org.ngbw.sdk.api.conversion.RecordFilter;
import org.ngbw.sdk.api.conversion.SourceDocumentReader;
import org.ngbw.sdk.api.core.GenericDataRecordCollection;
import org.ngbw.sdk.common.util.ValidationResult;
import org.ngbw.sdk.core.shared.GenericDataRecordCollectionImpl;
import org.ngbw.sdk.core.shared.IndexedDataRecord;
import org.ngbw.sdk.core.shared.SourceDocumentType;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.EntityType;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.core.types.RecordType;
import org.ngbw.sdk.database.DataRecord;
import org.ngbw.sdk.database.SourceDocument;


/**
 * 
 * @author Paul Hoover
 *
 */
public class PdbFinderParser implements RecordFilter, SourceDocumentReader {

	/**
	 *
	 */
	private static class PolymerChain {
		public String identity;
		public String sequence;
	}


	private static final Log m_log = LogFactory.getLog(PdbFinderParser.class);
	private static final RecordType m_recordType = RecordType.PROTEIN_SEQUENCE;
	private static final Set<SourceDocumentType> m_sourceTypes = new TreeSet<SourceDocumentType>();
	private static final Pattern m_identityPattern = Pattern.compile("^ID\\s*:.*$");
	private static final Pattern m_headerPattern = Pattern.compile("^Header\\s*:.*$");
	private static final Pattern m_sourcePattern = Pattern.compile("^Source\\s*:.*$");
	private static final Pattern m_chainPattern = Pattern.compile("^Chain\\s*:.*$");
	private static final Pattern m_sequencePattern = Pattern.compile("^\\s+Sequence\\s*:.*$");
	private static final Pattern m_recordEndPattern = Pattern.compile("^\\s*//\\s*$");

	static {
		m_sourceTypes.add(new SourceDocumentType(EntityType.NUCLEIC_ACID, DataType.SEQUENCE, DataFormat.PDB_FINDER));
		m_sourceTypes.add(new SourceDocumentType(EntityType.PROTEIN, DataType.SEQUENCE, DataFormat.PDB_FINDER));
	}

	private final ConversionRegistry m_convRegistry;
	private final Set<RecordFieldType> m_recordFields;
	private final Queue<SequenceRecord> m_records = new LinkedList<SequenceRecord>();
	private BufferedReader m_reader;
	private DataFormat m_format;
	private StringBuilder m_recordText;
	private String m_line;


	// constructors


	/**
	 * 
	 */
	public PdbFinderParser()
	{
		m_convRegistry = null;
		m_recordFields = null;
		m_line = "";
	}

	/**
	 * 
	 * @param convRegistry
	 */
	public PdbFinderParser(ConversionRegistry convRegistry)
	{
		m_convRegistry = convRegistry;
		m_recordFields = m_convRegistry.getCoreRegistry().getRecordFields(m_recordType);
		m_line = "";
	}


	// public methods


	/**
	 * 
	 */
	public void close()
	{
		if (m_reader != null) {
			try {
				m_reader.close();
			}
			catch (IOException ioErr) {
				m_log.error(ioErr);
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public DataFormat getFilteredFormat()
	{
		return m_format;
	}

	/**
	 * 
	 * @return
	 */
	public boolean hasNext()
	{
		return !m_records.isEmpty() || m_line != null;
	}

	/**
	 * 
	 * @return
	 */
	public String next()
	{
		try {
			SequenceRecord record = nextRecord();

			if (record == null)
				throw new NullPointerException("No more records!");

			return record.recordText;
		}
		catch (IOException ioErr) {
			throw new RuntimeException(ioErr);
		}
	}

	public void setInput(BufferedReader br)
	{
		m_records.clear();

		m_reader = br;
	}

	public boolean configure(DataFormat cfg)
	{
		m_format = cfg;

		return isConfigured();
	}

	public boolean isConfigured()
	{
		return m_format != null;
	}

	public Set<SourceDocumentType> getSupportedTypes()
	{
		return m_sourceTypes;
	}

	public GenericDataRecordCollection<IndexedDataRecord> read(SourceDocument document) throws IOException, SQLException, ParseException
	{
		String data = new String(document.getData());

		setInput(new BufferedReader(new StringReader(data)));

		int index = 0;
		SequenceRecord record;
		GenericDataRecordCollection<IndexedDataRecord> result = new GenericDataRecordCollectionImpl<IndexedDataRecord>(m_recordType, m_recordFields);

		while ((record = nextRecord()) != null) {
			IndexedDataRecord newRecord = new IndexedDataRecord(m_recordType, m_recordFields, index);

			newRecord.getField(RecordFieldType.PRIMARY_ID).setValue(record.primaryId);
			newRecord.getField(RecordFieldType.NAME).setValue(record.name);
			newRecord.getField(RecordFieldType.ORGANISM).setValue(record.organism);

			result.add(newRecord);

			index += 1;
		}

		return result;
	}

	public DataRecord readSingle(SourceDocument document) throws IOException, SQLException, ParseException
	{
		String data = new String(document.getData());

		setInput(new BufferedReader(new StringReader(data)));

		SequenceRecord record = nextRecord();
		IndexedDataRecord result = new IndexedDataRecord(m_recordType, m_recordFields, 0);

		result.getField(RecordFieldType.PRIMARY_ID).setValue(record.primaryId);
		result.getField(RecordFieldType.NAME).setValue(record.name);
		result.getField(RecordFieldType.ORGANISM).setValue(record.organism);

		return result;
	}

	public ValidationResult validate(SourceDocument document) throws IOException, SQLException
	{
		ValidationResult result = new ValidationResult();
		String data = new String(document.getData());
		boolean hasRecord = false;

		setInput(new BufferedReader(new StringReader(data)));

		while (hasNext()) {
			String record = next();

			if (record != null) {
				hasRecord = true;

				break;
			}
		}

		close();

		if (hasRecord)
			document.setValidated();
		else
			result.addError("Document does not contain PDB Finder records");

		return result;
	}

	public ConversionRegistry getConversionRegistry()
	{
		return m_convRegistry;
	}


	// package methods


	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	SequenceRecord nextRecord() throws IOException
	{
		if (m_records.isEmpty()) {
			if (!findFirstLine())
				return null;

			String identity = parseField();
			String name = "";
			String organism = "";
			List<PolymerChain> chains = new ArrayList<PolymerChain>();

			while (true) {
				if (m_line == null)
					return null;

				if (m_headerPattern.matcher(m_line).matches())
					name = parseField();
				else if (m_sourcePattern.matcher(m_line).matches())
					organism = parseSource();
				else if (m_chainPattern.matcher(m_line).matches())
					parseChain(chains);
				else if (m_recordEndPattern.matcher(m_line).matches()) {
					String text = m_recordText.toString();

					for (Iterator<PolymerChain> elements = chains.iterator() ; elements.hasNext() ; ) {
						PolymerChain chain = elements.next();
						SequenceRecord newRecord = new SequenceRecord();

						newRecord.primaryId = identity + "-" + chain.identity;
						newRecord.name = name;
						newRecord.organism = organism;
						newRecord.sequence = chain.sequence;
						newRecord.recordText = text;

						m_records.add(newRecord);
					}

					break;
				}
				else
					readAndStoreLine();
			}
		}

		return m_records.remove();
	}


	// private methods


	/**
	 * 
	 * @param pattern
	 * @return
	 * @throws IOException
	 */
	private boolean findFirstLine() throws IOException
	{
		m_recordText = new StringBuilder();

		while (true) {
			readLine();

			if (m_line == null)
				return false;

			if (m_identityPattern.matcher(m_line).matches()) {
				storeLine();

				break;
			}
		}

		return true;
	}

	/**
	 * 
	 * @throws IOException
	 */
	private void readAndStoreLine() throws IOException
	{
		readLine();
		storeLine();
	}

	/**
	 * 
	 * @throws IOException
	 */
	private void readLine() throws IOException
	{
		m_line = m_reader.readLine();
	}

	/**
	 * 
	 */
	private void storeLine()
	{
		m_recordText.append(m_line);
		m_recordText.append('\n');
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	private String parseField() throws IOException
	{
		String[] subStrings = m_line.split("\\s+:\\s+", 2);

		readAndStoreLine();

		if (subStrings.length > 1)
			return subStrings[1];
		else
			return "";
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	private String parseSource() throws IOException
	{
		StringBuilder field = new StringBuilder();
		String[] subStrings = m_line.split("\\s+:\\s+", 2);

		if (subStrings.length > 1)
			field.append(subStrings[1]);

		while (true) {
			readAndStoreLine();

			if (m_line == null || !m_sourcePattern.matcher(m_line).matches())
				break;

			subStrings = m_line.split("\\s+:\\s+", 2);

			if (subStrings.length > 1) {
				field.append(' ');
				field.append(subStrings[1]);
			}
		}

		return field.toString();
	}

	/**
	 * 
	 * @param ids
	 * @throws IOException
	 */
	private void parseChain(List<PolymerChain> chains) throws IOException
	{
		String[] subStrings = m_line.split("\\s+:\\s+", 2);

		if (subStrings.length < 2)
			return;

		PolymerChain newChain = new PolymerChain();

		newChain.identity = subStrings[1];

		while (true) {
			readAndStoreLine();

			if (m_line == null || !Character.isWhitespace(m_line.charAt(0)))
				break;

			if (m_sequencePattern.matcher(m_line).matches()) {
				subStrings = m_line.split("\\s+:\\s+", 2);

				if (subStrings.length > 1)
					newChain.sequence = subStrings[1];
			}
		}

		chains.add(newChain);
	}
}
