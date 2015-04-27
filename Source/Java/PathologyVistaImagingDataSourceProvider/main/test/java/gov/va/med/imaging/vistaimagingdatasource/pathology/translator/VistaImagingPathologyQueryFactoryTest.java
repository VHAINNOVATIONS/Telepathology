/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 5, 2012
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
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
package gov.va.med.imaging.vistaimagingdatasource.pathology.translator;

import java.util.List;

import gov.va.med.imaging.vistaimagingdatasource.pathology.query.VistaImagingPathologyQueryFactory;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author VHAISWWERFEJ
 *
 */
public class VistaImagingPathologyQueryFactoryTest
{
	
	@Test
	public void testLongLineSplitting()
	{
		
		String input = "<?xml version=\"1.0\" encoding=\"utf-8\"?><ReportTemplate xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><ReportTypeShort>EM</ReportTypeShort><ReportTypeLong>ElectronMicroscopy</ReportTypeLong><ReportFields><ReportFieldTemplat><DatabaseName>SPECIMEN</DatabaseName><DisplayName>Specimen";
		
		List<String> lines = VistaImagingPathologyQueryFactory.splitLineIntoShorterLines(input);
		/*
		for(String line : lines)
		{
			System.out.println(line);
		}*/
		assertEquals(2, lines.size());
	}

}
