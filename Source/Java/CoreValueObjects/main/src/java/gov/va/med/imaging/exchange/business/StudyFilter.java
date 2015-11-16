package gov.va.med.imaging.exchange.business;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.WellKnownOID;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.exceptions.OIDFormatException;
import gov.va.med.imaging.exchange.enums.PatientSensitivityLevel;
import java.io.Serializable;
import java.util.*;
import org.apache.log4j.Logger;

/**
 * This class is the internal, interface agnostic, Filter representation.
 *
 */
public class StudyFilter
implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	protected Date fromDate;
	protected Date toDate;
	private GlobalArtifactIdentifier studyId;
	private String study_package;
	private String study_class;
	private String study_type;
	private String study_event;
	private String study_specialty;
	private String origin;
	private Set<RoutingToken> excludeSiteNumbers = new HashSet<RoutingToken>();
	private PatientSensitivityLevel maximumAllowedLevel;
	private boolean includeDeleted = false;
	private int maximumResults = Integer.MAX_VALUE;
	
	public StudyFilter() 
	{
		studyId = null;
		study_class = study_event = study_package = study_specialty = study_type = origin = "";
		fromDate = null;
		toDate = null;
		maximumAllowedLevel = PatientSensitivityLevel.DISPLAY_WARNING;
	}
	
	/**
	 * Construct a Filter such that only the specified study is allowed through.
	 * 
	 * @param studyIen
	 */
	public StudyFilter(GlobalArtifactIdentifier studyId) 
	{
		this.studyId = studyId;
		study_class = study_event = study_package = study_specialty = study_type = origin = "";
		fromDate = null;
		toDate = null;
		maximumAllowedLevel = PatientSensitivityLevel.DISPLAY_WARNING;
	}
	
	/**
	 * Construct a Filter such that only filter instances meeting all of the non-null and 
	 * non-zero-length parameters are allowed through.
	 * 
	 * @param fromDate
	 * @param toDate
	 * @param study_package
	 * @param study_class
	 * @param study_type
	 * @param study_event
	 * @param study_specialty
	 * @param origin
	 * @param allowableStudyTypes
	 */
	public StudyFilter(
		Date fromDate, 
		Date toDate, 
		String study_package, 
		String study_class, 
		String study_type, 
		String study_event,
        String study_specialty, 
        String origin, 
        List<String> allowableStudyTypes)
    {
	    super();
	    this.studyId = null;
	    this.fromDate = fromDate;
	    this.toDate = toDate;
	    this.study_package = study_package;
	    this.study_class = study_class;
	    this.study_type = study_type;
	    this.study_event = study_event;
	    this.study_specialty = study_specialty;
	    this.origin = origin;
	    maximumAllowedLevel = PatientSensitivityLevel.DISPLAY_WARNING;
    }

	/**
	 * Method used to filter studies out of the result before all of the data has been retrieved from the datasource. 
	 * The filter should know how to remove studies from the collection. 
	 * This method should be overridden to perform necessary filtering.
	 * @param studies The collection of studies to filter. This collection will have studies that do not meet the filter removed
	 */
	public void preFilter(Collection<? extends StudyFilterFilterable> studies)
	{
		// do nothing here
	}
	
	/**
	 * Method used to filter the study list after it has been retrieved from the data source.
	 * The filter should know how to remove studies from the collection. 
	 * This method should be overridden to perform necessary filtering.
	 * @param studies The collection of studies to filter. This collection will have studies that do not meet the filter removed
	 */
	public void postFilter(Collection<? extends StudyFilterFilterable> studies)
	{
		// do nothing here
	}
	
	/**
	 * The 'correct' way to exclude routing destinations.
	 * 
	 * @param excludedDestinations
	 */
	public void setExcludedRoutingDestinations(Collection<RoutingToken> excludedDestinations)
	{
		excludeSiteNumbers.clear();
		excludeSiteNumbers.addAll(excludedDestinations);
	}
	
	/**
	 * Add a collection of VA sites to the list of excluded sites.
	 * This method creates two excluded RoutingToken instances for each site,
	 * one for radiology and one for documents.  This mimics the existing
	 * semantics.
	 * 
	 * @param siteNumbers
	 * @throws RoutingTokenFormatException 
	 */
	public void setExcludeSiteNumbers(Collection<String> siteNumbers) 
	throws RoutingTokenFormatException
	{
		excludeSiteNumbers.clear();
		for(String siteNumber : siteNumbers)
		{
			excludeSiteNumbers.add(RoutingTokenImpl.createVARadiologySite(siteNumber));
			excludeSiteNumbers.add(RoutingTokenImpl.createVADocumentSite(siteNumber));
		}
	}
	
	/**
	 * Determine if the given site number is on the list of excluded destinations.
	 * This will check only the VA sites (as either radiology and document) in the 
	 * exclude list.
	 * 
	 * @param siteNumber
	 * @return
	 */
	public boolean isSiteAllowed(String siteNumber)
	{
		if(excludeSiteNumbers == null || excludeSiteNumbers.size() == 0)
			return true;
		
		try
		{
			RoutingToken rtRadiology = RoutingTokenImpl.createVARadiologySite(siteNumber);
			RoutingToken rtDocument = RoutingTokenImpl.createVADocumentSite(siteNumber);
			
			return isRoutingDestinationAllowed(rtRadiology) && isRoutingDestinationAllowed(rtDocument);
		}
		catch (RoutingTokenFormatException x)
		{
			Logger.getLogger(this.getClass()).error("Error creating routing token from site number '" + siteNumber + "'.", x);
			return true;
		}
	}
	
	/**
	 * 
	 * @param routingToken
	 * @return
	 */
	public boolean isRoutingDestinationAllowed(RoutingToken routingToken)
	{
		if(excludeSiteNumbers == null || excludeSiteNumbers.size() == 0)
			return true;
		
		for(RoutingToken excludedDestination : excludeSiteNumbers)
			if(RoutingTokenImpl.isIncluding(excludedDestination, routingToken))
				return false;
		
		return true;
	}
	
	/**
	 * 
	 * @param fromDate
	 * @param toDate
	 * @param studyId
	 */
	public StudyFilter(Date fromDate, Date toDate, GlobalArtifactIdentifier studyId) 
	{
		super();
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.studyId = studyId;
		study_class = study_event = study_package = study_specialty = study_type = origin = "";
		maximumAllowedLevel = PatientSensitivityLevel.DISPLAY_WARNING;
	}

	/**
	 * @return the fromDate
	 */
	public Date getFromDate() 
	{
		return fromDate;
	}

	/**
	 * @param fromDate the fromDate to set
	 */
	public void setFromDate(Date fromDate) 
	{
		this.fromDate = fromDate;
	}

	/**
	 * @return the studyId
	 */
	public GlobalArtifactIdentifier getStudyId() 
	{
		return studyId;
	}

	/**
	 * @param studyId the studyId to set
	 */
	public void setStudyId(GlobalArtifactIdentifier studyId) 
	{
		this.studyId = studyId;
	}

	/**
	 * @return the toDate
	 */
	public Date getToDate() 
	{
		return toDate;
	}

	/**
	 * @param toDate the toDate to set
	 */
	public void setToDate(Date toDate) 
	{
		this.toDate = toDate;
	}

	/**
	 * @return the study_class
	 */
	public String getStudy_class() {
		return study_class;
	}

	/**
	 * @param study_class the study_class to set
	 */
	public void setStudy_class(String study_class) {
		this.study_class = study_class;
	}

	/**
	 * @return the study_event
	 */
	public String getStudy_event() {
		return study_event;
	}

	/**
	 * @param study_event the study_event to set
	 */
	public void setStudy_event(String study_event) {
		this.study_event = study_event;
	}

	/**
	 * @return the study_package
	 */
	public String getStudy_package() {
		return study_package;
	}

	/**
	 * @param study_package the study_package to set
	 */
	public void setStudy_package(String study_package) {
		this.study_package = study_package;
	}

	/**
	 * @return the study_specialty
	 */
	public String getStudy_specialty() {
		return study_specialty;
	}

	/**
	 * @param study_specialty the study_specialty to set
	 */
	public void setStudy_specialty(String study_specialty) {
		this.study_specialty = study_specialty;
	}

	/**
	 * @return the study_type
	 */
	public String getStudy_type() {
		return study_type;
	}

	/**
	 * @param study_type the study_type to set
	 */
	public void setStudy_type(String study_type) {
		this.study_type = study_type;
	}

	/**
	 * @return the origin
	 */
	public String getOrigin() {
		return origin;
	}

	/**
	 * @param origin the origin to set
	 */
	public void setOrigin(String origin) {
		this.origin = origin;
	}

	/**
	 * Filters are either for a specific study or for some
	 * filtered group of studies.  If the StudyIEN is specified then
	 * only that study will be allowed through.  If the StudyIEN is not
	 * specified then other criteria may be used.
	 * 
	 * @return
	 */
	public boolean isStudyIenSpecified()
	{
		return getStudyId() != null;
	}
	
	/**
	 * @param date
	 * @return returns true of the given date is within the range
	 *         specified in this filter (null start or end dates are evaluated
	 *         as BigBang and BigCrunch, respectively, assuming a closed universe)
	 */
	public boolean isWithinDateRange(Date date)
	{
		Date start = getFromDate();
		Date end = getToDate();
		
		if( start != null && start.after(date) )
			return false;
		
		if( end != null && end.before(date) )
			return false;
		
		return true;
	}

	/**
	 * 
	 * @param studyId
	 * @return returns true if the given study ID is allowed by this
	 *         Filter, is either the specified studyId or no studyID 
	 *         was specified. 
	 */
	public boolean isAllowableStudyId(GlobalArtifactIdentifier studyId)
	{
		if(studyId == null)
			return false;
		if(getStudyId() == null)
			return true;
		return getStudyId().toString().equals(studyId.toString());
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() 
	{
		StringBuilder sb = new StringBuilder(); 
		
		sb.append(this.getClass().getSimpleName());
		sb.append(':');
		
		sb.append(" fromDate: [" + this.fromDate + "]" );
		sb.append(" toDate: [" + this.toDate + "]" );
		sb.append(" studyID: [" + this.studyId + "]" );
		sb.append(" origin: [" + this.origin + "]" );
		sb.append(" studyClass: [" + this.study_class + "]" );
		sb.append(" studyEvent: [" + this.study_event + "]" );
		sb.append(" studyPackage: [" + this.study_package + "]" );
		sb.append(" studySpecialty: [" + this.study_specialty + "]" );
		sb.append(" studyType: [" + this.study_type + "]" );
		
		return sb.toString();
	}

	/**
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
    public int hashCode()
    {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((fromDate == null) ? 0 : fromDate.hashCode());
	    result = prime * result + ((origin == null) ? 0 : origin.hashCode());
	    result = prime * result + ((studyId == null) ? 0 : studyId.hashCode());
	    result = prime * result + ((study_class == null) ? 0 : study_class.hashCode());
	    result = prime * result + ((study_event == null) ? 0 : study_event.hashCode());
	    result = prime * result + ((study_package == null) ? 0 : study_package.hashCode());
	    result = prime * result + ((study_specialty == null) ? 0 : study_specialty.hashCode());
	    result = prime * result + ((study_type == null) ? 0 : study_type.hashCode());
	    result = prime * result + ((toDate == null) ? 0 : toDate.hashCode());
	    return result;
    }

	/**
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
    public boolean equals(Object obj)
    {
	    if (this == obj)
		    return true;
	    if (obj == null)
		    return false;
	    if (getClass() != obj.getClass())
		    return false;
	    final StudyFilter other = (StudyFilter) obj;
	    if (fromDate == null)
	    {
		    if (other.fromDate != null)
			    return false;
	    } else if (!fromDate.equals(other.fromDate))
		    return false;
	    if (origin == null)
	    {
		    if (other.origin != null)
			    return false;
	    } else if (!origin.equals(other.origin))
		    return false;
	    if (studyId == null)
	    {
		    if (other.studyId != null)
			    return false;
	    } else if (!studyId.equals(other.studyId))
		    return false;
	    if (study_class == null)
	    {
		    if (other.study_class != null)
			    return false;
	    } else if (!study_class.equals(other.study_class))
		    return false;
	    if (study_event == null)
	    {
		    if (other.study_event != null)
			    return false;
	    } else if (!study_event.equals(other.study_event))
		    return false;
	    if (study_package == null)
	    {
		    if (other.study_package != null)
			    return false;
	    } else if (!study_package.equals(other.study_package))
		    return false;
	    if (study_specialty == null)
	    {
		    if (other.study_specialty != null)
			    return false;
	    } else if (!study_specialty.equals(other.study_specialty))
		    return false;
	    if (study_type == null)
	    {
		    if (other.study_type != null)
			    return false;
	    } else if (!study_type.equals(other.study_type))
		    return false;
	    if (toDate == null)
	    {
		    if (other.toDate != null)
			    return false;
	    } else if (!toDate.equals(other.toDate))
		    return false;
	    return true;
    }

	/**
	 * @return the maximumAllowedLevel
	 */
	public PatientSensitivityLevel getMaximumAllowedLevel() {
		return maximumAllowedLevel;
	}

	/**
	 * @param maximumAllowedLevel the maximumAllowedLevel to set
	 */
	public void setMaximumAllowedLevel(PatientSensitivityLevel maximumAllowedLevel) {
		this.maximumAllowedLevel = maximumAllowedLevel;
	}

	public boolean isIncludeDeleted()
	{
		return includeDeleted;
	}

	public void setIncludeDeleted(boolean includeDeleted)
	{
		this.includeDeleted = includeDeleted;
	}

	public int getMaximumResults()
	{
		return maximumResults;
	}

	public void setMaximumResults(int maximumResults)
	{
		this.maximumResults = maximumResults;
	}
}
