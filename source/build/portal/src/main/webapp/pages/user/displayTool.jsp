<%@ taglib prefix="s" uri="/struts-tags" %>

<s:include value="/pages/static/tool/%{displayedTool.toLowerCase()}.html"/>
<br/>
<span class="button">
  <s:url id="toolsUrl" action="tools" includeParams="none"/>
  <s:a href="%{toolsUrl}">Return to Tool List</s:a>
</span>
