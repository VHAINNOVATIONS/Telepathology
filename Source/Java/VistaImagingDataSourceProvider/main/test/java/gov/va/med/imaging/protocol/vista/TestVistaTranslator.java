package gov.va.med.imaging.protocol.vista;

import gov.va.med.OID;
import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.artifactsource.ArtifactSourceMemento;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.enums.StudyDeletedImageState;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import junit.framework.TestCase;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Ignore;

public class TestVistaTranslator 
extends TestCase
{
	private Logger logger = Logger.getLogger(this.getClass());
	private TestPointStimulusAndResults[] testPoints;
	
	@Override
    protected void setUp() 
	throws Exception
    {
	    super.setUp();
	    Logger.getRootLogger().setLevel(Level.INFO);
	    
	    testPoints = new TestPointStimulusAndResults[1];
	    
	    // first test point
	    String[] lines = new String[]
	    {
    		"8", 
    		"NEXT_STUDY||712",
    		"STUDY_IEN|712",
    		"STUDY_PAT|1011|9217103663V710366|IMAGPATIENT1011,1011",
    		"NEXT_SERIES",
    		"SERIES_IEN|712",
    		"NEXT_IMAGE",
    		"IMAGE_IEN|713",
    		"IMAGE_INFO|B2^713^\\\\isw-werfelj-lt\\image1$\\DM\\00\\07\\DM000713.TGA^\\\\isw-werfelj-lt\\image1$\\DM\\00\\07\\DM000713.ABS^040600-28 CHEST SINGLE VIEW^3000406.1349^3^CR^04/06/2000^^M^A^^^1^1^SLC^^^1011^IMAGPATIENT1011,1011^CLIN^^^^"
	    };
	    List<gov.va.med.imaging.protocol.vista.VistaCommonTranslator.ParsedVistaLine> expectedLines = 
	    	new ArrayList<gov.va.med.imaging.protocol.vista.VistaCommonTranslator.ParsedVistaLine>();
	    
	    gov.va.med.imaging.protocol.vista.VistaCommonTranslator.ParsedVistaLine currentLine = null;
	    
	    gov.va.med.imaging.protocol.vista.VistaCommonTranslator.ParsedVistaLine rootLine = 
	    	new gov.va.med.imaging.protocol.vista.VistaCommonTranslator.ParsedVistaLine("NEXT_STUDY", new String[]{"", "712"});
	    rootLine.addProperty("STUDY_IEN", new String[]{"712"});
	    rootLine.addProperty("STUDY_PAT", new String[]{"1011", "9217103663V710366", "IMAGPATIENT1011,1011"});
	    currentLine = rootLine.addChild("NEXT_SERIES", null);
	    currentLine.addProperty("SERIES_IEN", new String[]{"712"});
	    currentLine = currentLine.addChild("NEXT_IMAGE", null);
	    currentLine.addProperty("IMAGE_IEN", new String[]{"713"});
	    currentLine.addProperty("IMAGE_INFO", new String[]{"B2^713^\\\\isw-werfelj-lt\\image1$\\DM\\00\\07\\DM000713.TGA^\\\\isw-werfelj-lt\\image1$\\DM\\00\\07\\DM000713.ABS^040600-28 CHEST SINGLE VIEW^3000406.1349^3^CR^04/06/2000^^M^A^^^1^1^SLC^^^1011^IMAGPATIENT1011,1011^CLIN^^^^"});
	    
	    expectedLines.add( rootLine );
	    
	    gov.va.med.imaging.protocol.vista.VistaCommonTranslator.OntologyDelimiterKey[] delimiterKeys = 
	    	new gov.va.med.imaging.protocol.vista.VistaCommonTranslator.OntologyDelimiterKey[]
		    {
		    		new gov.va.med.imaging.protocol.vista.VistaCommonTranslator.OntologyDelimiterKey("NEXT_STUDY"),
		    		new gov.va.med.imaging.protocol.vista.VistaCommonTranslator.OntologyDelimiterKey("NEXT_SERIES"),
		    		new gov.va.med.imaging.protocol.vista.VistaCommonTranslator.OntologyDelimiterKey("NEXT_IMAGE")
		    };
	    
	    testPoints[0] = new TestPointStimulusAndResults(lines, delimiterKeys, expectedLines);
    }
	
	/**
	 * 
	 */
	public void testParseLines()
	{
		for( TestPointStimulusAndResults testPoint : testPoints )
		{
			List<gov.va.med.imaging.protocol.vista.VistaCommonTranslator.ParsedVistaLine> actualResult = 
				VistaCommonTranslator.parseLines(testPoint.lines, testPoint.delimiterKeys, true);

			Iterator<gov.va.med.imaging.protocol.vista.VistaCommonTranslator.ParsedVistaLine> actualResultLines = actualResult.iterator();
			Iterator<gov.va.med.imaging.protocol.vista.VistaCommonTranslator.ParsedVistaLine> expectedResultLines = testPoint.expectedLines.iterator();
			
			assertEquals(testPoint.expectedLines.size(), actualResult.size());
			for( ; actualResultLines.hasNext() && expectedResultLines.hasNext(); )
			{
				gov.va.med.imaging.protocol.vista.VistaCommonTranslator.ParsedVistaLine actualLine = actualResultLines.next();
				gov.va.med.imaging.protocol.vista.VistaCommonTranslator.ParsedVistaLine expectedLine = expectedResultLines.next();
				
				assertEquals(expectedLine, actualLine);
			}
		}
	}	
	
	private String[] studyGraphFiles = new String[]
	{
		"clinImages.studyGraph", "noSeries.studyGraph", "singleImageStudy.studyGraph", "studyGraph1.studyGraph"	
	};
	
	private Site getSite()
	{
		Site site = new SLCSite();
		return site;
	}
	
	public void testStudyWithModality()
	{
		String studyGraphFile = "studyGraphWithModality.studyGraph";
		Site site = getSite();
		List<String> lines = new ArrayList<String>(); 
		try
        {
			InputStream inStream = getClass().getClassLoader().getResourceAsStream(studyGraphFile);
			LineNumberReader reader = new LineNumberReader( new InputStreamReader(inStream) );

			for( String line = reader.readLine(); line != null; line = reader.readLine() )
				lines.add(line);
			
            reader.close();
        } 
		catch (IOException e)
        {
			e.printStackTrace();
			fail(e.getMessage());
        }
		
		SortedSet<Study> studies = 
			VistaImagingTranslator.createStudiesFromGraph( site, lines.toArray(new String[lines.size()]), StudyLoadLevel.FULL, StudyDeletedImageState.cannotIncludeDeletedImages );
		validateStudiesHaveModality(studies);
	}
	
	private void validateStudiesHaveModality(SortedSet<Study> studies)
	{
		logger.info("Validating " + studies.size() + " studies have modality");
		for(Study study : studies)
		{
			assertNotNull("Null Modalities", study.getModalities());
			assertNotSame("0 modalities found", 0, study.getModalities().size());			
		}
	}
	
	public void testParseLinesFiles()
	{
		Site site = getSite();
		
		for(String studyGraphFile : studyGraphFiles)
		{
			logger.debug("Opening '" + studyGraphFile + "'");
			List<String> lines = new ArrayList<String>(); 
			try
            {
				InputStream inStream = getClass().getClassLoader().getResourceAsStream(studyGraphFile);
				LineNumberReader reader = new LineNumberReader( new InputStreamReader(inStream) );

				for( String line = reader.readLine(); line != null; line = reader.readLine() )
					lines.add(line);
				
	            reader.close();
            } 
			catch (IOException e)
            {
				e.printStackTrace();
				fail(e.getMessage());
            }
			
			SortedSet<Study> studies = 
				VistaImagingTranslator.createStudiesFromGraph( site, lines.toArray(new String[lines.size()]), StudyLoadLevel.FULL, StudyDeletedImageState.cannotIncludeDeletedImages );
		}
	}

	@Ignore
	class TestPointStimulusAndResults
	{
		private final String[] lines;
		private final gov.va.med.imaging.protocol.vista.VistaCommonTranslator.OntologyDelimiterKey[] delimiterKeys;
		private final List<gov.va.med.imaging.protocol.vista.VistaCommonTranslator.ParsedVistaLine> expectedLines;
		
		public TestPointStimulusAndResults(
				String[] lines, 
				gov.va.med.imaging.protocol.vista.VistaCommonTranslator.OntologyDelimiterKey[] delimiterKeys, 
				List<gov.va.med.imaging.protocol.vista.VistaCommonTranslator.ParsedVistaLine> expectedLines)
        {
	        super();
	        this.lines = lines;
	        this.delimiterKeys = delimiterKeys;
	        this.expectedLines = expectedLines;
        }

		public String[] getLines()
        {
        	return lines;
        }

		public gov.va.med.imaging.protocol.vista.VistaCommonTranslator.OntologyDelimiterKey[] getDelimiterKeys()
        {
        	return delimiterKeys;
        }

		public List<gov.va.med.imaging.protocol.vista.VistaCommonTranslator.ParsedVistaLine> getExpectedLines()
        {
        	return expectedLines;
        }
	}
}
