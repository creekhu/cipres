<%@ taglib prefix="s" uri="/struts-tags" %>

<s:if test="%{hasTransformedRecords()}">
  <h2>View Output Record Details</h2>
  <s:if test="%{resultCount > 0}">
    <div class="callout">
      <s:if test="%{resultCount != 1}">
        This output contains <s:property value="%{resultCount}"/> records.
        (Records <s:property value="%{thisPageFirstElementNumber + 1}"/> -
        <s:property value="%{thisPageLastElementNumber + 1}"/> are shown here.)
      </s:if>
      <s:else>
        This output contains 1 record.
      </s:else>
    </div>

    <s:url id="firstPageUrl" action="paginateOutput" method="setPage" includeParams="none">
      <s:param name="page" value="%{'0'}"/>
    </s:url>
    <s:url id="previousPageUrl" action="paginateOutput" method="setPage" includeParams="none">
      <s:param name="page" value="%{previousPageNumber}"/>
    </s:url>
    <s:url id="nextPageUrl" action="paginateOutput" method="setPage" includeParams="none">
      <s:param name="page" value="%{nextPageNumber}"/>
    </s:url>
    <s:url id="lastPageUrl" action="paginateOutput" method="setPage" includeParams="none">
      <s:param name="page" value="%{lastPageNumber}"/>
    </s:url>

    <%@ include file="/pages/common/pagination.jsp" %>
    <s:form action="paginateOutput" theme="simple">
        Show
        <s:select name="pageSize" list="#{ 20:'20', 40:'40', 100:'100', 200:'200' }"
          onchange="reload(this.form)" value="pageSizeString"/>
        records on each page
    </s:form>

    <s:form action="setTaskOutput" theme="simple">
      <table class="table table-striped">
        <!-- Field Headers -->
        <thead>
          <th>
            <s:checkbox name="allChecked" onclick="ids.check(this)"/>
            Select all
          </th>
          <s:set name="action" value="top"/>
          <s:iterator value="transformedRecordFields" id="field">
            <th><s:property value="%{#action.getRecordField(#field)}"/></th>
          </s:iterator>
        </thead>
          
        <!-- Data Item Rows -->
        <s:set name="action" value="top"/>
        <s:iterator value="transformedRecordPage" id="dataRecord" status="status">
          <s:if test="%{#status.odd == true}">
            <s:set name="rowclass" value="%{'tableroww'}"/>
          </s:if>
          <s:else>
            <s:set name="rowclass" value="%{'tablerowb'}"/>
          </s:else>
          <tr class="<s:property value="#rowclass"/>">
            <td>
              <s:set name="index" value="%{#status.index}"/>
              <s:checkbox name="selectedIds" fieldValue="%{#index}"
                value="%{selectedIds.{^ #this == #index}.size > 0}" theme="simple"
                onclick="ids.check(this)"/>
            </td>
            <s:iterator value="%{#action.getTransformedRecordFields()}" id="field">
              <td>
                <s:if test="%{#action.isPrimaryId(#field)}">
                  <s:url id="dataUrl" action="paginateOutput" method="displayTransformedRecord"
                    includeParams="none">
                    <s:param name="id" value="%{#index}"/>
                  </s:url>
                  <span class="simpleLink">
                    <s:a href="%{dataUrl}">
                      <s:property value="%{#action.getTransformedRecordField(#dataRecord, #field)}"/>
                    </s:a>
                  </span>
                </s:if>
                <s:else>
                  <s:property value="%{#action.getTransformedRecordField(#dataRecord, #field)}"/>
                </s:else>
              </td>
            </s:iterator>
          </tr>
        </s:iterator>
      </table>
      <div class="section">
        <s:submit cssClass="btn btn-primary" value="Save Results" method="saveSelected"/>
      </div>
    </s:form>
  </s:if>
  <s:else>
    <div class="callout">This output contains 0 records.</div>
  </s:else>
</s:if>
<s:else>
  <div class="callout">This output contains 0 records.</div>
</s:else>

<script type="text/javascript">
var ids = new CheckBoxGroup();
ids.addToGroup("selectedIds");
ids.setControlBox("allChecked");
ids.setMasterBehavior("all");
</script>
