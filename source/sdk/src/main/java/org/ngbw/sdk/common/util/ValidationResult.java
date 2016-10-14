package org.ngbw.sdk.common.util;


import java.util.ArrayList;
import java.util.List;

/**
 * @author Roland H. Niedner <br />
 *
 */
public class ValidationResult {

	private List<String> warnings = new ArrayList<String>();
	private List<String> errors = new ArrayList<String>();

	/**
	 * @return the valid
	 */
	public boolean isValid() {
		return errors.isEmpty();
	}
	/**
	 * @return the warnings
	 */
	public List<String> getWarnings() {
		return warnings;
	}
	/**
	 * @param warnings the warnings to set
	 */
	public void setWarnings(List<String> warnings) {
		this.warnings = warnings;
	}
	/**
	 * @param warning the warning to add
	 */
	public void addWarning(String warning) {
		this.warnings.add(warning);
	}
	/**
	 * @return the errors
	 */
	public List<String> getErrors() {
		return errors;
	}
	/**
	 * @param errors the errors to set
	 */
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
	/**
	 * @param error the error to add
	 */
	public void addError(String error) {
		this.errors.add(error);
	}

	public void displayErrors() {
		for (String error : errors) {
			System.out.println(error);
		}
	}

	public void displayWarnings() {
		for (String warning : warnings) {
			System.out.println(warning);
		}
	}
}
