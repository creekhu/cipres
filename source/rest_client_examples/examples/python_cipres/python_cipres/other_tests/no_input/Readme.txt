
With no input parameters python requests library does not send multpart form data
but url encoded data, so jersey gives a 415 unsupported media type error.

Unfortunately there's no way to test a multipart form post without a file
using the python requests library.
