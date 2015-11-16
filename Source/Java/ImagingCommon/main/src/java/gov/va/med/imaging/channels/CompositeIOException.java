package gov.va.med.imaging.channels;

import java.io.IOException;

/**
 * A simple array of IOException, that subclasses IOException. 
 * 
 * 
 * @author VHAISWBECKEC
 *
 */
public class CompositeIOException
extends IOException
{
	private static final long serialVersionUID = -3937767163151508082L;
	private IOException[] compositeIOExceptions;
	private IOException causeIoException = null;
	private final long bytesWritten;
	
	public static CompositeIOException create(IOException[] ioExceptions)
	{
		return create(ioExceptions, Long.MIN_VALUE);
	}
	
	/**
	 * Create a CompositeIOException from an array of IOException.
	 * If the array is not fully populated (i.e. some nulls) then only
	 * copy the non-null IOExceptions.
	 * 
	 * Returns null if no IOException array members are non-null.
	 * 
	 * @param ioExceptions
	 * @return
	 */
	public static CompositeIOException create(IOException[] ioExceptions, long bytesWritten)
	{
		int nonNullArrayMembers = 0;
		for(int i=0; i<ioExceptions.length; ++i)
			if(ioExceptions[i] != null)
				++nonNullArrayMembers;

		if(nonNullArrayMembers == 0)
			return null;
		
		IOException[] compositeIOExceptions = new IOException[nonNullArrayMembers];
		IOException sourceIoException = null;
		
		int destArrayIndex = 0;
		for(int i=0; i<ioExceptions.length; ++i)
			if(ioExceptions[i] != null)
			{
				if( sourceIoException == null )
					sourceIoException = ioExceptions[i];
				compositeIOExceptions[destArrayIndex ++] = ioExceptions[i];
			}
		CompositeIOException cioX = new CompositeIOException(bytesWritten);
		cioX.setCompositeIOExceptions(compositeIOExceptions);
		cioX.setCauseIoException(sourceIoException);
		
		return cioX;
	}

	public CompositeIOException()
	{
		super();
		this.bytesWritten = Long.MIN_VALUE;
	}
	
	public CompositeIOException(long bytesWritten)
	{
		super();
		this.bytesWritten = bytesWritten;
	}
	
	public CompositeIOException(String message)
	{
		super(message);
		this.bytesWritten = Long.MIN_VALUE;
	}

	public CompositeIOException(String message, long bytesWritten)
	{
		super(message);
		this.bytesWritten = bytesWritten;
	}

	public IOException getCauseIoException()
	{
		return this.causeIoException;
	}

	public void setCauseIoException(IOException causeIoException)
	{
		this.causeIoException = causeIoException;
	}

	public IOException[] getCompositeIOExceptions()
	{
		return this.compositeIOExceptions;
	}

	public void setCompositeIOExceptions(IOException[] compositeIoExceptions)
	{
		this.compositeIOExceptions = compositeIoExceptions;
	}

	public boolean isBytesWrittenKnown()
	{
		return this.bytesWritten != Long.MIN_VALUE;
	}
	
	public long getBytesWritten()
	{
		return this.bytesWritten;
	}

	@Override
	public String getMessage()
	{
		StringBuilder sb = new StringBuilder();
		for(IOException ioException : compositeIOExceptions)
			if(ioException != null)
				sb.append(ioException.getClass().getName() + "-" + ioException.getMessage());
		
		return sb.toString();
	}

	@Override
	public Throwable getCause()
	{
		return getCauseIoException();
	}
	
	
}