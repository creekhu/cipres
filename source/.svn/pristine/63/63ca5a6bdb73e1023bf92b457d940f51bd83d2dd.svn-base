package org.ngbw.sdk.tool;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

public class SchedulerProperties
{
	public static final String RUNHOURS = "runhours";
	public static final String JOBTYPE = "jobtype";
	public static final String NODES = "nodes";
	public static final String MPI_PROCESSES = "mpi_processes";
	public static final String NODE_EXCLUSIVE = "node_exclusive";
	public static final String THREADS_PER_PROCESS = "threads_per_process";

	public static final String JOBTYPE_MPI = "mpi";
	public static final String JOBTYPE_DIRECT = "direct";

	public static Double getRunHours(Properties p)
	{
		try
		{
			return Double.parseDouble(p.getProperty(RUNHOURS));
		}
		catch(Exception e)
		{
			return null;
		}
	}

	public static Long getNodes(Properties p)
	{
		try
		{
			return Long.parseLong(p.getProperty(NODES));
		}
		catch(Exception e)
		{
			return null;
		}
	}

	public static Long getMpiProcesses(Properties p)
	{
		try
		{
			return Long.parseLong(p.getProperty(MPI_PROCESSES));
		}
		catch(Exception e)
		{
			return null;
		}
	}

	public static Long getThreadsPerProcess(Properties p)
	{
		try
		{
			return Long.parseLong(p.getProperty(THREADS_PER_PROCESS));
		}
		catch(Exception e)
		{
			return null;
		}
	}


	public static boolean isMpi(Properties p)
	{
		String jt = p.getProperty(JOBTYPE);
		if (jt == null)
		{
			return false;
		}
		jt = jt.trim();
		return jt.equals(JOBTYPE_MPI);
	}

	public static boolean isDirect(Properties p)
	{
		String jt = p.getProperty(JOBTYPE);
		if (jt == null)
		{
			return false;
		}
		jt = jt.trim();
		return jt.equals(JOBTYPE_DIRECT);
	}

	public static boolean isNodeExclusive(Properties p)
	{
		String tmp = p.getProperty(NODE_EXCLUSIVE);
		if (tmp == null)
		{
			return false;
		}
		tmp = tmp.trim();
		return tmp.equalsIgnoreCase("true") || tmp.equals("1");
	}


	/*
		Convert string of format name=value;name=value;name=value ... to Properties object.
	*/
	public static Properties string2Properties(String pstring) throws IOException
	{
		pstring = pstring.replace(';', '\n');
		
		Properties properties = new Properties();
		properties.load(new ByteArrayInputStream(pstring.getBytes()));
		return properties;
	}

	public static String properties2String(Properties p) throws IOException
	{
		StringBuffer sb = new StringBuffer();
		for (Enumeration<?> e = p.propertyNames(); e.hasMoreElements() ;)
		{
			String name = (String)e.nextElement();
			sb.append(name + "=" +  p.getProperty(name) + ";");
		}
		return sb.toString();
	}
}
