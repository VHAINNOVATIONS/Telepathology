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

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.ParentREFDeletedMethodException;
import gov.va.med.imaging.exchange.business.dicom.PatientRef;
import gov.va.med.imaging.exchange.business.dicom.ProcedureRef;
import gov.va.med.imaging.exchange.business.dicom.SOPInstance;
import gov.va.med.imaging.exchange.business.dicom.Series;
import gov.va.med.imaging.exchange.business.dicom.Study;
import gov.va.med.imaging.exchange.business.dicom.importer.Order;
import gov.va.med.imaging.exchange.business.dicom.rdsr.CTDose;
import gov.va.med.imaging.exchange.business.dicom.rdsr.Dose;
import gov.va.med.imaging.exchange.business.dicom.rdsr.FluoroDose;
import gov.va.med.imaging.exchange.business.storage.exceptions.CreationException;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class RadiationDosageDAO extends EntityDAO<Dose>
{
	private static final String RPC_ATTACH_IRRADIATION_DOSE = "MAGV ATTACH IRRADIATION DOSE";
	private static final String RPC_GET_IRRADIATION_DOSE = "MAGV GET IRRADIATION DOSE";

	// CT Properties
	private static final String IRRADIATION_INSTANCE_UID = "IRRADIATION INSTANCE UID";
	private static final String TARGET_REGION = "TARGET REGION";
	private static final String CTDIVOL = "CTDIVOL";
	private static final String DLP = "DLP";
	private static final String PHANTOM_TYPE = "PHANTOM TYPE";
	
	// Fluoro Properties
	private static final String TOTAL_TIME_IN_FLUOROSCOPY = "TOTAL TIME IN FLUOROSCOPY"; 
	private static final String DOSE_AREA_PRODUCT = "DOSE AREA PRODUCTS";   
	private static final String DOSE_RP_TOTAL = "DOSE (RP) TOTAL (AKE)";
	private static final String FLUORO_DOSE_RP_TOTAL = "FLUORO DOSE (RP) TOTAL";
	private static final String FLUORO_DOSE_AREA_PRODUCT_TOTAL = "FLUORO DOSE AREA PRODUCT TOTAL";
	private static final String CINE_DOSE_RP_TOTAL = "CINE DOSE (RP) TOTAL";
	private static final String CINE_DOSE_AREA_PRODUCT_TOTAL = "CINE DOSE AREA PRODUCT TOTAL";
	private static final String CINE_TIME = "CINE TIME";

	//
	// Constructor
	//
	public RadiationDosageDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}
	

	//
	// Creation overrides
	//
	public Dose createDoseRecord(PatientRef patient, ProcedureRef procedure, Study study, Series series, Dose dose) 
	throws MethodException, ConnectionException
	{
		// Create the VistaQuery object
		VistaQuery vm = new VistaQuery(RPC_ATTACH_IRRADIATION_DOSE);

		vm.addParameter(VistaQuery.LITERAL, patient.getEnterprisePatientId());
		vm.addParameter(VistaQuery.LITERAL, procedure.getDicomAccessionNumber());
		vm.addParameter(VistaQuery.LITERAL, dose.getType());
		vm.addParameter(VistaQuery.LITERAL, study.getStudyIUID());
		vm.addParameter(VistaQuery.LITERAL, series.getSeriesIUID());
		vm.addParameter(VistaQuery.LITERAL, dose.getIrradiationEventUid());

		// Add the additional dose-type-specific parameters
		if (dose instanceof CTDose)
		{
			CTDose ctDose = (CTDose)dose;
			
			// Add CT dose attributes to a hashmap
			HashMap <String, String> hm = new HashMap <String, String>();
			hm.put("1", TARGET_REGION + dbSeparator + ctDose.getAnatomicTargetRegion());
			hm.put("2", CTDIVOL + dbSeparator + ctDose.getMeanCTDIvol());
			hm.put("3", DLP + dbSeparator + ctDose.getDlp());
			hm.put("4", PHANTOM_TYPE + dbSeparator + ctDose.getPhantomType());
			vm.addParameter(VistaQuery.LIST, hm);
		}
		else if (dose instanceof FluoroDose)
		{

			// Create a hashmap to hold the name/value radiation dosage properties
			HashMap <String, String> hm = new HashMap <String, String>();

			// Add fluoro dose properties
			FluoroDose fluoroDose = (FluoroDose)dose;
			hm.put("1", TOTAL_TIME_IN_FLUOROSCOPY + dbSeparator + fluoroDose.getFluoroTimeTotal());
			hm.put("2", DOSE_AREA_PRODUCT + dbSeparator + fluoroDose.getDoseAreaProductTotal());
			hm.put("3", DOSE_RP_TOTAL + dbSeparator + fluoroDose.getDoseRpTotal());
			hm.put("4", FLUORO_DOSE_RP_TOTAL + dbSeparator + fluoroDose.getFluoroDoseRpTotal());
			hm.put("5", FLUORO_DOSE_AREA_PRODUCT_TOTAL + dbSeparator + fluoroDose.getFluoroDoseAreaProductTotal());
			hm.put("6", CINE_DOSE_RP_TOTAL + dbSeparator + fluoroDose.getCineDoseRpTotal());
			hm.put("7", CINE_DOSE_AREA_PRODUCT_TOTAL + dbSeparator + fluoroDose.getCineDoseAreaProductTotal());
			hm.put("8", CINE_TIME + dbSeparator + fluoroDose.getCineTime());
			vm.addParameter(VistaQuery.LIST, hm);
		}
		
		// Add the radiation dose properties map to the query
		String result = executeRPC(vm);
		
		return translateCreateDoseRecord(dose, result);

	}

	private Dose translateCreateDoseRecord(Dose dose, String returnValue) 
							throws CreationException, ParentREFDeletedMethodException
	{
		//Check if the Series REF was deleted in the new data structure.  If deleted, throw exception.
		if(returnValue == null)
		{
			throw new CreationException("RPC returned value for " + RPC_ATTACH_IRRADIATION_DOSE +" is null.");
		}
		
		String[] results = StringUtils.Split(returnValue, DB_OUTPUT_SEPARATOR1);

		if (results[0].equals("-100"))
		{ 
			// Parent IEN Deleted
			throw new ParentREFDeletedMethodException("Parent Series REF IEN was deleted: " + results[1]);
		}
		
		dose.setId(Integer.parseInt(translateNewEntityIEN(returnValue, true)));
		return dose;
	}


	public List<Dose> getRadiationDoseDetails(String patientDfn, String accessionNumber) throws MethodException, ConnectionException
	{
		// Create the VistaQuery object
		VistaQuery vm = new VistaQuery(RPC_GET_IRRADIATION_DOSE);
		vm.addParameter(VistaQuery.LITERAL, patientDfn);
		vm.addParameter(VistaQuery.LITERAL, accessionNumber);
		String result = executeRPC(vm);
		
		return translateGetRadiationDoseDetails(result);
	}


	private List<Dose> translateGetRadiationDoseDetails(String result) throws MethodException
	{
		
		// Split the result into lines
		String[] lines = StringUtils.Split(result, LINE_SEPARATOR);

		String[] results = StringUtils.Split(lines[0],DB_OUTPUT_SEPARATOR1);
		if (!results[0].equals("0"))
		{ 
			// Error. Log it and return and empty dose object
			logger.error(lines[0]);
			return new ArrayList<Dose>();
		}

		// Build the list of dose records (which may end up emtpy, that's fine...)
		List<Dose> doseDetails = new ArrayList<Dose>();
		
		int currentLineIndex = 1;
		
		while (currentLineIndex < lines.length)
		{
			// Get the type
			String type = StringUtils.Split(lines[currentLineIndex], DB_OUTPUT_SEPARATOR2)[1];
			
			if (type.equals("CT"))
			{
				currentLineIndex = addCTDose(lines, ++currentLineIndex, doseDetails);
			}
			else
			{
				currentLineIndex = addFluoroDose(lines, ++currentLineIndex, doseDetails);
			}
		}
		
		return doseDetails;
	}


	// Parses CTDose fields from the output and returns a CT dose object
	private int addCTDose(String[] lines, int currentLineIndex, List<Dose> doseDetails)
	{

		CTDose dose = new CTDose();
		int length = lines.length;
		for (int i = currentLineIndex; i<length; i++)
		{
			String currentLine = lines[currentLineIndex];
			
			String tag = StringUtils.Split(currentLine, DB_OUTPUT_SEPARATOR2)[0];
			String value = StringUtils.Split(currentLine, DB_OUTPUT_SEPARATOR2)[1];
			
			if (tag.equals(IRRADIATION_INSTANCE_UID))
			{
				dose.setIrradiationEventUid(value);
			}
			else if (tag.equals(TARGET_REGION))
			{
				dose.setAnatomicTargetRegion(value);
			}
			else if (tag.equals(CTDIVOL))
			{
				dose.setMeanCTDIvol(value);
			}
			else if (tag.equals(DLP))
			{
				dose.setDlp(value);
			}
			else if (tag.equals(PHANTOM_TYPE))
			{
				dose.setPhantomType(value);
			}
			else if (tag.equals("TYPE"))
			{
				doseDetails.add(dose);
				return currentLineIndex;
			}
			
			currentLineIndex++;

		}
		doseDetails.add(dose);
		return currentLineIndex;
	}
	
	// Parses FluoroDose fields from the output and returns a fluoro dose object
	private int addFluoroDose(String[] lines, int currentLineIndex, List<Dose> doseDetails)
	{
		FluoroDose dose = new FluoroDose();
		
		int length = lines.length;
		for (int i = currentLineIndex; i<length; i++)
		{
			String tag = StringUtils.Split(lines[currentLineIndex], DB_OUTPUT_SEPARATOR2)[0];
			String value = StringUtils.Split(lines[currentLineIndex], DB_OUTPUT_SEPARATOR2)[1];
			
			if (tag.equals(TOTAL_TIME_IN_FLUOROSCOPY))
			{
				dose.setFluoroTimeTotal(value);
			}
			else if (tag.equals(DOSE_AREA_PRODUCT))
			{
				dose.setDoseAreaProductTotal(value);
			}
			else if (tag.equals(DOSE_RP_TOTAL))
			{
				dose.setDoseRpTotal(value);
			}
			else if (tag.equals(FLUORO_DOSE_RP_TOTAL))
			{
				dose.setFluoroDoseRpTotal(value);
			}
			else if (tag.equals(FLUORO_DOSE_AREA_PRODUCT_TOTAL))
			{
				dose.setFluoroDoseAreaProductTotal(value);
			}
			else if (tag.equals(CINE_DOSE_RP_TOTAL))
			{
				dose.setCineDoseRpTotal(value);
			}
			else if (tag.equals(CINE_DOSE_AREA_PRODUCT_TOTAL))
			{
				dose.setCineDoseAreaProductTotal(value);
			}
			else if (tag.equals(CINE_TIME))
			{
				dose.setCineTime(value);
			}
			else if (tag.equals("TYPE"))
			{
				doseDetails.add(dose);
				return currentLineIndex;
			}

			currentLineIndex++;
		}
		
		doseDetails.add(dose);
		return currentLineIndex;

	}


}
