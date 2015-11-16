/**
 * 
 */
package gov.va.med.imaging.exchange.business.dicom.exceptions;

/**
 * @author vhaiswpeterb
 *
 */
public class UnknownSOPClassException extends DicomException {

	/**
	 * 
	 */
	public UnknownSOPClassException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public UnknownSOPClassException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public UnknownSOPClassException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UnknownSOPClassException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
