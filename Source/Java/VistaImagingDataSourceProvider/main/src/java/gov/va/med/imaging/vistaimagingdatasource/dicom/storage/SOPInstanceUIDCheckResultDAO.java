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
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.dicom.UIDCheckInfo;
import gov.va.med.imaging.exchange.business.dicom.UIDCheckResult;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.url.vista.exceptions.InvalidVistaCredentialsException;
import gov.va.med.imaging.url.vista.exceptions.VistaMethodException;
import gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.io.IOException;


public class SOPInstanceUIDCheckResultDAO extends EntityDAO<UIDCheckResult>
{

	private String RPC_MAGV_SOP_UID_CHECK = "MAGV SOP UID CHECK";

	public SOPInstanceUIDCheckResultDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}

	//
	// Retrieval overrides
	//
	@Override
	public VistaQuery generateGetEntityByCriteriaQuery(Object criteria) 
	{
		UIDCheckInfo uidCheckInfo = (UIDCheckInfo)criteria;
		VistaQuery vm = new VistaQuery(RPC_MAGV_SOP_UID_CHECK);
		vm.addParameter(VistaQuery.LITERAL, uidCheckInfo.getPatientDFN());
		vm.addParameter(VistaQuery.LITERAL, uidCheckInfo.getStudyAccessionNumber());
		vm.addParameter(VistaQuery.LITERAL, uidCheckInfo.getSiteID());
		vm.addParameter(VistaQuery.LITERAL, uidCheckInfo.getInstrumentID());
		vm.addParameter(VistaQuery.LITERAL, uidCheckInfo.getStudyInstanceUID());
		vm.addParameter(VistaQuery.LITERAL, uidCheckInfo.getSeriesInstanceUID());
		vm.addParameter(VistaQuery.LITERAL, uidCheckInfo.getSOPInstanceUID());
		
		return vm;
	}

	@Override
	public UIDCheckResult translateGetEntityByCriteria(Object criteria, String returnValue) 
	{
		UIDCheckInfo uidCheckInfo = (UIDCheckInfo)criteria;
		return VistaImagingDicomStorageUtility.translateUIDCheckResults(returnValue, uidCheckInfo.getSOPInstanceUID(), FIELD_SEPARATOR1);
	}
	
}
