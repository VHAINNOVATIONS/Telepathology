package gov.va.med.imaging.tomcat.vistarealm.exceptions;

public class CommunicationException extends Exception {
	private final static long serialVersionUID = 1L;
	
	public CommunicationException() {
		super();
	}
	
	public CommunicationException(String ex) {
		super(ex);
	}
	
	public CommunicationException(Exception ex) {
		super(ex);
	}

}
