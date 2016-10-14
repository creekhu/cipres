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
</#macro>
</#compress>
<#-- ======================== Macros ============= -->
<#macro parameter>
<#--  change "-" to "_" since Java does not recognize '-' as valid name 
	  change " " to "_" since space is not allowed in a Java varname 
	  add "_" to name to avoid using common methods names as parent class -->
	<#assign name = .node.name[0]?default("")>
	<#assign name = name?replace("[ |-]","_","r")+"_">	    
	
		
	<#if .node.@type="Paragraph">
		<#recurse .node.paragraph.parameters>
	</#if>
	   
<#-- skip parameter when "ishidden" is "1" -->
	<#if .node.@isinput[0]?exists && .node.@isinput[0]?contains("1")>
	
	<Tool id="${Toolname?upper_case}" name="${description}" 
				configfile="pisexml/${Toolname}.xml" toolresource="8BALL"
				commandrenderer="org.ngbw.pise.commandrenderer.PiseCommandRenderer">
				<InputParameters>
					<InputParameter id="${name}" iomode="FILE" entityType="UNKNOWN" dataType="UNKNOWN" dataFormat="UNKNOWN"/>
				</InputParameters>
				<OutputParameters>
				</OutputParameters>
			</Tool> 
	</#if>	    
</#macro> 
