<%@ include file="/pages/common/taglibs.jsp" %>
<head>
	<title>Registration</title>
  <content tag="menu">Home</content>
</head>

<body>
<div class="col-xs-6 col-xs-offset-3">
  <div class="callout">
    <s:url id="guestUrl" action="guestLogin"/>
    New Users - please register <!-- comment out on the test portal or -->
  	<s:if test="%{allowGuests() == true}">
    	<s:a href="%{guestUrl}">. Or proceed</s:a> without registration.
	</s:if>
  </div>

  <h2>Registration</h2>
  <s:form id="register" cssClass="form-horizontal" action="register" theme="simple" role="form" method="post">
    <div class="form-group">
      <label class="col-xs-4 control-label">Username</label>
      <div class="col-xs-8">
        <s:textfield cssClass="form-control" name="username" placeholder="Username"/>
      </div>
    </div>
    <div class="form-group">
      <label class="col-xs-4 control-label">Password</label>
      <div class="col-xs-8">
        <s:password cssClass="form-control" name="newPassword" placeholder="Password"/>
      </div>
    </div>
    <div class="form-group">
      <label class="col-xs-4 control-label">Confirm Password</label>
      <div class="col-xs-8">
        <s:password cssClass="form-control" name="confirmNewPassword" placeholder="Confirm Password"/>
      </div>
    </div>
    <div class="form-group">
      <label class="col-xs-4 control-label">First Name</label>
      <div class="col-xs-8">
        <s:textfield cssClass="form-control" name="firstName" placeholder="First Name"/>
      </div>
    </div>
    <div class="form-group">
      <label class="col-xs-4 control-label">Last Name</label>
      <div class="col-xs-8">
        <s:textfield cssClass="form-control" name="lastName" placeholder="Last Name"/>
      </div>
    </div>
    <div class="form-group">
      <label class="col-xs-4 control-label">Email</label>
      <div class="col-xs-8">
        <s:textfield cssClass="form-control" name="email" placeholder="Email"/>
      </div>
    </div>
    <div class="form-group">
      <label class="col-xs-4 control-label">Confirm Email</label>
      <div class="col-xs-8">
        <s:textfield cssClass="form-control" name="confirmEmail" placeholder="Confirm Email"/>
      </div>
    </div>
    <div class="form-group">
      <label class="col-xs-4 control-label">Institution</label>
      <div class="col-xs-8">
        <s:textfield cssClass="form-control" key="institution" placeholder="Institution"/>
      </div>
    </div>
    <div class="form-group">
      <label class="col-xs-4 control-label">Country</label>
      <div class="col-xs-8">
      	<!--
        <select class="form-control input-sm bfh-countries"
       		data-country="<s:property value='%{(getCountry()==null || getCountry()=="" ? "US" : getCountry())}' />"
            name="country"
            title="What is your institution's home country?">
        </select>
        -->
        <select class="form-control input-sm bfh-countries"
       		data-country="<s:property value='%{("AF")}' />"
            name="country"
            title="What is your institution's home country?">
        </select>
      </div>
    </div>
    <div class="form-group">
      <label class="col-xs-4 control-label"></label>
      <div class="col-xs-8">
        <label>(for NSF accounting purposes only)</label>
      </div>
    </div>
    <div class="form-group form-buttons">
      <div class="col-xs-12">
        <s:submit value="Register" cssClass="btn btn-primary" method="register"/>
        <s:reset value="Reset" cssClass="btn btn-primary"/>
        <s:submit value="Cancel" cssClass="btn btn-primary" method="cancel" />
      </div>
    </div>
  </s:form>
</div>
</body>
