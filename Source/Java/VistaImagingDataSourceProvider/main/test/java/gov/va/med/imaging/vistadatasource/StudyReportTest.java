/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 14, 2009
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */
package gov.va.med.imaging.vistadatasource;

import gov.va.med.imaging.protocol.vista.AbstractVistaConnectionTest;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaImagingCommonUtilities;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

/**
 * @author vhaiswwerfej
 *
 */
public class StudyReportTest 
extends AbstractVistaConnectionTest 
{
	
	private int []illegalCharacters = new int [] 
       {
			0, 1, 2, 3, 4, 5, 6, 7, 8, 11, 12, 14, 15, 16, 
			17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 
			29, 30, 31
		};
	
	
	public void testExtractingIllegalCharactersFromReport()
	{
		try
		{
			StringBuilder sb = new StringBuilder();
			String reportFile = "studyReport.report";
			InputStream inStream = getClass().getClassLoader().getResourceAsStream(reportFile);
			LineNumberReader reader = new LineNumberReader( new InputStreamReader(inStream) );

			for( String line = reader.readLine(); line != null; line = reader.readLine() )
				sb.append(line + "\n");
			
            reader.close();
            
            String report = VistaImagingCommonUtilities.extractInvalidCharactersFromReport(sb.toString());
            
            for(int i : illegalCharacters)
            {
            	String s = String.valueOf((char)i);
            	if(report.contains(s))
            	{
            		fail("Report contains illegal character '" + s + "', decimal value=" + i);
            	}
            }
		}
		catch(Exception ex)
		{
			fail(ex.getMessage());
		}		
	}

}
