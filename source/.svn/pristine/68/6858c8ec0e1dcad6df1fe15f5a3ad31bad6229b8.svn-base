<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="%{hasOutput() == false}">
  <div class="callout">
    <h4>There is currently no output attached to this task.</h4>
  </div>
</s:if>
<s:else>
  <h2>View Task Output</h2>
  <div class="callout">
    Click on an output file below to review its contents.
  </div>
  <div id="column">
    <s:form name="selectOutput" action="setTaskOutput" theme="simple">
  		<s:token/>
    <table class="table table-stripe">
      <!-- Field Headers -->
      <tr>
        <th>
          <s:checkbox name="allChecked"
            onclick="ids.check(this)"/>&nbsp;Select&nbsp;all
        </th>
        <th>Tool Output</th>
        <th>File Name</th>
        <th>File Size (Bytes)</th>
        <!-- TODO: Add more properties -->
        <th/><th/>
      </tr>

      <!-- Data Item Rows -->
      <s:set name="action" value="top"/>
      <!--
        Iterable collection [#action.getOutput()] is Map<String, List<TaskOutputSourceDocument>>
        Iterated value [#outputFiles] is Map.Entry<String, List<TaskOutputSourceDocument>>
      -->
      <s:iterator value="output" id="outputFiles" status="status">
        <s:if test="%{#status.odd == true}">
          <s:set name="rowclass" value="%{'tableroww'}"/>
        </s:if>
        <s:else>
          <s:set name="rowclass" value="%{'tablerowb'}"/>
        </s:else>
        <s:set name="outputKey" value="%{#action.getOutputKey(#outputFiles)}"/>
        <!--
      	  Iterable collection [#outputFiles.getValue()] is List<TaskOutputSourceDocument>
      	  Iterated value [#outputFile] is TaskOutputSourceDocument
        -->
        <s:iterator value="#outputFiles.value" id="outputFile" status="fileStatus">
          <s:set name="outputFilename"
            value="%{#action.getOutputFile(#outputFile)}"/>
          <s:set name="outputId" value="%{#outputKey + ':' + #fileStatus.index}"/>
          <tr class="<s:property value="#rowclass"/>">
            <td>
              <s:checkbox name="selectedOutputs" fieldValue="%{#outputId}"
                value="%{selectedOutputs.{^ #this == #outputId}.size > 0}" theme="simple"
                onclick="ids.check(this)"/>
            </td>
            <s:if test="%{#fileStatus.first == true}">
              <td><s:property value="%{#action.formatOutputKey(#outputKey)}"/></td>
            </s:if>
            <s:else><td/></s:else>
            <td><s:property value="%{#outputFilename}"/></td>
            <td><s:property value="%{#action.getOutputFileSize(#outputFile)}"/></td>
            <td>
              <s:url id="outputUrl" action="setTaskOutput" method="selectOutput"
                includeParams="none">
                <s:param name="key" value="%{#outputKey}"/>
                <s:param name="file" value="%{#outputFilename}"/>
              </s:url>
              <span class="button">
                <s:a href="%{outputUrl}">View</s:a>
              </span>
            </td>
            <td>
              <s:url id="saveFileUrl" action="setTaskOutput"
                method="displayOutputFile" includeParams="none">
                <s:param name="key" value="%{#outputKey}"/>
                <s:param name="file" value="%{#outputFilename}"/>
              </s:url>
              <span class="button">
                <s:a href="%{saveFileUrl}">Download</s:a>
              </span>
            </td>
          </tr>
        </s:iterator>
      </s:iterator>
    </table>
    <s:submit value="Download Selected" method="downloadSelected" cssClass="btn btn-primary"/>
    </s:form>
  </div>
</s:else>

<br/>
<div class="clear">
  <s:url id="taskListUrl" action="task" method="list" includeParams="none"/>
  <s:a href="%{taskListUrl}" cssClass="btn btn-primary">Return to Task List</s:a>
  <s:url id="taskUrl" action="task" method="display" includeParams="none">
    <s:param name="id" value="%{#task.taskId}"/>
  </s:url>
  <s:a href="%{taskUrl}" cssClass="btn btn-primary">View Current Task</s:a>
</div>

<script type="text/javascript">
var ids = new CheckBoxGroup();
ids.addToGroup("selectedOutputs");
ids.setControlBox("allChecked");
ids.setMasterBehavior("all");
</script>
