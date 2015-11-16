package gov.va.med.imaging.core.router;

import java.util.Date;

/**
 * The base abstract class for any command processors that may be retried asynchronously.
 * As long as the getRemainingRetries() returns a positive number a command that has
 * failed can request that it be put back on the queue for another attempt.
 * 
 * @author VHAISWBECKEC
 *
 * @param <R> - the result type of the asynchronous process
 */
public abstract class AbstractRetryableCommandImpl<R> 
extends AbstractCommandImpl<R>
{
	// 
	private static final long serialVersionUID = -7429202447266747607L;
	private final int maximumRetries;
	private final long retryDelay;
	private int remainingRetries;
	
	public AbstractRetryableCommandImpl(
        int maximumRetries,
        long retryDelay)
    {
	    super();
	    this.maximumRetries = maximumRetries;
	    this.remainingRetries = maximumRetries;
	    this.retryDelay = retryDelay;
    }

	public void updateAccessibilityDateForRetry()
	{
		this.accessibilityDate = new Date(System.currentTimeMillis() + getRetryDelay());
	}
	
	public int getRemainingRetries()
	{
		return this.remainingRetries;
	}
	
	public int getMaximumRetries()
    {
    	return maximumRetries;
    }

	/**
	 * Get the delay before the next execution attempt on a retry.
	 * @return
	 */
	public long getRetryDelay()
    {
    	return retryDelay;
    }

	@Override
    protected void commandProcessingComplete()
    {
		--this.remainingRetries;
    }
}
