package gov.va.med.imaging.tomcat.vistarealm.exceptions;

public class InvalidCredentialsException extends Exception {
	private final static long serialVersionUID = 1L;
	
	public InvalidCredentialsException() {
		super();
	}
	
	public InvalidCredentialsException(String msg) {
		super(msg);
	}
	
	public InvalidCredentialsException(Exception ex) {
		super(ex);
	}

}
