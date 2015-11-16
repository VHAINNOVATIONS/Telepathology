/**
 * ImagingExchangeSiteServiceSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.vistaweb.webservices.ImagingExchangeSiteService.proxy;

public interface ImagingExchangeSiteServiceSoap extends java.rmi.Remote {
    public gov.va.med.vistaweb.webservices.ImagingExchangeSiteService.proxy.ImagingExchangeRegionTO getVISN(java.lang.String regionID) throws java.rmi.RemoteException;
    public gov.va.med.vistaweb.webservices.ImagingExchangeSiteService.proxy.ArrayOfImagingExchangeSiteTO getSites(java.lang.String siteIDs) throws java.rmi.RemoteException;
    public gov.va.med.vistaweb.webservices.ImagingExchangeSiteService.proxy.ImagingExchangeSiteTO getSite(java.lang.String siteID) throws java.rmi.RemoteException;
    public gov.va.med.vistaweb.webservices.ImagingExchangeSiteService.proxy.ArrayOfImagingExchangeSiteTO getImagingExchangeSites() throws java.rmi.RemoteException;
}
