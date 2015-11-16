/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Aug 21, 2008
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
package gov.va.med.siteservice;

/**
 * Exception occurs when the region map is not loaded properly
 * 
 * @author VHAISWWERFEJ
 *
 */
public class RegionMapLoadException 
extends Exception
{
	private static final long serialVersionUID = 1L;
	private final boolean remoteLoad;
	
	public RegionMapLoadException(String message, boolean remoteLoad)
	{
		super(message);
		this.remoteLoad = remoteLoad;
	}

	public RegionMapLoadException(Throwable cause, boolean remoteLoad)
	{
		super(cause);
		this.remoteLoad = remoteLoad;
	}

	/**
	 * 
	 * @return true if the exception occurred when doing a load from a remote source,
	 * else exception occurred when loading from a local source.
	 */
	public boolean isRemoteLoad()
    {
    	return remoteLoad;
    }
}
