package gov.va.med.imaging.dicom.utilities.reconstitution.interfaces;

import gov.va.med.imaging.SizedInputStream;
import gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet;
import gov.va.med.imaging.dicom.utilities.exceptions.GenericDicomReconstitutionException;
import gov.va.med.imaging.dicom.utilities.exceptions.GenericDicomUtilitiesTGAFileException;
import gov.va.med.imaging.dicom.utilities.exceptions.GenericDicomUtilitiesTGAFileNotFoundException;
import gov.va.med.imaging.dicom.utilities.exceptions.GenericDicomUtilitiesTextFileException;
import gov.va.med.imaging.dicom.utilities.exceptions.GenericDicomUtilitiesTextFileExtractionException;
import gov.va.med.imaging.dicom.utilities.exceptions.GenericDicomUtilitiesTextFileNotFoundException;

import java.util.HashMap;

public interface IDicomReconstitution {

	/**
	 * Assemble a DICOM object based on the existing Text and Targa files in Legacy Vista 
	 * Imaging and additional changes from Vista HIS.  This Control class performs the 
	 * actual work.
	 * 
	 * @param textFilename represents the name of the Text file.
	 * @param tgaFilename represents the name of the Targa file.
	 * @param hischanges represents the Vista HIS changes.
	 * @return represents the generic DicomDataSet object.
	 */
	public abstract IDicomDataSet assembleDicomObject(String textFilename,
			String tgaFilename, HashMap<String, String> hisChanges)
			throws GenericDicomUtilitiesTextFileNotFoundException,
			GenericDicomUtilitiesTextFileException,
			GenericDicomUtilitiesTextFileExtractionException,
			GenericDicomUtilitiesTGAFileException,
			GenericDicomUtilitiesTGAFileNotFoundException;

	/**
	 * Assemble a DICOM stream based on the existing Text and Targa streams from Legacy Vista 
	 * Imaging and additional changes from Vista HIS. Note: the stream format of the TXT file
	 * is expected to contain the HIS changes that represents the latest local Vista database
	 * values to the patient/study.
	 * 
	 * @param sizedTextStream represents the stream of VistA Imaging TXT data with byte size.
	 * @param sizedTgaStream represents the stream of the VistA Imaging Targa file with byte size..
	 * @param hischanges represents the Vista HIS changes.
	 * @return represents the generic DicomDataSet object (part 10 format) in a byte array.
	 */
	public abstract IDicomDataSet assembleDicomObject(
			SizedInputStream sizedTextStream, SizedInputStream sizedTgaStream,
			HashMap<String, String> hisChanges)
			throws GenericDicomUtilitiesTextFileException,
			GenericDicomUtilitiesTextFileExtractionException,
			GenericDicomUtilitiesTGAFileException;

	/**
	 * Assemble a DICOM stream based on the existing Text and Targa streams from Legacy Vista 
	 * Imaging and additional changes from Vista HIS. Note: the stream format of the TXT file
	 * is expected to contain the HIS changes that represents the latest local Vista database
	 * values to the patient/study.
	 * 
	 * @param sizedTextStream represents the stream of VistA Imaging TXT data with byte size.
	 * @param sizedTgaStream represents the stream of the VistA Imaging Targa file with byte size..
	 * @return represents the generic DicomDataSet object (part 10 format) in a byte array.
	 */
	public abstract byte[] assembleDicomStream(
			SizedInputStream sizedTextStream, SizedInputStream sizedTgaStream)
			throws GenericDicomUtilitiesTextFileNotFoundException,
			GenericDicomUtilitiesTextFileException,
			GenericDicomUtilitiesTextFileExtractionException,
			GenericDicomUtilitiesTGAFileException,
			GenericDicomUtilitiesTGAFileNotFoundException;

	/**
	 * Update a DICOM object based on the existing DICOM file in Legacy Vista Imaging and
	 * additional changes from Vista HIS.
	 * 
	 * @param dicomFile represents the DICOM file path and name.
	 * @param hisChanges represents the Vista HIS changes.
	 * @return represents the generic DicomDataSet object produced.
	 */
	public abstract IDicomDataSet updateDicomObject(String dicomFile,
			HashMap<String, String> hisChanges)
			throws GenericDicomReconstitutionException;

	/**
	 * Update a DICOM stream based on the existing DICOM stream from Legacy Vista Imaging and
	 * additional changes from Vista HIS. Note: the stream format of the TXT file is expected
	 * to contain the HIS changes that represents the latest local Vista database values to
	 * the patient/study.
	 * 
	 * @param sizedDicomStream represents the stream of VistA Imaging DCM (DICOM) data with byte size.
	 * @param sizedTextStream represents the stream of VistA Imaging TXT data with byte size.
	 * @param hisChanges represents the Vista HIS changes.
	 * @return represents the generic DicomDataSet object (part 10 format) in a byte array.
	 */
	public abstract IDicomDataSet updateDicomObject(
			SizedInputStream sizedDicomStream,
			HashMap<String, String> hisChanges)
			throws GenericDicomReconstitutionException;

	/**
	 * Update a DICOM stream based on the existing DICOM stream from Legacy Vista Imaging and
	 * additional changes from Vista HIS. Note: the stream format of the TXT file is expected
	 * to contain the HIS changes that represents the latest local Vista database values to
	 * the patient/study.
	 * 
	 * @param sizedDicomStream represents the stream of VistA Imaging DCM (DICOM) data with byte size.
	 * @param sizedTextStream represents the stream of VistA Imaging TXT data with byte size.
	 * @return represents the generic DicomDataSet object (part 10 format) in a byte array.
	 */
	public abstract byte[] updateDicomStream(SizedInputStream sizedDicomStream,
			SizedInputStream sizedTextStream)
			throws GenericDicomReconstitutionException;

}