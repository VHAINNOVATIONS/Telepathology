/**
 * ImageMetadataFederationServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.imaging.federation.webservices.soap.v3;

public class ImageMetadataFederationServiceLocator extends org.apache.axis.client.Service implements gov.va.med.imaging.federation.webservices.soap.v3.ImageMetadataFederationService {

    public ImageMetadataFederationServiceLocator() {
    }


    public ImageMetadataFederationServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ImageMetadataFederationServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ImageMetadataFederationV3
    private java.lang.String ImageMetadataFederationV3_address = "http://localhost:8080/ImagingExchangeWebApp/webservices/ImageMetadataFederation";

    public java.lang.String getImageMetadataFederationV3Address() {
        return ImageMetadataFederationV3_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ImageMetadataFederationV3WSDDServiceName = "ImageMetadataFederation.v3";

    public java.lang.String getImageMetadataFederationV3WSDDServiceName() {
        return ImageMetadataFederationV3WSDDServiceName;
    }

    public void setImageMetadataFederationV3WSDDServiceName(java.lang.String name) {
        ImageMetadataFederationV3WSDDServiceName = name;
    }

    public gov.va.med.imaging.federation.webservices.intf.v3.ImageFederationMetadata getImageMetadataFederationV3() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ImageMetadataFederationV3_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getImageMetadataFederationV3(endpoint);
    }

    public gov.va.med.imaging.federation.webservices.intf.v3.ImageFederationMetadata getImageMetadataFederationV3(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            gov.va.med.imaging.federation.webservices.soap.v3.ImageMetadataFederationSoapBindingStub _stub = new gov.va.med.imaging.federation.webservices.soap.v3.ImageMetadataFederationSoapBindingStub(portAddress, this);
            _stub.setPortName(getImageMetadataFederationV3WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setImageMetadataFederationV3EndpointAddress(java.lang.String address) {
        ImageMetadataFederationV3_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (gov.va.med.imaging.federation.webservices.intf.v3.ImageFederationMetadata.class.isAssignableFrom(serviceEndpointInterface)) {
                gov.va.med.imaging.federation.webservices.soap.v3.ImageMetadataFederationSoapBindingStub _stub = new gov.va.med.imaging.federation.webservices.soap.v3.ImageMetadataFederationSoapBindingStub(new java.net.URL(ImageMetadataFederationV3_address), this);
                _stub.setPortName(getImageMetadataFederationV3WSDDServiceName());
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
        if ("ImageMetadataFederation.v3".equals(inputPortName)) {
            return getImageMetadataFederationV3();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("urn:v3.soap.webservices.federation.imaging.med.va.gov", "ImageMetadataFederationService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("urn:v3.soap.webservices.federation.imaging.med.va.gov", "ImageMetadataFederation.v3"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("ImageMetadataFederationV3".equals(portName)) {
            setImageMetadataFederationV3EndpointAddress(address);
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
