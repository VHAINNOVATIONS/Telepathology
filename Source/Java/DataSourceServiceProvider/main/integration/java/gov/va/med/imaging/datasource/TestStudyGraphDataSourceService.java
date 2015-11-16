package gov.va.med.imaging.datasource;

import gov.va.med.PatientIdentifier;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import junit.framework.TestCase;

/**
 * @author VHAISWBECKEC
 *
 */
public abstract class TestStudyGraphDataSourceService 
extends TestCase
{
	/**
	 * 
	 * @return
	 */
	protected abstract StudyGraphDataSourceSpi getStudyGraphDataSource();
	protected abstract List<GetPatientStudiesParameters> getGetPatientStudiesParametersList();
	protected abstract List<SortedSet<Study>> getGetPatientStudiesResultsList();
	protected abstract ResolvedSite getResolvedSite();

	public void testGetPatientStudies(boolean compareResults) 
	{
		StudyGraphDataSourceSpi service = getStudyGraphDataSource();
		if(service == null)
			fail("No service available to test.");
		
		if(getGetPatientStudiesResultsList() == null && getGetPatientStudiesParametersList() == null && compareResults)
			return;
		
		if(getGetPatientStudiesResultsList() == null && getGetPatientStudiesParametersList() != null && compareResults)
			fail("Results list was NOT provided, while parameter list was provided.");
		
		if(getGetPatientStudiesResultsList() != null && getGetPatientStudiesParametersList() == null && compareResults)
			fail("Results list was provided, while parameter list was NOT provided.");
		
		if(getGetPatientStudiesResultsList().size() != getGetPatientStudiesParametersList().size() && compareResults)
			fail("Results list and parameter list are different sizes.");
		
		Iterator<SortedSet<Study>> expectedResultIter = getGetPatientStudiesResultsList().iterator();
		for( GetPatientStudiesParameters parameters : getGetPatientStudiesParametersList() )
		{
			SortedSet<Study> expectedResult = expectedResultIter.next();
			StudyFilter filter = parameters.getFilter();
			String patientIcn = parameters.getPatientIcn();
			SortedSet<Study> result;
			
			try
            {
	            result = service.getPatientStudies(getResolvedSite().getSite().createRoutingToken(), 
	            		PatientIdentifier.icnPatientIdentifier(patientIcn), 
	            		filter, StudyLoadLevel.FULL).getArtifacts();
	            if(compareResults)
	            	assertEquals(expectedResult, result);
            } 
			catch (MethodException e)
            {
	            e.printStackTrace();
	            fail(e.getMessage());
            } 
			catch (ConnectionException e)
            {
	            e.printStackTrace();
	            fail(e.getMessage());
            }
		}
	}

	/**
	 * A method to run the getPatientStudy() method with the parameters from 
	 * the getGetPatientStudiesParametersList() method and write the results.
	 * A derived class should store the results somewhere an provide them, once
	 * validated, to a regular test run.
	 * 
	 * @param parameters
	 * @param resultsStream
	 */
	protected List<SortedSet<Study>> getResults()
	{
		StudyGraphDataSourceSpi service = getStudyGraphDataSource();
		if(service == null)
		{
			System.err.println("No service available to produce results from.");
			return null;
		}
		
		if(getGetPatientStudiesParametersList() == null)
			return null;
		
		List<SortedSet<Study>> results = new ArrayList<SortedSet<Study>>();
		
		for( GetPatientStudiesParameters parameters : getGetPatientStudiesParametersList() )
		{
			StudyFilter filter = parameters.getFilter();
			String patientIcn = parameters.getPatientIcn();
			SortedSet<Study> result;
			
			try
            {
	            result = service.getPatientStudies(getResolvedSite().getSite().createRoutingToken(),
	            		PatientIdentifier.icnPatientIdentifier(patientIcn), 
	            		filter, StudyLoadLevel.FULL).getArtifacts();
	            results.add(result);
            } 
			catch (MethodException e)
            {
	            e.printStackTrace();
	            fail(e.getMessage());
            } 
			catch (ConnectionException e)
            {
	            e.printStackTrace();
	            fail(e.getMessage());
            }
		}
		
		return results;
	}
}
