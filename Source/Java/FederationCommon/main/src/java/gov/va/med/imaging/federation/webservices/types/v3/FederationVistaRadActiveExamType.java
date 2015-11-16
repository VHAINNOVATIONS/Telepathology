/**
 * FederationVistaRadActiveExamType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.imaging.federation.webservices.types.v3;

public class FederationVistaRadActiveExamType  implements java.io.Serializable {
    private java.lang.String examId;

    private java.lang.String patientIcn;

    private java.lang.String siteNumber;

    private java.lang.String rawValue;

    public FederationVistaRadActiveExamType() {
    }

    public FederationVistaRadActiveExamType(
           java.lang.String examId,
           java.lang.String patientIcn,
           java.lang.String siteNumber,
           java.lang.String rawValue) {
           this.examId = examId;
           this.patientIcn = patientIcn;
           this.siteNumber = siteNumber;
           this.rawValue = rawValue;
    }


    /**
     * Gets the examId value for this FederationVistaRadActiveExamType.
     * 
     * @return examId
     */
    public java.lang.String getExamId() {
        return examId;
    }


    /**
     * Sets the examId value for this FederationVistaRadActiveExamType.
     * 
     * @param examId
     */
    public void setExamId(java.lang.String examId) {
        this.examId = examId;
    }


    /**
     * Gets the patientIcn value for this FederationVistaRadActiveExamType.
     * 
     * @return patientIcn
     */
    public java.lang.String getPatientIcn() {
        return patientIcn;
    }


    /**
     * Sets the patientIcn value for this FederationVistaRadActiveExamType.
     * 
     * @param patientIcn
     */
    public void setPatientIcn(java.lang.String patientIcn) {
        this.patientIcn = patientIcn;
    }


    /**
     * Gets the siteNumber value for this FederationVistaRadActiveExamType.
     * 
     * @return siteNumber
     */
    public java.lang.String getSiteNumber() {
        return siteNumber;
    }


    /**
     * Sets the siteNumber value for this FederationVistaRadActiveExamType.
     * 
     * @param siteNumber
     */
    public void setSiteNumber(java.lang.String siteNumber) {
        this.siteNumber = siteNumber;
    }


    /**
     * Gets the rawValue value for this FederationVistaRadActiveExamType.
     * 
     * @return rawValue
     */
    public java.lang.String getRawValue() {
        return rawValue;
    }


    /**
     * Sets the rawValue value for this FederationVistaRadActiveExamType.
     * 
     * @param rawValue
     */
    public void setRawValue(java.lang.String rawValue) {
        this.rawValue = rawValue;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FederationVistaRadActiveExamType)) return false;
        FederationVistaRadActiveExamType other = (FederationVistaRadActiveExamType) obj;
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
            ((this.rawValue==null && other.getRawValue()==null) || 
             (this.rawValue!=null &&
              this.rawValue.equals(other.getRawValue())));
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
        if (getRawValue() != null) {
            _hashCode += getRawValue().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FederationVistaRadActiveExamType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationVistaRadActiveExamType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("examId");
        elemField.setXmlName(new javax.xml.namespace.QName("", "examId"));
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
        elemField.setFieldName("rawValue");
        elemField.setXmlName(new javax.xml.namespace.QName("", "raw-value"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
