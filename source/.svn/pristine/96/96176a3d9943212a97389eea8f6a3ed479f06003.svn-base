- tus_servlet is a generic tus file upload code.  It's also committed to github.

- tus_cipres implements a datastore (an interface defined in tus_servlet) that imports
uploaded files to the cipres db.  I put this here rather than in the sdk because it 
didn't seem right to have web servlet code in the sdk.

A cipres web app that uses tus file uploads needs to include both tus_servlet and tus_cipres
jars.  It may also need an authentication bridging class like UserBridge.java in the portal
code base.
