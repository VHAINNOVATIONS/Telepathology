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
 * This class maintains one or two DCMTOTGAParameter objects.  It is possible for a single Modality 
 * Parameter entry to contain two sets of DCMTOTGA parameters separated by a slash ("/").  Feeding the 
 * entire DCMTOTGA parameter entry into this class properly parses the entry into the respective 
 * Full Dataset DCMTOTGA parameter and Reduced Dataset DCMTOTGA parameter object.  It also maintains
 * the relationship of these objects to a single entry. 
 *
 * <p>
 * @author William Peterson
 *
 */
public class Parameters {

    //This represents the current set being used.  It is possible to have two sets 
    //  of parameters.  This variable points to the set wanted.
    private DCMTOTGAParameter matchedParameterSet = null;
    
    private DCMTOTGAParameter fullParameterSet = null;
    
    private DCMTOTGAParameter reducedParameterSet = null;
    
    private boolean slash = false;
    
    private boolean parametersExist = false;
    
    private static Logger logger = Logger.getLogger(Parameters.class);
    
    
    /**
     * Constructor.  Decompose (parse) the DCMTOTGA parameters from a single Modality line entry.
     * 
     * @throws
     */
    public Parameters(String parameters) throws ParameterDecompositionException{
        this.decomposition(parameters);
    }
    
    
    /**
     * @return Returns the Addition value for the selected DCMTOTGA Parameter Set.
     */
    public int getAdditionParameter() {
        return this.matchedParameterSet.getAdditionParameter();
    }
    
    /**
     * @return True if there is an Addition value for the selected DCMTOTGA Parameter Set.
     */
    public boolean isAdditionSet(){
        if(this.parametersExist){
            return this.matchedParameterSet.isAdditionSet();
        }
        return false;
    }
    
    /**
     * @return Returns the Bits Stored value for the selected DCMTOTGA Parameter Set.
     */
    public int getBitsParameter() {
        return this.matchedParameterSet.getBitsParameter();
    }

    /**
     * @return True if there is a Bits Stored value for the selected DCMTOTGA Parameter Set.
     */
    public boolean isBitsSet(){
        if(this.parametersExist){
            return this.matchedParameterSet.isBitsSet();
        }
        return false;
    }
    
    /**
     * @return Returns the Ceiling value for the selected DCMTOTGA Parameter Set.
     */
    public int getCeilingParameter() {
        return this.getCeilingParameter();
    }
    
    /**
     * @return True if there is a Ceiling value for the selected DCMTOTGA Parameter Set.
     */
    public boolean isCielingSet(){
        if(this.parametersExist){
            return this.matchedParameterSet.isCielingSet();
        }
        return false;
    }
    
    /**
     * @return Returns the Floor value for the selected DCMTOTGA Parameter Set.
     */
    public int getFloorParameter() {
        return this.matchedParameterSet.getFloorParameter();
    }

    /**
     * @return True if there is a Floor value for the selected DCMTOTGA Parameter Set.
     */
    public boolean isFloorSet(){
        if(this.parametersExist){
            return this.matchedParameterSet.isFloorSet();
        }
        return false;
    }
    
    /**
     * @return Returns the Reduction Factor value for the selected DCMTOTGA Parameter Set.
     */
    public int getReductionParameter() {
        return this.matchedParameterSet.getReductionParameter();
    }

    /**
     * @return True if there is a Reduction Factor value for the selected DCMTOTGA Parameter Set.
     */
    public boolean isReductionSet(){
        if(this.parametersExist){
            return this.isReductionSet();
        }
        return false;
    }
    
    /**
     * @return Returns the Subtraction value for the selected DCMTOTGA Parameter Set.
     */
    public int getSubtractionParameter() {
        return this.matchedParameterSet.getSubtractionParameter();
    }
    
    /**
     * @return True if there is a Subtraction value for the selected DCMTOTGA Parameter Set.
     */
    public boolean isSubtractionSet(){
        if(this.parametersExist){
            return this.matchedParameterSet.isSubtractionSet();
        }
        return false;
    }

    /**
     * @return True if there is a Reduction Factor value for the selected DCMTOTGA Parameter Set.
     */
    public boolean containsReductionFactor(){
        
        return this.matchedParameterSet.containsReductionFactor();
    }
    
    /**
     * @return True if there was a Slash in the String given.
     */
    public boolean containsSlash(){
        
        return this.slash;
    }
    
    /**
     * Set the Full Parameter Set as the selected Parameter Set.
     */
    public void setFullParameterSet(){
            this.matchedParameterSet = this.fullParameterSet;
    }
    
    /**
     * Set the Reduced Parameter Set as the selected Parameter Set.
     */
    public void setReducedParameterSet(){
        if(this.reducedParameterSet != null){
            this.matchedParameterSet = this.reducedParameterSet;
        }
    }
    
    /**
     * @return True if a Parameter Set is selected.
     */
    public boolean isParametersSet() {
        if(this.parametersExist){
            return this.matchedParameterSet.isParametersSet();
        }
        	return this.parametersExist;
    }
    
    /**
     * @return True if the selected Parameter Set was set as <DICOM>.
     */
    public boolean isDICOMParameterSet(){
    	if(this.parametersExist){
    		return this.matchedParameterSet.isDICOMParameterSet();
    	}
    		return false;
    }

    
    private void decomposition(String settings)throws ParameterDecompositionException{
        
        TextFileUtil parameterParser = new TextFileUtil();
        char slash = '/';
        
        //Check if settings are empty.
        if(settings == null || settings.length() == 0){
            //Set to false if no parameters exist in the input variable.
            this.parametersExist = false;
            return;
        }
        
        String trimmedSettings = settings.trim();
        //Set to true to recognize at least something existed.
        this.parametersExist = true;
        //Check to see if there are 2 sets of arguments.
        if(trimmedSettings.indexOf(slash) == -1){
            //If only one set, assign to fullParameterSet.
            this.fullParameterSet = new DCMTOTGAParameter(trimmedSettings);
            //Assign fullParameterSet to matchedParameterSet.
            this.matchedParameterSet = this.fullParameterSet;
        }
        else{
            this.slash = true;
            
            String firstParameterSet = StringUtil.Piece(settings, slash, 1);
            this.reducedParameterSet = new DCMTOTGAParameter(firstParameterSet);
        
            String secondParameterSet = StringUtil.Piece(settings, slash, 2);
            this.fullParameterSet = new DCMTOTGAParameter(secondParameterSet);
            
            if(this.fullParameterSet.containsReductionFactor()){
            	logger.error(this.getClass().getName()+": Generic DICOM layer: \n"+
            			"Second set of DCMTOTGA Parameters contains a Reduction Factor (Rx) value.\n"+
            			"Reduction Factor value may only exist with the first DCMTOTGA Parameters.");
            }
            
            if(this.fullParameterSet.isParametersSet()){
                this.matchedParameterSet = this.fullParameterSet;
            }
        }
    }
    
    @Override
	public String toString(){
        StringBuffer parameters = new StringBuffer(" ");
        if(this.slash){
            if(this.reducedParameterSet != null ){
                parameters.append(this.reducedParameterSet.toString()+"/");
            }
            if(this.fullParameterSet != null){
                parameters.append(this.fullParameterSet.toString());
            }
        }
        else{
            if(this.fullParameterSet != null){
                parameters.append(this.fullParameterSet.toString());
            }
        }
        return parameters.toString();
    }
}
