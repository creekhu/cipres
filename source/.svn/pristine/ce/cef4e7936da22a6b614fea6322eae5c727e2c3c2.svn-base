package org.ngbw.web.actions;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.database.Statistics;
import org.ngbw.sdk.database.Task;

/**
 * to insert the data for the register 'Statistics'
 * 
 * @author Juliane
 *
 */

import org.ngbw.sdk.database.Statistics;


public class RegisterStatistics extends SessionManager 
{
	private static final Log log = LogFactory.getLog(RegisterStatistics.class.getName());
	
	public String execute() throws Exception
	{
		List<Statistics> list = Statistics.findAllStatistics();
		List<Statistics> list2 = new ArrayList<Statistics>();
		for (Statistics statistics : list) 
		{
			try 
			{
				//Task task = statistics.getTask();
			  	list2.add(statistics);
			} catch (Exception e) 
			{
				log.debug("No Task is found..." + statistics.getTaskId());
			}
		}
		setSessionAttribute(LIST, list2); 
		return SUCCESS;
	}
	
	
	
	public List<Statistics> getList()
	{
		try 
		{
			return Statistics.findAllStatistics();
		} 
		catch (Throwable error) 
		{
			return null;
		}
	}

		

}
