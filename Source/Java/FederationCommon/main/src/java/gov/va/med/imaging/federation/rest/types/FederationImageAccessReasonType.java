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
public class FederationImageAccessReasonType
{
	private String routingTokenString;
	private int reasonCode;
	private String description;
	private FederationImageAccessReasonTypeType [] reasonTypes;
	private String globalReasonCode;
	
	public FederationImageAccessReasonType()
	{
		super();
	}

	/**
	 * @param routingToken
	 * @param reasonCode
	 * @param description
	 * @param reasonTypes
	 * @param globalReasonCode
	 */
	public FederationImageAccessReasonType(String routingTokenString, int reasonCode,
			String description,
			FederationImageAccessReasonTypeType []reasonTypes,
			String globalReasonCode)
	{
		super();
		this.routingTokenString = routingTokenString;
		this.reasonCode = reasonCode;
		this.description = description;
		this.reasonTypes = reasonTypes;
		this.globalReasonCode = globalReasonCode;
	}

	/**
	 * @return the routingTokenString
	 */
	public String getRoutingTokenString()
	{
		return routingTokenString;
	}

	/**
	 * @param routingTokenString the routingTokenString to set
	 */
	public void setRoutingTokenString(String routingTokenString)
	{
		this.routingTokenString = routingTokenString;
	}

	/**
	 * @return the reasonCode
	 */
	public int getReasonCode()
	{
		return reasonCode;
	}

	/**
	 * @param reasonCode the reasonCode to set
	 */
	public void setReasonCode(int reasonCode)
	{
		this.reasonCode = reasonCode;
	}

	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * @return the reasonTypes
	 */
	public FederationImageAccessReasonTypeType [] getReasonTypes()
	{
		return reasonTypes;
	}

	/**
	 * @param reasonTypes the reasonTypes to set
	 */
	public void setReasonTypes(FederationImageAccessReasonTypeType []reasonTypes)
	{
		this.reasonTypes = reasonTypes;
	}

	/**
	 * @return the globalReasonCode
	 */
	public String getGlobalReasonCode()
	{
		return globalReasonCode;
	}

	/**
	 * @param globalReasonCode the globalReasonCode to set
	 */
	public void setGlobalReasonCode(String globalReasonCode)
	{
		this.globalReasonCode = globalReasonCode;
	}

}
