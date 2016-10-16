package org.ngbw.web.actions.tool;
import org.ngbw.sdk.common.util.SuperString;
import org.ngbw.web.actions.ToolParameters;
@SuppressWarnings("serial")
public class relion_3d_refine_comet extends ToolParameters
{
boolean inReset = false;
// file-typed property getters and setters
public Long getInfile_()
{
return getInputDataItemId("infile_");
}
public void setInfile_(Long infile_)
{
setInputDataItem("infile_",infile_);
}
public String getParticle_diameter_()
{
return getUIParameters().get("particle_diameter_");
}
public void setParticle_diameter_(String particle_diameter_)
{
if (inReset == false)
setToolParameter("particle_diameter_", particle_diameter_);
getUIParameters().put("particle_diameter_", particle_diameter_);
}
public String getPixel_size_()
{
return getUIParameters().get("pixel_size_");
}
public void setPixel_size_(String pixel_size_)
{
if (inReset == false)
setToolParameter("pixel_size_", pixel_size_);
getUIParameters().put("pixel_size_", pixel_size_);
}
// file-typed property getters and setters
public Long getReference_map_()
{
return getInputDataItemId("reference_map_");
}
public void setReference_map_(Long reference_map_)
{
setInputDataItem("reference_map_",reference_map_);
}
public String getSymmetry_()
{
return getUIParameters().get("symmetry_");
}
public void setSymmetry_(String symmetry_)
{
if (inReset == false)
setToolParameter("symmetry_", symmetry_);
getUIParameters().put("symmetry_", symmetry_);
}
public String getGreyscale_()
{
return getUIParameters().get("greyscale_");
}
public void setGreyscale_(String greyscale_)
{
if (inReset == false)
setToolParameter("greyscale_", greyscale_);
getUIParameters().put("greyscale_", greyscale_);
}
public String getLow_pass_()
{
return getUIParameters().get("low_pass_");
}
public void setLow_pass_(String low_pass_)
{
if (inReset == false)
setToolParameter("low_pass_", low_pass_);
getUIParameters().put("low_pass_", low_pass_);
}
public String getRuntime_()
{
return getUIParameters().get("runtime_");
}
public void setRuntime_(String runtime_)
{
if (inReset == false)
setToolParameter("runtime_", runtime_);
getUIParameters().put("runtime_", runtime_);
}
public String getBasename_()
{
return getUIParameters().get("basename_");
}
public void setBasename_(String basename_)
{
if (inReset == false)
setToolParameter("basename_", basename_);
getUIParameters().put("basename_", basename_);
}
public String getAngular_sampling_()
{
return getUIParameters().get("angular_sampling_");
}
public void setAngular_sampling_(String angular_sampling_)
{
if (inReset == false)
setToolParameter("angular_sampling_", angular_sampling_);
getUIParameters().put("angular_sampling_", angular_sampling_);
}
public String getSearch_range_()
{
return getUIParameters().get("search_range_");
}
public void setSearch_range_(String search_range_)
{
if (inReset == false)
setToolParameter("search_range_", search_range_);
getUIParameters().put("search_range_", search_range_);
}
public String getStep_size_()
{
return getUIParameters().get("step_size_");
}
public void setStep_size_(String step_size_)
{
if (inReset == false)
setToolParameter("step_size_", step_size_);
getUIParameters().put("step_size_", step_size_);
}
public String getCtf_correction_()
{
return getUIParameters().get("ctf_correction_");
}
public void setCtf_correction_(String ctf_correction_)
{
if (inReset == false)
setToolParameter("ctf_correction_", ctf_correction_);
getUIParameters().put("ctf_correction_", ctf_correction_);
}
public String getCtf_corrected_()
{
return getUIParameters().get("ctf_corrected_");
}
public void setCtf_corrected_(String ctf_corrected_)
{
if (inReset == false)
setToolParameter("ctf_corrected_", ctf_corrected_);
getUIParameters().put("ctf_corrected_", ctf_corrected_);
}
public String getPhase_flipped_()
{
return getUIParameters().get("phase_flipped_");
}
public void setPhase_flipped_(String phase_flipped_)
{
if (inReset == false)
setToolParameter("phase_flipped_", phase_flipped_);
getUIParameters().put("phase_flipped_", phase_flipped_);
}
public String getIgnore_ctfs_()
{
return getUIParameters().get("ignore_ctfs_");
}
public void setIgnore_ctfs_(String ignore_ctfs_)
{
if (inReset == false)
setToolParameter("ignore_ctfs_", ignore_ctfs_);
getUIParameters().put("ignore_ctfs_", ignore_ctfs_);
}
public String getRegularization_()
{
return getUIParameters().get("regularization_");
}
public void setRegularization_(String regularization_)
{
if (inReset == false)
setToolParameter("regularization_", regularization_);
getUIParameters().put("regularization_", regularization_);
}
public String getE_step_limit_()
{
return getUIParameters().get("e_step_limit_");
}
public void setE_step_limit_(String e_step_limit_)
{
if (inReset == false)
setToolParameter("e_step_limit_", e_step_limit_);
getUIParameters().put("e_step_limit_", e_step_limit_);
}
public String getImage_alignment_()
{
return getUIParameters().get("image_alignment_");
}
public void setImage_alignment_(String image_alignment_)
{
if (inReset == false)
setToolParameter("image_alignment_", image_alignment_);
getUIParameters().put("image_alignment_", image_alignment_);
}
public String getAngular_search_()
{
return getUIParameters().get("angular_search_");
}
public void setAngular_search_(String angular_search_)
{
if (inReset == false)
setToolParameter("angular_search_", angular_search_);
getUIParameters().put("angular_search_", angular_search_);
}
public String getAngular_range_()
{
return getUIParameters().get("angular_range_");
}
public void setAngular_range_(String angular_range_)
{
if (inReset == false)
setToolParameter("angular_range_", angular_range_);
getUIParameters().put("angular_range_", angular_range_);
}
// this method pre-populates the form with default values
public void reset()
{
super.reset();
try
{
inReset = true;
setSymmetry_( "C1" );
setGreyscale_( "no" );
setLow_pass_("60");
setRuntime_("12");
setAngular_sampling_("7.5");
setSearch_range_("5");
setStep_size_("1");
setCtf_correction_( "yes" );
setCtf_corrected_( "no" );
setPhase_flipped_( "no" );
setIgnore_ctfs_( "no" );
setRegularization_("4");
setE_step_limit_("-1");
setImage_alignment_( "yes" );
setAngular_search_( "no" );
}
finally
{
inReset = false;
}
}
}
