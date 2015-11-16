/**
 * ImagingExchangeSiteTO.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.vistaweb.webservices.ImagingExchangeSiteService.proxy;

public class ImagingExchangeSiteTO  implements java.io.Serializable {
    private java.lang.String siteNumber;

    private java.lang.String siteName;

    private java.lang.String regionID;

    private java.lang.String siteAbbr;

    private java.lang.String vistaServer;

    private int vistaPort;

    private java.lang.String acceleratorServer;

    private int acceleratorPort;

    private gov.va.med.vistaweb.webservices.ImagingExchangeSiteService.proxy.FaultTO faultTO;

    public ImagingExchangeSiteTO() {
    }

    public ImagingExchangeSiteTO(
           java.lang.String siteNumber,
           java.lang.String siteName,
           java.lang.String regionID,
           java.lang.String siteAbbr,
           java.lang.String vistaServer,
           int vistaPort,
           java.lang.String acceleratorServer,
           int acceleratorPort,
           gov.va.med.vistaweb.webservices.ImagingExchangeSiteService.proxy.FaultTO faultTO) {
           this.siteNumber = siteNumber;
           this.siteName = siteName;
           this.regionID = regionID;
           this.siteAbbr = siteAbbr;
           this.vistaServer = vistaServer;
           this.vistaPort = vistaPort;
           this.acceleratorServer = acceleratorServer;
           this.acceleratorPort = acceleratorPort;
           this.faultTO = faultTO;
    }


    /**
     * Gets the siteNumber value for this ImagingExchangeSiteTO.
     * 
     * @return siteNumber
     */
    public java.lang.String getSiteNumber() {
        return siteNumber;
    }


    /**
     * Sets the siteNumber value for this ImagingExchangeSiteTO.
     * 
     * @param siteNumber
     */
    public void setSiteNumber(java.lang.String siteNumber) {
        this.siteNumber = siteNumber;
    }


    /**
     * Gets the siteName value for this ImagingExchangeSiteTO.
     * 
     * @return siteName
     */
    public java.lang.String getSiteName() {
        return siteName;
    }


    /**
     * Sets the siteName value for this ImagingExchangeSiteTO.
     * 
     * @param siteName
     */
    public void setSiteName(java.lang.String siteName) {
        this.siteName = siteName;
    }


    /**
     * Gets the regionID value for this ImagingExchangeSiteTO.
     * 
     * @return regionID
     */
    public java.lang.String getRegionID() {
        return regionID;
    }


    /**
     * Sets the regionID value for this ImagingExchangeSiteTO.
     * 
     * @param regionID
     */
    public void setRegionID(java.lang.String regionID) {
        this.regionID = regionID;
    }


    /**
     * Gets the siteAbbr value for this ImagingExchangeSiteTO.
     * 
     * @return siteAbbr
     */
    public java.lang.String getSiteAbbr() {
        return siteAbbr;
    }


    /**
     * Sets the siteAbbr value for this ImagingExchangeSiteTO.
     * 
     * @param siteAbbr
     */
    public void setSiteAbbr(java.lang.String siteAbbr) {
        this.siteAbbr = siteAbbr;
    }


    /**
     * Gets the vistaServer value for this ImagingExchangeSiteTO.
     * 
     * @return vistaServer
     */
    public java.lang.String getVistaServer() {
        return vistaServer;
    }


    /**
     * Sets the vistaServer value for this ImagingExchangeSiteTO.
     * 
     * @param vistaServer
     */
    public void setVistaServer(java.lang.String vistaServer) {
        this.vistaServer = vistaServer;
    }


    /**
     * Gets the vistaPort value for this ImagingExchangeSiteTO.
     * 
     * @return vistaPort
     */
    public int getVistaPort() {
        return vistaPort;
    }


    /**
     * Sets the vistaPort value for this ImagingExchangeSiteTO.
     * 
     * @param vistaPort
     */
    public void setVistaPort(int vistaPort) {
        this.vistaPort = vistaPort;
    }


    /**
     * Gets the acceleratorServer value for this ImagingExchangeSiteTO.
     * 
     * @return acceleratorServer
     */
    public java.lang.String getAcceleratorServer() {
        return acceleratorServer;
    }


    /**
     * Sets the acceleratorServer value for this ImagingExchangeSiteTO.
     * 
     * @param acceleratorServer
     */
    public void setAcceleratorServer(java.lang.String acceleratorServer) {
        this.acceleratorServer = acceleratorServer;
    }


    /**
     * Gets the acceleratorPort value for this ImagingExchangeSiteTO.
     * 
     * @return acceleratorPort
     */
    public int getAcceleratorPort() {
        return acceleratorPort;
    }


    /**
     * Sets the acceleratorPort value for this ImagingExchangeSiteTO.
     * 
     * @param acceleratorPort
     */
    public void setAcceleratorPort(int acceleratorPort) {
        this.acceleratorPort = acceleratorPort;
    }


    /**
     * Gets the faultTO value for this ImagingExchangeSiteTO.
     * 
     * @return faultTO
     */
    public gov.va.med.vistaweb.webservices.ImagingExchangeSiteService.proxy.FaultTO getFaultTO() {
        return faultTO;
    }


    /**
     * Sets the faultTO value for this ImagingExchangeSiteTO.
     * 
     * @param faultTO
     */
    public void setFaultTO(gov.va.med.vistaweb.webservices.ImagingExchangeSiteService.proxy.FaultTO faultTO) {
        this.faultTO = faultTO;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ImagingExchangeSiteTO)) return false;
        ImagingExchangeSiteTO other = (ImagingExchangeSiteTO) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.siteNumber==null && other.getSiteNumber()==null) || 
             (this.siteNumber!=null &&
              this.siteNumber.equals(other.getSiteNumber()))) &&
            ((this.siteName==null && other.getSiteName()==null) || 
             (this.siteName!=null &&
              this.siteName.equals(other.getSiteName()))) &&
            ((this.regionID==null && other.getRegionID()==null) || 
             (this.regionID!=null &&
              this.regionID.equals(other.getRegionID()))) &&
            ((this.siteAbbr==null && other.getSiteAbbr()==null) || 
             (this.siteAbbr!=null &&
              this.siteAbbr.equals(other.getSiteAbbr()))) &&
            ((this.vistaServer==null && other.getVistaServer()==null) || 
             (this.vistaServer!=null &&
              this.vistaServer.equals(other.getVistaServer()))) &&
            this.vistaPort == other.getVistaPort() &&
            ((this.acceleratorServer==null && other.getAcceleratorServer()==null) || 
             (this.acceleratorServer!=null &&
              this.acceleratorServer.equals(other.getAcceleratorServer()))) &&
            this.acceleratorPort == other.getAcceleratorPort() &&
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
        if (getSiteNumber() != null) {
            _hashCode += getSiteNumber().hashCode();
        }
        if (getSiteName() != null) {
            _hashCode += getSiteName().hashCode();
        }
        if (getRegionID() != null) {
            _hashCode += getRegionID().hashCode();
        }
        if (getSiteAbbr() != null) {
            _hashCode += getSiteAbbr().hashCode();
        }
        if (getVistaServer() != null) {
            _hashCode += getVistaServer().hashCode();
        }
        _hashCode += getVistaPort();
        if (getAcceleratorServer() != null) {
            _hashCode += getAcceleratorServer().hashCode();
        }
        _hashCode += getAcceleratorPort();
        if (getFaultTO() != null) {
            _hashCode += getFaultTO().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ImagingExchangeSiteTO.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/webservices/ImagingExchangeSiteService", "ImagingExchangeSiteTO"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("siteNumber");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/webservices/ImagingExchangeSiteService", "siteNumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("siteName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/webservices/ImagingExchangeSiteService", "siteName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("regionID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/webservices/ImagingExchangeSiteService", "regionID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("siteAbbr");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/webservices/ImagingExchangeSiteService", "siteAbbr"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("vistaServer");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/webservices/ImagingExchangeSiteService", "vistaServer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("vistaPort");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/webservices/ImagingExchangeSiteService", "vistaPort"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acceleratorServer");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/webservices/ImagingExchangeSiteService", "acceleratorServer"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("acceleratorPort");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/webservices/ImagingExchangeSiteService", "acceleratorPort"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("faultTO");
        elemField.setXmlName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/webservices/ImagingExchangeSiteService", "faultTO"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/webservices/ImagingExchangeSiteService", "FaultTO"));
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
