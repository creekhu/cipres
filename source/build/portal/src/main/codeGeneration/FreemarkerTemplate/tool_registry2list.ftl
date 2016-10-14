<#-- 
	Generates a list of the tools in the tool registry. 
	Lists the pise xml filenames of the tools.
-->
<#-- ======================== Start of Main() Code Generation ============= -->
<#compress>
<#recurse doc>	<#-- Strart recursively processing the document -->

<#macro ToolRegistry>    
	<#recurse .node.ToolGroups>	
</#macro>    

<#macro ToolGroups>	
	<#recurse .node.ToolGroup>	
</#macro>    

<#macro ToolGroup>    
	<#recurse >	
</#macro>    

<#macro Tool>    
	<#assign configfile=.node.@configfile> 
	<#if .node.@configfile  != "null">
		${configfile}
	</#if>
</#macro>

</#compress>
<#macro @element></#macro>   
