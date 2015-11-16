/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 10, 2012
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
package gov.va.med.imaging.federation.rest.types;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author VHAISWWERFEJ
 *
 */
@XmlRootElement
public class FederationImagingLogEventType
{
	private String routingTokenToLogToString;
	private String imagingUrnString;
	private String patientIcn;
	private String accessType;
	private String userInterface;
	private int imageCount;
	private String additionalData;
	
	public FederationImagingLogEventType()
	{
		super();
	}
	
	/**
	 * @param routingTokenToLogToString
	 * @param imagingUrnString
	 * @param patientIcn
	 * @param accessType
	 * @param userInterface
	 * @param imageCount
	 * @param additionalData
	 */
	public FederationImagingLogEventType(String routingTokenToLogToString,
			String imagingUrnString, String patientIcn, String accessType,
			String userInterface, int imageCount, String additionalData)
	{
		super();
		this.routingTokenToLogToString = routingTokenToLogToString;
		this.imagingUrnString = imagingUrnString;
		this.patientIcn = patientIcn;
		this.accessType = accessType;
		this.userInterface = userInterface;
		this.imageCount = imageCount;
		this.additionalData = additionalData;
	}

	/**
	 * @return the routingTokenToLogToString
	 */
	public String getRoutingTokenToLogToString()
	{
		return routingTokenToLogToString;
	}

	/**
	 * @param routingTokenToLogToString the routingTokenToLogToString to set
	 */
	public void setRoutingTokenToLogToString(String routingTokenToLogToString)
	{
		this.routingTokenToLogToString = routingTokenToLogToString;
	}

	/**
	 * @return the imagingUrnString
	 */
	public String getImagingUrnString()
	{
		return imagingUrnString;
	}

	/**
	 * @param imagingUrnString the imagingUrnString to set
	 */
	public void setImagingUrnString(String imagingUrnString)
	{
		this.imagingUrnString = imagingUrnString;
	}

	/**
	 * @return the patientIcn
	 */
	public String getPatientIcn()
	{
		return patientIcn;
	}

	/**
	 * @param patientIcn the patientIcn to set
	 */
	public void setPatientIcn(String patientIcn)
	{
		this.patientIcn = patientIcn;
	}

	/**
	 * @return the accessType
	 */
	public String getAccessType()
	{
		return accessType;
	}

	/**
	 * @param accessType the accessType to set
	 */
	public void setAccessType(String accessType)
	{
		this.accessType = accessType;
	}

	/**
	 * @return the userInterface
	 */
	public String getUserInterface()
	{
		return userInterface;
	}

	/**
	 * @param userInterface the userInterface to set
	 */
	public void setUserInterface(String userInterface)
	{
		this.userInterface = userInterface;
	}

	/**
	 * @return the imageCount
	 */
	public int getImageCount()
	{
		return imageCount;
	}

	/**
	 * @param imageCount the imageCount to set
	 */
	public void setImageCount(int imageCount)
	{
		this.imageCount = imageCount;
	}

	/**
	 * @return the additionalData
	 */
	public String getAdditionalData()
	{
		return additionalData;
	}

	/**
	 * @param additionalData the additionalData to set
	 */
	public void setAdditionalData(String additionalData)
	{
		this.additionalData = additionalData;
	}

}
