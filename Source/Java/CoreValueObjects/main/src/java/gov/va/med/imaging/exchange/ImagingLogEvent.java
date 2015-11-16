/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 26, 2012
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

import gov.va.med.RoutingToken;
import gov.va.med.imaging.AbstractImagingURN;

/**
 * @author VHAISWWERFEJ
 *
 */
public class ImagingLogEvent
{
	private final RoutingToken routingTokenToLogTo;
	private final AbstractImagingURN imagingUrn;
	private final String patientIcn;
	private final String accessType;
	private final String userInterface;
	private final int imageCount;
	private final String additionalData;
	
	public ImagingLogEvent(RoutingToken routingTokenToLogTo,
			AbstractImagingURN imagingUrn, String patientIcn,
			String accessType, String userInterface, int imageCount,
			String additionalData)
	{
		super();
		this.routingTokenToLogTo = routingTokenToLogTo;
		this.imagingUrn = imagingUrn;
		this.patientIcn = patientIcn;
		this.accessType = accessType;
		this.userInterface = userInterface;
		this.imageCount = imageCount;
		this.additionalData = additionalData;
	}

	public AbstractImagingURN getImagingUrn()
	{
		return imagingUrn;
	}

	public String getPatientIcn()
	{
		return patientIcn;
	}

	public String getAccessType()
	{
		return accessType;
	}

	public String getUserInterface()
	{
		return userInterface;
	}

	public int getImageCount()
	{
		return imageCount;
	}

	public String getAdditionalData()
	{
		return additionalData;
	}

	public RoutingToken getRoutingTokenToLogTo()
	{
		return routingTokenToLogTo;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getAccessType() + " from " + getUserInterface() + " for patient '" + getPatientIcn() + "' and identifier '" + (getImagingUrn() == null ? "<null>" : getImagingUrn().toString()) + "'.");
		return sb.toString();
	}

}
