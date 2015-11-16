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
import gov.va.med.imaging.exchange.business.dicom.Study;
import gov.va.med.imaging.exchange.business.dicom.Series;
import gov.va.med.imaging.exchange.business.storage.exceptions.RetrievalException;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;


public class TIUPointerDAO extends EntityDAO<Series> {

	//RPCs
    public static final String RPC_FIND_STUDY_TIU = "MAGV FIND STUDY TIU";

	// Constructor
	public TIUPointerDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}

	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO#generateGetEntityByExampleQuery(gov.va.med.imaging.exchange.business.PersistentEntity)
	 */
	@Override
	public VistaQuery generateGetEntityByExampleQuery(Series series)
			throws MethodException {

		VistaQuery vm = new VistaQuery(RPC_FIND_STUDY_TIU);
		vm.addParameter(VistaQuery.LITERAL, series.getStudyIEN());		
		return vm;
	}

	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO#translateGetEntityByExample(gov.va.med.imaging.exchange.business.PersistentEntity, java.lang.String)
	 */
	@Override
	public Series translateGetEntityByExample(Series series, String returnValue)
			throws MethodException, RetrievalException {
		
    	if(returnValue == null){
    		throw new RetrievalException("No data returned from get TIU pointer RPC Call.");
    	}
		Integer code = Integer.parseInt(StringUtils.MagPiece(returnValue, StringUtils.BACKTICK, 1));
    	if (code == 0)
		{
			series.setTiuNoteReference(StringUtils.MagPiece(returnValue, StringUtils.BACKTICK, 3));
		}
		else { 
			String[] values = StringUtils.Split(returnValue, StringUtils.BACKTICK);
			if (code > 0)
			{
				series.setTiuNoteReference("0");
			   	logger.info("No Consult or TIU note exists (status=" + values[1] + ")!");
//	    		throw new RetrievalException("No Consult or no TIU note exists: " + values[1]);
			}
			else // code < 0
			{
	    		throw new RetrievalException("Get TIU pointer : " + values[1]);
				
			}
		}
		return series;
	}	
}
