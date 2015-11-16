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

import gov.va.med.imaging.exchange.business.dicom.PatientStudyInfo;
import gov.va.med.imaging.exchange.business.dicom.PatientStudyLookupResults;
import gov.va.med.imaging.protocol.vista.DicomTranslatorUtility;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.util.ArrayList;
import java.util.List;

public class PatientStudyLookupResultsDAO extends EntityDAO<PatientStudyLookupResults>
{
	private String RPC_MAGV_STUDY_LOOKUP = "MAGV STUDY LOOKUP";

	// Constructor
	public PatientStudyLookupResultsDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}

	//
	// Retrieval method overrides
	//
	@Override
	public VistaQuery generateGetEntityByCriteriaQuery(Object criteria) 
	{
		PatientStudyInfo patientStudyInfo = (PatientStudyInfo)criteria;
		VistaQuery vm = new VistaQuery(RPC_MAGV_STUDY_LOOKUP);
		vm.addParameter(VistaQuery.LITERAL, patientStudyInfo.getStudyAccessionNumber());
		vm.addParameter(VistaQuery.LITERAL, patientStudyInfo.getStudyImagingService());
		vm.addParameter(VistaQuery.LITERAL, patientStudyInfo.getPatientName());
		vm.addParameter(VistaQuery.LITERAL, patientStudyInfo.getPatientID());
		vm.addParameter(VistaQuery.LITERAL, patientStudyInfo.getPatientBirthDate());
		vm.addParameter(VistaQuery.LITERAL, patientStudyInfo.getPatientSex());
		vm.addParameter(VistaQuery.LITERAL, patientStudyInfo.getPatientICN());
		return vm;
	}

	@Override
	public PatientStudyLookupResults translateGetEntityByCriteria(Object criteria, String returnValue) {
		PatientStudyInfo patientStudyInfo = (PatientStudyInfo)criteria;

		String[] results = DicomTranslatorUtility.createResultsArray(returnValue);
		
		// Check the first line. If there's no comma, it will be the DFN, otherwise it's an error message.
		String firstLine = results[0];
		String dfn = "";
		String siteId= "";
		String [] errorStatus={"", ""};
		List<String> errMsgList=null;
		PatientStudyLookupResults patientStudyLookupResults;
		
		if (firstLine.contains(FIELD_SEPARATOR1))
		{
			// First line has the DFN (2nd piece) and the SiteId (3rd piece) another status code (4th piece) and a message (5th) 
			String[] fields = StringUtils.Split(firstLine, FIELD_SEPARATOR1);
			//Added IF when the first field is -1.  When first field is -1, there is no DFN or Site ID
			//	passed in the results.
			if(fields[0].equals("-1")){
				errorStatus[0] = fields[0];
				errorStatus[1] = fields[1];
			}
			else{
				errorStatus[0]=fields[0];
				dfn = fields[1];
				siteId= fields[2];
				patientStudyInfo.setPatientDFN(dfn);
				patientStudyInfo.setSiteID(siteId);
				if ((fields.length>=5) && (!fields[3].startsWith("0"))) {
					errMsgList = new ArrayList<String>();
					errorStatus[0] = fields[3]; 
					errorStatus[1] = fields[4];
				}
			}
		}
		else
		{
			errorStatus = StringUtils.Split(firstLine, FIELD_SEPARATOR2);
		}

		if (!errorStatus[0].startsWith("0")) // error message exists
		{
			// error message exists (must be delivered to DICOM Correct call). Get it and add it to the list

			errMsgList = new ArrayList<String>();
			errMsgList.add(errorStatus[0]);
			if (errorStatus.length>1)
				errMsgList.add(errorStatus[1]);
		}

		patientStudyLookupResults = new PatientStudyLookupResults(patientStudyInfo, errMsgList);
		
		return patientStudyLookupResults;
	}
}
