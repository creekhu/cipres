package org.ngbw.cipresrest.webresource;


import java.util.Date;
import java.util.Map;
import java.util.ArrayList;

import org.ngbw.sdk.database.Application;
import org.ngbw.sdk.database.TaskLogMessage;
import org.ngbw.sdk.database.User;
import org.ngbw.sdk.jobs.JobStatus;
import org.ngbw.sdk.jobs.JobMetadata;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.ngbw.restdatatypes.LinkData;
import org.ngbw.restdatatypes.StatusData;
import org.ngbw.restdatatypes.JobMessage;


public class JStatus 
{
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(JStatus.class.getName());
	private JobStatus jobStatus;
	BaseResource resource;
	JobUriBuilder builder;

	public JStatus(String jobHandle, BaseResource resource, Application application) throws Exception
	{
		this.resource = resource;
		jobStatus = new JobStatus(jobHandle, application);
		builder = resource.getJobUriBuilder(jobHandle, jobStatus.getUser().getUsername());
	}

	public JStatus(String jobHandle, BaseResource resource, User user, Application application) throws Exception
	{
		this.resource = resource;
		jobStatus = new JobStatus(jobHandle, user, application);
		builder = resource.getJobUriBuilder(jobHandle, jobStatus.getUser().getUsername());
	}


	public StatusData toStatusData() throws Exception
	{
		StatusData sd = new StatusData();
		sd.selfUri = builder.getJobUri();
		sd.resultsUri = builder.getResultListUri();
		sd.workingDirUri = builder.getWorkingListUri();
		sd.jobHandle = getJobHandle();
		sd.metadata = getMetadata();
		sd.jobStage = getJobStage();
		sd.terminalStage = isTerminalStage();
		sd.failed = !jobStatus.isOk();
		sd.dateSubmitted = getDateSubmitted();
		ArrayList<JobMessage> messageList = new ArrayList<JobMessage>();
		for (TaskLogMessage message : jobStatus.getMessages())
		{
			JobMessage jm = new JobMessage();
			jm.text = message.getMessage();
			jm.stage = message.getStage().toString();
			jm.timestamp = message.getCreationDate();
			messageList.add(jm);
		}
		sd.message = messageList;

		// TODO: get this from a properties file or something we can easily change.
		sd.minPollIntervalSeconds = 60L;

		return sd;
	}

	public String getJobHandle() {
		return jobStatus.getJobHandle();
	}

	public void setJobHandle(String jobHandle) {
		jobStatus.setJobHandle(jobHandle);
	}

	public Map<String, String> getMetadata() throws Exception
	{
		JobMetadata m = jobStatus.getMetadata();
		if (m == null)
		{
			return null;
		}
		return m.toMap();
	}

	public void setMetadata(Map<String, String> properties) throws Exception
	{
		JobMetadata m = JobMetadata.fromMap(properties);
		jobStatus.setMetadata(m);
	}

	public String getJobStage() {
		return jobStatus.getJobStage();
	}

	public void setJobStage(String jobStage) {
		jobStatus.setJobStage(jobStage);
	}

	public boolean isTerminalStage() {
		return jobStatus.isTerminalStage();
	}

	public void setTerminalStage(boolean terminalStage) {
		jobStatus.setTerminalStage(terminalStage);
	}

	public boolean isOk() {
		return jobStatus.isOk();
	}

	public void setOk(boolean ok) {
		jobStatus.setOk(ok);
	}

	public Date getDateSubmitted() {
		return jobStatus.getDateSubmitted();
	}

	public void setDateSubmitted(Date dateSubmitted) {
		jobStatus.setDateSubmitted(dateSubmitted);
	}

}
