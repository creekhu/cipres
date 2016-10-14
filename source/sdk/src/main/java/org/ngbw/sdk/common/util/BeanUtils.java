/*******************************************************************************
 * Copyright (c) 2006 The Regents of the University of California
 * All rights reserved. Permission to use, copy, modify and distribute
 * any part of this software for educational, research and non-profit
 * purposes, without fee, and without a written agreement is hereby
 * granted, provided that the above copyright notice, this paragraph
 * and the following three paragraphs appear in all copies.
 *
 * Those desiring to incorporate this software into commercial products or
 * use for commercial purposes should contact the Technology Transfer &
 * Intellectual Property Services, University of California, San Diego,
 * 9500 Gilman Drive, Mail Code 0910, La Jolla, CA 92093-0910, Ph: (858)
 * 534-5815, FAX: (858) 534-7345, E-MAIL:invent@ucsd.edu.
 *
 * IN NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY
 * FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES,
 * INCLUDING LOST PROFITS, ARISING OUT OF THE USE OF this software, EVEN IF
 * THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 *
 * THIS SOFTWARE PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE UNIVERSITY
 * OF CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT,
 * UPDATES, ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA
 * MAKES NO REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER
 * IMPLIED OR EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR
 * THAT THE USE OF THE WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER
 * RIGHTS.
 *
 * Contributors:
 *     San Diego Supercomputer Center - SWAMI Team - initial API and implementation
 *******************************************************************************/
package org.ngbw.sdk.common.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BeanUtils extends org.apache.commons.beanutils.BeanUtils {

	public static final String SKIP = "SKIP";

	/**
	 * Set any bean property
	 *
	 * @param bean
	 * @param propertyName
	 * @param value
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static void setPropertyFromString(Object bean, String propertyName,
			String value) throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		String methodName = getSetterName(propertyName);
		Class<?>[] types = new Class[1];
		types[0] = String.class;
		Method method = getMethod(bean.getClass(), methodName, types);
		method.setAccessible(true);
		method.invoke(bean, value);
	}

	/**
	 * Switches the first character of the propertyName to upper case and adds
	 * the prefix
	 *
	 * @param propertyName
	 * @param prefix
	 * @return
	 */
	private static String buildMethodName(String prefix, String propertyName) {
		String firstChar = String.valueOf(propertyName.charAt(0));
		String firstCharUpper = firstChar.toUpperCase();
		return prefix + propertyName.replaceFirst(firstChar, firstCharUpper);
	}

	/**
	 *
	 * @param beanClass
	 * @param methodName
	 * @param parameters
	 * @return method
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	private static Method getMethod(Class<?> beanClass, String methodName,
			Class<?>[] types) throws SecurityException, NoSuchMethodException {
		return beanClass.getDeclaredMethod(methodName, types);
	}

	/**
	 * returns the method name for the property mutator
	 *
	 * @param propertyName
	 * @return methodName
	 */
	public static String getSetterName(String propertyName) {
		return buildMethodName("set", propertyName);
	}

	/**
	 * returns the method name for the property accessor
	 *
	 * @param propertyName
	 * @return methodName
	 */
	public static String getGetterName(String propertyName) {
		return buildMethodName("get", propertyName);
	}

	/**
	 * @param bean
	 * @param property
	 * @return type - Class of that property of that bean
	 */
	@SuppressWarnings({ "rawtypes" })
	public static Class getPropertyType(Object bean, String property) {
		Class type = null;
		try {
			type = bean.getClass().getDeclaredField(property).getType();
		} catch (Exception ex) {
			throw new RuntimeException("Cannot determine type of property " + property + " of class "
					+ bean.getClass() + " " + ex.getMessage(), ex);
		}
		return type;
	}
}
