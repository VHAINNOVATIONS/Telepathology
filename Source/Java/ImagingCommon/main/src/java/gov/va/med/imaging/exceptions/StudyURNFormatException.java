package gov.va.med.imaging.exceptions;

public class StudyURNFormatException 
extends URNFormatException
{
	private static final long serialVersionUID = 6271193731031546478L;

	public StudyURNFormatException()
	{
		super();
	}

	public StudyURNFormatException(String message)
	{
		super(message);
	}

	public StudyURNFormatException(Throwable cause)
	{
		super(cause);
	}

	public StudyURNFormatException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
