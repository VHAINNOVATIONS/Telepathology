/**
 * FederationStudyTypeStudyModalities.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.imaging.federation.webservices.types.v2;

public class FederationStudyTypeStudyModalities  implements java.io.Serializable {
    private java.lang.String[] modality;

    public FederationStudyTypeStudyModalities() {
    }

    public FederationStudyTypeStudyModalities(
           java.lang.String[] modality) {
           this.modality = modality;
    }


    /**
     * Gets the modality value for this FederationStudyTypeStudyModalities.
     * 
     * @return modality
     */
    public java.lang.String[] getModality() {
        return modality;
    }


    /**
     * Sets the modality value for this FederationStudyTypeStudyModalities.
     * 
     * @param modality
     */
    public void setModality(java.lang.String[] modality) {
        this.modality = modality;
    }

    public java.lang.String getModality(int i) {
        return this.modality[i];
    }

    public void setModality(int i, java.lang.String _value) {
        this.modality[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FederationStudyTypeStudyModalities)) return false;
        FederationStudyTypeStudyModalities other = (FederationStudyTypeStudyModalities) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.modality==null && other.getModality()==null) || 
             (this.modality!=null &&
              java.util.Arrays.equals(this.modality, other.getModality())));
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
        if (getModality() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getModality());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getModality(), i);
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
        new org.apache.axis.description.TypeDesc(FederationStudyTypeStudyModalities.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:v2.types.webservices.federation.imaging.med.va.gov", ">FederationStudyType>study-modalities"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("modality");
        elemField.setXmlName(new javax.xml.namespace.QName("", "modality"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:v2.types.webservices.federation.imaging.med.va.gov", "ModalityType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
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
