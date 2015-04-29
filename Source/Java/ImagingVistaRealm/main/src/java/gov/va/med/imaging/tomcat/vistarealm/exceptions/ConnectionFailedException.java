package gov.va.med.imaging.tomcat.vistarealm.exceptions;


/**
 * Exception occurs when not able to connect to a remote system
 * 
 * @author VHAISWWERFEJ
 *
 */
public class ConnectionFailedException extends Exception {
	private final static long serialVersionUID = 1L;
	
	public ConnectionFailedException() {
		super();
	}
	
	public ConnectionFailedException(String message) {
		super(message);
	}
	
	public ConnectionFailedException(Exception ex) {
		super(ex);
	}

}
