package org.ngbw.web.controllers;

import java.util.Calendar;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.ngbw.sdk.UserAuthenticationException;
import org.ngbw.sdk.WorkbenchSession;
import org.ngbw.sdk.database.Folder;

public class TestFolderController extends TestController
{
	/*================================================================
	 * Constants
	 *================================================================*/
	// folder creation test
	public static final String FOLDER_LABEL = "Test Folder " +
		Calendar.getInstance().getTimeInMillis();
	public static final String FOLDER_DESCRIPTION =
		"This folder exists only for the purposes of conducting tests.";
	
	// folder editing test
	public static final String EDITED_FOLDER_LABEL = "Edited Test Folder " +
		Calendar.getInstance().getTimeInMillis();
	public static final String EDITED_FOLDER_DESCRIPTION =
		"This folder description has been edited.";
	
	// subfolder creation test
	public static final String SUBFOLDER_LABEL = "Test Subfolder " +
		Calendar.getInstance().getTimeInMillis();
	public static final String SUBFOLDER_DESCRIPTION =
		"This is a subfolder of a test folder.";
	
	/*================================================================
	 * Properties
	 *================================================================*/
	private FolderController controller;
	
	/*================================================================
	 * Property accessor methods
	 *================================================================*/
	protected SessionController getController() {
		if (controller == null || controller.isAuthenticated() == false) {
			SessionController sessionController = super.getController();
			WorkbenchSession session = sessionController.getWorkbenchSession();
			if (session == null) {
				try {
					session = sessionController.login(USERNAME, PASSWORD);
				} catch (UserAuthenticationException error) {
					// confirm that no UserAuthenticationException
					// was generated during the test login
					Assert.fail("The test login should not have generated " +
						"a UserAuthenticationException.");
				}
				// confirm that the returned WorkbenchSession is not null
				Assert.assertNotNull("Logging in the test user (username \"" + USERNAME +
					"\", password \"" + PASSWORD + "\") should generate a non-null " +
					"WorkbenchSession.", session);
			}
			controller = new FolderController(session);
			Assert.assertNotNull("The FolderController constructor " +
				"should not return null.", controller);
			Assert.assertTrue("The controller should represent the workbench session " +
				"of a properly authenticated user.", controller.isAuthenticated());
		}
		return controller;
	}
	
	/*================================================================
	 * Fixture Methods
	 *================================================================*/
	/**
	 * Tears down the test fixture for testing NGBW folder functionality, to ensure that any
	 * created test folder is properly destroyed.
	 */
	@After public void tearDown() {
		Folder folder = findTestFolder(FOLDER_LABEL);
		if (folder != null) try {
			controller.deleteFolder(folder);
		} catch (UserAuthenticationException error) {
			// it doesn't really matter if a UserAuthenticationException was generated,
			// it should never happen anyway
		}
		folder = findTestFolder(EDITED_FOLDER_LABEL);
		if (folder != null) try {
			controller.deleteFolder(folder);
		} catch (UserAuthenticationException error) {
			// it doesn't really matter if a UserAuthenticationException was generated,
			// it should never happen anyway
		}
		super.tearDown();
	}
	
	/*================================================================
	 * Test Methods
	 *================================================================*/
	/**
	 * Tests the NGBW folder creation process.
	 * FolderController methods tested:
	 * 
	 * getNewFolder()
	 * isTransient(Folder folder)
	 * getDescription(Folder folder)
	 * setDescription(Folder folder, String description)
	 * getFolderCount()
	 * createFolder(String label, String description, Folder parentFolder)
	 * isPersisted(Folder folder)
	 * getAllFolders()
	 * setCurrentFolder(Folder folder)
	 * getCurrentFolder()
	 * isCurrentFolder(Folder folder)
	 * editFolder(Folder folder, String label, String description)
	 * getParentFolder(Folder folder)
	 * deleteFolder(Folder folder)
	 */
	@Test public void testCreateFolder() {
		// confirm that new folder instances are transient
		Folder transientFolder = controller.getNewFolder();
		Assert.assertTrue("The new folder instance should be transient.",
			controller.isTransient(transientFolder));
		
		// test retrieving and updating the new folder instance's description
		Assert.assertNull("The new folder instance should have a null description.",
			controller.getDescription(transientFolder));
		Assert.assertTrue("The new folder instance's description should be editable.",
			controller.setDescription(transientFolder, FOLDER_DESCRIPTION));
		Assert.assertEquals("The new folder instance's stored description should be " +
			"equal to the value that it was set to.", FOLDER_DESCRIPTION,
			controller.getDescription(transientFolder));
		Assert.assertTrue("The new folder instance should still be transient, " +
			"even after editing its description.", controller.isTransient(transientFolder));
		
		// store the test user's current number of folders
		int folderCount = controller.getFolderCount();
		
		// attempt to create a new top-level folder
		Folder folder = null;
		try {
			folder = controller.createFolder(FOLDER_LABEL, FOLDER_DESCRIPTION, null);
		} catch (UserAuthenticationException error) {
			// confirm that no UserAuthenticationException
			// was generated during the test folder creation
			Assert.fail("Creating a test folder should not have " +
				"generated a UserAuthenticationException.");
		}
		
		// confirm that a new folder was added
		Assert.assertTrue("The test user's folder count should have increased " +
			"by one after successfully creating a new folder.",
			controller.getFolderCount() == (folderCount + 1));
		
		// confirm that the folder was saved within the current session
		Assert.assertTrue("The folder returned from the folder creation method " +
			"should be a persistent folder.", controller.isPersisted(folder));
		Folder testFolder = findTestFolder(FOLDER_LABEL);
		Assert.assertNotNull("The retrieved test folder should not be null.", testFolder);
		Assert.assertTrue("The retrieved test folder should be a persistent folder.",
			controller.isPersisted(testFolder));
		Assert.assertEquals("The retrieved test folder should be equal to the " +
			"object returned from the folder creation method.", folder, testFolder);
		
		// restart the session to confirm that the folder was persisted across sessions
		restartSession();
		
		// confirm that the folder was persisted between sessions
		testFolder = findTestFolder(FOLDER_LABEL);
		Assert.assertNotNull("The persisted test folder should not be null.", testFolder);
		Assert.assertTrue("The persisted test folder should be a persistent folder.",
			controller.isPersisted(testFolder));
		Assert.assertEquals("The persisted test folder should be equal to the " +
			"object returned from the folder creation method.", folder, testFolder);
		
		// attempt to select the test folder
		Assert.assertTrue("Setting the currently selected folder should not return " +
			"a result of false, indicating error.", controller.setCurrentFolder(testFolder));
		
		// confirm that the folder selection was saved within the current session
		Assert.assertNotNull("The currently selected folder should not be null.",
			controller.getCurrentFolder());
		Assert.assertTrue("The persisted test folder should currently be selected.",
			controller.isCurrentFolder(testFolder));
		
		// restart the session to confirm that the folder selection
		// was persisted across sessions
		restartSession();
		
		// confirm that the folder selection was persisted between sessions
		testFolder = findTestFolder(FOLDER_LABEL);
		Assert.assertTrue("The persisted test folder should currently be selected.",
			controller.isCurrentFolder(testFolder));
		
		// attempt to edit the test folder
		try {
			folder = controller.editFolder(testFolder,
				EDITED_FOLDER_LABEL, EDITED_FOLDER_DESCRIPTION);
		} catch (UserAuthenticationException error) {
			// confirm that no UserAuthenticationException
			// was generated during the test folder editing
			Assert.fail("Editing a test folder should not have " +
				"generated a UserAuthenticationException.");
		}
		
		// confirm that no new folder was added
		Assert.assertTrue("The test user's folder count should not have changed " +
			"after successfully editing a new folder.",
			controller.getFolderCount() == (folderCount + 1));
		
		// confirm that the folder was edited within the current session
		Assert.assertTrue("The folder returned from the folder editing method " +
			"should be a persistent folder.", controller.isPersisted(folder));
		testFolder = findTestFolder(EDITED_FOLDER_LABEL);
		Assert.assertNotNull("The retrieved test folder should not be null.", testFolder);
		Assert.assertTrue("The retrieved test folder should be a persistent folder.",
			controller.isPersisted(testFolder));
		Assert.assertEquals("The retrieved test folder should be equal to the " +
			"object returned from the folder editing method.", folder, testFolder);
		
		// confirm that no folder with the previous properties is still present
		testFolder = findTestFolder(FOLDER_LABEL);
		Assert.assertNull("The retrieved test folder should be null " +
			"after its properties have been edited, because no folder " +
			"should be found with its previous properties.", testFolder);
		
		// restart the session to confirm that the folder was persisted across sessions
		restartSession();
		
		// confirm that the folder was persisted between sessions
		testFolder = findTestFolder(EDITED_FOLDER_LABEL);
		Assert.assertNotNull("The persisted test folder should not be null.", testFolder);
		Assert.assertTrue("The persisted test folder should be a persistent folder.",
			controller.isPersisted(testFolder));
		Assert.assertEquals("The persisted test folder should be equal to the " +
			"object returned from the folder editing method.", folder, testFolder);
		
		// attempt to create a subfolder
		try {
			folder = controller.createFolder(SUBFOLDER_LABEL, SUBFOLDER_DESCRIPTION,
				testFolder);
		} catch (UserAuthenticationException error) {
			// confirm that no UserAuthenticationException
			// was generated during the test subfolder creation
			Assert.fail("Creating a test subfolder should not have " +
				"generated a UserAuthenticationException.");
		}
		
		// confirm that a new folder was added
		Assert.assertTrue("The test user's folder count should have increased " +
			"by one after successfully creating a new subfolder.",
			controller.getFolderCount() == (folderCount + 2));
		
		// confirm that the subfolder was saved within the current session
		Assert.assertTrue("The subfolder returned from the folder creation method " +
			"should be a persistent folder.", controller.isPersisted(folder));
		Folder subfolder = findTestFolder(SUBFOLDER_LABEL);
		Assert.assertNotNull("The retrieved test subfolder should not be null.", subfolder);
		Assert.assertTrue("The retrieved test subfolder should be a persistent folder.",
			controller.isPersisted(testFolder));
		Assert.assertEquals("The retrieved test subfolder should be equal to the " +
			"object returned from the folder creation method.", folder, subfolder);
		
		// confirm that the subfolder has the proper parent folder
		Folder parentFolder = controller.getParentFolder(subfolder);
		Assert.assertNotNull("The retrieved parent folder should not be null.", parentFolder);
		Assert.assertEquals("The retrieved parent folder should be equal to the " +
			"previously created test folder.", testFolder, parentFolder);
		
		// restart the session to confirm that the subfolder was persisted across sessions
		restartSession();
		
		// confirm that the subfolder was persisted between sessions
		subfolder = findTestFolder(SUBFOLDER_LABEL);
		Assert.assertNotNull("The persisted test subfolder should not be null.", subfolder);
		Assert.assertTrue("The persisted test subfolder should be a persistent folder.",
			controller.isPersisted(testFolder));
		Assert.assertEquals("The persisted test subfolder should be equal to the " +
			"object returned from the folder creation method.", folder, subfolder);
		
		// attempt to delete the test folder
		try {
			Assert.assertTrue("Deleting the test folder should not return " +
				"a result of false, indicating error.", controller.deleteFolder(testFolder));
		} catch (UserAuthenticationException error) {
			// confirm that no UserAuthenticationException
			// was generated during folder deletion
			Assert.fail("Deleting the test folder should not have " +
				"generated a UserAuthenticationException.");
		}
		
		// confirm that a folder was removed
		Assert.assertTrue("The test user's folder count should have decreased " +
			"by one after successfully deleting a folder.",
			controller.getFolderCount() == folderCount);
		
		// confirm that the folder was deleted within the current session
		testFolder = findTestFolder(FOLDER_LABEL);
		Assert.assertNull("The retrieved folder should be null after " +
			"having been deleted.", testFolder);
		
		// confirm that folder selection was deleted as well
		Assert.assertNull("The currently selected folder should be null after " +
			"having been deleted.", controller.getCurrentFolder());
		
		// attempt to log the test user out and then back in
		restartSession();
		
		// confirm that the folder deletion was persisted between sessions
		testFolder = findTestFolder(FOLDER_LABEL);
		Assert.assertNull("The persisted folder should be null after " +
			"having been deleted.", testFolder);
	}
	
	/*================================================================
	 * Convenience Methods
	 *================================================================*/
	private Folder findTestFolder(String label) {
		List<Folder> folders = controller.getAllFolders();
		for (Folder folder : folders) {
			if (label.equals(folder.getLabel()))
				return folder;
		}
		return null;
	}
}
