/**
 * ArrayOfRegionTO.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.vistaweb.WebServices.SiteService;

public class ArrayOfRegionTO  implements java.io.Serializable {
    private gov.va.med.vistaweb.WebServices.SiteService.RegionTO[] regionTO;

    public ArrayOfRegionTO() {
    }

    public ArrayOfRegionTO(
           gov.va.med.vistaweb.WebServices.SiteService.RegionTO[] regionTO) {
           this.regionTO = regionTO;
    }


    /**
     * Gets the regionTO value for this ArrayOfRegionTO.
     * 
     * @return regionTO
     */
    public gov.va.med.vistaweb.WebServices.SiteService.RegionTO[] getRegionTO() {
        return regionTO;
    }


    /**
     * Sets the regionTO value for this ArrayOfRegionTO.
     * 
     * @param regionTO
     */
    public void setRegionTO(gov.va.med.vistaweb.WebServices.SiteService.RegionTO[] regionTO) {
        this.regionTO = regionTO;
    }

    public gov.va.med.vistaweb.WebServices.SiteService.RegionTO getRegionTO(int i) {
        return this.regionTO[i];
    }

    public void setRegionTO(int i, gov.va.med.vistaweb.WebServices.SiteService.RegionTO _value) {
        this.regionTO[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfRegionTO)) return false;
        ArrayOfRegionTO other = (ArrayOfRegionTO) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.regionTO==null && other.getRegionTO()==null) || 
             (this.regionTO!=null &&
              java.util.Arrays.equals(this.regionTO, other.getRegionTO())));
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
        if (getRegionTO() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRegionTO());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRegionTO(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfRegionTO.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "ArrayOfRegionTO"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("regionTO");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "RegionTO"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "RegionTO"));
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
