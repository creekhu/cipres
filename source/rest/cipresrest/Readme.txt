Since we're still using tomcat 5.x for cipres app and I think we need tomcat 6 for jersey
I source ~/.rest when I'm going to work on this project. 

I used maven 2, java 6, tomcat 6 

1. Run "mvn clean package"
2. Copy target/cipresrest.war to tomcat
3. Point a browser at http://localhost:8080/cipresrest/world and you should see the text "hello hello" on 
the page.

I based this on the example at 
	http://naptimecoder.blogspot.com/2012/03/how-to-create-web-service-using-jersey.html
I had to make a few minor changes to get it to build but it worked immediately once built.


I used a mvn archetype to create the basic pom and directory structure.
The only files I editted or created were:
	- pom.xml	
	- web.xml
	- HelloWorld.java


* The first part of the url, "cipresrest" I believe is the name of the .war file.

* The path beyond that is specified with @PATH directives in the resource classes (e.g.
HelloWorld.java).

LIFECYCLE:
	In Jersey, I believe the resource and filter objects are instaniated per request.
	I guess, for app singletons, like db connections and reading app config files into memory 
	you use singletons or a jersey application object?


BASIC AUTHENTICATION

* Apparently there are a lot of ways to implement security with jersey.  There are even
multiple ways to implement basic auth using only jersey.  

    - you can use a plain old servlet filter, but you wouldn't be able to use any 
	jersey code or annotations in the filter, I don't think.

	- you can use a jersey ContainerRequestFilter as I did, but I think the filter is applied
	to the entire application.

	- apparently you can also use a container.ResourceFilter, probably for finer grained 
	control.  atom-pub example may use this?

* Using spring security with jersey seems very popular.

To get my basic auth with a ContainerRequestFilter working, here's what I had to do:

- Copied SecurityFilter.java, AuthenticationException.java and AuthenticationMapper.java
from the jersey 1.16 https-clientserver-grizzly sample.  Returns an error response
if credentials not present or invalid, otherwise sets a SecurityContext in the request.

- But how to get that SecurityContext, and hence the username and role, in the
HelloWorld resource?  I did it by declaring a class variable " @Context SecurityContext context; "
and then context.getUserPrincipal().getName().

- And how to hook it up and install the filter?   In web.xml, simply add the init-param:o
<init-param>
	<param-name>com.sun.jersey.spi.container.ContainerRequestFilters</param-name>
	<param-value>org.ngbw.cipresrest.auth.SecurityFilter</param-value>
</init-param>



ECLIPSE NOTES:
Assuming maven remains the primary build mechnanism, I’d like to be able to create an eclipse 
project from the maven pom.xml and run the webapp in eclipse when needed.   Here’s what I had 
to do (using eclipse juno, java 1.6, tomcat 6):

	* copy whole project (cipresrest) to a new directory just to be safe
	* make sure the web.xml has no attributes in the <web-app> element.  Eclipse xml validation seems 
		to have a bug and will complain about the attributes if present.
	* launch eclipse: file/import/maven/existing maven projects, browse to and choose the directory 
		that contains the pom.xml, finish.
	* rt click the project name (cipresrest) in project explorer panel.   Choose properties/Project Facets.  
		Click “convert to faceted form” link.
	* Check “Dynamic Web Module” and change from version 3 to 2.5.
		“Further configuration available …” - don’t generate web.xml
		Over to the right where Project Facets shows Details/Runtimes, switch to Runtimes and choose 
		the apache server (or add a new one if needed).  Click Apply, then Ok.
	* Rt click the project in Project Explorer/Properties/Deployment Assembly.  You can remove 
		src/test/java and webcontent.   You need to add the folder src/main/webapp at path “/”.
	* Still in the Deployment Assembly page, click Add, then Java Build Path Entries, choose Maven Dependencies 
		and Finish.  You can expand the Maven Dependencies to see which jars are going to be copied to WEB-INF/lib 
		of your war file.   It doesn’t seem that you can pick and choose though, seems to be all or nothing.
	* Now try it: rt click the project, Run As, Run on Server, finish.

You may need to add the server.  If so, File/New/Other/Server … then fill it out and indicate that the 
project (cipresrest) is to be deployed on the server.

Every time you change pom.xml, you need to remove the project from the eclipse workspace (rt click, delete) and re-add
it, as described above.  It's the only way I found to get the changes to be picked up.
