/*
 * Created on Apr 17, 2006
 *
 */
package gov.va.med.imaging.dicom.test;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public class TGAFileParserTest extends DicomDCFUtilitiesTestBase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TGAFileParserTest.class);
    }

    /*
     * @see DicomDCFUtilitiesTestBase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        //Initialize DicomGatewayConfiguration static object.
        //Set Modality.dic file used by testing for Pete's stuff.
        //Set CT_Parameter.dic file used by testing for Pete's stuff.
        //Set the date of the CT_Parameter.dic file.
        //Set the date of the Modality.dic file.
        //Set the Site Code to 660.

    }

    /*
     * @see DicomDCFUtilitiesTestBase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Constructor for TGAFileParserTest.
     * @param arg0
     */
    public TGAFileParserTest(String arg0) {
        super(arg0);
    }
    
    public void testCTParameterOne(){
        
        //Create dummy DDS based on ct image used by testing for Pete's stuff.
            //Fill in basic, bogus Patient info.
            //Fill in Manufacturer, Model, and Modality Code.
            //Fill in Group 28 stuff.
            //Fill in Group 7FE0 pixel data as in LegacyTextFileParser class.
            //Fill in Study Date.
        
        //Create fixed 512x512 TGA file used for pixel data.  It is irrelevent what it
        //  contains.

        //Create OriginalPixelDataInfo object.
            //Modify it to match data used by testing for Pete's stuff.
        
        //Instantiate LegacyTGAFileParser class.
        //Call updateDicomDataSetwithPixelData() method.
        //Call encapsulateDicomDataSet() method to retrieve the DDS object.
        //Check values in Group 28 fields.  Confirm if they match the desired values
        //  predetermine by this test.  If match, test was a success.
    }

}
