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

package gov.va.med.imaging.vistaimagingdatasource.dicom.importer;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.Patient;
import gov.va.med.imaging.exchange.business.Patient.PatientSex;
import gov.va.med.imaging.exchange.business.dicom.DicomUtils;
import gov.va.med.imaging.exchange.business.dicom.importer.Order;
import gov.va.med.imaging.exchange.business.dicom.importer.Procedure;
import gov.va.med.imaging.exchange.business.dicom.importer.ProcedureModifier;
import gov.va.med.imaging.exchange.business.dicom.importer.Series;
import gov.va.med.imaging.exchange.business.dicom.importer.SopInstance;
import gov.va.med.imaging.exchange.business.dicom.importer.Study;
import gov.va.med.imaging.exchange.business.storage.exceptions.RetrievalException;
import gov.va.med.imaging.exchange.business.storage.exceptions.UpdateException;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class StudyDAO extends BaseImporterDAO<Study>
{
	private static String GET_IMPORT_STATUS = "MAGV IMPORT STATUS";
	private static String GET_PREVIOUS_RECONCILIATION_DETAILS = "MAGV CONFIRM RAD ORDER";
	//
	// Constructor
	//
	public StudyDAO(){}
	public StudyDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}
	
	//
	// Get Import Status
	//
	public Study getImportStatus(Study study) throws MethodException, ConnectionException 
	{
		// Chunk this into 100 items per call to avoid MUMPS call size restrictions
		HashMap<String, String> allItems = getUidTriples(study);
		HashMap<String, String> currentItems = new HashMap<String, String>(100);
		VistaQuery vm;
		int index = 1;
		String returnValue;
		while (index <= allItems.size()){
			currentItems.clear();
			for (int i = 1; i <= 100 && index <= allItems.size(); i++){
				currentItems.put(Integer.toString(index), allItems.get(Integer.toString(index)));
				index++;
			}
			vm = new VistaQuery(GET_IMPORT_STATUS);
			vm.addParameter(VistaQuery.LIST, currentItems);
			returnValue = executeRPC(vm);
			translateImportStatus(study, returnValue);
		}
		return study;
	}

	private HashMap<String, String> getUidTriples(Study study) 
	{
		HashMap<String, String> hm = new HashMap<String, String>();

		int counter = 1;
		StringBuilder builder = new StringBuilder();
		builder.append("Study Status inputs:\n");
		
		String studyUid = study.getUid();
		
		for (Series series : study.getSeries())
		{
			String seriesUid = series.getUid();
			
			for (SopInstance sopInstance : series.getSopInstances())
			{
				String triple = studyUid + "`" + seriesUid + "`" + sopInstance.getUid();
				builder.append(Integer.toString(counter) + ") " + triple + "\n");
				hm.put(Integer.toString(counter++), triple);
			}
		}
		logger.debug(builder.toString());
		return hm;
	}
	
	
	
	public Study translateImportStatus(Study study, String returnValue) throws MethodException
	{
		String[] instanceResults = StringUtils.Split(returnValue, StringUtils.CRLF);

		String[] callStatus = StringUtils.Split(instanceResults[0], StringUtils.STICK);

		if (!callStatus[0].equals("0"))
		{
			
			throw new MethodException(callStatus[1]);
		}
		
		StringBuilder builder = new StringBuilder();
		builder.append("Study Status Results:\n");
		
		// It was a successful call, so build a map of the results
		HashMap<String, Integer> instanceStatus = new HashMap<String, Integer>();
		for (int i=1; i<instanceResults.length; i++)
		{
			String instanceResult = instanceResults[i];
			builder.append(instanceResult + "\n");
			
			// First split out the result
			String[] fields = StringUtils.Split(instanceResult, StringUtils.STICK);
			int importStatus = Integer.parseInt(fields[0]);

			// Now parse the triple to get the key...
			String[] uidTriple = StringUtils.Split(fields[1], StringUtils.BACKTICK);
			String key = createInstanceKey(uidTriple[0], uidTriple[1], uidTriple[2]);
			
			// Add the key and result to the hashmap
			instanceStatus.put(key, importStatus);
		}

		// Log the results
		logger.debug(builder.toString());

		// Set the import status of each image
		String studyUid = study.getUid();
		
		for (Series series : study.getSeries())
		{
			String seriesUid = series.getUid();
			
			for (SopInstance sopInstance : series.getSopInstances())
			{
				// Only attempt to overwrite this value if the image is not already marked as
				// imported by the importer...
				if (!sopInstance.isImportedSuccessfully())
				{
					String key = createInstanceKey(studyUid, seriesUid, sopInstance.getUid());
					if (instanceStatus.containsKey(key)){
						boolean importStatus = instanceStatus.get(key) == 0 ? true : false;
						sopInstance.setImportedSuccessfully(importStatus);
					}
				}
			}
		}

		return study;
	}

	private String createInstanceKey(String studyUid, String seriesUid, String sopInstanceUid)
	{
		return studyUid + "`" + seriesUid + "`" + sopInstanceUid;
	}
	
	public Study getPreviousReconciliationDetails(Study study, 
			String studyUid,
			String seriesUid, 
			String instanceUid) 
	throws MethodException, ConnectionException 
	{
		VistaQuery vm;
		vm = new VistaQuery(GET_PREVIOUS_RECONCILIATION_DETAILS);
		vm.addParameter(VistaQuery.LITERAL, studyUid + "`" + seriesUid + "`" + instanceUid);
		String returnValue = executeRPC(vm);
		
		return translateGetPreviousReconciliationDetails(study, studyUid, seriesUid, instanceUid, returnValue);
	}
	
	
	public Study translateGetPreviousReconciliationDetails(Study study, String studyUid,
			String seriesUid, String instanceUid, String returnValue) 
	throws MethodException
	{
		String[] lines = StringUtils.Split(returnValue, StringUtils.NEW_LINE);

		if (lines.length == 1)
		{
			// Either no data or an error
			String fields[] = StringUtils.Split(lines[0], StringUtils.BACKTICK);
			if (fields[0].equals("0"))
			{
				// No data found
				logger.error("No previous reconcilation data found for study with study UID = " + studyUid 
						+ ", series UID = " + seriesUid 
						+ ", and SOP instance UID = " + instanceUid);
				
			}
			else
			{
				// Error occurred. Log the error
				logger.error("An error occurred while retrieving previous reconcilation data for study UID = " + studyUid 
						+ ", series UID = " + seriesUid 
						+ ", and SOP instance UID = " + instanceUid + ". Error message: " + fields[1]);
			}
		}
		else
		{
			// We have a successful call, with the data in the second line: Sample data format below:
			//
			// Piece	Java Index	Description			Sample Value				
			// ----------------------------------------------------------------------
			// 1		0			Patient DFN 		705							
			// 2		1			Patient Name		PATIENT,SEVENZEROFIVE		
			// 3		2			Patient ID (SSN)	000-00-0705					
			// 4		3			Patient DOB			19240000					
			// 5		4			Patient Sex			M							
			// 6		5			Patient ICN			9832150123V946114			
			// 7		6			Accession Number	041706-354					
			// 8		7			Exam Date			20060417					
			// 9		8			Proc Name			TEETH SINGLE VIEW			
			// 10		9			Proc Modifiers		LEFT						
			// 11		10			Order IEN			499							
			// 12		11			Order Date			JUL 05, 2011@14:32:38		
			// 13		12			Specialty			RADIOLOGY					
			// 14		13			Exam Status			COMPLETE					
			// 15		14			Order Reason		Import images from outside	
			//
			
			// Parse the patient data
			String fields[] = StringUtils.Split(lines[1], StringUtils.STICK);
			String dfn = fields[0];
			String patientName = fields[1];
			String ssn = fields[2];
			String dob = fields[3];
			String sex = fields[4];
			String icn = fields[5];

			// Create the Patient object
			Patient patient = Patient.create(patientName, 
					icn, 
					"", 
					PatientSex.valueOfPatientSex(sex), 
					parseDicomDate(dob), 
					ssn, 
					dfn, 
					null);

			// Parse the order data
			String accessionNumber = fields[6];
			DicomUtils du = new DicomUtils();
			String specialty = du.getServiceFromAccessionNumber(accessionNumber); // "RAD", "CON" or "LAB";
			String location = fields[12];
			String examDate = reformatDicomDate(fields[7]);
			String examStatus = fields[13];
			int orderIen = -1;
			if (!fields[10].equals(""));
			{
				orderIen = Integer.parseInt(fields[10]);
			}
			
			String orderDate = fields[11];

			// Parse and create procedure object
			Procedure procedure = new Procedure();
			procedure.setName(fields[8]);
			procedure.setId(-1);

			// Create procedure modifier object(s)
			List<ProcedureModifier> modifiers = parseProcedureModifiers(fields[9]);

			
			// Create the Order object
			Order order = new Order();
			order.setId(orderIen);
			order.setAccessionNumber(accessionNumber);
			order.setSpecialty(specialty);
			order.setExamDate(examDate);
			order.setOrderDate(orderDate);
			order.setProcedure(procedure);
			order.setProcedureModifiers(modifiers);
			order.setExamStatus(examStatus);
			order.setLocation(location);
			
			// Attach the objects to the study
			study.setPreviouslyReconciledPatient(patient);
			study.setPreviouslyReconciledOrder(order);

		}
		return study;
	}
	private String reformatLongDateTimeAsShortDate(String inputDate) 
	{
		try
		{
			String datePart = StringUtils.Split(inputDate, "@")[0];
			
			Date date = null;
			
			// Parse with the long date formatter
			DateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
			date = (Date)formatter.parse(datePart);
			
			// Create the short date formatter and format the date.
			formatter = new SimpleDateFormat("MM/dd/yyyy");
			return formatter.format(date);
		}
		catch (Exception e)
		{
			logger.warn("Coudn't reformat long date time into short date: input data: " + inputDate);
		}
		
		// If we got here, we couldn't reformat the date. Just return what was entered...
		return inputDate;
	}
	private Date parseDicomDate(String dicomDateString) 
	{
		Date date = null;
		try
		{
			DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		    date = (Date)formatter.parse(dicomDateString);
		}
		catch (Exception e)
		{
			logger.error("Error parsing DICOM date: " + dicomDateString, e);
		}
		
		return date;
	}

	private String reformatDicomDate(String dateString) 
	{
		// Attempt to parse the incoming date as a DICOM date. If we can't, just reflect back 
		// the input date string
		Date date = parseDicomDate(dateString);
		if (date != null)
		{
			String format = "MM/dd/yyyy";
			DateFormat dateFormat = new SimpleDateFormat(format);
			return dateFormat.format(date);
		}
		else
		{
			return dateString;
		}
	}
	
	
}
