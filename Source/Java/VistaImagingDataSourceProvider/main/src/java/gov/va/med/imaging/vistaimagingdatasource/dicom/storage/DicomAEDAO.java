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
import gov.va.med.imaging.exchange.business.dicom.DicomAE;
import gov.va.med.imaging.exchange.business.dicom.DicomServerConfiguration;
import gov.va.med.imaging.exchange.business.storage.exceptions.RetrievalException;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;


public class DicomAEDAO extends EntityDAO<DicomAE> {

	//RPCs
    public static final String RPC_CHECK_AETITLE = "MAG DICOM CHECK AE TITLE"; // query by Remote AE and Site#
    public static final String RPC_GET_AE_ENTRY = "MAG DICOM GET AE ENTRY"; // query by ApplicationName and Site#="0" using DB site#
    public static final String RPC_FIND_AE_ENTRY = "MAG DICOM GET AE ENTRY LOC"; // query by DICOM DIMSE Service and Role (of remote AE -- SCU or SCP) using DB site#
	private static final DicomServerConfiguration config = DicomServerConfiguration.getConfiguration();

	// Constructor
	public DicomAEDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}

	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO#generateGetEntityByExampleQuery(gov.va.med.imaging.exchange.business.PersistentEntity)
	 */
	@Override
	public VistaQuery generateGetEntityByExampleQuery(DicomAE appEntity)
			throws MethodException {
		VistaQuery vm;
		if (appEntity.getFindMode()==DicomAE.searchMode.REMOTE_AE) { // normal case: query by Remote AE and Site#
			vm = new VistaQuery(RPC_CHECK_AETITLE);
		} else if (appEntity.getFindMode()==DicomAE.searchMode.SERVICE_AND_ROLE) { // query by DICOM DIMSE Service and Role (of remote AE) and DB Site#
			vm = new VistaQuery(RPC_FIND_AE_ENTRY);
		} else {											 // query by ApplicationName (and Site#)
			vm = new VistaQuery(RPC_GET_AE_ENTRY);
		}
		vm.addParameter(VistaQuery.LITERAL, appEntity.getRemoteAETitle());	// Remote AE or service for SERVICE_AND_ROLE mode		
		vm.addParameter(VistaQuery.LITERAL, appEntity.getSiteNumber()); // site ID or Role for SERVICE_AND_ROLE mode
		if (appEntity.getFindMode()==DicomAE.searchMode.SERVICE_AND_ROLE) {
			vm.addParameter(VistaQuery.LITERAL, config.getSiteId()); // the site ID for SERVICE_AND_ROLE mode
		}
		return vm;
	}

	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO#translateGetEntityByExample(gov.va.med.imaging.exchange.business.PersistentEntity, java.lang.String)
	 */
	@Override
	public DicomAE translateGetEntityByExample(DicomAE appEntity, String returnValue)
			throws MethodException, RetrievalException {
		
    	if(returnValue == null){
    		throw new RetrievalException("No data returned from RPC Call.");
    	}
		Integer code = Integer.parseInt(StringUtils.MagPiece(returnValue, StringUtils.COMMA, 1));
    	if (code < 0)
		{
			appEntity.setResultCode(code);
			appEntity.setResultMessage(StringUtils.MagPiece(returnValue, StringUtils.COMMA, 2));
		}
		else
		{
			appEntity.setResultCode(0);
			
			String[] values = StringUtils.Split(returnValue, StringUtils.BACKTICK);
			appEntity.setApplicationName(StringUtils.MagPiece(values[0], StringUtils.COMMA, 2));
			appEntity.setRemoteAETitle(values[1]);
			if(appEntity.getRemoteAETitle() == null || appEntity.getRemoteAETitle().equals("")){
				throw new RetrievalException("No or empty Remote AETitle.");
			}
			appEntity.setLocalAETitle(values[2]);
			if(appEntity.getLocalAETitle() == null || appEntity.getLocalAETitle().equals("")){
				throw new RetrievalException("No or empty Local AETitle.");
			}
			appEntity.setSiteNumber(values[3]);
			appEntity.setHostName(values[4]);
			appEntity.setPort(values[5]);
			appEntity.setForceReconciliation(((values[6]!=null) && values[6].equals("1")));
			appEntity.setOriginIndex(values[7]!=null ? values[7] : "V");
			
			String[] flags = StringUtils.Split(values[8], StringUtils.STICK);
			
			for(int j=0; j<flags.length; j++){
				String flagName = StringUtils.MagPiece(flags[j], StringUtils.EQUALS, 1);
				String flagValue = StringUtils.MagPiece(flags[j], StringUtils.EQUALS, 2);
				
				if(flagName.equalsIgnoreCase("REJECT")){
					if(flagValue.equals("1")){
						appEntity.setRejectMessage(true);
					}
					else{
						appEntity.setRejectMessage(false);
					}
				}
				if(flagName.equalsIgnoreCase("WARNING")){				
					if(flagValue.equals("1")){
						appEntity.setWarningMessage(true);
					}
					else{
						appEntity.setWarningMessage(false);
					}
				}				
				if(flagName.equalsIgnoreCase("RESERR")){
					if(flagValue.equals("1")){
						appEntity.setResourceError(true);
					}
					else{
						appEntity.setResourceError(false);
					}
				}
				if(flagName.equalsIgnoreCase("VALIDATE")){
					if(flagValue.equals("1")){
						appEntity.setValidateIODs(true);
					}
					else{
						appEntity.setValidateIODs(false);
					}
				}
				if(flagName.equalsIgnoreCase("RELAX VALIDATION")){
					if(flagValue.equals("1")){
						appEntity.setRelaxValidation(true);
					}
					else{
						appEntity.setRelaxValidation(false);
					}
				}
				if(flagName.equalsIgnoreCase("SERVICE TYPE")){
					appEntity.setImagingService(flagValue);
				}
			}
			String services = StringUtils.MagPiece(values[9], StringUtils.EQUALS, 2);
			String[] serviceValues = StringUtils.Split(services, StringUtils.CARET);
			int remainder = serviceValues.length%2;
			for(int i=0; i<(serviceValues.length-remainder); i=i+2){
				String serviceMsg = serviceValues[i];
				String serviceRole = serviceValues[i+1];
				appEntity.addAEServiceAndRole(serviceMsg, serviceRole);
			}
			appEntity.setDicomNResponseDelay(strToInt(values[10]));
			appEntity.setDicomNRetriesLeft(strToInt(values[11]));
		}
		return appEntity;
	}
	
	private int strToInt (String strInt) {
		int i = 0;
		if ((strInt!=null) && !strInt.isEmpty())
			i=Integer.parseInt(strInt);
		return i;
	}
}
