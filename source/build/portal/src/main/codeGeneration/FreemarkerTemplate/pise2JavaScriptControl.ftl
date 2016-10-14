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
				 <#assign controlString=controlString?replace("^not ", " ! ", "r")> 
				 <#assign controlString=controlString?replace(" not ", " ! ")> 
					  					
				<#assign controlString=controlString?replace("defined $value &&", "")>
				<#assign controlString=controlString?replace("defined", "")>
				
				<#assign controlString=controlString?replace("$value", "$"+name)> 

				<#-- 
					Terri:
					this removes end of string anchor! 
					I think rami did it because it gets confused with a variable ref, but I fixed that
					by making the variable replacement pattern more specific 
				<#assign controlString=controlString?replace("$/", "/")>
				-->
					 
				<#-- Terri: Handle translation of perl r.e. written as a =~ /pattern/ to javascript as a.match(/pattern/) 
					This won't work if pattern includes an escaped slash.
				-->
    			<#assign controlString=controlString?replace(" =~ /([^/]*)/", ".match(/$1/)" , "r")> 
					
				<#-- Terri: this handling of !~ doesn't always work.  Try ' not (a =~ b) ' instead -->
				<#-- I'm rewriting this as ! match()
		     	<#assign controlString=controlString?replace("( !~ /\\^)(\\W*)(\\w*)(\\W*)", ".match(\"^"+"$2$3$4"+"==null","r")> 
				<#assign controlString=controlString?replace("( !~ /)(\\W*)(\\w*)(\\W*)", ".match(\""+"$2$3$4"+"==null","r")>
				-->
    			<#assign controlString=controlString?replace(" !~ /([^/]*)/", ".match(/$1/) == null" , "r")> 
	
				<#-- I changed w* to w+ so we only replace $var, if var starts with at least one 'word' char.  Therefore we won't replace $/, the end of line anchor in a r.e. --> 
				<#assign controlString = controlString?replace("\\$(\\w+)","getValue('"+"$1"+"_')","r")>
	
				<#--
					Terri: we no longer need this because I changed the line above that inserts "match"
					to replace the whole regular expression at once.  This used to be needed to replace the trailing slash in a perl r.e.
				<#assign controlString=controlString?replace("/", "/)")>
				-->
		
			<#-- Convert to Java the Perl precondition -->			  
		    <#if .node.attributes.precond?is_node>
				  					  	 	 
				 <#assign precondString=.node.attributes.precond[0].code>	 
				 	
				 <#assign precondString=precondString?replace(" or ", " || ")>
				 <#assign precondString= precondString?replace(" eq ", "==")>
				 <#assign precondString=precondString?replace(" ne ", "!=")> 
				 <#assign controlString=controlString?replace("^not ", " ! ", "r")> 
				 <#assign controlString=controlString?replace(" not ", " ! ")> 
					  					
				<#assign precondString=precondString?replace("defined $value &&", "")>
				<#assign precondString=precondString?replace("defined", "")>
				
				<#assign precondString=precondString?replace("$value", "$"+name)> 

				<#--
				<#assign precondString=precondString?replace("$/", "/")>
				-->
					 
    			<#assign controlString=controlString?replace(" =~ /([^/]*)/", ".match(/$1/)" , "r")> 
					
				<#--
		     	<#assign precondString=precondString?replace("( !~ /\\^)(\\W*)(\\w*)(\\W*)", ".match(\"^"+"$2$3$4"+"==null","r")> 
				<#assign precondString=precondString?replace("( !~ /)(\\W*)(\\w*)(\\W*)", ".match(\""+"$2$3$4"+"==null","r")>
				-->
    			<#assign controlString=controlString?replace(" !~ /([^/]*)/", ".match(/$1/) == null" , "r")> 
	
				<#assign precondString = precondString?replace("\\$(\\w+)","getValue('"+"$1"+"_')","r")>
	
				<#--
				<#assign controlString=controlString?replace("/", "/)")>
				-->

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
