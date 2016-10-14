/*
 * BaseSourceDocumentConverter.java
 */
package org.ngbw.sdk.conversion;


import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ngbw.sdk.api.conversion.ConversionRegistry;
import org.ngbw.sdk.api.conversion.SourceDocumentConverter;
import org.ngbw.sdk.core.shared.SourceDocumentType;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.EntityType;
import org.ngbw.sdk.database.SourceDocument;


/**
 * 
 * @author Roland H. Niedner
 *
 */
public abstract class BaseSourceDocumentConverter implements SourceDocumentConverter {

	protected final ConversionRegistry conversionRegistry;
	protected Map<SourceDocumentType, Set<SourceDocumentType>> conversions = new HashMap<SourceDocumentType, Set<SourceDocumentType>>();

	protected BaseSourceDocumentConverter(ConversionRegistry conversionRegistry) {
		this.conversionRegistry = conversionRegistry;
	}
	
	public Map<SourceDocumentType, Set<SourceDocumentType>> getSupportedConversions() {
		return conversions;
	}
	
	public Set<SourceDocumentType> getTargetTypes() {
		Set<SourceDocumentType> targetTypes = new HashSet<SourceDocumentType>();
		for (Set<SourceDocumentType> types : conversions.values())
			targetTypes.addAll(types);
		return targetTypes;
	}

	public boolean canRead(SourceDocument srcDoc) {
		return conversions.containsKey(srcDoc.getType());
	}
	
	public boolean canWrite(SourceDocumentType targetKey) {
		return getTargetTypes().contains(targetKey);
	}

	public abstract SourceDocument convert(SourceDocument srcDoc, SourceDocumentType targetKey) throws IOException, SQLException, ParseException;
	
	protected SourceDocumentType SourceDocumentType(EntityType entityType, DataType dataType, DataFormat dataFormat) {
		return new SourceDocumentType(entityType, dataType, dataFormat);
	}
	
	protected void addConversion(SourceDocumentType srcType, SourceDocumentType targetType) {
		if (srcType == null)
			throw new NullPointerException("srcType must not be null!");
		if (targetType == null)
			throw new NullPointerException("targetType must not be null!");
		Set<SourceDocumentType> targetTypes;
		if (conversions.containsKey(srcType) == false)
			targetTypes = new HashSet<SourceDocumentType>();
		else 
			targetTypes = conversions.get(srcType);
		targetTypes.add(targetType);
		conversions.put(srcType, targetTypes);
	}
}
