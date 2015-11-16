/*
 * Created on Nov 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.pixeledit;

import gov.va.med.imaging.dicom.dcftoolkit.utilities.reconstitution.DicomFileExtractor;
import junit.framework.TestCase;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
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
public class PixelEditTestBase extends TestCase {
  
    protected static Logger testLogger = Logger.getLogger("JUNIT_TEST");
    private static Logger logger = Logger.getLogger (PixelEditTestBase.class);
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
        testLogger.shutdown();
        logger.removeAllAppenders();
        logger.shutdown();
    }

    /**
     * Constructor for PixelEditTestBase.
     * @param arg0
     */
    public PixelEditTestBase(String arg0) {
        super(arg0);
    }

}
