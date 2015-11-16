/**
 * ArrayOfSiteTO.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.vistaweb.WebServices.SiteService;

public class ArrayOfSiteTO  implements java.io.Serializable {
    private gov.va.med.vistaweb.WebServices.SiteService.SiteTO[] siteTO;

    public ArrayOfSiteTO() {
    }

    public ArrayOfSiteTO(
           gov.va.med.vistaweb.WebServices.SiteService.SiteTO[] siteTO) {
           this.siteTO = siteTO;
    }


    /**
     * Gets the siteTO value for this ArrayOfSiteTO.
     * 
     * @return siteTO
     */
    public gov.va.med.vistaweb.WebServices.SiteService.SiteTO[] getSiteTO() {
        return siteTO;
    }


    /**
     * Sets the siteTO value for this ArrayOfSiteTO.
     * 
     * @param siteTO
     */
    public void setSiteTO(gov.va.med.vistaweb.WebServices.SiteService.SiteTO[] siteTO) {
        this.siteTO = siteTO;
    }

    public gov.va.med.vistaweb.WebServices.SiteService.SiteTO getSiteTO(int i) {
        return this.siteTO[i];
    }

    public void setSiteTO(int i, gov.va.med.vistaweb.WebServices.SiteService.SiteTO _value) {
        this.siteTO[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ArrayOfSiteTO)) return false;
        ArrayOfSiteTO other = (ArrayOfSiteTO) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.siteTO==null && other.getSiteTO()==null) || 
             (this.siteTO!=null &&
              java.util.Arrays.equals(this.siteTO, other.getSiteTO())));
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
        if (getSiteTO() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSiteTO());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSiteTO(), i);
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
        new org.apache.axis.description.TypeDesc(ArrayOfSiteTO.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "ArrayOfSiteTO"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("siteTO");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "SiteTO"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "SiteTO"));
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
