/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 4, 2009
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
package gov.va.med.imaging.federation.proxy;

import gov.va.med.imaging.AbstractImagingURN;
import gov.va.med.imaging.CprsIdentifier;
import gov.va.med.imaging.DateUtil;
import gov.va.med.imaging.SizedInputStream;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageConversionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNearLineException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNotFoundException;
import gov.va.med.imaging.core.interfaces.exceptions.InsufficientPatientSensitivityException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.exchange.ImageAccessLogEvent;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.exchange.business.Requestor;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.federation.translator.FederationDatasourceTranslatorV2;
import gov.va.med.imaging.federation.webservices.intf.v2.ImageFederationMetadata;
import gov.va.med.imaging.federation.webservices.soap.v2.ImageMetadataFederationServiceLocator;
import gov.va.med.imaging.federation.webservices.types.v2.FederationFilterType;
import gov.va.med.imaging.federation.webservices.types.v2.FederationImageAccessLogEventType;
import gov.va.med.imaging.federation.webservices.types.v2.FederationStudyLoadLevelType;
import gov.va.med.imaging.federation.webservices.types.v2.FederationStudyType;
import gov.va.med.imaging.federation.webservices.types.v2.PatientSensitiveCheckResponseType;
import gov.va.med.imaging.federation.webservices.types.v2.RequestorType;
import gov.va.med.imaging.federation.webservices.types.v2.RequestorTypePurposeOfUse;
import gov.va.med.imaging.federation.webservices.types.v2.StudiesType;
import gov.va.med.imaging.federationdatasource.configuration.FederationConfiguration;
import gov.va.med.imaging.proxy.ImageXChangeHttpCommonsSender;
import gov.va.med.imaging.proxy.exceptions.ProxyServiceNotFoundException;
import gov.va.med.imaging.proxy.services.ProxyService;
import gov.va.med.imaging.proxy.services.ProxyServiceType;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.transactioncontext.TransactionContextHttpHeaders;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.SortedSet;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.Stub;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * This proxy should not be used for retrieving images - this is not currently supported by this proxy!
 * 
 * @author vhaiswwerfej
 *
 */
public class FederationProxyV2 
extends AbstractFederationProxy 
implements IFederationProxy
{
	private final static FederationDatasourceTranslatorV2 federationTranslator = 
		new FederationDatasourceTranslatorV2();
	
	private ImageFormatQualityList currentImageFormatQualityList = null;
	
	public FederationProxyV2(ProxyServices proxyServices, FederationConfiguration federationConfiguration)
	{
		super(proxyServices, federationConfiguration);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.proxy.ImagingProxy#addOptionalGetInstanceHeaders(org.apache.commons.httpclient.methods.GetMethod)
	 */
	@Override
	protected void addOptionalGetInstanceHeaders(GetMethod getMethod) 
	{
		if(currentImageFormatQualityList != null)
		{
			String headerValue = currentImageFormatQualityList.getAcceptString(false, true); 
			logger.debug("Adding content type with sub type header value [" + headerValue + "]");
			getMethod.setRequestHeader(new Header(TransactionContextHttpHeaders.httpHeaderContentTypeWithSubType, 
				headerValue));
		}
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.proxy.ImagingProxy#getInstance(java.lang.String, gov.va.med.imaging.exchange.business.ImageFormatQualityList, boolean)
	 */
	@Override
	public SizedInputStream getInstance(String imageUrn,
		ImageFormatQualityList requestFormatQualityList,
		boolean includeVistaSecurityContext) 
	throws ImageNearLineException, ImageNotFoundException, 
	SecurityCredentialsExpiredException, ImageConversionException, ConnectionException, MethodException
	{
		try
		{
			currentImageFormatQualityList = requestFormatQualityList;
			return super.getInstance(imageUrn, requestFormatQualityList,
					includeVistaSecurityContext);
		}
		finally 
		{
			currentImageFormatQualityList = null;
		}
	}
	
	private ImageFederationMetadata getImageMetadataService() 
	throws ConnectionException
	{
		try
		{
			URL localTestUrl = new URL(proxyServices.getProxyService(ProxyServiceType.metadata).getConnectionURL());
			ImageMetadataFederationServiceLocator locator = new ImageMetadataFederationServiceLocator();
			ImageFederationMetadata imageMetadata = locator.getImageMetadataFederationV2(localTestUrl);
			((org.apache.axis.client.Stub)imageMetadata).setTimeout(getMetadataTimeoutMs());
			return imageMetadata;
		}
		catch(MalformedURLException murlX)
		{
			logger.error("Error creating URL to access service.", murlX);
			throw new ConnectionException(murlX);
		}
		catch(ServiceException sX)
		{
			logger.error("Service exception." + sX);
			throw new ConnectionException(sX);
		}
	}
	
	private Requestor getRequestor()
	{
		TransactionContext transactionContext = TransactionContextFactory.get();

		if(transactionContext != null)
			return new Requestor(transactionContext.getFullName(), transactionContext.getSsn(), transactionContext.getSiteNumber(), transactionContext.getSiteName());
		return null;
	}
	
	private void setMetadataCredentials(ImageFederationMetadata imageMetadata)
	{
		try
		{
			ProxyService metadataService = proxyServices.getProxyService(ProxyServiceType.metadata);
			
			System.out.println("Metadata parameters is " + (metadataService == null ? "NULL" : "NOT NULL") );
			
			System.out.println("UID = '" + metadataService.getUid() + "'.");
			System.out.println("PWD = '" + metadataService.getCredentials() + "'.");
			
			if(metadataService.getUid() != null)
				((Stub)imageMetadata)._setProperty(Stub.USERNAME_PROPERTY, metadataService.getUid());
			
			if(metadataService.getCredentials() != null)
				((Stub)imageMetadata)._setProperty(Stub.PASSWORD_PROPERTY, metadataService.getCredentials());
		
		}
		catch(ProxyServiceNotFoundException psnfX)
		{
			logger.error(psnfX);
		}
	}
	
	public SortedSet<Study> getStudies(String patientIcn, StudyFilter filter, 
			String siteId, StudyLoadLevel studyLoadLevel)
	throws InsufficientPatientSensitivityException, MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		logger.info("Transaction [" + transactionContext.getTransactionId() + "] initiated ");
		ImageFederationMetadata imageMetadata = getImageMetadataService();
		
		// if the metadata connection parameters is not null and the metadata connection parameters
		// specifies a user ID then set the UID/PWD parameters as XML parameters, which should
		// end up as a BASIC auth parameter in the HTTP header
		setMetadataCredentials(imageMetadata);

		Requestor requestor = getRequestor();
		
		RequestorType rt = requestor == null ?
				new RequestorType() : 
				new RequestorType(
				requestor.getUsername(), 
				requestor.getSsn(), 
				requestor.getFacilityId(), 
				requestor.getFacilityName(), 
				RequestorTypePurposeOfUse.value1);
		FederationStudyLoadLevelType loadLevel = federationTranslator.transformStudyLoadLevel(studyLoadLevel);
		
		//StudyFilter filter = parameters.getFilter();
		FederationFilterType ft = filter == null ? 
			new FederationFilterType() : 
			new FederationFilterType(filter.getStudy_package(), 
					filter.getStudy_class(), 
					filter.getStudy_type(), 
					filter.getStudy_event(), 
					filter.getStudy_specialty(), 
					filter.getFromDate() == null ? null : DateUtil.getShortDateFormat().format(filter.getFromDate()), 
					filter.getToDate() == null ? null : DateUtil.getShortDateFormat().format(filter.getToDate()),
					federationTranslator.transformOrigin(filter.getOrigin()),
					"urn:bhiestudy:" + filter.getStudyId());
		StudiesType wsResult = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try
		{
			Thread.currentThread().setContextClassLoader(ImageXChangeHttpCommonsSender.class.getClassLoader());
			wsResult = imageMetadata.getPatientStudyList(
					rt, 
					ft, 
					patientIcn, 
					transactionContext.getTransactionId(), 
					siteId,
					BigInteger.valueOf(filter.getMaximumAllowedLevel().getCode()), 
					loadLevel);
		}
		catch(RemoteException rX)
		{
			logger.error("Error in getStudies", rX);
			translateRemoteException(rX);
			return null; // this will never happen because translateRemoteException always throws an exception
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(loader);
		}
		logger.info("Transaction [" + transactionContext.getTransactionId() + "] returned " + 
				(wsResult == null ? "null" : "not null") + 
				" results");
		return federationTranslator.transformStudies(wsResult, filter, patientIcn, studyLoadLevel);
	}
	
	public gov.va.med.imaging.federation.webservices.types.v2.PatientType [] searchPatients(
			Site site, String searchCriteria)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		logger.info("searchPatients, Transaction [" + transactionContext.getTransactionId() + "] initiated, search Criteria '" + searchCriteria + "' to '" + site.getSiteNumber() + "'.");
		ImageFederationMetadata imageMetadata = getImageMetadataService();
		
		// if the metadata connection parameters is not null and the metadata connection parameters
		// specifies a user ID then set the UID/PWD parameters as XML parameters, which should
		// end up as a BASIC auth parameter in the HTTP header
		setMetadataCredentials(imageMetadata);
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try
		{
			Thread.currentThread().setContextClassLoader(ImageXChangeHttpCommonsSender.class.getClassLoader());
			gov.va.med.imaging.federation.webservices.types.v2.PatientType[] patientsResult = 
				imageMetadata.searchPatients(searchCriteria, transactionContext.getTransactionId(), 
						site.getSiteNumber());
			logger.info("searchPatients, Transaction [" + transactionContext.getTransactionId() + "] returned response of [" + (patientsResult == null ? "null" :  patientsResult.length) + "] patients.");
			return patientsResult;
		}
		catch(RemoteException rX)
		{
			logger.error("Error in searchPatients", rX);
			translateRemoteException(rX);
			return null; // this will never happen because translateRemoteException always throws an exception
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(loader);
		}
	}
	
	public FederationStudyType getStudyFromCprsIdentifier(Site site, String patientIcn,
		CprsIdentifier cprsIdentifier)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		logger.info("getStudyFromCprsIdentifier, Transaction [" + transactionContext.getTransactionId() + "] initiated, patient Icn '" + patientIcn + "' to '" + site.getSiteNumber() + "'.");
		ImageFederationMetadata imageMetadata = getImageMetadataService();
		
		// if the metadata connection parameters is not null and the metadata connection parameters
		// specifies a user ID then set the UID/PWD parameters as XML parameters, which should
		// end up as a BASIC auth parameter in the HTTP header
		setMetadataCredentials(imageMetadata);
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try
		{
			Thread.currentThread().setContextClassLoader(ImageXChangeHttpCommonsSender.class.getClassLoader());
			
			FederationStudyType studyType = 
				imageMetadata.getStudyFromCprsIdentifier(patientIcn, 
						transactionContext.getTransactionId(), site.getSiteNumber(), 
						cprsIdentifier.getCprsIdentifier());					
			logger.info("getStudyFromCprsIdentifier, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (studyType == null ? "null" : "not null") + "] study.");
			return studyType;		
		}
		catch(RemoteException rX)
		{
			logger.error("Error in searchPatients", rX);
			translateRemoteException(rX);
			return null; // this will never happen because translateRemoteException always throws an exception
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(loader);
		}		
	}
	
	public PatientSensitiveCheckResponseType getPatientSensitiveValue(Site site, String patientIcn)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		logger.info("getPatientSensitiveValue, Transaction [" + transactionContext.getTransactionId() + "] initiated, patient Icn '" + patientIcn + "' to '" + site.getSiteNumber() + "'.");
		ImageFederationMetadata imageMetadata = getImageMetadataService();
		
		// if the metadata connection parameters is not null and the metadata connection parameters
		// specifies a user ID then set the UID/PWD parameters as XML parameters, which should
		// end up as a BASIC auth parameter in the HTTP header
		setMetadataCredentials(imageMetadata);
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try
		{
			Thread.currentThread().setContextClassLoader(ImageXChangeHttpCommonsSender.class.getClassLoader());
			PatientSensitiveCheckResponseType responseType = 
				imageMetadata.getPatientSensitivityLevel(transactionContext.getTransactionId(), 
						site.getSiteNumber(), patientIcn);
			logger.info("getPatientSensitiveValue, Transaction [" + transactionContext.getTransactionId() + "] returned sensitive code of [" + (responseType == null ? "null" :  responseType.getPatientSensitivityLevel().getValue()) + "].");
			return responseType;
		}
		catch(RemoteException rX)
		{
			logger.error("Error in getPatientSensitiveValue", rX);
			translateRemoteException(rX);
			return null; // this will never happen because translateRemoteException always throws an exception
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(loader);
		}
	}

	public String[] getPatientSitesVisited(Site site, String patientIcn)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		logger.info("getPatientSitesVisited, Transaction [" + transactionContext.getTransactionId() + "] initiated, patient Icn '" + patientIcn + "' to '" + site.getSiteNumber() + "'.");
		ImageFederationMetadata imageMetadata = getImageMetadataService();
		
		// if the metadata connection parameters is not null and the metadata connection parameters
		// specifies a user ID then set the UID/PWD parameters as XML parameters, which should
		// end up as a BASIC auth parameter in the HTTP header
		setMetadataCredentials(imageMetadata);
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try
		{
			Thread.currentThread().setContextClassLoader(ImageXChangeHttpCommonsSender.class.getClassLoader());
			String [] sites = imageMetadata.getPatientSitesVisited(patientIcn, transactionContext.getTransactionId(), site.getSiteNumber());
			logger.info("getPatientSitesVisited, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (sites == null ? "null" :  sites.length) + "] sites.");
			return sites;
		}
		catch(RemoteException rX)
		{
			logger.error("Error in getPatientSitesVisited", rX);
			translateRemoteException(rX);
			return null; // this will never happen because translateRemoteException always throws an exception
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(loader);
		}
	}
	
	public boolean logImageAccessEvent(ImageAccessLogEvent logEvent)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		logger.info("logImageAccessEvent, Transaction [" + transactionContext.getTransactionId() + "] initiated, event Type '" + logEvent.getEventType().toString() + "'");
		ImageFederationMetadata imageMetadata = getImageMetadataService();
		
		// if the metadata connection parameters is not null and the metadata connection parameters
		// specifies a user ID then set the UID/PWD parameters as XML parameters, which should
		// end up as a BASIC auth parameter in the HTTP header
		setMetadataCredentials(imageMetadata);
		
		FederationImageAccessLogEventType federationLogEvent = federationTranslator.transformLogEvent(logEvent);
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try
		{
			Thread.currentThread().setContextClassLoader(ImageXChangeHttpCommonsSender.class.getClassLoader());
			boolean result = imageMetadata.postImageAccessEvent(transactionContext.getTransactionId(), federationLogEvent);
			logger.info("logImageAccessEvent, Transaction [" + transactionContext.getTransactionId() + "] logged image access event " + 
					(result == true ? "ok" : "failed"));
			return result;
		}
		catch(RemoteException rX)
		{
			logger.error("Error in searchPatients", rX);
			translateRemoteException(rX);
			return false; // this will never happen because translateRemoteException always throws an exception
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(loader);
		}
	}	
	
	public String getImageInformation(AbstractImagingURN imagingUrn)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		logger.info("getImageInformation, Transaction [" + transactionContext.getTransactionId() + "] initiated, image Urn '" + imagingUrn.toString() + "'");
		ImageFederationMetadata imageMetadata = getImageMetadataService();
		
		// if the metadata connection parameters is not null and the metadata connection parameters
		// specifies a user ID then set the UID/PWD parameters as XML parameters, which should
		// end up as a BASIC auth parameter in the HTTP header
		setMetadataCredentials(imageMetadata);
		String result = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try
		{
			Thread.currentThread().setContextClassLoader(ImageXChangeHttpCommonsSender.class.getClassLoader());
			result = imageMetadata.getImageInformation(imagingUrn.toString(), 
					transactionContext.getTransactionId());
		}
		catch(RemoteException rX)
		{
			logger.error("Error in getImageInformation", rX);
			translateRemoteException(rX);
			return null; // this will never happen because translateRemoteException always throws an exception
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(loader);
		}
		logger.info("getImageInformation, Transaction [" + transactionContext.getTransactionId() + "] returned response of length [" + (result == null ? "null" : result.length()) + "] bytes.");
		return result;
	}
	
	public String getImageSystemGlobalNode(AbstractImagingURN imagingUrn)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		logger.info("getImageSystemGlobalNode, Transaction [" + transactionContext.getTransactionId() + "] initiated, image Urn '" + imagingUrn.toString() + "'");
		ImageFederationMetadata imageMetadata = getImageMetadataService();
		
		// if the metadata connection parameters is not null and the metadata connection parameters
		// specifies a user ID then set the UID/PWD parameters as XML parameters, which should
		// end up as a BASIC auth parameter in the HTTP header
		setMetadataCredentials(imageMetadata);
		String result = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try
		{
			Thread.currentThread().setContextClassLoader(ImageXChangeHttpCommonsSender.class.getClassLoader());
			result = imageMetadata.getImageSystemGlobalNode(imagingUrn.toString(), 
					transactionContext.getTransactionId());
		}
		catch(RemoteException rX)
		{
			logger.error("Error in getImageSystemGlobalNode", rX);
			translateRemoteException(rX);
			return null; // this will never happen because translateRemoteException always throws an exception
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(loader);
		}
		logger.info("getImageSystemGlobalNode, Transaction [" + transactionContext.getTransactionId() + "] returned response of length [" + (result == null ? "null" : result.length()) + "] bytes.");
		return result;
	}	
	
	public String getImageDevFields(AbstractImagingURN imagingUrn, String flags)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		logger.info("getImageDevFields, Transaction [" + transactionContext.getTransactionId() + "] initiated, image Urn '" + imagingUrn.toString() + "'");
		ImageFederationMetadata imageMetadata = getImageMetadataService();
		
		// if the metadata connection parameters is not null and the metadata connection parameters
		// specifies a user ID then set the UID/PWD parameters as XML parameters, which should
		// end up as a BASIC auth parameter in the HTTP header
		setMetadataCredentials(imageMetadata);
		String result = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try
		{
			Thread.currentThread().setContextClassLoader(ImageXChangeHttpCommonsSender.class.getClassLoader());
			result = imageMetadata.getImageDevFields(imagingUrn.toString(), 
					flags, transactionContext.getTransactionId());
		}
		catch(RemoteException rX)
		{
			logger.error("Error in getImageDevFields", rX);
			translateRemoteException(rX);
			return null; // this will never happen because translateRemoteException always throws an exception
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(loader);	
		}		
		logger.info("getImageDevFields, Transaction [" + transactionContext.getTransactionId() + "] returned response of length [" + (result == null ? "null" : result.length()) + "] bytes.");
		return result;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.proxy.ImagingProxy#getInstanceRequestProxyServiceType()
	 */
	@Override
	protected ProxyServiceType getInstanceRequestProxyServiceType() 
	{
		return ProxyServiceType.image;
	}	
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.proxy.ImagingProxy#getTextFileRequestProxyServiceType()
	 */
	@Override
	protected ProxyServiceType getTextFileRequestProxyServiceType() 
	{
		return ProxyServiceType.text;
	}

	@Override
	protected String getDataSourceVersion()
	{
		return "2";
	}
}
