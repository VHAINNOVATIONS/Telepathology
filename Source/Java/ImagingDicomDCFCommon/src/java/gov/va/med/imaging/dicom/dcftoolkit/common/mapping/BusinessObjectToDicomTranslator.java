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

import java.util.HashSet;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.sun.rowset.CachedRowSetImpl;

/**
 * @author Csaba Titton
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BusinessObjectToDicomTranslator {
	
	private static Logger logger = Logger.getLogger(BusinessObjectToDicomTranslator.class);

	
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

	public static void getResponseDataSetFromRow(CachedRowSetImpl cRS, int rowNumber, HashSet<DicomMap> mappingSet,
										  IDicomDataSet dataSet, String MoveSCPAE)
	{
		try 
		{
            logger.debug("RowNumber in Translator: " + rowNumber);
			if ((cRS==null) || (cRS.getMetaData()==null) || (cRS.getMetaData().getColumnCount()<1) || (rowNumber<1) || (dataSet==null)) 
				return;
			
			int numCols = cRS.getMetaData().getColumnCount();
			String tag = "";
			String tag2 = "";
			
			// loop through columns
			// for each column get DICOM tag, create a new DICOM element with relevant value and insert to dataSet
			for (int i=1; i<numCols+1; i++) {
				
				Iterator<DicomMap> iter = mappingSet.iterator();
				while (iter.hasNext())
				{
					DicomMap mapping = iter.next();
						if (mapping.getTag().equals(cRS.getMetaData().getColumnName(i))){
							tag = mapping.getTag();
							tag2 = mapping.getTag2();
			                logger.debug("inserting into dataSet: " + tag + ": " + cRS.getString(i));
			                dataSet.insertDicomElement(tag, tag2, cRS.getString(i));
					}
				}
			}
            
			dataSet.insertDicomElement("0008,0054", null, MoveSCPAE); // set retrieve AE title in response row
		}
		catch (Exception ex) {
            logger.error(ex.getMessage());
            logger.error("Exception thrown while creating response Dataset from RowSet.");
            logger.error("Trace:", ex);
		}
	}
}
