/*
 * Created on Nov 18, 2005
 *
 */
package gov.va.med.imaging.dicom.test;

import gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet;
import gov.va.med.imaging.dicom.dcftoolkit.utilities.reconstitution.LegacyTextFileParser;
import gov.va.med.imaging.dicom.dcftoolkit.utilities.reconstitution.OriginalPixelDataInfo;
import gov.va.med.imaging.exceptions.TextFileException;
import gov.va.med.imaging.exceptions.TextFileExtractionException;
import gov.va.med.imaging.exceptions.TextFileNotFoundException;
import gov.va.med.imaging.exchange.business.dicom.DicomServerConfiguration;

import java.util.ArrayList;
import java.util.Iterator;

import com.lbs.DCS.AttributeTag;
import com.lbs.DCS.DCM;
import com.lbs.DCS.DCSException;
import com.lbs.DCS.DicomAEElement;
import com.lbs.DCS.DicomASElement;
import com.lbs.DCS.DicomATElement;
import com.lbs.DCS.DicomCSElement;
import com.lbs.DCS.DicomDAElement;
import com.lbs.DCS.DicomDSElement;
import com.lbs.DCS.DicomDTElement;
import com.lbs.DCS.DicomDataSet;
import com.lbs.DCS.DicomElement;
import com.lbs.DCS.DicomFDElement;
import com.lbs.DCS.DicomFLElement;
import com.lbs.DCS.DicomFileOutput;
import com.lbs.DCS.DicomISElement;
import com.lbs.DCS.DicomLOElement;
import com.lbs.DCS.DicomLTElement;
import com.lbs.DCS.DicomOBElement;
import com.lbs.DCS.DicomOFElement;
import com.lbs.DCS.DicomOWElement;
import com.lbs.DCS.DicomPNElement;
import com.lbs.DCS.DicomSHElement;
import com.lbs.DCS.DicomSLElement;
import com.lbs.DCS.DicomSQElement;
import com.lbs.DCS.DicomSSElement;
import com.lbs.DCS.DicomSTElement;
import com.lbs.DCS.DicomTMElement;
import com.lbs.DCS.DicomUIElement;
import com.lbs.DCS.DicomULElement;
import com.lbs.DCS.DicomUNElement;
import com.lbs.DCS.DicomUSElement;
import com.lbs.DCS.DicomUTElement;
import com.lbs.DCS.UID;
import com.lbs.DCS.VRValidator;
import com.lbs.DCS.ValidationErrorList;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public class TextFileParserTest extends DicomDCFUtilitiesTestBase {

    /*
     * @see DicomDCFSCUTestBase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see DicomDCFSCUTestBase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Constructor for TextFileParserTest.
     * @param arg0
     */
    public TextFileParserTest(String arg0) {
        super(arg0);
   }
    
   
    public void testFileParserOne(){
        
        testLogger.info("Starting Test 1.");
        String textFilename = ".\\testdata\\Test1.txt";
        DicomDataSet dds;
        dds = this.parseToDDS(textFilename);
        this.writeToDisk("output1.dcm", dds);
       testLogger.info("Test 1 Completed Successfully...");

    }

    public void testFileParserTwo(){
        
        testLogger.info("Starting Test 2.");
        String textFilename = ".\\testdata\\Test2.txt";
        DicomDataSet dds;
        dds = this.parseToDDS(textFilename);
        this.writeToDisk("output2.dcm", dds);
        testLogger.info("Test 2 Completed Successfully...");
   }

    public void testFileParserThree(){
        
        testLogger.info("Starting Test 3.");
        String textFilename = ".\\testdata\\Test3.txt";
        DicomDataSet dds;
        dds = this.parseToDDS(textFilename);
        this.writeToDisk("output3.dcm", dds);
       testLogger.info("Test 3 Completed Successfully...");
    }

    public void testFileParserFour(){
        
        testLogger.info("Starting Test 4.");
        String textFilename = ".\\testdata\\Test4.txt";
        DicomDataSet dds;
        dds = this.parseToDDS(textFilename);
        this.writeToDisk("output4.dcm", dds);
        testLogger.info("Test 4 Completed Successfully...");
    }

    public void testFileParserFive(){
        
       testLogger.info("Starting Test 5.");
       String textFilename = ".\\testdata\\Test5.txt";
       DicomDataSet dds;
       dds = this.parseToDDS(textFilename);
       this.writeToDisk("output5.dcm", dds);
       testLogger.info("Test 5 Completed Successfully...");
     }
   
    public void testFileParserSix(){
        
        testLogger.info("Starting Test 6.");
        String textFilename = ".\\testdata\\Test6.txt";
        DicomDataSet dds;
        dds = this.parseToDDS(textFilename);
        this.writeToDisk("output6.dcm", dds);
        testLogger.info("Test 6 Completed Successfully...");
     }

    public void testFileParserSeven(){
        
        testLogger.info("Starting Test 7.");
        String textFilename = ".\\testdata\\Test7.txt";
        DicomDataSet dds;
        dds = this.parseToDDS(textFilename);
        this.writeToDisk("output7.dcm", dds);
        testLogger.info("Test 7 Completed Successfully...");
     }

    public void testFileParserEight(){
        
        testLogger.info("Starting Test 8.");
        String textFilename = ".\\testdata\\Test8.txt";
        DicomDataSet dds;
        dds = this.parseToDDS(textFilename);
        this.writeToDisk("output8.dcm", dds);
        testLogger.info("Test 8 Completed Successfully...");
     }

    public void testFileParserNine(){
        
        testLogger.info("Starting Test 9.");
        String textFilename = ".\\testdata\\Test9.txt";
        DicomDataSet dds;
        dds = this.parseToDDS(textFilename);
        this.writeToDisk("output9.dcm", dds);
        testLogger.info("Test 9 Completed Successfully...");
     }

    public void testFileParserTen(){
        
        testLogger.info("Starting Test 10.");
        String textFilename = ".\\testdata\\cha_cr.txt";
        DicomDataSet dds;
        dds = this.parseToDDS(textFilename);
        this.writeToDisk("output10.dcm", dds);
        testLogger.info("Test 10 Completed Successfully...");
     }

/*
    public void testFileParserEleven(){
        TESTLOGGER.info("Starting Test 11.  Empty DICOM Data Section.");
        String textFilename = ".\\testdata\\test11.txt";
        DicomDataSet dds;
        dds = this.parseToDDS(textFilename);
        TESTLOGGER.info("Test 11 Completed.  A thrown Exception is considered successful for this test.");
    }
*/

    public void testFileParserTwelve(){
        
        testLogger.info("Starting Test 12.");
        String textFilename = ".\\testdata\\test12.txt";
        DicomDataSet dds;
        dds = this.parseToDDS(textFilename);
        this.writeToDisk("output12.dcm", dds);
        testLogger.info("Test 12 Completed Successfully...");
     }
    
    public void testFileParserThirteen(){
        
        testLogger.info("Starting Test 13.");
        String textFilename = ".\\testdata\\test13.txt";
        DicomDataSet dds;
        dds = this.parseToDDS(textFilename);
        this.writeToDisk("output13.dcm", dds);
        testLogger.info("Test 13 Completed Successfully...");
     }
    
    public void testFileParserFourteen(){
        
        testLogger.info("Starting Test 14.");
        String textFilename = ".\\testdata\\test14.txt";
        DicomDataSet dds;
        dds = this.parseToDDS(textFilename);
        this.writeToDisk("output14.dcm", dds);
        testLogger.info("Test 14 Completed Successfully...");
     }

    
    public void testFileParserFifteen(){
	    
	    testLogger.info("Starting Test 15.");
	    String textFilename = ".\\testdata\\test15.txt";
	    DicomDataSet dds;
	    dds = this.parseToDDS(textFilename);
	    this.writeToDisk("output15.dcm", dds);
	    testLogger.info("Test 15 Completed Successfully...");
	 }

    public void testFileParserSixteen(){
	    
	    testLogger.info("Starting Test 16.");
	    String textFilename = ".\\testdata\\test16.txt";
	    DicomDataSet dds;
	    dds = this.parseToDDS(textFilename);
	    this.writeToDisk("output16.dcm", dds);
	    testLogger.info("Test 16 Completed Successfully...");
	 }

    public void testFileParserSeventeen(){
	    
	    testLogger.info("Starting Test 17.");
	    String textFilename = ".\\testdata\\test17.txt";
	    DicomDataSet dds;
	    dds = this.parseToDDS(textFilename);
	    this.writeToDisk("output17.dcm", dds);
	    testLogger.info("Test 17 Completed Successfully...");
	 }

    public void testFileParserEightteen(){
	    
	    testLogger.info("Starting Test 18.");
	    String textFilename = ".\\testdata\\test18.txt";
	    DicomDataSet dds;
	    dds = this.parseToDDS(textFilename);
	    this.writeToDisk("output18.dcm", dds);
	    testLogger.info("Test 18 Completed Successfully...");
	 }

    public void testFileParserNineteen(){
	    
	    testLogger.info("Starting Test 19.");
	    String textFilename = ".\\testdata\\test19.txt";
	    DicomDataSet dds;
	    dds = this.parseToDDS(textFilename);
	    this.writeToDisk("output19.dcm", dds);
	    testLogger.info("Test 19 Completed Successfully...");
	 }

    public void testFileParserVIXImage(){
        
        testLogger.info("Starting VIX Test.");
        String textFilename = ".\\testdata\\vix0001.txt";
        DicomDataSet dds;
        dds = this.parseToDDS(textFilename);
        this.writeToDisk("vix0001.dcm", dds);
        testLogger.info("VIX Test Completed Successfully...");
     }

    private DicomDataSet parseToDDS(String textFilename){
        
        //FUTURE Change to pass Exceptions to the various tests.  Do not pass/fail test inside
        //  of exceptions in this method.  Gives misleading results.
        try{
            LegacyTextFileParser textParser = new LegacyTextFileParser();
            IDicomDataSet dds;
            DicomDataSet dicomDataSet;
            OriginalPixelDataInfo pixelInfo = new OriginalPixelDataInfo();
            dds = textParser.createDicomDataSet(textFilename, pixelInfo);
            dicomDataSet = (DicomDataSet) dds.getDicomDataSet();
            //if(dicomDataSet){
            //    throw new TextFileExtractionException();
            //}
            //if(dds.getSeriesInstanceUID().length() < 2){
            //    throw new TextFileExtractionException();
            //}
            //if(dds.getSOPInstanceUID().length() < 2){
            //    throw new TextFileExtractionException();
            //}
            
            testLogger.info("Final DataSet:\n" + dicomDataSet.toString());
            this.removeSelectedBadVRElements(dicomDataSet);
            
            return dicomDataSet;
        }
        catch(TextFileNotFoundException noFile){
            testLogger.info("Did not find Text File " + textFilename);
            fail("Test failed.");
        }
        catch(TextFileException fileException){
            testLogger.info("General problem with " + textFilename);
            fail("Test failed.");
        }
        catch(TextFileExtractionException fileExtract){
            fileExtract.printStackTrace();
            testLogger.info("Did not extract data properly from " + textFilename);
            fail("Test failed.");
        }
        return null;
    }
    
    private void writeToDisk(String filename, DicomDataSet dds){
        try{
        	assertFalse(dds.containsElement(new AttributeTag(0x0002,0x0003)));
            DicomFileOutput out = new DicomFileOutput(filename, UID.TRANSFERLITTLEENDIANEXPLICIT, true, true);
            out.open();
            out.writeDataSet(dds);
            out.close();
        }
        catch (DCSException dcse){
            System.out.println("Failed to make DICOM file, "+filename);
        }
    }

	private void removeSelectedBadVRElements(DicomDataSet dds){
		ArrayList<String> removeList = DicomServerConfiguration.getConfiguration().getRemovedElements();
		if(removeList != null){
			Iterator<String> iter = removeList.iterator();
			while(iter.hasNext()){
				String tagNumber  = iter.next();
				try{
					AttributeTag tag = new AttributeTag(tagNumber);
					if(dds.containsElement(tag)){
						DicomElement element = dds.findElement(tag);
						short vr = element.vr();
	
						try{
							switch (vr){
								case DCM.VR_AE: 
									VRValidator.instance().validateAE((DicomAEElement)element);
									break;
								case DCM.VR_AS: 
									VRValidator.instance().validateAS((DicomASElement)element);
									break;
								case DCM.VR_AT: 
									VRValidator.instance().validateAT((DicomATElement)element);
									break;
								case DCM.VR_CS: 
									VRValidator.instance().validateCS((DicomCSElement)element);
									break;
								case DCM.VR_DA: 
									VRValidator.instance().validateDA((DicomDAElement)element);
									break;
								case DCM.VR_DS: 
									VRValidator.instance().validateDS((DicomDSElement)element);
									break;
								case DCM.VR_DT: 
									VRValidator.instance().validateDT((DicomDTElement)element);
									break;
								case DCM.VR_FD: 
									VRValidator.instance().validateFD((DicomFDElement)element);
									break;
								case DCM.VR_FL: 
									VRValidator.instance().validateFL((DicomFLElement)element);
									break;
								case DCM.VR_IS: 
									VRValidator.instance().validateIS((DicomISElement)element);
									break;
								case DCM.VR_LO: 
									VRValidator.instance().validateLO((DicomLOElement)element);
									break;
								case DCM.VR_LT: 
									VRValidator.instance().validateLT((DicomLTElement)element);
									break;
								case DCM.VR_PN: 
									VRValidator.instance().validatePN((DicomPNElement)element);
									break;
								case DCM.VR_SH: 
									VRValidator.instance().validateSH((DicomSHElement)element);
									break;
								case DCM.VR_SL: 
									VRValidator.instance().validateSL((DicomSLElement)element);
									break;
								case DCM.VR_SQ: 
									ValidationErrorList list = VRValidator.instance().validateSQ((DicomSQElement)element,"0");
									if(list.hasErrors()){
										throw new DCSException();
									}
									break;
								case DCM.VR_SS: 
									VRValidator.instance().validateSS((DicomSSElement)element);
									break;
								case DCM.VR_ST: 
									VRValidator.instance().validateST((DicomSTElement)element);
									break;
								case DCM.VR_TM: 
									VRValidator.instance().validateTM((DicomTMElement)element);
									break;
								case DCM.VR_UI: 
									VRValidator.instance().validateUI((DicomUIElement)element);
									break;
								case DCM.VR_UL: 
									VRValidator.instance().validateUL((DicomULElement)element);
									break;
								case DCM.VR_UN: 
									VRValidator.instance().validateUN((DicomUNElement)element);
									break;
								case DCM.VR_US: 
									VRValidator.instance().validateUS((DicomUSElement)element);
									break;
								case DCM.VR_UT: 
									VRValidator.instance().validateUT((DicomUTElement)element);
									break;
								case DCM.VR_OB: 
									VRValidator.instance().validateOB((DicomOBElement)element);
									break;
								case DCM.VR_OW: 
									VRValidator.instance().validateOW((DicomOWElement)element);
									break;
								case DCM.VR_OF: 
									VRValidator.instance().validateOF((DicomOFElement)element);
									break;
							}
						}
						catch(DCSException dcsX){
							dds.insert(tag, "");
						}
					}
				}
				catch(DCSException dcsX){
					//Do nothing with exception.
				}
			}
		}
	}
}
