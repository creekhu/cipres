
<#-- ======================== Start of Main() Code Generation ============= -->
<#compress>		<#-- remove superfluous white-space -->
	<#recurse doc>		
	<#macro pise> 
		<#assign Toolname=.node.command>  
#	 ${Toolname?upper_case}  
#
# 
<#recurse .node.parameters>
	</#macro>
#
#
#	
</#compress>


<#macro parameters><#recurse></#macro>
  <#-- recursively process the nodes, the  macro that 
       matches the label of the node is 'parameter' -->

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
			
<#-- skip parameter when "isinput" is "1" -->
	<#if .node.@isinput[0]?exists && .node.@isinput[0]?contains("1")><#return></#if>
				
	<#-- process parameter node according to its attribute "type" -->
	<#switch type>

		<#case "Paragraph">
		  <#recurse .node.paragraph.parameters> 
		  <#--  recursive process child parameters -->	
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
			<#if .node.attributes.vdef.value?is_node>
${Toolname?upper_case}.${name}=<#list defaults as default><#if !defaults?seq_contains("\"\"")>${default}</#if><#t></#list>

	        </#if>
		<#break> 



	</#switch>	
</#macro>


