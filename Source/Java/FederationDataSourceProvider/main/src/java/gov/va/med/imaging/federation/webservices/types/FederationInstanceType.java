/**
 * FederationInstanceType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.imaging.federation.webservices.types;

public class FederationInstanceType  implements java.io.Serializable {
    private java.lang.String imageId;

    private java.lang.String dicomUid;

    private java.lang.Integer imageNumber;

    private java.lang.String description;

    private java.lang.String procedureDate;

    private java.lang.String procedure;

    private java.lang.String dicomSequenceNumberForDisplay;

    private java.lang.String dicomImageNumberForDisplay;

    private java.lang.String patientIcn;

    private java.lang.String patientName;

    private java.lang.String siteNumber;

    private java.lang.String siteAbbr;

    private java.math.BigInteger imageType;

    private java.lang.String absLocation;

    private java.lang.String fullLocation;

    private java.lang.String imageClass;

    private java.lang.String fullImageFilename;

    private java.lang.String absImageFilename;

    private java.lang.String bigImageFilename;

    private java.lang.String qaMessage;

    private java.lang.String imageModality;

    private java.lang.String studyId;

    private java.lang.String groupId;

    private gov.va.med.imaging.federation.webservices.types.ObjectOriginType objectOrigin;

    private java.lang.String errorMessage;

    public FederationInstanceType() {
    }

    public FederationInstanceType(
           java.lang.String imageId,
           java.lang.String dicomUid,
           java.lang.Integer imageNumber,
           java.lang.String description,
           java.lang.String procedureDate,
           java.lang.String procedure,
           java.lang.String dicomSequenceNumberForDisplay,
           java.lang.String dicomImageNumberForDisplay,
           java.lang.String patientIcn,
           java.lang.String patientName,
           java.lang.String siteNumber,
           java.lang.String siteAbbr,
           java.math.BigInteger imageType,
           java.lang.String absLocation,
           java.lang.String fullLocation,
           java.lang.String imageClass,
           java.lang.String fullImageFilename,
           java.lang.String absImageFilename,
           java.lang.String bigImageFilename,
           java.lang.String qaMessage,
           java.lang.String imageModality,
           java.lang.String studyId,
           java.lang.String groupId,
           gov.va.med.imaging.federation.webservices.types.ObjectOriginType objectOrigin,
           java.lang.String errorMessage) {
           this.imageId = imageId;
           this.dicomUid = dicomUid;
           this.imageNumber = imageNumber;
           this.description = description;
           this.procedureDate = procedureDate;
           this.procedure = procedure;
           this.dicomSequenceNumberForDisplay = dicomSequenceNumberForDisplay;
           this.dicomImageNumberForDisplay = dicomImageNumberForDisplay;
           this.patientIcn = patientIcn;
           this.patientName = patientName;
           this.siteNumber = siteNumber;
           this.siteAbbr = siteAbbr;
           this.imageType = imageType;
           this.absLocation = absLocation;
           this.fullLocation = fullLocation;
           this.imageClass = imageClass;
           this.fullImageFilename = fullImageFilename;
           this.absImageFilename = absImageFilename;
           this.bigImageFilename = bigImageFilename;
           this.qaMessage = qaMessage;
           this.imageModality = imageModality;
           this.studyId = studyId;
           this.groupId = groupId;
           this.objectOrigin = objectOrigin;
           this.errorMessage = errorMessage;
    }


    /**
     * Gets the imageId value for this FederationInstanceType.
     * 
     * @return imageId
     */
    public java.lang.String getImageId() {
        return imageId;
    }


    /**
     * Sets the imageId value for this FederationInstanceType.
     * 
     * @param imageId
     */
    public void setImageId(java.lang.String imageId) {
        this.imageId = imageId;
    }


    /**
     * Gets the dicomUid value for this FederationInstanceType.
     * 
     * @return dicomUid
     */
    public java.lang.String getDicomUid() {
        return dicomUid;
    }


    /**
     * Sets the dicomUid value for this FederationInstanceType.
     * 
     * @param dicomUid
     */
    public void setDicomUid(java.lang.String dicomUid) {
        this.dicomUid = dicomUid;
    }


    /**
     * Gets the imageNumber value for this FederationInstanceType.
     * 
     * @return imageNumber
     */
    public java.lang.Integer getImageNumber() {
        return imageNumber;
    }


    /**
     * Sets the imageNumber value for this FederationInstanceType.
     * 
     * @param imageNumber
     */
    public void setImageNumber(java.lang.Integer imageNumber) {
        this.imageNumber = imageNumber;
    }


    /**
     * Gets the description value for this FederationInstanceType.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this FederationInstanceType.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the procedureDate value for this FederationInstanceType.
     * 
     * @return procedureDate
     */
    public java.lang.String getProcedureDate() {
        return procedureDate;
    }


    /**
     * Sets the procedureDate value for this FederationInstanceType.
     * 
     * @param procedureDate
     */
    public void setProcedureDate(java.lang.String procedureDate) {
        this.procedureDate = procedureDate;
    }


    /**
     * Gets the procedure value for this FederationInstanceType.
     * 
     * @return procedure
     */
    public java.lang.String getProcedure() {
        return procedure;
    }


    /**
     * Sets the procedure value for this FederationInstanceType.
     * 
     * @param procedure
     */
    public void setProcedure(java.lang.String procedure) {
        this.procedure = procedure;
    }


    /**
     * Gets the dicomSequenceNumberForDisplay value for this FederationInstanceType.
     * 
     * @return dicomSequenceNumberForDisplay
     */
    public java.lang.String getDicomSequenceNumberForDisplay() {
        return dicomSequenceNumberForDisplay;
    }


    /**
     * Sets the dicomSequenceNumberForDisplay value for this FederationInstanceType.
     * 
     * @param dicomSequenceNumberForDisplay
     */
    public void setDicomSequenceNumberForDisplay(java.lang.String dicomSequenceNumberForDisplay) {
        this.dicomSequenceNumberForDisplay = dicomSequenceNumberForDisplay;
    }


    /**
     * Gets the dicomImageNumberForDisplay value for this FederationInstanceType.
     * 
     * @return dicomImageNumberForDisplay
     */
    public java.lang.String getDicomImageNumberForDisplay() {
        return dicomImageNumberForDisplay;
    }


    /**
     * Sets the dicomImageNumberForDisplay value for this FederationInstanceType.
     * 
     * @param dicomImageNumberForDisplay
     */
    public void setDicomImageNumberForDisplay(java.lang.String dicomImageNumberForDisplay) {
        this.dicomImageNumberForDisplay = dicomImageNumberForDisplay;
    }


    /**
     * Gets the patientIcn value for this FederationInstanceType.
     * 
     * @return patientIcn
     */
    public java.lang.String getPatientIcn() {
        return patientIcn;
    }


    /**
     * Sets the patientIcn value for this FederationInstanceType.
     * 
     * @param patientIcn
     */
    public void setPatientIcn(java.lang.String patientIcn) {
        this.patientIcn = patientIcn;
    }


    /**
     * Gets the patientName value for this FederationInstanceType.
     * 
     * @return patientName
     */
    public java.lang.String getPatientName() {
        return patientName;
    }


    /**
     * Sets the patientName value for this FederationInstanceType.
     * 
     * @param patientName
     */
    public void setPatientName(java.lang.String patientName) {
        this.patientName = patientName;
    }


    /**
     * Gets the siteNumber value for this FederationInstanceType.
     * 
     * @return siteNumber
     */
    public java.lang.String getSiteNumber() {
        return siteNumber;
    }


    /**
     * Sets the siteNumber value for this FederationInstanceType.
     * 
     * @param siteNumber
     */
    public void setSiteNumber(java.lang.String siteNumber) {
        this.siteNumber = siteNumber;
    }


    /**
     * Gets the siteAbbr value for this FederationInstanceType.
     * 
     * @return siteAbbr
     */
    public java.lang.String getSiteAbbr() {
        return siteAbbr;
    }


    /**
     * Sets the siteAbbr value for this FederationInstanceType.
     * 
     * @param siteAbbr
     */
    public void setSiteAbbr(java.lang.String siteAbbr) {
        this.siteAbbr = siteAbbr;
    }


    /**
     * Gets the imageType value for this FederationInstanceType.
     * 
     * @return imageType
     */
    public java.math.BigInteger getImageType() {
        return imageType;
    }


    /**
     * Sets the imageType value for this FederationInstanceType.
     * 
     * @param imageType
     */
    public void setImageType(java.math.BigInteger imageType) {
        this.imageType = imageType;
    }


    /**
     * Gets the absLocation value for this FederationInstanceType.
     * 
     * @return absLocation
     */
    public java.lang.String getAbsLocation() {
        return absLocation;
    }


    /**
     * Sets the absLocation value for this FederationInstanceType.
     * 
     * @param absLocation
     */
    public void setAbsLocation(java.lang.String absLocation) {
        this.absLocation = absLocation;
    }


    /**
     * Gets the fullLocation value for this FederationInstanceType.
     * 
     * @return fullLocation
     */
    public java.lang.String getFullLocation() {
        return fullLocation;
    }


    /**
     * Sets the fullLocation value for this FederationInstanceType.
     * 
     * @param fullLocation
     */
    public void setFullLocation(java.lang.String fullLocation) {
        this.fullLocation = fullLocation;
    }


    /**
     * Gets the imageClass value for this FederationInstanceType.
     * 
     * @return imageClass
     */
    public java.lang.String getImageClass() {
        return imageClass;
    }


    /**
     * Sets the imageClass value for this FederationInstanceType.
     * 
     * @param imageClass
     */
    public void setImageClass(java.lang.String imageClass) {
        this.imageClass = imageClass;
    }


    /**
     * Gets the fullImageFilename value for this FederationInstanceType.
     * 
     * @return fullImageFilename
     */
    public java.lang.String getFullImageFilename() {
        return fullImageFilename;
    }


    /**
     * Sets the fullImageFilename value for this FederationInstanceType.
     * 
     * @param fullImageFilename
     */
    public void setFullImageFilename(java.lang.String fullImageFilename) {
        this.fullImageFilename = fullImageFilename;
    }


    /**
     * Gets the absImageFilename value for this FederationInstanceType.
     * 
     * @return absImageFilename
     */
    public java.lang.String getAbsImageFilename() {
        return absImageFilename;
    }


    /**
     * Sets the absImageFilename value for this FederationInstanceType.
     * 
     * @param absImageFilename
     */
    public void setAbsImageFilename(java.lang.String absImageFilename) {
        this.absImageFilename = absImageFilename;
    }


    /**
     * Gets the bigImageFilename value for this FederationInstanceType.
     * 
     * @return bigImageFilename
     */
    public java.lang.String getBigImageFilename() {
        return bigImageFilename;
    }


    /**
     * Sets the bigImageFilename value for this FederationInstanceType.
     * 
     * @param bigImageFilename
     */
    public void setBigImageFilename(java.lang.String bigImageFilename) {
        this.bigImageFilename = bigImageFilename;
    }


    /**
     * Gets the qaMessage value for this FederationInstanceType.
     * 
     * @return qaMessage
     */
    public java.lang.String getQaMessage() {
        return qaMessage;
    }


    /**
     * Sets the qaMessage value for this FederationInstanceType.
     * 
     * @param qaMessage
     */
    public void setQaMessage(java.lang.String qaMessage) {
        this.qaMessage = qaMessage;
    }


    /**
     * Gets the imageModality value for this FederationInstanceType.
     * 
     * @return imageModality
     */
    public java.lang.String getImageModality() {
        return imageModality;
    }


    /**
     * Sets the imageModality value for this FederationInstanceType.
     * 
     * @param imageModality
     */
    public void setImageModality(java.lang.String imageModality) {
        this.imageModality = imageModality;
    }


    /**
     * Gets the studyId value for this FederationInstanceType.
     * 
     * @return studyId
     */
    public java.lang.String getStudyId() {
        return studyId;
    }


    /**
     * Sets the studyId value for this FederationInstanceType.
     * 
     * @param studyId
     */
    public void setStudyId(java.lang.String studyId) {
        this.studyId = studyId;
    }


    /**
     * Gets the groupId value for this FederationInstanceType.
     * 
     * @return groupId
     */
    public java.lang.String getGroupId() {
        return groupId;
    }


    /**
     * Sets the groupId value for this FederationInstanceType.
     * 
     * @param groupId
     */
    public void setGroupId(java.lang.String groupId) {
        this.groupId = groupId;
    }


    /**
     * Gets the objectOrigin value for this FederationInstanceType.
     * 
     * @return objectOrigin
     */
    public gov.va.med.imaging.federation.webservices.types.ObjectOriginType getObjectOrigin() {
        return objectOrigin;
    }


    /**
     * Sets the objectOrigin value for this FederationInstanceType.
     * 
     * @param objectOrigin
     */
    public void setObjectOrigin(gov.va.med.imaging.federation.webservices.types.ObjectOriginType objectOrigin) {
        this.objectOrigin = objectOrigin;
    }


    /**
     * Gets the errorMessage value for this FederationInstanceType.
     * 
     * @return errorMessage
     */
    public java.lang.String getErrorMessage() {
        return errorMessage;
    }


    /**
     * Sets the errorMessage value for this FederationInstanceType.
     * 
     * @param errorMessage
     */
    public void setErrorMessage(java.lang.String errorMessage) {
        this.errorMessage = errorMessage;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FederationInstanceType)) return false;
        FederationInstanceType other = (FederationInstanceType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.imageId==null && other.getImageId()==null) || 
             (this.imageId!=null &&
              this.imageId.equals(other.getImageId()))) &&
            ((this.dicomUid==null && other.getDicomUid()==null) || 
             (this.dicomUid!=null &&
              this.dicomUid.equals(other.getDicomUid()))) &&
            ((this.imageNumber==null && other.getImageNumber()==null) || 
             (this.imageNumber!=null &&
              this.imageNumber.equals(other.getImageNumber()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.procedureDate==null && other.getProcedureDate()==null) || 
             (this.procedureDate!=null &&
              this.procedureDate.equals(other.getProcedureDate()))) &&
            ((this.procedure==null && other.getProcedure()==null) || 
             (this.procedure!=null &&
              this.procedure.equals(other.getProcedure()))) &&
            ((this.dicomSequenceNumberForDisplay==null && other.getDicomSequenceNumberForDisplay()==null) || 
             (this.dicomSequenceNumberForDisplay!=null &&
              this.dicomSequenceNumberForDisplay.equals(other.getDicomSequenceNumberForDisplay()))) &&
            ((this.dicomImageNumberForDisplay==null && other.getDicomImageNumberForDisplay()==null) || 
             (this.dicomImageNumberForDisplay!=null &&
              this.dicomImageNumberForDisplay.equals(other.getDicomImageNumberForDisplay()))) &&
            ((this.patientIcn==null && other.getPatientIcn()==null) || 
             (this.patientIcn!=null &&
              this.patientIcn.equals(other.getPatientIcn()))) &&
            ((this.patientName==null && other.getPatientName()==null) || 
             (this.patientName!=null &&
              this.patientName.equals(other.getPatientName()))) &&
            ((this.siteNumber==null && other.getSiteNumber()==null) || 
             (this.siteNumber!=null &&
              this.siteNumber.equals(other.getSiteNumber()))) &&
            ((this.siteAbbr==null && other.getSiteAbbr()==null) || 
             (this.siteAbbr!=null &&
              this.siteAbbr.equals(other.getSiteAbbr()))) &&
            ((this.imageType==null && other.getImageType()==null) || 
             (this.imageType!=null &&
              this.imageType.equals(other.getImageType()))) &&
            ((this.absLocation==null && other.getAbsLocation()==null) || 
             (this.absLocation!=null &&
              this.absLocation.equals(other.getAbsLocation()))) &&
            ((this.fullLocation==null && other.getFullLocation()==null) || 
             (this.fullLocation!=null &&
              this.fullLocation.equals(other.getFullLocation()))) &&
            ((this.imageClass==null && other.getImageClass()==null) || 
             (this.imageClass!=null &&
              this.imageClass.equals(other.getImageClass()))) &&
            ((this.fullImageFilename==null && other.getFullImageFilename()==null) || 
             (this.fullImageFilename!=null &&
              this.fullImageFilename.equals(other.getFullImageFilename()))) &&
            ((this.absImageFilename==null && other.getAbsImageFilename()==null) || 
             (this.absImageFilename!=null &&
              this.absImageFilename.equals(other.getAbsImageFilename()))) &&
            ((this.bigImageFilename==null && other.getBigImageFilename()==null) || 
             (this.bigImageFilename!=null &&
              this.bigImageFilename.equals(other.getBigImageFilename()))) &&
            ((this.qaMessage==null && other.getQaMessage()==null) || 
             (this.qaMessage!=null &&
              this.qaMessage.equals(other.getQaMessage()))) &&
            ((this.imageModality==null && other.getImageModality()==null) || 
             (this.imageModality!=null &&
              this.imageModality.equals(other.getImageModality()))) &&
            ((this.studyId==null && other.getStudyId()==null) || 
             (this.studyId!=null &&
              this.studyId.equals(other.getStudyId()))) &&
            ((this.groupId==null && other.getGroupId()==null) || 
             (this.groupId!=null &&
              this.groupId.equals(other.getGroupId()))) &&
            ((this.objectOrigin==null && other.getObjectOrigin()==null) || 
             (this.objectOrigin!=null &&
              this.objectOrigin.equals(other.getObjectOrigin()))) &&
            ((this.errorMessage==null && other.getErrorMessage()==null) || 
             (this.errorMessage!=null &&
              this.errorMessage.equals(other.getErrorMessage())));
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
        if (getImageId() != null) {
            _hashCode += getImageId().hashCode();
        }
        if (getDicomUid() != null) {
            _hashCode += getDicomUid().hashCode();
        }
        if (getImageNumber() != null) {
            _hashCode += getImageNumber().hashCode();
        }
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getProcedureDate() != null) {
            _hashCode += getProcedureDate().hashCode();
        }
        if (getProcedure() != null) {
            _hashCode += getProcedure().hashCode();
        }
        if (getDicomSequenceNumberForDisplay() != null) {
            _hashCode += getDicomSequenceNumberForDisplay().hashCode();
        }
        if (getDicomImageNumberForDisplay() != null) {
            _hashCode += getDicomImageNumberForDisplay().hashCode();
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
        if (getSiteAbbr() != null) {
            _hashCode += getSiteAbbr().hashCode();
        }
        if (getImageType() != null) {
            _hashCode += getImageType().hashCode();
        }
        if (getAbsLocation() != null) {
            _hashCode += getAbsLocation().hashCode();
        }
        if (getFullLocation() != null) {
            _hashCode += getFullLocation().hashCode();
        }
        if (getImageClass() != null) {
            _hashCode += getImageClass().hashCode();
        }
        if (getFullImageFilename() != null) {
            _hashCode += getFullImageFilename().hashCode();
        }
        if (getAbsImageFilename() != null) {
            _hashCode += getAbsImageFilename().hashCode();
        }
        if (getBigImageFilename() != null) {
            _hashCode += getBigImageFilename().hashCode();
        }
        if (getQaMessage() != null) {
            _hashCode += getQaMessage().hashCode();
        }
        if (getImageModality() != null) {
            _hashCode += getImageModality().hashCode();
        }
        if (getStudyId() != null) {
            _hashCode += getStudyId().hashCode();
        }
        if (getGroupId() != null) {
            _hashCode += getGroupId().hashCode();
        }
        if (getObjectOrigin() != null) {
            _hashCode += getObjectOrigin().hashCode();
        }
        if (getErrorMessage() != null) {
            _hashCode += getErrorMessage().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FederationInstanceType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:types.webservices.federation.imaging.med.va.gov", "FederationInstanceType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("imageId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "image-id"));
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
        elemField.setFieldName("imageNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "image-number"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("procedure");
        elemField.setXmlName(new javax.xml.namespace.QName("", "procedure"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dicomSequenceNumberForDisplay");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dicom-sequence-number-for-display"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dicomImageNumberForDisplay");
        elemField.setXmlName(new javax.xml.namespace.QName("", "dicom-image-number-for-display"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("patientIcn");
        elemField.setXmlName(new javax.xml.namespace.QName("", "patient-icn"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("patientName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "patient-name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("siteNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "site-number"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("siteAbbr");
        elemField.setXmlName(new javax.xml.namespace.QName("", "site-abbr"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("imageType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "image-type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("absLocation");
        elemField.setXmlName(new javax.xml.namespace.QName("", "abs-location"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fullLocation");
        elemField.setXmlName(new javax.xml.namespace.QName("", "full-location"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("imageClass");
        elemField.setXmlName(new javax.xml.namespace.QName("", "image-class"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fullImageFilename");
        elemField.setXmlName(new javax.xml.namespace.QName("", "full-image-filename"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("absImageFilename");
        elemField.setXmlName(new javax.xml.namespace.QName("", "abs-image-filename"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bigImageFilename");
        elemField.setXmlName(new javax.xml.namespace.QName("", "big-image-filename"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("qaMessage");
        elemField.setXmlName(new javax.xml.namespace.QName("", "qaMessage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("imageModality");
        elemField.setXmlName(new javax.xml.namespace.QName("", "image-modality"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("studyId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "study-id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("groupId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "group-id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("objectOrigin");
        elemField.setXmlName(new javax.xml.namespace.QName("", "object-origin"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:types.webservices.federation.imaging.med.va.gov", "ObjectOriginType"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errorMessage");
        elemField.setXmlName(new javax.xml.namespace.QName("", "errorMessage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
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
