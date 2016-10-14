<%@ taglib prefix="s" uri="/struts-tags" %>

<h2>Upload Files</h2>
<h4>
  <span class="prompt">
	Wait for applet to load.
    Use CTRL-click (CMD-click on mac) to choose multiple files.<br/>
  	Click Upload button and watch progress bar.  
  </span>
</h4>

<s:form action="uploadData" theme="simple">
<table class="form">
  <tr><td><span class="warningPlain">* Required</span></td></tr>
  <tr>
    <td><span class="warningPlain">*</span>Upload your file(s):</td>
    <td>
      <%
		String requestURL = request.getRequestURL().toString();
		String server = requestURL.substring(0, requestURL.lastIndexOf(request.getContextPath())) + "/";
		String webapp = requestURL.substring(0, requestURL.lastIndexOf(request.getServletPath())) + "/";
      %>
	  <applet code="wjhk.jupload2.JUploadApplet"
  		codebase="<%= server + "wbapplet/jupload/applet-jars/" %>" name="JUpload"
		width="500" height="400" mayscript
		archive="jupload_signed.jar">
		<param name="postURL" value="<%= webapp + "uploadFiles.action" %>">
		<param name="showLogWindow" value="false">
		Java 1.5 or higher plugin required.
	  </applet>
    </td>
  </tr>
  <tr>
    <td colspan="1">
		<span class="button">
  			<s:url id="listDataUrl" action="data" method="list" includeParams="none"/>
  			<s:a href="%{listDataUrl}">Return To Data List</s:a>
		</span>
    </td>
  </tr>
</table>
</s:form>
