package org.ngbw.web.controllers;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.ngbw.sdk.UserAuthenticationException;
import org.ngbw.sdk.WorkbenchSession;

public abstract class TestController
{
	/*================================================================
	 * Constants
	 *================================================================*/
	// test user login parameters
	public static final String USERNAME = "test1";
	public static final String PASSWORD = "test1";
	
	/*================================================================
	 * Properties
	 *================================================================*/
	private SessionController controller;
	
	/*================================================================
	 * Property accessor methods
	 *================================================================*/
	protected SessionController getController() {
		if (controller == null) {
			controller = new SessionController();
			Assert.assertNotNull("The default SessionController constructor " +
				"should not return null.", controller);
			Assert.assertNotNull("The newly instantiated SessionController " +
				"should be able to generate a non-null Workbench with no " +
				"initialization.", controller.getWorkbench());
		}
		return controller;
	}
	
	/*================================================================
	 * Fixture Methods
	 *================================================================*/
	/**
	 * Sets up the test fixture for testing NGBW session functionality.  All installations
	 * of the workbench should contain a test user with the static properties specified
	 * in this class, for the purposes of conducting automated tests such as this.
	 */
	@Before public void setUp() {
		getController();
	}
	
	/**
	 * Tears down the test fixture for testing NGBW session functionality, to ensure that any
	 * remaining workbench session is properly suspended.
	 */
	@After public void tearDown() {
		logout();
	}
	
	/*================================================================
	 * Convenience Methods
	 *================================================================*/
	protected void login() {
		// get controller
		SessionController controller = getController();
		
		// attempt to log in the test user
		WorkbenchSession session = null;
		try {
			session = controller.login(USERNAME, PASSWORD);
		} catch (UserAuthenticationException error) {
			// confirm that no UserAuthenticationException was generated during the test login
			Assert.fail("The test login should not have generated a UserAuthenticationException.");
		}
		
		// confirm that the returned WorkbenchSession is not null
		Assert.assertNotNull("Logging in the test user (username \"" + USERNAME +
			"\", password \"" + PASSWORD + "\") should generate a non-null " +
			"WorkbenchSession.", session);
		
		// confirm that the controller represents the workbench
		// session of a properly authenticated user
		Assert.assertTrue("The controller should represent the workbench session " +
			"of a properly authenticated user.", controller.isAuthenticated());
	}

	protected void logout() {
		// get controller
		SessionController controller = getController();
		
		// attempt to log out the test user
		if (controller.isAuthenticated()) {
			Assert.assertTrue("Logging out the test user should not return " +
				"a result of false, indicating error.", controller.logout());
		}
		
		// confirm that the test user is logged out
		Assert.assertFalse("No authenticated user should be present in the controller " +
			"after a successful logout.", controller.isAuthenticated());
	}
	
	protected void restartSession() {
		logout();
		login();
	}
}
