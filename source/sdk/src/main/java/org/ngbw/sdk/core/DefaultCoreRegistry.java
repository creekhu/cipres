/*
 * DefaultCoreRegistry.java
 */
package org.ngbw.sdk.core;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.api.core.CoreRegistry;
import org.ngbw.sdk.api.core.SourceDocumentTransformer;
import org.ngbw.sdk.core.shared.SourceDocumentType;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.EntityType;
import org.ngbw.sdk.core.types.FieldDataType;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.core.types.RecordType;


/**
 * 
 * @author Roland H. Niedner
 * 
 */
public class DefaultCoreRegistry implements CoreRegistry {
	
	private static Log log = LogFactory
			.getLog(DefaultCoreRegistry.class);
	// maps RecordField to the FieldType
	private Map<RecordFieldType, FieldDataType> recordFieldTypeMap = new HashMap<RecordFieldType, FieldDataType>();
	// maps RecordType to the List of RecordFields
	private Map<RecordType, Set<RecordFieldType>> recordTypeRecordFieldsMap = new HashMap<RecordType, Set<RecordFieldType>>();
	// maps EntityType and DataType to a RecordType
	private Map<EntityType, Map<DataType, RecordType>> recordTypesMap = new TreeMap<EntityType, Map<DataType, RecordType>>();
	// maps RecordType to the is abstract attribute
	private Set<RecordType> abstractRecordTypes = new HashSet<RecordType>();
	// maps RecordType to its parent
	private Map<RecordType, RecordType> isaMapRecordType = new HashMap<RecordType, RecordType>();
	// maps EntityType to its parent
	private Map<EntityType, EntityType> isaMapEntityType = new HashMap<EntityType, EntityType>();
	// maps DataType to its parent
	private Map<DataType, DataType> isaMapDataType = new HashMap<DataType, DataType>();
	// maps RecordType to its transform target RecordType
	private Map<SourceDocumentType, Map<RecordType, Class<SourceDocumentTransformer>>> transformerClasses = new TreeMap<SourceDocumentType, Map<RecordType, Class<SourceDocumentTransformer>>>();

	DefaultCoreRegistry() { }
	
	public boolean isa(RecordType child, RecordType parent) {
		if (child == null)
			throw new NullPointerException("child RecordType is null!");
		if (parent == null)
			throw new NullPointerException("parent RecordType is null!");
		if (getAllRecordTypes().contains(child) == false)
			throw new RuntimeException("Inavalid child RecordType " + child);
		if (getAllRecordTypes().contains(parent) == false)
			throw new RuntimeException("Invalid parent RecordType " + parent);
		return checkIsaRecordType(child, parent);
	}
	
	private boolean checkIsaRecordType(RecordType child, RecordType parent) {
		if (isaMapRecordType.containsKey(child) == false)
			return false;
		RecordType directParent = isaMapRecordType.get(child);
		if (directParent.equals(parent))
			return true;
		else 
			return checkIsaRecordType(directParent, parent);
	}
	
	public boolean isa(EntityType child, EntityType parent) {
		if (child == null)
			throw new NullPointerException("child EntityType is null!");
		if (parent == null)
			throw new NullPointerException("parent EntityType is null!");
		if (getAllEntityTypes().contains(child) == false)
			throw new RuntimeException("Inavalid child EntityType " + child);
		if (getAllEntityTypes().contains(parent) == false)
			throw new RuntimeException("Inavalid parent EntityType " + parent);
		return checkIsaEntityType(child, parent);
	}
	
	private boolean checkIsaEntityType(EntityType child, EntityType parent) {
		if (isaMapEntityType.containsKey(child) == false)
			return false;
		EntityType directParent = isaMapEntityType.get(child);
		if (directParent.equals(parent))
			return true;
		else 
			return checkIsaEntityType(directParent, parent);
	}
	
	public boolean isa(DataType child, DataType parent) {
		if (child == null)
			throw new NullPointerException("child DataType is null!");
		if (parent == null)
			throw new NullPointerException("parent DataType is null!");
		if (getAllDataTypes().contains(child) == false)
			throw new RuntimeException("Inavalid child DataType " + child);
		if (getAllDataTypes().contains(parent) == false)
			throw new RuntimeException("Inavalid parent DataType " + parent);
		return checkIsaDataType(child, parent);
	}
	
	private boolean checkIsaDataType(DataType child, DataType parent) {
		if (isaMapDataType.containsKey(child) == false)
			return false;
		DataType directParent = isaMapDataType.get(child);
		if (directParent.equals(parent))
			return true;
		else 
			return checkIsaDataType(directParent, parent);
	}
	
	public boolean isAbstract(RecordType recordType) {
		if (recordType == null)
			throw new NullPointerException("RecordType is null!");
		if (getAllRecordTypes().contains(recordType) == false)
			throw new RuntimeException("RecordType is invalid!");
		return abstractRecordTypes.contains(recordType);
	}

	public Set<EntityType> getAllEntityTypes() {
		return recordTypesMap.keySet();
	}

	public Set<EntityType> getEntityTypes() {
		if (recordTypesMap.isEmpty())
			return null;

		Set<EntityType> entityTypes =  new HashSet<EntityType>();

		for (Iterator<Map.Entry<EntityType, Map<DataType, RecordType>>> mapNodes = recordTypesMap.entrySet().iterator() ; mapNodes.hasNext() ; ) {
			Map.Entry<EntityType, Map<DataType, RecordType>> node = mapNodes.next();
			EntityType entity = node.getKey();
			Map<DataType, RecordType> recordTypes = node.getValue();

			for (Iterator<RecordType> values = recordTypes.values().iterator() ; values.hasNext() ; ) {
				RecordType record = values.next();

				if (abstractRecordTypes.contains(record) == false) {
					entityTypes.add(entity);

					break;
				}
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("getEntityTypes() returns: ");
			for (EntityType entityType : entityTypes)
				log.debug("\tEntityType." + entityType);
		}

		return entityTypes;
	}

	public Set<DataType> getDataTypes() {
		if (recordTypesMap.isEmpty())
			return null;

		Set<DataType> dataTypes =  new HashSet<DataType>();

		for (Iterator<Map<DataType, RecordType>> values = recordTypesMap.values().iterator() ; values.hasNext() ; ) {
			Map<DataType, RecordType> typesMap = values.next();

			for (Iterator<Map.Entry<DataType, RecordType>> entries = typesMap.entrySet().iterator() ; entries.hasNext() ; ) {
				Map.Entry<DataType, RecordType> entry = entries.next();
				DataType data = entry.getKey();
				RecordType record = entry.getValue();

				if (abstractRecordTypes.contains(record) == false)
					dataTypes.add(data);
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("getDataTypes() returns: ");
			for (DataType dataType : dataTypes)
				log.debug("\tDataType." + dataType);
		}

		return dataTypes;
	}

	public Set<DataType> getAllDataTypes() {
		if (recordTypesMap.isEmpty())
			return null;

		Set<DataType> dataTypes = new HashSet<DataType>();

		for (Iterator<Map<DataType, RecordType>> values = recordTypesMap.values().iterator() ; values.hasNext() ; ) {
			Map<DataType, RecordType> typesMap = values.next();

			dataTypes.addAll(typesMap.keySet());
		}

		return dataTypes;
	}

	public Set<RecordType> getAllRecordTypes() {
		if (recordTypesMap.isEmpty())
			return null;

		Set<RecordType> recordTypes =  new HashSet<RecordType>();

		for (Iterator<Map<DataType, RecordType>> values = recordTypesMap.values().iterator() ; values.hasNext() ; ) {
			Map<DataType, RecordType> typesMap = values.next();

			recordTypes.addAll(typesMap.values());
		}

		if (log.isDebugEnabled()) {
			log.debug("getRecordTypes() returns: ");
			for (RecordType rt : recordTypes)
				log.debug("\tRecordType." + rt);
		}

		return recordTypes;
	}

	public Set<RecordType> getRecordTypes() {
		if (recordTypesMap.isEmpty())
			return null;

		Set<RecordType> result =  new HashSet<RecordType>();

		for (Iterator<Map<DataType, RecordType>> values = recordTypesMap.values().iterator() ; values.hasNext() ; ) {
			Map<DataType, RecordType> typesMap = values.next();

			for (Iterator<RecordType> types = typesMap.values().iterator() ; types.hasNext() ; ) {
				RecordType record = types.next();

				if (abstractRecordTypes.contains(record) == false)
					result.add(record);
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("getRecordTypes() returns: ");
			for (RecordType rt : result)
				log.debug("\tRecordType." + rt);
		}

		return result;
	}

	public RecordType getRecordType(EntityType entityType, DataType dataType) {
		if (entityType == null)
			throw new NullPointerException("EntityType is null!");

		if (dataType == null)
			throw new NullPointerException("DataType is null!");

		Map<DataType, RecordType> recordTypes = recordTypesMap.get(entityType);

		if (recordTypes == null)
			return null;

		return recordTypes.get(dataType);
	}

	public Set<RecordType> getRecordTypes(EntityType entityType) {
		if (entityType == null)
			throw new NullPointerException("EntityType is null!");

		Map<DataType, RecordType> recordTypes = recordTypesMap.get(entityType);

		if (recordTypes == null)
			return null;

		Set<RecordType> result =  new HashSet<RecordType>();

		for (Iterator<RecordType> types = recordTypes.values().iterator() ; types.hasNext() ; ) {
			RecordType record = types.next();

			if (abstractRecordTypes.contains(record) == false)
				result.add(record);
		}

		return result;
	}

	public Set<RecordType> getRecordTypes(DataType dataType) {
		if (dataType == null)
			throw new NullPointerException("DataType is null!");

		Set<RecordType> recordTypes =  new HashSet<RecordType>();

		for (Iterator<Map<DataType, RecordType>> values = recordTypesMap.values().iterator() ; values.hasNext() ; ) {
			Map<DataType, RecordType> typesMap = values.next();
			RecordType record = typesMap.get(dataType);

			if (record == null)
				continue;

			if (abstractRecordTypes.contains(record))
				recordTypes.add(record);
		}

		return recordTypes;
	}

	public EntityType getEntityType(RecordType recordType) {
		if (recordType == null)
			throw new NullPointerException("RecordType is null!");

		Set<EntityType> entityTypes = new HashSet<EntityType>();

		for (Iterator<Map.Entry<EntityType, Map<DataType, RecordType>>> mapNodes = recordTypesMap.entrySet().iterator() ; mapNodes.hasNext() ; ) {
			Map.Entry<EntityType, Map<DataType, RecordType>> node = mapNodes.next();

			if (node.getValue().containsValue(recordType))
				entityTypes.add(node.getKey());
		}

		if (entityTypes.isEmpty())
			throw new NullPointerException("No EntityType is registered for RecordType: " + recordType);

		if (entityTypes.size() > 1)
			throw new RuntimeException("Registration Error: Multipe EntityTypes mapped for this RecordType: " + recordType);

		EntityType entityType = entityTypes.iterator().next();

		if (entityType == null)
			throw new NullPointerException("No EntityType is registered for RecordType: " + recordType);

		return entityType;
	}

	public DataType getDataType(RecordType recordType) {
		if (recordType == null)
			throw new NullPointerException("RecordType is null!");

		Set<DataType> dataTypes = new HashSet<DataType>();

		for (Iterator<Map<DataType, RecordType>> values = recordTypesMap.values().iterator() ; values.hasNext() ; ) {
			Map<DataType, RecordType> typesMap = values.next();

			for (Iterator<Map.Entry<DataType, RecordType>> mapNodes = typesMap.entrySet().iterator() ; mapNodes.hasNext() ; ) {
				Map.Entry<DataType, RecordType> node = mapNodes.next();

				if (node.getValue().equals(recordType))
					dataTypes.add(node.getKey());
			}
		}

		if (dataTypes.isEmpty())
			throw new NullPointerException("No DataType is registered for RecordType: " + recordType);

		if (dataTypes.size() > 1)
			throw new RuntimeException("Registration Error: Multipe DataTypes mapped for this RecordType: " + recordType);

		DataType dataType = dataTypes.iterator().next();

		if (dataType == null)
			throw new NullPointerException("No DataType is registered for RecordType: " + recordType);

		return dataType;
	}

	public Set<RecordFieldType> getRecordFields(RecordType recordType) {
		if (recordType == null)
			throw new NullPointerException("RecordType is null!");
		if (abstractRecordTypes.contains(recordType))
			throw new RuntimeException("RecordType is abstract " + recordType);
		if (recordTypeRecordFieldsMap.containsKey(recordType) == false)
			throw new NullPointerException("RecordType is not registered: " + recordType);
		return recordTypeRecordFieldsMap.get(recordType);
	}

	public FieldDataType getFieldType(RecordFieldType recordField) {
		if (recordField == null)
			throw new NullPointerException("RecordField is null!");
		if (recordFieldTypeMap.containsKey(recordField) == false)
			throw new NullPointerException("RecordField is not registered: " + recordField);
		return recordFieldTypeMap.get(recordField);
	}

	public void registerRecordField(RecordFieldType field, FieldDataType type) {
		if (field == null)
			throw new NullPointerException("RecordField is null!");
		if (type == null)
			throw new NullPointerException("FieldType is null!");
		recordFieldTypeMap.put(field, type);
	}

	public void registerRecordFields(Map<RecordFieldType, FieldDataType> fieldTypeMap) {
		if (fieldTypeMap == null)
			throw new NullPointerException("fieldTypeMap is null!");
		if (fieldTypeMap.isEmpty())
			throw new NullPointerException("fieldTypeMap is empty!");
		recordFieldTypeMap.putAll(fieldTypeMap);
	}

	public void registerRecordType(RecordType recordType,
			EntityType entityType, DataType dataType, Set<RecordFieldType> fields) {
		registerRecordType(recordType, entityType, dataType, fields, false, null);
	}

	public void registerRecordType(RecordType recordType,
			EntityType entityType, DataType dataType, Set<RecordFieldType> fields, 
			boolean isAbstract) {
		registerRecordType(recordType, entityType, dataType, fields, isAbstract, null);
	}

	public void registerRecordType(RecordType recordType,
			EntityType entityType, DataType dataType, Set<RecordFieldType> fields, 
			RecordType extendedRecordType) {
		registerRecordType(recordType, entityType, dataType, fields, false, extendedRecordType);
	}

	public void registerRecordType(RecordType recordType,
			EntityType entityType, DataType dataType, Set<RecordFieldType> fields, 
			boolean isAbstract, RecordType extendedRecordType) {
		if (recordType == null)
			throw new NullPointerException("RecordType is null!");
		if (entityType == null)
			throw new NullPointerException("entityType is null for RecordType " + recordType);
		if (dataType == null)
			throw new NullPointerException("dataType is null for RecordType " + recordType);

		Map<DataType, RecordType> mapNode = recordTypesMap.get(entityType);

		if (mapNode != null && mapNode.containsKey(dataType))
			throw new RuntimeException("For EntityType." + entityType + " and DataType."
					+ dataType + " this type is already registered RecordType." 
					+ getRecordType(entityType, dataType));

		if (extendedRecordType == null) {
			if (fields == null && isAbstract == false)
				throw new NullPointerException("fields is null for RecordType " + recordType);
			if (fields.isEmpty() && isAbstract == false)
				throw new NullPointerException("fields is empty for RecordType " + recordType);
		} else {
			if (fields == null)
				fields = recordTypeRecordFieldsMap.get(extendedRecordType);
			else {
				Set<RecordFieldType> existingFields = recordTypeRecordFieldsMap.get(extendedRecordType);
				if (existingFields != null) fields.addAll(existingFields);
			}
			if (recordType.equals(extendedRecordType) == false) 
				isaMapRecordType.put(recordType, extendedRecordType);
			EntityType extendedEntityType = getEntityType(extendedRecordType);
			if (entityType.equals(extendedEntityType) == false) 
				isaMapEntityType.put(entityType, extendedEntityType);
			DataType extendedDataType = getDataType(extendedRecordType);
			if (dataType.equals(extendedDataType) == false) 
				isaMapDataType.put(dataType, extendedDataType);
		}
		if(recordFieldTypeMap.keySet().containsAll(fields) == false)
			throw new RuntimeException("Not all submitted RecordFields " +
					"have registered FieldTypes for RecordType " + recordType);
		if (isAbstract) abstractRecordTypes.add(recordType);
		recordTypeRecordFieldsMap.put(recordType, fields);

		if (mapNode == null) {
			mapNode = new TreeMap<DataType, RecordType>();

			recordTypesMap.put(entityType, mapNode);
		}

		mapNode.put(dataType, recordType);

		if (log.isInfoEnabled()) {
			log.info("Registered RecordType." + recordType + " has EntityType." + getEntityType(recordType)
					+ " has DataType." + getDataType(recordType) 
					+ ((isAbstract(recordType)) ? " is abstract" : "") 
					+ ((extendedRecordType != null && isa(recordType, extendedRecordType)) ? " extends RecordType." + extendedRecordType : ""));
			if (fields != null)
			for (RecordFieldType field : fields)
				log.info("Registered RecordType." + recordType + " has RecordField." + field
						+ " with FieldType." + getFieldType(field));
		}
	}
	
	public boolean hasTransformerClass(SourceDocumentType sourceDocumentType, RecordType targetType) {
		if (sourceDocumentType == null)
			throw new NullPointerException("SourceDocumentType is null!");

		if (targetType == null)
			throw new NullPointerException("RecordType is null!");

		Map<RecordType, Class<SourceDocumentTransformer>> mapNode = transformerClasses.get(sourceDocumentType);

		if (mapNode == null)
			return false;

		return mapNode.containsKey(targetType);
	}

	public Set<RecordType> getTransformationTargetRecordTypes(SourceDocumentType type) {
		if (type == null)
			throw new NullPointerException("SourceDocumentType is null!");

		Map<RecordType, Class<SourceDocumentTransformer>> mapNode = transformerClasses.get(type);

		if (mapNode == null)
			return null;

		return mapNode.keySet();
	}

	public Class<SourceDocumentTransformer> getTransformerClass(SourceDocumentType sourceDocumentType, RecordType targetType) {
		if (sourceDocumentType == null)
			throw new NullPointerException("SourceDocumentType is null!");

		if (targetType == null)
			throw new NullPointerException("RecordType is null!");

		Map<RecordType, Class<SourceDocumentTransformer>> mapNode = transformerClasses.get(sourceDocumentType);

		if (mapNode == null || !mapNode.containsKey(targetType))
			throw new NullPointerException("No SourceDocumentTransformer is registered for " + sourceDocumentType);

		return mapNode.get(targetType);
	}

	public void registerTransformerClass(RecordType recordType, DataFormat dataFormat,
			RecordType targetType, Class<SourceDocumentTransformer> transformerClass) {
		if (recordType == null)
			throw new NullPointerException("RecordType is null!");
		if (dataFormat == null)
			throw new NullPointerException("DataFormat is null!");
		if (targetType == null)
			throw new NullPointerException("RecordType is null!");
		if (transformerClass == null)
			throw new NullPointerException("transformerClass is null!");
		EntityType entityType = getEntityType(recordType);
		DataType dataType = getDataType(recordType);
		SourceDocumentType sourceDocumentType = new SourceDocumentType(entityType, dataType, dataFormat);
		registerTransformerClass(sourceDocumentType, targetType, transformerClass);
	}

	public void registerTransformerClass(SourceDocumentType sourceDocumentType, RecordType targetType, Class<SourceDocumentTransformer> transformerClass) {
		if (sourceDocumentType == null)
			throw new NullPointerException("SourceDocumentType is null!");

		if (targetType == null)
			throw new NullPointerException("RecordType is null!");

		if (transformerClass == null)
			throw new NullPointerException("transformerClass is null!");

		Map<RecordType, Class<SourceDocumentTransformer>> mapNode = transformerClasses.get(sourceDocumentType);

		if (mapNode == null) {
			mapNode = new TreeMap<RecordType, Class<SourceDocumentTransformer>>();

			transformerClasses.put(sourceDocumentType, mapNode);
		}

		mapNode.put(targetType, transformerClass);
	}
}
