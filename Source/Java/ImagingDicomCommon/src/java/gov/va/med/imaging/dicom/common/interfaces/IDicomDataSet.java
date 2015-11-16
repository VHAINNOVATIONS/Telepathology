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
package gov.va.med.imaging.dicom.common.interfaces;

import gov.va.med.imaging.exchange.business.dicom.DicomAE;
import gov.va.med.imaging.exchange.business.dicom.DicomCorrectInfo;
import gov.va.med.imaging.exchange.business.dicom.DicomMap;
import gov.va.med.imaging.exchange.business.dicom.DicomRequestParameters;
import gov.va.med.imaging.exchange.business.dicom.InstanceFile;
import gov.va.med.imaging.exchange.business.dicom.InstrumentConfig;
import gov.va.med.imaging.exchange.business.dicom.PatientRef;
import gov.va.med.imaging.exchange.business.dicom.PatientStudyInfo;
import gov.va.med.imaging.exchange.business.dicom.ProcedureRef;
import gov.va.med.imaging.exchange.business.dicom.SOPInstance;
import gov.va.med.imaging.exchange.business.dicom.Series;
import gov.va.med.imaging.exchange.business.dicom.StorageCommitElement;
import gov.va.med.imaging.exchange.business.dicom.Study;
import gov.va.med.imaging.exchange.business.dicom.exceptions.DicomException;
import gov.va.med.imaging.exchange.business.dicom.exceptions.IODViolationException;
import gov.va.med.imaging.exchange.business.dicom.exceptions.IllegalQueryDataException;
import gov.va.med.imaging.exchange.business.dicom.exceptions.UnknownSOPClassException;
import gov.va.med.imaging.exchange.business.dicom.exceptions.ValidateIODException;
import gov.va.med.imaging.exchange.business.dicom.exceptions.ValidateVRException;
import gov.va.med.imaging.exchange.business.dicom.rdsr.Dose;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author William Peterson
 *
 */
public interface IDicomDataSet {


    /**
     * return (unwrap) the toolkit specific DicomDataSet.
     * 
     * @return Object
     */
    public Object getDicomDataSet();
    
    
    /**
     * return (wrap) the toolkit specific DicomDataSet.
     * 
     * @param dicomDataSet  return as an Object.  Need to cast to the toolkit specific implementation.
     */
    public void setDicomDataSet(Object dicomDataSet);
    
    
    /**
     * Get generic DicomElement for the given Dicom tag.
     * 
     * @param dicomTag	represented as a string, i.e. "0010,0010".
     * @return	IDicomElement
     * @throws DicomException	if method fails to access Dicom tag in DicomDataSet.
     */
    public IDicomElement getDicomElement(String dicomTag) throws DicomException;

    
    /**
     * Get generic DicomElement for the given Dicom sequence's tag.
     * 
     * @param dicomTagName	or Sequence tag represented as a string, i.e. "0010,0010".
     * @param dicomTag2Name	represented as a string, i.e. "0010,0010".
     * @return	IDicomElement
     * @throws DicomException	if method fails to access Dicom tag in DicomDataSet.
     */
	public String getDicomElementValue(String dicomTagName, String dicomTag2Name) throws DicomException;
	
    /**
     * Remove generic DicomElement for the given Dicom tag, including if the DicomElement is within a Sequence.
     * 
     * @param dicomTagName	represented as a string, i.e. "0010,0010".
     * @param dicomTag2Name	represented as a string, i.e. "0010,0010".
     * @return	true if successful removing the DicomElement.
     * @throws DicomException	if method fails to access Dicom tag in DicomDataSet.
     */
    public boolean removeDicomElement(String dicomTagName, String dicomTag2Name) throws DicomException;

    
    /**
     * Insert generic DicomElement for a given Dicom tag, including if the DicomElement is within a Sequence.
     * 
     * @param dicomTagName	represented as a string, i.e. "0010,0010".
     * @param dicomTag2Name	represented as a string, i.e. "0010,0010".
     * @param value	for this specific Dicom tag represented as a string.
     * @return	IDicomElement
     * @throws DicomException	if method fails to access Dicom tag in DicomDataSet.
     */
    public IDicomElement insertDicomElement(String dicomTagName, String dicomTag2Name, String value) throws DicomException;

    
    /**
     * Insert new & record old DicomElement value for a given Dicom tag, including if the DicomElement is within a Sequence.
     * Insertion occurs only if the new value is different from the existing value in dataset 
     * or the ignoreNew flag is set to true. 
     * if insertion occurred old values are recorded in an original Dicom Sequence
     * 
     * @param tag	represented as a string, i.e. "0010,0010".
     * @param tag2	represented as a string, i.e. "0010,0010".
     * @param value	for this specific Dicom tag represented as a string.
     * @param ignoreNew if set inserts new and record old value regardless of matching
     * @return	boolean True if insertion and recording occured
     */
	public boolean insertAndRecordNewValue (String tag, String tag2, String value, boolean ignoreNew);

	
	/**
	 * Create and return a PatientRef business object using the values contained in 
	 * the DicomElements of this DicomDataSet.
	 * 
	 * @return PatientRef
	 */
	public PatientRef getPatientRef();

	
	/**
	 * Create and return a ProcedureRef business object using the values contained in 
	 * the DicomElements of this DicomDataSet.
	 * 
	 * @return ProcedureRef
	 */
	
	
	public ProcedureRef getProcedureRef(InstrumentConfig instrument);
	/**
	 * Create and return a Study business object using the values contained in 
	 * the DicomElements of this DicomDataSet.
	 * 
	 * @return Study
	 */
	public Study getStudy();

	
	/**
	 * Create and return a Series business object using the values contained in 
	 * the DicomElements of this DicomDataSet.
	 * 
	 * @param DicomAE	DICOM Application Entity object.
	 * @param InstrumentConfig	InstrumentConfig object representing an instrument from the instrument.dic file.
	 * @return
	 */
	public Series getSeries(DicomAE dicomAE, InstrumentConfig instrument);

	
	/**
	 * Create and return a SOPInstance business object using the values contained in 
	 * the DicomElements of this DicomDataSet.
	 * 
	 * @return SOPInstance
	 */
	public SOPInstance getSOPInstance();

	
	/**
	 * Create and return an InstanceFile business object using the values contained in 
	 * the DicomElements of this DicomDataSet.
	 * 
	 * @return InstanceFile
	 */
 	public InstanceFile getInstanceFile();
    
 	
 	/**
     * Return the Study Instance UID as a String.
     * 
     * @return String - representing the Study Instance UID.
     */
    public String getStudyInstanceUID();

    
    /**
     * Set the Study Instance UID.
     * 
     * @param studyInstanceUID
     */
    public void setStudyInstanceUID(String studyInstanceUID);
    
    
    /**
     * Return the Series Instance UID as a String.
     * 
     * @return String - representing the Series Instance UID.
     */
    public String getSeriesInstanceUID();
    
    
    /**
     * Set the Series Instance UID.
     * 
     * @param seriesInstanceUID
     */
    public void setSeriesInstanceUID(String studyInstanceUID);
    
    
    /**
     * Return the SOP Instance UID as a String.
     * 
     * @return String - representing the SOP Instance UID. 
     */
    public String getSOPInstanceUID();
    
    
    /**
     * Set the SOP Instance UID.
     * 
     * @param sopInstanceUID
     */
    public void setSOPInstanceUID(String sopInstanceUID);
    
    
    /**
     * Set the Study Instance UID.
     * 
     * @return boolean
     */
    public boolean getStudyInstanceUID(String studyInstanceUID);
    
    
    /**
     * Set the Series Instance UID.
     * 
     * @return boolean
     */
    public boolean getSeriesInstanceUID(String seriesInstanceUID);
    
    
    /**
     * Set the SOP Instance UID.
     * 
     * @return boolean
     */
    public boolean getSOPInstanceUID(String sopInstanceUID);
    
    
    /**
     * Get the SOP Class UID.
     * 
     * @return	the UID of the SOP Class as a string.
     */
	public String getSOPClass();
	
	/**
	 * 
	 * @return the Manufacturer's name
	 */
	public String getManufacturer();
	
	/**
	 * 
	 * @return	the Model Name
	 */
	public String getModelName();
	
	
	/**
	 * Get/Create a partial DicomCorrectInfo object.  It populates various fields in the object in case this object 
	 * has to enter Dicom Correct mechanism.
	 * 
	 * @return	a partially populated DicomCorrectInfo object.
	 */
	public DicomCorrectInfo getPartialDicomCorrectInfo();

	
    /**
     * Get Acquisition Site.
     * 
     * @return String - representing the Acquisition Site
     */
    public String getAcquisitionSite();

    
    /**
     * Set Acquisition Site.
     * 
     * @param acquisitionSite String
     */
    public void setAcquisitionSite(String acquisitionSite);
    
    
    /**
     * Get the Patient/Study Info.
     * 
     * @param instrument	information about the listener that received this DicomDataSet. 
     * @return	
     * @throws DicomException	if method fails to access this information in DicomDataSet.
     */
    public PatientStudyInfo getPatientStudyInfo(InstrumentConfig instrument) throws DicomException ;

    
    /**
     * Update containing DicomDataSet with a set of HIS changes.
     * 
     * @param HashMap HIS changes, each change is in the form of <(gggg.eeee)> <"change value">
     * @throws DicomException	if method fails to access this information in DicomDataSet. 
     */
    public void updateHISChangesToDDS(HashMap<String,String> HISChanges)throws DicomException;
    
    
    /**
     * Convert DICOM dataset to byte array (to a dicom file in memory).
     * 
     * @return	byte array
     * updateOnly - true if no reconstitution (creating new DICOM object was done
     * @return byte[]
     */
    public byte[] part10Buffer(boolean updateOnly);

    
    /**
     * Validate the VRs in this DicomDataSet.
     * 
     * @throws ValidateVRException	fails to access or complete the VR validation process of this DicomDataSet.
     */
    public void validateVR() throws ValidateVRException;
    
    
    /**
     * Validate the IOD represented by this DicomDataSet.
     * 
     * @return	violation list that contains all errors and warnings discovered.
     * @throws ValidateIODException	fails to access or complete the IOD validation process of this DicomDataSet.
     * @throws UnknownSOPClassException	fails because the SOP Class of the DicomDataSet is unknown.
     */
    public IIODViolationList validateIOD() throws ValidateIODException, UnknownSOPClassException;
    
    
	/**
     * Determine if the Dicom tag exist in this DicomDataSet.
     * 
     * @param dicomTagName	represented as a string, i.e. "0010,0010".
     * @param dicomTag2Name	null or represented as a string, i.e. "0010,0010", if dicomTagName is a sequence tag
     * @return	true if the Dicom tag exist.  False otherwise.
     */
	public boolean containsDicomElement(String dicomTagName, String dicomTag2Name);

	
	/**
	 * returns the translated DICOM Patient Root C-Find request. Currently, this call results in an exception.
	 * 
	 * @return DicomRequestParameters - containing the map of DicomElements and their corresponding CFind Request values.
	 * @throws IllegalQueryDataException	if the method fails to convert the DicomDataSet to a HashMap.
	 */
	public DicomRequestParameters createPatientQueryRequestParameters()
					throws IllegalQueryDataException;

	
	/**
	 * returns the translated DICOM Study Root C-Find request. Only Study Root, Study level is accepted.
	 * 
	 * @return DicomRequestParameters	containing the map of DicomElements and their corresponding CFind Request value.
	 * @throws IllegalQueryDataException	if the method fails to convert the DicomDataSet to a HashMap.
	 */
	public DicomRequestParameters createStudyQueryRequestParameters()
					throws IllegalQueryDataException;
	
	
	/**
	 * returns the translated DICOM C-Move request. Only Study Root, Study level is accepted.
	 * 
	 * @return DicomRequestParameters	containing the map of DicomElements and their corresponding CMove Request value.
	 * @throws IllegalQueryDataException	if the method fails to convert the DicomDataSet to a HashMap.
	 */
	public DicomRequestParameters createMoveRequestParameters()
					throws IllegalQueryDataException;
	
	
	/**
	 * Get a Query Mapping Set.  A mapping set contains the set of attributes, without values, that are in the DicomDataSet and
	 * matched against a static CFind attribute Table.
	 * 
	 * This set of attributes are used to translate the DicomDataSet to a HashMap object.  It is also used later to verify 
	 * the CFind responses contain the same attributes as the CFind request.
	 *   
	 * This is only used for a CFind Dimse message DicomDataSet.
	 * 
	 * @return HashSet containing a list of DicomMap objects.
	 */
	public HashSet<DicomMap> getQueryMappingSet();
	
	
	/**
	 * Set a Query Mapping Set.  A mapping set contains a set of attributes without values.  This mapping set can be used to
	 * translate and filter to use only selected attributes in the DicomDataSet.
	 * 
	 * This is only used for a CFind Dimse message DicomDataSet.
	 * 
	 * @param mappingSet containing a list of DicomMap objects.
	 */
	public void setQueryMappingSet(HashSet<DicomMap> mappingSet);

	
	/**
	 * Get the Source AETitle.
	 * 
	 * @return	the source AETitle as a string.
	 */	
	public String getSourceAET();

	
	/**
	 * Set the Source AETitle.
	 * 
	 * @param sourceAET	the source AETitle as a string.
	 */
	public void setSourceAET(String sourceAET);	
	
	
	/**
	 * Get the Transfer Syntax used to receive the DicomDataSet.  This could either originate from the listener or from file.
	 * 
	 * @return	Transfer Syntax UID as a string.
	 */
	public String getReceivedTransferSyntax();

	
	/**
	 * Set the Transfer Syntax.
	 * 
	 * @param receivedTransferSyntax	Transfer Syntax UID as a string.
	 */
	public void setReceivedTransferSyntax(String receivedTransferSyntax);
	
	
	/**
	 * Get the QueryRetrieve Level element value from this DicomDataSet.  This is only populated when the DicomDataSet
	 * represents a CFind or a CMove DicomDataSet object.
	 * 
	 * @return	one of four possible string values: PATIENT, STUDY, SERIES, IMAGE.
	 */
	public String getQueryRetrieveLevel();
	
	
	//CODECR #IMAG00000440 - SSN Format issue.
 	/**
 	 * 
 	 */
    public void changeDataPresentation();

	
	/**
	 * @return the name
	 */
	public String getName();

	
	/**
	 * @param name the name to set
	 */
	public void setName(String name);

	
	public String getAffectedSOPClass();
	
	public void setAffectedSOPClass(String sopClass);
	
	/**
	 *  Set the AETitle's Default Service Type (RAD or CON).
	 *  
	 * @param type	represents the Service Type.  Current value to use are RAD or CON.
	 */
	public void setAETDefaultServiceType(String type);
	
	/**
	 *  Get the AETitle's Default Service Type (RAD or CON).
	 * 
	 * @return	Service Type.
	 */
	public String getAETDefaultServiceType();
	
	/**
	 * this method inserts DICOM elements into the Original Attribute Sequence.  This sequence is used to
	 * keep track of changes to Patient and Study data.
	 * 
	 * @param tag	represents the element to store in Original Attribute Sequence.
	 * @param tagValue	represents the element's value to store in Original Attribute Sequence.
	 * @param context	represents some type of identifier that made the request.  This parameter is not
	 * stored.  It is only used to show the identifier in the logging mechanism if the method fails.
	 */
	public void insertToOriginalAttributeSequence(String tag, String tagValue, String context);

	/**
	 * this method inserts DICOM elements into the Original Attribute Sequence.  This sequence is used to
	 * keep track of changes to Patient and Study data.
	 * 
	 * @param tag	represents the element to store in Original Attribute Sequence.
	 * @param context	represents some type of identifier that made the request.  This parameter is not
	 * stored.  It is only used to show the identifier in the logging mechanism if the method fails.
	 */
	public void insertToOriginalAttributeSequence(String tag, String context);
	
	/**
	 * this method returns the transaction UID from the dataset if present, else null is returned.
	 */
	public String getTransactionUID();
	
	/**
	 * this method inserts DICOM SOP Class/Instance element pairs into a storage commitment structure.
	 * This sequence is used to keep track of Storage Commitment requests until a response is delivered to the sender.
	 */
	public List<StorageCommitElement> getRefSOPUIDList();
	
	/**
	 * Indicates whether or not the DICOM object is a radiation dose structured report (RDSR)
	 * @return
	 */
	public boolean isRadiationDoseStructuredReport();


	/**
	 * Returns a populated instance of a Dose subclass for known RDSR dose types. Returns null
	 * otherwise.
	 * 
	 * @return a Dose subclass if this is a known RDSR type; otherwise, returns null
	 */
	public List<Dose> getDose();
	
}
