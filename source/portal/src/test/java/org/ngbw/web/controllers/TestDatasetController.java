package org.ngbw.web.controllers;

import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.ngbw.sdk.UserAuthenticationException;
import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.WorkbenchSession;
import org.ngbw.sdk.core.types.DataType;
import org.ngbw.sdk.core.types.Dataset;
import org.ngbw.sdk.core.types.EntityType;

public class TestDatasetController
{
	/*================================================================
	 * Constants
	 *================================================================*/
	// test user login parameters
	public static final String USERNAME = "test1";
	public static final String PASSWORD = "test1";
	// test Datasets
	public static final Dataset SWISSPROT = Dataset.valueOf("SWISSPROT");
	public static final Dataset TREMBL = Dataset.valueOf("TREMBL");
	public static final Dataset PDB = Dataset.valueOf("PDB");
	public static final Dataset GBMAM = Dataset.valueOf("GBMAM");
	public static final Dataset GBROD = Dataset.valueOf("GBROD");
	
	/*================================================================
	 * Properties
	 *================================================================*/
	private DatasetController controller;
	
	/**
	 * Sets up the test fixture for testing NGBW dataset drop-down list functionality.
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
			return;
		} catch (Throwable error) {
			Assert.fail(error.getMessage());
		}
		Assert.assertNotNull("The workbench session retrieved when attempting to authenticate "+
			"user \"" + USERNAME + "\" is null.");
		controller = new DatasetController(session);
		Assert.assertNotNull("The default DatasetController constructor should not return null.",
			controller);
		Assert.assertEquals("By default, the EntityType stored in the controller should be " +
			EntityType.UNKNOWN, EntityType.UNKNOWN, controller.getEntityType());
		Assert.assertEquals("By default, the DataType stored in the controller should be " +
			DataType.UNKNOWN, DataType.UNKNOWN, controller.getDataType());
		Assert.assertEquals("By default, the Datasets array stored in the controller should be null.",
			null, controller.getSelectedDatasets());
		printSelections();
	}
	
	/**
	 * Tears down the test fixture for testing NGBW dataset drop-down list functionality,
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
	 * DatasetController methods tested:
	 * 
	 * getEntityTypes()
	 * getDataTypes()
	 * getDatasets()
	 * getEntityType()
	 * getDataType()
	 * getSelectedDatasets()
	 * setEntityType()
	 * setDataType()
	 * setSelectedDatasets()
	 * validate()
	 */
	@Test public void testSelectDatasets() {
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
		Assert.assertEquals("The Datasets array stored in the controller should still be null.",
			null, controller.getSelectedDatasets());
		printSelections();
		
		// get the set of registered searchable DataTypes associated with the selected EntityType
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
		Assert.assertEquals("The Datasets array stored in the controller should still be null.",
				null, controller.getSelectedDatasets());
		printSelections();
		
		// get the set of registered Datasets associated with the selected
		// EntityType and DataType
		Set<Dataset> datasets = controller.getDatasets();
		Assert.assertNotNull("The set of Datasets retrieved from the controller " +
			"should not be null.", datasets);
		Assert.assertTrue("The set of Datasets retrieved from the controller " +
			"should contain at least one valid Dataset.", datasets.size() > 0);
		
		// select the SWISSPROT Dataset
		Assert.assertTrue("The set of Datasets retrieved from the controller " +
			"should contain the Dataset " + SWISSPROT, datasets.contains(SWISSPROT));
		controller.setSelectedDatasets(new Dataset[]{SWISSPROT});
		Assert.assertTrue("After selecting a Dataset, the resulting drop-down " +
			"selections should all be valid.", controller.validate());
		Assert.assertEquals("The EntityType stored in the controller should still be " +
			EntityType.PROTEIN, EntityType.PROTEIN, controller.getEntityType());
		Assert.assertEquals("The DataType stored in the controller should still be " +
			DataType.SEQUENCE, DataType.SEQUENCE, controller.getDataType());
		Assert.assertEquals("The Datasets array stored in the controller should now " +
			"contain only one element.", 1, controller.getSelectedDatasets().length);
		Assert.assertEquals("The Datasets array stored in the controller should now contain only " +
			SWISSPROT, SWISSPROT, controller.getSelectedDatasets()[0]);
		printSelections();
		
		// select both the SWISSPROT and the TREMBL Dataset
		Assert.assertTrue("The set of Datasets retrieved from the controller " +
			"should contain the Dataset " + TREMBL, datasets.contains(TREMBL));
		controller.setSelectedDatasets(new Dataset[]{SWISSPROT, TREMBL});
		Assert.assertTrue("After selecting Datasets, the resulting drop-down " +
			"selections should all be valid.", controller.validate());
		Assert.assertEquals("The EntityType stored in the controller should still be " +
			EntityType.PROTEIN, EntityType.PROTEIN, controller.getEntityType());
		Assert.assertEquals("The DataType stored in the controller should still be " +
			DataType.SEQUENCE, DataType.SEQUENCE, controller.getDataType());
		Assert.assertEquals("The Datasets array stored in the controller should now " +
			"contain two elements.", 2, controller.getSelectedDatasets().length);
		Assert.assertEquals("The Datasets array stored in the controller should now contain " +
			SWISSPROT + " in its first index.", SWISSPROT, controller.getSelectedDatasets()[0]);
		Assert.assertEquals("The Datasets array stored in the controller should now contain " +
			TREMBL + " in its second index.", TREMBL, controller.getSelectedDatasets()[1]);
		printSelections();
		
		// repopulate the set of registered DataTypes associated with the selected EntityType
		dataTypes = controller.getDataTypes();
		Assert.assertNotNull("The set of DataTypes retrieved from the controller " +
			"should not be null.", dataTypes);
		Assert.assertTrue("The set of DataTypes retrieved from the controller " +
			"should contain at least one valid DataType.", dataTypes.size() > 0);
		
		// change the DataType selection to the STRUCTURE DataType,
		// which should invalidate the previous Dataset selections
		Assert.assertTrue("The set of DataTypes retrieved from the controller " +
			"should contain the DataType " + DataType.STRUCTURE,
			dataTypes.contains(DataType.STRUCTURE));
		controller.setDataType(DataType.STRUCTURE);
		Assert.assertEquals("The Datasets array stored in the controller should have been " +
			"invalidated, changing its value to null.", null, controller.getSelectedDatasets());
		Assert.assertTrue("After selecting a new DataType, the resulting drop-down " +
			"selections should all be valid.", controller.validate());
		Assert.assertEquals("The EntityType stored in the controller should still be " +
			EntityType.PROTEIN, EntityType.PROTEIN, controller.getEntityType());
		Assert.assertEquals("The DataType stored in the controller should now be " +
			DataType.STRUCTURE, DataType.STRUCTURE, controller.getDataType());
		Assert.assertEquals("The Datasets array stored in the controller should now be null.",
			null, controller.getSelectedDatasets());
		printSelections();
		
		// repopulate the set of registered searchable Datasets associated with the selected
		// EntityType and DataType
		datasets = controller.getDatasets();
		Assert.assertNotNull("The set of Datasets retrieved from the controller " +
			"should not be null.", datasets);
		Assert.assertTrue("The set of Datasets retrieved from the controller " +
			"should contain at least one valid Dataset.", datasets.size() > 0);
		
		// select the PDB Dataset
		Assert.assertTrue("The set of Datasets retrieved from the controller " +
			"should contain the Dataset " + PDB, datasets.contains(PDB));
		controller.setSelectedDatasets(new Dataset[]{PDB});
		Assert.assertTrue("After selecting a new Dataset, the resulting drop-down " +
			"selections should all be valid.", controller.validate());
		Assert.assertEquals("The EntityType stored in the controller should still be " +
			EntityType.PROTEIN, EntityType.PROTEIN, controller.getEntityType());
		Assert.assertEquals("The DataType stored in the controller should still be " +
			DataType.STRUCTURE, DataType.STRUCTURE, controller.getDataType());
		Assert.assertEquals("The Datasets array stored in the controller should now " +
			"contain only one element.", 1, controller.getSelectedDatasets().length);
		Assert.assertEquals("The Datasets array stored in the controller should now contain only " +
			PDB, PDB, controller.getSelectedDatasets()[0]);
		printSelections();
		
		// repopulate the set of registered EntityTypes
		entityTypes = controller.getEntityTypes();
		Assert.assertNotNull("The set of EntityTypes retrieved from the controller " +
			"should not be null.", entityTypes);
		Assert.assertTrue("The set of EntityTypes retrieved from the controller " +
			"should contain at least one valid EntityType.", entityTypes.size() > 0);
		
		// change the EntityType selection to the NUCLEIC_ACID EntityType,
		// which should NOT invalidate the STRUCTURE DataType selection,
		// but should invalidate the PDB DataFormat selection
		Assert.assertTrue("The set of EntityTypes retrieved from the controller " +
			"should contain the EntityType " + EntityType.NUCLEIC_ACID,
			entityTypes.contains(EntityType.NUCLEIC_ACID));
		controller.setEntityType(EntityType.NUCLEIC_ACID);
		Assert.assertEquals("The Datasets array stored in the controller should have been " +
			"invalidated, changing its value to null.", null, controller.getSelectedDatasets());
		Assert.assertTrue("After selecting a new EntityType, the resulting drop-down " +
			"selections should all be valid.", controller.validate());
		Assert.assertEquals("The EntityType stored in the controller should now be " +
			EntityType.NUCLEIC_ACID, EntityType.NUCLEIC_ACID, controller.getEntityType());
		Assert.assertEquals("The DataType stored in the controller should still be " +
			DataType.STRUCTURE, DataType.STRUCTURE, controller.getDataType());
		Assert.assertEquals("The Datasets array stored in the controller should now be null.",
			null, controller.getSelectedDatasets());
		printSelections();
		
		// repopulate the set of registered DataTypes associated with the selected EntityType
		dataTypes = controller.getDataTypes();
		Assert.assertNotNull("The set of DataTypes retrieved from the controller " +
			"should not be null.", dataTypes);
		Assert.assertTrue("The set of DataTypes retrieved from the controller " +
			"should contain at least one valid DataType.", dataTypes.size() > 0);
		
		// select the SEQUENCE DataType
		Assert.assertTrue("The set of DataTypes retrieved from the controller " +
			"should contain the DataType " + DataType.SEQUENCE,
			dataTypes.contains(DataType.SEQUENCE));
		controller.setDataType(DataType.SEQUENCE);
		Assert.assertTrue("After selecting a new DataType, the resulting drop-down " +
			"selections should all be valid.", controller.validate());
		Assert.assertEquals("The EntityType stored in the controller should still be " +
			EntityType.NUCLEIC_ACID, EntityType.NUCLEIC_ACID, controller.getEntityType());
		Assert.assertEquals("The DataType stored in the controller should now be " +
			DataType.SEQUENCE, DataType.SEQUENCE, controller.getDataType());
		Assert.assertEquals("The Datasets array stored in the controller should still be null.",
			null, controller.getSelectedDatasets());
		printSelections();
		
		// repopulate the set of registered Datasets associated with the selected
		// EntityType and DataType
		datasets = controller.getDatasets();
		Assert.assertNotNull("The set of Datasets retrieved from the controller " +
			"should not be null.", datasets);
		Assert.assertTrue("The set of Datasets retrieved from the controller " +
			"should contain at least one valid Dataset.", datasets.size() > 0);
		
		// select the GBMAM and GBROD Datasets
		Assert.assertTrue("The set of Datasets retrieved from the controller " +
			"should contain the Dataset " + GBMAM, datasets.contains(GBMAM));
		Assert.assertTrue("The set of Datasets retrieved from the controller " +
			"should contain the Dataset " + GBROD, datasets.contains(GBROD));
		controller.setSelectedDatasets(new Dataset[]{GBMAM, GBROD});
		Assert.assertTrue("After selecting new Datasets, the resulting drop-down " +
			"selections should all be valid.", controller.validate());
		Assert.assertEquals("The EntityType stored in the controller should still be " +
			EntityType.NUCLEIC_ACID, EntityType.NUCLEIC_ACID, controller.getEntityType());
		Assert.assertEquals("The DataType stored in the controller should still be " +
			DataType.SEQUENCE, DataType.SEQUENCE, controller.getDataType());
		Assert.assertEquals("The Datasets array stored in the controller should now " +
			"contain two elements.", 2, controller.getSelectedDatasets().length);
		Assert.assertEquals("The Datasets array stored in the controller should now contain " +
			GBMAM + " in its first index.", GBMAM, controller.getSelectedDatasets()[0]);
		Assert.assertEquals("The Datasets array stored in the controller should now contain " +
			GBROD + " in its second index.", GBROD, controller.getSelectedDatasets()[1]);
		printSelections();
	}
	
	public void printSelections() {
		Dataset[] selectedDatasets = controller.getSelectedDatasets();
		String selectedDatasetString = "";
		if (selectedDatasets != null) {
			for (int i=0; i<selectedDatasets.length; i++) {
				selectedDatasetString += selectedDatasets[i].toString();
				if (i < selectedDatasets.length - 1)
					selectedDatasetString += ", ";
			}
		} else selectedDatasetString += "null.";
		String datasetCount = controller.getDatasets() != null ?
			"" + controller.getDatasets().size() : "<null>";
		System.out.println("\nCurrent selections: EntityType " + controller.getEntityType() +
			", DataType " + controller.getDataType() +
			", Datasets " + selectedDatasetString);
		System.out.println("Current drop-down list sizes: " +
			"EntityType " + controller.getEntityTypes().size() +
			", DataType " + controller.getDataTypes().size() +
			", Datasets " + datasetCount);
	}
}
