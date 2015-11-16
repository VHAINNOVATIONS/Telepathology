/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: September 26, 2005
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

import gov.va.med.imaging.SizedInputStream;
import gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet;
import gov.va.med.imaging.dicom.dcftoolkit.utilities.exceptions.TGAFileException;
import gov.va.med.imaging.dicom.dcftoolkit.utilities.exceptions.TGAFileNotFoundException;
import gov.va.med.imaging.exchange.business.dicom.DicomGatewayConfiguration;
import gov.va.med.imaging.exchange.business.dicom.GatewayDictionaryContents;
import gov.va.med.imaging.exchange.business.dicom.ModalityDicInfo;
import gov.va.med.imaging.exchange.business.dicom.ParameterDeviceInfo;
import gov.va.med.imaging.exchange.business.dicom.Parameters;
import gov.va.med.imaging.exchange.business.dicom.exceptions.ParameterDecompositionException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.lbs.DCS.AttributeTag;
import com.lbs.DCS.DCM;
import com.lbs.DCS.DCSException;
import com.lbs.DCS.DicomCSElement;
import com.lbs.DCS.DicomDataSet;
import com.lbs.DCS.DicomElement;
import com.lbs.DCS.DicomFileInput;
import com.lbs.DCS.DicomOBElement;
import com.lbs.DCS.DicomOWElement;
import com.lbs.DCS.UID;

//FUTURE Consider changing code to instantiate the parsers once.  This requires moving
//	all instance variables.
/**
 *
 * @author William Peterson
 *
 */
public class LegacyTGAFileParser {

    private String tgaFile = null;
    
    private boolean bigFileFlag;
    
    private DicomDataSet toolkitDDS = null;
    
    private short tgaRows = 0;
    
    private short tgaColumns = 0;
    
    private byte tgaBitsPerPixel = 0;
    
    private int imagePlanes = 0;
    
    private boolean isImageReduced = false;
    
    private OriginalPixelDataInfo originalPixelDataInfo = null;
    
    private String acquisitionSite = null;

    private ByteBuffer imageBuffer=null;
    
    private static final Logger logger = Logger.getLogger (LegacyTGAFileParser.class);

    /**
     * Constructor
     */
    public LegacyTGAFileParser() {
        super();
    }
    
    public void updateDicomDataSetWithPixelData(IDicomDataSet dds, 
            String tgaFile, OriginalPixelDataInfo originalPixelDataInfo)
            throws TGAFileNotFoundException, TGAFileException{

        logger.info(this.getClass().getName()+": Dicom Toolkit Layer: " +
                "parsing TGA file to DicomDataSet ...");        

        this.originalPixelDataInfo = originalPixelDataInfo;
        this.acquisitionSite = dds.getAcquisitionSite();
        //Extract the DCF DicomDataSet object out of dicomDataSet.
        this.toolkitDDS = (DicomDataSet)dds.getDicomDataSet();
        try{
        	bigFileFlag=tgaFile.toLowerCase().endsWith(".big");
        	
    		if(!bigFileFlag){
    			tgaFile = tgaFile.toLowerCase();
    			tgaFile = tgaFile.replace(".big", ".tga");
    			logger.warn(this.getClass().getName()+": DICOM Toolkit layer: \n"+
    					".BIG file does not exist.  Reverting to subsampled .TGA file.");
    		}

            if(this.isPart10DicomFile(tgaFile)){
            	logger.error(this.getClass().getName()+": DICOM Toolkit layer: \n"+
            			"Should not be a DICOM Part 10 file.");
            	throw new TGAFileException("This is a DICOM Part 10 File.");
            }

            //JUNIT Create test to see how it fails if not correct permissions.
            //Create a binary stream connected to the TGA file.  
            logger.debug("TGA filename: "+tgaFile);
            
            FileChannel fc = new FileInputStream(tgaFile).getChannel();
            MappedByteBuffer buffer = fc.map(MapMode.READ_ONLY, 0, (int)fc.size());
            byte header[] = new byte[18]; 
            // The 18 bytes represents the TGA header which we do not want in the pixel data.
            // This makes sure the position is at 18 instead of 0.
            buffer = (MappedByteBuffer)buffer.get(header, 0, 18);
            int tgaImageBytes = processTgaHeader(header);

            // read in only TGA header sizing worth of image data!! 
            // (prevent inconsistent DICOM file and compression errors)
            int imageBytes = buffer.capacity()-buffer.position();
            if ((imageBytes > tgaImageBytes) && (tgaImageBytes>0))
            	imageBytes = tgaImageBytes; // prevent inconsistent DICOM file and compression errors)
            //  do a bulk push (transfer) to the new ByteBuffer object. The new ByteBuffer
            //  will be assign to the Pixel Data.
            imageBuffer = ByteBuffer.allocate(imageBytes);
            imageBuffer.put(buffer);
            
            processTgaData(dds);
        }
        catch(FileNotFoundException nofile){
            logger.error("Error: " + nofile.getMessage());
            logger.error(this.getClass().getName() + ": Dicom Toolkit layer: " +
                    "Exception thrown while attempting to open TGA file, " + tgaFile+".");
            throw new TGAFileNotFoundException("Could not find or open " + tgaFile+".", nofile);
        }
        catch(IOException ioe){
            logger.error("Error: " + ioe.getMessage());
            logger.error(this.getClass().getName() + ": Dicom Toolkit layer: " +
                    "Exception thrown while attempting to open TGA file, " + tgaFile+".");
            throw new TGAFileException("Could not find or open " + tgaFile + ".", ioe);
        }
        //INFO Keep in the back of your mind about if I need to explicit status LittleEndian
        //  or BigEndian when transferring the actual pixel data.
    }
   
    public void updateDicomDataSetWithPixelData(IDicomDataSet dds, 
    		SizedInputStream sizedTgaStream, OriginalPixelDataInfo originalPixelDataInfo)
            throws TGAFileNotFoundException, TGAFileException{

    	logger.info("... Dicom Toolkit Layer: parsing TGA file stream to DicomDataSet ...");
        
        this.originalPixelDataInfo = originalPixelDataInfo;
        this.acquisitionSite = dds.getAcquisitionSite();
         //Extract the DCF DicomDataSet object out of dicomDataSet.
        this.toolkitDDS = (DicomDataSet)dds.getDicomDataSet();
        try{
            //process the binary stream connected to the TGA file.  
            logger.debug("... TGA file stream processing ...");
            int byteSize = sizedTgaStream.getByteSize();
            bigFileFlag = (byteSize >= 1048576); // ** cpt .big if larger than a MB (used for CT logic only!)
            byte header[] = new byte[18];
            // The 18 bytes represents the TGA header which we do not want in the pixel data.
            // This makes sure the position is at 18 instead of 0.
            int sizeRead = sizedTgaStream.getInStream().read(header, 0, 18);
            if (sizeRead != 18)
            	throw new TGAFileException("Could not read TGA file stream header.");
            int tgaImageBytes = processTgaHeader(header);

            // read in only TGA header sizing worth of image data!! 
            int imageBytes = byteSize-header.length;
            if ((imageBytes > tgaImageBytes) && (tgaImageBytes>0))
            	imageBytes = tgaImageBytes; // prevent inconsistent DICOM file and compression errors)

   			imageBuffer = ByteBuffer.allocate(imageBytes);
            //  do a bulk push (transfer) to the new ByteBuffer object. The new ByteBuffer
            //  will be assign to the Pixel Data.
   			sizeRead = sizedTgaStream.getInStream().read(imageBuffer.array(), 0, imageBytes);
//   		LOGGER.info(sizeRead + " bytes of " + imageBytes + " in imageBuffer");

   			processTgaData(dds);
        }
        catch(IOException ioe){
            logger.error("Error: " + ioe.getMessage());
            logger.error("Dicom Toolkit layer: Exception thrown while attempting to process TGA file stream.");
        }     
    }
    
    private int processTgaHeader(byte[] header)
    throws TGAFileException
    {
        int tgaDataByteSize=0;
    	//Read Byte 2 (1 byte) to confirm it is a "3".  If not throw exception.
        if(header[2] != 3){
            throw new TGAFileException();
        }
        //Read Bytes 12-13 for # of columns.
        this.tgaColumns = 0;
        this.tgaColumns |= (0xFF & header[13]);
        this.tgaColumns <<=8;
        this.tgaColumns |= (0xFF & header[12]);  
        logger.debug("TGA Number of Columns: " + this.tgaColumns);
        //Read Bytes 14-15 for # of rows.
        this.tgaRows = 0;
        this.tgaRows |= (0xFF & header[15]);
        this.tgaRows <<=8;
        this.tgaRows |= (0xFF & header[14]);
        logger.debug("TGA Number of Rows: " + this.tgaRows);
        //Read Byte 16 for bits/pixel.
        this.tgaBitsPerPixel = header[16];
        logger.debug("TGA Bits Per Pixel: " + this.tgaBitsPerPixel);
        int bytesAllocated = (this.tgaBitsPerPixel > 8)? 2 : 1;
        tgaDataByteSize = this.tgaRows * this.tgaColumns * bytesAllocated;
        return tgaDataByteSize;
    }

    //INFO Keep in the back of your mind if we need explicit status LittleEndian
    //  or BigEndian when transferring the actual pixel data.
    private void processTgaData(IDicomDataSet dds)
    throws TGAFileException
    {
    	try {
        	//SRS This is basically where we introduce Patch 50 Header fixes.
            //Update Pixel Module with information extracted above.
            this.updateImagePixelInfo();

            //Original if statement.  MR IOD will not have the same rules applied.  Thus, I removed
            //MR IOD from if statement.  Currently, we do not alter the MR IOD.
            //if(this.isImageCTIOD() || this.isImageMRIOD()){
            if(this.isImageCTIOD()){
                this.updateCTIODInformation();
            }
            if(this.isMultiFrameIOD()){
                this.updateMultiFrameInfoToSingleFrameInfo();
            }
            this.checkVendorCompression();
            this.updatesForAllModalities();
            //IMRPOVE The following line is active in Patch 66.  Uncomment it back out and test it.
            // this.updateModalityCode();  // *** cpt must check if this is ok!!
            //Read from Byte 18 until the EOF.  Pass into Pixel Data element.  Save to DDS.
            //Images looked like crap.  Had to add statement to resolve byte order from .tga file.     
            imageBuffer.order(ByteOrder.LITTLE_ENDIAN);
            if(this.originalPixelDataInfo.getOriginalVR() == DCM.VR_OB){
                DicomOBElement pixelData = new DicomOBElement(
                        new AttributeTag("7FE0,0010"), imageBuffer);
                this.toolkitDDS.insert(pixelData);
            }
            else if(this.originalPixelDataInfo.getOriginalVR() == DCM.VR_OW){
                DicomOWElement pixelData = new DicomOWElement(
                        new AttributeTag("7FE0,0010"), imageBuffer);
                this.toolkitDDS.insert(pixelData);
            }
            else{
                throw new TGAFileException("No matching VR for Pixel Data.");
            }
            //Encapsulate the DicomDataSet.
            this.encapsulateDicomDataSet(dds);
        }
    	catch(DCSException dcse){
            logger.error("Error: " + dcse.getMessage());
            logger.error("Dicom Toolkit layer: Exception thrown while attempting to process TGA data.");
            throw new TGAFileException("Could not process TGA data.", dcse);
        }
    }

    private IDicomDataSet encapsulateDicomDataSet(IDicomDataSet dds){

        dds.setDicomDataSet(this.toolkitDDS);
        return dds;
    }
    
    private void updateImagePixelInfo() throws DCSException{
        boolean isEnoughRows = false;
        boolean isEnoughColumns = false;
        boolean isEnoughBits = false;
        boolean isSamplePerPixel = false;
        
        //Some of Patch 50 work is located in this method.  I had to perform this work right here
        //  and not somewhere else at a later date.
        
        //If Rows and Columns from TGA is smaller than those in dds.
        //Allow Columns-1 to be accepted.  This is due to odd column length resolution.
        logger.debug("Original number of Rows: " + this.toolkitDDS.getElementStringValue(DCM.E_ROWS));
        if((this.tgaRows == (short)this.toolkitDDS.getElementIntValue(DCM.E_ROWS))){
            isEnoughRows = true;
        }
        logger.debug("Original number of Columns: " + this.toolkitDDS.getElementStringValue(DCM.E_COLUMNS));
        if((this.tgaColumns == (short)this.toolkitDDS.getElementIntValue(DCM.E_COLUMNS))
        ||(this.tgaColumns == (short)((this.toolkitDDS.getElementIntValue(DCM.E_COLUMNS))-1))){
            isEnoughColumns = true;
            //Had to add this insert() method.  This is because Columns-1 is accepted, however 
            //  this is a difference that causes the image to be distorted.
            this.toolkitDDS.insert(DCM.E_COLUMNS, this.tgaColumns);
        }
        //Historical: We have always converted from b16 to b12 for CT images.  However, there is
        //  no missing data.  The CT only used 12bits, not 16.  This was a common practice for CT
        //  as well as some MR and a couple of CR/DX images.  The only time the image are really
        //  reduced was when using the R factors.  The R factors always took it down to 8bits.
        //  Thus, anything above 8bits is not a reduction.  A b12 image that was a b16 still 
        //  contains all the original information.
        logger.debug("Original number of Bits Stored: " + this.toolkitDDS.getElementStringValue(DCM.E_BITS_STORED));
        if((this.tgaBitsPerPixel == (byte)this.toolkitDDS.getElementIntValue(DCM.E_BITS_STORED))
                || (this.tgaBitsPerPixel > 8)){
           isEnoughBits = true;
           this.toolkitDDS.insert(DCM.E_BITS_STORED, this.tgaBitsPerPixel);
           this.toolkitDDS.insert(DCM.E_HIGH_BIT, this.tgaBitsPerPixel - 1);
           // cpt 04/16/09 -- handle the rare case where bits allocated = 16 but tgaBitsPerPixel = 8
           if ((this.tgaBitsPerPixel==8) && (this.toolkitDDS.getElementIntValue(DCM.E_BITS_ALLOCATED) > this.tgaBitsPerPixel)) {
               this.toolkitDDS.insert(DCM.E_BITS_ALLOCATED, tgaBitsPerPixel);
           }
        }
        logger.debug("Original number of Samples Per Pixel: " + this.toolkitDDS.getElementStringValue(DCM.E_SAMPLES_PER_PIXEL));
        if(this.toolkitDDS.getElementIntValue(DCM.E_SAMPLES_PER_PIXEL) == 1){
            isSamplePerPixel = true;
        }
            
        //SRS Patch50 3.2.3.5 Update Image Module info based on tga file used.    
        //SRS Patch50 3.2.3.9 Update Image Module info based on tga file used.
        if(isEnoughRows && isEnoughColumns && isEnoughBits && isSamplePerPixel){
            //if rows, columns, and bits match between DDS and TGA, do nothing.
        	// if IOD is already Secondary Capture remove rescale slope/intercept, etc. 
        	if (this.isImageSCIOD()) {
        		modifyImageToSCIOD();
        	}
        }
        else{
            //add the tga rows/columns fields to the DDS (early, so other computations are correct).
            this.toolkitDDS.insert(DCM.E_ROWS, this.tgaRows);
            this.toolkitDDS.insert(DCM.E_COLUMNS, this.tgaColumns);

            int fullImageSize = this.toolkitDDS.getElementIntValue(DCM.E_BITS_ALLOCATED)
                * this.toolkitDDS.getElementIntValue(DCM.E_ROWS)
                * this.toolkitDDS.getElementIntValue(DCM.E_COLUMNS)
                * this.toolkitDDS.getElementIntValue(DCM.E_SAMPLES_PER_PIXEL);
             
            int bitsAllocated;
            if(this.tgaBitsPerPixel > 8){
                bitsAllocated = 16;
                this.toolkitDDS.insert(DCM.E_BITS_ALLOCATED, bitsAllocated);
                this.toolkitDDS.insert(DCM.E_BITS_STORED, this.tgaBitsPerPixel);
                if(this.toolkitDDS.containsElement(DCM.E_HIGH_BIT)){
                    int highBit = this.toolkitDDS.getElementIntValue(DCM.E_HIGH_BIT);
                    if(highBit > this.tgaBitsPerPixel){
                        this.toolkitDDS.insert(DCM.E_HIGH_BIT, (this.tgaBitsPerPixel - 1));
                    }
                }
                else{
                    this.toolkitDDS.insert(DCM.E_HIGH_BIT, (this.tgaBitsPerPixel - 1));
                }
            }
            else{
                bitsAllocated = 8;
                this.toolkitDDS.insert(DCM.E_BITS_ALLOCATED, bitsAllocated);
                this.toolkitDDS.insert(DCM.E_BITS_STORED, this.tgaBitsPerPixel);
                this.toolkitDDS.insert(DCM.E_HIGH_BIT, (this.tgaBitsPerPixel - 1));
            }
            int reducedImageSize = this.tgaRows * this.tgaColumns * bitsAllocated;
            
	        if (!(isEnoughRows && isEnoughColumns))
	        	// cpt - 06/28/07: 
	        	// if Columns and Rows changed, and (0028, 0030) Pixel Spacing and/or
	        	// (0018, 1164) Imager Pixel Spacing is present, adjust them respectively
	        	adjustPixelSpacing();

            Integer reductionFactor = fullImageSize / reducedImageSize;
// ** cpt: set legacy lossy compression method for "down-sampled" image
            if (reductionFactor > 1.0) {
	            //set Lossy Image Compression field to 01.
	            this.toolkitDDS.insert(DCM.E_LOSSY_IMAGE_COMPRESSION,"01");  // VR=CS - coded string!
	            //set Ratio of Lossy Compression.
	            this.toolkitDDS.insert(DCM.E_LOSSY_IMAGE_CMP_RATIO, reductionFactor);
	            //set lossy compression method
		            this.toolkitDDS.insert(DCM.E_LOSSY_IMAGE_CMP_METHOD, "DOWN-SAMPLED");
            }
	        //Change Window Width and Level to match pixel depth.
            this.updateWindowValues();
            //Change Image Type to Derived.
            this.updateImageTypeToDerived();
            //Change SOP Class to Secondary Capture.
            //VERIFY proper solution for the following.
            //Solution: Csaba and I believe this is right.  I will double-check walking thru code.
            this.updateSOPClassToSecondaryCapture(); // *** cpt: this puts every downsampled CR into SC IOD!!!
        }
    }

    private void adjustPixelSpacing()throws DCSException {
    	// if present adjust (0028, 0030) Pixel Spacing and/or  (0018, 1164) Imager Pixel Spacing
    	// based on changed rows and columns ratio
    	checkAndUpdatePixelSpacing(DCM.E_IMAGER_PIXEL_SPACING);
    	checkAndUpdatePixelSpacing(DCM.E_PIXEL_SPACING);
    }
    
    private void checkAndUpdatePixelSpacing(AttributeTag pSTag) throws DCSException {
    	
	   	DicomElement dE=null;
		try {
			dE=this.toolkitDDS.findElement(pSTag);
	    } catch (DCSException dcse){
			return; // nothing to adjust
		}
	   	String pSString1=null;
	   	String pSString2=null;
		try {
			pSString1 = dE.getStringValue(0);
			pSString2 = dE.getStringValue(1);
	    } catch (DCSException dcse){
		}
		float rowsRatio = (short)this.toolkitDDS.getElementIntValue(DCM.E_ROWS) / this.tgaRows;
		float columnsRatio = (short)this.toolkitDDS.getElementIntValue(DCM.E_COLUMNS) / this.tgaColumns;
		Float pSR=null;
		Float pSC=null;
		
		try {
			String pSString="";
			if ((pSString1 != null) && (pSString1.length() > 0)) { 
				pSR = (Float)(Float.parseFloat(pSString1) * rowsRatio);
				pSString = pSR.toString();
			}
			if ((pSString2 != null) && (pSString2.length() > 0)) { 
				pSC = (Float)(Float.parseFloat(pSString2) * columnsRatio);
				if (pSString.length() > 0) {
		   			pSString += "\\" + pSC.toString();    				
				} else {
	    			pSString = pSC.toString();
	   			}
			}
			if (pSString.length()>0) {
	            this.toolkitDDS.insert(pSTag, pSString);
			}
		} 
		catch (NumberFormatException nfe) {}
	}

    private void updateImageTypeToDerived()throws DCSException{
        
        //SRS Patch50 3.2.3.1 All tga images shall set ImageType to Derived.
    	
        //change Image Type from Original to Derived.
    	//   but first check if type 1 field Image Type exists; if does not, create it as DERIVED/PRIMARY
    	//   -- for ACR-NEMA 2.0 compatibility
    	DicomCSElement imageType=null;
    	try {
    		imageType = (DicomCSElement)this.toolkitDDS.findElement(DCM.E_IMAGE_TYPE);
    	} 
    	catch (DCSException dcse) {
            String imageTypeValues[] = {"DERIVED", "PRIMARY"};
            DicomCSElement nuImageType = new DicomCSElement(DCM.E_IMAGE_TYPE, imageTypeValues);
            this.toolkitDDS.insert(nuImageType);
            return;
        }
        String imageTypeValues[] = new String[imageType.vm()];
        if((imageType.getStringValue(0).equals("ORIGINAL"))){
            for(int i=0; i<imageType.vm(); i++){
                String singleType = imageType.getStringValue(i);
                if(i == 0){
                    imageTypeValues[i] = "DERIVED";
                }
                else{
                    imageTypeValues[i] = singleType;
                }
            }
            DicomCSElement nuImageType = new DicomCSElement(DCM.E_IMAGE_TYPE, imageTypeValues);
            this.toolkitDDS.insert(nuImageType);
        }
    }
   
    private void changePhotometricInterpretation()throws DCSException{
        
        //SRS Patch50 3.2.3.8 Convert all reconstituted images to Monochrome2.
        //set Photometric Interpretation to Monochrome2.
        this.toolkitDDS.insert(DCM.E_PHOTOMETRIC_INTERPRETATION, "MONOCHROME2");
    }
    
    private void updateSOPClassToSecondaryCapture()throws DCSException{
        
        //SRS Patch50 3.2.3.2 Change SOP Class to Secondary Capture.
        //change SOP Class to Secondary Capture.
        //FUTURE I'm doing this, but I think it is wrong.  We should not change the SOP Class.
        //  However, this matches what is done in Patch 50. *** cpt: it is mostly overprotection now
        this.toolkitDDS.insert(new AttributeTag("0002,0002"), UID.SOPCLASSSECONDARYCAPTURE);
        this.toolkitDDS.insert(DCM.E_SOPCLASS_UID, UID.SOPCLASSSECONDARYCAPTURE);
    }
    
    private boolean isImageCTIOD()throws DCSException{
        
        boolean CT = false;
        String sopClass;
        sopClass = this.toolkitDDS.getElementStringValue(DCM.E_SOPCLASS_UID);
        if(sopClass.equals(UID.SOPCLASSCT)){
            CT = true;
        }
        return CT;
    }

    private boolean isImageSCIOD()throws DCSException{
        
        boolean SC = false;
        String sopClass;
        sopClass = this.toolkitDDS.getElementStringValue(DCM.E_SOPCLASS_UID);
        if(sopClass.equals(UID.SOPCLASSSECONDARYCAPTURE)){
            SC = true;
        }
        return SC;
    }
    
    private boolean isImageMRIOD() throws DCSException{
        
        boolean MR = false;
        String sopClass;
        sopClass = this.toolkitDDS.getElementStringValue(DCM.E_SOPCLASS_UID);
        if(sopClass.equals(UID.SOPCLASSMR)){
           MR = true;
        }
        return MR;
    }
    
    private void updateModalityCode()throws DCSException{
        
        //SRS Patch50 3.2.3.6 Validate/correct there is a single Modality Code in DDS.
                
        DicomCSElement modalityCodeElement = null;
        SOPClassModalityCodeMapping mcMap = new SOPClassModalityCodeMapping();
        if(this.toolkitDDS.containsElement(DCM.E_MODALITY)){
           modalityCodeElement = (DicomCSElement)this.toolkitDDS.findElement(DCM.E_MODALITY);
           String updateModalityCode;
           if(modalityCodeElement.vm() > 1){
               updateModalityCode = mcMap.getModalityCode(this.toolkitDDS.getElementStringValue(DCM.E_SOPCLASS_UID));
               if(updateModalityCode == null){
            	   updateModalityCode = modalityCodeElement.getStringValue(0);
               }
               this.toolkitDDS.insert(DCM.E_MODALITY, updateModalityCode);
           }
        }
        else{
            String nuModalityCode = "OT";
            if(this.toolkitDDS.containsElement(DCM.E_SOPCLASS_UID)){
                nuModalityCode = mcMap.getModalityCode(this.toolkitDDS.getElementStringValue(DCM.E_SOPCLASS_UID));
            }
            this.toolkitDDS.insert(DCM.E_MODALITY, nuModalityCode);
        }
    }
    
    private boolean isMultiFrameIOD()throws DCSException{
        boolean multiFrameCheck = false;
        if (this.toolkitDDS.getElementStringValue(DCM.E_SOPCLASS_UID).equals(
                UID.SOPCLASSUSMULTIFRAMEIMAGE)){
            multiFrameCheck = true;
        } 
        else if (this.toolkitDDS.getElementStringValue(DCM.E_SOPCLASS_UID).equals(
                UID.SOPCLASSNM)){
            multiFrameCheck = true;
        }
        else if ((this.toolkitDDS.containsElement(DCM.E_NUMBER_OF_FRAMES)) &&
        	(Integer.decode(this.toolkitDDS.getElementStringValue(DCM.E_NUMBER_OF_FRAMES).toString())!=1))
            multiFrameCheck = true;
        
        return multiFrameCheck;
    }
    
    private void updateCTIODInformation()throws DCSException{
        
        logger.debug(this.getClass().getName()+": Dicom Toolkit Layer: "+
                "...updating CT IOD using possible CT Parameters.");
        Parameters parameters = this.getCTParameters();
        //This method determines if the CT image is destined as a CT IOD or a SC IOD.
        //If CT Parameters exist, modify the dataset as CT IOD.
        if(parameters != null){
            logger.debug("Contains valid CT Parameters. Leave it as DICOM CT IOD.");
            this.modifyCTImageToCTIOD(parameters);
        }
        //If CT Parameters do not exist, modify the dataset as SC IOD.
        else{
            this.modifyImageToSCIOD();
        }
    }
    
    private void updateWindowValues()throws DCSException{
        
        //SRS Patch50 3.2.3.10 Calibrating the Window and Level values to match the 
        //  reconstituted image.
        //maximum_pixel_value = (1 << input_bits_per_pixel) - 1;
        
        //Determine Maximum Window and Level
// ** cpt: fixed window width (removed - 1) and center ( added - 1)
    	int maxWindowWidthValue = 1 << this.tgaBitsPerPixel;
        int maxWindowLevelValue = (maxWindowWidthValue/2) - 1;
        boolean wWCSet=false;
        
        //See if the current values in the DDS are larger
        if(this.toolkitDDS.containsElement(DCM.E_WINDOW_WIDTH)){
            if(this.toolkitDDS.getElementIntValue(DCM.E_WINDOW_WIDTH,0) > maxWindowWidthValue){
                //if true, assign new, default values.
                this.toolkitDDS.insert(DCM.E_WINDOW_WIDTH, maxWindowWidthValue);
                this.toolkitDDS.insert(DCM.E_WINDOW_CENTER, maxWindowLevelValue);
                this.toolkitDDS.removeElement(DCM.E_WINDOW_CNTR_AND_WIDTH_EXPL);
                wWCSet=true;
            }
        }
        else{
            //if no values, assign new, default values.
            //But first check to make sure there is no VOI LUT Sequence.
            if(!this.toolkitDDS.containsElement(DCM.E_VOI_LUT_SEQUENCE)){
                this.toolkitDDS.insert(DCM.E_WINDOW_WIDTH,maxWindowWidthValue);
                this.toolkitDDS.insert(DCM.E_WINDOW_CENTER, maxWindowLevelValue);
                this.toolkitDDS.removeElement(DCM.E_WINDOW_CNTR_AND_WIDTH_EXPL);
	            wWCSet=true;
            }
        }
        if (wWCSet) {
            // optionally adjust W/C explanation: take off 2nd,.. entries
            updateWindowLevelExplanation();
        }
    }

// ** cpt: added method to recompute window/center based on pixel values
    private void reComputeWindowValues()throws DCSException{
        
        //Recalculating the Window and Level values (for SC IODs only).
        //maximum_pixel_value = (1 << input_bits_per_pixel) - 1;
        int pixelMask = (1 << this.tgaBitsPerPixel) - 1;
        
        // find lowest and highest pixel values
        int minValue = pixelMask;
        int maxValue = 0;
        int pixelValue;
        int numPxs = this.tgaRows * this.tgaColumns;
    	if (pixelMask > 0xFF) { // word
    		for (int i=0; i < numPxs; i++ ) {
    			// pixelValue = (imageBuffer.getShort(i) & pixelMask);
    			pixelValue = (((imageBuffer.get((2*i) + 1) & 0xFF) * 256) + (imageBuffer.get(2*i)& 0xFF)) & pixelMask;
	        	if (pixelValue < minValue) minValue = pixelValue;
	        	if (pixelValue > maxValue) maxValue = pixelValue;
	        	pixelValue=0;
    		}
    	} else {
    		for (int i=0; i < numPxs; i++ ) {
    			pixelValue = (imageBuffer.get(i) & pixelMask);       		
	        	if (pixelValue < minValue) minValue = pixelValue;
	        	if (pixelValue > maxValue) maxValue = pixelValue;
	        	pixelValue=0;
    		}
    	}
        imageBuffer.rewind();

        int windowWidthValue;
        int windowLevelValue;
        //Determine Maximum Window and Level
        if (maxValue > (minValue + 1)) {
        	windowWidthValue = maxValue - minValue;
            windowLevelValue = minValue + (windowWidthValue/2);
        } else {
        	windowWidthValue = 1 << this.tgaBitsPerPixel;
            windowLevelValue = (windowWidthValue/2) - 1;
        }
        
        // assign new, default values.
        this.toolkitDDS.insert(DCM.E_WINDOW_WIDTH, windowWidthValue);
        this.toolkitDDS.insert(DCM.E_WINDOW_CENTER, windowLevelValue);
        
        // optionally adjust W/C explanation: take off 2nd,.. entries
        updateWindowLevelExplanation();
    }
    
//  ** cpt: added method to optionally adjust W/C explanation: take off 2nd,.. entries
//          must be called only if W/L was changed
    private void updateWindowLevelExplanation()throws DCSException{
    	
        if (this.toolkitDDS.containsElement(DCM.E_WINDOW_CNTR_AND_WIDTH_EXPL)){
        	// take first value only
        	String wCExpl=this.toolkitDDS.getElementStringValue(DCM.E_WINDOW_CNTR_AND_WIDTH_EXPL);
        	// replace old value(s) with first value
	        this.toolkitDDS.insert(DCM.E_WINDOW_CNTR_AND_WIDTH_EXPL, wCExpl); 
        }    	
    }
    
    private void updateMultiFrameInfoToSingleFrameInfo()throws DCSException{
        this.toolkitDDS.insert(DCM.E_NUMBER_OF_FRAMES,1);
        this.updateSOPClassToSecondaryCapture(); // *** cpt: must be updated with MF SC IODs
    }
    
    private void checkVendorCompression(){
        //Add code to check in comments field from GE PACS.
        //Here is the information for GE PACS archived images.
        //0008,2111|Derivation Description^ST|1,1|Non-reversible compressed image: For reference only [99]

        try{
            String compressionElement = this.toolkitDDS.getElementStringValue(new AttributeTag("0008, 2111"));
            String constant = "Non-reversible compressed image: For reference only [99]";
            if(compressionElement.equalsIgnoreCase(constant)){
                //set Lossy Image Compression field to 01.
                this.toolkitDDS.insert(DCM.E_LOSSY_IMAGE_COMPRESSION,"01");  // VR=CS - coded string!
                //Change Image Type to Derived.
                this.updateImageTypeToDerived();
            }
        }
        catch (DCSException dcse){
            return;
        }
    }
    
    private void updatesForAllModalities()throws DCSException{
        //Convert Samples Per Pixel field to 1.
        this.toolkitDDS.insert(DCM.E_SAMPLES_PER_PIXEL,1);
        //Change Pixel Representation to 0000H.
        //SRS Patch50 3.2.3.7 Correct Pixel Representation.
        this.toolkitDDS.insert(DCM.E_PIXEL_REPRESENTATION, 0);
        //Change Photometric to Monochrome2.
        this.changePhotometricInterpretation();
        this.updateModalityCode();
    }
    
    private Parameters getCTParameters(){
    	
    	//FUTURE -The CT_Params.dic and Modality.dic information is retrieved directly
    	//	from these files located in \DICOM\Dict\ folder.  The intent is to get this
    	//	information from VistA HIS.  Due to time constraints, we will address this later
    	//	when time permits.  There is no harm or limitation to continue reading the .dic 
    	//	files until time permits.  There is no current intention to run the HDIG on a 
    	//	box separate from the Legacy DGW.
    	
        String parameterString = "";
        Parameters acceptedParameters = null;
        
        //1.  Get the proper DCMTOTGA parameters if pixel depth is greater than 8bits.
        if(this.tgaBitsPerPixel > 8){
            //a.  If exist, use the DCMTOTGA parameters from the top section of the 
            //  Text file.  This starts appearing with Patch 50.
            parameterString = this.originalPixelDataInfo.getDcmtotgaParameters();
            if(!(parameterString == null)){
                logger.debug("The referenced Text File contains DCMTOTGA Parameters.");
                try{
                	acceptedParameters = new Parameters(parameterString);
                	if(acceptedParameters.isDICOMParameterSet()){
                		logger.warn(this.getClass().getName()+": DICOM Toolkit layer: \n"+
                				"The DCMTOTGA Parameter contains <DICOM>.  This is not valid "+
                				"for a Targa image.");
                		acceptedParameters = null;
                	}
                	return acceptedParameters;
                }
                catch(ParameterDecompositionException pdX){
                	logger.warn(this.getClass().getName()+": DICOM Toolkit layer: \n"+
                			"Could not decompose DCMTOTGA Parameters from referenced Text file.");
                	return null;
                }
            }
            
            try{
                //c.  If CT Image is newer than the date of the CT Historical Settings 
                //  file, use the DCMTOTGA parameters from the current Modality.dic global.
                
                //Get the CT_Parameters file Timestamp.  Convert to a Date.
                String ctParameterFileTimeStamp = DicomGatewayConfiguration.getInstance().getCTParametersTimeStamp();
                if(ctParameterFileTimeStamp == null || ctParameterFileTimeStamp.equals("")){
                    logger.debug("No CT Parameter file Time Stamp was found.");
                    return null;                	
                }
                logger.debug("CT Parameter Date Stamp: " + ctParameterFileTimeStamp);
                // SimpleDateFormat filePattern = new SimpleDateFormat("d-MMM-yyyy");
                SimpleDateFormat filePattern = new SimpleDateFormat("d-MMM-yyyy hh:mm:ss");
                Date ctParamFileDate;
                    ctParamFileDate = filePattern.parse(ctParameterFileTimeStamp);
                                
                //Get the Study Date from the DDS.  Convert to a Date.
                String studyDateString = this.toolkitDDS.getElementStringValue(DCM.E_STUDY_DATE);
                logger.debug("CT Image Study Date: "+studyDateString);
                SimpleDateFormat dicomPattern = new SimpleDateFormat("yyyyMMdd");
                Date studyDate = dicomPattern.parse(studyDateString);
                ModalityDicInfo deviceInfo = new ModalityDicInfo();
                deviceInfo.setManufacturer(this.toolkitDDS.getElementStringValue(DCM.E_MANUFACTURER).trim());
                deviceInfo.setModel(this.toolkitDDS.getElementStringValue(DCM.E_MANUFACTURERS_MODEL_NAME).trim());
                deviceInfo.setModalityCode(this.toolkitDDS.getElementStringValue(DCM.E_MODALITY));
                logger.debug("Mfg: " + deviceInfo.getManufacturer() +
                		 "   Model: "+deviceInfo.getModel() +
                		 "   Modality: " + deviceInfo.getModalityCode());
                if(studyDate.after(ctParamFileDate) || studyDate.equals(ctParamFileDate)){
                    //If the CT_Parmeters file is older, look in Modality.dic and extract 
                    //  the correct parameters.
                    Vector<ModalityDicInfo> modalityMatchingList;
                    modalityMatchingList = GatewayDictionaryContents.getInstance().getModalityDictionaryEntries();
                    if(modalityMatchingList == null){
                    	return null;
                    }
                    
                    Iterator<ModalityDicInfo> iterator = modalityMatchingList.iterator();
                    while(iterator.hasNext()){
                        ModalityDicInfo temp = (ModalityDicInfo)iterator.next();
                        if(deviceInfo.equals(temp)){
                            //Return parameters from modality.dic file.
                            logger.debug("Found match with Modality.dic file.");
                            acceptedParameters = temp.getDCMTOTGAParameters();
                        }
                    }
                }
                else{
                    //If the CT_Parameters file is newer, continue with the steps below.
                    //b.  If exist, return the DCMTOTGA parameters from the CT Historical 
                    //  Settings file (Ed’s Log).
                    Vector<ParameterDeviceInfo> ctParameterList;
                    ctParameterList = GatewayDictionaryContents.getInstance().getCTParametersList();
                    if(ctParameterList == null){
                    	return null;
                    }
                    //Find match based on Site/Mfg/Model
                    //ParameterDeviceInfo device = (ParameterDeviceInfo) deviceInfo;
                    ParameterDeviceInfo device = new ParameterDeviceInfo();
                    device.setManufacturer(deviceInfo.getManufacturer());
                    device.setModel(deviceInfo.getModel());
                    device.setModalityCode(deviceInfo.getModalityCode());
                    device.setDCMTOTGAParameters(deviceInfo.getDCMTOTGAParameters());
                    
                    if(this.acquisitionSite == null){
                        return null;
                    }
                    device.setSiteID(this.acquisitionSite);
                    Iterator<ParameterDeviceInfo> iterator = ctParameterList.iterator();
                    
                    while(iterator.hasNext()){
                         ParameterDeviceInfo deviceEntry = (ParameterDeviceInfo)iterator.next();
                         if(device.equals(deviceEntry)){
                             Date deviceEntryDate = deviceEntry.getChangeDate();
                             //Compare Date in List to current Study Date.
                             //You are looking for an entry that is same or older.
                             if((studyDate.equals(deviceEntryDate))
                                     ||(studyDate.after(deviceEntryDate))){
                                 //If above condition is met, assign the parameters from list entry to 
                                 //  current deviceInfo.
                                 logger.debug("Found match in CT Parameters file.");
                                 logger.debug("The Entry Date in the CT Parameters file is: " + deviceEntryDate);
                                 acceptedParameters = deviceEntry.getDCMTOTGAParameters();
                                 //I have to return as I find the first match.  I cannot
                                 // cycle through entire list.
                                 break;
                             }
                         }
                    }
                }
                //If no match is found in either search, return null.
                if(acceptedParameters == null){
                    return null;
                }

                //d.  If the DCMOTGA parameters cannot be retrieved by the steps 
                //  above, set to NULL.  This happens automatically if nothing is assigned
                //  to acceptedParameters variable.
                
                //2.  Determine appropriate action if there is a Rxx value (Reduction Factor)
                //  in the DCMTOTGA parameters.
                if(acceptedParameters.containsReductionFactor()){
                    if(acceptedParameters.containsSlash()){
                        //b.  Retrieve the .big image file instead of the .tga image file 
                        //  if there is a slash (“/”) symbol in the DCMTOTGA parameters.
                        //  Set the DCMTOTGA parameters to NULL if no .big image file exists.
                        if(bigFileFlag){
                            acceptedParameters.setFullParameterSet();
                        }
                        else{
                            acceptedParameters = null;
                        }
                        
                    }
                    else{
                        //a.  Set the retrieved DCMTOTGA parameters to NULL if there is no 
                        //  slash (“/”) symbol in the DCMTOTGA parameters.
                        acceptedParameters = null;
                    }
                }
                if(acceptedParameters != null){
                	if(acceptedParameters.isDICOMParameterSet()){
                		logger.warn(this.getClass().getName()+": DICOM Toolkit layer: \n"+
                				"The DCMTOTGA Parameter contains <DICOM>.  This is not valid "+
                				"for a Targa image.");
                		acceptedParameters = null;
                	}
                }
                return acceptedParameters;
            }
            catch(ParseException noparse){
                return null;
            }
            catch(DCSException dcse){
                return null;
            }
       }        
       return null;
    }
    
    private void modifyCTImageToCTIOD(Parameters params) throws DCSException{
        
        
        //SRS Patch50 3.2.3.10 Multiple fixes for CT images.
        //Remove selected fields from CT Image Module: 
        
        //a.  Add Sxxxx variable, if any, to Rescale Intercept.  Subtract the Axxxx variable, 
        //  if any, from the new Rescale Intercept value.  Update the Rescale Intercept field 
        //  with the new Rescale Intercept value.
        int nuRescaleIntercept = 0;
        if(this.toolkitDDS.containsElement(DCM.E_RESCALE_INTERCEPT)){
            nuRescaleIntercept = this.toolkitDDS.getElementIntValue(DCM.E_RESCALE_INTERCEPT);
        }
        else{
            nuRescaleIntercept = 0;
        }
        
        if(params.isSubtractionSet()){
            nuRescaleIntercept += params.getSubtractionParameter();
        }
        if(params.isAdditionSet()){
            nuRescaleIntercept -= params.getAdditionParameter();
        }
        
        this.toolkitDDS.insert(DCM.E_RESCALE_INTERCEPT, nuRescaleIntercept);
        this.toolkitDDS.insert(DCM.E_RESCALE_SLOPE, 0x01);
        //b.  Remove Largest Pixel field.
        this.toolkitDDS.removeElement(DCM.E_LARGEST_IMAGE_PIXEL_VALUE); // E_LARGEST_IMG_PXL_VAL_PLANE);
        this.toolkitDDS.removeElement(DCM.E_LARGEST_VALID_PIXEL_VAL_RET);
        this.toolkitDDS.removeElement(DCM.E_LARGEST_PIXEL_VALUE_SERIES);
        //c.  Remove Smallest Pixel field.
        this.toolkitDDS.removeElement(DCM.E_LARGEST_IMAGE_PIXEL_VALUE); // E_LARGEST_IMG_PXL_VAL_PLANE);
        this.toolkitDDS.removeElement(DCM.E_SMALLEST_VALID_PXL_VAL_RET);
        this.toolkitDDS.removeElement(DCM.E_SMALLEST_PIXEL_VAL_SERIES);
        //d.  Remove Largest Image Pixel field.
        this.toolkitDDS.removeElement(DCM.E_LARGEST_IMAGE_PIXEL_VALUE);
        //e.  Remove Smallest Image Pixel field.
        this.toolkitDDS.removeElement(DCM.E_SMALLEST_IMAGE_PIXEL_VALUE);
        //f.  Remove Pixel Padding field.
        this.toolkitDDS.removeElement(DCM.E_PIXEL_PADDING_VALUE);
        //Remove VOI LUT Sequence.
        this.toolkitDDS.removeElement(DCM.E_VOI_LUT_SEQUENCE);
        //h.  Set Pixel Representation value to 00 (unsigned data).
        this.toolkitDDS.insert(DCM.E_PIXEL_REPRESENTATION, 0x00);
        //i.  Re-calculate Window Width/Center values if Window Width value is greater 
        //  than the pixel depth.
        this.updateWindowValues();
        
        this.updateImageTypeToDerived();
    }
    
    private void modifyImageToSCIOD() throws DCSException{
        
        //SRS Patch50 3.2.3.10 Multiple fixes for CT images.
        //Remove selected fields from CT Image Module
        
    	this.toolkitDDS.insert(DCM.E_CONVERSION_TYPE, "WSD");
        //a.  Remove Rescale Slope field.
        this.toolkitDDS.removeElement(DCM.E_RESCALE_SLOPE);
        //b.  Remove Rescale Intercept field.
        this.toolkitDDS.removeElement(DCM.E_RESCALE_INTERCEPT);
        //c.  Change SOP Class value to Secondary Capture.
        this.updateSOPClassToSecondaryCapture();
        //d.  Set Lossy Compression value to 1.
// ** cpt: this flag is explicit about lossy compression. 
// The fact that IOD turns to SC is quite enough to emphasize data degradation,
// but it is not necessarily compression
        //e.  Set Pixel Representation value to 00 (unsigned data).
        this.toolkitDDS.insert(DCM.E_PIXEL_REPRESENTATION, 0x00);
        //f.  Remove Largest Pixel field.
        this.toolkitDDS.removeElement(DCM.E_LARGEST_IMAGE_PIXEL_VALUE); // E_LARGEST_IMG_PXL_VAL_PLANE);
        this.toolkitDDS.removeElement(DCM.E_LARGEST_VALID_PIXEL_VAL_RET);
        this.toolkitDDS.removeElement(DCM.E_LARGEST_PIXEL_VALUE_SERIES);
        //g.  Remove Smallest Pixel field.
        this.toolkitDDS.removeElement(DCM.E_LARGEST_IMAGE_PIXEL_VALUE); // E_LARGEST_IMG_PXL_VAL_PLANE);
        this.toolkitDDS.removeElement(DCM.E_SMALLEST_VALID_PXL_VAL_RET);
        this.toolkitDDS.removeElement(DCM.E_SMALLEST_PIXEL_VAL_SERIES);
        //h.  Remove Largest Image Pixel field.
        this.toolkitDDS.removeElement(DCM.E_LARGEST_IMAGE_PIXEL_VALUE);
        //i.  Remove Smallest Image Pixel field.
        this.toolkitDDS.removeElement(DCM.E_SMALLEST_IMAGE_PIXEL_VALUE);
        //j.  Remove Pixel Padding field.
        this.toolkitDDS.removeElement(DCM.E_PIXEL_PADDING_VALUE);
        //Remove VOI LUT Sequence.
        this.toolkitDDS.removeElement(DCM.E_VOI_LUT_SEQUENCE);
        //k.  Re-calculate Window Width/Center values if Window Width value is greater 
        //  than the pixel depth
// ** cpt: always recalculate window/center values for secondary capture IODs 
// as rescale slope and intercept is gone 
        this.reComputeWindowValues();
        
        //Alter Image Type field appropriately.
        this.updateImageTypeToDerived();
    }
    
    
    private boolean isPart10DicomFile(String filename){
    	boolean part10 = false;
    	try{
    		DicomFileInput file = new DicomFileInput(filename);
    		part10 = file.chapter10Encoding();
    		file.close();
    	}
    	catch(DCSException dcsX){
    		//Ignore.
    	}
    	return part10;
    }    
}

