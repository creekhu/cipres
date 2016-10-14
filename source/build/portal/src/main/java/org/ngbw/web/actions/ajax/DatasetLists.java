package org.ngbw.web.actions.ajax;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import org.ngbw.sdk.core.types.Dataset;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.EntityType;
import org.ngbw.web.actions.SearchData;
import org.ngbw.web.controllers.DatasetController;

@SuppressWarnings("serial")
public class DatasetLists extends SearchData
{
	/*================================================================
	 * Constants
	 *================================================================*/
	// search drop-down list properties
	public static final String ENTITY_TYPE = "entityType";
	public static final String DATA_TYPE = "dataType";
	public static final String SELECTED_DATASETS = "selectedDatasets";
	
	/*================================================================
	 * Action methods
	 *================================================================*/
	public String execute() {
		// get current drop-down list selections from request params, if present
		String[] entityType = (String[])getParameters().get(ENTITY_TYPE);
		String[] dataType = (String[])getParameters().get(DATA_TYPE);
		String[] datasets = (String[])getParameters().get(SELECTED_DATASETS);
		
		// if any of the selections are missing, initialize them correctly
		if (entityType == null || entityType.length < 1 || entityType[0].trim().equals(""))
			entityType = new String[]{UNKNOWN};
		if (dataType == null || dataType.length < 1 || dataType[0].trim().equals(""))
			dataType = new String[]{UNKNOWN};
		
		// populate selected dataset array
		Dataset[] selectedDatasets = null;
		if (datasets != null && datasets.length > 0 &&
			datasets[0].trim().equals("") == false) {
			datasets = datasets[0].split(",");
			selectedDatasets = new Dataset[datasets.length];
			for (int i=0; i<datasets.length; i++)
				selectedDatasets[i] = (Dataset.valueOf(datasets[i]));
		}
		
		// resolve the user's drop-down list selections
		getController().resolve(EntityType.valueOf(entityType[0]),
			DataType.valueOf(dataType[0]), selectedDatasets);
		return SUCCESS;
	}
	
	/*================================================================
	 * Struts result accessor methods
	 *================================================================*/
	public InputStream getSelectState() {
		DatasetController controller = getController();
		String xml = "<?xml version=\"1.0\" ?>\n<dropdowns>\n";
		
		// render entity types drop-down list
		xml += "  <select name=\"" + ENTITY_TYPE + "\">\n";
		String selected = controller.getEntityType().toString();
		Map<String, String> conceptMap = mapConceptSet(controller.getEntityTypes(), "EntityType");
		if (conceptMap != null && conceptMap.size() > 0) {
			for (String entityType : conceptMap.keySet()) {
				xml += "    <option value=\"" + entityType + "\"";
				if (entityType.equals(selected))
					xml += " selected=\"true\"";
				xml += ">" + conceptMap.get(entityType) + "</option>\n";
			}
		}
		xml += "  </select>\n";
		
		// render data types drop-down list
		xml += "  <select name=\"" + DATA_TYPE + "\">\n";
		selected = controller.getDataType().toString();
		conceptMap = mapConceptSet(controller.getDataTypes(), "DataType");
		if (conceptMap != null && conceptMap.size() > 0) {
			for (String dataType : conceptMap.keySet()) {
				xml += "    <option value=\"" + dataType + "\"";
				if (dataType.equals(selected))
					xml += " selected=\"true\"";
				xml += ">" + conceptMap.get(dataType) + "</option>\n";
			}
		}
		xml += "  </select>\n";
		
		// render datasets drop-down list
		xml += "  <select name=\"" + SELECTED_DATASETS + "\">\n";
		conceptMap = mapConceptSet(controller.getDatasets(), "Dataset");
		if (conceptMap != null && conceptMap.size() > 0) {
			for (String dataset : conceptMap.keySet()) {
				xml += "    <option value=\"" + dataset + "\"";
				if (isSelected(Dataset.valueOf(dataset)))
					xml += " selected=\"true\"";
				xml += ">" + conceptMap.get(dataset) + "</option>\n";

			}
		}
		xml += "  </select>\n";
		
		xml += "</dropdowns>";
		return new ByteArrayInputStream(xml.getBytes());
	}
	
	protected boolean isSelected(Dataset dataset) {
		Dataset[] selectedDatasets = getController().getSelectedDatasets();
		if (dataset == null || selectedDatasets == null)
			return false;
		else {
			for (int i=0; i<selectedDatasets.length; i++) {
				if (dataset.equals(selectedDatasets[i]))
					return true;
			}
			return false;
		}
	}
}
