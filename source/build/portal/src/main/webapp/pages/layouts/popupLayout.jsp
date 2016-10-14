<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ taglib uri="/WEB-INF/tld/tiles-jsp.tld" prefix="tiles" %>

<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">

<head>
  <title><tiles:getAsString name="title"/></title>
  <tiles:insertAttribute name="header"/>
</head>
	
<body>
	<tiles:insertAttribute name="body"/>
</body>
	
</html>
