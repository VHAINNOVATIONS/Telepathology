/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 7, 2012
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
package gov.va.med.imaging.exchange.business;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.exchange.enums.ImageAccessReasonType;

import java.util.List;

/**
 * Description of a reason why a user might print/copy an image.
 * 
 * @author VHAISWWERFEJ
 *
 */
public class ImageAccessReason
{
	private final RoutingToken routingToken;
	private final int reasonCode;
	private final String description;
	private final List<ImageAccessReasonType> reasonTypes;
	private final String globalReasonCode;
	/**
	 * @param reasonCode
	 * @param description
	 * @param reasonTypes
	 * @param globalReasonCode
	 */
	public ImageAccessReason(RoutingToken routingToken,
			int reasonCode, String description,
			List<ImageAccessReasonType> reasonTypes, String globalReasonCode)
	{
		super();
		this.routingToken = routingToken;
		this.reasonCode = reasonCode;
		this.description = description;
		this.reasonTypes = reasonTypes;
		this.globalReasonCode = globalReasonCode;
	}
	/**
	 * This is the reason code (IEN in #2005.88), it really should be a URN but everything else is just expecting the ID.  
	 * This is not a global ID and only good at the site where it came from 
	 * 
	 * @return the reasonCode
	 */
	public int getReasonCode()
	{
		return reasonCode;
	}
	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}
	/**
	 * @return the reasonTypes
	 */
	public List<ImageAccessReasonType> getReasonTypes()
	{
		return reasonTypes;
	}
	/**
	 * Global reason code for reasons that are the same at all sites
	 * 
	 * @return the globalReasonCode
	 */
	public String getGlobalReasonCode()
	{
		return globalReasonCode;
	}
	
	/**
	 * The site where the reason came from
	 * 
	 * @return the routingToken
	 */
	public RoutingToken getRoutingToken()
	{
		return routingToken;
	}

}
