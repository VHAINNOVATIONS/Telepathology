/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: August 24, 2006
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWPETRB
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
package gov.va.med.imaging.exchange.business;

import junit.framework.TestCase;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 *
 * @author William Peterson
 *
 */
public class DicomCommonTestBase extends TestCase {
    
    public static Logger TESTLOGGER = LogManager.getLogger("JUNIT_TEST");
    public static Logger logger = LogManager.getLogger(DicomCommonTestBase.class);
    public static ConsoleAppender appender;


    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        appender = new ConsoleAppender(new PatternLayout());
        TESTLOGGER.addAppender(appender);
        TESTLOGGER.setLevel(Level.INFO);
        logger.addAppender(appender);
        logger.setLevel(Level.ERROR);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        TESTLOGGER.removeAllAppenders();
        LogManager.shutdown();

    }

    /**
     * Constructor for DicomCommonTestBase.
     * @param arg0
     */
    public DicomCommonTestBase(String arg0) {
        super(arg0);
    }

}
