Architecture:

Authentication - our code in the auth package makes sure request uses basic auth 
and that username and password are valid for an existing cipres user.  Our SecurityFilter
has a nested class, Authorizer which implements SecurityContext.  Authorizer.isUserInRole
is somehow called automatically, I believe for methods in the api (CipresJob.java) that 
use the @RolesAllowed annotation.  It checks that the username in the request header
matches the username in the url, so that someone can't just create a url with someone
else's username in it to look at their jobs.
