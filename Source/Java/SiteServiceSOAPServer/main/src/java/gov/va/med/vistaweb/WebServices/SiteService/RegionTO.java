/**
 * RegionTO.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.vistaweb.WebServices.SiteService;

public class RegionTO  implements java.io.Serializable {
    private java.lang.String name;

    private java.lang.String ID;

    private gov.va.med.vistaweb.WebServices.SiteService.ArrayOfSiteTO sites;

    private gov.va.med.vistaweb.WebServices.SiteService.FaultTO faultTO;

    public RegionTO() {
    }

    public RegionTO(
           java.lang.String name,
           java.lang.String ID,
           gov.va.med.vistaweb.WebServices.SiteService.ArrayOfSiteTO sites,
           gov.va.med.vistaweb.WebServices.SiteService.FaultTO faultTO) {
           this.name = name;
           this.ID = ID;
           this.sites = sites;
           this.faultTO = faultTO;
    }


    /**
     * Gets the name value for this RegionTO.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this RegionTO.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the ID value for this RegionTO.
     * 
     * @return ID
     */
    public java.lang.String getID() {
        return ID;
    }


    /**
     * Sets the ID value for this RegionTO.
     * 
     * @param ID
     */
    public void setID(java.lang.String ID) {
        this.ID = ID;
    }


    /**
     * Gets the sites value for this RegionTO.
     * 
     * @return sites
     */
    public gov.va.med.vistaweb.WebServices.SiteService.ArrayOfSiteTO getSites() {
        return sites;
    }


    /**
     * Sets the sites value for this RegionTO.
     * 
     * @param sites
     */
    public void setSites(gov.va.med.vistaweb.WebServices.SiteService.ArrayOfSiteTO sites) {
        this.sites = sites;
    }


    /**
     * Gets the faultTO value for this RegionTO.
     * 
     * @return faultTO
     */
    public gov.va.med.vistaweb.WebServices.SiteService.FaultTO getFaultTO() {
        return faultTO;
    }


    /**
     * Sets the faultTO value for this RegionTO.
     * 
     * @param faultTO
     */
    public void setFaultTO(gov.va.med.vistaweb.WebServices.SiteService.FaultTO faultTO) {
        this.faultTO = faultTO;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RegionTO)) return false;
        RegionTO other = (RegionTO) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.ID==null && other.getID()==null) || 
             (this.ID!=null &&
              this.ID.equals(other.getID()))) &&
            ((this.sites==null && other.getSites()==null) || 
             (this.sites!=null &&
              this.sites.equals(other.getSites()))) &&
            ((this.faultTO==null && other.getFaultTO()==null) || 
             (this.faultTO!=null &&
              this.faultTO.equals(other.getFaultTO())));
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
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getID() != null) {
            _hashCode += getID().hashCode();
        }
        if (getSites() != null) {
            _hashCode += getSites().hashCode();
        }
        if (getFaultTO() != null) {
            _hashCode += getFaultTO().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RegionTO.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "RegionTO"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "ID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sites");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "sites"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "ArrayOfSiteTO"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("faultTO");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "faultTO"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "FaultTO"));
        elemField.setMinOccurs(0);
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
