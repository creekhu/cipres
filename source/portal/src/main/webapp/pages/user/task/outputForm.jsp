<%@ taglib prefix="s" uri="/struts-tags" %>

<h3>Save Output</h3>

<s:form cssClass="form-horizontal" action="setTaskOutput" theme="simple" role="form">
  <div class="form-group">
    <label class="col-xs-2 control-label">Tool</label>
    <div class="col-xs-10 no-input">
      <span class="form-control"><s:property value="toolLabel"/><span>
    </div>
  </div>
  <div class="form-group">
    <label class="col-xs-2 control-label">File Name</label>
    <div class="col-xs-10 no-input">
      <span class="form-control"><s:property value="filename"/><span>
    </div>
  </div>
  <div class="form-group">
    <label class="col-xs-2 control-label">File Size</label>
    <div class="col-xs-10 no-input">
      <span class="form-control"><s:property value="outputFileSize"/> Bytes<span>
    </div>
  </div>
  <div class="form-group">
    <label class="col-xs-2 control-label">Label</label>
    <div class="col-xs-10">
      <s:textfield cssClass="form-control" name="label"/>
    </div>
  </div>
  <div class="form-group">
    <label class="col-xs-2 control-label">Data Format:</label>
    <div class="col-xs-10">
      <s:select name="dataFormat" list="phyloDataFormats"/>
    </div>
  </div>
  <div class="form-group form-buttons">
    <div class="col-xs-12">
      <s:submit value="Save" method="execute" cssClass="btn btn-primary"/>
      <s:reset value="Cancel" method="cancel" cssClass="btn btn-primary"/>
    </div>
  </div>
  <s:hidden name="entityType" value="UNKNOWN"/>
  <s:hidden name="dataType" value="UNKNOWN"/>
</s:form>

<s:if test="%{canDisplay()}">
  <div class="data-content section">
    <s:a href="#" id="show-content-toggle">Show/Hide Data Contents</s:a>
    <div id="contents">
      <s:property value="formattedOutput" escape="false"/>
    </div>
  </div>
</s:if>
