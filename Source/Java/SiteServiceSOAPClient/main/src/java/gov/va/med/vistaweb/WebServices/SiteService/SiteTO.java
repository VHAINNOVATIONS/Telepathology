/**
 * SiteTO.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.vistaweb.WebServices.SiteService;

public class SiteTO  implements java.io.Serializable {
    private java.lang.String sitecode;

    private java.lang.String name;

    private java.lang.String displayName;

    private java.lang.String moniker;

    private java.lang.String regionID;

    private java.lang.String hostname;

    private int port;

    private java.lang.String status;

    private gov.va.med.vistaweb.WebServices.SiteService.FaultTO faultTO;

    public SiteTO() {
    }

    public SiteTO(
           java.lang.String sitecode,
           java.lang.String name,
           java.lang.String displayName,
           java.lang.String moniker,
           java.lang.String regionID,
           java.lang.String hostname,
           int port,
           java.lang.String status,
           gov.va.med.vistaweb.WebServices.SiteService.FaultTO faultTO) {
           this.sitecode = sitecode;
           this.name = name;
           this.displayName = displayName;
           this.moniker = moniker;
           this.regionID = regionID;
           this.hostname = hostname;
           this.port = port;
           this.status = status;
           this.faultTO = faultTO;
    }


    /**
     * Gets the sitecode value for this SiteTO.
     * 
     * @return sitecode
     */
    public java.lang.String getSitecode() {
        return sitecode;
    }


    /**
     * Sets the sitecode value for this SiteTO.
     * 
     * @param sitecode
     */
    public void setSitecode(java.lang.String sitecode) {
        this.sitecode = sitecode;
    }


    /**
     * Gets the name value for this SiteTO.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this SiteTO.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the displayName value for this SiteTO.
     * 
     * @return displayName
     */
    public java.lang.String getDisplayName() {
        return displayName;
    }


    /**
     * Sets the displayName value for this SiteTO.
     * 
     * @param displayName
     */
    public void setDisplayName(java.lang.String displayName) {
        this.displayName = displayName;
    }


    /**
     * Gets the moniker value for this SiteTO.
     * 
     * @return moniker
     */
    public java.lang.String getMoniker() {
        return moniker;
    }


    /**
     * Sets the moniker value for this SiteTO.
     * 
     * @param moniker
     */
    public void setMoniker(java.lang.String moniker) {
        this.moniker = moniker;
    }


    /**
     * Gets the regionID value for this SiteTO.
     * 
     * @return regionID
     */
    public java.lang.String getRegionID() {
        return regionID;
    }


    /**
     * Sets the regionID value for this SiteTO.
     * 
     * @param regionID
     */
    public void setRegionID(java.lang.String regionID) {
        this.regionID = regionID;
    }


    /**
     * Gets the hostname value for this SiteTO.
     * 
     * @return hostname
     */
    public java.lang.String getHostname() {
        return hostname;
    }


    /**
     * Sets the hostname value for this SiteTO.
     * 
     * @param hostname
     */
    public void setHostname(java.lang.String hostname) {
        this.hostname = hostname;
    }


    /**
     * Gets the port value for this SiteTO.
     * 
     * @return port
     */
    public int getPort() {
        return port;
    }


    /**
     * Sets the port value for this SiteTO.
     * 
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }


    /**
     * Gets the status value for this SiteTO.
     * 
     * @return status
     */
    public java.lang.String getStatus() {
        return status;
    }


    /**
     * Sets the status value for this SiteTO.
     * 
     * @param status
     */
    public void setStatus(java.lang.String status) {
        this.status = status;
    }


    /**
     * Gets the faultTO value for this SiteTO.
     * 
     * @return faultTO
     */
    public gov.va.med.vistaweb.WebServices.SiteService.FaultTO getFaultTO() {
        return faultTO;
    }


    /**
     * Sets the faultTO value for this SiteTO.
     * 
     * @param faultTO
     */
    public void setFaultTO(gov.va.med.vistaweb.WebServices.SiteService.FaultTO faultTO) {
        this.faultTO = faultTO;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SiteTO)) return false;
        SiteTO other = (SiteTO) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.sitecode==null && other.getSitecode()==null) || 
             (this.sitecode!=null &&
              this.sitecode.equals(other.getSitecode()))) &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.displayName==null && other.getDisplayName()==null) || 
             (this.displayName!=null &&
              this.displayName.equals(other.getDisplayName()))) &&
            ((this.moniker==null && other.getMoniker()==null) || 
             (this.moniker!=null &&
              this.moniker.equals(other.getMoniker()))) &&
            ((this.regionID==null && other.getRegionID()==null) || 
             (this.regionID!=null &&
              this.regionID.equals(other.getRegionID()))) &&
            ((this.hostname==null && other.getHostname()==null) || 
             (this.hostname!=null &&
              this.hostname.equals(other.getHostname()))) &&
            this.port == other.getPort() &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
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
        if (getSitecode() != null) {
            _hashCode += getSitecode().hashCode();
        }
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getDisplayName() != null) {
            _hashCode += getDisplayName().hashCode();
        }
        if (getMoniker() != null) {
            _hashCode += getMoniker().hashCode();
        }
        if (getRegionID() != null) {
            _hashCode += getRegionID().hashCode();
        }
        if (getHostname() != null) {
            _hashCode += getHostname().hashCode();
        }
        _hashCode += getPort();
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getFaultTO() != null) {
            _hashCode += getFaultTO().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SiteTO.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "SiteTO"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sitecode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "sitecode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("displayName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "displayName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("moniker");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "moniker"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("regionID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "regionID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hostname");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "hostname"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("port");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "port"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
