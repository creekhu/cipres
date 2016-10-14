<ul class="pagination pull-right">
  <!-- First Page -->
  <s:if test="%{isFirstPage() == false}">
    <li><s:a href="%{firstPageUrl}" cssClass="mc-replace">&laquo;</s:a></li>
  </s:if>
  <s:else>
    <li class="disabled"><a href="#">&laquo;</a></li>
  </s:else>
        
  <!-- Previous Page -->
  <s:if test="%{hasPreviousPage()}">
    <li><s:a href="%{previousPageUrl}" cssClass="mc-replace">&lsaquo;</s:a></li>
  </s:if>
  <s:else>
    <li class="disabled"><a href="#">&lsaquo;</a></li>
  </s:else>

  <li class="active">
    <a href="#">
      <!-- Current Page -->
      Page <s:property value="%{pageNumber + 1}"/>
      of <s:property value="%{lastPageNumber + 1}"/>
    </a>
  </li>

  <!-- Next Page -->
  <s:if test="%{hasNextPage()}">
    <li><s:a href="%{nextPageUrl}" cssClass="mc-replace">&rsaquo;</s:a></li>
  </s:if>
  <s:else>
    <li class="disabled"><a href="#">&rsaquo;</a></li>
  </s:else>

  <!-- Last Page -->
  <s:if test="%{isLastPage() == false}">
    <li><s:a cssClass="mc-replace" href="%{lastPageUrl}">&raquo;</s:a></li>
  </s:if>
  <s:else>
    <li class="disabled"><a href="#">&raquo;</a></li>
  </s:else>
</ul>
