package gov.va.med.imaging.datasource;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.annotations.SPI;
import gov.va.med.imaging.veins.ErrorTypeNotificationConfiguration;

import java.util.List;

/**
 * This class defines the Service Provider Interface (SPI) for the
 * VeinsDataSource. All the abstract methods in this class must be implemented
 * by each data source service provider who wishes to supply the implementation
 * of a VeinsDataSource for a particular datasource type.
 * 
 * @since 1.0
 * @author VHAISWGRAVER
 * 
 */
@SPI(description="Defines the interface the VEINS error reporting system.")
public interface VeinsDataSourceSpi 
extends VersionableDataSourceSpi 
{
	List<ErrorTypeNotificationConfiguration> findAllErrorTypeNotificationConfiguration()
			throws MethodException, ConnectionException;

	void updateErrorTypeNotificationConfiguration(
			ErrorTypeNotificationConfiguration config) throws MethodException,
			ConnectionException;

	ErrorTypeNotificationConfiguration getErrorTypeNotificationConfiguration(
			String errorType) throws MethodException, ConnectionException;
}
