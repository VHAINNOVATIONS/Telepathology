/*
 * Created on Oct 12, 2007
 *
 */
package gov.va.med.imaging.dicom.test.compositetests;

import gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet;
import gov.va.med.imaging.dicom.dcftoolkit.utilities.exceptions.TGAFileException;
import gov.va.med.imaging.dicom.dcftoolkit.utilities.exceptions.TGAFileNotFoundException;
import gov.va.med.imaging.dicom.dcftoolkit.utilities.reconstitution.LegacyTGAFileParser;
import gov.va.med.imaging.dicom.dcftoolkit.utilities.reconstitution.LegacyTextFileParser;
import gov.va.med.imaging.dicom.dcftoolkit.utilities.reconstitution.OriginalPixelDataInfo;
import gov.va.med.imaging.dicom.test.DicomDCFUtilitiesTestBase;
import gov.va.med.imaging.exceptions.TextFileException;
import gov.va.med.imaging.exceptions.TextFileExtractionException;
import gov.va.med.imaging.exceptions.TextFileNotFoundException;
import gov.va.med.imaging.exchange.business.dicom.DicomGatewayConfiguration;
import gov.va.med.imaging.exchange.business.dicom.DicomServerConfiguration;
import gov.va.med.imaging.exchange.business.dicom.exceptions.DicomException;

import java.util.ArrayList;
import java.util.HashMap;
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

public class TotalReconstitution extends DicomDCFUtilitiesTestBase {

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

    }

    /*
     * @see DicomDCFSCUTestBase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Constructor for TotalReconstitution.
     * @param arg0
     */
    public TotalReconstitution(String arg0) {
        super(arg0);
    }

    public void testTotalReconOne(){

        String textFilename = ".\\testdata\\CLE00016051073.txt";
        String tgaFilename = ".\\testdata\\CLE00016051073.tga";
        IDicomDataSet toolkitDDS;
        OriginalPixelDataInfo originalPixelData = new OriginalPixelDataInfo();
        try{
            testLogger.info("...parsing Text file "+textFilename+".");
            //Invoke the extraction of data from the Text file.
            LegacyTextFileParser textParser = new LegacyTextFileParser();
            toolkitDDS = textParser.createDicomDataSet(textFilename, originalPixelData);            
            testLogger.info("...parsing TGA/BIG file "+tgaFilename+".");
            
            HashMap<String, String> hisChanges = this.buildDummyHISHashMap();
            String acquisitionSite = null;
            if(hisChanges.containsKey("0032,1020")){
                acquisitionSite = (String)hisChanges.get("0032,1020");
                toolkitDDS.setAcquisitionSite(acquisitionSite);
            }
            //Invoke the extraction of pixel data from the Targa file.
            LegacyTGAFileParser tgaParser = new LegacyTGAFileParser();
            tgaParser.updateDicomDataSetWithPixelData(toolkitDDS, tgaFilename, originalPixelData);
            //Add the Vista HIS changes to the generic DicomDataSet object.  This is done
            // by calling Csaba's code.
            toolkitDDS.updateHISChangesToDDS(hisChanges);
            DicomDataSet dds = (DicomDataSet)toolkitDDS.getDicomDataSet();
            this.removeSelectedBadVRElements(dds);
            this.writeToDisk("CLE00016051073.dcm", dds);
        }
        catch(TextFileNotFoundException noText){
        	testLogger.error(noText.getMessage());
        	testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TextFileExtractionException extract){
        	testLogger.error(extract.getMessage());
        	testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TextFileException e){
        	testLogger.error(e.getMessage());
        	testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TGAFileNotFoundException notga){
        	testLogger.error(notga.getMessage());
        	testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TGAFileException badtga){
        	testLogger.error(badtga.getMessage());
        	testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(DicomException de){
        	testLogger.error(de.getMessage());
        	testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
    }

    public void testTotalReconTwo(){

        String textFilename = ".\\testdata\\CHA00005495957.txt";
        String tgaFilename = ".\\testdata\\CHA00005495957.big";
        IDicomDataSet toolkitDDS;
        OriginalPixelDataInfo originalPixelData = new OriginalPixelDataInfo();
        try{
        	testLogger.info("...parsing Text file "+textFilename+".");
            //Invoke the extraction of data from the Text file.
            LegacyTextFileParser textParser = new LegacyTextFileParser();
            toolkitDDS = textParser.createDicomDataSet(textFilename, originalPixelData);            
            testLogger.info("...parsing TGA/BIG file "+tgaFilename+".");
            
            HashMap<String, String> hisChanges = this.buildDummyHISHashMap();
            String acquisitionSite = null;
            if(hisChanges.containsKey("0032,1020")){
                acquisitionSite = (String)hisChanges.get("0032,1020");
                toolkitDDS.setAcquisitionSite(acquisitionSite);
            }
            //Invoke the extraction of pixel data from the Targa file.
            LegacyTGAFileParser tgaParser = new LegacyTGAFileParser();
            tgaParser.updateDicomDataSetWithPixelData(toolkitDDS, tgaFilename, originalPixelData);
            //Add the Vista HIS changes to the generic DicomDataSet object.  This is done
            // by calling Csaba's code.
            toolkitDDS.updateHISChangesToDDS(hisChanges);
            DicomDataSet dds = (DicomDataSet)toolkitDDS.getDicomDataSet();
            this.removeSelectedBadVRElements(dds);
            this.writeToDisk("CHA00005495957.dcm", dds);
        }
        catch(TextFileNotFoundException noText){
        	testLogger.error(noText.getMessage());
        	testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TextFileExtractionException extract){
        	testLogger.error(extract.getMessage());
        	testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TextFileException e){
        	testLogger.error(e.getMessage());
        	testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TGAFileNotFoundException notga){
        	testLogger.error(notga.getMessage());
        	testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TGAFileException badtga){
        	testLogger.error(badtga.getMessage());
        	testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(DicomException de){
        	testLogger.error(de.getMessage());
        	testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
    }

    public void testTotalReconThree(){

        String textFilename = ".\\testdata\\test13.txt";
        String tgaFilename = ".\\testdata\\test13.big";
        IDicomDataSet toolkitDDS;
        OriginalPixelDataInfo originalPixelData = new OriginalPixelDataInfo();
        try{
        	testLogger.info("...parsing Text file "+textFilename+".");
            //Invoke the extraction of data from the Text file.
            LegacyTextFileParser textParser = new LegacyTextFileParser();
            toolkitDDS = textParser.createDicomDataSet(textFilename, originalPixelData);            
            testLogger.info("...parsing TGA/BIG file "+tgaFilename+".");
            
            //HashMap hisChanges = this.buildDummyHISHashMap();
            //String acquisitionSite = null;
            //if(hisChanges.containsKey("0032,1020")){
            //    acquisitionSite = (String)hisChanges.get("0032,1020");
            //    toolkitDDS.setAcquisitionSite(acquisitionSite);
            //}
            //Invoke the extraction of pixel data from the Targa file.
            LegacyTGAFileParser tgaParser = new LegacyTGAFileParser();
            tgaParser.updateDicomDataSetWithPixelData(toolkitDDS, tgaFilename, originalPixelData);
            //Add the Vista HIS changes to the generic DicomDataSet object.  This is done
            // by calling Csaba's code.
            //toolkitDDS.updateHISChangesToDDS(hisChanges);
            DicomDataSet dds = (DicomDataSet)toolkitDDS.getDicomDataSet();
            this.removeSelectedBadVRElements(dds);            
            this.writeToDisk("outputTotalRecon13.dcm", dds);
        }
        catch(TextFileNotFoundException noText){
        	testLogger.error(noText.getMessage());
        	testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TextFileExtractionException extract){
        	testLogger.error(extract.getMessage());
        	testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TextFileException e){
        	testLogger.error(e.getMessage());
        	testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TGAFileNotFoundException notga){
        	testLogger.error(notga.getMessage());
        	testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TGAFileException badtga){
        	testLogger.error(badtga.getMessage());
        	testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
    }
    
    public void testTotalReconFour(){

        String textFilename = ".\\testdata\\CLE00017147655.txt";
        String tgaFilename = ".\\testdata\\CLE00017147655.big";
        IDicomDataSet toolkitDDS;
        OriginalPixelDataInfo originalPixelData = new OriginalPixelDataInfo();
        try{
        	testLogger.info("...parsing Text file "+textFilename+".");
            //Invoke the extraction of data from the Text file.
            LegacyTextFileParser textParser = new LegacyTextFileParser();
            toolkitDDS = textParser.createDicomDataSet(textFilename, originalPixelData);            
            testLogger.info("...parsing TGA/BIG file "+tgaFilename+".");
            
            //HashMap hisChanges = this.buildDummyHISHashMap();
            //String acquisitionSite = null;
            //if(hisChanges.containsKey("0032,1020")){
            //    acquisitionSite = (String)hisChanges.get("0032,1020");
            //    toolkitDDS.setAcquisitionSite(acquisitionSite);
            //}
            //Invoke the extraction of pixel data from the Targa file.
            LegacyTGAFileParser tgaParser = new LegacyTGAFileParser();
            tgaParser.updateDicomDataSetWithPixelData(toolkitDDS, tgaFilename, originalPixelData);
            //Add the Vista HIS changes to the generic DicomDataSet object.  This is done
            // by calling Csaba's code.
            //toolkitDDS.updateHISChangesToDDS(hisChanges);
            DicomDataSet dds = (DicomDataSet)toolkitDDS.getDicomDataSet();
            this.removeSelectedBadVRElements(dds);            
            this.writeToDisk("CLE00017147655.dcm", dds);
        }
        catch(TextFileNotFoundException noText){
        	testLogger.error(noText.getMessage());
        	testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TextFileExtractionException extract){
        	testLogger.error(extract.getMessage());
        	testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TextFileException e){
        	testLogger.error(e.getMessage());
        	testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TGAFileNotFoundException notga){
        	testLogger.error(notga.getMessage());
        	testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TGAFileException badtga){
        	testLogger.error(badtga.getMessage());
        	testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
    }


    public void testTotalReconFive(){

        String textFilename = ".\\testdata\\CHA00005568271.txt";
        String tgaFilename = ".\\testdata\\CHA00005568271.big";
        IDicomDataSet toolkitDDS;
        OriginalPixelDataInfo originalPixelData = new OriginalPixelDataInfo();
        try{
        	testLogger.info("...parsing Text file "+textFilename+".");
            //Invoke the extraction of data from the Text file.
            LegacyTextFileParser textParser = new LegacyTextFileParser();
            toolkitDDS = textParser.createDicomDataSet(textFilename, originalPixelData);            
            testLogger.info("...parsing TGA/BIG file "+tgaFilename+".");
            
            //HashMap hisChanges = this.buildDummyHISHashMap();
            //String acquisitionSite = null;
            //if(hisChanges.containsKey("0032,1020")){
            //    acquisitionSite = (String)hisChanges.get("0032,1020");
            //    toolkitDDS.setAcquisitionSite(acquisitionSite);
            //}
            //Invoke the extraction of pixel data from the Targa file.
            LegacyTGAFileParser tgaParser = new LegacyTGAFileParser();
            tgaParser.updateDicomDataSetWithPixelData(toolkitDDS, tgaFilename, originalPixelData);
            //Add the Vista HIS changes to the generic DicomDataSet object.  This is done
            // by calling Csaba's code.
            //toolkitDDS.updateHISChangesToDDS(hisChanges);
            DicomDataSet dds = (DicomDataSet)toolkitDDS.getDicomDataSet();
            this.removeSelectedBadVRElements(dds);            
            this.writeToDisk("CHA00005568271.dcm", dds);
        }
        catch(TextFileNotFoundException noText){
        	testLogger.error(noText.getMessage());
        	testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TextFileExtractionException extract){
        	testLogger.error(extract.getMessage());
        	testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TextFileException e){
        	testLogger.error(e.getMessage());
        	testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TGAFileNotFoundException notga){
        	testLogger.error(notga.getMessage());
        	testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TGAFileException badtga){
        	testLogger.error(badtga.getMessage());
        	testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
    }
    

    
    public void testTotalReconSix(){

        String textFilename = ".\\testdata\\STL00017421552.txt";
        String tgaFilename = ".\\testdata\\STL00017421552.big";
        IDicomDataSet toolkitDDS;
        OriginalPixelDataInfo originalPixelData = new OriginalPixelDataInfo();
        try{
        	testLogger.info("...parsing Text file "+textFilename+".");
            //Invoke the extraction of data from the Text file.
            LegacyTextFileParser textParser = new LegacyTextFileParser();
            toolkitDDS = textParser.createDicomDataSet(textFilename, originalPixelData);            
            testLogger.info("...parsing TGA/BIG file "+tgaFilename+".");
            
            //HashMap hisChanges = this.buildDummyHISHashMap();
            //String acquisitionSite = null;
            //if(hisChanges.containsKey("0032,1020")){
            //    acquisitionSite = (String)hisChanges.get("0032,1020");
            //    toolkitDDS.setAcquisitionSite(acquisitionSite);
            //}
            //Invoke the extraction of pixel data from the Targa file.
            LegacyTGAFileParser tgaParser = new LegacyTGAFileParser();
            tgaParser.updateDicomDataSetWithPixelData(toolkitDDS, tgaFilename, originalPixelData);
            //Add the Vista HIS changes to the generic DicomDataSet object.  This is done
            // by calling Csaba's code.
            //toolkitDDS.updateHISChangesToDDS(hisChanges);
            DicomDataSet dds = (DicomDataSet)toolkitDDS.getDicomDataSet();
            this.removeSelectedBadVRElements(dds);            
            this.writeToDisk("STL00017421552.dcm", dds);
        }
        catch(TextFileNotFoundException noText){
        	testLogger.error(noText.getMessage());
        	testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TextFileExtractionException extract){
        	testLogger.error(extract.getMessage());
        	testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TextFileException e){
        	testLogger.error(e.getMessage());
        	testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TGAFileNotFoundException notga){
        	testLogger.error(notga.getMessage());
        	testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TGAFileException badtga){
            testLogger.error(badtga.getMessage());
            testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
    }

    public void testTotalReconSeven(){

        String textFilename = ".\\testdata\\STL00017220475.txt";
        String tgaFilename = ".\\testdata\\STL00017220475.big";
        IDicomDataSet toolkitDDS;
        OriginalPixelDataInfo originalPixelData = new OriginalPixelDataInfo();
        try{
            testLogger.info("...parsing Text file "+textFilename+".");
            //Invoke the extraction of data from the Text file.
            LegacyTextFileParser textParser = new LegacyTextFileParser();
            toolkitDDS = textParser.createDicomDataSet(textFilename, originalPixelData);            
            testLogger.info("...parsing TGA/BIG file "+tgaFilename+".");
            
            //HashMap hisChanges = this.buildDummyHISHashMap();
            //String acquisitionSite = null;
            //if(hisChanges.containsKey("0032,1020")){
            //    acquisitionSite = (String)hisChanges.get("0032,1020");
            //    toolkitDDS.setAcquisitionSite(acquisitionSite);
            //}
            //Invoke the extraction of pixel data from the Targa file.
            LegacyTGAFileParser tgaParser = new LegacyTGAFileParser();
            tgaParser.updateDicomDataSetWithPixelData(toolkitDDS, tgaFilename, originalPixelData);
            //Add the Vista HIS changes to the generic DicomDataSet object.  This is done
            // by calling Csaba's code.
            //toolkitDDS.updateHISChangesToDDS(hisChanges);
            DicomDataSet dds = (DicomDataSet)toolkitDDS.getDicomDataSet();
            this.removeSelectedBadVRElements(dds);            
            this.writeToDisk("STL00017220475.dcm", dds);
        }
        catch(TextFileNotFoundException noText){
            testLogger.error(noText.getMessage());
            testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TextFileExtractionException extract){
            testLogger.error(extract.getMessage());
            testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TextFileException e){
            testLogger.error(e.getMessage());
            testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TGAFileNotFoundException notga){
            testLogger.error(notga.getMessage());
            testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TGAFileException badtga){
            testLogger.error(badtga.getMessage());
            testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
    }

    public void testTotalReconEight(){

        String textFilename = ".\\testdata\\test16.txt";
        String tgaFilename = ".\\testdata\\test13.big";
        IDicomDataSet toolkitDDS;
        OriginalPixelDataInfo originalPixelData = new OriginalPixelDataInfo();
        try{
            testLogger.info("...parsing Text file "+textFilename+".");
            //Invoke the extraction of data from the Text file.
            LegacyTextFileParser textParser = new LegacyTextFileParser();
            toolkitDDS = textParser.createDicomDataSet(textFilename, originalPixelData);            
            testLogger.info("...parsing TGA/BIG file "+tgaFilename+".");
            
            //HashMap hisChanges = this.buildDummyHISHashMap();
            //String acquisitionSite = null;
            //if(hisChanges.containsKey("0032,1020")){
            //    acquisitionSite = (String)hisChanges.get("0032,1020");
            //    toolkitDDS.setAcquisitionSite(acquisitionSite);
            //}
            //Invoke the extraction of pixel data from the Targa file.
            LegacyTGAFileParser tgaParser = new LegacyTGAFileParser();
            tgaParser.updateDicomDataSetWithPixelData(toolkitDDS, tgaFilename, originalPixelData);
            //Add the Vista HIS changes to the generic DicomDataSet object.  This is done
            // by calling Csaba's code.
            //toolkitDDS.updateHISChangesToDDS(hisChanges);
            DicomDataSet dds = (DicomDataSet)toolkitDDS.getDicomDataSet();
            this.removeSelectedBadVRElements(dds);            
            this.writeToDisk("TestRecon8.dcm", dds);
        }
        catch(TextFileNotFoundException noText){
            testLogger.error(noText.getMessage());
            testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TextFileExtractionException extract){
            testLogger.error(extract.getMessage());
            testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TextFileException e){
            testLogger.error(e.getMessage());
            testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TGAFileNotFoundException notga){
            testLogger.error(notga.getMessage());
            testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TGAFileException badtga){
            testLogger.error(badtga.getMessage());
            testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
    }

    public void testTotalReconNine(){

        String textFilename = ".\\testdata\\test17.txt";
        String tgaFilename = ".\\testdata\\test13.big";
        IDicomDataSet toolkitDDS;
        OriginalPixelDataInfo originalPixelData = new OriginalPixelDataInfo();
        try{
            testLogger.info("...parsing Text file "+textFilename+".");
            //Invoke the extraction of data from the Text file.
            LegacyTextFileParser textParser = new LegacyTextFileParser();
            toolkitDDS = textParser.createDicomDataSet(textFilename, originalPixelData);            
            testLogger.info("...parsing TGA/BIG file "+tgaFilename+".");
            
            //HashMap hisChanges = this.buildDummyHISHashMap();
            //String acquisitionSite = null;
            //if(hisChanges.containsKey("0032,1020")){
            //    acquisitionSite = (String)hisChanges.get("0032,1020");
            //    toolkitDDS.setAcquisitionSite(acquisitionSite);
            //}
            //Invoke the extraction of pixel data from the Targa file.
            LegacyTGAFileParser tgaParser = new LegacyTGAFileParser();
            tgaParser.updateDicomDataSetWithPixelData(toolkitDDS, tgaFilename, originalPixelData);
            //Add the Vista HIS changes to the generic DicomDataSet object.  This is done
            // by calling Csaba's code.
            //toolkitDDS.updateHISChangesToDDS(hisChanges);
            DicomDataSet dds = (DicomDataSet)toolkitDDS.getDicomDataSet();
            this.removeSelectedBadVRElements(dds);            
            this.writeToDisk("TestRecon9.dcm", dds);
        }
        catch(TextFileNotFoundException noText){
            testLogger.error(noText.getMessage());
            testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TextFileExtractionException extract){
            testLogger.error(extract.getMessage());
            testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TextFileException e){
            testLogger.error(e.getMessage());
            testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TGAFileNotFoundException notga){
            testLogger.error(notga.getMessage());
            testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TGAFileException badtga){
            testLogger.error(badtga.getMessage());
            testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
    }

    public void testTotalReconTen(){

        String textFilename = ".\\testdata\\STL00017421173.txt";
        String tgaFilename = ".\\testdata\\STL00017421173.big";
        IDicomDataSet toolkitDDS;
        OriginalPixelDataInfo originalPixelData = new OriginalPixelDataInfo();
        try{
            testLogger.info("...parsing Text file "+textFilename+".");
            //Invoke the extraction of data from the Text file.
            LegacyTextFileParser textParser = new LegacyTextFileParser();
            toolkitDDS = textParser.createDicomDataSet(textFilename, originalPixelData);            
            testLogger.info("...parsing TGA/BIG file "+tgaFilename+".");
            
            //HashMap hisChanges = this.buildDummyHISHashMap();
            //String acquisitionSite = null;
            //if(hisChanges.containsKey("0032,1020")){
            //    acquisitionSite = (String)hisChanges.get("0032,1020");
            //    toolkitDDS.setAcquisitionSite(acquisitionSite);
            //}
            //Invoke the extraction of pixel data from the Targa file.
            LegacyTGAFileParser tgaParser = new LegacyTGAFileParser();
            tgaParser.updateDicomDataSetWithPixelData(toolkitDDS, tgaFilename, originalPixelData);
            //Add the Vista HIS changes to the generic DicomDataSet object.  This is done
            // by calling Csaba's code.
            //toolkitDDS.updateHISChangesToDDS(hisChanges);
            DicomDataSet dds = (DicomDataSet)toolkitDDS.getDicomDataSet();
            this.removeSelectedBadVRElements(dds);            
            this.writeToDisk("STL00017421173.dcm", dds);
        }
        catch(TextFileNotFoundException noText){
            testLogger.error(noText.getMessage());
            testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TextFileExtractionException extract){
            testLogger.error(extract.getMessage());
            testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TextFileException e){
            testLogger.error(e.getMessage());
            testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TGAFileNotFoundException notga){
            testLogger.error(notga.getMessage());
            testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TGAFileException badtga){
            testLogger.error(badtga.getMessage());
            testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
    }
    
    public void testTotalReconEleven(){
	
	    String textFilename = ".\\testdata\\STL00017421174.txt";
	    String tgaFilename = ".\\testdata\\STL00017421174.tga";
	    IDicomDataSet toolkitDDS;
	    OriginalPixelDataInfo originalPixelData = new OriginalPixelDataInfo();
	    try{
	        testLogger.info("...parsing Text file "+textFilename+".");
	        //Invoke the extraction of data from the Text file.
	        LegacyTextFileParser textParser = new LegacyTextFileParser();
	        toolkitDDS = textParser.createDicomDataSet(textFilename, originalPixelData);            
	        testLogger.info("...parsing TGA/BIG file "+tgaFilename+".");
	        
	        //HashMap hisChanges = this.buildDummyHISHashMap();
	        //String acquisitionSite = null;
	        //if(hisChanges.containsKey("0032,1020")){
	        //    acquisitionSite = (String)hisChanges.get("0032,1020");
	        //    toolkitDDS.setAcquisitionSite(acquisitionSite);
	        //}
	        //Invoke the extraction of pixel data from the Targa file.
	        LegacyTGAFileParser tgaParser = new LegacyTGAFileParser();
	        tgaParser.updateDicomDataSetWithPixelData(toolkitDDS, tgaFilename, originalPixelData);
	        //Add the Vista HIS changes to the generic DicomDataSet object.  This is done
	        // by calling Csaba's code.
	        //toolkitDDS.updateHISChangesToDDS(hisChanges);
	        DicomDataSet dds = (DicomDataSet)toolkitDDS.getDicomDataSet();
	        this.removeSelectedBadVRElements(dds);            
	        this.writeToDisk("STL00017421173.dcm", dds);
	    }
	    catch(TextFileNotFoundException noText){
	        testLogger.error(noText.getMessage());
	        testLogger.error(this.getClass().getName()+": " +
	                "\nException thrown while assembling Dicom Object.");
	        fail();
	    }
	    catch(TextFileExtractionException extract){
	        testLogger.error(extract.getMessage());
	        testLogger.error(this.getClass().getName()+": " +
	                "\nException thrown while assembling Dicom Object.");
	        fail();
	    }
	    catch(TextFileException e){
	        testLogger.error(e.getMessage());
	        testLogger.error(this.getClass().getName()+": " +
	                "\nException thrown while assembling Dicom Object.");
	        fail();
	    }
	    catch(TGAFileNotFoundException notga){
	        testLogger.error(notga.getMessage());
	        testLogger.error(this.getClass().getName()+": " +
	                "\nException thrown while assembling Dicom Object.");
	        fail();
	    }
	    catch(TGAFileException badtga){
	        testLogger.error(badtga.getMessage());
	        testLogger.error(this.getClass().getName()+": " +
	                "\nException thrown while assembling Dicom Object.");
	        fail();
	    }
	}
    
	public void testTotalReconTwelve(){
		
	    String textFilename = ".\\testdata\\IOW00004823398.txt";
	    String tgaFilename = ".\\testdata\\IOW00004823398.tga";
	    IDicomDataSet toolkitDDS;
	    OriginalPixelDataInfo originalPixelData = new OriginalPixelDataInfo();
	    try{
	        testLogger.info("...parsing Text file "+textFilename+".");
	        //Invoke the extraction of data from the Text file.
	        LegacyTextFileParser textParser = new LegacyTextFileParser();
	        toolkitDDS = textParser.createDicomDataSet(textFilename, originalPixelData);            
	        testLogger.info("...parsing TGA/BIG file "+tgaFilename+".");
	        
	        //HashMap hisChanges = this.buildDummyHISHashMap();
	        //String acquisitionSite = null;
	        //if(hisChanges.containsKey("0032,1020")){
	        //    acquisitionSite = (String)hisChanges.get("0032,1020");
	        //    toolkitDDS.setAcquisitionSite(acquisitionSite);
	        //}
	        //Invoke the extraction of pixel data from the Targa file.
	        LegacyTGAFileParser tgaParser = new LegacyTGAFileParser();
	        tgaParser.updateDicomDataSetWithPixelData(toolkitDDS, tgaFilename, originalPixelData);
	        //Add the Vista HIS changes to the generic DicomDataSet object.  This is done
	        // by calling Csaba's code.
	        //toolkitDDS.updateHISChangesToDDS(hisChanges);
	        DicomDataSet dds = (DicomDataSet)toolkitDDS.getDicomDataSet();
	        this.removeSelectedBadVRElements(dds);            
	        this.writeToDisk("IOW00004823398.dcm", dds);
	    }
	    catch(TextFileNotFoundException noText){
	        testLogger.error(noText.getMessage());
	        testLogger.error(this.getClass().getName()+": " +
	                "\nException thrown while assembling Dicom Object.");
	        fail();
	    }
	    catch(TextFileExtractionException extract){
	        testLogger.error(extract.getMessage());
	        testLogger.error(this.getClass().getName()+": " +
	                "\nException thrown while assembling Dicom Object.");
	        fail();
	    }
	    catch(TextFileException e){
	        testLogger.error(e.getMessage());
	        testLogger.error(this.getClass().getName()+": " +
	                "\nException thrown while assembling Dicom Object.");
	        fail();
	    }
	    catch(TGAFileNotFoundException notga){
	        testLogger.error(notga.getMessage());
	        testLogger.error(this.getClass().getName()+": " +
	                "\nException thrown while assembling Dicom Object.");
	        fail();
	    }
	    catch(TGAFileException badtga){
	        testLogger.error(badtga.getMessage());
	        testLogger.error(this.getClass().getName()+": " +
	                "\nException thrown while assembling Dicom Object.");
	        fail();
	    }
	}
    

	public void testTotalReconThirteen(){

        String textFilename = ".\\testdata\\IOW00114520535.txt";
        String tgaFilename = ".\\testdata\\IOW00114520535.tga";
        IDicomDataSet toolkitDDS;
        OriginalPixelDataInfo originalPixelData = new OriginalPixelDataInfo();
        try{
            testLogger.info("...parsing Text file "+textFilename+".");
            //Invoke the extraction of data from the Text file.
            LegacyTextFileParser textParser = new LegacyTextFileParser();
            toolkitDDS = textParser.createDicomDataSet(textFilename, originalPixelData);            
            testLogger.info("...parsing TGA/BIG file "+tgaFilename+".");
            
            //HashMap hisChanges = this.buildDummyHISHashMap();
            //String acquisitionSite = null;
            //if(hisChanges.containsKey("0032,1020")){
            //    acquisitionSite = (String)hisChanges.get("0032,1020");
            //    toolkitDDS.setAcquisitionSite(acquisitionSite);
            //}
            //Invoke the extraction of pixel data from the Targa file.
            LegacyTGAFileParser tgaParser = new LegacyTGAFileParser();
            tgaParser.updateDicomDataSetWithPixelData(toolkitDDS, tgaFilename, originalPixelData);
            //Add the Vista HIS changes to the generic DicomDataSet object.  This is done
            // by calling Csaba's code.
            //toolkitDDS.updateHISChangesToDDS(hisChanges);
            DicomDataSet dds = (DicomDataSet)toolkitDDS.getDicomDataSet();
            this.removeSelectedBadVRElements(dds);            
            this.writeToDisk("IOW00114520535.dcm", dds);
        }
        catch(TextFileNotFoundException noText){
            testLogger.error(noText.getMessage());
            testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TextFileExtractionException extract){
            testLogger.error(extract.getMessage());
            testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TextFileException e){
            testLogger.error(e.getMessage());
            testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TGAFileNotFoundException notga){
            testLogger.error(notga.getMessage());
            testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
        catch(TGAFileException badtga){
            testLogger.error(badtga.getMessage());
            testLogger.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            fail();
        }
    }
	
    
    private HashMap<String, String> buildDummyHISHashMap(){

        HashMap<String, String> map = new HashMap<String, String>();
        
        map.put("0008,0018" , "1.2.840.113754.1.7.541.64.20070907.82134.1");
        map.put("0008,0020" , "20070713");
        map.put("0008,0050" , "010107-1234");
        map.put("0010,0010" , "IMAGPATIENT,TEST");
        map.put("0010,0020" , "000000001");
        map.put("0010,0030" , "19290401");
        map.put("0010,0032" , "000000");
        map.put("0010,0040" , "M");
        map.put("0010,1000" , "194493029484");
        map.put("0010,1040" , "1313 Mockingbird LA.");
        map.put("0010,2160" , "WHITE, NOT HISP.");
        map.put("0020,000D" , "1.2.840.113704.1.111.5812.1184364657.1");
        map.put("0020,000E" , "1.2.840.113704.1.111.5812.1184364797.7");
        map.put("0032,1020" , "541");
        map.put("0032,1032" , "IMAGPROVIDER,TEST");
        map.put("0032,1060" , "CT ABDOMEN W/O DYE");
        //map.put("0032,1064 0008,0100" , 74150);
        //map.put("0032,1064 0008,0102" , C4);
        //map.put("0032,1064 0008,0104" , CT ABDOMEN W/O DYE);
        
        return map;
    }
    
    private void writeToDisk(String filename, DicomDataSet dds){
        try{
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
