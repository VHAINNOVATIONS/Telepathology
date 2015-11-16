/**
 * FederationVistaRadExamImagesResponseType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.imaging.federation.webservices.types.v3;

public class FederationVistaRadExamImagesResponseType  implements java.io.Serializable {
    private gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImagesType examImages;

    public FederationVistaRadExamImagesResponseType() {
    }

    public FederationVistaRadExamImagesResponseType(
           gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImagesType examImages) {
           this.examImages = examImages;
    }


    /**
     * Gets the examImages value for this FederationVistaRadExamImagesResponseType.
     * 
     * @return examImages
     */
    public gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImagesType getExamImages() {
        return examImages;
    }


    /**
     * Sets the examImages value for this FederationVistaRadExamImagesResponseType.
     * 
     * @param examImages
     */
    public void setExamImages(gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImagesType examImages) {
        this.examImages = examImages;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof FederationVistaRadExamImagesResponseType)) return false;
        FederationVistaRadExamImagesResponseType other = (FederationVistaRadExamImagesResponseType) obj;
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
        if (getExamImages() != null) {
            _hashCode += getExamImages().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(FederationVistaRadExamImagesResponseType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationVistaRadExamImagesResponseType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("examImages");
        elemField.setXmlName(new javax.xml.namespace.QName("", "exam-images"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationVistaRadExamImagesType"));
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
