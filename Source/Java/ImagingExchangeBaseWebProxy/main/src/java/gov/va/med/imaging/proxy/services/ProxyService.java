/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 28, 2009
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
package gov.va.med.imaging.proxy.services;

/**
 * Interface defines the services available to a proxy. The ProxyService provides the information the proxy will need
 * to execute the specified service operation. 
 * 
 * @author vhaiswwerfej
 *
 */
public interface ProxyService 
{
	
	public ProxyServiceType getProxyServiceType();
	
	public String getApplicationPath();
	
	public String getOperationPath();
	
	public String getProtocol();
	
	public String getHost();
	
	public int getPort();
	
	public String getUid();
	
	public Object getCredentials();
	
	public String getConnectionURL();
	
	public String toString();

}
