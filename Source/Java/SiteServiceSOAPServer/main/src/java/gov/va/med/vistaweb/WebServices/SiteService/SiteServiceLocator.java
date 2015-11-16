/**
 * SiteServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.vistaweb.WebServices.SiteService;

public class SiteServiceLocator extends org.apache.axis.client.Service implements gov.va.med.vistaweb.WebServices.SiteService.SiteService {

/**
 * Returns VistA site information
 */

    public SiteServiceLocator() {
    }


    public SiteServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SiteServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for SiteServiceSoap
    private java.lang.String SiteServiceSoap_address = "http://siteserver.vista.med.va.gov/VistaWebSvcs/SiteService.asmx";

    public java.lang.String getSiteServiceSoapAddress() {
        return SiteServiceSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String SiteServiceSoapWSDDServiceName = "SiteServiceSoap";

    public java.lang.String getSiteServiceSoapWSDDServiceName() {
        return SiteServiceSoapWSDDServiceName;
    }

    public void setSiteServiceSoapWSDDServiceName(java.lang.String name) {
        SiteServiceSoapWSDDServiceName = name;
    }

    public gov.va.med.vistaweb.WebServices.SiteService.SiteServiceSoap getSiteServiceSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SiteServiceSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSiteServiceSoap(endpoint);
    }

    public gov.va.med.vistaweb.WebServices.SiteService.SiteServiceSoap getSiteServiceSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            gov.va.med.vistaweb.WebServices.SiteService.SiteServiceSoapStub _stub = new gov.va.med.vistaweb.WebServices.SiteService.SiteServiceSoapStub(portAddress, this);
            _stub.setPortName(getSiteServiceSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setSiteServiceSoapEndpointAddress(java.lang.String address) {
        SiteServiceSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (gov.va.med.vistaweb.WebServices.SiteService.SiteServiceSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                gov.va.med.vistaweb.WebServices.SiteService.SiteServiceSoapStub _stub = new gov.va.med.vistaweb.WebServices.SiteService.SiteServiceSoapStub(new java.net.URL(SiteServiceSoap_address), this);
                _stub.setPortName(getSiteServiceSoapWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("SiteServiceSoap".equals(inputPortName)) {
            return getSiteServiceSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "SiteService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "SiteServiceSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("SiteServiceSoap".equals(portName)) {
            setSiteServiceSoapEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
