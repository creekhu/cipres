package org.ngbw.pise.commandrenderer;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.ValidationException;
import org.ngbw.sdk.api.tool.FieldError;


/*
	This is for the rest api to simulate what happens in javascript when you open a tool gui
	and modify parameters and then submit the form.   

	populate() simulates the way the form is populated with all the visible and infile (and sequence)
	parameters that are in the pise document.  The value is the vdef, if there is one. Otherwise,
	if it's a switch w/o a vdef, then 0. Everything else w/o a vdef is set to "".

	merge() simulates what happens when a user modifies a field in the gui.  First of all modification
	is only allowed if the field is enabled.  Then once, the field is changed, the preconds of all
	fields are revevaluated to see what needs to be enabled/disabled.  So we do this for each parameter
	the user sent.  Then, we simulate submission: for each field that is enabled, we eval it's controls.
	Finally, we look at all the fields and eliminate those that are disabled or the empty string.  We
	also separate out InFile and Sequence parameters since they must be passed on separately.
*/
public class GuiSimulator 
{
	private static Log log = LogFactory.getLog(GuiSimulator.class.getName());

	private final Map<URL , PiseMarshaller> cfgMap = new HashMap<URL , PiseMarshaller>();
	private PiseMarshaller piseMarshaller;

	private PerlEval perlEval = null;
	List<FieldError> parameterErrors = new ArrayList<FieldError>();

	public static class Element
	{
		public String value;
		public boolean enabled;
		public String type;
		public Element(String value, boolean enabled, String type)
		{
			this.value = value;
			this.enabled = enabled;
			this.type = type;
		}
	}
	private Map<String, Element> parameters;
	private Map<String, String> userSupplied = new HashMap<String, String>();
	
	public GuiSimulator() 
	{
		parameters = null;
		piseMarshaller = null;
	}

	public Map<String, String> merge(Map<String, String> userInput, Set<String> userInputInfile, URL url) throws Exception
	{
		Map<String, String> retval = null;
		try 
		{
			piseMarshaller = initPiseMarshaller(url);
			perlEval = new PerlEval();
			perlEval.initialize();
		
			String piseToolName = url.getPath().substring( url.getPath().lastIndexOf('/')+1, url.getPath().length() );
			log.debug("START GuiSimulator " + piseToolName);
			retval = mergeWithUserInput(userInput, userInputInfile, url);

			perlEval.terminate();


			// for debugging:
			String tmp = "";
			for (String p : retval.keySet())
			{
				tmp += p + "=" + retval.get(p) + ", ";
			}
			log.debug("Returning: " + tmp);

			return retval;
		}
		finally
		{
			if (perlEval != null)
			{
				perlEval.cleanup();
			}
			log.debug("END GuiSimulator");
		}
	}

	/*
	*/
	private Map<String, String> mergeWithUserInput(Map<String, String> userInput, Set<String> userInputInfile, URL url) throws Exception
	{

		/*
			Merge userInput and userInputFile into a new map -> userSupplied, getting rid of trailing underscores in the names.
			Make sure we only add parameters that are in the pise file for the tool and report errors for any extraneous 
			parameters the user sent.
		*/
		Set<String> piseParams = piseMarshaller.getExtendedVisibleSet();
		for (String param: userInput.keySet())
		{
			String paramName = param.replaceFirst("_$", "");
			if (piseParams.contains(paramName))
			{
				userSupplied.put(paramName, userInput.get(param));
			} else
			{
				parameterErrors.add(new FieldError(paramName, paramName + " is not a parameter known to this tool."));
				log.debug("ADD ERROR: " + paramName + " unknown");
			}
		}
		for (String param : userInputInfile)
		{
			String paramName = param.replaceFirst("_$", "");
			if (piseParams.contains(paramName))
			{
				userSupplied.put(paramName, "placeholder");
			} else
			{
				parameterErrors.add(new FieldError(paramName, paramName + " is not a parameter known to this tool."));
				log.debug("ADD ERROR: " + paramName + " unknown");
			}
		}


		// From the pise, load all visible params with their default values, but skip userSupplied ones. 
		this.parameters = populate(url);
		//log.debug("There are " + this.parameters.size() + " parameters that user didn't supply.");

		log.debug("***********************************************************************************");
		log.debug("EVAL Preconditions of all default params that user DID NOT SUPPLY, using usersupplied values:"); 
		log.debug("***********************************************************************************");
		enableDisable();

		/* DEBUG ONLY */
		String elist = "";
		String dlist = "";
		String ulist = "";
		for (String s : this.parameters.keySet())
		{
			if (this.parameters.get(s).enabled == true)
			{
				elist += s + ", ";
			} else
			{
				dlist += s + ", ";
			}
		}
		for (String s : this.userSupplied.keySet())
		{
				ulist += s + "=" + this.userSupplied.get(s) + ", ";
		}
		log.debug("Initially enabled: " + elist);
		log.debug("Initially disabled: " + dlist);
		log.debug("User supplied: " + ulist);
		/* END DEBUG ONLY */



		log.debug("***********************************************************************************");
		log.debug("Create map with userSupplied first, then defaults:");
		log.debug("***********************************************************************************");
		Map<String, Element> allfields  = new LinkedHashMap<String, Element>();
		for (String p : userSupplied.keySet())
		{
			allfields.put(p, new Element(userSupplied.get(p), true, piseMarshaller.getType(p)));
		}
		allfields.putAll(this.parameters);

		this.parameters = allfields;	// this.parameters has both userSupplied and defaults now.

		// Save list of usersupplied params but set this.userSupplied to null so getValue won't use it.
		Collection<String> saveUserSuppliedNames = this.userSupplied.keySet(); 
		this.userSupplied = null;		


		/* DEBUG ONLY */
		String tmp = "";
		for (String s : this.parameters.keySet())
		{
			tmp += s + "=" + this.parameters.get(s).value + ", ";
		}
		log.debug(tmp);
		/* END DEBUG ONLY */


		log.debug("***********************************************************************************");
		log.debug("EVAL Preconditions of ALL params including user supplied. UserSupplied must remain enabled or its an error"); 
		log.debug("***********************************************************************************");
		enableDisable();

		// Check if all usersupplied params are still enabled.
		for (String p : saveUserSuppliedNames)
		{
			if (this.parameters.get(p).enabled == false)
			{
				parameterErrors.add(new FieldError(p, p + " precondition isn't met"));
				log.debug("ADD ERROR: " + p + " precondition isn't met");
			}
		}

		log.debug("***********************************************************************************");
		log.debug("EVAL Controls of all enabled params");
		log.debug("***********************************************************************************");

		// Now evaluate controls of all parameters that are enabled.
		for (String param : this.parameters.keySet())
		{
			Element element = this.parameters.get(param);
			if (element.enabled)
			{
				// Puts errors, if any, in parameterErrors.
				processControls(param);
			}
		}
		log.debug("***********************************************************************************");
		log.debug("EVAL required parameters present, if enabled.");
		log.debug("***********************************************************************************");
		validateRequiredParameters();

		if (parameterErrors.size() > 0)
		{
			throw new ValidationException(parameterErrors);
		}

		/*
			We don't have any errors, so build the parameter map to return by removing those that are disabled and those 
			that are empty as well as those of type InFile. Since we aren't raising any errors caller knows that all the 
			InFile params he passed in to use are valid.  (Sequence is equivalent to InFile).
		*/
		Map<String, String> visibleParams = new HashMap<String, String>();
		for (String param : this.parameters.keySet())
		{
			Element element = this.parameters.get(param);
			if (element.enabled)
			{
				if (element.value != null && !element.value.trim().equals("") && !element.type.equals("InFile") && !element.type.equals("Sequence"))
				{
					visibleParams.put(param + "_", element.value);
				}
			}
		}
		return visibleParams;
	}




	private PiseMarshaller initPiseMarshaller(URL url) 
	{
		if (url == null)
			throw new NullPointerException("Tool config file URL is null!");
		if (cfgMap.containsKey(url) == false)
		try 
		{
			PiseMarshaller pm = new PiseMarshaller(url.openStream());
			cfgMap.put(url, pm);
		} catch (IOException e) 
		{
			throw new RuntimeException("Cannot initialize PiseMarshaller.", e);
		}
		return cfgMap.get(url);
	}
	
	
	private String evaluatePerlStatement(String perlStatement) throws Exception 
	{
		return perlEval.evaluateStatement(perlStatement);
	}


	private boolean processPrecond(String paramName) throws Exception
	{
		// paramName argument doesn't have trailing underscore
		String precond = piseMarshaller.getPrecond(paramName);
		String vdef = piseMarshaller.getVdef(paramName);
		if (precond != null) 
		{
			log.debug("EVALUATE Precondition for " + paramName);
			precond = preparePerlExpression(precond, paramName, vdef);
			String perlPrecond = evaluatePerlStatement(precond);
			if (!Boolean.valueOf(perlPrecond))
			{
				log.debug("\tPrecond = false");
				return false;
			} else
			{
				log.debug("\tPrecond = true");
				return true;
			}
		}
		return true;
	}

	private void processControls(String paramName) throws Exception
	{
		List<PiseMarshaller.Control> controls = piseMarshaller.getCtrl(paramName);
		String vdef = piseMarshaller.getVdef(paramName);

		evaluateControls(controls, paramName, vdef); 
	}

	/*
		Replace instances of "defined $value" and "defined $var" within str with 0 or 1. 
		It's 0 if the parameter = "", 1 otherwise.   We use this mostly to check whether
		a parameter of type InFile has a value.

		Str is the str to search and replace.  ParamaterValue is the value to use for $value.
	*/
	private String replaceDefined(String str, String parameterValue)
	{
		StringBuffer buf = new StringBuffer();

		// Find the text "defined", followed by whitespace, followed by "$", followed by one or more
		// "word" characters (i.e letter, number or underscore).  Capture the "word" into group 1.
		Pattern p = Pattern.compile("defined\\s\\$(\\w+)");

		Matcher matcher = p.matcher(str);
		while(matcher.find())
		{
			/* 
				We should either have matched $value or $parameter.
			*/
			String var = matcher.group(1);
			if (var.equals("value"))
			{
				matcher.appendReplacement(buf, (parameterValue == "") ? "0" : "1");
			} else
			{
				boolean defined = (getValue(var) != "");
				matcher.appendReplacement(buf, defined ? "1": "0");
			}
		}
		matcher.appendTail(buf);
		return buf.toString();
	}


	/*
		Prepare a perl precond, control or warn expression, to be executed by our perlEval class.

		Replaces $var, $value, defined $var, etc with values from the parameter map, then 
		builds a perl expression of the form: (expr) ? print "true" : print "false
		and returns the expression.

		Calling code will pass this expression to "perl -e".  The stdout from running perl -e on the 
		expression will  produce the text "true" or "false" and we pass the stdout to java's Boolean.valueOf() to get 
		a boolean evaluation of the precond.

	*/
	private String preparePerlExpression(String precond, String paramName, String vdef) 
	{
		log.debug("\tOriginal Expression:      " + precond);
		String paramValue = getValue(paramName);
		precond = replaceDefined(precond, paramValue);

		//log.debug("Expression after replaceDefined:      " + precond);

		// look for $value, $vdef, $<var>
		Pattern p = Pattern.compile("\\$\\w*");
		Matcher m = p.matcher("" + precond);
		StringBuffer sb = new StringBuffer();

		while (m.find()) 
		{
			if (m.group().contains("$value"))
			{
				if (paramValue != null)
				{
					m.appendReplacement(sb, "\"" + paramValue + "\"");
				}
				// If paramValue is null, just leaves "$value" in the perl expression.
			}
			else if (m.group().contains("$vdef"))
			{
				m.appendReplacement(sb, "\"" + vdef + "\"");
			}
			else if (m.group().equalsIgnoreCase("$") == false) 
			{
				String myKey = m.group().substring(1);
				String theValue = getValue(myKey);
				if (theValue == null)
				{
					// leave the undefined variable reference in the expression and let perl handle it.
					log.debug("\tINVALID PRECONDITION - uses value of undefined parameter " + myKey + ". CORRECT THE PISE XML!" );
				} else
				{
					m.appendReplacement(sb,  "\"" + theValue  + "\"");
				}
			}
		}
		m.appendTail(sb);
		precond = sb.toString();

		precond = precond.replaceAll("false", "0");
		precond = precond.replaceAll("true", "1");

		// precond returns a True or False depends on the condition being verified
		precond = "(" + precond + ")? print \"true\" : print \"false\";";

		//log.debug("\tFinal Perl Expression: " + precond);
		return precond;
	}

	/*
		Controls are written to express an error condition when true.  For example to require runtime to be <= 168, you write
			"$runtime > 168.0"
		Returns true (ie. all's well) if there are no controls or all controls evaluate to false.
		For each control that is true, sets an error message in this.parameterErrors.
	*/
	private boolean evaluateControls(List<PiseMarshaller.Control> controls, String paramName, String vdef)
		throws Exception
	{
		int errorCount = 0;
		String perl;
		String evaluatedPerl; 
		if (controls == null)
		{
			return true;
		}
		for (PiseMarshaller.Control c : controls)
		{
			log.debug("EVALUATE Controls for parameter: " + paramName);
			perl = preparePerlExpression(c.perl, paramName, vdef);
			evaluatedPerl= evaluatePerlStatement(perl);
			log.debug("\tctrl: '" + perl + "' EVAL TO '" + evaluatedPerl + "'");
			if (Boolean.valueOf(evaluatedPerl) == true)
			{
				parameterErrors.add(new FieldError(paramName, c.message));
				log.debug("\tADD ERROR:" + paramName + ":" + c.message);
				errorCount += 1;
			} 
		}
		return errorCount == 0;
	}
	

	/*
		Build a map that has every visible parameter and every InFile or Sequence parameter from the pise document,
		in the order they appear in the pise document.

		But skip the parameters that user supplied.
	*/
	private Map<String, Element> populate(URL url) 
	{
		// LinkedHashMap maintains insertion order when iterating over the keys or elements.
		Map<String, Element> fields  = new LinkedHashMap<String, Element>();

		piseMarshaller = initPiseMarshaller(url);
		for (String param : piseMarshaller.getExtendedVisibleSet())
		{
			if (userSupplied.keySet().contains(param))
			{
				//log.debug("user supplied " + param + " so not adding to this.parameters");
				continue;
			}
			String value = null;
			String type = piseMarshaller.getType(param);

			// Add the parameters of type InFile and Sequence with value = empty string to indicate that the user hasn't set a value (yet).
			if (type.equals("InFile") || type.equals("Sequence"))
			{
				fields.put(param, new Element("", true, piseMarshaller.getType(param)));
				//log.debug("populate: " + param + "=''");
				continue;
			}

			// If there is a default value for the param ...
			String vdef = piseMarshaller.getVdef(param);
			if (vdef != null)
			{
				// Get rid of leading and trailing quotes if any.
				vdef = vdef.trim();
				if (vdef.startsWith("\""))
				{
					vdef = vdef.replaceFirst("\"", "");
				}
				if (vdef.endsWith("\""))
				{
					vdef = vdef.substring(0, vdef.length() - 1);
				}
				value = vdef;
			} else if (type != null && type.equals("Switch"))
			{
				value = "0";
			} else
			{
				value = "";
			}
			fields.put(param, new Element(value, true, type));
			//log.debug("populate: " + param + "=" + "'" + value + "'");
		}
		return fields;
	}


	/*
		Loop over all visible and InFile/Sequence parameter in the pise document and
		eval their preconds mark each one enabled or disabled.
	*/
	private void enableDisable() throws Exception
	{
		for (String param : parameters.keySet())
		{
			if (piseMarshaller.getPrecond(param) != null)
			{
				Element element = parameters.get(param); 
				if (processPrecond(param) == true)
				{
					element.enabled = true;
				} else
				{
					element.enabled = false;
				}
			}
		}
	}

	String getValue(String param)
	{
		// Look for value in user supplied parameters first.
		String value = null;

		if (userSupplied != null && (value = this.userSupplied.get(param)) != null)
		{
			return value;
		}

		Element element = this.parameters.get(param);
		if (element == null)
		{
			log.debug("PISE FILE ERROR, expression uses undefined parameter: " + param);
			return null;
		} 
		// When one parameter has an expression that references the value of a disabled parameter
		// return "0" if it's a switch or "" for anything else.  Similar to what the gui does in
		// code generator pise2JSP.ftl getValue() function.
		if (!element.enabled) 
		{
			if (element.type.equals("Switch"))
			{
				log.debug("\tGETVALUE of disabled parameter: " + param + " returns '0'");
				return "0";
			} else
			{
				log.debug("\tGETVALUE of disabled parameter: " + param + " returns ''");
				return "";
			}
		}
		return element.value;
	}

	/*
		If no precond or precond is true, and ismandatory=true, then make sure we've got a value for it.
		If not, add an error to parameterErrors.
	*/
	private void validateRequiredParameters() throws Exception
	{
		for (String param : piseMarshaller.getRequiredSet())
		{
			if (processPrecond(param) && (getValue(param) == null))
			{
				parameterErrors.add(new FieldError(param, param + " is required."));
				log.debug("ADD ERROR " + param + " is required.");
			}
		}
	}
}
