package org.ngbw.sdk.tool.validation;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import org.ngbw.sdk.api.tool.ParameterValidator2;
import org.ngbw.sdk.api.tool.FieldError;
import org.ngbw.sdk.common.util.BaseValidator;
import org.ngbw.sdk.database.TaskInputSourceDocument;
import org.ngbw.sdk.common.util.SuperString;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
public class relion_2d_class_cometValidator implements ParameterValidator2
{
private static final Log log = LogFactory.getLog(relion_2d_class_cometValidator.class.getName());
private Map<String, List<String>> defaultValues;
private Set<String> requiredParameters;
private Set<String> requiredInput;
private Set<String> allowedParameters;
private Set<String> allowedInput;
// Map of parameter name to list separator (a single character string) for List parameters.
private Map<String, String> listParameters;
// Maps param name to permitted values for List, Excl, Switch type params
private Map<String, List<String>> listValues;
private Map<String, List<String>> exclValues ;
private Map<String, List<String>> switchValues;
private List<FieldError> errors = new ArrayList<FieldError>();
public relion_2d_class_cometValidator() {
defaultValues = new HashMap<String, List<String>>();
requiredParameters = new HashSet<String>();
requiredInput = new HashSet<String>();
allowedParameters = new HashSet<String>();
allowedInput = new HashSet<String>();
listParameters = new HashMap<String, String>();
listValues = new HashMap<String, List<String>>();
exclValues = new HashMap<String, List<String>>();
switchValues = new HashMap<String, List<String>>();
List<String> switchList = new ArrayList<String>(2);
switchList.add("0");
switchList.add("1");
List<String> tmpList;
allowedInput.add("infile_");
requiredInput.add("infile_");
allowedParameters.add("particle_diameter_");
requiredParameters.add("particle_diameter_");
allowedParameters.add("pixel_size_");
requiredParameters.add("pixel_size_");
allowedParameters.add("classes_");
requiredParameters.add("classes_");
allowedParameters.add("iterations_");
requiredParameters.add("iterations_");
defaultValues.put("iterations_", new ArrayList<String>(Arrays.asList(
"25") ));
allowedParameters.add("angular_sampling_");
requiredParameters.add("angular_sampling_");
defaultValues.put("angular_sampling_", new ArrayList<String>(Arrays.asList(
"5") ));
allowedParameters.add("pixel_range_");
requiredParameters.add("pixel_range_");
defaultValues.put("pixel_range_", new ArrayList<String>(Arrays.asList(
"5") ));
allowedParameters.add("step_size_");
requiredParameters.add("step_size_");
defaultValues.put("step_size_", new ArrayList<String>(Arrays.asList(
"1") ));
allowedParameters.add("runtime_");
requiredParameters.add("runtime_");
defaultValues.put("runtime_", new ArrayList<String>(Arrays.asList(
"12") ));
allowedParameters.add("basename_");
requiredParameters.add("basename_");
tmpList = new ArrayList<String>();
tmpList.add("yes");
tmpList.add("no");
exclValues.put("ctf_correction_", tmpList);
allowedParameters.add("ctf_correction_");
requiredParameters.add("ctf_correction_");
defaultValues.put("ctf_correction_",
new ArrayList<String>(Arrays.asList(
"yes" )));
tmpList = new ArrayList<String>();
tmpList.add("yes");
tmpList.add("no");
exclValues.put("phase_flipped_", tmpList);
allowedParameters.add("phase_flipped_");
requiredParameters.add("phase_flipped_");
defaultValues.put("phase_flipped_",
new ArrayList<String>(Arrays.asList(
"no" )));
tmpList = new ArrayList<String>();
tmpList.add("yes");
tmpList.add("no");
exclValues.put("ignore_ctfs_", tmpList);
allowedParameters.add("ignore_ctfs_");
requiredParameters.add("ignore_ctfs_");
defaultValues.put("ignore_ctfs_",
new ArrayList<String>(Arrays.asList(
"no" )));
allowedParameters.add("regularization_");
requiredParameters.add("regularization_");
defaultValues.put("regularization_", new ArrayList<String>(Arrays.asList(
"2") ));
allowedParameters.add("e_step_limit_");
requiredParameters.add("e_step_limit_");
defaultValues.put("e_step_limit_", new ArrayList<String>(Arrays.asList(
"-1") ));
tmpList = new ArrayList<String>();
tmpList.add("yes");
tmpList.add("no");
exclValues.put("image_alignment_", tmpList);
allowedParameters.add("image_alignment_");
requiredParameters.add("image_alignment_");
defaultValues.put("image_alignment_",
new ArrayList<String>(Arrays.asList(
"yes" )));
}
public List<FieldError> getErrors()
{
return errors;
}
public Map<String, String> preProcessParameters(Map<String, List<String>> parameters)
{
Map<String, String> preProcessed = new LinkedHashMap<String, String>();
for (String param : parameters.keySet())
{
String newValue;
List<String> values = parameters.get(param);
// Is param of type List?
if (listValues.keySet().contains(param))
{
for (String value : values)
{
List<String> permittedValues = listValues.get(param);
if (!permittedValues.contains(value))
{
errors.add(new FieldError(param, "'" + value + "' is not a permitted value."));
}
}
// concatenate the multiple choices.
String separator = listParameters.get(param);
if (separator != null)
{
newValue = SuperString.valueOf(parameters.get(param), separator.charAt(0)).toString();
} else
{
newValue = SuperString.valueOf(parameters.get(param), '@').toString();
}
} else
{
newValue = parameters.get(param).get(0);
if (parameters.get(param).size() > 1)
{
errors.add(new FieldError(param, "multiple values are not permitted."));
}
if (exclValues.keySet().contains(param))
{
if (!exclValues.get(param).contains(newValue))
{
errors.add(new FieldError(param, "'" + newValue + "' is not a permitted value."));
}
} else if (switchValues.keySet().contains(param))
{
if (!switchValues.get(param).contains(newValue))
{
// todo: error
errors.add(new FieldError(param, "'" + newValue + "' is not a permitted value, must be 0 or 1."));
}
}
}
preProcessed.put(param, newValue);
}
return preProcessed;
}
/*
Call this before preProcessParameters to fill in default values that the user didn't enter.
WAIT - we can't do this because the params we add might have preconds that conflict
with user entered param preconds. Let the command renderer add defaults, only if
they don't conflict. We can't do it here, in this generated code, because we aren't
generating code to eval the perl preconds.
*/
public Map<String, List<String>> addDefaultParameters(Map<String, List<String>> parameters)
{
log.debug("User submitted parameter map:");
for(String param : parameters.keySet())
{
log.debug(param + ":'" + SuperString.valueOf(parameters.get(param), ',').toString() + "'");
}
log.debug("Adding default values, where not supplied by user:");
for (String param : defaultValues.keySet())
{
if (parameters.get(param) == null)
{
parameters.put(param, defaultValues.get(param));
log.debug(param + ":" + SuperString.valueOf(defaultValues.get(param), ',').toString());
}
}
return parameters;
}
public void validateParameters(Map<String, String> parameters)
{
/*
don't want to do this until we add default params in command renderer
Set<String> missingRequired = validateRequiredParameters(parameters);
for (String missing : missingRequired)
{
errors.add(new FieldError(missing, "Parameter is required."));
}
*/
for (String param : parameters.keySet())
{
if (!allowedParameters.contains(param))
{
errors.add(new FieldError("Parameter " + param, " does not exist."));
continue;
}
String error = validate(param, parameters.get(param));
if (error != null)
{
errors.add(new FieldError(param, error));
}
}
}
public void validateInput(Set<String> input)
{
Set<String> missingRequired = validateRequiredInput(input);
for (String missing : missingRequired)
{
errors.add(new FieldError(missing, "Input file parameter is required."));
}
for (String param : input)
{
if (!allowedInput.contains(param))
{
errors.add(new FieldError("Input file parameter " + param, " does not exist."));
continue;
}
}
}
String validate(String parameter, Object value) {
if (parameter.equals("particle_diameter_")) {
if (BaseValidator.validateInteger(value) == false)
return "Must be an integer.";
if (BaseValidator.validateString(value) == false)
return "Parameter is required.";
return null;
}
if (parameter.equals("pixel_size_")) {
if (BaseValidator.validateDouble(value) == false)
return "Must be a decimal number.";
if (BaseValidator.validateString(value) == false)
return "Parameter is required.";
return null;
}
if (parameter.equals("classes_")) {
if (BaseValidator.validateInteger(value) == false)
return "Must be an integer.";
if (BaseValidator.validateString(value) == false)
return "Parameter is required.";
return null;
}
if (parameter.equals("iterations_")) {
if (BaseValidator.validateInteger(value) == false)
return "Must be an integer.";
if (BaseValidator.validateString(value) == false)
return "Parameter is required.";
return null;
}
if (parameter.equals("angular_sampling_")) {
if (BaseValidator.validateInteger(value) == false)
return "Must be an integer.";
if (BaseValidator.validateString(value) == false)
return "Parameter is required.";
return null;
}
if (parameter.equals("pixel_range_")) {
if (BaseValidator.validateInteger(value) == false)
return "Must be an integer.";
if (BaseValidator.validateString(value) == false)
return "Parameter is required.";
return null;
}
if (parameter.equals("step_size_")) {
if (BaseValidator.validateInteger(value) == false)
return "Must be an integer.";
if (BaseValidator.validateString(value) == false)
return "Parameter is required.";
return null;
}
if (parameter.equals("runtime_")) {
if (BaseValidator.validateDouble(value) == false)
return "Must be a decimal number.";
if (BaseValidator.validateString(value) == false)
return "Parameter is required.";
return null;
}
if (parameter.equals("basename_")) {
if (BaseValidator.matchesWhitelist(value) == false)
{
return("\"" + parameter + "\" contains invalid characters");
}
}
if (parameter.equals("basename_")) {
if (BaseValidator.validateString(value) == false)
return "Empty string is not allowed.";
return null;
}
if (parameter.equals("regularization_")) {
if (BaseValidator.validateInteger(value) == false)
return "Must be an integer.";
if (BaseValidator.validateString(value) == false)
return "Parameter is required.";
return null;
}
if (parameter.equals("e_step_limit_")) {
if (BaseValidator.validateInteger(value) == false)
return "Must be an integer.";
if (BaseValidator.validateString(value) == false)
return "Parameter is required.";
return null;
}
return null;
}
private Set<String> validateRequiredParameters(Map<String, String> parameters) {
Set<String> required = new HashSet<String>(requiredParameters.size());
required.addAll(requiredParameters);
for (String parameter : parameters.keySet()) {
if (required.contains(parameter))
required.remove(parameter);
}
return required;
}
private Set<String> validateRequiredInput(Map<String, List<TaskInputSourceDocument>> input) {
Set<String> required = new HashSet<String>(requiredInput.size());
required.addAll(requiredInput);
for (String parameter : input.keySet()) {
if (required.contains(parameter))
required.remove(parameter);
}
return required;
}
private Set<String> validateRequiredInput(Set<String> input) {
Set<String> required = new HashSet<String>(requiredInput.size());
required.addAll(requiredInput);
for (String parameter : input) {
if (required.contains(parameter))
required.remove(parameter);
}
return required;
}
}