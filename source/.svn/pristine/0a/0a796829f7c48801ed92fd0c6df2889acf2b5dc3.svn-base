/*
 * BlastXMLReader.java
 */
package org.ngbw.sdk.conversion;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.ngbw.sdk.api.conversion.ConversionRegistry;
import org.ngbw.sdk.api.core.CoreRegistry;
import org.ngbw.sdk.api.core.GenericDataRecordCollection;
import org.ngbw.sdk.bioutils.blast.blastxml.BlastOutput;
import org.ngbw.sdk.bioutils.blast.blastxml.BlastOutputIterations;
import org.ngbw.sdk.bioutils.blast.blastxml.Hit;
import org.ngbw.sdk.bioutils.blast.blastxml.Hsp;
import org.ngbw.sdk.bioutils.blast.blastxml.Iteration;
import org.ngbw.sdk.common.util.ValidationResult;
import org.ngbw.sdk.core.shared.IndexedDataRecord;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.EntityType;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.core.types.RecordType;
import org.ngbw.sdk.database.DataRecord;
import org.ngbw.sdk.database.SourceDocument;


/**
 * 
 * @author Roland H. Niedner
 *
 */
public class BlastXMLReader extends BaseSourceDocumentReader {

	private void init() {
		srcTypes.add(getSourceDocumentType(EntityType.PROTEIN, DataType.BLAST_OUTPUT, DataFormat.BLAST_XML));
		srcTypes.add(getSourceDocumentType(EntityType.NUCLEIC_ACID, DataType.BLAST_OUTPUT, DataFormat.BLAST_XML));
	}
	
	public BlastXMLReader(ConversionRegistry conversionRegistry) {
		super(conversionRegistry);
		init();
	}

	public ValidationResult validate(SourceDocument document) {
		ValidationResult result = new ValidationResult();
		try {
			parse(document.getData());
		} catch (Exception e) {
			result.addError(e.getLocalizedMessage());
		}
		if (result.isValid()) document.setValidated();
		return result;
	}

	public GenericDataRecordCollection<IndexedDataRecord> read(SourceDocument document) throws IOException, SQLException, ParseException {
		GenericDataRecordCollection<IndexedDataRecord> drc = getDataRecordCollection(document);
		BlastOutput blast = parse(document.getData());
		String queryId = blast.getBlastOutputQueryID().getContent();
		BlastOutputIterations iter = blast.getBlastOutputIterations();
		Iteration it;
		int prevItNum = 0;
		while ((it = iter.getIteration()) != null) {
			int itNum = Integer.valueOf(it.getIterationIterNum().getContent());
			if(prevItNum >= itNum) break;
			prevItNum = itNum;
			List<Hit> hits = it.getIterationHits().getHit();
			for(int i=0; i<hits.size(); i++) {
				Hit hit = hits.get(i);
				IndexedDataRecord dr = getDataRecord(i, drc.getRecordType());
				populateDataRecord(dr, hit, queryId);
				drc.add(dr);
			}
		}
		return drc;
	}

	public IndexedDataRecord readSingle(SourceDocument document) throws IOException, SQLException, ParseException {
		CoreRegistry coreRegistry = conversionRegistry.getCoreRegistry();
		RecordType recordType = coreRegistry.getRecordType(document.getEntityType(), document.getDataType()); 
		IndexedDataRecord dr = getDataRecord(0, recordType);
		BlastOutput blast = parse(document.getData());
		String queryId = blast.getBlastOutputQueryID().getContent();
		BlastOutputIterations iter = blast.getBlastOutputIterations();
		Iteration it;
		int prevItNum = 0;
		while ((it = iter.getIteration()) != null) {
			//we kill it after the first query
			if (prevItNum > 0)
				throw new RuntimeException("More than 1 query listed in the SourceDocument!");
			int itNum = Integer.valueOf(it.getIterationIterNum().getContent());
			if(prevItNum >= itNum) break;
			prevItNum = itNum;
			List<Hit> hits = it.getIterationHits().getHit();
			if(hits.size() > 1)
				throw new RuntimeException("More than 1 hit listed in the SourceDocument!");
			populateDataRecord(dr, hits.get(0), queryId);
		}
		return dr;
	}

	private BlastOutput parse(byte[] data) {
	    try {
	    	BufferedReader br = new BufferedReader(new StringReader(new String(data)));
	    	String line;
	    	StringBuilder sb = new StringBuilder();
	    	boolean capture = false;
	    	while((line = br.readLine()) != null) {
	    		if ("<BlastOutput>".equals(line)) capture = true;
	    		if (capture) sb.append(line);
	    	}
	    	
			JAXBContext jc = JAXBContext.newInstance( "org.ngbw.sdk.conversion.blastxml" ); 

			Unmarshaller u = jc.createUnmarshaller();
			BlastOutput blast =
			  (BlastOutput)u.unmarshal(new ByteArrayInputStream(sb.toString().getBytes())); 
			return blast;
		} catch (JAXBException e) {
			throw new RuntimeException("Parsing error!");
		} catch (IOException e) {
			throw new RuntimeException("Error reading input data!");
		}
	}
	
	private void populateDataRecord(DataRecord dr, Hit hit, String queryId) throws ParseException {
		//we only care about the first alignment
		Hsp hsp = hit.getHitHsps().getHsp().get(0);
		Integer numPositives = Integer.valueOf(hsp.getHspPositive().getContent());
		Integer alignLen = Integer.valueOf(hsp.getHspAlignLen().getContent());
		Double percentIdentity = (double) numPositives / (double) alignLen;

		dr.getField(RecordFieldType.QUERY_ID).setValue(queryId);
		dr.getField(RecordFieldType.SUBJECT_ID).setValue(hit.getHitId().getContent());
		dr.getField(RecordFieldType.SCORE).setValue(hsp.getHspScore().getContent());
		dr.getField(RecordFieldType.E_VALUE).setValue(hsp.getHspEvalue().getContent());
		dr.getField(RecordFieldType.NUMBER_OF_IDENTITIES).setValue(hsp.getHspIdentity().getContent());
		dr.getField(RecordFieldType.NUMBER_OF_POSITIVES).setValue(numPositives);
		dr.getField(RecordFieldType.ALIGNMENT_LENGTH).setValue(alignLen);
		dr.getField(RecordFieldType.PERCENTAGE_IDENTITY).setValue(percentIdentity);
		dr.getField(RecordFieldType.QUERY_START).setValue(hsp.getHspQueryFrom().getContent());
		dr.getField(RecordFieldType.QUERY_END).setValue(hsp.getHspQueryTo().getContent());
		dr.getField(RecordFieldType.SUBJECT_START).setValue(hsp.getHspHitFrom().getContent());
		dr.getField(RecordFieldType.SUBJECT_END).setValue(hsp.getHspHitTo().getContent());
		dr.getField(RecordFieldType.ALIGNED_QUERY_SEQUENCE).setValue(hsp.getHspQseq().getContent());
		dr.getField(RecordFieldType.ALIGNED_SUBJECT_SEQUENCE).setValue(hsp.getHspHseq().getContent());
	}
}
