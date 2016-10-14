package org.ngbw.examples;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.WorkbenchSession;
import org.ngbw.sdk.common.util.FileUtils;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.EntityType;
import org.ngbw.sdk.database.CachedItem;
import org.ngbw.sdk.database.Folder;
import org.ngbw.sdk.database.Task;
import org.ngbw.sdk.database.TaskInputSourceDocument;
import org.ngbw.sdk.database.User;
import org.ngbw.sdk.database.UserDataItem;
import org.ngbw.sdk.database.util.TaskComparator;
import org.ngbw.sdk.database.util.TaskSortableField;


public class WorkbenchSessionClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			String username = "t"; 
			String password = "t";
			Workbench wb = Workbench.getInstance();
			WorkbenchSession session = wb.getSession(username, password);
			User user = session.getUser();
			String label = "Test Folder";
			Folder folder = findFolderByLabel(user.findFolders(), label);

			if (folder == null) {
				folder = new Folder(user);

				folder.setLabel(label);

				folder.save();
			}


			System.out.println("Start find all User Tasks: " + Calendar.getInstance().getTime());
			List<Task> userTasks = user.findTasks();
			TaskComparator.sort(userTasks, TaskSortableField.ID, false);
			System.out.println("ENF find all User Tasks: " + Calendar.getInstance().getTime());

			String taskLabel = "DSSP-TASK-TEST";
			Task task = findTaskByLabel(user.findTasks(), taskLabel);

			if (task == null) {
				task = new Task(folder);

				task.setToolId("DSSP");
				task.setLabel(taskLabel);

				Map<String, String> parameters = task.toolParameters();
				parameters.put("classic_","false");
				parameters.put("surface_","false");

				byte[] data = FileUtils.readFile("../samplefiles/pdb.pdb");
				List<TaskInputSourceDocument> paramInput = new ArrayList<TaskInputSourceDocument>();

				paramInput.add(new TaskInputSourceDocument("foo", EntityType.COMPOUND, DataType.STRUCTURE, DataFormat.PDB, data, false));

				task.input().put("pdbfile_", paramInput);

				task.save();
			}

			UserDataItem dataItem = new UserDataItem(folder);
			dataItem.setEntityType(EntityType.PROTEIN);
			dataItem.setDataType(DataType.SEQUENCE);
			dataItem.setDataFormat(DataFormat.FASTA);
			dataItem.setLabel("Protein Sequence");
			dataItem.setData(
					">gi|5524211|gb|AAD44166.1| cytochrome b [Elephas maximus maximus]\n" +
					"LCLYTHIGRNIYYGSYLYSETWNTGIMLLLITMATAFMGYVLPWGQMSFWGATVITNLFSAIPYIGTNLV\n" +
					"EWIWGGFSVDKATLNRFFAFHFILPFTMVALAGVHLTFLHETGSNNPLGLTSDSDKIPFHPYYTIKDFLG\n" +
					"LLILILLLLLLALLSPDMLGDPDNHMPADPLNTPLHIKPEWYFLFAYAILRSVPNKLGGVLALFLSIVIL\n" +
					"GLMPFLHTSKHRSMMLRPLSQALFWTLTMDLLTLTWIGSQPVEYPYTIIGQMASILYFSIILAFLPIAGX\n" +
			"IENY");

			dataItem.save();

			String attribute = "currentTask";
			CachedItem sessionItem = new CachedItem(user.getUsername(), attribute);
			sessionItem.setValue(String.valueOf(task.getTaskId()));
			sessionItem.save();
			long cachedItemId = sessionItem.getCachedItemId();
			System.out.println("before: " + task.getTaskId() + " : " + task.getLabel());
			task = null;
			sessionItem = null;
			sessionItem = new CachedItem(cachedItemId);
			task = new Task(Long.parseLong(new String(sessionItem.getValue())));
			System.out.println("after : " + task.getTaskId() + " : " + task.getLabel());
			sessionItem.delete();

			attribute = "currentDataItem";
			sessionItem = new CachedItem(user.getUsername(), attribute);
			sessionItem.setValue(String.valueOf(dataItem.getUserDataId()));
			sessionItem.save();
			cachedItemId = sessionItem.getCachedItemId();
			System.out.println("before: " + dataItem.getUserDataId() + " : " + dataItem.getLabel());
			dataItem = null;
			sessionItem = null;
			sessionItem = new CachedItem(cachedItemId);
			dataItem = new UserDataItem(Long.parseLong(new String(sessionItem.getValue())));
			System.out.println("after : " + dataItem.getUserDataId() + " : " + dataItem.getLabel());
			sessionItem.delete();

			attribute = "currentFolder";
			sessionItem = new CachedItem(user.getUsername(), attribute);
			sessionItem.setValue(String.valueOf(folder.getFolderId()));
			sessionItem.save();
			cachedItemId = sessionItem.getCachedItemId();
			System.out.println("before: " + folder.getFolderId() + " : " + folder.getLabel());
			folder = null;
			sessionItem = null;
			sessionItem = new CachedItem(cachedItemId);
			folder = new Folder(Long.parseLong(new String(sessionItem.getValue())));
			System.out.println("after : " + folder.getFolderId() + " : " + folder.getLabel());
			sessionItem.delete();
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

	private static Task findTaskByLabel(List<Task> tasks, String label)
	{
		for (Iterator<Task> elements = tasks.iterator() ; elements.hasNext() ; ) {
			Task task = elements.next();

			if (task.getLabel().equals(label))
				return task;
		}

		return null;
	}

}
