package gov.va.med.imaging.vistadatasource;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.GlobalArtifactIdentifierFactory;
import gov.va.med.RoutingToken;
import gov.va.med.URN;
import gov.va.med.exceptions.GlobalArtifactIdentifierFormatException;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.DocumentDataSourceSpi;
import gov.va.med.imaging.datasource.DocumentSetDataSourceSpi;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.DocumentFilter;
import gov.va.med.imaging.exchange.business.ImageStreamResponse;
import gov.va.med.imaging.exchange.business.documents.Document;
import gov.va.med.imaging.exchange.business.documents.DocumentSet;
import gov.va.med.imaging.exchange.storage.DataSourceInputStream;
import gov.va.med.imaging.protocol.vista.AbstractVistaConnectionTest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SortedSet;

import org.apache.log4j.Logger;

/**
 * 
 * @author vhaiswbeckec
 *
 */
public class DocumentSetTest 
extends AbstractVistaConnectionTest
{
	protected DocumentSetDataSourceSpi dsDataSource;
	protected DocumentDataSourceSpi dDataSource;
	protected ResolvedArtifactSource source;
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Override
    protected void setUp() 
	throws Exception
    {
	    super.setUp();
	    
		System.out.println("java.protocol.handler.pkgs is '" + System.getProperty("java.protocol.handler.pkgs") + "'.");

		URL url = new URL("vistaimaging://localhost:9300");
		source = getTestResolvedSite();
		
		dsDataSource = getProvider().createDocumentSetDataSource(source, "vistaimaging");
		dDataSource = getProvider().createDocumentDataSource(source, "vistaimaging");
    }

	public DocumentSetDataSourceSpi getDocumentSetDataSource()
    {
    	return dsDataSource;
    }
	
	public RoutingToken getRoutingToken()
	{
		return source.getArtifactSource().createRoutingToken();
	}

	public DocumentDataSourceSpi getDocumentDataSource()
    {
    	return dDataSource;
    }

	public void testAvailability() 
	throws MalformedURLException
	{
		assertNotNull( getDocumentSetDataSource() );
		assertNotNull( getDocumentDataSource() );
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
	
	public void testGetDocumentSetsStringFilter()
	throws URNFormatException, IOException
	{
		for(String[] testPatientIcn : testPatientIcns)
		{
			DocumentFilter filter = new DocumentFilter(testPatientIcn[PATIENT_ICN_INDEX]);
			testGetDocumentSets(filter);
		}
	}
	
	public void testGetDocumentSetsDateFilter()
	throws URNFormatException, IOException, ParseException
	{
		DateFormat df = new SimpleDateFormat("ddMMyyyy");
		Date fromDate = df.parse("01012000");
		Date toDate = df.parse("21312009");
		
		for(String[] testPatientIcn : testPatientIcns)
		{
			DocumentFilter filter = new DocumentFilter(testPatientIcn[PATIENT_ICN_INDEX], fromDate, toDate);
			testGetDocumentSets(filter);
		}
	}
	
	public void testGetDocumentSetsClassFilter()
	throws URNFormatException, IOException, ParseException
	{
		DateFormat df = new SimpleDateFormat("ddMMyyyy");
		
		for(String[] testPatientIcn : testPatientIcns)
		{
			DocumentFilter filter = new DocumentFilter(testPatientIcn[PATIENT_ICN_INDEX], "1");
			testGetDocumentSets(filter);
		}
	}
	
	public void testGetDocumentSetsClassAndDateFilter()
	throws URNFormatException, IOException, ParseException
	{
		DateFormat df = new SimpleDateFormat("ddMMyyyy");
		Date fromDate = df.parse("01012000");
		Date toDate = df.parse("21312009");
		
		for(String[] testPatientIcn : testPatientIcns)
		{
			DocumentFilter filter = new DocumentFilter(testPatientIcn[PATIENT_ICN_INDEX], fromDate, toDate, "1");
			testGetDocumentSets(filter);
		}
	}
	
	public void testGetDocumentSets(DocumentFilter filter) 
	throws URNFormatException, IOException
	{
		try
        {
	        SortedSet<DocumentSet> documentSets = getDocumentSetDataSource().getPatientDocumentSets(getRoutingToken(), 
	        		filter).getArtifacts();
	        
	        System.out.println("Patient '" + filter.getPatientId() + "' filtered by '" + filter.toString() + "' has " + documentSets.size() + " document sets.");
	        for(DocumentSet documentSet : documentSets)
	        {
		        System.out.println("Patient '" + filter.getPatientId() + "' document set '" + documentSet.getIdentifier() + "' has " + documentSet.size() + " documents.");
	        	for(Document document : documentSet)
	        	{
	        		System.out.println("Getting document '" + document.toString() + "'.");
	        		URN urn = document.getDocumentUrn();
	        		String imageId = urn.toString();
	        		
	        		GlobalArtifactIdentifier gai = null;
	        		try
	        		{
	        			gai = GlobalArtifactIdentifierFactory.create(imageId, "", "");	        		
	        		}
	        		catch(GlobalArtifactIdentifierFormatException gaifX)
	        		{
	        			throw new URNFormatException(gaifX);
	        		}
	        		ImageStreamResponse imageStream = getDocumentDataSource().getDocument(gai);
	        		DataSourceInputStream inStream = imageStream.getImageStream();
	        		while(inStream.getInputStream().read() >= 0);
	        	}
	        }
        } 
		catch (UnsupportedOperationException e)
        {
	        e.printStackTrace();
	        fail(e.getMessage());
        } 
		catch (MethodException e)
        {
			logger.warn(e.getMessage());
        } 
		catch (ConnectionException e)
        {
	        e.printStackTrace();
	        fail(e.getMessage());
        }
	}
}
