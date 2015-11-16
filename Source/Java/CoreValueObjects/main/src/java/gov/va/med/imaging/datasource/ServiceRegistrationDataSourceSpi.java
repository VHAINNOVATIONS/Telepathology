package gov.va.med.imaging.datasource;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.annotations.SPI;
import gov.va.med.imaging.exchange.business.ServiceRegistration;

import java.util.List;

/**
 * This class defines the Service Provider Interface (SPI) for the
 * ServiceRegistrationDataSource. All the abstract methods in this class must be
 * implemented by each data source service provider who wishes to supply the
 * implementation of a ServiceRegistrationDataSource for a particular datasource type.
 * 
 * @since 1.0
 * @author VHAISWGRAVER
 * 
 */
@SPI(description="This class defines the Service Provider Interface (SPI) for the ServiceRegistrationDataSource.")
public interface ServiceRegistrationDataSourceSpi extends
		VersionableDataSourceSpi {

	ServiceRegistration create(ServiceRegistration registration)
			throws MethodException, ConnectionException;

	boolean delete(ServiceRegistration registration) throws MethodException,
			ConnectionException;

	List<ServiceRegistration> findAll()
			throws MethodException, ConnectionException;

	ServiceRegistration getById(int id) throws MethodException,
			ConnectionException;

	List<ServiceRegistration> getByServiceId(String serviceId)
			throws MethodException, ConnectionException;

	ServiceRegistration update(ServiceRegistration registration)
			throws MethodException, ConnectionException;
}
