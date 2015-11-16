/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Oct 10, 2008
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWBECKEC
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
package gov.va.med.imaging.core;

import gov.va.med.URNFactory;
import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.core.interfaces.router.Command;
import gov.va.med.imaging.core.interfaces.router.CommandContext;
import gov.va.med.imaging.core.interfaces.router.CommandFactory;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.ImageAccessLogEvent;
import gov.va.med.imaging.exchange.ImageAccessLogEvent.ImageAccessLogEventType;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.storage.cache.DODSourcedCache;
import gov.va.med.imaging.exchange.storage.cache.VASourcedCache;
import gov.va.med.imaging.router.commands.PostImageAccessEventCommandImpl;
import gov.va.med.imaging.router.commands.PrefetchPatientIdentificationImageCommandImpl;
import gov.va.med.imaging.router.commands.PrefetchPatientStudyListCommandImpl;

import java.util.Date;

import junit.framework.TestCase;

/**
 * @author VHAISWBECKEC
 *
 */
public class CommandFactoryImplTest 
extends TestCase
{
	private CommandFactory factory;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		this.factory = new CommandFactoryImpl((CommandContext)null);
	}

	protected CommandFactory getFactory()
    {
    	return factory;
    }

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		this.factory = null;
		super.tearDown();
	}

	/**
	 * Test method for {@link gov.va.med.imaging.core.CommandFactoryImpl#createCreateImageAccessEventCommand(gov.va.med.imaging.exchange.ImageAccessLogEvent)}.
	 */
	public final void testCreateLogImageAccessEventCommand()
	{
		ImageAccessLogEvent event = new ImageAccessLogEvent("ien", null, "icn", "site", System.currentTimeMillis(), "reason", "reasonDescription", ImageAccessLogEventType.IMAGE_ACCESS, "200");
		Command<?> command = 
			getFactory().createCommand(java.lang.Void.class, 
					"PostImageAccessEventCommand", null,
					new Class<?>[]{ImageAccessLogEvent.class},
					new Object[]{event});
		
		assertExpectedType(command, PostImageAccessEventCommandImpl.class );
	}

	/**
	 * Test method for {@link gov.va.med.imaging.core.CommandFactoryImpl#createPrefetchPatientIdentificationImageCommand(java.lang.String, java.lang.String)}.
	 */
	public final void testCreatePrefetchPatientIdentificationImageCommand()
	{
		Command<?> command = getFactory().createCommand(java.lang.Void.class, 
				"PrefetchPatientIdentificationImageCommand", null, 
				new Class<?>[]{String.class, String.class},
				new Object[]{"icn", "site"});
		assertExpectedType(command, PrefetchPatientIdentificationImageCommandImpl.class );
	}

	/**
	 * Test method for {@link gov.va.med.imaging.core.CommandFactoryImpl#createPrefetchPatientStudyListCommand(java.lang.String, java.lang.String, gov.va.med.imaging.exchange.business.StudyFilter, gov.va.med.imaging.exchange.business.ImageFormatQualityList)}.
	 * @throws URNFormatException 
	 */
	public final void testCreatePrefetchPatientStudyListCommand() 
	throws URNFormatException
	{
		StudyFilter filter = new StudyFilter(new Date(0L), new Date(), URNFactory.create("urn:vastudy:200-300-400", StudyURN.class));
		ImageFormatQualityList formatList = new ImageFormatQualityList();
		
		Command<?> command = getFactory().createCommand(java.lang.Void.class, 
				"PrefetchPatientStudyListCommand", null, 
				new Class<?>[]{
		 		 String.class, String.class, StudyFilter.class, ImageFormatQualityList.class
				},
				new Object[]{"site", "patient", filter, formatList});
		assertExpectedType(command, PrefetchPatientStudyListCommandImpl.class );
	}

	public final void testCreateCommand() 
	throws URNFormatException
	{
		String siteNumber = "660";
		String patientIcn = "6553321";
		StudyFilter filter = new StudyFilter(new Date(0L), new Date(), URNFactory.create("urn:vastudy:200-300-400", StudyURN.class));
		ImageFormatQualityList formatList = new ImageFormatQualityList();
		
		Command<?> command = getFactory().createCommand(
				java.lang.Void.class, 
				"PrefetchPatientStudyListCommand",
				null, 
				new Class<?>[]{
			 		 String.class, String.class, StudyFilter.class, ImageFormatQualityList.class
	  			},
				new Object[] {siteNumber, patientIcn, filter, formatList}
		);
		
		assertExpectedType(command, PrefetchPatientStudyListCommandImpl.class);
	}

	private void assertExpectedType(Object obj, Class<?> expectedType)
	{
		assertTrue(
			"Command should be of type " + PostImageAccessEventCommandImpl.class.getName() + 
			" but is " + (obj == null ? "null" : obj.getClass().getName()), 
			expectedType.isInstance(obj) 
		);
	}
	

}
