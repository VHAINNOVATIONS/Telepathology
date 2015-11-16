/**
 * 
 */
package gov.va.med.imaging.transactioncontext;

import gov.va.med.SERIALIZATION_FORMAT;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.tomcat.vistarealm.VistaRealmPrincipal;
import gov.va.med.imaging.tomcat.vistarealm.VistaRealmPrincipal.AuthenticationCredentialsType;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * @author VHAISWBECKEC
 *
 */
public class TransactionContextFactory
{
	private static Logger logger = Logger.getLogger(TransactionContextFactory.class);

    private static ThreadLocal<Stack<TransactionContextMemento>> transactionContextMementoStack = 
    	new ThreadLocal<Stack<TransactionContextMemento>>();
    
	/**
	 * Get a reference to the TransactionContext.  The returned instance is 
	 * unique to the call, but the underlying data store is a thread-local
	 * instance.
	 * 
	 * @return
	 */
	public static TransactionContext get()
	{
		TransactionContext transactionContext = 
			(TransactionContext) Proxy.newProxyInstance(TransactionContext.class.getClassLoader(),
                new Class[] { TransactionContext.class },
                new TransactionContextProxyInvocationHandler() );
		
		return transactionContext;
	}
	
	/**
	 * Call this method only the first time the TransactionContext is needed on a
	 * thread, else the Principal instance passed will be ignored (a warning will be
	 * logged).
	 * 
	 * @param principal
	 * @return
	 */
	public static TransactionContext createClientTransactionContext(ClientPrincipal principal)
	{
		TransactionContext transactionContext = 
			(TransactionContext) Proxy.newProxyInstance(TransactionContext.class.getClassLoader(),
                new Class[] { TransactionContext.class },
                new TransactionContextProxyInvocationHandler(principal) );
		
		return transactionContext;
	}

	/**
	 * 
	 * @return
	 */
	public static boolean isTransactionContextEstablished()
	{
		return TransactionContextProxyInvocationHandler.isTransactionContextEstablished();
	}
	
	/**
	 * Returns an opaque representation of the current transaction context.
	 * The returned instance may be used in a pushTransactionContext() to 
	 * re-establish the transaction context on a different thread.
	 * 
	 * @return
	 */
	public static TransactionContextMemento getTransactionContextMemento()
	{
		TransactionContext threadTransactionContext = get();
		
		return threadTransactionContext.getMemento();
	}
	
	/**
	 * Store the current transaction context on the stack, creating the stack if it does not exist.
	 * Set the transaction context for the current thread to that encapsulated in the
	 * given transaction context memento.  
	 * 
	 * @param transactionContextMemento
	 */
	public static void pushTransactionContext(TransactionContextMemento transactionContextMemento)
	throws InvalidTransactionContextMementoException
	{
		Stack<TransactionContextMemento> threadContextMementoStack = transactionContextMementoStack.get();
		if(threadContextMementoStack == null)
		{		
			threadContextMementoStack = new Stack<TransactionContextMemento>();
			transactionContextMementoStack.set(threadContextMementoStack);
		}
		
		// If there is an existing transaction context then the values are serialized (made into a memento)
		// and then pushed onto the stack of memento.
		if(isTransactionContextEstablished())
		{
			TransactionContextMemento currentTransactionContextMemento = getTransactionContextMemento();
			if(currentTransactionContextMemento != null)
				threadContextMementoStack.push(currentTransactionContextMemento);
		}
		
		setSecurityContext(transactionContextMemento);
	}
	
	/**
	 * Restore the previous transaction context, if one exists. 
	 * Otherwise, silently do nothing.
	 */
	public static void popTransactionContext()
	{
		Stack<TransactionContextMemento> threadContextMementoStack = transactionContextMementoStack.get();
		if(threadContextMementoStack == null)
			return;
		
		TransactionContextMemento memento;
		try
		{
			memento = threadContextMementoStack.pop();
			if(memento == null)
				return;
		}
		catch (EmptyStackException x)
		{
			// not really an error, just no parent transaction context
			return;
		}
		
		setSecurityContext(memento);
	}
	
	/**
	 * Clears the current context on the main thread and restores an old one.
	 * @param memento
	 */
	public static void restoreTransactionContext(TransactionContextMemento memento) 
	{
		// Clear out the context on main thread and push the old one
        TransactionContextFactory.get().clear();                    
        try
        {
              TransactionContextFactory.pushTransactionContext(memento);
        } 
        catch (InvalidTransactionContextMementoException itcmX)
        {
              logger.error("Error pushing memento back onto context, " + itcmX.getMessage());
        }
	}

	
	/**
	 * In general this is another method that nobody but the security code should be calling.
	 * This allows code to change the security (and transaction context) on the current thread.
	 * This is usually done to provide an asynchronous thread the same context as an originating
	 * thread, for example in logging and in asynchrounous proxies.
	 * 
	 * Note that changing a security context is not really a security issue because the new context
	 * must have priveliges to invoke an operation.
	 * 
	 * @param securityContextMemento
	 */
	public static void setSecurityContext(TransactionContextMemento securityContextMemento)
	{
		get().setMemento(securityContextMemento);
	}
	
	private static int testTransactionId = 0;
	/**
	 * Create a security/transaction context for testing.
	 * 
	 * realm = "test"
	 * UID/Access = "testing_1"
	 * PWD/Verify = "testing_2"
	 * duz = "135"
	 * fullName = "IMAGPROVIDERONETHREEFIVE,ONETHREEFIVE"
	 * siteName = "SALT LAKE CITY"
	 * siteNumber = "660"
	 * ssn = "987670909"
	 * roles are "clinical-display-user", "vista-user", and "peer-vixs"
	 * transaction_id is a monotonically increasing integer starting at 0
	 * @throws IOException 
	 */
	public static synchronized void setTestSecurityContext() 
	throws IOException
	{
		List<String> testRoles = new ArrayList<String>();
		testRoles.add("clinical-display-user");
		testRoles.add("vista-user");
		testRoles.add("peer-vixs");
		
		VistaRealmPrincipal principal = new VistaRealmPrincipal(
				"test", false, AuthenticationCredentialsType.Password, "testing_1", "testing_2", 
				"135", "IMAGPROVIDERONETHREEFIVE,ONETHREEFIVE", "987670909", 
				"660", "SALT LAKE CITY", testRoles, null);
		setSecurityContext( TransactionContextMemento.create(principal) );
		get().setTransactionId(Integer.toString(testTransactionId++));
	}
	
	/**
	 * Set the Thread-Local security context for client applications.
	 *  
	 * @param uid
	 * @param pwd
	 * @throws IOException
	 */
	public static synchronized void setClientSecurityContext(String uid, String pwd) 
	throws IOException
	{
		List<String> clientRoles = new ArrayList<String>();
		
		VistaRealmPrincipal principal = new ClientPrincipal(
				"client", false, AuthenticationCredentialsType.Password, uid, pwd, 
				null, null, null, null, null, clientRoles, null);
		setSecurityContext( TransactionContextMemento.create(principal) );
	}
	
	/**
	 * 
	 * @param imageUrn
	 * @return
	 */
	public static String decodeUrnForLogging(ImageURN imageUrn)
	{
		// JMW 12/7/2010 P104 - now that the URN does not contain Base32 encoded pieces
		// no reason for this method to do anything but call toString()
		//return imageUrn.toString();
		return imageUrn.toString(SERIALIZATION_FORMAT.RAW);
		/*
		
		StringBuilder sb = new StringBuilder();
		String []urnPieces = imageUrn.toString().split(":");
		sb.append(urnPieces[0] + ":" + urnPieces[1] + ":");
		sb.append(imageUrn.getOriginatingSiteId());
		sb.append("-");
		// CTB 29Nov2009
		//sb.append(Base32ConversionUtility.base32Decode(imageUrn.getInstanceId()));
		sb.append(imageUrn.getInstanceId());
		sb.append("-");
		// CTB 29Nov2009
		//sb.append(Base32ConversionUtility.base32Decode(imageUrn.getStudyId()));
		sb.append(imageUrn.getStudyId());
		sb.append("-");
		sb.append(imageUrn.getPatientIcn());
		sb.append("-");
		sb.append(imageUrn.getImageModality());
		return sb.toString();*/
	}
	
	/**
	 * Construct a date range string suitable for inclusion in the transaction log. This information usually originates 
	 * from a web service filter object.
	 * @param fromDate - lower end of the date range
	 * @param toDate - upper end on the date range
	 * @return - the formatted date range string
	 */
	public static String getFilterDateRange(Date fromDate, Date toDate) 
	{
		String range = null;
		if (fromDate == null && toDate == null)
		{
			range = "all";
		}
		else
		{
			range = getDateString(fromDate) + " - " + getDateString(toDate);
		}
		return range;
	}
	
	/**
	 * Format a Date to a string suitable for inclusion in the transaction log
	 * @param date
	 * @return
	 */
	private static String getDateString(Date date) 
	{
		String dateString="";
		if (date != null)
		{
			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
			dateString = df.format(date);
		}
		return dateString;
	}
	
}
