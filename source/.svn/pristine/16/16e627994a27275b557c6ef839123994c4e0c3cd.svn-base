package org.ngbw.web.controllers;

import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ngbw.sdk.UserAuthenticationException;
import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.WorkbenchSession;
import org.ngbw.sdk.core.types.DataFormat;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.EntityType;

public class TestDataFormatController
{
	/*================================================================
	 * Constants
	 *================================================================*/
	public static final String USERNAME = "test1";
	public static final String PASSWORD = "test1";
	
	/*================================================================
	 * Properties
	 *================================================================*/
	private DataFormatController controller;
	
	/**
	 * Sets up the test fixture for testing NGBW data format drop-down list functionality.
	 * All installations of the workbench should contain a test user with the static properties
	 * specified in this class, for the purposes of conducting automated tests such as this.
	 */
	@Before public void setUp() {
		Workbench workbench = Workbench.getInstance();
		if (workbench.hasActiveSession(USERNAME))
			workbench.suspendSession(USERNAME);
		WorkbenchSession session = null;
		try {
			session = workbench.getSession(USERNAME, PASSWORD);
		} catch (UserAuthenticationException error) {
			Assert.fail("User \"" + USERNAME + "\" could not be logged in: " + error.getMessage());
		} catch (Throwable error) {
			Assert.fail(error.getMessage());
		}
		Assert.assertNotNull("The workbench session retrieved when attempting to authenticate "+
			"user \"" + USERNAME + "\" is null.");
		controller = new DataFormatController(session);
		Assert.assertNotNull("The default DataFormatController constructor should not return null.",
			controller);
		Assert.assertEquals("By default, the EntityType stored in the controller should be " +
			EntityType.UNKNOWN, EntityType.UNKNOWN, controller.getEntityType());
		Assert.assertEquals("By default, the DataType stored in the controller should be " +
			DataType.UNKNOWN, DataType.UNKNOWN, controller.getDataType());
		Assert.assertEquals("By default, the DataFormat stored in the controller should be " +
			DataFormat.UNKNOWN, DataFormat.UNKNOWN, controller.getDataFormat());
		printSelections();
	}
	
	/**
	 * Tears down the test fixture for testing NGBW data format drop-down list functionality,
	 * to ensure that any remaining workbench session is properly suspended.
	 */
	@After public void tearDown() {
		if (controller != null) {
			WorkbenchSession session = controller.getWorkbenchSession();
			if (session != null) {
				Workbench workbench = session.getWorkbench();
				String username = null;
				try {
					username = session.getUsername();
				} catch (Throwable error) {}
				if (workbench != null && username != null)
					workbench.suspendSession(username);
			}
		}
	}
	
	/**
	 * Tests the NGBW data format drop-down list navigation process.
	 * DataFormatController methods tested:
	 * 
	 * getEntityTypes()
	 * getDataTypes()
	 * getDataFormats()
	 * getEntityType()
	 * getDataType()
	 * getDataFormat()
	 * setEntityType()
	 * setDataType()
	 * setDataFormat()
	 * validate()
	 */
	@Test public void testSelectDataFormat() {
		// get the set of registered EntityTypes
		Set<EntityType> entityTypes = controller.getEntityTypes();
		Assert.assertNotNull("The set of EntityTypes retrieved from the controller " +
			"should not be null.", entityTypes);
		Assert.assertTrue("The set of EntityTypes retrieved from the controller " +
			"should contain at least one valid EntityType.", entityTypes.size() > 0);
		
		// select the PROTEIN EntityType
		Assert.assertTrue("The set of EntityTypes retrieved from the controller " +
			"should contain the EntityType " + EntityType.PROTEIN,
			entityTypes.contains(EntityType.PROTEIN));
		controller.setEntityType(EntityType.PROTEIN);
		Assert.assertTrue("After selecting an EntityType, the resulting drop-down " +
			"selections should all be valid.", controller.validate());
		Assert.assertEquals("The EntityType stored in the controller should now be " +
			EntityType.PROTEIN, EntityType.PROTEIN, controller.getEntityType());
		Assert.assertEquals("The DataType stored in the controller should still be " +
			DataType.UNKNOWN, DataType.UNKNOWN, controller.getDataType());
		Assert.assertEquals("The DataFormat stored in the controller should still be " +
			DataFormat.UNKNOWN, DataFormat.UNKNOWN, controller.getDataFormat());
		printSelections();
		
		// get the set of registered DataTypes associated with the selected EntityType
		Set<DataType> dataTypes = controller.getDataTypes();
		Assert.assertNotNull("The set of DataTypes retrieved from the controller " +
			"should not be null.", dataTypes);
		Assert.assertTrue("The set of DataTypes retrieved from the controller " +
			"should contain at least one valid DataType.", dataTypes.size() > 0);
		
		// select the SEQUENCE DataType
		Assert.assertTrue("The set of DataTypes retrieved from the controller " +
			"should contain the DataType " + DataType.SEQUENCE,
			dataTypes.contains(DataType.SEQUENCE));
		controller.setDataType(DataType.SEQUENCE);
		Assert.assertTrue("After selecting a DataType, the resulting drop-down " +
			"selections should all be valid.", controller.validate());
		Assert.assertEquals("The EntityType stored in the controller should still be " +
			EntityType.PROTEIN, EntityType.PROTEIN, controller.getEntityType());
		Assert.assertEquals("The DataType stored in the controller should now be " +
			DataType.SEQUENCE, DataType.SEQUENCE, controller.getDataType());
		Assert.assertEquals("The DataFormat stored in the controller should still be " +
			DataFormat.UNKNOWN, DataFormat.UNKNOWN, controller.getDataFormat());
		printSelections();
		
		// get the set of registered DataFormats associated with the selected
		// EntityType and DataType
		Set<DataFormat> dataFormats = controller.getDataFormats();
		Assert.assertNotNull("The set of DataFormats retrieved from the controller " +
			"should not be null.", dataFormats);
		Assert.assertTrue("The set of DataFormats retrieved from the controller " +
			"should contain at least one valid DataFormat.", dataFormats.size() > 0);
		
		// select the FASTA DataFormat
		Assert.assertTrue("The set of DataFormats retrieved from the controller " +
			"should contain the DataFormat " + DataFormat.FASTA,
			dataFormats.contains(DataFormat.FASTA));
		controller.setDataFormat(DataFormat.FASTA);
		Assert.assertTrue("After selecting a DataFormat, the resulting drop-down " +
			"selections should all be valid.", controller.validate());
		Assert.assertEquals("The EntityType stored in the controller should still be " +
			EntityType.PROTEIN, EntityType.PROTEIN, controller.getEntityType());
		Assert.assertEquals("The DataType stored in the controller should still be " +
			DataType.SEQUENCE, DataType.SEQUENCE, controller.getDataType());
		Assert.assertEquals("The DataFormat stored in the controller should now be " +
			DataFormat.FASTA, DataFormat.FASTA, controller.getDataFormat());
		printSelections();
		
		// repopulate the set of registered DataTypes associated with the selected EntityType
		dataTypes = controller.getDataTypes();
		Assert.assertNotNull("The set of DataTypes retrieved from the controller " +
			"should not be null.", dataTypes);
		Assert.assertTrue("The set of DataTypes retrieved from the controller " +
			"should contain at least one valid DataType.", dataTypes.size() > 0);
		
		// change the DataType selection to the STRUCTURE DataType,
		// which should invalidate the FASTA DataFormat selection
		Assert.assertTrue("The set of DataTypes retrieved from the controller " +
			"should contain the DataType " + DataType.STRUCTURE,
			dataTypes.contains(DataType.STRUCTURE));
		controller.setDataType(DataType.STRUCTURE);
		Assert.assertEquals("The DataFormat stored in the controller should have been " +
			"invalidated, changing its value to " + DataFormat.UNKNOWN, DataFormat.UNKNOWN,
			controller.getDataFormat());
		Assert.assertTrue("After selecting a new DataType, the resulting drop-down " +
			"selections should all be valid.", controller.validate());
		Assert.assertEquals("The EntityType stored in the controller should still be " +
			EntityType.PROTEIN, EntityType.PROTEIN, controller.getEntityType());
		Assert.assertEquals("The DataType stored in the controller should now be " +
			DataType.STRUCTURE, DataType.STRUCTURE, controller.getDataType());
		Assert.assertEquals("The DataFormat stored in the controller should still be " +
			DataFormat.UNKNOWN, DataFormat.UNKNOWN, controller.getDataFormat());
		printSelections();
		
		// repopulate the set of registered DataFormats associated with the selected
		// EntityType and DataType
		dataFormats = controller.getDataFormats();
		Assert.assertNotNull("The set of DataFormats retrieved from the controller " +
			"should not be null.", dataFormats);
		Assert.assertTrue("The set of DataFormats retrieved from the controller " +
			"should contain at least one valid DataFormat.", dataFormats.size() > 0);
		
		// select the PDB DataFormat
		Assert.assertTrue("The set of DataFormats retrieved from the controller " +
			"should contain the DataFormat " + DataFormat.PDB,
			dataFormats.contains(DataFormat.PDB));
		controller.setDataFormat(DataFormat.PDB);
		Assert.assertTrue("After selecting a new DataFormat, the resulting drop-down " +
			"selections should all be valid.", controller.validate());
		Assert.assertEquals("The EntityType stored in the controller should still be " +
			EntityType.PROTEIN, EntityType.PROTEIN, controller.getEntityType());
		Assert.assertEquals("The DataType stored in the controller should still be " +
			DataType.STRUCTURE, DataType.STRUCTURE, controller.getDataType());
		Assert.assertEquals("The DataFormat stored in the controller should now be " +
			DataFormat.PDB, DataFormat.PDB, controller.getDataFormat());
		printSelections();
		
		// repopulate the set of registered EntityTypes
		entityTypes = controller.getEntityTypes();
		Assert.assertNotNull("The set of EntityTypes retrieved from the controller " +
			"should not be null.", entityTypes);
		Assert.assertTrue("The set of EntityTypes retrieved from the controller " +
			"should contain at least one valid EntityType.", entityTypes.size() > 0);
		
		// change the EntityType selection to the DISCRETE_CHARACTER EntityType,
		// which should invalidate both the STRUCTURE DataType selection,
		// and the PDB DataFormat selection
		Assert.assertTrue("The set of EntityTypes retrieved from the controller " +
			"should contain the EntityType " + EntityType.DISCRETE_CHARACTER,
			entityTypes.contains(EntityType.DISCRETE_CHARACTER));
		controller.setEntityType(EntityType.DISCRETE_CHARACTER);
		Assert.assertEquals("The DataType stored in the controller should have been " +
			"invalidated, changing its value to " + DataType.UNKNOWN, DataType.UNKNOWN,
			controller.getDataType());
		Assert.assertEquals("The DataFormat stored in the controller should have been " +
			"invalidated, changing its value to " + DataFormat.UNKNOWN, DataFormat.UNKNOWN,
			controller.getDataFormat());
		Assert.assertTrue("After selecting a new EntityType, the resulting drop-down " +
			"selections should all be valid.", controller.validate());
		Assert.assertEquals("The EntityType stored in the controller should now be " +
			EntityType.DISCRETE_CHARACTER, EntityType.DISCRETE_CHARACTER, controller.getEntityType());
		Assert.assertEquals("The DataType stored in the controller should now be " +
			DataType.UNKNOWN, DataType.UNKNOWN, controller.getDataType());
		Assert.assertEquals("The DataFormat stored in the controller should now be " +
			DataFormat.UNKNOWN, DataFormat.UNKNOWN, controller.getDataFormat());
		printSelections();
		
		// repopulate the set of registered DataTypes associated with the selected EntityType
		dataTypes = controller.getDataTypes();
		Assert.assertNotNull("The set of DataTypes retrieved from the controller " +
			"should not be null.", dataTypes);
		Assert.assertTrue("The set of DataTypes retrieved from the controller " +
			"should contain at least one valid DataType.", dataTypes.size() > 0);
		
		// select the MATRIX DataType
		Assert.assertTrue("The set of DataTypes retrieved from the controller " +
			"should contain the DataType " + DataType.MATRIX,
			dataTypes.contains(DataType.MATRIX));
		controller.setDataType(DataType.MATRIX);
		Assert.assertTrue("After selecting a new DataType, the resulting drop-down " +
			"selections should all be valid.", controller.validate());
		Assert.assertEquals("The EntityType stored in the controller should still be " +
			EntityType.DISCRETE_CHARACTER, EntityType.DISCRETE_CHARACTER, controller.getEntityType());
		Assert.assertEquals("The DataType stored in the controller should now be " +
			DataType.MATRIX, DataType.MATRIX, controller.getDataType());
		Assert.assertEquals("The DataFormat stored in the controller should still be " +
			DataFormat.UNKNOWN, DataFormat.UNKNOWN, controller.getDataFormat());
		printSelections();
		
		// repopulate the set of registered DataFormats associated with the selected
		// EntityType and DataType
		dataFormats = controller.getDataFormats();
		Assert.assertNotNull("The set of DataFormats retrieved from the controller " +
			"should not be null.", dataFormats);
		Assert.assertTrue("The set of DataFormats retrieved from the controller " +
			"should contain at least one valid DataFormat.", dataFormats.size() > 0);
		
		// select the PHYLIP DataFormat
		Assert.assertTrue("The set of DataFormats retrieved from the controller " +
			"should contain the DataFormat " + DataFormat.PHYLIP,
			dataFormats.contains(DataFormat.PHYLIP));
		controller.setDataFormat(DataFormat.PHYLIP);
		Assert.assertTrue("After selecting a new DataFormat, the resulting drop-down " +
			"selections should all be valid.", controller.validate());
		Assert.assertEquals("The EntityType stored in the controller should still be " +
			EntityType.DISCRETE_CHARACTER, EntityType.DISCRETE_CHARACTER, controller.getEntityType());
		Assert.assertEquals("The DataType stored in the controller should still be " +
			DataType.MATRIX, DataType.MATRIX, controller.getDataType());
		Assert.assertEquals("The DataFormat stored in the controller should now be " +
			DataFormat.PHYLIP, DataFormat.PHYLIP, controller.getDataFormat());
		printSelections();
	}
	
	public void printSelections() {
		System.out.println("\nCurrent selections: EntityType " + controller.getEntityType() +
			", DataType " + controller.getDataType() +
			", DataFormat " + controller.getDataFormat());
		System.out.println("Current drop-down list sizes: " +
			"EntityType " + controller.getEntityTypes().size() +
			", DataType " + controller.getDataTypes().size() +
			", DataFormat " + controller.getDataFormats().size());
	}
}
