/**
 * ImagingExchangeSiteServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.vistaweb.webservices.ImagingExchangeSiteService.proxy;

public class ImagingExchangeSiteServiceLocator extends org.apache.axis.client.Service implements gov.va.med.vistaweb.webservices.ImagingExchangeSiteService.proxy.ImagingExchangeSiteService {

/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

/**
 * Returns VistA Imaging site information
 */

    public ImagingExchangeSiteServiceLocator() {
    }


    public ImagingExchangeSiteServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ImagingExchangeSiteServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ImagingExchangeSiteServiceSoap
    private java.lang.String ImagingExchangeSiteServiceSoap_address = "http://localhost/VistaWebSvcs/ImagingExchangeSiteService.asmx";

    public java.lang.String getImagingExchangeSiteServiceSoapAddress() {
        return ImagingExchangeSiteServiceSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ImagingExchangeSiteServiceSoapWSDDServiceName = "ImagingExchangeSiteServiceSoap";

    public java.lang.String getImagingExchangeSiteServiceSoapWSDDServiceName() {
        return ImagingExchangeSiteServiceSoapWSDDServiceName;
    }

    public void setImagingExchangeSiteServiceSoapWSDDServiceName(java.lang.String name) {
        ImagingExchangeSiteServiceSoapWSDDServiceName = name;
    }

    public gov.va.med.vistaweb.webservices.ImagingExchangeSiteService.proxy.ImagingExchangeSiteServiceSoap getImagingExchangeSiteServiceSoap() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ImagingExchangeSiteServiceSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getImagingExchangeSiteServiceSoap(endpoint);
    }

    public gov.va.med.vistaweb.webservices.ImagingExchangeSiteService.proxy.ImagingExchangeSiteServiceSoap getImagingExchangeSiteServiceSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            gov.va.med.vistaweb.webservices.ImagingExchangeSiteService.proxy.ImagingExchangeSiteServiceSoapStub _stub = new gov.va.med.vistaweb.webservices.ImagingExchangeSiteService.proxy.ImagingExchangeSiteServiceSoapStub(portAddress, this);
            _stub.setPortName(getImagingExchangeSiteServiceSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setImagingExchangeSiteServiceSoapEndpointAddress(java.lang.String address) {
        ImagingExchangeSiteServiceSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (gov.va.med.vistaweb.webservices.ImagingExchangeSiteService.proxy.ImagingExchangeSiteServiceSoap.class.isAssignableFrom(serviceEndpointInterface)) {
                gov.va.med.vistaweb.webservices.ImagingExchangeSiteService.proxy.ImagingExchangeSiteServiceSoapStub _stub = new gov.va.med.vistaweb.webservices.ImagingExchangeSiteService.proxy.ImagingExchangeSiteServiceSoapStub(new java.net.URL(ImagingExchangeSiteServiceSoap_address), this);
                _stub.setPortName(getImagingExchangeSiteServiceSoapWSDDServiceName());
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
        if ("ImagingExchangeSiteServiceSoap".equals(inputPortName)) {
            return getImagingExchangeSiteServiceSoap();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://vistaweb.med.va.gov/webservices/ImagingExchangeSiteService", "ImagingExchangeSiteService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/webservices/ImagingExchangeSiteService", "ImagingExchangeSiteServiceSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("ImagingExchangeSiteServiceSoap".equals(portName)) {
            setImagingExchangeSiteServiceSoapEndpointAddress(address);
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
