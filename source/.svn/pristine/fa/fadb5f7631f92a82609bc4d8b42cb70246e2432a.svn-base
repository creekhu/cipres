3/10/2015

Changed portal code so that user is required to open and save the tool parameter gui
whenever he creates a new job or changes the job's tool.  Thus the javascript
validations that happen in the gui will always be performed.

GuiSimulator is for the REST API and simulates what the gui javascript code does.

Both start out by putting into the parameter map everything that has a default value
in the pise xml.  Each time the user changes (or adds, in the case of REST) a parameter,
it's only allowed if the field is enabled.  After the change, the code re'evals preconds
of all params, enabling and disabling them as required.

On form submission, javascript validates controls of all parameters in the pise file,
if their precond is met.  GuiSimulator does the same.

On the backend, in the action class that handles the form submission for the gui,
generated code makes sure required parameters (that don't have preconds) are present,
that int and float params are of the correct datatype and that <min>, <max>
constraints are honored.   REST api has generated code that does the same, except it 
does better required parameter validation, in that it handles required params that have preconds.

If that validation is passed, the job is put in the cipres run queue.   When cipres is ready
to submit the job it instantiates the PiseCommandRenderer.  One of the first things it does
is validateRequired() which checks that each requiredParam is present, if it's precond
is met.  This is necessary because the gui javascript only checks requiredParams that don't 
have preconds (we should fix this).

