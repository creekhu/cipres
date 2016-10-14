<#-- ======================== Start of Code Generation ============= -->
<#recurse doc>
<#macro pise>


<#list .node.parameters.parameter as parameter>
	<#if parameter.@type?contains("Paragraph")>
		<br></br>
		<A HREF="#${parameter.paragraph.name}">${parameter.paragraph.prompt}</A>
	</#if>
</#list>


<H2>Some explanations about the options</H2>
<dl>
<dt><br></br><i><b>Main parameters</b></i></dt>
<dt><a name=_data><i>enter either the name of a file or the actual data</i></a></dt>
<dd>if you are using Netscape 2.x or later, you can select a file by typing its name, or better, by selecting it with the Netscape file browser (<STRONG>Browse</STRONG> button)</dd>
<dd>OR you can type your data in the next area, or cut and paste it from another application.</dd>
<dd>(but not both)
</dd>

<#recurse .node.parameters>

<dt><a name=fmtseq><i>Sequence format</i></dt>
<dd>The sequence will be automatically converted in the format needed for the program</dd>
<dd>providing you enter a sequence either:</dd>
<dd> in plain (raw) sequence format or in one of the following known formats:</dd>
<dd>IG,GenBank,NBRF,EMBL,GCG,DNAStrider,Fitch,fasta,Phylip,PIR,MSF,ASN,PAUP,CLUSTALW</dd>
<dd>You may enter in the text area a database entry code, or an accession number, in this form:<BR></BR><CODE>database:entry_name</CODE><BR></BR>or:<BR></BR><CODE>database:accession</CODE>.</dd>
</dl>
<hr></hr>

<A NAME="REFERENCE">References</A>:
<BR></BR>

<#if .node.head.reference?is_node>
	<#list .node.head.reference as reference>
		${reference}
	</#list>
</#if>

</#macro>


<#-- ======================== macro parameters ============= -->
<#macro parameters> <#recurse > </#macro>

<#-- ================= macro parameter type process ============= -->
<#macro parameter>

	<#-- the following is for simple form generation	 		-->
	<#-- to generate simple form, only process those nodes with -->
	<#-- attributes="issimple"									-->
	
    <#-- skip parameter when "ishidden" is "1" -->
	<#if .node.@ishidden[0]?exists && .node.@ishidden[0]?contains("1")><#return></#if>

	<#if .node.@type?contains("Paragraph")>
		
		<br></br>
		<#assign p= .node.paragraph>
		<dt><a name=${p.name}_comment><i><b>${p.prompt}</b></i></a></dt>
		<#list p?children as child>
			<#if child?node_name?contains("comment")>
				<#list child?children as value><dd>${value}</dd></#list>
			</#if>
		</#list>
		
		<#recurse .node.paragraph.parameters> 
		
	<#elseif .node.attributes.comment?is_node>
		
			<dt><a name=${.node.name}><i>${.node.attributes.prompt}</i></a></dt>
			<#list .node.attributes.comment.value as value><dd>${value}</dd></#list>
		
	</#if>
</#macro>

