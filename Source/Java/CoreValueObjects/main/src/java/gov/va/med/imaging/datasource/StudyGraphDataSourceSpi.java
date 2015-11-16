package gov.va.med.imaging.datasource;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.annotations.SPI;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.StudySetResult;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;

/**
 * This class defines the Service Provider Interface (SPI) for the StudyGraphDataSource class. 
 * All the abstract methods in this class must be implemented by each 
 * data source service provider who wishes to supply the implementation of a 
 * StudyGraphDataSource for a particular datasource type.
 * 
 * @since 1.0
 * @author VHAISWBECKEC
 *
 */
@SPI(description="Defines the interface for Study/Series/Instance graphs.")
public interface StudyGraphDataSourceSpi
extends VersionableDataSourceSpi
{
    /**
     * Get a Sorted Set of studies from the data source.  By implication therefore, 
     * the studies will be in the natural order as defined by Study (decreasing 
     * by study date) and will be unique within the Set (as determined by .equals())
     *  
     * @param globalRoutingToken The final destination where the data should come from, not just the next hop
     * @param patientIcn
     * @param filter
     * @return
     * @throws UnsupportedOperationException
     * @throws MethodException
     * @throws ConnectionException
     */
    public abstract StudySetResult getPatientStudies(RoutingToken globalRoutingToken, 
    		PatientIdentifier patientIdentifier, StudyFilter filter, StudyLoadLevel studyLoadLevel)
    throws MethodException, ConnectionException;
    
    /**
     * Get a single Study instance given a study identifier.  The type of the study
     * identifier varies by the data source and any individual data source may reject the
     * method call if the study identifier type is unknown.  In that case the data source
     * should throw a connection and not a method exception so as to allow other data sources
     * a chance to execute the call.
     * 
     * @param studyId
     * @return
     */
    public abstract Study getStudy(PatientIdentifier patientIdentifier, GlobalArtifactIdentifier studyId)
    throws MethodException, ConnectionException;
    
    /**
     * Get a the radiology report associated to a single Study instance given a study identifier.  
     * The type of the study identifier varies by the data source and any individual data source 
     * may reject the method call if the study identifier type is unknown.  In that case the data source
     * should throw a connection and not a method exception so as to allow other data sources
     * a chance to execute the call.
     * 
     * @param studyId
     * @return
     */
    public abstract String getStudyReport(PatientIdentifier patientIdentifier, GlobalArtifactIdentifier studyId)
    throws MethodException, ConnectionException;
}
