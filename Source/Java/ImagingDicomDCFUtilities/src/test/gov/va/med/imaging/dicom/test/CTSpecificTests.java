/*
 * Created on Oct 27, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.dicom.test;

import gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet;
import gov.va.med.imaging.dicom.dcftoolkit.common.impl.DicomDataSetImpl;
import gov.va.med.imaging.dicom.dcftoolkit.utilities.reconstitution.LegacyTGAFileParser;
import gov.va.med.imaging.dicom.dcftoolkit.utilities.reconstitution.LegacyTextFileParser;
import gov.va.med.imaging.dicom.dcftoolkit.utilities.reconstitution.OriginalPixelDataInfo;
import gov.va.med.imaging.exchange.business.dicom.DicomGatewayConfiguration;

import com.lbs.DCS.AttributeTag;
import com.lbs.DCS.DicomDataSet;
import com.lbs.DCS.UID;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public class CTSpecificTests extends DicomDCFUtilitiesTestBase {

    //public static void main(String[] args) {
    //    junit.textui.TestRunner.run(CTSpecificTests.class);
    //}

    /*
     * @see DicomDCFSCUTestBase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        //Set Modality.dic file used by testing for Pete's stuff.
        DicomGatewayConfiguration.getInstance().setModalityFileName("Modality_test.dic");
        //Set CT_Parameter.dic file used by testing for Pete's stuff.
        DicomGatewayConfiguration.getInstance().setCTParametersFileName("CT_Param_test.dic");
        //Set the date of the CT_Parameter.dic file.
        //DicomGatewayConfiguration.getInstance().setCTParametersTimeStamp("09012006");
        DicomGatewayConfiguration.getInstance().setCTParametersTimeStamp("1-SEP-2006 00:00:00");
        
        //Set the date of the Modality.dic file.
    }

    /*
     * @see DicomDCFSCUTestBase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Constructor for CTSpecificTests.
     * @param arg0
     */
    public CTSpecificTests(String arg0) {
        super(arg0);
    }

    public void testCTParameterOne(){

        //Set the Site Code to 660.
        DicomGatewayConfiguration.getInstance().setLocation("660");
        IDicomDataSet dds = new DicomDataSetImpl();
        DicomDataSet toolkitDDS;
        LegacyTextFileParser textParser = new LegacyTextFileParser();
        OriginalPixelDataInfo pixelModule = new OriginalPixelDataInfo();
        boolean failure = false;
        
        try{
            dds = (IDicomDataSet)textParser.createDicomDataSet(".\\testdata\\EmptyParamsHdr.txt", pixelModule);
        //Instantiate LegacyTGAFileParser class.
            LegacyTGAFileParser tgaParser = new LegacyTGAFileParser();
            toolkitDDS = (DicomDataSet)dds.getDicomDataSet();
        //Call updateDicomDataSetwithPixelData() method.
            dds.setAcquisitionSite("660");
            tgaParser.updateDicomDataSetWithPixelData(dds, ".\\testdata\\testParams.tga", pixelModule);
        //Call encapsulateDicomDataSet() method to retrieve the DDS object.
            toolkitDDS = (DicomDataSet)dds.getDicomDataSet();
        //Check values in Group 28 fields.  Confirm if they match the desired values
        //  predetermine by this test.  If match, test was a success.
            System.out.println(toolkitDDS.toString());
            
            if(!(toolkitDDS.getElementStringValue(new AttributeTag("0008,0016")).equals(UID.SOPCLASSCT))){
                System.out.println("Not a CT IOD.");
               failure = true;
            }
            if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,0100")) 
                    == 16)){
                System.out.println("BitsAllocated field is not correct.");
                failure = true;
            }
            if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,0101")) 
                    == 12)){
                System.out.println("BitsStored field is not correct.");
                failure = true;
            }
            if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,0102"))
                    == 11)){
                System.out.println("HighBit field is not correct.");
                failure = true;
            }
            if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,0103"))
                    == 0)){
                System.out.println("Pixel Representation is not 0.");
            }
            if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,1052")) 
                    == -1024)){
                System.out.println("Rescale Intercept field is not correct.");
                failure = true;
            }
            if(toolkitDDS.containsElement(new AttributeTag("0028,2110"))){
                System.out.println("Lossy Compression should not exist.");
                failure = true;
            }
            
            if(failure == true){
                System.out.println("Test One Failed.\n");
                fail();
            }
            System.out.println("Test One Passed.\n");
        }
        catch(Exception e){
            e.printStackTrace();
            fail();
        }
    }

    public void testCTParameterTwo(){

        DicomGatewayConfiguration.getInstance().setLocation("660");
        IDicomDataSet dds = new DicomDataSetImpl();
        DicomDataSet toolkitDDS;
        LegacyTextFileParser textParser = new LegacyTextFileParser();
        OriginalPixelDataInfo pixelModule = new OriginalPixelDataInfo();
        boolean failure = false;
        
        try{
            dds = (IDicomDataSet)textParser.createDicomDataSet(".\\testdata\\ParamsHdr.txt", pixelModule);
        //Instantiate LegacyTGAFileParser class.
            LegacyTGAFileParser tgaParser = new LegacyTGAFileParser();
            toolkitDDS = (DicomDataSet)dds.getDicomDataSet();
        //Call updateDicomDataSetwithPixelData() method.
            dds.setAcquisitionSite("660");
            tgaParser.updateDicomDataSetWithPixelData(dds, ".\\testdata\\testParams.tga", pixelModule);
        //Call encapsulateDicomDataSet() method to retrieve the DDS object.
            toolkitDDS = (DicomDataSet)dds.getDicomDataSet();
        //Check values in Group 28 fields.  Confirm if they match the desired values
        //  predetermine by this test.  If match, test was a success.
            System.out.println(toolkitDDS.toString());
            
            if(!(toolkitDDS.getElementStringValue(new AttributeTag("0008,0016")).equals(UID.SOPCLASSCT))){
                System.out.println("Not a CT IOD.");
               failure = true;
            }
            if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,0100")) 
                    == 16)){
                System.out.println("BitsAllocated field does not match.");
                failure = true;
            }
            if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,0101")) 
                    == 12)){
                System.out.println("BitsStored field does not match.");
                failure = true;
            }
            if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,0102"))
                    == 11)){
                System.out.println("HighBit field is not correct.");
                failure = true;
            }
            if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,0103"))
                    == 0)){
                System.out.println("Pixel Representation is not 0.");
                failure = true;
            }
            if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,1052")) 
                    == -2048)){
                System.out.println("Rescale Intercept field is not correct.");
                failure = true;
            }
            
            if(failure == true){
                System.out.println("Test Two Failed.\n");
                fail();
            }
            System.out.println("Test Two Passed.\n");
        }
        catch(Exception e){
            e.printStackTrace();
            fail();
        }
    }

    public void testCTParameterThree(){

        DicomGatewayConfiguration.getInstance().setLocation("660");
        IDicomDataSet dds = new DicomDataSetImpl();
        DicomDataSet toolkitDDS;
        LegacyTextFileParser textParser = new LegacyTextFileParser();
        OriginalPixelDataInfo pixelModule = new OriginalPixelDataInfo();
        boolean failure = false;
        
        try{
            dds = (IDicomDataSet)textParser.createDicomDataSet(".\\testdata\\NoParamsHdr.txt", pixelModule);
        //Instantiate LegacyTGAFileParser class.
            LegacyTGAFileParser tgaParser = new LegacyTGAFileParser();
            toolkitDDS = (DicomDataSet)dds.getDicomDataSet();
        //Call updateDicomDataSetwithPixelData() method.
            dds.setAcquisitionSite("661");
            tgaParser.updateDicomDataSetWithPixelData(dds, ".\\testdata\\testParams.tga", pixelModule);
        //Call encapsulateDicomDataSet() method to retrieve the DDS object.
            toolkitDDS = (DicomDataSet)dds.getDicomDataSet();
        //Check values in Group 28 fields.  Confirm if they match the desired values
        //  predetermine by this test.  If match, test was a success.
            System.out.println(toolkitDDS.toString());
            
            if(!(toolkitDDS.getElementStringValue(new AttributeTag("0008,0016")).equals(UID.SOPCLASSSECONDARYCAPTURE))){
                System.out.println("Not a SC IOD.");
               failure = true;
            }
            if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,0100")) 
                    == 16)){
                System.out.println("BitsAllocated field does not match.");
                failure = true;
            }
            if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,0101")) 
                    == 12)){
                System.out.println("BitsStored field does not match.");
                failure = true;
            }
            if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,0102"))
                    == 11)){
                System.out.println("HighBit field is not correct.");
                failure = true;
            }
            if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,0103"))
                    == 0)){
                System.out.println("Pixel Representation is not 0.");
                failure = true;
            }
            if(toolkitDDS.containsElement(new AttributeTag("0028,1052"))){
                System.out.println("Rescale Intercept should not exist.");
                failure = true;
            }            
            if(toolkitDDS.containsElement(new AttributeTag("0028,1053"))){
                System.out.println("Rescale Slope should not exist.");
                failure = true;
            }
            if(toolkitDDS.containsElement(new AttributeTag("0028,2110"))){
                System.out.println("Lossy Compression should not exist.");
                failure = true;
            }

            if(failure == true){
                System.out.println("Test Three Failed.\n");
                fail();
            }
            System.out.println("Test Three Passed.\n");
        }
        catch(Exception e){
            e.printStackTrace();
            fail();
        }
    }
    
    public void testCTParameterFour(){

        DicomGatewayConfiguration.getInstance().setLocation("660");
        IDicomDataSet dds = new DicomDataSetImpl();
        DicomDataSet toolkitDDS;
        LegacyTextFileParser textParser = new LegacyTextFileParser();
        OriginalPixelDataInfo pixelModule = new OriginalPixelDataInfo();
        boolean failure = false;
        
        try{
            dds = (IDicomDataSet)textParser.createDicomDataSet(".\\testdata\\NoParamsHdr_SD070105.txt", pixelModule);
        //Instantiate LegacyTGAFileParser class.
            LegacyTGAFileParser tgaParser = new LegacyTGAFileParser();
            toolkitDDS = (DicomDataSet)dds.getDicomDataSet();
        //Call updateDicomDataSetwithPixelData() method.
            dds.setAcquisitionSite("661");
            tgaParser.updateDicomDataSetWithPixelData(dds, ".\\testdata\\testParams.tga", pixelModule);
        //Call encapsulateDicomDataSet() method to retrieve the DDS object.
            toolkitDDS = (DicomDataSet)dds.getDicomDataSet();
        //Check values in Group 28 fields.  Confirm if they match the desired values
        //  predetermine by this test.  If match, test was a success.
            System.out.println(toolkitDDS.toString());
            
            if(!(toolkitDDS.getElementStringValue(new AttributeTag("0008,0016")).equals(UID.SOPCLASSCT))){
                System.out.println("Not a CT IOD.");
               failure = true;
            }
            if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,0100")) 
                    == 16)){
                System.out.println("BitsAllocated field does not match.");
                failure = true;
            }
            if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,0101")) 
                    == 12)){
                System.out.println("BitsStored field does not match.");
                failure = true;
            }
            if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,0102"))
                    == 11)){
                System.out.println("HighBit field is not correct.");
                failure = true;
            }
            if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,0103"))
                    == 0)){
                System.out.println("Pixel Representation is not 0.");
                failure = true;
            }
            if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,1052")) 
                    == -2048)){
                System.out.println("Rescale Intercept field is not correct.");
                failure = true;
            }
            
            if(failure == true){
                System.out.println("Test Four Failed.\n");
                fail();
            }
            System.out.println("Test Four Passed.\n");
        }
        catch(Exception e){
            e.printStackTrace();
            fail();
        }
    }

    public void testCTParameterFive(){

        DicomGatewayConfiguration.getInstance().setLocation("660");
        IDicomDataSet dds = new DicomDataSetImpl();
        DicomDataSet toolkitDDS;
        LegacyTextFileParser textParser = new LegacyTextFileParser();
        OriginalPixelDataInfo pixelModule = new OriginalPixelDataInfo();
        boolean failure = false;
        
        try{
            dds = (IDicomDataSet)textParser.createDicomDataSet(".\\testdata\\NoParamsHdr_SD070106.txt", pixelModule);
        //Instantiate LegacyTGAFileParser class.
            LegacyTGAFileParser tgaParser = new LegacyTGAFileParser();
            toolkitDDS = (DicomDataSet)dds.getDicomDataSet();
        //Call updateDicomDataSetwithPixelData() method.
            dds.setAcquisitionSite("661");
            tgaParser.updateDicomDataSetWithPixelData(dds, ".\\testdata\\testParams.tga", pixelModule);
        //Call encapsulateDicomDataSet() method to retrieve the DDS object.
            toolkitDDS = (DicomDataSet)dds.getDicomDataSet();
        //Check values in Group 28 fields.  Confirm if they match the desired values
        //  predetermine by this test.  If match, test was a success.
            System.out.println(toolkitDDS.toString());
            
            if(!(toolkitDDS.getElementStringValue(new AttributeTag("0008,0016")).equals(UID.SOPCLASSCT))){
                System.out.println("Not a CT IOD.");
               failure = true;
            }
            if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,0100")) 
                    == 16)){
                System.out.println("BitsAllocated field does not match.");
                failure = true;
            }
            if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,0101")) 
                    == 12)){
                System.out.println("BitsStored field does not match.");
                failure = true;
            }
            if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,0102"))
                    == 11)){
                System.out.println("HighBit field is not correct.");
                failure = true;
            }
            if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,0103"))
                    == 0)){
                System.out.println("Pixel Representation is not 0.");
                failure = true;
            }
            if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,1052")) 
                    == -1024)){
                System.out.println("Rescale Intercept field is not correct.");
                failure = true;
            }
            if(toolkitDDS.containsElement(new AttributeTag("0028,2110"))){
                System.out.println("Lossy Compression field is not correct.");
                failure = true;
            }
            
            if(failure == true){
                System.out.println("Test Five Failed.\n");
                fail();
            }
            System.out.println("Test Five Passed.\n");
        }
        catch(Exception e){
            e.printStackTrace();
            fail();
        }
    }

    public void testCTParameterSix(){

        DicomGatewayConfiguration.getInstance().setLocation("660");
        IDicomDataSet dds = new DicomDataSetImpl();
        DicomDataSet toolkitDDS;
        LegacyTextFileParser textParser = new LegacyTextFileParser();
        OriginalPixelDataInfo pixelModule = new OriginalPixelDataInfo();
        boolean failure = false;
        
        try{
            dds = (IDicomDataSet)textParser.createDicomDataSet(".\\testdata\\NoParamsHdr_SD101506.txt", pixelModule);
        //Instantiate LegacyTGAFileParser class.
            LegacyTGAFileParser tgaParser = new LegacyTGAFileParser();
            toolkitDDS = (DicomDataSet)dds.getDicomDataSet();
        //Call updateDicomDataSetwithPixelData() method.
            dds.setAcquisitionSite("661");
            tgaParser.updateDicomDataSetWithPixelData(dds, ".\\testdata\\testParams.tga", pixelModule);
        //Call encapsulateDicomDataSet() method to retrieve the DDS object.
            toolkitDDS = (DicomDataSet)dds.getDicomDataSet();
        //Check values in Group 28 fields.  Confirm if they match the desired values
        //  predetermine by this test.  If match, test was a success.
            System.out.println(toolkitDDS.toString());
            
            if(!(toolkitDDS.getElementStringValue(new AttributeTag("0008,0016")).equals(UID.SOPCLASSCT))){
                System.out.println("Not a CT IOD.");
               failure = true;
            }
            if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,0100")) 
                    == 16)){
                System.out.println("BitsAllocated field does not match.");
                failure = true;
            }
            if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,0101")) 
                    == 12)){
                System.out.println("BitsStored field does not match.");
                failure = true;
            }
            if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,0102"))
                    == 11)){
                System.out.println("HighBit field is not correct.");
                failure = true;
            }
            if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,0103"))
                    == 0)){
                System.out.println("Pixel Representation is not 0.");
                failure = true;
            }
            if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,1052")) 
                    == -3072)){
                System.out.println("Rescale Intercept field is not correct.");
                failure = true;
            }
            
            if(failure == true){
                System.out.println("Test Six Failed.\n");
                fail();
            }
            System.out.println("Test Six Passed.\n");
        }
        catch(Exception e){
            e.printStackTrace();
            fail();
        }
    }
    
    public void testCTParameterSeven(){

        System.out.println("Testing Patch 66 IR 8 and 9.");
        System.out.println("Calculate proper Window and Center values.");
        System.out.println("Remove Window Width and Center Explanation element.");
        DicomGatewayConfiguration.getInstance().setLocation("660");
        IDicomDataSet dds = new DicomDataSetImpl();
        DicomDataSet toolkitDDS;
        LegacyTextFileParser textParser = new LegacyTextFileParser();
        OriginalPixelDataInfo pixelModule = new OriginalPixelDataInfo();
        boolean failure = false;
        
        try{
            dds = (IDicomDataSet)textParser.createDicomDataSet(".\\testdata\\InflatedWindowCenterValues.txt", pixelModule);
        //Instantiate LegacyTGAFileParser class.
            LegacyTGAFileParser tgaParser = new LegacyTGAFileParser();
            toolkitDDS = (DicomDataSet)dds.getDicomDataSet();
        //Call updateDicomDataSetwithPixelData() method.
            dds.setAcquisitionSite("661");
            tgaParser.updateDicomDataSetWithPixelData(dds, ".\\testdata\\testParams.tga", pixelModule);
        //Call encapsulateDicomDataSet() method to retrieve the DDS object.
            toolkitDDS = (DicomDataSet)dds.getDicomDataSet();
        //Check values in Group 28 fields.  Confirm if they match the desired values
        //  predetermine by this test.  If match, test was a success.
            System.out.println(toolkitDDS.toString());
            
            if(!(toolkitDDS.getElementStringValue(new AttributeTag("0028,1050")).equals("2034"))){
                System.out.println("Window Center element does not contain correct value.");
                failure = true;
            }
            if(!(toolkitDDS.getElementStringValue(new AttributeTag("0028,1051")).equals("4069"))){
                System.out.println("Window Width element does not contain correct value.");
                failure = true;
            }
            if(!(toolkitDDS.getElementStringValue(new AttributeTag("0028,1055")).equals("Bogus Explanation"))){
                System.out.println("Window Width & Center Explanation does not contain correct value.");
                failure = true;
            }            

            if(failure == true){
                System.out.println("Test Seven Failed.\n");
                fail();
            }
            System.out.println("Test Seven Passed.\n");
        }
        catch(Exception e){
            e.printStackTrace();
            fail();
        }
    }

	public void testCTParameterEight(){
	
	    DicomGatewayConfiguration.getInstance().setLocation("660");
	    IDicomDataSet dds = new DicomDataSetImpl();
	    DicomDataSet toolkitDDS;
	    LegacyTextFileParser textParser = new LegacyTextFileParser();
	    OriginalPixelDataInfo pixelModule = new OriginalPixelDataInfo();
	    boolean failure = false;
	    
	    try{
	        dds = (IDicomDataSet)textParser.createDicomDataSet(".\\testdata\\DCMParamsHdr.txt", pixelModule);
	    //Instantiate LegacyTGAFileParser class.
	        LegacyTGAFileParser tgaParser = new LegacyTGAFileParser();
	        toolkitDDS = (DicomDataSet)dds.getDicomDataSet();
	    //Call updateDicomDataSetwithPixelData() method.
	        dds.setAcquisitionSite("660");
	        tgaParser.updateDicomDataSetWithPixelData(dds, ".\\testdata\\testParams.tga", pixelModule);
	    //Call encapsulateDicomDataSet() method to retrieve the DDS object.
	        toolkitDDS = (DicomDataSet)dds.getDicomDataSet();
	    //Check values in Group 28 fields.  Confirm if they match the desired values
	    //  predetermine by this test.  If match, test was a success.
	        System.out.println(toolkitDDS.toString());
	        
	        if(!(toolkitDDS.getElementStringValue(new AttributeTag("0008,0016")).equals(UID.SOPCLASSSECONDARYCAPTURE))){
	            System.out.println("Not a SC IOD.");
	           failure = true;
	        }
	        if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,0100")) 
	                == 16)){
	            System.out.println("BitsAllocated field does not match.");
	            failure = true;
	        }
	        if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,0101")) 
	                == 12)){
	            System.out.println("BitsStored field does not match.");
	            failure = true;
	        }
	        if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,0102"))
	                == 11)){
	            System.out.println("HighBit field is not correct.");
	            failure = true;
	        }
	        if(!(toolkitDDS.getElementIntValue(new AttributeTag("0028,0103"))
	                == 0)){
	            System.out.println("Pixel Representation is not 0.");
	            failure = true;
	        }
	        if(toolkitDDS.containsElement(new AttributeTag("0028,1052"))){
	            System.out.println("Rescale Intercept should not exist.");
	            failure = true;
	        }            
	        if(toolkitDDS.containsElement(new AttributeTag("0028,1053"))){
	            System.out.println("Rescale Slope should not exist.");
	            failure = true;
	        }
            if(toolkitDDS.containsElement(new AttributeTag("0028,2110"))){
                System.out.println("Lossy Compression should not exist.");
                failure = true;
            }
	
	        if(failure == true){
	            System.out.println("Test Eight Failed.\n");
	            fail();
	        }
	        System.out.println("Test Eight Passed.\n");
	    }
	    catch(Exception e){
	        e.printStackTrace();
	        fail();
	    }
	}
    

}
