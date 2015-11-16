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

import gov.va.med.imaging.exchange.business.dicom.DicomGatewayConfiguration;
import gov.va.med.imaging.exchange.business.dicom.GatewayDictionaryContents;
import gov.va.med.imaging.exchange.business.dicom.ModalityDicInfo;
import gov.va.med.imaging.exchange.business.dicom.ParameterDeviceInfo;
import gov.va.med.imaging.exchange.business.dicom.exceptions.DicomDictionaryException;

import java.io.File;
import java.util.Vector;

/**
 *
 * @author William Peterson
 *
 */
public class LoadDictionaryContentsTest extends DicomCommonTestBase {

	private String getRelativeDictionaryFilename(String filename)
	{
		String dicFilename = filename; 
		File dicFile = new File(dicFilename);
		if (!dicFile.exists())
		{
			// we are running from ImagingExchange project's buildallspi.cmd file
			dicFilename = "../ImagingDicomCommon/" + filename; 
			dicFile = new File(dicFilename);
		}
		return dicFilename;
	}

    /*
     * @see DicomCommonTestBase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see DicomCommonTestBase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Constructor for LoadDictionaryContentsTest.
     * @param arg0
     */
    public LoadDictionaryContentsTest(String arg0) {
        super(arg0);
    }
    
    public void testLoadingModalityDICFile(){
        String filename = getRelativeDictionaryFilename("Modality_test.dic");
        
        try{
            GatewayDictionaryContents.getInstance().loadModalityDictionaryList(filename);
            Vector<ModalityDicInfo> modalityList = GatewayDictionaryContents.getInstance().getModalityDictionaryEntries();
            //Iterator i = modalityList.iterator();
            //while(i.hasNext()){
            //    ModalityDicInfo lineEntry;
            //    lineEntry = (ModalityDicInfo)i.next();
            //    TESTLOGGER.info("Mfg: "+lineEntry.getManufacturer());
            //    TESTLOGGER.info("Model: "+ lineEntry.getModel());
            //    TESTLOGGER.info("Modality Code: "+ lineEntry.getModalityCode()+"\n");
            //}
            
            if(modalityList.isEmpty()){
            	fail("Modality_test.dic file is empty.");
            }
            TESTLOGGER.info("Modality_test.dic contains "+modalityList.size()+" entries.");
        }
        catch (DicomDictionaryException dde){
            TESTLOGGER.info("Exception thrown.");
            TESTLOGGER.info("Failed loading modality.dic file.");
            dde.printStackTrace();
            fail("Failed loading Modality_test.dic file.\n");
        }
        TESTLOGGER.info("Successfully loaded Modality_test.dic file.\n");
    }
    
    public void testLoadingCTParmsDICFile(){
        String filename = getRelativeDictionaryFilename("CT_Param_test.dic");
        DicomGatewayConfiguration.getInstance().setLocation("660");
        try{
            GatewayDictionaryContents.getInstance().loadCTParameterList(filename);
            Vector<ParameterDeviceInfo> parameterList = GatewayDictionaryContents.getInstance().getCTParametersList();
            //Iterator i = parameterList.iterator();
            //while(i.hasNext()){
            //    ParameterDeviceInfo lineEntry;
            //    lineEntry = (ParameterDeviceInfo)i.next();
            //    TESTLOGGER.info("Site: "+lineEntry.getSiteID());
            //    TESTLOGGER.info("Mfg: "+lineEntry.getManufacturer());
            //    TESTLOGGER.info("Model: "+ lineEntry.getModel());
            //    TESTLOGGER.info("Date: "+lineEntry.getChangeDate().toString()+"\n");
            //}
            
            if(parameterList.isEmpty()){
            	fail("CT_Param_test.dic file is empty.");
            }
            TESTLOGGER.info("CT_Param_test.dic contains "+parameterList.size()+" entries.");            
        }
        catch (DicomDictionaryException dde){
            TESTLOGGER.info("Exception thrown.");
            TESTLOGGER.info("Failed loading CT_Param_test.dic file.");
            dde.printStackTrace();
            fail("Failed loading CT_Param_test.dic file.\n");
        }
        TESTLOGGER.info("Succesfully loaded CT_Param_test.dic file.\n");
    }    
}
