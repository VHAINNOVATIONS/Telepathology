package gov.va.med.imaging.tomcat.vistarealm.exceptions;

public class MethodException extends Exception {
	private final static long serialVersionUID = 1L;
	
	public MethodException() {
		super();
	}
	
	public MethodException(String msg) {
		super(msg);
	}
	
	public MethodException(Exception ex) {
		super(ex);
	}

}
