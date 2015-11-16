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
import gov.va.med.imaging.exchange.business.dicom.importer.DiagnosticCode;
import gov.va.med.imaging.exchange.business.dicom.importer.Order;
import gov.va.med.imaging.exchange.business.dicom.importer.OrderFilter;
import gov.va.med.imaging.exchange.business.dicom.importer.OrderingProvider;
import gov.va.med.imaging.exchange.business.dicom.importer.Procedure;
import gov.va.med.imaging.exchange.business.dicom.importer.ProcedureModifier;
import gov.va.med.imaging.exchange.business.dicom.importer.Reconciliation;
import gov.va.med.imaging.exchange.business.dicom.importer.SecondaryDiagnosticCode;
import gov.va.med.imaging.exchange.business.dicom.importer.Series;
import gov.va.med.imaging.exchange.business.dicom.importer.StatusChangeDetails;
import gov.va.med.imaging.exchange.business.dicom.importer.Study;
import gov.va.med.imaging.exchange.business.storage.StorageServerConfiguration;
import gov.va.med.imaging.exchange.business.storage.exceptions.CreationException;
import gov.va.med.imaging.exchange.business.storage.exceptions.RetrievalException;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.log4j.Logger;

import sun.font.CreatedFontTracker;

public class OrderDAO extends BaseImporterDAO<Order>
{
	private static final String UNREGISTERED = "";
	private static final String EXAMINED = "EXAMINED";
	private static final String COMPLETE = "COMPLETE";
	private static final String WAITING_FOR_EXAM = "WAITING FOR EXAM";

	protected Logger radiologyRPCLogger = Logger.getLogger("RadiologyRPC");

	private static String GET_PATIENT_ORDERS = "MAGV GET PAT ORDERS";
	private static String CREATE_RADIOLOGY_ORDER = "MAGV RAD EXAM ORDER";
	private static String REGISTER_ORDER = "MAGV RAD EXAM REGISTER";
	private static String STATUS_EXAMINED = "MAGV RAD STAT EXAMINED";
	private static String STATUS_EXAM_COMPLETE = "MAGV RAD STAT COMPLETE";
	//
	// Constructor
	//
	public OrderDAO(){}
	public OrderDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}
	
	//
	// Retrieve By Criteria methods
	//
	@Override
	public VistaQuery generateFindByCriteriaQuery(Object criteria) 
	{
		OrderFilter filter = (OrderFilter)criteria;
		VistaQuery vm = new VistaQuery(GET_PATIENT_ORDERS);
		vm.addParameter(VistaQuery.LITERAL, filter.getDfn());
		vm.addParameter(VistaQuery.LITERAL, "D");
		vm.addParameter(VistaQuery.LITERAL, "v");
		vm.addParameter(VistaQuery.LITERAL, "V");
		vm.addParameter(VistaQuery.LITERAL, filter.getOrderType());
		vm.addParameter(VistaQuery.LITERAL, "01011900");
		vm.addParameter(VistaQuery.LITERAL, today());
		return vm;
	}

	@Override
	public List<Order> translateFindByCriteria(Object criteria, String result) throws RetrievalException
	{
		OrderFilter filter = (OrderFilter)criteria;
		
		if (filter.getOrderType().equalsIgnoreCase("RAD"))
		{
			return translateRadiologyOrders(result);	
		}
		else
		{
			return translateNonRadiologyOrders(result);	
		}
	}
	
	private List<Order> translateRadiologyOrders(String result)
			throws RetrievalException {
		// Create the OrderingLocation list
		List<Order> orders = new ArrayList<Order>();
		
		// Split the result into lines
		String[] lines = StringUtils.Split(result, LINE_SEPARATOR);

		String[] statusFields = StringUtils.Split(lines[0], StringUtils.BACKTICK);
		if (!"0".equals(statusFields[0]))
		{
			throw new RetrievalException(statusFields[1]);
		}
		else
		{
			// Parse the results
			for (int i=1; i<lines.length; i++)
			{
				// determine the examinationsIEN
				
				String[] fields = StringUtils.Split(lines[i], StringUtils.STICK);
				Order order = new Order();
				order.setSpecialty(fields[0]);
				order.setOrderDate(fields[2]);
				order.setOrderReason(fields[3]);
				order.setLocation(fields[4]);
				order.setExamDate(fields[5]);
				order.setAccessionNumber(fields[6]);
				order.setExamStatus(fields[7]);
				order.setProcedureId(getNullableInt(fields[8]));
				order.setProcedureName(fields[9]);
				order.setCaseNumber(fields[10]);
				order.setRegisteredExamsIen(fields[11]);
				order.setId(Integer.parseInt(fields[12]));
				order.setExaminationsIen(getNullableInt(fields[13]));
				order.setOrderingProviderIen(getNullableInt(fields[14]));
				order.setOrderingLocationIen(getNullableInt(fields[15]));
				
				// Attach procedure modifiers, if any
				List<ProcedureModifier> modifiers = parseProcedureModifiers(fields[16]);
				order.setProcedureModifiers(modifiers);
				
				order.setCreditMethod(fields[17]);
				order.setVistaGeneratedStudyUid(fields[18]);

				// Apply a filter to only allow orders with exam statuses valid for import
				if (isValidStatusForImport(order.getExamStatus(), order.getCreditMethod()))
				{
					orders.add(order);
				}
			}
		}

		return orders;
	}
	
	// Determines whether an order is in a valid status for import
	private boolean isValidStatusForImport(String examStatus, String creditMethod) 
	{
		// Clean up the exam status for comparison
		examStatus = examStatus.trim().toUpperCase();
		
		// Determine whether or not this is a no-credit exam
		String normalizedCreditMethod = creditMethod.toUpperCase().replace(" ", "");
		boolean isNoCredit = normalizedCreditMethod.equals("NOCREDIT");
		
		// Valid statuses for importer are no status (UNREGISTERED), WAITING FOR EXAM, or EXAMINED,
		// as well as COMPLETE exams which are No Credit. Any other statuses, such as CANCELLED, 
		// COMPLETE with other credit methods, etc will be filtered out.
		if (examStatus.equals(UNREGISTERED) || 
			examStatus.equals(WAITING_FOR_EXAM) || 
			examStatus.equals(EXAMINED) ||
			(examStatus.equals(COMPLETE) && isNoCredit))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	private List<Order> translateNonRadiologyOrders(String result) throws RetrievalException 
	{
		// Create the OrderingLocation list
		List<Order> orders = new ArrayList<Order>();
		
		// Split the result into lines
		String[] lines = StringUtils.Split(result, LINE_SEPARATOR);
		
		String[] statusFields = StringUtils.Split(lines[0], StringUtils.BACKTICK);
		if (!"0".equals(statusFields[0]))
		{
			throw new RetrievalException(statusFields[1]);
		}
		else
		{
			// Parse the results
			for (int i=1; i<lines.length; i++)
			{
				// determine the examinationsIEN
				
				String[] fields = StringUtils.Split(lines[i], StringUtils.STICK);
				Order order = new Order();

				order.setSpecialty(fields[0]);
				order.setOrderDate(fields[2]);
				order.setOrderReason(fields[3]);
				order.setLocation(fields[4]);
				order.setExamDate(fields[5]);
				order.setAccessionNumber(fields[6]);
				order.setExamStatus(fields[7]);
				order.setId(Integer.parseInt(fields[8]));
				order.setProcedureName(fields[9]);
				order.setCaseNumber(fields[10]);
				
				Procedure procedure = new Procedure();
				procedure.setId(0);
				procedure.setName(fields[9]);
				
				order.setProcedure(procedure);
				
				// Attach procedure modifiers, if any
				//List<ProcedureModifier> modifiers = parseProcedureModifiers(fields[11]);
				//order.setProcedureModifiers(modifiers);
				
				
				orders.add(order);
		
//				0 … OrderType={CON}
//				1 … PatientDFN
//				2 … RequestDate
//				3 … {}
//				4 … {}
//				5 … {}
//				6 … AccessionNumber =SiteNumber_”-GMR-“_CaseNumber
//				7 … OrderStatus
//				8 … ConsultOrderDFN (enry in Consult/Status File (#123)
//				9… ConsultTitle
//				10… ClinicalProcedure

			}
		}
		
		return orders;
	}

	private int getNullableInt(String stringValue) 
	{
		int intValue = -1;
		if (stringValue != null && !stringValue.trim().equals(UNREGISTERED))
		{
			try
			{
				intValue = Integer.parseInt(stringValue);
			}
			catch(Exception e)
			{
				logger.debug("Couldn't parse integer value from string: " + stringValue);
			}
		}
		return intValue;
	}

	//
	// Create Order
	//
	public Order createRadiologyOrder(Reconciliation reconciliation) throws MethodException, ConnectionException
	{
		Patient patient = reconciliation.getPatient();
		Study study = reconciliation.getStudy();
		Order order = reconciliation.getOrder();
		
		HashMap<String, String> clinicalHistoryData = getClinicalHistoryData(study);
		// log the parameter values
		StringBuilder builder = new StringBuilder();
		builder.append("Create Order (" + CREATE_RADIOLOGY_ORDER + ") Parameters\n");
		builder.append("---------------------------------------------\n");
		builder.append("Patient DFN: " + patient.getDfn() + "\n");
		builder.append("Imaging Location IEN: " + order.getProcedure().getImagingLocationId() + "\n");
		builder.append("Procedure and Modifier IENs: " + getProcedureAndModifiers(order) + "\n");
		builder.append("Study Date: " + study.getStudyDate() + "\n");
		builder.append(UNREGISTERED + "S\n");
		builder.append("Ordering Location IEN: " + order.getOrderingLocation().getId() + "\n");
		builder.append("Ordering Provider IEN: " + order.getOrderingProvider().getId() + "\n");
		builder.append("Reason: " + "Exam imported from outside\n");
		builder.append("Clinical History Data:\n");
		
		int lineNum = 1;
		for (String histLine : clinicalHistoryData.values())
		{
			builder.append("  " + lineNum++ + ": " + histLine + "\n");
		}
		logger.info(builder.toString());
		radiologyRPCLogger.info(builder.toString());
		
		// Call the RPC
		VistaQuery vm = new VistaQuery(CREATE_RADIOLOGY_ORDER);
		vm.addParameter(VistaQuery.LITERAL, patient.getDfn());
		vm.addParameter(VistaQuery.LITERAL, Integer.toString(order.getProcedure().getImagingLocationId()));
		vm.addParameter(VistaQuery.LITERAL, getProcedureAndModifiers(order));
		vm.addParameter(VistaQuery.LITERAL, study.getStudyDate());
		vm.addParameter(VistaQuery.LITERAL, "S");
		vm.addParameter(VistaQuery.LITERAL, Integer.toString(order.getOrderingLocation().getId()));
		vm.addParameter(VistaQuery.LITERAL, Integer.toString(order.getOrderingProvider().getId()));
		vm.addParameter(VistaQuery.LITERAL, "Exam imported from outside");
		vm.addParameter(VistaQuery.LIST, clinicalHistoryData);
		
		String result = executeRPC(vm);
		
		radiologyRPCLogger.info("Create Order Results\n---------------------------------------------\n" + result);
		return translateCreate(reconciliation.getOrder(), result);
	}
	
	private HashMap<String, String> getClinicalHistoryData(Study study) 
	{
		HashMap<String, String> hm = new LinkedHashMap<String, String>();
		
		// Get the formated date of birth
		SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
		String formattedBirthDate = UNREGISTERED;

		try
		{
			formattedBirthDate = formatter.format(study.getPatient().getDob());
		}
		catch (Exception e)
		{
			logger.warn("Couldn't format birthdate: " + study.getPatient().getDob());
		}
		
		// Get the formatted Study Date
		String formattedStudyDate = getFormattedStudyDate(study.getStudyDate());
		String formattedStudyTime = getFormattedStudyTime(study.getStudyTime());
		
		hm.put("1", "CLINHIST^1^ *** Original Data for Imported Study ***");
		hm.put("2", "CLINHIST^2^        Patient Name: " + study.getPatient().getPatientName());
 		hm.put("3", "CLINHIST^3^          Patient ID: " + study.getPatient().getSsn());
 		hm.put("4", "CLINHIST^4^          Birth Date: " + formattedBirthDate  + "          " 
 				+ "Sex: " + study.getPatient().getPatientSex().toDicomString());
 		
 		hm.put("5", "CLINHIST^5^         Accession #: " + study.getAccessionNumber());
 		hm.put("6", "CLINHIST^6^          Study Date: " + formattedStudyDate 
 				+ "          " + "Study Time: " + formattedStudyTime);
 		
 		hm.put("7", "CLINHIST^7^   Study Description: " + study.getDescription());
		hm.put("8", "CLINHIST^8^ Referring Physician: " + study.getReferringPhysician());
		
		int counter = 9;

		// Get a list of strings containing series data
		List<String> seriesData = getClinicalHistoryDataForSeries(study);
		
		// Add the series data to the hashmap
		for (String line : seriesData)
		{
			hm.put(Integer.toString(counter), "CLINHIST^" + counter + "^ " + line);
			counter++;
		}

		return hm;
	}
	
	private String getFormattedStudyDate(String dateString) 
	{
		// Default to the unformatted string, in case we can't parse it correctly
		String formattedStudyDate = dateString;
		
		try
		{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			Date studyDate = formatter.parse(dateString);
			formatter = new SimpleDateFormat("MM/dd/yy");
			formattedStudyDate = formatter.format(studyDate);
		}
		catch (ParseException e)
		{
			logger.error(e);
		}
		
		return formattedStudyDate;
	}
	
	private String getFormattedStudyTime(String timeString) 
	{
		// Default to the unformatted string, in case we can't parse it correctly
		String formattedStudyTime = timeString;
		
		// If the time is empty, set it to one second after midnight...
		if (timeString == null || timeString.equals(UNREGISTERED))
		{
			timeString = "000001";
		}
		
		try
		{
			SimpleDateFormat formatter = new SimpleDateFormat("hhmmss");
			Date studyTime = formatter.parse(timeString);
			formatter = new SimpleDateFormat("hh:mm:ss");
			formattedStudyTime = formatter.format(studyTime);
		}
		catch (ParseException e)
		{
			logger.error(e);
		}
		
		return formattedStudyTime;
	}

	private List<String> getClinicalHistoryDataForSeries(Study study) 
	{
		// First, see if all the facilities are the same
		boolean allAcquistionInfoEqual = true;
		
		String facility = UNREGISTERED;
		String institutionAddress = UNREGISTERED;
		for (int i = 0; i < study.getSeries().size(); i++)
		{
			// Get the current study
			Series series = study.getSeries().get(i);
			
			// First time through, initialize facility and address. Subsequent times, see if they change
			if (i==0)
			{
				facility = series.getFacility() + "";
				institutionAddress = series.getInstitutionAddress() + "";
			}
			else
			{
				facility = facility + "";
				if (!facility.equals(series.getFacility() + ""))
				{
					allAcquistionInfoEqual = false;
				}
				
				institutionAddress = institutionAddress + "";
				if (!institutionAddress.equals(series.getInstitutionAddress() + ""))
				{
					allAcquistionInfoEqual = false;
				}
			}
		}

		// Create the line items for each series
		List<String> seriesDataList = new ArrayList<String>();
		for (int i = 1; i <= study.getSeries().size(); i++)
		{
			Series series = study.getSeries().get(i-1);
			
			// Start with an empty string
			String text = UNREGISTERED;
			
			// If the number of series is less than 10, indent by one space
			if (i < 10)
				text += " ";
			
			// Add the image count for the series
			text += "Series " + i + ": " + series.getSopInstances().size() + " " + series.getModality() + " file";
			
			// Add an 's' to 'file' unless the number of images is 1...
			if (series.getSopInstances().size() != 1)
				text += "s";
			
			// If we have a series description, add it...
			String description = series.getSeriesDescription();
			if (description != null && !description.trim().equals(UNREGISTERED))
			{
				text += ", description: " + series.getSeriesDescription();
			}
			
			seriesDataList.add(text);
			
		}
		
		// Add the acquisition location and address if it's the same on all series, and not null 
		// or empty
		if (allAcquistionInfoEqual && facility != null && !facility.trim().equals(UNREGISTERED))
		{
			String text = "Acquisition site: " + facility;
			
			if (institutionAddress != null && !institutionAddress.trim().equals(UNREGISTERED))
			{
				text += "    Address: " + institutionAddress;
			}
			
			seriesDataList.add(text);
		}
		
		return seriesDataList;
	}
	
	private String getProcedureAndModifiers(Order order) 
	{
		// Add the procedure IEN
		String result = Integer.toString(order.getProcedure().getId());
		
		// If there are any modifiers, add them
		if (order.getProcedureModifiers() != null && order.getProcedureModifiers().size() > 0)
		{
			for (ProcedureModifier modifier : order.getProcedureModifiers())
			{
				result += "^" + modifier.getId();
			}
		}
		
		return result;
		
	}
	
	//
	// Translate create... 
	//
	public Order translateCreate(Order order, String result) throws CreationException
	{
		// Split the result into lines
		String[] lines = StringUtils.Split(result, LINE_SEPARATOR);

		if (!lines[0].startsWith("-"))
		{
			// The first line did not start with a minus sign, so there was no error. Parse the 
			// IEN into an int and set it on the order
			int orderId = Integer.parseInt(lines[0]);
			order.setId(orderId);
		}
		else
		{
			// There was an error. Throw the entire result as the exception message
			throw new CreationException(result);
		}

		return order;

	}
	
	//
	// Register Order
	//
	public Order registerOrder(Reconciliation reconciliation, int hospitalLocationId) throws MethodException, ConnectionException
	{
		Study study = reconciliation.getStudy();
		Order order = reconciliation.getOrder();
		
		int orderingLocationId = order.getOrderingLocationIen();

		// log the parameter values
		StringBuilder builder = new StringBuilder();
		builder.append("Register Order (" + REGISTER_ORDER + ") Parameters\n");
		builder.append("---------------------------------\n");
		builder.append("Order IEN: " + order.getId() + "\n");
		builder.append("Study Date and Time: " + getStudyDateTime(study) + "\n");
		builder.append("Flags:\n");
		builder.append(" 1:" + "PRINCLIN^^" + hospitalLocationId + "\n");
		builder.append(" 2:" + "FLAGS^^D\n");
		logger.info(builder.toString());
		radiologyRPCLogger.info(builder.toString());
		
		VistaQuery vm = new VistaQuery(REGISTER_ORDER);
		vm.addParameter(VistaQuery.LITERAL, Integer.toString(order.getId()));
		vm.addParameter(VistaQuery.LITERAL, getStudyDateTime(study));
		
		HashMap<String, String> hm = new LinkedHashMap<String, String>();
		hm.put("1", ("PRINCLIN^^" + hospitalLocationId));
		hm.put("2", "FLAGS^^D");
		
		vm.addParameter(VistaQuery.LIST, hm);
		String result = executeRPC(vm);
			 
		radiologyRPCLogger.info("Register Order Results\n---------------------------------------------\n" + result);
		return translateRegisterOrder(order, result);
	}
	
	private String getStudyDateTime(Study study) 
	{
		String studyTime = study.getStudyTime();
		
		if (studyTime == null || studyTime.trim().equals(UNREGISTERED))
		{
			// If the study time is null or empty, set it to one second after midnight
			studyTime = "000001";
		}
		else
		{
			// Attempt to split on the . delimiting milliseconds. Use the first piece after
			// the split
			String[] timeParts = StringUtils.Split(studyTime, ".");
			studyTime = timeParts[0];
			
			// If the study time after splitting is empty or all zeros, 
			// set it to one second after midnight
			if (studyTime.equals(UNREGISTERED) || studyTime.equals("0") ||
					studyTime.equals("00") || studyTime.equals("000") ||
					studyTime.equals("0000") || studyTime.equals("00000") ||
				studyTime.equals("000000"))
			{
				studyTime = "000001";
			}
		}
		
		return study.getStudyDate() + studyTime;
	}
	
	public Order translateRegisterOrder(Order order, String result) throws MethodException
	{
		// Split the result into lines
		String[] lines = StringUtils.Split(result, LINE_SEPARATOR);

		String[] statusFields = StringUtils.Split(lines[0], StringUtils.BACKTICK);
		
		// If there were errors, throw an exception
		if (!"0".equals(statusFields[0]))
		{
			throw new MethodException(result);
		}

		// Otherwise, parse out the data from the result line...
		// Sample successful return:
		// 0`1
		// 1211|6889496.9998|1|344|050311-344|201105030001-0500
		
		String[] fields = StringUtils.Split(lines[1], StringUtils.STICK);
		order.setRegisteredExamsIen(fields[1]);
		order.setExaminationsIen(Integer.parseInt(fields[2]));
		order.setAccessionNumber(fields[4]);
		order.setExamDate(fields[5]);

		return order;
	}

	//
	// set order to Examined
	//
	public Order setOrderExamined(Reconciliation reconciliation, String technicianDuz, String placeId) throws MethodException, ConnectionException
	{
		Study study = reconciliation.getStudy();
		Order order = reconciliation.getOrder();
		Patient patient = reconciliation.getPatient();
		
		// log the parameter values
		StringBuilder builder = new StringBuilder();
		builder.append("Set Order Examined (" + STATUS_EXAMINED + ") Parameters\n");
		builder.append("---------------------------------------------\n");
		builder.append("Patient DFN: " + patient.getDfn() + "\n");
		builder.append("Registered Exams IEN: " + order.getRegisteredExamsIen() + "\n");
		builder.append("Examinations IEN: " + order.getExaminationsIen() + "\n");
		builder.append("Technician DUZ: " + technicianDuz + "\n");
		builder.append("Division Number" + placeId + "\n");
		builder.append("Imaging Type Id: " + order.getProcedure().getImagingTypeId() + "\n");
		logger.info(builder.toString());
		radiologyRPCLogger.info(builder.toString());

		VistaQuery vm = new VistaQuery(STATUS_EXAMINED); 
		vm.addParameter(VistaQuery.LITERAL, patient.getDfn());
		vm.addParameter(VistaQuery.LITERAL, order.getRegisteredExamsIen());
		vm.addParameter(VistaQuery.LITERAL, Integer.toString(order.getExaminationsIen()));
		vm.addParameter(VistaQuery.LITERAL, technicianDuz);
		vm.addParameter(VistaQuery.LITERAL, placeId);
		vm.addParameter(VistaQuery.LITERAL, Integer.toString(order.getProcedure().getImagingTypeId()));


		String result = executeRPC(vm);
		radiologyRPCLogger.info("Set Order Examined Results\n---------------------------------------------\n" + result);
		return translateSetOrderExamined(order, result);
	}
	
	public Order translateSetOrderExamined(Order order, String result) throws MethodException
	{
		// Split the result into lines
		String[] lines = StringUtils.Split(result, LINE_SEPARATOR);

		String[] statusFields = StringUtils.Split(lines[0], StringUtils.BACKTICK);
		if (!"0".equals(statusFields[0]))
		{
			throw new MethodException(result);
		}

		return order;
	}

	//
	// set order to Exam Complete
	//
	public Order setOrderExamComplete(Reconciliation reconciliation, String technicianDuz, String placeId) throws MethodException, ConnectionException
	{
		Study study = reconciliation.getStudy();
		Order order = reconciliation.getOrder();
		Patient patient = reconciliation.getPatient();
		
		String standardReportNumber = "";
		String primaryDiagnosticCodeId = ""; 
		List<SecondaryDiagnosticCode> secondaryDiagnosticCodes = null; 
		
		// Get the status change details, if available
		StatusChangeDetails statusChangeDetails = order.getStatusChangeDetails(); 
		
		if (statusChangeDetails != null)
		{
			// Get the Standard Report Number
			standardReportNumber = statusChangeDetails.getStandardReportNumber() + "";

			// Get the primary diagnostic code ID, if available
			DiagnosticCode primaryDiagnosticCode = statusChangeDetails.getPrimaryDiagnosticCode();
			if (primaryDiagnosticCode != null)
			{
				primaryDiagnosticCodeId = Integer.toString(primaryDiagnosticCode.getId());
			}
			
			// Get the list of secondary diagnostic codes, if available
			secondaryDiagnosticCodes = statusChangeDetails.getSecondaryDiagnosticCodes();
		}
		
		VistaQuery vm = new VistaQuery(STATUS_EXAM_COMPLETE); 
		vm.addParameter(VistaQuery.LITERAL, patient.getDfn());
		vm.addParameter(VistaQuery.LITERAL, order.getRegisteredExamsIen());
		vm.addParameter(VistaQuery.LITERAL, Integer.toString(order.getExaminationsIen()));
		vm.addParameter(VistaQuery.LITERAL, technicianDuz);
		vm.addParameter(VistaQuery.LITERAL, placeId);
		vm.addParameter(VistaQuery.LITERAL, Integer.toString(order.getProcedure().getImagingTypeId()));
		vm.addParameter(VistaQuery.LITERAL, standardReportNumber + "");
		vm.addParameter(VistaQuery.LITERAL, primaryDiagnosticCodeId + "");
	
		// Add the list of secondary diagnostic codes, if any
		HashMap <String, String> hm = new HashMap <String, String>();
		if (secondaryDiagnosticCodes != null && secondaryDiagnosticCodes.size() > 0)
		{
			int counter=0;
			for (SecondaryDiagnosticCode diagnosticCode : order.getStatusChangeDetails().getSecondaryDiagnosticCodes())
			{
				hm.put(Integer.toString(counter), Integer.toString(diagnosticCode.getId()));
				counter++;
			}
		}		
		vm.addParameter(VistaQuery.LIST, hm);

		String result = executeRPC(vm);
		
		radiologyRPCLogger.info("Set Order Exam Complete Results\n---------------------------------------------\n" + result);
		return translateSetOrderExamComplete(order, result);
	}
	
	public Order translateSetOrderExamComplete(Order order, String result) throws MethodException
	{
		// Split the result into lines
		String[] lines = StringUtils.Split(result, LINE_SEPARATOR);

		String[] statusFields = StringUtils.Split(lines[0], StringUtils.BACKTICK);
		if (!"0".equals(statusFields[0]))
		{
			throw new MethodException(result);
		}

		return order;
	}

	private static String today()
	{
		String DATE_FORMAT_TODAY = "MMddyyyy";

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_TODAY);
		return sdf.format(cal.getTime());
	}

}
