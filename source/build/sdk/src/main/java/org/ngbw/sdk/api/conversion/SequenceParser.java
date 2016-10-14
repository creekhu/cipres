/**
 * 
 */
package org.ngbw.sdk.api.conversion;

import org.ngbw.sdk.api.core.Configurable;
import org.ngbw.sdk.core.types.DataFormat;

/**
 * SequenceParser extracts the plain sequence String from
 * all registered Sequence documents.
 * 
 * @author hannes
 *
 */
public interface SequenceParser extends Configurable<DataFormat> {

	/**
	 * Method returns the extracted sequence as a plain String from the
	 * submitted single record String of the submitted DataFormat.
	 * 
	 * @param sourceRecord
	 * @return sequence
	 */
	public String extractSequence(String sourceRecord);
}
