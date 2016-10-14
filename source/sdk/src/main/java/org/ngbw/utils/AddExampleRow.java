/*
 * AddExampleRow.java
 */

package org.ngbw.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.ngbw.sdk.database.ConnectionManager;
import org.ngbw.sdk.database.DriverConnectionSource;
import org.ngbw.sdk.database.ExampleRow;




public class AddExampleRow
{

	private static int  example_id        = 1;
	private static int  example_id_delete = 22;
	private static long task_id_insert    = 0;
	private static long task_id_compare   = 22;
	private static long task_id_jobhandle = 955;
	private static long task_id_email     = 1000;
	private static long task_id_date      = 228800;
	
	
	public static void main(String[] args)
	{
		try {
			ConnectionManager.setConnectionSource(new DriverConnectionSource());
			
			
			/**
			 *  do stuff...
			 */
			 System.out.println("test.....");
			
			/**
			 * to write a new TASK_ID
			 */
			 //ExampleRow row = new ExampleRow();
			 //row.setTaskId(task_id_insert);
			 //row.save();

			 /**
			  * compare task_id with other task_id
			  */
			  ExampleRow row = new ExampleRow();
//			  System.out.println(row.equals(task_id_insert));
			  Object compared = row.equals(task_id_compare);
			  System.out.println(compared);
			  
			  /**
			   *  delete a TASK_ID
			   */
			   //ExampleRow row = new ExampleRow(26); // (1) means the number of the EXAMPLE_ID-column in the example_table-TABLE
			   //row.delete();
			   //row.save();
			  
			  /**
			   * to get JOBHANDLE
			   */
				//ExampleRow row = new ExampleRow();
				//row.setTaskId(task_id_jobhandle);
				//System.out.println("TASK_ID: " + task_id_jobhandle+ " | JOBHANDLE: " + row.getTask().getJobHandle());
			
			
			/**
			 * to get EMail
		     */
			  //ExampleRow row = new ExampleRow();
			  //row.setTaskId(task_id_email);
			  //System.out.println("TASK_ID: " + task_id_email + " | EMAIL: " + row.getTask().getUser().getEmail());
			
			
			/**
			 *-------------------GET ACTUAL_DATE_TIME---------------------
			 */
				/**
				 * get date from ExampleRow CLASS - date-methods   
				 */
				 //ExampleRow row = new ExampleRow();
				 //System.out.println("This is the actual Date with 'java.util.Date': " + row.actualDateWithJavaUtilDate());

		       /**
		        * to store the Date in DB-column
		        */
			   //ExampleRow row = new ExampleRow();
			   //Date actualDateTime = new Date();
			   //row.setActualDateTime(actualDateTime);
			   //row.setTaskId(task_id_date);
			   //row.save();
			
		}
		catch (Exception err) {
			err.printStackTrace(System.err);

			System.exit(-1);
		}
	}
}
