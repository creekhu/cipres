<#-- ======================== Start of Main() Code Generation ============= -->
<#compress>		<#-- remove superfluous white-space -->
	<#recurse doc>		
	<#macro pise> 
		<#assign Toolname=.node.command>  
		${Toolname?upper_case}  
		<#recurse .node.parameters>
	</#macro>
</#compress>


<#macro parameters><#recurse></#macro>
<#macro parameter>

<#--  change "-" to "_" since Java does not recognize '-' as valid name 
	  change " " to "_" since space is not allowed in a Java varname 
	  add "_" to name to avoid using common methods names as parent class -->
<#assign name = .node.name[0]?default("")>
<#assign name = name?replace("[ |-]","_","r")+"_">
<#assign nameCap = name?cap_first>
<#assign type=.node.@type>
	
<#-- default list -->
<#assign defaults =.node.attributes.vdef.value>
<#-- for most field, there's only one default value: first item -->
<#assign default = defaults[0]?default("")>
<#-- get rid of first and last " if there's one in default value -->
<#assign default = default?replace("\"",'','f')>
<#assign default = default?replace("\"",'','l')>
<#assign size = defaults?size>
<#assign prompt = .node.attributes.prompt[0]?default("")>

	<#if .node.@isinput[0]?exists && .node.@isinput[0]?contains("1")>
		${name} - Primary Input File
		<#return>
	</#if>
				
	<#switch type>

	<#case "Paragraph">
		Section: ****************************************************************************
		${.node.paragraph.name}
		*************************************************************************************
		  <#recurse .node.paragraph.parameters> 
		<#break>
		
		<#case "OutFile">		
		<#case "Label">
		<#case "Results">
		<#case "Float"> 	
		<#case "Integer">
		<#case "Switch">
		<#case "Sequence">
		<#case "Excl">  
		<#case "String">
		<#case "InFile">
		<#case "List">	
			<#if .node.@ishidden[0]?exists && .node.@ishidden[0]?contains("1")><#break></#if>
			${name} - ${type} -  ${prompt}
			<#break> 
	</#switch>	
</#macro>


