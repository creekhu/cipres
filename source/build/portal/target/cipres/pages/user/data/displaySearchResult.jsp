<%@ taglib prefix="s" uri="/struts-tags" %>

<table class="list">
  <tr><th colspan="2">Search Result Details</th></tr>
  <tr class="tableroww">
    <td style="font-style: italic">Data Set</td>
    <td>
      <s:property value="dataset"/>
      (<s:property value="datasetText"/>)
    </td>
  </tr>
  <s:set name="action" value="top"/>
  <s:iterator value="searchResultFields" id="field" status="status">
    <s:if test="%{#status.odd == true}">
      <s:set name="rowclass" value="%{'tablerowb'}"/>
    </s:if>
    <s:else>
      <s:set name="rowclass" value="%{'tableroww'}"/>
    </s:else>
    <tr class="<s:property value="#rowclass"/>">
      <td style="font-style: italic">
        <s:property value="%{#action.getFieldLabel(#field)}"/>
      </td>
      <td><s:property value="%{#action.getFieldValue(#field)}"/></td>
    </tr>
  </s:iterator>
</table>
<br/>

<div class="clear">
<s:if test="%{hasSourceDocument()}">
  <br/>
  <span class="simpleLink">
    <s:a href="#" onclick="switchMenu('contents');">Show/Hide Data Contents</s:a>
  </span>
  <br/>
  <div id="contents" style="display: none">
    <br/>
    <s:property value="formattedSourceData" escape="false"/>
  </div>
  <br/>
</s:if>
</div>

<span class="button">
  <s:url id="saveDataUrl" action="searchData" method="save" includeParams="none"/>
  <s:a href="%{saveDataUrl}">Save Search Result</s:a>
</span>

<span class="button">
  <s:url id="listDataUrl" action="searchData" method="list" includeParams="none"/>
  <s:a href="%{listDataUrl}">Return To Search Result List</s:a>
</span>
