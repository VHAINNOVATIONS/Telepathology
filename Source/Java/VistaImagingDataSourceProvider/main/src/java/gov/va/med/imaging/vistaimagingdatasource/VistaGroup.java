/**
 * 
 */
package gov.va.med.imaging.vistaimagingdatasource;

import gov.va.med.PatientIdentifier;
import gov.va.med.imaging.exchange.business.StudyFilterFilterable;
import gov.va.med.imaging.exchange.enums.StudyDeletedImageState;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.exchange.enums.ObjectStatus;

import java.io.Serializable;
import java.util.*;

/**
 * @author vhaiswbeckec
 *
 * A VistaGroup is a VistA Imaging only construct that represents the grouping of images
 * in the VistA Imaging database.  A VistaGroup may be analogous to either a Study, a Series
 * or a DocumentSet depending on context.  A VistaGroup is an aggregation of VistaImage
 * instances.
 * 
 * NOTE: the VistaGroup class is not a VIX core value object and should never find its way
 * into that package, it should also never be used outside of the Vista Data Source.
 * 
 */
public class VistaGroup
implements Serializable, Comparable<VistaGroup>, StudyFilterFilterable
{
	private static final long serialVersionUID = 1L;

	private final StudyLoadLevel studyLoadLevel;
	private final StudyDeletedImageState studyDeletedImageState;
	private String ien;	// internal entry number in VistA
	
	private Hashtable<String, String> studyValues = new Hashtable<String, String>(10);
	private String radiologyReport;
	private PatientIdentifier patientIdentifier;
	private String patientName;
	private String studyUid; // dicom UID
	private String firstImageIen;
	private String rpcResponseMsg;
	private Set<String> modalities = new TreeSet<String>();
	private Date procedureDate;
	private String errorMessage;
	private String alienSiteNumber;
	private ObjectStatus studyStatus = ObjectStatus.NO_STATUS;
	private ObjectStatus studyViewStatus = ObjectStatus.NO_STATUS;
	private boolean sensitive = false;
	private Date documentDate = null;	
	private String siteAbbr;
	private boolean studyImagesHaveAnnotations = false;
	
	private VistaImage firstImage;

	/*
	 * private int imageCount; private String noteTitle; private String
	 * imagePackage; private String imageType; private String specialty; private
	 * String event; // event title private String origin; // VA, DOD, FEE,
	 * NON-VA private String captureDate; // date image captured private String
	 * captureBy; // empty if coming from Gateway
	 */

	public VistaGroup(StudyLoadLevel studyLoadLevel, StudyDeletedImageState studyDeletedImageState)
	{
		super();
		radiologyReport = patientName = studyUid = firstImageIen = rpcResponseMsg = errorMessage = alienSiteNumber = "";
		patientIdentifier = null;
		this.studyLoadLevel = studyLoadLevel;
		this.studyDeletedImageState = studyDeletedImageState;		
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

	public String getRadiologyReport()
	{
		return radiologyReport;
	}

	public void setRadiologyReport(String radiologyReport)
	{
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

	public PatientIdentifier getPatientIdentifier()
	{
		return patientIdentifier;
	}

	public void setPatientIdentifier(PatientIdentifier patientIdentifier)
	{
		this.patientIdentifier = patientIdentifier;
	}

	/**
	 * @return the studyIen
	 */
	public String getIen()
	{
		return ien;
	}

	public void setIen(String ien)
	{
		this.ien = ien;
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
	 * @return the studyUid
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
	public VistaImage getFirstVistaImage()
	{
		return firstImage;
	}

	/**
	 * @param firstImage
	 *            the firstImage to set
	 */
	public void setFirstVistaImage(VistaImage firstImage)
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
	public StudyLoadLevel getStudyLoadLevel() 
	{
		return studyLoadLevel;
	}

	// ================================================================================================
	// getters and setters for Study dynamic properties
	public int getImageCount() 
	{
		return getValueAsInt("# Img");
	}
	public void setImageCount(int imageCount)
	{
		setValue("# Img", imageCount);
	}

	public String getProcedure()
	{
		return getValue("Procedure");
	}
	public void setProcedure(String procedure)
	{
		setValue("Procedure", procedure);
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
	// ==========================================================================
	// StudyFilterFilterable Implementation
	// ==========================================================================
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.StudyFilterFilterable#getFirstImageClass()
	 */
	@Override
	public String getFirstImageClass()
	{
		return getFirstVistaImage() == null ? null : getFirstVistaImage().getImageClass();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.StudyFilterFilterable#getFirstImageType()
	 */
	@Override
	public int getFirstImageType()
	{
		return getFirstVistaImage() == null ? -1 : getFirstVistaImage().getImgType();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.StudyFilterFilterable#getStudyIen()
	 */
	@Override
	public String getStudyIen()
	{
		return getIen();
	}
	
	// ================================================================================================
	
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
	
	public StudyDeletedImageState getStudyDeletedImageState()
	{
		return studyDeletedImageState;
	}

	public String getSiteAbbr()
	{
		return siteAbbr;
	}

	public void setSiteAbbr(String siteAbbr)
	{
		this.siteAbbr = siteAbbr;
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
	 * The natural ordering of Study instances is defined as: 
	 * 1.) decreasing by procedureDate
	 * 2.) increasing by site number
	 * 3.) increasing by IEN
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(VistaGroup that)
	{
		int cumulativeCompare = 0;
		
		if(this.equals(that))
			return 0;
		
		cumulativeCompare = this.procedureDate == null ?  
			this.procedureDate.after(that.procedureDate) ? -1 : 
			this.procedureDate.after(that.procedureDate) ? 1 : 0 : 1;
		if(cumulativeCompare != 0)
			return cumulativeCompare;
		
		return this.ien == null ? -1 : this.ien.compareTo(that.ien);
	}

	/**
	 * The implementation of hashCode is consistent with the definition in
	 * java.lang.Object, including compatibility with .equals(). The hashCode is
	 * based on the procedure date, first image IEN and the study IEN, which will 
	 * provide the correct behavior and uniqueness if those fields are populated.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.firstImageIen == null) ? 0 : this.firstImageIen.hashCode());
		result = prime * result + ((this.ien == null) ? 0 : this.ien.hashCode());
		result = prime * result + ((this.procedureDate == null) ? 0 : this.procedureDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final VistaGroup other = (VistaGroup) obj;
		if (this.firstImageIen == null)
		{
			if (other.firstImageIen != null)
				return false;
		}
		else if (!this.firstImageIen.equalsIgnoreCase(other.firstImageIen))
			return false;
		if (this.ien == null)
		{
			if (other.ien != null)
				return false;
		}
		else if (!this.ien.equalsIgnoreCase(other.ien))
			return false;
		if (this.procedureDate == null)
		{
			if (other.procedureDate != null)
				return false;
		}
		else if (!this.procedureDate.equals(other.procedureDate))
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
		sb.append( "VistaGroup [" + this.ien + "]" );
		

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
}
