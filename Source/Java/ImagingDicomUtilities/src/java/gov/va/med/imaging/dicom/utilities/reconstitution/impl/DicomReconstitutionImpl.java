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
package gov.va.med.imaging.dicom.utilities.reconstitution.impl;

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
import gov.va.med.imaging.dicom.utilities.reconstitution.interfaces.IDicomReconstitution;
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
public class DicomReconstitutionImpl implements IDicomReconstitution {
    
    private static final Logger LOGGER = Logger.getLogger (DicomReconstitutionImpl.class);

    /**
     * Constructor
     *
     * 
     */
    public DicomReconstitutionImpl() {
        super();
        //
    }
    
    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utilities.reconstitution.IDicomReconstution#assembleDicomObject(java.lang.String, java.lang.String, java.util.HashMap)
	 */
    public IDicomDataSet assembleDicomObject(String textFilename,
            String tgaFilename, HashMap<String, String> hisChanges)
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

    
    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utilities.reconstitution.IDicomReconstution#assembleDicomObject(gov.va.med.imaging.SizedInputStream, gov.va.med.imaging.SizedInputStream, java.util.HashMap)
	 */
    public IDicomDataSet assembleDicomObject(SizedInputStream sizedTextStream, 
    		SizedInputStream sizedTgaStream, HashMap<String, String> hisChanges)
            throws GenericDicomUtilitiesTextFileException, 
            GenericDicomUtilitiesTextFileExtractionException, GenericDicomUtilitiesTGAFileException{
        
        IDicomDataSet toolkitDDS;
        OriginalPixelDataInfo originalPixelData = new OriginalPixelDataInfo();
        try{
            LOGGER.info("Generic DICOM Layer: Start parsing input streams ...");
            // Extract all DICOM data from the Text file, build dataset;
            LegacyTextFileParser textParser = new LegacyTextFileParser();
            BufferedReader buffer = new BufferedReader(new InputStreamReader(sizedTextStream.getInStream()));
            toolkitDDS = textParser.createDicomDataSet(buffer, originalPixelData);

            if(hisChanges != null) { // make sure acquisitionSite is extracted from update section if available
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
            toolkitDDS.updateHISChangesToDDS(hisChanges);
            
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
        return toolkitDDS;
    }

        
    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utilities.reconstitution.IDicomReconstution#assembleDicomStream(gov.va.med.imaging.SizedInputStream, gov.va.med.imaging.SizedInputStream)
	 */
    public byte[] assembleDicomStream(SizedInputStream sizedTextStream, 
    		SizedInputStream sizedTgaStream)
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



    
    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utilities.reconstitution.IDicomReconstution#updateDicomObject(java.lang.String, java.util.HashMap)
	 */
    public IDicomDataSet updateDicomObject(String dicomFile, HashMap<String, String> hisChanges)
    			throws GenericDicomReconstitutionException {
        
        IDicomDataSet toolkitDDS = null;
        DicomFileExtractor fileExtractor = null;
        // Add the Vista HIS changes to the generic DicomDataSet object.  This is done
        // by calling Csaba's code.
        try{
            fileExtractor = new DicomFileExtractor();
            toolkitDDS = fileExtractor.getDDSFromDicomFile(dicomFile);
            toolkitDDS.updateHISChangesToDDS(hisChanges);
            
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

    
    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utilities.reconstitution.IDicomReconstution#updateDicomObject(gov.va.med.imaging.SizedInputStream, java.util.HashMap)
	 */
    public IDicomDataSet updateDicomObject(SizedInputStream sizedDicomStream, HashMap<String, String> hisChanges)
    			throws GenericDicomReconstitutionException {

    	IDicomDataSet toolkitDDS;
        DicomFileExtractor fileExtractor;
        try{
            fileExtractor = new DicomFileExtractor();
            toolkitDDS = fileExtractor.getDDSFromDicomStream(sizedDicomStream);
           	toolkitDDS.updateHISChangesToDDS(hisChanges);            
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
        return toolkitDDS;
    }


    /* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.utilities.reconstitution.IDicomReconstution#updateDicomStream(gov.va.med.imaging.SizedInputStream, gov.va.med.imaging.SizedInputStream)
	 */
    public byte[] updateDicomStream(SizedInputStream sizedDicomStream, SizedInputStream sizedTextStream)
    			throws GenericDicomReconstitutionException {

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
