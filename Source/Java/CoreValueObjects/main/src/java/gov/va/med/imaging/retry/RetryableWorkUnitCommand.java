/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: April 2, 2013 
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswlouthj 
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */
package gov.va.med.imaging.retry;

import gov.va.med.imaging.core.interfaces.exceptions.MethodException;

import org.apache.log4j.Logger;

/**
 * A generic class that accepts a WorkUnit<T> in its constructor, and then
 * executes the WorkUnit a configurable number of times, with a configurable
 * delay. The delay will also be escalated (by doubling) if the isEscalatingDelay
 * flag is set to true.
 * 
 * By default, the command will be retried 5 times (6 attempts total) with an initial 
 * delay of 1 second, and escalating delay will be set to true. The flow below shows 
 * the behavior if the work unit fails all attempts, using the default parameter values.
 * 
 * Initial Attempt
 *   catch exception, 1 second delay
 * Retry 1
 *   catch exception, 2 second delay
 * Retry 2
 *   catch exception, 4 second delay
 * Retry 3
 *   catch exception, 8 second delay
 * Retry 4
 *   catch exception, 16 second delay
 * Retry 5
 *   give up and throw the exception back up to the caller
 * 
 * @author vhaiswlouthj
 *
 * @param <T>
 */
public class RetryableWorkUnitCommand<T>
{
	private int initialRetryDelayInSeconds = 1;
	private int maximumNumberOfRetries = 5;
	private boolean isEscalatingDelay = true;
	private WorkUnit<T> workUnit;

	private Logger logger = Logger.getLogger(this.getClass());

	/**
	 * The only constructor. Requires a work unit to be provided.
	 * @param workUnit
	 */
	public RetryableWorkUnitCommand(WorkUnit<T> workUnit)
	{
		super();
		this.workUnit = workUnit;
	}

	/**
	 * Get the initial retry delay. Default value is 1
	 * 
	 * @return
	 */
	public int getInitialRetryDelayInSeconds()
	{
		return initialRetryDelayInSeconds;
	}

	/**
	 * Set the initial retry delay
	 * 
	 * @param initialRetryDelayInSeconds
	 */
	public void setInitialRetryDelayInSeconds(int initialRetryDelayInSeconds)
	{
		this.initialRetryDelayInSeconds = initialRetryDelayInSeconds;
	}

	/**
	 * Get the maximum number of retries. Default value is 5 retries.
	 * 
	 * @return
	 */
	public int getMaximumNumberOfRetries()
	{
		return maximumNumberOfRetries;
	}

	/**
	 * Set the maximum number of retries
	 * 
	 * @param maximumNumberOfRetries
	 */
	public void setMaximumNumberOfRetries(int maximumNumberOfRetries)
	{
		this.maximumNumberOfRetries = maximumNumberOfRetries;
	}

	/**
	 * Whether or not to escalate the delay after each failure. Default is true.
	 * @return
	 */
	public boolean isEscalatingDelay()
	{
		return isEscalatingDelay;
	}

	/**
	 * Set whether or not to escalate the delay after each failure
	 * @param isEscalatingDelay
	 */
	public void setEscalatingDelay(boolean isEscalatingDelay)
	{
		this.isEscalatingDelay = isEscalatingDelay;
	}

	/**
	 * This is the method to call on an initialized command instance to perform the work unit.
	 * It contains all the retry logic.
	 * @return
	 * @throws MethodException
	 */
	public T executeWorkWithRetries() throws MethodException
	{

		boolean success = false;
		int retryCount = 0;
		long delay = initialRetryDelayInSeconds * 1000;
		T result = null;

		logger.info("Starting operation");

		//
		// Loop until the operation completes successfully or the maximum number
		// of retries is exceeded.
		//
		// The catch block has a nested try catch to handle InterruptedException
		//
		// In case of ultimate failure, the exception thrown on the last retry is 
		// propagated up to the calling method.
		//
		while (retryCount <= maximumNumberOfRetries && !success)
		{
			try
			{
				result = workUnit.doWork();
				success = true;
			} 
			catch (Exception e)
			{
				try
				{
					logger.warn("Caught exception during retryable work", e);

					if (retryCount == maximumNumberOfRetries)
					{
						logger.error("Retry maximum reached, propagating error.");
						throw new MethodException( "Retry maximum reached, rethrowing the underlying exception.", e);
					}

					logger.debug("Sleeping for " + delay + " milliseconds before next retry.");

					// Sleep for the delay time
					Thread.sleep(delay);
				
					// If this is an escalating delay, double the delay for the next attempt
					if (isEscalatingDelay)
					{
						delay *= 2;
					}

				} 
				catch (InterruptedException ie)
				{
					// If we catch an interrupted exception, reinterrupt the thread since we're not handling it
					// here, and throw the original exception back up to the caller.
					Thread.currentThread().interrupt();
					String message = "InterruptedException while retrying. Propagating underlying exception: " + e.getMessage();
					logger.error(message, e);
					throw new MethodException(message, e);
				}
			}
			finally
			{
				retryCount++;
			}
		}

		return result;

	}
}
