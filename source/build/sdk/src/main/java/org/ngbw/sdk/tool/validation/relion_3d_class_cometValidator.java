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
public class relion_3d_class_cometValidator implements ParameterValidator2
{
private static final Log log = LogFactory.getLog(relion_3d_class_cometValidator.class.getName());
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
public relion_3d_class_cometValidator() {
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
allowedInput.add("reference_map_");
requiredInput.add("reference_map_");
tmpList = new ArrayList<String>();
tmpList.add("C1");
tmpList.add("Ci");
tmpList.add("Cs");
tmpList.add("C2");
tmpList.add("C2v");
tmpList.add("C2h");
tmpList.add("C3");
tmpList.add("C3v");
tmpList.add("C3h");
tmpList.add("C4");
tmpList.add("C4v");
tmpList.add("C4h");
tmpList.add("C5");
tmpList.add("C5v");
tmpList.add("C5h");
tmpList.add("C6");
tmpList.add("C6v");
tmpList.add("C6h");
tmpList.add("C7");
tmpList.add("C7v");
tmpList.add("C7h");
tmpList.add("C8");
tmpList.add("C8v");
tmpList.add("C8h");
tmpList.add("C9");
tmpList.add("C9v");
tmpList.add("C9h");
tmpList.add("C10");
tmpList.add("C10v");
tmpList.add("C10h");
tmpList.add("S1");
tmpList.add("S2");
tmpList.add("S3");
tmpList.add("S4");
tmpList.add("S5");
tmpList.add("S6");
tmpList.add("S7");
tmpList.add("S8");
tmpList.add("S9");
tmpList.add("S10");
tmpList.add("D1");
tmpList.add("D2");
tmpList.add("D3");
tmpList.add("D4");
tmpList.add("D5");
tmpList.add("D6");
tmpList.add("D7");
tmpList.add("D8");
tmpList.add("D9");
tmpList.add("D10");
tmpList.add("D11");
tmpList.add("D12");
tmpList.add("D1v");
tmpList.add("D1v");
tmpList.add("D3v");
tmpList.add("D4v");
tmpList.add("D5v");
tmpList.add("D6v");
tmpList.add("D7v");
tmpList.add("D8v");
tmpList.add("D9v");
tmpList.add("D10v");
tmpList.add("D11v");
tmpList.add("D12v");
tmpList.add("D1h");
tmpList.add("D2h");
tmpList.add("D3h");
tmpList.add("D4h");
tmpList.add("D5h");
tmpList.add("D6h");
tmpList.add("D7h");
tmpList.add("D8h");
tmpList.add("D9h");
tmpList.add("D10h");
tmpList.add("D11h");
tmpList.add("D12h");
tmpList.add("T");
tmpList.add("Td");
tmpList.add("Th");
tmpList.add("O");
tmpList.add("Oh");
tmpList.add("I1");
tmpList.add("I2");
tmpList.add("I3");
tmpList.add("I4");
tmpList.add("I5");
tmpList.add("Ih");
exclValues.put("symmetry_", tmpList);
allowedParameters.add("symmetry_");
requiredParameters.add("symmetry_");
defaultValues.put("symmetry_",
new ArrayList<String>(Arrays.asList(
"C1" )));
tmpList = new ArrayList<String>();
tmpList.add("yes");
tmpList.add("no");
exclValues.put("greyscale_", tmpList);
allowedParameters.add("greyscale_");
requiredParameters.add("greyscale_");
defaultValues.put("greyscale_",
new ArrayList<String>(Arrays.asList(
"no" )));
allowedParameters.add("low_pass_");
requiredParameters.add("low_pass_");
defaultValues.put("low_pass_", new ArrayList<String>(Arrays.asList(
"60") ));
allowedParameters.add("classes_");
requiredParameters.add("classes_");
tmpList = new ArrayList<String>();
tmpList.add("30");
tmpList.add("15");
tmpList.add("7.5");
tmpList.add("3.7");
tmpList.add("1.8");
tmpList.add("0.9");
tmpList.add("0.5");
tmpList.add("0.2");
tmpList.add("0.1");
exclValues.put("angular_sampling_", tmpList);
allowedParameters.add("angular_sampling_");
requiredParameters.add("angular_sampling_");
defaultValues.put("angular_sampling_",
new ArrayList<String>(Arrays.asList(
"7.5" )));
allowedParameters.add("search_range_");
requiredParameters.add("search_range_");
defaultValues.put("search_range_", new ArrayList<String>(Arrays.asList(
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
exclValues.put("ctf_corrected_", tmpList);
allowedParameters.add("ctf_corrected_");
requiredParameters.add("ctf_corrected_");
defaultValues.put("ctf_corrected_",
new ArrayList<String>(Arrays.asList(
"no" )));
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
"4") ));
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
tmpList = new ArrayList<String>();
tmpList.add("yes");
tmpList.add("no");
exclValues.put("angular_search_", tmpList);
allowedParameters.add("angular_search_");
requiredParameters.add("angular_search_");
defaultValues.put("angular_search_",
new ArrayList<String>(Arrays.asList(
"no" )));
allowedParameters.add("angular_range_");
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
if (parameter.equals("low_pass_")) {
if (BaseValidator.validateInteger(value) == false)
return "Must be an integer.";
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
if (parameter.equals("search_range_")) {
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
if (parameter.equals("angular_range_")) {
if (BaseValidator.validateInteger(value) == false)
return "Must be an integer.";
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