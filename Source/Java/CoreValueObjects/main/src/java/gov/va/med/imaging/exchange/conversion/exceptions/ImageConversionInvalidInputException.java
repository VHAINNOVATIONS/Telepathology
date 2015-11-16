/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: August 10, 2006
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

package gov.va.med.imaging.exchange.conversion.exceptions;

 /**
  *
  * @author Csaba Titton
  *
  */
public class ImageConversionInvalidInputException extends Exception {
	
	static final long serialVersionUID = 1L;
	
   /**
    * 
    */
   public ImageConversionInvalidInputException() {
       super();
       // TODO Auto-generated constructor stub
   }

   /**
    * @param message
    */
   public ImageConversionInvalidInputException(String message) {
       super(message);
       // TODO Auto-generated constructor stub
   }

   /**
    * @param cause
    */
   public ImageConversionInvalidInputException(Throwable cause) {
       super(cause);
       // TODO Auto-generated constructor stub
   }

   /**
    * @param message
    * @param cause
    */
   public ImageConversionInvalidInputException(String message, Throwable cause) {
       super(message, cause);
       // TODO Auto-generated constructor stub
   }

}
