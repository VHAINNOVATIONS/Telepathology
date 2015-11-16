/*
 * Created on Nov 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.dicom.test;

import junit.framework.TestCase;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public class DicomDCFUtilitiesTestBase extends TestCase {
  
    protected static Logger testLogger = LogManager.getLogger("JUNIT_TEST");
    protected static Logger logger = LogManager.getRootLogger();
    public static ConsoleAppender appender;


    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        appender = new ConsoleAppender(new PatternLayout());
        testLogger.addAppender(appender);
        testLogger.setLevel(Level.INFO);
        logger.addAppender(appender);
        logger.setLevel(Level.DEBUG);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        testLogger.removeAllAppenders();
        //LOGGER1.removeAllAppenders();
        LogManager.shutdown();
    }

    /**
     * Constructor for DicomDCFUtilitiesTestBase.
     * @param arg0
     */
    public DicomDCFUtilitiesTestBase(String arg0) {
        super(arg0);
    }

}
