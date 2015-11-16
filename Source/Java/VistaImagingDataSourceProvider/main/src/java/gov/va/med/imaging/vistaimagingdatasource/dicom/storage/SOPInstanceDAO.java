/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Nov, 2009
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswlouthj
  Description: DICOM Study cache manager. Maintains the cache of study instances
  			   and expires old studies after 15 minutes. 

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

package gov.va.med.imaging.vistaimagingdatasource.dicom.storage;

import gov.va.med.imaging.core.interfaces.exceptions.ParentREFDeletedMethodException;
import gov.va.med.imaging.exchange.business.dicom.SOPInstance;
import gov.va.med.imaging.exchange.business.storage.exceptions.CreationException;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.util.HashMap;

public class SOPInstanceDAO extends EntityDAO<SOPInstance>
{
	private String RPC_ATTACH_SOP_INSTANCE = "MAGV ATTACH SOP";
//	private String RPC_FIND_SOP_BY_UID= "MAGV FIND SOP BY UID";

	private String DB_PARENT_IEN = "SERIES REFERENCE"; // was "PARENT IEN"; // the immediate parent of the child entity

	private String SOP_INSTANCE_UID = "SOP INSTANCE UID";
	private String SOP_ORIG_INST_UID = "ORIGINAL SOP INSTANCE UID";
	private String SOP_CLASS_UID = "SOP CLASS UID";
	private String SOP_TYPE_IX = "TYPE INDEX"; // fixed 05/28/10
	private String SOP_DESCRIPTION = "DESCRIPTION";
	private String SOP_ACQ_DATIME = "ACQUISITION DATE/TIME";
	private String SOP_ACQ_NUMBER = "ACQUISITION NUMBER";
	private String SOP_INSTANCE_NUMBER = "INSTANCE NUMBER";
	private String SOP_KEY_IMAGE = "KEY OBJECT"; // was "KEY IMAGE?"; // fixed 05/28/10
	private String SOP_IMAGE_NOT_OK = "QUESTIONABLE IMAGE"; // was "IMAGE NOT OK?"; // fixed 05/28/10
	private String SOP_CONTRAST_BOLUS_AGENT = "CONTRAST OR BOLUS AGENT"; // fixed 05/28/10
	private String SOP_IMAGE_POSITION = "IMAGE POSITION";
	private String SOP_IMAGE_LATERALITY =  "IMAGE LATERALITY"; // added 09/05/11
	private String SOP_IMAGE_ORIENTATION = "IMAGE ORIENTATION";
	private String SOP_PATIENT_ORIENTATION = "PATIENT ORIENTATION";
	private String SOP_PHOTOMETRIC_INTERP = "PHOTOMETRIC INTERPRETATION";
	private String SOP_NUMBER_OF_FRAMES = "NUMBER OF FRAMES";
	private String SOP_ROWS = "ROWS";
	private String SOP_COLUMNS = "COLUMNS";
	private String SOP_PIXEL_SPACING = "PIXEL SPACING";
	private String SOP_SAMPLES_PER_PIXEL = "SAMPLES PER PIXEL";
	private String SOP_BITS_ALLOCATED = "BITS ALLOCATED";
	private String SOP_BITS_STORED = "BITS STORED";
	private String SOP_HIGH_BIT = "HIGH BIT";
	private String SOP_PIXEL_REPRESENTATION = "PIXEL REPRESENTATION";
	private String SOP_RESCALE_INTERCEPT = "RESCALE INTERCEPT";
	private String SOP_RESCALE_SLOPE = "RESCALE SLOPE";
	private String SOP_WINDOW_CENTER = "WINDOW CENTER";
	private String SOP_WINDOW_WIDTH = "WINDOW WIDTH";
	private String SOP_PLATE_ID = "PLATE ID";
	private String SOP_SLICE_THICKNESS = "SLICE THICKNESS";
	private String SOP_RECONSTRUCTION_DIAMETER = "RECONSTRUCTION DIAMETER";
	private String SOP_SCANNING_SEQUENCE = "SCANNING SEQUENCE";
	private String SOP_SCANNING_VARIANT = "SCANNING VARIANT";
	private String SOP_MR_ACQ_TYPE = "MR ACQUISITION TYPE";
	private String SOP_ACQ_CONTEXT_DESCRIPTION = "ACQUISITION CONTEXT DESC";
	private String SOP_REPETITION_TIME = "REPETITION TIME";
	private String SOP_ECHO_TIME = "ECHO TIME";
	private String SOP_REF_SOP_INSTANCES = "REFERENCED SOP INSTANCES";
	private String SOP_SOURCE_OF_REF_INSTANCES = "SOURCE OF REFERENCE INSTANCE";
	private String SOP_MICROSCOPIC_OBJECTIVE = "MICROSCOPIC OBJECTIVE";
	private String SOP_LAB_SLICE_ID =  "LAB SLIDE ID";
	private String SOP_HISTOL_STAIN =  "HISTOLOGICAL STAIN";
	//
	// Constructor
	//
	public SOPInstanceDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}
	

	//
	// Creation overrides
	//
	@Override
	public VistaQuery generateCreateQuery(SOPInstance sopInstance) 
	{
		VistaQuery vm = new VistaQuery(RPC_ATTACH_SOP_INSTANCE);
		HashMap <String, String> hm = new HashMap <String, String>();
		hm.put("1", DB_PARENT_IEN + dbSeparator + sopInstance.getSeriesIEN());
		hm.put("2", SOP_INSTANCE_UID + dbSeparator + sopInstance.getSOPInstanceUID());
		hm.put("3", SOP_ORIG_INST_UID + dbSeparator + sopInstance.getOriginalSOPInstanceUID());
		hm.put("4", SOP_CLASS_UID + dbSeparator + sopInstance.getSOPClassUID());
		hm.put("5", SOP_TYPE_IX + dbSeparator + sopInstance.getTypeIX());
		hm.put("6", SOP_DESCRIPTION + dbSeparator + sopInstance.getDescription());
		hm.put("7", SOP_ACQ_DATIME + dbSeparator + sopInstance.getAcqDateTime());
		hm.put("8", SOP_ACQ_NUMBER + dbSeparator + sopInstance.getAcqNumber());
		hm.put("9", SOP_INSTANCE_NUMBER + dbSeparator + sopInstance.getInstanceNumber());
		hm.put("10", SOP_KEY_IMAGE + dbSeparator + sopInstance.getIsKeyimage());
		hm.put("11", SOP_IMAGE_NOT_OK + dbSeparator + sopInstance.getImageNotOK());
		hm.put("12", SOP_CONTRAST_BOLUS_AGENT + dbSeparator + sopInstance.getContrastBolusAgent());
		hm.put("13", SOP_IMAGE_POSITION + dbSeparator + sopInstance.getImagePosition());
		hm.put("14", SOP_IMAGE_ORIENTATION + dbSeparator + sopInstance.getImageOrientation());
		hm.put("15", SOP_PATIENT_ORIENTATION + dbSeparator + sopInstance.getPatientOrientation());
		hm.put("16", SOP_PHOTOMETRIC_INTERP + dbSeparator + sopInstance.getPhotometricInterptation());
		hm.put("17", SOP_NUMBER_OF_FRAMES + dbSeparator + sopInstance.getNumberOfFrames());
		hm.put("18", SOP_ROWS + dbSeparator + sopInstance.getRows());
		hm.put("19", SOP_COLUMNS + dbSeparator + sopInstance.getColumns());
		hm.put("20", SOP_PIXEL_SPACING + dbSeparator + sopInstance.getPixelSpacing());
		hm.put("21", SOP_SAMPLES_PER_PIXEL + dbSeparator + sopInstance.getSamplesPerpixel());
		hm.put("22", SOP_BITS_ALLOCATED + dbSeparator + sopInstance.getBitsAllocated());
		hm.put("23", SOP_BITS_STORED + dbSeparator + sopInstance.getBitsStored());
		hm.put("24", SOP_HIGH_BIT + dbSeparator + sopInstance.getHighBit());
		hm.put("25", SOP_PIXEL_REPRESENTATION + dbSeparator + sopInstance.getPixelRepresentation());
		hm.put("26", SOP_RESCALE_INTERCEPT + dbSeparator + sopInstance.getRescaleIntercept());
		hm.put("27", SOP_RESCALE_SLOPE + dbSeparator + sopInstance.getRescaleSlope());
		hm.put("28", SOP_WINDOW_CENTER + dbSeparator + sopInstance.getWindowCenter());
		hm.put("29", SOP_WINDOW_WIDTH + dbSeparator + sopInstance.getWindowWidth());
		hm.put("30", SOP_PLATE_ID + dbSeparator + sopInstance.getPlateID());
		hm.put("31", SOP_SLICE_THICKNESS + dbSeparator + sopInstance.getSliceThickness());
		hm.put("32", SOP_RECONSTRUCTION_DIAMETER + dbSeparator + sopInstance.getReconstructionDiameter());
		hm.put("33", SOP_SCANNING_SEQUENCE + dbSeparator + sopInstance.getScanningSequence());
		hm.put("34", SOP_SCANNING_VARIANT + dbSeparator + sopInstance.getScanningVariant());
		hm.put("35", SOP_MR_ACQ_TYPE + dbSeparator + sopInstance.getMrAcqType());
		hm.put("36", SOP_ACQ_CONTEXT_DESCRIPTION + dbSeparator + sopInstance.getAcqContextDescription());
		hm.put("37", SOP_REPETITION_TIME + dbSeparator + sopInstance.getRepetitionTime());
		hm.put("38", SOP_ECHO_TIME + dbSeparator + sopInstance.getEchoTime());
		hm.put("39", SOP_REF_SOP_INSTANCES + dbSeparator + sopInstance.getReferencedSOPInstances());
		hm.put("40", SOP_SOURCE_OF_REF_INSTANCES + dbSeparator + sopInstance.getSourceOfReferencedInstances());
		hm.put("41", SOP_MICROSCOPIC_OBJECTIVE + dbSeparator + sopInstance.getMicroscopicObjective());
		hm.put("42", SOP_LAB_SLICE_ID + dbSeparator + sopInstance.getLabSliceID());
		hm.put("43", SOP_HISTOL_STAIN + dbSeparator + sopInstance.getHistologicalStain());
		hm.put("44", SOP_IMAGE_LATERALITY + dbSeparator + sopInstance.getImageLaterality());
		vm.addParameter(VistaQuery.LIST, hm);
		return vm;
	}

	@Override
	public SOPInstance translateCreate(SOPInstance sopInstance, String returnValue) 
							throws CreationException, ParentREFDeletedMethodException
	{
		//Check if the Series REF was deleted in the new data structure.  If deleted, throw exception.
		if(returnValue == null){
			throw new CreationException("RPC returned value for "+RPC_ATTACH_SOP_INSTANCE+" is null.");
		}
		String[] results = StringUtils.Split(returnValue, DB_OUTPUT_SEPARATOR1);
		if (results[0].equals("-100")){ // Parent IEN Deleted
			throw new ParentREFDeletedMethodException("Parent Series REF IEN was deleted: " + results[1]);
		}
		
		sopInstance.setIEN(translateNewEntityIEN(returnValue, true));
		return sopInstance;
	}
}
