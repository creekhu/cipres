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
<%@include file ="header.jsp" %>

<p>
Because this is a simple demo, not a real application, we don't store any user information.  
That's why there is no registration form and you are asked to enter all the data each 
time you log in.  
</p>

<s:form action="login">

 <s:textfield label="Username" name="username" />
 <s:password label="Password" name="password"/>
 <s:textfield label="Email" name="email" />
 <s:textfield label="Institution" name="institution" />
 <s:textfield label="Country Code (2 letters)" name="country" />

 <s:submit/>
</s:form>

