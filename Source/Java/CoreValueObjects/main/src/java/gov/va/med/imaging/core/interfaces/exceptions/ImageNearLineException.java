/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: September 4, 2006
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
 */package gov.va.med.imaging.core.interfaces.exceptions;

import gov.va.med.imaging.ImageURN;

/**
 * An MethodRemoteException that is thrown when data is available in off-line media only. 
 * 
 * @author Csaba Titton
 * 
 */
public class ImageNearLineException 
extends MethodRemoteException 
implements TransientCondition
{
	static final long serialVersionUID = 1L;
	
   /**
    * Create an instance of an ImageNearLineException.
    * The ImageURN must be provided.
    */
   public ImageNearLineException(ImageURN imageUrn) 
   {
	   this( imageUrn.toString() );
   }

   public ImageNearLineException(String imageUrnAsString) 
   {
	   super(
	       "The requested image '" + imageUrnAsString + 
	       "' is not available in on-line or near-line storage and must be loaded from off-line storage." + 
	       "The request for the image may be re-issued at any time, the result will be the same until the media is loaded."
	   );
   }
   
   /**
    * Return -1 because we really don't know when the platter will be loaded.
    * 
    * @see gov.va.med.imaging.core.interfaces.exceptions.TransientCondition#getSuggestedDelay()
    */
	@Override
	public long getSuggestedDelay()
	{
		return -1;
	}
}
