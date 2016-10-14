<#-- ======================== Start of Main() Code Generation ============= -->
<#compress>		<#-- remove superfluous white-space -->
	<#recurse doc>		
	<#macro pise> 
		<#assign Toolname=.node.command>  
		toolId = ${Toolname?upper_case}  
	</#macro>
</#compress>

