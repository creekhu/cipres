package org.ngbw.web.actions.ajax;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import org.ngbw.sdk.core.types.EntityType;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.web.actions.CreateData;
import org.ngbw.web.controllers.DataFormatController;

@SuppressWarnings("serial")
public class DataUploadLists extends CreateData
{
	/*================================================================
	 * Constants
	 *================================================================*/
	// semantic annotation drop-down list properties
	public static final String ENTITY_TYPE = "entityType";
	public static final String DATA_TYPE = "dataType";
	public static final String DATA_FORMAT = "dataFormat";
	
	/*================================================================
	 * Properties
	 *================================================================*/
	private DataFormatController controller;
	
	/*================================================================
	 * Action methods
	 *================================================================*/
	public String execute() {
		// get current drop-down list selections from request params, if present
		String[] entityType = (String[])getParameters().get(ENTITY_TYPE);
		String[] dataType = (String[])getParameters().get(DATA_TYPE);
		String[] dataFormat = (String[])getParameters().get(DATA_FORMAT);
		
		// if any of the selections are missing, initialize them to "UNKNOWN"
		if (entityType == null || entityType.length < 1 || entityType[0].trim().equals(""))
			entityType = new String[]{UNKNOWN};
		if (dataType == null || dataType.length < 1 || dataType[0].trim().equals(""))
			dataType = new String[]{UNKNOWN};
		if (dataFormat == null || dataFormat.length < 1 || dataFormat[0].trim().equals(""))
			dataFormat = new String[]{UNKNOWN};
		
		// resolve the user's drop-down list selections
		getController().resolve(EntityType.valueOf(entityType[0]),
			DataType.valueOf(dataType[0]), DataFormat.valueOf(dataFormat[0]));
		return SUCCESS;
	}
	
	/*================================================================
	 * Property accessor methods
	 *================================================================*/
	public DataFormatController getController() {
		if (controller ==  null)
			controller = new DataFormatController(getWorkbenchSession());
		return controller;
	}
	
	/*================================================================
	 * Struts result accessor methods
	 *================================================================*/
	public InputStream getSelectState() {
		DataFormatController controller = getController();
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
		
		// render data formats drop-down list
		xml += "  <select name=\"" + DATA_FORMAT + "\">\n";
		selected = controller.getDataFormat().toString();
		conceptMap = mapConceptSet(controller.getDataFormats(), "DataFormat");
		if (conceptMap != null && conceptMap.size() > 0) {
			for (String dataFormat : conceptMap.keySet()) {
				xml += "    <option value=\"" + dataFormat + "\"";
				if (dataFormat.equals(selected))
					xml += " selected=\"true\"";
				xml += ">" + conceptMap.get(dataFormat) + "</option>\n";
			}
		}
		xml += "  </select>\n";
		
		xml += "</dropdowns>";
		return new ByteArrayInputStream(xml.getBytes());
	}
}
