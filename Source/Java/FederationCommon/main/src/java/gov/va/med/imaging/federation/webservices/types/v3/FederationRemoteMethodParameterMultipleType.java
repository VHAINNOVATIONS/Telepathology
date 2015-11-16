/**
 * FederationRemoteMethodParameterMultipleType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.imaging.federation.webservices.types.v3;

public class FederationRemoteMethodParameterMultipleType  implements java.io.Serializable {
    private java.lang.String[] multipleValue;

    public FederationRemoteMethodParameterMultipleType() {
    }

    public FederationRemoteMethodParameterMultipleType(
           java.lang.String[] multipleValue) {
           this.multipleValue = multipleValue;
    }


    /**
     * Gets the multipleValue value for this FederationRemoteMethodParameterMultipleType.
     * 
     * @return multipleValue
     */
    public java.lang.String[] getMultipleValue() {
        return multipleValue;
    }


    /**
     * Sets the multipleValue value for this FederationRemoteMethodParameterMultipleType.
     * 
     * @param multipleValue
     */
    public void setMultipleValue(java.lang.String[] multipleValue) {
        this.multipleValue = multipleValue;
    }

    public java.lang.String getMultipleValue(int i) {
        return this.multipleValue[i];
    }

    public void setMultipleValue(int i, java.lang.String _value) {
        this.multipleValue[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FederationRemoteMethodParameterMultipleType)) return false;
        FederationRemoteMethodParameterMultipleType other = (FederationRemoteMethodParameterMultipleType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.multipleValue==null && other.getMultipleValue()==null) || 
             (this.multipleValue!=null &&
              java.util.Arrays.equals(this.multipleValue, other.getMultipleValue())));
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
        if (getMultipleValue() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMultipleValue());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMultipleValue(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FederationRemoteMethodParameterMultipleType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationRemoteMethodParameterMultipleType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("multipleValue");
        elemField.setXmlName(new javax.xml.namespace.QName("", "multipleValue"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
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
