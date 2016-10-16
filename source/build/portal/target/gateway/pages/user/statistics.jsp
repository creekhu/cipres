<%@ include file="/pages/common/taglibs.jsp"%>

<head>
	<title>Statistics</title>
	<link rel="stylesheet" href="<c:url value='/css/displaytag.css'/>" type="text/css" media="screen" title="no title" charset="utf-8"/>
	<meta name="menu" content="Statistics"/>
</head>

<h4><strong>Job Information:</strong></h4>
<br>
<s:set name="list" value="%{#session.list}" scope="request"/>
<display:table name="list"
		       requestURI=""
	           class="dataTable"
	           id="row"
		       export="true"
               pagesize="50"
	           cellspacing="1"
	           cellpadding="7">

<display:column title="Statistic ID" sortable="true" style="white-space:nowrap">
		<cc:out value="${row.statisticId}"/>
    </display:column>

    <display:column title="Task ID" sortable="true" style="white-space:nowrap">
		<cc:out value="${row.taskId}"/>
	</display:column>

    <display:column title="Job Start Time" sortable="true" style="white-space:nowrap">
		<cc:out value="${row.jobStartTime}"/>
	</display:column>

	<display:column title="Job End Time" sortable="true" style="white-space:nowrap">
		<cc:out value="${row.jobEndTime}"/>
	</display:column>

	<display:column title="Charge #" sortable="true" style="white-space:nowrap">
		<cc:out value="${row.tgChargeNumber}"/>
	</display:column>


	<display:column title="Process Worker" sortable="true" style="white-space:nowrap">
		<cc:out value="${row.processWorker}"/>
	</display:column>

	<display:column title="Resource" sortable="true" style="white-space:nowrap">
		<cc:out value="${row.resource}"/>
	</display:column>

	<display:column title="Remote Job Id" sortable="true" style="white-space:nowrap">
		<cc:out value="${row.remoteJobId}"/>
    </display:column>




	<display:column title="Submit URL" sortable="true" style="white-space:nowrap">
		<cc:out value="${row.submitUrl}"/>
	</display:column>

	<display:column title="Submit User" sortable="true" style="white-space:nowrap">
		<cc:out value="${row.submitUser}"/>
	</display:column>

	<display:column title="Submit Host" sortable="true" style="white-space:nowrap">
		<cc:out value="${row.submitHost}"/>
	</display:column>

	<display:column title="Process Id" sortable="true" style="white-space:nowrap">
		<cc:out value="${row.processId}"/>
	</display:column>




	<display:column title="SDK Version" sortable="true" style="white-space:nowrap">
		<cc:out value="${row.sdkVersion}"/>
	</display:column>

	<display:column title="Job Handle" sortable="true" style="white-space:nowrap">
		<cc:out value="${row.jobhandle}"/> 
	</display:column >

	<display:column title="EMail" sortable="true" style="white-space:nowrap">
		<cc:out value="${row.email}"/>
	</display:column >

	<display:column title="User Id" sortable="true" style="white-space:nowrap">
		<cc:out value="${row.userId}"/>
	</display:column >

	<display:column title="User Name" sortable="true" style="white-space:nowrap">
		<cc:out value="${row.username}"/>
	</display:column >

	<display:column title="Stage" sortable="true" style="white-space:nowrap">
		<cc:out value="${row.stage}"/>
	</display:column >

	<display:column title="OK" sortable="true" style="white-space:nowrap">
		<cc:out value="${row.ok}"/>
	</display:column >

	<display:column title="Tool Id" sortable="true" style="white-space:nowrap">
		<cc:out value="${row.toolId}"/>
	</display:column >

	<display:column title="Label" sortable="true" style="white-space:nowrap">
		<cc:out value="${row.label}"/>
	</display:column >

	<display:setProperty name="export.pdf" value="true"/>
	<display:setProperty name="basic.empty.showtable" value="true"/>

</display:table>

