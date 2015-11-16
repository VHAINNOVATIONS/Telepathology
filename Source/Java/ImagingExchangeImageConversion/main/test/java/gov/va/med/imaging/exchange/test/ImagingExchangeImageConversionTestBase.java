/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: August 10, 2006
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWTITTOC
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

package gov.va.med.imaging.exchange.test;

import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public abstract class ImagingExchangeImageConversionTestBase extends TestCase {
	protected final Log logger = LogFactory.getLog(getClass());
	protected static ClassPathXmlApplicationContext springFactory;
	
	static {
		springFactory = new ClassPathXmlApplicationContext(new String[] {"imageConversionUnitTestContext.xml"});
	}
	
	public ImagingExchangeImageConversionTestBase() {
		super();
	}
	
	public ImagingExchangeImageConversionTestBase(String name) {
		super(name);
	}
	
/*	public void init() {
		String classPath = System.getProperty("java.class.path");
		System.out.println("ImagingExchangeCommonTestBase reports: java.class.path="+classPath);
	}
*/	
	
}
