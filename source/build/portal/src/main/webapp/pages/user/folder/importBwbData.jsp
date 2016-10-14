<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<p>
  Please use this form to import your data from your original Biology Workbench account
  to the current folder.
</p>

<div class="formBox"> 
  <h4><strong>Original Biology Workbench Login</strong></h4>
  <s:form action="importBwbData" theme="simple">
    <p><span class="warning-red">*</span> Required</p>
    <p>
      <span class="warning-red">*</span> Username:<br/>
      <s:textfield name="username" size="30"/>
    </p>
    <p>
      <span class="warning-red">*</span> Password:<br/>
      <s:password name="currentPassword" size="30"/>
    </p>
    <p>
      <s:submit value="Import Data"/>&nbsp;
      <s:reset value="Reset"/>
    </p>
  </s:form>
</div>
