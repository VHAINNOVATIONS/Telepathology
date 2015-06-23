/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 20, 2012
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
package gov.va.med.imaging.federationdatasource.pathology.v6;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityException;
import gov.va.med.imaging.datasource.exceptions.UnsupportedProtocolException;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.federation.proxy.FederationProxyUtilities;
import gov.va.med.imaging.federationdatasource.AbstractFederationDataSourceService;
import gov.va.med.imaging.federationdatasource.FederationPatientArtifactDataSourceServiceV4;
import gov.va.med.imaging.federationdatasource.pathology.PathologyFederationProxyServiceType;
import gov.va.med.imaging.federationdatasource.pathology.proxy.v6.FederationRestPathologyProxyV6;
import gov.va.med.imaging.federationdatasource.pathology.proxy.v6.FederationRestPathologyProxyV6Impl;
import gov.va.med.imaging.pathology.AbstractPathologySite;
import gov.va.med.imaging.pathology.PathologyAcquisitionSite;
import gov.va.med.imaging.pathology.PathologyCase;
import gov.va.med.imaging.pathology.PathologyCaseConsultationURN;
import gov.va.med.imaging.pathology.PathologyCaseReportField;
import gov.va.med.imaging.pathology.PathologyCaseSlide;
import gov.va.med.imaging.pathology.PathologyCaseSpecimen;
import gov.va.med.imaging.pathology.PathologyCaseSupplementalReport;
import gov.va.med.imaging.pathology.PathologyCaseTemplate;
import gov.va.med.imaging.pathology.PathologyCaseURN;
import gov.va.med.imaging.pathology.PathologyCaseUpdateAttributeResult;
import gov.va.med.imaging.pathology.PathologyCptCode;
import gov.va.med.imaging.pathology.PathologyCptCodeResult;
import gov.va.med.imaging.pathology.PathologyFieldURN;
import gov.va.med.imaging.pathology.PathologyFieldValue;
import gov.va.med.imaging.pathology.PathologyReadingSite;
import gov.va.med.imaging.pathology.PathologySaveCaseReportResult;
import gov.va.med.imaging.pathology.PathologySite;
import gov.va.med.imaging.pathology.PathologySnomedCode;
import gov.va.med.imaging.pathology.datasource.PathologyDataSourceSpi;
import gov.va.med.imaging.pathology.enums.PathologyCaseAssistance;
import gov.va.med.imaging.pathology.enums.PathologyCaseConsultationUpdateStatus;
import gov.va.med.imaging.pathology.enums.PathologyCaseReserveResult;
import gov.va.med.imaging.pathology.enums.PathologyElectronicSignatureNeed;
import gov.va.med.imaging.pathology.enums.PathologyField;
import gov.va.med.imaging.proxy.services.ProxyServiceType;
import gov.va.med.imaging.proxy.services.ProxyServices;
import gov.va.med.imaging.url.vftp.VftpConnection;

/**
 * @author VHAISWWERFEJ
 *
 */
public class FederationPathologyDataSourceServiceV6
extends AbstractFederationDataSourceService
implements PathologyDataSourceSpi
{
	
	@SuppressWarnings("unused")
	private final VftpConnection federationConnection;
	private ProxyServices federationProxyServices = null;
	
	private final static String DATASOURCE_VERSION = "6";
	private FederationRestPathologyProxyV6Impl proxy = null;
	public final static String SUPPORTED_PROTOCOL = "vftp";
	private final static String FEDERATION_PROXY_SERVICE_NAME = "Federation";
	
	private final static Logger logger = Logger.getLogger(FederationPatientArtifactDataSourceServiceV4.class);

	public FederationPathologyDataSourceServiceV6(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	throws UnsupportedOperationException
	{
		super(resolvedArtifactSource, protocol);
		federationConnection = new VftpConnection(getMetadataUrl());

		if(! (resolvedArtifactSource instanceof ResolvedSite) )
			throw new UnsupportedOperationException("The artifact source must be an instance of ResolvedSite and it is a '" + resolvedArtifactSource.getClass().getSimpleName() + "'.");
	}
	
	public static FederationPathologyDataSourceServiceV6 create(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	throws ConnectionException, UnsupportedProtocolException
	{
		return new FederationPathologyDataSourceServiceV6(resolvedArtifactSource, protocol);
	}
	
	protected Logger getLogger()
	{
		return logger;
	}
	
	/**
	 * The artifact source must be checked in the constructor to assure that it is an instance
	 * of ResolvedSite.
	 * 
	 * @return
	 */
	protected ResolvedSite getResolvedSite()
	{
		return (ResolvedSite)getResolvedArtifactSource();
	}
	
	protected Site getSite()
	{
		return getResolvedSite().getSite();
	}
	
	protected ProxyServices getFederationProxyServices()
	{
		if(federationProxyServices == null)
		{
			federationProxyServices = 
				FederationProxyUtilities.getFederationProxyServices(getSite(), 
						getFederationProxyName(), getDataSourceVersion());
		}
		return federationProxyServices;
	}
	
	protected String getFederationProxyName()
	{
		return FEDERATION_PROXY_SERVICE_NAME;
	}
	
	public String getDataSourceVersion() 
	{
		return DATASOURCE_VERSION;
	}
	
	private FederationRestPathologyProxyV6 getProxy()
	throws ConnectionException
	{
		if(proxy == null)
		{
			ProxyServices proxyServices = getFederationProxyServices();
			if(proxyServices == null)
				throw new ConnectionException("Did not receive any applicable services from IDS service for site [" + getSite().getSiteNumber() + "]");
			proxy = new FederationRestPathologyProxyV6Impl(proxyServices,
					getFederationConfiguration());
		}
		return proxy;
	}
	
	@Override
	public boolean isVersionCompatible() 
	throws SecurityException
	{
		if(getFederationProxyServices() == null)
			return false;		
		try
		{
			getLogger().debug("Found FederationProxyServices, looking for '" + getProxyServiceType() + "' service type at site [" + getSite().getSiteNumber() + "].");
			getFederationProxyServices().getProxyService(getProxyServiceType());
			getLogger().debug("Found service type '" + getProxyServiceType() + "' at site [" + getSite().getSiteNumber() + "], returning true for version compatible.");
			return true;
		}
		catch(gov.va.med.imaging.proxy.exceptions.ProxyServiceNotFoundException psnfX)
		{
			getLogger().warn("Cannot find proxy service type '" + getProxyServiceType().toString() + "' at site [" + getSite().getSiteNumber() + "]");
			return false;
		}
	}
		
	private ProxyServiceType getProxyServiceType()
	{
		return new PathologyFederationProxyServiceType();
		//return ProxyServiceType.pathology;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.pathology.datasource.PathologyDataSourceSpi#getCases(gov.va.med.RoutingToken, boolean, int)
	 */
	@Override
	public List<PathologyCase> getCases(RoutingToken globalRoutingToken,
			boolean released, int days, String requestingSiteId) 
	throws MethodException, ConnectionException
	{
		if(requestingSiteId == null || requestingSiteId.length() <= 0)
			return getProxy().getCases(globalRoutingToken, released, days);
		else
			return getProxy().getCases(globalRoutingToken, released, days, requestingSiteId);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.pathology.datasource.PathologyDataSourceSpi#getCaseSpecimens(gov.va.med.imaging.pathology.PathologyCaseURN)
	 */
	@Override
	public List<PathologyCaseSpecimen> getCaseSpecimens(
			PathologyCaseURN pathologyCaseUrn) 
	throws MethodException, ConnectionException
	{
		return getProxy().getCaseSpecimens(pathologyCaseUrn);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.pathology.datasource.PathologyDataSourceSpi#getPatientCases(gov.va.med.RoutingToken, java.lang.String)
	 */
	@Override
	public List<PathologyCase> getPatientCases(RoutingToken globalRoutingToken,
			PatientIdentifier patientIdentifier, String requestingSiteId) 
	throws MethodException, ConnectionException
	{		
		if(patientIdentifier.getPatientIdentifierType().isLocal())
			throw new MethodException("Cannot use local patient identifier to retrieve remote patient information");
		if(requestingSiteId == null)
			return getProxy().getPatientCases(globalRoutingToken, patientIdentifier);
		else
			return getProxy().getPatientCases(globalRoutingToken, patientIdentifier, requestingSiteId);
		//return getProxy().getPatientCases(globalRoutingToken, patientIcn);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.pathology.datasource.PathologyDataSourceSpi#getSites(gov.va.med.RoutingToken, boolean)
	 */
	@Override
	public List<AbstractPathologySite> getSites(
			RoutingToken globalRoutingToken, boolean reading)
	throws MethodException, ConnectionException
	{
		List<AbstractPathologySite> result = new ArrayList<AbstractPathologySite>();
		if(reading)
		{
			List<PathologyReadingSite> readingSites = 
				getProxy().getReadingSites(globalRoutingToken);
			result.addAll(readingSites);
		}
		else
		{
			List<PathologyAcquisitionSite> acquisitionSites = 
				getProxy().getAcquisitionSites(globalRoutingToken);
			result.addAll(acquisitionSites);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.pathology.datasource.PathologyDataSourceSpi#lockCase(gov.va.med.imaging.pathology.PathologyCaseURN, boolean)
	 */
	@Override
	public PathologyCaseUpdateAttributeResult lockCase(
			PathologyCaseURN pathologyCaseUrn, boolean lock)
	throws MethodException, ConnectionException
	{
		return getProxy().lockCase(pathologyCaseUrn, lock);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.pathology.datasource.PathologyDataSourceSpi#addCaseAssistance(gov.va.med.imaging.pathology.PathologyCaseURN, gov.va.med.imaging.pathology.enums.PathologyCaseAssistance, java.lang.String)
	 */
	@Override
	public PathologyCaseUpdateAttributeResult addCaseAssistance(
			PathologyCaseURN pathologyCaseUrn,
			PathologyCaseAssistance assistanceType, String stationNumber)
	throws MethodException, ConnectionException
	{
		return getProxy().addCaseAssistance(pathologyCaseUrn, assistanceType, stationNumber);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.pathology.datasource.PathologyDataSourceSpi#getSiteTemplate(java.util.List)
	 */
	@Override
	public List<String> getSiteTemplate(RoutingToken globalRoutingToken, List<String> apSections)
	throws MethodException, ConnectionException
	{
		return getProxy().getSiteTemplate(globalRoutingToken, apSections);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.pathology.datasource.PathologyDataSourceSpi#saveSiteTemplate(java.lang.String, java.lang.String)
	 */
	@Override
	public void saveSiteTemplate(RoutingToken globalRoutingToken, String xmlTemplate, String apSection)
	throws MethodException, ConnectionException
	{
		getProxy().saveSiteTemplate(globalRoutingToken, xmlTemplate, apSection);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.pathology.datasource.PathologyDataSourceSpi#updateReadingSiteList(java.util.List)
	 */
	@Override
	public void updateReadingSite(RoutingToken globalRoutingToken, PathologyReadingSite readingSite, boolean delete)
	throws MethodException, ConnectionException
	{
		getProxy().updateReadingSite(globalRoutingToken, readingSite, delete);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.pathology.datasource.PathologyDataSourceSpi#updateAcquisitionSiteList(java.util.List)
	 */
	@Override
	public void updateAcquisitionSite(RoutingToken globalRoutingToken, 
			PathologyAcquisitionSite acquisitionSite, boolean delete)
	throws MethodException, ConnectionException
	{
		getProxy().updateAcquisitionSite(globalRoutingToken, acquisitionSite, delete);
	}

	@Override
	public String getPathologyCaseReport(PathologyCaseURN pathologyCaseUrn)
	throws MethodException, ConnectionException
	{
		return getProxy().getPathologyCaseReport(pathologyCaseUrn);
	}

	@Override
	public List<PathologyCaseSupplementalReport> getCaseSupplementalReports(
			PathologyCaseURN pathologyCaseUrn) 
	throws MethodException, ConnectionException
	{
		return getProxy().getCaseSupplementalReports(pathologyCaseUrn);
	}

	@Override
	public PathologyCaseTemplate getCaseTemplateData(
			PathologyCaseURN pathologyCaseUrn, List<String> fields)
	throws MethodException, ConnectionException
	{
		return getProxy().getCaseTemplateData(pathologyCaseUrn, fields);
	}

	@Override
	public PathologyCaseReserveResult reserveCase(
			PathologyCaseURN pathologyCaseUrn, boolean lock)
	throws MethodException, ConnectionException
	{
		return getProxy().reserveCase(pathologyCaseUrn, lock);
	}

	@Override
	public PathologyElectronicSignatureNeed checkElectronicSignatureNeeded(
			RoutingToken routingToken, String apSection)
	throws MethodException, ConnectionException
	{
		return getProxy().checkElectronicSignatureNeeded(routingToken, apSection);
	}

	@Override
	public List<PathologyFieldValue> getPathologyFields(
			RoutingToken globalRoutingToken, PathologyField pathologyField,
			String searchParameter) 
	throws MethodException, ConnectionException
	{
		return getProxy().getPathologyFields(globalRoutingToken, pathologyField, searchParameter);
	}

	@Override
	public void updateConsultationStatus(
			PathologyCaseConsultationURN pathologyCaseConsultationUrn,
			PathologyCaseConsultationUpdateStatus consultationUpdateStatus)
	throws MethodException, ConnectionException
	{
		getProxy().updateConsultationStatus(pathologyCaseConsultationUrn, consultationUpdateStatus);		
	}

	@Override
	public PathologySaveCaseReportResult saveCaseReportFields(PathologyCaseURN pathologyCaseUrn,
			List<PathologyCaseReportField> fields) 
	throws MethodException, ConnectionException
	{
		return getProxy().saveCaseReportFields(pathologyCaseUrn, fields);		
	}

	@Override
	public void saveCaseSupplementalReport(PathologyCaseURN pathologyCaseUrn,
			String reportContents, Date date, boolean verified)
	throws MethodException, ConnectionException
	{
		getProxy().saveCaseSupplementalReport(pathologyCaseUrn, reportContents, date, verified);		
	}

	@Override
	public List<PathologySite> getSites(RoutingToken globalRoutingToken)
	throws MethodException, ConnectionException
	{
		return getProxy().getSites(globalRoutingToken);
	}

	@Override
	public Integer getLockExpiresMinutes(RoutingToken globalRoutingToken)
	throws MethodException, ConnectionException
	{
		return getProxy().getLockExpiresMinutes(globalRoutingToken);
	}

	@Override
	public void setLockExpiresMinutes(RoutingToken globalRoutingToken, int minutes)
	throws MethodException, ConnectionException
	{
		getProxy().setLockExpiresMinutes(globalRoutingToken, minutes);		
	}

	@Override
	public String getPreferences(RoutingToken globalRoutingToken,
			String userId, String label) 
	throws MethodException, ConnectionException
	{
		if(userId == null || userId.length() <= 0)
			return getProxy().getPreferences(globalRoutingToken, label);
		else
			return getProxy().getUserPreferences(globalRoutingToken, userId, label);
	}

	@Override
	public void savePreferences(RoutingToken globalRoutingToken, String userId,
			String label, String xml) 
	throws MethodException,
			ConnectionException
	{
		if(userId == null || userId.length() <= 0)
			getProxy().savePreferences(globalRoutingToken, label, xml);
		else
			getProxy().saveUserPreferences(globalRoutingToken, userId, label, xml);
	}

	@Override
	public List<PathologySnomedCode> getCaseSnomedCodes(
			PathologyCaseURN pathologyCaseUrn) 
	throws MethodException, ConnectionException
	{
		return getProxy().getCaseSnomedCodes(pathologyCaseUrn);
	}

	@Override
	public String saveCaseSnomedCode(PathologyCaseURN pathologyCaseUrn,
			String tissueId, PathologyFieldURN pathologyFieldUrn)
	throws MethodException, ConnectionException
	{
		return getProxy().saveCaseSnomedCode(pathologyCaseUrn, tissueId, pathologyFieldUrn);
	}

	@Override
	public String saveCaseEtiologySnomedCodeForMorphology(
			PathologyCaseURN pathologyCaseUrn, String tissueId,
			String morphologyId, PathologyFieldURN etiologyFieldUrn)
	throws MethodException, ConnectionException
	{
		return getProxy().saveCaseSnomedCodeForMorphology(pathologyCaseUrn, tissueId, morphologyId, etiologyFieldUrn);
	}

	@Override
	public List<PathologyCptCodeResult> saveCaseCptCodes(PathologyCaseURN pathologyCaseUrn,
			PathologyFieldURN locationFieldUrn, List<String> cptCodes)
	throws MethodException, ConnectionException
	{
		return getProxy().saveCaseCptCodes(pathologyCaseUrn, locationFieldUrn, cptCodes);
	}

	@Override
	public List<PathologyCptCode> getCaseCptCodes(
			PathologyCaseURN pathologyCaseUrn) 
	throws MethodException, ConnectionException
	{
		return getProxy().getCaseCptCodes(pathologyCaseUrn);
	}

	@Override
	public List<String> getPathologyUserKeys(RoutingToken globalRoutingToken)
	throws MethodException, ConnectionException
	{
		return getProxy().getPathologyUserKeys(globalRoutingToken);
	}

	@Override
	public List<PathologyCase> getSpecificCases(List<PathologyCaseURN> cases)
	throws MethodException, ConnectionException
	{
		return getProxy().getSpecificCases(cases);
	}

	@Override
	public Boolean checkPendingConsultationStatus(
			RoutingToken globalRoutingToken, String stationNumber)
	throws MethodException, ConnectionException
	{
		return getProxy().checkPendingConsultationStatus(globalRoutingToken, stationNumber);
	}

	@Override
	public String saveCaseTissue(PathologyCaseURN pathologyCaseUrn,
			PathologyFieldURN tissueFieldUrn) 
	throws MethodException, ConnectionException
	{
		return getProxy().saveCaseTissues(pathologyCaseUrn, tissueFieldUrn);
	}

	@Override
	public PathologyCaseURN copyCase(RoutingToken globalRoutingToken, PathologyCaseURN pathologyCaseUrn) 
	throws MethodException, ConnectionException
	{
		return getProxy().copyCase(globalRoutingToken, pathologyCaseUrn);
	}

	@Override
	public void deleteSnomedCode(PathologyCaseURN pathologyCaseUrn,
			String tissueId, String snomedId, PathologyField snomedField,
			String etiologyId) 
	throws MethodException, ConnectionException
	{
		if(snomedId == null)
		{			
			// delete the tissue
			getProxy().deleteTissue(pathologyCaseUrn, tissueId);
		}
		else if(etiologyId == null)
		{
			// deleting a snomed code
			if(snomedField == null)
				throw new MethodException("SnomedField must be specified when deleting a snomed code");
			// delete snomed code
			getProxy().deleteSnomedCode(pathologyCaseUrn, tissueId, snomedId, snomedField);
		}
		else
		{
			// delete etiology
			getProxy().deleteSnomedEtiologyCode(pathologyCaseUrn, tissueId, snomedId, etiologyId);
		}		
	}

	@Override
	public void saveCaseNote(PathologyCaseURN pathologyCaseUrn, String note)
	throws MethodException, ConnectionException
	{
		getProxy().saveCaseNote(pathologyCaseUrn, note);
	}

	@Override
	public String getCaseNote(PathologyCaseURN pathologyCaseUrn)
	throws MethodException, ConnectionException
	{
		return getProxy().getCaseNote(pathologyCaseUrn);
	}

	@Override
	public List<PathologyCaseSlide> getCaseSlideInformation(
			PathologyCaseURN pathologyCaseUrn) 
	throws MethodException, ConnectionException 
	{
		return getProxy().getCaseSlides(pathologyCaseUrn);
	}


}
