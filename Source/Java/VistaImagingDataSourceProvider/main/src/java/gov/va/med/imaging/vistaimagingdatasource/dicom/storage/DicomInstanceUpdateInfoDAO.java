/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: 
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswpeterb
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
package gov.va.med.imaging.vistaimagingdatasource.dicom.storage;

import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.dicom.DicomInstanceUpdateInfo;
import gov.va.med.imaging.exchange.business.dicom.InstanceStorageInfo;
import gov.va.med.imaging.exchange.business.storage.exceptions.RetrievalException;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import org.apache.log4j.Logger;

/**
 * @author vhaiswpeterb
 *
 */
public class DicomInstanceUpdateInfoDAO extends EntityDAO<DicomInstanceUpdateInfo> {

    private String RPC_IMAGE_CURRENT_INFO = "MAG IMAGE CURRENT INFO";

	
	// Constructor
	public DicomInstanceUpdateInfoDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}


	/* (non-Javadoc)
	 * @see gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO#generateGetEntityByCriteriaQuery(java.lang.Object)
	 */
	@Override
	public VistaQuery generateGetEntityByCriteriaQuery(Object criteria)
			throws MethodException {

		InstanceStorageInfo instance = (InstanceStorageInfo)criteria;
		String ien = instance.getObjectIdentifier();
		String type = instance.getType();
		VistaQuery vm = new VistaQuery(RPC_IMAGE_CURRENT_INFO);
		vm.addParameter(VistaQuery.LITERAL, ien);				
		vm.addParameter(VistaQuery.LITERAL, type);				
		return vm;
	}


	/* (non-Javadoc)
	 * @see gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO#translateGetEntityByCriteria(java.lang.Object, java.lang.String)
	 */
	@Override
	public DicomInstanceUpdateInfo translateGetEntityByCriteria(
			Object criteria, String returnValue) throws MethodException,
			RetrievalException {


		DicomInstanceUpdateInfo imageInfo = new DicomInstanceUpdateInfo();
		if(returnValue == null){
			throw new RetrievalException();
		}
		
		int tagCount = StringUtils.intVal(returnValue);
		logger.debug ("Image has " + tagCount + " tags.");
		for (int t = 0; t < tagCount; t++){
			String line = StringUtils.MagPiece(returnValue, "\r\n", t + 2);
			String tagkey = StringUtils.MagPiece (line, "^", 1);
			String tagval = StringUtils.MagPiece (line, "^", 2);
			imageInfo.put (tagkey, tagval);
		}
		return imageInfo;
	}
}
