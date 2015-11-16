/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 25, 2010
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
package gov.va.med.imaging.federation.rest.types;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author vhaiswwerfej
 *
 */
@XmlRootElement
public class FederationDocumentSetType
{
	private String homeCommunityId;
	private String repositoryId;
	private String identifier;
	private Date acquisitionDate;
	private String patientIcn;
	private String patientName;
	private String firstImageIen;
	private String siteName;
	private String siteAbbr;
	private String rpcResponseMsg;
	private Date procedureDate;
	private String errorMessage;
	private String alienSiteNumber;
	private String clinicalType;
	private String consolidatedSiteNumber;
	private FederationDocumentType [] documents;
	
	public FederationDocumentSetType()
	{
		super();
	}

	public String getHomeCommunityId()
	{
		return homeCommunityId;
	}

	public void setHomeCommunityId(String homeCommunityId)
	{
		this.homeCommunityId = homeCommunityId;
	}

	public String getRepositoryId()
	{
		return repositoryId;
	}

	public void setRepositoryId(String repositoryId)
	{
		this.repositoryId = repositoryId;
	}

	public String getIdentifier()
	{
		return identifier;
	}

	public void setIdentifier(String identifier)
	{
		this.identifier = identifier;
	}

	public Date getAcquisitionDate()
	{
		return acquisitionDate;
	}

	public void setAcquisitionDate(Date acquisitionDate)
	{
		this.acquisitionDate = acquisitionDate;
	}

	public String getPatientIcn()
	{
		return patientIcn;
	}

	public void setPatientIcn(String patientIcn)
	{
		this.patientIcn = patientIcn;
	}

	public String getPatientName()
	{
		return patientName;
	}

	public void setPatientName(String patientName)
	{
		this.patientName = patientName;
	}

	public String getFirstImageIen()
	{
		return firstImageIen;
	}

	public void setFirstImageIen(String firstImageIen)
	{
		this.firstImageIen = firstImageIen;
	}

	public String getSiteName()
	{
		return siteName;
	}

	public void setSiteName(String siteName)
	{
		this.siteName = siteName;
	}

	public String getSiteAbbr()
	{
		return siteAbbr;
	}

	public void setSiteAbbr(String siteAbbr)
	{
		this.siteAbbr = siteAbbr;
	}

	public String getRpcResponseMsg()
	{
		return rpcResponseMsg;
	}

	public void setRpcResponseMsg(String rpcResponseMsg)
	{
		this.rpcResponseMsg = rpcResponseMsg;
	}

	public Date getProcedureDate()
	{
		return procedureDate;
	}

	public void setProcedureDate(Date procedureDate)
	{
		this.procedureDate = procedureDate;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	public String getAlienSiteNumber()
	{
		return alienSiteNumber;
	}

	public void setAlienSiteNumber(String alienSiteNumber)
	{
		this.alienSiteNumber = alienSiteNumber;
	}

	public String getClinicalType()
	{
		return clinicalType;
	}

	public void setClinicalType(String clinicalType)
	{
		this.clinicalType = clinicalType;
	}

	public FederationDocumentType[] getDocuments()
	{
		return documents;
	}

	public void setDocuments(FederationDocumentType[] documents)
	{
		this.documents = documents;
	}

	public String getConsolidatedSiteNumber()
	{
		return consolidatedSiteNumber;
	}

	public void setConsolidatedSiteNumber(String consolidatedSiteNumber)
	{
		this.consolidatedSiteNumber = consolidatedSiteNumber;
	}
}
