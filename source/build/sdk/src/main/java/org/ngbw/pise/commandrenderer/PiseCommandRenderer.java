package org.ngbw.pise.commandrenderer;

import java.io.IOException;
import java.io.StringBufferInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ngbw.sdk.ValidationException;
import org.ngbw.sdk.api.tool.CommandRenderer;
import org.ngbw.sdk.common.util.StringUtils;
import org.ngbw.sdk.common.util.SuperString;
import org.ngbw.sdk.tool.RenderedCommand;
import org.ngbw.pise.commandrenderer.PiseMarshaller;
import org.ngbw.sdk.api.tool.FieldError;


/**
 * This Class implements CommandRenderer this implementation takes care of
 * PiseXML and Perl contents
 * <br />
 * @author Rami
 * <br />
 * @author R. Hannes Niedner
 * 
 */

public class PiseCommandRenderer implements CommandRenderer 
{
	private static Log log = LogFactory.getLog(PiseCommandRenderer.class.getName());

	private final Map<URL , PiseMarshaller> cfgMap = new HashMap<URL , PiseMarshaller>();

	private PiseMarshaller piseMarshaller;

	//parameter name -> parameter value
	private Map<String, String> parameters;

	// private variable for the toUnixCmd() method
	private String[] unixCmdGroup = null;
	private String[] unixCmdGroupNegative = null;
	private Map<String, String[]> paramFiles = null;
	
	private RenderedCommand renderedCommand= null;
	private final static String SCHEDULER_CONF = "scheduler.conf";
	private PerlEval perlEval = null;

	List<FieldError> parameterErrors = new ArrayList<FieldError>();
	
	public PiseCommandRenderer() 
	{
		super();
		init();
	}
	
	private void init() 
	{
		parameters = null;
		piseMarshaller = null;
		unixCmdGroup = new String[100];
		unixCmdGroupNegative = new String[100];
		paramFiles = new HashMap<String, String[]>();
		renderedCommand = new RenderedCommand();
	}


	// TODO:
	public RenderedCommand validate(URL url, Map<String, String> parameters) 
	{
		log.debug("\n********** PiseCommmandRenderer VALIDATE submission ***********");
		return render(url, parameters, true);
	}

	public RenderedCommand render(URL url, Map<String, String> parameters) 
	{
		log.debug("\n********** PiseCommmandRenderer RENDER submission ***********");
		return render(url, parameters, false );
	}

	/**
		url Url of the pise xml file 

		param parameters A map of parameter name to value, where parameter is one of the visible
		parameters in the pise xml (including InFile and Sequence parameters, even the hidden "isinput"
		primary input).   These are just the parameters that the user has supplied a value for, and
		that aren't disabled.  It also includes ones for which the gui (or the GuiSimulator for the
		rest api) supplied a default value.

		For InFile and Sequence types, the value of the parameter is the source document ID, as a string.

		Caller must verify that entries in parameters map meet certain criteria BEFORE 
		calling this method: 

		- keys must correspond to parameters in the pise xml file 
		- values of the correct type as specified by the pise file (for example, integer, string, double)
		- values are in min/max range specified by the pise xml

		This method evaluates perl precond and ctrls elements for the parameters. Throws JobValidationException if a ctrl is violated.  
		If precond for a parameter is not met, the parameter is skipped, in the sense that if it has a format element, the code in
		the format element will not be generated.  However, the value of the parameter may be referenced in another parameter's code, 
		so all parameters that have preconds and are referenced elsewhere MUST supply a default value with vdef element.  If a precond isn't met
		this method replaces the value of the parameter in the parameter map with the param's default value.
	*/
	private RenderedCommand render(URL url, Map<String, String> parameters, boolean validateOnly) 
	{
		try 
		{
			log.debug("##########################################################################################");
			log.debug("Render command from " + parameters.size() + " visible parameters using config:" + url +
				", validateOnly = " + validateOnly);

			/*
				Keep a pointer to the passed in parameters map.  This method will modify the map - 
				upon return, caller will see a modified map.  For example, this method adds dummy 
				parameters that correspond to the parameter files that are generated by the pisexml.
			*/
			this.parameters = parameters;
			piseMarshaller = initPiseMarshaller(url);

			perlEval = new PerlEval();
			perlEval.initialize();
			List<String> commandList = toUnixCmd(validateOnly);
			perlEval.terminate();

			//log.debug("toUnixCmd returns a list with " + commandList.size() + " elements");
			String[] commandArray = new String[commandList.size()];
			renderedCommand.setCommand(commandList.toArray(commandArray));
			if (log.isDebugEnabled()) 
			{
				log.debug("Returned command: " + StringUtils.join(renderedCommand.getCommand(), " "));
				Map<String, String> input = renderedCommand.getInputFileMap();
				Map<String, String> output = renderedCommand.getOutputFileMap();
				for(String parameter : input.keySet())
					log.debug("Input: " + parameter + ": " + input.get(parameter));
				for(String parameter : output.keySet())
					log.debug("Output: " + parameter + ": " + output.get(parameter));
			}
			setSchedulerProperties();
			return renderedCommand;
		}
		catch(ValidationException jve)
		{
			throw jve;
		}
		catch (Exception err) 
		{
			throw new RuntimeException(err);
		}
		finally
		{
			if (perlEval != null)
			{
				perlEval.cleanup();
			}
		}
	}

	/**
		If the xml specified a param file with a specific name (given by SCHEDULER_CONF)
		we expect that file to contain properties for scheduling the job.   We load the contents
		of the file into the renderedCommand.schedulerProperties.
	*/
	private void setSchedulerProperties()
	{
		Map<String, byte[]> inputData = new TreeMap<String, byte[]>();
		Map<String, String> inputFileNames = renderedCommand.getInputFileMap();

		String p = null;
		for (Iterator<Map.Entry<String, String>> names = inputFileNames.entrySet().iterator() ; names.hasNext() ; )
		{
			Map.Entry<String, String> entry = names.next();
			String parameter = entry.getKey();
			String fileName = entry.getValue();
			if (fileName.equals(SCHEDULER_CONF))
			{
				log.debug("Found " + SCHEDULER_CONF + " in renderedCommand.inputFileMap, for parameter: " + parameter); 
				p = parameter;
				break;
			}
		}
		String value;
		if (p == null || (value = parameters.get(p)) == null)
		{
			log.debug("Parameter " + p + " value is null.");
			return;
		}
		log.debug("Parameter " + p + " value is:" + new String(value));
		try
		{
			renderedCommand.getSchedulerProperties().load(new StringBufferInputStream(value));
		}
		catch (Throwable t)
		{
			log.warn("Error loading scheduler properties", t);
		}
	}


	//initialize the JAXB Marshaller
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
	
	/* 
		all parameter names in the submitted maps have an underscore appended.  This returns the names
		without the trailing underscore.
	*/
	private Set<String> getParameterSet() 
	{
		Set<String> keySet = new HashSet<String>();
		for (String key : parameters.keySet()) 
		{
			String paramName = key.replaceFirst("_$", "");
			keySet.add(paramName);
		}
		return keySet;
	}
	
	private String getParameterKey(String parameter) 
	{
		return parameter + "_";
	}
	
	private String getParameterValue(String parameter) 
	{
		String key = getParameterKey(parameter);
		if (parameters.containsKey(key) && parameters.get(key) != null)
			return new String(parameters.get(key));
		else
			return null; 
	}
	private void setParameterValue(String parameter, String value) 
	{
		String key = parameter + "_";
		parameters.put(key, value);
	}

	private void removeParameter(String parameter) 
	{
		String key = parameter + "_";
		parameters.remove(key);
	}


	private void setInputFileName(String parameter, String fileName) 
	{
		String key = parameter + "_";
		renderedCommand.getInputFileMap().put(key, fileName);
	}
	private void setOutputFileName(String parameter, String fileName) 
	{
		renderedCommand.getOutputFileMap().put(parameter, fileName);
	}
	
	private String evaluatePerlStatement(String perlStatement) throws Exception 
	{
		return perlEval.evaluateStatement(perlStatement);
	}


	/*
		If no precond or precond is true, and ismandatory=true, then make sure we've got a value for it.
		If not, add an error to parameterErrors.
	*/
	public void validateRequiredParameters() throws Exception
	{
		for (String param : piseMarshaller.getRequiredSet())
		{
			if (processPrecond(param) && (getParameterValue(param) == null))
			{
				parameterErrors.add(new FieldError(param, param + " is required."));
				log.debug("\tERR " + param + " is required.");
			}
		}
	}


	private List<String> toUnixCmd(boolean validateOnly) throws IOException, InterruptedException, ExecutionException, Exception
	{

		List<String> commandList = new ArrayList<String>();

		log.debug("##########################################################################################");
		log.debug("Validating required parameters"); 
		validateRequiredParameters();

		log.debug("##########################################################################################");
		log.debug("Evaluate visible parameters"); 
		for (String paramName : getParameterSet()) 
		{
			processParameter(paramName, false);
		}

		//If we've found errors, bail out.  Errors that occur after this are NOT expected and are most likely a pisexml error.
		if (parameterErrors.size() > 0)
		{
			throw new ValidationException(parameterErrors);
		}

		// These are the parameters hidden from the GUI, but necessary for generating the command line correctly
		log.debug("##########################################################################################");
		log.debug("toUnixCmd: processing " + piseMarshaller.getHiddenSet().size() + " HIDDEN parameters");
		log.debug("Evaluate hidden parameters"); 
		for (String paramName : piseMarshaller.getHiddenSet())
			processParameter(paramName, true);

		/*
			These are the parameters that generate an outfile, they are hidden from the GUI since the GUI dosen't
			give the user the possibility to specify the names of the output files.  
		*/
		log.debug("##########################################################################################");
		log.debug("toUnixCmd: processing " + piseMarshaller.getOutfileSet().size() + " OUTFILESET parameters");
		for (String paramName : piseMarshaller.getOutfileSet())
			processParameter(paramName, true);

		// Not sure of the difference bewteen Pise ResultFile and piseOutfile, but here we handle ResultFiles. 
		log.debug("toUnixCmd: processing " + piseMarshaller.getResultSet().size() + " RESULTSET parameters");
		for (String paramName : piseMarshaller.getResultSet()) 
			processParameter(paramName, true);

		/*
			All kinds of parameters, visible, hidden, etc can specify text to be placed in a parameter
			file instead of on the commandline.  The calls to processParameter above put entries in 
			paramFiles each time they find a parameter that puts text in a parameter file.  The map is keyed by 
			parameter file name.  Here we append all the lines that go into each parameter file into a single
			string and we add the param file name/ param file contents to our list of input files.
		*/
		for(String key : paramFiles.keySet()) 
		{
			// Append the lines that go in the parameter file.
			String[] values = paramFiles.get(key);
			StringBuilder valueSb = new StringBuilder();
			for(String value : values) 
			{
				if (value != null)
				{
					valueSb.append(value);
				}
			}	
			String value = valueSb.toString();

			/*
				This adds an entry in the parameter map, with key = parameter file name, value = contents.
				See NEWLINE comment at end of file.
			*/
			setParameterValue(key, value.replace("\\n", "\n"));

			/*
				This adds an entry in the input file map with key = parameter file name, value = parameter file name.
			*/
			setInputFileName(key, key);
		}

		// 3- Creating the command line by ordering them with respect to their group
		if (unixCmdGroup[0] != null) 
		{
			commandList.add(unixCmdGroup[0]);
			log.debug("for unixCmdGroup[0] commandList.add: " + unixCmdGroup[0]);
		}
		
		for (int j = 1; j < unixCmdGroup.length; j++) 
		{
			// Arranging positve groups
			if (unixCmdGroup[j] != null) 
			{
				commandList.add(unixCmdGroup[j]);
				log.debug("for unixCmdGroup[" + j + "] commandList.add: " + unixCmdGroup[j]);
			}
		}

		// Arranging the negative groups
		for (int k=1 ; k <unixCmdGroupNegative.length - 1; k++) 
		{
			if (unixCmdGroupNegative[k] != null) 
			{
				commandList.add(0, unixCmdGroupNegative[k]);
				log.debug("for unixCmdGroupNegative[" + k + "] commandList.add: " + unixCmdGroupNegative[k]);
			}
		}
		return commandList;
	}

	private boolean processPrecond(String paramName) throws Exception
	{
		String precond = piseMarshaller.getPrecond(paramName);
		String vdef = piseMarshaller.getVdef(paramName);
		if (precond != null) 
		{
			log.debug("\tEVALUATE Precondition for " + paramName);
			precond = preparePerlExpression(precond, paramName, vdef);
			String perlPrecond = evaluatePerlStatement(precond);
			if (!Boolean.valueOf(perlPrecond))
			{
				log.debug("\tprecond is false");
				return false;
			}
		}
		log.debug("\tprecond is true");
		return true;
	}

	private void processControls(String paramName) throws Exception
	{
		List<PiseMarshaller.Control> controls = piseMarshaller.getCtrl(paramName);
		String vdef = piseMarshaller.getVdef(paramName);

		log.debug("EVALUATE Controls for InFile " + paramName);
		evaluateControls(controls, paramName, vdef); 
	}

	private void processParameter(String paramName, boolean hidden) 
		throws IOException, InterruptedException, ExecutionException, Exception
	{
		log.debug("START: processParameter: " + paramName + " (hidden: " + hidden + ")");

		String parameterValue = hidden ? null : getParameterValue(paramName);

		String format = piseMarshaller.getFormat(paramName);
		String group = piseMarshaller.getGroup(paramName);
		String precond = piseMarshaller.getPrecond(paramName);
		String type = piseMarshaller.getType(paramName);
		String vdef = piseMarshaller.getVdef(paramName);
		String separator = piseMarshaller.getSeparator(paramName);
		String filenames = piseMarshaller.getFileNames(paramName);
		String paramfile = piseMarshaller.getParamFile(paramName);
		List<PiseMarshaller.Control> controls = piseMarshaller.getCtrl(paramName);

		if (parameterValue == null || (parameterValue.length() < 100))
		{
			log.debug("\t" + paramName +"=" + parameterValue + ", type=" + type);
		} else
		{
			log.debug("\t" + paramName +"=" + parameterValue.substring(0, Math.min(100, parameterValue.length())) + 
				"...(truncated), type=" + type);
		}

		@SuppressWarnings("unused")
		String isCommand = piseMarshaller.getIsCommand(paramName);
		@SuppressWarnings("unused")
		String ishidden = piseMarshaller.getIsHidden(paramName);

		// With flist, user chooses a label that pise maps to a string of code. 
		String flist = piseMarshaller.getflistValue(paramName, parameterValue);
		String perlFormat = null;
		String perlPrecond = null;

		if (type.equals("Results") && group == null)
		{
			group = "-99"; // this group is not going to be used at all
		}
		log.debug("Group: " + group);
		int groupValue=1;
		if (group !=null)
		{
 			groupValue = Integer.parseInt(group);
		}
		/*
			here we assume that flist and format are mutually exclusive however the case where we have a format and a flist, 
			the flist will override format I don't see the sense of having both in the same parameter
		*/
		if (flist != null)
		{
			format = flist;
		}
		/*
			The front end code is responsible for inserting the separator character, if any,
			and concatenating the list elements.  However, if the pise doesn't specify a separator,
			the front end uses "@" as a separator because it must maintain the distinct elements in order
			to repopulate a form from them. THIS MEANS YOU CAN'T USE AN '@' in a LIST ELEMENT.
		*/
		if (type != null && type.equals("List"))
		{
			if (separator == null)
			{
				 // Remove the dummy '@' that the front end inserted and concatenate the elements since there
				 // is not separator specified in the pise xml.
				 setParameterValue(paramName, SuperString.valueOf(parameterValue, '@').concatenate());
			} else
			{
				;
			} 
		}
		if (precond != null) 
		{
			log.debug("Evaluate Precondition for Hidden Parameter");
			precond = preparePerlExpression(precond, paramName, vdef);
			perlPrecond = evaluatePerlStatement(precond);
			log.debug("Precond evaluation: " + perlPrecond + ", boolean value is " +  Boolean.valueOf(perlPrecond));
			if (!Boolean.valueOf(perlPrecond))
			{
				log.debug("\tPrecondition not satisfied, not generating code for " + paramName);
				return;
			}
		}
		if (evaluateControls(controls, paramName, vdef) == false)
		{
			// If controls are violated quit processing this parameter
			log.debug("Return early because evaluateControls returned false");
			return;
		}

		log.debug("format: " + (format == null ? "null" : format));
		if (format != null) 
		{
			format = restitutionFormat(format, paramName, vdef);
			perlFormat = evaluatePerlStatement(format);
			log.debug("Format evaluation: " + perlFormat);
		}
		if (perlFormat != null) 
		{
			log.debug("paramfile: " + (paramfile == null ? "null" : paramfile));
			if (paramfile != null) 
			{
				String[] value;
				if (paramFiles.containsKey(paramfile) == false || paramFiles.get(paramfile) == null)
				{
					paramFiles.put(paramfile, new String[100]);
				}	
				value = paramFiles.get(paramfile);
				if (value[groupValue] != null)
				{
					value[groupValue] += perlFormat;
				}
				else
				{
					value[groupValue] = perlFormat;
				}
				log.debug("parameter: " + paramName + " put in inputDataMap: '" + paramfile + "' -> '" + value[groupValue] + "'");
			} else 
			{
				if (groupValue >= 0) 
				{
					if (unixCmdGroup[groupValue] != null)
					{
						unixCmdGroup[groupValue] += " " + perlFormat;
					}
					else
					{
						unixCmdGroup[groupValue] = perlFormat;
					}
					log.debug("unixCmdGroup[" + groupValue + "] = " + unixCmdGroup[groupValue]);
				} else 
				{
					if (unixCmdGroupNegative[Math.abs(groupValue)] != null)
					{
						unixCmdGroupNegative[Math.abs(groupValue)] += " " + perlFormat;
					}
					else
					{
						unixCmdGroupNegative[Math.abs(groupValue)] = perlFormat;
					}	
					log.debug("unixCmdGroupNegative[" + Math.abs(groupValue) + "] = " + unixCmdGroupNegative[Math.abs(groupValue)]);
				}
			}
		}

		if (filenames != null) 
		{
			// filenames not null for infile and sequence means we have assigned standard names for these input files
			if (type.equals("InFile") || type.equals("Sequence"))
			{
				setInputFileName(paramName, filenames);
				log.debug("setInputFileName: '" + paramName + "' -> '" + filenames + "'");
			}
			// Most output filenames are given in Results but can also be defined in Switch, List, OutFile, etc.
			else 
			{
				setOutputFileName(paramName, filenames);
				log.debug("setOutputFileName: '" + paramName + "' -> '" + filenames + "'");
			}
		} else if (type.equals("InFile") || type.equals("Sequence"))
		{
			// No filename given in pise InFile so caller will use source document name as filename 
			// See TaskInitiate.stageInputData().
			setInputFileName(paramName, "");
			log.debug("setInputFileName: '" + paramName + "' -> '" + " empty string.");
		}
		log.debug("END : processParameter: " + paramName + " (hidden: " + hidden + ")\n");
	}

	/*
		- replaces "defined $value", and "defined $param_name" in "str" argument.
		- "parameterValue" argument is the value of the current parameter, i.e the one that has the code we're evaluating.

		This is frequently used to check whether a data item has been supplied for
		a parameter of type InFile or Sequence.  If the user hasn't supplied a data item,
		there is no default, so the parameter will *not* be in our map.

		This version is different than replaceDefined() in GuiSimulator.  In GuiSimulator
		we put all the parameters from the pise file into our map with a value of empty
		string (if there is no vdef and user hasn't entered anything) so we don't test
		for null there, we test for "".   However, once GuiSimulator is finished we 
		remove all the empty string parameters and only pass along those that have 
		non-empty values, so here we need to test for the parameter not being in
		our map at all instead of testing whether it is the empty string.
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
			String var = matcher.group(1);
			if (var.equals("value"))
			{
				if (parameterValue == null)
				{
					matcher.appendReplacement(buf,  "0");
				} else
				{
					matcher.appendReplacement(buf,  "1");
					//matcher.appendReplacement(buf, (parameterValue == "") ? "0" : "1");
				}
			}  else
			{
				String value = getParameterValue(var);
				matcher.appendReplacement(buf, (value != null) ? "1": "0");
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
		String paramValue = getParameterValue(paramName);
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
				} else
				{
					// If paramValue is null, just leaves "$value" in the perl expression.
					log.warn("\tpreparePerlExpression '$value  NOTHING TO REPLACE IT WITH");
				}
			}
			else if (m.group().contains("$vdef"))
			{
				if (vdef == null)
				{
					log.warn("\tpreparePerlExpression '$value' NOTHING TO REPLACE IT WITH");
				} else
				{
					m.appendReplacement(sb, "\"" + vdef + "\"");
				}
			}
			else if (m.group().equalsIgnoreCase("$") == false) 
			{
				String myKey = m.group().substring(1);
				String theValue = getParameterValue(myKey);
				if (theValue == null)
				{
					// leave the undefined variable reference in the expression and let perl handle it.
					log.warn("\tINVALID PRECONDITION - uses value of undefined parameter " + myKey + ".  Use 'defined'" );
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

		log.debug("\tFinal Perl Expression: " + precond);
		return precond;
	}



	private String restitutionFormat(String format, String paramName, String vdef) 
	{

		log.debug("Original Format:      " + format);
		
		String paramValue = getParameterValue(paramName);
		format = replaceDefined(format, paramValue);

		log.debug("Format after replaceDefined:      " + format);

		Pattern p = Pattern.compile("\\$\\w*");
		Matcher m = p.matcher(format);

		while (m.find())
		{
			if (m.group().contains("$value")) 
			{
				if (paramValue == null)
				{
					log.warn("'$value' is NULL");
				} else
				{
					format = format.replace("$value", paramValue);
				}
			} else if (m.group().contains("$vdef")) 
			{
				if (vdef == null)
				{
					log.warn("'$vdef' is NULL");
				} else
				{
					format = format.replace("$vdef", vdef);
				}
			} else 
			{
				String myKey = m.group().substring(1);
				String myValue = null;

				if (getParameterSet().contains(myKey) == false)
				{
					/*
						TL: modified for raxmlhpc.xml "model" parameter.  Need to get default value of parameters that 
						haven't been set in the gui but are referenced in this parameter's code element.

						2/23/2015 TL: I'm not sure this is still true.   Parameters like 'dna_grtcat' do reference others
						like 'invariable' which is not mandatory.  However, if it has a vdef and it's precond was met,
						it should be in our map.  If that isn't guaranteed, based on the precond or lack of a vdef,
						I think we can rewrite the format statements to be conditional on, say 'defined $invariable'.
					*/
					log.warn("INVALID FORMAT statement references undefined parameter " + myKey + ", will use vdef or empty string.");
					myValue = piseMarshaller.getVdef(myKey);
					if (myValue == null) // no vDef element in the pise xml
					{
						log.warn("INVALID FORMAT statement references undefined parameter " + myKey + ", will use empty string.");
						myValue = "";
					} else
					{
						log.warn("INVALID FORMAT statement references undefined parameter " + myKey + ", will use vdef '" + myValue + "'");
					}
				} else
				{
					myValue = getParameterValue(myKey);
				}
				format = format.replace(m.group(), myValue);
			}
		}
		/*
			A format can be a simple "..." or a condition (...)? "...":"..."
			If it's ? expression we stick a "print" in front of both components.
			But if it starts with "<<" we're going to assume the whole thing is a here document
			and not  process the ? operator.  This isn't exactly what a real perl interpreter
			would do but I think it's close enough for the pise and ngbw xml files we deal with these
			days.
		*/
		if (!format.trim().startsWith("<<") && format.contains("?")) 
		{
			format = format.substring(0, format.indexOf("?") + 1) + " print " + format.substring(format.indexOf("?") + 1);
			format = format.substring(0, format.indexOf(":") + 1) + " print " + format.substring(format.indexOf(":") + 1);
		} else 
		{
			format = "print " + format;
		}
		format = format.replaceAll("false", "0");
		format = format.replaceAll("true", "1");

		log.debug("Parsed Format: " + format);

		return format;
	}

	/*
		Controls are written to express an error condition when true. 
		For example to require runtime to be <= 168, you write
			"$runtime > 168.0"

		Returns true (ie. all's well) if there are no controls or all controls evaluate to false.
		For each control that is true, sets an error message in ??? TODO
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
			perl = preparePerlExpression(c.perl, paramName, vdef);
			evaluatedPerl= evaluatePerlStatement(perl);
			log.debug("ctrl: '" + perl + "' EVAL TO '" + evaluatedPerl + "'");
			if (Boolean.valueOf(evaluatedPerl) == true)
			{
				parameterErrors.add(new FieldError(paramName, c.message));
				errorCount += 1;
			} 
		}
		return errorCount == 0;
	}
}
	


/*
	NEWLINE replacement:  (search for "NEWLINE" to see what this comment is referring to).

	This code replaces the two character sequence of <backslash, n> with a single newline character.
	Since evalutePerl() returns the stdtout trimmed of leading and trailing whitespace, if we
	want to have a parameter be followed by a newline, we need the result of the perl evaluation
	to end not with a newline character but with a backslash followed by an n, which we'll replace
	with a newline right here.

	note that when you print a java string, as the logging messages in this file do, if
	the string contains an actual newline, it will print on multiple lines.  If you see
	"\n" in the value displayed, it means the string contains two characters: a backslash
	followed by an n.  This is mostly what we see because if you put "\n" in an xml file
	element, the xml unmarshaller delivers this as a java string containing a backslash
	followed by an n.

	Both perl (if using double quoted strings) and java interpret "\\n" as the two character
	sequence <backslash, newline>.  The first quote escapes the second, yielding a literal
	backslash and the n is unquoted.  In a perl single quoted string, '\\n' is three characters:
	two backslashes followed by an n.  If you have a format like '$value\\n', with single quotes,
	perl leaves the \\n alone and when we get here we replace the \n with a newline, so we're
	left with a single backslash followed by a newline ...  probably not what you want.
	On the other hand, "$value\\n" works because when ask perl to print this it gives us
	the value followed by a backslash follwed by an n.
*/
