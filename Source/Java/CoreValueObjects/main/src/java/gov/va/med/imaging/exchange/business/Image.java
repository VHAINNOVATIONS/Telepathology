/**
 * 
 */
package gov.va.med.imaging.exchange.business;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.ImageURNFactory;
import gov.va.med.PatientIdentifier;
import gov.va.med.PatientIdentifierType;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.enums.ObjectOrigin;
import gov.va.med.imaging.exchange.enums.ObjectStatus;

import java.io.Serializable;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * @author VHAISWWERFEJ
 *
 * The RoutingToken implementation is here so that an Image
 * instance can be used to get an Instance.  Since the Image 
 * identifier is a GlobalArtifactIdentifier the RoutingToken
 * interface can simply delegate to the identifier.
 *
 */
public class Image 
implements Serializable, Comparable<Image>, RoutingToken 
{
	private static final long serialVersionUID = -4029416178345334605L;
	private final static Logger logger = Logger.getLogger(Image.class);
	
	private GlobalArtifactIdentifier globalArtifactIdentifier;
	// fields that make up the image identifier(s)
    //protected String patientICN;
    
    protected String absFilename; // UNC/HTTP path of image (full path)
    protected String fullFilename; // UNC/HTTP path of image (full path)
    protected String bigFilename; // UNC/HTTP path of image (full path)
    protected String description;
    protected Date procedureDate;
    protected String procedure;
    protected int imgType; // image type (100 = study, 3 = radiology?)
    protected String absLocation; // location of abstract (M = magnetic)
    protected String fullLocation; // location of full image (m = magnetic)
    protected String dicomSequenceNumberForDisplay;
    protected String dicomImageNumberForDisplay;
    protected String patientDFN; // not sure if we will be able to get this from DOD - may not matter
    protected String patientName;
    protected String imageClass;    
    protected String siteAbbr;
    //protected String studyIen;
    //protected String groupIen;
    protected String imageUid; // DICOM Image UID
    protected String imageNumber; // DICOM Image sequence number
    private String qaMessage;
    private ObjectOrigin objectOrigin;
    //private String imageModality;
    private String errorMessage;
    private String alienSiteNumber;
    private ObjectStatus imageStatus = ObjectStatus.NO_STATUS;
	private ObjectStatus imageViewStatus = ObjectStatus.NO_STATUS;
	private boolean sensitive = false;
	private Date documentDate = null;
	private Date captureDate = null;
	private String consolidatedSiteNumber = null;
	private boolean imageHasAnnotations = false;
	// if the image is associated with a progress note, indicates if it is resulted.  If this is not a progress note
	// then it is null
	private String associatedNoteResulted = "";
	private int imageAnnotationStatus = 0;
	private String imageAnnotationStatusDescription = "";
	private String imagePackage = "";
	
	
	//NOTE: If you add more fields to this class, be sure to add the new fields to cloneWithConsolidatedSiteNumber() 
	// to ensure the new fields are copied correctly
    
    /**
     * 
     */
    public static Image create(String originatingSiteId, String imageId, String studyId, 
    		PatientIdentifier patientIdentifier,
    		String imageModality) 
    throws URNFormatException
    {
    	ImageURN imageUrn = ImageURNFactory.create(originatingSiteId, imageId, studyId, 
    			patientIdentifier.getValue(), imageModality, ImageURN.class);
    	imageUrn.setPatientIdentifierTypeIfNecessary(patientIdentifier.getPatientIdentifierType());
    	
    	return create( imageUrn );
    }

    /**
     * 
     * @param globalArtifactIdentifier
     * @return
     */
    public static Image create(GlobalArtifactIdentifier globalArtifactIdentifier)
    {
    	return new Image(globalArtifactIdentifier);
    }
    
    /**
     * 
     * @param globalArtifactIdentifier
     * @return
     */
    public static Image create(ImageURN imageUrn)
    {
    	return new Image(imageUrn);
    }
    
    /**
     * 
     * @param globalArtifactIdentifier
     */
    private Image(GlobalArtifactIdentifier globalArtifactIdentifier)
    {
    	this.globalArtifactIdentifier = globalArtifactIdentifier;
        absFilename = fullFilename = bigFilename = description = procedure = absLocation = fullLocation = imageNumber = 
            dicomSequenceNumberForDisplay = dicomImageNumberForDisplay = patientDFN = patientName = imageClass = 
            siteAbbr = imageUid = qaMessage = "";
        imgType = 0;
        procedureDate = null;
        errorMessage = "";
    }
    
	/**
	 * @return
	 */
	public final GlobalArtifactIdentifier getGlobalArtifactIdentifier()
	{
		return globalArtifactIdentifier; 
	}

	// implementation of RoutingToken simply delegates to the identifier
    @Override
	public String getHomeCommunityId(){return getGlobalArtifactIdentifier().getHomeCommunityId();}

	@Override
	public String getRepositoryUniqueId(){return getGlobalArtifactIdentifier().getRepositoryUniqueId();}

	/**
     * @return the absFilename
     */
    public String getAbsFilename() {
        return absFilename;
    }

    /**
     * @param absFilename the absFilename to set
     */
    public void setAbsFilename(String absFilename) {
        this.absFilename = absFilename;
    }

    /**
     * @return the absLocation
     */
    public String getAbsLocation() {
        return absLocation;
    }

    /**
     * @param absLocation the absLocation to set
     */
    public void setAbsLocation(String absLocation) {
        this.absLocation = absLocation;
    }

    /**
     * @return the bigFilename
     */
    public String getBigFilename() {
        return bigFilename;
    }

    /**
     * @param bigFilename the bigFilename to set
     */
    public void setBigFilename(String bigFilename) {
        this.bigFilename = bigFilename;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the fullFilename
     */
    public String getFullFilename() {
        return fullFilename;
    }

    /**
     * @param fullFilename the fullFilename to set
     */
    public void setFullFilename(String fullFilename) {
        this.fullFilename = fullFilename;
    }

    /**
     * @return the fullLocation
     */
    public String getFullLocation() {
        return fullLocation;
    }

    /**
     * @param fullLocation the fullLocation to set
     */
    public void setFullLocation(String fullLocation) {
        this.fullLocation = fullLocation;
    }

    /**
     * @return the image IEN if this is a VistA Imaging image, else return null
     */
    public String getIen() 
    {
        return this.globalArtifactIdentifier instanceof ImageURN ? 
        	((ImageURN)(this.globalArtifactIdentifier)).getImageId() :
        	null;
    }

    /**
     * @return the imageClass
     */
    public String getImageClass() {
        return imageClass;
    }

    /**
     * @param imageClass the imageClass to set
     */
    public void setImageClass(String imageClass) {
        this.imageClass = imageClass;
    }

    /**
     * @return the imgType
     */
    public int getImgType() {
        return imgType;
    }

    /**
     * @param imgType the imgType to set
     */
    public void setImgType(int imgType) {
        this.imgType = imgType;
    }

    /**
     * @return the patientDFN
     */
    public String getPatientDFN() {
        return patientDFN;
    }

    /**
     * @param patientDFN the patientDFN to set
     */
    public void setPatientDFN(String patientDFN) {
        this.patientDFN = patientDFN;
    }

    /**
     * @return the patientICN
     */
    public String getPatientId() 
    {
    	return this.globalArtifactIdentifier instanceof ImageURN ? 
            	((ImageURN)(this.globalArtifactIdentifier)).getPatientId() :
            	null;
    }
    
    public PatientIdentifier getPatientIdentifier()
	{
		String patientId = getPatientId();
		PatientIdentifierType patientIdentifierType = getPatientIdentifierType();
		if(patientId == null || patientIdentifierType == null)
			return null;
		
		return new PatientIdentifier(patientId, patientIdentifierType);
	}
	
	public PatientIdentifierType getPatientIdentifierType()
	{
		return this.globalArtifactIdentifier instanceof ImageURN ? 
            	((ImageURN)(this.globalArtifactIdentifier)).getPatientIdentifierTypeOrDefault() :
            	null;
	}

    /**
     * @return the patientName
     */
    public String getPatientName() {
        return patientName;
    }

    /**
     * @param patientName the patientName to set
     */
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    /**
     * @return the procedure
     */
    public String getProcedure() {
        return procedure;
    }

    /**
     * @param procedure the procedure to set
     */
    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    /**
     * @return the procedureDate
     */
    public Date getProcedureDate() {
        return procedureDate;
    }

    /**
     * @param procedureDate the procedureDate to set
     */
    public void setProcedureDate(Date procedureDate) {
        this.procedureDate = procedureDate;
    }

    /**
     * @return the siteNumber
     */
    public String getSiteNumber() 
    {
        return globalArtifactIdentifier.getRepositoryUniqueId();
    }

    /**
     * @param siteNumber the siteNumber to set
     */
    //public void setSiteNumber(String siteNumber) 
    //{
    //	try
	//	{
	//		if(this.globalArtifactIdentifier != null)
	//			this.globalArtifactIdentifier = 
	//				GlobalArtifactIdentifierFactory.create(this.globalArtifactIdentifier.getHomeCommunityId(), siteNumber, this.globalArtifactIdentifier.getDocumentUniqueId());
	//		else
	//			this.globalArtifactIdentifier = 
	//				GlobalArtifactIdentifierFactory.create(null, siteNumber, null);
	//	}
	//	catch (Throwable x)
	//	{
	//		x.printStackTrace();
	//	}
    //}

	public String getSiteAbbr() {
		return siteAbbr;
	}

	public void setSiteAbbr(String siteAbbr) {
		this.siteAbbr = siteAbbr;
	}

	/**
	 * @return the studyIen
	 */
	public String getStudyIen() 
	{
		return this.globalArtifactIdentifier instanceof ImageURN ? 
	        	((ImageURN)(this.globalArtifactIdentifier)).getStudyId() :
	        	null;
	}

	/**
	 * @return the imageUid
	 */
	public String getImageUid() {
		return imageUid;
	}

	/**
	 * @param imageUid the imageUid to set
	 */
	public void setImageUid(String imageUid) {
		this.imageUid = imageUid;
	}

	/**
	 * @return the dicomImageNumberForDisplay
	 */
	public String getDicomImageNumberForDisplay() {
		return dicomImageNumberForDisplay;
	}

	/**
	 * @param dicomImageNumberForDisplay the dicomImageNumberForDisplay to set
	 */
	public void setDicomImageNumberForDisplay(String dicomImageNumberForDisplay) {
		this.dicomImageNumberForDisplay = dicomImageNumberForDisplay;
	}

	/**
	 * @return the dicomSequenceNumberForDisplay
	 */
	public String getDicomSequenceNumberForDisplay() {
		return dicomSequenceNumberForDisplay;
	}

	/**
	 * @param dicomSequenceNumberForDisplay the dicomSequenceNumberForDisplay to set
	 */
	public void setDicomSequenceNumberForDisplay(
			String dicomSequenceNumberForDisplay) {
		this.dicomSequenceNumberForDisplay = dicomSequenceNumberForDisplay;
	}

	/**
	 * @return the imageNumber
	 */
	public String getImageNumber() {
		return imageNumber;
	}

	/**
	 * @param imageNumber the imageNumber to set
	 */
	public void setImageNumber(String imageNumber) {
		this.imageNumber = imageNumber;
	}

	/**
	 * @return the objectOrigin
	 */
	public ObjectOrigin getObjectOrigin() {
		return objectOrigin;
	}

	/**
	 * @param objectOrigin the objectOrigin to set
	 */
	public void setObjectOrigin(ObjectOrigin objectOrigin) {
		this.objectOrigin = objectOrigin;
	}

	/**
	 * @return the qaMessage
	 */
	public String getQaMessage() {
		return qaMessage;
	}

	/**
	 * @param qaMessage the qaMessage to set
	 */
	public void setQaMessage(String qaMessage) {
		this.qaMessage = qaMessage;
	}
	
	public ImageURN getImageUrn() 
	{
		return this.globalArtifactIdentifier instanceof ImageURN ?
			(ImageURN)(this.globalArtifactIdentifier) : null;
	}

	public String getImageModality() 
	{
		return this.globalArtifactIdentifier instanceof ImageURN ? 
            	((ImageURN)(this.globalArtifactIdentifier)).getImageModality() :
            	null;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public boolean hasErrorMessage()
	{
		if((errorMessage == null) || (errorMessage.length() <= 0))
			return false;
		return true;
	}

	/**
	 * The alien site number is the number for a site where the image came from. This number is the number
	 * from the alien data source (DOD).  This is not the site number used internally that exists within the
	 * site service but an external number. This number is not required to be present and should not transmit
	 * across Federation. 
	 * @return the alienSiteNumber
	 */
	public String getAlienSiteNumber() {
		return alienSiteNumber;
	}

	public int getImageAnnotationStatus()
	{
		return imageAnnotationStatus;
	}

	public void setImageAnnotationStatus(int imageAnnotationStatus)
	{
		this.imageAnnotationStatus = imageAnnotationStatus;
	}

	public String getImageAnnotationStatusDescription()
	{
		return imageAnnotationStatusDescription;
	}

	public void setImageAnnotationStatusDescription(
			String imageAnnotationStatusDescription)
	{
		this.imageAnnotationStatusDescription = imageAnnotationStatusDescription;
	}

	/**
	 * @param alienSiteNumber the alienSiteNumber to set
	 */
	public void setAlienSiteNumber(String alienSiteNumber) {
		this.alienSiteNumber = alienSiteNumber;
	}

	@Override
	public boolean isEquivalent(RoutingToken that)
	{
		return getGlobalArtifactIdentifier().isEquivalent(that);
	}

	@Override
	public boolean isIncluding(RoutingToken that)
	{
		return getGlobalArtifactIdentifier().isIncluding(that);
	}

	public ObjectStatus getImageStatus()
	{
		return imageStatus;
	}

	public void setImageStatus(ObjectStatus imageStatus)
	{
		this.imageStatus = imageStatus;
	}

	public ObjectStatus getImageViewStatus()
	{
		return imageViewStatus;
	}

	public void setImageViewStatus(ObjectStatus imageViewStatus)
	{
		this.imageViewStatus = imageViewStatus;
	}

	public boolean isSensitive()
	{
		return sensitive;
	}

	public void setSensitive(boolean sensitive)
	{
		this.sensitive = sensitive;
	}

	public Date getDocumentDate()
	{
		return documentDate;
	}

	public void setDocumentDate(Date documentDate)
	{
		this.documentDate = documentDate;
	}

	public Date getCaptureDate()
	{
		return captureDate;
	}

	public void setCaptureDate(Date captureDate)
	{
		this.captureDate = captureDate;
	}
	
	public boolean isDeleted()
	{
		return (imageViewStatus == ObjectStatus.DELETED);
	}

	public String getConsolidatedSiteNumber()
	{
		return consolidatedSiteNumber;
	}

	public void setConsolidatedSiteNumber(String consolidatedSiteNumber)
	{
		this.consolidatedSiteNumber = consolidatedSiteNumber;
	}

	public boolean isImageHasAnnotations()
	{
		return imageHasAnnotations;
	}

	public void setImageHasAnnotations(boolean imageHasAnnotations)
	{
		this.imageHasAnnotations = imageHasAnnotations;
	}

	public String getAssociatedNoteResulted()
	{
		return associatedNoteResulted;
	}

	public void setAssociatedNoteResulted(String associatedNoteResulted)
	{
		this.associatedNoteResulted = associatedNoteResulted;
	}

	public String getImagePackage()
	{
		return imagePackage;
	}

	public void setImagePackage(String imagePackage)
	{
		this.imagePackage = imagePackage;
	}

	@Override
	public String toString() 
	{
		String output = "";
		output += "GlobalArtifactIdentifier[" + this.getGlobalArtifactIdentifier().toString() + "]\n"; 
		output += "Image Properties:\n";
		output += "AbsFilename: " + absFilename + "\n";
		output += "FullFilename: " + fullFilename + "\n";
		output += "BigFilename: " + bigFilename + "\n";
		output += "Description: " + description + "\n";
		output += "Procedure Date: " + procedureDate + "\n";
		output += "Procedure: " + procedure + "\n";
		output += "ImgType: " + imgType + "\n";
		output += "AbsLocation: " + absLocation + "\n";
		output += "FullLocation: " + fullLocation + "\n";
		output += "DicomSequenceNumber: " + dicomSequenceNumberForDisplay + "\n";
		output += "DicomImageNumber: " + dicomImageNumberForDisplay + "\n";
		output += "PatientDFN: " + patientDFN + "\n";
		//output += "PatientICN: " + patientICN + "\n";
		output += "PatientName: " + patientName + "\n";
		output += "ImageClass: " + imageClass + "\n";
		output += "SiteAbbr: " + siteAbbr + "\n";
		
		return output;
	}

	/**
	 * @see gov.va.med.RoutingToken#toRoutingTokenString()
	 */
	@Override
	public String toRoutingTokenString()
	{
		return getGlobalArtifactIdentifier() == null ? null : getGlobalArtifactIdentifier().toRoutingTokenString();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object arg0) 
	{
		if(arg0 instanceof Image) 
		{
			Image that = (Image)arg0;
			
			// if the study, group and image IENs are equal then the procedure date had better
			// be equal, nonetheless it is compared here to assure that .equals and .compareTo
			// are strictly compatible
			if(	this.globalArtifactIdentifier.equalsGlobalArtifactIdentifier(that.getGlobalArtifactIdentifier()) &&
				this.procedureDate != null && this.procedureDate.equals(that.procedureDate) &&
				this.getSiteNumber() != null && this.getSiteNumber().equalsIgnoreCase(that.getSiteNumber())) 
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * The natural sort order of Image instances is:
	 * 1.) decreasing by procedure date
	 * 2.) increasing by site number
	 * 3.) increasing by study IEN
	 * 4.) increasing by group IEN (series)
	 * 5.) increasing by image IEN
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Image that) 
	{
		int cumulativeCompare = 0;
		
		cumulativeCompare = ComparableUtil.compare(this.procedureDate, that.procedureDate, false);
		if(cumulativeCompare != 0)
			return cumulativeCompare;
		
		cumulativeCompare = ComparableUtil.compare(this.getSiteNumber(), that.getSiteNumber(), true);
		if(cumulativeCompare != 0)
			return cumulativeCompare;
			
		cumulativeCompare = ComparableUtil.compare(this.getStudyIen(), that.getStudyIen(), true);
		if(cumulativeCompare != 0)
			return cumulativeCompare;
		
		return ComparableUtil.compare(
			this.getGlobalArtifactIdentifier().getDocumentUniqueId(), 
			that.getGlobalArtifactIdentifier().getDocumentUniqueId(), 
			true
		);
	}
	
	public boolean containsConsolidatedSiteNumber()
	{
		if(this.consolidatedSiteNumber == null)
			return false;
		if(this.consolidatedSiteNumber.length() <= 0)
			return false;
		// if the consolidated site number value is the same as the site number, no need to update the value
		// just return false to avoid unnecessary effort
		if(this.consolidatedSiteNumber.equals(this.getSiteNumber()))
			return false;
		return true;
	}
	
	public Image cloneWithConsolidatedSiteNumber()
	{
		if(containsConsolidatedSiteNumber())
		{
			if(!this.getSiteNumber().equals(this.consolidatedSiteNumber))
			{
				ImageURN imageUrn = this.getImageUrn();
				// if the image URN is null then this is not a VA image and we shouldn't do anything with it
				if(imageUrn != null)
				{
					try
					{
						ImageURN newUrn =
							ImageURNFactory.create(this.consolidatedSiteNumber, imageUrn.getImageId(),
									imageUrn.getStudyId(), imageUrn.getPatientId(), imageUrn.getImageModality(), 
									ImageURN.class);
						newUrn.setPatientIdentifierTypeIfNecessary(imageUrn.getPatientIdentifierType());
						
						logger.debug("Updating image '" + imageUrn.toString() + "' with new site ID, created new URN '" + newUrn.toString() + "'.");
						Image image = new Image(newUrn);
						image.absFilename = this.absFilename;
						image.absLocation = this.absLocation;
						image.alienSiteNumber = this.alienSiteNumber;
						image.bigFilename = this.bigFilename;
						image.captureDate = this.captureDate;
						image.consolidatedSiteNumber = this.consolidatedSiteNumber;
						image.description = this.description;
						image.dicomImageNumberForDisplay = this.dicomImageNumberForDisplay;
						image.dicomSequenceNumberForDisplay = this.dicomSequenceNumberForDisplay;
						image.documentDate = this.documentDate;
						image.errorMessage = this.errorMessage;
						image.fullFilename = this.fullFilename;
						image.fullLocation = this.fullLocation;
						image.imageClass = this.imageClass;
						image.imageNumber = this.imageNumber;
						image.imageStatus = this.imageStatus;
						image.imageUid = this.imageUid;
						image.imageViewStatus = this.imageViewStatus;
						image.imgType = this.imgType;
						image.objectOrigin = this.objectOrigin;
						image.patientDFN = this.patientDFN;
						image.patientName = this.patientName;
						image.procedure = this.procedure;
						image.procedureDate = this.procedureDate;
						image.qaMessage = this.qaMessage;
						image.sensitive = this.sensitive;
						image.siteAbbr = this.siteAbbr; // this should be correct from VistA
						image.associatedNoteResulted = this.associatedNoteResulted;
						image.imageAnnotationStatus = this.imageAnnotationStatus;
						image.imageAnnotationStatusDescription = this.imageAnnotationStatusDescription;
						image.imageHasAnnotations = this.imageHasAnnotations;
						image.imagePackage = this.imagePackage;

						return image;
					}
					catch(URNFormatException urnfX)
					{
						logger.warn("Error creating new ImageURN from consolidated site number, " + urnfX.getMessage(), urnfX);
					}					
				}
			}
		}
		// if we get here then either something went wrong or the conversion was not necessary
		return this;
	}
}
