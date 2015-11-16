/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 29, 2010
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
package gov.va.med.imaging.federation.rest.proxy;

import javax.ws.rs.core.MediaType;

import gov.va.med.imaging.federationdatasource.configuration.FederationConfiguration;
import gov.va.med.imaging.proxy.rest.AbstractRestClient;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.transactioncontext.TransactionContextHttpHeaders;

/**
 * @author vhaiswwerfej
 *
 */
public abstract class AbstractFederationRestClient
extends AbstractRestClient
{
	private final static int defaultMetadataTimeoutMs = 600000;

	public AbstractFederationRestClient(String url, String mediaType, 
			FederationConfiguration federationConfiguration)
	{
		super(url, mediaType, getMetadataTimeoutMs(federationConfiguration));
	}
	
	public AbstractFederationRestClient(String url, MediaType mediaType, 
			FederationConfiguration federationConfiguration)
	{
		this(url, mediaType.toString(), federationConfiguration);
	}

	@Override
	protected void addTransactionHeaders()
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		String duz = transactionContext.getDuz();
    	if(duz != null && duz.length() > 0)
    		request.header( TransactionContextHttpHeaders.httpHeaderDuz, duz);
    	
    	String fullname = transactionContext.getFullName();
    	if(fullname != null && fullname.length() > 0)
    		request.header( TransactionContextHttpHeaders.httpHeaderFullName, fullname);

    	String sitename = transactionContext.getSiteName();
    	if(sitename != null && sitename.length() > 0)
    		request.header( TransactionContextHttpHeaders.httpHeaderSiteName, sitename);

    	String sitenumber = transactionContext.getSiteNumber();
    	if(sitenumber != null && sitenumber.length() > 0)
    		request.header( TransactionContextHttpHeaders.httpHeaderSiteNumber, sitenumber);
    	
    	String ssn = transactionContext.getSsn();
    	if(ssn != null && ssn.length() > 0)
    		request.header( TransactionContextHttpHeaders.httpHeaderSSN, ssn);
    	
    	String securityToken = transactionContext.getBrokerSecurityToken();
    	if(securityToken != null && securityToken.length() > 0)
    		request.header(TransactionContextHttpHeaders.httpHeaderBrokerSecurityTokenId, securityToken);
    	
    	String cacheLocationId = transactionContext.getCacheLocationId();
    	if(cacheLocationId != null && cacheLocationId.length() > 0)
    		request.header(TransactionContextHttpHeaders.httpHeaderCacheLocationId, cacheLocationId);
    	
    	String userDivision = transactionContext.getUserDivision();
    	if(userDivision != null && userDivision.length() > 0)
    		request.header(TransactionContextHttpHeaders.httpHeaderUserDivision, userDivision);	
    	
    	String transactionId = transactionContext.getTransactionId();
    	if(transactionId != null && transactionId.length() > 0)
    		request.header(TransactionContextHttpHeaders.httpHeaderTransactionId, transactionId);
    	
    	String requestingVixSiteNumber = transactionContext.getVixSiteNumber();
    	if(requestingVixSiteNumber != null && requestingVixSiteNumber.length() > 0)
    		request.header(TransactionContextHttpHeaders.httpHeaderRequestingVixSiteNumber, requestingVixSiteNumber);
    	
    	String imagingSecurityContextType = transactionContext.getImagingSecurityContextType();
    	if(imagingSecurityContextType != null && imagingSecurityContextType.length() > 0)
    		request.header(TransactionContextHttpHeaders.httpHeaderOptionContext, imagingSecurityContextType);
	}

	private static int getMetadataTimeoutMs(FederationConfiguration federationConfiguration)
	{
		if(federationConfiguration != null)
		{
			if(federationConfiguration.getMetadataTimeoutMs() != null)
				return federationConfiguration.getMetadataTimeoutMs();
		}
		return defaultMetadataTimeoutMs;
	}
}
