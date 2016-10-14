<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="%{#session.target != null}">
  <s:set name="target" value="%{#session.target}"/>
  <s:set name="target" scope="session" value="null"/>
  <s:action name="%{#target}" executeResult="true"/>
</s:if>
<s:else>
  <s:action name="home" executeResult="true"/>
</s:else>
