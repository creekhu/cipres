package org.ngbw.examples;


import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.WorkbenchSession;
import org.ngbw.sdk.api.core.GenericDataRecordCollection;
import org.ngbw.sdk.api.data.SimpleSearchMetaQuery;
import org.ngbw.sdk.data.SearchHit;
import org.ngbw.sdk.core.types.Dataset;
import org.ngbw.sdk.core.types.RecordFieldType;
import org.ngbw.sdk.database.Folder;
import org.ngbw.sdk.database.RecordField;
import org.ngbw.sdk.database.SourceDocument;
import org.ngbw.sdk.database.User;
import org.ngbw.sdk.database.UserDataItem;


public class SimpleMetaQueryClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			Workbench wb = Workbench.getInstance();
			WorkbenchSession session = wb.getSession("t", "t");
			User user = session.getUser();
			String folderLabel = "Test Folder";
			Folder folder = findFolderByLabel(user.findFolders(), folderLabel);

			if (folder == null) {
				folder = new Folder(user);

				folder.setLabel(folderLabel);

				folder.save();
			}

			Set<Dataset> datasets = new HashSet<Dataset>();

//			datasets.add(Dataset.valueOf("GBEST"));
//			datasets.add(Dataset.valueOf("GBGSS"));
			datasets.add(Dataset.valueOf("GBSTS"));
			datasets.add(Dataset.valueOf("GBBCT"));
			datasets.add(Dataset.valueOf("GBENV"));
			datasets.add(Dataset.valueOf("GBHTC"));
			datasets.add(Dataset.valueOf("GBHTG"));
			datasets.add(Dataset.valueOf("GBINV"));
			datasets.add(Dataset.valueOf("GBMAM"));
			datasets.add(Dataset.valueOf("GBPAT"));
			datasets.add(Dataset.valueOf("GBPHG"));
			datasets.add(Dataset.valueOf("GBPLN"));
			datasets.add(Dataset.valueOf("GBPRI"));
			datasets.add(Dataset.valueOf("GBROD"));
			datasets.add(Dataset.valueOf("GBSTS"));
			datasets.add(Dataset.valueOf("GBSYN"));
			datasets.add(Dataset.valueOf("GBUNA"));
			datasets.add(Dataset.valueOf("GBVRL"));
			datasets.add(Dataset.valueOf("GBVRT"));
//			datasets.add(Dataset.valueOf("PDB"));
//			datasets.add(Dataset.valueOf("SWISSPROT"));
//			datasets.add(Dataset.valueOf("TREMBL"));

			String searchPhrase = "dog";
			SimpleSearchMetaQuery query = wb.getSimpleSearchQuery(datasets);
			query.setMaxResultsPerDataset(1000);
			query.execute(searchPhrase, true);
			for (Dataset ds : datasets) 
				System.out.println("Parallel Mode: SearchPhrase '" + searchPhrase + "' returned " 
						+ query.getResultCount(ds) + " results for " + ds);

			GenericDataRecordCollection<SearchHit> results = query.getResults();
			if (results == null) System.exit(0);
			System.out.println(results.size() + " search hits returned");
			int index = 0;
			for(SearchHit searchHit : results) {
				System.out.println(searchHit.getDataset());
				for(RecordFieldType field : results.getFields()) {
					System.out.println(field + ": " + searchHit.getField(field).getValueAsString());
				}
				System.out.println("SOURCE METADATA");
//				try {
				SourceDocument srcDoc = query.getRecordSource(searchHit);
				UserDataItem dataItem = new UserDataItem(srcDoc, folder);
				Map<String, String> metaData = dataItem.metaData();

				for (RecordField field : searchHit.getFields())
					metaData.put(field.getFieldType().toString(), field.getValueAsString());

				System.out.println("EntityType: " + srcDoc.getEntityType());
				System.out.println("DataType: " + srcDoc.getDataType());
				System.out.println("DataFormat: " + srcDoc.getDataFormat());
				System.out.println("\nSOURCE");
				System.out.println(srcDoc);


				System.out.println("USERDATA_ID: " + dataItem.getUserDataId());
//				}

				System.out.println("---------------------------\n");
				if (++index == 5) break;
			}
			System.out.println("Search time: " + query.getSearchTime());


			query.setMaxResultsPerDataset(1000);
			query.execute(searchPhrase, false);
			for (Dataset ds : datasets) 
				System.out.println("Serial Mode: SearchPhrase '" + searchPhrase + "' returned " 
						+ query.getResultCount(ds) + " results for " + ds);

			results = query.getResults();
			if (results == null) System.exit(0);
			System.out.println(results.size() + " search hits returned");
			System.out.println("Search time: " + query.getSearchTime());
		}
		catch (Exception err) {
			err.printStackTrace(System.err);

			System.exit(-1);
		}
		
	}

	private static Folder findFolderByLabel(List<Folder> folders, String label)
	{
		for (Iterator<Folder> elements = folders.iterator() ; elements.hasNext() ; ) {
			Folder folder = elements.next();

			if (folder.getLabel().equals(label))
				return folder;
		}

		return null;
	}
}
