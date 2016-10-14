<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="%{isAuthenticated()}">
  <s:include value="/pages/user/folder/folderList.jsp"/>
</s:if>
<s:else>
  <div class="column span-5 append-1">
    <h3>User Information</h3>
    <ul class="side-list">
	  <li><a href="http://www.ngbw.org">Home</a></li>
	  <li><a href="<s:url action="help"/>">Help</a></li>
	  <li><a href="<s:url action="links"/>">Related Links</a></li>
	  <li><a href="<s:url action="publications"/>">Publications</a></li>
	  <li><a href="<s:url action="contact"/>">Contact Us</a></li>
	  <li><a href="<s:url action="news"/>">News</a></li>
	  <li><a href="<s:url action="funding"/>">Funding</a></li>
	  <li><a href="<s:url action="compatibility"/>">Browser Compatibility</a></li>
	  <li><a href="<s:url action="faq"/>">FAQ</a><br/></li>
    </ul>
  </div>
</s:else>
