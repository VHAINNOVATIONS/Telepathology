/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 17, 2010
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
package gov.va.med.imaging.proxy.rest;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;

import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.ClientResponse;

/**
 * Rest client for HTTP POST commands. This supports posting input parameters
 * in XML format
 * 
 * @author vhaiswwerfej
 *
 */
public abstract class AbstractRestPostClient 
extends AbstractRestClient 
{

	public AbstractRestPostClient(String url, MediaType mediaType, int metadataTimeoutMs)
	{
		super(url, mediaType, metadataTimeoutMs);
	}
	
	public AbstractRestPostClient(String url, String mediaType, int metadataTimeoutMs)
	{
		super(url, mediaType, metadataTimeoutMs);
	}
	
	public <T extends Object> T executeRequest(Class<T> c, Object ... postParameter)
	throws MethodException, ConnectionException
	{
		if(postParameter != null)
		{
			for(Object pp : postParameter)
			{
				getRequest().entity(pp, MediaType.APPLICATION_XML_TYPE);
			}
		}
		return super.executeRequest(c);
	}

	@Override
	protected <T> ClientResponse executeMethodInternal(Class<T> c) 
	{
		return getRequest().post(ClientResponse.class);
	}

}
