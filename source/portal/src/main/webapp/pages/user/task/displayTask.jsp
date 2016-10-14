<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="%{isRefreshable()}">
  <s:url id="refreshTaskUrl" action="task" method="display"
         includeParams="none">
    <s:param name="id" value="currentTaskId"/>
  </s:url>
  <s:a href="%{refreshTaskUrl}" cssClass="btn btn-default pull-right">
    <span class="glyphicon glyphicon-refresh"></span> Refresh Task
  </s:a>
</s:if>
<h2>Task Details</h2>
<div class="form-horizontal">
  <div class="form-group">
    <label class="col-xs-2 control-label">Task</label>
    <div class="col-xs-10">
      <span class="form-control"><s:property value="currentTaskLabel"/></span>
    </div>
  </div>
  <div class="form-group">
    <label class="col-xs-2 control-label">Owner</label>
    <div class="col-xs-10">
      <span class="form-control"><s:property value="currentTaskOwner"/></span>
    </div>
  </div>
  <div class="form-group">
    <label class="col-xs-2 control-label">Group</label>
    <div class="col-xs-10">
      <span class="form-control"><s:property value="currentTaskGroup"/></span>
    </div>
  </div>
  <div class="form-group">
    <label class="col-xs-2 control-label">Date Created</label>
    <div class="col-xs-10">
      <span class="form-control"><s:property value="currentTaskCreationDate"/></span>
    </div>
  </div>
  <div class="form-group">
    <label class="col-xs-2 control-label">Tool</label>
    <div class="col-xs-10">
      <span class="form-control"><s:property value="toolLabel"/></span>
    </div>
  </div>
  <div class="form-group">
    <label class="col-xs-2 control-label">Input</label>
    <div class="col-xs-10">
      <span class="form-control">
        <s:if test="%{hasMainInput()}">
          <s:url id="inputUrl" action="task" method="displayInput" includeParams="none"/>
          <s:a href="javascript:popitup('%{inputUrl}')">
            View (<s:property value="mainInputCount"/>)
          </s:a>
        </s:if>
        <s:else>None</s:else>
      </span>
    </div>
  </div>
  <div class="form-group">
    <label class="col-xs-2 control-label">Parameters</label>
    <div class="col-xs-10">
      <span class="form-control">
        <s:if test="%{hasParameters()}">
          <s:url id="parametersUrl" action="task" method="displayParameters"
            includeParams="none"/>
          <s:a href="javascript:popitup('%{parametersUrl}')">
            View (<s:property value="parameterCount"/>)
          </s:a>
        </s:if>
        <s:else>None</s:else>
      </span>
    </div>
  </div>
  <div class="form-group">
    <label class="col-xs-2 control-label">Output</label>
    <div class="col-xs-10">
      <span class="form-control">
        <s:if test="%{hasOutput()}">
          <s:url id="outputUrl" action="setTaskOutput" method="displayOutput"
            includeParams="none"/>
          <s:a href="%{outputUrl}">
            View (<s:property value="outputCount"/>)
          </s:a>
        </s:if>
        <s:else>None</s:else>
      </span>
    </div>
  </div>
  <div class="form-group">
    <label class="col-xs-2 control-label">Intermediate Results</label>
    <div class="col-xs-10">
      <span class="form-control">
        <s:if test="%{hasWorkingDirectory()}">
          <s:url id="workingDirectoryUrl" action="task" method="displayWorkingDirectory"
            includeParams="none"/>
          <s:a href="javascript:popitup('%{workingDirectoryUrl}')">List</s:a>
        </s:if>
        <s:else>None</s:else>
      </span>
    </div>
  </div>
  <div class="form-group">
    <label class="col-xs-2 control-label">Status</label>
    <div class="col-xs-10">
      <span class="form-control">
        <s:property value="currentTaskStage"/>
      </span>
    </div>
  </div>
</div>

<s:if test="%{hasTaskMessages()}">
  <h3>Task Messages</h3>
  <div class="message-box">
    <s:iterator value="taskMessages" id="message">
      <s:property value="#message"/><br/>
    </s:iterator>
  </div>
</s:if>
  
<div class="section">
  <s:url id="listTasksUrl" action="task" method="list" includeParams="none"/>
  <s:a cssClass="btn btn-primary" href="%{listTasksUrl}">Return To Task List</s:a>

  <s:url id="deleteTaskUrl" action="task" method="delete" includeParams="none"/>
  <s:a cssClass="btn btn-primary" href="javascript:confirm_delete('%{deleteTaskUrl}')">Delete Task</s:a>
</div>
