package org.ngbw.web.controllers;

import java.util.HashSet;
import java.util.Set;

import org.ngbw.sdk.WorkbenchSession;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.EntityType;
import org.ngbw.sdk.core.types.RecordType;

/**
 * Controller class to populate NGBW web application entity and data type
 * drop-down lists, and to validate selections from these lists.
 * 
 * @author Jeremy Carver
 */
public abstract class RecordTypeController extends SessionController
{
	/*================================================================
	 * Properties
	 *================================================================*/
	private EntityType entityType;
	private DataType dataType;
	
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
	public RecordTypeController(WorkbenchSession workbenchSession)
	throws IllegalArgumentException {
		this(workbenchSession, EntityType.UNKNOWN, DataType.UNKNOWN);
	}
	
	/**
	 * Constructor taking default EntityType and DataType arguments.
	 * Requires a valid WorkbenchSession argument in order to establish
	 * the proper connection with the model layer that is needed to
	 * interact with the workbench.
	 * 
	 * @param workbenchSession	The WorkbenchSession object representing the
	 * 							current authenticated user's workbench session.
	 * @param entityType		The default EntityType to be stored in this controller.
	 * @param dataType			The default DataType to be stored in this controller.
	 * 
	 * @throws IllegalArgumentException	if the provided WorkbenchSession
	 * 									object is null.
	 */
	public RecordTypeController(WorkbenchSession workbenchSession,
	EntityType entityType, DataType dataType)
	throws IllegalArgumentException {
		super(workbenchSession);
		resolve(entityType, dataType);
	}
	
	/*================================================================
	 * Property Accessor Methods
	 *================================================================*/
	public EntityType getEntityType() {
		return entityType;
	}

	public void setEntityType(EntityType entityType) {
		if (isValid(entityType) == false)
			throw new IllegalArgumentException("The provided EntityType is not valid.");
		else {
			this.entityType = entityType;
			if (isValid(dataType) == false)
				setDataType(DataType.UNKNOWN);
		}
	}

	public DataType getDataType() {
		return dataType;
	}

	public void setDataType(DataType dataType) {
		if (isValid(dataType) == false)
			throw new IllegalArgumentException("The provided DataType is not valid.");
		else this.dataType = dataType;
	}
	
	public RecordType getRecordType() {
		try {
			return getWorkbench().getRecordType(entityType, dataType);
		} catch (Throwable error) {
			reportError(error, "Error retrieving RecordType for EntityType " +
				entityType + " and DataType " + dataType);
			return null;
		}
	}
	
	/*================================================================
	 * General Accessor Methods
	 *================================================================*/
	public Set<EntityType> getEntityTypes() {
		try {
			Set<EntityType> entityTypes =
				new HashSet<EntityType>(getWorkbench().getEntityTypes());
			entityTypes.add(EntityType.UNKNOWN);
			return entityTypes;
		} catch (Throwable error) {
			reportError(error, "Error retrieving the set of all non-abstract EntityTypes");
			return null;
		}
	}
	
	public Set<DataType> getDataTypes() {
		try {
			Set<DataType> dataTypes;
			if (entityType == null)
				dataTypes = new HashSet<DataType>(1);
			else if (entityType.equals(EntityType.UNKNOWN))
				dataTypes = new HashSet<DataType>(getWorkbench().getDataTypes());
			else {
				Set<RecordType> recordTypes = getWorkbench().getRecordTypes(entityType);
				dataTypes = new HashSet<DataType>(recordTypes.size());
				for (RecordType recordType : recordTypes) {
					dataTypes.add(getWorkbench().getDataType(recordType));
				}
			}
			dataTypes.add(DataType.UNKNOWN);
			return dataTypes;
		} catch (Throwable error) {
			reportError(error, "Error retrieving the set of non-abstract DataTypes " +
				"for EntityType " + entityType);
			return null;
		}
	}
	
	/*================================================================
	 * Functionality Methods
	 *================================================================*/
	public boolean isValid(EntityType entityType) {
		if (entityType == null)
			return false;
		else {
			Set<EntityType> entityTypes = getEntityTypes();
			if (entityTypes == null)
				return false;
			else return entityTypes.contains(entityType);
		}
	}
	
	public boolean isValid(DataType dataType) {
		if (dataType == null)
			return false;
		else {
			Set<DataType> dataTypes = getDataTypes();
			if (dataTypes == null)
				return false;
			else return dataTypes.contains(dataType);
		}
	}
	
	public boolean validate() {
		if (isValid(entityType) == false) {
			setEntityType(EntityType.UNKNOWN);
			return false;
		} else if (isValid(dataType) == false) {
			setDataType(DataType.UNKNOWN);
			return false;
		} else return true;
	}
	
	public void resolve(EntityType entityType, DataType dataType) {
		if (isValid(entityType) == false)
			setEntityType(EntityType.UNKNOWN);
		else setEntityType(entityType);
		if (isValid(dataType) == false)
			setDataType(DataType.UNKNOWN);
		else setDataType(dataType);
	}
}
