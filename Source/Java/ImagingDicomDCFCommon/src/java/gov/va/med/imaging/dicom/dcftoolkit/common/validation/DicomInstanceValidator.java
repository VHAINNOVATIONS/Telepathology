/* Copyright (c) 2001-2003, David A. Clunie DBA Pixelmed Publishing. All rights reserved. */

package gov.va.med.imaging.dicom.dcftoolkit.common.validation;

import gov.va.med.imaging.StringUtil;
import gov.va.med.imaging.dicom.common.interfaces.IIODViolation;
import gov.va.med.imaging.dicom.dcftoolkit.common.impl.IODViolationImpl;
import gov.va.med.imaging.dicom.dcftoolkit.common.impl.IODViolationListImpl;
import gov.va.med.imaging.exchange.business.dicom.exceptions.UnknownSOPClassException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.lbs.DCS.DCM;
import com.lbs.DCS.DicomDataSet;
import com.lbs.DCS.DicomFileInput;

/**
 * <p>The {@link DicomInstanceValidator DicomInstanceValidator} class is
 * for validating composite storage SOP instances against the standard IOD for the corresponding storage SOP Class.</p>
 *
 * <p>Typically used by reading the list of attributes that comprise an object, validating them
 * and displaying the resulting string results to the user on the standard output, in a dialog
 * box or whatever. The basic implementation of the {@link #main main} method (that may be useful as a
 * command line utility in its own right) is as follows:</p>
 *
 * <pre>
 * 	AttributeList list = new AttributeList();
 * 	list.read(arg[0],null,true,true);
 * 	DicomInstanceValidator validator = new DicomInstanceValidator();
 * 	System.err.print(validator.validate(list));
 * </pre>
 *
 * @see com.pixelmed.dicom.AttributeList
 *
 * @author	dclunie
 */
public class DicomInstanceValidator {

	private Transformer transformer;
	
	private static DicomInstanceValidator instance_ = null;
	
    public static final Logger logger = Logger.getLogger (DicomInstanceValidator.class);
	
	private class OurURIResolver implements URIResolver {
		/**
		 * @param	href
		 * @param	base
		 */
		public Source resolve(String href,String base) throws TransformerException {
		logger.debug("OurURIResolver.resolve() href="+href+" base="+base);
			InputStream stream = DicomInstanceValidator.class.getResourceAsStream(href);
			return new StreamSource(stream);
		}
	}

	public static DicomInstanceValidator getInstance() throws TransformerConfigurationException{
		if(instance_ == null){
			instance_ = new DicomInstanceValidator();
		}
		return instance_;
	}
	
	/**
	 * <p>Create an instance of validator.</p>
	 *
	 * <p>Once created, a validator may be reused for as many validations as desired.</p>
	 *
	 * @exception	javax.xml.transform.TransformerConfigurationException
	 */
	private DicomInstanceValidator() throws javax.xml.transform.TransformerConfigurationException {
		//IMPROVE Attempt to better understand this and improve upon it if possible.
		//P34 Dee requested if I can change the build process so not have to run a build each time there is a change
		//	to the XML file.
		logger.debug("Initializing DicomInstanceValidator...");
		String compiledXSLPath = "gov/va/med/imaging/dicom/dcftoolkit/common/validation/";
		String compiledXSLFile = "DicomIODDescriptionsCompiled.xsl";
		InputStream transformStream = DicomInstanceValidator.class.getResourceAsStream(compiledXSLFile);
		Source transformSource = new StreamSource(transformStream);
		transformSource.setSystemId(compiledXSLPath);
		logger.debug("System ID: "+transformSource.getSystemId());
		TransformerFactory tf = TransformerFactory.newInstance("com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl", null);
		tf.setURIResolver(new OurURIResolver());			// this helps us find the common rules in the jar file
		transformer = tf.newTransformer(transformSource);
		if(transformStream != null){
			try {
				transformStream.close();
			} catch (IOException ioX) {
				logger.warn("Failed to close stream: "+transformStream.getClass().getName()+": "+ioX.getMessage(), ioX);
			}
			transformStream = null;
		}
		logger.debug("DicomInstanceValidator Initialization completed...");
	}


	private DicomInstanceValidator(String path) throws javax.xml.transform.TransformerConfigurationException {
		logger.debug("Initializing DicomInstanceValidator...");
		String compiledXSLPath = path;
		String compiledXSLFile = "DicomIODDescriptionsCompiled.xsl";
		InputStream transformStream = DicomInstanceValidator.class.getResourceAsStream(compiledXSLFile);
		Source transformSource = new StreamSource(transformStream);
		transformSource.setSystemId(compiledXSLPath);
		logger.debug("System ID: "+transformSource.getSystemId());
		TransformerFactory tf = TransformerFactory.newInstance("com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl", null);
		tf.setURIResolver(new OurURIResolver());			// this helps us find the common rules in the jar file
		transformer = tf.newTransformer(transformSource);
		if(transformStream != null){
			try {
				transformStream.close();
			} catch (IOException ioX) {
				logger.warn("Failed to close stream: "+transformStream.getClass().getName()+": "+ioX.getMessage(), ioX);
			}
			transformStream = null;
		}
		logger.debug("DicomInstanceValidator Initialization completed...");
	}
	
	
	/**
	 * <p>Validate a DICOM composite storage instance against the standard IOD for the appropriate storage SOP Class.</p>
	 *
	 * @param	dds	the list of attributes comprising the DICOM composite storage instance to be validated
	 * @return		a string describing the results of the validation
	 * @exception	javax.xml.parsers.ParserConfigurationException
	 * @exception	javax.xml.transform.TransformerException
	 * @exception	java.io.UnsupportedEncodingException
	 */
	public synchronized IODViolationListImpl validate(DicomDataSet dds) throws
			javax.xml.parsers.ParserConfigurationException,
			javax.xml.transform.TransformerException,
			java.io.UnsupportedEncodingException,
			UnknownSOPClassException{
		
		Document inputDocument = new XMLRepresentationOfDicomObjectFactory().getDocument(dds);
		Source inputSource = new DOMSource(inputDocument);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		StreamResult outputResult = new StreamResult(outputStream);
		transformer.transform(inputSource,outputResult);
		String result = outputStream.toString("UTF-8");
		IODViolationListImpl violations = this.convertResultStringToViolationList(result, dds);
		if(outputStream != null){
			try {
				outputStream.close();
			} catch (IOException ioX) {
				logger.warn("Failed to close stream: "+outputStream.getClass().getName()+": "+ioX.getMessage(), ioX);
			}
			outputStream = null;
			inputDocument = null;
		}
		return violations;
	}
	
	private IODViolationListImpl convertResultStringToViolationList(String result, DicomDataSet dds)
										throws ParserConfigurationException, UnknownSOPClassException{
		IODViolationListImpl violationList = null;
		String mfg = dds.getElementStringValue(DCM.E_MANUFACTURER, "");
		String model = dds.getElementStringValue(DCM.E_MANUFACTURERS_MODEL_NAME, "");
		String version = dds.getElementStringValue(DCM.E_SOFTWARE_VERSIONS, "");
		String sopClass = dds.getElementStringValue(DCM.E_SOPCLASS_UID, "");
		
		String[] lines = StringUtil.split(result, StringUtil.CRLF);
		if(lines.length < 1){
			throw new ParserConfigurationException("No data in the result.");
		}

		if(lines[0].contains("IOD (SOP Class) unrecognized")){
			throw new UnknownSOPClassException("IOD Validation does not know this SOP Class: "+sopClass);
		}
		
		violationList = new IODViolationListImpl(mfg, model, version, sopClass);
		
		for(int i=1; i<lines.length; i++){
			String[] splitLine = StringUtil.split(lines[i], StringUtil.DOUBLECOLON);
			if(splitLine.length == 2){
				String errorType = splitLine[0];
				int error = 0;
				if(errorType.contains("Warning")){
					error = IIODViolation.VIOLATION_WARNING;
				}
				else if(errorType.contains("Error")){
					error = IIODViolation.VIOLATION_ERROR;
				}
			
				if(error != 0){
					String comment = splitLine[1];
					IODViolationImpl violation = new IODViolationImpl(error, comment);
					violationList.addViolation(violation);
				}
			}
		}
		return violationList;
	}

	/**
	 * <p>Read the DICOM file specified on the command line and validate it against the standard IOD for the appropriate storage SOP Class.</p>
	 *
	 * <p>The result of the validation is printed to the standard output.</p>
	 *
	 * @param	arg	the name of the file containing the DICOM composite storage instance to be validated
	 */
	public static void main(String arg[]) {
		try {
			DicomDataSet list;
			DicomFileInput input = new DicomFileInput(arg[0]);
			input.open();
			list = input.readDataSet();
			System.out.println("Getting ready to initialize DicomInstanceValidator...");
			System.out.println(DicomInstanceValidator.getInstance().validate(list));
			System.out.println("IOD Validation Complete.");
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
}

