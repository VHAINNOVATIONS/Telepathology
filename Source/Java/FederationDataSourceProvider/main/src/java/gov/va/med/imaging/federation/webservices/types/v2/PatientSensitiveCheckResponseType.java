/**
 * PatientSensitiveCheckResponseType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.imaging.federation.webservices.types.v2;

public class PatientSensitiveCheckResponseType  implements java.io.Serializable {
    private gov.va.med.imaging.federation.webservices.types.v2.PatientSensitivityLevelType patientSensitivityLevel;

    private java.lang.String warningMessage;

    public PatientSensitiveCheckResponseType() {
    }

    public PatientSensitiveCheckResponseType(
           gov.va.med.imaging.federation.webservices.types.v2.PatientSensitivityLevelType patientSensitivityLevel,
           java.lang.String warningMessage) {
           this.patientSensitivityLevel = patientSensitivityLevel;
           this.warningMessage = warningMessage;
    }


    /**
     * Gets the patientSensitivityLevel value for this PatientSensitiveCheckResponseType.
     * 
     * @return patientSensitivityLevel
     */
    public gov.va.med.imaging.federation.webservices.types.v2.PatientSensitivityLevelType getPatientSensitivityLevel() {
        return patientSensitivityLevel;
    }


    /**
     * Sets the patientSensitivityLevel value for this PatientSensitiveCheckResponseType.
     * 
     * @param patientSensitivityLevel
     */
    public void setPatientSensitivityLevel(gov.va.med.imaging.federation.webservices.types.v2.PatientSensitivityLevelType patientSensitivityLevel) {
        this.patientSensitivityLevel = patientSensitivityLevel;
    }


    /**
     * Gets the warningMessage value for this PatientSensitiveCheckResponseType.
     * 
     * @return warningMessage
     */
    public java.lang.String getWarningMessage() {
        return warningMessage;
    }


    /**
     * Sets the warningMessage value for this PatientSensitiveCheckResponseType.
     * 
     * @param warningMessage
     */
    public void setWarningMessage(java.lang.String warningMessage) {
        this.warningMessage = warningMessage;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PatientSensitiveCheckResponseType)) return false;
        PatientSensitiveCheckResponseType other = (PatientSensitiveCheckResponseType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.patientSensitivityLevel==null && other.getPatientSensitivityLevel()==null) || 
             (this.patientSensitivityLevel!=null &&
              this.patientSensitivityLevel.equals(other.getPatientSensitivityLevel()))) &&
            ((this.warningMessage==null && other.getWarningMessage()==null) || 
             (this.warningMessage!=null &&
              this.warningMessage.equals(other.getWarningMessage())));
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
        if (getPatientSensitivityLevel() != null) {
            _hashCode += getPatientSensitivityLevel().hashCode();
        }
        if (getWarningMessage() != null) {
            _hashCode += getWarningMessage().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PatientSensitiveCheckResponseType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:v2.types.webservices.federation.imaging.med.va.gov", "PatientSensitiveCheckResponseType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("patientSensitivityLevel");
        elemField.setXmlName(new javax.xml.namespace.QName("", "patientSensitivityLevel"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:v2.types.webservices.federation.imaging.med.va.gov", "PatientSensitivityLevelType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("warningMessage");
        elemField.setXmlName(new javax.xml.namespace.QName("", "warningMessage"));
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
