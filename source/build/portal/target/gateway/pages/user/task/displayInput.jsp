<%@ taglib prefix="s" uri="/struts-tags" %>

<h3><s:property value="toolLabel"/> - Input</h3>

<h5 style="margin-bottom: 5px">
  Parameter '<s:property value="mainInputParameter"/>'
</h5>
<table class="form">
  <s:set name="action" value="top"/>
  <s:iterator value="mainInput" id="input">
    <tr><th><s:property value="%{#input.name}"/></th></tr>
    <tr>
      <td>
        <pre class="source"><s:property value="%{#action.getData(#input)}"/></pre>
      </td>
    </tr>
  </s:iterator>
</table>
