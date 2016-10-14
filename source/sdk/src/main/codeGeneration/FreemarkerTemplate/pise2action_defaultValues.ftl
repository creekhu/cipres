<#-- ======================== Start of Main() Code Generation ============= -->
<#recurse doc>
<#macro pise>

		<#recurse>  

</#macro>

<#-- ======================== End of Main() Code Generation ============= -->

<#-- ======================== Macros ============= -->

<#macro parameters><#recurse></#macro>


<#macro parameter>

<#--  change "-" to "_" since Java does not recognize '-' as valid name 
	  change " " to "_" since space is not allowed in a Java varname 
	  add "_" to name to avoid using common methods names as parent class -->
<#assign name = .node.name[0]?default("")>
<#assign name = name?replace("[ |-]","_","r")+"_">
<#assign name = name?cap_first>
<#assign type=.node.@type>
	
<#-- skip parameter when "ishidden" is "1" -->
<#if .node.@ishidden[0]?exists && .node.@ishidden[0]?contains("1")><#return></#if>
	
	<#-- process parameter node according to its attribute "type" -->
		 	<#-- default list -->
			<#assign defaults =.node.attributes.vdef.value>
			<#-- for most field, there's only one default value: first item -->
			<#assign default = defaults[0]?default("")>
			<#-- get rid of first and last " if there's one in default value -->
			<#assign default = default?replace("\"",'','f')>
			<#assign default = default?replace("\"",'','l')>
			<#assign size = defaults?size>
				

	<#switch type>

		<#case "Paragraph">
		  <#recurse .node.paragraph.parameters> 
		<#break>
		
		<#case "OutFile">
		<#case "Label">
		<#case "Results">
		<#break>

		<#case "Sequence">
		<#case "String">
			<#if .node.attributes.vdef.value?is_node>
	        set${name}(<#list defaults as default> <#if !defaults?seq_contains("\"\"")>"${default}" <#else>""</#if><#t></#list>); 
	        </#if>
		<#break> 
		
		<#case "Excl">
			<#if .node.attributes.vdef.value?is_node>
	        set${name}(<#list defaults as default> <#if !defaults?seq_contains("\"\"")>"${default}" <#else>""</#if><#t></#list>); 
	        </#if>
		<#break> 
		
		<#case "Switch"> 
			<#if .node.attributes.vdef.value?is_node>
	        set${name}(<#list defaults as default><#if defaults?seq_contains("1")>"true"</#if><#if defaults?seq_contains("0")>"false"</#if><#t></#list>);
	        </#if>
		<#break>
			
		<#case "Float">  
		<#case "Integer">	
		<#case "InFile">	
			<#if .node.attributes.vdef.value?is_node>
	        set${name}(<#list defaults as default>"${default}"<#t></#list>);
	        </#if>
		<#break>

		<#case "List">
			<#-- default list -->		
	        <#if .node.attributes.vdef?is_node>
	        
	        set${name}(new String[]{<#list defaults as default>"${default}"<#t>
				<#if defaults?seq_index_of(default) lt (size-1)>,</#if><#t>
			</#list>});	
			
			</#if> 
		<#break>
		
		<#default>
		<#break>

	</#switch> 

		
</#macro>









