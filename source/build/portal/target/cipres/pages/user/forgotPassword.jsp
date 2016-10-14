<%@ taglib prefix="s" uri="/struts-tags" %>

<head>
  <title>Password Reset</title>
  <content tag="menu">Home</content>
</head>
<body>
  <div class="col-xs-6 col-xs-offset-3">
    <div class="callout">
      Please enter either your registered username or email address,
      and you will receive a password reset confirmation link by email.
    </div>
    <h2>Forgot your login information?</h2>
    <s:form id="forgot-password" cssClass="form-horizontal" action="forgotPassword" theme="simple" role="form">
      <div class="form-group">
        <label class="col-xs-2 control-label">Username</label>
        <div class="col-xs-10">
          <s:password cssClass="form-control" name="username" placeholder="Username"/>
        </div>
      </div>
      <div class="form-group">
        <label class="col-xs-2 control-label">Email</label>
        <div class="col-xs-10">
          <s:password cssClass="form-control" name="email" placeholder="Email"/>
        </div>
      </div>
      <div class="form-group form-buttons">
        <div class="col-xs-10">
          <s:submit value="Submit" cssClass="btn btn-primary"/>
          <s:reset value="Reset" cssClass="btn btn-primary"/>
        </div>
      </div>
    </s:form>
  </div>
</body>
