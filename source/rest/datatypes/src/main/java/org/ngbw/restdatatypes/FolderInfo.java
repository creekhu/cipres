/*
 * FolderInfo.java
 */
package org.ngbw.restdatatypes;


import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


/**
 *
 * @author Paul Hoover
 *
 */
@XmlRootElement(name = "folder")
@XmlAccessorType(XmlAccessType.FIELD)
public class FolderInfo extends FolderItemInfo {

	// data fields


	@XmlElementWrapper(name = "files")
	@XmlElement(name = "file")
	public Collection<FileInfo> files;

	@XmlElementWrapper(name = "folders")
	@XmlElement(name = "folder")
	public Collection<FolderInfo> folders;

	@XmlElementWrapper(name = "tasks")
	@XmlElement(name = "task")
	public Collection<TaskInfo> tasks;


	// constructors


	public FolderInfo()
	{

	}
}
