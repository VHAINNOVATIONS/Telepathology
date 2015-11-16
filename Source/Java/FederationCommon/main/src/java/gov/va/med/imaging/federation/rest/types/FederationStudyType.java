/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 15, 2010
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
public class FederationStudyType 
{
	private String studyId;
    private String dicomUid;
    private String description;
    private Date procedureDate;
    private String procedureDescription;
    private String patientIcn; 
    private String patientName;
    private String siteNumber;
    private String siteName;
    private String siteAbbreviation;
    private int imageCount;
//    private int seriesCount;
    private String specialtyDescription;
    private String radiologyReport;
    private String noteTitle;
    private String imagePackage;
    private String imageType;
    private String event;
    private String origin;
    private String studyPackage;
    private String studyClass;
    private String studyType;
    private String captureDate;
    private String capturedBy;
    private String rpcResponseMsg;
    private String firstImageIen;
    private String errorMessage;

    private FederationStudyLoadLevelType studyLoadLevel;
    private FederationSeriesType [] series;
    private FederationImageType firstImage;
    private String [] studyModalities;
    
    private FederationObjectStatusType studyStatus = FederationObjectStatusType.NO_STATUS;
	private FederationObjectStatusType studyViewStatus = FederationObjectStatusType.NO_STATUS;
	private boolean sensitive = false;
	private Date documentDate = null;
	private FederationStudyDeletedImageStateType studyDeletedImageState;
	private String cptCode;
	private String consolidatedSiteNumber;
	private String alternateArtifactId;
	private String alienSiteNumber;
	private boolean studyImagesHaveAnnotations;

    public FederationStudyType()
    {
    	super();
    }

	public String getStudyId() {
		return studyId;
	}

	public void setStudyId(String studyId) {
		this.studyId = studyId;
	}

	public String getDicomUid() {
		return dicomUid;
	}

	public void setDicomUid(String dicomUid) {
		this.dicomUid = dicomUid;
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

	public String getProcedureDescription() {
		return procedureDescription;
	}

	public void setProcedureDescription(String procedureDescription) {
		this.procedureDescription = procedureDescription;
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

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getSiteAbbreviation() {
		return siteAbbreviation;
	}

	public void setSiteAbbreviation(String siteAbbreviation) {
		this.siteAbbreviation = siteAbbreviation;
	}

	public int getImageCount() {
		return imageCount;
	}

	public void setImageCount(int imageCount) {
		this.imageCount = imageCount;
	}

	/*
	public int getSeriesCount() {
		return seriesCount;
	}

	public void setSeriesCount(int seriesCount) {
		this.seriesCount = seriesCount;
	}*/

	public String getSpecialtyDescription() {
		return specialtyDescription;
	}

	public void setSpecialtyDescription(String specialtyDescription) {
		this.specialtyDescription = specialtyDescription;
	}

	public String getRadiologyReport() {
		return radiologyReport;
	}

	public void setRadiologyReport(String radiologyReport) {
		this.radiologyReport = radiologyReport;
	}

	public String getNoteTitle() {
		return noteTitle;
	}

	public void setNoteTitle(String noteTitle) {
		this.noteTitle = noteTitle;
	}

	public String getImagePackage() {
		return imagePackage;
	}

	public void setImagePackage(String imagePackage) {
		this.imagePackage = imagePackage;
	}

	public String getImageType() {
		return imageType;
	}

	public void setImageType(String imageType) {
		this.imageType = imageType;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getStudyPackage() {
		return studyPackage;
	}

	public void setStudyPackage(String studyPackage) {
		this.studyPackage = studyPackage;
	}

	public String getStudyClass() {
		return studyClass;
	}

	public void setStudyClass(String studyClass) {
		this.studyClass = studyClass;
	}

	public String getStudyType() {
		return studyType;
	}

	public void setStudyType(String studyType) {
		this.studyType = studyType;
	}

	public String getCaptureDate() {
		return captureDate;
	}

	public void setCaptureDate(String captureDate) {
		this.captureDate = captureDate;
	}

	public String getCapturedBy() {
		return capturedBy;
	}

	public void setCapturedBy(String capturedBy) {
		this.capturedBy = capturedBy;
	}

	public String getRpcResponseMsg() {
		return rpcResponseMsg;
	}

	public void setRpcResponseMsg(String rpcResponseMsg) {
		this.rpcResponseMsg = rpcResponseMsg;
	}

	public String getFirstImageIen() {
		return firstImageIen;
	}

	public void setFirstImageIen(String firstImageIen) {
		this.firstImageIen = firstImageIen;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public FederationStudyLoadLevelType getStudyLoadLevel() {
		return studyLoadLevel;
	}

	public void setStudyLoadLevel(FederationStudyLoadLevelType studyLoadLevel) {
		this.studyLoadLevel = studyLoadLevel;
	}

	public FederationSeriesType[] getSeries() {
		return series;
	}

	public void setSeries(FederationSeriesType[] series) {
		this.series = series;
	}

	public FederationImageType getFirstImage() {
		return firstImage;
	}

	public void setFirstImage(FederationImageType firstImage) {
		this.firstImage = firstImage;
	}

	public String[] getStudyModalities() {
		return studyModalities;
	}

	public void setStudyModalities(String[] studyModalities) {
		this.studyModalities = studyModalities;
	}

	public FederationObjectStatusType getStudyStatus()
	{
		return studyStatus;
	}

	public void setStudyStatus(FederationObjectStatusType studyStatus)
	{
		this.studyStatus = studyStatus;
	}

	public FederationObjectStatusType getStudyViewStatus()
	{
		return studyViewStatus;
	}

	public void setStudyViewStatus(FederationObjectStatusType studyViewStatus)
	{
		this.studyViewStatus = studyViewStatus;
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

	public FederationStudyDeletedImageStateType getStudyDeletedImageState()
	{
		return studyDeletedImageState;
	}

	public void setStudyDeletedImageState(
			FederationStudyDeletedImageStateType studyDeletedImageState)
	{
		this.studyDeletedImageState = studyDeletedImageState;
	}

	public String getCptCode()
	{
		return cptCode;
	}

	public void setCptCode(String cptCode)
	{
		this.cptCode = cptCode;
	}

	public String getConsolidatedSiteNumber()
	{
		return consolidatedSiteNumber;
	}

	public void setConsolidatedSiteNumber(String consolidatedSiteNumber)
	{
		this.consolidatedSiteNumber = consolidatedSiteNumber;
	}

	public String getAlternateArtifactId()
	{
		return alternateArtifactId;
	}

	public void setAlternateArtifactId(String alternateArtifactId)
	{
		this.alternateArtifactId = alternateArtifactId;
	}

	public String getAlienSiteNumber()
	{
		return alienSiteNumber;
	}

	public void setAlienSiteNumber(String alienSiteNumber)
	{
		this.alienSiteNumber = alienSiteNumber;
	}

	public boolean isStudyImagesHaveAnnotations()
	{
		return studyImagesHaveAnnotations;
	}

	public void setStudyImagesHaveAnnotations(boolean studyImagesHaveAnnotations)
	{
		this.studyImagesHaveAnnotations = studyImagesHaveAnnotations;
	}
}
