package org.ngbw.cipresrest.webresource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.ngbw.sdk.Workbench;
import org.ngbw.sdk.api.tool.ToolConfig;
import org.ngbw.sdk.tool.Tool;
import org.ngbw.restdatatypes.ErrorData;
import org.ngbw.restdatatypes.LinkData;
import org.ngbw.restdatatypes.ToolData;

/*
	Public API for getting information about cipres tools.  None of the optional
	parameters are implemented yet.  In the future I may add tool version and api version
	to the URLs where we currently have <tooldid>.

	GET	/v1/tool (optional parameter 'all', default is 'active')
		Returns list of tools. Just links for the v1/tool/<toolid> urls.

	In the future, each of the following will take optional params 'toolversion' and 'apiversion'.
	Defaults to the active versions.

	GET	/v1/tool/<toolid>
		Get info about the specific tool.  Info will include toolid, tool name, tool version,
		api version, link to cipres html page that describes the tool, link to pisexml, link to
		developer docs for the tool.

	GET /v1/tool/<toolid>/doc/pise
		Returns pise xml.  Does this as a file download.  TODO: change so that we read the file
		into memory and return an object instead of making it a download?

	// TODO: implement these.  It may make more sense to host these outside the jersey web app.
	// Can the jersey method at these urls do a redirect?  Hard for client to handle that?
	GET /v1/tool/<toolid>/doc/example
		Html example.

	GET /v1/tool/<toolid>/doc/portal2
		Portal2 html info page for the tool.

	GET /v1/tool/<toolid>/doc/parameter
		Html description of tool parameters.
*/
/*
TODO:
Ideally this would have info about the specific tool.  Info includes toolid, tool name, tool version,
api version, link to cipres html page that describes the tool, link to pisexml, link to
developer docs for the tool.  Unfortunately, some of that stuff needs to be pulled from the pisexml
file and we don't currently do that in the sdk.

Maybe extend ToolConfig?  We would have to create a CommandRenderer to get the info from the pise -
would want to do that once per tool, not repeatedly.

We have the tool version in the pise xml file but we don't version our pisexml files.  We should increment
the pise file's version number whenever the parameters change such that it would break cloning, or break
rest clients.
*/


@Path("/v1/tool/")
public class CipresTool
{
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(CipresTool.class.getName());
	@Context SecurityContext securityContext;
	@Context UriInfo uriInfo;

 	@GET
    @Produces( MediaType.APPLICATION_XML )
    public List<ToolData> getTools() throws Exception
    {
		Set<String> toolIds = Workbench.getInstance().getActiveToolIds();

		// sort the set of toolIds
		List<String> l = new ArrayList<String>(toolIds);
		Collections.sort(l);

		List<ToolData> tools = new ArrayList<ToolData>(toolIds.size());
		for (String tid : l)
		{
			tools.add(createToolData(tid));
		}
		return tools;
    }

    @GET
	@Path("{tool}")
    @Produces( MediaType.APPLICATION_XML )
    public ToolData getTool( @PathParam("tool") String toolId) throws Exception
    {
		ToolData td = createToolData(toolId);
		if (td == null)
		{
			throw new CipresException("Tool " + toolId + "not found.", Status.NOT_FOUND, ErrorData.NOT_FOUND);
		}
		return td;
    }

    @GET
	@Path("{tool}/doc")
    @Produces( MediaType.APPLICATION_XML )
    public Collection<LinkData> getDoc( @PathParam("tool") String toolId) throws Exception
	{
		List<LinkData> list  = new ArrayList<LinkData>();
		LinkData getPiseUri = getToolDocUri(toolId, "pise");
		list.add(getPiseUri);

		return list;
	}

    @GET
	@Path("{tool}/doc/pise")
    public Response getPise( @PathParam("tool") String toolId ) throws Exception
    {
		Tool tool = null;
    	try
		{
			tool =  Workbench.getInstance().getTool(toolId);
		}
		catch(Exception e)
		{
			throw new CipresException("Tool " + toolId + " not found.", Status.NOT_FOUND, ErrorData.NOT_FOUND);
		}
		ToolConfig toolConfig = tool.getToolConfig();
		URL url = toolConfig.getConfigFileURL();
		InputStream is = null;
		try
		{
			is = url.openStream();
			return Response.ok(is).
				header("Content-Disposition", "attachment; filename=" + toolId + ".pisexml").
				type(MediaType.APPLICATION_XML).
				build();
		}
		catch(Exception e)
		{
			if (is != null) { try { is.close(); } catch (Exception ee) {} }
			throw e;
		}
	}

    @GET
	@Path("{tool}/doc/@{build.portal.appName}")
    @Produces( MediaType.TEXT_HTML)
    public Response getPortal2( @PathParam("tool") String toolId) throws Exception
	{
		// this does a redirect to the cipres tool info page. Status 303.
		String name = toolId.toLowerCase() + ".html";
		URI other = new URI("http://www.phylo.org/tools/" + name);
		return Response.seeOther(other).build();
	}

    @GET
	@Path("{tool}/doc/example")
    @Produces( MediaType.TEXT_HTML)
    public Response getExample( @PathParam("tool") String toolId) throws Exception
	{
		return Response.ok("Not implemented yet.").build();
	}

    @GET
	@Path("{tool}/doc/param")
    @Produces( MediaType.TEXT_HTML)
    public Response getParam( @PathParam("tool") String toolId) throws Exception
	{
		// this does a redirect to the rest specific tool doc page, hostd on www.phylo.org. Status 303.
		String name = toolId.toLowerCase() + ".html";
		//URI other = new URI("http://www.phylo.org/rest/" + name);
		URI other = new URI("http://www.phylo.org/index.php/rest/" + name);
		return Response.seeOther(other).build();
	}

	private  ToolData createToolData(String toolId)
	{
		if (!Workbench.getInstance().getActiveToolIds().contains(toolId))
		{
			return null;
		}
		Tool tool =  Workbench.getInstance().getTool(toolId);
		ToolConfig toolConfig = tool.getToolConfig();

		ToolData td = new ToolData();
		td.toolId = toolId;
		td.toolName = toolConfig.getName();
		td.selfUri = getToolUri(toolId);

		// These aren't really needed since you can get them from td.doc url.
		td.piseUri = getToolDocUri(toolId, "pise");
		td.portal2Uri= getToolDocUri(toolId, "@{build.portal.appName}");
		td.parameterUri= getToolDocUri(toolId, "param");
		td.exampleUri = getToolDocUri(toolId, "example");
		return td;
	}

	private LinkData getToolUri(String toolId)
	{
		UriBuilder ub = uriInfo.getBaseUriBuilder();
		URI uri = ub.path(CipresTool.class).path(toolId).build();
		return new LinkData(uri.toString(), ToolData.name, toolId);
	}


	private LinkData getToolDocUri(String toolId, String type)
	{
		UriBuilder ub = uriInfo.getBaseUriBuilder();
		URI uri;
		/*
		if (type.equals(""))
		{
			uri = ub.path(CipresTool.class).path(toolId).path("doc").build();
			return new LinkData(uri.toString(), "Documentation List", toolId + " documentation");
		}
		*/
		uri = ub.path(CipresTool.class).path(toolId).path("doc").path(type).build();
		if (type.equals("pise"))
		{
			return new LinkData(uri.toString(), "Pise XML", toolId + " pise");
		}
		return new LinkData(uri.toString(), "Html Web Page", toolId + " type");
	}


}

