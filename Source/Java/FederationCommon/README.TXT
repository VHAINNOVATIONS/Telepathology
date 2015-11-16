After running wsdl2java to generate the webservice code do the following:

1.) copy the following files from FederationCommon into FederationDataSourceProvider:
  a.) gov.va.med.imaging.federation.webservices.soap.v3.ImageMetadataFederationService.java
  b.) gov.va.med.imaging.federation.webservices.soap.v3.ImageMetadataFederationServiceLocator.java
  c.) gov.va.med.imaging.federation.webservices.soap.v3.ImageMetadataFederationSoapBindingStub.java
  
2.) copy the following files from FederationCommon into FederationWebApp:
  a.) gov.va.med.imaging.federation.webservices.soap.v3.ImageMetadataFederationServiceImpl.java
  b.) gov.va.med.imaging.federation.webservices.soap.v3.ImageMetadataFederationSoapBindingSkeleton.java
  c.) deploy.wsdd
  d.) undeploy.wsdd
  
3.) Modify ImageMetadataFederationServiceImpl as follows:
  a.) delete all methods from the class
  b.) extend gov.va.med.imaging.federation.webservices.FederationWebservices_v3
