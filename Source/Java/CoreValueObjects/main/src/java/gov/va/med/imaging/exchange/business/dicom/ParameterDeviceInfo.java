/*
 * Created on Mar 4, 2006
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * This class inherits the ModalityDicInfo class.  The CT Parameters file contains the same 
 * information as needed for the ModalityDicInfo class along with two additional parameters.  The 
 * two additional parameters:
 * 		Site ID
 * 		Last Modification Date of the CT Parameters file entry.
 * 
 *<p>
 * @author William Peterson
 *
 */
public class ParameterDeviceInfo extends ModalityDicInfo {

    
    private static Logger logger = Logger.getLogger(ParameterDeviceInfo.class);
    
    private String siteID = null;
    
    private Date lastModificationDate = null;


    /**
     * Constructor.
     */
    public ParameterDeviceInfo(){
    	
    }
    
    /**
     * 
     * Constructor.  Decompose (parse) an entry based on the CT_Params.dic file.
     * 
     * @param deviceLineEntry
     * @throws ParameterDecompositionException
     */
    public ParameterDeviceInfo(String deviceLineEntry)throws ParameterDecompositionException {
        //super(deviceLineEntry);
    	this.decomposition(deviceLineEntry);
    }

                
        /**
         * @return Returns the changeDate.
         */
        public Date getChangeDate() {
            return lastModificationDate;
        }
        
        /**
         * @param changeDate The changeDate to set.
         */
        public void setChangeDate(Date modifiedDate) {
            this.lastModificationDate = modifiedDate;
        }
        
        /**
         * @return Returns the siteID.
         */
        public String getSiteID() {
            return siteID;
        }
        
        /**
         * @param siteID The siteID to set.
         */
        public void setSiteID(String siteID) {
            this.siteID = siteID;
        }
        
        private void decomposition(String deviceInfo)throws ParameterDecompositionException{
            
            //Extract each piece between the | delimiters and assign accordingly.
            char delimiter = '|';
            TextFileUtil parser = new TextFileUtil();
            this.siteID = StringUtil.Piece(deviceInfo, delimiter, 1);
            if((this.siteID == null) || (this.siteID.length() == 0)){
            	throw new ParameterDecompositionException("Bad Site ID value.");
            }
            this.manufacturer = StringUtil.Piece(deviceInfo, delimiter, 2);
            if((this.manufacturer == null) || (this.manufacturer.length() == 0)){
            	throw new ParameterDecompositionException("Bad Manufacturer value.");
            }
            this.model = StringUtil.Piece(deviceInfo, delimiter, 3);
            if((this.model == null) || (this.model.length() == 0)){
            	throw new ParameterDecompositionException("Bad Manufacturer value.");
            }
            try{
                String changeDate = StringUtil.Piece(deviceInfo, delimiter, 4);
                SimpleDateFormat filePattern = new SimpleDateFormat("d-MMM-yyyy");
                this.lastModificationDate = filePattern.parse(changeDate);

                String params = "";
                if((params = StringUtil.Piece(deviceInfo, delimiter, 5)) == null){
                    this.dcmtotgaParameters = null;                
                }
                else{
                    this.dcmtotgaParameters = new Parameters(params);
                }
            }
            catch(ParseException parseError){
                logger.warn(this.getClass().getName()+": Cannot parse Date for ...");
                logger.warn("Site: "+this.siteID);
                logger.warn("Mfg: "+this.manufacturer);
                logger.warn("Model: "+this.model);
                throw new ParameterDecompositionException(parseError);
            }
            catch(NumberFormatException nfe){
                logger.warn(this.getClass().getName()+": Cannot parse parameters for ...");
                logger.warn("Site: "+this.siteID);
                logger.warn("Mfg: "+this.manufacturer);
                logger.warn("Model: "+this.model);
                throw new ParameterDecompositionException(nfe);
            }            
        }
        
        
        /**
         * Compare two ParameterDeviceInfo objects.  
         * 
         * @return True if the Site ID, Manufacturer, and Model matches.
         */
        @Override
		public boolean equals(Object obj) {
            if (!(obj instanceof ParameterDeviceInfo)) {
              return false;
            }
            ParameterDeviceInfo modality = (ParameterDeviceInfo) obj;
            return this.siteID.equalsIgnoreCase(modality.getSiteID())
                    && this.manufacturer.equalsIgnoreCase(modality.getManufacturer())
                    && this.model.equalsIgnoreCase(modality.getModel());
        }
}

