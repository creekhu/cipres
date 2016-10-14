
<#-- ======================== Start of Main() Code Generation ============= -->
<#compress>		<#-- remove superfluous white-space -->

<#recurse doc>	<#-- Strart recursively processing the document -->

<#macro pise>    <#-- This macro name should match with the label of a node in the 'doc'
                 	when this node is found, it will execute the following -->

<#assign Toolname=.node.command>  <#-- Declaration of a varialbe Toolname-->
								<#-- .node is the current context on the imput tree
								     .node.command is its child-->

package org.ngbw.web.actions.tool;

import org.ngbw.sdk.common.util.SuperString;
import org.ngbw.web.actions.ToolParameters;


 
<#-- Text to be written in the target generated file
     complete the whole set of import for the  action class-->

@SuppressWarnings("serial")
public class ${Toolname} extends ToolParameters <#-- call of the varialbe Toolname-->
{

	  boolean inReset = false;

	  <#recurse>      
	  <#-- Permit to recursively process the nodes, FreeMaker will look for a 
	       macro that matches the label of the node in the doc. In this case
	       it will find 'parameters' -->
	    
	
	// this method pre-populates the form with default values
	    public void reset() 
		{
			super.reset();
			try
			{
				inReset = true;
				<#-- nested call for all parameters to set their default value-->
				<#include "pise2action_defaultValues.ftl">
		 	} 
			finally
			{
				inReset = false;
			}
         } 

}                    
</#macro>
</#compress>

<#-- ======================== End of Main() Code Generation ============= -->

<#-- ======================== Macros ============= -->
<#macro @element></#macro>   
<#-- this macro is needed because we call for <#include...> in the Main() -->

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

	
<#-- skip parameter when "ishidden" is "1" -->
<#if .node.@ishidden[0]?exists && .node.@ishidden[0]?contains("1")><#return></#if>
	
	<#-- process parameter node according to its attribute "type" -->
	<#switch type>

		<#case "Paragraph">
		  <#recurse .node.paragraph.parameters> 
		  <#--  recursive process child parameters -->	
		<#break>
		
		<#case "OutFile">		
		<#case "Label">
		<#case "Results">
		<#break>
		
		<#case "Float"> 	
		<#case "Integer">
		<#case "Switch">
		<#case "Excl">  <#-- parameter type=Excl is a list with one choice-->
		<#case "String"> <#-- parameter type=String is not much used ???-->
	    public String get${nameCap}() 
		{
			return getUIParameters().get("${name}");
	    }
	    
	    public void set${nameCap}(String ${name}) 
	    { 
			if (inReset == false)
				setToolParameter("${name}", ${name});
			getUIParameters().put("${name}", ${name});
	    }
		<#break> 

		<#case "Sequence">
		<#case "InFile">
		 // file-typed property getters and setters
	    public Long get${nameCap}() 
	    { 
	    		return getInputDataItemId("${name}");
	    }
	    
	    public void set${nameCap}(Long ${name}) 
	    {
	        setInputDataItem("${name}",${name});
         }
		<#break> 


		<#case "List">		 
		 <#if  .node.attributes.separator[0]?exists>
		   <#assign separator=.node.attributes.separator>
		    public String [] get${nameCap}() 
			{ 
				String value= getUIParameters().get("${name}");
			  	return SuperString.valueOf(value,${separator}).toArray();
		    }
		   
		    public void set${nameCap}(String [] ${name}) 
		    { 
			 	String value=SuperString.valueOf(${name}, ${separator}).toString(); 
				if (inReset == false)
		      		setToolParameter("${name}", value);
		      	getUIParameters().put("${name}", value);
		    }
		   <#else> 
		    public String [] get${nameCap}() 
			{ 
				String value= getUIParameters().get("${name}");
			  	return SuperString.valueOf(value,'@').toArray();
		    }
		   
		    public void set${nameCap}(String [] ${name}) 
		    { 
				String value=SuperString.valueOf(${name},'@').toString(); 
				if (inReset == false)
		      		setToolParameter("${name}", value);
		      	getUIParameters().put("${name}", value);
		    }
		 </#if>   
		<#break>
		
		<#default>
		<#break>

	</#switch>
		
</#macro>


