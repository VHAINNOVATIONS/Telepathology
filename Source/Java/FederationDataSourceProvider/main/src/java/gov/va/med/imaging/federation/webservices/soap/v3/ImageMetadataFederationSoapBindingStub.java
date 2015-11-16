/**
 * ImageMetadataFederationSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.imaging.federation.webservices.soap.v3;

public class ImageMetadataFederationSoapBindingStub extends org.apache.axis.client.Stub implements gov.va.med.imaging.federation.webservices.intf.v3.ImageFederationMetadata {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[20];
        _initOperationDesc1();
        _initOperationDesc2();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getPatientStudyList");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "requestor"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "RequestorType"), gov.va.med.imaging.federation.webservices.types.v3.RequestorType.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "filter"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationFilterType"), gov.va.med.imaging.federation.webservices.types.v3.FederationFilterType.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "patient-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "PatientIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "transaction-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "TransactionIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "site-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "SiteIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "authorizedSensitivityLevel"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "integer"), java.math.BigInteger.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "studyLoadLevel"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationStudyLoadLevelType"), gov.va.med.imaging.federation.webservices.types.v3.FederationStudyLoadLevelType.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "StudiesType"));
        oper.setReturnClass(gov.va.med.imaging.federation.webservices.types.v3.StudiesType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "result"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "securityCredentialsExpiredFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationSecurityCredentialsExpiredExceptionFaultType"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "methodExceptionFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationMethodExceptionFaultType"), 
                      true
                     ));
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("postImageAccessEvent");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "transaction-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "TransactionIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "log-event"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationImageAccessLogEventType"), gov.va.med.imaging.federation.webservices.types.v3.FederationImageAccessLogEventType.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        oper.setReturnClass(boolean.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "result"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "securityCredentialsExpiredFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationSecurityCredentialsExpiredExceptionFaultType"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "methodExceptionFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationMethodExceptionFaultType"), 
                      true
                     ));
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("prefetchStudyList");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "transaction-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "TransactionIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "site-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "SiteIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "patient-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "PatientIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "filter"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationFilterType"), gov.va.med.imaging.federation.webservices.types.v3.FederationFilterType.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "value"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "securityCredentialsExpiredFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationSecurityCredentialsExpiredExceptionFaultType"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "methodExceptionFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationMethodExceptionFaultType"), 
                      true
                     ));
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getImageInformation");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "image-urn"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "ImageUrnType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "transaction-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "TransactionIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "imageInfo"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "securityCredentialsExpiredFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationSecurityCredentialsExpiredExceptionFaultType"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "methodExceptionFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationMethodExceptionFaultType"), 
                      true
                     ));
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getImageSystemGlobalNode");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "image-urn"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "ImageUrnType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "transaction-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "TransactionIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "imageInfo"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "securityCredentialsExpiredFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationSecurityCredentialsExpiredExceptionFaultType"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "methodExceptionFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationMethodExceptionFaultType"), 
                      true
                     ));
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getImageDevFields");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "image-urn"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "ImageUrnType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "flags"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "transaction-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "TransactionIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "imageInfo"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "securityCredentialsExpiredFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationSecurityCredentialsExpiredExceptionFaultType"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "methodExceptionFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationMethodExceptionFaultType"), 
                      true
                     ));
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getPatientSitesVisited");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "patient-icn"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "PatientIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "transaction-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "TransactionIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "site-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "SiteIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "SiteIdentifierType"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "site-number"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "securityCredentialsExpiredFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationSecurityCredentialsExpiredExceptionFaultType"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "methodExceptionFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationMethodExceptionFaultType"), 
                      true
                     ));
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("searchPatients");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "search-criteria"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "transaction-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "TransactionIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "site-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "SiteIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "PatientType"));
        oper.setReturnClass(gov.va.med.imaging.federation.webservices.types.v3.PatientType[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "patient"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "securityCredentialsExpiredFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationSecurityCredentialsExpiredExceptionFaultType"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "methodExceptionFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationMethodExceptionFaultType"), 
                      true
                     ));
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getPatientSensitivityLevel");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "transaction-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "TransactionIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "site-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "SiteIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "patient-icn"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "PatientIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "PatientSensitiveCheckResponseType"));
        oper.setReturnClass(gov.va.med.imaging.federation.webservices.types.v3.PatientSensitiveCheckResponseType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "response"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "securityCredentialsExpiredFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationSecurityCredentialsExpiredExceptionFaultType"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "methodExceptionFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationMethodExceptionFaultType"), 
                      true
                     ));
        _operations[8] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getStudyFromCprsIdentifier");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "patient-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "PatientIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "transaction-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "TransactionIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "site-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "SiteIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "cprsIdentifier"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "CprsIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationStudyType"));
        oper.setReturnClass(gov.va.med.imaging.federation.webservices.types.v3.FederationStudyType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "result"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "securityCredentialsExpiredFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationSecurityCredentialsExpiredExceptionFaultType"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "methodExceptionFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationMethodExceptionFaultType"), 
                      true
                     ));
        _operations[9] = oper;

    }

    private static void _initOperationDesc2(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getActiveWorklist");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "transaction-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "TransactionIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "site-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "SiteIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "listDescriptor"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "ListDescriptorType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationVistaRadActiveExamsType"));
        oper.setReturnClass(gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadActiveExamsType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "result"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "securityCredentialsExpiredFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationSecurityCredentialsExpiredExceptionFaultType"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "methodExceptionFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationMethodExceptionFaultType"), 
                      true
                     ));
        _operations[10] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getPatientExams");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "transaction-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "TransactionIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "site-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "SiteIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "patient-icn"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "PatientIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "fully-loaded"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationVistaRadExamType"));
        oper.setReturnClass(gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamType[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "exams"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "securityCredentialsExpiredFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationSecurityCredentialsExpiredExceptionFaultType"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "methodExceptionFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationMethodExceptionFaultType"), 
                      true
                     ));
        _operations[11] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getVistaRadRadiologyReport");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "transaction-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "TransactionIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "study-urn"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "StudyUrnType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "ReportType"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "report"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "securityCredentialsExpiredFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationSecurityCredentialsExpiredExceptionFaultType"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "methodExceptionFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationMethodExceptionFaultType"), 
                      true
                     ));
        _operations[12] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getVistaRadRequisitionReport");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "transaction-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "TransactionIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "study-urn"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "StudyUrnType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "ReportType"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "report"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "securityCredentialsExpiredFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationSecurityCredentialsExpiredExceptionFaultType"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "methodExceptionFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationMethodExceptionFaultType"), 
                      true
                     ));
        _operations[13] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getExamImagesForExam");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "transaction-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "TransactionIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "study-urn"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "StudyUrnType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationVistaRadExamImagesType"));
        oper.setReturnClass(gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImagesType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "exam-images"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "securityCredentialsExpiredFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationSecurityCredentialsExpiredExceptionFaultType"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "methodExceptionFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationMethodExceptionFaultType"), 
                      true
                     ));
        _operations[14] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getRelevantPriorCptCodes");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "transaction-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "TransactionIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "cpt-code"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationVistaRadCptCodeType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "site-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "SiteIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationVistaRadCptCodeType"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "cpt-codes"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "securityCredentialsExpiredFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationSecurityCredentialsExpiredExceptionFaultType"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "methodExceptionFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationMethodExceptionFaultType"), 
                      true
                     ));
        _operations[15] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getNextPatientRegistration");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "transaction-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "TransactionIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "site-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "SiteIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationVistaRadPatientRegistrationType"));
        oper.setReturnClass(gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadPatientRegistrationType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "patientRegistration"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "securityCredentialsExpiredFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationSecurityCredentialsExpiredExceptionFaultType"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "methodExceptionFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationMethodExceptionFaultType"), 
                      true
                     ));
        _operations[16] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getPatientExam");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "transaction-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "TransactionIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "study-urn"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "StudyUrnType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationVistaRadExamType"));
        oper.setReturnClass(gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamType.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "exam"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "securityCredentialsExpiredFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationSecurityCredentialsExpiredExceptionFaultType"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "methodExceptionFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationMethodExceptionFaultType"), 
                      true
                     ));
        _operations[17] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("remoteMethodPassthrough");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "transaction-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "TransactionIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "site-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "SiteIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "method-name"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationRemoteMethodNameType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "parameters"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationRemoteMethodParameterType"), gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterType[].class, false, false);
        param.setNillable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "imaging-context-type"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationRemoteMethodImagingContextType"), java.lang.String.class, false, false);
        param.setNillable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "response"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "securityCredentialsExpiredFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationSecurityCredentialsExpiredExceptionFaultType"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "methodExceptionFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationMethodExceptionFaultType"), 
                      true
                     ));
        _operations[18] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("postVistaRadExamAccessEvent");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "transaction-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "TransactionIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "site-id"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "SiteIdentifierType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "input-parameter"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationVistaRadLogExamAccessInputType"), java.lang.String.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        oper.setReturnClass(boolean.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "result"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "securityCredentialsExpiredFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationSecurityCredentialsExpiredExceptionFaultType"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "methodExceptionFaultElement"),
                      "gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType",
                      new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationMethodExceptionFaultType"), 
                      true
                     ));
        _operations[19] = oper;

    }

    public ImageMetadataFederationSoapBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public ImageMetadataFederationSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public ImageMetadataFederationSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", ">FederationFilterType>origin");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.FederationFilterTypeOrigin.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", ">FederationImageAccessLogEventType>eventType");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.FederationImageAccessLogEventTypeEventType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", ">FederationSeriesType>component-instances");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.FederationSeriesTypeComponentInstances.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", ">FederationSeriesType>description");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", ">FederationStudyType>component-series");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.FederationStudyTypeComponentSeries.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", ">FederationStudyType>description");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", ">FederationStudyType>procedure-description");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", ">FederationStudyType>specialty-description");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", ">FederationStudyType>study-modalities");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.FederationStudyTypeStudyModalities.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", ">PatientType>veteran-status");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", ">RequestorType>facility-id");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", ">RequestorType>facility-name");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", ">RequestorType>purpose-of-use");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.RequestorTypePurposeOfUse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", ">RequestorType>username");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "CprsIdentifierType");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "DicomDateType");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "DicomUidType");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationFilterType");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.FederationFilterType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationImageAccessLogEventType");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.FederationImageAccessLogEventType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationInstanceType");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.FederationInstanceType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationMethodExceptionFaultType");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationPatientSexType");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.FederationPatientSexType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationRemoteMethodImagingContextType");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationRemoteMethodNameType");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationRemoteMethodParameterMultipleType");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterMultipleType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationRemoteMethodParameterType");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationRemoteMethodParameterTypeType");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterTypeType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationRemoteMethodParameterValueType");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterValueType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationSecurityCredentialsExpiredExceptionFaultType");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationSeriesType");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.FederationSeriesType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationStudiesErrorMessageType");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.FederationStudiesErrorMessageType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationStudiesErrorType");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.FederationStudiesErrorType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationStudyLoadLevelType");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.FederationStudyLoadLevelType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationStudyType");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.FederationStudyType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationVistaRadActiveExamsType");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadActiveExamsType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationVistaRadActiveExamType");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadActiveExamType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationVistaRadCptCodeType");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationVistaRadExamImagesResponseType");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImagesResponseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationVistaRadExamImagesType");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImagesType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationVistaRadExamImageType");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImageType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationVistaRadExamStatusType");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamStatusType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationVistaRadExamType");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationVistaRadLogExamAccessInputType");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationVistaRadPatientRegistrationType");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadPatientRegistrationType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "FederationVistaRadRawValueType");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "ImageUrnType");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "InstanceIdentifierType");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "ListDescriptorType");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "ModalityType");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "ObjectOriginType");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.ObjectOriginType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "PatientDOBType");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "PatientIdentifierType");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "patientNameType");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "PatientSensitiveCheckResponseType");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.PatientSensitiveCheckResponseType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "PatientSensitivityLevelType");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.PatientSensitivityLevelType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "PatientType");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.PatientType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "ReportType");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "RequestorType");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.RequestorType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "SeriesIdentifierType");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "SiteAbbreviationType");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "SiteIdentifierType");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "SiteNameType");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "SocialSecurityNumberType");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "StudiesType");
            cachedSerQNames.add(qName);
            cls = gov.va.med.imaging.federation.webservices.types.v3.StudiesType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "StudyIdentifierType");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "StudyUrnType");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("urn:v3.types.webservices.federation.imaging.med.va.gov", "TransactionIdentifierType");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public gov.va.med.imaging.federation.webservices.types.v3.StudiesType getPatientStudyList(gov.va.med.imaging.federation.webservices.types.v3.RequestorType requestor, gov.va.med.imaging.federation.webservices.types.v3.FederationFilterType filter, java.lang.String patientId, java.lang.String transactionId, java.lang.String siteId, java.math.BigInteger authorizedSensitivityLevel, gov.va.med.imaging.federation.webservices.types.v3.FederationStudyLoadLevelType studyLoadLevel) throws java.rmi.RemoteException, gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType, gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("getPatientStudyList");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "getPatientStudyList"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {requestor, filter, patientId, transactionId, siteId, authorizedSensitivityLevel, studyLoadLevel});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (gov.va.med.imaging.federation.webservices.types.v3.StudiesType) _resp;
            } catch (java.lang.Exception _exception) {
                return (gov.va.med.imaging.federation.webservices.types.v3.StudiesType) org.apache.axis.utils.JavaUtils.convert(_resp, gov.va.med.imaging.federation.webservices.types.v3.StudiesType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public boolean postImageAccessEvent(java.lang.String transactionId, gov.va.med.imaging.federation.webservices.types.v3.FederationImageAccessLogEventType logEvent) throws java.rmi.RemoteException, gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType, gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("postImageAccessEvent");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "postImageAccessEvent"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {transactionId, logEvent});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return ((java.lang.Boolean) _resp).booleanValue();
            } catch (java.lang.Exception _exception) {
                return ((java.lang.Boolean) org.apache.axis.utils.JavaUtils.convert(_resp, boolean.class)).booleanValue();
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public java.lang.String prefetchStudyList(java.lang.String transactionId, java.lang.String siteId, java.lang.String patientId, gov.va.med.imaging.federation.webservices.types.v3.FederationFilterType filter) throws java.rmi.RemoteException, gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType, gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("prefetchStudyList");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "prefetchStudyList"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {transactionId, siteId, patientId, filter});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public java.lang.String getImageInformation(java.lang.String imageUrn, java.lang.String transactionId) throws java.rmi.RemoteException, gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType, gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("getImageInformation");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "getImageInformation"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {imageUrn, transactionId});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public java.lang.String getImageSystemGlobalNode(java.lang.String imageUrn, java.lang.String transactionId) throws java.rmi.RemoteException, gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType, gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("getImageSystemGlobalNode");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "getImageSystemGlobalNode"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {imageUrn, transactionId});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public java.lang.String getImageDevFields(java.lang.String imageUrn, java.lang.String flags, java.lang.String transactionId) throws java.rmi.RemoteException, gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType, gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("getImageDevFields");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "getImageDevFields"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {imageUrn, flags, transactionId});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public java.lang.String[] getPatientSitesVisited(java.lang.String patientIcn, java.lang.String transactionId, java.lang.String siteId) throws java.rmi.RemoteException, gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType, gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("getPatientSitesVisited");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "getPatientSitesVisited"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {patientIcn, transactionId, siteId});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public gov.va.med.imaging.federation.webservices.types.v3.PatientType[] searchPatients(java.lang.String searchCriteria, java.lang.String transactionId, java.lang.String siteId) throws java.rmi.RemoteException, gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType, gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("searchPatients");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "searchPatients"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {searchCriteria, transactionId, siteId});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (gov.va.med.imaging.federation.webservices.types.v3.PatientType[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (gov.va.med.imaging.federation.webservices.types.v3.PatientType[]) org.apache.axis.utils.JavaUtils.convert(_resp, gov.va.med.imaging.federation.webservices.types.v3.PatientType[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public gov.va.med.imaging.federation.webservices.types.v3.PatientSensitiveCheckResponseType getPatientSensitivityLevel(java.lang.String transactionId, java.lang.String siteId, java.lang.String patientIcn) throws java.rmi.RemoteException, gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType, gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("getPatientSensitivityLevel");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "getPatientSensitivityLevel"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {transactionId, siteId, patientIcn});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (gov.va.med.imaging.federation.webservices.types.v3.PatientSensitiveCheckResponseType) _resp;
            } catch (java.lang.Exception _exception) {
                return (gov.va.med.imaging.federation.webservices.types.v3.PatientSensitiveCheckResponseType) org.apache.axis.utils.JavaUtils.convert(_resp, gov.va.med.imaging.federation.webservices.types.v3.PatientSensitiveCheckResponseType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public gov.va.med.imaging.federation.webservices.types.v3.FederationStudyType getStudyFromCprsIdentifier(java.lang.String patientId, java.lang.String transactionId, java.lang.String siteId, java.lang.String cprsIdentifier) throws java.rmi.RemoteException, gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType, gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[9]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("getStudyFromCprsIdentifier");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "getStudyFromCprsIdentifier"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {patientId, transactionId, siteId, cprsIdentifier});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (gov.va.med.imaging.federation.webservices.types.v3.FederationStudyType) _resp;
            } catch (java.lang.Exception _exception) {
                return (gov.va.med.imaging.federation.webservices.types.v3.FederationStudyType) org.apache.axis.utils.JavaUtils.convert(_resp, gov.va.med.imaging.federation.webservices.types.v3.FederationStudyType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadActiveExamsType getActiveWorklist(java.lang.String transactionId, java.lang.String siteId, java.lang.String listDescriptor) throws java.rmi.RemoteException, gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType, gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[10]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("getActiveWorklist");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "getActiveWorklist"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {transactionId, siteId, listDescriptor});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadActiveExamsType) _resp;
            } catch (java.lang.Exception _exception) {
                return (gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadActiveExamsType) org.apache.axis.utils.JavaUtils.convert(_resp, gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadActiveExamsType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamType[] getPatientExams(java.lang.String transactionId, java.lang.String siteId, java.lang.String patientIcn, boolean fullyLoaded) throws java.rmi.RemoteException, gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType, gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[11]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("getPatientExams");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "getPatientExams"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {transactionId, siteId, patientIcn, new java.lang.Boolean(fullyLoaded)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamType[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamType[]) org.apache.axis.utils.JavaUtils.convert(_resp, gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamType[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public java.lang.String getVistaRadRadiologyReport(java.lang.String transactionId, java.lang.String studyUrn) throws java.rmi.RemoteException, gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType, gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[12]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("getVistaRadRadiologyReport");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "getVistaRadRadiologyReport"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {transactionId, studyUrn});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public java.lang.String getVistaRadRequisitionReport(java.lang.String transactionId, java.lang.String studyUrn) throws java.rmi.RemoteException, gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType, gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[13]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("getVistaRadRequisitionReport");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "getVistaRadRequisitionReport"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {transactionId, studyUrn});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImagesType getExamImagesForExam(java.lang.String transactionId, java.lang.String studyUrn) throws java.rmi.RemoteException, gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType, gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[14]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("getExamImagesForExam");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "getExamImagesForExam"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {transactionId, studyUrn});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImagesType) _resp;
            } catch (java.lang.Exception _exception) {
                return (gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImagesType) org.apache.axis.utils.JavaUtils.convert(_resp, gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImagesType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public java.lang.String[] getRelevantPriorCptCodes(java.lang.String transactionId, java.lang.String cptCode, java.lang.String siteId) throws java.rmi.RemoteException, gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType, gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[15]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("getRelevantPriorCptCodes");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "getRelevantPriorCptCodes"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {transactionId, cptCode, siteId});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadPatientRegistrationType getNextPatientRegistration(java.lang.String transactionId, java.lang.String siteId) throws java.rmi.RemoteException, gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType, gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[16]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("getNextPatientRegistration");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "getNextPatientRegistration"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {transactionId, siteId});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadPatientRegistrationType) _resp;
            } catch (java.lang.Exception _exception) {
                return (gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadPatientRegistrationType) org.apache.axis.utils.JavaUtils.convert(_resp, gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadPatientRegistrationType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamType getPatientExam(java.lang.String transactionId, java.lang.String studyUrn) throws java.rmi.RemoteException, gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType, gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[17]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("getPatientExam");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "getPatientExam"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {transactionId, studyUrn});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamType) _resp;
            } catch (java.lang.Exception _exception) {
                return (gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamType) org.apache.axis.utils.JavaUtils.convert(_resp, gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamType.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public java.lang.String remoteMethodPassthrough(java.lang.String transactionId, java.lang.String siteId, java.lang.String methodName, gov.va.med.imaging.federation.webservices.types.v3.FederationRemoteMethodParameterType[] parameters, java.lang.String imagingContextType) throws java.rmi.RemoteException, gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType, gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[18]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("remoteMethodPassthrough");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "remoteMethodPassthrough"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {transactionId, siteId, methodName, parameters, imagingContextType});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public boolean postVistaRadExamAccessEvent(java.lang.String transactionId, java.lang.String siteId, java.lang.String inputParameter) throws java.rmi.RemoteException, gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType, gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[19]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("postVistaRadExamAccessEvent");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:v3.intf.webservices.federation.imaging.med.va.gov", "postVistaRadExamAccessEvent"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {transactionId, siteId, inputParameter});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return ((java.lang.Boolean) _resp).booleanValue();
            } catch (java.lang.Exception _exception) {
                return ((java.lang.Boolean) org.apache.axis.utils.JavaUtils.convert(_resp, boolean.class)).booleanValue();
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationSecurityCredentialsExpiredExceptionFaultType) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) {
              throw (gov.va.med.imaging.federation.webservices.types.v3.FederationMethodExceptionFaultType) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

}
