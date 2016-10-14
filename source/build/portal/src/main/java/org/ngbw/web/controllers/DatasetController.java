package org.ngbw.web.controllers;

import java.util.HashSet;
import java.util.Set;

import org.ngbw.sdk.WorkbenchSession;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.Dataset;
import org.ngbw.sdk.core.types.EntityType;
import org.ngbw.sdk.core.types.RecordType;

public class DatasetController extends RecordTypeController
{
	/*================================================================
	 * Properties
	 *================================================================*/
	private Dataset[] selectedDatasets;
	
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
	public DatasetController(WorkbenchSession workbenchSession)
	throws IllegalArgumentException {
		this(workbenchSession, EntityType.UNKNOWN, DataType.UNKNOWN, null);
	}
	
	/**
	 * Constructor taking default EntityType, DataType and Dataset array arguments.
	 * Requires a valid WorkbenchSession argument in order to establish
	 * the proper connection with the model layer that is needed to
	 * interact with the workbench.
	 * 
	 * @param workbenchSession	The WorkbenchSession object representing the
	 * 							current authenticated user's workbench session.
	 * @param entityType		The default EntityType to be stored in this controller.
	 * @param dataType			The default DataType to be stored in this controller.
	 * @param selectedDatasets	The default Dataset array to be stored in this controller.
	 * 
	 * @throws IllegalArgumentException	if the provided WorkbenchSession
	 * 									object is null.
	 */
	public DatasetController(WorkbenchSession workbenchSession,
	EntityType entityType, DataType dataType, Dataset[] selectedDatasets)
	throws IllegalArgumentException {
		super(workbenchSession);
		resolve(entityType, dataType, selectedDatasets);
	}
	
	/*================================================================
	 * Property Accessor Methods
	 *================================================================*/
	public void setEntityType(EntityType entityType) {
		super.setEntityType(entityType);
		if (isValid(selectedDatasets) == false)
			setSelectedDatasets(null);
	}
	
	public void setDataType(DataType dataType) {
		super.setDataType(dataType);
		if (isValid(selectedDatasets) == false)
			setSelectedDatasets(null);
	}
	
	public Dataset[] getSelectedDatasets() {
		return selectedDatasets;
	}
	
	public void setSelectedDatasets(Dataset[] selectedDatasets) {
		if (isValid(selectedDatasets) == false)
			throw new IllegalArgumentException("The provided Datasets array is not valid.");
		else this.selectedDatasets = selectedDatasets;
	}
	
	/*================================================================
	 * General Accessor Methods
	 *================================================================*/
	public Set<EntityType> getEntityTypes() {
		try {
			Set<EntityType> entityTypes =
				new HashSet<EntityType>(getWorkbench().getSearchableEntityTypes());
			entityTypes.add(EntityType.UNKNOWN);
			return entityTypes;
		} catch (Throwable error) {
			reportError(error, "Error retrieving the set of searchable EntityTypes");
			return null;
		}
	}
	
	public Set<DataType> getDataTypes() {
		try {
			Set<DataType> dataTypes;
			EntityType entityType = getEntityType();
			if (entityType == null || entityType.equals(EntityType.UNKNOWN))
				dataTypes = new HashSet<DataType>(1);
			else {
				Set<RecordType> recordTypes = getWorkbench().getSearchableRecordTypes();
				dataTypes = new HashSet<DataType>(recordTypes.size());
				for (RecordType recordType : recordTypes) {
					if (getWorkbench().getEntityType(recordType).equals(entityType))
						dataTypes.add(getWorkbench().getDataType(recordType));
				}
			}
			dataTypes.add(DataType.UNKNOWN);
			return dataTypes;
		} catch (Throwable error) {
			reportError(error, "Error retrieving the set of searchable DataTypes " +
				"for EntityType " + getEntityType());
			return null;
		}
	}
	
	public Set<Dataset> getDatasets() {
		try {
			RecordType recordType = getRecordType();
			if (recordType == null || recordType.equals(RecordType.UNKNOWN))
				return null;
			else return new HashSet<Dataset>(getWorkbench().getDatasets(recordType));
		} catch (Throwable error) {
			reportError(error, "Error retrieving the set of registered Datasets " +
				"for RecordType " + getRecordType());
			return null;
		}
	}
	
	/*================================================================
	 * Functionality Methods
	 *================================================================*/
	public boolean isValid(Dataset[] selectedDatasets) {
		if (selectedDatasets == null)
			return true;
		else {
			Set<Dataset> datasets = getDatasets();
			if (datasets == null)
				return false;
			else {
				for (int i=0; i<selectedDatasets.length; i++) {
					if (datasets.contains(selectedDatasets[i]) == false)
						return false;
				}
				return true;
			}
		}
	}
	
	public boolean validate() {
		boolean recordTypeValid = super.validate();
		if (isValid(selectedDatasets) == false) {
			setSelectedDatasets(null);
			return false;
		} else return recordTypeValid;
	}
	
	public void resolve(EntityType entityType, DataType dataType, Dataset[] selectedDatasets) {
		super.resolve(entityType, dataType);
		if (isValid(selectedDatasets) == false)
			setSelectedDatasets(null);
		else setSelectedDatasets(selectedDatasets);
	}
}
