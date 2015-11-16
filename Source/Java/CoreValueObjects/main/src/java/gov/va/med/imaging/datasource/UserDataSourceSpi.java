/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 18, 2011
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
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
package gov.va.med.imaging.datasource;

import java.util.List;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.annotations.SPI;
import gov.va.med.imaging.exchange.business.ApplicationTimeoutParameters;
import gov.va.med.imaging.exchange.business.Division;
import gov.va.med.imaging.exchange.business.ElectronicSignatureResult;
import gov.va.med.imaging.exchange.business.UserInformation;

/**
 * @author vhaiswwerfej
 *
 */
@SPI(description="Interface to get user information.")
public interface UserDataSourceSpi
extends VersionableDataSourceSpi
{
	
	public List<String> getUserKeys(RoutingToken globalRoutingToken)
	throws MethodException, ConnectionException;
	
	public List<Division> getDivisionList(String accessCode, RoutingToken globalRoutingToken)
	throws MethodException, ConnectionException;
	
	/**
	 * User information includes the user keys but keeping the methods separate so to not affect the Importer functionality
	 * @param globalRoutingToken
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public UserInformation getUserInformation(RoutingToken globalRoutingToken)
	throws MethodException, ConnectionException;
	
	/**
	 * Verify an electronic signature is correct
	 * @param globalRoutingToken
	 * @param electronicSignature Cleartext version of electronic signature
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public ElectronicSignatureResult verifyElectronicSignature(RoutingToken globalRoutingToken, String electronicSignature)
	throws MethodException, ConnectionException;

	/**
	 * Get the timeout value for the application
	 * @param siteId
	 * @param applicationName
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	ApplicationTimeoutParameters getApplicationTimeoutParameters(String siteId, String applicationName) 
	throws MethodException, ConnectionException;
	

}
