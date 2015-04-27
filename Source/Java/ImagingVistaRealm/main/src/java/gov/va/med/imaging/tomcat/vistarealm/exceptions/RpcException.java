package gov.va.med.imaging.tomcat.vistarealm.exceptions;

public class RpcException extends MethodException {
	private final static long serialVersionUID = 1L;
	
	public RpcException() {
		super();
	}
	
	public RpcException(String message) {
		super(message);
	}

	public RpcException(Exception ex) {
		super(ex);
	}
}
