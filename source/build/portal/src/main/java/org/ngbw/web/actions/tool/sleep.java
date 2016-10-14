package org.ngbw.web.actions.tool;
import org.ngbw.sdk.common.util.SuperString;
import org.ngbw.web.actions.ToolParameters;
@SuppressWarnings("serial")
public class sleep extends ToolParameters
{
boolean inReset = false;
public String getSeconds_()
{
return getUIParameters().get("seconds_");
}
public void setSeconds_(String seconds_)
{
if (inReset == false)
setToolParameter("seconds_", seconds_);
getUIParameters().put("seconds_", seconds_);
}
// file-typed property getters and setters
public Long getInfile_()
{
return getInputDataItemId("infile_");
}
public void setInfile_(Long infile_)
{
setInputDataItem("infile_",infile_);
}
// this method pre-populates the form with default values
public void reset()
{
super.reset();
try
{
inReset = true;
setSeconds_("10");
}
finally
{
inReset = false;
}
}
}
