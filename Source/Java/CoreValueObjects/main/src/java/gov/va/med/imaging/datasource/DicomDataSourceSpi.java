package gov.va.med.imaging.datasource;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.annotations.SPI;
import gov.va.med.imaging.exchange.business.dicom.DicomAE;

/**
 * This class defines the Service Provider Interface (SPI) for the DicomDataSource class. 
 * All the abstract methods in this class must be implemented by each 
 * data source service provider who wishes to supply the implementation of a 
 * DicomDataSource for a particular datasource type.
 * 
 * @since 1.0
 * @author vhaiswlouthj
 *
 */
@SPI(description="The service provider interface for DICOM operations")
public interface DicomDataSourceSpi
extends VersionableDataSourceSpi
{
	@Deprecated
    abstract String getStudyDetails(String studyId) 
    throws MethodException, ConnectionException;
    
	@Deprecated
    abstract String getImageDetails(String image)
    throws MethodException, ConnectionException;
    
    abstract boolean isModalityDeviceAuthenticated(String manufacturer, String model, String softwareVersion)
    throws MethodException, ConnectionException;
    
    abstract void loadDicomGatewayConfig(String hostName) 
    throws MethodException, ConnectionException;
    
}
