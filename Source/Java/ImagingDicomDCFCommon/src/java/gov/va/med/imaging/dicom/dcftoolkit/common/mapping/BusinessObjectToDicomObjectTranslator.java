/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: March 21, 2005
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWTITTOC
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+
 */
package gov.va.med.imaging.dicom.dcftoolkit.common.mapping;

// import gov.va.med.imaging.dicom.Constants;
import gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet;
import gov.va.med.imaging.exchange.business.dicom.DicomMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * @author Csaba Titton
 */
public class BusinessObjectToDicomObjectTranslator {
	
	private static Logger logger = Logger.getLogger(BusinessObjectToDicomObjectTranslator.class);
	
//	/**************************************************************************
//	 * Static methods
//	 *************************************************************************/
//	
//	/*
//	 * REENG Implement existing Dataset patch from business object
//	 */
//
//	public static void updateDataSetFromBusinessObject(Class businessObjectClass, IDicomDataSet dataSet, HashSet mappingSet)
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
//			Iterator iter = mappingSet.iterator();
//			while (iter.hasNext())
//			{
////				BusinessPropertyMapping mapping = (BusinessPropertyMapping)iter.next();
//				DicomMap mapping = (DicomMap)iter.next();
//				if (mapping.getImagingEntity().equals(businessObjectClassName))
//				{
//					// This mapped element belongs in this business object. Map it...
//					updateDicomElement(businessObject, mapping, dataSet);
//				}
//			}
//		}
//		catch (InstantiationException ie){}
//		catch (IllegalAccessException iae){}
//	}
//
//	/**
//	 * @param businessObject
//	 * @param mapping
//	 * @param dataSet
//	 */	
//
//   private static void updateDicomElement(Object businessObject, DicomMap mapping, IDicomDataSet dataSet) {
//
//		boolean exists=true;
//		String  strVal=null;
//		try
//		{
//			IDicomElement iDE = dataSet.getDicomElement(mapping.getTag());
//			if (iDE == null) // no such Dicom Element in dataset, Add new one (option to exit !!!)
//				exists=false;
//			
//			// Check whether the Dicom Element was be mapped to one or multiple business object fields.
//			if (mapping.getFieldMultiplicity() > 1) // no date/time and sequence embedded tags assumed here !!!
//			{		
//				strVal="";
//				// generate field names and construct a single DICOM value
//				for (int i = 1; i <= mapping.getFieldMultiplicity(); i++)
//				{
//
//					if (iDE.vm() < i) // not enough items in dataset Dicom Element array
//						return;
//					if (i>1)
//						strVal += "\\";
//					String fieldName = mapping.getEntityField() + Integer.toString(i);
//					// compose new value
//					strVal += getBusinessObjectFieldValue(businessObject, fieldName);
//				}
//			}
//			else
//			{
//				strVal = getBusinessObjectFieldValue(businessObject, mapping.getEntityField());
//				// handle data time switching here Checking fieldName and tags
//				if ((mapping.getTag2()!=null) && (mapping.getEntityField().indexOf("Date") > -1)) {
//					// Split value and store date and time separatly
//					dataSet.removeDicomElement(mapping.getTag(), null);	// date
//					dataSet.removeDicomElement(mapping.getTag2(), null);// time
//					dataSet.insertDicomElement(mapping.getTag(), null, strVal.substring(0, 8)); // date
//					dataSet.insertDicomElement(mapping.getTag2(), null, strVal.substring(9)); // time
//					return;
//				}
//			}
//			if (exists)
//				dataSet.removeDicomElement(mapping.getTag(), mapping.getTag2());
//
//			dataSet.insertDicomElement(mapping.getTag(), mapping.getTag2(), strVal);
//		}
//		catch (DicomException de)
//		{
//			logger.warn("Dicom Element '" + mapping.getTag() + "' was not present in the DataSet. The field '" + mapping.getImagingEntity() + "." + mapping.getEntityField() + "' was not mapped");
//		}
//	}
//    
//	private static String getBusinessObjectFieldValue(Object businessObject, String fieldName)
//	{
//		String stringVal=null;
//
//		try 
//		{			
//			// Find the return type of the getter, so we can retrieve the setter correctly
//			java.lang.Class[] c=null;
//			Method getter = businessObject.getClass().getMethod("get" + fieldName, c);
//			Class outFieldType = getter.getReturnType();
//						
//			// String, Int, Date, Timestamp and Float types in persistence (MetaDB schema)
//			java.lang.Object[] obj={null};
//			Object o = getter.invoke(businessObject, obj); // get value from the field into an object
//
//			if (outFieldType.equals(Date.class))
//			{				
//				java.sql.Date sd=(java.sql.Date)o;
//				String tmpString = sd.toString(); // yyyy-mm-dd  (JDBC date escape format)
//				stringVal = (tmpString.substring(0, 4) + tmpString.substring(5, 7) + tmpString.substring(8));
//				// stringVal is in 'YYYYMMDD' format,
//			}
//			else if (outFieldType.equals(Timestamp.class)) // Date will do the same!
//			{
//				java.sql.Timestamp sts=(java.sql.Timestamp)o;
//				String tmpString = sts.toString(); // yyyy-mm-dd hh:mm:ss.fffffffff (JDBC timestamp escape format) 
//				stringVal = (tmpString.substring(0, 4) + tmpString.substring(5, 7) + tmpString.substring(8, 13)
//						     + tmpString.substring(14, 16) + tmpString.substring(17, 27));
//				// stringVal is in 'YYYYMMDD HHMISS.ffffff' format
//			}
//			else
//				stringVal = o.toString();			// convert field value into string: 
//
//			return stringVal;
//			
//		} catch (NoSuchMethodException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}  catch (InvocationTargetException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}  catch (IllegalAccessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return stringVal;
//	}
	
		
//	======================================= Query support =======================================

	/**
	 * Converts an DICOM query response row to a DICOM dataSet. if in legacyMode, the HashSet is in
	 *  ("gggg,eeee", string value) format, otherwise in (fieldname, string value) format.
	 *   1. for DICOM date and time -- DA and TM tags are individual entries in the HashMap!
	 *   2. for a tag in a sequence -- dicomElement is the SQ tag and dicomElement2 is the tag in scope
	 * @param cRS -- the cachedRowSetImpl instance
	 * @param rownumber -- the row (1..) to be translated 
	 * @param mappingSet -- list of DICOM tags applicable for conversion (has DICOM tag as well as fieldname
	 *                      for each item) -- used for fieldname to tag conversion only (not in legacy)
	 * @param dataSet -- the dataSet to be updated
	 * @throws
	 */	

	public static void updateDataSetFromHISChanges(IDicomDataSet dataSet, HashMap<String, String> HISChanges, HashSet<DicomMap> mappingSet)
	{
		try 
		{
			if (dataSet==null) return;
			
			String tag = "";
			String tag2 = "";
							
				Iterator<DicomMap> iter = mappingSet.iterator();
				while (iter.hasNext())
				{
					DicomMap mapping = (DicomMap)iter.next();
					tag = mapping.getTag();
					tag2 = mapping.getTag2();
                    if (!(tag.equals(""))){
                        
                        if(tag2 == null){
                            String value = "";
                            value = (String)HISChanges.get(tag);
                            if(value != null){
                                //FUTURE We need to fix this.  We are calling this from DicomDataSet
                                //  object then call back to the DicomDataSet object.  Make no sense.
                                //  It works for now, so let it go.
                                dataSet.insertDicomElement(tag, tag2, value);
                            }
                        }
                        else{
                            StringBuffer sequenceTag = new StringBuffer();
                            sequenceTag.append(tag);
                            sequenceTag.append(" ");
                            sequenceTag.append(tag2);
                            String nuHashKey = sequenceTag.substring(0);
                            String value = (String)HISChanges.get(nuHashKey);
                            if (value != null)
                            	dataSet.insertDicomElement(tag, tag2, value);
                        }
                    }
				}
		}
		catch (Exception ex) {
            logger.error("Error: "+ex.getMessage());
            logger.error("Failure while updating DataSet with HIS changes.");
            logger.error("Trace:", ex);
		}
	}
}
