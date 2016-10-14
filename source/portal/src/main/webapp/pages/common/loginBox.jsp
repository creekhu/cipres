<%@ taglib prefix="s" uri="/struts-tags" %>

<div id="loginBox">
  <s:if test="%{isAuthenticated()}">
    <p>
      Welcome,
      <b><s:property value="authenticatedUsername"/></b> |
      <s:property value="%{formatDate(new java.util.Date())}"/>
    </p>
    <p>
      <s:url id="logoutUrl" action="logout"/>
      <s:a href="%{logoutUrl}">Log Out</s:a>
    </p>
  </s:if>
  <s:else>
    <s:form action="login">
      <label>Username: <input size="12" name="username"/> </label>
      <label>Password: <input type="password" size="12" name="password"/> </label>
      <label><input type="submit" value="Login"/> </label><br/>
      <s:url id="registerUrl" action="home" includeParams="none"/>
      <s:url id="guestUrl" action="guestLogin" includeParams="none"/>
      <p>
        <s:a href="%{registerUrl}">Create an account</s:a> |
        <s:a href="%{guestUrl}">Guest Login </s:a>
      </p>
    </s:form>
  </s:else>
</div>
