/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 22, 2011
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
package gov.va.med.imaging.federation;

import gov.va.med.imaging.exchange.business.User;

/**
 * @author VHAISWWERFEJ
 *
 */
public class FederationUser 
implements User
{
	private final String userId;
	private final String name;
	private final String title;
	private final String service;

	public FederationUser(String userId, String name, String title,
			String service)
	{
		super();
		this.userId = userId;
		this.name = name;
		this.title = title;
		this.service = service;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.User#getUserId()
	 */
	@Override
	public String getUserId()
	{
		return userId;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.User#getName()
	 */
	@Override
	public String getName()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.User#getTitle()
	 */
	@Override
	public String getTitle()
	{
		return title;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.exchange.business.User#getService()
	 */
	@Override
	public String getService()
	{
		return service;
	}

}
