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

				 if (${precondString})  	
				  enable('${name}_');
					else disable('${name}_');
					
				 

		    </#if>
		    
		    
		<#break>


		<#default>
		<#break>

	</#switch>
	    
</#macro> 
