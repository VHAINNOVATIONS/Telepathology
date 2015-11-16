/**
 * FederationImageAccessLogEventType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.imaging.federation.webservices.types;

public class FederationImageAccessLogEventType  implements java.io.Serializable {
    private java.lang.String imageId;

    private java.lang.String patientIcn;

    private java.lang.String reason;

    private java.lang.String siteNumber;

    private java.lang.String userSiteNumber;

    private gov.va.med.imaging.federation.webservices.types.FederationImageAccessLogEventTypeEventType eventType;

    public FederationImageAccessLogEventType() {
    }

    public FederationImageAccessLogEventType(
           java.lang.String imageId,
           java.lang.String patientIcn,
           java.lang.String reason,
           java.lang.String siteNumber,
           java.lang.String userSiteNumber,
           gov.va.med.imaging.federation.webservices.types.FederationImageAccessLogEventTypeEventType eventType) {
           this.imageId = imageId;
           this.patientIcn = patientIcn;
           this.reason = reason;
           this.siteNumber = siteNumber;
           this.userSiteNumber = userSiteNumber;
           this.eventType = eventType;
    }


    /**
     * Gets the imageId value for this FederationImageAccessLogEventType.
     * 
     * @return imageId
     */
    public java.lang.String getImageId() {
        return imageId;
    }


    /**
     * Sets the imageId value for this FederationImageAccessLogEventType.
     * 
     * @param imageId
     */
    public void setImageId(java.lang.String imageId) {
        this.imageId = imageId;
    }


    /**
     * Gets the patientIcn value for this FederationImageAccessLogEventType.
     * 
     * @return patientIcn
     */
    public java.lang.String getPatientIcn() {
        return patientIcn;
    }


    /**
     * Sets the patientIcn value for this FederationImageAccessLogEventType.
     * 
     * @param patientIcn
     */
    public void setPatientIcn(java.lang.String patientIcn) {
        this.patientIcn = patientIcn;
    }


    /**
     * Gets the reason value for this FederationImageAccessLogEventType.
     * 
     * @return reason
     */
    public java.lang.String getReason() {
        return reason;
    }


    /**
     * Sets the reason value for this FederationImageAccessLogEventType.
     * 
     * @param reason
     */
    public void setReason(java.lang.String reason) {
        this.reason = reason;
    }


    /**
     * Gets the siteNumber value for this FederationImageAccessLogEventType.
     * 
     * @return siteNumber
     */
    public java.lang.String getSiteNumber() {
        return siteNumber;
    }


    /**
     * Sets the siteNumber value for this FederationImageAccessLogEventType.
     * 
     * @param siteNumber
     */
    public void setSiteNumber(java.lang.String siteNumber) {
        this.siteNumber = siteNumber;
    }


    /**
     * Gets the userSiteNumber value for this FederationImageAccessLogEventType.
     * 
     * @return userSiteNumber
     */
    public java.lang.String getUserSiteNumber() {
        return userSiteNumber;
    }


    /**
     * Sets the userSiteNumber value for this FederationImageAccessLogEventType.
     * 
     * @param userSiteNumber
     */
    public void setUserSiteNumber(java.lang.String userSiteNumber) {
        this.userSiteNumber = userSiteNumber;
    }


    /**
     * Gets the eventType value for this FederationImageAccessLogEventType.
     * 
     * @return eventType
     */
    public gov.va.med.imaging.federation.webservices.types.FederationImageAccessLogEventTypeEventType getEventType() {
        return eventType;
    }


    /**
     * Sets the eventType value for this FederationImageAccessLogEventType.
     * 
     * @param eventType
     */
    public void setEventType(gov.va.med.imaging.federation.webservices.types.FederationImageAccessLogEventTypeEventType eventType) {
        this.eventType = eventType;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FederationImageAccessLogEventType)) return false;
        FederationImageAccessLogEventType other = (FederationImageAccessLogEventType) obj;
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
            ((this.patientIcn==null && other.getPatientIcn()==null) || 
             (this.patientIcn!=null &&
              this.patientIcn.equals(other.getPatientIcn()))) &&
            ((this.reason==null && other.getReason()==null) || 
             (this.reason!=null &&
              this.reason.equals(other.getReason()))) &&
            ((this.siteNumber==null && other.getSiteNumber()==null) || 
             (this.siteNumber!=null &&
              this.siteNumber.equals(other.getSiteNumber()))) &&
            ((this.userSiteNumber==null && other.getUserSiteNumber()==null) || 
             (this.userSiteNumber!=null &&
              this.userSiteNumber.equals(other.getUserSiteNumber()))) &&
            ((this.eventType==null && other.getEventType()==null) || 
             (this.eventType!=null &&
              this.eventType.equals(other.getEventType())));
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
        if (getPatientIcn() != null) {
            _hashCode += getPatientIcn().hashCode();
        }
        if (getReason() != null) {
            _hashCode += getReason().hashCode();
        }
        if (getSiteNumber() != null) {
            _hashCode += getSiteNumber().hashCode();
        }
        if (getUserSiteNumber() != null) {
            _hashCode += getUserSiteNumber().hashCode();
        }
        if (getEventType() != null) {
            _hashCode += getEventType().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FederationImageAccessLogEventType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:types.webservices.federation.imaging.med.va.gov", "FederationImageAccessLogEventType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("imageId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "image-Id"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("patientIcn");
        elemField.setXmlName(new javax.xml.namespace.QName("", "patientIcn"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("reason");
        elemField.setXmlName(new javax.xml.namespace.QName("", "reason"));
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
        elemField.setFieldName("userSiteNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("", "user-site-number"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("eventType");
        elemField.setXmlName(new javax.xml.namespace.QName("", "eventType"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:types.webservices.federation.imaging.med.va.gov", ">FederationImageAccessLogEventType>eventType"));
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
