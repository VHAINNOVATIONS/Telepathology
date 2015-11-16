/**
 * SiteServiceSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.vistaweb.WebServices.SiteService;

public interface SiteServiceSoap extends java.rmi.Remote {

    /**
     * Gets entire list of VHA sites and data sources
     */
    public gov.va.med.vistaweb.WebServices.SiteService.ArrayOfRegionTO getVHA() throws java.rmi.RemoteException;

    /**
     * Gets data source and site data for each site in a single VISN
     */
    public gov.va.med.vistaweb.WebServices.SiteService.RegionTO getVISN(java.lang.String regionID) throws java.rmi.RemoteException;

    /**
     * Gets data source data for caret-delimited list of VAMCs
     */
    public gov.va.med.vistaweb.WebServices.SiteService.ArrayOfSiteTO getSites(java.lang.String siteIDs) throws java.rmi.RemoteException;

    /**
     * Gets data source data for a single VAMC
     */
    public gov.va.med.vistaweb.WebServices.SiteService.SiteTO getSite(java.lang.String siteID) throws java.rmi.RemoteException;
}
