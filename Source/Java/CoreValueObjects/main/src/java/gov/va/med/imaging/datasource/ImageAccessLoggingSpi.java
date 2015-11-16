/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 13, 2008
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
package gov.va.med.imaging.datasource;

import java.util.List;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.datasource.annotations.SPI;
import gov.va.med.imaging.exchange.ImageAccessLogEvent;
import gov.va.med.imaging.exchange.ImagingLogEvent;
import gov.va.med.imaging.exchange.business.ImageAccessReason;
import gov.va.med.imaging.exchange.enums.ImageAccessReasonType;

/**
 * @author VHAISWWERFEJ
 *
 */
@SPI(description="This SPI defines operations to log image access")
public interface ImageAccessLoggingSpi 
extends VersionableDataSourceSpi 
{

	public abstract void LogImageAccessEvent(ImageAccessLogEvent logEvent)
	throws MethodException, ConnectionException;
	
	/**
	 * A more generic method of logging Imaging data to the datasource
	 * @param logEvent
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract void LogImagingLogEvent(ImagingLogEvent logEvent)
	throws MethodException, ConnectionException;
	
	/**
	 * Get the list of reasons a user has decided to copy/print an image (and other options).
	 * 
	 * @param globalRoutingToken
	 * @param reasonTypes
	 * @return
	 * @throws MethodException
	 * @throws ConnectionException
	 */
	public abstract List<ImageAccessReason> getImageAccessReasons(RoutingToken globalRoutingToken, 
			List<ImageAccessReasonType> reasonTypes)
	throws MethodException, ConnectionException;
}
