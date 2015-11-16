/*
 * Created on Mar 21, 2005
// Per VHA Directive 2004-038, this routine should not be modified.
//+---------------------------------------------------------------+
//| Property of the US Government.                                |
//| No permission to copy or redistribute this software is given. |
//| Use of unreleased versions of this software requires the user |
//| to execute a written test agreement with the VistA Imaging    |
//| Development Office of the Department of Veterans Affairs,     |
//| telephone (301) 734-0100.                                     |
//|                                                               |
//| The Food and Drug Administration classifies this software as  |
//| a medical device.  As such, it may not be changed in any way. |
//| Modifications to this software may result in an adulterated   |
//| medical device under 21CFR820, the use of which is considered |
//| to be a violation of US Federal Statutes.                     |
//+---------------------------------------------------------------+
 *
 */
package gov.va.med.imaging.dicom.dcftoolkit.common.mapping;

import gov.va.med.imaging.dicom.common.Constants;
import gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet;
import gov.va.med.imaging.dicom.common.interfaces.IDicomElement;
import gov.va.med.imaging.exchange.business.dicom.DicomMap;
import gov.va.med.imaging.exchange.business.dicom.DicomRequestParameters;
import gov.va.med.imaging.exchange.business.dicom.exceptions.DicomException;
import gov.va.med.imaging.exchange.business.dicom.exceptions.IllegalQueryDataException;

import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * @author Jon / Csaba
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DicomToBusinessObjectTranslator {
	
	private static Logger logger = Logger.getLogger(DicomToBusinessObjectTranslator.class);

//	/**************************************************************************
//	 * Static methods
//	 *************************************************************************/
//	public static Object getBusinessObjectFromDataSet(Class businessObjectClass, IDicomDataSet dataSet, HashSet<DicomMap> mappingSet)
//	{
//		Object businessObject = null;
//	
//		try
//		{
//			// Create an instance of the business object class
//			businessObject = businessObjectClass.newInstance();
//			
//			// Get the substring containing the name of the class, without the package name included...
//			String businessObjectClassName = businessObjectClass.getName().substring(businessObjectClass.getName().lastIndexOf(".") + 1);
//			
//			// for each field in the mapping for this class, set the appropriate field in the business object
//			Iterator<DicomMap> iter = mappingSet.iterator();
//			while (iter.hasNext())
//			{
////				BusinessPropertyMapping mapping = (BusinessPropertyMapping)iter.next();
//				DicomMap mapping = iter.next();
//				if (mapping.getImagingEntity().equals(businessObjectClassName))
//				{
//					// This mapped element belongs in this business object. Map it...
//					handleDicomElement(businessObject, mapping, dataSet);
//				}
//			}
//			
//		}
//		catch (InstantiationException ie){
//	        logger.error(ie.getMessage());
//	        logger.error("Exception thrown while instantiating class.");
//	    }
//		catch (IllegalAccessException iae){
//	        logger.error(iae.getMessage());
//	        logger.error("Exception thrown while instantiating class.");
//	    }
//		return businessObject;
	//}
	//
	///**
	// * @param businessObject
	// * @param mapping
	// * @param dataSet
	// */	
	//private static void handleDicomElement(Object businessObject, DicomMap mapping, IDicomDataSet dataSet) {
	//
//		try
//		{
//			IDicomElement iDE = dataSet.getDicomElement(mapping.getTag());
//			if (iDE == null) // no such Dicom Element in dataset, nothing to do, null in output field assumed!
//				return;
	//
//			// Check whether the Dicom Element will be mapped to one or multiple business object fields.
//			if (mapping.getFieldMultiplicity() > 1)
//			{				
//				// generate out field names and set field values from parsed array
//				for (int i = 1; i <= mapping.getFieldMultiplicity(); i++)
//				{
//					if (iDE.vm() < i) // not enough items in dataset Dicom Element array
//						return;
//					String fieldName = mapping.getEntityField() + Integer.toString(i);
//					setBusinessObjectFieldValue(businessObject, fieldName, iDE, dataSet.getDicomElement(mapping.getTag2()), i);
//				}
//			}
//			else
//			{
//				setBusinessObjectFieldValue(businessObject, mapping.getEntityField(), iDE, dataSet.getDicomElement(mapping.getTag2()), 0);
//			}
//		}
//		catch (DicomException de)
//		{
//			logger.warn("Dicom Element '" + mapping.getTag() + "' was not present in the DataSet. The field '" + mapping.getImagingEntity() + "." + mapping.getEntityField() + "' was not mapped");
//		} 
	//}
	//
	//private static void setBusinessObjectFieldValue(Object businessObject, String fieldName, IDicomElement dicomElement, IDicomElement dicomElement2, int parseCode)
	//{
//		try 
//		{			
//			// Find the return type of the getter, so we can retrieve the setter correctly
//			java.lang.Class[] c=null;
//			Method getter = businessObject.getClass().getMethod("get" + fieldName, c);
//			Class outFieldType = getter.getReturnType();
//			
//			// Get the setter method with data type
//			Method setter = businessObject.getClass().getMethod("set" + fieldName, new Class[] {outFieldType});
//			
//			// convert DICOM tag into string first (the parse issue is handled here if parseCode > 0)
//			String inString = convertDicomTagToString(dicomElement, dicomElement2, parseCode, false);
//			
//			// deal with output conversion now according to output type -- currently we have only 
//			// String, Int, Date and Float types in persistence (MetaDB schema)
//			if (outFieldType.equals(String.class))
//			{
//				setter.invoke(businessObject, new Object[] {inString}); // pull value from the dicomElement as a String
//			} 
//			else if (outFieldType.equals(Byte.class) ||
//					outFieldType.equals(byte.class))
//			{
//				setter.invoke(businessObject,  new Object[] {Byte.decode(inString)});
//			}
//			else if (outFieldType.equals(Integer.class) || outFieldType.equals(int.class))
//			{
//				setter.invoke(businessObject,  new Object[] {Integer.decode(inString)});
//			}
//			else if (outFieldType.equals(Float.class) || outFieldType.equals(float.class))
//			{
//				setter.invoke(businessObject, new Object[] {Float.valueOf(inString)});
//			}
//			else if (outFieldType.equals(Double.class) || outFieldType.equals(double.class))
//			{
//				setter.invoke(businessObject, new Object[] {Double.valueOf(inString)});
//			}
//			else if (outFieldType.equals(Date.class))
//			{				
//				// inString is in 'YYYYMMDD' format,
//				java.sql.Date sd = toSqlDate(inString);
	//
//				setter.invoke(businessObject, new Object[] {java.sql.Date.valueOf(sd.toString())}); // ???
//			}
//			else if (outFieldType.equals(Timestamp.class)) // Date will do the same!
//			{
//				// inString is in 'YYYYMMDD [HHMISS[.ffffff][&ZZZZ]]' format (& is '+' or '-')
//				java.sql.Timestamp sts = toSqlTimestamp(inString);
	//
//				setter.invoke(businessObject, new Object[] {java.sql.Timestamp.valueOf(sts.toString())}); // ???
//			}
//			return;
//			
//		} catch (NoSuchMethodException e) {
//			logger.error(e.getMessage());
//	        logger.error("Exception thrown while setting Business Object Field value.");
//		}  catch (InvocationTargetException e) {
//			logger.error(e.getMessage());
//	        logger.error("Exception thrown while setting Business Object Field value.");
//		}  catch (IllegalAccessException e) {
//			logger.error(e.getMessage());
//			logger.error("Exception thrown while setting Business Object Field value.");
//		}  catch (IllegalQueryDataException e) {
//	        logger.error(e.getMessage());
//	        logger.error("Exception thrown while setting Business Object Field value.");
//			// never happens when ConvertDicomtagToString is called with QueryFlag==false
//		}
//	}
	
//	======================================= query / move support =======================================

	/**
	 * Converts an DICOM request dataSet into a HashMap. if in legacyMode, the HashSet is in
	 *  ("gggg,eeee", string value) format, otherwise in (fieldname, string value) format.
	 *   1. for DICOM date and time -- DA and TM tags are individual entries in the HashMap!
	 *   2. for a tag in a sequence -- dicomElement is the SQ tag and dicomElement2 is the tag in scope
	 * @param dataSet -- of DICOM Query/Move request
	 * @param mappingSet -- list of DICOM tags applicable for conversion (has DICOM tag as well as fieldname
	 *                      for each item)
	 * @return HashMap -- containing the mapping set: ("gggg,eeee", value) for legacy, (fieldName, value) otherwise
	 * @throws IllegalQueryDataException -- if query dtae/time input syntax is illegal, no exception for move requests
	 */	

	public static DicomRequestParameters getRequestFromDataSet(IDicomDataSet dataSet, HashSet<DicomMap> mappingSet)
	throws IllegalQueryDataException
	{
		DicomRequestParameters qRequest= new DicomRequestParameters();
		try 
		{			
			// loop through map with dataset:
			// for each field in the mapping for this class, set the appropriate field and value in a HashMap
			Iterator<DicomMap> iter = mappingSet.iterator();
			while (iter.hasNext())
			{
				DicomMap mapping = (DicomMap)iter.next();
                
				IDicomElement iDE = dataSet.getDicomElement(mapping.getTag());
				IDicomElement iDE2 = dataSet.getDicomElement(mapping.getTag2());
				if (iDE != null) // found Dicom Element in dataset
				{
					// This mapped element belongs in the query. Map it...
					// convert DICOM tag into string first (the parse issue is handled here if parseCode > 0)
					String inString = convertDicomTagToString(iDE, iDE2, 0, true);
					if (mapping.getTag2()==null)
						qRequest.put(mapping.getTag(), inString);  // for any single tag
					else
						qRequest.put(mapping.getTag2(), inString); // for sequence element tag
							
				} // else (iDE==null): nothing to do, null in output field assumed!!!
			}
		}
		catch (DicomException ex) {
            logger.error(ex.getMessage());
            logger.error("Exception thrown while requesting from Dataset.");
			throw new IllegalQueryDataException("Failure to request from Dataset.", ex);
		}
		return qRequest;
	}



	/**
	 * Converts an IDicomElement value into string format. Normally dicomElement2 is null, except:
	 *   1. for a date and time DICOM tag pair -- dicomElement is the DA tag and dicomElement2 is the TM tag
	 *   2. for a tag in a sequence -- dicomElement is the SQ tag and dicomElement2 is the tag in scope
	 * @param dicomElement
	 * @param dicomElement2
	 * @param parseCode -- if 0, no effect, >0 tells which subelement to extract from the dicom value that has VM>1.
	 * @param querySyntax -- true if query syntax applies for (date/time ranges, wildcards, etc.)
	 * @return string
	 * @throws IllegalQueryDataException -- if query input syntax is illegal
	 */	
	private static String convertDicomTagToString(IDicomElement dicomElement, IDicomElement dicomElement2,
								int parseCode, boolean querySyntax)throws IllegalQueryDataException{
		String outString="";

		try
		{
			// here comes the parse issue if parseCode > 0
			// convert each DICOM tag into string

			String tmpString = "";
			int i = 0;

			switch (dicomElement.vr()) {

			case Constants.DICOM_VR_AS: // age string (4 char fixed: nnnD, nnnW, nnnM or nnnY, where nnn is '0'..'9')
			case Constants.DICOM_VR_SH:	// Short string (max 16: No '\',LF,FF,CR, but Esc)
			case Constants.DICOM_VR_LO:	// Long string (max 64: no '\',LF,FF,CR, but Esc)
				outString = getTagStringValue(dicomElement, parseCode);
				break;

			case Constants.DICOM_VR_PN: // Person Name (max 64: no '/' ,LF,FF,CR, but Esc,
										//     <family>^<given>^<middle>^<prefix>^<suffix>, middle Initial in given!, middle is kept for backward compat.
										//     trailing null components can be omitted; trailing spaces are Ok)
										//     ACR-NEMA2 allowed non-delimited bulked name string...
				outString = getTagStringValue(dicomElement, parseCode);
				break;

			case Constants.DICOM_VR_UI: // world-wide unique identifier (max 64 chars: '0'..'9' and '.'), '.0.' is illegal
				outString = getTagStringValue(dicomElement, parseCode);
				break;				

			case Constants.DICOM_VR_LT:	// long text (max 10240: LF,FF,CR,Esc & graphics chars included, trailing spaces might be ignored)
			case Constants.DICOM_VR_ST: // short text max 1024: LF,FF,CR,Esc & graphics chars included, trailing spaces might be ignored)
			case Constants.DICOM_VR_UT:	// unlimited text (max 2^32-2 chars): No '\' and No trailing spaces 
				tmpString = getTagStringValue(dicomElement, parseCode);
				// remove trailing spaces
                if(tmpString.equals("")){
                    outString = tmpString;
                }
                else{
                    for (i=(tmpString.length()-1); (tmpString.charAt(i)==' ')&&(i>0); i--);
                    if (i<(tmpString.length()-1))
                        outString = tmpString.substring(0, i);
                    else
                        outString = tmpString;
                }
				break;

			case Constants.DICOM_VR_AE: // AE title (max 16 char: No '\',LF,FF,CR or Esc, remove leading/trailing spaces)
			case Constants.DICOM_VR_IS:	// integer string (max 12: +/- and '0'..'9' only, remove leading/trailing spaces)
			case Constants.DICOM_VR_CS: // coded string (max 16 chars: UCASE chars, '_', Space, '0'..'9', remove leading/trailing spaces)
			case Constants.DICOM_VR_DS:	// decimal string (max 16 chars of fixed point or floating point number, remove leading/trailing spaces)
                tmpString = getTagStringValue(dicomElement, parseCode);
				if(tmpString.equals("")){
				    outString = tmpString;
                }
                else{
                    if (dicomElement.vr() == Constants.DICOM_VR_CS)
                        tmpString.toUpperCase(); 	// reinforce upper case
                    outString = tmpString.trim();// remove leading and trailing spaces (not inside string!, except DS)
                }
				break;

			case Constants.DICOM_VR_AT: // Attribute tag: pair of 16 bit unsigned integers)
				return outString; // CODEME throw misc. exception: NOT USED by metaDB, Not implemented !!!
			
			case Constants.DICOM_VR_FD:	// double float
			case Constants.DICOM_VR_FL:	// single float
				double dd = getTagDecimalValue(dicomElement, parseCode);
				outString = Double.toString(dd);
				break;

			case Constants.DICOM_VR_OB: //
			case Constants.DICOM_VR_OW: //
				return outString; // CODEME throw misc. exception: Might be needed for icon sequence item

			case Constants.DICOM_VR_SL: // signed long:  32 bit 2's complement (-2^31..+2^31 - 1)
			case Constants.DICOM_VR_SS:	// signed short: 16 bit 2's complement (-2^15..+2^15 - 1)
			case Constants.DICOM_VR_UL:	// unsigned long: 32 bit (0..+2^32)
			case Constants.DICOM_VR_US:	// unsigned short: 16 bit (0..+2^16)
				i = getTagIntValue(dicomElement, parseCode);
				outString = Integer.toString(i);
				break;
				
			case Constants.DICOM_VR_SQ: 	 	// sequence tags not supported for mapping except for RPCode
				if ((dicomElement2!=null) && 
					((dicomElement.getTagName()=="0008,1032") || (dicomElement.getTagName()=="0032,1064")) && 
					(dicomElement2.getTagName()=="0008,0100")) { // rp code translation
					outString = getSequenceTagStringValue(/*SQ*/dicomElement, /*tag*/dicomElement2.getTagName(), parseCode);
					break;
				} else
					return outString;
			
			case Constants.DICOM_VR_SQ_DELIM:	// sequence tags not supported for mapping
			case Constants.DICOM_VR_INVALID:
				return outString; // CODEME throw misc. exception				
			
			case Constants.DICOM_VR_DA: 
			case Constants.DICOM_VR_TM:
				if (!querySyntax) { // date [and time] is in one entry (2 elements)
					// Date in 'YYYYMMDD' or 'YYYY.MM.DD' (for ACR-NEMA2 backward comp)
					tmpString = getTagStringValue(dicomElement, parseCode);
					if (tmpString.length()<6) return outString; // ignore illegal garbage
					if (tmpString.length()<8) // 'YYYYMM' ??, put fake first day of month there
						tmpString = tmpString.substring(0, 6) + "01";
					if (tmpString.length()>8) { // 'YYYY.MM.DD' -- remove '.'-s
						outString = tmpString.substring(0, 4) + tmpString.substring(5, 7) + tmpString.substring(8);	
					} else
						outString = tmpString;
					// here inString is Date only in 'YYYYMMDD' format
					
					if ((dicomElement2!=null) && (dicomElement2.vr()==Constants.DICOM_VR_TM)) {
						// Time in 'HHMISS.ffffff' or HH:MI:SS.ffffff (24 hour clock with max 6 fractions)
						tmpString = getTagStringValue(dicomElement2, parseCode);
						if (tmpString.length()<6) // 'HHMI' ??, put fake 0 seconds there
							tmpString = tmpString.substring(0, 4) + "00";
						if (tmpString.charAt(2)==':') { // 'HH:MI:SS[.ffffff]' -- remove ':'-s
							outString += " " + tmpString.substring(0, 2) + tmpString.substring(3, 5) + tmpString.substring(6);
						} else
							outString += " " + tmpString;
						// here inString is Date/Time in  'YYYYMMDD HHMISS[.ffffff]' format
					}
					
				} else { // Query syntax applies -- either Date or Time applies [1 element at a time]
					tmpString = getTagStringValue(dicomElement, parseCode);
					if(tmpString.equals("")) return outString;
                    tmpString.trim();
					tmpString.replaceAll(" ", "");
					tmpString.replaceAll("\t", "");
					if (dicomElement.vr()==Constants.DICOM_VR_DA) {
						tmpString.replaceAll(".", "");
						// Date is blank, yyyymmdd, -yyyymmdd, yyyymmdd-, or yyyymmdd-yyyymmdd
						if (tmpString.length()==0) return outString; // no criteria passed
						if (tmpString.length()<8) // illegal input
							throw new IllegalQueryDataException();
						else if (tmpString.length()>8) { // yyyymmdd-, -yyyymmdd or yyyymmdd-yyyymmdd
							if (((tmpString.charAt(0)=='-') && (tmpString.length()!=9)) || // -yyyymmdd
								((tmpString.charAt(8)=='-') &&
										(!((tmpString.length()==9) || (tmpString.length()==17))))) // yyyymmdd-, or yyyymmdd-yyyymmdd	
								throw new IllegalQueryDataException(); // illegal input
						} // else length is 8; 'yyyymmdd' (exact match)

					} else { // TM
						// Time is blank, hhmmss, hhmmss-, -hhmmss or hhmmss-hhmmss
						if (tmpString.length()==0) return outString; // no criteria passed
						if (tmpString.length()<6) // illegal input
							throw new IllegalQueryDataException();
                        //wfp   01182006    Changed below statement from 8 to 33.  Reason. TM VR 
                        //  allows up to 16 bytes to represent a single Time.  For a range, it would
                        //  take 32 bytes plus 1 for the "-" separator.
						else if (tmpString.length()>33) {  // hhmmss-, -hhmmss or hhmmss-hhmmss
							if (((tmpString.charAt(0)=='-') && (tmpString.length()!=7)) || // -hhmmss
								((tmpString.charAt(6)=='-') &&
										(!((tmpString.length()==7) || (tmpString.length()==13))))) // hhmmss-, or hhmmss-hhmmss	
								throw new IllegalQueryDataException(); // illegal input
						} // else length is 6; 'hhmmss' (exact match)
						
					}
					outString = tmpString;
				}
				outString.trim();
				break;
                
                
			case Constants.DICOM_VR_DT: // DateTime in 'YYYYMMDD HHMISS[.ffffff][&ZZZZ]' (24 hour clock, &ZZZZ is +/-HHMI, optional )
				outString = getTagStringValue(dicomElement, parseCode);
                if(!(outString.equals(""))){
                    outString.trim();
                }
				break;				
			
			case Constants.DICOM_VR_UN: // unknown
				outString = getTagStringValue(dicomElement, parseCode);
				break;	
			} // end switch
	
		} catch (SecurityException e) {
			logger.error(e.getMessage());
			logger.error("Exception thrown while converting Dicom Tag to String.");
            logger.error("Trace:", e);
		}
		return outString;
	}
	
	
	private static String getTagStringValue(IDicomElement dicomElement, int parseCode){
		String str = "";
		if (parseCode > 0)
			str = dicomElement.getStringValue(parseCode-1);
		else
			str= dicomElement.getStringValue();
		if (str == null)
			str="";
		return str;
	}
    
	private static int getTagIntValue(IDicomElement dicomElement, int parseCode){
		if (parseCode > 0)
			return dicomElement.getIntValue(parseCode-1);
		else
			return dicomElement.getIntValue();
	}
    
	private static double getTagDecimalValue(IDicomElement dicomElement, int parseCode){
		if (parseCode > 0)
			return dicomElement.getDecimalValue(parseCode-1);
		else
			return dicomElement.getDecimalValue();
	}

	private static String getSequenceTagStringValue(IDicomElement dicomElement, String seqtag, int parseCode){
		String str = "";
		if (parseCode > 0)
			str = dicomElement.getSequenceElementStringValue(seqtag, parseCode-1);
		else
			str = dicomElement.getSequenceElementStringValue(seqtag);		
		if (str == null)
			str="";
		return str;
	}
	
//	private static java.sql.Date toSqlDate(String dateString) {
//	// dateString is in 'YYYYMMDD' format,
//	// munge it into SQL Date (millis) format with local timezone
//
//	SimpleDateFormat dtformat = new SimpleDateFormat("yyyyMMdd");
//	java.util.Date ud=null;
//	java.sql.Date sd= new java.sql.Date(0);
//	try {
//		ud = dtformat.parse(dateString);
//		sd.setTime(ud.getTime());
//		return sd;
//	}
//	catch (ParseException pe) {
//        logger.error(pe.getMessage());
//        logger.error("Exception thrown while converting to SQL Date "+dateString+".");
//		logger.error("Trace:", pe);
//	}
//	return sd;
//}
//
//private static java.sql.Timestamp toSqlTimestamp(String tsString) {
//
//	// inString is in 'YYYYMMDD [HHMISS[.ffffff][&ZZZZ]]' format (& is '+' or '-')
//	// munge it into Timestamp (millis) format with local (unless given) timezone
//
//	// Calendar calendar = new GregorianCalendar(); // with default timezone and locale
//	// DateFormat df = DateFormat.getDateInstance(DateFormat.YEAR_FIELD, DateFormat.MONTH_FIELD,,, Locale.US);
//	// ParsePosition pp;
//	// calendar.setTime(df.parse(inString));
//	
//	// extract fraction for Timestamp as other Java Date formats know millis only
//	// also extract time zone if present
//	String ts = null;
//	String fraction=null;
//	int dotChar=tsString.indexOf('.');
//	int signChar=tsString.indexOf('+');
//	if (signChar == -1) signChar=tsString.indexOf('-');
//	if (dotChar == -1) {	// no fraction, be careful with date format
//		ts = tsString;
//	} 
//	else if	(signChar == -1) {	// no time zone
//		fraction = tsString.substring(dotChar+1);	// extract fractions from after '.'
//		ts = tsString.substring(0, dotChar);		// extract datetime from before '.'
//	} else {					// ther is time zone !!
//		fraction = tsString.substring(dotChar+1, signChar);	// extract fractions from middle
//		ts = tsString.substring(0, dotChar);		// extract datetime from before '.'
//		ts += tsString.substring(signChar);			// extend it with timezone substring
//	}
//		
//	SimpleDateFormat dtformat;
//	if (signChar != -1) // time zone is available in string
//		dtformat = new SimpleDateFormat("yyyyMMdd HHmmssZZZZZ");
//	else 				// no time zone
//		dtformat = new SimpleDateFormat("yyyyMMdd HHmmss");
//
//	java.util.Date dt = null;
//	java.sql.Timestamp sts= new java.sql.Timestamp(0);
//	try {
//		dt = dtformat.parse(ts);
//		// inString = dt.toGMTString(); // depricated
//		sts.setTime(dt.getTime());
//		if (fraction != null) {
//			// first make sure fraction string is in millis;
//			for (int i=fraction.length(); (i < 6); i++ )
//				fraction += "0";
//			sts.setNanos(Integer.parseInt(fraction + "000"));
//		}
//		return sts;
//	}
//	catch (ParseException pe) {
//        logger.error(pe.getMessage());
//        logger.error("Exception thrown while converting to SQL Timestamp "+tsString+".");
//		logger.error("Trace:", pe);
//	}
//	return sts;
//}
//

}
