/*
 * Created on May 25, 2011
 *
 */
package gov.va.med.imaging.dicom.dcftoolkit.test;

import java.io.File;

import org.springframework.core.io.ClassPathResource;

import com.lbs.APC.APCException;
import com.lbs.APC_a.AppControl_a;
import com.lbs.CDS_a.CFGDB_a;
import com.lbs.LOG.LOGClient;
import com.lbs.LOG_a.LOGClient_a;
import com.lbs.CDS.CDSException;
import com.lbs.DCS.DCSException;
import com.lbs.DCS.DicomFileInput;

import gov.va.med.imaging.dicom.dcftoolkit.common.mediainterchange.Part10Files;
import gov.va.med.imaging.dicom.dcftoolkit.common.mediainterchange.CINFO;
import gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet;
import gov.va.med.imaging.exchange.business.dicom.exceptions.DicomException;

/**
* @author Csaba Titton
*/

//public class ReadPart10FileTest extends DicomDCFCommonTestBase {
//	public ReadPart10FileTest(String arg0) {
//      super(arg0);
//  }
////	private static String ViXTestDataFolder = "c:/tmp/TestData"; // needed only on build systems
////
////	/**
////     * Constructor
////     * @param arg0
////     */
////    public ReadPart10FileTest(String arg0) {
////        super(arg0);
////    }
////   
////    protected void setUp()throws Exception{
////		File cacheDir = new File(ViXTestDataFolder.toString());
////		if (! cacheDir.exists())
////		{
////			cacheDir.mkdirs();
////		}
////
////    	super.setUp();
////    }
////    
////    protected void tearDown() throws Exception{
////    	super.tearDown();
////    }
////    
////    public void testOpenPart10File() throws DCSException
////    {
////    	boolean success;
////    	
////		System.out.println("Starting Open Part10 file Tests:");
////    	initDCF();
////
////    	success = processAFile("IOTest_JPG.dcm"); 						// JPEG (50)
////    	if (success) success = processAFile("Skull_Lx10j2k.dcm");  		// J2K Lossy (91)
////        if (success) success = processAFile("US-PAL-8-10x-echo.dcm");	// RLE (5)
////       
////    	System.out.println("Finished Open Part10 file Tests.");
////    }
////
////	private void initDCF() {
////		String[] emptyArgs = new String[0];
////		try {
////			//The following is a basic setup necessary for DCF to become operational.
////			System.out.println("Setting up basic DCF services");
////			AppControl_a.setupORB(emptyArgs);
////			CFGDB_a.setFSysMode(true);
////	
////			System.out.println("Setting up DCF Configuration Adapter.");
////			CFGDB_a.setup(emptyArgs);
////	
////			System.out.println("Setting up DCF AppControl Adapter.");
////			AppControl_a.setup(emptyArgs, CINFO.instance());
//////			LOGClient_a.setConsoleMode(true);
////		} catch (APCException ae) {
////			System.out.println("DCF AppControl Exception thrown");
////			return;
////		} catch (CDSException ce) {	
////			System.out.println("DCF CDS Exception thrown");
////			return;
////		}
////	}
////
////	private boolean processAFile(String filename) throws DCSException {
////		boolean retval=false;
////    	System.out.println("Opening file " + filename + " ...");
////		IDicomDataSet idds = null;
//////		ClassPathResource res = new ClassPathResource(filename); // this needs to be in main/test/resources
////
////        try {
////    		// copy test data to temp folder, it might be modified
//////        	String targetFileName = ViXTestDataFolder + "\\" + filename;
//////    		File targetFile = null;
//////			targetFile = new File(targetFileName);
//////			if (targetFile.exists()) {
//////				targetFile.delete();
//////			}
////
////        	DicomFileInput dicomFileHandle = null;
////        	idds = Part10Files.readDicomFile(".\\src\\resources\\" + filename, dicomFileHandle);
////        	dicomFileHandle.close(true);
////        	System.out.println(idds.getDicomDataSet().toString());
//////        	System.out.println("(0002,0010) = " + idds.getDicomElementValue("0002, 0010", null));
//////        	System.out.println("received TS =" + idds.getReceivedTransferSyntax());
////        	System.out.println(" ... SUCCESS");
////        	retval=true;
////        } catch (DicomException de) {
////        	System.out.println(" ... FAILED");
////        }
////        return retval;
////	}
//}
