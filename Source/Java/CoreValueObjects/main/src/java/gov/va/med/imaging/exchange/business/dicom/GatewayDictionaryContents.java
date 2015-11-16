/*
 * Created on Mar 25, 2006
 * Per VHA Directive 2004-038, this routine should not be modified.
//+---------------------------------------------------------------+
//| Property of the US Government.                                |
//| No permission to copy or redistribute this software is given. |
//| Use of unreleased versions of this software requires the user |
//| to execute a written test agreement with the VistA Imaging    |
//| Development Office of the Department of Veterans Affairs,     |
//| telephone (301) 734-0100.                                     |
//|                                                               |
//| The Food and Drug Administration classifies this software as  |
//| a medical device.  As such, it may not be changed in any way. |
//| Modifications to this software may result in an adulterated   |
//| medical device under 21CFR820, the use of which is considered |
//| to be a violation of US Federal Statutes.                     |
//+---------------------------------------------------------------+
 *
 */
package gov.va.med.imaging.exchange.business.dicom;

import gov.va.med.imaging.TextFileUtil;
import gov.va.med.imaging.exceptions.TextFileException;
import gov.va.med.imaging.exceptions.TextFileExtractionException;
import gov.va.med.imaging.exchange.business.dicom.exceptions.DicomDictionaryException;
import gov.va.med.imaging.exchange.business.dicom.exceptions.ParameterDecompositionException;

import java.util.Collections;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * This class performs the action of create lists based on the CT Parameters file and the Modality
 * Parameters file.  It loads CT Parameters file and the Modality Parameters file and parse each entry
 * into a collection.  Once both collections are built, they are available with Get commands. 
 *
 *<p>
 * @author William Peterson
 *
 */
public class GatewayDictionaryContents {

    private static GatewayDictionaryContents contents_ = null;
    
    private static Vector<ParameterDeviceInfo> ctParametersList = null;
    
    private static Vector<ModalityDicInfo> modalityDictionaryList = null;
    
    private static Logger logger = Logger.getLogger(GatewayDictionaryContents.class);
    
    
    /**
     * Constructor
     *
     * 
     */
    private GatewayDictionaryContents() {
        super();
    }
    
    /**
     * Singleton pattern. 
     *
     * @return Returns the single instance object of this class.
     */
    public synchronized static GatewayDictionaryContents getInstance(){
        if(contents_ == null){
            contents_ = new GatewayDictionaryContents();
        }
        return contents_;
    }
    
    /**
     * Load the CT Parameter List.  This list is based on the contents in the CT_Params.dic file.
     *
     * @param filename
     * @throws DicomDictionaryException
     */
    public void loadCTParameterList(String filename) throws DicomDictionaryException{
        if(ctParametersList == null){
            boolean decomposeProblem = false;
            try{
                String extractedLine = "";
                TextFileUtil util= new TextFileUtil();
                util.openTextFile(filename);
                ctParametersList = new Vector<ParameterDeviceInfo>();
                while((extractedLine = util.getNextTextLine()) != null){
                    ParameterDeviceInfo device = null;
                    try{
                    	device = new ParameterDeviceInfo(extractedLine);
                    }
                    catch(ParameterDecompositionException pde){
                        decomposeProblem = true;
                    }
                    if(device != null){
                        if(!ctParametersList.add(device)){
                            logger.warn("CT Parameter was not added to List: ");
                            logger.warn(device.getSiteID());
                            logger.warn(device.getManufacturer());
                            logger.warn(device.getModel());
                        }
                    }
                }
                if(decomposeProblem == true){
                    throw new TextFileExtractionException("CT Parameter Decomposition Exception.");
                }
                logger.info("CT Parameter List is loaded.");
                this.sortCTParametersList();
            }
            catch(TextFileException nofile){
                logger.warn(this.getClass().getName()+": ");
                logger.warn("Can not access file: "+filename);
                throw new DicomDictionaryException(nofile);
            }
            catch(TextFileExtractionException extractError){
                logger.warn(this.getClass().getName()+": ");
                logger.warn("Can not extract from file "+filename);
                throw new DicomDictionaryException(extractError);
            }
            catch(NullPointerException nulPointer){
                logger.warn(this.getClass().getName()+": ");
                logger.warn("Null Pointer thrown.");
                throw new DicomDictionaryException(nulPointer);
            }
        }
    }
    
    /**
     * Load the CT Parameter List.
     *
     * @param parameterList represents a collection of CT parameter entries.
     * @throws DicomDictionaryException
     */
    public void loadCTParameterList(Vector<ParameterDeviceInfo> parameterList){
        if(ctParametersList == null){
            ctParametersList = parameterList;
        }
    }
    
    /**
     * Load the Modality Parameter List.  This list is based on the contents in the Modality.dic file.
     *
     * @param filename
     * @throws DicomDictionaryException
     */
    public void loadModalityDictionaryList(String filename) 
            throws DicomDictionaryException{
        if(modalityDictionaryList == null){
            try{
                String extractedLine = "";
                TextFileUtil util= new TextFileUtil();
                util.openTextFile(filename);
                modalityDictionaryList = new Vector<ModalityDicInfo>();
                
                while((extractedLine = util.getNextTextLine()) != null){
                    ModalityDicInfo modality = null;
                    try{
                    	modality = new ModalityDicInfo(extractedLine);
                    }
                    catch(ParameterDecompositionException pde){
                    	//ignore.  This leaves modality object to null.
                    }
                    if(modality != null){
                        if(!modalityDictionaryList.add(modality)){
                            logger.warn("Modality was not added to List: ");
                            logger.warn(modality.getManufacturer());
                            logger.warn(modality.getModel());
                        }
                    }
                }
                logger.info("Modality.dic List is loaded.");
            }
            catch(TextFileException nofile){
                logger.warn(this.getClass().getName()+": ");
                logger.warn("Can not access file: "+filename);
                throw new DicomDictionaryException(nofile);
            }
            catch(TextFileExtractionException extractError){
                logger.warn(this.getClass().getName()+": ");
                logger.warn("Can not extract from file "+filename);
                throw new DicomDictionaryException(extractError);
            }
            catch(NullPointerException nulPointer){
                logger.warn(this.getClass().getName()+": ");
                logger.warn("Null Pointer thrown");
                throw new DicomDictionaryException(nulPointer);
            }
        }
    }
    
    /**
     * Load the Modality Parameter List.
     *
     * @param dictionaryList represents a collection of Modality parameter entries.
     * @throws DicomDictionaryException
     */
    public void loadModalityDictionaryList(Vector<ModalityDicInfo> dictionaryList){
        if(modalityDictionaryList == null){
            modalityDictionaryList = dictionaryList;
        }
    }
    
    /**
     * Get Modality parameter entries as a collection.
     *
     * @return represents the collection.
     */
    public Vector<ModalityDicInfo> getModalityDictionaryEntries(){
        return modalityDictionaryList;
    }
    
    /**
     * Get the CT parameter entries as a collection.
     *
     * @return represent the collection.
     */
    public Vector<ParameterDeviceInfo> getCTParametersList(){
        return ctParametersList;
    }
    
    private void sortCTParametersList(){
        //Create Comparator for sort mechanism.  Have Date in descending order.
        //  This is needed for searching later.  If this does not work, just revers
        //  the Iterator while cycling thru the list.
        CTParameterComparator sorter = new CTParameterComparator();
        //Sort list.  The sorting is done here instead of within the CTParameter
        //	class.  This is because other applications may want to sort by a 
        //	different method.  This sort method is specific to this solution.
        Collections.sort(ctParametersList, sorter);
        //Find match based on Site/Mfg/Model
    }
}
