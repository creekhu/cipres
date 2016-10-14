<#-- ======================== Start of Main() Code Generation ============= -->
	
<#recurse doc>

<#macro pise>    

	<#recurse .node.parameters>   

              
</#macro>

<#-- ======================== End of Main() Code Generation ============= -->


<#-- ======================== Macros ============= -->


<#macro parameter>

	<#-- set defaults to the list of default values in <vdef> node -->
	<#assign defaults =.node.attributes.vdef.value>
	       
<#-- skip parameter when "ishidden" is "1" -->
	<#if .node.@ishidden[0]?exists && .node.@ishidden[0]?contains("1")><#return></#if>
	
	<#if .node.@isinput[0]?exists && .node.@isinput[0]?contains("1")><#return></#if>

	<#assign name = .node.name[0]?default("")>

	<#assign prompt = .node.attributes.prompt[0]?default("")>

	<#-- process parameter node according to its attribute "type" -->
	<#switch .node.@type>
	
		<#case "Paragraph">
			<#recurse .node.paragraph.parameters> 
			<#--  recursive process child parameters -->	
		<#break>
		
		<#case "Switch">
		<#case "Label">	
		<#case "Sequence"> 
		<#case "Float">
		<#case "Integer">
		<#case "String">
		<#case "InFile">
		<#case "Excl">
		<#case "List">	
		
		<#assign nameimproved = name?replace("[ |-]","_","r")>
		// ${name} 
		
			<#-- Verify Parameter with Ctrl (range, minimum, maximum) -->  	 
			<#if .node.attributes.ctrls?exists>	 		  	
			 <#assign size = .node.attributes.ctrls.ctrl?size> 			
				<#list .node.attributes.ctrls.ctrl as ctrl>	
							
				<#--	Control_code= ${.node.attributes.ctrls.ctrl[ctrl_index].code[0]}	-->
					
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
		
			<#-- Convert to Java the Perl precondition -->			  
		    <#if .node.attributes.precond?is_node>
				  					  	 	 
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

				 if (${precondString}) {
					if (${controlString}) {
				  		alert('${.node.attributes.ctrls.ctrl[ctrl_index].message}');
				  		return false;
				  	}
				 }
		    <#else>

					if (${controlString}) {
				  		alert('${.node.attributes.ctrls.ctrl[ctrl_index].message}');
				  		return false;
				  	}
			</#if>
		</#list>
	</#if>  
		    
		    
		<#break>


		<#default>
		<#break>

	</#switch>
	    
</#macro> 
