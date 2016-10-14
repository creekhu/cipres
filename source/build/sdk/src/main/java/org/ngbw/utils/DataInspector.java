/*
 * DataInspector.java
 */
package org.ngbw.utils;


import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

import org.ngbw.sdk.database.ConnectionManager;
import org.ngbw.sdk.database.DriverConnectionSource;
import org.ngbw.sdk.database.RecordField;
import org.ngbw.sdk.database.UserDataItem;
import org.ngbw.sdk.database.UserItemDataRecord;


/**
 * 
 * @author Paul Hoover
 *
 */
public class DataInspector {

	private static final String INDENT = "   ";


	public static void inspect(long id) throws IOException, SQLException
	{
		UserDataItem inspected = new UserDataItem(id);

		

		inspect(inspected);
	}

	public static void inspect(UserDataItem inspected) throws IOException, SQLException
	{
		System.out.println("Label: " + inspected.getLabel().replaceAll("\\s+", " "));
		System.out.println("Key:   " + inspected.getUserDataId());
		System.out.println("Date:  " + inspected.getCreationDate());
		System.out.println("Type:  " + inspected.getType());
		System.out.println("\nMeta Data:");

		for (Iterator<Map.Entry<String, String>> entries = inspected.metaData().entrySet().iterator() ; entries.hasNext() ; ) {
			Map.Entry<String, String> metaData = entries.next();

			System.out.println(INDENT + metaData.getKey() + " = " + metaData.getValue());
		}

		System.out.println("\nRecord Fields:");

		for (Iterator<UserItemDataRecord> records = inspected.dataRecords().iterator() ; records.hasNext() ; ) {
			UserItemDataRecord record = records.next();

			System.out.println(INDENT + "Record Type " + record.getRecordType());

			for (Iterator<RecordField> fields = record.getFields().iterator() ; fields.hasNext() ; ) {
				RecordField field = fields.next();

				System.out.println(INDENT + INDENT + "Field " + field.getFieldType() + " = " + field.getValueAsString());
			}

			System.out.println();
		}

		String fileName = inspected.getLabel().replaceAll("\\W", "_") + ".txt";
		OutputStream outStream = new BufferedOutputStream(new FileOutputStream(fileName));
		InputStream inStream = inspected.getDataAsStream();

		try {
			byte[] readBuffer = new byte[8192];
			int bytesRead;

			while ((bytesRead = inStream.read(readBuffer, 0, readBuffer.length)) >= 0)
				outStream.write(readBuffer, 0, bytesRead);
		}
		finally {
			inStream.close();
			outStream.close();
		}

		System.out.println("Wrote " + fileName);
	}

	public static void main(String[] args)
	{
		try {
			if (args.length != 1)
				throw new Exception("usage: DataInspector id");

			ConnectionManager.setConnectionSource(new DriverConnectionSource());

			inspect(Long.valueOf(args[0]));
		}
		catch (Exception err) {
			err.printStackTrace(System.err);

			System.exit(-1);
		}
	}
}
