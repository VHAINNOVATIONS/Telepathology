/**
 * 
 */
package gov.va.med.imaging.exchange.business.dicom.exceptions;

/**
 * @author vhaiswpeterb
 *
 */
public class VRViolationException extends Exception {

	/**
	 * 
	 */
	public VRViolationException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public VRViolationException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public VRViolationException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public VRViolationException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
