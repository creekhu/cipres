package org.ngbw.sdk.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Boilerplate code fore several java.io methods.
 * <br />
 * @author hannes
 *
 */
public class IOUtils {
	/**
	 * Converts an object to an array of bytes
	 * 
	 * @param object
	 *            the object to convert.
	 * @return the associated byte array.
	 * @throws IOException 
	 */
	public static byte[] toBytes(Serializable object) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);

		oos.writeObject(object);

		return baos.toByteArray();
	}

	/**
	 * Converts an array of bytes back to its constituent object. The input
	 * array is assumed to have been created from the original object.
	 * 
	 * @param bytes
	 *            the byte array to convert.
	 * @return the associated object.
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public static Object toObject(byte[] bytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bais);

		return ois.readObject();
	}

}
