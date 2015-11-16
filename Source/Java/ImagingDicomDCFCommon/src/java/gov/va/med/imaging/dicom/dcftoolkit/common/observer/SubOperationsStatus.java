/*
 * Created on Aug 22, 2005
// Per VHA Directive 2004-038, this routine should not be modified.
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
 */
package gov.va.med.imaging.dicom.dcftoolkit.common.observer;

import java.util.ArrayList;

/**
 *
 * This class contains the various fields belonging to a SubOperationsStatus Dimse message.
 * The fields in this class are updated on the C-Store SCU side.  Then the fields are then
 * read by the Subscriber to pass along in the C-Move-Response Dimse message.
 *
 * @author William Peterson
 *
 */
public class SubOperationsStatus {

    /**
     * 
     * Create instances.
     *
     */
    private int remainingSubOperations = 0;
    
    private int successfulSubOperations = 0;
    
    private int failedSubOperations = 0;
    
    private int warningSubOperations = 0;
    
    private int subOperationCompleteStatus = -1;
    
    private int totalSubOperations = 0;
    
    private ArrayList<String> sopInstanceUIDs = null;
    
    boolean isSubOperationsComplete = false;
    
    private boolean isSubOperationsActive = true;
    
    private String errorComment = null;
    
    public static final int SUCCESS = 0;
    public static final int CANCEL = 1;
    public static final int FAILURE = 2;
    public static final int WARNING = 3;
    public static final int FAILURE_COMMENT = 4;
    
    /**
     * 
     */
    public SubOperationsStatus() {
        super();
        this.sopInstanceUIDs = new ArrayList<String>();
    }

    public void setRemainingSubOperations(int total){
        this.remainingSubOperations = total;
        this.totalSubOperations = total;
    }
    
    public int getRemainingSubOperations(){
        return this.remainingSubOperations;
    }
    
    public void setSuccessfulSubOperations(){
        this.successfulSubOperations++;
        this.updateRemainingSubOperations();
    }
    
    public int getSuccessfulSubOperations(){
        return this.successfulSubOperations;
    }
    
    public void setFailedSubOperations(){
        this.failedSubOperations++;
        this.updateRemainingSubOperations();
    }
    
    public int getFailedSubOperations(){
        return this.failedSubOperations;
    }
    
    public void setWarningSubOperations(){
        this.warningSubOperations++;
        this.updateRemainingSubOperations();
    }
    
    public int getWarningSubOperations(){
        return this.warningSubOperations;
    }
       
    private void updateRemainingSubOperations(){
        if(this.remainingSubOperations > 0){
            this.remainingSubOperations--;
        }
    }
    
    public boolean isSubOperationsComplete(){
        return this.isSubOperationsComplete;
    }
    
    public void setCompleteStatus(){
        this.isSubOperationsComplete = true;
    }

    public void setCompleteStatus(int status){
        this.isSubOperationsComplete = true;
        this.subOperationCompleteStatus = status;
    }
    
    public int getCompleteStatus(){
        this.updateCompleteStatusBasedOnSubOperations();
        return this.subOperationCompleteStatus;
    }
    
    public ArrayList<String> getSOPInstanceUIDs(){
        return this.sopInstanceUIDs;
    }

    public void setSOPInstanceUIDs(ArrayList<String> rejectedSOPInstances){
        this.sopInstanceUIDs = rejectedSOPInstances;
    }
    
    public void addFailedSOPInstanceUID(String sopInstanceUID){
    	this.sopInstanceUIDs.add(sopInstanceUID);
        this.failedSubOperations++;
        this.updateRemainingSubOperations();
    }

    
    /**
     * @return Returns the isSubOperationsActive.
     */
    public boolean isSubOperationsActive() {
        return isSubOperationsActive;
    }
    
    /**
     * @param isSubOperationsActive The isSubOperationsActive to set.
     */
    public void setSubOperationsActive(boolean isSubOperationsActive) {
        this.isSubOperationsActive = isSubOperationsActive;
    }
    
    /**
     * Get the errorComment. 
     *
     * @return Returns the errorComment.
     */
    public String getErrorComment() {
        return errorComment;
    }
    
    /**
     * Set the errorComment. 
     *
     * @param errorComment The errorComment to set.
     */
    public void setErrorComment(String errorComment) {
        this.errorComment = errorComment;
        this.subOperationCompleteStatus = FAILURE_COMMENT;
    }
    
    private void updateCompleteStatusBasedOnSubOperations(){
    	if(this.subOperationCompleteStatus == -1 
    			|| this.subOperationCompleteStatus == FAILURE
    			|| this.subOperationCompleteStatus == FAILURE_COMMENT
    			|| this.subOperationCompleteStatus == WARNING){
    		
    		if(this.failedSubOperations == 0 && this.warningSubOperations == 0 
    				&& this.remainingSubOperations < 1){
    			if(this.subOperationCompleteStatus == FAILURE
    					|| this.subOperationCompleteStatus == FAILURE_COMMENT){
    				return;
    			}
    			this.subOperationCompleteStatus = SUCCESS;
    			return;
    		}
    		
    		if(this.failedSubOperations == 0 && this.warningSubOperations == 0 
    				&& this.remainingSubOperations > 0){
    			this.subOperationCompleteStatus = FAILURE;
    			return;
    		}
    		
    		if(this.successfulSubOperations == 0){
    			if(this.failedSubOperations == this.totalSubOperations){
    				this.subOperationCompleteStatus = FAILURE;
    				return;
    			}
    			if(this.warningSubOperations == this.totalSubOperations){
    				this.subOperationCompleteStatus = WARNING;
    				return;
    			}
    		}
    		
    		this.subOperationCompleteStatus = WARNING;
    		return;
    	}
    }
}
