/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: September 26, 2006
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
import gov.va.med.imaging.dicom.dcftoolkit.common.impl.DicomDataSetImpl;
import gov.va.med.imaging.exceptions.TextFileException;
import gov.va.med.imaging.exceptions.TextFileExtractionException;
import gov.va.med.imaging.exceptions.TextFileNotFoundException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.lbs.DCS.AttributeTag;
import com.lbs.DCS.DCM;
import com.lbs.DCS.DCSException;
import com.lbs.DCS.DicomCSElement;
import com.lbs.DCS.DicomDataDictionary;
import com.lbs.DCS.DicomDataSet;
import com.lbs.DCS.DicomElement;
import com.lbs.DCS.DicomElementFactory;
import com.lbs.DCS.DicomFDElement;
import com.lbs.DCS.DicomFLElement;
import com.lbs.DCS.DicomOBElement;
import com.lbs.DCS.DicomOWElement;
import com.lbs.DCS.DicomSLElement;
import com.lbs.DCS.DicomSQElement;
import com.lbs.DCS.DicomSSElement;
import com.lbs.DCS.DicomULElement;
import com.lbs.DCS.DicomUSElement;

/**
 *
 * Text File Parser class. This parser is specific to the Vista Imaging Legacy
 * environment.  The class will parse the Text file passed from Archiving 
 * and build a new DicomDataSet from the Text file information.  Then the class shall
 * wrap the DicomDataSet object into a generic DicomDataSet object.  The wrapping allows
 * the DicomDataSet object to be passed to the Dicom Generic Layer.
 *
 * @author William Peterson
 * extended by Csaba Titton
 * 			for ViX streaming 
 */

//NOTE 0008,1032/0008,0103 and
//		0040,0260/0008,0103 and
//		0040,0275/0040,0008/0008,0103 has no value in output14.dcm. Investigated the issue.
//	This is acceptable according to the DICOM Standard.

public class LegacyTextFileParser {
    /*
     * Create a DicomDataSet object.  This object is DCF Toolkit specific.
     */
    private DicomDataSet dicomDataSet = null;
    
    private IDicomDataSet toolkitDDS = null;
    
    private OriginalPixelDataInfo originalPixelData = null;
    
    private static Logger logger = Logger.getLogger (LegacyTextFileParser.class);
    private static Logger testLogger = Logger.getLogger("JUNIT_TEST");

        
    /**
     * Constructor
     */
    public LegacyTextFileParser() {
        super();
    }
    
    /**
     * Invoke method to create a DicomDataSet based on a Text file.
     * 
     * @param textFilename represents the name (and path) of the Text file.
     * @return represents the encapsulated DicomDataSet that is safe for DicomGeneric Layer.
     * @throws TextFileNotFoundException
     * @throws TextFileException
     * @throws TextFileExtractionException
     */
    public IDicomDataSet createDicomDataSet(String textFilename, OriginalPixelDataInfo pixelData)
            throws TextFileNotFoundException, TextFileException, 
            TextFileExtractionException{
        
        logger.info(this.getClass().getName()+": Dicom Toolkit Layer: " +
                "...parsing Text file into DicomDataSet.");
        logger.debug("Text File: "+ textFilename);
        this.originalPixelData = pixelData;
        BufferedReader buffer = null;
        try{
            //Get Text file.
            //JUNIT Create test to verify how this fails if not correct permissions.
            buffer = new BufferedReader(new FileReader(textFilename));
            //Invoke parser.
            this.parseTextFile(buffer, true);
            //REMINDER Find out why I have the following line.  It does not make sense, but
            //	I don't want to change it now.  Unsure of effects if omitted.
            pixelData = this.originalPixelData;
            //encapsulate DicomDataSet object.
            return (this.encapsulateDicomDataSet());
        }
        catch(FileNotFoundException noFile){
            logger.error(noFile.getMessage());
            logger.error(this.getClass().getName()+": Dicom Toolkit layer: " +
                    "Exception thrown while attempting to open "+textFilename+".");
            throw new TextFileNotFoundException("Could not find or open "+textFilename+".", noFile);
        }
        finally{
    		if(buffer != null){
    			try{
        			buffer.close();
        		}
    			catch(Throwable T){
        			logger.error(this.getClass().getName()+": Dicom Toolkit layer: "+
        					"Exception thrown while closing Text File "+textFilename+".");
    			}
    			System.gc();
    		}
        }     
    		
    }
    /**
     * Invoke method to create a DicomDataSet based on a Text data stream.
     * 
     * @param textStream represents the stream of VistA Imaging TXT data.
     * @return represents the encapsulated DicomDataSet that is safe for DicomGeneric Layer.
     * @throws TextFileNotFoundException
     * @throws TextFileException
     * @throws TextFileExtractionException
     */
    public IDicomDataSet createDicomDataSet(BufferedReader buffer, OriginalPixelDataInfo pixelData)
            throws  TextFileException, TextFileExtractionException {
        
        logger.info("... Dicom Toolkit Layer: parsing Text data into DicomDataSet ...");
        logger.debug("... Start Text Data Stream parsing... ");
        this.originalPixelData = pixelData;

        //Invoke parser.
        this.parseTextFile(buffer, false);
        
        pixelData = this.originalPixelData;
        //encapsulate DicomDataSet object.
        return (this.encapsulateDicomDataSet());
    }
    
    /**
     * Encapsulates the DCF Toolkit specific DicomDataSet object.
     * 
     * @return represents the Generic DicomDataSet object.
     */
    private IDicomDataSet encapsulateDicomDataSet(){
        
        testLogger.info("... encapsulating DDS ...");
        try{
            //toolkitDDS = (IDicomDataSet)SpringContext.getContext().getBean("DicomDataSet");
        	//toolkitDDS.setDicomDataSet(dicomDataSet);
            toolkitDDS = new DicomDataSetImpl(dicomDataSet);
        }
        catch(Exception e){
            logger.error("Error: " + e.getMessage());
            logger.error(this.getClass().getName()+": Dicom Toolkit layer: " +
                    "Exception thrown while encapsulating Dicom Dataset.");
            e.printStackTrace();
        }
        return toolkitDDS;
    }
    
    /**
     * Invoke method to extract HIS updates from an open Text data stream.
     * 
     * @param buffer represents the stream of VistA Imaging TXT data.
     * @return HashMap of DICOm tag-value pairs to be updated in DICOM DataSet.
     * @throws TextFileExtractionException
     */
    public HashMap<String, String> getHisUpdates(BufferedReader buffer)
            throws  TextFileException, TextFileExtractionException {
        
        logger.info("... Parsing text data HIS update section ...");
        logger.debug("... Continue Text Data parsing for VistA updates... ");

        HashMap<String, String> hisChanges=null;
        hisChanges = this.parseHisUpdates(buffer);
        try {
        	buffer.close();
        }
        catch(IOException io){
            logger.error("Cannot close Text Stream Buffer.");
            throw new TextFileExtractionException();
        }
        return (hisChanges);
    }
    
    /**
     * Invoke method to extract HIS updates from an open Text data stream.
     * 
     * @param buffer represents the stream of VistA Imaging TXT data.
     * @return HashMap of DICOm tag-value pairs to be updated in DICOM DataSet.
     * @throws TextFileExtractionException
     */
    public HashMap<String, String> extractHisUpdatesfromTextStream(SizedInputStream sizedTextStream)
            throws  TextFileException, TextFileExtractionException {
        
        logger.info("... Parsing text data HIS update section ...");
        logger.debug("... Start parsing VistA updates... ");

        HashMap<String, String> hisChanges=null;
        BufferedReader buffer = new BufferedReader(new InputStreamReader(sizedTextStream.getInStream()));

        hisChanges = this.parseHisUpdates(buffer);
        try {
        	buffer.close();
        }
        catch(IOException io){
            logger.error("Cannot close Text Stream Buffer.");
            throw new TextFileExtractionException();
        }
        return (hisChanges);
    }
    
    /**
     * Parse the Text file.  The Text file is made up of two sections, "Data1" and 
     * "DICOM Data".  Both sections are read and decoded.
     * 
     * @param buffer represents the Text file now in the form of a BufferReader object.
     * @throws TextFileException
     * @throws TextFileExtractionException
     */
    private void parseTextFile(BufferedReader buffer, boolean doClose) throws TextFileException,
            TextFileExtractionException{
        
        String textLine = "";
        testLogger.info("... Parsing text data top section ...");
        try{
            //Loop thru the lines until $$BEGIN DATA1.
            //Ignore each line until $$BEGIN DATA1
            do{
                textLine = this.getNextTextLine(buffer);
            } while(!(textLine.equals("$$BEGIN DATA1")));
            
            do{
                textLine = this.getNextTextLine(buffer);
                //Extract only the PATIENTS_XXX fields
                //Replace commas with carats in Patient's Name field
                //100507-WFP-Removing all IF statements except DCM_TO_TGA_PARAMETERS.
                //  Reason is the application does not use this information.  It serves
                //  no purpose.  But I'm leaving the code in case I'm wrong.
                /*
                if(textLine.startsWith("PATIENTS_NAME")){
                    String patientsName = textLine.substring(textLine.indexOf("=")+1);
                }
                if(textLine.startsWith("PATIENTS_ID")){
                    String patientsID = textLine.substring(textLine.indexOf("=")+1);
                }
                if(textLine.startsWith("PATIENTS_BIRTH_DATE")){
                    String patientsBirth = textLine.substring(textLine.indexOf("=")+1);
                }
                if(textLine.startsWith("PATIENTS_AGE")){
                    String patientsAge = textLine.substring(textLine.indexOf("=")+1);
                }
                if(textLine.startsWith("PATIENTS_SEX")){
                    String patientsSex = textLine.substring(textLine.indexOf("=")+1);
                }
                if(textLine.startsWith("ACCESSION_NUMBER")){
                    String accessionNumber = textLine.substring(textLine.indexOf("=")+1);
                }
                */
                if(textLine.startsWith("DCM_TO_TGA_PARAMETERS")){
                    String dcmtotgaParameters = textLine.substring(textLine.indexOf("=")+1);
                    this.originalPixelData.setDcmtotgaParameters(dcmtotgaParameters);
                }
                //Place these PATIENTS_XXX fields into a temp DicomDataSet object.
            }while(!(textLine.equals("$$END DATA1")));
            
            //Invoke parseDicomDataSection to parse rest of file.
            this.parseDicomDataSection(buffer);
            if (doClose) 
            	buffer.close();
        }
        catch(IOException io){
            logger.error("Cannot extract from Text File.");
            logger.error("Working on Line: " + textLine);
            throw new TextFileExtractionException();
        }
    }
    
    /**
     * Parse the "DICOM Data" section of the Text file.  This is the grunt of the work
     * that needs to be done.
     * 
     * @param buffer represents the Text file now in the form of a BufferedReader object.
     * @throws TextFileExtractionException
     */
    private void parseDicomDataSection(BufferedReader buffer)
            throws TextFileExtractionException{
        
        this.dicomDataSet = new DicomDataSet();
        String textDicomLine = "";
        boolean sequenceFlag = false;
        DicomSQElement sequenceElement = null;
        testLogger.info("... Parsing text data Dicom DataSet section ...");
        try{
            //Mark Buffer.
            buffer.mark(255);
            //Declare lineArray object.
            ArrayList<String> lineArray = new ArrayList<String>();
            //Loop thru the lines until $BEGIN DICOM DATA.
            //Ignore each line until $$BEGIN DICOM DATA. 
            do{
                textDicomLine = this.getNextTextLine(buffer);
            } while(!(textDicomLine.equals("$$BEGIN DICOM DATA")));
            
            textDicomLine = this.getNextTextLine(buffer);
//          testLogger.debug("Current Line: " + textDicomLine);
            //Loop thru each line until $$END DICOM DATA.
            while(!(textDicomLine.equals("$$END DICOM DATA"))){
                //Check for Odd Group.
                String checkGroup = textDicomLine.substring(0,4);
                //String checkElement = textDicomLine.substring(5,9);
                int i = Integer.parseInt(checkGroup, 16);
                if(this.isGroupToBeAdded(i)){
                //If no Odd group or Group 88,
                    //Check if 9th character in line is a "|".
                    if(!(textDicomLine.substring(9,10).equals("|"))){
                    //If no,
                        //Add string to lineArray object.
                        lineArray.add(textDicomLine);
                        //Set Sequence flag.
                        sequenceFlag = true;
                    }
                    else{
                    //If yes, 
                        //Check if Sequence flag is set.
                        if(sequenceFlag){
                        //If yes,
                            //Reset Mark Buffer.  This allows to pick up the element after the
                            //  sequence again.
                            buffer.reset();
                            //Invoke extractSequenceData method and pass lineArray.
                            sequenceElement = this.extractSequenceData(lineArray);
                            //Add DicomSQElement object to dds.
                            this.dicomDataSet.insert(sequenceElement);
                            //Clean lineArray object.
                            lineArray.clear();
                            //Unset the Sequence flag.
                            sequenceFlag = false;
                        }
                        else{
                        //If no,
                            //Invoke extractDicomElement method and pass the line.
                            this.extractDicomElement(textDicomLine, this.dicomDataSet);
                        }
                    //End If for "|" delimiter.
                    }
                //End If for Odd Group.
                }
                //Mark Buffer.
                buffer.mark(255);
                textDicomLine = this.getNextTextLine(buffer);
//                testLogger.debug("Current Line: " + textDicomLine);

            //End Loop due to $$END DICOM DATA or EOF.
            }
        }
        catch(IOException io){
            logger.error(io.getMessage());
            logger.error(this.getClass().getName()+": " +
                    "Exception thrown while reading Text file's Dicom Data Section.");
            throw new TextFileExtractionException("Failure to read DicomData Section.", io);
        }
        catch(NumberFormatException number){
            logger.error(number.getMessage());
            logger.error(this.getClass().getName()+": Working on Dicom Line: " + textDicomLine);
            throw new TextFileExtractionException("Failure on Number Format.", number);
        }
        
        this.customTGAElementCleanup();
    }
    
    /**
     * Recursive method to handle the Sequences and nested Sequences inside of the "DICOM
     * Data" section.
     * 
     * @param lines represents the Array of Sequence lines in the Text file.
     * @return represents the Sequence lines converted into a single Dicom Sequence Element.
     * @throws TextFileExtractionException
     */
    private DicomSQElement extractSequenceData(ArrayList<String> lines)
            throws TextFileExtractionException{
        
        DicomDataSet seqDDS = new DicomDataSet();
        ArrayList<DicomDataSet> ddsArrayList = new ArrayList<DicomDataSet>();
        //Create DicomSQElement object.
        DicomSQElement sequence = null;
        
        String element = new String("");
        //Initialize seqDDS index to 0.
        int ddsIndex = 0;
        //Create previouseSeqItem.
        String previousSeqItem = "";
        //Set Sequence flag to false.
        boolean sequenceFlag = false;
        //Declare seqArray object.
        ArrayList<String> seqArray = new ArrayList<String>();
        try{
        //Loop thru each line of lines array until null.  Grab line in Loop.
        for(int x=0; x<lines.size(); x++){
            
            //Split line into two substrings using first period.
            String seqLine = (String)lines.get(x);
            String subLines[] = seqLine.split("\\.",2);
            //Assign first substring to Tag.
            element = subLines[0];
            //Split second substring into two sub-strings using first carat.
            String subSubLines[] = subLines[1].split("\\^",2);
            //Assign first sub-substring to seqItem.
            String seqItem = subSubLines[0];
            //Assign second sub-substring to seqArray object.
            String elementData = subSubLines[1];
            if(previousSeqItem.equals("")){
            //If yes, 
                //Initialize previousSeqItem with seqItem.
                previousSeqItem = seqItem;
            }
            //Check if first sub-substring matches seqItem.
            if(!(previousSeqItem.equals(seqItem))){
            //If no,
                ddsArrayList.add(seqDDS);
                seqDDS = new DicomDataSet();
                previousSeqItem = seqItem;
            //End If
            }
            String checkGroup = elementData.substring(0,4);
            int g = Integer.parseInt(checkGroup, 16);
            if(this.isGroupToBeAdded(g)){
                //Check if 9th character is a "|".  This means another Sequence.
                //This if/else determines if the line is another sequence.
                if(!(elementData.substring(9,10).equals("|"))){
                    //If no,
                    //Add string to seqArray object.
                    seqArray.add(elementData);
                    //Set Sequence flag.
                    sequenceFlag = true;
                    //If yes,
                }
                else{
                    //Check if Sequence flag is set.
                    if(sequenceFlag){
                        //If yes,
                        //Decrement lines array index.
                        x--;
                        //Re-invoke extractSequenceData method and pass lineArray.
                        DicomSQElement subSequence = this.extractSequenceData(seqArray);
                        //add DicomSQElement object to dds.
                        seqDDS.insert(subSequence);
                        //Unset the Sequence flag.

                        sequenceFlag = false;
                    }
                    else{
                        //If no,
                        //Invoke extractDicomElement method and pass the second substring and
                        //  and temp DicomDataSet object.
                        this.extractDicomElement(elementData, seqDDS);
                        //End If for Sequence flag.
                    }
                    //End If for "|" delimiter.
                }
                //End If for Group Check
            }
            //Increment lines array index.
        //Loop thru lines array is complete.
        }
        //Check if Sequence flag is set.
        if(sequenceFlag){
        //If yes,
            //Re-invoke extractSequenceData method and pass lineArray.
            DicomSQElement subSequence = this.extractSequenceData(seqArray);
            //add DicomSQElement object to dds.
            seqDDS.insert(subSequence);
            //Unset the Sequence flag.
            sequenceFlag = false;
        }
        ddsArrayList.add(seqDDS);
        ddsIndex = ddsArrayList.size();
        DicomDataSet ddsSeqItems[] = new DicomDataSet[ddsIndex];
        for(int y=0; y<ddsIndex; y++){
           ddsSeqItems[y] = ddsArrayList.get(y); 
        }
        AttributeTag tag = new AttributeTag(element);
        sequence = new DicomSQElement(tag, ddsSeqItems);
        }
        catch(DCSException dcs){
            logger.error(dcs.getMessage());
            logger.error(this.getClass().getName()+": " +
                    "Exception thrown while extracting Sequence Data.");
            throw new TextFileExtractionException("Failure to extract Sequence.", dcs);
        }
        return sequence;
    }
    
    /**
     * Extracts a single parsed line, independent of any sequences, and converts it to a DICOM 
     * Element and stores the DICOM Elment into the desired DCF Toolkit specific 
     * DicomDataSet.
     * 
     * @param line represents the parsed line from the Text file. 
     * @param dds represents the DCF Toolkit specific DicomDataSet object.
     * @throws TextFileExtractionException
     */
    private void extractDicomElement(String line, DicomDataSet dds)
            throws TextFileExtractionException{
        //Setup the ^ parser.
        String splitCaratPattern = "\\^";
        Pattern pInfo = Pattern.compile(splitCaratPattern);
        //Setup the | parser.
        String splitPipePattern = "\\|";
        Pattern pFields = Pattern.compile(splitPipePattern);
        //Setup the , parser.
        String splitCommaPattern = ",";
        Pattern pTag = Pattern.compile(splitCommaPattern);

        String fields[] = new String[4];
        fields = pFields.split(line);
        //Parse the basic data.
        //Set the Tag variable.
        String tag = fields[0];
        
        String elementInfo = fields[1];
        short elementVR = ' ';

        try{
            AttributeTag aTag = new AttributeTag(tag);
            
            //Get the VR.
            if(elementInfo.charAt(elementInfo.length()-3) == '^'){
                String subElementInfo[] = new String[2];
                subElementInfo = pInfo.split(line);
                elementVR = DicomDataDictionary.getVR(subElementInfo[1]);
                originalPixelData.setValueRepresentationInTextFile(true);
            }
            else{
                elementVR = DicomDataDictionary.getElementVR(aTag);
                originalPixelData.setValueRepresentationInTextFile(false);
            }

            String multiplicity;
            //This IF makes sure the VM,ML values exist.
            if(fields.length > 2){
                multiplicity = fields[2];
                //This IF makes sure there is a comma delimiter.
                if(multiplicity.indexOf(",") >= 0){
                    String value;
                    if(fields.length < 4){
                        value = "";
                    }
                    else{
                        value = fields[3];
                        if(value.equals("<unknown>")){
                            value = "";
                        }
                    }
                    String multiple[] = multiplicity.split(",");
                    //Assign the VM to a temp field. |VM,ML|
                    int vm = Integer.parseInt(multiple[0]);
                    //Assign the ML to a temp field. |VM,ML|
                    int ml = Integer.parseInt(multiple[1]);
                    //100407-WFP-Discovered DCF does not handle VR=OF.  Adding IF 
                    //  statement to ignore any text lines with this VR value.
                    if(elementVR == DicomDataDictionary.getVR("OF")){
                        return;
                    }
                    //If VM is greater than 1.
                    if(vm > 1){

                        if(elementVR == DicomDataDictionary.getVR("OB")){
                            DicomOBElement element;
                            byte[] dataArray = null;
                            //If element already exist, then pull the data and add new value.
                            if(dds.containsElement(aTag)){
                                //Extract element from dds.
                                element = (DicomOBElement)dds.findElement(aTag);
                                //Extract the existing data from the element
                                ByteBuffer dataBuffer = element.getBuffer();
                                dataBuffer.get(dataArray);
                            }
                            //Assign value to nuValue.  It uses Long primitive to make sure there is no
                            //  truncation of data.
                            Long nuValue = new Long(value);
                            //Add nuValue to existing data.
                            byte[] nuArray = this.addElementToByteArray(dataArray, nuValue);
                            //You cannot just add the nuArray to the existing element in the dds.
                            //  Must create a new element and insert it into the dds.  This automatically
                            //  overwrites the original element.
                            DicomOBElement updatedElement = new DicomOBElement(aTag, nuArray);
                            dds.insert(updatedElement);
                        }
                        else if(elementVR == DicomDataDictionary.getVR("US")){
                            DicomUSElement element;
                            int[] dataArray = null;
                            //If element already exist, then pull the data and add new value.
                            if(dds.containsElement(aTag)){
                                //Extract element from dds.
                                element = (DicomUSElement)dds.findElement(aTag);
                                //Extract the existing data from the element
                                dataArray = element.getUSData();   
                            }
                            //Assign value to nuValue.  It uses Long primitive to make sure there is no
                            //  truncation of data.
                            Long nuValue = new Long(value);
                            //Add nuValue to existing data.
                            int[] nuArray = this.addElementToIntArray(dataArray, nuValue);
                            //You cannot just add the nuArray to the existing element in the dds.
                            //  Must create a new element and insert it into the dds.  This automatically
                            //  overwrites the original element.
                            DicomUSElement updatedElement = new DicomUSElement(aTag, nuArray);
                            dds.insert(updatedElement);
                        }
                        else if(elementVR == DicomDataDictionary.getVR("SL")){
                            DicomSLElement element;
                            int[] dataArray = null;
                            //If element already exist, then pull the data and add new value.
                            if(dds.containsElement(aTag)){
                                //Extract element from dds.
                                element = (DicomSLElement)dds.findElement(aTag);
                                //Extract the existing data from the element
                                dataArray = element.getSLData();   
                            }
                            //Assign value to nuValue.  It uses Long primitive to make sure there is no
                            //  truncation of data.
                            Long nuValue = new Long(value);
                            //Add nuValue to existing data.
                            int[] nuArray = this.addElementToIntArray(dataArray, nuValue);
                            //You cannot just add the nuArray to the existing element in the dds.
                            //  Must create a new element and insert it into the dds.  This automatically
                            //  overwrites the original element.
                            DicomSLElement updatedElement = new DicomSLElement(aTag, nuArray);
                            dds.insert(updatedElement);
                        }
                        else if(elementVR == DicomDataDictionary.getVR("OW")){
                            DicomOWElement element;
                            short[] dataArray = null;
                            //If element already exist, then pull the data and add new value.
                            if(dds.containsElement(aTag)){
                                //Extract element from dds.
                                element = (DicomOWElement)dds.findElement(aTag);
                                //Extract the existing data from the element
                                ShortBuffer dataBuffer = (ShortBuffer)element.getValue(); // 3.2.2c getShortBuffer();
                                dataBuffer.get(dataArray);
                            }
                            //Assign value to nuValue.  It uses Long primitive to make sure there is no
                            //  truncation of data.
                            Long nuValue = new Long(value);
                            //Add nuValue to existing data.
                            short[] nuArray = this.addElementToShortArray(dataArray, nuValue);
                            //You cannot just add the nuArray to the existing element in the dds.
                            //  Must create a new element and insert it into the dds.  This automatically
                            //  overwrites the original element.
                            DicomOWElement updatedElement = new DicomOWElement(aTag, nuArray);
                            dds.insert(updatedElement);
                        }
                        else if(elementVR == DicomDataDictionary.getVR("SS")){
                            DicomSSElement element;
                            short[] dataArray = null;
                            //If element already exist, then pull the data and add new value.
                            if(dds.containsElement(aTag)){
                                //Extract element from dds.
                                element = (DicomSSElement)dds.findElement(aTag);
                                //Extract the existing data from the element
                                dataArray = element.getSSData();   
                            }
                            //Assign value to nuValue.  It uses Long primitive to make sure there is no
                            //  truncation of data.
                            Long nuValue = new Long(value);
                            //Add nuValue to existing data.
                            short[] nuArray = this.addElementToShortArray(dataArray, nuValue);
                            //You cannot just add the nuArray to the existing element in the dds.
                            //  Must create a new element and insert it into the dds.  This automatically
                            //  overwrites the original element.
                            DicomSSElement updatedElement = new DicomSSElement(aTag, nuArray);
                            dds.insert(updatedElement);
                        }
                        else if(elementVR == DicomDataDictionary.getVR("UL")){
                            DicomULElement element;
                            int[] dataArray = null;
                            //If element already exist, then pull the data and add new value.
                            if(dds.containsElement(aTag)){
                                //Extract element from dds.
                                element = (DicomULElement)dds.findElement(aTag);
                                //Extract the existing data from the element
                                dataArray = element.getULData();   
                            }
                            //Assign value to nuValue.  It uses Long primitive to make sure there is no
                            //  truncation of data.
                            Long nuValue = new Long(value);
                            //Add nuValue to existing data.
                            int[] nuArray = this.addElementToIntArray(dataArray, nuValue);
                            //You cannot just add the nuArray to the existing element in the dds.
                            //  Must create a new element and insert it into the dds.  This automatically
                            //  overwrites the original element.
                            DicomULElement updatedElement = new DicomULElement(aTag, nuArray);
                            dds.insert(updatedElement);
                        }
                        else if(elementVR == DicomDataDictionary.getVR("FL")){
                        	DicomFLElement element;
                            float[] dataArray = null;
                            //If element already exist, then pull the data and add new value.
                            if(dds.containsElement(aTag)){
                                //Extract element from dds.
                                element = (DicomFLElement)dds.findElement(aTag);
                                //Extract the existing data from the element
                                dataArray = element.getFLData();   
                            }
                            //Assign value to nuValue.  It uses Long primitive to make sure there is no
                            //  truncation of data.
                            float nuValue = new Float(value);
                            //Add nuValue to existing data.
                            float[] nuArray = this.addElementToFloatArray(dataArray, nuValue);
                            //You cannot just add the nuArray to the existing element in the dds.
                            //  Must create a new element and insert it into the dds.  This automatically
                            //  overwrites the original element.
                            DicomFLElement updatedElement = new DicomFLElement(aTag, nuArray);
                            dds.insert(updatedElement);
                        }
                        else if(elementVR == DicomDataDictionary.getVR("FD")){
                        	DicomFDElement element;
                            double[] dataArray = null;
                            //If element already exist, then pull the data and add new value.
                            if(dds.containsElement(aTag)){
                                //Extract element from dds.
                                element = (DicomFDElement)dds.findElement(aTag);
                                //Extract the existing data from the element
                                dataArray = element.getFDData();   
                            }
                            //Assign value to nuValue.  It uses Long primitive to make sure there is no
                            //  truncation of data.
                            double nuValue = new Double(value);
                            //Add nuValue to existing data.
                            double[] nuArray = this.addElementToDoubleArray(dataArray, nuValue);
                            //You cannot just add the nuArray to the existing element in the dds.
                            //  Must create a new element and insert it into the dds.  This automatically
                            //  overwrites the original element.
                            DicomFDElement updatedElement = new DicomFDElement(aTag, nuArray);
                            dds.insert(updatedElement);
                        }
                        else{ 
                        	// at this point it is assumed value VR is of char string, not binary!
                            String currentValue = "";
                            if(dds.containsElement(aTag)){
                                // Extract element from dds -- Must retrieve each individual value this way
                            	// as the VM is greater than 1. -- Then concatenate together again.
                                DicomElement element = dds.findElement(aTag);
                                for(int i=0; i<element.vm(); i++){
                                    currentValue = currentValue.concat(element.getStringValue(i));
                                    if(i < element.vm()-1){
                                        currentValue = currentValue.concat("\\");
                                    }
                                }
                            }
                            else{
                                currentValue = null;
                            }

                            //then Append a "\\" and the Value variable to this Tag.
                            DicomElement updatedElement = DicomElementFactory.instance().createElement(aTag,
                                    (currentValue+"\\"+value));
                            dds.insert(updatedElement);
                        }
                    }
                    //If ML is greater than 1.
                    else if(ml > 1){
                        //then extract this Tag from the dds.
                        String currentValue;
                        if(dds.containsElement(aTag)){
                            currentValue = dds.getElementStringValue(aTag);
                        }
                        else{
                            currentValue = null;
                        }
                        //then Append the Value variable to this Tag.
                        if(aTag.group() != 0x7FE0){
                            DicomElement updatedElement = DicomElementFactory.instance().createElement(aTag,
                                    (currentValue+value));
                            dds.insert(updatedElement);
                        }
                        if(aTag.group() == 0x7FE0){
                            if(ml == 2){
                                String parsedValues[] = value.split("=");
                                String lengthValue = parsedValues[1];
                                String parsedLength[] = lengthValue.split(" ");
                                String pixelDataLength = parsedLength[0].trim();
                                Long lengthLong = new Long(pixelDataLength);
                                this.originalPixelData.setOriginalLength(lengthLong.longValue());
                            }
                            if(ml == 3){
                                String parsedValues[] = value.split("=");
                                String lengthValue = parsedValues[1];
                                String parsedOffset[] = lengthValue.split(" ");
                                String pixelDataOffset = parsedOffset[0].trim();
                                Integer offsetInt = new Integer(pixelDataOffset);
                                this.originalPixelData.setOriginalOffset(offsetInt.intValue());
                            }
                        }
                    }
                    else{
                        //FUTURE The if sequence works.  But I like to find a more efficient way.
                        //AttributeTag aTag = new AttributeTag(tag);
                        if((aTag.element() == 0) || (aTag.element() == 1)){
                            if(aTag.group() <= 2){
                                DicomElement nuElement = DicomElementFactory.instance().createElement(aTag,
                                        value);
                                dds.insert(nuElement);
                            }
                        }
                        else{
                            if(aTag.group() != 0x7FE0){
                                DicomElement nuElement = DicomElementFactory.instance().createElement(aTag,
                                        value);
                                dds.insert(nuElement);
                            }
                            if((aTag.group() == 0x7FE0) && (aTag.element() == 0x0010)) {
                                char isCarat = fields[1].charAt((fields[1].length())-3);
                                if(isCarat == '^'){
                                    String desc_vrField = fields[1];
                                    String desc_vr[] = desc_vrField.split("\\^");
                                    String textfileVR = desc_vr[1];
                                    originalPixelData.setOriginalVR(DicomDataDictionary.getVR(textfileVR));
                                }
                                else{
                                    short bitsAllocated = (short)dds.getElementIntValue(DCM.E_BITS_ALLOCATED);
                                    this.originalPixelData.setBitsAllocated(bitsAllocated);
                                }
                            }
                        }
                    }
                    //End If for VM/VL.
                }
            }
            else if (fields.length == 2){
                short vr = DicomDataDictionary.getElementVR(aTag);
                if(DicomDataDictionary.getVRString(vr).equals("SQ")){
                    DicomSQElement nuElement = new DicomSQElement( aTag, (DicomDataSet[])null );                    
                    dds.insert(nuElement);    
                }
            }
        }
        catch(DCSException dcs){
            logger.error(dcs.getMessage());
            logger.error(this.getClass().getName()+": " +
                    "Exception thrown while extracting Dicom Element.");
            throw new TextFileExtractionException("Failure to extract Dicom Element.", dcs);
        }
    }

    /**
     * Parse the Text file HIS Update section.  The Text file is made up of three sections,
     * "Data1", "DICOM Data" and  optionally "HIS UPDATE".  Here only the HIS UPDATE section
     * is read and decoded.
     * 
     * @param buffer represents the Text file now in the form of a BufferReader object.
     * @throws TextFileExtractionException
     */
    private HashMap<String, String> parseHisUpdates(BufferedReader buffer) 
    	throws TextFileExtractionException {
        
        String textLine = "";
        HashMap<String, String> hisUpdates=null;
//      testLogger.info("... Parsing text data update section ...");
        try { // parse line for initial "gggg,eeee" or "gggg,eeee gggg,eeee" tags
	        do {
	           textLine = this.getNextTextLine(buffer);
	        } while(!(textLine.startsWith("$$BEGIN HIS UPDATE")));
        }
	    catch(TextFileExtractionException tfee) { // catch all
	        logger.info("Warning: NO HIS Update section in text data !!!");
	        logger.debug("Warning: obsolete TXT format NO VistA updates !!! ");
	        return hisUpdates;
	    }
        textLine = this.getNextTextLine(buffer); // skip to first HIS update line
        hisUpdates=new HashMap<String, String>();
           
        while(!(textLine.startsWith("$$END HIS UPDATE"))) {
           try { // parse line for initial "gggg,eeee" or "gggg,eeee gggg,eeee" tags
	           // and ending text value 
	           String splitPipePattern = "\\|";
	           Pattern pFields = Pattern.compile(splitPipePattern);
	           String fields[] = new String[4];
	           fields = pFields.split(textLine);
	           if ( ((fields[0].length()==9) || (fields[0].length()==18)) &&
	        		   (fields[3].length()>0)) {
	        	   // place tag(s)-value pairs to HashMap
	        	   hisUpdates.put(fields[0], fields[3]);
	           }
           }
           catch(Throwable t) { // catch all
           }
           textLine = this.getNextTextLine(buffer);
        } // end wile
        
        if (hisUpdates.isEmpty()) 
        	return null;
        else 
        	return hisUpdates; 
    }

    /**
     * Reads next line from the Text file, which is now in the form of BufferedReader 
     * object.  This method checks for EOF, nulls, and blank lines.  This is primarily in
     * case the Text file was not properly formatted or corrupted.  If the line is valid, 
     * the line is returned.
     * 
     * @param in represents the BufferedReader object.
     * @return represents the next line read from the BufferedReader object.
     * @throws TextFileExtractionException
     */
    private String getNextTextLine(BufferedReader in) throws TextFileExtractionException{
        String line = null;
        try{
            do{
                if((line = in.readLine()) == null){
                    throw new TextFileExtractionException();
                }
            }while(line.equals(""));
        }
        catch(IOException io){
            logger.error(io.getMessage());
            logger.error(this.getClass().getName()+": " +
                    "Exception thrown while getting next line from Text Line.");
            throw new TextFileExtractionException("Failure to get next line.", io);
        }
        return line;
    }
    
    private boolean isGroupToBeAdded(int Group){
        boolean addGroup = true;
        
        //Do not allow Odd Groups
        if((Group % 2) != 0){
            addGroup = false;
        }
        
        //Do not allow Icon Image Sequences
        if(Group == 0x0088){
            addGroup = false;
        }
        
        //Need to remove Group 0002 elements.  Found bug of sending Group 0002 elements
        //  to CSTore SCP device.  This is not valid.  I strongly believe to add it here.  I do
        //  not think any code between here and the sending uses this group.  But I could be 
        //  wrong.
        if(Group == 0x0002){
            addGroup = false;
        }
        
        return addGroup;
    }
    
    private void customTGAElementCleanup(){
    	try{
    		if(this.dicomDataSet.containsElement(new AttributeTag("0008,0008"))){
    			DicomCSElement objectType = (DicomCSElement)this.dicomDataSet.findElement(new AttributeTag("0008,0008"));
    			String[] values = objectType.getStringData();
    			ArrayList<String> nuValueArray = new ArrayList<String>(values.length);
    			for(int i=0; i<values.length; i++){
    					nuValueArray.add(values[i]);
    			}
    			while(nuValueArray.contains("")){
    				nuValueArray.remove("");
    			}
    			nuValueArray.trimToSize();
    			
    			String[] nuValues = new String[nuValueArray.size()];
    			Iterator<String> iter = nuValueArray.iterator();
    			int j = 0;
    			while(iter.hasNext()){
    				nuValues[j] = iter.next();
    				j++;
    			}
    			DicomCSElement nuObjectType = new DicomCSElement(new AttributeTag("0008,0008"),
    											nuValues);
    			this.dicomDataSet.insert(nuObjectType);
    		}
    	}
    	catch(DCSException dcsX){
    		//do nothing
    	}
    	
    }
    
    private byte[] addElementToByteArray(byte[] oldArray, Long nuValue){
    
        if(oldArray == null){
            oldArray = new byte[0];
        }
        int length = oldArray.length;
        int index = length+1;
        byte nuArray[] = new byte[index];
        System.arraycopy(oldArray, 0, nuArray, 0, length);
        nuArray[index-1] = nuValue.byteValue();
        return nuArray;
    }
    
    private short[] addElementToShortArray(short[] oldArray, Long nuValue){
        
            int length = oldArray.length;
            int index = length+1;
            short nuArray[] = new short[index];
            System.arraycopy(oldArray, 0, nuArray, 0, length);
            nuArray[index-1] = nuValue.shortValue();
            return nuArray;
        }

    private int[] addElementToIntArray(int[] oldArray, Long nuValue){
        
            int length = oldArray.length;
            int index = length+1;
            int nuArray[] = new int[index];
            System.arraycopy(oldArray, 0, nuArray, 0, length);
            nuArray[index-1] = nuValue.intValue();
            return nuArray;
        }

    private float[] addElementToFloatArray(float[] oldArray, Float nuValue){
        
            int length = oldArray.length;
            int index = length+1;
            float nuArray[] = new float[index];
            System.arraycopy(oldArray, 0, nuArray, 0, length);
            nuArray[index-1] = nuValue.floatValue();
            return nuArray;
        }

    private double[] addElementToDoubleArray(double[] oldArray, Double nuValue){
        
            int length = oldArray.length;
            int index = length+1;
            double nuArray[] = new double[index];
            System.arraycopy(oldArray, 0, nuArray, 0, length);
            nuArray[index-1] = nuValue.doubleValue();
            return nuArray;
        }    
}
