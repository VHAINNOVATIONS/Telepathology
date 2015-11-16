/*
 * Created on Nov 17, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package gov.va.med.imaging.exchange;

import gov.va.med.imaging.core.interfaces.exceptions.ImageNotFoundException;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.ClonableAdler32;

import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedInputStream;

import org.apache.log4j.Logger;


/**
 * <p>Title: FileTypeIdentifier </p> 	
 * <p>Description: Determines the type of image based on the data recieved from the input stream.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Dept. of Veterans Affairs - VistA Imaging Project</p>
 * @author Julian Werfel
 * @version 1.0
 * 
 * 
 * 
 * TIF info:
 * http://www.mail-archive.com/tika-commits@lucene.apache.org/msg00087.html
 * 	Big endian, starts with "0x4d4d002a"
 *  Little endian, starts with "0x49492a00"
 * 
 * 
 */
public class FileTypeIdentifierStream 
extends BufferedInputStream 
{
	// define the values it will return for each type of file stored in Imaging
	final static String Default = "text/plain";//"TXT";	
	final static String DOC = "application/msword";//"DOC";
	final static String TGA = "image/x-targa";//"TGA";
	final static String DCM = "application/dicom";//"DCM";
	final static String DCMJPG = "application/dicom/jpeg";// DCM JPEG / JPEG LS lossy and lossless 
	final static String DCMJ2K = "application/dicom/jpeg2000";//DCM JPEG2000 image (or part2 multi-component) lossless, lossy; and JPIP
	final static String DCMMP2 = "application/dicom/mpeg2";//"DCM MPEG2 main profile @ main level";
	final static String DCMRLE = "application/dicom/rle";// lossless run-length encoding;
	final static String DCMPDF = "application/dicom/pdf";// DICOM encapsulated PDF file;
	final static String DCMCDA = "application/dicom/cda";// DICOM encapsulated CDA file;
	final static String AVI = "video/x-msvideo";//"AVI";
	final static String PDF = "application/pdf";//"PDF";
	final static String JPG = "image/jpeg";//"JPG";
	final static String RTF = "text/rtf";//"RTF";
	final static String BMP = "image/bmp";//"BMP";
	final static String TIF = "image/tiff";//"TIF";
	final static String WAV = "audio/x-wav";//"WAV";
	final static String HTML = "text/html";//"HTML";
	final static String MP3 = "audio/mpeg";//"MP3";
	final static String MPG = "video/mpeg";//"MPG";
	final static String J2K = "image/j2k"; // JPEG 2000
	final static String PNG = "image/png"; // PNG
	final static String GIF = "image/gif"; // GIF
	final static String XLS = "application/vnd.ms-excel";
	final static String DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
	final static String XML = "text/xml";
	
	private final static ImageFormat defaultImageFormat = ImageFormat.ORIGINAL;
	
	// the size of the data the buffer will hold before it loses its mark location.
	// DICOM files read the 128th byte preamble plus "DICM", plus group 2 up to Transfer Syntax UID
	final static int MarkSize = 520;  // so this value needs to be at least 128+4+30+68+68+68+1=367
	
	private ClonableAdler32 runningChecksum = null;
	private ClonableAdler32 markedChecksum = null;
	
	private int runningLength = 0;
	private int markedLength = 0;
	
	private Logger logger;
	private synchronized Logger getLogger()
	{
		if( logger == null) {
		
			logger = Logger.getLogger(this.getClass());
//			BasicConfigurator.configure();
		}
	
		return logger;
	}
	
	/**
	 * Default constructor for FileTypeIdentifierStream. This calls the inherited constructor. 
	 * @param In The input file stream, it must be opened prior to using the FileTypeIdentifierStream
	 * @see java.io.BufferedInputStream
	 */	
	public FileTypeIdentifierStream(InputStream In) 
	{
		super(In);
		runningChecksum = new ClonableAdler32();
	}
	
	/**
	 * Default constructor for FileTypeIdentifierStream. This calls the inherited constructor. 
	 * @param In The input file stream, it must be opened prior to using the FileTypeIdentifierStream
	 * @param Size The size of the buffer to use.
	 * @see java.io.BufferedInputStream
	 */	
	public FileTypeIdentifierStream(InputStream In, int Size) 
	{
		super(In, Size);
		runningChecksum = new ClonableAdler32();
	}
	
	/**
	 * Returns the Adler32 checksum of the Stream
	 * at its current location.  This checksum value
	 * should probably be used only after the file has been 
	 * completely read.
	 * @return
	 */
	public long getChecksumValue()
	{
		return runningChecksum.getValue();
	}
	
	/**
	 * @return The current location in the file (number of bytes).
	 * Once the file is completely read, this is the file length.
	 */
	public int getRunningLength()
	{
		return runningLength;
	}
	
	/**
	 * Determines if the file is a Bitmap image. Always return a file type or the default.
	 * @param input1 The first byte of data read from the file, this function needs this first byte
	 * @throws IOException
	 * @return A valid file type or the default value
	 */	
	private String tryBMPorTIF(int byte1) 
	throws IOException
	{
		int byte2 = 0;
		int byte3 = -1;
		if(byte1 == 77) {	// if the first byte is a 77
			byte2 = read();	// read in the next byte
			if(byte2 == 66) return BMP;	// if it is a 66 then this image is a BMP
			byte3 = read();
			if((byte2 == 77) && (byte3 == 0)) return TIF;
		}
		else {
			byte2 = read();
			if(byte2 == 77) return BMP;
			byte3 = read();	// read in the third byte
		}		
		return tryTGA(3, byte2, byte3);	// return the value from trying a TGA
	}
	/**
	 * Determines if the file is a TIFF image or an MP3 file. Always return a file type or the default.
	 * @throws IOException
	 * @return A valid file type or the default value
	 */	
	private String tryTIF() 
	throws IOException 
	{
		// read in the first 2 bytes
		int byte2 = read();
		int byte3 = read();
		if((byte2 == 73) && (byte3 == 42)) { // if the next two bytes are 73 and 42 
			return TIF;	// this is a TIF file
		}
		else if((byte2 == 68) && (byte3 == 51)) { // if the next two bytes are a 68 and 51
			return MP3;	// this is an MP3 file
		}
		else {	// determine if this is a TGA file
			return tryTGA(3, byte2, byte3);
		}
	}
    
	/**
	 * Determines if the file is a JPG image. Always return a file type or the default.
	 * @param input1 The first byte of data read from the file, this function needs this first byte
	 * @throws IOException
	 * @return A valid file type or the default value
	 */	
	private String tryJPG(int byte1) 
	throws IOException 
	{
		int byte2 = 0;
		int byte3, byte4, byte5 = 0;
		byte2 = read();
		if(byte2 == 216) {	// 0xD8; might need to check next byte for TGA, but not totally sure...
			int skipped = 0;
			for(int i = read(); i != -1; i = read()) {
				skipped++;
				if(i == 255) { // 0xFF
					byte1 = read();
					if(byte1 == 224) { // 0xE0
						byte1 = read(); // input the length
						byte1 = read(); // input the length
						byte1 = read();
						byte2 = read();
						byte3 = read();
						byte4 = read();
						if((byte1 == 74) && (byte2 == 70) && (byte3 == 73) && (byte4 == 70)) {
							// 0x 45 78 69 66 "Exif"
							return JPG;
						}
						else if((byte1 == 69) && (byte2 == 120) && (byte3 == 105) && (byte4 == 102)) {
							// 0x 45 46 49 46 "JFIF"
							return JPG;
						}
						else {
							return checkOtherTypes(skipped + 9);
						}
					}
					else if(byte1 == 225) { // 0xE1
						byte1 = read(); // input the length
						byte1 = read(); // input the length
						byte1 = read();
						byte2 = read();
						byte3 = read();
						byte4 = read();	
						if((byte1 == 69) && (byte2 == 120) && (byte3 == 105) && (byte4 == 102)) {
							// 0x 45 46 49 46 "JFIF"
							return JPG; // this is a JPG file
						}
						else {
							return checkOtherTypes(skipped + 9); // check other file types
						}
					}
					else if(byte1 == 238) { // 0xEE
						byte1 = read(); // input the length
						byte1 = read(); // input the length
						byte1 = read();
						byte2 = read();
						byte3 = read();
						byte4 = read();	
						byte5 = read();	
						if((byte1 == 65) && (byte2 == 100) && (byte3 == 111) && (byte4 == 98) && (byte5 == 101)) {
							// 0x 41 64 6F 62 65 "Adobe"
							return JPG; // this is a JPG-LS (lossless) file
						}
						else {
							return checkOtherTypes(skipped + 10); // check other file types
						}
					}
				}
			}
		}
		else if(byte2 == 79) // trying JPEG 2000 (not DICOM)
		{
			byte3 = read();
			byte4 = read();
			if((byte3 == 255) && (byte4 == 81))
			{
				return J2K;
			}
			else
				return checkOtherTypes(4);
		}
		else {
			byte3 = read(); // read in the third byte
			return tryTGA(3, byte2, byte3);
		}
		return checkOtherTypes(2); // this should not ever happen
	}
    
	/**
	 * Determines if the file is a Raster TGA image. Always return a file type or the default.
	 * @throws IOException
	 * @return A valid file type or the default value
	 */	
	private String tryRasterTGA() throws IOException {
		int byte2, byte3, byte4 = 0;
		byte2 = read();
		byte3 = read();
		byte4 = read();
		
		if((byte2 == 166) && (byte3 == 106) && (byte4 == 149)) {
			return TGA;	// Sun raster image data
		}
		return tryTGA(4, byte2, byte3);
	}
    
	/**
	 * Determines if the file is a PDF file. Always return a file type or the default.
	 * @throws IOException
	 * @return A valid file type or the default value
	 */	
	private String tryPDF() throws IOException {
		int byte2, byte3, byte4, byte5 = 0;
		byte2 = read();
		byte3 = read();
		byte4 = read();
		byte5 = read();
		if((byte2 == 80) && (byte3 == 68) && (byte4 == 70) && (byte5 == 45)) {
			return PDF;
		}
		return tryTGA(5, byte2, byte3);
	}
    
	/**
	 * Determines if the file is an RTF file. Always return a file type or the default.
	 * @throws IOException
	 * @return A valid file type or the default value
	 */	
	private String tryRTF() throws IOException {
		int byte2, byte3, byte4, byte5 = 0;
		byte2 = read();
		byte3 = read();
		byte4 = read();
		byte5 = read();
		if((byte2 == 92) && (byte3 == 114) && (byte4 == 116) && (byte5 == 102)) {
			return RTF;
		}
		return tryTGA(5, byte2, byte3);
	}
	
	/**
	 * 50 4B 03 04 14 00 06 00
	 * 
	 * To properly identify the actual Office type we will need to crack open the zip file and look at the [Content_Types].xml file
	 * 
	 */
	private String tryOffice2007Doc()
	throws IOException
	{
		int byte2, byte3, byte4, byte5, byte6, byte7, byte8 = 0;
		byte2 = read();
		byte3 = read();
		byte4 = read();
		byte5 = read();
		byte6 = read();
		byte7 = read();
		byte8 = read();
		if((byte2 == 75) && 
			(byte3 == 3) && 
			(byte4 == 4) && 
			(byte5 == 20) && 
			(byte6 == 0) && 
			(byte7 == 6) && 
			(byte8 == 0))
		{
			// not actually sure its docx - could be PPTX or XLSX
			return DOCX;
		}
		return tryTGA(8, byte2, byte3);
	}
	
	/**
	 * If the document starts with: D0 CF 11 E0 A1 B1 1A E1
	 * @return
	 * @throws IOException
	 */
	private String tryMSOffice()
	throws IOException
	{
		int byte2, byte3, byte4, byte5, byte6 = 0;
		byte2 = read();
		byte3 = read();
		byte4 = read();
		byte5 = read();
		byte6 = read();
		if((byte2 == 207) && (byte3 == 17) && (byte4 == 224) && (byte5 == 161) && (byte6 == 177)) 
		{
			// it is an MS Office document
			return identifyMSOffice(6);
		}
		return tryTGA(6, byte2, byte3);
	}
	
	private String identifyMSOffice(int bytesRead)
	throws IOException
	{
		// XLS files have the following pattern at the 512 byte offset: 09 08 10 00 00 06 05 00
		int loc = 512 - bytesRead;
		skip(loc);
		int byte1 = read();
		int byte2 = read();
		int byte3 = read();
		int byte4 = read();
		int byte5 = read();
		int byte6 = read();
		int byte7 = read();
		int byte8 = read();
		
		if((byte1 == 9) &&
			(byte2 == 8)  &&
			(byte3 == 16) && 
			(byte4 == 0)  && 
			(byte5 == 0)  &&
			(byte6 == 6)  &&
			(byte7 == 5)  &&
			(byte8 == 0))
		{
			return XLS;
		}
		else if((byte1 == 253) &&
				(byte2 == 255) &&
				(byte3 == 255) &&
				(byte4 == 255) &&
				((byte6 == 0) ||
						(byte6 == 2)))
		{
			// another format of XLS: FD FF FF FF nn 00 or FD FF FF FF nn 02
			return XLS;
		}
		else if((byte1 == 253) &&
				(byte2 == 255) &&
				(byte3 == 255) &&
				(byte4 == 255) &&
				(byte5 == 32) &&
				(byte6 == 0) &&
				(byte7 == 0) &&
				(byte8 == 0))
		{
			// another format of XLS: FD FF FF FF 20 00 00 00
			return XLS;
		}
		// DOC is EC A5 C1 00
		else if((byte1 == 236) &&
			(byte2 == 165) &&
			(byte3 == 193) &&
			(byte4 == 0))
		{
			return DOC;
		}
		// PPT is FD FF FF FF nn 00 00 00
		// 253 255 255 255 ?? 0 0 0
		
		// this didn't seem to work well for PPT, so ignoring for now until needed...
		
		return DOC; // default, not great but should usually work
	}
	
	private String tryGIF()
	throws IOException
	{
		// GIF87a
		// GIF89a
		int byte2 = read();
		int byte3 = read();
		int byte4 = read();
		int byte5 = read();
		int byte6 = read();
		
		if((byte2 == 73) && 
			(byte3 == 70) &&
			(byte4 == 56) && 
			(compareWith(57, 55, byte5)) && 
			(byte6 == 97))						
		{
			return GIF;
		}
		
		return tryTGA(6, byte2, byte3);
	}
	
	private String tryPNG()
	throws IOException
	{
		int byte2 = read();
		int byte3 = read();
		int byte4 = read();
		
		if((byte2 == 80) && (byte3 == 78) && (byte4 == 71))
		{
			return PNG;
		}
		return tryTGA(4, byte2, byte3);
	}
	
	/**
	 * Determines if the file is an HTML file. Always return a file type or the default.
	 * @throws IOException
	 * @return A valid file type or the default value
	 */	
	private String tryHTML() throws IOException {
		int byte2, byte3, byte4, byte5 = 0;
		byte2 = read();
		byte3 = read();
		byte4 = read();
		byte5 = read();
		int bytesread = 5;
		switch(byte2) {
			case 33: // for !DOC
			case 65:
				int byte6, byte7, byte8, byte9, byte10, byte11, byte12, byte13, byte14 = 0;
				byte6 = read();
				byte7 = read();
				byte8 = read();
				byte9 = read();
				byte10 = read();
				byte11 = read();
				byte12 = read();
				byte13 = read();
				byte14 = read();
				bytesread = 14;
				if(compareWith(68, 100, byte3) && compareWith(79, 111, byte4) && compareWith(67, 99, byte5) && compareWith(84, 116, byte6) && compareWith(89, 121, byte7) && compareWith(80, 112, byte8) && compareWith(69, 101, byte9) && compareWith(32, 64, byte10) && compareWith(72, 104, byte11) && compareWith(84, 116, byte12) && compareWith(77, 109, byte13) && compareWith(76, 108, byte14)) {
					return HTML;
				}
				break;
			case 72:	// for HTML and HEAD
			case 104:
				if(compareWith(69, 101, byte3) && compareWith(65, 97, byte4) && compareWith(68, 100, byte5)) { // HEAD
					return HTML;
				}
				else { // HTML
					if(compareWith(84, 116, byte3) && compareWith(77, 109, byte4) && compareWith(76, 108, byte5)) {
						return HTML;
					}
				}
				break;
			case 84: // for TITLE
			case 116:
				byte6 = read();
				bytesread++;
				if(compareWith(73, 105, byte3) && compareWith(84, 116, byte4) && compareWith(76, 108, byte5) && compareWith(69, 101, byte6)) {
					return HTML;
				}
				break;
			case 63: //<?xml version="1.0"?>
				if(compareWith(88, 120, byte3) && compareWith(77, 109, byte4) && compareWith(76, 108, byte5))				
				{
					return XML;
				}
				break;
		}
		return tryTGA(bytesread, byte2, byte3);	
	}
	
	/**
	 * Determines if either of the first two parameters are the same as the third parameter.
	 * @param FirstPossible The first value that is allowed.
	 * @param SecondPossible The second possible value that is allowed.
	 * @param CompareWith The value to compare the first two values with
	 * @return True if one of the first two values is the same as the third, false otherwise.
	 */	
	private static boolean compareWith(int FirstPossible, int SecondPossible, int CompareWith) {
		if((FirstPossible == CompareWith) || (SecondPossible == CompareWith)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Determines if the file is a TGA image. If it is not a TGA image type it will deteremine if it is another type. Always return a file type or the default.
	 * @param CurrentLocation The current location in the file. (The number of bytes read in)
	 * @param byte2 The second byte of data read from the file, this function needs this first byte
	 * @param byte3 The third byte of data read from the file, this function needs this first byte
	 * @throws IOException
	 * @return A valid file type or the default value
	 */	
	private String tryTGA(int CurrentLocation, int byte2, int byte3) 
	throws IOException 
	{
		if(!isTGAValue(byte3)) 
		{
			return checkOtherTypes(CurrentLocation);
		}
		String retval = checkOtherTypes(CurrentLocation);
		if(!retval.equals(Default)) 
		{
			return retval;
		}
		if(compareWith(1, 9, byte3)) 
		{
			if(byte2 == 1) 
			{
				return TGA;
			}
			return retval;//  checkOtherTypes(In, CurrentLocation);
		}
		else {
			if(byte2 == 0) {
				return TGA;
			}
			return retval;// checkOtherTypes(In, CurrentLocation);
		}
	}
	
	/**
	 * Determines if the file is an MPG file. Always return a file type or the default.
	 * @throws IOException
	 * @return A valid file type or the default value
	 */	
	private String tryMPG() throws IOException {
		int byte2, byte3, byte4 = 0;
		byte2 = read();
		byte3 = read();
		byte4 = read();
		if((byte2 == 0) && (byte3 == 1) && (byte4 == 186)) {
			return MPG;
		}
		return tryTGA(4, byte2, byte3);
	}
	
	/**
	 * Determine the ImageFormat of the input stream, do not throw an exception if the stream is empty
	 * @return
	 */
	public ImageFormat getImageFormat()
	{
		try
		{
			return getImageFormat(false);
		}
		catch(ImageNotFoundException infX)
		{
			getLogger().debug("ImageNotFoundException but not throwing to user, " + infX.getMessage());
			return defaultImageFormat;
		}
	}
	
	/**
	 * Determine the ImageFormat of the input stream
	 * 
	 * @param throwExceptionIfEmptyFile This method will throw an ImageNotFoundException if a 0 byte length stream is detected
	 * @return
	 * @throws ImageNotFoundException Thrown if a 0 byte length stream is detected
	 */
	public ImageFormat getImageFormat(boolean throwExceptionIfEmptyFile)
	throws ImageNotFoundException
	{
		String imageFormat = getImageType(throwExceptionIfEmptyFile);
	
		if(TGA.equals(imageFormat)) {
			return ImageFormat.TGA;
		}
		else if(DCM.equals(imageFormat)) {
			return ImageFormat.DICOM;
		}
		else if(DCMJPG.equals(imageFormat)) {
			return ImageFormat.DICOMJPEG;
		}
		else if(DCMJ2K.equals(imageFormat)) {
			return ImageFormat.DICOMJPEG2000;
		}
		else if(TIF.equals(imageFormat)) {
			return ImageFormat.TIFF;
		}
		else if(JPG.equals(imageFormat)) {
			return ImageFormat.JPEG;
		}
		else if(BMP.equals(imageFormat)) {
			return ImageFormat.BMP; 
		}
		else if(PDF.equals(imageFormat))
		{
			return ImageFormat.PDF;
		}
		else if(DOC.equals(imageFormat))
		{
			return ImageFormat.DOC;
		}
		else if(AVI.equals(imageFormat))
		{
			return ImageFormat.AVI;
		}
		else if(RTF.equals(imageFormat))			
		{
			return ImageFormat.RTF;	
		}
		else if(TIF.equals(imageFormat))
		{
			return ImageFormat.TIFF;
		}
		else if(WAV.equals(imageFormat))
		{
			return ImageFormat.WAV;
		}
		else if(HTML.equals(imageFormat))
		{
			return ImageFormat.HTML;
		}
		else if(MP3.equals(imageFormat))
		{
			return ImageFormat.MP3;
		}
		else if(MPG.equals(imageFormat))
		{
			return ImageFormat.MPG;
		}			
		else if(DCMMP2.equals(imageFormat))
		{
			return ImageFormat.DICOM;
		}
		else if(DCMRLE.equals(imageFormat))
		{
			return ImageFormat.DICOM;
		}
		else if(J2K.equals(imageFormat))
		{
			return ImageFormat.J2K;
		}
		else if(DCMPDF.equals(imageFormat))
		{
			return ImageFormat.DICOMPDF;
		}
		else if(PNG.equals(imageFormat))
		{
			return ImageFormat.PNG;
		}
		else if(GIF.equals(imageFormat))
		{
			return ImageFormat.GIF;
		}
		else if(XLS.equals(imageFormat))
		{
			return ImageFormat.XLS;
		}
		else if(DOCX.equals(imageFormat))
		{
			return ImageFormat.DOCX;
		}
		else if(XML.equals(imageFormat))
		{
			return ImageFormat.XML;
		}
		// otherwise return null, even for DCMMP2 & DCMRLE
		return defaultImageFormat;
	}
    
	/**
	 * Determines what type of file is in the input stream. The input stream must be open and connected to a file.
	 * @return The type of file or the default type
	 */	
	private String getImageType(boolean throwExceptionIfEmptyFile) 
	throws ImageNotFoundException
	{
		if(in == null) return "no input";
		mark(MarkSize);	// mark the reset point and set it to a size larger than should be read.
		String ImgType = Default;	// set the current type to the default
		int input1 = 0;
		int input2 = 0;
		try 
		{    	
			input1 = read();	// read the first byte
			switch(input1)// switch based on the first byte 
			{	
				case 0:	// could be an MPG
					ImgType = tryMPG();
					break;
				case 37:	// could be a PDF
					ImgType = tryPDF();
					break;
				case 60:	// could be an HTML file
					ImgType = tryHTML();
					break;
				case 66:
					ImgType = tryBMPorTIF(input1);
					break;
				case 71: // could be a GIF image
					ImgType = tryGIF();
					break;
				case 73:	// could be a little-endian TIF
					ImgType = tryTIF();
					break;
				case 77:	// could be a BMP or big-endian TIF
					ImgType = tryBMPorTIF(input1);
					break;
				case 80: // DOCX, PPTX, XLSX
					ImgType = tryOffice2007Doc();
					break;
				case 89:	// could be a raster TGA
					ImgType = tryRasterTGA();
					break;
				case 123:	// could be an RTF
					ImgType = tryRTF();
					break;
				case 137: // could be a PNG image
					ImgType = tryPNG();
					break;
				case 208:	// could be a Word document
					ImgType = tryMSOffice();
					break;
				case 255:	// could be a JPG
					ImgType = tryJPG(input1);
					break;
				default:	// not one of the above
					if((input1 == -1) && (throwExceptionIfEmptyFile))
					{
						throw new ImageNotFoundException("Input stream has 0 bytes");
					}
					input2 = read();	// read in the next byte
					int input3 = read();	// read in the third byte
					ImgType = tryTGA(3, input2, input3);	// could be a TGA or others
					break;
			}
		}		
		catch(IOException e) {	// catch any file input errors
			e.printStackTrace();
			getLogger().error("Error: " + e.toString());
			return "error";	// return an error message
		}		
		finally
		{
			try {
				reset();	// reset the stream to the mark position
			}
			catch(IOException e) 
			{
				e.printStackTrace();	
				getLogger().error("Error [unable to reset file pointer location]: " + e.toString());
			}
		}
		getLogger().info("Image identified as: [" + ImgType + "]");
		return ImgType;	// return the image type
	}
    
	/**
	 * Determines if the type of TGA file based on the image type.
	 * @param Value The image type to determine the type of TGA image
	 * @return The type of TGA.
	 */	
	private static String TGAType(int Value) {
		String ret = "NONE";
		switch(Value) {
			case 0:
				ret = "No image data included."; 
				break;
			case 1:
    			ret = "Uncompressed, color-mapped image.";
				break;
			case 2:
				ret = "Uncompressed, RGB image";
				break;
			case 3:
				ret = "Uncompressed, black and white image.";
				break;
			case 9:
				ret = "Runlength encoded color-mapped image";
				break;
			case 10:
				ret = "Runlength encoded RGB image";
				break;
			case 11:
				ret = "Compressed, black and white image";
				break;
			case 32:
				ret = "Compressed color-mapped data, using Huffman, Delta, and runlength encoding";
				break;
			case 33:
				ret = "Compressed color-mapped data, using Huffman, Delta, and runlength encoding. 4-pass quadtree-type process";
				break;
			default:
				ret = "NONE";
				break;
		}
		return ret;
	}
    
	/**
	 * Determines if the value is valid for a TGA image.
	 * @param Value The value to use to determine if this is a TGA image
	 * @return True if it can be a TGA, false otherwise
	 */	
	private static boolean isTGAValue(int Value) {
		boolean ret = false;
		switch(Value) {
			// all valid TGA types
			case 1:
			case 2:
			case 3:
			case 9:
			case 10:
			case 11:
			case 32:
			case 33:
				ret = true;
				break;
			default:
				ret = false;
				break;	
		}
		return ret;
	}
    
    /**
     * Checks the file to determine if it is an AVI, WAV, DCM, DCMJPG, DCMJ2K or the default file.
     * This will always return an image type. 
     * This should be run last because it skips data in the file to determine the type.
     * @param CurrentLocation The number of bytes read from the file. This is needed to determine how many more bytes need to be read to get to the image information
     * @return The image type. This will always return an image type, either AVI, WAV, DCM, DCMJPG, DCMJ2K or the default type
     */
	private String checkOtherTypes(int CurrentLocation) {
		if(CurrentLocation > 128) return Default;
		if(CurrentLocation < 0) CurrentLocation = 0;
		// First check for AVI files since that only needs to go 8 bytes
		int loc = 0;
		loc = 8 - CurrentLocation;
		if(loc >= 0) 
		{
			try 
			{
				skip(loc);				
				int byte1, byte2, byte3 = 0;
				byte1 = read();
				byte2 = read();
				byte3 = read();
				
				if((byte1 == 65) && (byte2 == 86) && (byte3 == 73)) 
				{
					return AVI;
				}
				else if(byte1 == 87) 
				{
					int input4 = read();
					if((byte2 == 65) && (byte3 == 86) && (input4 == 69)) {
						return WAV;
					}
				}
				CurrentLocation = 11;
			}
			catch (IOException e) {
				e.printStackTrace();
				return "error";
			}
		}		
		// Next check for DICOM files
		loc = 128 - CurrentLocation; // DICM is located at the 128th byte in the file
		CurrentLocation = 128;
		try 
		{
			skip(loc);
			int byte1, byte2, byte3, byte4 = 0;
			byte1 = read();
			byte2 = read();
			byte3 = read();
			byte4 = read();
			CurrentLocation = 132;
			if((byte1== 68) && (byte2 == 73) && (byte3 == 67) && (byte4 == 77)) 
			{
				// check transfer syntax UID for JPEG or JPEG2000 compression
				String UID=""; // look for DICOM tag (0002,0010) TS UID or (0002,0002) MEDIA SOP CLASS UID 				
				for (int i=132; (i < MarkSize-8); ) {
					if (UID.length()==0) 
						byte1 = read();
					else
						UID="";
					byte2 = read();
					// fact: DICOM group 2 is always in Little Endian Explicit VR!
					if ((byte1 == 2) && (byte2 == 0)){
						byte3 = read();
						byte4 = read();
						if((byte3 == 0x10) && (byte4 == 0)) {
							// DICOM tag (0002,0010) -- TS UID -- found
							skip(4); //  skip VR and length
							i+=8;
							UID=""; 
							while ((i<MarkSize) && ((byte1 = read()) >= 0x2e)) {
								UID += Character.toString((char)byte1); // collect TS UID string
								i++;
							}
							if ((UID.length() > 21) && (UID.charAt(18) == '4')) {
								// 1.2.840.10008.1.2.x.y where x='4' and y=1(MPEG2), y=5,6,7,8(JPG) or y=9(J2K)
								if (UID.charAt(20) == '9') 
									 return DCMJ2K; // JPEG2000 -- 1.2.840.10008.1.2.4.9z
								else if (UID.charAt(20) != '1')
									 return DCMJPG; // JPEG -- 1.2.840.10008.1.2.4.yz, y=5,6,7,8
								else
									 return DCMMP2; // MPEG2 -- 1.2.840.10008.1.2.4.100
							} else {
								if (UID.equalsIgnoreCase("1.2.840.10008.1.5"))
									return DCMRLE; // RLE lossless -- 1.2.840.10008.1.5
								else
									return DCM;
										// Impl.VR LE (default) -- 1.2.840.10008.1.2
										// Exp.VR LE/BE -- 1.2.840.10008.1.2.1[.99] / 1.2.840.10008.1.2.2
										// RFC 2557 MIME -- 1.2.840.10008.1.6.1
									    // XML -- 1.2.840.10008.1.6.2
							}
						}
						else if((byte3 == 2) && (byte4 == 0)) {
							// DICOM tag (0002,0002) -- Media SOP Class UID -- found
							skip(4); //  skip VR and length
							i+=8;
							UID=""; 
							while ((i<MarkSize) && ((byte1 = read()) >= 0x2e)) {
								UID += Character.toString((char)byte1); // collect Media SOP Class UID string
								i++;
							}
							if (UID.contentEquals("1.2.840.10008.5.1.4.1.1.104.1")) {
								return DCMPDF; // DICOM encapsulated PDF -- 1.2.840.10008.5.1.4.1.1.104.1
							}
							else if (UID.contentEquals("1.2.840.10008.5.1.4.1.1.104.2")) {
								return DCMCDA; // DICOM encapsulated CDA -- 1.2.840.10008.5.1.4.1.1.104.2
							}
							if (byte1 != 2) { // CPT - 01/29/09
								UID="";  // clean uid --> do read byte1 again!
							}
						} else {
							i+=4;  // loop back						
						}
					} else {
						i+=2; // loop back
					}
				} // for loop
				return DCM;// "DCM";
			} // end of DICOM file MarkSize found
			
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			return "error";
		}
		return Default;
	}
	
	/**
	 * 
	 */
	public synchronized void mark(int readlimit)
	{
		super.mark(readlimit);
		try
		{
			markedChecksum = (ClonableAdler32)runningChecksum.clone();
			markedLength = runningLength;
		}
		catch (CloneNotSupportedException cnsX)
		{
			// this should never happen and is a major oops if it does
			cnsX.printStackTrace();
			throw new RuntimeException("Checksum generator is not clonable, code has been modified in appropriately.");
		}
	}
	
	/**
	 * 
	 */
	public synchronized void reset() throws IOException
	{
		super.reset();
		runningChecksum = markedChecksum;
		runningLength = markedLength;
	}
	
	/**
	 * We can't just skip 'cause we have to maintain the checksum
	 * calculation.  So we have to read the bytes.
	 */
	public synchronized long skip(long distance) 
	throws IOException
	{
		long n = 0;
		for(n=0; (n < distance) && (read() >= 0) ; ++n );
		
		runningLength += n;

		return n;
	}
	
	/**
	 * Encapsulates equivalent FilterInputStream method and adds functionality to calculate
	 * a CRC on the fly.
	 * 
	 * Reads the next byte of data from this input stream. 
	 * The value byte is returned as an int in the range 0 to 255. 
	 * If no byte is available because the end of the stream has been reached, the value -1 is returned. 
	 * This method blocks until input data is available, the end of the stream is detected, or an exception is thrown.
	 * @see java.io.FilterInputStream
	 */
	public synchronized int read() throws IOException
	{
		int value = super.read();
		++runningLength;
		
		runningChecksum.update(value);
		return value;
	}

	/**
	 * Encapsulates equivalent FilterInputStream method and adds functionality to calculate
	 * a CRC on the fly.
	 * 
	 */
	public synchronized int read(byte[] b, int off, int len) throws IOException
	{
		int bytesRead = super.read(b, off, len);
		runningChecksum.update(b, off, len);
		runningLength += bytesRead;
		
		return bytesRead;
	}
	
	/**
	 * Encapsulates equivalent FilterInputStream method and adds functionality to calculate
	 * a CRC on the fly.
	 * 
	 */
	public int read(byte[] b) throws IOException
	{
		int bytesRead = super.read(b);
		runningChecksum.update(b);
		runningLength += bytesRead;
		
		return bytesRead;
	}
	
}
