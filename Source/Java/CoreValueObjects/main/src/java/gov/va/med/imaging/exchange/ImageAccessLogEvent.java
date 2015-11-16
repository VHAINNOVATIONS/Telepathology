/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Dec 19, 2006
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
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
package gov.va.med.imaging.exchange;

/**
 * Log Event to be put into the database. Logs Image access, copy access and print access
 * 
 * @author VHAISWWERFEJ
 *
 */
public class ImageAccessLogEvent 
{
	public enum ImageAccessLogEventType 
	{
		IMAGE_ACCESS, IMAGE_COPY, IMAGE_PRINT, PATIENT_ID_MISMATCH, RESTRICTED_ACCESS;

		@Override
		public String toString() {
			if(this == IMAGE_ACCESS)
			{
				return "Image_Access";
			}
			else if(this == IMAGE_COPY)
			{
				return "Image_Copy";
			}
			else if(this == IMAGE_PRINT)
			{
				return "Image_Print";
			}
			else if(this == PATIENT_ID_MISMATCH)
			{
				return "Patient_ID_Mismatch";
			}
			else if(this == RESTRICTED_ACCESS)
			{
				return "Restricted Access";
			}
			// TODO Auto-generated method stub
			return super.toString();
		}
	}
	
	private final String imageIen;
	private String patientDfn = null;			// the patient DFN is mutable
	private final String patientIcn;
	private final long imageAccessEventTime;
	private final String reasonCode; // if the event is a copy or print a reason must be given
	private final String reasonDescription;
	private final String siteNumber;
	private final ImageAccessLogEventType eventType;
	private final boolean dodImage; // determines if the image being looked at is a DOD image being accessed by a VA user
	private String decodedImageIen = null;
	private final String userSiteNumber;
	
	/**
	 * Constructor to use when logging access to a VA image and the patient DFN is unknown.
	 *  
	 * @param imageIen
	 * @param patientIcn
	 * @param siteNumber
	 * @param eventTime
	 * @param reason
	 * @param eventType
	 */
	/*
	public ImageAccessLogEvent(
			String imageIen, 
			String patientIcn, 
			String siteNumber, 
			long eventTime, 
			String reason, 
			ImageAccessLogEventType eventType) 
	{
		this(imageIen, null, patientIcn, siteNumber, eventTime, reason, eventType);
	}
	*/
	
	/**
	 * Constructor to use when logging access to a VA image.
	 * 
	 * @param imageIen
	 * @param patientDfn
	 * @param patientIcn
	 * @param siteNumber
	 * @param eventTime
	 * @param reason
	 * @param eventType
	 */
	public ImageAccessLogEvent(
			String imageIen, 
			String patientDfn, 
			String patientIcn, 
			String siteNumber, 
			long eventTime, 
			String reasonCode,
			String reasonDescription, 
			ImageAccessLogEventType eventType,
			String userSiteNumber) 
	{
		this( imageIen, patientDfn, patientIcn, siteNumber, eventTime, reasonCode, 
				reasonDescription, eventType, false, userSiteNumber ); 
	}
	
	/**
	 * Constructor to use for logging VA or DOD images.
	 * @param imageIen
	 * @param patientDfn
	 * @param patientIcn
	 * @param siteNumber
	 * @param eventTime
	 * @param reason
	 * @param eventType
	 * @param dodImage
	 */
	public ImageAccessLogEvent(
			String imageIen, 
			String patientDfn, 
			String patientIcn, 
			String siteNumber, 
			long eventTime, 
			String reasonCode, 
			String reasonDescription,
			ImageAccessLogEventType eventType, 
			boolean dodImage,
			String userSiteNumber)
	{
		this.dodImage = dodImage;
		
		this.imageIen = imageIen;
		this.patientIcn = patientIcn;		
		
		this.patientDfn = patientDfn;
		this.imageAccessEventTime = eventTime;
		this.reasonCode = reasonCode;
		this.siteNumber = siteNumber;
		this.eventType = eventType;
		this.userSiteNumber = userSiteNumber;
		this.reasonDescription = reasonDescription;
	}

	/**
	 * @return the imageAccessEventTime
	 */
	public long getImageAccessEventTime() {
		return imageAccessEventTime;
	}

	/**
	 * @return the imageIen
	 */
	public String getImageIen() {
		return imageIen;
	}

	/**
	 * @return the patientDfn
	 */
	public String getPatientDfn() {
		return patientDfn;
	}
	/**
	 * @param patientDfn the patientDfn to set
	 */
	public void setPatientDfn(String patientDfn)
	{
		this.patientDfn = patientDfn;
	}

	/**
	 * @return the siteNumber
	 */
	public String getSiteNumber() {
		return siteNumber;
	}

	public String getReasonCode()
	{
		return reasonCode;
	}

	public String getReasonDescription()
	{
		return reasonDescription;
	}

	/**
	 * @return the eventType
	 */
	public ImageAccessLogEventType getEventType() {
		return eventType;
	}

	/**
	 * @return the patientIcn
	 */
	public String getPatientIcn() {
		return patientIcn;
	}

	/**
	 * determines if the image being looked at is a DOD image being accessed by a VA user
	 * @return the dodImage
	 */
	public boolean isDodImage() {
		return dodImage;
	}

	public String getDecodedImageIen() {
		return decodedImageIen;
	}

	public void setDecodedImageIen(String decodedImageIen) {
		this.decodedImageIen = decodedImageIen;
	}

	/**
	 * This describes the user's site number - the site where the user viewed the image from.
	 * This value is expected to be 200 for DOD or one of the other known VA site numbers (756, 660, 688, etc)
	 * @return the userSiteNumber
	 */
	public String getUserSiteNumber() {
		return userSiteNumber;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.decodedImageIen == null) ? 0 : this.decodedImageIen.hashCode());
		result = prime * result + (this.dodImage ? 1231 : 1237);
		result = prime * result + ((this.eventType == null) ? 0 : this.eventType.hashCode());
		result = prime * result + (int) (this.imageAccessEventTime ^ (this.imageAccessEventTime >>> 32));
		result = prime * result + ((this.imageIen == null) ? 0 : this.imageIen.hashCode());
		result = prime * result + ((this.patientDfn == null) ? 0 : this.patientDfn.hashCode());
		result = prime * result + ((this.patientIcn == null) ? 0 : this.patientIcn.hashCode());
		result = prime * result + ((this.reasonCode == null) ? 0 : this.reasonCode.hashCode());
		result = prime * result + ((this.reasonDescription == null) ? 0 : this.reasonDescription.hashCode());
		result = prime * result + ((this.siteNumber == null) ? 0 : this.siteNumber.hashCode());
		result = prime * result + ((this.userSiteNumber == null) ? 0 : this.userSiteNumber.hashCode());
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
		ImageAccessLogEvent other = (ImageAccessLogEvent) obj;
		if (this.decodedImageIen == null)
		{
			if (other.decodedImageIen != null)
				return false;
		}
		else if (!this.decodedImageIen.equals(other.decodedImageIen))
			return false;
		if (this.dodImage != other.dodImage)
			return false;
		if (this.eventType == null)
		{
			if (other.eventType != null)
				return false;
		}
		else if (!this.eventType.equals(other.eventType))
			return false;
		//if (this.imageAccessEventTime != other.imageAccessEventTime)
		//	return false;
		if (this.imageIen == null)
		{
			if (other.imageIen != null)
				return false;
		}
		else if (!this.imageIen.equals(other.imageIen))
			return false;
		if (this.patientIcn == null)
		{
			if (other.patientIcn != null)
				return false;
		}
		else if (!this.patientIcn.equals(other.patientIcn))
			return false;
		if (this.reasonCode == null)
		{
			if (other.reasonCode != null)
				return false;
		}
		else if (!this.reasonCode.equals(other.reasonCode))
			return false;
		if (this.reasonDescription == null)
		{
			if (other.reasonDescription != null)
				return false;
		}
		else if (!this.reasonDescription.equals(other.reasonDescription))
			return false;
		if (this.siteNumber == null)
		{
			if (other.siteNumber != null)
				return false;
		}
		else if (!this.siteNumber.equals(other.siteNumber))
			return false;
		if (this.userSiteNumber == null)
		{
			if (other.userSiteNumber != null)
				return false;
		}
		else if (!this.userSiteNumber.equals(other.userSiteNumber))
			return false;
		return true;
	}
}
