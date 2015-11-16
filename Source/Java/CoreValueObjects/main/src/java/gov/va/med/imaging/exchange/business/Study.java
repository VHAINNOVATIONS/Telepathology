/**
 * 
 */
package gov.va.med.imaging.exchange.business;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.MockDataGenerationField;
import gov.va.med.PatientIdentifier;
import gov.va.med.PatientIdentifierType;
import gov.va.med.StudyURNFactory;
import gov.va.med.imaging.BhieStudyURN;
import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.enums.ObjectOrigin;
import gov.va.med.imaging.exchange.enums.StudyDeletedImageState;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.exchange.enums.ObjectStatus;

import java.io.Serializable;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * @author vhaiswwerfej
 * 
 */
public class Study
implements Serializable, Comparable<Study>, Iterable<Series>, StudyFilterFilterable
{
	private static final long serialVersionUID = -7467740571635450273L;
	private final static Logger logger = Logger.getLogger(Study.class);

	private final GlobalArtifactIdentifier globalArtifactIdentifier;
	// JMW 9/30/2010 P104 - study load level is no longer final to support updating a study to 
	// include more information (usually the report although potentially the images)
	// the StudyLoadLevel should only be changed by specific methods that set other values
	// ie: only update the StudyLoadLevel if the report or images are added to the Study object
	private StudyLoadLevel studyLoadLevel;
	private final StudyDeletedImageState studyDeletedImageState;
	
	private Hashtable<String, String> studyValues = new Hashtable<String, String>(10);
	@MockDataGenerationField(minimumStringLength=32, maximumStringLength=1024)
	private String radiologyReport = null;
	@MockDataGenerationField(pattern=MockDataGenerationField.NAME_PATTERN)
	private String patientName;
	@MockDataGenerationField(pattern=MockDataGenerationField.UID_PATTERN)
	private String studyUid; // dicom UID
	private String firstImageIen;
	@MockDataGenerationField(pattern="[A-Z][a-z]{4,16}, [A-Z][a-z]")
	private String siteName;
	@MockDataGenerationField(pattern="[1-9][0-9][0-9]")
	private String siteAbbr;
	@MockDataGenerationField(componentValueType="gov.va.med.imaging.exchange.business.Series")
	private Set<Series> series = new TreeSet<Series>(new SeriesComparator());
	private Image firstImage;
	@MockDataGenerationField(pattern="[A-Z][a-z][0-9] [A-Z][a-z][0-9] [A-Z][a-z][0-9] [A-Z][a-z][0-9] [A-Z][a-z][0-9]")
	private String rpcResponseMsg;
	private Set<String> modalities = null;
	@MockDataGenerationField(minimumDate="01-01-1990", maximumDate="12-31-2010")
	private Date procedureDate;
	@MockDataGenerationField(pattern="[A-Z][a-z][0-9] [A-Z][a-z][0-9] [A-Z][a-z][0-9] [A-Z][a-z][0-9] [A-Z][a-z][0-9]")
	private String errorMessage;
	private String alienSiteNumber;
	
	private ObjectStatus studyStatus = ObjectStatus.NO_STATUS;
	private ObjectStatus studyViewStatus = ObjectStatus.NO_STATUS;
	private boolean sensitive = false;
	private Date documentDate = null;	 
	private String cptCode = null;
	private String consolidatedSiteNumber = null;
	// if this object was created by cloning another object and changing the site number, then hold onto the old identifier just in case
	private GlobalArtifactIdentifier alternateArtifactIdentifier = null;
	private boolean studyImagesHaveAnnotations = false;
	
	//NOTE: If you add more fields to this class, be sure to add the new fields to cloneWithConsolidatedSiteNumber() 
	// to ensure the new fields are copied correctly

	/**
	 * 
	 * @param objectOrigin
	 * @param siteNumber
	 * @param studyIen
	 * @param studyLoadLevel
	 * @return
	 * @throws URNFormatException 
	 */
	public static Study create(
		ObjectOrigin objectOrigin, 
		String repositoryId, 
		String studyIen, 
		PatientIdentifier patientIdentifier,
		StudyLoadLevel studyLoadLevel,
		StudyDeletedImageState studyDeletedImageState) 
	throws URNFormatException
	{
		if(objectOrigin == null)
		{
			Logger.getLogger(Study.class).error("Unable to create Study instance because object origin is null.");
			return null;
		}
		if(repositoryId == null)
		{
			Logger.getLogger(Study.class).error("Unable to create Study instance because repository ID is null.");
			return null;
		}
		if(studyIen == null)
		{
			Logger.getLogger(Study.class).error("Unable to create Study instance because Study IEN is null.");
			return null;
		}
		if(patientIdentifier == null)
		{
			Logger.getLogger(Study.class).error("Unable to create Study instance because Patient ID is null.");
			return null;
		}
		if(studyLoadLevel == null)
		{
			Logger.getLogger(Study.class).error("Unable to create Study instance because given StudyLoadLevel is null.");
			return null;
		}
		
		StudyURN studyUrn = StudyURNFactory.create(repositoryId, studyIen, patientIdentifier.getValue(), StudyURN.class);
		// set the patient identifier type if it is necessary
		studyUrn.setPatientIdentifierTypeIfNecessary(patientIdentifier.getPatientIdentifierType());
		return create(studyUrn, studyLoadLevel, studyDeletedImageState);
	}

	/**
	 * 
	 * @param identifier
	 * @return
	 */
	public static Study create(GlobalArtifactIdentifier identifier, StudyLoadLevel studyLoadLevel,
			StudyDeletedImageState studyDeletedImageState)
	{
		if(identifier == null)
		{
			Logger.getLogger(Study.class).error("Unable to create Study instance because given identifier is null.");
			return null;
		}
		if(studyLoadLevel == null)
		{
			Logger.getLogger(Study.class).error("Unable to create Study instance because given StudyLoadLevel is null.");
			return null;
		}
		
		return new Study(identifier, studyLoadLevel, studyDeletedImageState);
	}

	/*
	 * private int imageCount; private String noteTitle; private String
	 * imagePackage; private String imageType; private String specialty; private
	 * String event; // event title private String origin; // VA, DOD, FEE,
	 * NON-VA private String captureDate; // date image captured private String
	 * captureBy; // empty if coming from Gateway
	 */
	private Study(GlobalArtifactIdentifier globalArtifactIdentifier, StudyLoadLevel studyLoadLevel, 
			StudyDeletedImageState studyDeletedImageState)
	{
		this.globalArtifactIdentifier = globalArtifactIdentifier;
		this.studyLoadLevel = studyLoadLevel;
		this.studyDeletedImageState = studyDeletedImageState;
		
		radiologyReport = patientName = studyUid = firstImageIen = siteName = siteAbbr = "";
		modalities = new TreeSet<String>();
		/*
		 * imageCount = 0; noteTitle = imagePackage = imageType = specialty =
		 * event = origin = captureBy = captureDate = "";
		 */
		errorMessage = "";
		alienSiteNumber = "";
		
	}

	/**
	 * Get the unique identifier of this Study
	 * @return
	 */
	public GlobalArtifactIdentifier getGlobalArtifactIdentifier()
	{
		return this.globalArtifactIdentifier;
	}

	public int getSeriesCount()
	{
		return this.series.size();
	}

	public Set<Series> getSeries()
	{
		return series;
	}

	public void addSeries(Series series)
	{
		this.series.add(series);
	}

	@Override
	public Iterator<Series> iterator()
	{
		return series.iterator();
	}

	/**
	 * Return a child Series from the series (group) IEN.
	 * Returns null if the Series identified by the IEN is not a child.
	 * Returns null if seriesIen is null.
	 * 
	 * @param seriesIen
	 * @return
	 */
	public Series getChildSeries(String seriesIen)
	{
		if(seriesIen == null || getSeries() == null)
			return null;
		
		for(Series series : getSeries())
			if( seriesIen.equals(series.getSeriesIen()) )
				return series;
		
		return null;
	}
	
	public void setValue(String key, String value)
	{
		studyValues.put(key, value);
	}

	public String getValue(String key)
	{
		String value = studyValues.get(key);
		return value == null ? "" : value;
	}

	public void setValue(String key, int value)
	{
		studyValues.put(key, Integer.toString(value));
	}

	public int getValueAsInt(String key)
	{
		String value = studyValues.get(key);
		int valueAsInt = 0;
		if (value != null)
		{
			valueAsInt = Integer.parseInt(value);
		}
		return valueAsInt;
	}

	public Enumeration<String> getKeys()
	{
		return studyValues.keys();
	}

	// getters and setters for Study dynamic properties
	public String getProcedure()
	{
		return getValue("Procedure");
	}

	public void setProcedure(String procedure)
	{
		setValue("Procedure", procedure);
	}

//  public int getImageCount() {
//  return getValueAsInt("# Img");
//}

	public int getImageCount() {
	    // CPT -- 07/17/08: make sure all series' image counts added up for multiple groups
	    int numImages = 0;
	    Set<Series> seriesSet = this.getSeries();
	    if(seriesSet != null && seriesSet.size() > 1) {
	          for(Iterator<Series> seriesIter = seriesSet.iterator(); seriesIter.hasNext(); )
	          {
	                Series series = seriesIter.next();
	                numImages += series.getImageCount();
	          }
	    } else { // no series in study ??
	          numImages = getValueAsInt("# Img");
	    }
	    return numImages;
	}

	public void setImageCount(int imageCount)
	{
		setValue("# Img", imageCount);
	}

	public String getNoteTitle()
	{
		return getValue("Note Title");
	}

	public void setNoteTitle(String noteTitle)
	{
		setValue("Note Title", noteTitle);
	}

	public String getImagePackage()
	{
		return getValue("Pkg");
	}

	public void setImagePackage(String imagePackage)
	{
		setValue("Pkg", imagePackage);
	}

	public String getImageType()
	{
		return getValue("Type");
	}

	public void setImageType(String imageType)
	{
		setValue("Type", imageType);
	}

	public String getSpecialty()
	{
		return getValue("Specialty");
	}

	public void setSpecialty(String specialty)
	{
		setValue("Specialty", specialty);
	}

	public String getEvent()
	{
		return getValue("Event");
	}

	public void setEvent(String event)
	{
		setValue("Event", event);
	}

	public String getOrigin()
	{
		return getValue("Origin");
	}

	public void setOrigin(String origin)
	{
		setValue("Origin", origin);
	}

	public String getCaptureDate()
	{
		return getValue("Cap Dt");
	}

	public void setCaptureDate(String captureDate)
	{
		setValue("Cap Dt", captureDate);
	}

	public String getCaptureBy()
	{
		return getValue("Cap by");
	}

	public void setCaptureBy(String captureBy)
	{
		setValue("Cap by", captureBy);
	}

	public String getRadiologyReport()
	{
		return radiologyReport;
	}

	public void setRadiologyReport(String radiologyReport)
	{
		// JMW 9/30/2010 P104
		// if the report is set, ensure the StudyLoadLevel represents that information
		if(radiologyReport != null)
		{
			this.studyLoadLevel = StudyLoadLevel.promoteWithReport(this.studyLoadLevel);
		}
		this.radiologyReport = radiologyReport;
	}

	public void setProcedureDate(Date procedureDate)
	{
		this.procedureDate = procedureDate;
	}
	
	public String getProcedureDateString()
	{
		return getValue("Proc DT");
	}
	
	public void setProcedureDateString(String value)
	{
		setValue("Proc DT", value);
	}

	public Date getProcedureDate()
	{
		return this.procedureDate;
	}

	public void setDescription(String description)
	{
		setValue("Short Desc", description);
	}

	public String getDescription()
	{
		return getValue("Short Desc");
	}
	
	public void setStudyClass(String studyClass)
	{
		setValue("Class", studyClass);
	}
	
	public String getStudyClass()
	{
		return getValue("Class");
	}

	/**
	 * If this is a VA Study then the patient ICN is in the global artifact identifier,
	 * else we don't have it.
	 * 
	 * @return the patientIcn
	 */
	public String getPatientId()
	{
		GlobalArtifactIdentifier gai = getGlobalArtifactIdentifier();
		if(gai instanceof StudyURN)
			return ((StudyURN)gai).getPatientId();
		if(gai instanceof BhieStudyURN)
			return ((BhieStudyURN)gai).getPatientId();
		
		return null;	
	}
	
	public PatientIdentifier getPatientIdentifier()
	{
		String patientId = getPatientId();
		PatientIdentifierType patientIdentifierType = getPatientIdentifierType();
		if(patientId == null || patientIdentifierType == null)
			return null;
		
		return new PatientIdentifier(patientId, patientIdentifierType);
	}
	
	public PatientIdentifierType getPatientIdentifierType()
	{
		GlobalArtifactIdentifier gai = getGlobalArtifactIdentifier();
		if(gai instanceof StudyURN)
			return ((StudyURN)gai).getPatientIdentifierTypeOrDefault();
		if(gai instanceof BhieStudyURN)
			return ((BhieStudyURN)gai).getPatientIdentifierTypeOrDefault();
		
		return null;	
	}

	/**
	 * If this is a VA Study then the study IEN is in the global artifact identifier,
	 * else we don't have it and the correct thing to ask for is the documentIdentifier
	 * 
	 * @return the studyIen
	 */
	public String getStudyIen()
	{
		GlobalArtifactIdentifier gai = getGlobalArtifactIdentifier();
		if(gai instanceof StudyURN)
			return ((StudyURN)gai).getGroupId();
		if(gai instanceof BhieStudyURN)
			return ((BhieStudyURN)gai).getDocumentUniqueId();
		
		return null;	
	}

	/**
	 * @return the siteNumber
	 */
	public String getSiteNumber()
	{
		return getGlobalArtifactIdentifier().getRepositoryUniqueId();
	}

	/**
	 * @return the patientName
	 */
	public String getPatientName()
	{
		return patientName;
	}

	/**
	 * @param patientName
	 *            the patientName to set
	 */
	public void setPatientName(String patientName)
	{
		this.patientName = patientName;
	}

	/**
	 * @return the studyUid (the DICOM UID)
	 */
	public String getStudyUid()
	{
		return studyUid;
	}

	/**
	 * @return the firstImageIen
	 */
	public String getFirstImageIen()
	{
		return firstImageIen;
	}

	/**
	 * @param firstImageIen
	 *            the firstImageIen to set
	 */
	public void setFirstImageIen(String firstImageIen)
	{
		this.firstImageIen = firstImageIen;
	}

	/**
	 * @return the siteAbbr
	 */
	public String getSiteAbbr()
	{
		return siteAbbr;
	}

	/**
	 * @param siteAbbr
	 *            the siteAbbr to set
	 */
	public void setSiteAbbr(String siteAbbr)
	{
		this.siteAbbr = siteAbbr;
	}

	/**
	 * @return the siteName
	 */
	public String getSiteName()
	{
		return siteName;
	}

	/**
	 * @param siteName
	 *            the siteName to set
	 */
	public void setSiteName(String siteName)
	{
		this.siteName = siteName;
	}

	/**
	 * @param studyUid
	 *            the studyUid to set
	 */
	public void setStudyUid(String studyUid)
	{
		this.studyUid = studyUid;
	}

	/**
	 * @return the firstImage
	 */
	public Image getFirstImage()
	{
		return firstImage;
	}

	/**
	 * @param firstImage
	 *            the firstImage to set
	 */
	public void setFirstImage(Image firstImage)
	{
		this.firstImage = firstImage;
	}

	/**
	 * @return the rpcResponseMsg
	 */
	public String getRpcResponseMsg()
	{
		return rpcResponseMsg;
	}

	/**
	 * @param rpcResponseMsg
	 *            the rpcResponseMsg to set
	 */
	public void setRpcResponseMsg(String rpcResponseMsg)
	{
		this.rpcResponseMsg = rpcResponseMsg;
	}

	/**
	 * @return the objectOrigin
	 */
	public ObjectOrigin getObjectOrigin()
	{
		return ObjectOrigin.inferFromHomeCommunityId(getGlobalArtifactIdentifier().getHomeCommunityId());
	}

	public StudyURN getStudyUrn() 
	{
		return getGlobalArtifactIdentifier() instanceof StudyURN ?
			(StudyURN)getGlobalArtifactIdentifier() : null;
	}

	public void addModality(String modality)
	{
		this.modalities.add(modality);
	}

	public Set<String> getModalities()
	{
		return this.modalities;
	}	

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public boolean hasErrorMessage()
	{
		if((errorMessage == null) || (errorMessage.length() <= 0))
			return false;
		return true;
	}
	
	public boolean isDeleted()
	{
		return (studyViewStatus == ObjectStatus.DELETED);
	}


	/**
	 * The alien site number is the number for a site where the study came from. This number is the number
	 * from the alien data source (DOD).  This is not the site number used internally that exists within the
	 * site service but an external number. This number is not required to be present and should not transmit
	 * across Federation. 
	 * @return the alienSiteNumber
	 */
	public String getAlienSiteNumber() {
		return alienSiteNumber;
	}

	/**
	 * @param alienSiteNumber the alienSiteNumber to set
	 */
	public void setAlienSiteNumber(String alienSiteNumber) {
		this.alienSiteNumber = alienSiteNumber;
	}

	/**
	 * @return the studyLoadLevel
	 */
	public StudyLoadLevel getStudyLoadLevel() {
		return studyLoadLevel;
	}

	public ObjectStatus getStudyStatus()
	{
		return studyStatus;
	}

	public void setStudyStatus(ObjectStatus studyStatus)
	{
		this.studyStatus = studyStatus;
	}

	public ObjectStatus getStudyViewStatus()
	{
		return studyViewStatus;
	}

	public void setStudyViewStatus(ObjectStatus studyViewStatus)
	{
		this.studyViewStatus = studyViewStatus;
	}

	public boolean isSensitive()
	{
		return sensitive;
	}

	public void setSensitive(boolean sensitive)
	{
		this.sensitive = sensitive;
	}

	public Date getDocumentDate()
	{
		return documentDate;
	}

	public void setDocumentDate(Date documentDate)
	{
		this.documentDate = documentDate;
	}

	public String getCptCode()
	{
		return cptCode;
	}

	public void setCptCode(String cptCode)
	{
		this.cptCode = cptCode;
	}

	public String getConsolidatedSiteNumber()
	{
		return consolidatedSiteNumber;
	}

	public void setConsolidatedSiteNumber(String consolidatedSiteNumber)
	{
		this.consolidatedSiteNumber = consolidatedSiteNumber;
	}
	
	public boolean containsConsolidatedSiteNumber()
	{
		if(this.consolidatedSiteNumber == null)
			return false;
		if(this.consolidatedSiteNumber.length() <= 0)
			return false;
		// if the consolidated site number value is the same as the site number, no need to update the value
		// just return false to avoid unnecessary effort
		if(this.consolidatedSiteNumber.equals(this.getSiteNumber()))
			return false;
		return true;
	}

	/**
	 * Needs to be set if the study contains deleted images, to be used to determine if the study objects
	 * can satisfy a request from the client
	 * @return
	 */
	public StudyDeletedImageState getStudyDeletedImageState()
	{
		return studyDeletedImageState;
	}

	public GlobalArtifactIdentifier getAlternateArtifactIdentifier()
	{
		return alternateArtifactIdentifier;
	}

	public void setAlternateArtifactIdentifier(
			GlobalArtifactIdentifier alternateArtifactIdentifier)
	{
		this.alternateArtifactIdentifier = alternateArtifactIdentifier;
	}

	// ==========================================================================
	// StudyFilterFilterable Implementation
	// ==========================================================================
	@Override
	public String getFirstImageClass()
	{
		return getFirstImage() == null ? null : getFirstImage().getImageClass();
	}

	@Override
	public int getFirstImageType()
	{
		return getFirstImage() == null ? -1 : getFirstImage().getImgType();
	}
	
	public boolean isStudyImagesHaveAnnotations()
	{
		return studyImagesHaveAnnotations;
	}

	public void setStudyImagesHaveAnnotations(boolean studyImagesHaveAnnotations)
	{
		this.studyImagesHaveAnnotations = studyImagesHaveAnnotations;
	}

	/**
	 * The natural ordering of Study instances is defined as: 1.) decreasing by
	 * procedureDate
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Study that)
	{
		int cumulativeCompare = 0;
		
		cumulativeCompare = ComparableUtil.compare(this.procedureDate, that.procedureDate, false);
		if(cumulativeCompare != 0)
			return cumulativeCompare;

		return getGlobalArtifactIdentifier().compareTo(that.getGlobalArtifactIdentifier());
	}

	/**
	 * The implementation of hashCode is consistent with the definition in
	 * java.lang.Object, including compatibility with .equals(). The hashCode is
	 * based on the site number and the study IEN, which will provide the
	 * correct behavior and uniqueness if those fields are populated.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
			+ ((this.globalArtifactIdentifier == null) ? 0 : this.globalArtifactIdentifier.hashCode());
		return result;
	}

	/**
	 * The .equals() is based on the site number and the study IEN, which will
	 * provide the correct behavior and uniqueness if those fields are
	 * populated.
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
		final Study other = (Study) obj;
		if (this.globalArtifactIdentifier == null)
		{
			if (other.globalArtifactIdentifier != null)
				return false;
		}
		else if (!this.globalArtifactIdentifier.equalsGlobalArtifactIdentifier(other.globalArtifactIdentifier))
			return false;
		return true;
	}	
	
	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		/*
		 * String output = super.toString(); output += "\nStudy Properties:\n";
		 * Enumeration<String> enumeration = studyValues.keys();
		 * while(enumeration.hasMoreElements()) { String key =
		 * enumeration.nextElement(); output += key + ": " +
		 * studyValues.get(key) + "\n"; }
		 */
		sb.append("Study Details [" + this.getGlobalArtifactIdentifier() + "]\t" + "Study UID [" + this.studyUid + "]\n" + "Contains " + series.size()
		        + " series:\n");
		

		Iterator<Series> seriesIter = series.iterator();
		while (seriesIter.hasNext())
		{
			Series series = seriesIter.next();
			sb.append(series.toString());
		}

		/*
		 * output += "NoteTitle: " + noteTitle + "\n"; output += "ImagePackage: " +
		 * imagePackage + "\n"; output += "ImageType: " + imageType + "\n";
		 * output += "Specialty: " + specialty + "\n"; output += "Event: " +
		 * event + "\n"; output += "Origin: " + origin + "\n"; output +=
		 * "CaptureDate: " + captureDate + "\n"; output += "CapturedBy: " +
		 * captureBy + "\n";
		 */
		return sb.toString();
	}

	/**
	 * This method clones all pieces of the Study and updates the URN with the value from the consolidated site number.
	 * This method should only be called if its been confirmed that the consolidated site number is "valid"
	 * @return
	 */
	public Study cloneWithConsolidatedSiteNumber(Site site)
	{
		if(containsConsolidatedSiteNumber())
		{
			if(!this.getSiteNumber().equals(this.consolidatedSiteNumber))
			{
				StudyURN studyUrn = this.getStudyUrn();
				// if the study URN is null then this is not a VA study and we shouldn't do anything with it
				if(studyUrn != null)
				{
					try
					{
						
						StudyURN newUrn = 
							StudyURNFactory.create(this.consolidatedSiteNumber, studyUrn.getStudyId(), 
									studyUrn.getPatientId(), StudyURN.class);
						newUrn.setPatientIdentifierTypeIfNecessary(studyUrn.getPatientIdentifierType());
						logger.debug("Updating study '" + studyUrn.toString() + "' with new site ID, created new URN '" + newUrn.toString() + "'.");
						Study study = new Study(newUrn, this.studyLoadLevel, this.studyDeletedImageState);
												
						study.alienSiteNumber = this.alienSiteNumber;
						study.consolidatedSiteNumber = this.consolidatedSiteNumber;
						study.cptCode = this.cptCode;
						study.documentDate = this.documentDate;
						study.errorMessage = this.errorMessage;
						study.firstImage = this.firstImage;
						study.firstImageIen = this.firstImageIen;
						study.modalities = this.modalities;
						study.patientName = this.patientName;
						study.procedureDate = this.procedureDate;
						study.radiologyReport = this.radiologyReport;
						study.rpcResponseMsg = this.rpcResponseMsg;
						study.sensitive = this.sensitive;
						study.series = this.series;
						// really need to update these!
						study.siteAbbr = this.siteAbbr; // don't update this because it should be correct from VistA
						study.siteName = site.getSiteName();
						study.studyStatus = this.studyStatus;
						study.studyUid = this.studyUid;
						study.studyValues.putAll(this.studyValues);
						study.studyViewStatus = this.studyViewStatus;
						study.alternateArtifactIdentifier = studyUrn;
						study.studyImagesHaveAnnotations = this.studyImagesHaveAnnotations;
						
						// add the series to the study, don't update the image objects because they could
						// have different consolidated site numbers than the study
						// updating the images happens elsewhere
						for(Series series : this)
						{
							study.addSeries(series);
						}
						
						return study;
					}
					catch(URNFormatException urnfX)
					{
						logger.warn("Error creating new StudyURN from consolidated site number, " + urnfX.getMessage(), urnfX);
					}					
				}
			}
		}
		// if we get here then either something went wrong or the conversion was not necessary
		return this;
	}
	
}
