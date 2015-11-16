/**
 * ImageFederationMetadata.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package gov.va.med.imaging.federation.webservices.intf;

public interface ImageFederationMetadata extends java.rmi.Remote {
    public gov.va.med.imaging.federation.webservices.types.FederationStudyType[] getPatientStudyList(gov.va.med.imaging.federation.webservices.types.RequestorType requestor, gov.va.med.imaging.federation.webservices.types.FederationFilterType filter, java.lang.String patientId, java.lang.String transactionId, java.lang.String siteId) throws java.rmi.RemoteException;
    public boolean postImageAccessEvent(java.lang.String transactionId, gov.va.med.imaging.federation.webservices.types.FederationImageAccessLogEventType logEvent) throws java.rmi.RemoteException;
    public java.lang.String prefetchStudyList(java.lang.String transactionId, java.lang.String siteId, java.lang.String patientId, gov.va.med.imaging.federation.webservices.types.FederationFilterType filter) throws java.rmi.RemoteException;
    public java.lang.String getImageInformation(java.lang.String imageUrn, java.lang.String transactionId) throws java.rmi.RemoteException;
    public java.lang.String getImageSystemGlobalNode(java.lang.String imageUrn, java.lang.String transactionId) throws java.rmi.RemoteException;
    public java.lang.String getImageDevFields(java.lang.String imageUrn, java.lang.String flags, java.lang.String transactionId) throws java.rmi.RemoteException;
    public java.lang.String[] getPatientSitesVisited(java.lang.String patientIcn, java.lang.String transactionId, java.lang.String siteId) throws java.rmi.RemoteException;
    public gov.va.med.imaging.federation.webservices.types.PatientType[] searchPatients(java.lang.String searchCriteria, java.lang.String transactionId, java.lang.String siteId) throws java.rmi.RemoteException;
}
