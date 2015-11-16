/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Nov, 2009
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswlouthj
  Description: DICOM Study cache manager. Maintains the cache of study instances
  			   and expires old studies after 15 minutes. 

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

package gov.va.med.imaging.vistaimagingdatasource.dicom.storage;

import gov.va.med.imaging.exchange.business.dicom.DicomCorrectEntry;
import gov.va.med.imaging.exchange.business.storage.exceptions.DeletionException;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class DicomCorrectEntryDAO extends EntityDAO<DicomCorrectEntry> {
//	//
//	// RPC Names
//	//
//	private String RPC_FIND_ALL = "MAGV GET DICOM FAILED IMAGE";
//	private String RPC_DELETE = "MAGV DELETE DICOM FAILED IMAGE";
//
//	private String FILEPATH = "FILEPATH";
//	private String GATEWAY_LOCATION = "GATEWAY LOCATION";
//	private String IMAGE_UID = "IMAGE UID";
//	private String STUDY_UID = "STUDY UID";
//	private String SERVICE_TYPE = "SERVICE TYPE";
//	private String MACHINE_ID = "MACHINE ID";
//	private String INSTRUMENT_NAME = "INSTRUMENT NAME";
//	private String CORRECTED_NAME = "NEWNME";
//	private String CORRECTED_SSN = "NEWSSN";
//	private String CORRECTED_CASE_NO = "NEWCASE NO";
//	private String CORRECTED_PROCEDURE_IEN = "NEW PROC IEN";
//	private String CORRECTED_PROCEDURE_DESCRIPTION = "NEW PROCEDURE";
//	private String CORRECTED_DOB = "DATE OF BIRTH";
//	private String CORRECTED_SEX = "SEX";
//	private String CORRECTED_DFN = "DFN";
//	private String CORRECTED_ICN = "INTEGRATION CONTROL NUMBER";
//	private String FILEDELETE = "DELETE FLAG";
//
//	// Constructor
//	public DicomCorrectEntryDAO(VistaSessionFactory sessionFactory) {
//		this.setSessionFactory(sessionFactory);
//	}
//
//	//
//	// Find All methods
//	//
//	@Override
//	public VistaQuery generateFindByExampleQuery(DicomCorrectEntry dicomCorrectEntry) {
//		VistaQuery vm = new VistaQuery(RPC_FIND_ALL);
//		String host = dicomCorrectEntry.getHostname();
//		vm.addParameter(VistaQuery.LITERAL, host);
//		logger.debug(this.getClass().getName()+": RPC Request "
//				+RPC_FIND_ALL+"\n"+host);
//		return vm;
//	}
//
//	@Override
//	public List<DicomCorrectEntry> translateFindByExample(DicomCorrectEntry dicomCorrectEntry, String returnValue) {
//		List<DicomCorrectEntry> entries = new ArrayList<DicomCorrectEntry>();
//
//		logger.debug(this.getClass().getName()+": RPC Results:\n"+StringUtils.displayEncodedChars(returnValue));
//		String[] resultLines = StringUtils.Split(returnValue,
//				StringUtils.NEW_LINE);
//
//		// Check that the first line starts with zero
//		String resultCode = StringUtils.Split(resultLines[0],
//				StringUtils.BACKTICK)[0];
//		if ("0".equals(resultCode)) {
//			DicomCorrectEntry entry = null;
//
//			// Loop over the result lines, building objects until we're done...
//			for (int i = 1; i < resultLines.length; i++) {
//
//				String currentLine = resultLines[i].trim();
//				if (!currentLine.equals("")) {
//					
//					// Get rid of trailing backtick if any
//					String nameValuePair = StringUtils.Split(resultLines[i], StringUtils.BACKTICK)[0];
//
//					// Break remainder into name and value
//					String[] nameValueFields = StringUtils.Split(nameValuePair,StringUtils.STICK);
//					String name = nameValueFields[0];
//					String value = nameValueFields[1];
//
//					if (name.equals(FILEPATH)) {
//						// If the entry is not null, it means we've just
//						// finished filling an entry
//						// since we're back to filepath. Add it to the list
//						// before creating a new one...
//						if (entry != null) {
//							entries.add(entry);
//						}
//
//						// Create new entry and set the filepath
//						entry = new DicomCorrectEntry();
//						entry.setFilePath(value);
//					} 
//					else if (name.equals(GATEWAY_LOCATION)) 
//					{
//						entry.setGatewayLocation(value);
//					} 
//					else if (name.equals(IMAGE_UID)) 
//					{
//						entry.setImageUID(value);
//					} 
//					else if (name.equals(STUDY_UID)) 
//					{
//						entry.setStudyUID(value);
//					} 
//					else if (name.equals(SERVICE_TYPE)) 
//					{
//						entry.setServiceType(value);
//					} 
//					else if (name.equals(MACHINE_ID)) 
//					{
//						entry.setHostname(value);
//					} 
//					else if (name.equals(INSTRUMENT_NAME)) 
//					{
//						entry.setInstrumentNickName(value);
//					} 
//					else if (name.equals(CORRECTED_NAME)) 
//					{
//						entry.setCorrectedName(value);
//					} 
//					else if (name.equals(CORRECTED_SSN)) 
//					{
//						entry.setCorrectedSSN(value);
//					} 
//					else if (name.equals(CORRECTED_CASE_NO)) 
//					{
//						entry.setCorrectedCaseNumber(value);
//					} 
//					else if (name.equals(CORRECTED_PROCEDURE_IEN)) 
//					{
//						entry.setCorrectedProcedureIEN(value);
//					} 
//					else if (name.equals(CORRECTED_PROCEDURE_DESCRIPTION)) 
//					{
//						entry.setCorrectedProcedureDescription(value);
//					}
//					else if (name.equals(CORRECTED_DOB)) 
//					{
//						entry.setCorrectedDOB(value);
//					}
//					else if (name.equals(CORRECTED_SEX)) 
//					{
//						entry.setCorrectedSex(value);
//					}
//					else if (name.equals(CORRECTED_DFN)) 
//					{
//						entry.setCorrectedDFN(value);
//					}
//					else if (name.equals(CORRECTED_ICN)) 
//					{
//						entry.setCorrectedICN(value);
//					}
//					else if(name.equals(FILEDELETE)){
//						if(value.equals("1")){
//							entry.setFileToBeDeleted(true);
//						}
//						else{
//							entry.setFileToBeDeleted(false);
//						}
//					}
//				}
//			}
//			// 
//			// We've populated the last field of the last entry.
//			// Add the entry into the list if it's not null...
//			if (entry != null)
//			{
//				entries.add(entry);
//			}
//		}
//
//		return entries;
//	}
//
//	//
//	// Delete methods
//	//
//	@Override
//	public VistaQuery generateDeleteQuery(DicomCorrectEntry dicomCorrectEntry) {
//		VistaQuery vm = new VistaQuery(RPC_DELETE);
//		String filePath = dicomCorrectEntry.getFilePath();
//		vm.addParameter(VistaQuery.LITERAL, filePath);
//		logger.debug(this.getClass().getName()+": RPC Request "
//				+RPC_DELETE+"\n"+filePath);
//		return vm;
//	}
//
//	@Override
//	public void translateDelete(String returnValue) throws DeletionException
//	{
//		logger.debug(this.getClass().getName()+": RPC Results:\n"+StringUtils.displayEncodedChars(returnValue));
//		String[] results = StringUtils.Split(returnValue, StringUtils.BACKTICK);
//
//		if (!results[0].equals("0"))
//		{
//			
//			throw new DeletionException(results[1]);
//		}
//	}

}
