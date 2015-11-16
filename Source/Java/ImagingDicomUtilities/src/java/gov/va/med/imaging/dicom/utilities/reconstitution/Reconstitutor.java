/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: September 26, 2005
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
package gov.va.med.imaging.dicom.utilities.reconstitution;

import gov.va.med.imaging.SizedInputStream;
import gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet;
import gov.va.med.imaging.dicom.dcftoolkit.utilities.exceptions.DicomFileException;
import gov.va.med.imaging.dicom.dcftoolkit.utilities.exceptions.TGAFileException;
import gov.va.med.imaging.dicom.dcftoolkit.utilities.exceptions.TGAFileNotFoundException;
import gov.va.med.imaging.dicom.dcftoolkit.utilities.reconstitution.DicomFileExtractor;
import gov.va.med.imaging.dicom.dcftoolkit.utilities.reconstitution.LegacyTGAFileParser;
import gov.va.med.imaging.dicom.dcftoolkit.utilities.reconstitution.LegacyTextFileParser;
import gov.va.med.imaging.dicom.dcftoolkit.utilities.reconstitution.OriginalPixelDataInfo;
import gov.va.med.imaging.dicom.utilities.exceptions.GenericDicomReconstitutionException;
import gov.va.med.imaging.dicom.utilities.exceptions.GenericDicomUtilitiesTGAFileException;
import gov.va.med.imaging.dicom.utilities.exceptions.GenericDicomUtilitiesTGAFileNotFoundException;
import gov.va.med.imaging.dicom.utilities.exceptions.GenericDicomUtilitiesTextFileException;
import gov.va.med.imaging.dicom.utilities.exceptions.GenericDicomUtilitiesTextFileExtractionException;
import gov.va.med.imaging.dicom.utilities.exceptions.GenericDicomUtilitiesTextFileNotFoundException;
import gov.va.med.imaging.exceptions.TextFileException;
import gov.va.med.imaging.exceptions.TextFileExtractionException;
import gov.va.med.imaging.exceptions.TextFileNotFoundException;
import gov.va.med.imaging.exchange.business.dicom.exceptions.DicomException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 *
 * @author William Peterson
 * extended by Csaba Titton
 * 			for ViX streaming
 */
public class Reconstitutor {
    
    private static final Logger LOGGER = Logger.getLogger (Reconstitutor.class);

    /**
     * Constructor
     *
     * 
     */
    public Reconstitutor() {
        super();
        //
    }
    
    /**
     * Assemble a DICOM object based on the existing Text and Targa files in Legacy Vista 
     * Imaging and additional changes from Vista HIS.  This Control class performs the 
     * actual work.
     * 
     * @param textFilename represents the name of the Text file.
     * @param tgaFilename represents the name of the Targa file.
     * @param hischanges represents the Vista HIS changes.
     * @param toValidate represents if the DicomDataSet object is to be validate before returning.
     * @return represents the generic DicomDataSet object.
     */
    public IDicomDataSet assembleDicomObject(String textFilename,
            String tgaFilename, HashMap<String, String> hisChanges, boolean toValidate)
            throws GenericDicomUtilitiesTextFileNotFoundException, GenericDicomUtilitiesTextFileException, 
            GenericDicomUtilitiesTextFileExtractionException, GenericDicomUtilitiesTGAFileException,
            GenericDicomUtilitiesTGAFileNotFoundException{
        
        IDicomDataSet toolkitDDS = null;
        OriginalPixelDataInfo originalPixelData = new OriginalPixelDataInfo();
        try{
            LOGGER.info(this.getClass().getName() + ": Generic DICOM Layer: " +
                    "parsing Text file " + textFilename + " ...");
            //Invoke the extraction of data from the Text file.
            LegacyTextFileParser textParser = new LegacyTextFileParser();
            //FUTURE A better design would have the OriginalPixelDataInfo object
            //  encapsulated within the DicomDataSet since its being pushed around.
            toolkitDDS = textParser.createDicomDataSet(textFilename, originalPixelData);
            
            LOGGER.info(this.getClass().getName() + ": Generic DICOM Layer: " +
                    "parsing TGA/BIG file " + tgaFilename + " ...");
            String acquisitionSite = null;
            if(hisChanges.containsKey("0032,1020")){
                acquisitionSite = (String)hisChanges.get("0032,1020");
                toolkitDDS.setAcquisitionSite(acquisitionSite);
            }
            //Invoke the extraction of pixel data from the Targa file.
            LegacyTGAFileParser tgaParser = new LegacyTGAFileParser();
            tgaParser.updateDicomDataSetWithPixelData(toolkitDDS, tgaFilename, originalPixelData);
            //Add the Vista HIS changes to the generic DicomDataSet object.  This is done
            // by calling Csaba's code.
            toolkitDDS.updateHISChangesToDDS(hisChanges);
            
            //Validate the DicomDataSet if desired.
            if(toValidate){
                //FUTURE Validate the DicomDataSet.
            }
            return toolkitDDS;
        }
        catch(TextFileNotFoundException noText){
            LOGGER.error(noText.getMessage());
            LOGGER.error(this.getClass().getName() + ": " +
                    "\nException thrown while assembling Dicom Object.");
            throw new GenericDicomUtilitiesTextFileNotFoundException(
                    "Failure to assemble Dicom Object.", noText);
        }
        catch(TextFileExtractionException extract){
            LOGGER.error(extract.getMessage());
            LOGGER.error(this.getClass().getName() + ": " +
                    "\nException thrown while assembling Dicom Object.");
            throw new GenericDicomUtilitiesTextFileExtractionException(
                    "Failure to assemble Dicom Object.", extract);
        }
        catch(TextFileException e){
            LOGGER.error(e.getMessage());
            LOGGER.error(this.getClass().getName() + ": " +
                    "\nException thrown while assembling Dicom Object.");
            throw new GenericDicomUtilitiesTextFileException(
                    "Failure to assemble Dicom Object.", e);
        }
        catch(TGAFileNotFoundException notga){
            LOGGER.error(notga.getMessage());
            LOGGER.error(this.getClass().getName() + ": " +
                    "\nException thrown while assembling Dicom Object.");
            throw new GenericDicomUtilitiesTGAFileException(
                    "Failure to assemble Dicom Object.", notga);
        }
        catch(TGAFileException badtga){
            LOGGER.error(badtga.getMessage());
            LOGGER.error(this.getClass().getName()+": " +
                    "\nException thrown while assembling Dicom Object.");
            throw new GenericDicomUtilitiesTGAFileException(
                    "Failure to assemble Dicom Object.", badtga);
        }
        catch(DicomException de){
            LOGGER.error(de.getMessage());
            LOGGER.error(this.getClass().getName() + ": " +
                    "\nException thrown while assembling Dicom Object.");
            throw new GenericDicomUtilitiesTGAFileException(
                    "Failure to assemble Dicom Object.", de);
        }
    }

    /**
     * Assemble a DICOM stream based on the existing Text and Targa streams from Legacy Vista 
     * Imaging and additional changes from Vista HIS. Note: the stream format of the TXT file
     * is expected to contain the HIS changes that represents the latest local Vista database
     * values to the patient/study.
     * 
     * @param sizedTextStream represents the stream of VistA Imaging TXT data with byte size.
     * @param sizedTgaStream represents the stream of the VistA Imaging Targa file with byte size..
     * @param toValidate represents if the DicomDataSet object is to be validate before returning.
     * @return represents the generic DicomDataSet object (part 10 format) in a byte array.
     */
    public byte[] assembleDicomStream(SizedInputStream sizedTextStream, 
    		SizedInputStream sizedTgaStream, boolean toValidate)
            throws GenericDicomUtilitiesTextFileNotFoundException, GenericDicomUtilitiesTextFileException, 
            GenericDicomUtilitiesTextFileExtractionException, GenericDicomUtilitiesTGAFileException,
            GenericDicomUtilitiesTGAFileNotFoundException{
        
    	HashMap<String, String> hisChanges=null;
        IDicomDataSet toolkitDDS;
        OriginalPixelDataInfo originalPixelData = new OriginalPixelDataInfo();
        try{
            LOGGER.info("Generic DICOM Layer: Start parsing input streams ...");
            // Extract all DICOM data from the Text file, build dataset;
            // Make sure ViX updates at the end of file are processed too
            LegacyTextFileParser textParser = new LegacyTextFileParser();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(sizedTextStream.getInStream()));
            toolkitDDS = textParser.createDicomDataSet(buffer, originalPixelData);
            hisChanges=textParser.getHisUpdates(buffer);

            if (hisChanges!=null) { // make sure acquisitionSite is extracted from update section if available
	            String acquisitionSite = null;
	            if(hisChanges.containsKey("0032,1020")){
	                acquisitionSite = (String)hisChanges.get("0032,1020");
	                toolkitDDS.setAcquisitionSite(acquisitionSite);
	            }
            }
            //Invoke the extraction of pixel data from the Targa file.
            LegacyTGAFileParser tgaParser = new LegacyTGAFileParser();
            tgaParser.updateDicomDataSetWithPixelData(toolkitDDS, sizedTgaStream, originalPixelData);
            LOGGER.info("... Generic DICOM Layer: Parsing input streams completed.");
            // Extract the Vista HIS changes to the generic DicomDataSet object.  
            if (hisChanges!=null)
            	toolkitDDS.updateHISChangesToDDS(hisChanges);
            
            //Validate the DicomDataSet if desired.
            if(toValidate){
                //Validate the DicomDataSet.
            }
            return toolkitDDS.part10Buffer(false);
        }
        catch(TextFileExtractionException extract){
            LOGGER.error("Error: " + extract.getMessage());
            LOGGER.error("Exception thrown while assembling Dicom Object.");
            throw new GenericDicomUtilitiesTextFileExtractionException(
                    "Failure to assemble Dicom Object.", extract);
        }
        catch(TextFileException e){
            LOGGER.error("Error: " + e.getMessage());
            LOGGER.error("Exception thrown while assembling Dicom Object.");
            throw new GenericDicomUtilitiesTextFileException(
                    "Failure to assemble Dicom Object.", e);
        }
        catch(TGAFileNotFoundException notga){
            LOGGER.error("Error: " + notga.getMessage());
            LOGGER.error("Exception thrown while assembling Dicom Object.");
            throw new GenericDicomUtilitiesTGAFileException(
                    "Failure to assemble Dicom Object.", notga);
        }
        catch(TGAFileException badtga){
            LOGGER.error("Error: " + badtga.getMessage());
            LOGGER.error("Exception thrown while assembling Dicom Object.");
            throw new GenericDicomUtilitiesTGAFileException(
                    "Failure to assemble Dicom Object.", badtga);
        }
        catch(DicomException de){
            LOGGER.error("Error: " + de.getMessage());
            LOGGER.error("Exception thrown while assembling Dicom Object.");
            throw new GenericDicomUtilitiesTGAFileException("Failure to assemble Dicom Object.", de);
        }
    }

    /**
     * Update a DICOM object based on the existing DICOM file in Legacy Vista Imaging and
     * additional changes from Vista HIS.
     * 
     * @param dicomFile represents the DICOM file path and name.
     * @param hisChanges represents the Vista HIS changes.
     * @param toValidate represents if the DicomDataSet object is to be validate before returning.
     * @return represents the generic DicomDataSet object produced.
     */
    public IDicomDataSet updateDicomObject(String dicomFile, HashMap<String, String> hisChanges,
            boolean toValidate)throws GenericDicomReconstitutionException {
        
        IDicomDataSet toolkitDDS = null;
        DicomFileExtractor fileExtractor = null;
        // Add the Vista HIS changes to the generic DicomDataSet object.  This is done
        // by calling Csaba's code.
        try{
            fileExtractor = new DicomFileExtractor();
            toolkitDDS = fileExtractor.getDDSFromDicomFile(dicomFile);
            toolkitDDS.updateHISChangesToDDS(hisChanges);
            
            //Validate the DicomDataSet if desired.
            if(toValidate){
                //Validate the DicomDataSet.
            }
        }
        catch(DicomFileException file){
            LOGGER.error(file.getMessage());
            LOGGER.error(this.getClass().getName() + ": " +
                    "\nException thrown while updating Dicom Object.");
            throw new GenericDicomReconstitutionException("Failure to update Dicom Object.", file);
        }
        catch(DicomException de){
            LOGGER.error(de.getMessage());
            LOGGER.error(this.getClass().getName() + ": " +
                    "\nException thrown while updating Dicom Object.");
            throw new GenericDicomReconstitutionException("Failure to update Dicom Object.", de);
        }
        return toolkitDDS;
    }

    /**
     * Update a DICOM stream based on the existing DICOM stream from Legacy Vista Imaging and
     * additional changes from Vista HIS. Note: the stream format of the TXT file is expected
     * to contain the HIS changes that represents the latest local Vista database values to
     * the patient/study.
     * 
     * @param sizedDicomStream represents the stream of VistA Imaging DCM (DICOM) data with byte size.
     * @param sizedTextStream represents the stream of VistA Imaging TXT data with byte size.
     * @param toValidate represents if the DicomDataSet object is to be validate before returning.
     * @return represents the generic DicomDataSet object (part 10 format) in a byte array.
     */
    public byte[] updateDicomStream(SizedInputStream sizedDicomStream, SizedInputStream sizedTextStream,
            boolean toValidate)throws GenericDicomReconstitutionException {

    	HashMap<String,String> hisChanges=null;
    	IDicomDataSet toolkitDDS;
        DicomFileExtractor fileExtractor;
        //Add the Vista HIS changes to the generic DicomDataSet object.  This is done
        // by calling Csaba's code.
        try{
            fileExtractor = new DicomFileExtractor();
            toolkitDDS = fileExtractor.getDDSFromDicomStream(sizedDicomStream);
            // Extract all ViX update data section from the Text file and update
            // dataset with it;
            LegacyTextFileParser textParser = new LegacyTextFileParser();
            hisChanges=textParser.extractHisUpdatesfromTextStream(sizedTextStream);
            if (hisChanges!=null)
            	toolkitDDS.updateHISChangesToDDS(hisChanges);
            
            //Validate the DicomDataSet if desired.
            if(toValidate){
                //Validate the DicomDataSet.
            }
            return toolkitDDS.part10Buffer(true);
        }
        catch(DicomFileException file){
            LOGGER.error("Error: " + file.getMessage());
            LOGGER.error("Exception thrown while updating Dicom Stream.");
            throw new GenericDicomReconstitutionException("Failure to update Dicom Stream.", file);
        }
        catch(DicomException de){
            LOGGER.error("Error: " + de.getMessage());
            LOGGER.error("Exception thrown while updating Dicom Object.");
            throw new GenericDicomReconstitutionException("Failure to update Dicom Stream.", de);
        }
        catch(TextFileExtractionException extract){
            LOGGER.error("Error: " + extract.getMessage());
            LOGGER.error("Exception thrown while assembling Dicom Object.");
            throw new GenericDicomReconstitutionException(
                    "Failure to extract HIS Update data.", extract);
        }
        catch(TextFileException e){
            LOGGER.error("Error: " + e.getMessage());
            LOGGER.error("Exception thrown while assembling Dicom Object.");
            throw new GenericDicomReconstitutionException(
                    "Failure to handle Text file for HIS Updates.", e);
        }

    }
}
