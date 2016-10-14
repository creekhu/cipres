package org.ngbw.pise.commandrenderer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import org.ngbw.pise.commandrenderer.pise.Attributes;
import org.ngbw.pise.commandrenderer.pise.Code;
import org.ngbw.pise.commandrenderer.pise.Ctrl;
import org.ngbw.pise.commandrenderer.pise.Ctrls;
import org.ngbw.pise.commandrenderer.pise.Filenames;
import org.ngbw.pise.commandrenderer.pise.Flist;
import org.ngbw.pise.commandrenderer.pise.Format;
import org.ngbw.pise.commandrenderer.pise.Group;
import org.ngbw.pise.commandrenderer.pise.Name;
import org.ngbw.pise.commandrenderer.pise.Parameter;
import org.ngbw.pise.commandrenderer.pise.Parameters;
import org.ngbw.pise.commandrenderer.pise.Paramfile;
import org.ngbw.pise.commandrenderer.pise.Pise;
import org.ngbw.pise.commandrenderer.pise.Precond;
import org.ngbw.pise.commandrenderer.pise.Separator;
import org.ngbw.pise.commandrenderer.pise.Value;
import org.ngbw.pise.commandrenderer.pise.Vdef;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Pise Marshaller contains all the methods to  UnMarshall 
 * a Pise XML file
 * It provides also methods to get the values/contents 
 * 
 * @author Rami
 *
 */

public class PiseMarshaller {
	private static Log log = LogFactory.getLog(PiseMarshaller.class.getName());

	private JAXBContext jc;
	private Pise pise;

	private Set<String> hiddenSet = new LinkedHashSet<String>();
	private Set<String> outfileSet = new LinkedHashSet<String>();
	private Set<String> resultSet = new LinkedHashSet<String>();
	private Set<String> infileSet = new LinkedHashSet<String>();
	private Set<String> visibleSet = new LinkedHashSet<String>();
	private Set<String> requiredSet = new LinkedHashSet<String>();
	private Set<String> extendedVisibleSet = new LinkedHashSet<String>();

	public static class Control
	{
		public Control(String perl, String message)
		{
			this.perl = perl;
			this.message = message;
		}
		public String perl;
		public String message;
	}

	public PiseMarshaller(InputStream piseXMLIs) {
		init(piseXMLIs);
	}

	public PiseMarshaller(File piseXMLFile) {
		try {
			InputStream is = new FileInputStream(piseXMLFile);
			init(is);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	protected void init(InputStream is) {
		
		try 
		{
			// START: resolve included DOCTYPE and ENTITY files from our jar, not filesystem or url
			// Depends on  pise xml files must using PUBLIC Ids not SYSTEM Ids.
			EntityResolver resolver = new EntityResolver() 
			{
				public InputSource resolveEntity(String publicId, String systemId) 
				{
					//log.debug("systemId is " + systemId + ", publicId is " + publicId);
					InputStream in = getClass().getResourceAsStream(  "/pisexml/" + publicId);
					//log.debug("created inputstream, in=" + in);
					if (in == null)
					{
						return null;
					}
					return new InputSource( in );
				}
			};
			//package name = namespace
			jc = JAXBContext.newInstance("org.ngbw.pise.commandrenderer.pise");
			Unmarshaller u = jc.createUnmarshaller();

			XMLReader xmlreader = XMLReaderFactory.createXMLReader();
			xmlreader.setEntityResolver(resolver);
			InputSource input = new InputSource(is);
			Source source = new SAXSource(xmlreader, input);
			pise = (Pise) u.unmarshal(source);
			// END: resolve included DOCTYPE and ENTITY files from our jar, not filesystem or url

			//pise = (Pise) u.unmarshal(is);

			// Get Hidden and Output parameters
			Parameters parameters = pise.getParameters();
			Recursive(parameters);

		} catch (JAXBException e) {
			log.error("", e);
			throw new RuntimeException(e);
		}
		catch(SAXException se)
		{
			log.error("", se);
			throw new RuntimeException(se);
		}
	}

	/**
	 * 
	 *
	 */

	private Object getRecursive(Parameters parameters, String parameterName,
			String element) {

		List<Parameter> paramList = parameters.getParameter();

		for (int i = 0; i < paramList.size(); i++) {
			boolean afterParagraph = false;
			Parameter param = paramList.get(i);

			if (param.getType().contains("Paragraph")) {
				afterParagraph = true;
				Object found = null;
				found = getRecursive(param.getParagraph().getParameters(),
						parameterName, element);
				if (found != null)
					return found;
			}

			if (!afterParagraph)

			{
				List<?> nameAndAttributes = param.getNameAndAttributes();

				if (nameAndAttributes.size() > 1) {
					Name name = (Name) nameAndAttributes.get(0);

					if (name.getContent().equals(parameterName)) {

						Attributes attribute = (Attributes) nameAndAttributes
								.get(1);
						for (int j = 0; j < attribute.getPromptOrInfoOrFormat()
								.size(); j++) {
							List<?> attributesList = attribute
									.getPromptOrInfoOrFormat();

							if (element.equals("Vdef"))
								return attributesList;

							// exception
							if (element == "Type" || element == "iscommand"
									|| element == "ismandatory"
									|| element == "ishidden")
								return param;

							// for all other methods; we only need attributesList.get(k)
							// --> which represents the attributes
							for (int k = 0; k < attributesList.size(); k++)
								if (attributesList.get(k).toString().contains(
										element))
									return attributesList.get(k);
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Find the Format in the PiseXML for parameterName
	 * @param parameterName
	 * @return dataFormat
	 */
	public String getFormat(String parameterName) {

		String result = null;
		Parameters parameters = pise.getParameters();

		Format format = (Format) getRecursive(parameters, parameterName,
				"Format");
		if (format != null) {
			List<?> languageAndCode = format.getLanguageAndCode();

			if (languageAndCode.size() > 1) //this Means it has at least one langauge and  one code
			{
				Code code = (Code) languageAndCode.get(1);
				// this method is not complete because :
				// 1- it can have code not perl, we should test language to perl
				//	2- it can have multiple tag of language code, we should iterate
				// we assume here that the code language is perl and the code attached is in one statement
				result = code.getContent();
			}
		}

		else {
			// format dosen't exist or format is empty <format/>
		}
		return result;
	}

	/*
		Same comment as for getFormat applies here - we are expecting perl code only.
	*/
	public List<Control> getCtrl(String parameterName) 
	{
		Parameters parameters = pise.getParameters();
		Ctrls ctrls = (Ctrls) getRecursive(parameters, parameterName, "Ctrls");
		if (ctrls == null)
		{
			return null;
		}
		List<Control> controls = new ArrayList<Control>();
		for (Ctrl c : ctrls.getCtrl())
		{
			String perl;
			String message;
			List<?> languageAndCode = c.getLanguageAndCode();
			if (languageAndCode.size() > 1) 
			{
				Code code = (Code) languageAndCode.get(1);
				perl = code.getContent();
				message = c.getMessage().getContent();
				controls.add(new Control(perl, message));
			}
		}
		return controls;
	}

		/**
		 * Find the Group in the PiseXML for parameterName
		 * @param parameterName
		 * @return group
		 */
		public String getGroup(String parameterName) {

			String result = null;
			Parameters parameters = pise.getParameters();

			Group group = (Group) getRecursive(parameters, parameterName, "Group");
			if (group != null) {

				result = group.getContent();
			} else {
				// group dosen't exist or Group is empty <group/>
			}
			return result;

		}

		/**
		 * Find the Type in the PiseXML for parameterName
		 * @param parameterName
		 * @return type
		 */
		public String getType(String parameterName) {

			String result = null;
			Parameters parameters = pise.getParameters();

			Parameter param = (Parameter) getRecursive(parameters, parameterName,
					"Type");
			if (param != null) {

				result = param.getType();
			}

			return result;
		}

		/**
		 * Find if the parameter is the command 
		 * @param parameterName
		 * @return isCommand
		 */
		public String getIsCommand(String parameterName) {

			String result = null;
			Parameters parameters = pise.getParameters();

			Parameter param = (Parameter) getRecursive(parameters, parameterName,
					"iscommand");
			if (param != null) {

				result = param.getIscommand();
			}

			return result;
		}

		/**
		 * Find if the parameter is hidden from the user
		 * @param parameterName
		 * @return hidden
		 */
		public String getIsHidden(String parameterName) {

			String result = null;
			Parameters parameters = pise.getParameters();

			Parameter param = (Parameter) getRecursive(parameters, parameterName,
					"ishidden");
			if (param != null) {

				result = param.getIshidden();
			}

			return result;
		}

		/**
		 * Find the isMandatory value in the PiseXML for parameterName
		 * @param parameterName
		 * @return mandatory
		 */
		public String getIsMandatory(String parameterName) {

			String result = "false";
			Parameters parameters = pise.getParameters();

			Parameter param = (Parameter) getRecursive(parameters, parameterName,
					"ismandatory");
			if (param != null) {
				result = param.getIsmandatory();

			}

			return result;
		}

		/**
		 * Find the high level Command in the Pise XML 
		 * @return command
		 */
		public String getCmd() {

			return pise.getCommand().getContent();
		}

		/**
		 * Find the Separator in the PiseXML for parameterName
		 * A separator is used for lists with multiple choices 
		 * @param parameterName
		 * @return separator
		 */
		public String getSeparator(String parameterName) {

			String result = null;
			Parameters parameters = pise.getParameters();

			Separator separator = (Separator) getRecursive(parameters,
					parameterName, "Separator");
			if (separator != null) {

				result = separator.getContent();
			}

			return result;
		}

		/**
		 * @param parameterName
		 * @return parameterFileContent
		 */
		public String getParamFile(String parameterName) {

			String result = null;
			Parameters parameters = pise.getParameters();

			Paramfile paramfile = (Paramfile) getRecursive(parameters,
					parameterName, "Paramfile");
			if (paramfile != null) {

				result = paramfile.getContent();
			}

			return result;
		}

		/**
		 * @param parameterName
		 * @return filenames
		 */
		public String getFileNames(String parameterName) {

			String result = null;
			Parameters parameters = pise.getParameters();

			Filenames filenames = (Filenames) getRecursive(parameters,
					parameterName, "Filenames");
			if (filenames != null) {

				result = filenames.getContent();
			}

			return result;
		}


		public String getPrecond(Parameter param) 
		{
			List<?> nameAndAttributes = param.getNameAndAttributes();
			Name name = (Name) nameAndAttributes.get(0);
			return getPrecond(name.getContent());
		}

		/**
		 * Find the Precond in the PiseXML for parameterName
		 * @param parameterName
		 * @return precondition
		 */
		public String getPrecond(String parameterName) {

			String result = null;
			Parameters parameters = pise.getParameters();

			Precond precond = (Precond) getRecursive(parameters, parameterName,
					"Precond");
			if (precond != null) {
				List<?> languageAndCode = precond.getLanguageAndCode();

				if (languageAndCode.size() > 1)
			//this Means it has at leat one langauge and  one code
			{
				Code code = (Code) languageAndCode.get(1);
				// this method is not complete because :
				// 1- it can have code not perl, we should test language to perl
				//	2- it can have multiple tag of language code, we should iterate
				// we assume here that the code language is perl and the code attached is in one statement
				result = code.getContent();
			}

		} else {
			// parmeter doesn't exist or Group is empty <precond/>
		}
		return result;
	}

	/**
	 * @param parameterName
	 * @param userValue
	 * @return listValue
	 */
	public String getflistValue(String parameterName, String userValue) {

		String result = null;
		Parameters parameters = pise.getParameters();

		Flist flist = (Flist) getRecursive(parameters, parameterName, "Flist");
		if (flist != null) {
			List<?> valueAndCode = flist.getValueAndCode();

			if (valueAndCode.size() > 1)
			//this Means it has at leat one value and  one code
			{
				for (int i = 0; i < valueAndCode.size(); i += 2) {
					Value value = (Value) valueAndCode.get(i);
					Code code = (Code) valueAndCode.get(i + 1);
					if (value.getContent().equals(userValue))
						result = code.getContent();

				}

			}

		} else {
			// parmeter doesn't exist or flist is empty
		}
		return result;
	}

	/**
	 * Find the Vdef in the PiseXML for parameterName, 
	 * it will use the element separator in order to concatenate them together
	 * @param parameterName
	 * @return vdef
	 */
	public String getVdef(String parameterName) {

		String result = null;
		Parameters parameters = pise.getParameters();

		List<?> attributesList = (List<?>) getRecursive(parameters,
				parameterName, "Vdef");

		Vdef vdef = null;
		Separator separator = null;

		if (attributesList != null) {

			for (int k = 0; k < attributesList.size(); k++)
				if (attributesList.get(k).toString().contains("Vdef"))
					vdef = (Vdef) attributesList.get(k);

			for (int k = 0; k < attributesList.size(); k++)
				if (attributesList.get(k).toString().contains("Separator"))
					separator = (Separator) attributesList.get(k);

			if (vdef != null) {
				List<Value> defaultValueList = vdef.getValue();

				result = defaultValueList.get(0).getContent();
				for (int k = 1; k < defaultValueList.size(); k++) {
					if (separator != null && separator.getContent() != "''")
						result = result
								+ separator.getContent().replace("'", "")
								+ defaultValueList.get(k).getContent();
					else
						result = result + defaultValueList.get(k).getContent();
				}

			}

		}
		// sometimes users specify vdef empty string as "" instead of just leaving the vdef element empty.  
		if (result != null && result.equals("\"\""))
		{
			result = "";
		}
		return result;
	}

	public Object Recursive(Parameters parameters) {

		List<Parameter> paramList = parameters.getParameter();

		for (int i = 0; i < paramList.size(); i++) {
			boolean afterParagraph = false;
			Parameter param = paramList.get(i);

			if (param.getType().contains("Paragraph")) {
				afterParagraph = true;
				Object found = null;
				found = Recursive(param.getParagraph().getParameters());
				if (found != null)
					return found;
			}

			if (!afterParagraph) {
				List<?> nameAndAttributes = param.getNameAndAttributes();

				Name name = (Name) nameAndAttributes.get(0);

				//   Add this parameter to the HiddenSet
				if (isHidden(param))
					hiddenSet.add(name.getContent());

				//	Add this parameter to the OutfileSet
				if (param.getType().equals("OutFile") && !isHidden(param))
					outfileSet.add(name.getContent());

				// Add this parameter to the ResultSet
				if (param.getType().equals("Results") && !isHidden(param))
					resultSet.add(name.getContent());

				// Add this parameter to the infileSet
				if (param.getType().equals("InFile") || param.getType().equals("Sequence")) 
				{
					infileSet.add(name.getContent());
					extendedVisibleSet.add(name.getContent());

					/*
						For REST API required parameter validation, we want the primary input to be in our requiredSet.  
						The primary input always has isinput=1, ishidden=1 and ismandatory=1.  

						TODO: does this work when the gui executes PiseCommandRenderer.validateRequired()?  Does gui
						send the primary input file in the parameter map?

						We never have any other Input or Sequence parameter that is both mandatory and hidden
						because there'd be no way to supply a value for it.
					*/
					if (isMandatory(param))
					{
						requiredSet.add(name.getContent());
					}
				}

				// Add this parameter to the set of visible, non file parameters
				if (!isHidden(param))
				{
					if (param.getType().equals("List") || 
						param.getType().equals("Excl") ||
						param.getType().equals("String") || 
						param.getType().equals("Integer") || 
						param.getType().equals("Float") || 
						param.getType().equals("Switch") )
					{
						visibleSet.add(name.getContent());
						extendedVisibleSet.add(name.getContent());
						if (isMandatory(param)) 
						{
							requiredSet.add(name.getContent());
						}
					}
				}

			}
		}
		return null;
	}

	// ismandatory=1 and no precond
	public boolean isMandatory(Parameter param)
	{
		return (param.getIsmandatory() != null && param.getIsmandatory().equals("1")) && (getPrecond(param) == null);
	}

	//has isHidden flag
	public boolean isHidden(Parameter param)
	{
		return param.getIshidden() != null && param.getIshidden().equals("1");
	}

	public Set<String> getRequiredSet() {
		return this.requiredSet;
	}

	public Set<String> getVisibleSet() {
		return this.visibleSet;
	}

	public Set<String> getExtendedVisibleSet() {
		return this.extendedVisibleSet;
	}


	public Set<String> getHiddenSet() {
		return this.hiddenSet;
	}

	public Set<String> getOutfileSet() {
		return this.outfileSet;
	}

	public Set<String> getResultSet() {
		return this.resultSet;
	}

	public Set<String> getInfileSet() {
		return this.infileSet;
	}

}
