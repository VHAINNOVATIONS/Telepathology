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
public class FederationDocumentType
{
	private String identifier;
	private String documentSetIen;
	private String name;
	private String description;
	private Date creationDate;
	private FederationMediaType mediaType;
	private String clinicalType;
	private int vistaImageType;
	private Long contentLenth;
	private String languageCode;
	private FederationChecksumType checksum;
	private int confidentialityCode;
	private String consolidatedSiteNumber;

	public FederationDocumentType()
	{
		super();
	}

	public String getIdentifier()
	{
		return identifier;
	}

	public void setIdentifier(String identifier)
	{
		this.identifier = identifier;
	}

	public String getDocumentSetIen()
	{
		return documentSetIen;
	}

	public void setDocumentSetIen(String documentSetIen)
	{
		this.documentSetIen = documentSetIen;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Date getCreationDate()
	{
		return creationDate;
	}

	public void setCreationDate(Date creationDate)
	{
		this.creationDate = creationDate;
	}

	public FederationMediaType getMediaType()
	{
		return mediaType;
	}

	public void setMediaType(FederationMediaType mediaType)
	{
		this.mediaType = mediaType;
	}

	public String getClinicalType()
	{
		return clinicalType;
	}

	public void setClinicalType(String clinicalType)
	{
		this.clinicalType = clinicalType;
	}

	public int getVistaImageType()
	{
		return vistaImageType;
	}

	public void setVistaImageType(int vistaImageType)
	{
		this.vistaImageType = vistaImageType;
	}

	public Long getContentLenth()
	{
		return contentLenth;
	}

	public void setContentLenth(Long contentLenth)
	{
		this.contentLenth = contentLenth;
	}

	public String getLanguageCode()
	{
		return languageCode;
	}

	public void setLanguageCode(String languageCode)
	{
		this.languageCode = languageCode;
	}

	public FederationChecksumType getChecksum()
	{
		return checksum;
	}

	public void setChecksum(FederationChecksumType checksum)
	{
		this.checksum = checksum;
	}

	public int getConfidentialityCode()
	{
		return confidentialityCode;
	}

	public void setConfidentialityCode(int confidentialityCode)
	{
		this.confidentialityCode = confidentialityCode;
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
