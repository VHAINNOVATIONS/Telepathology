/**
 * SiteServiceSoapSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.vistaweb.WebServices.SiteService;

public class SiteServiceSoapSkeleton implements gov.va.med.vistaweb.WebServices.SiteService.SiteServiceSoap, org.apache.axis.wsdl.Skeleton {
    private gov.va.med.vistaweb.WebServices.SiteService.SiteServiceSoap impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)_myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return _myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
        };
        _oper = new org.apache.axis.description.OperationDesc("getVHA", _params, new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "getVHAResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "ArrayOfRegionTO"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "getVHA"));
        _oper.setSoapAction("http://vistaweb.med.va.gov/WebServices/SiteService/getVHA");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getVHA") == null) {
            _myOperations.put("getVHA", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getVHA")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "regionID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getVISN", _params, new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "getVISNResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "RegionTO"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "getVISN"));
        _oper.setSoapAction("http://vistaweb.med.va.gov/WebServices/SiteService/getVISN");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getVISN") == null) {
            _myOperations.put("getVISN", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getVISN")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "siteIDs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getSites", _params, new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "getSitesResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "ArrayOfSiteTO"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "getSites"));
        _oper.setSoapAction("http://vistaweb.med.va.gov/WebServices/SiteService/getSites");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getSites") == null) {
            _myOperations.put("getSites", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getSites")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "siteID"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getSite", _params, new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "getSiteResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "SiteTO"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://vistaweb.med.va.gov/WebServices/SiteService", "getSite"));
        _oper.setSoapAction("http://vistaweb.med.va.gov/WebServices/SiteService/getSite");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getSite") == null) {
            _myOperations.put("getSite", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getSite")).add(_oper);
    }

    public SiteServiceSoapSkeleton() {
        this.impl = new gov.va.med.vista.siteservice.soap.SiteServiceImpl();
    }

    public SiteServiceSoapSkeleton(gov.va.med.vistaweb.WebServices.SiteService.SiteServiceSoap impl) {
        this.impl = impl;
    }
    public gov.va.med.vistaweb.WebServices.SiteService.ArrayOfRegionTO getVHA() throws java.rmi.RemoteException
    {
        gov.va.med.vistaweb.WebServices.SiteService.ArrayOfRegionTO ret = impl.getVHA();
        return ret;
    }

    public gov.va.med.vistaweb.WebServices.SiteService.RegionTO getVISN(java.lang.String regionID) throws java.rmi.RemoteException
    {
        gov.va.med.vistaweb.WebServices.SiteService.RegionTO ret = impl.getVISN(regionID);
        return ret;
    }

    public gov.va.med.vistaweb.WebServices.SiteService.ArrayOfSiteTO getSites(java.lang.String siteIDs) throws java.rmi.RemoteException
    {
        gov.va.med.vistaweb.WebServices.SiteService.ArrayOfSiteTO ret = impl.getSites(siteIDs);
        return ret;
    }

    public gov.va.med.vistaweb.WebServices.SiteService.SiteTO getSite(java.lang.String siteID) throws java.rmi.RemoteException
    {
        gov.va.med.vistaweb.WebServices.SiteService.SiteTO ret = impl.getSite(siteID);
        return ret;
    }

}
