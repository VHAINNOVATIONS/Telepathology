/*
 * Created on Mar 21, 2006
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

import gov.va.med.imaging.exchange.business.dicom.exceptions.ParameterDecompositionException;

import org.apache.log4j.Logger;

/**
 *
 * The DCM-to-TGA Parameters historically served the purpose of creating a TARGA file from
 * a DICOM file.  These parameters can be found on the DICOM Gateway in the Modality.dic file.
 * These parameters are the 4th piece of the Modality.dic file.  A line entry in the Modality.dic
 * file may appear as follows.
 * <p>
 * ACME Medical|Beep Beep|CR|<b>b10 f0 c1024</b>|LONGCASE^MAGDIR3||misc.dic|A
 * <p>
 *      <b>b</b> = bits stored
 *      <b>f</b> = floor pixel value (Clip all pixel values below this value)
 *      <b>c</b> = ceiling pixel value (Clip all pixel values above this value)
 *      <b>a</b> = add value to all pixel values
 *      <b>s</b> = subtract value to all pixel values
 *      <b>r</b> = reduction factor
 *<p>
 * It is possible to have two sets of DCMTOTGA parameters for a single line entry.  Use the Parameters
 * class in this case.  This class now allows this information to be maintained in a Java object.
 *<p>
 * @author William Peterson
 *
 */
public class DCMTOTGAParameter {
    
    private static Logger logger = Logger.getLogger(DCMTOTGAParameter.class);

    private int bitsParameter = 0;
    
    private boolean bitsSet = false;
    
    private int additionParameter = 0;
    
    private boolean additionSet = false;
    
    private int subtractionParameter = 0;
    
    private boolean subtractionSet = false;
    
    private int floorParameter = 0;
    
    private boolean floorSet = false;
    
    private int ceilingParameter = 0;
    
    private boolean ceilingSet = false;
    
    private int reductionParameter = 0;
    
    private boolean reductionSet = false;
    
    private boolean parametersSet = false;
    
    private boolean dicomParameterSet = false;
    
    
    /**
     * 
     * Constructor.  Decompose (parse) a DCMTOTGA parmeters string.  DCMTOTGA parameters 
     * is based on the DICOM to TGA conversion parameters used in the Modality.dic file.
     * 
     * @param parameter
     * @throws NumberFormatException
     */
    public DCMTOTGAParameter(String parameter) throws ParameterDecompositionException {
        //super();
    	this.decomposition(parameter);
    }


    /**
     * Get the Addition parameter.
     * 
     * @return Returns the addition parameter.
     */
    public int getAdditionParameter() {
        return additionParameter;
    }
    
    /**
     * Determine if the Addition parameter exist within the string.
     *
     * @return True means the Addition parameter exist.
     */
    public boolean isAdditionSet(){
        return this.additionSet;
    }
    
    /**
     * Get the Bits Stored parameter.
     * 
     * @return Returns the Bits Stored parameter.
     */
    public int getBitsParameter() {
        return bitsParameter;
    }

    /**
     * Determine if the Bits Stored parameter exist within the string.
     *
     * @return  True means the Bits Stored parameter exist.
     */
    public boolean isBitsSet(){
        return this.bitsSet;
    }
    /**
     * Get the Ceiling parameter.
     * 
     * @return Returns the Ceiling parameter.
     */
    public int getCeilingParameter() {
        return ceilingParameter;
    }
    
    /**
     * Determine if the Ceiling parameter exist within the string.
     *
     * @return True means the Ceiling parameter exist.
     */
    public boolean isCielingSet(){
        return this.ceilingSet;
    }
    
    /**
     * Get the Floor parameter.
     * 
     * @return Returns the Floor parameter.
     */
    public int getFloorParameter() {
        return floorParameter;
    }

    /**
     * Determine if the Floor parameter exist within the string.
     *
     * @return True means the Floor parameter exist.
     */
    public boolean isFloorSet(){
        return this.floorSet;
    }
    
    /**
     * Get the Reduction Factor parameter.
     * 
     * @return Returns the Reduction Factor parameter.
     */
    public int getReductionParameter() {
        return reductionParameter;
    }
    
    /**
     * Get the Substraction parameter.
     * 
     * @return Returns the Subtraction parameter.
     */
    public int getSubtractionParameter() {
        return subtractionParameter;
    }
    
    /**
     * Determine if the Subtraction parameter exist in the string.
     *
     * @return True means the Subtraction parameter exist.
     */
    public boolean isSubtractionSet(){
        return this.subtractionSet;
    }

    /**
     * Determine if the Reduction Factor parameter exist within the string.
     *
     * @return True means the Reduction Factor parameter exist.
     */
    public boolean containsReductionFactor(){
        
        return this.reductionSet;
    }
    
    /**
     * Determine if a valid parameters set exist within the string.
     * 
     * @return True means a valid parameter set exist.
     */
    public boolean isParametersSet() {
        return parametersSet;
    }

	/**
	 * @return the DICOMParameter
	 */
	public boolean isDICOMParameterSet() {
		return dicomParameterSet;
	}

    
    private void decomposition(String settings) throws ParameterDecompositionException{
        
        String space = new String(" ");
        
        //Decompose the string into each individual field.
        if(settings == null || settings.length() == 0){
            throw new ParameterDecompositionException("No DCMTOTGA Parameter data to decompose.");
        }
            try{
            	if(settings.equals("<DICOM>")){
            		this.dicomParameterSet = true;
                    this.parametersSet = true;
            		return;
            	}
                String[] pieces = new String[10];
                pieces = settings.split(space);
                for(int i=0; i < pieces.length; i++){
                    //pieces[i].toUpperCase();
                    //Determine if there is an A value.  Set the flag and value.
                    if(pieces[i].toUpperCase().startsWith("A")){
                        this.additionSet = true;
                        this.additionParameter = this.convertStringToInt(pieces[i]);
                    }
                    //Determine if there is a S value.  Set the flag and value.
                    if(pieces[i].toUpperCase().startsWith("S")){
                        this.subtractionSet = true;
                        this.subtractionParameter = this.convertStringToInt(pieces[i]);
                    }
                    //Determine if there is a B value.  Set the flag and value.
                    if(pieces[i].toUpperCase().startsWith("B")){
                        this.bitsSet = true;
                        this.bitsParameter = this.convertStringToInt(pieces[i]);
                    }

                    //Determine if there is a F value.  Set the flag and value.
                    if(pieces[i].toUpperCase().startsWith("F")){
                        this.floorSet = true;
                        this.floorParameter = this.convertStringToInt(pieces[i]);
                    }

                    //Determine if there is a C value.  Set the flag and value.
                    if(pieces[i].toUpperCase().startsWith("C")){
                        this.ceilingSet = true;
                        this.ceilingParameter = this.convertStringToInt(pieces[i]);
                    }

                    //Determine if there is a Reduction Factor.  Set the flag and value.
                    if(pieces[i].toUpperCase().startsWith("R")){
                        this.reductionSet = true;
                        this.reductionParameter = this.convertStringToInt(pieces[i]);
                    }
                }
                this.parametersSet = true;
            }
            catch (NumberFormatException nfe){
                logger.error("Current DCMTOTGA Parameters: "+settings+"\n");
                this.parametersSet = false;
                throw new ParameterDecompositionException("Failed to convert Parameter.");
            }
    }
    
    private int convertStringToInt(String argument)throws NumberFormatException{
        
        int temp = 0;
        String argumentValue = "";
        //Remove the First character, which is the letter.
        argumentValue = argument.substring(1);
        try{
            //convert String to int.
            temp = Integer.parseInt(argumentValue, 10);
        }
        catch(NumberFormatException badNumber){
            logger.error(this.getClass().getName()+": \n"+
                    "Failed to convert DCMTOTGA Parameters to integers.");
            logger.error("Current argument: "+argument);
            logger.error("Possible problem is typo between the letter O and the" +
                    " number 0 or similar.");
            throw new NumberFormatException("Failed to convert Parameter.");
            
        }
        return temp;
    }
    
    /**
     * Override method to display recieved Parameter Set into a custom String format.
     * 
     * @return represents the recieved data as a String.
     */
    @Override
	public String toString(){
        StringBuffer parameter= new StringBuffer(" ");
        if(this.dicomParameterSet){
        	parameter.append("Parameter Set is set to <DICOM>.");
        }
        else{
        	if(this.bitsSet){
        		parameter.append("b"+this.bitsParameter+" ");
        	}
        	if(this.additionSet){
        		parameter.append("a"+this.additionParameter+" ");
        	}
        	if(this.subtractionSet){
        		parameter.append("s"+this.subtractionParameter+" ");
        	}
        	if(this.floorSet){
        		parameter.append("f"+this.floorParameter+" ");
        	}
        	if(this.ceilingSet){
        		parameter.append("c"+this.ceilingParameter+" ");
        	}
        	if(this.reductionSet){
        		parameter.append("R"+this.reductionParameter+" ");
        	}
        }
        return parameter.toString();
    }
}
