package gov.va.med.imaging.tomcat.vistarealm.exceptions;

public class ImageNotFoundException extends Exception {
	private final static long serialVersionUID = 1L;
	
	public ImageNotFoundException() {
		super();
	}
	
	public ImageNotFoundException(Exception ex) {
		super(ex);
	}
	
	public ImageNotFoundException(String msg) {
		super(msg);
	}

}
