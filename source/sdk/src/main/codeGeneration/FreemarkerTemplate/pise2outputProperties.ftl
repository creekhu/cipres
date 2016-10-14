<#-- ======================== Start of Main() Code Generation ============= -->
<#compress>
<#recurse doc>	<#-- Strart recursively processing the document -->
<#macro pise>    <#-- A macro name matched with the label of the node in the doc-->
                 <#-- when this node is found execute the following commands-->
<#assign description=.node.head.description>
<#assign Toolname=.node.command>  <#-- Declaration of a varialbe Toolname-->
								<#-- .node is the current context on the imput tree
								     .node.command is its child-->
	<#recurse .node.parameters>
	@
</#macro>
</#compress>
<#-- ======================== Macros ============= -->
<#macro parameter>	
	<#if .node.@type="Paragraph">
		<#recurse .node.paragraph.parameters>
	</#if>
	
<#--  change "-" to "_" since Java does not recognize '-' as valid name 
	  change " " to "_" since space is not allowed in a Java varname 
	  add "_" to name to avoid using common methods names as parent class -->
	<#assign name = .node.name[0]?default("")>
	<#assign name = name?replace("[ |-]","_","r")+"_">	    
	
	<#if .node.@type="Results" || .node.@type="OutFile">
		<#if .node.attributes[0]?exists && .node.attributes[0].filenames[0]?exists>
			<#assign filename = .node.attributes[0].filenames[0]>
			${Toolname?upper_case}.${name}=${filename}	
		</#if>
	</#if>
</#macro> 
