package gov.va.med.imaging.core.interfaces.exceptions;

/**
 * Exception thrown when a work item to be tranistioned is not in the expected starting
 * status.
 * 
 * @author vhaiswlouthj
 *
 */
public class InvalidWorkItemStatusException
extends MethodException
{
	private static final long serialVersionUID = 1L;

	public InvalidWorkItemStatusException()
    {
	    super();
    }

	public InvalidWorkItemStatusException(String message, Throwable cause)
    {
	    super(message, cause);
    }

	public InvalidWorkItemStatusException(String message)
    {
	    super(message);
    }

	public InvalidWorkItemStatusException(Throwable cause)
    {
	    super(cause);
    }
}
