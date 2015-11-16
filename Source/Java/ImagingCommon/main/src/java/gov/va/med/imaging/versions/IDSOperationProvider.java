/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 26, 2012
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
package gov.va.med.imaging.versions;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * Provider used by IDS to find additional operations for a service that is not defined in the ImagingServices.xml
 * 
 * @author vhaiswwerfej
 *
 */
public abstract class IDSOperationProvider
{
	private final static Logger logger = Logger.getLogger(IDSOperationProvider.class);
	
	public IDSOperationProvider()
	{
		super();
	}
	
	protected Logger getLogger()
	{
		return logger;
	}
	
	/**
	 * Returns the name of the application type to add operations to
	 * @return
	 */
	public abstract String getApplicationType();
	
	/**
	 * Returns the version of the service to add operations to
	 * @return
	 */
	public abstract String getVersion();
	
	/**
	 * Returns the operations to add to the service
	 * @return
	 */
	public abstract List<ImagingOperation> getImagingOperations();
	

}
