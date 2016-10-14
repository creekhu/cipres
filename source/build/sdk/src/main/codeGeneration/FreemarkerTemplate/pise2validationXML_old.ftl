<#-- ======================== Start of Main() Code Generation ============= -->
<#compress>		<#-- remove superfluous white-space -->

<#recurse doc>	<#-- Strart recursively processing the document -->

<#macro pise>    <#-- A macro name matched with the label of the node in the doc-->
                 <#-- when this node is found execute the following commands-->

<#assign Toolname=.node.command>  <#-- Declaration of a varialbe Toolname-->

<#assign name=.node.command>
	<!DOCTYPE validators PUBLIC
    "-//OpenSymphony Group//XWork Validator 1.0.2//EN"
    "http://www.opensymphony.com/xwork/xwork-validator-1.0.2.dtd">

	<validators>
	  
	  <#recurse .node.parameters>     
	  <#-- Permit to recursively process the nodes, FreeMaker will look for a 
	       macro that matches the label of the node in the doc. In this case
	       it will find 'parameters' -->	
	</validators>
             
</#macro>
</#compress>
<#-- ======================== End of Main() Code Generation ============= -->


<#-- ======================== Macros ============= -->

<#macro parameters><#recurse></#macro>

<#macro parameter>

<#--  change "-" to "_" since Java does not recognize '-' as valid name 
	  change " " to "_" since space is not allowed in a Java varname 
	  add "_" to name to avoid using common methods names as parent class -->
	<#assign name = .node.name[0]?default("")>
	<#assign name = name?replace("[ |-]","_","r")+"_">

	       
<#-- skip parameter when "ishidden" is "1" -->
	<#if .node.@ishidden[0]?exists && .node.@ishidden[0]?contains("1")><#return></#if>

		<#switch .node.@type>
	
		<#case "Paragraph">
				<#-- Take care of the precond for a paragraph-->
		    <#recurse .node.paragraph.parameters> 
		<#break>
		
		<#case "InFile">
		   <#if .node.@ismandatory[0]?exists>  
		    <field name="${name}">
			    <field-validator type="required">
        				<message>You must enter a value for ${name}, an empty String is not acceptable.</message>
    				</field-validator>
  				</field>
			 
			</#if>
		<#break>
		
		<#case "Sequence"> 
		<#case "List">
		<#case "Excl">
    	<#case "String">	
		 <#if .node.@ismandatory[0]?exists>  
		    <field name="${name}">
			    <field-validator type="requiredstring">
        				<message>You must enter a value for ${name}, an empty String is not acceptable.</message>
    				</field-validator>
  				</field>
			 
			</#if>
		<#break>
					
		<#case "Integer">
		  <#if .node.@ismandatory[0]?exists> 
		    <field name="${name}">
		    
		     <#if (.node.attributes.scalemin?is_node  && .node.attributes.scalemin.value?is_node) 
					    || (.node.attributes.scalemax?is_node  && .node.attributes.scalemax.value?is_node)>
		   	 <field-validator type="int">
    			<#if .node.attributes.scalemin?is_node  && .node.attributes.scalemin.value?is_node >	
    			     <param name="min">${.node.attributes.scalemin.value}</param> 
    			     <message>myProperty must be > than "${.node.attributes.scalemin.value}".</message>
    			</#if>
    			<#if .node.attributes.scalemax?is_node  && .node.attributes.scalemax.value?is_node>	
    			     <param name="max">${.node.attributes.scalemax.value}</param>
    			     <message>myProperty must be < than"${.node.attributes.scalemax.value}".</message> 
    			</#if>
             </field-validator>
            </#if>
            
        	<field-validator type="conversion">
			    <param name="repopulateField">true</param>
			    <message>${name} must be of type Integer</message>
			</field-validator>
			
        	<field-validator type="required">
    			<message>You must enter a value for ${name}.</message>
			</field-validator>    

		 </field>
		 <#else>
		  <field name="${name}">
		   <#if (.node.attributes.scalemin?is_node  && .node.attributes.scalemin.value?is_node) 
					 || (.node.attributes.scalemax?is_node  && .node.attributes.scalemax.value?is_node)>
		    <field-validator type="int">
    			<#if .node.attributes.scalemin?is_node  && .node.attributes.scalemin.value?is_node >	
    			     <param name="min">${.node.attributes.scalemin.value}</param> 
    			     <message>myProperty must be > than "${.node.attributes.scalemin.value}".</message>
    			</#if>
    			<#if .node.attributes.scalemax?is_node  && .node.attributes.scalemax.value?is_node>	
    			     <param name="max">${.node.attributes.scalemax.value}</param>
    			     <message>myProperty must be < than"${.node.attributes.scalemax.value}".</message> 
    			</#if>
             </field-validator>
            </#if>
            
        	<field-validator type="conversion">
			    <param name="repopulateField">true</param>
			    <message>${name} must be of type Integer</message>
			</field-validator>
		  </field>
		 </#if>	
		<#break>
		
		<#case "Float">
		  <#if .node.@ismandatory[0]?exists> 
					    <field name="${name}">
					    
					    <#if (.node.attributes.scalemin?is_node  && .node.attributes.scalemin.value?is_node) 
					    		|| (.node.attributes.scalemax?is_node  && .node.attributes.scalemax.value?is_node)>
					    <field-validator type="double">
		        			<#if .node.attributes.scalemin?is_node  && .node.attributes.scalemin.value?is_node >	
		        			     <param name="min">${.node.attributes.scalemin.value}</param> 
		        			     <message>myProperty must be > than "${.node.attributes.scalemin.value}".</message>
		        			</#if>
		        			<#if .node.attributes.scalemax?is_node  && .node.attributes.scalemax.value?is_node>	
		        			     <param name="max">${.node.attributes.scalemax.value}</param>
		        			     <message>myProperty must be < than"${.node.attributes.scalemax.value}".</message> 
		        			</#if>
		                 </field-validator>
		                </#if>
		                
		            	<field-validator type="conversion">
						    <param name="repopulateField">true</param>
						    <message>${name} must be of type Double</message>
						</field-validator>
						
		            	<field-validator type="required">
		        			<message>You must enter a value for ${name}.</message>
		    			</field-validator>    
		
					 </field>
					 <#else>
					  <field name="${name}">
					  
					   <#if (.node.attributes.scalemin?is_node  && .node.attributes.scalemin.value?is_node) 
					    		|| (.node.attributes.scalemax?is_node  && .node.attributes.scalemax.value?is_node)>
					    <field-validator type="double">
		        			<#if .node.attributes.scalemin?is_node  && .node.attributes.scalemin.value?is_node >	
		        			     <param name="min">${.node.attributes.scalemin.value}</param> 
		        			     <message>myProperty must be > than "${.node.attributes.scalemin.value}".</message>
		        			</#if>
		        			<#if .node.attributes.scalemax?is_node  && .node.attributes.scalemax.value?is_node>	
		        			     <param name="max">${.node.attributes.scalemax.value}</param>
		        			     <message>myProperty must be < than"${.node.attributes.scalemax.value}".</message> 
		        			</#if>
		                 </field-validator>
		                </#if>
		                
		            	<field-validator type="conversion">
						    <param name="repopulateField">true</param>
						    <message>${name} must be of type Double</message>
						</field-validator>
					  </field>
			</#if>	
		<#break>
		
	</#switch>		
	    
	  
</#macro>
