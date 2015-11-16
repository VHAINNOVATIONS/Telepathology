/**
 * 
 */
package gov.va.med.imaging.exchange.business.documents;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.MediaType;
import gov.va.med.WellKnownOID;
import gov.va.med.imaging.DocumentURN;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.channels.ChecksumValue;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.AttachedContent;
import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.exchange.enums.VistaImageType;

import java.io.Serializable;
import java.nio.channels.ByteChannel;
import java.text.DateFormat;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 * @author vhaiswbeckec
 *
 */
public class Document
extends AttachedContent
implements Serializable, Comparable<Document>
{
	private static final long serialVersionUID = -4029416178345334605L;
	private static Logger logger = Logger.getLogger(Document.class);

	private final GlobalArtifactIdentifier identifier;	// includes community, repository and document ID
														// will be a DocumentURN if this is a VA document
	
    private final String documentSetIen;		// parent document set IEN
	
	private String name;
	private String description;
	private Date creationDate;
	private MediaType mediaType;				// an enumerated mime type
	private String clinicalType;				// The study image type
	private int vistaImageType;					// the VistA Image type, kinda mixed dimension of media and clinical
	private Long contentLength = null;			// 
	private String languageCode;
	private ChecksumValue checksumValue;

	private int confidentialityCode;
	private String consolidatedSiteNumber = null;
	//NOTE: If you add more fields to this class, be sure to add the new fields to cloneWithConsolidatedSiteNumber() 
	// to ensure the new fields are copied correctly
	
	/**
	 * Translates a VA image into a document, this should only be used when translating VA 
	 * images and not DoD documents
	 * 
	 * @param homeCommunityId
	 * @param image
	 * @param creationDate
	 * @return
	 * @throws URNFormatException 
	 */
	public static Document translate(
		Image image, 
		DocumentSet documentSet) 
	throws URNFormatException
	{
		if(image.hasErrorMessage())
		{
			logger.warn("Image '" + image.getImageUrn().toString() + "' contains error message, excluding from document result.");
			return null;
		}
		if(image.isDeleted())
		{
			logger.warn("Image '" + image.getImageUrn().toString() + "' is deleted, excluding from document result.");
			return null;
		}
		
		DocumentURN documentUrn = image.getGlobalArtifactIdentifier() instanceof ImageURN ?
			DocumentURN.create((ImageURN)image.getGlobalArtifactIdentifier()) :
			DocumentURN.createFromGlobalArtifactIdentifiers(WellKnownOID.VA_DOCUMENT.toString(), image.getGlobalArtifactIdentifier().getRepositoryUniqueId(), image.getGlobalArtifactIdentifier().getDocumentUniqueId());
		Document document = new Document(
			documentSet, 
			documentUrn, 
			documentSet.getProcedureDate(), 
			image.getImgType(),
			documentSet.getClinicalType(), 
			0L, 
			null);
		document.setDescription( image.getDescription() );
		String name = image.getProcedure();
		document.setName(name);
		document.setConsolidatedSiteNumber(image.getConsolidatedSiteNumber());
		populateMediaType(document, image);
		return document;
	}
	
	private static void populateMediaType(Document document, Image image)
	{
		if(document.mediaType == null)
		{
			if(document.vistaImageType == VistaImageType.PDF.getImageType())
				document.mediaType = MediaType.APPLICATION_PDF;
			else if(document.vistaImageType == VistaImageType.ASCII_TEXT.getImageType())
				document.mediaType = MediaType.TEXT_PLAIN;
			else if(document.vistaImageType == VistaImageType.HTML.getImageType())
				document.mediaType = MediaType.TEXT_HTML;
			else if(document.vistaImageType == VistaImageType.RTF.getImageType())
				document.mediaType = MediaType.TEXT_RTF;
			else if(document.vistaImageType == VistaImageType.TIFF.getImageType())
				document.mediaType = MediaType.IMAGE_TIFF;
			else if(document.vistaImageType == VistaImageType.WORD_DOCUMENT.getImageType())
				document.mediaType = MediaType.APPLICATION_DOC;
			else if(document.vistaImageType == VistaImageType.JPEG.getImageType())
				document.mediaType = MediaType.IMAGE_JPEG;
			else
			{
				String filename = image.getBigFilename();
				if((filename == null) || (filename.length() == 0))
					filename = image.getFullFilename();
				document.mediaType = MediaType.lookupByFileExtension(getFileExtension(filename));
				// if still null, default to application/octet-stream
				if(document.mediaType == null)
				{
					logger.debug("Could not find appropriate media type for filename '" + filename + "', using application/octet-stream");
					document.mediaType = MediaType.APPLICATION_OCTETSTREAM;
				}
			}			
			
		}
	}
	
	private static String getFileExtension(String imageFilename)
	{
		if(imageFilename == null)
			return null;
		int loc = imageFilename.lastIndexOf(".");
		if(loc >= 0) 
		{			
			return imageFilename.substring(loc + 1);
		}
		return null;
	}
	
    public static Document create(
    	String originatingSiteId, 
    	String imageId, 
    	String studyId, 
    	String patientIcn) 
    throws URNFormatException
    {
    	return create( null, originatingSiteId, imageId, studyId, patientIcn );
    }

    public static Document create(
    	String documentSetIdentifier, 
    	String originatingSiteId, 
    	String imageId, 
    	String studyId, 
    	String patientIcn) 
    throws URNFormatException
    {
    	return create( documentSetIdentifier, DocumentURN.create(originatingSiteId, imageId, studyId, patientIcn) );
    }
    
    /**
     * 
     * @param globalArtifactIdentifier
     * @return
     */
    public static Document create(GlobalArtifactIdentifier globalArtifactIdentifier)
    {
    	return create(null, globalArtifactIdentifier);
    }
    public static Document create(
    	String documentSetIdentifier, GlobalArtifactIdentifier globalArtifactIdentifier)
    {
    	return new Document(documentSetIdentifier, globalArtifactIdentifier);
    }
    
    /**
     * 
     * @param globalArtifactIdentifier
     * @return
     */
    public static Document create(DocumentURN documentUrn)
    {
    	return create(null, documentUrn);
    }
    public static Document create(String documentSetIdentifier, DocumentURN documentUrn)
    {
    	return new Document(documentSetIdentifier, documentUrn);
    }
    
	private Document(
		String documentSetIdentifier, 
		GlobalArtifactIdentifier identifier)
	{
		this.identifier = identifier;
		this.documentSetIen = documentSetIdentifier;
	}
	
	
	/**
	 * @param documentSet
	 * @param documentUrn
	 * @param creationDate2
	 * @param imgType
	 * @param clinicalType2
	 * @param l
	 * @param object
	 */
	public Document(
		DocumentSet documentSet, 
		GlobalArtifactIdentifier identifier, 
		Date creationDate, 
		int vistaImageType,
		String clinicalType, 
		long contentLength, 
		ChecksumValue checksumValue)
	{
		this(documentSet.getGroupIen(), 
				identifier, creationDate, vistaImageType, 
				clinicalType, contentLength, checksumValue);
	}
	
	public Document(
			String documentSetIen, 
			GlobalArtifactIdentifier identifier, 
			Date creationDate, 
			int vistaImageType,
			String clinicalType, 
			long contentLength, 
			ChecksumValue checksumValue)
		{
			super();
			
			if(identifier == null)
				throw new java.lang.NullPointerException("GlobalArtifactIdentifier cannot be a null value.");
			
			this.identifier = identifier;
			this.documentSetIen = documentSetIen;
			this.creationDate = creationDate;
			this.clinicalType = clinicalType;
			this.vistaImageType = vistaImageType;
			this.contentLength = new Long(contentLength);
			this.checksumValue = checksumValue;
			this.mediaType = null;
		}

	/**
	 * @return
	 * @throws URNFormatException 
	 */
	public DocumentURN getDocumentUrn() 
	{
		if(this.identifier instanceof DocumentURN)
			return (DocumentURN)(this.identifier);
		return null;
	}

	public GlobalArtifactIdentifier getGlobalArtifactIdentifier()
	{
		return identifier;
	}
	
	public boolean isVASourced()
	{
		return this.identifier instanceof DocumentURN;
	}
	
	public String getSiteNumber()
	{
		if(getDocumentUrn() != null)
			return getDocumentUrn().getOriginatingSiteId();
		return null;
	}

	public String getDocumentSetIen()
	{
		return this.documentSetIen;
	}

	public String getPatientId()
	{
		if(getDocumentUrn() != null)
			return getDocumentUrn().getPatientId();
		return null;
	}

	public String getDescription()
	{
		return this.description;
	}

	public Date getCreationDate()
	{
		return this.creationDate;
	}

	public String getClinicalType()
	{
		return this.clinicalType;
	}

	/**
	 * @return the vistaImageType
	 */
	public int getVistaImageType()
	{
		return this.vistaImageType;
	}
	public void setVistaImageType(int vistaImageType)
	{
		this.vistaImageType = vistaImageType;
	}

	public void setClinicalType(String clinicalType)
	{
		this.clinicalType = clinicalType;
	}

	@Override
	public MediaType getMediaType()
	{
		if(this.mediaType != null)
			return this.mediaType;
		// as of 3/15/2011 the mediaType is set in the translate method so it should never be null
		// the code below should never be executed			
		if(getVistaImageType() == VistaImageType.PDF.getImageType())
			return MediaType.APPLICATION_PDF;
		else if(getVistaImageType() == VistaImageType.ASCII_TEXT.getImageType())
			return MediaType.TEXT_PLAIN;
		else if(getVistaImageType() == VistaImageType.HTML.getImageType())
			return MediaType.TEXT_HTML;
		else if(getVistaImageType() == VistaImageType.RTF.getImageType())
			return MediaType.TEXT_RTF;
		else if(getVistaImageType() == VistaImageType.TIFF.getImageType())
			return MediaType.IMAGE_TIFF;
		else if(getVistaImageType() == VistaImageType.WORD_DOCUMENT.getImageType())
			return MediaType.APPLICATION_DOC;
		else if(getVistaImageType() == VistaImageType.JPEG.getImageType())
			return MediaType.IMAGE_JPEG;
		else
			return MediaType.APPLICATION_OCTETSTREAM;
	}

	public void setMediaType(MediaType mediaType)
	{
		this.mediaType = mediaType;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public void setCreationDate(Date creationDate)
	{
		this.creationDate = creationDate;
	}

	@Override
	public ChecksumValue getChecksumValue()
	{
		return checksumValue;
	}

	
	public void setContentLength(long contentLength)
	{
		this.contentLength = new Long(contentLength);
	}

	@Override
	public long getContentLength()
	{
		return contentLength == null ? -1 : this.contentLength.longValue();
	}
	
	public String getLanguageCode()
	{
		return this.languageCode;
	}

	public void setLanguageCode(String languageCode)
	{
		this.languageCode = languageCode;
	}

	/**
	 * @param confidentialityCode
	 */
	public void setConfidentialityCode(int confidentialityCode)
	{
		this.confidentialityCode = confidentialityCode;
	}

	public int getConfidentialityCode()
	{
		return this.confidentialityCode;
	}

	@Override
	public ByteChannel getContent()
    {
    	return null;
    }
	
	@Override
	public int compareTo(Document o)
	{
		if((this.creationDate != null) && (o.creationDate != null))
		{
			return this.creationDate.compareTo(o.creationDate);
		}
		return this.identifier.compareTo(o.identifier);
	}

	/**
	 * @return the repositoryId
	 */
	public String getRepositoryId() 
	{
		return identifier.getRepositoryUniqueId();
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	public String getConsolidatedSiteNumber()
	{
		return consolidatedSiteNumber;
	}

	public void setConsolidatedSiteNumber(String consolidatedSiteNumber)
	{
		this.consolidatedSiteNumber = consolidatedSiteNumber;
	}
	
	public boolean containsConsolidatedSiteNumber()
	{
		if(this.consolidatedSiteNumber == null)
			return false;
		if(this.consolidatedSiteNumber.length() <= 0)
			return false;
		// if the consolidated site number value is the same as the repository ID, no need to update the value
		// just return false to avoid unnecessary effort
		if(this.consolidatedSiteNumber.equals(this.getRepositoryId()))
			return false;
		return true;
	}
	
	public Document cloneWithConsolidatedSiteNumber()
	{
		if(containsConsolidatedSiteNumber())
		{
			if(!this.getSiteNumber().equals(this.consolidatedSiteNumber))
			{
				DocumentURN documentUrn = this.getDocumentUrn();			
				// if the documentUrn is null then this is not a VA image and we shouldn't do anything with it
				if(documentUrn != null)
				{
					try
					{
						DocumentURN newDocumentUrn = 
							DocumentURN.create(this.consolidatedSiteNumber, documentUrn.getDocumentId(), documentUrn.getDocumentSetId(), documentUrn.getPatientId());
						logger.info("Updating document '" + documentUrn.toString() + "' with new site ID, created new URN '" + newDocumentUrn.toString() + "'.");
						Document document = new Document(this.documentSetIen, newDocumentUrn, 
								this.creationDate, this.vistaImageType, this.clinicalType, 
								this.contentLength, this.checksumValue);
						document.confidentialityCode = this.confidentialityCode;
						document.consolidatedSiteNumber = this.consolidatedSiteNumber;
						document.description = this.description;
						document.languageCode = this.languageCode;
						document.mediaType = this.mediaType;
						document.name = this.name;
						return document;
					}
					catch(URNFormatException urnfX)
					{
						logger.warn("Error creating new DocumentURN from consolidated site number, " + urnfX.getMessage(), urnfX);
					}					
				}
			}
		}
		// if we get here then either something went wrong or the conversion was not necessary
		return this;
	}

	@Override
	public String toString()
	{
		DateFormat df = DateFormat.getDateInstance();
		StringBuilder sb = new StringBuilder(); 
		
		sb.append(this.getClass().getSimpleName());
		sb.append(':');
		
		sb.append(" identifier: [" + getGlobalArtifactIdentifier().toString() + "]" );
		sb.append(" vistaImageType: [" + this.getVistaImageType() + "]" );
		sb.append(" clinicalType: [" + this.getClinicalType() + "]" );
		sb.append(" creationDate: [" + (this.creationDate == null ? "null" : df.format(this.creationDate)) + "]" );
		sb.append(" confidentialityCode: [" + this.confidentialityCode + "]" );
		sb.append(" description: [" + this.description + "]" );
		sb.append(" documentSetIen: [" + this.documentSetIen + "]" );
		sb.append(" languageCode: [" + this.languageCode + "]" );
		sb.append(" name: [" + this.name + "]" );
		
		return sb.toString();
	}

}
