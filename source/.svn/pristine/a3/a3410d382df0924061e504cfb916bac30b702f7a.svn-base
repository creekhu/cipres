package org.ngbw.web.model;

import java.util.HashMap;
import java.util.Map;

public class Tab<T>
{
	/*================================================================
	 * Properties
	 *================================================================*/
	private Page<T> contents;
	private String label;
	private Map<String, Object> properties;

	/*================================================================
	 * Constructors
	 *================================================================*/
	public Tab() {
		this(null, null);
	}
	
	public Tab(Page<T> contents, String label) {
		setContents(contents);
		setLabel(label);
	}

	/*================================================================
	 * Property accessor methods
	 *================================================================*/
	public Page<T> getContents() {
		return contents;
	}
	
	public void setContents(Page<T> contents) {
		this.contents = contents;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public Object getProperty(String property) {
		if (properties == null || property == null)
			return null;
		else return properties.get(property);
	}
	
	public void setProperty(String property, Object value) {
		if (property == null)
			return;
		else {
			if (properties == null)
				properties = new HashMap<String, Object>();
			if (value == null)
				properties.remove(property);
			else properties.put(property, value);
		}
	}
	
	public void removeProperty(String property) {
		if (properties != null)
			properties.remove(property);
	}
}
