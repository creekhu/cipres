/*
 * Dataset.java
 */
package org.ngbw.sdk.core.types;

import java.io.Serializable;

import org.ngbw.sdk.common.util.StringType;

/**
 * 
 * @author Roland H. Niedner
 * 
 */
public class Dataset extends StringType implements Serializable {

	public static final Dataset UNKNOWN = Dataset.valueOf("UNKNOWN");
	
	private static final long serialVersionUID = -5492218546723460210L;

	protected Dataset(String value) {
		super(value);
	}
	
	public static Dataset valueOf(String value) {
		return new Dataset(value);
	}

	public boolean equals(Object other) {
		final Dataset that = (Dataset) other;
		return value.equals(that.value);
	}
}