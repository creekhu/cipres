package org.ngbw.web.actions.ajax;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import org.ngbw.sdk.database.Folder;
import org.ngbw.web.actions.FolderManager;

@SuppressWarnings("serial")
public class ToggleFolder extends FolderManager
{
	/*================================================================
	 * Properties
	 *================================================================*/
	private Folder folder;
	
	/*================================================================
	 * Action methods
	 *================================================================*/
	public String execute() {
		Folder folder = getRequestFolder(ID);
		if (folder != null) {
			setFolder(folder);
			toggleExpanded(folder);
		}
		return SUCCESS;
	}
	
	/*================================================================
	 * Property accessor methods
	 *================================================================*/
	public void setFolder(Folder folder) {
		this.folder = folder;
	}
	
	/*================================================================
	 * Struts result accessor methods
	 *================================================================*/
	public InputStream getFolder() {
		String xml = "<?xml version=\"1.0\" ?>\n";
		if (folder != null) {
			xml += "<folders>\n";
			xml += "  <folder id=\"" + folder.getFolderId() + "\"";
			List<Folder> subfolders = getSubfolders(folder);
			if (isExpanded(folder)) {
				xml += " expanded=\"true\"/>\n";
				if (subfolders != null && subfolders.size() > 0)
					for (Folder subfolder : getSubfolders(folder)) {
						if (isExpanded(subfolder))
							xml += "  <folder id=\"" + subfolder.getFolderId() +
								"\" expanded=\"true\"/>\n";
					}
			} else {
				xml += "/>\n";
				if (subfolders != null && subfolders.size() > 0)
					for (Folder subfolder : getSubfolders(folder)) {
						xml += "  <folder id=\"" + subfolder.getFolderId() + "\"/>\n";
					}
			}
			xml += "</folders>";
		}
		return new ByteArrayInputStream(xml.getBytes());
	}
}
