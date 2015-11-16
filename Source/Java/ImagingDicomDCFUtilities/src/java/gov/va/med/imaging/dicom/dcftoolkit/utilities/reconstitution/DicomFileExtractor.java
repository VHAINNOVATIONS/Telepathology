/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: December 13, 2006
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWPETERB
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
package gov.va.med.imaging.dicom.dcftoolkit.utilities.reconstitution;

// import java.io.ByteArrayOutputStream;
// import java.io.IOException;
// import java.nio.ByteOrder;

import gov.va.med.imaging.SizedInputStream;
import gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet;
import gov.va.med.imaging.dicom.dcftoolkit.common.impl.DicomDataSetImpl;
import gov.va.med.imaging.dicom.dcftoolkit.utilities.exceptions.DicomFileException;

import org.apache.log4j.Logger;

import com.lbs.DCS.AttributeTag;
import com.lbs.DCS.DCSException;
import com.lbs.DCS.DicomDataSet;
import com.lbs.DCS.DicomElement;
import com.lbs.DCS.DicomFileInput;
import com.lbs.DCS.DicomSessionSettings;
import com.lbs.DCS.DicomStreamReader;
import com.lbs.DCS.UID;

/**
 *
 * @author William Peterson
 *
 */
public class DicomFileExtractor {
	//IMPROVE Correct this class to match my implementation in P66.  In P66, this class did not have instance variables.
    IDicomDataSet toolkitDDS;
    DicomDataSet  dDS;

    //private static Logger LOGGER = Logger.getLogger (DicomFileExtractor.class);
	private static Logger logger = Logger.getLogger (DicomFileExtractor.class);

    /**
     * Constructor
     *
     * 
     */
    public DicomFileExtractor() {
        super();
        // TODO Auto-generated constructor stub
    }

    public IDicomDataSet getDDSFromDicomFile(String dicomFile)throws DicomFileException{
        
        DicomFileInput fileInput = null;
        this.dDS = null;
        String tsUID = null;
        
        try{
            fileInput = new DicomFileInput(dicomFile);
            fileInput.open();
            this.dDS = fileInput.readDataSet();
            tsUID = fileInput.getActualTSUID();
            if(fileInput.chapter10Encoding()){
                logger.debug(this.getClass().getName()+": Chapter 10 Encoding.");
            }
            else{
                logger.debug(this.getClass().getName()+": Not Chapter 10 Encoding.");
            }
            logger.debug("Transfer Syntax is "+fileInput.transferSyntax());
            //IMPROVE I have this commented out for a reason in P66.  Test it both ways.
            fileInput.close();
        }
        catch(DCSException dcse){
            logger.error(dcse.getMessage());
            logger.error(this.getClass().getName()+": Dicom Toolkit layer: " +
                    "Exception thrown while getting Dicom Dataset from Dicom File.");
            throw new DicomFileException("Failure to extract Dicom Dataset from Dicom file "+dicomFile+".",dcse);
        }
        finally{
        	if(fileInput != null){
        		try{
        			fileInput.close();
        		}
        		catch (Throwable T){
        			logger.error(this.getClass().getName()+": Dicom Toolkit layer: "+
        					"Exception thrown while closing Dicom File "+dicomFile+".");
        		}
        		System.gc();
        	}
        }
        this.removeMetaData(this.dDS);
        return this.encapsulateDDS(tsUID);
    }

    public IDicomDataSet getDDSFromDicomStream(SizedInputStream sizedDicomStream)throws DicomFileException{
        
    String tsUID = null;    
    try{
       	// read InputStream into a ByteBuffer
        DicomSessionSettings dss = new DicomSessionSettings();
        DicomStreamReader reader = new DicomStreamReader(sizedDicomStream.getInStream(), dss);
            DicomFileInput dfi = new DicomFileInput(
                reader,
                null, // UID.TRANSFERLITTLEENDIAN, cpt 4/12/11 -- removed as DCF 3.3.22c figures the right TS of of a part10 file! (and we use only Part10 files)!
                dss );
        dDS = dfi.readDataSet();
        tsUID = dfi.getActualTSUID();
    }
        catch(DCSException dcse){
            logger.error("Error: " + dcse.getMessage());
            logger.error("Dicom Toolkit layer: " +
                    "Exception thrown while getting Dicom Dataset from Dicom Stream.");
            throw new DicomFileException("Failure to extract Dicom Dataset from Dicom Stream.", dcse);
        }
        this.removeMetaData(this.dDS);
        return this.encapsulateDDS(tsUID);
    }
    
    //IMPROVE Make it configurable outside of this class.
    private void removeElement(String element){
        boolean elementRemoved=false;
        try{
        	AttributeTag aT = new AttributeTag(element);
        	elementRemoved=dDS.removeElement(aT);
        }
        catch(DCSException e){
        }
        if (elementRemoved){
            logger.info("DICOM Element (" + element + ") removed");
       }
    }
    
    private void removeMetaData(DicomDataSet dds){
    	boolean isMetaData = true;
    	int i = 0;
    	while (isMetaData){
    		DicomElement element = dds.getElementAt(i);
    		AttributeTag tag = element.tag();
    		int group = tag.group();
    		if(group <= 0x0002){
    			dds.removeElement(tag);
    		}
    		else{
    			isMetaData = false;
    		}
    	}    	
    }
    
    private IDicomDataSet encapsulateDDS(String transferSyntax){
     
        logger.info("...Encapsulating DicomDataSet...");
        // Remove unwanted GE private tags
        //IMPROVE  Make configurable outside of this class.
        removeElement("6003,0010");
        removeElement("6003,1010");
        removeElement("7fe1,0010");
        removeElement("7fe1,1001");
        try{
            toolkitDDS = new DicomDataSetImpl(dDS);
            toolkitDDS.setReceivedTransferSyntax(transferSyntax);
        }
        catch(Exception e){
            logger.error("Error: " + e.getMessage());
            logger.error("Dicom Toolkit layer: Exception thrown while encapsulating the Dicom DataSet.");
            e.printStackTrace();
        }
        return toolkitDDS;
    }
}
