/**
 * 
 */
package gov.va.med.imaging.exchange.business;

import gov.va.med.imaging.exchange.ProcedureFilter;
import gov.va.med.imaging.exchange.enums.ProcedureFilterMatchMode;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * This class defines the search filter criteria for a document query.
 * The fields in this class are derived from the XDS.b specification though the 
 * VA does not currently support all of these.  The supported filter criteria
 * are expressed in the presence of initialization in a constructor.
 * 
 * @author vhaiswbeckec
 *
 */
public class DocumentFilter
extends ProcedureFilter 
{
	private static final long serialVersionUID = 1L;
	private final static int DEFAULT_MAX_RESULTS_COUNT = 1000;
	
	private final String patientId;
	private final String classCode;
	private final String practiceSettingCode;
	private final Date serviceStartTimeFrom;
	private final Date serviceStartTimeTo;
	private final Date serviceStopTimeFrom;
	private final Date serviceStopTimeTo;
	private final String healthcareFacilityTypeCode;
	private final String[] eventCodes;
	private final String[] confidentialityCodes;
	private final String author;
	private final String formatCode;
	private final String entryStatus;
	private final int maxResultsCount;
	private final String[] documentClassCodes;
	
	// Temporary fix for CVIX demo
	private String siteNumber;
	private boolean useAlternatePatientId;

	/**
	 * 
	 * @param patientId
	 */
	public DocumentFilter(String patientId)
	{
		super(ProcedureFilterMatchMode.excludedInProcedureList);
		this.patientId = patientId;
		this.classCode = null;
		this.practiceSettingCode = null;
		this.fromDate = null;
		this.toDate = null;
		this.serviceStartTimeFrom = null;
		this.serviceStartTimeTo = null;
		this.serviceStopTimeFrom = null;
		this.serviceStopTimeTo = null;
		this.healthcareFacilityTypeCode = null;
		this.eventCodes = null;
		this.confidentialityCodes = null;
		this.author = null;
		this.formatCode = null;
		this.entryStatus = null;
		this.maxResultsCount = DEFAULT_MAX_RESULTS_COUNT;
		this.useAlternatePatientId = false;
		this.documentClassCodes = null;
	}
	
	/**
	 * 
	 * @param patientId
	 * @param creationTimeFrom
	 * @param creationTimeTo
	 */
	public DocumentFilter(String patientId, Date creationTimeFrom, Date creationTimeTo)
	{
		super(ProcedureFilterMatchMode.excludedInProcedureList);
		this.patientId = patientId;
		this.classCode = null;
		this.practiceSettingCode = null;
		this.fromDate = creationTimeFrom;
		this.toDate = creationTimeTo;
		this.serviceStartTimeFrom = null;
		this.serviceStartTimeTo = null;
		this.serviceStopTimeFrom = null;
		this.serviceStopTimeTo = null;
		this.healthcareFacilityTypeCode = null;
		this.eventCodes = null;
		this.confidentialityCodes = null;
		this.author = null;
		this.formatCode = null;
		this.entryStatus = null;
		this.maxResultsCount = DEFAULT_MAX_RESULTS_COUNT;
		this.useAlternatePatientId = false;
		this.documentClassCodes = null;
	}

	/**
	 * 
	 * @param patientId
	 * @param creationTimeFrom
	 * @param creationTimeTo
	 * @param documentClassCodes
	 */
	public DocumentFilter(String patientId, Date creationTimeFrom, Date creationTimeTo, String[] documentClassCodes)
	{
		super(ProcedureFilterMatchMode.excludedInProcedureList);
		this.patientId = patientId;
		this.classCode = null;
		this.practiceSettingCode = null;
		this.fromDate = creationTimeFrom;
		this.toDate = creationTimeTo;
		this.serviceStartTimeFrom = null;
		this.serviceStartTimeTo = null;
		this.serviceStopTimeFrom = null;
		this.serviceStopTimeTo = null;
		this.healthcareFacilityTypeCode = null;
		this.eventCodes = null;
		this.confidentialityCodes = null;
		this.author = null;
		this.formatCode = null;
		this.entryStatus = null;
		this.maxResultsCount = DEFAULT_MAX_RESULTS_COUNT;
		this.useAlternatePatientId = false;
		this.documentClassCodes = documentClassCodes;
	}
	
	/**
	 * 
	 * @param patientId
	 * @param classCode
	 */
	public DocumentFilter(String patientId, String classCode)
	{
		super(ProcedureFilterMatchMode.excludedInProcedureList);
		this.patientId = patientId;
		this.classCode = classCode;
		this.practiceSettingCode = null;
		this.fromDate = null;
		this.toDate = null;
		this.serviceStartTimeFrom = null;
		this.serviceStartTimeTo = null;
		this.serviceStopTimeFrom = null;
		this.serviceStopTimeTo = null;
		this.healthcareFacilityTypeCode = null;
		this.eventCodes = null;
		this.confidentialityCodes = null;
		this.author = null;
		this.formatCode = null;
		this.entryStatus = null;
		this.maxResultsCount = DEFAULT_MAX_RESULTS_COUNT;
		this.useAlternatePatientId = false;
		this.documentClassCodes = null;
	}
	
	public DocumentFilter(String patientId, Date creationTimeFrom, Date creationTimeTo, String classCode)
	{
		super(ProcedureFilterMatchMode.excludedInProcedureList);
		this.patientId = patientId;
		this.classCode = classCode;
		this.practiceSettingCode = null;
		this.fromDate = creationTimeFrom;
		this.toDate = creationTimeTo;
		this.serviceStartTimeFrom = null;
		this.serviceStartTimeTo = null;
		this.serviceStopTimeFrom = null;
		this.serviceStopTimeTo = null;
		this.healthcareFacilityTypeCode = null;
		this.eventCodes = null;
		this.confidentialityCodes = null;
		this.author = null;
		this.formatCode = null;
		this.entryStatus = null;
		this.maxResultsCount = DEFAULT_MAX_RESULTS_COUNT;
		this.useAlternatePatientId = false;
		this.documentClassCodes = null;
	}
	
	/**
	 * @param patientId
	 * @param classCode
	 * @param practiceSettingCode
	 * @param creationTimeFrom
	 * @param creationTimeTo
	 * @param serviceStartTimeFrom
	 * @param serviceStartTimeTo
	 * @param serviceStopTimeFrom
	 * @param serviceStopTimeTo
	 * @param healthcareFacilityTypeCode
	 * @param eventCodes
	 * @param confidentialityCodes
	 * @param author
	 * @param formatCode
	 * @param entryStatus
	 * @param maxResultsCount
	 * @param siteNumber
	 * @param useAlternatePatientId
	 */
	public DocumentFilter(
		String patientId, String classCode, String practiceSettingCode, Date creationTimeFrom,
		Date creationTimeTo, Date serviceStartTimeFrom, Date serviceStartTimeTo, Date serviceStopTimeFrom,
		Date serviceStopTimeTo, String healthcareFacilityTypeCode, String[] eventCodes, String[] confidentialityCodes,
		String author, String formatCode, String entryStatus, int maxResultsCount, String siteNumber,
		boolean useAlternatePatientId)
	{
		this(patientId, classCode, practiceSettingCode, creationTimeFrom,
			creationTimeTo, serviceStartTimeFrom, serviceStartTimeTo, serviceStopTimeFrom,
			serviceStopTimeTo, healthcareFacilityTypeCode, eventCodes, confidentialityCodes,
			author, formatCode, entryStatus, maxResultsCount, siteNumber,
			useAlternatePatientId, (String[])null);
	}
	/**
	 * @param patientId
	 * @param classCode
	 * @param practiceSettingCode
	 * @param creationTimeFrom
	 * @param creationTimeTo
	 * @param serviceStartTimeFrom
	 * @param serviceStartTimeTo
	 * @param serviceStopTimeFrom
	 * @param serviceStopTimeTo
	 * @param healthcareFacilityTypeCode
	 * @param eventCodes
	 * @param confidentialityCodes
	 * @param author
	 * @param formatCode
	 * @param entryStatus
	 * @param maxResultsCount
	 * @param siteNumber
	 * @param useAlternatePatientId
	 */
	public DocumentFilter(
		String patientId, String classCode, String practiceSettingCode, Date creationTimeFrom,
		Date creationTimeTo, Date serviceStartTimeFrom, Date serviceStartTimeTo, Date serviceStopTimeFrom,
		Date serviceStopTimeTo, String healthcareFacilityTypeCode, String[] eventCodes, String[] confidentialityCodes,
		String author, String formatCode, String entryStatus, int maxResultsCount, String siteNumber,
		boolean useAlternatePatientId, String[] documentClassCodes)
	{
		super(ProcedureFilterMatchMode.excludedInProcedureList);
		this.patientId = patientId;
		this.classCode = classCode;
		this.practiceSettingCode = practiceSettingCode;
		this.fromDate = creationTimeFrom;
		this.toDate = creationTimeTo;
		this.serviceStartTimeFrom = serviceStartTimeFrom;
		this.serviceStartTimeTo = serviceStartTimeTo;
		this.serviceStopTimeFrom = serviceStopTimeFrom;
		this.serviceStopTimeTo = serviceStopTimeTo;
		this.healthcareFacilityTypeCode = healthcareFacilityTypeCode;
		this.eventCodes = eventCodes;
		this.confidentialityCodes = confidentialityCodes;
		this.author = author;
		this.formatCode = formatCode;
		this.entryStatus = entryStatus;
		this.maxResultsCount = maxResultsCount;
		this.siteNumber = siteNumber;
		this.useAlternatePatientId = useAlternatePatientId;
		this.documentClassCodes = documentClassCodes;
	}

	/**
	 * 
	 * @return
	 */
	public String getPatientId()
	{
		return this.patientId;
	}

	public String getClassCode()
	{
		return this.classCode;
	}

	public String getPracticeSettingCode()
	{
		return this.practiceSettingCode;
	}

	public Date getCreationTimeFrom()
	{
		return this.fromDate;
	}

	public Date getCreationTimeTo()
	{
		return this.toDate;
	}

	public Date getServiceStartTimeFrom()
	{
		return this.serviceStartTimeFrom;
	}

	public Date getServiceStartTimeTo()
	{
		return this.serviceStartTimeTo;
	}

	public Date getServiceStopTimeFrom()
	{
		return this.serviceStopTimeFrom;
	}

	public Date getServiceStopTimeTo()
	{
		return this.serviceStopTimeTo;
	}

	public String getHealthcareFacilityTypeCode()
	{
		return this.healthcareFacilityTypeCode;
	}

	public String[] getEventCodes()
	{
		return this.eventCodes;
	}

	public String[] getConfidentialityCodes()
	{
		return this.confidentialityCodes;
	}

	public String getAuthor()
	{
		return this.author;
	}

	public String getFormatCode()
	{
		return this.formatCode;
	}

	public String getEntryStatus()
	{
		return this.entryStatus;
	}
	
	public int getMaxResultsCount()
	{
		return this.maxResultsCount;
	}
	
	/**
	 * @return the documentClassCodes
	 */
	public String[] getDocumentClassCodes()
	{
		return this.documentClassCodes;
	}

	private final static Logger logger = Logger.getLogger(DocumentFilter.class);
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.StudyFilter#postFilter(java.util.Collection)
	 */
	@Override
	public void postFilter(Collection<? extends StudyFilterFilterable> studies) 
	{
		logger.info("Filter collection of '" + studies.size() + " documents.");
		for(Iterator<? extends StudyFilterFilterable> iter = studies.iterator(); iter.hasNext();)
		{
			StudyFilterFilterable study = iter.next();
			
			// if a creation time start range was specified and the start of the range is after the procedure time
			// then remove the study from the list
			if(getCreationTimeFrom() != null && study.getProcedureDate() != null && getCreationTimeFrom().after(study.getProcedureDate()))
			{
				iter.remove();
				continue;
			}
			// if a creation time end range was specified and the end of the range is before the procedure time
			// then remove the study from the list
			if(getCreationTimeTo() != null && study.getProcedureDate() != null && getCreationTimeTo().before(study.getProcedureDate()))
			{
				iter.remove();
				continue;
			}
			
			String imageClass = study.getFirstImageClass();
			if(imageClass == null)
				logger.warn("Study [" + study.getStudyIen() + "] has null first image class, not removing from list");
			else if( getClassCode() != null && getClassCode().length() != 0 && 
				study.getFirstImageClass() != null && 
				!getClassCode().equals(study.getFirstImageClass()))
			{
				iter.remove();
				continue;
			}
			
			if(this.documentClassCodes != null)
			{
				String imageType = ((Study)study).getImageType();
				boolean passthrough = false;
				for(String documentClassCode : documentClassCodes)
					if(documentClassCode == null || documentClassCode.equals(imageType))
						passthrough = true;
				
				if(!passthrough)
					iter.remove();
			}
		}
		logger.info("resulting collection after filtering has '" + studies.size() + " documents.");
	}

	public String getSiteNumber()
	{
		return siteNumber;
	}

	public void setSiteNumber(String siteNumber)
	{
		this.siteNumber = siteNumber;
	}

	/**
	 * @return the useAlternatePatientId
	 */
	public boolean isUseAlternatePatientId() {
		return useAlternatePatientId;
	}

	/**
	 * @param useAlternatePatientId the useAlternatePatientId to set
	 */
	public void setUseAlternatePatientId(boolean useAlternatePatientId) {
		this.useAlternatePatientId = useAlternatePatientId;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder(); 
		
		sb.append(this.getClass().getSimpleName());
		sb.append(':');
		
		sb.append(" creationTimeFrom: [" + this.fromDate + "]" );
		sb.append(" creationTimeTo: [" + this.toDate + "]" );
		sb.append(" author: [" + this.author + "]" );
		sb.append(" classCode: [" + this.classCode + "]" );
		sb.append(" entryStatus: [" + this.entryStatus + "]" );
		sb.append(" healthcareFacilityTypeCode: [" + this.healthcareFacilityTypeCode + "]" );
		sb.append(" maxResultsCount: [" + this.maxResultsCount + "]" );
		sb.append(" patientId: [" + this.patientId + "]" );
		sb.append(" practiceSettingCode: [" + this.practiceSettingCode + "]" );
		
		sb.append(" documentClassCode: [" );
		if(this.documentClassCodes != null)
			for(String documentClassCode : this.documentClassCodes)
				sb.append( documentClassCode + "," );
		else
			sb.append("null");
		sb.append("]");
		
		return sb.toString();
	}

}
