<%@ taglib prefix="s" uri="/struts-tags" %>

<div class="callout">
  Please select the type of data in this file
</div>
<div class="form-group">
  <label class="col-xs-2 control-label">Entity Type</label>
  <div class="col-xs-10">
    <s:select name="entityType" list="entityTypes" onchange="reloadLists(this.form)"/>
  </div>
</div>
<div class="form-group">
  <label class="col-xs-2 control-label">Data Type</label>
  <div class="col-xs-10">
    <s:select name="dataType" list="dataTypes" onchange="reloadLists(this.form)"/>
  </div>
</div>
<div class="form-group">
  <label class="col-xs-2 control-label">Data Format</label>
  <div class="col-xs-10">
    <s:select name="dataFormat" list="dataFormats"/>
  </div>
</div>

<script type="text/javascript">
function reloadLists(form) {
	var request = getAjaxRequest();
	if (request ==  null) {
		alert("getAjaxRequest() returned null!");
		return;
	}
	request.onreadystatechange = function() {
		if (request.readyState == 4) {
			if (request.status == 200) {
				var dropdowns = request.responseXML.getElementsByTagName("select");
				for (i=0; i<dropdowns.length; i++) {
					var options = dropdowns[i].getElementsByTagName("option");
					var listName = dropdowns[i].attributes.getNamedItem("name").nodeValue;
					var dropdown = form.elements[listName];
					// clear and then repopulate the drop-down list
					dropdown.length = 0;
					dropdown.length = options.length;
					for (j=0; j<options.length; j++) {
						dropdown[j].value = options[j].attributes.getNamedItem("value").nodeValue;
						dropdown[j].text = options[j].firstChild.nodeValue;
						// if we encounter the selected option, select it
						var selected = options[j].attributes.getNamedItem("selected");
						if (selected != null && selected.nodeValue == "true")
							dropdown[j].selected = true;
					}
				}
			}
		}
	}
	var parameters = "?entityType=" + getDropdownSelection(form, "entityType") +
		"&dataType=" + getDropdownSelection(form, "dataType") +
		"&dataFormat=" + getDropdownSelection(form, "dataFormat");
	request.open("GET", "dataUploadLists.action" + parameters, true);
	request.send(null);
}
</script>
<!-- END "dataUploadLists" Tile -->
