package gov.va.med.imaging.datasource;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.annotations.SPI;
import gov.va.med.imaging.exchange.business.DocumentFilter;
import gov.va.med.imaging.exchange.business.documents.DocumentSetResult;

/**
 * 
 * @author VHAISWBECKEC
 *
 */
@SPI(description="The service provider interface for document set access")
public interface DocumentSetDataSourceSpi 
extends VersionableDataSourceSpi
{
    /**
     * Get a Sorted Set of studies from the data source.  By implication therefore, 
     * the studies will be in the natural order as defined by Study (decreasing 
     * by study date) and will be unique within the Set (as determined by .equals())
     *  
     * @param patientIcn
     * @param filter
     * @return
     * @throws UnsupportedOperationException
     * @throws MethodException
     * @throws ConnectionException
     */
    public abstract DocumentSetResult getPatientDocumentSets(RoutingToken globalRoutingToken,
    		DocumentFilter filter)
    throws MethodException, ConnectionException;
}
