package org.ngbw.sdk.api.conversion;

import java.io.BufferedReader;

import org.ngbw.sdk.api.core.Configurable;
import org.ngbw.sdk.core.types.DataFormat;

public interface RecordFilter extends Configurable<DataFormat> {

	public void setInput(BufferedReader br);
	
	public boolean hasNext();

	public String next();

	public DataFormat getFilteredFormat();

	public void close();

}