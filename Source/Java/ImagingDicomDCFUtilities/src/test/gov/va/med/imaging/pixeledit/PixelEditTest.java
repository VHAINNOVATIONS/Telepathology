package gov.va.med.imaging.pixeledit;

import gov.va.med.imaging.dicom.dcftoolkit.utilities.exceptions.TGAFileException;
import gov.va.med.imaging.dicom.dcftoolkit.utilities.exceptions.TGAFileNotFoundException;
// import gov.va.med.imaging.dicom.test.DicomDCFUtilitiesTestBase;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;


public class PixelEditTest extends PixelEditTestBase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(PixelEditTest.class);
    }
    protected void setUp() throws Exception {
        super.setUp();
    }
    private ByteBuffer imageBuffer=null;
    private static int headerBytes=18; // TGA header size, for DCM files must be computed per file
    private static boolean isDCMFile=false;

    private short rows;
    private short columns;
    private byte bitsPerPixel;
    private byte samplesPerPixel=1; // for DICOM only support other than 1 (like 3)
    private File outFileHandle = null;
    private FileOutputStream outStream = null;
    private int errCount = 0;
 
    public PixelEditTest(String arg0) {
        super(arg0);
    }
    /*
     * testTGAEdit sequentially reads TGA input files with preset path and names, 
     * paints a rectangle in the TGA pixeldata and writes out the files with the
     * same name to another folder path. The TGA header is only used to determine 
     * matrix size pixel bits used, the header is written out unchanged. 
     * Basic sanity checks are done for paint input parameters. 
     */
    public void testPixelEdit() {
		Integer fileCount=37; // 38, 47  last-first+1
		Integer firstFileNum=7516; // first;
		int counter=0;
		for (Integer i=firstFileNum; (i<(firstFileNum+fileCount)); i++) {
			String inFile="C:\\ImageCache\\test\\tempStuart\\G000" + i.toString() + ".DCM";
			String outFile="C:\\ImageCache\\test\\TOStuart\\G000" + i.toString() + ".DCM";
//			String inFile="C:\\ImageCache\\ELP\\VA\\CT\\ReconInTGA\\GB0000000" + i.toString() + ".TGA";
//			String outFile="C:\\ImageCache\\ELP\\VA\\CT\\ReconOutTGA\\GB0000000" + i.toString() + ".TGA";
			if (inFile.endsWith(".DCM")) {
				isDCMFile=true;
			}
//			paintRectangleInPixelData(inFile, outFile, 960, 62, 5, 330, 0); // Used for US (dcm) studies (color included)
//			paintRectangleInPixelData(inFile, outFile, 0, 18, 40, 200, 0); // Used for US (dcm) studies (color included)
//			paintRectangleInPixelData(inFile, outFile, 0, 36, 245, 280, 0); // Used for US (dcm) studies (color included)
//			paintRectangleInPixelData(inFile, outFile, 7, 15, 10, 610, 0); // Used for US (dcm) studies (color included)
			paintRectangleInPixelData(inFile, outFile, 32, 25, 62, 285, 0); // Used for US (dcm) studies (color included)
//			paintRectangleInPixelData(inFile, outFile, 5, 55, 360, 240, 0); // Used for US (dcm) studies (color included)
//			paintRectangleInPixelData(inFile, outFile, 5, 60, 70, 170, 0x3FF); // Used for EP CT (tga) recon intro frame
//			paintRectangleInTGAPixelData(inFile, outFile, 25, 37, 363, 130, 0x3FF); // used for EP CT (tga) recon images
			counter++;
		}
		if (errCount==0)
			System.out.println(" *** Finished processing " + counter + " " + fileType() + " files. ****");
		else
			System.out.println(" *** Finished processing " + counter + " " + fileType() + " files, " +
					errCount + " file(s) processing FAILED! ****");
	}
    
    /*
     * takes in and out TGA file specs, 0 based row and column pixel coordinates (Pos), pixel ranges (Size)
     * and a pixel mask value it assumes the caller knows the pixel size and bits used (in Monochrome2 
     * 0=black max value=white). Sanity chack prevents illegal acts.
     */
    private void paintRectangleInPixelData( 
            String tgaInFile, String tgaOutFile, int rowPos, int rowSize, int colPos,  int colSize, int pxValue)
            {

    	try {
    		OpenInFile(tgaInFile); // parses TGA header, and reads file to imageBuffer
    		
    		// check TGA input params for pixel frame and pixel size
    		if ((rowPos < 0) || (rowSize < 1) || ((rowPos+rowSize) >= rows) || 
    			(colPos < 0) || (colSize < 1) || ((colPos+colSize) >= columns) || 
    			(pxValue < 0) || (pxValue > ((1 << this.bitsPerPixel)-1)) ){
	        	System.out.println(" Invalid input parameter for " + tgaInFile + 
	        			";\n  rowPos=" + rowPos + " colPos=" + colPos + 
	        			" rowSize=" + rowSize + " colSize=" + colSize + 
	        			"\n  -- TGA rows=" + rows + " Columns=" + columns + 
	        			" maxPixelValue=" + ((1 << bitsPerPixel)-1));
	        	return;
    		}	
    		OpenOutFile(tgaOutFile); // prepares out file for writing
    		    		
    		// Do the paint job 
    		int bytesPerPixel;
    		if ((bitsPerPixel > 8) && (bitsPerPixel < 16))
    			bytesPerPixel = 2;
    		else // 8, 16, ++
    			bytesPerPixel = bitsPerPixel / 8;
    		bytesPerPixel*=samplesPerPixel;
    		byte[] pattern=new byte[bytesPerPixel];
    		if (bytesPerPixel < 2) // byte mode
    			pattern[0]=(byte)pxValue;
    		else if (bytesPerPixel < 3) { // word mode; we do not handle colors...
    			pattern[0]=(byte)(pxValue & 0xFF);
    			pattern[1]=(byte)((pxValue >> 8) & 0xFF);    			
    		} else { // color RGB
    			pattern[0]=(byte)(pxValue & 0xFF);
    			pattern[1]=(byte)((pxValue >> 8) & 0xFF);    			
    			pattern[2]=(byte)((pxValue >> 16) & 0xFF);    			    			
    		}
    		for (int r=rowPos; (r<(rowPos+rowSize)); r++) {
                for (int c=colPos; (c<(colPos+colSize)); c++) {
	       			imageBuffer.rewind();
	    			imageBuffer.position( (headerBytes + ((r*columns) + c) * bytesPerPixel));
	    			imageBuffer.put(pattern);
                }
            }
    		
            WriteOutFile(tgaOutFile); // write out the imageBuffer and close file
    	}
    	catch (TGAFileNotFoundException nofile) {
    		errCount++;
        }
        catch (TGAFileException tfe) {
    		errCount++;
        }
    }
   
    private short findDicomUSTagValue(int g, int e) {
    	// returns position of a DICOM US value!
    	int value;
    	imageBuffer.rewind();
    	short short1=0, short2;
		for (int i=0; (i < imageBuffer.capacity()-6);) {
			short2=imageBuffer.getShort();
			i+=2;
			if ((short1==g) && (short2==e)) {
				value=imageBuffer.getInt(); // skip 4 bytes 
				value=imageBuffer.getShort();
				i+=6;
		    	imageBuffer.rewind();
		    	short1=0;
		    	short1|=(0xFF & value) << 8;
				short1+=((value >> 8) & 0xFF) ;
				return short1;
			}
			short1=short2;
		}
    	imageBuffer.rewind();
    	return -1;   	
    }
    
    private String fileType() {
    	return (isDCMFile?"DCM":"TGA");
    }

    	
    private void OpenInFile(String InFile)
    throws TGAFileNotFoundException, TGAFileException {
 
    	try {
    		//Create a binary stream connected to the input file.  
    		System.out.println(">>In file: " + InFile);
	        FileChannel fc = new FileInputStream(InFile).getChannel();
	        MappedByteBuffer buffer = fc.map(MapMode.READ_ONLY, 0, (int)fc.size());
            
    		if (isDCMFile) { // DCM case
    	        //  do a bulk push (transfer) to the new ByteBuffer object.  The new ByteBuffer
    	        //  will be assign to the header followed by the Pixel Data.
    	        imageBuffer = ByteBuffer.allocate(buffer.capacity());
    	        imageBuffer.put(buffer);
				// manually enter columns rows and bitsPerpixel
    			int i;
    			if ((rows=findDicomUSTagValue(0x2800, 0x1000)) < 0)
		        	throw new TGAFileException(" ERROR -> Cannot find DCM tag 0028,0010 (rows)! ");
    			if ((columns=findDicomUSTagValue(0x2800, 0x1100)) < 0)
		        	throw new TGAFileException(" ERROR -> Cannot find DCM tag 0028,0011 (column)! ");
    			if ((bitsPerPixel=(byte)findDicomUSTagValue(0x2800, 0x0001)) < 0)
		        	throw new TGAFileException(" ERROR -> Cannot find DCM tag 2800,0100 (bits/pixel)! ");
    			if ((samplesPerPixel=(byte)findDicomUSTagValue(0x2800, 0x0200)) < 0)
		        	throw new TGAFileException(" ERROR -> Cannot find DCM tag 2800,0002 (samples par pixel)! ");
				// and figure first pixel offset
    			headerBytes=imageBuffer.capacity() - (rows*columns*samplesPerPixel*(bitsPerPixel/8));
    			if (headerBytes < 256)
		        	throw new TGAFileException(" ERROR -> Cannot find DCM image offset ! ");
       			imageBuffer.rewind();
    		} else {
    	        byte header[] = new byte[headerBytes];	        
    	        buffer = (MappedByteBuffer)buffer.get(header, 0, headerBytes);
                buffer.rewind();
		        // check TGA header (reject colormap and other than top left first pixel)
		        if(header[2] != 3) {
		        	throw new TGAFileException(" ERROR -> Only TGA and DCM file types are supported! ");
		        }
		        if(header[1] != 0) {
		            throw new TGAFileException(" ERROR -> No color map supported!");
		        }
		        //Read Bytes 12-13 for # of columns.
		        this.columns = 0;
		        this.columns |= (0xFF & header[13]);
		        this.columns <<=8;
		        this.columns |= (0xFF & header[12]);  
		        // System.out.println("  TGA Number of Columns: " + this.tgaColumns);
		        // Read Bytes 14-15 for # of rows.
		        this.rows = 0;
		        this.rows |= (0xFF & header[15]);
		        this.rows <<=8;
		        this.rows |= (0xFF & header[14]);
		        // System.out.println("  TGA Number of Rows: " + this.tgaRows);
		        // Read Byte 16 for bits/pixel.
		        this.bitsPerPixel = header[16];
		        // System.out.println("  TGA Bits Per Pixel: " + this.tgaBitsPerPixel);
		        //  do a bulk push (transfer) to the new ByteBuffer object.  The new ByteBuffer
		        //  will be assign to the header followed by the Pixel Data.
		        imageBuffer = ByteBuffer.allocate(buffer.capacity());
		        imageBuffer.put(buffer);
    		}         
        }
        catch (FileNotFoundException nofile) {
           	System.out.println("  Error: " + nofile.getMessage());
           	System.out.println("  Exception while attempting to open input file, " + InFile + ".");
            throw new TGAFileNotFoundException("Could not open file " + InFile + ".", nofile);
        }
        catch (IOException ioe) {
           	System.out.println("  Error: " + ioe.getMessage());
           	System.out.println("  Exception thrown while attempting to open file, " + InFile+".");
            throw new TGAFileException("  Could not find or open " + InFile + ".", ioe);
        }
    }
    	
    private void OpenOutFile(String tgaOutFile)
    throws TGAFileNotFoundException, TGAFileException {
 
        try {
            //Create a binary stream connected to the input file.  
	        System.out.println("<<Out file: " + tgaOutFile);
	        outFileHandle = new File(tgaOutFile);
			outStream = new FileOutputStream(outFileHandle);

			if (outFileHandle.exists()) {
				outFileHandle.delete();
			}
			outFileHandle.createNewFile();
        }
        catch (FileNotFoundException nofile) {
           	System.out.println("  Error: " + nofile.getMessage());
           	System.out.println("  Exception while attempting to open" + fileType() + "output file, " + tgaOutFile + ".");
            throw new TGAFileNotFoundException("Could not open" + fileType() + "file " + tgaOutFile + ".", nofile);
        }
        catch (IOException ioe) {
          	System.out.println("  Error: " + ioe.getMessage());
           	System.out.println("  Exception thrown while attempting to open" + fileType() + "file, " + tgaOutFile+".");
            throw new TGAFileException("  Could not find or open " + tgaOutFile + ".", ioe);
        }
    }
    	
    private void WriteOutFile(String tgaOutFile)
    throws TGAFileException {
    		
    	try {
   			imageBuffer.rewind();
    		outStream.write(imageBuffer.array());
			FileDescriptor fd = outStream.getFD();
			fd.sync();
			outStream.close();
			outStream = null;
            imageBuffer.clear();
            imageBuffer.rewind();
	        System.out.println(" ");
    	} 
    	catch (IOException ioe) {
    		throw new TGAFileException("  ERROR: Could not write " + tgaOutFile + ".", ioe);
    	}
    }
}
