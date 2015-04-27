package gov.va.med.imaging.tomcat.vistarealm.exceptions;

/**
 * Exception occurs when unable to connect to a server.  When the connection to the server cannot be established  
 * 
 * @author VHAISWWERFEJ
 *
 */
public class UnableToConnectToBrokerException extends ConnectionFailedException {
	private final static long serialVersionUID = 1L;
	
	public UnableToConnectToBrokerException() {
		super();
	}
	
	public UnableToConnectToBrokerException(String message) {
		super(message);
	}
	
	public UnableToConnectToBrokerException(Exception ex) {
		super(ex);
	}

}
