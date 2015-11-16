/**
 * 
 */
package gov.va.med.imaging.dicom.dcftoolkit.test;

import gov.va.med.imaging.dicom.common.interfaces.IIODViolationList;
import gov.va.med.imaging.dicom.dcftoolkit.common.validation.DicomInstanceValidator;

import java.io.UnsupportedEncodingException;

import org.apache.log4j.Level;

import com.lbs.DCS.DicomDataSet;
import com.lbs.DCS.DicomFileInput;

/**
 * @author vhaiswpeterb
 *
 */
public class IODValidationTest extends DicomDCFCommonTestBase {

	/**
	 * @param name
	 */
	public IODValidationTest(String name) {
		super(name);
	}

    protected void setUp()throws Exception{
    	super.setUp();
        DicomInstanceValidator.logger.addAppender(appender);
        DicomInstanceValidator.logger.setLevel(Level.DEBUG);
    	
    }
    
    protected void tearDown() throws Exception{
    	super.tearDown();
    }
	
	/**
	 * Test method for {@link gov.va.med.imaging.dicom.dcftoolkit.common.validation.DicomInstanceValidator#validate(com.lbs.DCS.DicomDataSet)}.
	 */
	public void testIOD_1(){
		try {
			DicomDataSet list;
			TESTLOGGER.info("\n\nTesting iod_1.dcm");
			DicomFileInput input = new DicomFileInput(".\\src\\resources\\iod_1.dcm");
			input.open();
			list = input.readDataSetNoPixels();
			IIODViolationList violations = DicomInstanceValidator.getInstance().validate(list);
			boolean err = violations.hasViolationErrors();
			if(err != true){
				fail();
			}
			TESTLOGGER.info(violations.toString());
			if(violations.getErrorCount() != 1){
				TESTLOGGER.error("Error Count does not match expected value.");
				fail();
			}
			if(violations.getWarningCount() != 1){
				TESTLOGGER.error("Warning Count does not match expected value.");
				fail();
			}
		} 
		catch(UnsupportedEncodingException ueX){
			TESTLOGGER.error("Unsupported SOP Class.");
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			fail();
		}		
	}
	
	/**
	 * Test method for {@link gov.va.med.imaging.dicom.dcftoolkit.common.validation.DicomInstanceValidator#validate(com.lbs.DCS.DicomDataSet)}.
	 */  
    public void testIOD_2(){
		try {
			DicomDataSet list;
			TESTLOGGER.info("\n\nTesting iod_2.dcm");
			DicomFileInput input = new DicomFileInput(".\\src\\resources\\iod_2.dcm");
			input.open();
			list = input.readDataSetNoPixels();
			IIODViolationList violations = DicomInstanceValidator.getInstance().validate(list);
			boolean err = violations.hasViolationErrors();
			if(err != true){
				fail();
			}
			TESTLOGGER.info(violations.toString());			
			if(violations.getErrorCount() != 1){
				TESTLOGGER.error("Error Count does not match expected value.");
				fail();
			}
			if(violations.getWarningCount() != 1){
				TESTLOGGER.error("Warning Count does not match expected value.");
				fail();
			}
		} 
		catch(UnsupportedEncodingException ueX){
			TESTLOGGER.error("Unsupported SOP Class.");
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			fail();
		}		
	}

	/**
	 * Test method for {@link gov.va.med.imaging.dicom.dcftoolkit.common.validation.DicomInstanceValidator#validate(com.lbs.DCS.DicomDataSet)}.
	 */    
    public void testIOD_3(){
		try {
			DicomDataSet list;
			TESTLOGGER.info("\n\nTesting iod_3.dcm");
			DicomFileInput input = new DicomFileInput(".\\src\\resources\\iod_3.dcm");
			input.open();
			list = input.readDataSetNoPixels();
			IIODViolationList violations = DicomInstanceValidator.getInstance().validate(list);
			boolean err = violations.hasViolationErrors();
			if(err != true){
				fail();
			}
			TESTLOGGER.info(violations.toString());			
			if(violations.getErrorCount() != 1){
				TESTLOGGER.error("Error Count does not match expected value.");
				fail();
			}
			if(violations.getWarningCount() != 1){
				TESTLOGGER.error("Warning Count does not match expected value.");
				fail();
			}
		} 
		catch(UnsupportedEncodingException ueX){
			TESTLOGGER.error("Unsupported SOP Class.");
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			fail();
		}		
	}

	/**
	 * Test method for {@link gov.va.med.imaging.dicom.dcftoolkit.common.validation.DicomInstanceValidator#validate(com.lbs.DCS.DicomDataSet)}.
	 */    
    public void testIOD_4(){
		try {
			DicomDataSet list;
			TESTLOGGER.info("\n\nTesting iod_4.dcm");
			DicomFileInput input = new DicomFileInput(".\\src\\resources\\iod_4.dcm");
			input.open();
			list = input.readDataSetNoPixels();
			IIODViolationList violations = DicomInstanceValidator.getInstance().validate(list);
			boolean err = violations.hasViolationErrors();
			if(err != true){
				fail();
			}
			TESTLOGGER.info(violations.toString());			
			if(violations.getErrorCount() != 1){
				TESTLOGGER.error("Error Count does not match expected value.");
				fail();
			}
			if(violations.getWarningCount() != 1){
				TESTLOGGER.error("Warning Count does not match expected value.");
				fail();
			}
		} 
		catch(UnsupportedEncodingException ueX){
			TESTLOGGER.error("Unsupported SOP Class.");
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			fail();
		}		
	}
    
	/**
	 * Test method for {@link gov.va.med.imaging.dicom.dcftoolkit.common.validation.DicomInstanceValidator#validate(com.lbs.DCS.DicomDataSet)}.
	 */    
    public void testIOD_5(){
		try {
			DicomDataSet list;
			TESTLOGGER.info("\n\nTesting iod_5.dcm");
			DicomFileInput input = new DicomFileInput(".\\src\\resources\\iod_5.dcm");
			input.open();
			list = input.readDataSetNoPixels();
			IIODViolationList violations = DicomInstanceValidator.getInstance().validate(list);
			boolean err = violations.hasViolationErrors();
			if(err != true){
				fail();
			}
			TESTLOGGER.info(violations.toString());			
			if(violations.getErrorCount() != 1){
				TESTLOGGER.error("Error Count does not match expected value.");
				fail();
			}
			if(violations.getWarningCount() != 1){
				TESTLOGGER.error("Warning Count does not match expected value.");
				fail();
			}
		} 
		catch(UnsupportedEncodingException ueX){
			TESTLOGGER.error("Unsupported SOP Class.");
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			fail();
		}		
	}

	/**
	 * Test method for {@link gov.va.med.imaging.dicom.dcftoolkit.common.validation.DicomInstanceValidator#validate(com.lbs.DCS.DicomDataSet)}.
	 */    
    public void testIOD_6(){
		try {
			DicomDataSet list;
			TESTLOGGER.info("\n\nTesting iod_6.dcm");
			DicomFileInput input = new DicomFileInput(".\\src\\resources\\iod_6.dcm");
			input.open();
			list = input.readDataSetNoPixels();
			IIODViolationList violations = DicomInstanceValidator.getInstance().validate(list);
			boolean err = violations.hasViolationErrors();
			if(err != true){
				fail();
			}
			TESTLOGGER.info(violations.toString());			
			if(violations.getErrorCount() != 1){
				TESTLOGGER.error("Error Count does not match expected value.");
				fail();
			}
			if(violations.getWarningCount() != 1){
				TESTLOGGER.error("Warning Count does not match expected value.");
				fail();
			}
		} 
		catch(UnsupportedEncodingException ueX){
			TESTLOGGER.error("Unsupported SOP Class.");
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			fail();
		}		
	}

	/**
	 * Test method for {@link gov.va.med.imaging.dicom.dcftoolkit.common.validation.DicomInstanceValidator#validate(com.lbs.DCS.DicomDataSet)}.
	 */
	public void testIOD_7(){
		try {
			DicomDataSet list;
			TESTLOGGER.info("\n\nTesting iod_7.dcm");
			DicomFileInput input = new DicomFileInput(".\\src\\resources\\iod_7.dcm");
			input.open();
			list = input.readDataSetNoPixels();
			IIODViolationList violations = DicomInstanceValidator.getInstance().validate(list);
			boolean err = violations.hasViolationErrors();
			if(err != true){
				fail();
			}
			TESTLOGGER.info(violations.toString());			
			if(violations.getErrorCount() != 7){
				TESTLOGGER.error("Error Count does not match expected value.");
				fail();
			}
			if(violations.getWarningCount() != 1){
				TESTLOGGER.error("Warning Count does not match expected value.");
				fail();
			}
		} 
		catch(UnsupportedEncodingException ueX){
			TESTLOGGER.error("Unsupported SOP Class.");
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			fail();
		}		
	}

	/**
	 * Test method for {@link gov.va.med.imaging.dicom.dcftoolkit.common.validation.DicomInstanceValidator#validate(com.lbs.DCS.DicomDataSet)}.
	 */
	public void testIOD_8(){
		try {
			DicomDataSet list;
			TESTLOGGER.info("\n\nTesting iod_8.dcm");
			DicomFileInput input = new DicomFileInput(".\\src\\resources\\iod_8.dcm");
			input.open();
			list = input.readDataSetNoPixels();
			IIODViolationList violations = DicomInstanceValidator.getInstance().validate(list);
			boolean err = violations.hasViolationErrors();
			if(err != true){
				fail();
			}
			TESTLOGGER.info(violations.toString());			
			if(violations.getErrorCount() != 9){
				TESTLOGGER.error("Error Count does not match expected value.");
				fail();
			}
			if(violations.getWarningCount() != 1){
				TESTLOGGER.error("Warning Count does not match expected value.");
				fail();
			}
		} 
		catch(UnsupportedEncodingException ueX){
			TESTLOGGER.error("Unsupported SOP Class.");
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			fail();
		}		
	}

	/**
	 * Test method for {@link gov.va.med.imaging.dicom.dcftoolkit.common.validation.DicomInstanceValidator#validate(com.lbs.DCS.DicomDataSet)}.
	 */
	public void testIOD_9(){
		try {
			DicomDataSet list;
			TESTLOGGER.info("\n\nTesting iod_9.dcm");
			DicomFileInput input = new DicomFileInput(".\\src\\resources\\iod_9.dcm");
			input.open();
			list = input.readDataSetNoPixels();
			IIODViolationList violations = DicomInstanceValidator.getInstance().validate(list);
			boolean err = violations.hasViolationErrors();
			if(err != true){
				fail();
			}
			TESTLOGGER.info(violations.toString());			
			if(violations.getErrorCount() != 14){
				TESTLOGGER.error("Error Count does not match expected value.");
				fail();
			}
			if(violations.getWarningCount() != 2){
				TESTLOGGER.error("Warning Count does not match expected value.");
				fail();
			}
		} 
		catch(UnsupportedEncodingException ueX){
			TESTLOGGER.error("Unsupported SOP Class.");
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			fail();
		}		
	}

	/**
	 * Test method for {@link gov.va.med.imaging.dicom.dcftoolkit.common.validation.DicomInstanceValidator#validate(com.lbs.DCS.DicomDataSet)}.
	 */
	/*
	public void testIOD_10(){
		try {
			DicomDataSet list;
			TESTLOGGER.info("\n\nTesting iod_10.dcm");
			DicomFileInput input = new DicomFileInput(".\\src\\resources\\iod_10.dcm");
			input.open();
			list = input.readDataSetNoPixels();
			IIODViolationList violations = DicomInstanceValidator.getInstance().validate(list);
			boolean err = violations.hasViolationErrors();
			if(err != true){
				fail();
			}
			TESTLOGGER.info(violations.toString());			
			if(violations.getErrorCount() != 1){
				TESTLOGGER.error("Error Count does not match expected value.");
				fail();
			}
			if(violations.getWarningCount() != 4){
				TESTLOGGER.error("Warning Count does not match expected value.");
				fail();
			}
		} 
		catch(UnsupportedEncodingException ueX){
			TESTLOGGER.error("Unsupported SOP Class.");
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			fail();
		}		
	}
	*/

	/**
	 * Test method for {@link gov.va.med.imaging.dicom.dcftoolkit.common.validation.DicomInstanceValidator#validate(com.lbs.DCS.DicomDataSet)}.
	 */
	public void testIOD_11(){
		try {
			DicomDataSet list;
			TESTLOGGER.info("\n\nTesting iod_11.dcm");
			DicomFileInput input = new DicomFileInput(".\\src\\resources\\iod_11.dcm");
			input.open();
			list = input.readDataSetNoPixels();
			IIODViolationList violations = DicomInstanceValidator.getInstance().validate(list);
			boolean err = violations.hasViolationErrors();
			if(err == true){
				fail();
			}
			TESTLOGGER.info(violations.toString());			
			if(violations.getErrorCount() != 0){
				TESTLOGGER.error("Error Count does not match expected value.");
				fail();
			}
			if(violations.getWarningCount() != 0){
				TESTLOGGER.error("Warning Count does not match expected value.");
				fail();
			}
		} 
		catch(UnsupportedEncodingException ueX){
			TESTLOGGER.error("Unsupported SOP Class.");
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			fail();
		}		
	}

	/**
	 * Test method for {@link gov.va.med.imaging.dicom.dcftoolkit.common.validation.DicomInstanceValidator#validate(com.lbs.DCS.DicomDataSet)}.
	 */
	public void testIOD_12(){
		try {
			DicomDataSet list;
			TESTLOGGER.info("\n\nTesting iod_12.dcm");
			DicomFileInput input = new DicomFileInput(".\\src\\resources\\iod_12.dcm");
			input.open();
			list = input.readDataSetNoPixels();
			IIODViolationList violations = DicomInstanceValidator.getInstance().validate(list);
			boolean err = violations.hasViolationErrors();
			if(err != true){
				fail();
			}
			TESTLOGGER.info(violations.toString());			
			if(violations.getErrorCount() != 3){
				TESTLOGGER.error("Error Count does not match expected value.");
				fail();
			}
			if(violations.getWarningCount() != 0){
				TESTLOGGER.error("Warning Count does not match expected value.");
				fail();
			}
		} 
		catch(UnsupportedEncodingException ueX){
			TESTLOGGER.error("Unsupported SOP Class.");
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			fail();
		}		
	}

	/**
	 * Test method for {@link gov.va.med.imaging.dicom.dcftoolkit.common.validation.DicomInstanceValidator#validate(com.lbs.DCS.DicomDataSet)}.
	 */
	public void testIOD_13(){
		try {
			DicomDataSet list;
			TESTLOGGER.info("\n\nTesting iod_13.dcm");
			DicomFileInput input = new DicomFileInput(".\\src\\resources\\iod_13.dcm");
			input.open();
			list = input.readDataSetNoPixels();
			IIODViolationList violations = DicomInstanceValidator.getInstance().validate(list);
			boolean err = violations.hasViolationErrors();
			if(err != true){
				fail();
			}
			TESTLOGGER.info(violations.toString());			
			if(violations.getErrorCount() != 7){
				TESTLOGGER.error("Error Count does not match expected value.");
				fail();
			}
			if(violations.getWarningCount() != 1){
				TESTLOGGER.error("Warning Count does not match expected value.");
				fail();
			}
		} 
		catch(UnsupportedEncodingException ueX){
			TESTLOGGER.error("Unsupported SOP Class.");
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			fail();
		}		
	}

	/**
	 * Test method for {@link gov.va.med.imaging.dicom.dcftoolkit.common.validation.DicomInstanceValidator#validate(com.lbs.DCS.DicomDataSet)}.
	 */
	public void testIOD_14(){
		try {
			DicomDataSet list;
			TESTLOGGER.info("\n\nTesting iod_14.dcm");
			DicomFileInput input = new DicomFileInput(".\\src\\resources\\iod_14.dcm");
			input.open();
			list = input.readDataSetNoPixels();
			IIODViolationList violations = DicomInstanceValidator.getInstance().validate(list);
			boolean err = violations.hasViolationErrors();
			if(err != true){
				fail();
			}
			TESTLOGGER.info(violations.toString());			
			if(violations.getErrorCount() != 2){
				TESTLOGGER.error("Error Count does not match expected value.");
				fail();
			}
			if(violations.getWarningCount() != 1){
				TESTLOGGER.error("Warning Count does not match expected value.");
				fail();
			}
		} 
		catch(UnsupportedEncodingException ueX){
			TESTLOGGER.error("Unsupported SOP Class.");
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			fail();
		}		
	}
	
	
	/**
	 * Test method for {@link gov.va.med.imaging.dicom.dcftoolkit.common.validation.DicomInstanceValidator#validate(com.lbs.DCS.DicomDataSet)}.
	 */
	public void testIOD_16(){
		try {
			DicomDataSet list;
			TESTLOGGER.info("\n\nTesting iod_16.dcm");
			DicomFileInput input = new DicomFileInput(".\\src\\resources\\iod_16.dcm");
			input.open();
			list = input.readDataSetNoPixels();
			IIODViolationList violations = DicomInstanceValidator.getInstance().validate(list);
			boolean err = violations.hasViolationErrors();
			if(err != true){
				fail();
			}
			TESTLOGGER.info(violations.toString());			
			if(violations.getErrorCount() != 1){
				TESTLOGGER.error("Error Count does not match expected value.");
				fail();
			}
			if(violations.getWarningCount() != 1){
				TESTLOGGER.error("Warning Count does not match expected value.");
				fail();
			}
		} 
		catch(UnsupportedEncodingException ueX){
			TESTLOGGER.error("Unsupported SOP Class.");
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			fail();
		}		
	}
	
	/**
	 * Test method for {@link gov.va.med.imaging.dicom.dcftoolkit.common.validation.DicomInstanceValidator#validate(com.lbs.DCS.DicomDataSet)}.
	 */
	public void testIOD_17(){
		try {
			DicomDataSet list;
			TESTLOGGER.info("\n\nTesting iod_17.dcm");
			DicomFileInput input = new DicomFileInput(".\\src\\resources\\iod_17.dcm");
			input.open();
			list = input.readDataSetNoPixels();
			IIODViolationList violations = DicomInstanceValidator.getInstance().validate(list);
			boolean err = violations.hasViolationErrors();
			if(err != true){
				fail();
			}
			TESTLOGGER.info(violations.toString());			
			if(violations.getErrorCount() != 4){
				TESTLOGGER.error("Error Count does not match expected value.");
				fail();
			}
			if(violations.getWarningCount() != 0){
				TESTLOGGER.error("Warning Count does not match expected value.");
				fail();
			}
		} 
		catch(UnsupportedEncodingException ueX){
			TESTLOGGER.error("Unsupported SOP Class.");
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			fail();
		}		
	}

	/**
	 * Test method for {@link gov.va.med.imaging.dicom.dcftoolkit.common.validation.DicomInstanceValidator#validate(com.lbs.DCS.DicomDataSet)}.
	 */
	public void testIOD_18(){
		try {
			DicomDataSet list;
			TESTLOGGER.info("\n\nTesting iod_18.dcm");
			DicomFileInput input = new DicomFileInput(".\\src\\resources\\iod_18.dcm");
			input.open();
			list = input.readDataSetNoPixels();
			IIODViolationList violations = DicomInstanceValidator.getInstance().validate(list);
			boolean err = violations.hasViolationErrors();
			if(err != true){
				fail();
			}
			TESTLOGGER.info(violations.toString());			
			if(violations.getErrorCount() != 4){
				TESTLOGGER.error("Error Count does not match expected value.");
				fail();
			}
			if(violations.getWarningCount() != 0){
				TESTLOGGER.error("Warning Count does not match expected value.");
				fail();
			}
		} 
		catch(UnsupportedEncodingException ueX){
			TESTLOGGER.error("Unsupported SOP Class.");
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
			fail();
		}		
	}	

}
