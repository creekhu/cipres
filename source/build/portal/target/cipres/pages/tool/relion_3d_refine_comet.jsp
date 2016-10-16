<%@ taglib prefix="s" uri="/struts-tags" %>
<title>Relion 3D refinement</title>
<h2>Relion 3D refinement: Iteratively refine 3D volume against single particles using Relion (<a href="#REFERENCE">S. H. W. Scheres</a>)</h2>
<s:form action="relion_3d_refine_comet" theme="simple">
<!-- Begin Simple Parameters -->
<a href="javascript:simple.slideit()" class="panel">Simple Parameters</a>
<div id="simple" style="width: 100%; background-color: #FFF;">
<div style="padding: 0.75em 0 0 0">
Diameter of particle (Angstroms)
<font color="red" size="3">*</font>
<s:textfield name="particle_diameter_" size="10" maxlength="600" onchange="resolveParameters()"/>
<br/>
Pixel size of data (Angstroms/pix)
<font color="red" size="3">*</font>
<s:textfield name="pixel_size_" size="10" maxlength="600" onchange="resolveParameters()"/>
<br/>
Reference map
<font color="red" size="3">*</font>
<s:select name="reference_map_" headerKey='' headerValue='' list="%{ getDataForParameter('reference_map_')}" onchange="resolveParameters()"/>
<br/>
Symmetry of sample
<font color="red" size="3">*</font>
<s:select name="symmetry_" list="#{ 'C1':'C1' , 'Ci':'Ci' , 'Cs':'Cs' , 'C2':'C2' , 'C2v':'C2v' , 'C2h':'C2h' , 'C3':'C3' , 'C3v':'C3v' , 'C3h':'C3h' , 'C4':'C4' , 'C4v':'C4v' , 'C4h':'C4h' , 'C5':'C5' , 'C5v':'C5v' , 'C5h':'C5h' , 'C6':'C6' , 'C6v':'C6v' , 'C6h':'C6h' , 'C7':'C7' , 'C7v':'C7v' , 'C7h':'C7h' , 'C8':'C8' , 'C8v':'C8v' , 'C8h':'C8h' , 'C9':'C9' , 'C9v':'C9v' , 'C9h':'C9h' , 'C10':'C10' , 'C10v':'C10v' , 'C10h':'C10h' , 'S1':'S1' , 'S2':'S2' , 'S3':'S3' , 'S4':'S4' , 'S5':'S5' , 'S6':'S6' , 'S7':'S7' , 'S8':'S8' , 'S9':'S9' , 'S10':'S10' , 'D1':'D1' , 'D2':'D2' , 'D3':'D3' , 'D4':'D4' , 'D5':'D5' , 'D6':'D6' , 'D7':'D7' , 'D8':'D8' , 'D9':'D9' , 'D10':'D10' , 'D11':'D11' , 'D12':'D12' , 'D1v':'D2v' , 'D1v':'D2v' , 'D3v':'D3v' , 'D4v':'D4v' , 'D5v':'D5v' , 'D6v':'D6v' , 'D7v':'D7v' , 'D8v':'D8v' , 'D9v':'D9v' , 'D10v':'D10v' , 'D11v':'D11v' , 'D12v':'D12v' , 'D1h':'D1h' , 'D2h':'D2h' , 'D3h':'D3h' , 'D4h':'D4h' , 'D5h':'D5h' , 'D6h':'D6h' , 'D7h':'D7h' , 'D8h':'D8h' , 'D9h':'D9h' , 'D10h':'D10h' , 'D11h':'D11h' , 'D12h':'D12h' , 'T':'T' , 'Td':'Td' , 'Th':'Th' , 'O':'O' , 'Oh':'Oh' , 'I1':'I1' , 'I2':'I2' , 'I3':'I3' , 'I4':'I4' , 'I5':'I5' , 'Ih':'Ih' }" onchange="resolveParameters()"/>
<br/>
<br/>
Reference map is on absolute greyscale
<font color="red" size="3">*</font>
<s:radio name="greyscale_"
list="#{ 'yes':'Yes' , 'no':'No' }" onclick="resolveParameters()"/>
<br/>
Initial low-pass filter (Angstrom)
<font color="red" size="3">*</font>
<s:textfield name="low_pass_" size="10" maxlength="600" onchange="resolveParameters()"/>
<br/>
<A HREF="javascript:help.slidedownandjump('#runtime')">Maximum Relion runtime (hours)</A>
<font color="red" size="3">*</font>
<s:textfield name="runtime_" size="10" maxlength="600" onchange="resolveParameters()"/>
<br/>
<A HREF="javascript:help.slidedownandjump('#basename')">Base name to use for the output file</A>
<font color="red" size="3">*</font>
<s:textfield name="basename_" size="10" maxlength="600" onchange="resolveParameters()"/>
<br/>
</div>
</div>
<script type="text/javascript">
var simple=new animatedcollapse("simple", 800, false, "block")
</script>
<!--End Simple Parameters -->
<br/><hr/><br/>
<!--Begin Advanced Parameters -->
<a href="javascript:advanced.slideit()" class="panel">Advanced Parameters</a>
<div id="advanced" style="width: 100%; background-color: #FFF;">
<div style="padding: 0.75em 0 0 0">
Angular sampling interval (degrees)
<font color="red" size="3">*</font>
<s:textfield name="angular_sampling_" size="10" maxlength="600" onchange="resolveParameters()"/>
<br/>
Pixel search range (pixels)
<font color="red" size="3">*</font>
<s:textfield name="search_range_" size="10" maxlength="600" onchange="resolveParameters()"/>
<br/>
Pixel search range step size (pixels)
<font color="red" size="3">*</font>
<s:textfield name="step_size_" size="10" maxlength="600" onchange="resolveParameters()"/>
<br/>
Do CTF correction?
<font color="red" size="3">*</font>
<s:radio name="ctf_correction_"
list="#{ 'yes':'Yes' , 'no':'No' }" onclick="resolveParameters()"/>
<br/>
Has reference been CTF-corrected
<font color="red" size="3">*</font>
<s:radio name="ctf_corrected_"
list="#{ 'yes':'Yes' , 'no':'No' }" onclick="resolveParameters()"/>
<br/>
Have data been phase-flipped
<font color="red" size="3">*</font>
<s:radio name="phase_flipped_"
list="#{ 'yes':'Yes' , 'no':'No' }" onclick="resolveParameters()"/>
<br/>
Ignore CTFs until first peak
<font color="red" size="3">*</font>
<s:radio name="ignore_ctfs_"
list="#{ 'yes':'Yes' , 'no':'No' }" onclick="resolveParameters()"/>
<br/>
Regularization parameter
<font color="red" size="3">*</font>
<s:textfield name="regularization_" size="10" maxlength="600" onchange="resolveParameters()"/>
<br/>
Limit resolution E-step to (Angstroms)
<font color="red" size="3">*</font>
<s:textfield name="e_step_limit_" size="10" maxlength="600" onchange="resolveParameters()"/>
<br/>
Perform image alignment
<font color="red" size="3">*</font>
<s:radio name="image_alignment_"
list="#{ 'yes':'Yes' , 'no':'No' }" onclick="resolveParameters()"/>
<br/>
Perform local angular searches
<font color="red" size="3">*</font>
<s:radio name="angular_search_"
list="#{ 'yes':'Yes' , 'no':'No' }" onclick="resolveParameters()"/>
<br/>
Local angular search range (degrees)
<s:textfield name="angular_range_" size="10" maxlength="600" onchange="resolveParameters()"/>
<br/>
</div>
</div>
<script type="text/javascript">
var advanced=new animatedcollapse("advanced", 800, true)
</script>
<!--End Advanced Parameters -->
<br/><hr/><br/>
<s:submit value="Save Parameters" onclick="return validateControl()"/>
<s:submit value="Reset" method="resetPage"/>
<s:submit value="Cancel" method="cancel"/>
<hr></hr>
<!--Begin Advanced Help -->
<a href="javascript:help.slideit()" class="panel">Advanced Help</a>
<div id="help" style="width: 100%; background-color: #FFF;">
<div style="padding: 0.75em 0 0 0">
<dt><a name=runtime><i>Maximum Relion runtime (hours)</i></a></dt>
<dd>
Estimate the maximum time your job will need to run (up to 48 hrs). Your job will be killed if it doesn't finish within the time you specify, however jobs with shorter maximum run times are often scheduled sooner than longer jobs.
</dd>
<dt><a name=basename><i>Base name to use for the output file</i></a></dt>
<dd>This value determines the name of output files.</dd>
</div>
</div>
<script type="text/javascript">
var help=new animatedcollapse("help", 800, true)
</script>
<!--End Advanced Help -->
</s:form>
<script type="text/javascript">
function resolveParameters() {
// particle_diameter
// pixel_size
// reference_map
// symmetry
// greyscale
// low_pass
// runtime
// basename
// angular_sampling
// search_range
// step_size
// ctf_correction
// ctf_corrected
// phase_flipped
// ignore_ctfs
// regularization
// e_step_limit
// image_alignment
// angular_search
// angular_range
}
function validateControl() {
// particle_diameter
if (getValue('particle_diameter_') < 1) {
alert('Please enter an integer > 0 for the particle diameter (in Angstroms)');
return false;
}
// pixel_size
if (getValue('pixel_size_') < 0.01) {
alert('Please enter a float > 0.00 for the pixel size (in Angstroms/pix)');
return false;
}
// reference_map
if (!getValue('reference_map_')) {
alert('Please choose a reference map file');
return false;
}
// symmetry
// greyscale
// low_pass
if (getValue('low_pass_') < 1) {
alert('Please enter an integer > 0 for the initial low-pass filter (in Angstroms)');
return false;
}
// runtime
if (getValue('runtime_') < 0.1 || getValue('runtime_') > 48.0) {
alert('Please enter a float between 0.1 and 48.0, inclusively, for the maximum run time (in hours)');
return false;
}
// basename
if (!getValue('basename_')) {
alert('Please enter a string for the base name of the output file');
return false;
}
// angular_sampling
if (getValue('angular_sampling_') < 0.01) {
alert('Please enter a float > 0.00 for the angular sampling interval (in degrees)');
return false;
}
// search_range
if (getValue('search_range_') < 1) {
alert('Please enter an integer > 0 for pixel search range (in pixels)');
return false;
}
// step_size
if (getValue('step_size_') < 1) {
alert('Please enter an integer > 0 for pixel search range step size (in pixels)');
return false;
}
// ctf_correction
// ctf_corrected
// phase_flipped
// ignore_ctfs
// regularization
if (getValue('regularization_') < 1 || getValue('regularization_') > 4) {
alert('Please enter an integer between 1 and 4, inclusively, for the regularization parameter');
return false;
}
// e_step_limit
if (getValue('e_step_limit_') < -1 || getValue('e_step_limit_') == 0) {
alert('Please enter a non-zero integer > -1 for E-step resolution limit (in Angstroms)');
return false;
}
// image_alignment
// angular_search
// angular_range
if ( getValue('angular_range_') && getValue('angular_range_') < 1) {
alert('Please enter an integer > 0 for local angular search range (in degrees)');
return false;
}
return issueWarning();
}
function issueWarning() {
// particle_diameter
// pixel_size
// reference_map
// symmetry
// greyscale
// low_pass
// runtime
// basename
// angular_sampling
// search_range
// step_size
// ctf_correction
// ctf_corrected
// phase_flipped
// ignore_ctfs
// regularization
// e_step_limit
// image_alignment
// angular_search
// angular_range
addSpecialField();
return true;
}
function messageSplit(str)
{
var tokens = str.split(" ");
var newStr = ""
var tmp;
for (i = 0; i < tokens.length; i++)
{
if ((tokens[i].indexOf("getValue(") == 0))
{
tmp = tokens[i];
var tmp1, tmp2;
var closeParen = tmp.indexOf(")");
tmp1 = tmp.substring(0, closeParen + 1);
if ((closeParen + 1) == tmp.length)
{
tmp = tmp1 + " + ' '";
} else
{
tmp2=tmp.substring(closeParen + 1);
tmp = tmp1 + " + '" + tmp2 + "'";
tmp = tmp + " + ' '";
}
} else
{
tmp = "'" + tokens[i] + " '";
}
if (newStr.length > 0)
{
newStr = newStr + " + " + tmp;
} else
{
newStr = tmp;
}
}
return eval(newStr);
}
// For multiple selection list, pise "List" type, this only returns the 1st value selected.
// To get all values you need to get all element[i].value where element[i].selected = true.
function getValue(parameter)
{
var element = document.forms['relion_3d_refine_comet'].elements[parameter];
if (element == null)
{
return null;
}
if (isDisabled(parameter))
{
if (element.type == 'checkbox')
{
return false;
}
return "";
}
else if (element.length != null)
{
// if the element has a value, it's a drop-down list
if (element.value != null)
{
return element.value;
} else
{
for (i=0; element.length>i; i++)
{
if (element[i].checked)
{
return element[i].value;
}
}
return null;
}
} else if (element.type == 'checkbox')
{
return element.checked;
} else
{
return element.value;
}
}
function enable(parameter)
{
var element = document.forms['relion_3d_refine_comet'].elements[parameter];
if (element == null)
{
return;
}
if (element.length != null)
{
for (i=0; element.length>i; i++)
{
element[i].disabled = false;
}
}
element.disabled = false;
}
function disable(parameter)
{
var element = document.forms['relion_3d_refine_comet'].elements[parameter];
if (element == null)
{
return;
}
if (element.length != null)
{
for (i=0; element.length>i; i++)
{
element[i].disabled = true;
}
}
element.disabled = true;
}
function isDisabled(parameter)
{
var element = document.forms['relion_3d_refine_comet'].elements[parameter];
if (element == null)
{
return true;
}
// radio button is special case
if (element.length != null )
{
return element[0].disabled;
}
return element.disabled;
}
function allDisabledFields()
{
var str = '';
var element = document.forms['relion_3d_refine_comet'].elements;
for(var i = 0; i < element.length; i++)
{
if (isDisabled(element[i].name))
{
str += element[i].name + ",";
}
}
return str;
}
function addSpecialField()
{
var str = allDisabledFields();
var input = document.createElement('input');
input.type = 'hidden';
input.name = 'disabledFields__';
input.value = str;
document.forms['relion_3d_refine_comet'].appendChild(input);
}
</script>