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


import gov.va.med.imaging.exchange.business.dicom.exceptions.DicomDictionaryException;

import org.apache.log4j.Logger;

/**
 * This class maintains information about the legacy DICOM Gateway on which the application
 * is running.
 * 
 * <p>
 * @author William Peterson
 *
 */
public class DicomGatewayConfiguration {

    /*
     * The singleton instance.
     */
     private static DicomGatewayConfiguration instance_ = null;

    
    private String InstrumentTimeStamp = null;
    private String InstrumentFileName = null;
    private String ModalityTimeStamp = null;
    private String ModalityFileName = null;
    private String PortListTimeStamp = null;
    private String PortListFileName = null;
    private String SCUListTimeStamp = null;
    private String SCUListFileName = null;
    private String WorkListTimeStamp = null;
    private String WorkListFileName = null;
    private String CTParametersTimeStamp = null;
    private String CTParametersFileName = null;
    private String GatewayVersion = null;
    private String DICOMViewerTimeStamp = null;
    private String DICOMViewerFileName = null;
    private String MakeAbstractTimeStamp = null;
    private String MakeAbstractFileName = null;
    private String CStoreTimeStamp = null;
    private String CStoreFileName = null;
    private String ReconstructorTimeStamp = null;
    private String ReconstructorFileName = null;
    private String DICOMtoTargaTimeStamp = null;
    private String DICOMtoTargaFileName = null;
    private String Location = null;
    private String HostName = null;
    
    private static Logger logger = Logger.getLogger(DicomGatewayConfiguration.class);


    /**
     * Constructor
     *
     * 
     */
    private DicomGatewayConfiguration() {
        super();
    }
    
    /**
     * Allows access to the Singleton.
     * 
     * @return Instance of this Class.
     * @throws DCSException represents DCS Exceptions from the DCF API.  Refer to DCF API.
     * @throws CDSException represents CDS Exceptions from the DCF API.  Refer to DCF API.
     */
    public synchronized static DicomGatewayConfiguration getInstance(){
        //Get Instance of the Listen object through this.  Due to being a Singleton.
        if (instance_ == null){
            instance_ = new DicomGatewayConfiguration();
        }
        return instance_;
    }
    
    /**
     * @return Returns the cStoreFileName.
     */
    public String getCStoreFileName() {
        return CStoreFileName;
    }
    /**
     * @return Returns the cStoreTimeStamp.
     */
    public String getCStoreTimeStamp() {
        return CStoreTimeStamp;
    }
    /**
     * @return Returns the cTParametersFileName.
     */
    public String getCTParametersFileName() {
        return CTParametersFileName;
    }
    /**
     * @return Returns the cTParametersTimeStamp.
     */
    public String getCTParametersTimeStamp() {
        return CTParametersTimeStamp;
    }
    /**
     * @return Returns the dICOMtoTargaFileName.
     */
    public String getDICOMtoTargaFileName() {
        return DICOMtoTargaFileName;
    }
    /**
     * @return Returns the dICOMtoTargaTimeStamp.
     */
    public String getDICOMtoTargaTimeStamp() {
        return DICOMtoTargaTimeStamp;
    }
    /**
     * @return Returns the dICOMViewerFileName.
     */
    public String getDICOMViewerFileName() {
        return DICOMViewerFileName;
    }
    /**
     * @return Returns the dICOMViewerTimeStamp.
     */
    public String getDICOMViewerTimeStamp() {
        return DICOMViewerTimeStamp;
    }
    /**
     * @return Returns the gatewayVersion.
     */
    public String getGatewayVersion() {
        return GatewayVersion;
    }
    /**
     * @return Returns the hostName.
     */
    public String getHostName() {
        return HostName;
    }
    /**
     * @return Returns the instrumentFileName.
     */
    public String getInstrumentFileName() {
        return InstrumentFileName;
    }
    /**
     * @return Returns the instrumentTimeStamp.
     */
    public String getInstrumentTimeStamp() {
        return InstrumentTimeStamp;
    }
    /**
     * @return Returns the location.
     */
    public String getLocation() {
        return Location;
    }
    /**
     * @return Returns the makeAbstractFileName.
     */
    public String getMakeAbstractFileName() {
        return MakeAbstractFileName;
    }
    /**
     * @return Returns the makeAbstractTimeStamp.
     */
    public String getMakeAbstractTimeStamp() {
        return MakeAbstractTimeStamp;
    }
    /**
     * @return Returns the modalityFileName.
     */
    public String getModalityFileName() {
        return ModalityFileName;
    }
    /**
     * @return Returns the modalityTimeStamp.
     */
    public String getModalityTimeStamp() {
        return ModalityTimeStamp;
    }
    /**
     * @return Returns the portListFileName.
     */
    public String getPortListFileName() {
        return PortListFileName;
    }
    /**
     * @return Returns the portListTimeStamp.
     */
    public String getPortListTimeStamp() {
        return PortListTimeStamp;
    }
    /**
     * @return Returns the reconstructorFileName.
     */
    public String getReconstructorFileName() {
        return ReconstructorFileName;
    }
    /**
     * @return Returns the reconstructorTimeStamp.
     */
    public String getReconstructorTimeStamp() {
        return ReconstructorTimeStamp;
    }
    /**
     * @return Returns the sCUListFileName.
     */
    public String getSCUListFileName() {
        return SCUListFileName;
    }
    /**
     * @return Returns the sCUListTimeStamp.
     */
    public String getSCUListTimeStamp() {
        return SCUListTimeStamp;
    }
    /**
     * @return Returns the workListFileName.
     */
    public String getWorkListFileName() {
        return WorkListFileName;
    }
    /**
     * @return Returns the workListTimeStamp.
     */
    public String getWorkListTimeStamp() {
        return WorkListTimeStamp;
    }
    
    
    /**
     * @param storeFileName The cStoreFileName to set.
     */
    public void setCStoreFileName(String storeFileName) {
        CStoreFileName = storeFileName;
    }
    /**
     * @param storeTimeStamp The cStoreTimeStamp to set.
     */
    public void setCStoreTimeStamp(String storeTimeStamp) {
        CStoreTimeStamp = storeTimeStamp;
    }
    /**
     * @param parametersFileName The cTParametersFileName to set.
     */
    public void setCTParametersFileName(String parametersFileName) {
        CTParametersFileName = parametersFileName;
        try{
            GatewayDictionaryContents.getInstance().loadCTParameterList(CTParametersFileName);
    }
        catch(DicomDictionaryException dde){
            logger.warn(this.getClass().getName());
            logger.warn("Can not load file "+CTParametersFileName);
        }
    }
    /**
     * @param parametersTimeStamp The cTParametersTimeStamp to set.
     */
    public void setCTParametersTimeStamp(String parametersTimeStamp) {
        CTParametersTimeStamp = parametersTimeStamp;
    }
    /**
     * @param mtoTargaFileName The dICOMtoTargaFileName to set.
     */
    public void setDICOMtoTargaFileName(String mtoTargaFileName) {
        DICOMtoTargaFileName = mtoTargaFileName;
    }
    /**
     * @param mtoTargaTimeStamp The dICOMtoTargaTimeStamp to set.
     */
    public void setDICOMtoTargaTimeStamp(String mtoTargaTimeStamp) {
        DICOMtoTargaTimeStamp = mtoTargaTimeStamp;
    }
    /**
     * @param viewerFileName The dICOMViewerFileName to set.
     */
    public void setDICOMViewerFileName(String viewerFileName) {
        DICOMViewerFileName = viewerFileName;
    }
    /**
     * @param viewerTimeStamp The dICOMViewerTimeStamp to set.
     */
    public void setDICOMViewerTimeStamp(String viewerTimeStamp) {
        DICOMViewerTimeStamp = viewerTimeStamp;
    }
    /**
     * @param gatewayVersion The gatewayVersion to set.
     */
    public void setGatewayVersion(String gatewayVersion) {
        GatewayVersion = gatewayVersion;
    }
    /**
     * @param hostName The hostName to set.
     */
    public void setHostName(String hostName) {
        HostName = hostName;
    }
    /**
     * @param instrumentFileName The instrumentFileName to set.
     */
    public void setInstrumentFileName(String instrumentFileName) {
        InstrumentFileName = instrumentFileName;
    }
    /**
     * @param instrumentTimeStamp The instrumentTimeStamp to set.
     */
    public void setInstrumentTimeStamp(String instrumentTimeStamp) {
        InstrumentTimeStamp = instrumentTimeStamp;
    }
    /**
     * @param location The location to set.
     */
    public void setLocation(String location) {
        Location = location;
    }
    /**
     * @param makeAbstractFileName The makeAbstractFileName to set.
     */
    public void setMakeAbstractFileName(String makeAbstractFileName) {
        MakeAbstractFileName = makeAbstractFileName;
    }
    /**
     * @param makeAbstractTimeStamp The makeAbstractTimeStamp to set.
     */
    public void setMakeAbstractTimeStamp(String makeAbstractTimeStamp) {
        MakeAbstractTimeStamp = makeAbstractTimeStamp;
    }
    /**
     * @param modalityFileName The modalityFileName to set.
     */
    public void setModalityFileName(String modalityFileName) {
        ModalityFileName = modalityFileName;
        try{
            GatewayDictionaryContents.getInstance().loadModalityDictionaryList(ModalityFileName);
        }
        catch(DicomDictionaryException dde){
            logger.warn(this.getClass().getName());
            logger.warn("Can not load file "+modalityFileName);
        }
    }
    /**
     * @param modalityTimeStamp The modalityTimeStamp to set.
     */
    public void setModalityTimeStamp(String modalityTimeStamp) {
        ModalityTimeStamp = modalityTimeStamp;
    }
    /**
     * @param portListFileName The portListFileName to set.
     */
    public void setPortListFileName(String portListFileName) {
        PortListFileName = portListFileName;
    }
    /**
     * @param portListTimeStamp The portListTimeStamp to set.
     */
    public void setPortListTimeStamp(String portListTimeStamp) {
        PortListTimeStamp = portListTimeStamp;
    }
    /**
     * @param reconstructorFileName The reconstructorFileName to set.
     */
    public void setReconstructorFileName(String reconstructorFileName) {
        ReconstructorFileName = reconstructorFileName;
    }
    /**
     * @param reconstructorTimeStamp The reconstructorTimeStamp to set.
     */
    public void setReconstructorTimeStamp(String reconstructorTimeStamp) {
        ReconstructorTimeStamp = reconstructorTimeStamp;
    }
    /**
     * @param listFileName The sCUListFileName to set.
     */
    public void setSCUListFileName(String listFileName) {
        SCUListFileName = listFileName;
    }
    /**
     * @param listTimeStamp The sCUListTimeStamp to set.
     */
    public void setSCUListTimeStamp(String listTimeStamp) {
        SCUListTimeStamp = listTimeStamp;
    }
    /**
     * @param workListFileName The workListFileName to set.
     */
    public void setWorkListFileName(String workListFileName) {
        WorkListFileName = workListFileName;
    }
    /**
     * @param workListTimeStamp The workListTimeStamp to set.
     */
    public void setWorkListTimeStamp(String workListTimeStamp) {
        WorkListTimeStamp = workListTimeStamp;
    }
}
