Terri 9/26/2016: For large file uploads I wanted to be able to move a file
from it's temporary upload location into a UserDataItem without actually
moving the data.  For this to happen the tmp upload location needs to be
configured to be on the same filesystem as the database document storage.
The database also needs to be configured not to compress data.  Paul added
a UserDataItem ctor to that takes the pathname of a file to import and
UserDataItem.setData(File file) method.  I asked Paul to explain when
the source file would be moved vs copied and whether compression always
copies the data and he said this:

	All the code is in FileColumn.java . If the compression flag is set, the compressed 
	data from fileName is written to a temporary file, so it's effectively copied. The 
	temporary file is moved to its final location via File.renameTo() while the 
	update/insert statement is being prepared. If the compression flag is not set, then 
	the constructor marks fileName as the temporary file, so it's just moved. The vdjserver 
	people told me that their inputs average about 10GB, so I'm definitely using compression 
	for them. I assume that compression won't take too long, compared to upload time, but I'd 
	still use a separate thread/thread pool to do it.

Compression in the db is controlled by a build property, "database.useFileCompression"
and set in SourceDocumentRow.java.

Note that if you use the UserDataItem(String pathname, ...) ctor, the source file is
moved when the ctor calls setData(pathname).  If the save() of the user data item
fails, the file has already been moved and is effectively lost.


