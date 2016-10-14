package org.ngbw.sdk.common.util;

import java.util.ArrayList;
import java.util.List;

public class SuperString {
	
	private char delimiter;
	
	private String value;
	
	
	private SuperString(String value, char delimiter) {
		this.value = value;
		this.delimiter = delimiter;
	}

	public static SuperString valueOf(String value, char delimiter) {
		return new SuperString(value, delimiter);
	}

	public static SuperString valueOf(int value, char delimiter) {
		return new SuperString(String.valueOf(value), delimiter);
	}

	public static SuperString valueOf(double value, char delimiter) {
		return new SuperString(String.valueOf(value), delimiter);
	}

	public static SuperString valueOf(float value, char delimiter) {
		return new SuperString(String.valueOf(value), delimiter);
	}

	public static SuperString valueOf(boolean value, char delimiter) {
		return new SuperString(String.valueOf(value), delimiter);
	}

	public static SuperString valueOf(String[] values, char delimiter) {
		return new SuperString(join(values, delimiter), delimiter);
	}

	public static SuperString valueOf(Object[] values, char delimiter) {
		return new SuperString(join(values, delimiter), delimiter);
	}

	@SuppressWarnings("unchecked")
	public static SuperString valueOf(List values, char delimiter) {
		return new SuperString(join(values, delimiter), delimiter);
	}
	
	public String toString() {
		return value;
	}
	
	public String concatenate() {
		if (value == null) return null;
		String[] array = split(value, delimiter);
		int length = array.length;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			sb.append(array[i]);
		}
		return sb.toString();
	}
	
	public String[] toArray() {
		return split(value, delimiter);
	}
	
	public List<String> toList() {
		List<String> list = new ArrayList<String>();
		for (String element : split(value, delimiter))
			list.add(element);
		return list;
	}

	protected static String join(String[] array, char delimiter) {
		if (array == null) return null;
		int length = array.length;
		StringBuilder sb = new StringBuilder();
		sb.append(array[0]);
		for (int i = 1; i < length; i++) {
			sb.append(delimiter);
			sb.append(array[i]);
		}
		return sb.toString();
	}

	protected static String join(Object[] array, char delimiter) {
		if (array == null) return null;
		int length = array.length;
		StringBuilder sb = new StringBuilder();
		sb.append(array[0]);
		for (int i = 1; i < length; i++) {
			sb.append(delimiter);
			sb.append(array[i]);
		}
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	protected static String join(List list, char delimiter) {
		if (list == null) return null;
		int length = list.size();
		StringBuilder sb = new StringBuilder();
		sb.append(list.get(0));
		for (int i = 1; i < length; i++) {
			sb.append(delimiter);
			sb.append(list.get(i));
		}
		return sb.toString();
	}
	
	protected static String[] split(String value, char delimiter) {
		return value.split(String.valueOf(delimiter));
	}
}
