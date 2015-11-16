/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Sep 24, 2008
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWBECKEC
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */
package gov.va.med.imaging.core.router;

import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.WellKnownOID;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.GUID;
import gov.va.med.imaging.core.CommandContextImpl;
import gov.va.med.imaging.core.RouterImpl;
import gov.va.med.imaging.core.interfaces.exceptions.CompositeExceptionComponent;
import gov.va.med.imaging.core.interfaces.exceptions.CompositeMethodException;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.router.AsynchronousCommandResult;
import gov.va.med.imaging.core.interfaces.router.AsynchronousCommandResultListener;
import gov.va.med.imaging.core.interfaces.router.Command;
import gov.va.med.imaging.core.interfaces.router.CommandContext;
import gov.va.med.imaging.core.router.queue.ScheduledPriorityQueueElement;
import gov.va.med.imaging.datasource.DataSourceProvider;
import gov.va.med.imaging.exchange.business.TransactionContextLogEntrySnapshot;
import gov.va.med.imaging.transactioncontext.InvalidTransactionContextMementoException;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.transactioncontext.TransactionContextMemento;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import org.apache.log4j.Logger;

/**
 * An abstract thread pool task to complete. Executes the assigned task.
 * @author VHAISWWERFEJ
 * 
 * The abstract class that contains the strategy for processing asynchronous
 * commands.
 * 
 * @author VHAISWBECKEC
 *
 */
public abstract class AbstractCommandImpl<R>
implements Command<R>, Callable<AsynchronousCommandResult<R>>, ScheduledPriorityQueueElement, Serializable
{
	// The serialVersionUID is required and must be maintained 
	private static final long serialVersionUID = -5069306441372156085L;
	
	private transient Logger logger = null;
	private final Set<AsynchronousCommandResultListener<R>> listeners;
	private TransactionContextMemento transactionContextMemento;
	private final String parentCommandClassName;
	private transient CommandContext commandContext;
	private final GUID commandIdentifier = new GUID();	// a unique identifier of every command
	private GUID parentCommandIdentifier = null;		// by default, no parent, this is a root command
	private boolean childCommand = false; // by default, not a child command
	
	// these properties are all associated with asynchronous processing
	protected Date accessibilityDate;
	private Priority priority;
	private Date processingTargetCommencementDate;
	private long processingDurationEstimate;
	
	// these properties are all associated with periodic processing
	private boolean isPeriodic = false;
	private int periodicExecutionDelay;
	private boolean periodicProcessingTerminated = false;

	protected String getLocalSiteId() 
	{
		return getCommandContext().getLocalSite().getArtifactSource().getRepositoryId();
	}

	public boolean isPeriodic()
	{
		return isPeriodic;
	}
	
	public void setPeriodic(boolean isPeriodic)
	{
		this.isPeriodic = isPeriodic;
	}
	
	public int getPeriodicExecutionDelay()
	{
		return periodicExecutionDelay;
	}
	
	public void setPeriodicExecutionDelay(int periodicExecutionDelay)
	{
		this.periodicExecutionDelay = periodicExecutionDelay;
	}
	
	public Command<R> getNewPeriodicInstance()
	throws MethodException
	{
		throw new MethodException("getNewPeriodicInstance is undefined for this command. It must be implemented if the command will be used in an asynchronous periodic fashion.");
	}
	
	/**
	 * Return the list of fatal periodic command exceptions. If a periodic command throws an exception and that exception is in this list of fatal exceptions, the periodic 
	 * command will not be rescheduled
	 * @return List of classes that extend Throwable that are fatal exceptions for this periodic command
	 */
	public List<Class<? extends MethodException>> getFatalPeriodicExceptionClasses()
	{
		return null;
	}
	
	/**
	 * This method is called when a periodic command has thrown a fatal exception as defined by the list in getFatalPeriodicExceptionClasses(). At the point when this method is called
	 * the periodic command has already stopped executing and will not execute again.  This method is meant to allow the command to alert someone of the failure (such as by sending 
	 * an email message)
	 * @param t
	 */
	public void handleFatalPeriodicException(Throwable t)
	{
		// implement in other commands
	}

	
	public GUID getParentCommandIdentifier()
	{
		return this.parentCommandIdentifier;
	}

	public void setParentCommandIdentifier(GUID parentCommandIdentifier)
	{
		this.parentCommandIdentifier = parentCommandIdentifier;
	}

	public GUID getCommandIdentifier()
	{
		return this.commandIdentifier;
	}

	public boolean isChildCommand()
	{
		return childCommand;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.Command#setChildCommand(boolean)
	 */
	@Override
	public void setChildCommand(boolean childCommand) 
	{
		this.childCommand = childCommand;
	}

	/**
	 * 
	 */
	protected AbstractCommandImpl() 
	{
		// get the current transaction context and copy it to our local reference
		// set the command name in the context to our command name, while preserving
		// all other context properties
		gov.va.med.imaging.transactioncontext.TransactionContext transactionContext =
			  gov.va.med.imaging.transactioncontext.TransactionContextFactory.get();
		parentCommandClassName = transactionContext.getCommandClassName();		// may be null if no parent command
		
		// Always save a copy of the transaction context that this instance was created
		// under.  At this point we do not know whether we will execute synchronously or
		// asynchronously, or as a child command.
		this.transactionContextMemento = transactionContext.getMemento();
		
		this.listeners = new HashSet<AsynchronousCommandResultListener<R>>();

		// set the asynchronous processing properties to 
		// make the command available for execution immediately at normal priority.
		this.accessibilityDate = new Date();	// by default, available for immediate processing
		this.priority = ScheduledPriorityQueueElement.Priority.NORMAL;	// normal priority	
		this.processingTargetCommencementDate = new Date();	// by default, get it done ASAP
		this.processingDurationEstimate = -1L;	// by default, do not provide an estimated duration, forces priority only sort
	}
	
	/**
	 * @return the provider
	 */
	protected DataSourceProvider getProvider()
	{
		return this.getCommandContext().getProvider();
	}

	/**
	 * Get the Logger to use for info, debug, errors, etc ...
	 * This is NOT the transaction logger.
	 * 
	 * @return
	 */
	protected synchronized Logger getLogger()
    {
		if(logger == null)
			logger = Logger.getLogger(AbstractCommandImpl.class);
    	return logger;
    }

	/**
	 * The command context contains references to the environment in which the Command
	 * is running.
	 * @return the commandContext
	 */
	protected CommandContext getCommandContext()
	{
		return this.commandContext;
	}
	
	/**
	 * @param commandContext the commandContext to set
	 */
	public void setCommandContext(CommandContext commandContext)
	{
		this.commandContext = commandContext;
	}

	@Deprecated
	protected RouterImpl getRouterImpl()
	{
		return ((CommandContextImpl)getCommandContext()).getRouterImpl();
	}

	/**
	 * Based on the given routing token, return a RoutingToken of a repository
	 * that will provide a treating facility list.
	 * If the given routing token is not a VA destination then this will return null.
	 * If the given routing token is a VA destination then this returns the local site.
	 * 
	 * @param routingToken
	 * @return
	 * @throws MethodException
	 */
	protected RoutingToken getTreatingFacilityRepositoryRoutingToken(String homeCommunityId)
	throws MethodException
	{
		WellKnownOID oid = WellKnownOID.get(homeCommunityId);
		RoutingToken localRoutingToken = null;
		
		TransactionContext xactionContext = TransactionContextFactory.get();
		String realmSiteNumber = xactionContext.getRealm();
		
		try
		{
			switch(oid)
			{
			case VA_DOCUMENT:
				//String localSiteNumber = getCommandContext().getRouter().getAppConfiguration().getLocalSiteNumber();
				localRoutingToken = RoutingTokenImpl.createVADocumentSite(realmSiteNumber);
				break;
			case VA_RADIOLOGY_IMAGE:
				//String localSiteNumber = getCommandContext().getRouter().getAppConfiguration().getLocalSiteNumber();
				localRoutingToken = RoutingTokenImpl.createVARadiologySite(realmSiteNumber);
				break;
			default:
				break;
			}
		}
		catch (RoutingTokenFormatException x)
		{
			getLogger().error(x);
			throw new MethodException(x);
		}
		
		return localRoutingToken;
	}	
	
	/**
	 * Get a routing token that will route to the local VA site.
	 * 
	 * @return
	 * @throws MethodException
	 */
	protected RoutingToken getLocalRealmRadiologyRoutingToken()
	throws MethodException
	{
		try
		{
			TransactionContext xactionContext = TransactionContextFactory.get();
			String realmSiteNumber = xactionContext.getRealm();
			
			//String localSiteNumber = getCommandContext().getRouter().getAppConfiguration().getLocalSiteNumber();
			RoutingToken localRoutingToken = RoutingTokenImpl.createVARadiologySite(realmSiteNumber);
			
			return localRoutingToken;
		}
		catch (RoutingTokenFormatException x)
		{
			getLogger().error(x);
			throw new MethodException(x);
		}
	}
	
	/**
	 * Get a routing token that will route to the local VA site.
	 * 
	 * @return
	 * @throws MethodException
	 */
	protected RoutingToken getLocalRealmDocumentRoutingToken()
	throws MethodException
	{
		try
		{
			TransactionContext xactionContext = TransactionContextFactory.get();
			String realmSiteNumber = xactionContext.getRealm();
			
			//String localSiteNumber = getCommandContext().getRouter().getAppConfiguration().getLocalSiteNumber();
			RoutingToken localRoutingToken = RoutingTokenImpl.createVADocumentSite(realmSiteNumber);
			
			return localRoutingToken;
		}
		catch (RoutingTokenFormatException x)
		{
			getLogger().error(x);
			throw new MethodException(x);
		}
	}
	
	/**
	 * By default, route requests to ourselves as radiology artifacts.
	 */
	@Override
	public RoutingToken getRoutingToken()
	throws MethodException
	{
		return getLocalRealmRadiologyRoutingToken();
	}

	/**
     * @return the listeners
     */
    public Set<AsynchronousCommandResultListener<R>> getListeners()
    {
    	return this.listeners;
    }
    
    public void addListener(AsynchronousCommandResultListener listener)
    {
    	if(listener != null)
    		listeners.add(listener);
    }
    
    public void removeListener(AsynchronousCommandResultListener listener)
    {
    	if(listener != null)
    		listeners.remove(listener);
    }

	public String getParentCommandClassName()
	{
		return this.parentCommandClassName;
	}

	/**
	 * @return the transactionContextMemento
	 */
	public TransactionContextMemento getTransactionContextMemento()
	{
		return this.transactionContextMemento;
	}
	
	// Retryable commands may turn off listener notification if the
    // command failed and it will be retried.
    // This flag is always set before the command is executed and checked immediately
    // the listeners are notified.
    // Derived classes may call resetListenerNotification() in its commandCompletionNotification()
    // method if the listeners should not be notified
	private boolean notifyListeners;
	
	protected void resetListenerNotification()
	{
		notifyListeners = false;
	}
	protected void setListenerNotification()
	{
		notifyListeners = true;
	}
	public boolean isListenerNotification()
	{
		return notifyListeners;
	}
	
	/**
	 * Getting a resolved site (i.e. the URL list of a particular site) is dependent on the
	 * SPI and the method arguments.  The inclusion of the method arguments is to allow
	 * the override providers the opportunity to inspect and make routing
	 * determinations based on those values.
	 *   
	 * @param siteNumber
	 * @param spiClass
	 * @param methodName
	 * @param methodParameterTypes
	 * @param methodParameters
	 * @return
	 * @throws MethodException
	 */
	/*
	protected ResolvedSite getResolvedSite(
		String siteNumber,
		Class<? extends VersionableDataSourceSpi> spiClass,
		String methodName,
		Class<?>[] methodParameterTypes,
		Object[] methodParameters) 
	throws MethodException
	{
		ResolvedSite resolvedSite = null;
		try
		{
			Method spiMethod = spiClass.getDeclaredMethod("getPatientDocumentSets", methodParameterTypes);
			resolvedSite = getCommandContext().getSite(siteNumber, spiClass,  spiMethod, methodParameters );
		}
		catch(Throwable t)
		{
			getLogger().error("Exception [" + t.getMessage() + "] getting site number with redirect handling, using default site resolution.", t);
			resolvedSite = getCommandContext().getSite(siteNumber);
		}
		
		// If there are no URLs to try then throw a MethodConnectionException
		// which is an indication that we could not establish a connection
		// during a method call
		if(resolvedSite == null || resolvedSite.getInterfaceUrls() == null || resolvedSite.getInterfaceUrls().isEmpty())
		{
			throw new MethodConnectionException( new ConnectionException(
				"The site '" + siteNumber + "' has no available interface URLs.\n" +
				"Please check that the protocol handlers are properly installed and that the \n" +
				"protocol preferences for the site specify valid protocols.")
			);
		}
		
		return resolvedSite;
	}*/

	
	/**
	 * This method should be called to execute a command synchronously.  It will
	 * populate the transaction context fields correctly.
	 * Do not allow derived classes to override this method so that the transaction
	 * information is correctly populated.
	 * 
	 */
	public final R callSynchronously()
	throws MethodException, ConnectionException
	{
		R result = null;
		Throwable commandException = null;
		
		if(isChildCommand())
		{
			try
			{
				// push the transaction context of the client, making it the current TC
				TransactionContextFactory.pushTransactionContext(getTransactionContextMemento());
			} 
			catch (InvalidTransactionContextMementoException itcmX)
			{
				String errorMessage = "Unable to push transaction context (make it the current context).  Command cannot be executed.";
				getLogger().error(errorMessage);
				throw new MethodException(errorMessage);
			}
		}
		
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setCommandClassName(this.getClass().getSimpleName());
		transactionContext.setAsynchronousCommand(false);
		transactionContext.setVixSoftwareVersion (getCommandContext().getRouter().getAppConfiguration().getVixSoftwareVersion ());
		transactionContext.setVixSiteNumber(getCommandContext().getRouter().getAppConfiguration().getLocalSiteNumber());
		transactionContext.setCommandId(this.getCommandIdentifier().toString());
		transactionContext.setThreadId(Thread.currentThread().getName());
		if(isChildCommand())
		{
			transactionContext.setStartTime( new Long(System.currentTimeMillis()) );
			if(getParentCommandIdentifier() != null)
			{
				transactionContext.setParentCommandId(getParentCommandIdentifier().toString());
			}
			else
			{
				getLogger().warn("command '" + this.getClass().getName() + "' is a child command but parentCommandId is null, this should never happen...");
			}
		}
		
		try
		{
			result = callSynchronouslyInTransactionContext();
		}
		catch(CompositeMethodException cmX)
		{
			StringBuilder sb = new StringBuilder();
			sb.append(cmX.getClass().getSimpleName());
			sb.append('-');
			sb.append(cmX.getMessage());
			sb.append('\n');
			sb.append( Integer.toString(cmX.size()) );
			sb.append(" exceptions in composite. \n");
			for(Iterator<CompositeExceptionComponent<MethodException>> iter = cmX.iterator(); iter.hasNext(); )
			{
				CompositeExceptionComponent<MethodException> cxc = iter.next();
				Exception x = cxc.getException();
				sb.append(x == null ? "<null>" : x.getClass().getSimpleName());
				sb.append('-');
				sb.append(x == null ? "" : x.getMessage());
				sb.append('\n');
			}
			getLogger().error(sb.toString());
			commandException = cmX;
			throw cmX;
		}
		catch (MethodException x)
		{
			getLogger().error(x);
			commandException = x;
			throw x;
		}
		catch (ConnectionException x)
		{
			getLogger().error(x);
			commandException = x;
			throw x;
		}
		finally
		{
			if(isChildCommand())
			{
				long duration = System.currentTimeMillis() - transactionContext.getStartTime().longValue();
				transactionContext.setDuration( new Long(duration) );
			}
			
			if(commandException != null)
			{
				transactionContext.setExceptionClassName(commandException.getClass().getSimpleName());
				transactionContext.setErrorMessage(commandException.getMessage());
			}
			
			// restore the transaction context if it was pushed
			if(isChildCommand())
			{
				try
				{
					getCommandContext().getTransactionLoggerService().writeLogEntry(new TransactionContextLogEntrySnapshot(TransactionContextFactory.get()));
				}
				catch (Exception x) 
				{
					StringBuilder sb = new StringBuilder();
					StackTraceElement[] stackTrace = x.getStackTrace();
					if(stackTrace != null && stackTrace.length > 0)
					{
						sb.append( x.getMessage() );
						sb.append( '@' );
						sb.append( stackTrace[0].getFileName() );
						sb.append( '[' );
						sb.append( stackTrace[0].getLineNumber() );
						sb.append( ']' );
						
						getLogger().error (sb.toString(), x);
					}
					else
						getLogger().error (x);
				}
				
				// save the transaction context so any changes made are preserved
				this.transactionContextMemento = transactionContext.getMemento();
				TransactionContextFactory.popTransactionContext();
			}
		}
		
		return result;
	}
	
	/**
	 * This method is called by the asynchronous executor when a command is being executed
	 * ... asynchronously.
	 * Do not allow derived classes to override this method because the transaction context
	 * must be correctly established.
	 * 
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
    public final AsynchronousCommandResult<R> call() 
    {
		R result = null;
		
		try
		{
			// push the transaction context of the client, making it the current TC
			TransactionContextFactory.pushTransactionContext(getTransactionContextMemento());
		} 
		catch (InvalidTransactionContextMementoException itcmX)
		{
			getLogger().error("Unable to push transaction context (make it the current context).  Command cannot be executed.", itcmX);
			return new AsynchronousCommandResult<R>(this, itcmX);
		}
		
		TransactionContext threadContext = TransactionContextFactory.get();
		
		if(threadContext == null)
		{
			getLogger().error("PANIC - transaction context not associated with an asynchrnonous command!");
		}
		else
		{
			if(threadContext.getStartTime() == null && !isChildCommand())
			{
				getLogger().warn("Transaction context start time was not set and this is not a child command.  Start time is being set to now.");
				threadContext.setStartTime( new Long(System.currentTimeMillis()) );
			}
			
			threadContext.setCommandClassName(this.getClass().getSimpleName());
			threadContext.setAsynchronousCommand(true);
			threadContext.setCommandId(this.getCommandIdentifier().toString());
			threadContext.setThreadId(Thread.currentThread().getName());
			if(getCommandContext() == null || getCommandContext().getRouter() == null || 
					getCommandContext().getRouter().getAppConfiguration() == null)
			{
				threadContext.setVixSoftwareVersion("<unknown version, no application context available>");
				threadContext.setVixSiteNumber("<unknown site number, no application context available>");
			}
			else
			{
				threadContext.setVixSoftwareVersion(
						getCommandContext().getRouter().getAppConfiguration().getVixSoftwareVersion());
				threadContext.setVixSiteNumber(getCommandContext().getRouter().getAppConfiguration().getLocalSiteNumber());
			}
			
			
			if(isChildCommand() || isPeriodic())
			{
				threadContext.setStartTime( new Long(System.currentTimeMillis()) );
				if(isChildCommand())
				{
					if(getParentCommandIdentifier() != null)
					{
						threadContext.setParentCommandId(getParentCommandIdentifier().toString());
					}
					else
					{
						getLogger().warn("command is a child command but parentCommandId is null, this should never happen...");
					}
				}
			}
		}
		
		AsynchronousCommandResult<R> asynchResult = null;
		try
		{
			setListenerNotification();
			result = callSynchronouslyInTransactionContext();
			asynchResult = new AsynchronousCommandResult<R>(this, result);
		}
		catch(CompositeMethodException cmX)
		{
			StringBuilder sb = new StringBuilder();
			sb.append( "Task [" + threadContext.getRequestType() + "-" + threadContext.getChildRequestType() + "]");
			sb.append( "completed with composite exception, details follow.\n");
			getLogger().error(sb.toString());
			
			asynchResult = new AsynchronousCommandResult<R>(this, cmX);
		}
		catch(Throwable t)
		{
			t.printStackTrace();
			getLogger().info("Task [" + threadContext.getRequestType() + "-" + threadContext.getChildRequestType() + "] completed with declared exception.");	
			asynchResult = new AsynchronousCommandResult<R>(this, t);
		}
		finally
		{
			if(threadContext == null)
			{
				getLogger().error("PANIC - transaction context has been lost !, logged transaction information is cursory.");
				threadContext = TransactionContextFactory.get();
				threadContext.setCommandClassName(this.getClass().getSimpleName());
				threadContext.setVixSoftwareVersion (getCommandContext().getRouter().getAppConfiguration().getVixSoftwareVersion ());
			}
			
			if(threadContext.getStartTime() == null)
			{
				getLogger().error("PANIC - Transaction context start time was not set.");
				threadContext.setDuration( new Long(0L) );
			}
			else if(isChildCommand())
			{
				long duration = System.currentTimeMillis() - threadContext.getStartTime().longValue();
				threadContext.setDuration( new Long(duration) );
			}
			
			if(asynchResult.getThrowable() != null)
			{
				threadContext.setExceptionClassName(asynchResult.getException().getClass().getSimpleName());
				threadContext.setErrorMessage(asynchResult.getException().getMessage());
			}
			
			// for the moment don't care if it was successful or not, just log it
			getLogger().info(
				"Task [" + threadContext.getCommandClassName() + "-" + 
				this.getCommandIdentifier() +
				(isChildCommand() ? ("->" + this.getParentCommandIdentifier() ): "") + 
				"] completed, writing transaction log entry."
			);	
			
			try
			{
				if (getCommandContext() != null){
					getCommandContext().getTransactionLoggerService().writeLogEntry(new TransactionContextLogEntrySnapshot(TransactionContextFactory.get()));
				}
			}
			catch (Exception x) 
			{
				StringBuilder sb = new StringBuilder();
				StackTraceElement[] stackTrace = x.getStackTrace();
				if(stackTrace != null && stackTrace.length > 0)
				{
					sb.append( x.getMessage() );
					sb.append( '@' );
					sb.append( stackTrace[0].getFileName() );
					sb.append( '[' );
					sb.append( stackTrace[0].getLineNumber() );
					sb.append( ']' );
					
					getLogger().error (sb.toString(), x);
				}
				else
					getLogger().error (x);
			}

			// callback to the CommandProcessor, used by the retryable commands
			// to decrement its remaining retry count
			commandProcessingComplete();

			// save the transaction context so any changes made are preserved
			this.transactionContextMemento = threadContext.getMemento();
			// restore the transaction context
			TransactionContextFactory.popTransactionContext();
		}
		
		return asynchResult;
    }
	
	/**
	 * A method that may be overridden by derived classes to receive notification
	 * that the command has run, while the transaction context is still set correctly.
	 * This is used by the Retryable commands to track the execution attempts. 
	 * In this implementation it does nothing.
	 * Retryable commands may turn off listener notification if the
	 * command failed and it will be retried.
	 * This flag is always set before the command is executed and checked immediately
	 * before the listeners are notified.
	 * Derived classes may call resetListenerNotification() in its commandCompletionNotification()
	 * method if the listeners should not be notified
	 */
	protected void commandProcessingComplete()
	{
		
	}

	/**
	 * Notify the command completion listeners that the command has completed.
	 * This method is called while the TransactionContext is set correctly so that
	 * listeners may operate in that context.
	 * @param result 
	 */
	private void notifyListeners(AsynchronousCommandResult<R> result)
	{
		for( AsynchronousCommandResultListener<R> listener : listeners )
			listener.commandComplete(result);
	}
	
	/**
	 * This method executes the command synchronously and assumes that the transaction
	 * context is correctly set.  
	 * For a typical synchronous call (on a Tomcat HTTP thread) this method may be called directly.
	 * For an asynchronous call the call() method will be invoked by the command executor,
	 * which will set the transaction context, call this method, wrap the result in a
	 * AsynchronousCommandResult instance and notify listeners.
	 * 
	 * @return
	 * @throws Exception
	 */
	public abstract R callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException;
	
	// =============================================================================
	// ScheduledPriorityQueueElement implementation
	// =============================================================================
	
	@Override
    public Date getAccessibilityDate()
    {
	    return accessibilityDate;
    }


	@Override
    public Priority getPriority()
    {
	    return priority;
    }


	@Override
    public Date getProcessingCommencementTargetDate()
    {
	    return processingTargetCommencementDate;
    }


	@Override
    public long getProcessingDurationEstimate()
    {
	    return processingDurationEstimate;
    }

	/**
	 * @param accessibilityDate the accessibilityDate to set
	 */
	public void setAccessibilityDate(Date accessibilityDate)
	{
		this.accessibilityDate = accessibilityDate;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int ordinal)
	{
		this.priority = Priority.valueOfNormalized(ordinal);
	}

	/**
	 * @param processingDurationEstimate the processingDurationEstimate to set
	 */
	public void setProcessingDurationEstimate(long processingDurationEstimate)
	{
		this.processingDurationEstimate = processingDurationEstimate;
	}

	/**
	 * @return the processingTargetCommencementDate
	 */
	Date getProcessingTargetCommencementDate()
	{
		return this.processingTargetCommencementDate;
	}

	/**
	 * @param processingTargetCommencementDate the processingTargetCommencementDate to set
	 */
	void setProcessingTargetCommencementDate(Date processingTargetCommencementDate)
	{
		this.processingTargetCommencementDate = processingTargetCommencementDate;
	}

	/**
	 * Force derived classes to define a meaningful equals() method.
	 * The result of the .equals() method MUST follow the semantics defined
	 * in the core Java API and must:
	 * 1.) for idempotent commands must return .equals() when the type and the parameters of the command are equals, or
	 *     the results of the command are expected to be equal assuming no change in state of the source data
	 * 2.) for methods that are NOT idempotent (logging, etc...) the .equals() must never return true
	 *   
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
    @Override
    public abstract boolean equals(Object obj);

    /**
     * A simple generic toString() implementation to make debugging and management easier.
     *  
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
    	StringBuffer sb = new StringBuffer();
    	sb.append(getClass().getSimpleName());
    	sb.append("[");
    	sb.append(getClass().getSimpleName());
    	sb.append("(");
    	sb.append(parameterToString());
        sb.append(")]");
    	return sb.toString(); 
    }
    
    /**
     * Just used for producing a meaningful toString() result
     * @return
     */
    protected abstract String parameterToString();

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.interfaces.router.Command#setParentCommandIdString(java.lang.String)
	 */
	@Override
	public void setParentCommandIdString(String parentCommandId) 
	{
		if(parentCommandId != null)			
			setParentCommandIdentifier(new GUID(parentCommandId));
		else
			getLogger().warn("Setting null parent command Id String, this shouldn't happen!");
	}

	@Override
	public boolean isPeriodicProcessingTerminated()
	{
		return periodicProcessingTerminated;
	}

	@Override
	public void setPeriodicProcessingTerminated(
			boolean periodicProcessingTerminated)
	{
		this.periodicProcessingTerminated = periodicProcessingTerminated;
	}

	protected void rethrowIfFatalException(Exception e) throws MethodException 
	{
		if (e instanceof MethodException)
		{
			// We need to rethrow InvalidUserCredentialsException if that was the cause of the error.
			// This will allow the periodic command to be shut down gracefully.
			if (getFatalPeriodicExceptionClasses().contains(e.getClass()))
			{
				throw (MethodException)e;
			}
		}
	}


}
