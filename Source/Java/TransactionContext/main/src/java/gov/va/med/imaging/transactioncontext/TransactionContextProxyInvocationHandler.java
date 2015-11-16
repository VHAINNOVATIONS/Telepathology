/**
 * 
 */
package gov.va.med.imaging.transactioncontext;

import gov.va.med.ApplicationPropertyAccessor;
import gov.va.med.imaging.StackTraceAnalyzer;
import gov.va.med.imaging.tomcat.vistarealm.VistaRealmPrincipal;
import gov.va.med.imaging.tomcat.vistarealm.VistaRealmSecurityContext;
import gov.va.med.imaging.tomcat.vistarealm.VistaRealmPrincipal.AuthenticationCredentialsType;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import org.apache.log4j.Logger;

/**
 * @author VHAISWBECKEC
 *
 */
public class TransactionContextProxyInvocationHandler
implements InvocationHandler
{
	/**
	 * Meta Method method name constants
	 */
	private static final String SET_MEMENTO = "setMemento";
	private static final String GET_CONTEXT_DEBUG_STATE = "getContextDebugState";
	private static final String IS_CLIENT_PRINCIPAL = "isClientPrincipal";
	private static final String GET_DISPLAY_IDENTITY = "getDisplayIdentity";
	private static final String CLEAR = "clear";
	private static final String GET_MEMENTO = "getMemento";
	private static final String GET_IDENTITY = "getIdentity";
	private static final String CREATE_CHILD_CLONE = "createChildClone";
	private static final String GET_ADDITIONAL_DEBUG_INFORMATION = "getAdditionalDebugInformation";
	
	/**
	 * The standard prefix for a GET accessor.
	 * Note we only do String properties so no "is" methods but we have
	 * some special cases with boolen types in VistaRealmPrincipal. 
	 */
	private static final String GET_ACCESSOR_PREFIX = "get";
	private static final String IS_ACCESSOR_PREFIX = "is";
	
	/**
	 * The standard prefix for a SET accessor.
	 */
	private static final String SET_ACCESSOR_PREFIX = "set";
	
	private static final String ADD_ACCESSOR_PREFIX = "add";
	
	/**
	 * Logger accessor methods get their values from application properties
	 * preferentially over "real" properties. 
	 */
	private static final String LOGGER_ACCESSOR_PREFIX = "Logger";		// i.e. as in getLoggerSiteName()
	private static final String LOGGER_PROPERTY_PREFIX = "logger";		// i.e. loggerSiteName
	
	// =======================================================================================================
	// Known Properties by the type of access granted
	// =======================================================================================================
	
	// Immutable Properties
	private static final String ACCESS_CODE_PROPERTY = "accessCode";	// mutable if principal is client type 
	private static final String VERIFY_CODE_PROPERTY = "verifyCode";	// mutable if principal is client type 
	private static final String NAME_PROPERTY = "name";
	private static final String REALM_PROPERTY = "realm";
	private static final String AUTHENTICATED_BY_DELEGATE_PROPERTY = "authenticatedByDelegate";
	private static final String CREDENTIALS_TYPE_PROPERTY = "credentialsType";
	private static final String SECURITY_HASHCODE_PROPERTY = "securityHashCode";
	private static final String AUTHENTICATED_BY_VISTA_PROPERTY = "authenticatedByVista";

	private static boolean isImmutableProperty(String propertyName)
	{
		return 
			ACCESS_CODE_PROPERTY.equals(propertyName) ||
			VERIFY_CODE_PROPERTY.equals(propertyName) ||
			NAME_PROPERTY.equals(propertyName) ||
			REALM_PROPERTY.equals(propertyName) ||
			AUTHENTICATED_BY_DELEGATE_PROPERTY.equals(propertyName) ||
			CREDENTIALS_TYPE_PROPERTY.equals(propertyName) ||
			SECURITY_HASHCODE_PROPERTY.equals(propertyName) ||
			AUTHENTICATED_BY_VISTA_PROPERTY.equals(propertyName);
	}
	
	// Requestor Properties
	private static final String SITE_NAME_PROPERTY = "siteName";
	private static final String SITE_NUMBER_PROPERTY = "siteNumber";
	private final static String SSN_PROPERTY = "ssn";
	private static final String DUZ_PROPERTY = "duz";
	private static final String FULLNAME_PROPERTY = "fullName";
	private static final String SECURITY_TOKEN_PROPERTY = "brokerSecurityToken";
	private static final String CACHE_LOCATION_ID_PROPERTY = "cacheLocationId";
	private static final String USER_DIVISION_PROPERTY = "userDivision";

	private static boolean isRequestorProperty(String propertyName)
	{
		return 	
			SITE_NAME_PROPERTY.equals(propertyName) ||
			SITE_NUMBER_PROPERTY.equals(propertyName) ||
			SSN_PROPERTY.equals(propertyName) ||
			DUZ_PROPERTY.equals(propertyName) ||
			FULLNAME_PROPERTY.equals(propertyName) ||
			SECURITY_TOKEN_PROPERTY.equals(propertyName) || 
			CACHE_LOCATION_ID_PROPERTY.equals(propertyName) ||
			USER_DIVISION_PROPERTY.equals(propertyName);
	}
	
	private final static String DEBUG_PROPERTY = "debugInformation";
	
	private static boolean isDebugProperty(String propertyName)
	{
		return DEBUG_PROPERTY.equals(propertyName);
	}
	
	// ApplicationPropertyAccessor instances will take any Object instance and try to
	//extract a value in the desired type.  If they cannot do the type conversion then
	// they return null.
	private static ApplicationPropertyAccessor<String> stringPropertyAccessor;
	
	private static ApplicationPropertyAccessor<Boolean> booleanPropertyAccessor;
	
	private static ApplicationPropertyAccessor<Character> characterPropertyAccessor;
	
	private static ApplicationPropertyAccessor<Byte> bytePropertyAccessor;
	private static ApplicationPropertyAccessor<Short> shortPropertyAccessor;
	
	private static ApplicationPropertyAccessor<Integer> integerPropertyAccessor;
	private static ApplicationPropertyAccessor<Long> longPropertyAccessor;

	private static ApplicationPropertyAccessor<Float> floatPropertyAccessor;
	private static ApplicationPropertyAccessor<Double> doublePropertyAccessor;
	
	private static ApplicationPropertyAccessor<Object> objectPropertyAccessor;
	
	private static boolean warnOnPropertOverwrite = false;
	
	static
    {
		stringPropertyAccessor = 
			new ApplicationPropertyAccessor<String>("toString", null);
		
		try
        {
	        booleanPropertyAccessor = 
	        	new ApplicationPropertyAccessor<Boolean>("booleanValue", 
	        		Boolean.class.getMethod("parseBoolean", new Class[]{String.class})
	        	);
        } 
		catch (Exception e)
        {
			Logger.getLogger(TransactionContextProxyInvocationHandler.class).error("Exception creating boolean property accessor", e);
        } 
		
		characterPropertyAccessor = 
			new ApplicationPropertyAccessor<Character>("charValue", null);

		try
		{
			bytePropertyAccessor = 
				new ApplicationPropertyAccessor<Byte>("byteValue", 
					Byte.class.getMethod("parseByte", new Class[]{String.class})
				);
        } 
		catch (Exception e)
        {
			Logger.getLogger(TransactionContextProxyInvocationHandler.class).error("Exception creating byte property accessor", e);
        } 

		try
		{
			shortPropertyAccessor = 
				new ApplicationPropertyAccessor<Short>("shortValue", 
					Short.class.getMethod("parseShort", new Class[]{String.class})
				);
        } 
		catch (Exception e)
        {
			Logger.getLogger(TransactionContextProxyInvocationHandler.class).error("Exception creating short property accessor", e);
        } 
		
		try
		{
			integerPropertyAccessor = 
				new ApplicationPropertyAccessor<Integer>("intValue", 
					Integer.class.getMethod("parseInt", new Class[]{String.class})
				);
        } 
		catch (Exception e)
        {
			Logger.getLogger(TransactionContextProxyInvocationHandler.class).error("Exception creating integer property accessor", e);
        } 
		
		try
		{
			longPropertyAccessor = 
				new ApplicationPropertyAccessor<Long>("longValue", 
					Long.class.getMethod("parseLong", new Class[]{String.class})
				);
        } 
		catch (Exception e)
        {
			Logger.getLogger(TransactionContextProxyInvocationHandler.class).error("Exception creating long property accessor", e);
        } 

		try
		{
			floatPropertyAccessor = 
				new ApplicationPropertyAccessor<Float>("floatValue", 
					Float.class.getMethod("parseFloat", new Class[]{String.class})
				);
        } 
		catch (Exception e)
        {
			Logger.getLogger(TransactionContextProxyInvocationHandler.class).error("Exception creating float property accessor", e);
        } 
		
		try
		{
			doublePropertyAccessor = 
				new ApplicationPropertyAccessor<Double>("doubleValue", 
					Double.class.getMethod("parseDouble", new Class[]{String.class})
				);
        } 
		catch (Exception e)
        {
			Logger.getLogger(TransactionContextProxyInvocationHandler.class).error("Exception creating double property accessor", e);
        } 
		
		try
		{
			objectPropertyAccessor = new ApplicationPropertyAccessor<Object>(null, null);
        } 
		catch (Exception e)
        {
			Logger.getLogger(TransactionContextProxyInvocationHandler.class).error("Exception creating double property accessor", e);
        } 
    }
	
	private static ApplicationPropertyAccessor<?> getPropertyAccessorForType(Class<?> clazz)
	{
		if(Boolean.class.isAssignableFrom(clazz) || Boolean.TYPE == clazz )
			return booleanPropertyAccessor;
		if(Byte.class.isAssignableFrom(clazz) || Byte.TYPE == clazz)
			return bytePropertyAccessor;
		if(Character.class.isAssignableFrom(clazz) || Character.TYPE == clazz )
			return characterPropertyAccessor;
		if(Short.class.isAssignableFrom(clazz) || Short.TYPE == clazz )
			return shortPropertyAccessor;
		if(Integer.class.isAssignableFrom(clazz) || Integer.TYPE == clazz )
			return integerPropertyAccessor;
		if(Long.class.isAssignableFrom(clazz) || Long.TYPE == clazz )
			return longPropertyAccessor;
		if(Float.class.isAssignableFrom(clazz) || Float.TYPE == clazz )
			return floatPropertyAccessor;
		if(Double.class.isAssignableFrom(clazz) || Double.TYPE == clazz )
			return doublePropertyAccessor;
		if(String.class == clazz )
			return stringPropertyAccessor;
		
		return objectPropertyAccessor;
	}

	/**
	 * if true then a warning will be logged when a property value is overwritten
	 * this should usually be false because it can produce so much log output that
	 * it significantly slows unit testing
	 * @return
	 */
	public static boolean isWarnOnPropertOverwrite()
	{
		return warnOnPropertOverwrite;
	}

	public static void setWarnOnPropertOverwrite(boolean warnOnPropertOverwrite)
	{
		TransactionContextProxyInvocationHandler.warnOnPropertOverwrite = warnOnPropertOverwrite;
	}

	/**
	 * Return a boolean indicating if a transaction context has been established.
	 * Returns true if a call to create a TransactionContextProxyInvocationHandler would
	 * result in a valid transaction context.  If the underlying security context does not
	 * exist then this will return false and a new instance of TransactionContextProxyInvocationHandler
	 * will be running against a "client" instance of Principal with no real security context.
	 * Note that this differs from testing the get() method result for null, which 
	 * will create a transaction context.
	 * 
	 * @return
	 */
	public static boolean isTransactionContextEstablished()
	{
		return VistaRealmSecurityContext.get() != null;
	}
	
	// the instance logger 
	Logger logger = Logger.getLogger(this.getClass());

	// the VistaRealmPrincipal instance that this transaction context wraps
	private final VistaRealmPrincipal principal;

	/**
	 * Construct a new TransactionContextProxyInvocationHandler using the current
	 * threads VistaRealmPrincipal as the backing store.
	 *
	 */
	public TransactionContextProxyInvocationHandler()
	{
		super();
		
		VistaRealmPrincipal tempPrincipal = VistaRealmSecurityContext.get();
		if(tempPrincipal == null)
		{
			Throwable t = new Throwable();
			StackTraceAnalyzer analyzer = new StackTraceAnalyzer( t.getStackTrace() );
			StackTraceElement culprit = analyzer.getFirstElementNotInPackage("gov.va.med.imaging.transactioncontext");
			//t.printStackTrace();
			
			// JMW 1/26/2012 - we never pay attention to this warning message so I'm down grading the log level so it doesn't show up as much
			logger.debug(
				"VistaRealmSecurityContext principal has not been created on thread '" + Thread.currentThread().getName() + "' before instantiating TransactionContext. \n" + 
				"A null security context will be created.\n" + 
				"If this occurs while running in a server environment then \n" + 
				"\tthe thread is currently operating with no real security context\n" + 
				"\tasycnchronous proxy threads will have a security context assigned when they are used.\n" + 
				"If this occurs while running in a client environment then \n" + 
				"\tthe application should set the security context before calling proxies.\n" + 
				"The most likely calling code is '" + culprit.getClassName() + ":" + + culprit.getLineNumber() + "'."
			);
			
			tempPrincipal = new VistaRealmPrincipal("client", true, AuthenticationCredentialsType.Password);
			//tempPrincipal = null;
		}
		
		principal = tempPrincipal;
		VistaRealmSecurityContext.set(principal);
	}

	/**
	 * 
	 * @param principal
	 */
	public TransactionContextProxyInvocationHandler(final VistaRealmPrincipal principal)
	{
		super();
		VistaRealmPrincipal tempPrincipal = VistaRealmSecurityContext.get();
		if(tempPrincipal == null)
		{
			this.principal = principal;
			VistaRealmSecurityContext.set(principal);
		}
		else
		{
			logger.warn("Attempt to set Principal instance in TransactionContext after transaction context has been established.");
			this.principal = tempPrincipal;
		}
	}

	/**
	 * 
	 * @param securityContextMemento
	 * @throws IllegalArgumentException
	 */
	public TransactionContextProxyInvocationHandler(final Object securityContextMemento)
	throws IllegalArgumentException
	{
		super();
		if(securityContextMemento instanceof VistaRealmPrincipal)
			this.principal = (VistaRealmPrincipal)securityContextMemento;
		else
			throw new IllegalArgumentException("Security Context Memento is of an unknown type");
	}
	
	/**
	 * @return the principal
	 */
	private VistaRealmPrincipal getPrincipal()
	{
		return this.principal;
	}

	private Boolean isClientPrincipal()
	{
		VistaRealmPrincipal p = getPrincipal();
		return p != null && p instanceof ClientPrincipal;
	}
	
	/**
	 * All invocations should come through the TransactionContext interface.  We do one of two things
	 * with the invoke(): 
	 * first, if it is a known accessor method being called we simply pass the
	 * call to the known method,  
	 * second: if the method is not one of the known methods and it is a String accessor (i.e.
	 * String getX(){} or void setX(String value)) then we get or set the value from a
	 * Map<String,String> that is maintained by the VistaRealmPrincipal instance.
	 * 
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	public synchronized Object invoke(Object proxy, Method method, Object[] args) 
	throws Throwable
	{
		try
		{
			// meta-methods are methods that return info about the transaction
			// context and not necessarily the transaction context properties
			// for example: getting a memento (serializable representation)
			return invokeMetaMethods(getPrincipal(), proxy, method, args);
		}
		catch(NoSuchMethodException nsmX)
		{
			// if the security principal has been cleared then just return a null
			if(principal == null)
			{
				logger.warn("Attempt to call method " + method.getName() + " after security context has been cleared is being ignored.");
				return null;
			}
			
			try
			{
				return invokeKnownMethod(getPrincipal(), method, args);
			} 
			catch (NoSuchMethodException nsmX2)
			{
				// if the method is not one of the statically mapped methods then parse the method
				// to get the field name desired.
				return invokeUnknownMethod(getPrincipal(), method, args);
			}
		}
		
	}

	/**
	 * MetaMethods are transaction context management methods.
	 * In general, application code should not call these methods.  These 
	 * methods are used predominantly for transferring transaction context
	 * between asynchronous threads (and for testing).
	 * Mete-methods are called before checking that a principal exists, meta-methods
	 * that rely on the existence of a Principal must check that it exists before
	 * continuing.
	 * 
	 * @param proxy
	 * @param proxy2 
	 * @param method
	 * @param args
	 */
	private Object invokeMetaMethods(VistaRealmPrincipal principal, Object proxy, Method method, Object[] args)
	throws NoSuchMethodException
	{
		// the setMemento method may run without a Principal instance associated to the context,
		// because it sets one
		if( SET_MEMENTO.equals(method.getName()) && (args != null && args.length == 1) && args[0] instanceof TransactionContextMemento )
		{
			try
			{
				VistaRealmPrincipal principalClone = TransactionContextMemento.create((TransactionContextMemento)args[0]);
				principal.setAll(principalClone);
				
				return null;
			} 
			catch (Throwable x)
			{
				String location;
				if( x.getStackTrace() != null && x.getStackTrace().length > 0)
				{
					location = x.getStackTrace()[0].getFileName() + ":" + x.getStackTrace()[0].getLineNumber();
				}
				else
					location = "<location unknown>";
				
				logger.error(location, x);
				return null;
			}
		}
		
		// As close to bulletproof as we can make it, return a useful description of the current context
		// state.
		if(GET_CONTEXT_DEBUG_STATE.equals(method.getName()) && 
			(method.getParameterTypes() == null || method.getParameterTypes().length == 0) && 
			java.lang.String.class.equals(method.getReturnType()) )
		{
			return buildContextStateDebugString();
		}
		
		// methods below this conditional may rely on the Principal existing.
		if(principal == null)
		{
			logger.warn("Attempt to call method " + method.getName() + " with no security context is being ignored.");
			return null;
		}
		
		if( IS_CLIENT_PRINCIPAL.equals(method.getName()) && (args == null || args.length == 0) && Boolean.class.isAssignableFrom(method.getReturnType()) )
			return isClientPrincipal();
		
		
		if( GET_DISPLAY_IDENTITY.equals(method.getName()) && (args == null || args.length == 0) && String.class.isAssignableFrom(method.getReturnType()) )
			return principal.getAccessCode() + ", " + principal.getApplicationProperty("transactionId");
		
		if( CLEAR.equals(method.getName()) && (args == null || args.length == 0) )
		{
			VistaRealmSecurityContext.clear();
			principal = null;
			return null;
		}
		
		if( GET_MEMENTO.equals(method.getName()) && (args == null || args.length == 0) && TransactionContextMemento.class.isAssignableFrom(method.getReturnType()) )
		{
			try
			{
				TransactionContextMemento memento = TransactionContextMemento.create(principal);
				return memento;
			} 
			catch (IOException x)
			{
				logger.error(x);
				return null;
			}
		}
		
		if( GET_IDENTITY.equals(method.getName()) && (args == null || args.length == 0) )
		{
			return new Integer(principal == null ? 0 : System.identityHashCode(principal));
		}
		
		if( CREATE_CHILD_CLONE.equals(method.getName()) && (args == null || args.length == 0) )
		{
			return principal == null ? null : principal.clone();
		}
		
		if(GET_ADDITIONAL_DEBUG_INFORMATION.equals(method.getName()) && 
				(method.getParameterTypes() == null || method.getParameterTypes().length == 0) && 
				java.lang.String.class.equals(method.getReturnType()) )
		{
			return buildAdditionalDebugInformationString();
		}

		throw new NoSuchMethodException();
	}

	/**
	 * @return
	 */
	private Object buildContextStateDebugString()
	{
		if(principal == null)
			return "Principal is not set, transaction context unavailable.";
		
		if(TransactionContext.debugProperties == null)
			return "No debug properties configured.";
		
		StringBuilder sb = new StringBuilder();
		
		for(String propertyName : TransactionContext.debugProperties)
		{
			sb.append(propertyName);
			sb.append('=');
			sb.append('\'');
			
			Object value = null;
			if(isImmutableProperty(propertyName))
				value = invokeImmutablePropertyGetAccessor(principal, propertyName);
			else if(isRequestorProperty(propertyName))
				value = invokePropertyGetAccessor(principal, propertyName);
			else
				value = principal.getApplicationProperty(propertyName);
			
			sb.append(value==null ? "<null>" : value.toString());
			sb.append('\'');
		}
		
		return sb.toString();
	}
	
	private String buildAdditionalDebugInformationString()
	{
		if(principal == null)
			return null;
		
		if(TransactionContext.additionalDebugInformationProperties == null)
			return null;
		
		StringBuilder sb = new StringBuilder();
		String prefix = "";
		
		for(String propertyName : TransactionContext.additionalDebugInformationProperties)
		{
			Object value = null;
			if(isImmutableProperty(propertyName))
				value = invokeImmutablePropertyGetAccessor(principal, propertyName);
			else if(isRequestorProperty(propertyName))
				value = invokePropertyGetAccessor(principal, propertyName);
			else
				value = principal.getApplicationProperty(propertyName);
			
			if(value != null)
			{
				sb.append(prefix);
				sb.append(propertyName);
				sb.append('=');
				sb.append(value.toString());
				prefix = TransactionContext.debugInformationDelimiter;
			}
		}
		
		// if none of the properties have values then just return null
		if(sb.length() == 0)
			return null;
		
		return sb.toString();
	}

	/**
	 * Invoke one of the known methods, those that map directly to VistaRealmPrincipal calls.
	 * NOTE: all of these methods are, and must be, String accessors.
	 * @param principal
	 * @param method
	 * @param args
	 * @return
	 * @throws NoSuchMethodException - thrown if the method is not one of the known mappings
	 */
	private Object invokeKnownMethod(VistaRealmPrincipal principal, Method method, Object[] args) 
	throws NoSuchMethodException
	{
		if(principal != null)
		{
			String methodName = method.getName();									// the accessor name
			String propertyName = getPropertyName(methodName, true);				// the property name for logger properties will have logger stripped off
			boolean isGetterArgs = args == null || args.length == 0;				// true if the arg list is compatible with a String getter
			boolean isSetterArgs = args != null && args.length == 1 && args[0] instanceof String;	// true if the arg list is compatible with a String setter
			
			// ==========================================================================================
			// Immutable fields
			// VistaRealm sets these.
			// ==========================================================================================
			
			if(isImmutableProperty(propertyName))
			{
				if(isGetterAccessor(methodName) && isGetterArgs)
					return invokeImmutablePropertyGetAccessor(principal, propertyName);
				else if( isSetterAccessor(methodName) && isSetterArgs )
				{
					// if the backing Principal instance is a ClientPrincipal then allow changes to UID and PWD
					if(isClientPrincipal())
					{
						if( ACCESS_CODE_PROPERTY.equals(propertyName) )
							principal.setAccessCode((String)args[0]);
						if( VERIFY_CODE_PROPERTY.equals(propertyName) )
							principal.setVerifyCode((String)args[0]);
					}
					else
					{
						StackTraceAnalyzer analyzer = StackTraceAnalyzer.currentStackAnalyzer();
						StackTraceElement element = analyzer.getFirstElementNotInPackage( this.getClass().getPackage().getName() );
						logger.warn(
							"Attempt to set transaction context property '" + propertyName + "', which is immutable in the current context.\n" + 
							"Likely culprit is [" + Thread.currentThread().getName() + "]'" + element.getClassName() + "." + element.getMethodName() + ":" + element.getLineNumber() + "'. "
						);
					}
					return null;
				}
				else
				{
					logger.warn("Attempt to set immutable transaction context property '" + propertyName + "' or calling get accessor with arguments is being ignored.");
					return null;
				}
			}

			// ==========================================================================================
			// Real Requestor Fields and Logger Requestor Fields
			// Real requestor fields are always retreived from real properties though they may come from 
			// HTTP headers or webservice elements.
			// Logger requestor fields may come from the application properties or the "real" properties
			// if the application properties do not exist.
			// See the comments below on the setter methods for more info.
			// ==========================================================================================
			else if(isRequestorProperty(propertyName))
			{
				if( isGetterAccessor(methodName) && isGetterArgs )
				{
					if( isGetLoggerAccessor(methodName) )
						return invokeLoggerPropertyGetAccessor(principal, propertyName);
					else
						return invokePropertyGetAccessor(principal, propertyName);
				}
				
				if( isSetterAccessor(methodName) && isSetterArgs )
					return invokePropertySetAccessor(principal, propertyName, (String)args[0]);
				
				logger.warn("Attempt to access transaction context property '" + propertyName + 
						"' but either method naming or argument list is not correct.  Accessor methods must follow JavaBean standards for String properties");
				return null;
			}
			else if(isDebugProperty(propertyName))
			{
				if( isGetterAccessor(methodName) && isGetterArgs )
				{
					return invokeLoggerPropertyGetAccessor(principal, propertyName);
				}
				
				if( isSetterAccessor(methodName) && isSetterArgs )
				{
					String newValue = (String)args[0];
					setApplicationProperty(principal, propertyName, newValue);
					return null;
				}
				if((isAdderAccessor(methodName) && isSetterArgs ))
				{
					String oldValue = invokeLoggerPropertyGetAccessor(principal, propertyName);
					String newValue = (String)args[0];
					if((oldValue != null) && (oldValue.length() > 0))
					{
						newValue = oldValue + TransactionContext.debugInformationDelimiter + newValue;
					}
					setApplicationProperty(principal, propertyName, newValue);
					return null;
				}
				
				logger.warn("Attempt to access transaction context property '" + propertyName + 
						"' but either method naming or argument list is not correct.  Accessor methods must follow JavaBean standards for String properties");
				return null;
			}
	
			// we don't know what the method is, throw a NoSuchMethodException
			// the caller should interpret this as an unknown method not as a runtime error
			else
				throw new NoSuchMethodException();
		}
		else
		{
			logger.warn("Attempted to call TransactionContext method '" + method.getName() + "' and backing instance of VistaRealmPrincipal does not exist.");
			return null;
		}
	}
	
	/**
	 * Whether a field may be set depends on whether it has already been set and whether the
	 * values were set by VistARealm (not by the delegate realm). 
	 * VistaRealm sourced values are immutable.
	 * Requestor information that is being added to a VistaRealmPrincipal instance that was
	 * originally populated by VistaRealm will be stored as application properties
	 * and will be accessible as getLoggingXXX methods.
	 * Requestor information that is being added to a VistaRealmPrincipal instance that was
	 * NOT originally populated by VistaRealm will be stored in the "real" properties
	 * and will be accessible as either getXXX or getLoggingXXX methods.
	 * The getLoggingXXX methods return the application properties if they exist, else they return
	 * the fields of the same names.
	 * 
	 * @param principal
	 * @param propertyName
	 * @param string
	 * @return
	 */
	private String invokePropertySetAccessor(VistaRealmPrincipal principal, String propertyName, String value)
	{
		String oldValue = invokePropertyGetAccessor(principal, propertyName);
		
		if(oldValue != null)
			logger.warn("Overwriting known property '" + propertyName + "'; from '" + oldValue + "' to '" + value + "'.");
		
		if(principal.isAuthenticatedByDelegate())
		{
			if( SITE_NAME_PROPERTY.equals(propertyName) )
				principal.setSiteName(value);
			if( SITE_NUMBER_PROPERTY.equals(propertyName) )
				principal.setSiteNumber(value);
			if( SSN_PROPERTY.equals(propertyName) )
				principal.setSsn(value);
			if( DUZ_PROPERTY.equals(propertyName) )
				principal.setDuz(value);
			if( FULLNAME_PROPERTY.equals(propertyName) )
				principal.setFullName(value);
			if( SECURITY_TOKEN_PROPERTY.equals(propertyName))
				principal.setSecurityToken(value);
			if( CACHE_LOCATION_ID_PROPERTY.equals(propertyName))
				principal.setCacheLocationId(value);
			if( USER_DIVISION_PROPERTY.equals(propertyName))
				principal.setUserDivision(value);
			
		}
		else
			setApplicationProperty(principal, propertyName, value);
		
		return null;
	}

	/**
	 * Real Requestor Fields and Logger Requestor Fields
	 * Real requestor fields are always retreived from real properties though they may come from 
	 * HTTP headers or webservice elements.
	 * Logger requestor fields may come from the application properties or the "real" properties
	 * if the application properties do not exist.
	 * See the comments below on the setter methods for more info.
	 * 
	 * This method provides a mapping from property name to get accessor for known properties
	 * 
	 * @param principal
	 * @param propertyName
	 * @return
	 */
	private String invokePropertyGetAccessor(VistaRealmPrincipal principal, String propertyName)
	{
		if( SITE_NAME_PROPERTY.equals(propertyName) )
			return principal.getSiteName();
		if( SITE_NUMBER_PROPERTY.equals(propertyName) )
			return principal.getSiteNumber();
		if( SSN_PROPERTY.equals(propertyName) )
			return principal.getSsn();
		if( DUZ_PROPERTY.equals(propertyName) )
			return principal.getDuz();
		if( FULLNAME_PROPERTY.equals(propertyName) )
			return principal.getFullName();
		if( SECURITY_TOKEN_PROPERTY.equals(propertyName))
			return principal.getSecurityToken();
		if( CACHE_LOCATION_ID_PROPERTY.equals(propertyName))
			return principal.getCacheLocationId();
		if( USER_DIVISION_PROPERTY.equals(propertyName))
			return principal.getUserDivision();
		
		return null;
	}

	/**
	 * Real Requestor Fields and Logger Requestor Fields
	 * Real requestor fields are always retreived from real properties though they may come from 
	 * HTTP headers or webservice elements.
	 * Logger requestor fields may come from the application properties or the "real" properties
	 * if the application properties do not exist.
	 * See the comments below on the setter methods for more info.
	 * 
	 * Get the property value from the application properties first and then from the
	 * known properties if the propertyName is not set in the application properties
	 * 
	 * @param principal
	 * @param propertyName
	 * @return
	 */
	private String invokeLoggerPropertyGetAccessor(VistaRealmPrincipal principal, String propertyName)
	{
		Object propertyValue = getApplicationProperty(principal, propertyName);
		return propertyValue == null ? 
			invokePropertyGetAccessor(principal, propertyName) : 
			propertyValue.toString();
	}

	/**
	 * @param principal
	 * @param propertyName
	 * @return
	 */
	private Object invokeImmutablePropertyGetAccessor(VistaRealmPrincipal principal, String propertyName)
	{
		// access code and name are synonomous
		// name is part of the Principal interface, which TransactionContext extends
		if( ACCESS_CODE_PROPERTY.equals(propertyName) || NAME_PROPERTY.equals(propertyName) )
			return principal.getAccessCode();
		
		if( VERIFY_CODE_PROPERTY.equals(propertyName) )
			return principal.getVerifyCode();
		
		if( REALM_PROPERTY.equals(propertyName) )
			return principal.getRealm();
		
		if( AUTHENTICATED_BY_DELEGATE_PROPERTY.equals(propertyName) )
			return (Boolean)( principal.isAuthenticatedByDelegate() );

		if( CREDENTIALS_TYPE_PROPERTY.equals(propertyName) )
			return principal.getCredentialsType().toString();
		
		if( SECURITY_HASHCODE_PROPERTY.equals(propertyName) )
			return principal.getSecurityHashCode();
		
		if(AUTHENTICATED_BY_VISTA_PROPERTY.equals(propertyName))
			return new Boolean( principal.isAuthenticatedByVista() );	
		
		logger.warn("Attempt to get unknown immutable transaction context properties '" + propertyName + "' is being ignored.");
		return null;
	}

	/**
	 * @param propertyName
	 * @return
	 */
	private boolean isLoggerProperty(String propertyName)
	{
		return propertyName.startsWith(LOGGER_PROPERTY_PREFIX);
	}

	/**
	 * Unknown Methods are accessors on application properties.  VistaRealm has nothing to
	 * do with these other than storing them in the thread local on behalf of the application.
	 * 
	 * Invoke an unknown accessor.  The accessor name must be encoded in the method name using
	 * the JavaBean standard.  The return type of the method must be a base java.lang.* for get methods,
	 * or void for set methods.  Set methods must take one object argument.
	 * The values are get/set in the VistaRealmPrincipal attributeMap.
	 * 
	 * @param principal
	 * @param method
	 * @param args
	 * @return
	 */
	private Object invokeUnknownMethod(VistaRealmPrincipal principal, Method method, Object[] args)
	{
		if(principal != null)
		{
			String methodName = method.getName();
			boolean isIsGetter = methodName.startsWith(IS_ACCESSOR_PREFIX);
			boolean isGetter = methodName.startsWith(GET_ACCESSOR_PREFIX);
			boolean isSetter = methodName.startsWith(SET_ACCESSOR_PREFIX);
			
			// if the method name does not start with 'get' or 'set' then we don't know what
			// to do with it
			if(!isGetter && !isSetter && !isIsGetter)
				return null;
			
			String propertyName = getPropertyName(methodName, false);
			Object currentValue = getApplicationProperty(principal, propertyName);
			
			// if it is a proper getter method then try to convert to the return type of the
			// method
			if( isGetter || isIsGetter)
			{
				Class<?> getterReturnType = method.getReturnType();
				ApplicationPropertyAccessor<?> accessor = getPropertyAccessorForType(getterReturnType);

				return accessor.getValueAs(currentValue);
			}
			
			// if it is a proper setter then set the property by name 
			if(isSetter && args != null && args.length == 1)
			{
				StackTraceAnalyzer analyzer = StackTraceAnalyzer.currentStackAnalyzer();
				StackTraceElement element = analyzer.getFirstElementNotInPackage( this.getClass().getPackage().getName() );
				
				if(currentValue != null)
				{
					if( isWarnOnPropertOverwrite() )
						logger.warn( "TransactionContext[" + principal.hashCode() + "]" + 
							"Overwriting application property '" + propertyName + "'; from '" + currentValue + "' to '" + 
							(args[0] == null ? "<null>" : args[0].toString()) + "'.\n" +
							(element == null ? 
								"Unknown offending element" :
								"Offending element may be '" + element.getClassName() + "." + element.getMethodName() + ":" + element.getLineNumber() + "'"
							)
						);
				}
				logger.debug( (element == null ? "unknown" : (element.getClassName() + ":" + element.getLineNumber())) + 
					" is setting property name '" + propertyName + "' from value '" + currentValue + "' to '" + args[0] + "'");
				
				setApplicationProperty(principal, propertyName, args[0]);
			}
			return null;
		}
		else
		{
			logger.warn("Attempted to call TransactionContext method '" + method.getName() + "' and backing instance of VistaRealmPrincipal does not exist.");
			return null;
		}
	}

	/**
	 * Semantic Helper Method
	 * 
	 * @param principal
	 * @param propertyName
	 * @param value
	 */
	private void setApplicationProperty(VistaRealmPrincipal principal, String propertyName, Object value)
	{
		principal.setApplicationProperty(propertyName, value);
	}
	
	/**
	 * Semantic Helper Method
	 * 
	 * @param principal
	 * @param propertyName
	 * @return
	 */
	private Object getApplicationProperty(VistaRealmPrincipal principal, String propertyName)
	{
		return principal.getApplicationProperty(propertyName);
	}
	
	/**
	 * If the method name is getXXX or setXXX return the 
	 * (i.e. first char is lower case).
	 * 
	 * e.g. getSiteNumber => siteNumber
	 *      setSumpin => sumpin
	 * 
	 * @param accessorMethodName
	 * @return
	 */
	private String getPropertyName(String accessorMethodName, boolean removeLoggerPrefix)
	{
		boolean isIs = isIsAccessor(accessorMethodName);
		boolean isGetter = isGetterAccessor(accessorMethodName);
		boolean isSetter = isSetterAccessor(accessorMethodName);
		boolean isAdder = isAdderAccessor(accessorMethodName);
		
		// if the method name does not start with 'get' or 'set' then we don't know what
		// to do with it
		if( ! isGetter && ! isSetter && !isAdder)
			return null;
		
		int prefixLength = 
			isIs ? IS_ACCESSOR_PREFIX.length() :
			isGetter ? GET_ACCESSOR_PREFIX.length() : 
			isSetter ? SET_ACCESSOR_PREFIX.length() :
			ADD_ACCESSOR_PREFIX.length();
			
		// there must be a field name to be a proper accessor, i.e. get() is not a proper
		// accessor as far as we are concerned
		if( accessorMethodName.length() <= prefixLength )
			return null;
		
		//	the fieldname begins after the 'is', 'get' or 'set'
		String propertyName = accessorMethodName.substring(prefixLength);
		
		// lower case the first char
		propertyName = propertyName.substring(0,1).toLowerCase() + (propertyName.length()>1 ? propertyName.substring(1) : "");
		
		if(removeLoggerPrefix && propertyName.startsWith(LOGGER_PROPERTY_PREFIX))
		{
			propertyName = propertyName.substring(LOGGER_PROPERTY_PREFIX.length());
			propertyName = propertyName.substring(0,1).toLowerCase() + (propertyName.length()>1 ? propertyName.substring(1) : "");
		}
		
		return propertyName;
	}
	
	/**
	 * Create a standard getter method name from the property name.
	 * 
	 * @param propertyName
	 * @return
	 */
	private String getGetterName(String propertyName)
	{
		return GET_ACCESSOR_PREFIX + propertyName.substring(0,1).toUpperCase() + (propertyName.length()>1 ? propertyName.substring(1) : "");
	}

	/**
	 * Create a standard setter method name from the property name.
	 * 
	 * @param propertyName
	 * @return
	 */
	private String getSetterName(String propertyName)
	{
		return SET_ACCESSOR_PREFIX + propertyName.substring(0,1).toUpperCase() + (propertyName.length()>1 ? propertyName.substring(1) : "");
	}
	
	private boolean isGetterAccessor(String methodName)
	{
		return methodName.startsWith(GET_ACCESSOR_PREFIX) || methodName.startsWith(IS_ACCESSOR_PREFIX); 
	}
	
	private boolean isIsAccessor(String methodName)
	{
		return methodName.startsWith(IS_ACCESSOR_PREFIX); 
	}
	
	private boolean isSetterAccessor(String methodName)
	{
		return methodName.startsWith(SET_ACCESSOR_PREFIX); 
	}
	
	private boolean isAdderAccessor(String methodName)
	{
		return methodName.startsWith(ADD_ACCESSOR_PREFIX);
	}
	
	/**
	 * Accessor methods that start eith "getLogger" or "isLogger" are considered
	 * known properties but are handled special.  This method just tells us that the
	 * method name matches the pattern.
	 * 
	 * @param methodName
	 * @return
	 */
	private boolean isGetLoggerAccessor(String methodName)
	{
		return methodName.startsWith(GET_ACCESSOR_PREFIX + LOGGER_ACCESSOR_PREFIX) || methodName.startsWith(IS_ACCESSOR_PREFIX + LOGGER_ACCESSOR_PREFIX); 
	}

}
