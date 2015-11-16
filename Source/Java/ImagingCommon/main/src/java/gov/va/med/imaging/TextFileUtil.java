/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: March 4, 2005
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
package gov.va.med.imaging;

import gov.va.med.imaging.exceptions.ReadFileException;
import gov.va.med.imaging.exceptions.TextFileException;
import gov.va.med.imaging.exceptions.TextFileExtractionException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 *
 * @author William Peterson
 *
 */
public class TextFileUtil {

    private static final Logger logger = Logger.getLogger (TextFileUtil.class);
    
    private BufferedReader buffer;
    private final int MAX_SIZE = 5000;

    /**
     * Constructor
     *
     * 
     */
    public TextFileUtil() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void openTextFile(String textFilename)throws TextFileException{
        
        try{
            //Get Text file.
            //JUNIT Create test to verify how this fails if not correct permissions.
            this.buffer = new BufferedReader(new FileReader(textFilename));
        }
        catch(FileNotFoundException noFile){
            logger.error("Error: "+noFile.getMessage());
            logger.error(this.getClass().getName()+": Dicom Toolkit layer: " +
                    "Exception thrown while attempting to open "+textFilename+".");
            throw new TextFileException("Could not find or open "+textFilename+".", noFile);
        }
    }
    
    public void openTextFile(File file) throws TextFileException{
        try{
            //Get Text file.
            //JUNIT Create test to verify how this fails if not correct permissions.
            
            this.buffer = new BufferedReader(new FileReader(file));
        }
        catch(FileNotFoundException noFile){
            logger.error("Error: "+noFile.getMessage());
            logger.error(this.getClass().getName()+": Dicom Toolkit layer: " +
                    "Exception thrown while attempting to open "+file.getPath()+".");
            throw new TextFileException("Could not find or open "+file.getPath()+".", noFile);
        }

    }

    public String getNextTextLine() throws TextFileExtractionException{
        String line;
        try{
            boolean eof = false;
            do{
                if((line = this.buffer.readLine()) == null){
                    eof = true;
                    break;
                    //return null;
                    //notEOF = false;
                    //line = "";
                }
                else{
                    if(line.length() > 1){
                        line.trim();
                        char firstChar;
                        firstChar = line.charAt(0);
                        if(firstChar == '#'){
                            line = "";
                        }
                    }
                    else{
                        line = "";
                    }
                }
            }while(line.equals(""));
            
            if (eof){
                buffer.close();
                return null;
            }
        }
        catch(IOException io){
            logger.error("Error: "+io.getMessage());
            logger.error(this.getClass().getName()+": " +
                    "Exception thrown while getting next line from Text Line.");
            throw new TextFileExtractionException("Failure to get next line.", io);
        }
        catch(NullPointerException noptr){
            logger.error("Error: "+noptr.getMessage());
            logger.error(this.getClass().getName()+": "+
                    "Exception thrown while getting next line from Text Line.");
            throw new TextFileExtractionException("Failure to get next line.", noptr);
        }
        catch(StringIndexOutOfBoundsException noIndex){
            logger.error("Error: "+noIndex.getMessage());
            logger.error(this.getClass().getName()+": "+
                    "Exception thrown while getting next line from Text Line.");
            throw new TextFileExtractionException("Failure to get next line.", noIndex);
        }
        return line;
    }

    
    public char[] getAllText()throws ReadFileException{
        char[] text = new char[MAX_SIZE];
        try{
            this.buffer.read(text);
        }
        catch(IOException ioX){
            throw new ReadFileException();
        }
        return text;
    }    
}
