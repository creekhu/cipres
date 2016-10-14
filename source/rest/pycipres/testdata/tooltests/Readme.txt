Tests:

minimal_params - submit job with *no* parameters in most cases.  Some tools, like the 'restart'
ones, have no logical default for some parameters.  In those cases, we specify only those params.

The minimal_params dirs were generated automatically with the src/main/codeGeneration
ant "Test" target.  Empty dummy input files were added manually.  In cases where params
must be given, the .properties files were manually editted - this was easy enough to do
based on looking at the error messages.

variants - Manually created test directories to test with different combinations of
parameters.  

should_fail - These are tests that should give ValidationExceptions, unlike the other
tests, all of which should succeed.  Purpose is to test our parameter validation.

The tooltests directly in this directory, like BEAST_TG, READSEQ, etc don't work.  I'm not
sure how I came up with them.  Maybe I used them before I implemented validations.

