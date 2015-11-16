/*
 * Created on Nov 18, 2005
 *
 */
package gov.va.med.imaging.dicom.dcftoolkit.test;

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
public class DicomDCFCommonTestBase extends TestCase {
  
    public static Logger TESTLOGGER = LogManager.getLogger("JUNIT_TEST");
    public static Logger logger = LogManager.getRootLogger();
    public static ConsoleAppender appender;


    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        
        appender = new ConsoleAppender(new PatternLayout());
        TESTLOGGER.addAppender(appender);
        TESTLOGGER.setLevel(Level.DEBUG);
        logger.addAppender(appender);
        logger.setLevel(Level.DEBUG);
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        TESTLOGGER.removeAllAppenders();
        logger.removeAllAppenders();
        LogManager.shutdown();
    }

    /**
     * Constructor for DicomDCFSCUTestBase.
     * @param arg0
     */
    public DicomDCFCommonTestBase(String arg0) {
        super(arg0);
    }

}
