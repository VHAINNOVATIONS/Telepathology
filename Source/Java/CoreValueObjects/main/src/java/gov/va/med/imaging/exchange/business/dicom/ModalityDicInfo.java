/*
 * Created on Feb 13, 2006
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

import gov.va.med.imaging.StringUtil;
import gov.va.med.imaging.TextFileUtil;
import gov.va.med.imaging.exchange.business.dicom.exceptions.ParameterDecompositionException;

import org.apache.log4j.Logger;

/**
 * This class a single Modality parameter entry.  It represents the first four parameters of a 
 * Modality.dic line entry.  It contains:
 * 		Manufacturer
 * 		Model
 * 		Modality Code
 *		DCMTOTGA parameters
 *<p>
 * It is possible for the single entry to contain more than four parameters.  Any additional parameters
 * will be ignored.  
 *
 *<p>
 * @author William Peterson
 *
 */
public class ModalityDicInfo {

    private static final Logger logger = Logger.getLogger(ModalityDicInfo.class);
        
    protected String manufacturer = null;
    
    protected String model = null;
    
    protected String modalityCode = null;
    
    protected Parameters dcmtotgaParameters = null;
    
    /**
     * Constructor
     */
    public ModalityDicInfo(){
  	
    }
    
    
    /**
     * Constructor.  Decompose (parse) a single Modality.dic file entry.
     * 
     * @param deviceLineEntry represents the single entry.
     * @throws ParameterDecompositionException
     */
    public ModalityDicInfo(String deviceLineEntry)throws ParameterDecompositionException {
        //super();
    	this.decomposition(deviceLineEntry);
    }
        
    
    /**
     * @return Returns the manufacturer.
     */
    public String getManufacturer() {
        return manufacturer;
    }
    /**
     * @param manufacturer The manufacturer to set.
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    /**
     * @return Returns the model.
     */
    public String getModel() {
        return model;
    }
    /**
     * @param model The model to set.
     */
    public void setModel(String model) {
        this.model = model;
    }
    /**
     * @return Returns the DCMTOTGA parameters.
     */
    public Parameters getDCMTOTGAParameters() {
        return this.dcmtotgaParameters;
    }
    /**
     * @param parameters The DCMTOTGA parameters to set.
     */
    public void setDCMTOTGAParameters(Parameters parameters) {
        this.dcmtotgaParameters = parameters;
    }
    
    /**
     * @return Returns the modalityCode.
     */
    public String getModalityCode() {
        return modalityCode;
    }
    
    /**
     * @param modalityCode The modalityCode to set.
     */
    public void setModalityCode(String modalityCode) {
        this.modalityCode = modalityCode;
    }

    
    
    private void decomposition(String deviceInfo)throws ParameterDecompositionException{
        
        //Extract each piece between the | delimiters and assign accordingly.
        char delimiter = '|';
        TextFileUtil parser = new TextFileUtil();
        this.manufacturer = StringUtil.Piece(deviceInfo, delimiter, 1);
        if((this.manufacturer == null) || (this.manufacturer.length() == 0)){
        	throw new ParameterDecompositionException("Bad Manufacturer value.");
        }
        this.model = StringUtil.Piece(deviceInfo, delimiter, 2);
        if((this.model == null) || (this.model.length() == 0)){
        	throw new ParameterDecompositionException("Bad Model value.");
        }
        this.modalityCode = StringUtil.Piece(deviceInfo, delimiter, 3);
        if((this.modalityCode == null) || (this.modalityCode.length() == 0)){
        	throw new ParameterDecompositionException("Bad Modality Code value.");
        }
        try{
            String params = "";
            if((params = StringUtil.Piece(deviceInfo, delimiter, 4)) == null){
                this.dcmtotgaParameters = null;                
            }
            else{
                this.dcmtotgaParameters = new Parameters(params);
            }
        }
        catch(NumberFormatException nfe){
            logger.error(this.getClass().getName()+": Cannot parse parameters for ...");
            logger.error("Mfg: "+this.manufacturer);
            logger.error("Model: "+this.model);
            throw new ParameterDecompositionException(nfe);
        }            
    }
        
    /**
     * Compares this ModalityDicInfo object with another ModalityDicInfo object.
     * 
     * @return True if the Manufacturer, Model, and Modality Code match.
     */
    @Override
	public boolean equals(Object obj) {
        if (!(obj instanceof ModalityDicInfo)) {
          return false;
        }
        ModalityDicInfo modality = (ModalityDicInfo) obj;
        return this.manufacturer.equalsIgnoreCase(modality.getManufacturer())
                && this.model.equalsIgnoreCase(modality.getModel())
                && this.modalityCode.equalsIgnoreCase(modality.getModalityCode());
    }
}
