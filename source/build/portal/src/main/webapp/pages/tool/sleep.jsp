<%@ taglib prefix="s" uri="/struts-tags" %>
<title>Sleep</title>
<h2>Sleep: Should not be in production. For testing sshprocessworker. </h2>
<s:form action="sleep" theme="simple">
<!-- Begin Simple Parameters -->
<a href="javascript:simple.slideit()" class="panel">Simple Parameters</a>
<div id="simple" style="width: 100%; background-color: #FFF;">
<div style="padding: 0.75em 0 0 0">
Number of seconds to sleep
<font color="red" size="3">*</font>
<s:textfield name="seconds_" size="10" maxlength="600" onchange="resolveParameters()"/>
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
</div>
</div>
<script type="text/javascript">
var help=new animatedcollapse("help", 800, true)
</script>
<!--End Advanced Help -->
</s:form>
<script type="text/javascript">
function resolveParameters() {
// seconds
}
function validateControl() {
// seconds
return issueWarning();
}
function issueWarning() {
// seconds
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
var element = document.forms['sleep'].elements[parameter];
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
var element = document.forms['sleep'].elements[parameter];
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
var element = document.forms['sleep'].elements[parameter];
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
var element = document.forms['sleep'].elements[parameter];
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
var element = document.forms['sleep'].elements;
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
document.forms['sleep'].appendChild(input);
}
</script>