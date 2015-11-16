/**
 * FederationStudyType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.imaging.federation.webservices.types.v3;

public class FederationStudyType  implements java.io.Serializable {
    private java.lang.String studyId;

    private java.lang.String dicomUid;

    private java.lang.String description;

    private java.lang.String procedureDate;

    private java.lang.String procedureDescription;

    private java.lang.String patientIcn;

    private java.lang.String patientName;

    private java.lang.String siteNumber;

    private java.lang.String siteName;

    private java.lang.String siteAbbreviation;

    private int imageCount;

    private int seriesCount;

    private java.lang.String specialtyDescription;

    private java.lang.String radiologyReport;

    private java.lang.String noteTitle;

    private java.lang.String imagePackage;

    private java.lang.String imageType;

    private java.lang.String event;

    private java.lang.String origin;

    private java.lang.String studyPackage;

    private java.lang.String studyClass;

    private java.lang.String studyType;

    private java.lang.String captureDate;

    private java.lang.String capturedBy;

    private gov.va.med.imaging.federation.webservices.types.v3.FederationInstanceType firstImage;

    private java.lang.String rpcResponseMsg;

    private gov.va.med.imaging.federation.webservices.types.v3.FederationStudyTypeComponentSeries componentSeries;

    private gov.va.med.imaging.federation.webservices.types.v3.FederationStudyTypeStudyModalities studyModalities;

    private java.lang.String firstImageIen;

    private gov.va.med.imaging.federation.webservices.types.v3.ObjectOriginType objectOrigin;

    private java.lang.String errorMessage;

    private gov.va.med.imaging.federation.webservices.types.v3.FederationStudyLoadLevelType studyLoadLevel;

    public FederationStudyType() {
    }

    public FederationStudyType(
           java.lang.String studyId,
           java.lang.String dicomUid,
           java.lang.String description,
           java.lang.String procedureDate,
           java.lang.String procedureDescription,
           java.lang.String patientIcn,
           java.lang.String patientName,
           java.lang.String siteNumber,
           java.lang.String siteName,
           java.lang.String siteAbbreviation,
           int imageCount,
           int seriesCount,
           java.lang.String specialtyDescription,
           java.lang.String radiologyReport,
           java.lang.String noteTitle,
           java.lang.String imagePackage,
           java.lang.String imageType,
           java.lang.String event,
           java.lang.String origin,
           java.lang.String studyPackage,
           java.lang.String studyClass,
           java.lang.String studyType,
           java.lang.String captureDate,
           java.lang.String capturedBy,
           gov.va.med.imaging.federation.webservices.types.v3.FederationInstanceType firstImage,
           java.lang.String rpcResponseMsg,
           gov.va.med.imaging.federation.webservices.types.v3.FederationStudyTypeComponentSeries componentSeries,
           gov.va.med.imaging.federation.webservices.types.v3.FederationStudyTypeStudyModalities studyModalities,
           java.lang.String firstImageIen,
           gov.va.med.imaging.federation.webservices.types.v3.ObjectOriginType objectOrigin,
           java.lang.String errorMessage,
           gov.va.med.imaging.federation.webservices.types.v3.FederationStudyLoadLevelType studyLoadLevel) {
           this.studyId = studyId;
           this.dicomUid = dicomUid;
           this.description = description;
           this.procedureDate = procedureDate;
           this.procedureDescription = procedureDescription;
           this.patientIcn = patientIcn;
           this.patientName = patientName;
           this.siteNumber = siteNumber;
           this.siteName = siteName;
           this.siteAbbreviation = siteAbbreviation;
           this.imageCount = imageCount;
           this.seriesCount = seriesCount;
           this.specialtyDescription = specialtyDescription;
           this.radiologyReport = radiologyReport;
           this.noteTitle = noteTitle;
           this.imagePackage = imagePackage;
           this.imageType = imageType;
           this.event = event;
           this.origin = origin;
           this.studyPackage = studyPackage;
           this.studyClass = studyClass;
           this.studyType = studyType;
           this.captureDate = captureDate;
           this.capturedBy = capturedBy;
           this.firstImage = firstImage;
           this.rpcResponseMsg = rpcResponseMsg;
           this.componentSeries = componentSeries;
           this.studyModalities = studyModalities;
           this.firstImageIen = firstImageIen;
           this.objectOrigin = objectOrigin;
           this.errorMessage = errorMessage;
           this.studyLoadLevel = studyLoadLevel;
    }


    /**
     * Gets the studyId value for this FederationStudyType.
     * 
     * @return studyId
     */
    public java.lang.String getStudyId() {
        return studyId;
    }


    /**
     * Sets the studyId value for this FederationStudyType.
     * 
     * @param studyId
     */
    public void setStudyId(java.lang.String studyId) {
        this.studyId = studyId;
    }


    /**
     * Gets the dicomUid value for this FederationStudyType.
     * 
     * @return dicomUid
     */
    public java.lang.String getDicomUid() {
        return dicomUid;
    }


    /**
     * Sets the dicomUid value for this FederationStudyType.
     * 
     * @param dicomUid
     */
    public void setDicomUid(java.lang.String dicomUid) {
        this.dicomUid = dicomUid;
    }


    /**
     * Gets the description value for this FederationStudyType.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this FederationStudyType.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the procedureDate value for this FederationStudyType.
     * 
     * @return procedureDate
     */
    public java.lang.String getProcedureDate() {
        return procedureDate;
    }


    /**
     * Sets the procedureDate value for this FederationStudyType.
     * 
     * @param procedureDate
     */
    public void setProcedureDate(java.lang.String procedureDate) {
        this.procedureDate = procedureDate;
    }


    /**
     * Gets the procedureDescription value for this FederationStudyType.
     * 
     * @return procedureDescription
     */
    public java.lang.String getProcedureDescription() {
        return procedureDescription;
    }


    /**
     * Sets the procedureDescription value for this FederationStudyType.
     * 
     * @param procedureDescription
     */
    public void setProcedureDescription(java.lang.String procedureDescription) {
        this.procedureDescription = procedureDescription;
    }


    /**
     * Gets the patientIcn value for this FederationStudyType.
     * 
     * @return patientIcn
     */
    public java.lang.String getPatientIcn() {
        return patientIcn;
    }


    /**
     * Sets the patientIcn value for this FederationStudyType.
     * 
     * @param patientIcn
     */
    public void setPatientIcn(java.lang.String patientIcn) {
        this.patientIcn = patientIcn;
    }


    /**
     * Gets the patientName value for this FederationStudyType.
     * 
     * @return patientName
     */
    public java.lang.String getPatientName() {
        return patientName;
    }


    /**
     * Sets the patientName value for this FederationStudyType.
     * 
     * @param patientName
     */
    public void setPatientName(java.lang.String patientName) {
        this.patientName = patientName;
    }


    /**
     * Gets the siteNumber value for this FederationStudyType.
     * 
     * @return siteNumber
     */
    public java.lang.String getSiteNumber() {
        return siteNumber;
    }


    /**
     * Sets the siteNumber value for this FederationStudyType.
     * 
     * @param siteNumber
     */
    public void setSiteNumber(java.lang.String siteNumber) {
        this.siteNumber = siteNumber;
    }


    /**
     * Gets the siteName value for this FederationStudyType.
     * 
     * @return siteName
     */
    public java.lang.String getSiteName() {
        return siteName;
    }


    /**
     * Sets the siteName value for this FederationStudyType.
     * 
     * @param siteName
     */
    public void setSiteName(java.lang.String siteName) {
        this.siteName = siteName;
    }


    /**
     * Gets the siteAbbreviation value for this FederationStudyType.
     * 
     * @return siteAbbreviation
     */
    public java.lang.String getSiteAbbreviation() {
        return siteAbbreviation;
    }


    /**
     * Sets the siteAbbreviation value for this FederationStudyType.
     * 
     * @param siteAbbreviation
     */
    public void setSiteAbbreviation(java.lang.String siteAbbreviation) {
        this.siteAbbreviation = siteAbbreviation;
    }


    /**
     * Gets the imageCount value for this FederationStudyType.
     * 
     * @return imageCount
     */
    public int getImageCount() {
        return imageCount;
    }


    /**
     * Sets the imageCount value for this FederationStudyType.
     * 
     * @param imageCount
     */
    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }


    /**
     * Gets the seriesCount value for this FederationStudyType.
     * 
     * @return seriesCount
     */
    public int getSeriesCount() {
        return seriesCount;
    }


    /**
     * Sets the seriesCount value for this FederationStudyType.
     * 
     * @param seriesCount
     */
    public void setSeriesCount(int seriesCount) {
        this.seriesCount = seriesCount;
    }


    /**
     * Gets the specialtyDescription value for this FederationStudyType.
     * 
     * @return specialtyDescription
     */
    public java.lang.String getSpecialtyDescription() {
        return specialtyDescription;
    }


    /**
     * Sets the specialtyDescription value for this FederationStudyType.
     * 
     * @param specialtyDescription
     */
    public void setSpecialtyDescription(java.lang.String specialtyDescription) {
        this.specialtyDescription = specialtyDescription;
    }


    /**
     * Gets the radiologyReport value for this FederationStudyType.
     * 
     * @return radiologyReport
     */
    public java.lang.String getRadiologyReport() {
        return radiologyReport;
    }


    /**
     * Sets the radiologyReport value for this FederationStudyType.
     * 
     * @param radiologyReport
     */
    public void setRadiologyReport(java.lang.String radiologyReport) {
        this.radiologyReport = radiologyReport;
    }


    /**
     * Gets the noteTitle value for this FederationStudyType.
     * 
     * @return noteTitle
     */
    public java.lang.String getNoteTitle() {
        return noteTitle;
    }


    /**
     * Sets the noteTitle value for this FederationStudyType.
     * 
     * @param noteTitle
     */
    public void setNoteTitle(java.lang.String noteTitle) {
        this.noteTitle = noteTitle;
    }


    /**
     * Gets the imagePackage value for this FederationStudyType.
     * 
     * @return imagePackage
     */
    public java.lang.String getImagePackage() {
        return imagePackage;
    }


    /**
     * Sets the imagePackage value for this FederationStudyType.
     * 
     * @param imagePackage
     */
    public void setImagePackage(java.lang.String imagePackage) {
        this.imagePackage = imagePackage;
    }


    /**
     * Gets the imageType value for this FederationStudyType.
     * 
     * @return imageType
     */
    public java.lang.String getImageType() {
        return imageType;
    }


    /**
     * Sets the imageType value for this FederationStudyType.
     * 
     * @param imageType
     */
    public void setImageType(java.lang.String imageType) {
        this.imageType = imageType;
    }


    /**
     * Gets the event value for this FederationStudyType.
     * 
     * @return event
     */
    public java.lang.String getEvent() {
        return event;
    }


    /**
     * Sets the event value for this FederationStudyType.
     * 
     * @param event
     */
    public void setEvent(java.lang.String event) {
        this.event = event;
    }


    /**
     * Gets the origin value for this FederationStudyType.
     * 
     * @return origin
     */
    public java.lang.String getOrigin() {
        return origin;
    }


    /**
     * Sets the origin value for this FederationStudyType.
     * 
     * @param origin
     */
    public void setOrigin(java.lang.String origin) {
        this.origin = origin;
    }


    /**
     * Gets the studyPackage value for this FederationStudyType.
     * 
     * @return studyPackage
     */
    public java.lang.String getStudyPackage() {
        return studyPackage;
    }


    /**
     * Sets the studyPackage value for this FederationStudyType.
     * 
     * @param studyPackage
     */
    public void setStudyPackage(java.lang.String studyPackage) {
        this.studyPackage = studyPackage;
    }


    /**
     * Gets the studyClass value for this FederationStudyType.
     * 
     * @return studyClass
     */
    public java.lang.String getStudyClass() {
        return studyClass;
    }


    /**
     * Sets the studyClass value for this FederationStudyType.
     * 
     * @param studyClass
     */
    public void setStudyClass(java.lang.String studyClass) {
        this.studyClass = studyClass;
    }


    /**
     * Gets the studyType value for this FederationStudyType.
     * 
     * @return studyType
     */
    public java.lang.String getStudyType() {
        return studyType;
    }


    /**
     * Sets the studyType value for this FederationStudyType.
     * 
     * @param studyType
     */
    public void setStudyType(java.lang.String studyType) {
        this.studyType = studyType;
    }


    /**
     * Gets the captureDate value for this FederationStudyType.
     * 
     * @return captureDate
     */
    public java.lang.String getCaptureDate() {
        return captureDate;
    }


    /**
     * Sets the captureDate value for this FederationStudyType.
     * 
     * @param captureDate
     */
    public void setCaptureDate(java.lang.String captureDate) {
        this.captureDate = captureDate;
    }


    /**
     * Gets the capturedBy value for this FederationStudyType.
     * 
     * @return capturedBy
     */
    public java.lang.String getCapturedBy() {
        return capturedBy;
    }


    /**
     * Sets the capturedBy value for this FederationStudyType.
     * 
     * @param capturedBy
     */
    public void setCapturedBy(java.lang.String capturedBy) {
        this.capturedBy = capturedBy;
    }


    /**
     * Gets the firstImage value for this FederationStudyType.
     * 
     * @return firstImage
     */
    public gov.va.med.imaging.federation.webservices.types.v3.FederationInstanceType getFirstImage() {
        return firstImage;
    }


    /**
     * Sets the firstImage value for this FederationStudyType.
     * 
     * @param firstImage
     */
    public void setFirstImage(gov.va.med.imaging.federation.webservices.types.v3.FederationInstanceType firstImage) {
        this.firstImage = firstImage;
    }


    /**
     * Gets the rpcResponseMsg value for this FederationStudyType.
     * 
     * @return rpcResponseMsg
     */
    public java.lang.String getRpcResponseMsg() {
        return rpcResponseMsg;
    }


    /**
     * Sets the rpcResponseMsg value for this FederationStudyType.
     * 
     * @param rpcResponseMsg
     */
    public void setRpcResponseMsg(java.lang.String rpcResponseMsg) {
        this.rpcResponseMsg = rpcResponseMsg;
    }


    /**
     * Gets the componentSeries value for this FederationStudyType.
     * 
     * @return componentSeries
     */
    public gov.va.med.imaging.federation.webservices.types.v3.FederationStudyTypeComponentSeries getComponentSeries() {
        return componentSeries;
    }


    /**
     * Sets the componentSeries value for this FederationStudyType.
     * 
     * @param componentSeries
     */
    public void setComponentSeries(gov.va.med.imaging.federation.webservices.types.v3.FederationStudyTypeComponentSeries componentSeries) {
        this.componentSeries = componentSeries;
    }


    /**
     * Gets the studyModalities value for this FederationStudyType.
     * 
     * @return studyModalities
     */
    public gov.va.med.imaging.federation.webservices.types.v3.FederationStudyTypeStudyModalities getStudyModalities() {
        return studyModalities;
    }


    /**
     * Sets the studyModalities value for this FederationStudyType.
     * 
     * @param studyModalities
     */
    public void setStudyModalities(gov.va.med.imaging.federation.webservices.types.v3.FederationStudyTypeStudyModalities studyModalities) {
        this.studyModalities = studyModalities;
    }


    /**
     * Gets the firstImageIen value for this FederationStudyType.
     * 
     * @return firstImageIen
     */
    public java.lang.String getFirstImageIen() {
        return firstImageIen;
    }


    /**
     * Sets the firstImageIen value for this FederationStudyType.
     * 
     * @param firstImageIen
     */
    public void setFirstImageIen(java.lang.String firstImageIen) {
        this.firstImageIen = firstImageIen;
    }


    /**
     * Gets the objectOrigin value for this FederationStudyType.
     * 
     * @return objectOrigin
     */
    public gov.va.med.imaging.federation.webservices.types.v3.ObjectOriginType getObjectOrigin() {
        return objectOrigin;
    }


    /**
     * Sets the objectOrigin value for this FederationStudyType.
     * 
     * @param objectOrigin
     */
    public void setObjectOrigin(gov.va.med.imaging.federation.webservices.types.v3.ObjectOriginType objectOrigin) {
        this.objectOrigin = objectOrigin;
    }


    /**
     * Gets the errorMessage value for this FederationStudyType.
     * 
     * @return errorMessage
     */
    public java.lang.String getErrorMessage() {
        return errorMessage;
    }


    /**
     * Sets the errorMessage value for this FederationStudyType.
     * 
     * @param errorMessage
     */
    public void setErrorMessage(java.lang.String errorMessage) {
        this.errorMessage = errorMessage;
    }


    /**
     * Gets the studyLoadLevel value for this FederationStudyType.
     * 
     * @return studyLoadLevel
     */
    public gov.va.med.imaging.federation.webservices.types.v3.FederationStudyLoadLevelType getStudyLoadLevel() {
        return studyLoadLevel;
    }


    /**
     * Sets the studyLoadLevel value for this FederationStudyType.
     * 
     * @param studyLoadLevel
     */
    public void setStudyLoadLevel(gov.va.med.imaging.federation.webservices.types.v3.FederationStudyLoadLevelType studyLoadLevel) {
        this.studyLoadLevel = studyLoadLevel;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FederationStudyType)) return false;
        FederationStudyType other = (FederationStudyType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.studyId==null && other.getStudyId()==null) || 
             (this.studyId!=null &&
              this.studyId.equals(other.getStudyId()))) &&
            ((this.dicomUid==null && other.getDicomUid()==null) || 
             (this.dicomUid!=null &&
              this.dicomUid.equals(other.getDicomUid()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.procedureDate==null && other.getProcedureDate()==null) || 
             (this.procedureDate!=null &&
              this.procedureDate.equals(other.getProcedureDate()))) &&
            ((this.procedureDescription==null && other.getProcedureDescription()==null) || 
             (this.procedureDescription!=null &&
              this.procedureDescription.equals(other.getProcedureDescription()))) &&
            ((this.patientIcn==null && other.getPatientIcn()==null) || 
             (this.patientIcn!=null &&
              this.patientIcn.equals(other.getPatientIcn()))) &&
            ((this.patientName==null && other.getPatientName()==null) || 
             (this.patientName!=null &&
              this.patientName.equals(other.getPatientName()))) &&
            ((this.siteNumber==null && other.getSiteNumber()==null) || 
             (this.siteNumber!=null &&
              this.siteNumber.equals(other.getSiteNumber()))) &&
            ((this.siteName==null && other.getSiteName()==null) || 
             (this.siteName!=null &&
              this.siteName.equals(other.getSiteName()))) &&
            ((this.siteAbbreviation==null && other.getSiteAbbreviation()==null) || 
             (this.siteAbbreviation!=null &&
              this.siteAbbreviation.equals(other.getSiteAbbreviation()))) &&
            this.imageCount == other.getImageCount() &&
            this.seriesCount == other.getSeriesCount() &&
            ((this.specialtyDescription==null && other.getSpecialtyDescription()==null) || 
             (this.specialtyDescription!=null &&
              this.specialtyDescription.equals(other.getSpecialtyDescription()))) &&
            ((this.radiologyReport==null && other.getRadiologyReport()==null) || 
             (this.radiologyReport!=null &&
              this.radiologyReport.equals(other.getRadiologyReport()))) &&
            ((this.noteTitle==null && other.getNoteTitle()==null) || 
             (this.noteTitle!=null &&
              this.noteTitle.equals(other.getNoteTitle()))) &&
            ((this.imagePackage==null && other.getImagePackage()==null) || 
             (this.imagePackage!=null &&
              this.imagePackage.equals(other.getImagePackage()))) &&
            ((this.imageType==null && other.getImageType()==null) || 
             (this.imageType!=null &&
              this.imageType.equals(other.getImageType()))) &&
            ((this.event==null && other.getEvent()==null) || 
             (this.event!=null &&
              this.event.equals(other.getEvent()))) &&
            ((this.origin==null && other.getOrigin()==null) || 
             (this.origin!=null &&
              this.origin.equals(other.getOrigin()))) &&
            ((this.studyPackage==null && other.getStudyPackage()==null) || 
             (this.studyPackage!=null &&
              this.studyPackage.equals(other.getStudyPackage()))) &&
            ((this.studyClass==null && other.getStudyClass()==null) || 
             (this.studyClass!=null &&
              this.studyClass.equals(other.getStudyClass()))) &&
            ((this.studyType==null && other.getStudyType()==null) || 
             (this.studyType!=null &&
              this.studyType.equals(other.getStudyType()))) &&
            ((this.captureDate==null && other.getCaptureDate()==null) || 
             (this.captureDate!=null &&
              this.captureDate.equals(other.getCaptureDate()))) &&
            ((this.capturedBy==null && other.getCapturedBy()==null) || 
             (this.capturedBy!=null &&
              this.capturedBy.equals(other.getCapturedBy()))) &&
            ((this.firstImage==null && other.getFirstImage()==null) || 
             (this.firstImage!=null &&
              this.firstImage.equals(other.getFirstImage()))) &&
            ((this.rpcResponseMsg==null && other.getRpcResponseMsg()==null) || 
             (this.rpcResponseMsg!=null &&
              this.rpcResponseMsg.equals(other.getRpcResponseMsg()))) &&
            ((this.componentSeries==null && other.getComponentSeries()==null) || 
             (this.componentSeries!=null &&
              this.componentSeries.equals(other.getComponentSeries()))) &&
            ((this.studyModalities==null && other.getStudyModalities()==null) || 
             (this.studyModalities!=null &&
              this.studyModalities.equals(other.getStudyModalities()))) &&
            ((this.firstImageIen==null && other.getFirstImageIen()==null) || 
             (this.firstImageIen!=null &&
              this.firstImageIen.equals(other.getFirstImageIen()))) &&
            ((this.objectOrigin==null && other.getObjectOrigin()==null) || 
             (this.objectOrigin!=null &&
              this.objectOrigin.equals(other.getObjectOrigin()))) &&
            ((this.errorMessage==null && other.getErrorMessage()==null) || 
             (this.errorMessage!=null &&
              this.errorMessage.equals(other.getErrorMessage()))) &&
            ((this.studyLoadLevel==null && other.getStudyLoadLevel()==null) || 
             (this.studyLoadLevel!=null &&
              this.studyLoadLevel.equals(other.getStudyLoadLevel())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getStudyId() != null) {
            _hashCode += getStudyId().hashCode();
        }
        if (getDicomUid() != null) {
            _hashCode += getDicomUid().hashCode();
        }
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getProcedureDate() != null) {
            _hashCode += getProcedureDate().hashCode();
        }
        if (getProcedureDescription() != null) {
            _hashCode += getProcedureDescription().hashCode();
        }
        if (getPatientIcn() != null) {
            _hashCode += getPatientIcn().hashCode();
        }
        if (getPatientName() != null) {
            _hashCode += getPatientName().hashCode();
        }
        if (getSiteNumber() != null) {
            _hashCode += getSiteNumber().hashCode();
        }
        if (getSiteName() != null) {
            _hashCode += getSiteName().hashCode();
        }
        if (getSiteAbbreviation() != null) {
            _hashCode += getSiteAbbreviation().hashCode();
        }
        _hashCode += getImageCount();
        _hashCode += getSeriesCount();
        if (getSpecialtyDescription() != null) {
            _hashCode += getSpecialtyDescription().hashCode();
        }
        if (getRadiologyReport() != null) {
            _hashCode += getRadiologyReport().hashCode();
        }
        if (getNoteTitle() != null) {
            _hashCode += getNoteTitle().hashCode();
        }
        if (getImagePackage() != null) {
            _hashCode += getImagePackage().hashCode();
        }
        if (getImageType() != null) {
            _hashCode += getImageType().hashCode();
        }
        if (getEvent() != null) {
            _hashCode += getEvent().hashCode();
        }
        if (getOrigin() != null) {
            _hashCode += getOrigin().hashCode();
        }
        if (getStudyPackage() != null) {
            _hashCode += getStudyPackage().hashCode();
        }
        if (getStudyClass() != null) {
            _hashCode += getStudyClass().hashCode();
        }
        if (getStudyType() != null) {
            _hashCode += getStudyType().hashCode();
        }
        if (getCaptureDate() != null) {
            _hashCode += getCaptureDate().hashCode();
        }
        if (getCapturedBy() != null) {
            _hashCode += getCapturedBy().hashCode();
        }
        if (getFirstImage() != null) {
            _hashCode += getFirstImage().hashCode();
        }
        if (getRpcResponseMsg() != null) {
            _hashCode += getRpcResponseMsg().hashCode();
        }
        if (getComponentSeries() != null) {
            _hashCode += getComponentSeries().hashCode();
        }
        if (getStudyModalities() != null) {
            _hashCode += getStudyModalities().hashCode();
        }
        if (getFirstImageIen() != null) {
            _hashCode += getFirstImageIen().hashCode();
        }
        if (getObjectOrigin() != null) {
            _hashCode += getObjectOrigin().hashCode();
        }
        if (getErrorMessage() != null) {
            _hashCode += getErrorMessage().hashCode();
        }
        if (getStudyLoadLevel() != null) {
            _hashCode += getStudyLoadLevel().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FederationStudyType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationStudyType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("studyId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "study-id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dicomUid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dicom-uid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("", "description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("procedureDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "procedure-date"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("procedureDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("", "procedure-description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("patientIcn");
        elemField.setXmlName(new javax.xml.namespace.QName("", "patient-icn"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("patientName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "patient-name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("siteNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "site-number"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("siteName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "site-name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("siteAbbreviation");
        elemField.setXmlName(new javax.xml.namespace.QName("", "site-abbreviation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("imageCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "image-count"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("seriesCount");
        elemField.setXmlName(new javax.xml.namespace.QName("", "series-count"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("specialtyDescription");
        elemField.setXmlName(new javax.xml.namespace.QName("", "specialty-description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("radiologyReport");
        elemField.setXmlName(new javax.xml.namespace.QName("", "radiology-report"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("noteTitle");
        elemField.setXmlName(new javax.xml.namespace.QName("", "note-title"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("imagePackage");
        elemField.setXmlName(new javax.xml.namespace.QName("", "image-package"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("imageType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "image-type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("event");
        elemField.setXmlName(new javax.xml.namespace.QName("", "event"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("origin");
        elemField.setXmlName(new javax.xml.namespace.QName("", "origin"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("studyPackage");
        elemField.setXmlName(new javax.xml.namespace.QName("", "study-package"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("studyClass");
        elemField.setXmlName(new javax.xml.namespace.QName("", "study-class"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("studyType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "study-type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("captureDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "capture-date"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("capturedBy");
        elemField.setXmlName(new javax.xml.namespace.QName("", "captured-by"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("firstImage");
        elemField.setXmlName(new javax.xml.namespace.QName("", "first-image"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationInstanceType"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rpcResponseMsg");
        elemField.setXmlName(new javax.xml.namespace.QName("", "rpcResponseMsg"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("componentSeries");
        elemField.setXmlName(new javax.xml.namespace.QName("", "component-series"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", ">FederationStudyType>component-series"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("studyModalities");
        elemField.setXmlName(new javax.xml.namespace.QName("", "study-modalities"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", ">FederationStudyType>study-modalities"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("firstImageIen");
        elemField.setXmlName(new javax.xml.namespace.QName("", "first-image-ien"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("objectOrigin");
        elemField.setXmlName(new javax.xml.namespace.QName("", "object-origin"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "ObjectOriginType"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errorMessage");
        elemField.setXmlName(new javax.xml.namespace.QName("", "errorMessage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("studyLoadLevel");
        elemField.setXmlName(new javax.xml.namespace.QName("", "study-load-level"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationStudyLoadLevelType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
