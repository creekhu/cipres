/*
   NGBW tables
*/


CREATE TABLE users ( 
	USER_ID         	bigint(20) AUTO_INCREMENT NOT NULL,
	DEFAULT_GROUP_ID	bigint(20) NULL,
	COMMENT         	varchar(255) NULL,
	INSTITUTION     	varchar(255) NULL,
	AREA_CODE       	varchar(10) NULL,
	CITY            	varchar(100) NULL,
	COUNTRY         	varchar(50) NULL,
	EMAIL           	varchar(100) NOT NULL,
	FIRST_NAME      	varchar(100) NOT NULL,
	LAST_NAME       	varchar(100) NOT NULL,
	MAILCODE        	varchar(10) NULL,
	PASSWORD        	varchar(50) NOT NULL,
	PHONE_NUMBER    	varchar(20) NULL,
	ROLE            	varchar(50) NOT NULL,
	STATE           	varchar(50) NULL,
	STREET_ADDRESS  	varchar(255) NULL,
	USERNAME        	varchar(50) NOT NULL,
	WEBSITE_URL     	varchar(255) NULL,
	ZIP_CODE        	varchar(10) NULL,
	ACTIVE          	bit(1) NOT NULL DEFAULT 1,
	CAN_SUBMIT      	bit(1) NOT NULL DEFAULT 1,
	VERSION         	int(11) NOT NULL DEFAULT 0,

	PRIMARY KEY (USER_ID),
	UNIQUE KEY (USERNAME),
	UNIQUE KEY (EMAIL)
	);


CREATE TABLE user_preferences ( 
	USER_ID   	bigint(20) NOT NULL,
	VALUE     	varchar(100) NULL,
	PREFERENCE	varchar(100) NOT NULL,

	PRIMARY KEY (USER_ID, PREFERENCE),
	FOREIGN KEY (USER_ID) REFERENCES users (USER_ID)
	);


CREATE TABLE groups ( 
	GROUP_ID      	bigint(20) AUTO_INCREMENT NOT NULL,
	ADMINISTRATOR 	bigint(20) NULL,
	COMMENT       	varchar(255) NULL,
	CREATION_DATE 	datetime NOT NULL,
	DESCRIPTION   	varchar(1023) NULL,
	GROUPNAME     	varchar(255) NOT NULL,
	VERSION       	int(11) NOT NULL DEFAULT 0,

	PRIMARY KEY (GROUP_ID),
	UNIQUE KEY (GROUPNAME),
	FOREIGN KEY (ADMINISTRATOR) REFERENCES users (USER_ID)
	);


ALTER TABLE users
	ADD CONSTRAINT
	FOREIGN KEY (DEFAULT_GROUP_ID) REFERENCES groups (GROUP_ID);


CREATE TABLE user_group_lookup ( 
	USER_ID 	bigint(20) NOT NULL,
	GROUP_ID	bigint(20) NOT NULL,

	PRIMARY KEY (USER_ID, GROUP_ID),
	FOREIGN KEY (USER_ID) REFERENCES users (USER_ID),
	FOREIGN KEY (GROUP_ID) REFERENCES groups (GROUP_ID)
	);


CREATE TABLE folders ( 
	FOLDER_ID           	bigint(20) AUTO_INCREMENT NOT NULL,
	GROUP_ID            	bigint(20) NOT NULL,
	USER_ID             	bigint(20) NOT NULL,
	ENCLOSING_FOLDER_ID 	bigint(20) NULL,
	COMMENT             	varchar(255) NULL,
	CREATION_DATE       	datetime NOT NULL,
	LABEL               	varchar(255) NOT NULL,
	GROUP_READABLE      	bit(1) NOT NULL DEFAULT 0,
	WORLD_READABLE      	bit(1) NOT NULL DEFAULT 0,
	VERSION             	int(11) NOT NULL DEFAULT 0,

	PRIMARY KEY (FOLDER_ID),
	FOREIGN KEY (GROUP_ID) REFERENCES groups (GROUP_ID),
	FOREIGN KEY (USER_ID) REFERENCES users (USER_ID),
	FOREIGN KEY (ENCLOSING_FOLDER_ID) REFERENCES folders (FOLDER_ID)
	);


CREATE TABLE folder_preferences ( 
	FOLDER_ID 	bigint(20) NOT NULL,
	VALUE     	varchar(100) NULL,
	PREFERENCE	varchar(100) NOT NULL,

	PRIMARY KEY (FOLDER_ID, PREFERENCE),
	FOREIGN KEY (FOLDER_ID) REFERENCES folders (FOLDER_ID)
	);


CREATE TABLE source_documents ( 
	SOURCE_DOCUMENT_ID	bigint(20) AUTO_INCREMENT NOT NULL,
	FILENAME          	varchar(1023) NOT NULL,
	LENGTH            	bigint(20) NOT NULL,

	PRIMARY KEY (SOURCE_DOCUMENT_ID)
	);


CREATE TABLE userdata ( 
	USERDATA_ID         	bigint(20) AUTO_INCREMENT NOT NULL,
	GROUP_ID            	bigint(20) NOT NULL,
	USER_ID             	bigint(20) NOT NULL,
	ENCLOSING_FOLDER_ID 	bigint(20) NOT NULL,
	SOURCE_DOCUMENT_ID  	bigint(20) NOT NULL,
	COMMENT             	varchar(255) NULL,
	CREATION_DATE       	datetime NOT NULL,
	LABEL               	varchar(1023) NOT NULL,
	DATAFORMAT          	varchar(255) NOT NULL,
	DATATYPE            	varchar(255) NOT NULL,
	ENTITYTYPE          	varchar(255) NOT NULL,
	VALIDATED           	bit(1) NOT NULL DEFAULT 0,
	VERSION             	int(11) NOT NULL DEFAULT 0,

	PRIMARY KEY (USERDATA_ID),
	FOREIGN KEY (GROUP_ID) REFERENCES groups (GROUP_ID),
	FOREIGN KEY (USER_ID) REFERENCES users (USER_ID),
	FOREIGN KEY (ENCLOSING_FOLDER_ID) REFERENCES folders (FOLDER_ID),
	FOREIGN KEY (SOURCE_DOCUMENT_ID) REFERENCES source_documents (SOURCE_DOCUMENT_ID)
	);


CREATE TABLE item_metadata ( 
	USERDATA_ID  bigint(20) NOT NULL,
	VALUE        varchar(255) NULL,
	FIELD        varchar(100) NOT NULL,

	PRIMARY KEY (USERDATA_ID, FIELD),
	FOREIGN KEY (USERDATA_ID) REFERENCES userdata (USERDATA_ID)
	);


CREATE TABLE data_records ( 
	RECORD_ID    bigint(20) AUTO_INCREMENT NOT NULL,
	USERDATA_ID  bigint(20) NOT NULL,
	RECORD_TYPE  varchar(100) NOT NULL,

	PRIMARY KEY (RECORD_ID),
	FOREIGN KEY (USERDATA_ID) REFERENCES userdata (USERDATA_ID)
	);


CREATE TABLE record_fields ( 
	RECORD_ID     bigint(20) NOT NULL,
	RECORD_FIELD  varchar(100) NOT NULL,
	VALUE         varchar(1023) NULL,

	PRIMARY KEY (RECORD_ID, RECORD_FIELD),
	FOREIGN KEY (RECORD_ID) REFERENCES data_records (RECORD_ID)
	);


CREATE TABLE tasks ( 
	TASK_ID             	bigint(20) AUTO_INCREMENT NOT NULL,
	GROUP_ID            	bigint(20) NOT NULL,
	USER_ID             	bigint(20) NOT NULL,
	ENCLOSING_FOLDER_ID 	bigint(20) NOT NULL,
	COMMENT             	varchar(255) NULL,
	CREATION_DATE       	datetime NOT NULL,
	LABEL               	varchar(1023) NOT NULL,
	JOBHANDLE           	varchar(255) NULL,
	OK                  	bit(1) NOT NULL DEFAULT 1,
	STAGE               	varchar(20) NOT NULL,
	TOOL_ID             	varchar(100) NULL,
	VERSION             	int(11) NOT NULL DEFAULT 0,

	PRIMARY KEY (TASK_ID),
	FOREIGN KEY (GROUP_ID) REFERENCES groups (GROUP_ID),
	FOREIGN KEY (USER_ID) REFERENCES users (USER_ID),
	FOREIGN KEY (ENCLOSING_FOLDER_ID) REFERENCES folders (FOLDER_ID)
	);


CREATE TABLE task_properties ( 
	TASK_ID  	bigint(20) NOT NULL,
	VALUE    	varchar(255) NULL,
	PROPERTY	varchar(100) NOT NULL,

	PRIMARY KEY (TASK_ID, PROPERTY),
	FOREIGN KEY (TASK_ID) REFERENCES tasks (TASK_ID)
	);


CREATE TABLE tool_parameters ( 
	TASK_ID   	bigint(20) NOT NULL,
	VALUE     	varchar(255) NULL,
	PARAMETER 	varchar(100) NOT NULL,

	PRIMARY KEY (TASK_ID, PARAMETER),
	FOREIGN KEY (TASK_ID) REFERENCES tasks (TASK_ID)
	);


CREATE TABLE task_input_parameters ( 
	INPUT_ID  	bigint(20) AUTO_INCREMENT NOT NULL,
	TASK_ID   	bigint(20) NOT NULL,
	PARAMETER 	varchar(255) NOT NULL,

	PRIMARY KEY (INPUT_ID),
	FOREIGN KEY (TASK_ID) REFERENCES tasks (TASK_ID)
	);


CREATE TABLE task_input_source_documents ( 
	INPUT_DOCUMENT_ID 	bigint(20) AUTO_INCREMENT NOT NULL,
	INPUT_ID          	bigint(20) NOT NULL,
	SOURCE_DOCUMENT_ID 	bigint(20) NOT NULL,
	DATAFORMAT        	varchar(255) NOT NULL,
	DATATYPE          	varchar(255) NOT NULL,
	ENTITYTYPE        	varchar(255) NOT NULL,
	NAME              	varchar(255) NULL,
	VALIDATED         	bit(1) NOT NULL DEFAULT 0,

	PRIMARY KEY (INPUT_DOCUMENT_ID),
	FOREIGN KEY (INPUT_ID) REFERENCES task_input_parameters (INPUT_ID),
	FOREIGN KEY (SOURCE_DOCUMENT_ID) REFERENCES source_documents (SOURCE_DOCUMENT_ID)
	);


CREATE TABLE task_output_parameters ( 
	OUTPUT_ID 	  bigint(20) AUTO_INCREMENT NOT NULL,
	TASK_ID   	  bigint(20) NOT NULL,
	PARAMETER 	  varchar(255) NOT NULL,
	INTERMEDIATE  bit(1) NOT NULL DEFAULT 0,

	PRIMARY KEY (OUTPUT_ID),
	FOREIGN KEY (TASK_ID) REFERENCES tasks (TASK_ID)
	);


CREATE TABLE task_output_source_documents ( 
	OUTPUT_DOCUMENT_ID	bigint(20) AUTO_INCREMENT NOT NULL,
	OUTPUT_ID         	bigint(20) NOT NULL,
	SOURCE_DOCUMENT_ID 	bigint(20) NOT NULL,
	DATAFORMAT        	varchar(255) NOT NULL,
	DATATYPE          	varchar(255) NOT NULL,
	ENTITYTYPE        	varchar(255) NOT NULL,
	NAME              	varchar(255) NULL,
	VALIDATED         	bit(1) NOT NULL DEFAULT 0,

	PRIMARY KEY (OUTPUT_DOCUMENT_ID),
	FOREIGN KEY (OUTPUT_ID) REFERENCES task_output_parameters (OUTPUT_ID),
	FOREIGN KEY (SOURCE_DOCUMENT_ID) REFERENCES source_documents (SOURCE_DOCUMENT_ID)
	);


CREATE TABLE task_log_messages ( 
	TASK_ID       	bigint(20) NOT NULL,
	MESSAGE_INDEX 	int(11) NOT NULL,
	CREATION_DATE 	datetime NOT NULL,
	STAGE         	varchar(20) NOT NULL,
	ERROR         	bit(1) NOT NULL DEFAULT 0,
	MESSAGE       	longtext NULL,

	PRIMARY KEY (TASK_ID, MESSAGE_INDEX),
	FOREIGN KEY (TASK_ID) REFERENCES tasks (TASK_ID)
	);

/*
DELIMITER $$

CREATE TRIGGER set_task_log_message_index BEFORE INSERT ON task_log_messages
FOR EACH ROW
BEGIN
	DECLARE max_index bigint(20);
	SELECT COALESCE(MAX(MESSAGE_INDEX), 0) INTO max_index FROM task_log_messages WHERE TASK_ID = NEW.TASK_ID;
	SET NEW.MESSAGE_INDEX = max_index + 1;
END;

$$

DELIMITER ;
*/


CREATE TABLE cached_items ( 
	CACHED_ITEM_ID	bigint(20) AUTO_INCREMENT NOT NULL,
	SESSION_ID    	varchar(255) NOT NULL,
	ATTRIBUTE     	varchar(255) NOT NULL,
	COMMENT       	varchar(255) NULL,
	VERSION       	int(11) NOT NULL DEFAULT 0,
	VALUE         	longblob NULL,

	PRIMARY KEY (CACHED_ITEM_ID),
	UNIQUE KEY (SESSION_ID, ATTRIBUTE)
	);

/*	Adding this table to support iplant shibboleth login.

	Existing tables as of 1/2012 are MyISAM but the default table type is now Innodb.  I
	was getting a 105 error trying to create this table (and any other with a foreign
	key constrating) - I think because the new table was innodb and the foreign key is
	in a MyIsam table and so the name of the table needed to be qualified.  Anyhow, the
	problem goes away when I explicily make this table use the MyISAM engine.

*/
CREATE TABLE sso (
	SSO_ID			bigint(20) AUTO_INCREMENT NOT NULL,
	USER_ID         bigint(20) NOT NULL,
	SSO_USERNAME	varchar(255) NOT NULL,

	PRIMARY KEY 	(SSO_ID),
	FOREIGN KEY		(USER_ID) REFERENCES users (USER_ID),
	UNIQUE KEY		(SSO_USERNAME)
) engine = MyISAM;

/* 
	If you want to pretend it's your first time signing into cipres with an
	iplant ID (eg to test the account linking page and logic) you can just
	delete the sso record that has sso_username = iplant_login_name.
	For example:
		delete from sso where sso_username = "terri@iplantcollaborative.org"
*/


