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

// import gov.va.med.imaging.dicom.Constants;
import gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet;
import gov.va.med.imaging.exchange.business.dicom.DicomMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * @author Csaba Titton
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BusinessObjectToDicomSCUTranslator {
	
	/**************************************************************************
	 * Static fields and initializer
	 *************************************************************************/
	
	static Logger logger = Logger.getLogger(BusinessObjectToDicomSCUTranslator.class);
	
	
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
			
			String tag = null;
			String tag2 = null;
							
				Iterator<DicomMap> iter = mappingSet.iterator();
				while (iter.hasNext())
				{
					DicomMap mapping = (DicomMap)iter.next();
					tag = mapping.getTag();
					tag2 = mapping.getTag2();
                    if (!(tag == null)){
                        
                        if(tag2 == null){
                            String value = HISChanges.get(tag);
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
                            String value = HISChanges.get(nuHashKey);
                            if(value != null){
                                dataSet.insertDicomElement(tag, tag2, value);
                            }
                        }
                    }
				}
		}
		catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error("Failure while updating DataSet with HIS changes.");
            logger.error("Trace:", ex);
		}
	}
}
