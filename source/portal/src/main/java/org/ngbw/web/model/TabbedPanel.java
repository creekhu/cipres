package org.ngbw.web.model;

import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import org.ngbw.sdk.database.Folder;

public class TabbedPanel<T> implements Comparator<Tab<T>>
{
	/*================================================================
	 * Constants
	 *================================================================*/
	// Tab labels
	public static final String ALL = "All";
	public static final String UNKNOWN = "Unknown";
	public static final String MIXED = "Mixed";
	
	/*================================================================
	 * Properties
	 *================================================================*/
	private Page<Tab<T>> tabs;
	private Tab<T> currentTab;
	private Folder folder;
	
	/*================================================================
	 * Constructors
	 *================================================================*/
	public TabbedPanel(Folder folder) {
		this(null, folder);
	}
	
	public TabbedPanel(Page<Tab<T>> tabs, Folder folder) {
		setTabs(tabs);
		setFolder(folder);
	}
	
	/*================================================================
	 * Property accessor methods
	 *================================================================*/
	public Page<Tab<T>> getTabs() {
		return tabs;
	}

	public void setTabs(Page<Tab<T>> tabs) {
		this.tabs = tabs;
		refreshCurrentTab();
	}

	public Tab<T> getCurrentTab() {
		return currentTab;
	}

	public void setCurrentTab(Tab<T> currentTab) {
		this.currentTab = currentTab;
	}

	public void setCurrentTab(String currentTab) {
		setCurrentTab(getTab(currentTab));
	}
	
	public Folder getFolder() {
		return folder;
	}

	public void setFolder(Folder folder) {
		this.folder = folder;
	}
	
	public boolean isParentFolder(Folder folder) {
		Folder parentFolder = getFolder();
		if (folder == null || parentFolder == null)
			return false;
		else return folder.getFolderId() == parentFolder.getFolderId();
	}
	
	public List<T> getCurrentTabContents() {
		if (currentTab == null)
			return null;
		else {
			Page<T> contents = currentTab.getContents();
			if (contents == null)
				return null;
			else return contents.getThisPageElements();
		}
	}
	
	public int getCurrentTabSize() {
		if (currentTab == null)
			return 0;
		else {
			Page<T> contents = currentTab.getContents();
			if (contents == null)
				return 0;
			else return contents.getTotalNumberOfElements();
		}
	}
	
	public Tab<T> getTab(String label) {
		if (label == null || tabs == null)
			return null;
		else {
			List<Tab<T>> tabList = tabs.getThisPageElements();
			if (tabList == null)
				return null;
			else {
				for (Tab<T> tab : tabList) {
					if (label.equalsIgnoreCase(tab.getLabel()))
						return tab;
				}
				return null;
			}
		}
	}
	
	public List<String> getTabLabels() {
		if (tabs == null)
			return null;
		else {
			List<Tab<T>> tabList = tabs.getThisPageElements();
			if (tabList == null || tabList.size() < 1)
				return null;
			else {
				List<String> tabLabels = new Vector<String>(tabList.size());
				for (Tab<T> tab : tabList)
					tabLabels.add(tab.getLabel());
				return tabLabels;
			}
		}
	}
	
	public String getFirstTabLabel() {
		List<String> tabLabels = getTabLabels();
		if (tabLabels == null || tabLabels.size() < 1)
			return null;
		else return tabLabels.get(0);
	}
	
	public void sortTabs() {
		if (tabs == null)
			return;
		else {
			tabs.sortElements(this);
			setDefaultTab();
		}
	}
	
	/*================================================================
	 * Comparator methods
	 *================================================================*/
	public int compare(Tab<T> t1, Tab<T> t2) {
		//TODO: implement pluggable Comparator<Tab<T>>
		if (t1 == null || t2 == null)
			throw new NullPointerException("Compared tabs cannot be null.");
		else {
			String label1 = t1.getLabel();
			String label2 = t2.getLabel();
			if (label1 == null || label2 == null)
				throw new NullPointerException("Compared tab labels cannot be null.");
			else if (label1.equalsIgnoreCase(label2))
				return 0;
			// "All" always comes last
			else if (label1.trim().startsWith(ALL))
				return 1;
			else if (label2.trim().startsWith(ALL))
				return -1;
			// "Unknown" always comes next to last
			else if (label1.trim().startsWith(UNKNOWN))
				return 1;
			else if (label2.trim().startsWith(UNKNOWN))
				return -1;
			else return label1.compareToIgnoreCase(label2);
		}
	}
	
	/*================================================================
	 * Convenience methods
	 *================================================================*/
	protected void refreshCurrentTab() {
		if (tabs == null)
			clearCurrentTab();
		else if (currentTab == null || tabs.getThisPageElements() == null ||
			tabs.getThisPageElements().contains(currentTab) == false)
			setDefaultTab();
	}
	
	protected void clearCurrentTab() {
		this.currentTab = null;
	}
	
	protected void setDefaultTab() {
		if (tabs != null) {
			Tab<T> firstTab = tabs.getPageElement(tabs.getThisPageFirstElementNumber());
			if (firstTab != null)
				setCurrentTab(firstTab);
		}
	}
}
