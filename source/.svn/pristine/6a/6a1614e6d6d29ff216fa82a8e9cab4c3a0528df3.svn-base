package org.ngbw.sdk.common.util;

import java.io.Serializable;



/**
 * @author Roland H. Niedner <br />
 *
 */
public abstract class StringType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5767706178866318831L;
	protected String value;

	protected StringType(String value) {
		if (value == null || value.trim().length() == 0)
			throw new NullPointerException("value cannot be null or empty");
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return value.hashCode();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	public boolean equals(Object other) {
		if (other == null) return false;
		if (other instanceof String)
			return value.equalsIgnoreCase((String) other);
		if (this == other)
			return true;
		final StringType that = (StringType) other;
		return value.equals(that.value);
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
}
