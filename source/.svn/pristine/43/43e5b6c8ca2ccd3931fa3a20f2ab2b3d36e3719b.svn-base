/*
 * CipresFile.java
 */
package org.ngbw.cipresrest.webresource;


import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.ngbw.cipresrest.auth.AuthorizationException;
import org.ngbw.restdatatypes.ErrorData;
import org.ngbw.restdatatypes.FileInfo;
import org.ngbw.restdatatypes.FolderInfo;
import org.ngbw.restdatatypes.FolderItemInfo;
import org.ngbw.restdatatypes.TaskInfo;
import org.ngbw.sdk.ValidationException;
import org.ngbw.sdk.core.shared.UserRole;
import org.ngbw.sdk.database.Folder;
import org.ngbw.sdk.database.FolderItem;
import org.ngbw.sdk.database.NotExistException;
import org.ngbw.sdk.database.Task;
import org.ngbw.sdk.database.User;
import org.ngbw.sdk.database.UserDataItem;


/**
 *
 * @author Paul Hoover
 *
 */
@Path("/v1/file/{user}")
public class CipresFile {

	// data fields


	private static final String METADATA_SEPARATOR = ";";
	private @Context SecurityContext m_securityContext;
	private @PathParam("user") String m_username;


	// public methods


	@POST
	@Path("{folderName: .+}")
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public FolderItemInfo writeFile(@PathParam("folderName") String folderName, MultivaluedMap<String, String> formData) throws IOException, SQLException, URISyntaxException, CipresException
	{
		authorizeUser();

		if (!folderName.startsWith(Folder.SEPARATOR))
			throw new CipresException("Destination pathname must be absolute", Status.BAD_REQUEST, ErrorData.BAD_REQUEST);

		String sourceUriValue;

		if (formData != null && (sourceUriValue = formData.getFirst("sourceUri")) != null) {
			String fileName = formData.getFirst("fileName");

			if (fileName == null) {
				int offset = sourceUriValue.lastIndexOf(Folder.SEPARATOR);

				if (offset >= 0)
					fileName = sourceUriValue.substring(offset + 1);
				else
					fileName = sourceUriValue;
			}

			Folder destFolder = Folder.findFolder(folderName);

			if (destFolder == null)
				throw new CipresException("Folder " + folderName + " not found", Status.NOT_FOUND, ErrorData.NOT_FOUND);

			URI sourceUri = new URI(sourceUriValue);
			String scheme = sourceUri.getScheme();
			UserDataItem newItem;

			if (scheme == null || scheme.equalsIgnoreCase("file"))
				newItem = new UserDataItem(sourceUri.getPath(), destFolder);
			else if (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https")) {
				InputStream inStream = sourceUri.toURL().openStream();

				newItem = new UserDataItem(destFolder);

				newItem.setData(inStream);
			}
			else
				throw new ValidationException("sourceUri", "unsupported scheme " + scheme);

			newItem.setLabel(fileName);
			newItem.save();

			return createFileInfo(newItem, folderName);
		}
		else {
			int offset = folderName.lastIndexOf(Folder.SEPARATOR);
			String baseName;

			if (offset > 0)
				baseName = folderName.substring(0, offset);
			else
				baseName = Folder.SEPARATOR;

			User owner = getTargetUser();
			Folder newFolder = Folder.findOrCreateFolder(owner, folderName);

			return createFolderInfo(newFolder, baseName, false);
		}
	}

	@GET
	@Path("{fileName: .+}")
	public Response getFile(@PathParam("fileName") String fileName) throws IOException, SQLException, CipresException
	{
		authorizeUser();

		if (!fileName.startsWith(Folder.SEPARATOR))
			throw new CipresException("Pathname must be absolute", Status.BAD_REQUEST, ErrorData.BAD_REQUEST);

		UserDataItem targetItem = UserDataItem.findDataItem(fileName);

		if (targetItem == null)
			throw new CipresException(fileName + " not found", Status.NOT_FOUND, ErrorData.NOT_FOUND);

		ResponseBuilder response = Response.ok(targetItem.getDataAsStream());

		response.header("Content-Disposition", "attachment; filename=" + targetItem.getLabel());
		response.type("text/plain");

		return response.build();
	}

	@PUT
	@Path("{fileName: .+}")
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public FolderItemInfo updateFile(@PathParam("fileName") String fileName, MultivaluedMap<String, String> formData) throws IOException, SQLException, CipresException
	{
		authorizeUser();

		if (!fileName.startsWith(Folder.SEPARATOR))
			throw new CipresException("Target pathname must be absolute", Status.BAD_REQUEST, ErrorData.BAD_REQUEST);

		String action;

		if (formData == null || (action = formData.getFirst("action")) == null)
			throw new ValidationException("action", "missing");

		if (action.equalsIgnoreCase("move")) {
			String newPathName = formData.getFirst("fileName");

			if (newPathName == null)
				throw new ValidationException("fileName", "missing");

			if (!newPathName.startsWith(Folder.SEPARATOR))
				throw new ValidationException("fileName", "must be absolute");

			int offset = newPathName.lastIndexOf(Folder.SEPARATOR);
			String newFolderName = newPathName.substring(0, offset);
			String newFileName = newPathName.substring(offset + 1);
			Folder newFolder = Folder.findFolder(newFolderName);

			if (newFolder == null)
				throw new CipresException(newFolderName + " not found", Status.NOT_FOUND, ErrorData.NOT_FOUND);

			Folder targetFolder = Folder.findFolder(fileName);

			if (targetFolder != null) {
				targetFolder.setLabel(newFileName);
				targetFolder.setEnclosingFolder(newFolder);
				targetFolder.save();

				return createFolderInfo(targetFolder, newFolderName, false);
			}

			UserDataItem targetItem = UserDataItem.findDataItem(fileName);

			if (targetItem != null) {
				targetItem.setLabel(newFileName);
				targetItem.setEnclosingFolder(newFolder);
				targetItem.save();

				return createFileInfo(targetItem, newFolderName);
			}

			Task targetTask = Task.findTask(fileName);

			if (targetTask != null) {
				targetTask.setLabel(newFileName);
				targetTask.setEnclosingFolder(newFolder);
				targetTask.save();

				return createTaskInfo(targetTask, newFolderName);
			}

			throw new CipresException(fileName + " not found", Status.NOT_FOUND, ErrorData.NOT_FOUND);

		}
		else
			throw new ValidationException("action", "unrecognized value " + action);
	}

	@DELETE
	@Path("{fileName: .+}")
	public void deleteFile(@PathParam("fileName") String fileName) throws IOException, SQLException, CipresException
	{
		authorizeUser();

		if (!fileName.startsWith(Folder.SEPARATOR))
			throw new CipresException("Pathname must be absolute", Status.BAD_REQUEST, ErrorData.BAD_REQUEST);

		Folder targetFolder = Folder.findFolder(fileName);

		if (targetFolder != null) {
			targetFolder.delete();

			return;
		}

		UserDataItem targetItem = UserDataItem.findDataItem(fileName);

		if (targetItem != null) {
			targetItem.delete();

			return;
		}

		Task targetTask = Task.findTask(fileName);

		if (targetTask != null) {
			targetTask.delete();

			return;
		}

		throw new CipresException(fileName + " not found", Status.NOT_FOUND, ErrorData.NOT_FOUND);
	}

	@GET
	@Path("list/{fileName: .+}")
	@Produces(MediaType.APPLICATION_XML)
	public FolderItemInfo getListing(@PathParam("fileName") String fileName) throws IOException, SQLException, CipresException
	{
		authorizeUser();

		if (!fileName.startsWith(Folder.SEPARATOR))
			throw new CipresException("Pathname must be absolute", Status.BAD_REQUEST, ErrorData.BAD_REQUEST);

		int offset = fileName.lastIndexOf(Folder.SEPARATOR);
		String baseFolderName;

		if (offset > 0)
			baseFolderName = fileName.substring(0, offset);
		else
			baseFolderName = Folder.SEPARATOR;

		Folder targetFolder = Folder.findFolder(fileName);

		if (targetFolder != null)
			return createFolderInfo(targetFolder, baseFolderName, true);

		UserDataItem targetItem = UserDataItem.findDataItem(fileName);

		if (targetItem != null)
			return createFileInfo(targetItem, baseFolderName);

		Task targetTask = Task.findTask(fileName);

		if (targetTask != null)
			return createTaskInfo(targetTask, baseFolderName);

		throw new CipresException(fileName + " not found", Status.NOT_FOUND, ErrorData.NOT_FOUND);
	}

	@POST
	@Path("meta/{fileName: .+}")
	@Produces(MediaType.APPLICATION_XML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String setMetadata(@PathParam("fileName") String fileName, MultivaluedMap<String, String> formData) throws IOException, SQLException, CipresException
	{
		authorizeUser();

		if (!fileName.startsWith(Folder.SEPARATOR))
			throw new CipresException("Pathname must be absolute", Status.BAD_REQUEST, ErrorData.BAD_REQUEST);

		UserDataItem targetItem = UserDataItem.findDataItem(fileName);

		if (targetItem == null)
			throw new CipresException(fileName + " not found", Status.NOT_FOUND, ErrorData.NOT_FOUND);

		for (Map.Entry<String, List<String>> entry : formData.entrySet()) {
			Iterator<String> values = entry.getValue().iterator();
			StringBuilder newValue = new StringBuilder();

			newValue.append(values.next());

			while (values.hasNext()) {
				newValue.append(METADATA_SEPARATOR);
				newValue.append(values.next());
			}

			targetItem.metaData().put(entry.getKey(), newValue.toString());
			targetItem.save();
		}

		return createMetadataXml(targetItem);
	}

	@GET
	@Path("meta/{fileName: .+}")
	@Produces(MediaType.APPLICATION_XML)
	public String getMetadata(@PathParam("fileName") String fileName) throws IOException, SQLException, CipresException
	{
		authorizeUser();

		if (!fileName.startsWith(Folder.SEPARATOR))
			throw new CipresException("Pathname must be absolute", Status.BAD_REQUEST, ErrorData.BAD_REQUEST);

		UserDataItem targetItem = UserDataItem.findDataItem(fileName);

		if (targetItem == null)
			throw new CipresException(fileName + " not found", Status.NOT_FOUND, ErrorData.NOT_FOUND);

		return createMetadataXml(targetItem);
	}

	@DELETE
	@Path("meta/{fileName: .+}")
	public void deleteMetadata(@PathParam("fileName") String fileName) throws IOException, SQLException, CipresException
	{
		authorizeUser();

		if (!fileName.startsWith(Folder.SEPARATOR))
			throw new CipresException("Pathname must be absolute", Status.BAD_REQUEST, ErrorData.BAD_REQUEST);

		UserDataItem targetItem = UserDataItem.findDataItem(fileName);

		if (targetItem == null)
			throw new CipresException(fileName + " not found", Status.NOT_FOUND, ErrorData.NOT_FOUND);

		targetItem.metaData().clear();
		targetItem.save();
	}


	// private methods


	private void authorizeUser() throws IOException, SQLException, CipresException
	{
		m_securityContext.isUserInRole(UserRole.STANDARD.toString());

		User authUser = getAuthUser();
		User targetUser = getTargetUser();

		if (!authUser.getRole().equals(UserRole.ADMIN) && !authUser.equals(targetUser))
			throw new AuthorizationException("Access forbidden for " + authUser.getUsername(), "Cipres Authentication");
	}

	private User getTargetUser() throws IOException, SQLException, CipresException
	{
		User result = User.findUserByUsername(m_username);

		if (result == null)
			throw new CipresException("user " + m_username + " not found", Status.NOT_FOUND, ErrorData.NOT_FOUND);

		return result;
	}

	private User getAuthUser() throws IOException, SQLException, CipresException
	{
		try {
			long userId = Long.parseLong(m_securityContext.getUserPrincipal().getName());

			return new User(userId);
		}
		catch (NotExistException err) {
			throw new CipresException(err.getMessage(), Status.NOT_FOUND, ErrorData.NOT_FOUND);
		}
	}

	private FileInfo createFileInfo(UserDataItem item, String path) throws IOException, SQLException
	{
		FileInfo result = new FileInfo();

		setFolderItemFields(result, item, item.getUserDataId(), path);

		result.length = item.getDataLength();

		return result;
	}

	private TaskInfo createTaskInfo(Task task, String path) throws IOException, SQLException
	{
		TaskInfo result = new TaskInfo();

		setFolderItemFields(result, task, task.getTaskId(), path);

		result.handle = task.getJobHandle();

		return result;
	}

	private FolderInfo createFolderInfo(Folder folder, String path, boolean showContents) throws IOException, SQLException
	{
		FolderInfo result = new FolderInfo();

		setFolderItemFields(result, folder, folder.getFolderId(), path);

		if (showContents) {
			result.files = new ArrayList<FileInfo>();
			result.folders = new ArrayList<FolderInfo>();
			result.tasks = new ArrayList<TaskInfo>();

			String basePath = path;

			if (!path.endsWith(Folder.SEPARATOR))
				basePath += Folder.SEPARATOR;

			basePath += result.name;

			for (UserDataItem item : folder.findDataItems()) {
				FileInfo info = createFileInfo(item, basePath);

				result.files.add(info);
			}

			for (Folder subFolder : folder.findSubFolders()) {
				FolderInfo info = createFolderInfo(subFolder, basePath, false);

				result.folders.add(info);
			}

			for (Task task : folder.findTasks()) {
				TaskInfo info = createTaskInfo(task, basePath);

				result.tasks.add(info);
			}
		}

		return result;
	}

	private void setFolderItemFields(FolderItemInfo info, FolderItem item, Long itemId, String path) throws IOException, SQLException
	{
		info.id = itemId;
		info.path = path;
		info.name = item.getLabel();
		info.owner = item.getUser().getUsername();
		info.dateCreated = item.getCreationDate();
	}

	private String createMetadataXml(UserDataItem item) throws IOException, SQLException
	{
		StringBuilder result = new StringBuilder();

		result.append("<metadata>\n");

		for (Map.Entry<String, String> entry : item.metaData().entrySet()) {
			String key = entry.getKey();
			String[] values = entry.getValue().split(METADATA_SEPARATOR);

			for (int i = 0 ; i < values.length ; i += 1) {
				result.append("\t<");
				result.append(key);
				result.append(">");

				result.append(values[i]);

				result.append("</");
				result.append(key);
				result.append(">\n");
			}
		}

		result.append("</metadata>\n");

		return result.toString();
	}
}
