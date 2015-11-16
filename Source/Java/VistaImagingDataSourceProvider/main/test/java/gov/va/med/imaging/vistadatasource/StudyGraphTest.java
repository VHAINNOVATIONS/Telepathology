package gov.va.med.imaging.vistadatasource;

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.business.LocalizedSiteTestImpl;
import gov.va.med.imaging.core.interfaces.LocalizedSite;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.DataSourceProvider;
import gov.va.med.imaging.datasource.Provider;
import gov.va.med.imaging.datasource.StudyGraphDataSourceSpi;
import gov.va.med.imaging.exchange.business.SiteImpl;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.protocol.vista.AbstractVistaConnectionTest;

import java.net.MalformedURLException;
import java.net.URL;

public class StudyGraphTest 
extends AbstractVistaConnectionTest
{
	protected StudyGraphDataSourceSpi studyGraphDataSource;
	protected ResolvedArtifactSource source;
	

	@Override
    protected void setUp() throws Exception
    {
	    super.setUp();
	    
		System.out.println("java.protocol.handler.pkgs is '" + System.getProperty("java.protocol.handler.pkgs") + "'.");

		source = getTestResolvedSite();
		
		studyGraphDataSource = getProvider().createStudyGraphDataSource(source, "vistaimaging");
    }

	public StudyGraphDataSourceSpi getStudyGraphDataSource()
    {
    	return studyGraphDataSource;
    }
	
	public RoutingToken getRoutingToken()
	{
		return source.getArtifactSource().createRoutingToken();
	}

	public void testAvailability() 
	throws MalformedURLException
	{
		assertNotNull( getStudyGraphDataSource() );
	}
	
	private final static int PATIENT_ICN_INDEX = 0;
	private final static int PATIENT_CHECKSUM_INDEX = 1;
	
	private String[][] testPatientIcns = 
	{
		{ "1006184063", "088473" },
		{ "1008861107", "475740" },
		{ "1006167324", "385420" },
		{ "1006170647", "052871" },
		{ "1006170580", "294705" },
		{ "1006151329", "503966" },
		{ "1006152719", "948936" },
		{ "1006147276", "569483" },
		{ "1006147126", "079083" },
		{ "1006145121", "631417" }    			
	};
	
	public void testGetPatientStudiesStringFilter()
	{
		StudyFilter filter = new StudyFilter();
		
		for(String[] testPatientIcn : testPatientIcns)
			testGetPatientStudies(PatientIdentifier.icnPatientIdentifier(testPatientIcn[PATIENT_ICN_INDEX]), filter);
	}	
	
	public void testGetPatientStudies(PatientIdentifier patientIdentifier, StudyFilter filter)
	{
		try
        {
	        getStudyGraphDataSource().getPatientStudies(getRoutingToken(), patientIdentifier, filter, StudyLoadLevel.FULL);
        } 
		catch (UnsupportedOperationException e)
        {
	        e.printStackTrace();
	        fail(e.getMessage());
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
