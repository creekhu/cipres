package org.ngbw.web.controllers;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.ngbw.sdk.UserAuthenticationException;
import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.WorkbenchSession;
import org.ngbw.sdk.common.util.StringUtils;
import org.ngbw.sdk.common.util.ValidationResult;
import org.ngbw.sdk.core.shared.UserRole;
import org.ngbw.sdk.database.Folder;
import org.ngbw.sdk.database.User;

public class TestSessionController extends TestController
{
	/*================================================================
	 * Constants
	 *================================================================*/
	// login test
	public static final String USERNAME = "test1";
	public static final String PASSWORD = "test1";
	public static final String STANDARD_ROLE = "STANDARD";
	public static final String ADMIN_ROLE = "ADMIN";
	
	// registration test
	public static final String GUEST_ROLE = "GUEST";
	
	// profile test
	public static final String NEW_PASSWORD = "12345";
	
	// user preferences test
	public static final String PREFERENCE_KEY = "testPreference";
	public static final String PREFERENCE_VALUE = "true";
	
	/*================================================================
	 * Test Methods
	 *================================================================*/
	/**
	 * Tests the NGBW user registration process.
	 * SessionController methods tested:
	 * 
	 * registerGuestUser() -> registerUser(...)
	 * getWorkbenchSession()
	 * getWorkbench()
	 * isAuthenticated()
	 * getAuthenticatedUser()
	 * getAuthenticatedUsername()
	 * getAuthenticatedUserRole()
	 * isRegistered()
	 * logout()
	 */
	public void testRegistration() {
		// get controller
		SessionController controller = getController();
		
		// attempt to register a guest user
		ValidationResult result = controller.registerGuestUser();
		
		// confirm that the returned ValidationResult is not null, and is valid
		Assert.assertNotNull("Registering a guest user should generate a non-null " +
			"ValidationResult.", result);
		Assert.assertTrue("Registering a guest user should generate a valid " +
			"ValidationResult.", result.isValid());
		
		// confirm that the WorkbenchSession stored in the controller is not null
		WorkbenchSession session = controller.getWorkbenchSession();
		Assert.assertNotNull("The WorkbenchSession stored in the controller " +
			"should not be null after successfully registering a guest user.", session);
		
		// confirm that the Workbench stored in the controller is not null,
		// and is the same object as the one stored in the WorkbenchSession
		Workbench workbench = controller.getWorkbench();
		Assert.assertNotNull("The Workbench stored in the controller " +
			"should not be null after a successful login.", workbench);
		Assert.assertSame("The Workbench stored in the controller should " +
			"be the same as the object stored in the WorkbenchSession " +
			"after a successful login.",
			session.getWorkbench(), workbench);
		
		// confirm that the controller represents the workbench
		// session of a properly authenticated user
		Assert.assertTrue("The controller should represent the workbench session " +
			"of a properly authenticated user.", controller.isAuthenticated());
		
		// confirm that the User object stored in the controller is not null
		User user = controller.getAuthenticatedUser();
		Assert.assertNotNull("The User stored in the controller " +
			"should not be null after successfully registering a guest user.", user);
		
		// confirm that the username stored in the controller is not null
		String username = controller.getAuthenticatedUsername();
		Assert.assertNotNull("The username stored in the controller " +
			"should not be null after successfully registering a guest user.", username);
		
		// confirm that the UserRole object stored in the controller is not null,
		// and is appropriate for a guest user
		UserRole role = controller.getAuthenticatedUserRole();
		Assert.assertNotNull("The UserRole stored in the controller " +
			"should not be null after successfully registering a guest user.", role);
		Assert.assertEquals("The UserRole stored in the controller should " +
			"be appropriate for a guest user.", GUEST_ROLE, role.toString());
		
		// confirm that the authenticated guest user stored
		// in the controller is NOT a registered user
		Assert.assertFalse("The authenticated guest user whose workbench session is " +
			"represented by the controller should not be a registered user.",
			controller.isRegistered());
		
		// confirm that a default guest folder is present for the authenticated guest user
		List<Folder> folders = null;
		try {
			folders = session.findAllUserFolders();
		} catch (Throwable error) {
			Assert.fail(error.getMessage());
		}
		Assert.assertNotNull("The list of folders retrieved for the guest user " +
			"should not be null.", folders);
		Assert.assertTrue("The list of folders retrieved for the guest user " +
			"should have size exactly equal to 1.", folders.size() == 1);
		Folder folder = folders.get(0);
		
		// confirm that the default guest folder has the expected default properties
		Assert.assertEquals("The default guest folder should have the expected " +
			"default label.", SessionController.GUEST_FOLDER_LABEL, folder.getLabel());
		Map<String, String> preferences = null;
		try {
			preferences = folder.preferences();
		} catch (Throwable error) {
			Assert.fail(error.getMessage());
		}
		Assert.assertNotNull("The map of preferences retrieved for the default guest " +
			"folder should not be null.", preferences);
		Assert.assertTrue("The map of preferences retrieved for the default guest " +
			"folder should have size exactly equal to 1.", preferences.size() == 1);
		Assert.assertEquals("The default guest folder should have the expected " +
			"default description.", SessionController.GUEST_FOLDER_DESCRIPTION,
			preferences.get(SessionController.DESCRIPTION));
		
		// attempt to log out the guest user
		logout();
		
		// TODO: attempt to delete the guest user
	}
	
	/**
	 * Tests the NGBW user session life cycle.
	 * SessionController methods tested:
	 * 
	 * login(String username, String password)
	 * getWorkbenchSession()
	 * getWorkbench()
	 * isAuthenticated()
	 * getAuthenticatedUser()
	 * getAuthenticatedUsername()
	 * getAuthenticatedUserRole()
	 * isRegistered()
	 * logout()
	 */
	@Test public void testLogin() {
		// get controller
		SessionController controller = getController();
		
		// attempt to log in a non-present user
		WorkbenchSession session = null;
		try {
			session = controller.login(Long.toString(System.currentTimeMillis()), PASSWORD);
			Assert.fail("Attempting to log in as a nonexistent user " +
				"should have generated a UserAuthenticationException.");
		} catch (UserAuthenticationException error) {
			// confirm that a UserAuthenticationException was generated during
			// this login attempt, since the user should not exist in the database
		}
		
		// confirm that the returned WorkbenchSession is null
		Assert.assertNull("Attempting to log in as a nonexistent user " +
			"should generate a null WorkbenchSession.", session);
		
		// attempt to log in the test user with an incorrect password
		try {
			session = controller.login(USERNAME, Long.toString(System.currentTimeMillis()));
			Assert.fail("Attempting to log in with an incorrect password " +
				"should have generated a UserAuthenticationException.");
		} catch (UserAuthenticationException error) {
			// confirm that a UserAuthenticationException was generated during
			// this login attempt, since the provided password was incorrect
		}
		
		// confirm that the returned WorkbenchSession is null
		Assert.assertNull("Attempting to log in with an incorrect password " +
			"should generate a null WorkbenchSession.", session);
		
		// attempt to log in the test user with correct login parameters
		login();
		
		// confirm that the WorkbenchSession stored in the controller is not null
		session = controller.getWorkbenchSession();
		Assert.assertNotNull("The WorkbenchSession stored in the controller " +
			"should not be null after a successful login.", session);
		
		// confirm that the Workbench stored in the controller is not null,
		// and is the same object as the one stored in the WorkbenchSession
		Workbench workbench = controller.getWorkbench();
		Assert.assertNotNull("The Workbench stored in the controller " +
			"should not be null after a successful login.", workbench);
		Assert.assertSame("The Workbench stored in the controller should " +
			"be the same as the object stored in the WorkbenchSession " +
			"after a successful login.",
			session.getWorkbench(), workbench);
		
		// confirm that the controller represents the workbench
		// session of a properly authenticated user
		Assert.assertTrue("The controller should represent the workbench session " +
			"of a properly authenticated user.", controller.isAuthenticated());
		
		// confirm that the User object stored in the controller is not null,
		// and represents the same test user that was logged in
		User user = controller.getAuthenticatedUser();
		Assert.assertNotNull("The User stored in the controller " +
			"should not be null after a successful login.", user);
		Assert.assertEquals("The User stored in the controller should " +
			"have the same username as the test user that was logged in.",
			USERNAME, user.getUsername());
		Assert.assertEquals("The User stored in the controller should " +
			"have the same password as the test user that was logged in.",
			StringUtils.getMD5HexString(PASSWORD), user.getPassword());
		
		// confirm that the username stored in the controller is not null,
		// and is the same as that of the test user that was logged in
		String username = controller.getAuthenticatedUsername();
		Assert.assertNotNull("The username stored in the controller " +
			"should not be null after a successful login.", username);
		Assert.assertEquals("The username stored in the controller should " +
			"be the same as that of the test user that was logged in.",
			USERNAME, username);
		
		// confirm that the UserRole object stored in the controller is not null,
		// and is the same as that of the test user that was logged in
		UserRole role = controller.getAuthenticatedUserRole();
		Assert.assertNotNull("The UserRole stored in the controller " +
			"should not be null after a successful login.", role);
		Assert.assertEquals("The UserRole stored in the controller should " +
			"be the same as that of the test user that was logged in.",
			ADMIN_ROLE, role.toString());
		
		// confirm that the authenticated user stored
		// in the controller is a registered user
		Assert.assertTrue("The authenticated user whose workbench session is " +
			"represented by the controller should be a registered user.",
			controller.isRegistered());
		
		// attempt to log out the test user
		logout();
	}
	
	/**
	 * Tests the NGBW user profile update process.
	 * SessionController methods tested:
	 * 
	 * login(String username, String password)
	 * editPassword(String oldPassword, String newPassword)
	 * logout()
	 * getWorkbenchSession()
	 */
	@Test public void testProfile() {
		// get controller
		SessionController controller = getController();
		
		// attempt to log in the test user
		login();
		
		// attempt to update the test user's password
		try {
			Assert.assertTrue("Updating the user's password should not return " +
				"a result of false, indicating error.",
				controller.editPassword(PASSWORD, NEW_PASSWORD));
		} catch (UserAuthenticationException error) {
			// confirm that no UserAuthenticationException was
			// generated when updating the user's password
			Assert.fail("Updating the test user's password should " +
				"not have generated a UserAuthenticationException.");
		}
		
		// attempt to log out the test user
		logout();
		
		// attempt to log the test user back in with the old password
		try {
			controller.login(USERNAME, PASSWORD);
			Assert.fail("Attempting to log in with the old password " +
				"should have generated a UserAuthenticationException.");
		} catch (UserAuthenticationException error) {
			// confirm that a UserAuthenticationException was generated during
			// this login attempt, since the provided password was incorrect
		}
		
		// attempt to log the test user back in with the new password
		try {
			controller.login(USERNAME, NEW_PASSWORD);
		} catch (UserAuthenticationException error) {
			// confirm that no UserAuthenticationException was generated during the test login
			Assert.fail("The test login should not have generated a UserAuthenticationException.");
		}
		
		// attempt to restore the test user's old password
		try {
			Assert.assertTrue("Updating the user's password should not return " +
				"a result of false, indicating error.",
				controller.editPassword(NEW_PASSWORD, PASSWORD));
		} catch (UserAuthenticationException error) {
			// confirm that no UserAuthenticationException was
			// generated when updating the user's password
			Assert.fail("Updating the test user's password should " +
				"not have generated a UserAuthenticationException.");
		}
		
		// attempt to log out the test user
		logout();
		
		// attempt to log the test user back in with the previously updated password
		try {
			controller.login(USERNAME, NEW_PASSWORD);
			Assert.fail("Attempting to log in with the previously updated password " +
				"should have generated a UserAuthenticationException.");
		} catch (UserAuthenticationException error) {
			// confirm that a UserAuthenticationException was generated during
			// this login attempt, since the provided password was incorrect
		}
		
		// attempt to log the test user back in with the old password
		login();
		
		// TODO: test editUser(...) here
		
		// attempt to log out the test user
		logout();
	}
	
	/**
	 * Tests the NGBW user preference process.
	 * SessionController methods tested:
	 * 
	 * login(String username, String password)
	 * setUserPreference(String preference, String value)
	 * getUserPreference(String preference)
	 * logout()
	 * getWorkbenchSession()
	 * clearUserPreference(String preference)
	 */
	@Test public void testUserPreferences() {
		// get controller
		SessionController controller = getController();
		
		// attempt to log in the test user
		login();
		
		// attempt to save a user preference
		Assert.assertTrue("Writing a user preference should not return " +
			"a result of false, indicating error.",
			controller.setUserPreference(PREFERENCE_KEY, PREFERENCE_VALUE));
		
		// confirm that the user preference was saved within the current session
		String value = controller.getUserPreference(PREFERENCE_KEY);
		Assert.assertEquals("The retrieved preference should have the " +
			"same value as what was saved.", PREFERENCE_VALUE, value);
		
		// attempt to log the test user out and then back in
		restartSession();
		
		// confirm that the user preference was persisted between sessions
		value = controller.getUserPreference(PREFERENCE_KEY);
		Assert.assertEquals("The persisted preference should have the " +
			"same value as what was saved.", PREFERENCE_VALUE, value);
		
		// attempt to clear the user preference
		Assert.assertTrue("Clearing a user preference should not return " +
			"a result of false, indicating error.",
		controller.clearUserPreference(PREFERENCE_KEY));
		
		// confirm that the user preference was deleted within the current session
		value = controller.getUserPreference(PREFERENCE_KEY);
		Assert.assertNull("The retrieved preference should be null after " +
			"having been cleared.", value);
		
		// attempt to log the test user out and then back in
		restartSession();
		
		// confirm that the user preference deletion was persisted between sessions
		value = controller.getUserPreference(PREFERENCE_KEY);
		Assert.assertNull("The persisted preference should be null after " +
			"having been cleared.", value);
		
		// attempt to log out the test user
		logout();
	}
}
