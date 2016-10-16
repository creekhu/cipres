<%@ taglib prefix="s" uri="/struts-tags" %>

<h2>Output Record Details</h2>

<s:set name="action" value="top"/>
<div class="form-horizontal">
  <s:iterator value="transformedRecordFields" id="field" status="status">
    <div class="form-group">
      <label class="col-xs-2 control-label"><s:property value="%{#action.getFieldLabel(#field)}"/></label>
      <div class="col-xs-10">
        <span class="form-control"><s:property value="%{#action.getFieldValue(#field)}"/></span>
      </div>
    </div>
  </s:iterator>
</div>

<div class="data-content section">
  <s:a href="#" id="show-content-toggle">Show/Hide Data Contents</s:a>
  <div id="contents">
    <s:property value="formattedSourceData" escape="false"/>
  </div>
</div>

<div class="section">
  <s:url id="saveDataUrl" action="setTaskOutput" method="save" includeParams="none"/>
  <s:a href="%{saveDataUrl}" cssClass="btn btn-primary">Save Search Result</s:a>

  <s:url id="listDataUrl" action="setTaskOutput" method="displayTransformedOutput"
    includeParams="none"/>
  <s:a cssClass="btn btn-primary"href="%{listDataUrl}">Return To Output Record List</s:a>
</div>
