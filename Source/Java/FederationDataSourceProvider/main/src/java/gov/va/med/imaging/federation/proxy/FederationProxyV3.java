/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 16, 2009
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

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.SortedSet;
import java.util.zip.Checksum;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.Stub;

import gov.va.med.*;
import gov.va.med.imaging.*;
import gov.va.med.imaging.core.interfaces.exceptions.*;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.ImageAccessLogEvent;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.exchange.business.PassthroughInputMethod;
import gov.va.med.imaging.exchange.business.Patient;
import gov.va.med.imaging.exchange.business.PatientSensitiveValue;
import gov.va.med.imaging.exchange.business.Requestor;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.business.vistarad.ActiveExams;
import gov.va.med.imaging.exchange.business.vistarad.Exam;
import gov.va.med.imaging.exchange.business.vistarad.ExamImages;
import gov.va.med.imaging.exchange.business.vistarad.PatientRegistration;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.exchange.translation.AbstractTranslator;
import gov.va.med.imaging.exchange.translation.exceptions.TranslationException;
import gov.va.med.imaging.exchange.utility.Base32ConversionUtility;
import gov.va.med.imaging.federation.webservices.intf.v3.ImageFederationMetadata;
import gov.va.med.imaging.federation.webservices.translation.v3.Translator;
import gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType;
import gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType;
import gov.va.med.imaging.federationdatasource.configuration.FederationConfiguration;
import gov.va.med.imaging.proxy.ImageXChangeHttpCommonsSender;
import gov.va.med.imaging.proxy.exceptions.ProxyServiceNotFoundException;
import gov.va.med.imaging.proxy.services.ProxyService;
import gov.va.med.imaging.proxy.services.ProxyServiceType;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.transactioncontext.TransactionContextHttpHeaders;
import gov.va.med.imaging.url.federation.exceptions.FederationMethodException;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * @author vhaiswwerfej
 *
 */
public class FederationProxyV3 
extends AbstractFederationProxy 
implements IFederationProxy 
{
	// Registering the translator class allows other translators to use the translators
	// methods through reflection and type comparison.
	static
	{
		AbstractTranslator.registerTranslatorClass(gov.va.med.imaging.federation.webservices.translation.v3.Translator.class);
	}
	
	private ImageFormatQualityList currentImageFormatQualityList = null;

	public FederationProxyV3(ProxyServices proxyServices, FederationConfiguration federationConfiguration)
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
	
	private ImageFederationMetadata getImageMetadataService(ProxyServiceType proxyServiceType) 
	throws ConnectionException
	{
		try
		{
			URL localTestUrl = new URL(proxyServices.getProxyService(proxyServiceType).getConnectionURL());
			gov.va.med.imaging.federation.webservices.soap.v3.ImageMetadataFederationServiceLocator locator = 
				new gov.va.med.imaging.federation.webservices.soap.v3.ImageMetadataFederationServiceLocator();
			ImageFederationMetadata imageMetadata = locator.getImageMetadataFederationV3(localTestUrl);
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
	
	private void setMetadataCredentials(ImageFederationMetadata imageMetadata, ProxyServiceType proxyServiceType)
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

	/**
	 * Override to encode the study and image IDs in Base32
	 */
	@Override
	public SizedInputStream getInstance(
		String imageUrn, 
		ImageFormatQualityList requestFormatQualityList, 
		Checksum checksum, 
		boolean includeVistaSecurityContext) 
	throws ImageNearLineException, ImageNotFoundException, 
	SecurityCredentialsExpiredException, ImageConversionException, MethodException, ConnectionException
	{
		try
		{
			// JMW 3/15/2011 Patch 104
			// The patch 104 VIX/CVIX introduces new image formats to support (XLS, DOCX, etc). When making a request
			// to a patch 83 VIX if those new formats are included, it causes an exception (not really sure why).
			// To solve this issue, the new formats are removed from the request when being made from a P104 VIX to a P83 VIX
			List<ImageFormat> fedV3AllowedImageFormats = 
				federationConfiguration.getAllowedImageFormats().get(FederationConfiguration.federationVersion3Number);
			requestFormatQualityList.pruneToAllowedFormats(fedV3AllowedImageFormats);
			currentImageFormatQualityList = requestFormatQualityList;
			try
			{
				URN urn = URNFactory.create(imageUrn, SERIALIZATION_FORMAT.RAW);
				if(urn instanceof ImageURN)
				{
					urn = ImageURNFactory.create(((ImageURN) urn).getOriginatingSiteId(),
						Base32ConversionUtility.base32Encode( ((ImageURN) urn).getImageId() ), 
						Base32ConversionUtility.base32Encode( ((ImageURN) urn).getStudyId() ), 
						((ImageURN) urn).getPatientId(), 
						((ImageURN) urn).getImageModality(),
						ImageURN.class
					);
					imageUrn = urn.toString();
				}
			}
			catch (URNFormatException x)
			{
				logger.warn("Exception creating URN from '" + imageUrn + "'.");
			}		
			return super.getInstance(imageUrn, requestFormatQualityList, checksum, includeVistaSecurityContext);
		}
		finally
		{
			currentImageFormatQualityList = null;
		}
	}	
	
	public String getExamRequisitionReport(StudyURN studyUrn)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		logger.info("getExamRequisitionReport, Transaction [" + transactionContext.getTransactionId() + "] initiated, studyURN '" + studyUrn.toString() + "'.");
		setDataSourceMethodAndVersion("getExamRequisitionReport");
		ImageFederationMetadata imageMetadata = getImageMetadataService(ProxyServiceType.vistaRadMetadata);
		
		// if the metadata connection parameters is not null and the metadata connection parameters
		// specifies a user ID then set the UID/PWD parameters as XML parameters, which should
		// end up as a BASIC auth parameter in the HTTP header
		setMetadataCredentials(imageMetadata, ProxyServiceType.vistaRadMetadata);
		String result = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try
		{
			Thread.currentThread().setContextClassLoader(ImageXChangeHttpCommonsSender.class.getClassLoader());
			result = imageMetadata.getVistaRadRequisitionReport(transactionContext.getTransactionId(), 
					studyUrn.toString(SERIALIZATION_FORMAT.PATCH83_VFTP));
		}
		catch(FederationSecurityCredentialsExpiredExceptionFaultType fsveXft)
		{
			logger.error("Error in getExamRequisitionReport", fsveXft);
			throw new SecurityCredentialsExpiredException(fsveXft.getMessage1());
		}
		catch(FederationMethodExceptionFaultType fmXft)
		{
			logger.error("Error in getExamRequisitionReport", fmXft);
			throw new FederationMethodException(fmXft);
		}
		catch(RemoteException rX)
		{
			// RemoteExceptions should not happen anymore unless major problem on server, use connection exception since it is safer for router
			logger.error("Error in getExamRequisitionReport", rX);
			throw new ConnectionException(rX);
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(loader);
		}
		logger.info("getExamRequisitionReport, Transaction [" + transactionContext.getTransactionId() + "] returned response of length [" + (result == null ? "null" : result.length()) + "] bytes.");
		return result;
	}
	
	public String getExamRadiologyReport(StudyURN studyUrn)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		logger.info("getExamRadiologyReport, Transaction [" + transactionContext.getTransactionId() + "] initiated, studyURN '" + studyUrn.toString() + "'.");
		setDataSourceMethodAndVersion("getExamRadiologyReport");
		ImageFederationMetadata imageMetadata = getImageMetadataService(ProxyServiceType.vistaRadMetadata);
		
		// if the metadata connection parameters is not null and the metadata connection parameters
		// specifies a user ID then set the UID/PWD parameters as XML parameters, which should
		// end up as a BASIC auth parameter in the HTTP header
		setMetadataCredentials(imageMetadata, ProxyServiceType.vistaRadMetadata);
		String result = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try
		{
			Thread.currentThread().setContextClassLoader(ImageXChangeHttpCommonsSender.class.getClassLoader());
			result = imageMetadata.getVistaRadRadiologyReport(transactionContext.getTransactionId(), 
					studyUrn.toString(SERIALIZATION_FORMAT.PATCH83_VFTP));
		}
		catch(FederationSecurityCredentialsExpiredExceptionFaultType fsveXft)
		{
			logger.error("Error in getExamRadiologyReport", fsveXft);
			throw new SecurityCredentialsExpiredException(fsveXft.getMessage1());
		}
		catch(FederationMethodExceptionFaultType fmXft)
		{
			logger.error("Error in getExamRadiologyReport", fmXft);
			throw new FederationMethodException(fmXft);
		}
		catch(RemoteException rX)
		{
			// RemoteExceptions should not happen anymore unless major problem on server, use connection exception since it is safer for router
			logger.error("Error in getExamRadiologyReport", rX);
			throw new ConnectionException(rX);
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(loader);
		}
		logger.info("getExamRadiologyReport, Transaction [" + transactionContext.getTransactionId() + "] returned response of length [" + (result == null ? "null" : result.length()) + "] bytes.");
		return result;
	}
	
	public ActiveExams getActiveExams(Site site, String listDescriptor)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		logger.info("getActiveExams, Transaction [" + transactionContext.getTransactionId() + "] initiated, list Descriptor '" + listDescriptor + "' to '" + site.getSiteNumber() + "'.");
		setDataSourceMethodAndVersion("getActiveExams");
		ImageFederationMetadata imageMetadata = getImageMetadataService(ProxyServiceType.vistaRadMetadata);
		
		// if the metadata connection parameters is not null and the metadata connection parameters
		// specifies a user ID then set the UID/PWD parameters as XML parameters, which should
		// end up as a BASIC auth parameter in the HTTP header
		setMetadataCredentials(imageMetadata, ProxyServiceType.vistaRadMetadata);
		gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadActiveExamsType activeExams = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try
		{
			Thread.currentThread().setContextClassLoader(ImageXChangeHttpCommonsSender.class.getClassLoader());
			activeExams = imageMetadata.getActiveWorklist(transactionContext.getTransactionId(), 
					site.getSiteNumber(), listDescriptor);					

			logger.info("getActiveExams, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (activeExams == null ? "null" :  "not null") + "] active exams webservice objects.");
			ActiveExams result = gov.va.med.imaging.federation.webservices.translation.v3.Translator.translate(activeExams);
				//translator.transformActiveExams(activeExams);		
			logger.info("getActiveExams, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (result == null ? "null" :  result.size()) + "] active exams business objects.");		
			return result;
		}
		catch(FederationSecurityCredentialsExpiredExceptionFaultType fsveXft)
		{
			logger.error("Error in getActiveExams", fsveXft);
			throw new SecurityCredentialsExpiredException(fsveXft.getMessage1());
		}
		catch(FederationMethodExceptionFaultType fmXft)
		{
			logger.error("Error in getActiveExams", fmXft);
			throw new FederationMethodException(fmXft);
		}
		catch(RemoteException rX)
		{
			// RemoteExceptions should not happen anymore unless major problem on server, use connection exception since it is safer for router
			logger.error("Error in getActiveExams", rX);
			throw new ConnectionException(rX);
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(loader);
		}		
	}
	
	public ExamImages getExamImagesForExam(StudyURN studyUrn)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		logger.info("getExamImagesForExam, Transaction [" + transactionContext.getTransactionId() + "] initiated, studyURN '" + studyUrn.toString() + "'.");
		setDataSourceMethodAndVersion("getExamImagesForExam");
		ImageFederationMetadata imageMetadata = getImageMetadataService(ProxyServiceType.vistaRadMetadata);
		
		// if the metadata connection parameters is not null and the metadata connection parameters
		// specifies a user ID then set the UID/PWD parameters as XML parameters, which should
		// end up as a BASIC auth parameter in the HTTP header
		setMetadataCredentials(imageMetadata, ProxyServiceType.vistaRadMetadata);
		gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImagesType examImagesType = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try
		{
			String base32EncodedStudyUrn = studyUrn.toString(SERIALIZATION_FORMAT.PATCH83_VFTP);
			Thread.currentThread().setContextClassLoader(ImageXChangeHttpCommonsSender.class.getClassLoader());
			examImagesType = imageMetadata.getExamImagesForExam(transactionContext.getTransactionId(), 
					base32EncodedStudyUrn);								
		}
		catch(FederationSecurityCredentialsExpiredExceptionFaultType fsveXft)
		{
			logger.error("Error in getExamImagesForExam", fsveXft);
			throw new SecurityCredentialsExpiredException(fsveXft.getMessage1());
		}
		catch(FederationMethodExceptionFaultType fmXft)
		{
			logger.error("Error in getExamImagesForExam", fmXft);
			throw new FederationMethodException(fmXft);
		}
		catch(RemoteException rX)
		{
			// RemoteExceptions should not happen anymore unless major problem on server, use connection exception since it is safer for router
			logger.error("Error in getExamImagesForExam", rX);
			throw new ConnectionException(rX);
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(loader);
		}
		try
		{
			logger.info("getExamImagesForExam, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (examImagesType == null ? "null" :  "not null") + "] exam images webservice objects.");
			ExamImages result = gov.va.med.imaging.federation.webservices.translation.v3.Translator.translate(examImagesType); 
				// translator.transformExamImages(examImagesType);
			logger.info("getExamImagesForExam, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (result == null ? "null" :  result.size()) + "] exam images business objects.");		
			return result;
		}
		catch (URNFormatException urnFX)
		{
			logger.error("Error translating getExamImagesForExam", urnFX);
			throw new MethodException(urnFX);
		}
	}
	
	public Exam getExam(StudyURN studyUrn)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		logger.info("getExam, Transaction [" + transactionContext.getTransactionId() + "] initiated, studyURN '" + studyUrn.toString() + "'.");
		setDataSourceMethodAndVersion("getExam");
		ImageFederationMetadata imageMetadata = getImageMetadataService(ProxyServiceType.vistaRadMetadata);
		
		// if the metadata connection parameters is not null and the metadata connection parameters
		// specifies a user ID then set the UID/PWD parameters as XML parameters, which should
		// end up as a BASIC auth parameter in the HTTP header
		setMetadataCredentials(imageMetadata, ProxyServiceType.vistaRadMetadata);
		gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamType exam = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try
		{
			String base32EncodedStudyUrn = studyUrn.toString(SERIALIZATION_FORMAT.PATCH83_VFTP);
			logger.info("getExam, studyURN base 32 encoded '" + base32EncodedStudyUrn + "'.");
			Thread.currentThread().setContextClassLoader(ImageXChangeHttpCommonsSender.class.getClassLoader());
			exam = imageMetadata.getPatientExam(transactionContext.getTransactionId(), 
					base32EncodedStudyUrn);					
		}
		catch(FederationSecurityCredentialsExpiredExceptionFaultType fsveXft)
		{
			logger.error("Error in getExam", fsveXft);
			throw new SecurityCredentialsExpiredException(fsveXft.getMessage1());
		}
		catch(FederationMethodExceptionFaultType fmXft)
		{
			logger.error("Error in getExam", fmXft);
			throw new FederationMethodException(fmXft);
		}
		catch(RemoteException rX)
		{
			// RemoteExceptions should not happen anymore unless major problem on server, use connection exception since it is safer for router
			logger.error("Error in getExam", rX);
			throw new ConnectionException(rX);
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(loader);
		}
		try
		{
			logger.info("getExam, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (exam == null ? "null" :  "not null") + "] exam webservice objects.");
			Exam result = gov.va.med.imaging.federation.webservices.translation.v3.Translator.translate(exam); 
				//translator.transformExam(exam);		
			logger.info("getExam, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (result == null ? "null" :  "not null") + "] patient exams business objects.");		
			return result;
		}
		catch (URNFormatException urnFX)
		{
			logger.error("Error translating getExam", urnFX);
			throw new MethodException(urnFX);
		}
	}
	
	public List<Exam> getExamsForPatient(Site site, String patientIcn,
			boolean fullyLoadExams)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		logger.info("getExamsForPatient, Transaction [" + transactionContext.getTransactionId() + "] initiated, patient Icn '" + patientIcn + "' to '" + site.getSiteNumber() + "'.");
		setDataSourceMethodAndVersion("getExamsForPatient");
		ImageFederationMetadata imageMetadata = getImageMetadataService(ProxyServiceType.vistaRadMetadata);
		
		// if the metadata connection parameters is not null and the metadata connection parameters
		// specifies a user ID then set the UID/PWD parameters as XML parameters, which should
		// end up as a BASIC auth parameter in the HTTP header
		setMetadataCredentials(imageMetadata, ProxyServiceType.vistaRadMetadata);
		gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamType [] exams = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try
		{
			Thread.currentThread().setContextClassLoader(ImageXChangeHttpCommonsSender.class.getClassLoader());
			exams = imageMetadata.getPatientExams(transactionContext.getTransactionId(), 
					site.getSiteNumber(), patientIcn, fullyLoadExams);					
		}
		catch(FederationSecurityCredentialsExpiredExceptionFaultType fsveXft)
		{
			logger.error("Error in getExamsForPatient", fsveXft);
			throw new SecurityCredentialsExpiredException(fsveXft.getMessage1());
		}
		catch(FederationMethodExceptionFaultType fmXft)
		{
			logger.error("Error in getExamsForPatient", fmXft);
			throw new FederationMethodException(fmXft);
		}
		catch(RemoteException rX)
		{
			// RemoteExceptions should not happen anymore unless major problem on server, use connection exception since it is safer for router
			logger.error("Error in getExamsForPatient", rX);
			throw new ConnectionException(rX);
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(loader);
		}
		try
		{
			logger.info("getExamsForPatient, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (exams == null ? "null" :  "not null") + "] exams webservice objects.");
			List<Exam> result = gov.va.med.imaging.federation.webservices.translation.v3.Translator.translate(exams); 

				//translator.transformExams(exams);		
			logger.info("getExamsForPatient, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (result == null ? "null" :  result.size()) + "] patient exams business objects.");		
			return result;
		}
		catch (URNFormatException urnFX)
		{
			logger.error("Error translating getExamsForPatient", urnFX);
			throw new MethodException(urnFX);
		}
	}
	
	public PatientRegistration getNextPatientRegistration(Site site)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		logger.info("getNextPatientRegistration, Transaction [" + transactionContext.getTransactionId() + "] initiated, to '" + site.getSiteNumber() + "'.");
		setDataSourceMethodAndVersion("getNextPatientRegistration");
		ImageFederationMetadata imageMetadata = getImageMetadataService(ProxyServiceType.vistaRadMetadata);
		
		// if the metadata connection parameters is not null and the metadata connection parameters
		// specifies a user ID then set the UID/PWD parameters as XML parameters, which should
		// end up as a BASIC auth parameter in the HTTP header
		setMetadataCredentials(imageMetadata, ProxyServiceType.vistaRadMetadata);
		gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadPatientRegistrationType patientRegistration = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try
		{
			Thread.currentThread().setContextClassLoader(ImageXChangeHttpCommonsSender.class.getClassLoader());
			patientRegistration = imageMetadata.getNextPatientRegistration(transactionContext.getTransactionId(), 
					site.getSiteNumber());

			logger.info("getNextPatientRegistration, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (patientRegistration == null ? "null" :  "not null") + "] patient registration webservice objects.");
			PatientRegistration result = gov.va.med.imaging.federation.webservices.translation.v3.Translator.translate(patientRegistration); 
			logger.info("getNextPatientRegistration, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (result == null ? "null" :  "not null") + "] patient registration business objects.");		
			return result;
		}
		catch(FederationSecurityCredentialsExpiredExceptionFaultType fsveXft)
		{
			logger.error("Error in getNextPatientRegistration", fsveXft);
			throw new SecurityCredentialsExpiredException(fsveXft.getMessage1());
		}
		catch(FederationMethodExceptionFaultType fmXft)
		{
			logger.error("Error in getNextPatientRegistration", fmXft);
			throw new FederationMethodException(fmXft);
		}
		catch(RemoteException rX)
		{
			// RemoteExceptions should not happen anymore unless major problem on server, use connection exception since it is safer for router
			logger.error("Error in getNextPatientRegistration", rX);
			throw new ConnectionException(rX);
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(loader);
		}
	}
	
	public String[] getRelevantPriorCptCodes(Site site, String cptCode)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		logger.info("getRelevantPriorCptCodes, Transaction [" + transactionContext.getTransactionId() + "] initiated, cpt code '" + cptCode + "', to '" + site.getSiteNumber() + "'.");
		setDataSourceMethodAndVersion("getRelevantPriorCptCodes");
		ImageFederationMetadata imageMetadata = getImageMetadataService(ProxyServiceType.vistaRadMetadata);
		
		// if the metadata connection parameters is not null and the metadata connection parameters
		// specifies a user ID then set the UID/PWD parameters as XML parameters, which should
		// end up as a BASIC auth parameter in the HTTP header
		setMetadataCredentials(imageMetadata, ProxyServiceType.vistaRadMetadata);
		String [] cptCodes = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try
		{
			Thread.currentThread().setContextClassLoader(ImageXChangeHttpCommonsSender.class.getClassLoader());
			cptCodes = imageMetadata.getRelevantPriorCptCodes(transactionContext.getTransactionId(), 
					cptCode, site.getSiteNumber());
		}
		catch(FederationSecurityCredentialsExpiredExceptionFaultType fsveXft)
		{
			logger.error("Error in getRelevantPriorCptCodes", fsveXft);
			throw new SecurityCredentialsExpiredException(fsveXft.getMessage1());
		}
		catch(FederationMethodExceptionFaultType fmXft)
		{
			logger.error("Error in getRelevantPriorCptCodes", fmXft);
			throw new FederationMethodException(fmXft);
		}
		catch(RemoteException rX)
		{
			// RemoteExceptions should not happen anymore unless major problem on server, use connection exception since it is safer for router
			logger.error("Error in getRelevantPriorCptCodes", rX);
			throw new ConnectionException(rX);
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(loader);
		}				
		logger.info("getRelevantPriorCptCodes, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (cptCodes == null ? "null" :  cptCodes.length) + "] cpt codes.");		
		return cptCodes;
	}
	
	public SortedSet<Study> getStudies(String patientIcn, StudyFilter filter, 
			String siteId, StudyLoadLevel studyLoadLevel)
	throws InsufficientPatientSensitivityException, MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		setDataSourceMethodAndVersion("getStudies");
		logger.info("Transaction [" + transactionContext.getTransactionId() + "] initiated ");
		ImageFederationMetadata imageMetadata = getImageMetadataService(ProxyServiceType.metadata);
		
		// if the metadata connection parameters is not null and the metadata connection parameters
		// specifies a user ID then set the UID/PWD parameters as XML parameters, which should
		// end up as a BASIC auth parameter in the HTTP header
		setMetadataCredentials(imageMetadata, ProxyServiceType.metadata);

		Requestor requestor = getRequestor();
		
		gov.va.med.imaging.federation.webservices.types.v3.StudiesType wsResult = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try
		{
			Thread.currentThread().setContextClassLoader(ImageXChangeHttpCommonsSender.class.getClassLoader());
			
			gov.va.med.imaging.federation.webservices.types.v3.RequestorType rt = requestor == null ?
				new gov.va.med.imaging.federation.webservices.types.v3.RequestorType() : 
				new gov.va.med.imaging.federation.webservices.types.v3.RequestorType(
				requestor.getUsername(), 
				requestor.getSsn(), 
				requestor.getFacilityId(), 
				requestor.getFacilityName(), 
				gov.va.med.imaging.federation.webservices.types.v3.RequestorTypePurposeOfUse.value1);
				gov.va.med.imaging.federation.webservices.types.v3.FederationStudyLoadLevelType loadLevel = 
					gov.va.med.imaging.federation.webservices.translation.v3.Translator.translate(studyLoadLevel);
					//translator.transformStudyLoadLevel(studyLoadLevel);
		
			//StudyFilter filter = parameters.getFilter();
				
			// need to base 32 encode the study Id if it is included in the filter
			String studyId = null;
			if(filter.isStudyIenSpecified())
			{
				GlobalArtifactIdentifier gai = filter.getStudyId();
				if(gai instanceof StudyURN)
				{
					StudyURN studyUrn = (StudyURN)gai;
					studyId = Base32ConversionUtility.base32Encode(studyUrn.getStudyId());
					logger.info("Filter study Id '" + filter.getStudyId() + "' converted to study Id '" + studyId + "'.");
				}
			}				
				
			gov.va.med.imaging.federation.webservices.types.v3.FederationFilterType ft = filter == null ? 
				new gov.va.med.imaging.federation.webservices.types.v3.FederationFilterType() : 
				new gov.va.med.imaging.federation.webservices.types.v3.FederationFilterType(
					filter.getStudy_package(), 
					filter.getStudy_class(), 
					filter.getStudy_type(), 
					filter.getStudy_event(), 
					filter.getStudy_specialty(), 
					filter.getFromDate() == null ? null : DateUtil.getShortDateFormat().format(filter.getFromDate()), 
					filter.getToDate() == null ? null : DateUtil.getShortDateFormat().format(filter.getToDate()),
					gov.va.med.imaging.federation.webservices.translation.v3.Translator.translateOrigin(filter.getOrigin()),				
							// translator.transformOrigin(filter.getOrigin()),
					studyId
				);
			
			wsResult = imageMetadata.getPatientStudyList(
				rt, 
				ft, 
				patientIcn, 
				transactionContext.getTransactionId(), 
				siteId,
				BigInteger.valueOf(filter.getMaximumAllowedLevel().getCode()), 
				loadLevel);
			
			logger.info("getStudies, Transaction [" + transactionContext.getTransactionId() + "] returned " + 
				(wsResult == null ? "null" : "not null") + 
				" web service results");
			SortedSet<Study> result = 
				gov.va.med.imaging.federation.webservices.translation.v3.Translator.translate(wsResult, filter, patientIcn);
			logger.info("getStudies, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (result == null ? "null" :  result.size()) + "] study business objects.");
			return result;
		}
		catch(FederationSecurityCredentialsExpiredExceptionFaultType fsveXft)
		{
			logger.error("Error in getStudies", fsveXft);
			throw new SecurityCredentialsExpiredException(fsveXft.getMessage1());
		}
		catch(FederationMethodExceptionFaultType fmXft)
		{
			logger.error("Error in getStudies", fmXft);
			throw new FederationMethodException(fmXft);
		}
		catch(RemoteException rX)
		{
			logger.error("Error in getStudies", rX);
			throw new ConnectionException(rX);
		}
		catch (TranslationException tX)
		{
			throw new MethodException(tX);
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(loader);
		}
	}
	
	public Study getStudyFromCprsIdentifier(Site site, String patientIcn,
			CprsIdentifier cprsIdentifier)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		logger.info("getStudyFromCprsIdentifier, Transaction [" + transactionContext.getTransactionId() + "] initiated, patient Icn '" + patientIcn + "' to '" + site.getSiteNumber() + "'.");
		setDataSourceMethodAndVersion("getStudyFromCprsIdentifier");
		ImageFederationMetadata imageMetadata = getImageMetadataService(ProxyServiceType.metadata);
		
		// if the metadata connection parameters is not null and the metadata connection parameters
		// specifies a user ID then set the UID/PWD parameters as XML parameters, which should
		// end up as a BASIC auth parameter in the HTTP header
		setMetadataCredentials(imageMetadata, ProxyServiceType.metadata);
		gov.va.med.imaging.federation.webservices.types.v3.FederationStudyType studyType = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try
		{
			Thread.currentThread().setContextClassLoader(ImageXChangeHttpCommonsSender.class.getClassLoader());
			
			studyType = imageMetadata.getStudyFromCprsIdentifier(patientIcn, 
						transactionContext.getTransactionId(), site.getSiteNumber(), 
						cprsIdentifier.getCprsIdentifier());
			logger.info("getStudyFromCprsIdentifier, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (studyType == null ? "null" :  "not null") + "] study webservice object.");
			Study result = gov.va.med.imaging.federation.webservices.translation.v3.Translator.translate(studyType);
				// translator.transformStudy(studyType);
			logger.info("getStudyFromCprsIdentifier, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (result == null ? "null" : "not null") + "] study business object.");
			return result;
		}
		catch(FederationSecurityCredentialsExpiredExceptionFaultType fsveXft)
		{
			logger.error("Error in getStudyFromCprsIdentifier", fsveXft);
			throw new SecurityCredentialsExpiredException(fsveXft.getMessage1());
		}
		catch(FederationMethodExceptionFaultType fmXft)
		{
			logger.error("Error in getStudyFromCprsIdentifier", fmXft);
			throw new FederationMethodException(fmXft);
		}
		catch(RemoteException rX)
		{
			logger.error("Error in getStudyFromCprsIdentifier", rX);
			throw new ConnectionException(rX);
		}
		catch (TranslationException tX)
		{
			logger.error("Error in getStudyFromCprsIdentifier", tX);
			throw new FederationMethodException(tX);
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
		setDataSourceMethodAndVersion("logImageAccessEvent");
		ImageFederationMetadata imageMetadata = getImageMetadataService(ProxyServiceType.metadata);
		
		// if the metadata connection parameters is not null and the metadata connection parameters
		// specifies a user ID then set the UID/PWD parameters as XML parameters, which should
		// end up as a BASIC auth parameter in the HTTP header
		setMetadataCredentials(imageMetadata, ProxyServiceType.metadata);
		
		boolean result = false;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try
		{
			gov.va.med.imaging.federation.webservices.types.v3.FederationImageAccessLogEventType federationLogEvent =
				gov.va.med.imaging.federation.webservices.translation.v3.Translator.translate(logEvent);
				// translator.transformLogEvent(logEvent);
			Thread.currentThread().setContextClassLoader(ImageXChangeHttpCommonsSender.class.getClassLoader());
			result = imageMetadata.postImageAccessEvent(transactionContext.getTransactionId(), federationLogEvent);		
		}
		catch(FederationSecurityCredentialsExpiredExceptionFaultType fsveXft)
		{
			logger.error("Error in logImageAccessEvent", fsveXft);
			throw new SecurityCredentialsExpiredException(fsveXft.getMessage1());
		}
		catch(FederationMethodExceptionFaultType fmXft)
		{
			logger.error("Error in logImageAccessEvent", fmXft);
			throw new FederationMethodException(fmXft);
		}
		catch(RemoteException rX)
		{
			logger.error("Error in logImageAccessEvent", rX);
			throw new ConnectionException(rX);
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(loader);
		}
		
		logger.info("logImageAccessEvent, Transaction [" + transactionContext.getTransactionId() + "] logged image access event " + 
				(result == true ? "ok" : "failed"));
		return result;
	}
	
	public SortedSet<Patient> searchPatients(Site site, String searchCriteria)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		logger.info("searchPatients, Transaction [" + transactionContext.getTransactionId() + "] initiated, search Criteria '" + searchCriteria + "' to '" + site.getSiteNumber() + "'.");
		setDataSourceMethodAndVersion("searchPatients");
		ImageFederationMetadata imageMetadata = getImageMetadataService(ProxyServiceType.metadata);
		
		// if the metadata connection parameters is not null and the metadata connection parameters
		// specifies a user ID then set the UID/PWD parameters as XML parameters, which should
		// end up as a BASIC auth parameter in the HTTP header
		setMetadataCredentials(imageMetadata, ProxyServiceType.metadata);
		gov.va.med.imaging.federation.webservices.types.v3.PatientType[] patients = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try
		{
			Thread.currentThread().setContextClassLoader(ImageXChangeHttpCommonsSender.class.getClassLoader());
			patients = imageMetadata.searchPatients(searchCriteria, 
					transactionContext.getTransactionId(), site.getSiteNumber());			
			
			logger.info("searchPatients, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (patients == null ? "null" :  "not null") + "] patient webservice objects.");
			SortedSet<Patient> result = gov.va.med.imaging.federation.webservices.translation.v3.Translator.translate(patients);
				//translator.transformPatients(patients);
			logger.info("searchPatients, Transaction [" + transactionContext.getTransactionId() + "] returned response of [" + (result == null ? "null" :  result.size()) + "] patients business objects.");
			return result;
		}
		catch(FederationSecurityCredentialsExpiredExceptionFaultType fsveXft)
		{
			logger.error("Error in searchPatients", fsveXft);
			throw new SecurityCredentialsExpiredException(fsveXft.getMessage1());
		}
		catch(FederationMethodExceptionFaultType fmXft)
		{
			logger.error("Error in searchPatients", fmXft);
			throw new FederationMethodException(fmXft);
		}
		catch(RemoteException rX)
		{
			logger.error("Error in searchPatients", rX);
			throw new ConnectionException(rX);
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(loader);
		}
	}
	
	public PatientSensitiveValue getPatientSensitiveValue(Site site, String patientIcn)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		logger.info("getPatientSensitiveValue, Transaction [" + transactionContext.getTransactionId() + "] initiated, patient Icn '" + patientIcn + "' to '" + site.getSiteNumber() + "'.");
		setDataSourceMethodAndVersion("getPatientSensitiveValue");
		ImageFederationMetadata imageMetadata = getImageMetadataService(ProxyServiceType.metadata);
		
		// if the metadata connection parameters is not null and the metadata connection parameters
		// specifies a user ID then set the UID/PWD parameters as XML parameters, which should
		// end up as a BASIC auth parameter in the HTTP header
		setMetadataCredentials(imageMetadata, ProxyServiceType.metadata);
		gov.va.med.imaging.federation.webservices.types.v3.PatientSensitiveCheckResponseType patientSensitivity = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try
		{
			Thread.currentThread().setContextClassLoader(ImageXChangeHttpCommonsSender.class.getClassLoader());
			patientSensitivity = imageMetadata.getPatientSensitivityLevel(transactionContext.getTransactionId(), 
						site.getSiteNumber(), patientIcn);			

			logger.info("getPatientSensitiveValue, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (patientSensitivity == null ? "null" :  "not null") + "] patient sensitivity webservice object.");
			PatientSensitiveValue result = gov.va.med.imaging.federation.webservices.translation.v3.Translator.translate(PatientSensitiveValue.class, patientSensitivity);
				//translator.transformPatientSensitiveValue(patientSensitivity);
			logger.info("getPatientSensitiveValue, Transaction [" + transactionContext.getTransactionId() + "] returned sensitive code of [" + (result == null ? "null" : result.getSensitiveLevel().getCode()) + "] business object.");
			return result;
		}
		catch(FederationSecurityCredentialsExpiredExceptionFaultType fsveXft)
		{
			logger.error("Error in getPatientSensitiveValue", fsveXft);
			throw new SecurityCredentialsExpiredException(fsveXft.getMessage1());
		}
		catch(FederationMethodExceptionFaultType fmXft)
		{
			logger.error("Error in getPatientSensitiveValue", fmXft);
			throw new FederationMethodException(fmXft);
		}
		catch(RemoteException rX)
		{
			logger.error("Error in getPatientSensitiveValue", rX);
			throw new ConnectionException(rX);
		}
		catch (TranslationException tX)
		{
			logger.error("Error in getPatientSensitiveValue", tX);
			throw new FederationMethodException(tX);
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(loader);
		}
	}
	
	public List<String> getPatientSitesVisited(Site site, String patientIcn)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		logger.info("getPatientSitesVisited, Transaction [" + transactionContext.getTransactionId() + "] initiated, patient Icn '" + patientIcn + "' to '" + site.getSiteNumber() + "'.");
		setDataSourceMethodAndVersion("getPatientSitesVisited");
		ImageFederationMetadata imageMetadata = getImageMetadataService(ProxyServiceType.metadata);
		
		// if the metadata connection parameters is not null and the metadata connection parameters
		// specifies a user ID then set the UID/PWD parameters as XML parameters, which should
		// end up as a BASIC auth parameter in the HTTP header
		setMetadataCredentials(imageMetadata, ProxyServiceType.metadata);
		String [] sites = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try
		{
			Thread.currentThread().setContextClassLoader(ImageXChangeHttpCommonsSender.class.getClassLoader());
			sites = imageMetadata.getPatientSitesVisited(patientIcn, transactionContext.getTransactionId(), site.getSiteNumber());			
			
			logger.info("getPatientSitesVisited, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (sites == null ? "null" :  "not null") + "] site webservice objects.");
			List<String> result = gov.va.med.imaging.federation.webservices.translation.v3.Translator.translateSites(sites); 
				//translator.transformSiteNumbers(sites);
			logger.info("getPatientSitesVisited, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (result == null ? "null" :  result.size()) + "] site business objects.");
			return result;
		}
		catch(FederationSecurityCredentialsExpiredExceptionFaultType fsveXft)
		{
			logger.error("Error in getPatientSitesVisited", fsveXft);
			throw new SecurityCredentialsExpiredException(fsveXft.getMessage1());
		}
		catch(FederationMethodExceptionFaultType fmXft)
		{
			logger.error("Error in getPatientSitesVisited", fmXft);
			throw new FederationMethodException(fmXft);
		}
		catch(RemoteException rX)
		{
			logger.error("Error in getPatientSitesVisited", rX);
			throw new ConnectionException(rX);
		}
		catch (TranslationException tX)
		{
			logger.error("Error in getPatientSitesVisited", tX);
			throw new FederationMethodException(tX);
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
		setDataSourceMethodAndVersion("getImageInformation");
		ImageFederationMetadata imageMetadata = getImageMetadataService(ProxyServiceType.metadata);
		
		// if the metadata connection parameters is not null and the metadata connection parameters
		// specifies a user ID then set the UID/PWD parameters as XML parameters, which should
		// end up as a BASIC auth parameter in the HTTP header
		setMetadataCredentials(imageMetadata, ProxyServiceType.metadata);
		String result = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try
		{
			Thread.currentThread().setContextClassLoader(ImageXChangeHttpCommonsSender.class.getClassLoader());
			result = imageMetadata.getImageInformation(imagingUrn.toString(SERIALIZATION_FORMAT.PATCH83_VFTP), 
					transactionContext.getTransactionId());
		}
		catch(FederationSecurityCredentialsExpiredExceptionFaultType fsveXft)
		{
			logger.error("Error in getImageInformation", fsveXft);
			throw new SecurityCredentialsExpiredException(fsveXft.getMessage1());
		}
		catch(FederationMethodExceptionFaultType fmXft)
		{
			logger.error("Error in getImageInformation", fmXft);
			throw new FederationMethodException(fmXft);
		}
		catch(RemoteException rX)
		{
			logger.error("Error in getImageInformation", rX);
			throw new ConnectionException(rX);
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
		setDataSourceMethodAndVersion("getImageSystemGlobalNode");
		ImageFederationMetadata imageMetadata = getImageMetadataService(ProxyServiceType.metadata);
		
		// if the metadata connection parameters is not null and the metadata connection parameters
		// specifies a user ID then set the UID/PWD parameters as XML parameters, which should
		// end up as a BASIC auth parameter in the HTTP header
		setMetadataCredentials(imageMetadata, ProxyServiceType.metadata);
		String result = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try
		{
			Thread.currentThread().setContextClassLoader(ImageXChangeHttpCommonsSender.class.getClassLoader());
			result = imageMetadata.getImageSystemGlobalNode(imagingUrn.toString(SERIALIZATION_FORMAT.PATCH83_VFTP), 
					transactionContext.getTransactionId());
		}
		catch(FederationSecurityCredentialsExpiredExceptionFaultType fsveXft)
		{
			logger.error("Error in getImageSystemGlobalNode", fsveXft);
			throw new SecurityCredentialsExpiredException(fsveXft.getMessage1());
		}
		catch(FederationMethodExceptionFaultType fmXft)
		{
			logger.error("Error in getImageSystemGlobalNode", fmXft);
			throw new FederationMethodException(fmXft);
		}
		catch(RemoteException rX)
		{
			logger.error("Error in getImageSystemGlobalNode", rX);
			throw new ConnectionException(rX);
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
		setDataSourceMethodAndVersion("getImageDevFields");
		ImageFederationMetadata imageMetadata = getImageMetadataService(ProxyServiceType.metadata);
		
		// if the metadata connection parameters is not null and the metadata connection parameters
		// specifies a user ID then set the UID/PWD parameters as XML parameters, which should
		// end up as a BASIC auth parameter in the HTTP header
		setMetadataCredentials(imageMetadata, ProxyServiceType.metadata);
		String result = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try
		{
			Thread.currentThread().setContextClassLoader(ImageXChangeHttpCommonsSender.class.getClassLoader());
			result = imageMetadata.getImageDevFields(imagingUrn.toString(SERIALIZATION_FORMAT.PATCH83_VFTP), 
					flags, transactionContext.getTransactionId());
		}
		catch(FederationSecurityCredentialsExpiredExceptionFaultType fsveXft)
		{
			logger.error("Error in getImageDevFields", fsveXft);
			throw new SecurityCredentialsExpiredException(fsveXft.getMessage1());
		}
		catch(FederationMethodExceptionFaultType fmXft)
		{
			logger.error("Error in getImageDevFields", fmXft);
			throw new FederationMethodException(fmXft);
		}
		catch(RemoteException rX)
		{
			logger.error("Error in getImageDevFields", rX);
			throw new ConnectionException(rX);
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(loader);	
		}		
		logger.info("getImageDevFields, Transaction [" + transactionContext.getTransactionId() + "] returned response of length [" + (result == null ? "null" : result.length()) + "] bytes.");
		return result;
	}
	
	public String executePassthroughMethod(Site site, PassthroughInputMethod method)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		logger.info("executePassthroughMethod, Transaction [" + transactionContext.getTransactionId() + "] initiated, method name '" + method.getMethodName() + "' to '" + site.getSiteNumber() + "'.");
		setDataSourceMethodAndVersion("executePassthroughMethod");
		ImageFederationMetadata imageMetadata = getImageMetadataService(ProxyServiceType.metadata);
		
		// if the metadata connection parameters is not null and the metadata connection parameters
		// specifies a user ID then set the UID/PWD parameters as XML parameters, which should
		// end up as a BASIC auth parameter in the HTTP header
		setMetadataCredentials(imageMetadata, ProxyServiceType.metadata);
		String result = null;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try
		{
			Thread.currentThread().setContextClassLoader(ImageXChangeHttpCommonsSender.class.getClassLoader());			

			gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterType[] parameters = 
				gov.va.med.imaging.federation.webservices.translation.v3.Translator.translate(method.getParameters());
				// translator.transformPassthroughMethodParameters(method.getParameters());
			
			result = imageMetadata.remoteMethodPassthrough(transactionContext.getTransactionId(), 
				site.getSiteNumber(), method.getMethodName(), parameters, 
				transactionContext.getImagingSecurityContextType());
		}
		catch(FederationSecurityCredentialsExpiredExceptionFaultType fsveXft)
		{
			logger.error("Error in executePassthroughMethod", fsveXft);
			throw new SecurityCredentialsExpiredException(fsveXft.getMessage1());
		}
		catch(FederationMethodExceptionFaultType fmXft)
		{
			logger.error("Error in executePassthroughMethod", fmXft);
			throw new FederationMethodException(fmXft);
		}
		catch(RemoteException rX)
		{
			logger.error("Error in executePassthroughMethod", rX);
			throw new ConnectionException(rX);
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(loader);
		}
		logger.info("executePassthroughMethod, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (result == null ? "null" : result.length()) + "] bytes from webservice.");
		return result;
	}
	
	public boolean postExamAccess(Site site, String inputParameter)
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		logger.info("postExamAccess, Transaction [" + transactionContext.getTransactionId() + "] initiated to '" + site.getSiteNumber() + "'.");
		setDataSourceMethodAndVersion("postExamAccess");
		ImageFederationMetadata imageMetadata = getImageMetadataService(ProxyServiceType.vistaRadMetadata);
		
		// if the metadata connection parameters is not null and the metadata connection parameters
		// specifies a user ID then set the UID/PWD parameters as XML parameters, which should
		// end up as a BASIC auth parameter in the HTTP header
		setMetadataCredentials(imageMetadata, ProxyServiceType.vistaRadMetadata);
		boolean result = false;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try
		{
			// the 3rd piece of the string is the image URN, it must be base32 encoded to pass to a P83 VIX
			String encodedInputParameter = Translator.translateEncodeExamImageAccessInputParameter(inputParameter);
			if(encodedInputParameter == null)
				return false;
			logger.info("Converted input parameter from '" + inputParameter + "' into '" + encodedInputParameter + "'.");
			
			Thread.currentThread().setContextClassLoader(ImageXChangeHttpCommonsSender.class.getClassLoader());			
			result = imageMetadata.postVistaRadExamAccessEvent(transactionContext.getTransactionId(), 
				site.getSiteNumber(), encodedInputParameter);
		}
		catch(FederationSecurityCredentialsExpiredExceptionFaultType fsveXft)
		{
			logger.error("Error in postExamAccess", fsveXft);
			throw new SecurityCredentialsExpiredException(fsveXft.getMessage1());
		}
		catch(FederationMethodExceptionFaultType fmXft)
		{
			logger.error("Error in postExamAccess", fmXft);
			throw new FederationMethodException(fmXft);
		}
		catch(RemoteException rX)
		{
			logger.error("Error in postExamAccess", rX);
			throw new ConnectionException(rX);
		}
		finally
		{
			Thread.currentThread().setContextClassLoader(loader);
		}
		logger.info("executePassthroughMethod, Transaction [" + transactionContext.getTransactionId() + "] returned [" + (result) + "] from webservice.");
		return result;
	}
	
	private Requestor getRequestor()
	{
		TransactionContext transactionContext = TransactionContextFactory.get();

		if(transactionContext != null)
			return new Requestor(transactionContext.getFullName(), transactionContext.getSsn(), 
					transactionContext.getSiteNumber(), transactionContext.getSiteName());
		return null;
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
		return "3";
	}
	
	
}
