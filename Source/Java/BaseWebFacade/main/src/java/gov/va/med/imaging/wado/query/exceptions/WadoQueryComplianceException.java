/*
 * Created on Jun 23, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package gov.va.med.imaging.wado.query.exceptions;

/**
 * @author Chris Beckey
 *
 * A generic exception that indicates that the WADO query is not in compliance
 * with the specification.  More specific exceptions are thrown where the type
 * of error can be determined.
 * 
 * Note that this exception, and its derived classes, will not be thrown if
 * the strictCompliance property of WADOQuery is false.
 */
public class WadoQueryComplianceException 
extends Exception 
{

	/**
	 * 
	 */
	public WadoQueryComplianceException() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public WadoQueryComplianceException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public WadoQueryComplianceException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public WadoQueryComplianceException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
