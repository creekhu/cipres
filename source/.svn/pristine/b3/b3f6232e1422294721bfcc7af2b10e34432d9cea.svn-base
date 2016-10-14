package org.ngbw.web.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.WorkbenchSession;
import org.ngbw.sdk.api.core.GenericDataRecordCollection;
import org.ngbw.sdk.core.shared.IndexedDataRecord;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.core.types.RecordType;
import org.ngbw.sdk.database.Folder;
import org.ngbw.sdk.database.SourceDocument;
import org.ngbw.sdk.database.UserDataItem;
import org.ngbw.sdk.database.util.UserDataItemSortableField;

public class DataController extends FolderController
{
	/*================================================================
	 * Constructor
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
	public DataController(WorkbenchSession workbenchSession)
	throws IllegalArgumentException {
		super(workbenchSession);
	}
	
	/*================================================================
	 * Accessor Methods
	 *================================================================*/
	/**
	 * Gets the specified data item from the database.
	 * 
	 * @param dataId	The Long identifier of the data item to be retrieved.
	 * 
	 * @return	The specified UserDataItem retrieved from the database.
	 * 			Returns null if the argument ID is null, or if
	 * 			an error occurs.
	 */
	public UserDataItem getDataItem(Long dataId) {
		if (dataId == null) {
			debug("The selected data item could not be retrieved " +
				"because the provided data item ID is null.");
			return null;
		} else try {
			return getWorkbenchSession().findUserDataItem(dataId);
		} catch (Throwable error) {
			reportError(error, "Error retrieving data item with ID " + dataId);
			return null;
		}
	}
	
	public List<UserDataItem> getFolderData(Folder folder) {
		if (folder == null) {
			debug("The selected folder's data item set could not " +
				"be retrieved because the provided folder is null.");
			return null;
		} else try {
			return folder.findDataItems();
		} catch (Throwable error) {
			reportError(error, "Error retrieving data list for folder " +
				getFolderText(folder));
			return null;
		}
	}
	
	public List<UserDataItem> sortFolderData(Folder folder,
		UserDataItemSortableField field, boolean reverse) {
		List<UserDataItem> dataItems = getFolderData(folder);
		if (dataItems == null) {
			debug("The selected folder's data items could not be sorted " +
				"because the retrieved data item list is null.");
			return null;
		} else return sortDataItems(dataItems, field, reverse);
	}
	
	public Map<RecordType, List<UserDataItem>> getFolderDataMap(Folder folder,
		UserDataItemSortableField field, boolean reverse) {
		if (folder == null) {
			debug("The selected folder's data item map could not " +
				"be retrieved because the provided folder is null.");
			return null;
		} else try {
			Map<RecordType, List<UserDataItem>> dataMap =
				getWorkbench().sortDataItemsByRecordType(folder);
			if (dataMap == null)
				return null;
			else {
				Map<RecordType, List<UserDataItem>> sortedMap =
					new HashMap<RecordType, List<UserDataItem>>(dataMap.size());
				for (RecordType recordType : dataMap.keySet()) {
					List<UserDataItem> dataList = sortDataItems(
						dataMap.get(recordType), field, reverse);
					if (dataList != null && dataList.size() > 0)
						sortedMap.put(recordType, dataList);
				}
				if (sortedMap.size() < 1)
					return null;
				else return sortedMap;
			}
		} catch (Throwable error) {
			reportError(error, "Error retrieving data item map from folder " +
				getFolderText(folder));
			return null;
		}
	}
	
	public Map<RecordType, List<UserDataItem>> getFolderDataMap(Folder folder) {
		return getFolderDataMap(folder, UserDataItemSortableField.ID, true);
	}
	
	public SourceDocument getSourceDocument(UserDataItem dataItem) {
		if (dataItem == null) {
			debug("Source document could not be extracted from the selected data item " +
				"because the provided data item is null.");
			return null;
		} else try {
			return getWorkbenchSession().getSourceDocument(dataItem);
		} catch (Throwable error) {
			reportError(error, "Error retrieving source document from data item " +
				"with ID " + dataItem.getUserDataId() + " (\"" + dataItem.getLabel() + "\")");
			return null;
		}
	}
	public SourceDocument getSourceDocument(UserDataItem dataItem,
		IndexedDataRecord dataRecord) {
		SourceDocument sourceDocument = getSourceDocument(dataItem);
		if (sourceDocument == null) {
			debug("The selected data record's source document could not be " +
				"extracted from the selected data item because the retrieved " +
				"parent source document is null.");
			return null;
		} else if (dataRecord == null) {
			debug("The selected data record's source document could not be " +
				"extracted from the selected data item because the provided " +
				"data record is null.");
			return null;
		} else try {
			return getWorkbench().extractSubSourceDocument(sourceDocument,
				dataRecord.getIndex());
		} catch (Throwable error) {
			reportError(error, "Error retrieving source document for data record " +
				"with index " + dataRecord.getIndex() + " from data item " +
				"with ID " + dataItem.getUserDataId() + " (\"" + dataItem.getLabel() + "\")");
			return null;
		}
	}
	
	public GenericDataRecordCollection<IndexedDataRecord> getDataRecords(
		UserDataItem dataItem) {
		SourceDocument sourceDocument = getSourceDocument(dataItem);
		if (sourceDocument == null) {
			debug("Data records could not be extracted from the selected data item " +
				"because the retrieved source document is null.");
			return null;
		} else try {
			Workbench workbench = getWorkbench();
			if (workbench.canRead(sourceDocument.getType()) == false) {
				debug("Data records could not be extracted from the selected data " +
					"item because the retrieved source document cannot be read.");
				return null;
			} else return workbench.extractDataRecords(dataItem);
		} catch (Throwable error) {
			reportError(error, "Error extracting data records from data item " +
				"with ID " + dataItem.getUserDataId() + " (\"" + dataItem.getLabel() + "\")");
			return null;
		}
	}
	
	public Map<UserDataItem, GenericDataRecordCollection<IndexedDataRecord>> getDataRecordMap(
		List<UserDataItem> dataList) {
		if (dataList == null || dataList.size() < 1) {
			debug("Data records could not be extracted from the selected data " +
				"item list because the provided list contains no data items.");
			return null;
		} else {
			Map<UserDataItem, GenericDataRecordCollection<IndexedDataRecord>>
				dataRecordMap = new HashMap<UserDataItem,
					GenericDataRecordCollection<IndexedDataRecord>>(dataList.size());
			for (UserDataItem dataItem : dataList) {
				GenericDataRecordCollection<IndexedDataRecord> dataRecords =
					getDataRecords(dataItem);
				if (dataRecords != null)
					dataRecordMap.put(dataItem, dataRecords);
			}
			if (dataRecordMap.size() < 1) {
				debug("Data records could not be extracted from the selected data " +
					"item list because none of the data items in the list could be " +
					"successfully parsed into data records.");
				return null;
			} else return dataRecordMap;
		}
	}
	
	/*================================================================
	 * Functionality Methods
	 *================================================================*/
	/**
	 * Retrieves a fresh copy of the specified data item from the database.
	 * 
	 * @param dataItem	The UserDataItem to be refreshed.
	 * 
	 * @return	The UserDataItem freshly retrieved from the database.
	 * 			Returns null if the specified data item is not present in
	 * 			the database, or if an error occurs.
	 */
	public UserDataItem refreshDataItem(UserDataItem dataItem) {
		if (dataItem == null) {
			debug("The selected data item could not be refreshed " +
				"because the provided data item is null.");
			return null;
		} else try {
			dataItem = getDataItem(dataItem.getUserDataId());
			if (dataItem == null) {
				debug("The selected data item could not be refreshed " +
					"because the data item was not found in the database.");
				return null;
			} else return dataItem;
		} catch (Throwable error) {
			if (dataItem == null)
				reportError(error, "Error refreshing data item");
			else reportError(error, "Error refreshing data item with ID " +
				dataItem.getUserDataId() + " (\"" + dataItem.getLabel() + "\")");
			return null;
		}
	}
	
	public List<UserDataItem> sortDataItems(List<UserDataItem> dataItems,
		UserDataItemSortableField field, boolean reverse) {
		try {
			getWorkbenchSession().sortUserDataItems(
				dataItems, field, reverse);
			return dataItems;
		} catch (Throwable error) {
			reportError(error, "Error sorting data items");
			return null;
		}
	}
	
	public GenericDataRecordCollection<IndexedDataRecord> sortDataRecords(
		GenericDataRecordCollection<IndexedDataRecord> dataRecords,
		RecordFieldType fieldType, boolean reverse) {
		if (dataRecords == null) {
			debug("The selected data records could not be sorted " +
				"because the provided data record collection is null.");
			return null;
		} else try {
			dataRecords.sortByField(fieldType, reverse);
			return dataRecords;
		} catch (Throwable error) {
			reportError(error, "Error sorting data records");
			return null;
		}
	}
}
