/**
 * FederationRemoteMethodParameterValueType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.imaging.federation.webservices.types.v3;

public class FederationRemoteMethodParameterValueType  implements java.io.Serializable {
    private java.lang.String value;

    private gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterMultipleType multipleValue;

    public FederationRemoteMethodParameterValueType() {
    }

    public FederationRemoteMethodParameterValueType(
           java.lang.String value,
           gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterMultipleType multipleValue) {
           this.value = value;
           this.multipleValue = multipleValue;
    }


    /**
     * Gets the value value for this FederationRemoteMethodParameterValueType.
     * 
     * @return value
     */
    public java.lang.String getValue() {
        return value;
    }


    /**
     * Sets the value value for this FederationRemoteMethodParameterValueType.
     * 
     * @param value
     */
    public void setValue(java.lang.String value) {
        this.value = value;
    }


    /**
     * Gets the multipleValue value for this FederationRemoteMethodParameterValueType.
     * 
     * @return multipleValue
     */
    public gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterMultipleType getMultipleValue() {
        return multipleValue;
    }


    /**
     * Sets the multipleValue value for this FederationRemoteMethodParameterValueType.
     * 
     * @param multipleValue
     */
    public void setMultipleValue(gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterMultipleType multipleValue) {
        this.multipleValue = multipleValue;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FederationRemoteMethodParameterValueType)) return false;
        FederationRemoteMethodParameterValueType other = (FederationRemoteMethodParameterValueType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.value==null && other.getValue()==null) || 
             (this.value!=null &&
              this.value.equals(other.getValue()))) &&
            ((this.multipleValue==null && other.getMultipleValue()==null) || 
             (this.multipleValue!=null &&
              this.multipleValue.equals(other.getMultipleValue())));
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
        if (getValue() != null) {
            _hashCode += getValue().hashCode();
        }
        if (getMultipleValue() != null) {
            _hashCode += getMultipleValue().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FederationRemoteMethodParameterValueType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationRemoteMethodParameterValueType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("value");
        elemField.setXmlName(new javax.xml.namespace.QName("", "value"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("multipleValue");
        elemField.setXmlName(new javax.xml.namespace.QName("", "multipleValue"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationRemoteMethodParameterMultipleType"));
        elemField.setMinOccurs(0);
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
