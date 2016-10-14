<%@ taglib prefix="s" uri="/struts-tags" %>

<head>
	<title>User Profile Update</title>
  <content tag="menu">My Profile</content>
</head>
<body>
<h2>Update Personal Information</h2>
<s:form id="update-personal-info" cssClass="form-horizontal" action="updateProfile" theme="simple">
  <div class="form-group">
    <label class="col-xs-2 control-label">First Name</label>
    <div class="col-xs-10">
      <s:textfield cssClass="form-control" name="firstName" placeholder="First Name"/>
    </div>
  </div>
  <div class="form-group">
    <label class="col-xs-2 control-label">Last Name</label>
    <div class="col-xs-10">
      <s:textfield cssClass="form-control" name="lastName" placeholder="Last Name"/>
    </div>
  </div>
  <div class="form-group">
    <label class="col-xs-2 control-label">Email</label>
    <div class="col-xs-10">
      <s:textfield cssClass="form-control" name="email" placeholder="Email"/>
    </div>
  </div>
  <div class="form-group">
    <label class="col-xs-2 control-label">Confirm Email</label>
    <div class="col-xs-10">
      <s:textfield cssClass="form-control" name="confirmEmail" placeholder="Confirm Email"/>
    </div>
  </div>
  <div class="form-group">
    <label class="col-xs-2 control-label">Institution</label>
    <div class="col-xs-10">
      <s:textfield cssClass="form-control" name="institution" placeholder="Institution"/>
    </div>
  </div>
  <div class="form-group">
    <label class="col-xs-2 control-label">Country</label>
    <div class="col-xs-10">
      <select class="form-control input-sm bfh-countries"
      	data-country="<s:property value='%{(getCountry()==null || getCountry()=="" ? "AF" : getCountry())}' />"
     	name="country"
     	title="What is your institution's home country?">
      </select>
    </div>
  </div>
  <div class="form-group form-buttons">
    <div class="col-xs-12">
      <s:submit value="Update Profile" cssClass="btn btn-primary"/>
      <s:reset value="Reset" cssClass="btn btn-primary"/>
    </div>
  </div>
</s:form>
</body>
