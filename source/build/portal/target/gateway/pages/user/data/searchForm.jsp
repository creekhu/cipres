<%@ taglib prefix="s" uri="/struts-tags" %>

<!-- Data search step tabs -->
<div class="navInner">
  <ul>
    <s:url id="basicSearchTabUrl" action="searchData" method="changeTab"
      includeParams="none">
      <s:param name="tab" value="%{'Basic Search'}"/>
    </s:url>
    <li>
      <s:if test="%{tab == null || tab == 'Basic Search'}">
        <s:a href="%{basicSearchTabUrl}" cssClass="current">
          <span>Basic Search</span>
        </s:a>
      </s:if>
      <s:else>
        <s:a href="%{basicSearchTabUrl}">
          <span>Basic Search</span>
        </s:a>
      </s:else>
    </li>
    <s:url id="resultsPageTabUrl" action="searchData" method="changeTab"
      includeParams="none">
      <s:param name="tab" value="%{'Results Page'}"/>
    </s:url>
    <li>
      <s:if test="%{tab == 'Results Page'}">
        <s:a href="%{resultsPageTabUrl}" cssClass="current">
          <span>Results Page</span>
        </s:a>
      </s:if>
      <s:else>
        <s:a href="%{resultsPageTabUrl}">
          <span>Results Page</span>
        </s:a>
      </s:else>
    </li>
  </ul>
</div>
<div class="tabbedBox">
  <s:if test="%{tab == null || tab == 'Basic Search'}">
    <s:include value="/pages/user/data/basicSearch.jsp"/>
  </s:if>
  <s:elseif test="%{tab == 'Results Page'}">
    <s:include value="/pages/user/data/resultsPage.jsp"/>
  </s:elseif>
  <s:else>
    <h4>Unknown Tab</h4>
  </s:else>
</div>
