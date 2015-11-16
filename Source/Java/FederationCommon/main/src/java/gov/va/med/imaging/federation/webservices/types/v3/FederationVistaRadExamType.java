/**
 * FederationVistaRadExamType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.imaging.federation.webservices.types.v3;

public class FederationVistaRadExamType  implements java.io.Serializable {
    private java.lang.String examId;

    private java.lang.String patientIcn;

    private java.lang.String siteNumber;

    private java.lang.String patientName;

    private gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamStatusType examStatus;

    private java.lang.String modality;

    private java.lang.String cptCode;

    private java.lang.String siteName;

    private java.lang.String siteAbbr;

    private java.lang.String radiologyReport;

    private java.lang.String requisitionReport;

    private java.lang.String presentationState;

    private java.lang.String rawHeader1;

    private java.lang.String rawHeader2;

    private java.lang.String rawValue;

    private gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImagesResponseType examImages;

    public FederationVistaRadExamType() {
    }

    public FederationVistaRadExamType(
           java.lang.String examId,
           java.lang.String patientIcn,
           java.lang.String siteNumber,
           java.lang.String patientName,
           gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamStatusType examStatus,
           java.lang.String modality,
           java.lang.String cptCode,
           java.lang.String siteName,
           java.lang.String siteAbbr,
           java.lang.String radiologyReport,
           java.lang.String requisitionReport,
           java.lang.String presentationState,
           java.lang.String rawHeader1,
           java.lang.String rawHeader2,
           java.lang.String rawValue,
           gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImagesResponseType examImages) {
           this.examId = examId;
           this.patientIcn = patientIcn;
           this.siteNumber = siteNumber;
           this.patientName = patientName;
           this.examStatus = examStatus;
           this.modality = modality;
           this.cptCode = cptCode;
           this.siteName = siteName;
           this.siteAbbr = siteAbbr;
           this.radiologyReport = radiologyReport;
           this.requisitionReport = requisitionReport;
           this.presentationState = presentationState;
           this.rawHeader1 = rawHeader1;
           this.rawHeader2 = rawHeader2;
           this.rawValue = rawValue;
           this.examImages = examImages;
    }


    /**
     * Gets the examId value for this FederationVistaRadExamType.
     * 
     * @return examId
     */
    public java.lang.String getExamId() {
        return examId;
    }


    /**
     * Sets the examId value for this FederationVistaRadExamType.
     * 
     * @param examId
     */
    public void setExamId(java.lang.String examId) {
        this.examId = examId;
    }


    /**
     * Gets the patientIcn value for this FederationVistaRadExamType.
     * 
     * @return patientIcn
     */
    public java.lang.String getPatientIcn() {
        return patientIcn;
    }


    /**
     * Sets the patientIcn value for this FederationVistaRadExamType.
     * 
     * @param patientIcn
     */
    public void setPatientIcn(java.lang.String patientIcn) {
        this.patientIcn = patientIcn;
    }


    /**
     * Gets the siteNumber value for this FederationVistaRadExamType.
     * 
     * @return siteNumber
     */
    public java.lang.String getSiteNumber() {
        return siteNumber;
    }


    /**
     * Sets the siteNumber value for this FederationVistaRadExamType.
     * 
     * @param siteNumber
     */
    public void setSiteNumber(java.lang.String siteNumber) {
        this.siteNumber = siteNumber;
    }


    /**
     * Gets the patientName value for this FederationVistaRadExamType.
     * 
     * @return patientName
     */
    public java.lang.String getPatientName() {
        return patientName;
    }


    /**
     * Sets the patientName value for this FederationVistaRadExamType.
     * 
     * @param patientName
     */
    public void setPatientName(java.lang.String patientName) {
        this.patientName = patientName;
    }


    /**
     * Gets the examStatus value for this FederationVistaRadExamType.
     * 
     * @return examStatus
     */
    public gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamStatusType getExamStatus() {
        return examStatus;
    }


    /**
     * Sets the examStatus value for this FederationVistaRadExamType.
     * 
     * @param examStatus
     */
    public void setExamStatus(gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamStatusType examStatus) {
        this.examStatus = examStatus;
    }


    /**
     * Gets the modality value for this FederationVistaRadExamType.
     * 
     * @return modality
     */
    public java.lang.String getModality() {
        return modality;
    }


    /**
     * Sets the modality value for this FederationVistaRadExamType.
     * 
     * @param modality
     */
    public void setModality(java.lang.String modality) {
        this.modality = modality;
    }


    /**
     * Gets the cptCode value for this FederationVistaRadExamType.
     * 
     * @return cptCode
     */
    public java.lang.String getCptCode() {
        return cptCode;
    }


    /**
     * Sets the cptCode value for this FederationVistaRadExamType.
     * 
     * @param cptCode
     */
    public void setCptCode(java.lang.String cptCode) {
        this.cptCode = cptCode;
    }


    /**
     * Gets the siteName value for this FederationVistaRadExamType.
     * 
     * @return siteName
     */
    public java.lang.String getSiteName() {
        return siteName;
    }


    /**
     * Sets the siteName value for this FederationVistaRadExamType.
     * 
     * @param siteName
     */
    public void setSiteName(java.lang.String siteName) {
        this.siteName = siteName;
    }


    /**
     * Gets the siteAbbr value for this FederationVistaRadExamType.
     * 
     * @return siteAbbr
     */
    public java.lang.String getSiteAbbr() {
        return siteAbbr;
    }


    /**
     * Sets the siteAbbr value for this FederationVistaRadExamType.
     * 
     * @param siteAbbr
     */
    public void setSiteAbbr(java.lang.String siteAbbr) {
        this.siteAbbr = siteAbbr;
    }


    /**
     * Gets the radiologyReport value for this FederationVistaRadExamType.
     * 
     * @return radiologyReport
     */
    public java.lang.String getRadiologyReport() {
        return radiologyReport;
    }


    /**
     * Sets the radiologyReport value for this FederationVistaRadExamType.
     * 
     * @param radiologyReport
     */
    public void setRadiologyReport(java.lang.String radiologyReport) {
        this.radiologyReport = radiologyReport;
    }


    /**
     * Gets the requisitionReport value for this FederationVistaRadExamType.
     * 
     * @return requisitionReport
     */
    public java.lang.String getRequisitionReport() {
        return requisitionReport;
    }


    /**
     * Sets the requisitionReport value for this FederationVistaRadExamType.
     * 
     * @param requisitionReport
     */
    public void setRequisitionReport(java.lang.String requisitionReport) {
        this.requisitionReport = requisitionReport;
    }


    /**
     * Gets the presentationState value for this FederationVistaRadExamType.
     * 
     * @return presentationState
     */
    public java.lang.String getPresentationState() {
        return presentationState;
    }


    /**
     * Sets the presentationState value for this FederationVistaRadExamType.
     * 
     * @param presentationState
     */
    public void setPresentationState(java.lang.String presentationState) {
        this.presentationState = presentationState;
    }


    /**
     * Gets the rawHeader1 value for this FederationVistaRadExamType.
     * 
     * @return rawHeader1
     */
    public java.lang.String getRawHeader1() {
        return rawHeader1;
    }


    /**
     * Sets the rawHeader1 value for this FederationVistaRadExamType.
     * 
     * @param rawHeader1
     */
    public void setRawHeader1(java.lang.String rawHeader1) {
        this.rawHeader1 = rawHeader1;
    }


    /**
     * Gets the rawHeader2 value for this FederationVistaRadExamType.
     * 
     * @return rawHeader2
     */
    public java.lang.String getRawHeader2() {
        return rawHeader2;
    }


    /**
     * Sets the rawHeader2 value for this FederationVistaRadExamType.
     * 
     * @param rawHeader2
     */
    public void setRawHeader2(java.lang.String rawHeader2) {
        this.rawHeader2 = rawHeader2;
    }


    /**
     * Gets the rawValue value for this FederationVistaRadExamType.
     * 
     * @return rawValue
     */
    public java.lang.String getRawValue() {
        return rawValue;
    }


    /**
     * Sets the rawValue value for this FederationVistaRadExamType.
     * 
     * @param rawValue
     */
    public void setRawValue(java.lang.String rawValue) {
        this.rawValue = rawValue;
    }


    /**
     * Gets the examImages value for this FederationVistaRadExamType.
     * 
     * @return examImages
     */
    public gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImagesResponseType getExamImages() {
        return examImages;
    }


    /**
     * Sets the examImages value for this FederationVistaRadExamType.
     * 
     * @param examImages
     */
    public void setExamImages(gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImagesResponseType examImages) {
        this.examImages = examImages;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FederationVistaRadExamType)) return false;
        FederationVistaRadExamType other = (FederationVistaRadExamType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.examId==null && other.getExamId()==null) || 
             (this.examId!=null &&
              this.examId.equals(other.getExamId()))) &&
            ((this.patientIcn==null && other.getPatientIcn()==null) || 
             (this.patientIcn!=null &&
              this.patientIcn.equals(other.getPatientIcn()))) &&
            ((this.siteNumber==null && other.getSiteNumber()==null) || 
             (this.siteNumber!=null &&
              this.siteNumber.equals(other.getSiteNumber()))) &&
            ((this.patientName==null && other.getPatientName()==null) || 
             (this.patientName!=null &&
              this.patientName.equals(other.getPatientName()))) &&
            ((this.examStatus==null && other.getExamStatus()==null) || 
             (this.examStatus!=null &&
              this.examStatus.equals(other.getExamStatus()))) &&
            ((this.modality==null && other.getModality()==null) || 
             (this.modality!=null &&
              this.modality.equals(other.getModality()))) &&
            ((this.cptCode==null && other.getCptCode()==null) || 
             (this.cptCode!=null &&
              this.cptCode.equals(other.getCptCode()))) &&
            ((this.siteName==null && other.getSiteName()==null) || 
             (this.siteName!=null &&
              this.siteName.equals(other.getSiteName()))) &&
            ((this.siteAbbr==null && other.getSiteAbbr()==null) || 
             (this.siteAbbr!=null &&
              this.siteAbbr.equals(other.getSiteAbbr()))) &&
            ((this.radiologyReport==null && other.getRadiologyReport()==null) || 
             (this.radiologyReport!=null &&
              this.radiologyReport.equals(other.getRadiologyReport()))) &&
            ((this.requisitionReport==null && other.getRequisitionReport()==null) || 
             (this.requisitionReport!=null &&
              this.requisitionReport.equals(other.getRequisitionReport()))) &&
            ((this.presentationState==null && other.getPresentationState()==null) || 
             (this.presentationState!=null &&
              this.presentationState.equals(other.getPresentationState()))) &&
            ((this.rawHeader1==null && other.getRawHeader1()==null) || 
             (this.rawHeader1!=null &&
              this.rawHeader1.equals(other.getRawHeader1()))) &&
            ((this.rawHeader2==null && other.getRawHeader2()==null) || 
             (this.rawHeader2!=null &&
              this.rawHeader2.equals(other.getRawHeader2()))) &&
            ((this.rawValue==null && other.getRawValue()==null) || 
             (this.rawValue!=null &&
              this.rawValue.equals(other.getRawValue()))) &&
            ((this.examImages==null && other.getExamImages()==null) || 
             (this.examImages!=null &&
              this.examImages.equals(other.getExamImages())));
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
        if (getExamId() != null) {
            _hashCode += getExamId().hashCode();
        }
        if (getPatientIcn() != null) {
            _hashCode += getPatientIcn().hashCode();
        }
        if (getSiteNumber() != null) {
            _hashCode += getSiteNumber().hashCode();
        }
        if (getPatientName() != null) {
            _hashCode += getPatientName().hashCode();
        }
        if (getExamStatus() != null) {
            _hashCode += getExamStatus().hashCode();
        }
        if (getModality() != null) {
            _hashCode += getModality().hashCode();
        }
        if (getCptCode() != null) {
            _hashCode += getCptCode().hashCode();
        }
        if (getSiteName() != null) {
            _hashCode += getSiteName().hashCode();
        }
        if (getSiteAbbr() != null) {
            _hashCode += getSiteAbbr().hashCode();
        }
        if (getRadiologyReport() != null) {
            _hashCode += getRadiologyReport().hashCode();
        }
        if (getRequisitionReport() != null) {
            _hashCode += getRequisitionReport().hashCode();
        }
        if (getPresentationState() != null) {
            _hashCode += getPresentationState().hashCode();
        }
        if (getRawHeader1() != null) {
            _hashCode += getRawHeader1().hashCode();
        }
        if (getRawHeader2() != null) {
            _hashCode += getRawHeader2().hashCode();
        }
        if (getRawValue() != null) {
            _hashCode += getRawValue().hashCode();
        }
        if (getExamImages() != null) {
            _hashCode += getExamImages().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FederationVistaRadExamType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationVistaRadExamType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("examId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "exam-Id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("patientIcn");
        elemField.setXmlName(new javax.xml.namespace.QName("", "patient-icn"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("siteNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "site-number"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("patientName");
        elemField.setXmlName(new javax.xml.namespace.QName("", "patient-name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("examStatus");
        elemField.setXmlName(new javax.xml.namespace.QName("", "exam-status"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationVistaRadExamStatusType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("modality");
        elemField.setXmlName(new javax.xml.namespace.QName("", "modality"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cptCode");
        elemField.setXmlName(new javax.xml.namespace.QName("", "cpt-code"));
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
        elemField.setFieldName("siteAbbr");
        elemField.setXmlName(new javax.xml.namespace.QName("", "site-abbr"));
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
        elemField.setFieldName("requisitionReport");
        elemField.setXmlName(new javax.xml.namespace.QName("", "requisition-report"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("presentationState");
        elemField.setXmlName(new javax.xml.namespace.QName("", "presentation-state"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rawHeader1");
        elemField.setXmlName(new javax.xml.namespace.QName("", "raw-header-1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rawHeader2");
        elemField.setXmlName(new javax.xml.namespace.QName("", "raw-header-2"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rawValue");
        elemField.setXmlName(new javax.xml.namespace.QName("", "raw-value"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("examImages");
        elemField.setXmlName(new javax.xml.namespace.QName("", "exam-images"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationVistaRadExamImagesResponseType"));
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
