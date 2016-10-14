
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

			
		<#if .node.@type="Paragraph">
		<#recurse .node.paragraph.parameters>
	</#if>

	<#if .node.@isinput[0]?exists && .node.@isinput[0]?contains("1")>
${Toolname?upper_case}.${name}=	examples/tooltest/${Toolname?upper_case}/${Toolname?upper_case}_in.txt	
	</#if>
	


</#macro>


