/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: November 21, 2006
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

import com.lbs.DCS.DicomDataDictionary;

/**
 *
 * @author William Peterson
 *
 */
public class OriginalPixelDataInfo {
    
    private boolean valueRepresentationInTextFile = false;
    
    private short originalVR = 0;
    
    private long originalLength = 0;
    
    private int originalOffset = 0;
    
    private int bitsAllocated = 0;
    
    private String dcmtotgaParameters = null;
    
    /**
     * Constructor
     *
     * 
     */
    public OriginalPixelDataInfo() {
        super();
        //
    }
    
    
    /**
     * @return Returns the originalLength.
     */
    public long getOriginalLength() {
        return originalLength;
    }
    /**
     * @param originalLength The originalLength to set.
     */
    public void setOriginalLength(long originalLength) {
        this.originalLength = originalLength;
    }
    /**
     * @return Returns the originalOffset.
     */
    public long getOriginalOffset() {
        return originalOffset;
    }
    /**
     * @param originalOffset The originalOffset to set.
     */
    public void setOriginalOffset(int originalOffset) {
        this.originalOffset = originalOffset;
    }
    /**
     * @return Returns the originalVR.
     */
    public short getOriginalVR() {
        return originalVR;
    }
    /**
     * @param originalVR The originalVR to set.
     */
    public void setOriginalVR(short originalVR) {
        this.originalVR = originalVR;
        if(this.originalVR == DicomDataDictionary.getVR("OW")){
            this.bitsAllocated = 16;
        }
        if(this.originalVR == DicomDataDictionary.getVR("OB")){
            this.bitsAllocated = 8;
        }
    }
    
    
    /**
     * @return Returns the bitsAllocated.
     */
    public int getBitsAllocated() {
        return bitsAllocated;
    }
    /**
     * @param bitsAllocated The bitsAllocated to set.
     */
    public void setBitsAllocated(int bitsAllocated) {
        this.bitsAllocated = bitsAllocated;
        if(this.originalVR == 0){
            if(this.bitsAllocated == 8){
                this.originalVR = DicomDataDictionary.getVR("OB");
            }
            if(this.bitsAllocated == 16){
                this.originalVR = DicomDataDictionary.getVR("OW");
            }
        }
    }
    
    /**
     * @return Returns the dcmtotgaParameters.
     */
    public String getDcmtotgaParameters() {
        return dcmtotgaParameters;
    }
    /**
     * @param dcmtotgaParameters The dcmtotgaParameters to set.
     */
    public void setDcmtotgaParameters(String dcmtotgaParameters) {
        this.dcmtotgaParameters = dcmtotgaParameters;
    }
    
    public void setValueRepresentationInTextFile(boolean exist){
        this.valueRepresentationInTextFile = exist;
    }
    
    public boolean vrFieldExistInTextFile(){
        return this.valueRepresentationInTextFile;
    }
}
