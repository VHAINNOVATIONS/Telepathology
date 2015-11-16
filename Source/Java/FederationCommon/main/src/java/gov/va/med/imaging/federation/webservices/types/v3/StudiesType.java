/**
 * StudiesType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.imaging.federation.webservices.types.v3;

public class StudiesType  implements java.io.Serializable {
    private gov.va.med.imaging.federation.webservices.types.v3.FederationStudyType[] study;

    private gov.va.med.imaging.federation.webservices.types.v3.FederationStudiesErrorMessageType error;

    public StudiesType() {
    }

    public StudiesType(
           gov.va.med.imaging.federation.webservices.types.v3.FederationStudyType[] study,
           gov.va.med.imaging.federation.webservices.types.v3.FederationStudiesErrorMessageType error) {
           this.study = study;
           this.error = error;
    }


    /**
     * Gets the study value for this StudiesType.
     * 
     * @return study
     */
    public gov.va.med.imaging.federation.webservices.types.v3.FederationStudyType[] getStudy() {
        return study;
    }


    /**
     * Sets the study value for this StudiesType.
     * 
     * @param study
     */
    public void setStudy(gov.va.med.imaging.federation.webservices.types.v3.FederationStudyType[] study) {
        this.study = study;
    }

    public gov.va.med.imaging.federation.webservices.types.v3.FederationStudyType getStudy(int i) {
        return this.study[i];
    }

    public void setStudy(int i, gov.va.med.imaging.federation.webservices.types.v3.FederationStudyType _value) {
        this.study[i] = _value;
    }


    /**
     * Gets the error value for this StudiesType.
     * 
     * @return error
     */
    public gov.va.med.imaging.federation.webservices.types.v3.FederationStudiesErrorMessageType getError() {
        return error;
    }


    /**
     * Sets the error value for this StudiesType.
     * 
     * @param error
     */
    public void setError(gov.va.med.imaging.federation.webservices.types.v3.FederationStudiesErrorMessageType error) {
        this.error = error;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof StudiesType)) return false;
        StudiesType other = (StudiesType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.study==null && other.getStudy()==null) || 
             (this.study!=null &&
              java.util.Arrays.equals(this.study, other.getStudy()))) &&
            ((this.error==null && other.getError()==null) || 
             (this.error!=null &&
              this.error.equals(other.getError())));
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
        if (getStudy() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getStudy());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getStudy(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getError() != null) {
            _hashCode += getError().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(StudiesType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "StudiesType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("study");
        elemField.setXmlName(new javax.xml.namespace.QName("", "study"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationStudyType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("error");
        elemField.setXmlName(new javax.xml.namespace.QName("", "error"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationStudiesErrorMessageType"));
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
