/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 1, 2010
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
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
package gov.va.med.imaging.exchange.enums;

/**
 * Statuses of the business object, determines viewability and usability
 * 
 * @author vhaiswwerfej
 *
 */
public enum ObjectStatus
{
	NO_STATUS(0, "Viewable (no status)"),
	VIEWABLE(1, "Viewable"),
	QA_REVIEWED(2, "QA Reviewed"),
	IMAGE_GROUP(4, "Image Group"),
	CONTROLLED(7, "Controlled"),
	NEEDS_REFRESH(8, "Needs Refresh"), // might never come from the database?
	IN_PROGRESS(10, "In Progress"),
	NEEDS_REVIEW(11, "Needs Review"),
	DELETED(12, "Deleted"),
	QUESTIONABLE_INTEGRITY(21, "Questionable Integrity"),
	TIU_AUTHORIZATION_BLOCK(22, "TIU Authorization Block"),
	RAD_EXAM_STATUS_BLOCK(23, "RAD Exam Status Block"),
	UNKNOWN(-1, "Unknown"); // does not come from the database
		
	final int value;
	final String description;

	ObjectStatus(int value, String description)
	{
		this.value = value;
		this.description = description;
	}

	public int getValue()
	{
		return value;
	}

	public String getDescription()
	{
		return description;
	}
	
	public static ObjectStatus valueOf(int statusValue)
	{
		for(ObjectStatus studyStatus : ObjectStatus.values())
		{
			if(studyStatus.getValue() == statusValue)
				return studyStatus;
		}
		return UNKNOWN;
	}
}
