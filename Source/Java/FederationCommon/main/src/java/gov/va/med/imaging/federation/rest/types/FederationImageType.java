/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 19, 2010
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
public class FederationImageType 
{
	private String imageId;
    private String dicomUid;
    private Integer imageNumber;
    private String description;
    private Date procedureDate;
    private String procedure;
    private String dicomSequenceNumberForDisplay;
    private String dicomImageNumberForDisplay;
    private String patientIcn;
    private String patientName;
    private String siteNumber;
    private String siteAbbr;
    private int imageType;
    private String absLocation;
    private String fullLocation;
    private String imageClass;
    private String fullImageFilename;
    private String absImageFilename;
    private String bigImageFilename;
    private String qaMessage;
    private String imageModality;
    private String studyId;
    //private String groupId;
    private String errorMessage;
    private String consolidatedSiteNumber;
    private String alienSiteNumber;
    private boolean imageHasAnnotations;
	// if the image is associated with a progress note, indicates if it is resulted
	private String associatedNoteResulted;
	private int imageAnnotationStatus;
	private String imageAnnotationStatusDescription;
	private String imagePackage;
	
	private FederationObjectStatusType imageStatus = FederationObjectStatusType.NO_STATUS;
	private FederationObjectStatusType imageViewStatus = FederationObjectStatusType.NO_STATUS;
	private boolean sensitive = false;
	private Date documentDate = null;
	private Date captureDate = null;
	
    
    public FederationImageType()
    {
    	super();
    }

	public String getImageId() {
		return imageId;
	}

	public void setImageId(String imageId) {
		this.imageId = imageId;
	}

	public String getDicomUid() {
		return dicomUid;
	}

	public void setDicomUid(String dicomUid) {
		this.dicomUid = dicomUid;
	}

	public Integer getImageNumber() {
		return imageNumber;
	}

	public void setImageNumber(Integer imageNumber) {
		this.imageNumber = imageNumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getProcedureDate() {
		return procedureDate;
	}

	public void setProcedureDate(Date procedureDate) {
		this.procedureDate = procedureDate;
	}

	public String getProcedure() {
		return procedure;
	}

	public void setProcedure(String procedure) {
		this.procedure = procedure;
	}

	public String getDicomSequenceNumberForDisplay() {
		return dicomSequenceNumberForDisplay;
	}

	public void setDicomSequenceNumberForDisplay(
			String dicomSequenceNumberForDisplay) {
		this.dicomSequenceNumberForDisplay = dicomSequenceNumberForDisplay;
	}

	public String getDicomImageNumberForDisplay() {
		return dicomImageNumberForDisplay;
	}

	public void setDicomImageNumberForDisplay(String dicomImageNumberForDisplay) {
		this.dicomImageNumberForDisplay = dicomImageNumberForDisplay;
	}

	public String getPatientIcn() {
		return patientIcn;
	}

	public void setPatientIcn(String patientIcn) {
		this.patientIcn = patientIcn;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getSiteNumber() {
		return siteNumber;
	}

	public void setSiteNumber(String siteNumber) {
		this.siteNumber = siteNumber;
	}

	public String getSiteAbbr() {
		return siteAbbr;
	}

	public void setSiteAbbr(String siteAbbr) {
		this.siteAbbr = siteAbbr;
	}

	public int getImageType() {
		return imageType;
	}

	public void setImageType(int imageType) {
		this.imageType = imageType;
	}

	public String getAbsLocation() {
		return absLocation;
	}

	public void setAbsLocation(String absLocation) {
		this.absLocation = absLocation;
	}

	public String getFullLocation() {
		return fullLocation;
	}

	public void setFullLocation(String fullLocation) {
		this.fullLocation = fullLocation;
	}

	public String getImageClass() {
		return imageClass;
	}

	public void setImageClass(String imageClass) {
		this.imageClass = imageClass;
	}

	public String getFullImageFilename() {
		return fullImageFilename;
	}

	public void setFullImageFilename(String fullImageFilename) {
		this.fullImageFilename = fullImageFilename;
	}

	public String getAbsImageFilename() {
		return absImageFilename;
	}

	public void setAbsImageFilename(String absImageFilename) {
		this.absImageFilename = absImageFilename;
	}

	public String getBigImageFilename() {
		return bigImageFilename;
	}

	public void setBigImageFilename(String bigImageFilename) {
		this.bigImageFilename = bigImageFilename;
	}

	public String getQaMessage() {
		return qaMessage;
	}

	public void setQaMessage(String qaMessage) {
		this.qaMessage = qaMessage;
	}

	public String getImageModality() {
		return imageModality;
	}

	public void setImageModality(String imageModality) {
		this.imageModality = imageModality;
	}

	public String getStudyId() {
		return studyId;
	}

	public void setStudyId(String studyId) {
		this.studyId = studyId;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getConsolidatedSiteNumber()
	{
		return consolidatedSiteNumber;
	}

	public void setConsolidatedSiteNumber(String consolidatedSiteNumber)
	{
		this.consolidatedSiteNumber = consolidatedSiteNumber;
	}

	public String getAlienSiteNumber()
	{
		return alienSiteNumber;
	}

	public void setAlienSiteNumber(String alienSiteNumber)
	{
		this.alienSiteNumber = alienSiteNumber;
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

	public String getImagePackage()
	{
		return imagePackage;
	}

	public void setImagePackage(String imagePackage)
	{
		this.imagePackage = imagePackage;
	}

	public FederationObjectStatusType getImageStatus()
	{
		return imageStatus;
	}

	public void setImageStatus(FederationObjectStatusType imageStatus)
	{
		this.imageStatus = imageStatus;
	}

	public FederationObjectStatusType getImageViewStatus()
	{
		return imageViewStatus;
	}

	public void setImageViewStatus(FederationObjectStatusType imageViewStatus)
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
}
