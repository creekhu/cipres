/*
 * FolderItem.java
 */
package org.ngbw.sdk.database;


import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;


/**
 * The <code>FolderItem</code> interface specifies the common functionality
 * of all data in the user data area that can be contained 
 * within a folder (= enclosing folder).
 * 
 * @author Roland H. Niedner
 * @author Paul Hoover
 * 
 */
public interface FolderItem {

	/**
	 * Method returns the id of the group that owns this folder item.
	 * 
	 * @return group id
	 */
	long getGroupId();

	/**
	 * Method set the id of the group that owns this folder item.
	 * 
	 * @param groupId
	 */
	void setGroupId(Long groupId);

	/**
	 * Method returns an instance of the group that owns this folder item.
	 * 
	 * @return group
	 */
	Group getGroup() throws IOException, SQLException;

	/**
	 * Method set the group that owns this folder item.
	 * 
	 * @param group
	 */
	void setGroup(Group group);

	/**
	 * Method returns the id of the user that owns this folder item.
	 * 
	 * @return owner id
	 */
	long getUserId();

	/**
	 * Method set the id of the user that owns this folder item.
	 * 
	 * @param userId
	 */
	void setUserId(Long userId);

	/**
	 * Method returns an instance of the user that owns this folder item.
	 * 
	 * @return owner
	 */
	User getUser() throws IOException, SQLException;

	/**
	 * Method set the user that owns this folder item.
	 * 
	 * @param user
	 */
	void setUser(User user);

	/**
	 * Method returns the creation date of this folder item.
	 * 
	 * @return creationDate
	 */
	Date getCreationDate();

	/**
	 * Method returns the label (to be displayed by the GUI) of this folder item.
	 * 
	 * @return label
	 */
	String getLabel();

	/**
	 * Method sets the label (to be displayed by the GUI) of this folder item.
	 * 
	 * @param label
	 */
	void setLabel(String label);

	/**
	 * Method returns the enclosing folder id.
	 * 
	 * @return enclosingFolder id
	 */
	long getEnclosingFolderId();

	/**
	 * Method sets the enclosing folder id.
	 * 
	 * @param folderId
	 */
	void setEnclosingFolderId(Long folderId);

	/**
	 * Method returns an instance of the enclosing folder.
	 * 
	 * @return enclosingFolder instance
	 */
	Folder getEnclosingFolder() throws IOException, SQLException;

	/**
	 * Method sets the enclosing folder.
	 * 
	 * @param enclosingFolder enclosingFolder
	 */
	void setEnclosingFolder(Folder enclosingFolder);
}
