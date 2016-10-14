/**
 * 
 */
package org.ngbw.sdk.api.conversion;


import java.util.Set;

import org.ngbw.sdk.api.core.CoreRegistryAware;
import org.ngbw.sdk.core.shared.SourceDocumentType;
import org.ngbw.sdk.core.types.DataFormat;


/**
 * @author hannes
 *
 */
public interface ConversionRegistry extends CoreRegistryAware {
	
	public ConversionService getConversionService();

	public Set<SourceDocumentType> getRegisteredDocumentTypes();

	public Set<SourceDocumentType> getReadableDocumentTypes();
	
	public boolean hasReader(SourceDocumentType sourceDocumentType);
	
	public boolean hasConverterFrom(SourceDocumentType sourceType);
	
	public boolean hasConverterTo(SourceDocumentType targetType);
	
	public SourceDocumentReader getReader(SourceDocumentType sourceDocumentType);
	
	public boolean hasConverter(SourceDocumentType sourceKey, SourceDocumentType targetKey);
	
	public SourceDocumentConverter getConverter(SourceDocumentType sourceKey, SourceDocumentType targetKey);

	public RecordFilter getRecordFilter(DataFormat dataFormat);
	
	public Set<DataFormat> getFilterableDataFormats();
	
	public void registerReaderClass(Class<SourceDocumentReader> reader, SourceDocumentType sourceDocumentType);

	public void registerConverterClass(Class<SourceDocumentConverter> converter);

	public void registerRecordFilterClass(Class<RecordFilter> filter, DataFormat dataFormat);
	
	public void registerSequenceParserClass(Class<SequenceParser> parser, DataFormat dataFormat);
	
	public boolean hasSequenceParser(DataFormat dataFormat);
	
	public SequenceParser getSequenceParser(DataFormat dataFormat);

	public Set<SourceDocumentType> getTargetSourceDocumentTypes(
			SourceDocumentType sourceDocumentType);
}
