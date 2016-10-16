<%@ taglib prefix="s" uri="/struts-tags" %>

<body onload="resolveParameters()">

<h2>Create new task</h2>

<!-- Task creation step tabs -->
<ul class="nav nav-tabs">
  <s:url id="summaryTabUrl" action="createTask" method="changeTab"
    includeParams="none">
    <s:param name="tab" value="%{'Task Summary'}"/>
  </s:url>
  <s:if test="%{tab == null || tab == 'Task Summary'}">
    <li class="active">
  </s:if>
  <s:else>
    <li>
  </s:else>
    <s:a href="%{summaryTabUrl}">Task Summary</s:a>
  </li>

  <s:url id="dataTabUrl" action="createTask" method="changeTab"
    includeParams="none">
    <s:param name="tab" value="%{'Select Data'}"/>
  </s:url>
  <s:if test="%{tab == 'Select Data'}">
    <li class="active">
  </s:if>
  <s:else>
    <li>
  </s:else>
    <s:a href="%{dataTabUrl}">Select Data</s:a>
  </li>

  <s:url id="toolTabUrl" action="createTask" method="changeTab"
    includeParams="none">
    <s:param name="tab" value="%{'Select Tool'}"/>
  </s:url>
  <s:if test="%{tab == 'Select Tool'}">
    <li class="active">
  </s:if>
  <s:else>
    <li>
  </s:else>
    <s:a href="%{toolTabUrl}">Select Tool</s:a>
  </li>

  <s:url id="parametersTabUrl" action="createTask" method="changeTab"
    includeParams="none">
    <s:param name="tab" value="%{'Set Parameters'}"/>
  </s:url>
  <s:if test="%{tab == 'Set Parameters'}">
    <li class="active">
  </s:if>
  <s:else>
    <li>
  </s:else>
    <s:a href="%{parametersTabUrl}">Set Parameters</s:a>
  </li>
</ul>

<s:if test="%{tab == null || tab == 'Task Summary'}">
  <s:include value="/pages/user/task/taskSummary.jsp"/>
</s:if>
<s:elseif test="%{tab == 'Select Data'}">
  <s:include value="/pages/user/task/selectData.jsp"/>
</s:elseif>
<s:elseif test="%{tab == 'Select Tool'}">
  <s:include value="/pages/user/task/selectTool.jsp"/>
</s:elseif>
<s:elseif test="%{tab == 'Set Parameters'}">
  <div class="tabbedBox">
    <s:if test="%{tool != null}">
      <div id="parameters">
        <s:include value="/pages/tool/%{tool.toLowerCase()}.jsp"/>
      </div>
    </s:if>
    <s:else>
      <div class="callout">
        You must select a tool before you can set parameters.
      </div>
    </s:else>
  </div>
</s:elseif>
