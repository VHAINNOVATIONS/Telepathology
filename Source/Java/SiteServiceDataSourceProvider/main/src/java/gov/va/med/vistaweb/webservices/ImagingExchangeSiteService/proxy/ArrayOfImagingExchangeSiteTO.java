/**
 * ArrayOfImagingExchangeSiteTO.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.vistaweb.webservices.ImagingExchangeSiteService.proxy;

public class ArrayOfImagingExchangeSiteTO  implements java.io.Serializable {
    private gov.va.med.vistaweb.webservices.ImagingExchangeSiteService.proxy.ImagingExchangeSiteTO[] imagingExchangeSiteTO;

    public ArrayOfImagingExchangeSiteTO() {
    }

    public ArrayOfImagingExchangeSiteTO(
           gov.va.med.vistaweb.webservices.ImagingExchangeSiteService.proxy.ImagingExchangeSiteTO[] imagingExchangeSiteTO) {
           this.imagingExchangeSiteTO = imagingExchangeSiteTO;
    }


    /**
     * Gets the imagingExchangeSiteTO value for this ArrayOfImagingExchangeSiteTO.
     * 
     * @return imagingExchangeSiteTO
     */
    public gov.va.med.vistaweb.webservices.ImagingExchangeSiteService.proxy.ImagingExchangeSiteTO[] getImagingExchangeSiteTO() {
        return imagingExchangeSiteTO;
    }


    /**
     * Sets the imagingExchangeSiteTO value for this ArrayOfImagingExchangeSiteTO.
     * 
     * @param imagingExchangeSiteTO
     */
    public void setImagingExchangeSiteTO(gov.va.med.vistaweb.webservices.ImagingExchangeSiteService.proxy.ImagingExchangeSiteTO[] imagingExchangeSiteTO) {
        this.imagingExchangeSiteTO = imagingExchangeSiteTO;
    }

    public gov.va.med.vistaweb.webservices.ImagingExchangeSiteService.proxy.ImagingExchangeSiteTO getImagingExchangeSiteTO(int i) {
        return this.imagingExchangeSiteTO[i];
    }

    public void setImagingExchangeSiteTO(int i, gov.va.med.vistaweb.webservices.ImagingExchangeSiteService.proxy.ImagingExchangeSiteTO _value) {
        this.imagingExchangeSiteTO[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfImagingExchangeSiteTO)) return false;
        ArrayOfImagingExchangeSiteTO other = (ArrayOfImagingExchangeSiteTO) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.imagingExchangeSiteTO==null && other.getImagingExchangeSiteTO()==null) || 
             (this.imagingExchangeSiteTO!=null &&
              java.util.Arrays.equals(this.imagingExchangeSiteTO, other.getImagingExchangeSiteTO())));
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
        if (getImagingExchangeSiteTO() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getImagingExchangeSiteTO());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getImagingExchangeSiteTO(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfImagingExchangeSiteTO.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/webservices/ImagingExchangeSiteService", "ArrayOfImagingExchangeSiteTO"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("imagingExchangeSiteTO");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/webservices/ImagingExchangeSiteService", "ImagingExchangeSiteTO"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/webservices/ImagingExchangeSiteService", "ImagingExchangeSiteTO"));
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
