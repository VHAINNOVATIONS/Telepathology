/**
 * ImageFederationMetadata.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.imaging.federation.webservices.intf.v2;

public interface ImageFederationMetadata extends java.rmi.Remote {
    public gov.va.med.imaging.federation.webservices.types.v2.StudiesType getPatientStudyList(gov.va.med.imaging.federation.webservices.types.v2.RequestorType requestor, gov.va.med.imaging.federation.webservices.types.v2.FederationFilterType filter, java.lang.String patientId, java.lang.String transactionId, java.lang.String siteId, java.math.BigInteger authorizedSensitivityLevel, gov.va.med.imaging.federation.webservices.types.v2.FederationStudyLoadLevelType studyLoadLevel) throws java.rmi.RemoteException;
    public boolean postImageAccessEvent(java.lang.String transactionId, gov.va.med.imaging.federation.webservices.types.v2.FederationImageAccessLogEventType logEvent) throws java.rmi.RemoteException;
    public java.lang.String prefetchStudyList(java.lang.String transactionId, java.lang.String siteId, java.lang.String patientId, gov.va.med.imaging.federation.webservices.types.v2.FederationFilterType filter) throws java.rmi.RemoteException;
    public java.lang.String getImageInformation(java.lang.String imageUrn, java.lang.String transactionId) throws java.rmi.RemoteException;
    public java.lang.String getImageSystemGlobalNode(java.lang.String imageUrn, java.lang.String transactionId) throws java.rmi.RemoteException;
    public java.lang.String getImageDevFields(java.lang.String imageUrn, java.lang.String flags, java.lang.String transactionId) throws java.rmi.RemoteException;
    public java.lang.String[] getPatientSitesVisited(java.lang.String patientIcn, java.lang.String transactionId, java.lang.String siteId) throws java.rmi.RemoteException;
    public gov.va.med.imaging.federation.webservices.types.v2.PatientType[] searchPatients(java.lang.String searchCriteria, java.lang.String transactionId, java.lang.String siteId) throws java.rmi.RemoteException;
    public gov.va.med.imaging.federation.webservices.types.v2.PatientSensitiveCheckResponseType getPatientSensitivityLevel(java.lang.String transactionId, java.lang.String siteId, java.lang.String patientIcn) throws java.rmi.RemoteException;
    public gov.va.med.imaging.federation.webservices.types.v2.FederationStudyType getStudyFromCprsIdentifier(java.lang.String patientId, java.lang.String transactionId, java.lang.String siteId, java.lang.String cprsIdentifier) throws java.rmi.RemoteException;
}
