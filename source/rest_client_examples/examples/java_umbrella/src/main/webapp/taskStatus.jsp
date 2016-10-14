<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
 pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Login page</title>
<link rel="stylesheet" type="text/css" href="style.css" />
</head>
<body>
<p>
<p>
<%@include file ="navbar.jsp" %>
<%@include file ="header.jsp" %>

<s:url action="taskDelete" var="taskDeleteUrl">
	<s:param name="cipresUrl"><s:property value="statusData.selfUri.url"/></s:param>
</s:url>
<a href="${taskDeleteUrl}">DELETE This Job.</a>

<div style="background:lightyellow; border=solid; width=400px;">
Refresh the page to see the job progress.
</div>

<div>
<p>
<b>JOB STATUS</b>
<br>
JobHandle: <s:property value="statusData.jobHandle"/>
<br>
Date Submitted: <s:date name="statusData.dateSubmitted"/>
<br>
Stage: <s:property value="statusData.jobStage"/>
<br>
Finished:<s:if test="statusData.terminalStage"> Yes </s:if> <s:else> No </s:else>
<br>
Error:
<s:if test="statusData.failed">
	<font color = "red">There was an ERROR processing the job! Job failed.</font>
</s:if>
<s:else>
	No
</s:else>
</div>

<s:if test="statusData.terminalStage">
<div>
	<b>RESULTS:</b>
	<p>
	<table>
		<s:iterator value="resultFiles" id="fileData">
			<tr>
				<td> Filename: <s:property value="filename"/> </td>
				<td> Length: <s:property value="length"/> </td>

				<s:url action="download" var="downloadUrl">
					<s:param name="fileUrl"><s:property value="downloadUri.url"/></s:param>
				</s:url>
				<td>
				<a href="${downloadUrl}">Download</a>
				</td>


			</tr>
		</s:iterator>
	</table>
</div>
</s:if>
<s:else>
<div>
	<b>WORKING DIRECTORY</b>
	<div style="background:lightyellow; border=solid; width=400px;">
	Job is NOT finished.  Contents of the working directory will be shown
	below, once the job has been staged, and before CIPRES transfers the results
	back from the execution host.  
	<p>	
	Contents of the working directory will be modified and eventually removed. 
	If you try to download at the moment a file is being moved, you may get an error.  You may also
	see transient error message on this page if the directory listing is attempted right 
	after the contents of the directory have been changed.
	</div>
	<table>
		<s:iterator value="workingDirFiles" id="fileData">
			<tr>
				<td> Filename: <s:property value="filename"/> </td>
				<td> Length: <s:property value="length"/> </td>

				<s:url action="download" var="downloadUrl">
					<s:param name="fileUrl"><s:property value="downloadUri.url"/></s:param>
				</s:url>
				<td>
				<a href="${downloadUrl}">Download</a>
				</td>

			</tr>
		</s:iterator>
	</table>
</div>
</s:else>
<br>
<b>TASK MESSAGES</b>
<br>
<table class="taskmessages">
	<s:iterator value="statusData.message" id="taskMessage">
		<tr>
			<td><s:date name="timestamp"/> </td>
			<td><s:property value="stage"/> </td>
			<td><s:property value="text"/> </td>
		</tr>
	</s:iterator>
</table>
</div>

</body>



