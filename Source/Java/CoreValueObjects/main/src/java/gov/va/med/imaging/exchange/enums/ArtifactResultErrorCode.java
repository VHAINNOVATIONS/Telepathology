/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 29, 2010
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
 * Types of errors that might occur when requesting a collection of artifacts
 * 
 * @author vhaiswwerfej
 *
 */
public enum ArtifactResultErrorCode
{
	authorizationException("Unable to request data from this location because of a credentials problem", 4),
	invalidRequestException("The request to the location is invalid", 5),
	timeoutException("The request to the location timed out", 1),
	internalException("There was an internal exception communicating with the location", 2),
	unknownPatientId("The patient could not be found in the system", 3);
	
	final String description;
	final int priority;
	
	ArtifactResultErrorCode(String description, int priority)
	{
		this.description = description;
		this.priority = priority;
	}

	public String getDescription()
	{
		return description;
	}

	public int getPriority()
	{
		return priority;
	}
}
