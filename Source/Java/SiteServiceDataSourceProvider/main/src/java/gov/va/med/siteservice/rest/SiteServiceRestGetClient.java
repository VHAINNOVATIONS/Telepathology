/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 28, 2012
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
package gov.va.med.siteservice.rest;

import javax.ws.rs.core.MediaType;

import gov.va.med.imaging.proxy.rest.AbstractRestGetClient;

/**
 * @author VHAISWWERFEJ
 *
 */
public class SiteServiceRestGetClient
extends AbstractRestGetClient
{

	/**
	 * @param url
	 * @param mediaType
	 */
	public SiteServiceRestGetClient(String url, MediaType mediaType)
	{
		super(url, mediaType, 30000);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.proxy.rest.AbstractRestClient#addTransactionHeaders()
	 */
	@Override
	protected void addTransactionHeaders()
	{
		
	}


}
