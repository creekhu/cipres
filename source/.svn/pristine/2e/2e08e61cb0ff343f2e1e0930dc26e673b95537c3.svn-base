WARNING: this is an old example, developed before the cipres_java_client was 
existed.  Please use the cipres_java_client instead.





Example jersey and struts based web application that talks to the CIPRES REST API
using an UMBRELLA authentication model.  

Please read the documentation at https://www.phylo.org/restusers/docs/guide.html to make sure 
you need to use UMBRELLA authentication in the application you're developing.  If you do, you will 
need to register an UMBRELLA Application (and wait for us to review your request and activate the 
application ID).  You will need an UMBRELLA application ID in order to run this example. 

To build and run this example:

1. You must have maven and a java sdk installed.  The example was tested with maven 3.2.3
and java 1.7.   

2. Before building this example, build the restdatatypes jar.  
	$ svn export https://svn.sdsc.edu/repo/scigap/trunk/rest/datatypes datatypes
	$ cd datatypes
	$ mvn clean install 

Be sure to get the datatypes source code from the same release that you get the java_umbrella
example from.  For example, if you get java_umbrella from the trunk, then the url shown above
would be fine, but if you get java_umbrella from a branch, such as 
https://svn.sdsc.edu/repo/scigap/branches/rest-R4/rest_client_examples/examples/java_umbrella
then get datatypes from the same branch, i.e. https://svn.sdsc.edu/repo/scigap/branches/rest-R4/rest/datatypes.

3. Register for a CIPRES Rest account at http://www.phylo.org/restusers then 
login and go to the "Developers" menu and choose "Application Management".   Press 
"Create New Application" and fill in the form to register a new application that uses 
UMBRELLA Authentication.   

4. Copy pycipres.conf to your home directory and edit it, filling in the values with your username, 
password, application name and application ID.  (Or put pycipres.conf elsewhere on disk and set
an environment variable named PYCIPRES to its full pathname).

5. To build the application, run "mvn package" from this directory.

6. To run the application, run "mvn jetty:run".

7. To use the application, navigate to 
"http://localhost:9090/java_umbrella/login.jsp" in a browser.
