Before an application can submit jobs to the rest service, we create an application record for it.
Each request (except those coming from a cipresadmin) must have the following headers:
	cipres-appkey
	cipres-eu
	cipes-eu-email
	cipres-eu-institution
We form the actual username by concatenating the application's name (found by looking up the cipres-appkey)
with a period and then the cipres-eu.

Application record has:
	NAME aka appname	
		primary key, letters numbers and underscores only.  
	APP_ID 
		uuid, to be including in header of each request as value of cipres-appkey.  
		If it gets compromised we can assign a new value since it isn't referenced in any other tables.
	LONGNAME 
		Descriptive name for the application.

	AUTH_TYPE = UMBRELLA or DIRECT 
	AUTH_USER_ID 
		Only used for UMBRELLA.  Refers to the account who's username and password will be used. 

	# PRIMARY CONTACT INFO FOR APPLICATION DEVELOPER OR ADMIN
	FIRST_NAME varchar(100) NOT NULL,
	LAST_NAME varchar(100) NOT NULL,
	INSTITUTION varchar(255) DEFAULT NULL,
	STREET_ADDRESS varchar(255) DEFAULT NULL,
	CITY varchar(100) DEFAULT NULL,
	STATE varchar(50) DEFAULT NULL,
	COUNTRY varchar(50) DEFAULT NULL,
	MAILCODE varchar(10) DEFAULT NULL,
	ZIP_CODE varchar(10) DEFAULT NULL,
	AREA_CODE varchar(10) DEFAULT NULL,
	PHONE_NUMBER varchar(20) DEFAULT NULL,
	EMAIL varchar(200) NOT NULL,
	WEBSITE_URL varchar(255) DEFAULT NULL,
	COMMENT varchar(255) DEFAULT NULL,

	ACTIVE 
		Used to disable requests by this application

	PRIMARY KEY (APP_ID),
	UNIQUE KEY NAME (NAME)


For umbrella application accounts, we 

	1) validate username and password from basic auth request headers against our user db.  Hang
	onto the user record.
	
	2) Look up Application record by looking for Application.app_id = cipres-appkey. Get the name
	from that record. Aka appname. 
	
	3) Make sure that the user record from step 1 matches the application's auth_usr_id.  

	4) look up appname.cipres-euser, and if it doesn't exist we add a new user record with
	username = appname.cipres-euser, password = some static text + timestamp (not meant to
	ever be used).  Once we've got our end user record, it will be our effective user and must 
	be used in the urls.*
	
Note that email address doesn't need to be unique across all users, just all users associated
an application.  A user can use multiple apps with the same email address.  

For direct application accounts, each user of the application must create a cipres user record
for himself.  We'll require that the username have the form appname.username. 

User records created for the REST service, both those for umbrella account auth_users and direct
account end users will be limited to alphanumerics and a few punctation marks,but not colon
and periods.  Colons make basic auth parsing impossible.  The username will always have the form
appname.desiredusername.  I left username as a unique key (instead of making the unique key be
appname, username) because it meant fewer changes to the existing cipres portal.  However, I'm 
not sure why I decided that direct accounts needed to include appname in the username.  Maybe
to avoid clashes with cipres portal users if we ever want to run on the same db?


Accounts that are created on the fly when an unknown cipres-eu header is sent by an umbrella application, 
can have virtually any ascii characters.

In all cases we will have to reject usernames that have single quotes since that would make escaping them to
pass to shell commands like deleteUser.py to difficult.
