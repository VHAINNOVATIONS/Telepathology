/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 10, 2008
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
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
package gov.va.med.imaging.exchange.business.dicom;

import gov.va.med.imaging.exchange.business.PersistentEntity;

import java.io.Serializable;
// import java.util.Date;

/**
 * Represents a patient Reference entity in persistence (DB).
 * 
 * @author vhaiswtittoc
 *
 */
public class SOPInstance implements PersistentEntity, Serializable, Comparable<SOPInstance>
{	
	private int id;
	private static final long serialVersionUID = -5185851367113539916L;
	private String ien;						// primary key in the DB
	private String seriesIEN;				// foreign key to the series record in the DB
	private String SOPInstanceUID;			// actual UID
	private String originalSOPInstanceUID;	// incoming UID, only if UID generation was forced 
	private String SOPClassUID;				// DICOM Service Object Pair UID
	private String typeIX;					// image type from one of 100+ values from an enumerated list 
	private String description;				// Max 64 chars free text
	private String acqDateTime;				// when the modality Instrument created the instance
	private String acqNumber;				// Max 12 chars
	private String instanceNumber;			// Max 12 chars
	private String isKeyimage;				// 'Y' or 'N' 1 char
	private String imageNotOK;				// 'Y' or 'N' 1 char
	private String contrastBolusAgent;		// description only if agent present, else null
	private String imagePosition;			// 3 '\' separated strings
	private String imageLaterality;			// 'L','R','B' or 'U' from (0020, 0062)
	private String imageOrientation;		// 6 '\' separated strings
	private String patientOrientation;		// 2 '\' separated strings;(‘A’or‘P’)+(‘R’or‘L’)+(‘H’+‘F’), like “ARF”.
	private String photometricInterptation;	// 0..32
	private String numberOfFrames;			// if more than one in object
	private String rows;					// Image pixel matrix’s # of rows, null if n/a
	private String columns;					// Image pixel matrix’s # of columns, null id n/a
	private String pixelSpacing;			// 2 decimal strings separated by ‘\’
	private String samplesPerPixel;			// 1, 3 or 4 (1 char)
	private String bitsAllocated;			// All bits allocated for one pixel (max 12 ch)
	private String bitsStored;				// Number of bits used to store image info (max 12 ch)
	private String highBit;					// The high bit's number (0..N) N is most commonly 7 or 15 (max 12 ch)
	private String pixelRepresentation;		// If applicable:  0 - unsigned integer;  1 - 2's complement (max 12 ch)
	private String rescaleIntercept;		// 'b' in output units = m*SV + b (max 12 ch)
	private String rescaleSlope;			// 'm' in output units = m*SV + b (max 12 ch)
	private String windowCenter;			// N ‘\’ separated integer (max 12 ch) strings; by default N=1 
	private String windowWidth;				// N ‘\’ separated integer (max 12 ch) strings; by default N=1 
	private String plateID;					// The ID or serial number of the (CR) sensing plate if aplicable (0..64)
	private String SliceThickness;			// Nominal (CT) slice thickness, in mm if applicable (0..16)
	private String reconstructionDiameter;	// in mm; the region from within which data were used creating the recon image if applicable (0..64)
	private String scanningSequence;		// for MR only, multi-valued, limited 2 char identifiers separated by '\' (SE, IR, GR, EP and/or RM) 
	private String scanningVariant;			// for MR only, limited identifiers separated by '\' (SK,MTC,SS,TRSS,SP,MP,OSP,NONE) 
	private String mrAcqType;				// for MR only, 2D or 3D 
	private String acqContextDescription;	// max 64 ch
	private String repetitionTime;			// in msec between the beginning of a pulse sequence and the beginning of the succeeding pulse sequence if applicable
	private String echoTime;				// in msec between the middle of the excitation pulse and the peak of the echo produced if applicable
	private String referencedSOPInstances;	// SOP Class '\' SOP Instance UID pairs separated by '\'? (0 or more pairs can be contained)
	private String sourceOfReferencedInstances;	// DICOM tag ID of source tag for 
	private String microscopicObjective;	// identifies the power of the microscope objective at slide capture time (0..10 ch)
	private String labSliceID;				// max 64
	private String histologicalStain;		// max 30
	private String transferSyntaxUid;
	
	// here are the Read Only attributes; they have Getters only and must be set only internally by DB API call
	private String LastUpdateDateTime; 		// set by create/attach and update methods (YYYYMMDD.HHMISS)
	
	
	public SOPInstance() 
	{
	}

	/**
	 * Create a new SOP Instance
	 * @param sopIUID actual UID
	 * @param originalSOPIUID incoming UID if UID generation was forced, else null
	 * @param sopClassUID the series' non-unique ID within the Study (max 12)
	 * @param typeIx free text (64)
	 * @param desc free text (64)
	 * @param acqDT when the modality Instrument created the instance
	 * @param acqNum acquisition number
	 * @param instNum instance number
	 * @param isKeyImg is this a Key image?
	 * @param imgNotOk image not OK? (of bad quality?)
	 * @param contrBolusAgent Contrast or BBolus Agent description, null if not present
	 * @param imgPos image position: 3 '\' separated strings
	 * @param imgOrient image orientation: 6 '\' separated strings
	 * @param patOrient patient position: 2 '\' separated strings
	 * @param photomInterp photometric interpretation
	 * @param numFrs number of Frames in multiframe object
	 * @param rows Image pixel matrix’s # of rows
	 * @param columns Image pixel matrix’s # of columns
	 * @param rescInterc rescale intercept: 'b' in output units = m*SV + b 
	 * @param rescSlope rescale slope: 'm' in output units = m*SV + b 
	 * @param winCtr window center
	 * @param winWidth window width
	 * @param plateId the ID or serial number of the (CR) sensing plate if aplicable
	 * @param sliceThick nominal (CT) slice thickness, in mm if applicable
	 * @param reconDiam reconstruction diameter in mm
	 * @param scanningSeq scanning sequence (for MRs only)
	 * @param scanningVart scanning variant (for MRs only)
	 * @param mrAcqTyp MR ACQ type
	 * @param acqContextDesc ACQ context description (max 64)
	 * @param repeatTime repetition time in msec
	 * @param echoTim echo time in msec
	 * @param refSOPIs reference SOP Instance UID pairs (SOP and Class UIDs)
	 * @param srcOfRefSOPIs reference SOP Instance UID pairs (SOP and Class UIDs)
	 * @param microscObj; the power of the microscope objective at slide capture time
	 * @param labSliceID max 64
	 * @param histologicalStain max 30
	 * @param imageLaterality 'L','R','B'(both) or 'U'(unpaired) from (0020, 0062)
	 */
	public SOPInstance(String sopIUID, String origSOPIUID, String sopClassUID, String typeIx, String desc, String acqDT, 
				String acqNum, String instNum, String isKeyImg, String imgNotOk, String contrBolusAgent, String imgPos, 
				String imgOrient, String patOrient, String photomInterp, String numFrs, String rows, String columns,
				String pxSpacing, String samplesPerPx, String  bitsAllotted, String bitsStored, String hiBit, String pxRepr,
				String rescInterc, String rescSlope, String winCtr, String winWidth, String plateId, String sliceThick,
				String reconDiam, String scanningSeq, String scanningVart, String mrAcqTyp, String acqContextDesc,
				String repeatTime, String echoTim, String refSOPIs, String srcOfRefSOPIs, String microscObj,
				String ladSiceId, String histStain, String imgLaterality)
	{
		this.SOPInstanceUID = sopIUID;
		this.originalSOPInstanceUID = origSOPIUID;
		this.SOPClassUID =sopClassUID;	
		this.typeIX = typeIx;
		this.description = desc;
		this.acqDateTime = acqDT;
		this.acqNumber = acqNum;
		this.instanceNumber = instNum;
		this.isKeyimage = isKeyImg;
		this.imageNotOK = imgNotOk;
		this.contrastBolusAgent = contrBolusAgent;
		this.imagePosition = imgPos;
		this.imageOrientation = imgOrient;
		this.imageLaterality = imgLaterality;
		this.patientOrientation = patOrient;
		this.photometricInterptation = photomInterp;
		this.numberOfFrames = numFrs;
		this.rows = rows;
		this.columns = columns;
		this.pixelSpacing = pxSpacing;
		this.samplesPerPixel = samplesPerPx;
		this.bitsAllocated = bitsAllotted;
		this.bitsStored = bitsStored;
		this.highBit = hiBit;
		this.pixelRepresentation = pxRepr;
		this.rescaleIntercept = rescInterc;
		this.rescaleSlope = rescSlope;
		this.windowCenter = winCtr;
		this.windowWidth = winWidth;
		this.plateID = plateId;
		this.SliceThickness = sliceThick;
		this.reconstructionDiameter = reconDiam;
		this.scanningSequence = scanningSeq;
		this.scanningVariant = scanningVart;
		this.mrAcqType = mrAcqTyp;
		this.acqContextDescription = acqContextDesc;
		this.repetitionTime = repeatTime;
		this.echoTime = echoTim;
		this.referencedSOPInstances = refSOPIs;
		this.sourceOfReferencedInstances = srcOfRefSOPIs;
		this.microscopicObjective = microscObj;
		this.labSliceID = ladSiceId;
		this.histologicalStain = histStain;
	}
	
	public String toString() 
	{
		return this.SOPInstanceUID + " (origSOPIUID=" + this.originalSOPInstanceUID + "; SOPClass=" + this.SOPClassUID + 
				"; Desc=" + this.description + "; TypeIndex=" + this.typeIX + "; AcqDateTime=" + this.acqDateTime + 
				"; acqNumber=" + this.acqNumber + "; instanceNumber=" + this.instanceNumber +
				"; isKeyimage=" + this.isKeyimage + "; imageNotOK=" + this.imageNotOK +
				"; contrastBolusAgent=" + this.contrastBolusAgent + "; imgPosition=" + this.imagePosition + 
				"; imgOrientation=" + this.imageOrientation + "; patOrientation=" + this.patientOrientation + 
				"; photometricInterptation=" + this.photometricInterptation + "; #Frames=" + this.numberOfFrames + 
				"; rows=" + this.rows + "; columns=" + this.columns + "; pixelSpacing=" + this.pixelSpacing +
				"; samplesPerPX=" + this.samplesPerPixel + "; bitsAllocated=" + this.bitsAllocated +
				"; bitsStored=" + this.bitsStored + "; highBit=" + this.highBit + "; pixelRepr=" + this.pixelRepresentation + 
				"; rescaleIntercept=" + this.rescaleIntercept + "; rescaleSlope=" + this.rescaleSlope + 
				"; windowCenter=" + this.windowCenter + "; windowWidth=" + this.windowWidth + "; plateID=" + this.plateID + 
				"; SliceThickness=" + this.SliceThickness + "; reconDiam=" + this.reconstructionDiameter + 
				"; scanningSeq=" + this.scanningSequence + "; scanningVariant=" + this.scanningVariant + 
				"; mrAcqType=" + this.mrAcqType + "; acqContextDesc=" + this.acqContextDescription +
				"; repetitionTime=" + this.repetitionTime + "; echoTime=" + this.echoTime + 
				"; refSOPInst=" + this.referencedSOPInstances + "; sourceOfReferencedInstances" + this.sourceOfReferencedInstances +
				"; microscopicObjective=" + this.microscopicObjective + "; labSliceID=" + this.labSliceID + 
				"; histoStain=" + this.histologicalStain + "imageLaterality" + this.imageLaterality + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((SOPInstanceUID == null) ? 0 : SOPInstanceUID.hashCode());
		result = prime * result
				+ ((originalSOPInstanceUID == null) ? 0 : originalSOPInstanceUID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final SOPInstance other = (SOPInstance) obj;
		if (SOPInstanceUID == null) {
			if (other.SOPInstanceUID != null)
				return false;
		} else if (!SOPInstanceUID.equals(other.SOPInstanceUID))
			return false;
		if (originalSOPInstanceUID == null) {
			if (other.originalSOPInstanceUID != null)
				return false;
		} else if (!originalSOPInstanceUID.equals(other.originalSOPInstanceUID))
			return false;
		return true;
	}	
	
	@Override
	public int compareTo(SOPInstance that) 
	{
		return this.SOPInstanceUID.compareTo(that.SOPInstanceUID);
	}

    public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getLastUpdateDateTime() {
		return LastUpdateDateTime;
	}

	public String getSOPInstanceUID() {
		return SOPInstanceUID;
	}

	public void setSOPInstanceUID(String instanceUID) {
		SOPInstanceUID = instanceUID;
	}

	public String getOriginalSOPInstanceUID() {
		return originalSOPInstanceUID;
	}

	public void setOriginalSOPInstanceUID(String originalSOPInstanceUID) {
		this.originalSOPInstanceUID = originalSOPInstanceUID;
	}

	public String getSOPClassUID() {
		return SOPClassUID;
	}

	public void setSOPClassUID(String classUID) {
		SOPClassUID = classUID;
	}

	public String getTypeIX() {
		return typeIX;
	}

	public void setTypeIX(String typeIX) {
		this.typeIX = typeIX;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAcqDateTime() {
		return acqDateTime;
	}

	public void setAcqDateTime(String acqDateTime) {
		this.acqDateTime = acqDateTime;
	}

	public String getAcqNumber() {
		return acqNumber;
	}

	public void setAcqNumber(String acqNumber) {
		this.acqNumber = acqNumber;
	}

	public String getInstanceNumber() {
		return instanceNumber;
	}

	public void setInstanceNumber(String instanceNumber) {
		this.instanceNumber = instanceNumber;
	}

	public String getIsKeyimage() {
		return isKeyimage;
	}

	public void setIsKeyimage(String isKeyimage) {
		this.isKeyimage = isKeyimage;
	}

	public String getImageNotOK() {
		return imageNotOK;
	}

	public void setImageNotOK(String imageNotOK) {
		this.imageNotOK = imageNotOK;
	}

	public String getContrastBolusAgent() {
		return contrastBolusAgent;
	}

	public void setContrastBolusAgent(String contrastBolusAgent) {
		this.contrastBolusAgent = contrastBolusAgent;
	}

	public String getImagePosition() {
		return imagePosition;
	}

	public void setImagePosition(String imagePosition) {
		this.imagePosition = imagePosition;
	}

	public String getImageOrientation() {
		return imageOrientation;
	}

	public void setImageOrientation(String imageOrientation) {
		this.imageOrientation = imageOrientation;
	}

	public String getPatientOrientation() {
		return patientOrientation;
	}

	public void setPatientOrientation(String patientOrientation) {
		this.patientOrientation = patientOrientation;
	}

	public String getPhotometricInterptation() {
		return photometricInterptation;
	}

	public void setPhotometricInterptation(String photometricInterptation) {
		this.photometricInterptation = photometricInterptation;
	}

	public String getNumberOfFrames() {
		return numberOfFrames;
	}

	public void setNumberOfFrames(String numberOfFrames) {
		this.numberOfFrames = numberOfFrames;
	}

	public String getRows() {
		return rows;
	}

	public void setRows(String rows) {
		this.rows = rows;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public String getPixelSpacing() {
		return pixelSpacing;
	}

	public void setPixelSpacing(String pixelSpacing) {
		this.pixelSpacing = pixelSpacing;
	}

	public String getSamplesPerpixel() {
		return samplesPerPixel;
	}

	public void setSamplesPerpixel(String samplesPerpixel) {
		this.samplesPerPixel = samplesPerpixel;
	}

	public String getBitsAllocated() {
		return bitsAllocated;
	}

	public void setBitsAllocated(String bitsAllocated) {
		this.bitsAllocated = bitsAllocated;
	}

	public String getBitsStored() {
		return bitsStored;
	}

	public void setBitsStored(String bitsStored) {
		this.bitsStored = bitsStored;
	}

	public String getHighBit() {
		return highBit;
	}

	public void setHighBit(String highBit) {
		this.highBit = highBit;
	}

	public String getPixelRepresentation() {
		return pixelRepresentation;
	}

	public void setPixelRepresentation(String pixelRepresentation) {
		this.pixelRepresentation = pixelRepresentation;
	}

	public String getRescaleIntercept() {
		return rescaleIntercept;
	}

	public void setRescaleIntercept(String rescaleIntercept) {
		this.rescaleIntercept = rescaleIntercept;
	}

	public String getRescaleSlope() {
		return rescaleSlope;
	}

	public void setRescaleSlope(String rescaleSlope) {
		this.rescaleSlope = rescaleSlope;
	}

	public String getWindowCenter() {
		return windowCenter;
	}

	public void setWindowCenter(String windowCenter) {
		this.windowCenter = windowCenter;
	}

	public String getWindowWidth() {
		return windowWidth;
	}

	public void setWindowWidth(String windowWidth) {
		this.windowWidth = windowWidth;
	}

	public String getPlateID() {
		return plateID;
	}

	public void setPlateID(String plateID) {
		this.plateID = plateID;
	}

	public String getSliceThickness() {
		return SliceThickness;
	}

	public void setSliceThickness(String sliceThickness) {
		SliceThickness = sliceThickness;
	}

	public String getReconstructionDiameter() {
		return reconstructionDiameter;
	}

	public void setReconstructionDiameter(String reconstructionDiameter) {
		this.reconstructionDiameter = reconstructionDiameter;
	}

	public String getScanningSequence() {
		return scanningSequence;
	}

	public void setScanningSequence(String scanningSequence) {
		this.scanningSequence = scanningSequence;
	}

	public String getScanningVariant() {
		return scanningVariant;
	}

	public void setScanningVariant(String scanningVariant) {
		this.scanningVariant = scanningVariant;
	}

	public String getMrAcqType() {
		return mrAcqType;
	}

	public void setMrAcqType(String mrAcqType) {
		this.mrAcqType = mrAcqType;
	}

	public String getAcqContextDescription() {
		return acqContextDescription;
	}

	public void setAcqContextDescription(String acqContextDescription) {
		this.acqContextDescription = acqContextDescription;
	}

	public String getRepetitionTime() {
		return repetitionTime;
	}

	public void setRepetitionTime(String repetitionTime) {
		this.repetitionTime = repetitionTime;
	}

	public String getEchoTime() {
		return echoTime;
	}

	public void setEchoTime(String echoTime) {
		this.echoTime = echoTime;
	}

	public String getReferencedSOPInstances() {
		return referencedSOPInstances;
	}

	public void setReferencedSOPInstances(String referencedSOPInstances) {
		this.referencedSOPInstances = referencedSOPInstances;
	}
	public String getSourceOfReferencedInstances() {
		return sourceOfReferencedInstances;
	}

	public void setSourceOfReferencedInstances(String sourceOfReferencedInstances) {
		this.sourceOfReferencedInstances = sourceOfReferencedInstances;
	}
	public String getMicroscopicObjective() {
		return microscopicObjective;
	}

	public void setMicroscopicObjective(String microscObj) {
		this.microscopicObjective = microscObj;
	}

	public String getLabSliceID() {
		return labSliceID;
	}

	public void setLabSliceID(String labSliceID) {
		this.labSliceID = labSliceID;
	}

	public String getHistologicalStain() {
		return histologicalStain;
	}

	public void setHistologicalStain(String histologicalStain) {
		this.histologicalStain = histologicalStain;
	}

	public String getIEN()
	{
		return ien;
	}

	public void setIEN(String sopInstanceIEN)
	{
		this.ien = sopInstanceIEN;
	}

	public String getSeriesIEN()
	{
		return seriesIEN;
	}

	public void setSeriesIEN(String seriesIEN)
	{
		this.seriesIEN = seriesIEN;
	}

	public void setTransferSyntaxUid(String transferSyntaxUid) {
		this.transferSyntaxUid = transferSyntaxUid;
	}

	public String getTransferSyntaxUid() {
		return transferSyntaxUid;
	}

	public String getImageLaterality() {
		return imageLaterality;
	}

	public void setImageLaterality(String imageLaterality) {
		this.imageLaterality = imageLaterality;
	}
}
