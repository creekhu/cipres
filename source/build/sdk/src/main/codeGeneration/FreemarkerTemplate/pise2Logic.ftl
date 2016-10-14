<#-- ======================== Start of Main() Code Generation ============= -->
<#compress>		<#-- remove superfluous white-space -->

<#recurse doc>	<#-- Strart recursively processing the document -->

<#macro pise>    <#-- A macro name matched with the label of the node in the doc-->
                 <#-- when this node is found execute the following commands-->

<#assign command = .node.command?default("")>

<#assign Toolname=.node.command>  <#-- Declaration of a varialbe Toolname-->

	<#recurse .node.parameters>     
	  <#-- Permit to recursively process the nodes, FreeMaker will look for a 
	       macro that matches the label of the node in the doc. In this case
	       it will find 'parameters' -->	
              
</#macro>
</#compress>
<#-- ======================== End of Main() Code Generation ============= -->


<#-- ======================== Macros ============= -->


<#macro parameter>

	<#-- set defaults to the list of default values in <vdef> node -->
	<#assign defaults =.node.attributes.vdef.value>
	       
<#-- skip parameter when "ishidden" is "1" -->
	<#if .node.@ishidden[0]?exists && .node.@ishidden[0]?contains("1")><#return></#if>

	<#assign name = .node.name[0]?default("")>

	<#assign prompt = .node.attributes.prompt[0]?default("")>

	<#-- process parameter node according to its attribute "type" -->
	<#switch .node.@type>
	
		<#case "Paragraph">
		<#recurse .node.paragraph.parameters> 
		<#--  recursive process child parameters -->	
		<#break>
	
		<#case "OutFile">
		<#case "Results">
		<#case "Label">	
		<#case "Sequence"> 
		<#case "Float">
		<#case "Integer">
		<#case "String">
		<#case "InFile">
		<#case "Switch">
		<#case "Excl">
		<#case "List">	
		/////
		<#assign nameimproved = name?replace("[ |-]","_","r")>
		${name} 
		
			<#-- Convert to Java the Perl precondition -->			  
		    <#if .node.attributes.precond?is_node>
				  	 
				 ${Toolname} Precond= ${.node.attributes.precond[0].code}  	 
				 <#assign precondString=.node.attributes.precond[0].code>	 
				 	
				 <#assign precondString=precondString?replace(" or ", " || ")>
				 <#assign precondString= precondString?replace(" eq ", "==")>
				 <#assign precondString=precondString?replace(" ne ", "!=")> 
					  					
				<#assign precondString=precondString?replace("defined $value &&", "")>
				<#assign precondString=precondString?replace("defined", "")>
				
				<#assign precondString=precondString?replace("$value", "$"+name)> 
				<#assign precondString=precondString?replace("$/", "/")>
					 
    			<#assign precondString=precondString?replace(" =~ /", ".match(\"")> 
					
		     	<#assign precondString=precondString?replace("( !~ /\\^)(\\W*)(\\w*)(\\W*)", ".match(\"^"+"$2$3$4"+"==null","r")> 
				<#assign precondString=precondString?replace("( !~ /)(\\W*)(\\w*)(\\W*)", ".match(\""+"$2$3$4"+"==null","r")>
	
				<#assign precondString = precondString?replace("\\$(\\w*)","getValue('"+"$1"+"_')","r")>
	
				<#assign precondString=precondString?replace("/", "\")")>

				  Precond_Java= if (${precondString}) then {expand()} else {collapse()}
		    </#if>
		
			<#-- Verify Mandatory Parameter -->
			<#if .node.@ismandatory[0]?exists && .node.@ismandatory[0]?contains("1")>
			  	 Mandatory=True 
			</#if>
		  	 
			<#-- Verify Parameter with Ctrl (range, minimum, maximum) -->  	 
			<#if .node.attributes.ctrls?exists>	 		  	
			 <#assign size = .node.attributes.ctrls.ctrl?size> 			
				<#list .node.attributes.ctrls.ctrl as ctrl>				
					${Toolname} Control_code= ${.node.attributes.ctrls.ctrl[ctrl_index].code[0]}	
					<#assign controlString=.node.attributes.ctrls.ctrl[ctrl_index].code[0]>	 
				 	
				 <#assign controlString=controlString?replace(" or ", " || ")>
				 <#assign controlString= controlString?replace(" eq ", "==")>
				 <#assign controlString=controlString?replace(" ne ", "!=")> 
					  					
				<#assign controlString=controlString?replace("defined $value &&", "")>
				<#assign controlString=controlString?replace("defined", "")>
				
				<#assign controlString=controlString?replace("$value", "$"+name)> 
				<#assign controlString=controlString?replace("$/", "/")>
					 
    			<#assign controlString=controlString?replace(" =~ /", ".match(\"")> 
					
		     	<#assign controlString=controlString?replace("( !~ /\\^)(\\W*)(\\w*)(\\W*)", ".match(\"^"+"$2$3$4"+"==null","r")> 
				<#assign controlString=controlString?replace("( !~ /)(\\W*)(\\w*)(\\W*)", ".match(\""+"$2$3$4"+"==null","r")>
	
				<#assign controlString = controlString?replace("\\$(\\w*)","getValue('"+"$1"+"_')","r")>
	
				<#assign controlString=controlString?replace("/", "\")")>

					 
					<#-- Do the Same as done for precond -->
					Control_code_Java= if (${controlString}) then {"Control_message=  ${.node.attributes.ctrls.ctrl[ctrl_index].message}"} else {}
				

				</#list>
		    </#if>  
		    /////
		<#break>


		<#default>
		<#break>

	</#switch>
	    
</#macro> 
