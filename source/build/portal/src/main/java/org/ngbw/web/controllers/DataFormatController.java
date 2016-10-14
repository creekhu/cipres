package org.ngbw.web.controllers;

import java.util.HashSet;
import java.util.Set;

import org.ngbw.sdk.WorkbenchSession;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.EntityType;
import org.ngbw.sdk.core.types.RecordType;

/**
 * Controller class to populate NGBW web application data format
 * drop-down lists, and to validate selections from these lists.
 * 
 * @author Jeremy Carver
 */
public class DataFormatController extends RecordTypeController
{
	/*================================================================
	 * Properties
	 *================================================================*/
	private DataFormat dataFormat;
	
	/*================================================================
	 * Constructors
	 *================================================================*/
	/**
	 * Default constructor.  Requires a valid WorkbenchSession argument in
	 * order to establish the proper connection with the model layer that
	 * is needed to interact with the workbench.
	 * 
	 * @param workbenchSession	The WorkbenchSession object representing the
	 * 							current authenticated user's workbench session.
	 * 
	 * @throws IllegalArgumentException	if the provided WorkbenchSession
	 * 									object is null.
	 */
	public DataFormatController(WorkbenchSession workbenchSession)
	throws IllegalArgumentException {
		this(workbenchSession, EntityType.UNKNOWN, DataType.UNKNOWN, DataFormat.UNKNOWN);
	}
	
	/**
	 * Constructor taking default EntityType, DataType and DataFormat arguments.
	 * Requires a valid WorkbenchSession argument in order to establish
	 * the proper connection with the model layer that is needed to
	 * interact with the workbench.
	 * 
	 * @param workbenchSession	The WorkbenchSession object representing the
	 * 							current authenticated user's workbench session.
	 * @param entityType		The default EntityType to be stored in this controller.
	 * @param dataType			The default DataType to be stored in this controller.
	 * @param dataFormat		The default DataFormat to be stored in this controller.
	 * 
	 * @throws IllegalArgumentException	if the provided WorkbenchSession
	 * 									object is null.
	 */
	public DataFormatController(WorkbenchSession workbenchSession,
	EntityType entityType, DataType dataType, DataFormat dataFormat)
	throws IllegalArgumentException {
		super(workbenchSession);
		resolve(entityType, dataType, dataFormat);
	}
	
	/*================================================================
	 * Property Accessor Methods
	 *================================================================*/
	public void setEntityType(EntityType entityType) {
		super.setEntityType(entityType);
		if (isValid(dataFormat) == false)
			setDataFormat(DataFormat.UNKNOWN);
	}
	
	public void setDataType(DataType dataType) {
		super.setDataType(dataType);
		if (isValid(dataFormat) == false)
			setDataFormat(DataFormat.UNKNOWN);
	}
	
	public DataFormat getDataFormat() {
		return dataFormat;
	}
	
	public void setDataFormat(DataFormat dataFormat) {
		if (isValid(dataFormat) == false)
			throw new IllegalArgumentException("The provided DataFormat is not valid.");
		else this.dataFormat = dataFormat;
	}
	
	/*================================================================
	 * General Accessor Methods
	 *================================================================*/
	public Set<DataFormat> getDataFormats() {
		try {
			Set<DataFormat> dataFormats;
			RecordType recordType = getRecordType();
			if (recordType == null)
				dataFormats = new HashSet<DataFormat>(1);
			else if (recordType.equals(RecordType.UNKNOWN))
				dataFormats = new HashSet<DataFormat>(getWorkbench().getRegisteredDataFormats());
			else dataFormats =
				new HashSet<DataFormat>(getWorkbench().getRegisteredDataFormats(recordType));
			dataFormats.add(DataFormat.UNKNOWN);
			return dataFormats;
		} catch (Throwable error) {
			reportError(error, "Error retrieving the set of registered DataFormats " +
				"for RecordType " + getRecordType());
			return null;
		}
	}
	
	/*================================================================
	 * Functionality Methods
	 *================================================================*/
	public boolean isValid(DataFormat dataFormat) {
		if (dataFormat == null)
			return false;
		else {
			Set<DataFormat> dataFormats = getDataFormats();
			if (dataFormats == null)
				return false;
			else return dataFormats.contains(dataFormat);
		}
	}
	
	public boolean validate() {
		boolean recordTypeValid = super.validate();
		if (isValid(dataFormat) == false) {
			setDataFormat(DataFormat.UNKNOWN);
			return false;
		} else return recordTypeValid;
	}
	
	public void resolve(EntityType entityType, DataType dataType, DataFormat dataFormat) {
		super.resolve(entityType, dataType);
		if (isValid(dataFormat) == false)
			setDataFormat(DataFormat.UNKNOWN);
		else setDataFormat(dataFormat);
	}
}
