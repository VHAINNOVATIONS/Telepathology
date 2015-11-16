/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: October 10, 2006
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWTITTOC
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

package gov.va.med.imaging.core.interfaces;

import java.io.IOException;
import java.io.OutputStream;

import gov.va.med.imaging.GUID;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.InstanceChecksumNotification;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNearLineException;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.exchange.enums.ImageFormat;

// import java.nio.channels.ReadableByteChannel;

/**
 * The interface implemented by a core router.
 */
public interface ImageAccessorFacade
{
	public abstract int getInstanceByImageURN(
			ImageURN imageUrn, 
			String subType, 
			ImageQuality imageQuality,  
			ImageFormat conversionTargetFormat, 
			OutputStream outStream,
			InstanceChecksumNotification checksumCallback)
	throws IOException, ImageNearLineException;

	public abstract int getInstanceByGuid(
			GUID instanceGuid, 
			String subType, 
			ImageQuality imageQuality,  
			ImageFormat conversionTargetFormat, 
			OutputStream outStream,
			InstanceChecksumNotification checksumCallback)
	throws IOException, ImageNearLineException;
}