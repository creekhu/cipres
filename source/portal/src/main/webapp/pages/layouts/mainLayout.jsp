<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ taglib uri="/WEB-INF/tld/tiles-jsp.tld" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">

<head>
  <title><tiles:getAsString name="title"/></title>
  <tiles:insertAttribute name="header"/>
</head>
	
<body onload="resolveParameters()">
  <div class="container">
    <!--
      This variable must be set with a JSTL tag, because the equivalent Struts
      tag does not support setting the variable with body content.
    -->
    <c:set var="tab" scope="session"><tiles:getAsString name='tab'/></c:set>
    <tiles:insertAttribute name="banner"/>
    <tiles:insertAttribute name="loginBox"/>
    <tiles:insertAttribute name="mainMenu"/>
    
    <div id="leftColumn" class="column span-24">	
      <tiles:insertAttribute name="navMenu"/>
      <div id="mainContent" class="column span-18 last"><!-- Begin Main Content Area (DO NOT REMOVE!)-->
        <tiles:insertAttribute name="messages"/>
	    <tiles:insertAttribute name="body"/>
      </div><!-- End Main Content Area (DO NOT REMOVE!)-->
    </div>
    
    <tiles:insertAttribute name="footer"/>
  </div>
</body>
</html>
