<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
 pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Login page</title>
<link rel="stylesheet" type="text/css" href="style.css" />

</head>
<body>
<%@include file ="navbar.jsp" %>
<%@include file ="header.jsp" %>

<s:form action="taskCreate" enctype="multipart/form-data" method="post">

	<s:textfield label="Tool Id" name="toolId" value="CLUSTALW"/>
	<br>

	<s:file label="Primary Input File" name="primaryInput" />

	<s:textfield label="Parameter Name" name="inputFileParameterName" value="infile_"/>
	<br>

	<s:textarea label="Metadata Properties" name="metadata" cols="50" rows="5" />
	<br>

	<s:textarea label="Tool Parameter Properties" name="toolParameters" cols="50" rows="5"  
		value="%{'runtime_=0.02\nactions_=-align'}"/>

	For this demo, vparam.runtime_ will automatically be set to .1, regardless of what you enter.
	<s:submit/>
</s:form>

</body>

