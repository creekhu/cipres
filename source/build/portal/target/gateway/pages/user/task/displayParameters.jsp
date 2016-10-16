<%@ taglib prefix="s" uri="/struts-tags" %>

<h3><s:property value="toolLabel"/> - Parameters</h3>

<h5 style="margin-bottom: 5px">Simple Parameters</h5>
<table class="form" style="width: 300px">
  <s:iterator value="simpleParameters" id="parameter">
    <tr>
      <th><s:property value="%{#parameter.getKey()}"/></th>
      <td><s:property value="%{#parameter.getValue()}"/></td>
    </tr>
  </s:iterator>
</table>
<s:set name="action" value="top"/>
<s:iterator value="parameterInput" id="parameter">
  <h5 style="margin-bottom: 5px">
    Parameter '<s:property value="%{#parameter.getKey()}"/>'
  </h5>
  <table class="form">
    <s:iterator value="%{#parameter.getValue()}" id="file">
      <tr>
        <td><b><s:property value="%{#file.name)}"/></b></td>
      </tr>
      <tr>
        <td>
          <pre class="source"><s:property value="%{#action.getData(#file)}"/></pre>
        </td>
      </tr>
    </s:iterator>
  </table>
</s:iterator>
