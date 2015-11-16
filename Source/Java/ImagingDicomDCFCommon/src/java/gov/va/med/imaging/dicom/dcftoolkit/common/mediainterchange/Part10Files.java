/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: 
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswpeterb
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
package gov.va.med.imaging.dicom.dcftoolkit.common.mediainterchange;

import gov.va.med.imaging.core.interfaces.StorageCredentials;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNotFoundException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.dicom.common.DicomFileMetaInfo;
import gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet;
import gov.va.med.imaging.dicom.dcftoolkit.common.exceptions.Part10FileException;
import gov.va.med.imaging.dicom.dcftoolkit.common.impl.DicomDataSetImpl;
import gov.va.med.imaging.exchange.business.dicom.exceptions.DicomException;
import gov.va.med.imaging.exchange.business.storage.NetworkLocationInfo;
import gov.va.med.imaging.exchange.storage.DataSourceImageInputStream;
import gov.va.med.imaging.vista.storage.SmbStorageUtility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.lbs.CDS.CFGGroup;
import com.lbs.CDS.CFGAttribute;
//import com.lbs.DCS.CINFO;
import gov.va.med.imaging.dicom.dcftoolkit.common.mediainterchange.CINFO;
import com.lbs.DCS.DCM;
import com.lbs.DCS.DCSException;
import gov.va.med.imaging.dicom.dcftoolkit.common.DataSetByteReader;
import com.lbs.DCS.DicomCSElement;
import com.lbs.DCS.DicomDataSet;
import com.lbs.DCS.DicomFileInput;
import com.lbs.DCS.DicomFileOutput;
import com.lbs.DCS.DicomOBElement;
import com.lbs.DCS.DicomOWElement;
import com.lbs.DCS.DicomSessionSettings;
import com.lbs.DCS.DicomStreamReader;
import com.lbs.DCS.DicomStreamWriter;
import com.lbs.DCS.DicomStreamableDataElement;
import com.lbs.DCS.DicomUSElement;
import com.lbs.DCS.FileMetaInformation;
import com.lbs.DCS.UID;
import com.lbs.DCS.DicomElement;
import com.lbs.DCS.DicomStreamableDataElement.DataType;
import com.lbs.DCF.DCFException;

/**
 * @author vhaiswpeterb
 *
 */
public class Part10Files {
	
    private static Logger logger = Logger.getLogger(Part10Files.class);

	public static void storeAsPart10(String filename, String transferSyntax, IDicomDataSet dds)throws Part10FileException{
		
		try{
		DicomDataSet dataset = (DicomDataSet)dds.getDicomDataSet();
        //This is offered to give an alternative method to write the file
        //  to disk.  This does not use streaming.
        DicomFileOutput dfo = new DicomFileOutput(
                filename, transferSyntax, true, true );
        dfo.open();
        dfo.writeDataSet(dataset);
        dfo.close();
        logger.info("Saved message to disk by pushing DicomDataSet object to DicomFileOutput.");
		}
		catch(Exception X){
			logger.error("Failed to save DICOM object to disk by pushing DicomDataSet object to DicomFileOutput.");
			throw new Part10FileException();
		}	
	}
	
	public static void storeAsPart10(String filename, InputStream in) throws Part10FileException{
    	
		try{
			int block_size = 16384;
			byte buffer[] = new byte[block_size];

			FileOutputStream out = new FileOutputStream(filename);
			int count;

			while ((count = in.read( buffer )) > 0){
				out.write(buffer, 0, count);
			}
			out.close();
		}
		catch (Exception X){
			logger.error("Failed to save DICOM object to disk using InputStream from DataSetByteReader.");
			throw new Part10FileException();
		}
    }
	
	//BILL This does not work.  Fix it later.
/*	public void storeAsPart10(String filename, String transferSyntax, ReadableByteChannel message) throws Part10FileException{
    	
		try{
			DataSetByteReader dbr = (DataSetByteReader)message;
			int block_size = 16384;
			ByteBuffer buffer = ByteBuffer.allocate(block_size);
			DicomSessionSettings settings = new DicomSessionSettings();
			
			DicomFileWriter writer = new DicomFileWriter(filename, settings);
			DicomFileOutput out = new DicomFileOutput(writer, transferSyntax, true, false, settings);

			while(dbr.read(buffer) > 0 ){
				writer.write(buffer);
			}
			out.close();
			logger.info("Saved object to disk using DataByteReader.");
		}
		catch (Exception X){
			logger.error("Failed to save DICOM object to disk using DataByteReader.");
			throw new Part10FileException();
		}
    }
*/

	public static IDicomDataSet readDicomFile(NetworkLocationInfo networkLocationInfo, String filename, DicomFileInput fileIn)
			throws DicomException
	{
    // check for DICOM TS and if TS is supported, read the dataset, if TS is not supported, convert it to
	// the VI preferred DICOM TS (LEEVR) by writing it out to a temporary file (converting) and reading it back
    	String fromTSUID = null;
    	String toTSUID = UID.TRANSFERLITTLEENDIANEXPLICIT;
//		fromTSUID = extractTSUID(filename);
//		if (fromTSUID == null) {
//			logger.error("Failed to find/open/pre-scan a DICOM Part10 format file " + filename);
//			return null;
//		}
    	DicomDataSet dataSet = null;
    	FileMetaInformation metadata = null;
    	IDicomDataSet dds = new DicomDataSetImpl();
    	DicomSessionSettings dSS=null;
    	boolean decompress = false;
    	boolean firstFile=true;
		SmbStorageUtility util = new SmbStorageUtility();
		DataSourceImageInputStream fis = null;
		DicomStreamReader dsr = null;
    	try {
  			// first open file, and read dataset
			dSS = new DicomSessionSettings();
			try 
			{
				fis = util.openFileInputStream(filename, (StorageCredentials)networkLocationInfo);
			} 
			catch (Exception e) 
			{
				logger.error("Failed to open or read DICOM object file, " + filename + " as a stream.");
	    		throw new DicomException(e);
			}
			
			dsr = new DicomStreamReader(fis.getInputStream(), dSS);
			fileIn = new DicomFileInput(dsr, null, dSS);			
			fileIn.open();
    		dataSet = fileIn.readDataSet();
 			metadata = fileIn.fileMetaInformation();
 			fromTSUID = metadata.transferSyntaxUid();

			if (fromTSUID.equals(UID.RLE_LOSSLESS)) // "1.2.840.10008.1.2.5" SOP Class UID
   				convertRLEDataSet(dataSet); // // RLE DCF 3.3.22c bug fix support

			//		now determine if TS is in VI acceptable TSs
			decompress = true;
			try {
   				// get allowed TSs from \cfg\app\defaults\Listen config file
   				CFGGroup cfgOfDCS = CINFO.getConfig();
   				CFGGroup theTSGroup = cfgOfDCS.getGroup("default_session_cfg/supported_transfer_syntaxes");
    			CFGAttribute[] theTSs = theTSGroup.getAttributes();
    			int i;
    			for (i=0; i < theTSs[0].getValues().length; i++) {
    				if (theTSs[0].getValues()[i].equals(fromTSUID)) {
    					decompress=false;
        				break;
    				}
    			}
			}
    		catch (DCFException de) {
    			logger.error(de.getMessage());
    	        logger.error("Exception thrown while reading Attributes from the DCF configuration file.");
    		}
    		
    		if (decompress) {
    			
    			fileIn.close(true);
    			
    			// 		some conversion needed: Output dataset to same folder to a file called converted*.dcm
    			String newFName = filename.concat("2"); 	// append '2' to extension
    			firstFile = false;
    	    	//		reopen newly created (& renamed) file
    			OutputStream fos = null;
    			try 
    			{
    				fos = util.openOutputStream(newFName, (StorageCredentials)networkLocationInfo);
    			} 
    			catch (Exception e) 
    			{ 
   	    			logger.error("Failed to open output stream for " + newFName);
   	    			throw new DicomException(e);
    			}

    			DicomStreamWriter dsw = new DicomStreamWriter(fos, dSS);
    			DicomFileOutput dfo = new DicomFileOutput(
    	    							dsw,
    	                                toTSUID,
    	                                true, false, dSS); // make Part 10 header, keep group 2
    	    	dfo.open();
        		dataSet.insert(DCM.E_TRANSFER_SYNTAX_UID, toTSUID);    	    	
    	    	dfo.writeDataSet( dataSet );
    	    	dfo.close();
    	    	
    	    	dataSet.clear();
    	    	
    			//		replace original file with new file
    	    	try 
    	    	{
	    	    	if (util.fileExists(filename, (StorageCredentials)networkLocationInfo)) 
	    	    	{
	    	    		util.deleteFile(filename, (StorageCredentials)networkLocationInfo);
	    			}
	    	    	
    				util.renameFile(newFName, filename, (StorageCredentials)networkLocationInfo);
    				
    	    	} 
    	    	catch (IOException ioe) 
    	    	{
	    			logger.error("Failed to write / rename converted DICOM object file, " + newFName);
	    			throw new DicomException(ioe);
    	    	}
    	    	
    	    	//		reopen newly created (& renamed) file
    			try 
    			{
    				fis = util.openFileInputStream(filename, (StorageCredentials)networkLocationInfo);
    			} 
    			catch (Exception e) 
    			{ 
   	    			logger.error("Failed to open or read DICOM object file, " + filename + " as a stream.");
   	    			throw new DicomException(e);
    			}
    			
    			dsr = new DicomStreamReader(fis.getInputStream(), dSS);
    	    	fileIn = new DicomFileInput(dsr, null, dSS);			
    			fileIn.open();
        		dataSet = fileIn.readDataSet();
     			metadata = fileIn.fileMetaInformation();
     			fromTSUID = metadata.transferSyntaxUid();

    		}
    		
 			dds.setDicomDataSet(dataSet);
			dds.setSourceAET(getSourceAeTitle(metadata));
    		dds.setReceivedTransferSyntax(fromTSUID);
   			
		} 
    	catch (DCSException dcsX) 
		{
    		String message = "";
    		if (firstFile) 
    		{
    			message = "Failed to open or read DICOM object file, " + filename;
    		} 
    		else 
    		{
    			message = "Failed to open, convert or read DICOM object file " + filename + 
    						 " (Convert from " + fromTSUID + " to " + toTSUID + ")";
    		}
    		
    		logger.error(message);
            throw new DicomException(message, dcsX);
		} 
		return dds;
    }

	
	public static IDicomDataSet readLocalDicomFile(String filename, DicomFileInput fileIn)
			throws DicomException
	{
		// check for DICOM TS and if TS is supported, read the dataset, if TS is not supported, convert it to
		// the VI preferred DICOM TS (LEEVR) by writing it out to a temporary file (converting) and reading it back
    	String fromTSUID = null;

    	DicomDataSet dataSet = null;
    	FileMetaInformation metadata = null;
    	IDicomDataSet dds = new DicomDataSetImpl();
    	DicomSessionSettings dSS=null;
		FileInputStream fis = null;
		DicomStreamReader dsr = null;
    	try {
  			// first open file, and read dataset
			dSS = new DicomSessionSettings();
			try 
			{
				fis = new FileInputStream(filename);
			} 
			catch (Exception e) 
			{
				logger.error("Failed to open or read DICOM object file, " + filename + " as a stream.");
	    		throw new DicomException(e);
			}
			
			dsr = new DicomStreamReader(fis, dSS);
			fileIn = new DicomFileInput(dsr, null, dSS);			
			fileIn.open();
    		dataSet = fileIn.readDataSet();
 			metadata = fileIn.fileMetaInformation();
 			fromTSUID = metadata.transferSyntaxUid();

 			dds.setDicomDataSet(dataSet);
			dds.setSourceAET(getSourceAeTitle(metadata));
    		dds.setReceivedTransferSyntax(fromTSUID);
   			
		} 
    	catch (DCSException dcsX) 
		{
    		String message = "";
   			message = "Failed to open or read DICOM object file, " + filename;
    		
    		logger.error(message);
            throw new DicomException(message, dcsX);
		} 
		return dds;
    }

	private static String getSourceAeTitle(FileMetaInformation metadata)
	{
		try
		{
			return metadata.sourceAeTitle();
		}
		catch (Exception e) {}
		
		return "";
	}

/*		this is not used now, but handy 
	final static int MarkSize = 520;  // this value needs to be at least 128+4+30+68+68+68+1=367

	private static String extractTSUID(String filename) {
		String tSUID=null;
		FileInputStream ins=null;
        try {
            ins = new FileInputStream(filename);
            ins.skip(128); 
			int byte1, byte2, byte3, byte4 = 0;
			byte1 = ins.read();
			byte2 = ins.read();
			byte3 = ins.read();
			byte4 = ins.read();
			if((byte1== 68) && (byte2 == 73) && (byte3 == 67) && (byte4 == 77)) { // found the DICM string
				String UID=""; // look for DICOM tag (0002,0010) TS UID				
				for (int i=132; (i < MarkSize-8); ) {
					if (UID.length()==0) 
						byte1 = ins.read();
					else
						UID="";
					byte2 = ins.read();
					// fact: DICOM group 2 is always in Little Endian Explicit VR!
					if ((byte1 == 2) && (byte2 == 0)){
						byte3 = ins.read();
						byte4 = ins.read();
						if((byte3 == 0x10) && (byte4 == 0)) {
							// DICOM tag (0002,0010) -- TS UID -- found
							ins.skip(4); //  skip VR and length
							i+=8;
							UID=""; 
							while ((i<MarkSize) && ((byte1 = ins.read()) >= 0x2e)) {
								UID += Character.toString((char)byte1); // collect TS UID string
								i++;
							}
						} else
							i+=2;
					} else
						i+=2;
					if (UID.length() > 0)
						break;
				}
				tSUID=UID;
			} else { 
				// DICM string was not found
				return null;
			}
        } catch (IOException ioe) {
        	return null;
        } finally {
        	try {
        		if (ins != null) {
        			ins.close();
        		}
            } catch (IOException ioe) {}
        }
		return tSUID;
	}
*/
	// RLE DCF 3.3.22c bug fix support		
    private static void convertRLEDataSet(DicomDataSet ds) throws DCSException
    {
       boolean isPaletteColorImage = ds.findElement(DCM.E_PHOTOMETRIC_INTERPRETATION).getStringValue().trim()
                .equals("PALETTE COLOR");
        
       if (isPaletteColorImage)
       {
            DicomElement bits = ds.findElement(DCM.E_BITS_ALLOCATED);
            int outputBits = bits.getIntValue(); // 0, 8 or 16
            if ( (outputBits != 0) && (outputBits != 8) && (outputBits != 16) )
        	    outputBits = 0;
            DicomElement pix = ds.findElement(DCM.E_PIXEL_DATA);

        	logger.debug("Processing Photometric Interpretation PALETTE COLOR.");

            // Provides info about the number of samples, the minimum sample value, and bits per sample.
            int[] redPaletteInfo = ((DicomUSElement) ds.findElement(DCM.E_RED_PALETTE_COLOR_LUT_DESC)).getUSData();
            int[] greenPaletteInfo = ((DicomUSElement) ds.findElement(DCM.E_GREEN_PALETTE_CLR_LUT_DESC)).getUSData();
            int[] bluePaletteInfo = ((DicomUSElement) ds.findElement(DCM.E_BLUE_PALETTE_COLOR_LUT_DESC)).getUSData();
            if (redPaletteInfo[0] != greenPaletteInfo[0] || greenPaletteInfo[0] != bluePaletteInfo[0])
            {
                throw new DCSException("number of entries is different between channels");
            }
            if (redPaletteInfo[1] != greenPaletteInfo[1] || greenPaletteInfo[1] != bluePaletteInfo[1])
            {
                throw new DCSException("first entry is different between channels");
            }
            if (redPaletteInfo[2] != greenPaletteInfo[2] || greenPaletteInfo[2] != bluePaletteInfo[2])
            {
                throw new DCSException("number of bits is different between channels");
            }
            
            int pixelBits = ds.findElement(DCM.E_BITS_STORED).getIntValue();
            if (outputBits == 0) {
                outputBits = redPaletteInfo[2];
            }

            int[] redPaletteLUT = convertToLUT((DicomOWElement) ds.findElement(DCM.E_RED_PALETTE_COLOR_LUT_DATA), redPaletteInfo, pixelBits, outputBits);
            int[] greenPaletteLUT = convertToLUT((DicomOWElement) ds.findElement(DCM.E_GREEN_PALETTE_CLR_LUT_DATA), greenPaletteInfo, pixelBits, outputBits);
            int[] bluePaletteLUT = convertToLUT((DicomOWElement) ds.findElement(DCM.E_BLUE_PALETTE_COLOR_LUT_DATA), bluePaletteInfo, pixelBits, outputBits);
            
            // dumpLuts();

            // note that this assumes unsigned pixel data.
            DicomStreamableDataElement sPix = (DicomStreamableDataElement)pix;
            int dataType = sPix.getBufferDataType();
            if (dataType == DataType.BYTE || dataType == DataType.SBYTE)
            {
                byte[] pixels = sPix.getByteData(true, true);
                if (outputBits == 8)
                {
                    byte[] tmpPix = new byte[pixels.length * 3];
                    for (int i = 0; i < pixels.length; i++)
                    {
                        int pixelValue = pixels[i] & 0xFF;
                        int j = i * 3;
                        tmpPix[j] = (byte) redPaletteLUT[pixelValue];
                        tmpPix[j + 1] = (byte) greenPaletteLUT[pixelValue];
                        tmpPix[j + 2] = (byte) bluePaletteLUT[pixelValue];              
                    }
                    ds.insert(new DicomOBElement(DCM.E_PIXEL_DATA, tmpPix));
                }
                else /*(outputBits == 16)*/
                {
                    short[] tmpPix = new short[pixels.length * 3];
                    for (int i = 0; i < pixels.length; i++)
                    {
                        int pixelValue = pixels[i] & 0xFF;
                        int j = i * 3;
                        tmpPix[j] = (short) redPaletteLUT[pixelValue];
                        tmpPix[j + 1] = (short) greenPaletteLUT[pixelValue];
                        tmpPix[j + 2] = (short) bluePaletteLUT[pixelValue];              
                    }
                    ds.insert(new DicomOWElement(DCM.E_PIXEL_DATA, tmpPix));
                }
            }
            else if (dataType == DataType.USHORT || dataType == DataType.SHORT)
            {
                short[] pixels = sPix.getShortData(true, true);
                int nBitShift = 0 /* redPaletteInfo[2] == 8 ? 0 : 8 */;
                if (outputBits == 8)
                {
                    byte[] tmpPix = new byte[pixels.length * 3];
                    for (int i = 0; i < pixels.length; i++)
                    {
                        int pixelValue = pixels[i] & 0xFFFF; // ((pixels[i] >> 8) & 0xFF) | ((pixels[i] << 8) & 0xFF00);
                        int j = i * 3;
                        tmpPix[j] = (byte) (redPaletteLUT[pixelValue] >> nBitShift);
                        tmpPix[j + 1] = (byte) (greenPaletteLUT[pixelValue] >> nBitShift);
                        tmpPix[j + 2] = (byte) (bluePaletteLUT[pixelValue] >> nBitShift);              
                    }
                    ds.insert(new DicomOBElement(DCM.E_PIXEL_DATA, tmpPix));
                    ds.insert(new DicomUSElement(DCM.E_BITS_ALLOCATED, 8));
                    ds.insert(new DicomUSElement(DCM.E_BITS_STORED, 8));
                    ds.insert(new DicomUSElement(DCM.E_HIGH_BIT, 7));
                }
                else
                {
                    short[] tmpPix = new short[pixels.length * 3];
                    for (int i = 0; i < pixels.length; i++)
                    {
                        int pixelValue = pixels[i] & 0xFFFF;
                        int j = i * 3;
                        tmpPix[j] = (short) redPaletteLUT[pixelValue];
                        tmpPix[j + 1] = (short) greenPaletteLUT[pixelValue];
                        tmpPix[j + 2] = (short) bluePaletteLUT[pixelValue];              
                    }
                    ds.insert(new DicomOWElement(DCM.E_PIXEL_DATA, tmpPix));
                }
            }
            else
            {
                throw new DCSException("invalid pixel data type(" + dataType + ")");
            }
            
            // Change Photometric Interpretation value, Samples Per Pixel 1 -> 3, and Planar Configuration
            ds.insert(new DicomCSElement(DCM.E_PHOTOMETRIC_INTERPRETATION, "RGB "));
            ds.insert(new DicomUSElement(DCM.E_SAMPLES_PER_PIXEL, 3));
            ds.insert(new DicomUSElement(DCM.E_PLANAR_CONFIGURATION, 0));
            ds.removeElement(DCM.E_RED_PALETTE_COLOR_LUT_DESC);
            ds.removeElement(DCM.E_GREEN_PALETTE_CLR_LUT_DESC);
            ds.removeElement(DCM.E_BLUE_PALETTE_COLOR_LUT_DESC);
            ds.removeElement(DCM.E_RED_PALETTE_COLOR_LUT_DATA);
            ds.removeElement(DCM.E_GREEN_PALETTE_CLR_LUT_DATA);
            ds.removeElement(DCM.E_BLUE_PALETTE_COLOR_LUT_DATA);
            ds.removeElement(DCM.E_PALETTE_COLOR_LUT_UID);
       }
    }

    private static List<Object> iLuts = new ArrayList<Object>();
    private static List<Object> oLuts = new ArrayList<Object>();
    /**
     * Convert one channel of a palette color lut to one channel of an 8 or 16 bit RGB lut.
     * If the palette color specifies 16bit values, and outputBits is 8, we divide these by
     * 256 to provide an 8bit output value.  We assume that the palette color channel has unsigned
     * pixel values.
     * @param el the lut data OW element.
     * @param paletteInfo the array of size 3 that constitutes the lut descriptor.
     * @param outputBits the number of output bits, 8 or 16 for 16 bit luts, or 8 for 8 bit luts.
     * @return an integer lut that maps the palette to an 8 or 16 bit rgb channel depending on outputBits.
     * @throws DCSException for invalid values or parameters.
     */
    private static int[] convertToLUT(DicomOWElement el, int[] paletteInfo, int pixelBits, int outputBits) throws DCSException
    {
        if (paletteInfo.length != 3)
            throw new DCSException("invalid length for paletteInfo");
        int nEntries = paletteInfo[0] == 0 ? 65536 : paletteInfo[0];
        int firstEntry = paletteInfo[1];
        int nBits = paletteInfo[2];
        if (nBits != 8 && nBits != 16)
            throw new DCSException("invalid bits allocated");
        
        if (outputBits > nBits) {
            // note that we do not convert 8 bit to 16 bit pixel values
            throw new DCSException("invalid output bits");
        }

        int[] lut;

        if (el.length() == nEntries) {
            // bits allocated was 8
            byte[] entries = el.getByteData(true, true);
            iLuts.add(entries);
            
            lut = new int[256];
            int mask = 0xFF;
            for (int i = 0; i < lut.length; i++) {
                int tmp;
                if (i < firstEntry)
                    tmp = entries[0];
                else if (i >= firstEntry + nEntries)
                    tmp = entries[entries.length - 1];
                else
                    tmp = entries[i - firstEntry];
                lut[i] = (tmp & mask);
            }
            oLuts.add(lut);
        }
        else if (el.length() == 2 * nEntries)
        {
            // bits allocated was 16
            short[] entries = el.getShortData(true, false);
            iLuts.add(entries);
            lut = new int[65536];
            int nBitShift, mask;
//            if (pixelBits > 8 && nBits == 16)
//            {
                if (outputBits == 8) {
                    nBitShift = 8;
                    mask = 0xFF;
                }
                else {
                    nBitShift = 0;
                    mask = 0xFFFF;
                }
//            }
//            else {
//                nBitShift = 0;
//                mask = 0xFF;
//            }
            for (int i = 0; i < 65536; i++)
            {
                int tmp;
                if (i < firstEntry)
                    tmp = entries[0];
                else if (i >= firstEntry + nEntries)
                    tmp = entries[entries.length - 1];
                else
                    tmp = entries[i - firstEntry];
                lut[i] = (tmp >> nBitShift) & mask;
            }
            oLuts.add(lut);
        }
        else
            throw new DCSException("invalid bits allocated");

        return lut;
    }
    // end RLE DCF bug fix

	// ParseIOD from DicomStorageSCPImpl morphed into here! 
    // Also allows now not forcing metaheader creation!
    public static DataSetByteReader getDicomFileReadableByteChannel(IDicomDataSet dds, DicomFileMetaInfo metaData) throws DicomException {

    	DataSetByteReader dbr = null;
		DicomDataSet dataSet = (DicomDataSet)dds.getDicomDataSet();
        FileMetaInformation filemeta = null;
        byte metaVersion[] = new byte[2];

        // if metaData is null -- a part10 file was read in -- no need to create filemeta
        if (metaData != null) { 
			// Set the DICOM version for the MetaFileInfo.  The MetaFileInfo is the Group 0002 information.
			// Group 0002 must be created for the DataSetByteReader to process successfully.
	        metaVersion[0] = 1;
	        metaVersion[1] = 0;
		}
                
        try{
            // if metaData is null -- a part10 file was read in -- no need to create filemeta (null)
            if (metaData != null) { // a DICOM feed received -- needs new meta header
                // build FileMetaInformation object.
            	filemeta = new FileMetaInformation();
	            filemeta.transferSyntaxUid(metaData.getTransfersyntaxUID());
	            filemeta.fileMetaInformationVer(metaVersion);
	            filemeta.implementationClassUid(metaData.getImplementationClassUID());
	            filemeta.implementationVersionName(metaData.getImplementationVersionName());
	            filemeta.sourceAeTitle(metaData.getSourceAET());
            }
            //Create the DataSetByteReader object.  This sets up using streaming rest
            //  of the message, which contains the pixel data.
            dbr = new DataSetByteReader(
                dataSet,
                dds.getReceivedTransferSyntax(),
                true, // create part 10 header
                (filemeta!=null), // invent group 2 data
                filemeta,
                null,
                new DicomSessionSettings());

            return dbr;
        }
        catch (DCSException dcsX)
        {
        	String message = "DICOM Correct failed to create DataSetByteReader object. "+ dcsX.getMessage(); 
            logger.error(message);
            throw new DicomException(message, dcsX);
        } 
        catch (Exception e) 
        {
        	String message = "DICOM Correct failed to create DataSetByteReader object. "+ e.getMessage(); 
            logger.error(message);
            throw new DicomException(message, e);
		}
    }
    
    public static InputStream getDicomFileInputStream(ReadableByteChannel message) 
    throws DicomException
    {
    	try 
    	{
			return Channels.newInputStream(message);
		} 
    	catch (Exception e) 
    	{
            logger.error("DICOM Correct failed to create a FileInputStream from the ReadableByteChannel. "+ e.getMessage());
            throw new DicomException();
		}
    }
}

