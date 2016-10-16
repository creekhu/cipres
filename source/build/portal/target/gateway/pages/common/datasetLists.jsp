<%@ taglib prefix="s" uri="/struts-tags" %>

<!-- BEGIN "datasetLists" Tile -->
<table>
  <tr><td>&nbsp;</td></tr>
  <tr>
    <td colspan="2">
      <span class="prompt">
        Please select the type of data you want to search for
      </span>
    </td>
  </tr>
  <tr>
    <td>Entity Type:</td>
    <td><s:select name="entityType" list="entityTypes" onchange="reloadLists(this.form)"/></td>
  </tr>
  <tr>
    <td>Data Type:</td>
    <td><s:select name="dataType" list="dataTypes" onchange="reloadLists(this.form)"/></td>
  </tr>
  <tr>
    <td>Data Sets:</td>
    <td><s:select name="selectedDatasets" list="datasets" multiple="true"/></td>
  </tr>
  <tr><td>&nbsp;</td></tr>
</table>

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
					if (listName == "selectedDatasets") {
						var submit = getSubmitElements(form);
						if (dropdown.length > 0)
							submit[0].disabled = false;
						else submit[0].disabled = true;
					}
				}
			}
		}
	}
	var parameters = "?entityType=" + getDropdownSelection(form, "entityType") +
		"&dataType=" + getDropdownSelection(form, "dataType");
	var selectedDatasets = getDropdownSelection(form, "selectedDatasets");
	if (selectedDatasets != null)
		parameters += "&selectedDatasets=" + selectedDatasets;
	request.open("GET", "datasetLists.action" + parameters, true);
	request.send(null);
}
</script>
<!-- END "datasetLists" Tile -->
