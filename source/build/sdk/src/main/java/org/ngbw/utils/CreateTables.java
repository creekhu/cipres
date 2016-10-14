/*
 * CreateTables.java
 */
package org.ngbw.utils;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.ngbw.sdk.database.DatabaseTools;
import org.ngbw.sdk.database.DriverConnectionSource;


/**
 *
 * @author Paul Hoover
 *
 */
public class CreateTables {

	// data fields


	private final boolean m_scriptOnly;


	// constructors


	private CreateTables(boolean script)
	{
		m_scriptOnly = script;
	}


	// public methods


	public void create(Connection dbConn) throws SQLException
	{
		createUsersTable(dbConn);
		createUserPreferencesTable(dbConn);
		createGroupsTable(dbConn);
		executeCommand(dbConn, "ALTER TABLE users ADD FOREIGN KEY (DEFAULT_GROUP_ID) REFERENCES groups (GROUP_ID)");
		createUserGroupLookupTable(dbConn);
		createFoldersTable(dbConn);
		createFolderPreferencesTable(dbConn);
		createSourceDocumentsTable(dbConn);
		createUserDataTable(dbConn);
		createItemMetadataTable(dbConn);
		createDataRecordsTable(dbConn);
		createRecordFieldsTable(dbConn);
		createTasksTable(dbConn);
		createTaskPropertiesTable(dbConn);
		createToolParametersTable(dbConn);
		createTaskInputParametersTable(dbConn);
		createTaskInputSourceDocumentsTable(dbConn);
		createTaskOutputParametersTable(dbConn);
		createTaskOutputSourceDocumentsTable(dbConn);
		createTaskLogMessagesTable(dbConn);
		createApplicationsTable(dbConn);
		createApplicationPreferencesTable(dbConn);
		createCachedItemsTable(dbConn);
		createJobEventsTable(dbConn);
		createJobStatsTable(dbConn);
		createRunningTasksTable(dbConn);
		createRunningTaskParametersTable(dbConn);
		createSsoTable(dbConn);
		createTgusageTable(dbConn);
	}


	/**
	 *
	 * @param args
	 */
	public static void main(String[] args)
	{
		try {
			boolean script;

			if (args.length == 0)
				script = false;
			else if (args.length == 1 && args[0].equals("--script"))
				script = true;
			else
				throw new Exception("usage: CreateTables [ --script ]");

			Connection dbConn = (new DriverConnectionSource()).getConnection();

			try {
				dbConn.setAutoCommit(false);

				(new CreateTables(script)).create(dbConn);
			}
			finally {
				dbConn.close();
			}
		}
		catch (Exception err) {
			err.printStackTrace(System.err);

			System.exit(-1);
		}
	}


	// private methods


	/**
	 *
	 * @param dbConn
	 * @param command
	 * @throws SQLException
	 */
	private void executeCommand(Connection dbConn, String command) throws SQLException
	{
		if (m_scriptOnly) {
			System.out.println(command + ";\n");

			return;
		}

		Statement createStmt = dbConn.createStatement();

		try {
			createStmt.executeUpdate(command);

			dbConn.commit();
		}
		catch (SQLException sqlErr) {
			dbConn.rollback();

			throw sqlErr;
		}
		finally {
			createStmt.close();
		}
	}

	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private void createUsersTable(Connection dbConn) throws SQLException
	{
		String keyTypeName = DatabaseTools.getKeyTypeName();
		String longTypeName = DatabaseTools.getLongTypeName();
		String stringTypeName = DatabaseTools.getStringTypeName();
		String dateTypeName = DatabaseTools.getDateTypeName();
		String boolTypeName = DatabaseTools.getBooleanTypeName();
		String boolTrue = DatabaseTools.getBooleanTrue();
		String intTypeName = DatabaseTools.getIntegerTypeName();
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("CREATE TABLE users (\n  USER_ID ");
		queryBuilder.append(keyTypeName);
		queryBuilder.append(" NOT NULL,\n  DEFAULT_GROUP_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(",\n  COMMENT ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255),\n  INSTITUTION ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255),\n  AREA_CODE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(10),\n  CITY ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(100),\n  COUNTRY ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(50),\n  EMAIL ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(200) NOT NULL,\n  FIRST_NAME ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(100) NOT NULL,\n  LAST_NAME ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(100) NOT NULL,\n  MAILCODE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(10),\n  PASSWORD ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(50) NOT NULL,\n  PHONE_NUMBER ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(20),\n  ROLE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(50) NOT NULL,\n  STATE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(50),\n  STREET_ADDRESS ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255),\n  USERNAME ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(200) NOT NULL,\n  WEBSITE_URL ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255),\n  ZIP_CODE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(10),\n  ACTIVE ");
		queryBuilder.append(boolTypeName);
		queryBuilder.append(" DEFAULT ");
		queryBuilder.append(boolTrue);
		queryBuilder.append(" NOT NULL,\n  VERSION ");
		queryBuilder.append(intTypeName);
		queryBuilder.append(" DEFAULT 0 NOT NULL,\n  CAN_SUBMIT ");
		queryBuilder.append(boolTypeName);
		queryBuilder.append(" DEFAULT ");
		queryBuilder.append(boolTrue);
		queryBuilder.append(" NOT NULL,\n  LAST_LOGIN ");
		queryBuilder.append(dateTypeName);
		queryBuilder.append(",\n  UMBRELLA_APPNAME ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(30) DEFAULT '' NOT NULL,\n  ACTIVATION_CODE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(50),\n  ACTIVATION_SENT ");
		queryBuilder.append(dateTypeName);
		queryBuilder.append(",\n  DATE_CREATED ");
		queryBuilder.append(dateTypeName);
		queryBuilder.append(" DEFAULT CURRENT_TIMESTAMP,\n  PRIMARY KEY (USER_ID),\n  " +
				"UNIQUE (USERNAME),\n  " +
				"UNIQUE (EMAIL, ROLE, UMBRELLA_APPNAME)\n)");

		executeCommand(dbConn, queryBuilder.toString());
		executeCommand(dbConn, "CREATE INDEX ACTIVATION ON users (ACTIVATION_SENT)");

		if (!DatabaseTools.usesGeneratedKeys())
			executeCommand(dbConn, "CREATE SEQUENCE users_user_id_seq START WITH 1");
	}

	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private void createUserPreferencesTable(Connection dbConn) throws SQLException
	{
		String longTypeName = DatabaseTools.getLongTypeName();
		String stringTypeName = DatabaseTools.getStringTypeName();
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("CREATE TABLE user_preferences (\n  USER_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  VALUE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(100),\n  PREFERENCE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(100) NOT NULL,\n  PRIMARY KEY (USER_ID, PREFERENCE),\n  " +
				"FOREIGN KEY (USER_ID) REFERENCES users (USER_ID)\n)");

		executeCommand(dbConn, queryBuilder.toString());
	}

	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private void createGroupsTable(Connection dbConn) throws SQLException
	{
		String keyTypeName = DatabaseTools.getKeyTypeName();
		String longTypeName = DatabaseTools.getLongTypeName();
		String stringTypeName = DatabaseTools.getStringTypeName();
		String dateTypeName = DatabaseTools.getDateTypeName();
		String intTypeName = DatabaseTools.getIntegerTypeName();
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("CREATE TABLE groups (\n  GROUP_ID ");
		queryBuilder.append(keyTypeName);
		queryBuilder.append(" NOT NULL,\n  ADMINISTRATOR ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(",\n  COMMENT ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255),\n  CREATION_DATE ");
		queryBuilder.append(dateTypeName);
		queryBuilder.append(" NOT NULL,\n  DESCRIPTION ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255),\n  GROUPNAME ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255) NOT NULL,\n  VERSION ");
		queryBuilder.append(intTypeName);
		queryBuilder.append(" DEFAULT 0 NOT NULL,\n  PRIMARY KEY (GROUP_ID),\n  " +
				"UNIQUE (GROUPNAME),\n  " +
				"FOREIGN KEY (ADMINISTRATOR) REFERENCES users (USER_ID)\n)");

		executeCommand(dbConn, queryBuilder.toString());

		if (!DatabaseTools.usesGeneratedKeys())
			executeCommand(dbConn, "CREATE SEQUENCE groups_group_id_seq START WITH 1");
	}

	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private void createUserGroupLookupTable(Connection dbConn) throws SQLException
	{
		String longTypeName = DatabaseTools.getLongTypeName();
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("CREATE TABLE user_group_lookup (\n  USER_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  GROUP_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  PRIMARY KEY (USER_ID, GROUP_ID),\n  " +
				"FOREIGN KEY (USER_ID) REFERENCES users (USER_ID),\n  " +
				"FOREIGN KEY (GROUP_ID) REFERENCES groups (GROUP_ID)\n)");

		executeCommand(dbConn, queryBuilder.toString());
	}

	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private void createFoldersTable(Connection dbConn) throws SQLException
	{
		String keyTypeName = DatabaseTools.getKeyTypeName();
		String longTypeName = DatabaseTools.getLongTypeName();
		String stringTypeName = DatabaseTools.getStringTypeName();
		String dateTypeName = DatabaseTools.getDateTypeName();
		String boolTypeName = DatabaseTools.getBooleanTypeName();
		String boolFalse = DatabaseTools.getBooleanFalse();
		String intTypeName = DatabaseTools.getIntegerTypeName();
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("CREATE TABLE folders (\n  FOLDER_ID ");
		queryBuilder.append(keyTypeName);
		queryBuilder.append(" NOT NULL,\n  GROUP_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  USER_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  ENCLOSING_FOLDER_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(",\n  COMMENT ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255),\n  CREATION_DATE ");
		queryBuilder.append(dateTypeName);
		queryBuilder.append(" NOT NULL,\n  LABEL ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255) NOT NULL,\n  GROUP_READABLE ");
		queryBuilder.append(boolTypeName);
		queryBuilder.append(" DEFAULT ");
		queryBuilder.append(boolFalse);
		queryBuilder.append(" NOT NULL,\n  WORLD_READABLE ");
		queryBuilder.append(boolTypeName);
		queryBuilder.append(" DEFAULT ");
		queryBuilder.append(boolFalse);
		queryBuilder.append(" NOT NULL,\n  VERSION ");
		queryBuilder.append(intTypeName);
		queryBuilder.append(" DEFAULT 0 NOT NULL,\n  PRIMARY KEY (FOLDER_ID),\n  " +
				"FOREIGN KEY (GROUP_ID) REFERENCES groups (GROUP_ID),\n  " +
				"FOREIGN KEY (USER_ID) REFERENCES users (USER_ID),\n  " +
				"FOREIGN KEY (ENCLOSING_FOLDER_ID) REFERENCES folders (FOLDER_ID)\n)");

		executeCommand(dbConn, queryBuilder.toString());

		if (!DatabaseTools.usesGeneratedKeys())
			executeCommand(dbConn, "CREATE SEQUENCE folders_folder_id_seq START WITH 1");
	}

	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private void createFolderPreferencesTable(Connection dbConn) throws SQLException
	{
		String longTypeName = DatabaseTools.getLongTypeName();
		String stringTypeName = DatabaseTools.getStringTypeName();
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("CREATE TABLE folder_preferences (\n  FOLDER_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  VALUE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(100),\n  PREFERENCE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(100) NOT NULL,\n  PRIMARY KEY (FOLDER_ID, PREFERENCE),\n  " +
				"FOREIGN KEY (FOLDER_ID) REFERENCES folders (FOLDER_ID)\n)");

		executeCommand(dbConn, queryBuilder.toString());
	}

	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private void createSourceDocumentsTable(Connection dbConn) throws SQLException
	{
		String keyTypeName = DatabaseTools.getKeyTypeName();
		String stringTypeName = DatabaseTools.getStringTypeName();
		String longTypeName = DatabaseTools.getLongTypeName();
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("CREATE TABLE source_documents (\n  SOURCE_DOCUMENT_ID ");
		queryBuilder.append(keyTypeName);
		queryBuilder.append(" NOT NULL,\n  FILENAME ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(1023) NOT NULL,\n  LENGTH ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  SIGNATURE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255) NOT NULL,\n  PRIMARY KEY (SOURCE_DOCUMENT_ID)\n)");

		executeCommand(dbConn, queryBuilder.toString());
		executeCommand(dbConn, "CREATE INDEX SD_SIGNATURE_INDEX ON source_documents (SIGNATURE)");

		if (!DatabaseTools.usesGeneratedKeys())
			executeCommand(dbConn, "CREATE SEQUENCE source_documents_source_document_id_seq START WITH 1");
	}

	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private void createUserDataTable(Connection dbConn) throws SQLException
	{
		String keyTypeName = DatabaseTools.getKeyTypeName();
		String longTypeName = DatabaseTools.getLongTypeName();
		String stringTypeName = DatabaseTools.getStringTypeName();
		String dateTypeName = DatabaseTools.getDateTypeName();
		String boolTypeName = DatabaseTools.getBooleanTypeName();
		String boolFalse = DatabaseTools.getBooleanFalse();
		String intTypeName = DatabaseTools.getIntegerTypeName();
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("CREATE TABLE userdata (\n  USERDATA_ID ");
		queryBuilder.append(keyTypeName);
		queryBuilder.append(" NOT NULL,\n  GROUP_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  USER_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  ENCLOSING_FOLDER_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  SOURCE_DOCUMENT_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  COMMENT ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255),\n  CREATION_DATE ");
		queryBuilder.append(dateTypeName);
		queryBuilder.append(" NOT NULL,\n  LABEL ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(1023) NOT NULL,\n  DATAFORMAT ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255) NOT NULL,\n  DATATYPE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255) NOT NULL,\n  ENTITYTYPE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255) NOT NULL,\n  VALIDATED ");
		queryBuilder.append(boolTypeName);
		queryBuilder.append(" DEFAULT ");
		queryBuilder.append(boolFalse);
		queryBuilder.append(" NOT NULL,\n  VERSION ");
		queryBuilder.append(intTypeName);
		queryBuilder.append(" DEFAULT 0 NOT NULL,\n  PRIMARY KEY (USERDATA_ID),\n  " +
				"FOREIGN KEY (GROUP_ID) REFERENCES groups (GROUP_ID),\n  " +
				"FOREIGN KEY (USER_ID) REFERENCES users (USER_ID),\n  " +
				"FOREIGN KEY (ENCLOSING_FOLDER_ID) REFERENCES folders (FOLDER_ID),\n  " +
				"FOREIGN KEY (SOURCE_DOCUMENT_ID) REFERENCES source_documents (SOURCE_DOCUMENT_ID)\n)");

		executeCommand(dbConn, queryBuilder.toString());
		executeCommand(dbConn, "CREATE INDEX COMMENT_INDEX ON userdata (COMMENT)");

		if (!DatabaseTools.usesGeneratedKeys())
			executeCommand(dbConn, "CREATE SEQUENCE userdata_userdata_id_seq START WITH 1");
	}

	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private void createItemMetadataTable(Connection dbConn) throws SQLException
	{
		String longTypeName = DatabaseTools.getLongTypeName();
		String stringTypeName = DatabaseTools.getStringTypeName();
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("CREATE TABLE item_metadata (\n  USERDATA_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  VALUE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255),\n  FIELD ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(100) NOT NULL,\n  PRIMARY KEY (USERDATA_ID, FIELD),\n  " +
				"FOREIGN KEY (USERDATA_ID) REFERENCES userdata (USERDATA_ID)\n)");

		executeCommand(dbConn, queryBuilder.toString());
	}

	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private void createDataRecordsTable(Connection dbConn) throws SQLException
	{
		String keyTypeName = DatabaseTools.getKeyTypeName();
		String longTypeName = DatabaseTools.getLongTypeName();
		String stringTypeName = DatabaseTools.getStringTypeName();
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("CREATE TABLE data_records (\n  RECORD_ID ");
		queryBuilder.append(keyTypeName);
		queryBuilder.append(" NOT NULL,\n  USERDATA_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  RECORD_TYPE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(100) NOT NULL,\n  PRIMARY KEY (RECORD_ID),\n  " +
				"FOREIGN KEY (USERDATA_ID) REFERENCES userdata (USERDATA_ID)\n)");

		executeCommand(dbConn, queryBuilder.toString());

		if (!DatabaseTools.usesGeneratedKeys())
			executeCommand(dbConn, "CREATE SEQUENCE data_records_record_id_seq START WITH 1");
	}

	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private void createRecordFieldsTable(Connection dbConn) throws SQLException
	{
		String longTypeName = DatabaseTools.getLongTypeName();
		String stringTypeName = DatabaseTools.getStringTypeName();
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("CREATE TABLE record_fields (\n  RECORD_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  RECORD_FIELD ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(100) NOT NULL,\n  VALUE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(1023),\n  PRIMARY KEY (RECORD_ID, RECORD_FIELD),\n  " +
				"FOREIGN KEY (RECORD_ID) REFERENCES data_records (RECORD_ID)\n)");

		executeCommand(dbConn, queryBuilder.toString());
	}

	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private void createTasksTable(Connection dbConn) throws SQLException
	{
		String keyTypeName = DatabaseTools.getKeyTypeName();
		String longTypeName = DatabaseTools.getLongTypeName();
		String stringTypeName = DatabaseTools.getStringTypeName();
		String dateTypeName = DatabaseTools.getDateTypeName();
		String boolTypeName = DatabaseTools.getBooleanTypeName();
		String boolTrue = DatabaseTools.getBooleanTrue();
		String boolFalse = DatabaseTools.getBooleanFalse();
		String intTypeName = DatabaseTools.getIntegerTypeName();
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("CREATE TABLE tasks (\n  TASK_ID ");
		queryBuilder.append(keyTypeName);
		queryBuilder.append(" NOT NULL,\n  GROUP_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  USER_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  ENCLOSING_FOLDER_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  COMMENT ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255),\n  CREATION_DATE ");
		queryBuilder.append(dateTypeName);
		queryBuilder.append(" NOT NULL,\n  LABEL ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255) NOT NULL,\n  JOBHANDLE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255),\n  OK ");
		queryBuilder.append(boolTypeName);
		queryBuilder.append(" DEFAULT ");
		queryBuilder.append(boolTrue);
		queryBuilder.append(" NOT NULL,\n  STAGE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(20) NOT NULL,\n  TOOL_ID ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(100),\n  IS_TERMINAL ");
		queryBuilder.append(boolTypeName);
		queryBuilder.append(" DEFAULT ");
		queryBuilder.append(boolFalse);
		queryBuilder.append(" NOT NULL,\n  APPNAME ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(30) DEFAULT '' NOT NULL,\n  VERSION ");
		queryBuilder.append(intTypeName);
		queryBuilder.append(" DEFAULT 0 NOT NULL,\n  PRIMARY KEY (TASK_ID),\n  " +
				"UNIQUE (JOBHANDLE),\n  " +
				"FOREIGN KEY (GROUP_ID) REFERENCES groups (GROUP_ID),\n  " +
				"FOREIGN KEY (USER_ID) REFERENCES users (USER_ID),\n  " +
				"FOREIGN KEY (ENCLOSING_FOLDER_ID) REFERENCES folders (FOLDER_ID)\n)");

		executeCommand(dbConn, queryBuilder.toString());

		if (!DatabaseTools.usesGeneratedKeys())
			executeCommand(dbConn, "CREATE SEQUENCE tasks_task_id_seq START WITH 1");
	}

	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private void createTaskPropertiesTable(Connection dbConn) throws SQLException
	{
		String longTypeName = DatabaseTools.getLongTypeName();
		String stringTypeName = DatabaseTools.getStringTypeName();
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("CREATE TABLE task_properties (\n  TASK_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  VALUE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(4095),\n  PROPERTY ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(100) NOT NULL,\n  PRIMARY KEY (TASK_ID, PROPERTY),\n  " +
				"FOREIGN KEY (TASK_ID) REFERENCES tasks (TASK_ID)\n)");

		executeCommand(dbConn, queryBuilder.toString());
	}

	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private void createToolParametersTable(Connection dbConn) throws SQLException
	{
		String longTypeName = DatabaseTools.getLongTypeName();
		String stringTypeName = DatabaseTools.getStringTypeName();
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("CREATE TABLE tool_parameters (\n  TASK_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  VALUE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(1024),\n  PARAMETER ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(100) NOT NULL,\n  PRIMARY KEY (TASK_ID, PARAMETER),\n  " +
				"FOREIGN KEY (TASK_ID) REFERENCES tasks (TASK_ID)\n)");

		executeCommand(dbConn, queryBuilder.toString());
	}

	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private void createTaskInputParametersTable(Connection dbConn) throws SQLException
	{
		String keyTypeName = DatabaseTools.getKeyTypeName();
		String longTypeName = DatabaseTools.getLongTypeName();
		String stringTypeName = DatabaseTools.getStringTypeName();
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("CREATE TABLE task_input_parameters (\n  INPUT_ID ");
		queryBuilder.append(keyTypeName);
		queryBuilder.append(" NOT NULL,\n  TASK_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  PARAMETER ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255) NOT NULL,\n  PRIMARY KEY (INPUT_ID),\n  " +
				"FOREIGN KEY (TASK_ID) REFERENCES tasks (TASK_ID)\n)");

		executeCommand(dbConn, queryBuilder.toString());

		if (!DatabaseTools.usesGeneratedKeys())
			executeCommand(dbConn, "CREATE SEQUENCE task_input_parameters_input_id_seq START WITH 1");
	}

	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private void createTaskInputSourceDocumentsTable(Connection dbConn) throws SQLException
	{
		String keyTypeName = DatabaseTools.getKeyTypeName();
		String longTypeName = DatabaseTools.getLongTypeName();
		String stringTypeName = DatabaseTools.getStringTypeName();
		String boolTypeName = DatabaseTools.getBooleanTypeName();
		String boolFalse = DatabaseTools.getBooleanFalse();
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("CREATE TABLE task_input_source_documents (\n  INPUT_DOCUMENT_ID ");
		queryBuilder.append(keyTypeName);
		queryBuilder.append(" NOT NULL,\n  INPUT_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  SOURCE_DOCUMENT_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  DATAFORMAT ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255) NOT NULL,\n  DATATYPE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255) NOT NULL,\n  ENTITYTYPE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255) NOT NULL,\n  NAME ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255),\n  VALIDATED ");
		queryBuilder.append(boolTypeName);
		queryBuilder.append(" DEFAULT ");
		queryBuilder.append(boolFalse);
		queryBuilder.append(" NOT NULL,\n  PRIMARY KEY (INPUT_DOCUMENT_ID),\n  " +
				"FOREIGN KEY (INPUT_ID) REFERENCES task_input_parameters (INPUT_ID),\n  " +
				"FOREIGN KEY (SOURCE_DOCUMENT_ID) REFERENCES source_documents (SOURCE_DOCUMENT_ID)\n)");

		executeCommand(dbConn, queryBuilder.toString());

		if (!DatabaseTools.usesGeneratedKeys())
			executeCommand(dbConn, "CREATE SEQUENCE task_input_source_documents_input_document_id_seq START WITH 1");
	}

	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private void createTaskOutputParametersTable(Connection dbConn) throws SQLException
	{
		String keyTypeName = DatabaseTools.getKeyTypeName();
		String longTypeName = DatabaseTools.getLongTypeName();
		String stringTypeName = DatabaseTools.getStringTypeName();
		String boolTypeName = DatabaseTools.getBooleanTypeName();
		String boolFalse = DatabaseTools.getBooleanFalse();
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("CREATE TABLE task_output_parameters (\n  OUTPUT_ID ");
		queryBuilder.append(keyTypeName);
		queryBuilder.append(" NOT NULL,\n  TASK_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  PARAMETER ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255) NOT NULL,\n  INTERMEDIATE ");
		queryBuilder.append(boolTypeName);
		queryBuilder.append(" DEFAULT ");
		queryBuilder.append(boolFalse);
		queryBuilder.append(" NOT NULL,\n  PRIMARY KEY (OUTPUT_ID),\n  " +
				"FOREIGN KEY (TASK_ID) REFERENCES tasks (TASK_ID)\n)");

		executeCommand(dbConn, queryBuilder.toString());

		if (!DatabaseTools.usesGeneratedKeys())
			executeCommand(dbConn, "CREATE SEQUENCE task_output_parameters_output_id_seq START WITH 1");
	}

	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private void createTaskOutputSourceDocumentsTable(Connection dbConn) throws SQLException
	{
		String keyTypeName = DatabaseTools.getKeyTypeName();
		String longTypeName = DatabaseTools.getLongTypeName();
		String stringTypeName = DatabaseTools.getStringTypeName();
		String boolTypeName = DatabaseTools.getBooleanTypeName();
		String boolFalse = DatabaseTools.getBooleanFalse();
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("CREATE TABLE task_output_source_documents (\n  OUTPUT_DOCUMENT_ID ");
		queryBuilder.append(keyTypeName);
		queryBuilder.append(" NOT NULL,\n  OUTPUT_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  SOURCE_DOCUMENT_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  DATAFORMAT ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255) NOT NULL,\n  DATATYPE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255) NOT NULL,\n  ENTITYTYPE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255) NOT NULL,\n  NAME ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255),\n  VALIDATED ");
		queryBuilder.append(boolTypeName);
		queryBuilder.append(" DEFAULT ");
		queryBuilder.append(boolFalse);
		queryBuilder.append(" NOT NULL,\n  PRIMARY KEY (OUTPUT_DOCUMENT_ID),\n  " +
				"FOREIGN KEY (OUTPUT_ID) REFERENCES task_output_parameters (OUTPUT_ID),\n  " +
				"FOREIGN KEY (SOURCE_DOCUMENT_ID) REFERENCES source_documents (SOURCE_DOCUMENT_ID)\n)");

		executeCommand(dbConn, queryBuilder.toString());

		if (!DatabaseTools.usesGeneratedKeys())
			executeCommand(dbConn, "CREATE SEQUENCE task_output_source_documents_output_document_id_seq START WITH 1");
	}

	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private void createTaskLogMessagesTable(Connection dbConn) throws SQLException
	{
		String longTypeName = DatabaseTools.getLongTypeName();
		String intTypeName = DatabaseTools.getIntegerTypeName();
		String dateTypeName = DatabaseTools.getDateTypeName();
		String stringTypeName = DatabaseTools.getStringTypeName();
		String boolTypeName = DatabaseTools.getBooleanTypeName();
		String boolFalse = DatabaseTools.getBooleanFalse();
		String textTypeName = DatabaseTools.getTextTypeName();
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("CREATE TABLE task_log_messages (\n  TASK_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  MESSAGE_INDEX ");
		queryBuilder.append(intTypeName);
		queryBuilder.append(" NOT NULL,\n  CREATION_DATE ");
		queryBuilder.append(dateTypeName);
		queryBuilder.append(" NOT NULL,\n  STAGE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(20) NOT NULL,\n  ERROR ");
		queryBuilder.append(boolTypeName);
		queryBuilder.append(" DEFAULT ");
		queryBuilder.append(boolFalse);
		queryBuilder.append(" NOT NULL,\n  MESSAGE ");
		queryBuilder.append(textTypeName);
		queryBuilder.append(",\n  PRIMARY KEY (TASK_ID, MESSAGE_INDEX),\n  " +
				"FOREIGN KEY (TASK_ID) REFERENCES tasks (TASK_ID)\n)");

		executeCommand(dbConn, queryBuilder.toString());
	}

	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private void createApplicationsTable(Connection dbConn) throws SQLException
	{
		String boolTypeName = DatabaseTools.getBooleanTypeName();
		String longTypeName = DatabaseTools.getLongTypeName();
		String stringTypeName = DatabaseTools.getStringTypeName();
		String dateTypeName = DatabaseTools.getDateTypeName();
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("CREATE TABLE applications (\n  NAME ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(30) NOT NULL,\n  APP_ID ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255) NOT NULL,\n  LONGNAME ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(100) NOT NULL,\n  AUTH_TYPE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(30) NOT NULL,\n  AUTH_USER_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  WEBSITE_URL ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255),\n  COMMENT ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255),\n  ACTIVE ");
		queryBuilder.append(boolTypeName);
		queryBuilder.append(" NOT NULL,\n  DATE_CREATED ");
		queryBuilder.append(dateTypeName);
		queryBuilder.append(" DEFAULT CURRENT_TIMESTAMP,\n  PRIMARY KEY (NAME),\n  " +
				"UNIQUE (APP_ID)\n)");

		executeCommand(dbConn, queryBuilder.toString());
	}

	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private void createApplicationPreferencesTable(Connection dbConn) throws SQLException
	{
		String stringTypeName = DatabaseTools.getStringTypeName();
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("CREATE TABLE application_preferences (\n  NAME ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(30) NOT NULL,\n  VALUE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(100),\n  PREFERENCE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(100) NOT NULL,\n  PRIMARY KEY (NAME, PREFERENCE),\n  " +
				"FOREIGN KEY (NAME) REFERENCES applications (NAME)\n)");

		executeCommand(dbConn, queryBuilder.toString());
	}

	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private void createCachedItemsTable(Connection dbConn) throws SQLException
	{
		String keyTypeName = DatabaseTools.getKeyTypeName();
		String stringTypeName = DatabaseTools.getStringTypeName();
		String intTypeName = DatabaseTools.getIntegerTypeName();
		String binaryTypeName = DatabaseTools.getBinaryTypeName();
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("CREATE TABLE cached_items (\n  CACHED_ITEM_ID ");
		queryBuilder.append(keyTypeName);
		queryBuilder.append(" NOT NULL,\n  SESSION_ID ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255) NOT NULL,\n  ATTRIBUTE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255) NOT NULL,\n  COMMENT ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255),\n  VERSION ");
		queryBuilder.append(intTypeName);
		queryBuilder.append(" DEFAULT 0 NOT NULL,\n  VALUE ");
		queryBuilder.append(binaryTypeName);
		queryBuilder.append(" NOT NULL,\n  PRIMARY KEY (CACHED_ITEM_ID),\n  " +
				"UNIQUE (SESSION_ID, ATTRIBUTE)\n)");

		executeCommand(dbConn, queryBuilder.toString());

		if (!DatabaseTools.usesGeneratedKeys())
			executeCommand(dbConn, "CREATE SEQUENCE cached_items_cached_item_id_seq START WITH 1");
	}

	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private void createJobEventsTable(Connection dbConn) throws SQLException
	{
		String keyTypeName = DatabaseTools.getKeyTypeName();
		String stringTypeName = DatabaseTools.getStringTypeName();
		String dateTypeName = DatabaseTools.getDateTypeName();
		String textTypeName = DatabaseTools.getTextTypeName();
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("CREATE TABLE job_events (\n  SE_ID ");
		queryBuilder.append(keyTypeName);
		queryBuilder.append(" NOT NULL,\n  JOBHANDLE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255) NOT NULL,\n  EVENT_DATE ");
		queryBuilder.append(dateTypeName);
		queryBuilder.append(" NOT NULL,\n  TASK_STAGE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(20),\n  NAME ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(100) NOT NULL,\n  VALUE ");
		queryBuilder.append(textTypeName);
		queryBuilder.append(",\n  PRIMARY KEY (SE_ID)\n)");

		executeCommand(dbConn, queryBuilder.toString());
		executeCommand(dbConn, "CREATE INDEX JOBHANDLE ON job_events (JOBHANDLE)");

		if (!DatabaseTools.usesGeneratedKeys())
			executeCommand(dbConn, "CREATE SEQUENCE job_events_se_id_seq START WITH 1");
	}

	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private void createJobStatsTable(Connection dbConn) throws SQLException
	{
		String stringTypeName = DatabaseTools.getStringTypeName();
		String longTypeName = DatabaseTools.getLongTypeName();
		String dateTypeName = DatabaseTools.getDateTypeName();
		String intTypeName = DatabaseTools.getIntegerTypeName();
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("CREATE TABLE job_stats (\n  JOBHANDLE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255) NOT NULL,\n  RESOURCE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255) NOT NULL,\n  TASK_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  USER_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  TOOL_ID ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(100) NOT NULL,\n  EMAIL ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(200) NOT NULL,\n  SUBMITTER ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(1023) DEFAULT 'UNKNOWN' NOT NULL,\n  REMOTE_JOB_ID ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(1023),\n  TG_CHARGE_NUMBER ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(100),\n  DATE_ENTERED ");
		queryBuilder.append(dateTypeName);
		queryBuilder.append(" NOT NULL,\n  DATE_SUBMITTED ");
		queryBuilder.append(dateTypeName);
		queryBuilder.append(",\n  DATE_TERMINATED ");
		queryBuilder.append(dateTypeName);
		queryBuilder.append(",\n  SU_PREDICTED ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(",\n  SU_COMPUTED ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(",\n  SU_CHARGED ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(",\n  SU_OVERRIDE ");
		queryBuilder.append(intTypeName);
		queryBuilder.append(",\n  APPNAME ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(30) DEFAULT '' NOT NULL,\n  PRIMARY KEY (JOBHANDLE)\n)");

		executeCommand(dbConn, queryBuilder.toString());
	}

	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private void createRunningTasksTable(Connection dbConn) throws SQLException
	{
		String stringTypeName = DatabaseTools.getStringTypeName();
		String longTypeName = DatabaseTools.getLongTypeName();
		String dateTypeName = DatabaseTools.getDateTypeName();
		String intTypeName = DatabaseTools.getIntegerTypeName();
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("CREATE TABLE running_tasks (\n  JOBHANDLE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255) NOT NULL,\n  RESOURCE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255) NOT NULL,\n  TOOL_ID ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(100) NOT NULL,\n  USER_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  TASK_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  SUBMITTER ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(1023) NOT NULL,\n  REMOTE_JOB_ID ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(1023),\n  DATE_ENTERED ");
		queryBuilder.append(dateTypeName);
		queryBuilder.append(" NOT NULL,\n  LOCKED ");
		queryBuilder.append(dateTypeName);
		queryBuilder.append(",\n  HOSTNAME ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(200),\n  PID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(",\n  STATUS ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(100) NOT NULL,\n  ATTEMPTS ");
		queryBuilder.append(intTypeName);
		queryBuilder.append(" DEFAULT 0 NOT NULL,\n  OUTPUT_DESC ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(5000),\n  COMMANDLINE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(5000),\n  SPROPS ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(500),\n  PRIMARY KEY (JOBHANDLE)\n)");

		executeCommand(dbConn, queryBuilder.toString());
	}

	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private void createRunningTaskParametersTable(Connection dbConn) throws SQLException
	{
		String stringTypeName = DatabaseTools.getStringTypeName();
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("CREATE TABLE running_tasks_parameters (\n  JOBHANDLE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255) NOT NULL,\n  NAME ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(100) NOT NULL,\n  VALUE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(1023),\n  PRIMARY KEY (JOBHANDLE, NAME),\n  " +
				"FOREIGN KEY (JOBHANDLE) REFERENCES running_tasks (JOBHANDLE)\n)");

		executeCommand(dbConn, queryBuilder.toString());
	}

	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private void createSsoTable(Connection dbConn) throws SQLException
	{
		String keyTypeName = DatabaseTools.getKeyTypeName();
		String stringTypeName = DatabaseTools.getStringTypeName();
		String longTypeName = DatabaseTools.getLongTypeName();
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("CREATE TABLE sso (\n  SSO_ID ");
		queryBuilder.append(keyTypeName);
		queryBuilder.append(" NOT NULL,\n  USER_ID ");
		queryBuilder.append(longTypeName);
		queryBuilder.append(" NOT NULL,\n  SSO_USERNAME ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255) NOT NULL,\n  PRIMARY KEY (SSO_ID),\n  " +
				"UNIQUE (SSO_USERNAME),\n  " +
				"FOREIGN KEY (USER_ID) REFERENCES users (USER_ID)\n)");

		executeCommand(dbConn, queryBuilder.toString());

		if (!DatabaseTools.usesGeneratedKeys())
			executeCommand(dbConn, "CREATE SEQUENCE sso_sso_id_seq START WITH 1");
	}

	/**
	 *
	 * @param dbConn
	 * @throws SQLException
	 */
	private void createTgusageTable(Connection dbConn) throws SQLException
	{
		String keyTypeName = DatabaseTools.getKeyTypeName();
		String stringTypeName = DatabaseTools.getStringTypeName();
		String intTypeName = DatabaseTools.getIntegerTypeName();
		String dateTypeName = DatabaseTools.getDateTypeName();
		StringBuilder queryBuilder = new StringBuilder();

		queryBuilder.append("CREATE TABLE tgusage (\n  TGUSAGE_ID ");
		queryBuilder.append(keyTypeName);
		queryBuilder.append(" NOT NULL,\n  RESOURCE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(100) NOT NULL,\n  JOBID ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(100) NOT NULL,\n  FULL_JOBID ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255) NOT NULL,\n  FULL_RESOURCE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(255) NOT NULL,\n  CHARGE ");
		queryBuilder.append(intTypeName);
		queryBuilder.append(" NOT NULL,\n  START_TIME ");
		queryBuilder.append(dateTypeName);
		queryBuilder.append(" NOT NULL,\n  END_TIME ");
		queryBuilder.append(dateTypeName);
		queryBuilder.append(" NOT NULL,\n  SUBMIT_TIME ");
		queryBuilder.append(dateTypeName);
		queryBuilder.append(" NOT NULL,\n  CHARGE_NUMBER ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(100) NOT NULL,\n  WALL_HRS ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(10) NOT NULL,\n  SU ");
		queryBuilder.append(intTypeName);
		queryBuilder.append(" NOT NULL,\n  NODECOUNT ");
		queryBuilder.append(intTypeName);
		queryBuilder.append(" NOT NULL,\n  PROCESSORS ");
		queryBuilder.append(intTypeName);
		queryBuilder.append(" NOT NULL,\n  QUEUE ");
		queryBuilder.append(stringTypeName);
		queryBuilder.append("(100) NOT NULL,\n  PRIMARY KEY (TGUSAGE_ID),\n  " +
				"UNIQUE (RESOURCE, JOBID, SUBMIT_TIME)\n)");

		executeCommand(dbConn, queryBuilder.toString());

		if (!DatabaseTools.usesGeneratedKeys())
			executeCommand(dbConn, "CREATE SEQUENCE tgusage_tgusage_id_seq START WITH 1");
	}
}
