/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 9, 2013
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWPETERB
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
package gov.va.med.configuration;


import gov.va.med.imaging.notifications.email.NotificationEmailConfiguration;
import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author VHAISWPETERB
 *
 */
public class TestEmailConfiguration extends TestCase{

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMaxMsgCount(){
		NotificationEmailConfiguration config = NotificationEmailConfiguration.getConfiguration();
		int maxCountValue = config.getMaximumMessageCountPerEmail();
		assertTrue(maxCountValue == 100);
	}

	@Test
	public void testMaxByteSize(){
		NotificationEmailConfiguration config = NotificationEmailConfiguration.getConfiguration();
		int maxSize = config.getMaximumByteSizePerEmail();
		assertTrue(maxSize == (5*1024*1024));
	}
}
