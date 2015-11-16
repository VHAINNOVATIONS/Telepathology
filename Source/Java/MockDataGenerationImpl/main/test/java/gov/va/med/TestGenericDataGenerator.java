/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Apr 28, 2010
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author vhaiswbeckec
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */

package gov.va.med;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import gov.va.med.GenericDataGenerator.Mode;
import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class TestGenericDataGenerator
extends TestCase
{
	private GenericDataGenerator generator = null;
	
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		this.generator = new GenericDataGenerator(Mode.RANDOMIZE);
		generator.registerDataGenerator(new ClinicalDisplayDataGenerator(this.generator.getConfiguration()));
	}

	@Override
	protected void tearDown() throws Exception
	{
		this.generator = null;
		super.tearDown();
	}

	protected GenericDataGenerator getGenerator()
	{
		return this.generator;
	}

	/**
	 * Test method for {@link gov.va.med.GenericDataGenerator#createInstance(java.lang.Class)}.
	 */
	public void testCreateInstanceClass()
	{
		gov.va.med.imaging.exchange.business.Study study = 
			getGenerator().createInstance(gov.va.med.imaging.exchange.business.Study.class);
		assertNotNull(study);
	}

	/**
	 * Test method for {@link gov.va.med.GenericDataGenerator#createCollectionInstance(java.lang.Class, java.lang.Class, gov.va.med.InstancePopulation, gov.va.med.AggregationPopulation, gov.va.med.CompositionPopulation, gov.va.med.ReferenceMap, java.lang.Object[])}.
	 */
	public void testCreateCollectionInstance()
	{
		Collection<gov.va.med.imaging.exchange.business.Study> studies = 
			getGenerator().createCollectionInstance(List.class, gov.va.med.imaging.exchange.business.Study.class);
		assertNotNull(studies);
		for(gov.va.med.imaging.exchange.business.Study study : studies)
			assertNotNull(study);
	}

	/**
	 * Test method for {@link gov.va.med.GenericDataGenerator#createMapInstance(java.lang.Class, java.lang.Class, java.lang.Class, gov.va.med.InstancePopulation, gov.va.med.AggregationPopulation, gov.va.med.CompositionPopulation, gov.va.med.ReferenceMap, java.lang.Object[])}.
	 */
	public void testCreateMapInstance()
	{
		Map<String, gov.va.med.imaging.exchange.business.Study> studies = 
			getGenerator().createMapInstance(Map.class, String.class, gov.va.med.imaging.exchange.business.Study.class);
		assertNotNull(studies);
		for(Map.Entry<String, gov.va.med.imaging.exchange.business.Study> study : studies.entrySet())
		{
			assertNotNull(study);
			assertNotNull(study.getKey());
			assertNotNull(study.getValue());
		}
	}
	
	/**
	 * 
	 */
	public void testCreateInputStream()
	{
		InputStream inStream;

		for(String mediaType : new String[]{
			"application/dicom", "application/postscript","application/zip",
			"image/gif", "image/jpeg", "image/png", "image/x-tga",
			"video/mpeg", "video/quicktime"})
		{
			try
			{
				inStream = getGenerator().getInputStream(mediaType);
				assertNotNull("Error getting stream with media type '" + mediaType + "'.", inStream);
				inStream.close();
			}
			catch (IOException x)
			{
				fail(x.getMessage());
			}
		}
	}
}
