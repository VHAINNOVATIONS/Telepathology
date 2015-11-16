/**
 * FederationVistaRadExamImagesType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.imaging.federation.webservices.types.v3;

public class FederationVistaRadExamImagesType  implements java.io.Serializable {
    private gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImageType[] examImages;

    private java.lang.String rawHeader;

    public FederationVistaRadExamImagesType() {
    }

    public FederationVistaRadExamImagesType(
           gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImageType[] examImages,
           java.lang.String rawHeader) {
           this.examImages = examImages;
           this.rawHeader = rawHeader;
    }


    /**
     * Gets the examImages value for this FederationVistaRadExamImagesType.
     * 
     * @return examImages
     */
    public gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImageType[] getExamImages() {
        return examImages;
    }


    /**
     * Sets the examImages value for this FederationVistaRadExamImagesType.
     * 
     * @param examImages
     */
    public void setExamImages(gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImageType[] examImages) {
        this.examImages = examImages;
    }

    public gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImageType getExamImages(int i) {
        return this.examImages[i];
    }

    public void setExamImages(int i, gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImageType _value) {
        this.examImages[i] = _value;
    }


    /**
     * Gets the rawHeader value for this FederationVistaRadExamImagesType.
     * 
     * @return rawHeader
     */
    public java.lang.String getRawHeader() {
        return rawHeader;
    }


    /**
     * Sets the rawHeader value for this FederationVistaRadExamImagesType.
     * 
     * @param rawHeader
     */
    public void setRawHeader(java.lang.String rawHeader) {
        this.rawHeader = rawHeader;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FederationVistaRadExamImagesType)) return false;
        FederationVistaRadExamImagesType other = (FederationVistaRadExamImagesType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.examImages==null && other.getExamImages()==null) || 
             (this.examImages!=null &&
              java.util.Arrays.equals(this.examImages, other.getExamImages()))) &&
            ((this.rawHeader==null && other.getRawHeader()==null) || 
             (this.rawHeader!=null &&
              this.rawHeader.equals(other.getRawHeader())));
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
        if (getExamImages() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getExamImages());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getExamImages(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getRawHeader() != null) {
            _hashCode += getRawHeader().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FederationVistaRadExamImagesType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationVistaRadExamImagesType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("examImages");
        elemField.setXmlName(new javax.xml.namespace.QName("", "exam-images"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationVistaRadExamImageType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rawHeader");
        elemField.setXmlName(new javax.xml.namespace.QName("", "raw-header"));
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
