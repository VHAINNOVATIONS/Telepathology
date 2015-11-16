/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 4, 2012
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
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
package gov.va.med.imaging.awiv;

import gov.va.med.imaging.encryption.AesEncryption;
import gov.va.med.imaging.encryption.exceptions.AesEncryptionException;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author VHAISWWERFEJ
 *
 */
public class AwivEncryption
{
	public final static String viewTypeVistaImaging = "VI";
	public final static String viewTypeVistaWeb = "VW";
	public final static String viewTypeVistaImagingDoD = "VIDOD";
	
	public final static String ncatHasKey = "1";
	public final static String ncatDoesNotHasKey = "0";
	
	public static String encryptParameters(String patientName, String patientSsn, String patientIcn, 
			String identifier, String identifierSiteNumber, String siteServiceUrl, String cvixSiteNumber, 
			String viewType, String hasNcatKey,
			String patientLookupSite)
	throws AesEncryptionException
	{
		StringBuilder awivParameters = new StringBuilder();
		
		awivParameters.append("&A{" + patientName + "}");
    	awivParameters.append("&B{" + patientSsn + "}");
    	awivParameters.append("&C{" + patientIcn + "}");
    
    	TransactionContext context = TransactionContextFactory.get();    	    	
    
    	awivParameters.append("&D{" + identifier + "}");
    	
    	awivParameters.append("&E{" + identifierSiteNumber + "}");
    	awivParameters.append("&F{" + context.getFullName() + "}");
    	awivParameters.append("&G{" + context.getDuz() + "}");
    	awivParameters.append("&H{" + context.getSsn() + "}");
    	awivParameters.append("&I{" + context.getSiteName() + "}");
    	awivParameters.append("&J{" + context.getSiteNumber() + "}");    	
		awivParameters.append("&K{" + context.getBrokerSecurityToken() + "}"); // place holder saved for BSE token
		awivParameters.append("&L{" + siteServiceUrl + "}");
		awivParameters.append("&M{" + cvixSiteNumber + "}"); // CVIX site in site service
		//awivParameters.append("&L{" + configuration.getSiteServiceUrl() + "}");
		//awivParameters.append("&M{" + configuration.getCvixSiteServiceSiteNumber() + "}"); // CVIX site in site service
		awivParameters.append("&O{" + viewType + "}"); // VI for VistA Imaging, VW for VistAWeb and VIDOD for DoD artifacts
		/*
		if(configuration.isCVIXSiteInformationIncluded())
		{
			awivParameters.append("&P{" + configuration.getCVIXSiteInformationParameterString() + "}");	
		} */
		awivParameters.append("&Q{" + hasNcatKey + "}"); // 1 if the user has the key, 0 otherwise   
		awivParameters.append("&R{" + patientLookupSite + "}"); // the patient lookup site
		
		return AesEncryption.encrypt(awivParameters.toString());
	}

}
