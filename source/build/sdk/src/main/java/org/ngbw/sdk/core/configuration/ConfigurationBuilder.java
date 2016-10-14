/**
 *
 */
package org.ngbw.sdk.core.configuration;

import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.common.util.BeanUtils;
import org.ngbw.sdk.common.util.Resource;

/**
 * @author Roland H. Niedner <br />
 * 
 */
public class ConfigurationBuilder {

	private static Log log = LogFactory.getLog(ConfigurationBuilder.class);

	public static Configuration build(Configuration config, Resource resource) {
		if (log.isInfoEnabled()) log.info("Start Building Workbench Configuration");
		Properties properties = resource.getProperties();
		config = buildIt(config, properties);
		if (log.isInfoEnabled()) log.info("Workbench Configuration complete");
		return config;
	}

	public static Configuration buildIt(Configuration config,
			Properties properties) {
		String field = null;
		try {
			Enumeration<Object> keys = properties.keys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement().toString();
				field = key;//keep track of the current field for error message
				String value = properties.getProperty(key).trim();
				if (value == null || value.length() == 0) {
					throw new WorkbenchException("Missing value for " + key);
				} else {
					BeanUtils.setPropertyFromString(config, key.trim(), value);
					if (log.isInfoEnabled()) log.info("Set " + key + " = " + value);
				}
			}
		} catch (SecurityException e) {
			throw new WorkbenchException("The configuration property " + field
					+ " could not be set. Mutator has to be public!", e);
		} catch (IllegalArgumentException e) {
			throw new WorkbenchException("The configuration property " + field
					+ " could not be set. Wrong argument!", e);
		} catch (NoSuchMethodException e) {
			throw new WorkbenchException("The configuration property " + field
					+ " could not be set. Method does not exist!", e);
		} catch (IllegalAccessException e) {
			throw new WorkbenchException("The configuration property " + field
					+ " could not be set. Illegal access!", e);
		} catch (InvocationTargetException e) {
			throw new WorkbenchException(
					"The configuration can not be built! Invocation Error", e);
		}
		return config;
	}
}
