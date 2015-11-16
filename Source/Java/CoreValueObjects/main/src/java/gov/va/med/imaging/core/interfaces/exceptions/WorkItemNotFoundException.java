package gov.va.med.imaging.core.interfaces.exceptions;

/**
 * Exception thrown when a work item to be tranistioned is not in the expected starting
 * status.
 * 
 * @author vhaiswlouthj
 *
 */
public class WorkItemNotFoundException
extends MethodException
{
	private static final long serialVersionUID = 1L;

	public WorkItemNotFoundException()
    {
	    super();
    }

	public WorkItemNotFoundException(String message, Throwable cause)
    {
	    super(message, cause);
    }

	public WorkItemNotFoundException(String message)
    {
	    super(message);
    }

	public WorkItemNotFoundException(Throwable cause)
    {
	    super(cause);
    }
}
