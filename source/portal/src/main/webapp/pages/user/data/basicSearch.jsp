<%@ taglib prefix="s" uri="/struts-tags" %>

<h3>Basic Search</h3>

<s:form action="searchData" theme="simple">
<table class="form">
  <tr>
    <td colspan="2">
      <span class="prompt">
        Please enter your search terms
      </span>
    </td>
  </tr>
  <tr><td><span class="warningPlain">* Required</span></td></tr>
  <tr>
    <td>
      <span class="warningPlain">*</span>Query:&nbsp;
      <s:textfield name="queryString" size="50"/>
    </td>
  </tr>
  <tr><td><s:include value="/pages/common/datasetLists.jsp"/></td></tr>
  <tr>
    <td>
      <s:if test="%{hasDatasets()}">
        <s:submit value="Submit Search" method="execute"/>
      </s:if>
      <s:else>
        <s:submit value="Submit Search" method="execute" disabled="true"/>
      </s:else>
    </td>
  </tr>
</table>
</s:form>
