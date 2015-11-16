/**
 * SiteService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.vistaweb.WebServices.SiteService;

public interface SiteService extends javax.xml.rpc.Service {

/**
 * Returns VistA site information
 */
    public java.lang.String getSiteServiceSoapAddress();

    public gov.va.med.vistaweb.WebServices.SiteService.SiteServiceSoap getSiteServiceSoap() throws javax.xml.rpc.ServiceException;

    public gov.va.med.vistaweb.WebServices.SiteService.SiteServiceSoap getSiteServiceSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
