package gov.va.med.imaging.tomcat.vistarealm.exceptions;

public class UnableToCreateContextException extends Exception {
	private final static long serialVersionUID = 1L;
	
	public UnableToCreateContextException() {
		super();
	}
	
	public UnableToCreateContextException(String message) {
		super(message);
	}
	
	public UnableToCreateContextException(Exception ex) {
		super(ex);
	}

}
