/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: October 19, 2005
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
package gov.va.med.imaging.dicom.dcftoolkit.common.impl;

import gov.va.med.imaging.StringUtil;
import gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet;
import gov.va.med.imaging.dicom.common.interfaces.IDicomElement;
import gov.va.med.imaging.dicom.common.interfaces.IIODViolationList;
import gov.va.med.imaging.dicom.dcftoolkit.common.impl.rdsr.RDSRProcessor;
import gov.va.med.imaging.dicom.dcftoolkit.common.mapping.BusinessObjectToDicomSCUTranslator;
import gov.va.med.imaging.dicom.dcftoolkit.common.mapping.DicomToBusinessObjectTranslator;
import gov.va.med.imaging.dicom.dcftoolkit.common.validation.DicomInstanceValidator;
import gov.va.med.imaging.exchange.business.ImagingServiceRequest;
import gov.va.med.imaging.exchange.business.RequestedProcedure;
import gov.va.med.imaging.exchange.business.ScheduledProcedureStep;
import gov.va.med.imaging.exchange.business.dicom.DicomAE;
import gov.va.med.imaging.exchange.business.dicom.DicomCorrectInfo;
import gov.va.med.imaging.exchange.business.dicom.DicomMap;
import gov.va.med.imaging.exchange.business.dicom.DicomRequestParameters;
import gov.va.med.imaging.exchange.business.dicom.DicomServerConfiguration;
import gov.va.med.imaging.exchange.business.dicom.DicomUtils;
import gov.va.med.imaging.exchange.business.dicom.InstanceFile;
import gov.va.med.imaging.exchange.business.dicom.InstrumentConfig;
import gov.va.med.imaging.exchange.business.dicom.MockDicomMap;
import gov.va.med.imaging.exchange.business.dicom.PatientRef;
import gov.va.med.imaging.exchange.business.dicom.PatientStudyInfo;
import gov.va.med.imaging.exchange.business.dicom.ProcedureRef;
import gov.va.med.imaging.exchange.business.dicom.SOPInstance;
import gov.va.med.imaging.exchange.business.dicom.Series;
import gov.va.med.imaging.exchange.business.dicom.StorageCommitElement;
import gov.va.med.imaging.exchange.business.dicom.Study;
import gov.va.med.imaging.exchange.business.dicom.exceptions.DicomException;
import gov.va.med.imaging.exchange.business.dicom.exceptions.IllegalQueryDataException;
import gov.va.med.imaging.exchange.business.dicom.exceptions.UnknownSOPClassException;
import gov.va.med.imaging.exchange.business.dicom.exceptions.ValidateIODException;
import gov.va.med.imaging.exchange.business.dicom.exceptions.ValidateVRException;
import gov.va.med.imaging.exchange.business.dicom.rdsr.Dose;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;

import com.lbs.DCS.AttributeTag;
import com.lbs.DCS.DCM;
import com.lbs.DCS.DCSException;
import com.lbs.DCS.DicomDataDictionary;
import com.lbs.DCS.DicomDataSet;
import com.lbs.DCS.DicomElement;
import com.lbs.DCS.DicomFileOutput;
import com.lbs.DCS.DicomSQElement;
import com.lbs.DCS.DicomSessionSettings;
import com.lbs.DCS.DicomStreamWriter;
import com.lbs.DCS.UID;
import com.lbs.DCS.VRValidator;
import com.lbs.DCS.ValidationError;
import com.lbs.DCS.ValidationErrorList;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public class DicomDataSetImpl implements IDicomDataSet {
    
	private static final String RADIATION_DOSE_SOP_CLASS_UID = "1.2.840.10008.5.1.4.1.1.88.67";

	DicomDataSet dataSet = null;
        
	// Business object references
	PatientRef patientRef = null;
	ProcedureRef procedureRef = null;
	Series series = null;
	Study study = null;
	SOPInstance sopInstance = null;
	ImagingServiceRequest imagingServiceRequest = null;
	RequestedProcedure requestedProcedure = null;
	ScheduledProcedureStep scheduledProcedureStep = null;
	DicomRequestParameters serviceRequestParameters = null;
	HashSet<DicomMap> serviceMappingSet=null;
	HashSet<DicomMap> HISMappingSet = null;
    HashMap<String, String> HISChanges = null;
    String acquisitionSite = null;
    String sourceAET = null;
    String receivedTransferSyntax = null;
    String name = null;
    String affectSOPClass = null;
    String transactionUID = null;
    String aETDefaultServiceType = null;

    private static Logger logger = Logger.getLogger(DicomDataSetImpl.class);
	private static final DicomServerConfiguration config = DicomServerConfiguration.getConfiguration();
	private static final String originalAttributeSequenceTag = "0400,0561";


	/**
     * Constructor
     *
     */
    public DicomDataSetImpl() {
    	this.dataSet = new DicomDataSet();
    }
    
    /**
     * Constructor
     * 
     * @param arg0
     */
    public DicomDataSetImpl(DicomDataSet arg0) {
        this.dataSet = arg0;
    }
    
    
    /* (non-Javadoc)
     * @see gov.va.med.imaging.dicom.dcftoolkit.scu.interfaces.IDicomDataSet#getDicomDataSet()
     */
    public Object getDicomDataSet() {
        //
        return this.dataSet;
    }

    
    /* (non-Javadoc)
     * @see gov.va.med.imaging.dicom.dcftoolkit.scu.interfaces.IDicomDataSet#setDicomDataSet(java.lang.Object)
     */
    public void setDicomDataSet(Object dicomDataSet) {
        this.dataSet = (DicomDataSet) dicomDataSet;
    }
    
        
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.interfaces.DicomDataSet#getDicomElement(java.lang.String)
	 */
	public IDicomElement getDicomElement(String dicomTagName) throws DicomException {
		DicomElementImpl dicomElementImpl = null;
		
		try {
			if (dicomTagName == null)	// don't crash, if incoming tag is null 
				return dicomElementImpl;
            if(this.dataSet.containsElement(new AttributeTag(dicomTagName))){
                DicomElement element = this.dataSet.findElement(new AttributeTag(dicomTagName));
                dicomElementImpl = new DicomElementImpl(element);
            }
		} 
		catch (DCSException dcse) 
		{
            logger.error(dcse.getMessage());
			logger.error(this.getClass().getName()+
                    " Could not retrieve Dicom Element with tag name: " + dicomTagName);
			throw new DicomException("Failure to retrieve DICOM Element.", dcse);
		}
		return dicomElementImpl;
	}

 
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.interfaces.DicomDataSet#getDicomElementValue(java.lang.String, java.lang.String)
	 */
	public String getDicomElementValue(String dicomTagName, String dicomTag2Name)
			throws DicomException {
		String strval = null;

		try {
			if (dicomTagName == null) // don't crash, return null if incoming
				// tag is null
				return strval;
			if (dicomTag2Name == null) // top level element
				strval = this.dataSet.getElementStringValue(new AttributeTag(
						dicomTagName));
			else { // we deal with a sequence element
				DicomSQElement sq_e = (DicomSQElement) this.dataSet
						.findElement(new AttributeTag(dicomTagName));
				if (sq_e != null) {
					// remove element from sequence dataset
					DicomDataSet sq_ds = sq_e.SQ(); // handle 1 sequence element
					// only (for now)
					strval = sq_ds.getElementStringValue(new AttributeTag(
							dicomTag2Name));
				}
			}
		} catch (DCSException dcse) {
			logger.error(dcse.getMessage());
			logger.error(this.getClass().getName()
					+ "Could not get Dicom Element value of tag name: "
					+ dicomTagName
					+ ((dicomTag2Name == null) ? " " : (", " + dicomTag2Name)));
			throw new DicomException("Failure to retrieve DICOM Element value.", dcse);
		}
		if ((strval != null) && (!strval.isEmpty()))
			strval = strval.trim();
		return strval;
	}

	//IMPROVE Change return value to void.  Compile and test to see if something breaks.
	/*
	 * 
	 */
	public IDicomElement insertDicomElement(String dicomTagName, String dicomTag2Name, String value) throws DicomException {
	DicomElementImpl dicomElementImpl = null;
	
	try {
		if (dicomTagName == null)	// don't crash, return null if incoming tag is null 
			return dicomElementImpl;
		if (dicomTag2Name == null){ // top level element
            value = this.formatStringValue(dicomTagName, value);
			this.dataSet.insert(new AttributeTag(dicomTagName), value);
        }
		else {						// we deal with a sequence element
			DicomSQElement sq_e = null;
			if (this.dataSet.containsElement(new AttributeTag(dicomTagName))) {
				sq_e = (DicomSQElement)this.dataSet.findElement(new AttributeTag(dicomTagName));
				DicomDataSet sq_ds = sq_e.SQ(); // handle 1 sequence element only (for now)
                String nuValue = this.formatStringValue(dicomTag2Name, value);
				sq_ds.insert(new AttributeTag(dicomTag2Name), nuValue);
				DicomSQElement nuSequence = new DicomSQElement(new AttributeTag(dicomTagName), sq_ds);
				this.dataSet.insert(nuSequence);
			} 
			else{
                String nuValue = this.formatStringValue(dicomTag2Name, value);
				DicomDataSet sq_ds = new DicomDataSet();
				sq_ds.insert(new AttributeTag(dicomTag2Name), nuValue);
				sq_e = new DicomSQElement(new AttributeTag(dicomTagName), sq_ds);                
				this.dataSet.insert(sq_e);
			}
		}
	}
	catch (DCSException dcse) 
	{
        logger.error(dcse.getMessage());
		logger.error(this.getClass().getName()+
                "Could not insert Dicom Element with tag name: "
				+ dicomTagName + ((dicomTag2Name==null)?" ":(", " + dicomTag2Name)));
		throw new DicomException("Failure to insert DICOM Element.", dcse);
	}
		return dicomElementImpl;
	}
	
    /* (non-Javadoc)
     * @see gov.va.med.imaging.dicom.dcftoolkit.scu.interfaces.IDicomDataSet#removeDicomElement(java.lang.String, java.lang.String)
     */
    public boolean removeDicomElement(String dicomTagName, String dicomTag2Name)
            throws DicomException {
		boolean removed=false;
		
		try {
			if (dicomTagName == null)	// don't crash, return null if incoming tag is null 
				return removed;
			if (dicomTag2Name == null)  // top level element
				removed = this.dataSet.removeElement(new AttributeTag(dicomTagName));
			else {						// we deal with a sequence element
				DicomSQElement sq_e = (DicomSQElement)this.dataSet.findElement(new AttributeTag(dicomTagName));
				if (sq_e!=null) {
					// remove element from sequence dataset
					DicomDataSet sq_ds = sq_e.SQ(); // handle 1 sequence element only (for now)
					removed = sq_ds.removeElement(new AttributeTag(dicomTag2Name));
				}
			}
		}
		catch (DCSException dcse) 
		{
            logger.error(dcse.getMessage());
			logger.error(this.getClass().getName()+
                    "Could not remove Dicom Element with tag name: "
					+ dicomTagName + ((dicomTag2Name==null)?" ":(", " + dicomTag2Name)));
			throw new DicomException("Failure to remove DICOM Element.", dcse);
		}
		return removed;
	}
    
    /*
     * (non-Javadoc)
     * @see gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet#containsDicomElement(java.lang.String, java.lang.String)
     */
	public boolean containsDicomElement(String dicomTagName, String dicomTag2Name){
		boolean elementExist = false;
		try {
			if (dicomTagName == null){	// don't crash, return null if incoming tag is null 
				throw new DCSException("Tag is null");
			}
			if(this.dataSet.containsElement(new AttributeTag(dicomTagName))){
				if (dicomTag2Name == null){	// top level element found
					elementExist = true;
				} else { 					// we deal with a sequence element
					DicomSQElement sq_e = (DicomSQElement)this.dataSet.findElement(new AttributeTag(dicomTagName));
					if (sq_e==null)	// no sequence element found
						elementExist = false;
					else {
						// remove element from sequence dataset
						DicomDataSet sq_ds = sq_e.SQ(); // handle 1 sequence element only (for now)
						elementExist = sq_ds.containsElement(new AttributeTag(dicomTag2Name));
					}
				}
			}
			else{
				elementExist = false;
			}
		}
		catch (DCSException dcse) 
		{
            logger.error(dcse.getMessage());
			logger.error(this.getClass().getName()+
                    "Could not determine if Dicom Element exist with tag name(s): " + dicomTagName + ((dicomTag2Name==null)?"  null":dicomTag2Name));
			elementExist = false;
		}
		return elementExist;
	}

    /*
     * (non-Javadoc)
     * @see gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet#insertAndRecordNewValue(java.lang.String, java.lang.String, java.lang.String, java.lang.boolean)
     */
	public boolean insertAndRecordNewValue (String tag, String tag2, String value, boolean ignoreNew)
	{
		// check if value is new
		String tagValue=null;
		try {
			if (this.containsDicomElement(tag, tag2))
				tagValue = this.getDicomElementValue(tag, tag2);	// fetch old value
			else {
				ignoreNew = true;									// force insert of new value
				tagValue = "";										// set old value to empty String
			}
			if (ignoreNew || ((tagValue != null) && (!tagValue.equals(value))) ) {
				
				// do insert old value to original sequence and value to dataset
				try {
					if (tag2==null) {
						this.insertDicomElement(originalAttributeSequenceTag, tag, tagValue);
					} 
					else {	// sequence tag
						DicomDataSet dataSet = null;
						DicomDataSet sq_ds = null; // orig SQ dataset
						dataSet = (DicomDataSet)this.getDicomDataSet();
						DicomSQElement origSQ_e = (DicomSQElement) this.dataSet.findElement(new AttributeTag(originalAttributeSequenceTag));
						if (origSQ_e == null) { // insert orig SQ with old tag value in belly to parent dataset 
							sq_ds = new DicomDataSet();
			                sq_ds.insert(new AttributeTag(tag), tagValue);
			                origSQ_e = new DicomSQElement(new AttributeTag(originalAttributeSequenceTag), sq_ds);
			                dataSet.insert(origSQ_e);
						} else { 		// orig SQ already exists
							sq_ds = origSQ_e.SQ(); 
							if (tag2==null) { // insert old element to orig SQ
								sq_ds.insert(new AttributeTag(tag), tagValue);
							} else { 		// find/create old SQ in orig SQ
								DicomSQElement childSQ_e = getChildSequenceElement(tag, sq_ds);
								DicomDataSet sq_ds2 = null; // child SQ dataset
								if (childSQ_e == null) { // create child SQ with old element in belly and add child SQ to orig SQ
									sq_ds2 = new DicomDataSet(); 
					                sq_ds2.insert(new AttributeTag(tag2), tagValue);
					                childSQ_e = new DicomSQElement(new AttributeTag(tag), sq_ds2);
					                sq_ds.insert(childSQ_e);
								} else {				// insert old SQ element to orig SQ's child SQ
									sq_ds2 = childSQ_e.SQ();
									sq_ds2.insert(new AttributeTag(tag2), tagValue);	
								}
								
							}
						}
					}					
				} catch (DCSException dcse) {
					// do high level log only, Detailed Error logged on lower level
					logger.error("Inserting (" + tag + ") with value " + tagValue + " to Original Attribute sequence Failed ");
				}
				
				try {
					//now insert the new value
					this.insertDicomElement(tag, tag2, value);
				} catch (DicomException de) {
					// do high level log only, Detailed Error logged on lower level
					logger.error("Inserting (" + tag + ") with value " + value + " to dataset Failed ");

				}
			}
		} catch (DicomException de) {
			logger.error("Getting (" + tag + ") from dataset for check/update failed ");
		}
		return false;
	}

	private DicomSQElement getChildSequenceElement(String tag, DicomDataSet sq_ds) throws DCSException 
	{
		try
		{
			return (DicomSQElement) sq_ds.findElement(new AttributeTag(tag));
		}
		catch (DCSException e)
		{
			
		}
		return null;
	}


	/*
	 * 
	 */
    public HashSet<DicomMap> getHISMappingSet() {
    	if (this.HISMappingSet == null) {
    		this.HISMappingSet = getHISPropertyMappingSet(); 
    	}
    	return this.HISMappingSet;
    }

    /*
     * (non-Javadoc)
     * @see gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet#setQueryMappingSet(java.util.HashSet)
     */
    public void setQueryMappingSet(HashSet<DicomMap> mappingSet) {
        this.serviceMappingSet = mappingSet;
    }

    /*
     * (non-Javadoc)
     * @see gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet#getQueryMappingSet()
     */
    public HashSet<DicomMap> getQueryMappingSet() {
		return this.serviceMappingSet;
	}	

	/* (non-Javadoc)
     * @see gov.va.med.imaging.dicom.dcftoolkit.scu.interfaces.IDicomDataSet#getStudyInstanceUID()
     */
    public String getStudyInstanceUID() {
        String studyInstanceUID = null;
        try{
            studyInstanceUID = this.dataSet.getElementStringValue(DCM.E_STUDY_INSTANCE_UID);
        }
        catch(DCSException dcsX){
            return null;
        }
        return studyInstanceUID;
    }

    /*
     * (non-Javadoc)
     * @see gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet#setStudyInstanceUID(java.lang.String)
     */
	@Override
	public void setStudyInstanceUID(String studyInstanceUID)
	{
        try{
        	insertDicomElement("0020,000d", null, studyInstanceUID);
        }
        catch(DicomException dcsX){
        	logger.error("Couldn't insert studyInstanceUID into the dataSet");
        }	
	}
	
    /* (non-Javadoc)
     * @see gov.va.med.imaging.dicom.dcftoolkit.scu.interfaces.IDicomDataSet#getSeriesInstanceUID()
     */
    public String getSeriesInstanceUID() {
        String seriesInstanceUID = null;
        try{
            seriesInstanceUID = this.dataSet.getElementStringValue(DCM.E_SERIES_INSTANCE_UID);
        }
        catch(DCSException dcsX){
            return null;
        }
        return seriesInstanceUID;
    }

    /*
     * (non-Javadoc)
     * @see gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet#setSeriesInstanceUID(java.lang.String)
     */
	@Override
	public void setSeriesInstanceUID(String seriesInstanceUID)
	{
        try{
        	insertDicomElement("0020,000e", null, seriesInstanceUID);
        }
        catch(DicomException dcsX){
           	logger.error("Couldn't insert seriesInstanceUID into the dataSet");
        }		
	}

    /* (non-Javadoc)
     * @see gov.va.med.imaging.dicom.dcftoolkit.scu.interfaces.IDicomDataSet#getSOPInstanceUID()
     */
    public String getSOPInstanceUID() {
        String sopInstanceUID = null;
        try{
            sopInstanceUID = this.dataSet.getElementStringValue(DCM.E_SOPINSTANCE_UID);
        }
        catch(DCSException dcsX){
            return null;
        }
        return sopInstanceUID;
    }
    
    /*
     * (non-Javadoc)
     * @see gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet#setSOPInstanceUID(java.lang.String)
     */
	@Override
	public void setSOPInstanceUID(String sopInstanceUID)
	{
        try{
        	insertDicomElement("0008,0018", null, sopInstanceUID);
        }
        catch(DicomException dcsX){
           	logger.error("Couldn't insert sopInstanceUID into the dataSet");
       }	
	}
	
    /* (non-Javadoc)
     * @see gov.va.med.imaging.dicom.dcftoolkit.scu.interfaces.IDicomDataSet#getSeriesInstanceUID(java.lang.String)
     */
    public boolean getStudyInstanceUID(String studyInstanceUID) {
        return false;
    }
    
    /* (non-Javadoc)
     * @see gov.va.med.imaging.dicom.dcftoolkit.scu.interfaces.IDicomDataSet#getSeriesInstanceUID(java.lang.String)
     */
    public boolean getSeriesInstanceUID(String seriesInstanceUID) {
        return false;
    }
    
    /* (non-Javadoc)
     * @see gov.va.med.imaging.dicom.dcftoolkit.scu.interfaces.IDicomDataSet#getSOPInstanceUID(java.lang.String)
     */
    public boolean getSOPInstanceUID(String sopInstanceUID) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet#updateHISChangesToDDS(java.util.HashMap)
     */
    public void updateHISChangesToDDS(HashMap<String, String> HISChanges)throws DicomException{
        
        this.HISChanges = HISChanges;
        if(this.HISChanges != null){
            this.HISMappingSet = this.getHISMappingSet();
            sanitizeHISChanges(HISChanges);
            BusinessObjectToDicomSCUTranslator.updateDataSetFromHISChanges(this, this.HISChanges, 
                    this.HISMappingSet);
        }
    }
   
    /* (non-Javadoc)
     * @see gov.va.med.imaging.dicom.dcftoolkit.scu.interfaces.IDicomDataSet#getAcquisitionSite()
     */
    public String getAcquisitionSite() {
        return this.acquisitionSite;
    }
    
    /* (non-Javadoc)
     * @see gov.va.med.imaging.dicom.dcftoolkit.scu.interfaces.IDicomDataSet#setAcquisitionSite(java.lang.String)
     */
    public void setAcquisitionSite(String site) {
        this.acquisitionSite = site;
    }
    
    /*
     * (non-Javadoc)
     * @see gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet#getSOPClass()
     */
	public String getSOPClass(){
        String sopClass = null;
        try{
            sopClass = this.dataSet.getElementStringValue(DCM.E_SOPCLASS_UID);
        }
        catch(DCSException dcsX){
            return null;
        }
        return sopClass;
	}
    

	
    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet#getManufacturer()
	 */
	@Override
	public String getManufacturer() {
        String manufacturersName = null;
        try{
        	manufacturersName = this.dataSet.getElementStringValue(DCM.E_MANUFACTURER);
        }
        catch(DCSException dcsX){
            return null;
        }
        return manufacturersName;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet#getModelName()
	 */
	@Override
	public String getModelName() {
        String modelName = null;
        try{
        	modelName = this.dataSet.getElementStringValue(DCM.E_MANUFACTURERS_MODEL_NAME);
        }
        catch(DCSException dcsX){
            return null;
        }
        return modelName;
	}

	public byte[] part10Buffer(boolean updateOnly) {

    	byte[] data = null;

    	try {
    		String outputTS=UID.TRANSFERLITTLEENDIAN; // Little Endian Implicit VR (default)
    		if ( updateOnly ) { 
    			String inputTS=this.dataSet.getElementStringValue(DCM.E_TRANSFER_SYNTAX_UID);
    			if (inputTS.startsWith("1.2.840.10008.1.2.4")) {
    				// for Updates only, do not decompress already compressed DICOM Objects
    				outputTS=inputTS;
    			}
    		}
    		DicomSessionSettings ss = new DicomSessionSettings();
            // ss.setStreamModeBufferSize(ss.getMaxWritePduSize());
	    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    	DicomStreamWriter writer = new DicomStreamWriter( bos, ss );
	    	DicomFileOutput dfo = new DicomFileOutput(
	                                writer,
	                                outputTS,
	                                true, true, ss );
	    	dfo.open();
	    	// this.dataSet.expandStreamingModeData(true);
//			logger.warn("TXT+TGA DataSet Element Count = " + this.dataSet.count()); // +
			//			"; hashcode = " + this.dataSet.hashCode());
	    	dfo.writeDataSet( this.dataSet );
	    	data = bos.toByteArray();
	    	bos.flush();
	    	// dfo.close();
	    	return (data);
        }
        catch(DCSException dcse){
            return data;
        }
        catch(IOException ioe){
            return data;
        }
    }
    
		
    /*
     * (non-Javadoc)
     * @see gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet#createPatientQueryRequestParameters()
     */
    public DicomRequestParameters createPatientQueryRequestParameters()
	throws IllegalQueryDataException // -- from getrequestFromDataSet
	{
		// Return the the query request from the DCF DicomDataSet in non-DICOM format
		if (this.serviceMappingSet == null){
			this.serviceMappingSet = new HashSet<DicomMap>();
		}
		else{
			this.serviceMappingSet.clear();
		}

		HashSet<DicomMap> tempMappingSet = getFindPropertyMappingSet(); 
		Iterator<DicomMap> iter = tempMappingSet.iterator();
		while (iter.hasNext()){
			DicomMap mapping = iter.next();
			try{
				if(this.dataSet.containsElement(new AttributeTag(mapping.getTag()))){
					this.serviceMappingSet.add(mapping);
				}
			}
			catch(DCSException dcsX){
				//do nothing
			}
		}

		this.serviceRequestParameters = DicomToBusinessObjectTranslator.getRequestFromDataSet(
									this,
									this.serviceMappingSet);
		return this.serviceRequestParameters;
	}

	/*
	 * (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet#createStudyQueryRequestParameters()
	 */
	public DicomRequestParameters createStudyQueryRequestParameters()
			throws IllegalQueryDataException
	{
		// Return the query request from the DCF DicomDataSet in non-DICOM format
		if (this.serviceMappingSet == null){
			this.serviceMappingSet = new HashSet<DicomMap>();
		}
		else{
			this.serviceMappingSet.clear();
		}

		HashSet<DicomMap> tempMappingSet = getFindPropertyMappingSet(); 
		Iterator<DicomMap> iter = tempMappingSet.iterator();
		while (iter.hasNext()){
			DicomMap mapping = iter.next();
			try{
				if(this.dataSet.containsElement(new AttributeTag(mapping.getTag()))){
					this.serviceMappingSet.add(mapping);
				}
			}
			catch(DCSException dcsX){
				//do nothing
			}
		}
		
		this.serviceRequestParameters = DicomToBusinessObjectTranslator.getRequestFromDataSet(
									this,
									this.serviceMappingSet);
		return this.serviceRequestParameters;
	}

	/*
	 * (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet#createMoveRequestParameters()
	 */
	public DicomRequestParameters createMoveRequestParameters()
	throws IllegalQueryDataException // -- from getrequestFromDataSet
	{
		// Return the Move request from the DCF DicomDataSet in non-DICOM format (feedable to persistence)
		this.serviceMappingSet = getMovePropertyMappingSet();
		
		this.serviceRequestParameters = DicomToBusinessObjectTranslator.getRequestFromDataSet(
					this,
					this.serviceMappingSet);
		return this.serviceRequestParameters;
	}

	/*
	 * (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet#getPartialDicomCorrectInfo()
	 */
	public DicomCorrectInfo getPartialDicomCorrectInfo(){
		
        String patientName = null;
        String nonDCMPatientName = null;
        String patientID = null;
        String accessionNumber = null;
        String caseNumber = null;
        String serviceType = null;
        String studyInstanceUID = null;
        String sopInstanceUID = null;
        
        try{
            patientName = this.dataSet.getElementStringValue(DCM.E_PATIENTS_NAME);
            if(patientName != null){
            	patientName = patientName.trim();
            	nonDCMPatientName = patientName.toString();
            }
        }
        catch(DCSException dcsX){
            logger.warn(dcsX.getMessage());
			logger.warn(this.getClass().getName()+": Could not retrieve Patient Name Dicom Element");
        }
        try{
            patientID = this.dataSet.getElementStringValue(DCM.E_PATIENT_ID);
            if(patientID != null){
            	patientID = patientID.trim();
            }
        }
        catch(DCSException dcsX){
            logger.warn(dcsX.getMessage());
			logger.warn(this.getClass().getName()+": Could not retrieve Patient ID Dicom Element");
        }
        try{
            accessionNumber = this.dataSet.getElementStringValue(DCM.E_ACCESSION_NUMBER);
            if(accessionNumber != null){
            	accessionNumber = accessionNumber.trim();
            	caseNumber = accessionNumber;
            }
        }
        catch(DCSException dcsX){
            logger.warn(dcsX.getMessage());
			logger.warn(this.getClass().getName()+": Could not retrieve Accession Number Dicom Element");
        }
        try{
            studyInstanceUID = this.dataSet.getElementStringValue(DCM.E_STUDY_INSTANCE_UID);
            if(studyInstanceUID != null){
            	studyInstanceUID = studyInstanceUID.trim();
            }
        }
        catch(DCSException dcsX){
            logger.warn(dcsX.getMessage());
			logger.warn(this.getClass().getName()+": Could not retrieve Study Instance UID Dicom Element");
        }
        try{
            sopInstanceUID = this.dataSet.getElementStringValue(DCM.E_SOPINSTANCE_UID);
            if(sopInstanceUID != null){
            	sopInstanceUID = sopInstanceUID.trim();
            }
        }
        catch(DCSException dcsX){
            logger.warn(dcsX.getMessage());
			logger.warn(this.getClass().getName()+": Could not retrieve SOP Instance UID Dicom Element");
        }
				
		DicomCorrectInfo dicomCorrectInfo = new DicomCorrectInfo(patientID, patientName, nonDCMPatientName, caseNumber, accessionNumber, 
								sopInstanceUID, studyInstanceUID);
		return dicomCorrectInfo;
	}

	
	/*
	 * (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet#validateIOD()
	 */
	//@Override
	public IIODViolationList validateIOD() throws ValidateIODException, UnknownSOPClassException {
    	
		IODViolationListImpl violationList = null;
		try{
    		violationList = DicomInstanceValidator.getInstance().validate(this.dataSet);
    	}
    	catch(ParserConfigurationException pcX){
    		logger.error(this.getClass().getName()+": Parser Configuration Exception: /n"+pcX.getMessage());
    		throw new ValidateIODException("IOD Validation Parser problem.");
    	}
    	catch(TransformerException tX){
    		logger.error(this.getClass().getName()+": Transformer Exception: /n"+tX.getMessage());
    		throw new ValidateIODException("IOD Validation Transformer problem.");
    	}
    	catch(UnsupportedEncodingException ueX){
    		logger.error(this.getClass().getName()+": Unsupported Encoding Exception: /n"+ueX.getMessage());
    		throw new ValidateIODException("IOD Validation Unsupported Encoding problem.");
    	}
    	return (IIODViolationList)violationList;
	}
	
	/*
	 * (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet#validateVR()
	 */
    public void validateVR()
    	throws ValidateVRException{
    	
    	VRValidator validator = VRValidator.instance();
    	ValidationErrorList list = validator.validateDataSet(this.dataSet);
    	if(list.hasErrors()){
    		int errorCount = list.getErrorCount();
    		Vector<ValidationError> vectorList = list.getErrorVector();
    		Iterator<ValidationError> iter = vectorList.iterator();
    		while(iter.hasNext()){
    			ValidationError error = (ValidationError)iter.next();
    			DicomElement element = error.getElement();
    			if(element.length() == 0){
    				errorCount--;
    			}
    		}
    		if(errorCount > 0){
    			logger.error("The DICOMDataSet has the following VR errors. ");
    			logger.error(list.toString());
    			throw new ValidateVRException();    				
    		}
    	}
    }	

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.interfaces.DicomDataSet#getPatientRef()
	 */
	public PatientRef getPatientRef() {
	   	String creatingEntity = ""; // local siteID, stationID or divisionID -- the creating (DB) entity of the ID
		if (config != null)
			creatingEntity = config.getSiteId(); // this is the Station# not the IEN in institution file!)
		
		return new PatientRef("", "V", creatingEntity, "D"); // DFN value will be resolved after Patient Lookup
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.interfaces.DicomDataSet#getProcedureRef()
	 */
	public ProcedureRef getProcedureRef(InstrumentConfig instrument) {
		ProcedureRef procRef = new ProcedureRef (null, null, null, null, null, null);
		String accessionNumber = getStringValueFromElement("0008,0050", ""); // if no ACC# we don't add to new DB anyway, But DICOM Correct
		procRef.setDicomAccessionNumber(accessionNumber); // this is the in-processing ACC# from header! not from ProcRef domain!
		procRef.setProcedureID(accessionNumber);
		procRef.setAssigningAuthority("V");
	   	String creatingEntity = ""; // local siteID, stationID or divisionID -- the creating (DB) entity of the ID
		if (config != null)
			creatingEntity = config.getSiteId(); // this is the Station# not the IEN in institution file!)
		procRef.setCreatingEntity(creatingEntity);
	   	
		if(instrument.isConsultInstrument())
		{
    		procRef.setPackageIX("CONS");
    	}
    	else
    	{ 
    		// TODO: Check for other entities based on ACC# format?
    		procRef.setPackageIX(instrument.getService());
    	}

		procRef.setProcedureIDType(instrument.getService());

		String stdTime = getStringValueFromElement("0040,2005", ""); // Imaging Service Request Issue Time
		if (!stdTime.isEmpty())
			stdTime = "." + stdTime;
		String procDT = (getStringValueFromElement("0040,2004", "") + stdTime); // ISR Issue Date&Time
		if (procDT.isEmpty()) {
			stdTime = getStringValueFromElement("0008,0030", ""); // study time
			if (!stdTime.isEmpty())
				stdTime = "." + stdTime;
			procDT = (getStringValueFromElement("0008,0020", "") + stdTime); // study Date&Time

		}
		procRef.setProcedureExamDateTime(procDT);
		
		return procRef;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.interfaces.DicomDataSet#getStudy()
	 */
	public Study getStudy() {
		// Return the Study using the data from the DCF DicomDataSet
		String stdDate = getStringValueFromElement("0008,0020", "");
		String stdTime = getStringValueFromElement("0008,0030", "");
		String stdTimeWithSeparator = stdTime;
		if (!stdTimeWithSeparator.isEmpty())
			stdTimeWithSeparator = "." + stdTimeWithSeparator;
		
		Study study = new Study(
				getStringValueFromElement("0020,000d", ""), // studyIUID -- needs optional Fill-in after Study UID Check!
				"", // originalStudyIUID,-- needs optional Fill-in after Study UID Check!
				getStringValueFromElement("0020,0010", ""), // studyID
				getStringValueFromElement("0008,1030", ""), // description
				getStringValueFromElement("0008,0061", ""), // modalitiesInStudy
				(getStringValueFromElement("0008,0020", "") + stdTimeWithSeparator), // studyDateTime
				getStringValueFromElement("0040,1002", ""), // reasonForStudy
				"A", // Acq. in progress ("A"), partially complete ("P") or completed ("C")
				"V", // originIX = 'V'A / 'D'OD / 'F'EE / 'O'ther
				getStringValueFromElement("0040,1003", "R"), // priority ‘S'TAT, ‘R'OUTINE, 'L'OW or 'H'IGH
				getStringValueFromElement("0008,0050", "") // Accession Number for Pete
				);
		study.setStudyDate(stdDate);
		study.setStudyTime(stdTime);
		study.setReferringPhysician(DicomUtils.reformatDicomName(getStringValueFromElement("0008,0090", "")));
		
		return study;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.interfaces.DicomDataSet#getSeries()
	 */
	public Series getSeries(DicomAE dicomAE,InstrumentConfig instrument) {
		// Return the Series using the data from the DCF DicomDataSet
		String serTime = getStringValueFromElement("0008,0031", "");
		String remoteAETitle = (dicomAE != null) ? dicomAE.getRemoteAETitle() : "";
		String localAETitle = (dicomAE != null) ? dicomAE.getLocalAETitle() : "";
		String acqLocation = (instrument != null) ? instrument.getSiteId() : DicomServerConfiguration.getConfiguration().getSiteId();
		if (!serTime.isEmpty())
			serTime = "." + serTime;
		Series series = new Series(
				getStringValueFromElement("0020,000e", ""), // seriesIUID -- needs optional Fill-in after Series UID Check!
				"", // originalSeriesIUID, -- needs optional Fill-in after Series UID Check!
				getStringValueFromElement("0020,0011", ""), // serNum
				getStringValueFromElement("0008,103e", ""), // series description
				getStringValueFromElement("0008,0060", ""), // modality
				getStringValueFromElement("0008,0015", ""), // bodypart
				//The purpose of this is to assign the Site Number of where the DICOM object came from.  This is based on the 
				// InstrumentConfig object.  We added it to override the HDIG's Site Number.
				acqLocation, // acqSite
				(getStringValueFromElement("0008,0021", "") + serTime), // series Date.Time
				getStringValueFromElement("0008,0070", ""), // series Creator -- Manufacturer
				getStringValueFromElement("0008,1090", ""), // series Creater Device Model -- Manuf. Model
				getStringValueFromElement("0020,0052", ""), // Frame Of Reference UID
				getStringValueFromElement("0020,0060", ""), // laterality
				getStringValueFromElement("0018,5100", ""), // Spatial Position
				remoteAETitle,
				localAETitle,
				"1", // "DICOM Storage", vIAcqEntryPoint
			    "0", // IODViolationDetected - none
			    "CLIN",
			    "",  // procEventIX
			    "",  // specSubSpecIX
			    ""); // tiuNoteReference
		
		// New fields for DICOM Importer
		series.setFacility(getStringValueFromElement("0008,0080", ""));
		series.setInstitutionAddress(getStringValueFromElement("0008,0081", ""));
		
		return series;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.interfaces.DicomDataSet#getSOPInstance()
	 */
 	public SOPInstance getSOPInstance() {
		// Return the Instance using the data in the DCF DicomDataSet
		String sopTime = getStringValueFromElement("0008,0033", "");
		if (!sopTime.isEmpty())
			sopTime = "." + sopTime;
		SOPInstance sopInstance = new SOPInstance(
			getStringValueFromElement("0008,0018", ""), // SOPInstanceUID -- needs optional Fill-in after SOP UID Check!
	 		"", // originalSOPInstanceUID -- needs optional Fill-in after SOP UID Check!
	 		getStringValueFromElement("0008,0016", ""), // SOPClassUID
	 		"IMAGE", // typeIX by natural key.
	 		"", // image description
	 		(getStringValueFromElement("0008,0023", "") + sopTime), // acqDateTime
	 		getStringValueFromElement("0020,0012", ""), // acqNumber
	 		getStringValueFromElement("0020,0013", ""), // instanceNumber
	 		"", // is Key image? - 1 or 0
	 		"", // image Not OK? - 1 or 0
	 		getStringValueFromElement("0018,0010", ""), // contrastBolusAgent
	 		getStringValueFromElement("0020,0032", ""), // imagePosition
	 		getStringValueFromElement("0020,0037", ""), // imageOrientation
	 		getStringValueFromElement("0020,0020", ""), // patientOrientation
	 		getStringValueFromElement("0028,0004", ""), // photometricInterptation
	 		getStringValueFromElement("0028,0008", ""), // numberOfFrames
	 		getStringValueFromElement("0028,0010", ""), // rows
	 		getStringValueFromElement("0028,0011", ""), // columns
	 		getStringValueFromElement("0028,0030", ""), // pixelSpacing
	 		getStringValueFromElement("0028,0002", ""), // samplesPerPixel
	 		getStringValueFromElement("0028,0100", ""), // bitsAllocated
	 		getStringValueFromElement("0028,0101", ""), // bitsStored
	 		getStringValueFromElement("0028,0102", ""), // highBit
	 		getStringValueFromElement("0028,0103", ""), // pixelRepresentation
	 		getStringValueFromElement("0028,1052", ""), // rescaleIntercept
	 		getStringValueFromElement("0028,1053", ""), // rescaleSlope
	 		getStringValueFromElement("0028,1050", ""), // windowCenter 
	 		getStringValueFromElement("0028,1051", ""), // windowWidth
	 		getStringValueFromElement("0018,1004", ""), // plateID
	 		getStringValueFromElement("0018,0050", ""), // sliceThickness
	 		getStringValueFromElement("0018,1100", ""), // reconstructionDiameter
	 		getStringValueFromElement("0018,0020", ""), // scanningSequence 
	 		getStringValueFromElement("0018,0021", ""), // scanningVariant 
	 		getStringValueFromElement("0018,0023", ""), // mrAcqType
	 		getStringValueFromElement("0040,0556", ""), // acqContextDescription
	 		getStringValueFromElement("0018,0080", ""), // repetitionTime
	 		getStringValueFromElement("0018,0081", ""), // echoTime
	 		(getStringValueFromElement("0018,1150", "") + "\\" + getStringValueFromElement("0018,1155", "")), // referencedSOPInstances
	 		"0018,1150\\0018,1155",
	 		"", // MicroscopicObjective
			"", // lab Slice Id
		    "", // histological Stain n/a for RAD studies
	 		getStringValueFromElement("0020,0062", "") // imageLaterality (L, R, B or U)			
		);
		
		// New fields for DICOM Importer
		sopInstance.setTransferSyntaxUid(getStringValueFromElement("0004,1512", ""));
 		return sopInstance;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.interfaces.DicomDataSet#getInstanceFile()
	 */
 	public InstanceFile getInstanceFile() {
		InstanceFile instanceFile = new InstanceFile(
				"", // ArtifactToken
				"0", // isConfidential
				"1", // isOriginal
				"", // deleteDateTime in the format YYYMMDD.HHMMSS
				"", // deletedBy
				"", // deleteReason
				getStringValueFromElement("0008,0008", ""), // imageType
				getStringValueFromElement("0008,2111", ""), // derivationDesc
				getStringValueFromElement("0008,2112", ""), // compressionRatio
				getStringValueFromElement("0008,2114", ""),  // compressionMethod
				0);
 		return instanceFile;
	}

 	/*
 	 * (non-Javadoc)
 	 * @see gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet#getSourceAET()
 	 */
 	public String getSourceAET() {
		return sourceAET;
	}

 	/*
 	 * (non-Javadoc)
 	 * @see gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet#setSourceAET(java.lang.String)
 	 */
 	public void setSourceAET(String sourceAET) {
		if(sourceAET != null){
			sourceAET = sourceAET.trim();
		}
		this.sourceAET = sourceAET;
	}

 	/*
 	 * (non-Javadoc)
 	 * @see gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet#getReceivedTransferSyntax()
 	 */
 	public String getReceivedTransferSyntax() {
		return receivedTransferSyntax;
	}

 	/*
 	 * (non-Javadoc)
 	 * @see gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet#setReceivedTransferSyntax(java.lang.String)
 	 */
 	public void setReceivedTransferSyntax(String receivedTransferSyntax) {
		if(receivedTransferSyntax != null){
			receivedTransferSyntax = receivedTransferSyntax.trim();
		}
		this.receivedTransferSyntax = receivedTransferSyntax;
	}
	
	//CODECR #IMAG00000440 - SSN Format issue.
 	/*
 	 * 
 	 */
    public void changeDataPresentation(){

		if(DicomServerConfiguration.getConfiguration().isFormatPatientIDwithDashes()){
			try{
				if(dataSet.containsElement(DCM.E_PATIENT_ID)){
					String patientID = dataSet.getElementStringValue(DCM.E_PATIENT_ID, "");
					if(!patientID.equals("")){
						String out = StringUtil.formatPatientIDWithDashes(patientID);
						dataSet.insert(DCM.E_PATIENT_ID, out);
					}
				}
			}
			catch(DCSException dX){
				logger.error("Failed to re-format the Patient ID.");
			}
		}
    }
    
    /*
     * (non-Javadoc)
     * @see gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet#getQueryRetrieveLevel()
     */
    public String getQueryRetrieveLevel(){
        String qrLevel = this.dataSet.getElementStringValue( DCM.E_QUERYRETRIEVE_LEVEL, "" );
        if(qrLevel != null){
        	qrLevel = qrLevel.trim();
        }
        return qrLevel;
    }

    /*
     *     (non-Javadoc)
     * @see gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet#getPatientStudyInfo(gov.va.med.imaging.exchange.business.dicom.InstrumentConfig)
     */
	@Override
	public PatientStudyInfo getPatientStudyInfo(InstrumentConfig instrument) throws DicomException {
		
		PatientStudyInfo patientStudyInfo = new PatientStudyInfo();

		// Extract values from DICOM data set
		String accessionNumber = getStringValueFromElement("0008,0050", "");

		String patientName = getStringValueFromElement("0010,0010", "");
		String patientId = getStringValueFromElement("0010,0020", "");
		String patientBirthDate = getStringValueFromElement("0010,0030", "");
		String patientSex = getStringValueFromElement("0010,0040", "");
		String patientICN = "";

		// Set properties on patientStudyInfo object
		patientStudyInfo.setStudyAccessionNumber(accessionNumber);
		patientStudyInfo.setStudyImagingService(instrument.getService());
		patientStudyInfo.setPatientName(patientName);
		patientStudyInfo.setPatientID(patientId);
		patientStudyInfo.setPatientBirthDate(patientBirthDate);
		patientStudyInfo.setPatientSex(patientSex);
		patientStudyInfo.setPatientICN(patientICN);
		
		return patientStudyInfo;
	}
    
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet#getAffectedSOPClass()
	 */
	@Override
	public String getAffectedSOPClass() {
        if(this.affectSOPClass == null){
			try{
	            this.affectSOPClass = this.dataSet.getElementStringValue(DCM.E_AFFECTED_SOPCLASS_UID);
	        }
	        catch(DCSException dcsX){
	            return null;
	        }
        }
        return this.affectSOPClass;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet#setAffectedSOPClass(java.lang.String)
	 */
	@Override
	public void setAffectedSOPClass(String sopClass) {
		this.affectSOPClass = sopClass;
	}

    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet#setServiceType(java.lang.String)
	 */
	@Override
	public void setAETDefaultServiceType(String type) {
		this.aETDefaultServiceType = type;
		
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet#getServiceType()
	 */
	@Override
	public String getAETDefaultServiceType() {
		return this.aETDefaultServiceType;
	}

	/*
	 * (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet#insertToOriginalAttributeSequence(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void insertToOriginalAttributeSequence(String tag, String tagValue, String context) {
		try {
			insertDicomElement(originalAttributeSequenceTag, tag, tagValue);
		} catch (DicomException de) {
	        logger.error("Inserting (" + tag + ") with UID " + tagValue + " to Original Attribute sequence Failed " + context);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet#insertToOriginalAttributeSequence(java.lang.String, java.lang.String)
	 */
	public void insertToOriginalAttributeSequence(String tag, String context) {
		try {
			insertDicomElement(originalAttributeSequenceTag, tag, getStringValueFromElement(tag, null));
		} catch (DicomException de) {
	        logger.error("Inserting (" + tag + ") to Original Attribute sequence Failed " + context);
		}
	}

	public String getTransactionUID(){
        if(this.transactionUID == null){
			try{
	            this.transactionUID = this.dataSet.getElementStringValue(DCM.E_TRANSACTION_UID);
	        }
	        catch(DCSException dcsX){
	            return null;
	        }
        }
        return this.transactionUID;
	}

	/*
	 * this method inserts DICOM SOP Class/Instance element pairs into a storage commitment structure.
	 * This sequence is used to keep track of Storage Commitment requests until a response is delivered to the sender.
	 */
	public List<StorageCommitElement> getRefSOPUIDList() {
		List<StorageCommitElement> sCEs= new ArrayList<StorageCommitElement>();
		sCEs.clear();
		
		int i=0;
		try {
			DicomSQElement sq_e = (DicomSQElement) this.dataSet.findElement(DCM.E_REFERENCED_SOP_SEQUENCE);
			if (sq_e != null) {
				// extract elements from sequence dataset
				for (i=0; ; i++) { // took (back) out correct looking code: "i<sq_e.length()" -- did not fill list!!!
					DicomDataSet sq_ds = sq_e.SQ(i); // handle sequence elements
					StorageCommitElement sCE = new StorageCommitElement();
					sCE.setSopClassUid(sq_ds.getElementStringValue(DCM.E_REFERENCED_SOPCLASS_UID));
					sCE.setSOPInstanceUID(sq_ds.getElementStringValue(DCM.E_REFERENCED_SOPINSTANCE_UID));
					sCEs.add(sCE);
				}
			}
		}
		catch (DCSException de) {
				if (i==0)
					logger.error("Error extracting SOP references from Storage Commit Request");
			}
			
		return sCEs;
	}

	/**
     * returns the DICOM Study Root, Study level C-FIND DICOM to java mapping set. (to be used
     * for Query Request DataSet translation.)
     * The mapping set is hardcoded for legacy but queried from persistence mapping table for RE
     * @return HashSet containing the mapping set: ("gggg,eeee", value) for legacy, (fieldName, value) otherwise
     */
    private HashSet<DicomMap> getHISPropertyMappingSet()
    {
		// Create the HashSet
		HashSet<DicomMap> hashSet = new HashSet<DicomMap>();

		//	feed static table for legacy(could be in M persistence!!!!)
		hashSet.add(new MockDicomMap("0008,0018", null, "HISUpdate", "SOPInstanceUID", 1));
		hashSet.add(new MockDicomMap("0008,0020", null, "HISUpdate", "StudyDate", 1));
		hashSet.add(new MockDicomMap("0008,0050", null, "HISUpdate", "AccessionNumber", 1));
        hashSet.add(new MockDicomMap("0008,0090", null, "HISUpdate", "ReferringPhysiciansName", 1));
        hashSet.add(new MockDicomMap("0008,1030", null, "HISUpdate", "Procedure", 1));
        hashSet.add(new MockDicomMap("0008,1032", "0008,0100", "HISUpdate", "CPTCode", 1));
		hashSet.add(new MockDicomMap("0008,1032", "0008,0102", "HISUpdate", "CodeSchemeDesignator", 1));
		hashSet.add(new MockDicomMap("0008,1032", "0008,0104", "HISUpdate", "CodeMeaning", 1));
        hashSet.add(new MockDicomMap("0008,1050", null, "HISUpdate", "PerformingPhysician", 1));

        hashSet.add(new MockDicomMap("0010,0010", null, "HISUpdate", "PatientName", 1));
		hashSet.add(new MockDicomMap("0010,0020", null, "HISUpdate", "PatientId", 1));
        hashSet.add(new MockDicomMap("0010,0030", null, "HISUpdate", "PatientBirthDate", 1));
        hashSet.add(new MockDicomMap("0010,0032", null, "HISUpdate", "PatientBirthTime", 1));
        hashSet.add(new MockDicomMap("0010,0040", null, "HISUpdate", "PatientSex", 1));
        hashSet.add(new MockDicomMap("0010,1000", null, "HISUpdate", "OtherPatientIDs", 1));
        hashSet.add(new MockDicomMap("0010,1040", null, "HISUpdate", "PatientAddress", 1));
        hashSet.add(new MockDicomMap("0010,2000", null, "HISUpdate", "MedicalAlerts", 1));
        hashSet.add(new MockDicomMap("0010,2160", null, "HISUpdate", "PatientRace", 1));

		hashSet.add(new MockDicomMap("0020,000D", null, "HISUpdate", "StudyInstanceUID", 1));
        hashSet.add(new MockDicomMap("0020,000E", null, "HISUpdate", "SeriesInstanceUID", 1));
		
        hashSet.add(new MockDicomMap("0032,1032", null, "HISUpdate", "RequestingPhysicianName", 1));
		hashSet.add(new MockDicomMap("0032,1033", null, "HISUpdate", "RequestingService", 1));
        hashSet.add(new MockDicomMap("0032,1060", null, "HISUpdate", "RequestedProcedureDescription", 1));
        hashSet.add(new MockDicomMap("0032,1064", "0008,0100", "HISUpdate", "CPTCode", 1));
		hashSet.add(new MockDicomMap("0032,1064", "0008,0102", "HISUpdate", "CodeSchemeDesignator", 1));
		hashSet.add(new MockDicomMap("0032,1064", "0008,0104", "HISUpdate", "CodeMeaning", 1));

		hashSet.add(new MockDicomMap("0038,0300", null, "HISUpdate", "CurrentPatientLocation",  1));
		hashSet.add(new MockDicomMap("0032,1020", null, "HISUpdate", "Scheduled Study Location", 1));
        
		return hashSet;    
    }
    
	/**
	 * returns the DICOM Study Root, Study level C-FIND DICOM to java mapping set. (to be used
	 * for Query Request DataSet translation.)
	 * The mapping set is hardcoded for legacy but queried from persistence mapping table for RE
	 * @return HashSet containing the mapping set: ("gggg,eeee", value) for legacy, (fieldName, value) otherwise
	 */
	private HashSet<DicomMap> getFindPropertyMappingSet()
	{
		// Create the HashSet
		HashSet<DicomMap> hashSet = new HashSet<DicomMap>();
          
		//	feed static table for Study Root Study level query (could be in M persistence!!!!)
		hashSet.add(new MockDicomMap("0008,0020", null, "QueryRequest", "StudyDate", 1)); // DA -> String
		hashSet.add(new MockDicomMap("0008,0030", null, "QueryRequest", "StudyDate", 1)); // TM -> String(
		hashSet.add(new MockDicomMap("0008,0050", null, "QueryRequest", "AccessionNumber", 1)); // SH -> String(16)
        hashSet.add(new MockDicomMap("0008,0052", null, "QueryRequest", "Level", 1)); // DA -> String
        hashSet.add(new MockDicomMap("0008,0061", null, "QueryRequest", "Modalities in Study", 1)); // DA -> String
		hashSet.add(new MockDicomMap("0010,0010", null, "QueryRequest", "Name", 1)); // PN -> String(64)
		hashSet.add(new MockDicomMap("0010,0020", null, "QueryRequest", "PatientId", 1)); // LO -> String(64)
		hashSet.add(new MockDicomMap("0020,0010", null, "QueryRequest", "StudyId", 1)); // SH -> String(16)
		hashSet.add(new MockDicomMap("0020,000D", null, "QueryRequest", "StudyInstanceUID", 1)); // UI -> String(64)
		hashSet.add(new MockDicomMap("0008,0090", null, "QueryRequest", "ReferringPhysicianName", 1)); // PN -> String(64)
		hashSet.add(new MockDicomMap("0008,1030", null, "QueryRequest", "Description", 1)); // LO -> String(64)
		hashSet.add(new MockDicomMap("0008,1032", "0008,0100", "QueryRequest", "MedicalSpecialtyProcedureCode", 1)); // SQ/SH -> String(16)
		hashSet.add(new MockDicomMap("0008,1032", "0008,0102", "QueryRequest", "CodeSchemeDesignator", 1)); // SQ/SH -> String(16)
		hashSet.add(new MockDicomMap("0008,1032", "0008,0103", "QueryRequest", "CodeSchemeVersion", 1)); // SQ/SH -> String(16)
		hashSet.add(new MockDicomMap("0008,1032", "0008,0104", "QueryRequest", "CodeMeaning", 1)); // SQ/LO -> String(64)
		hashSet.add(new MockDicomMap("0008,1060", null, "QueryRequest", "ReadingPhysician",  1)); // n*PN -> 5*String(64)
		hashSet.add(new MockDicomMap("0010,0030", null, "QueryRequest", "DateOfBirth", 1)); // DA -> String(15)
		hashSet.add(new MockDicomMap("0010,0032", null, "QueryRequest", "TimeOfBirth", 1)); // DA -> String(15)
		hashSet.add(new MockDicomMap("0010,0040", null, "QueryRequest", "Sex", 1)); // CS -> String(1)
		hashSet.add(new MockDicomMap("0010,1000", null, "QueryRequest", "OtherPatientIds", 1)); // LO -> String(64)
		hashSet.add(new MockDicomMap("0010,1001", null, "QueryRequest", "OtherPatientNames", 1)); // PN -> String(64)
		hashSet.add(new MockDicomMap("0010,1010", null, "QueryRequest", "PatientAge", 1)); // AS -> String(4)
		hashSet.add(new MockDicomMap("0010,2160", null, "QueryRequest", "EthnicGroup", 1)); // SH -> String(16)
		hashSet.add(new MockDicomMap("0010,2180", null, "QueryRequest", "Occupation", 1)); // SH -> String(16)
		hashSet.add(new MockDicomMap("0010,21B0", null, "QueryRequest", "addditionalPatientHistory", 1)); // LT -> String(10240)
		hashSet.add(new MockDicomMap("0020,1206", null, "QueryRequest", "NumberOfStudySeries", 1)); // IS -> String(12)
		hashSet.add(new MockDicomMap("0020,1208", null, "QueryRequest", "NumberOfStudyInstances", 1)); // IS -> String(12)
		hashSet.add(new MockDicomMap("4008,010C", null, "QueryRequest", "InterpretattionAuthor", 1)); // PN -> String(64)
				
		return hashSet;	
	}

	/**
	 * returns the DICOM Study Root, Study level C-MOVE DICOM to java mapping set. (to be used
	 * for Move (Study) DataSet translation.)
	 * The mapping set is hardcoded for legacy but queried from persistence mapping table for RE
	 * @return HashSet containing the mapping set: ("gggg,eeee", value) for legacy, (fieldName, value) otherwise
	 */
	private HashSet<DicomMap> getMovePropertyMappingSet()
	{
        //FUTURE Need to find out if this method even gets called.  I do not think so.  If not,
        //  remove it.
			
		// Create the HashSet
		HashSet<DicomMap> hashSet = new HashSet<DicomMap>();

		//	feed static table for legacy Study Root Study level move (could be in M persistence!!!!)
		hashSet.add(new MockDicomMap("0008,0052", null, "MoveRequest", "Level", 1)); // DA -> String
		hashSet.add(new MockDicomMap("0020,000D", null, "MoveRequest", "InstanceUID", 1)); // UI -> String(64)					

		return hashSet;
	}
	
    private void sanitizeHISChanges(HashMap<String, String> HISChanges){
    	// 1. if (0032,1064) is present in HISchanges and (0008,1032) is present in the DataSet,
    	// copy (0032,1064) to (0008,1032) in HIS Changes
    	if (HISChanges.containsKey("0032,1064 0008,0100") && (this.dataSet.containsElement(DCM.E_PROCEDURE_CODE_SEQUENCE))) {
        	String value = (String)HISChanges.get("0032,1064 0008,0100");
        	HISChanges.put("0008,1032 0008,0100", value);
        	value = (String)HISChanges.get("0032,1064 0008,0102");
        	HISChanges.put("0008,1032 0008,0102", value);
        	value = (String)HISChanges.get("0032,1064 0008,0104");
        	HISChanges.put("0008,1032 0008,0104", value);
    	}
    }
    
    private String formatStringValue(String tag, String value){
        String formattedValue = value;
        
        try{
            AttributeTag dicomTag = new AttributeTag(tag);
            short vr = DicomDataDictionary.getElementVR(dicomTag);
            String vm = DicomDataDictionary.getElementVM(dicomTag);
            if(value == null){
            	return value;
            }
            
            if(vr == DicomDataDictionary.getVR("PN")){
                //Check if element allows for multiple values.
                if(vm.equals("1")){
                    //If only 1 value allowed, strip off everything else.
                    String[] values  = value.split("\\\\", 2);
                    formattedValue = values[0];
                }
                else{
                    formattedValue = value;
                }
                //Convert commas to carats to meet DICOM standard.
                formattedValue = formattedValue.replace(',', '^');
            }
            else{
            	formattedValue = value;
            }
        }
        catch(DCSException dcse){
            logger.warn(this.getClass().getName()+": Warning thrown while formatting String Value.");
            return value;
        }
        return formattedValue;
    }
    
	private String getStringValueFromElement(String tagName, String defaultValue)
	{
		String value = "";
		try
		{
			// Extract values from DICOM data set
			value = dataSet.getElementStringValue(new AttributeTag(tagName), defaultValue);
			if ((value!=null) && !value.isEmpty())
				value = value.trim(); // take out leading and trailing whitespaces
		}
		catch (DCSException dcse) 
		{
            logger.warn(dcse.getMessage()+ "; DefaultValue (" + ((defaultValue==null)?"null":defaultValue) + ") used.");
            return defaultValue;
		}
		
		return removeControlCharacters(value);
	}
	
	// This code replaces the loop over each char found in the original code...
	private String removeControlCharacters(String stringToClean)
	{
		String CONTROL_CHARACTER_REGEX = "\\p{Cntrl}";
		
		if (stringToClean != null)
		{
			return stringToClean.replaceAll(CONTROL_CHARACTER_REGEX, "");
		}
		else
		{
			return null;
		}
	}

	@Override
	public boolean isRadiationDoseStructuredReport() 
	{
		return getSOPClass().equals(RADIATION_DOSE_SOP_CLASS_UID);
	}

	@Override
	public List<Dose> getDose() 
	{
		RDSRProcessor processor = new RDSRProcessor(this);
		
		return processor.getDose(this.getSOPInstance());
	}	
}
