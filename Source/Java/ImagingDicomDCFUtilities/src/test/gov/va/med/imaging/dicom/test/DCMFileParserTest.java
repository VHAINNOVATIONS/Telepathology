/*
 * Created on Apr 5, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package gov.va.med.imaging.dicom.test;

import gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet;
import gov.va.med.imaging.dicom.dcftoolkit.utilities.exceptions.DicomFileException;
import gov.va.med.imaging.dicom.dcftoolkit.utilities.reconstitution.DicomFileExtractor;
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
public class DCMFileParserTest extends DicomDCFUtilitiesTestBase {

    /*
     * @see DicomDCFUtilitiesTestBase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see DicomDCFUtilitiesTestBase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Constructor for DCMFileParserTest.
     * @param arg0
     */
    public DCMFileParserTest(String arg0) {
        super(arg0);
    }
    public void testDicomFileParserOne(){
        
        testLogger.info("Starting Test 1.");
        String dcmFilename = ".\\testdata\\test1.dcm";
        
        this.parseToDDS(dcmFilename);
       testLogger.info("Test 1 Completed Successfully...");

    }
    
    public void testDicomFileParserTwo(){
        
        testLogger.info("Starting Test 2.");
        String dcmFilename = ".\\testdata\\test2.dcm";
        
        this.parseToDDS(dcmFilename);
       testLogger.info("Test 2 Completed Successfully...");

    }

    public void testDicomFileParserThree(){
        
        testLogger.info("Starting Test 3.");
        String dcmFilename = ".\\testdata\\test3.dcm";
        
        this.parseToDDS(dcmFilename);
       testLogger.info("Test 3 Completed Successfully...");

    }

    private void parseToDDS(String dcmFilename){
        
        try{
            DicomFileExtractor parser = new DicomFileExtractor();
            IDicomDataSet dds;
            dds = (IDicomDataSet)parser.getDDSFromDicomFile(dcmFilename);
            DicomDataSet toolkitDDS = (DicomDataSet)dds.getDicomDataSet();
            this.removeSelectedBadVRElements(toolkitDDS);
            testLogger.info("DataSet: " + toolkitDDS.toString());
        }
        catch(DicomFileException noFile){
            testLogger.info("Did not find DICOM File " + dcmFilename);
            noFile.printStackTrace();
            fail("Test failed.");
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
