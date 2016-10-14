
<#-- ======================== Start of Main() Code Generation ============= -->
<#compress>		<#-- remove superfluous white-space -->
	<#recurse doc>		
	<#macro pise>
		<#assign Toolname=.node.command?upper_case>
		<#assign title=.node.head.title>
		<#if .node.head.version[0]?exists><#assign version=.node.head.version></#if>
		<#assign description=.node.head.description>
		<#assign category=.node.head.category>
		ToolLabel_CL.${Toolname}=${title}
		<#if version?exists>ToolVersion_CL.${Toolname}=${version}</#if>
		ToolDescription_CL.${Toolname}=${description}
		ToolType_CL.${Toolname}=${category}
 
<#recurse .node.parameters>
	</#macro>
</#compress>

<#-- ======================== Macros ============= -->
<#macro parameter>

	<#if .node.@type="Paragraph">
		<#recurse .node.paragraph.parameters>
	</#if>
	
	<#assign name = .node.name[0]?default("")>
	<#assign name = name?replace("[ |-]","_","r")+"_">
	<#if .node.@isinput[0]?exists && .node.@isinput[0]?contains("1")>
	ToolInput_CL.${Toolname}=${name}
	
	</#if>	    
</#macro> 
