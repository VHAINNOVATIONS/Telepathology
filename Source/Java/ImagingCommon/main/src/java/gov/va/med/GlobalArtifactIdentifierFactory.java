/**
 * 
 */
package gov.va.med;

import gov.va.med.exceptions.GlobalArtifactIdentifierFormatException;
import gov.va.med.imaging.exceptions.URNFormatException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import org.apache.log4j.Logger;

/**
 * A factory to create instances of GlobalArtifactIdentifier realizing classes.
 * The specific type created is a function of the value of the home community ID.
 * The instance created may be a derivation of URN but that is not guaranteed. 
 * 
 * @author vhaiswbeckec
 *
 */
public class GlobalArtifactIdentifierFactory
{
	private static List<Class<? extends GlobalArtifactIdentifier>> registeredRealizations = 
		new ArrayList<Class<? extends GlobalArtifactIdentifier>>();
	
	public static final String REQUIRED_CREATE_METHOD_NAME = "createFromGlobalArtifactIdentifiers";
	public static final Class<?>[] REQUIRED_CREATE_METHOD_PARAMETERS = new Class<?>[]{String.class, String.class, String.class, String[].class};
	public static final String REQUIRED_APPLICABLE_METHOD_NAME = "isApplicableHomeCommunityId";
	public static final Class<?>[] REQUIRED_APPLICABLE_METHOD_PARAMETERS = new Class<?>[]{String.class, String.class, String.class};
	
	static
	{
		// The order of the following list is significant, though the GlobalArtifactIdentifierImpl
		// will always be forced to the end of the list.
		// Where multiple realizations belong to the same home community ID, the first one in the list
		// will be picked and an instance of that will be created in the create() methods.
		registerRealization(gov.va.med.imaging.DocumentURN.class);				// An identifier of a document
		registerRealization(gov.va.med.imaging.DocumentSetURN.class);			// An identifier of a document set
		registerRealization(gov.va.med.imaging.ImageURN.class);					// An identifier of an VA image
		registerRealization(gov.va.med.imaging.StudyURN.class);					// An identifier of a VA study
		registerRealization(gov.va.med.imaging.BhieImageURN.class);				// An identifier of a BHIE sourced image
		registerRealization(gov.va.med.imaging.BhieStudyURN.class);				// An identifier of a BHIE sourced study
		//registerRealization(gov.va.med.PatientArtifactIdentifier.class);		// this class should be after all the URN classes
		registerRealization(gov.va.med.GlobalArtifactIdentifierImpl.class);		// this class will be last in the Sorted Set
	}
	
	/**
	 * 
	 * @param realization
	 */
	public static void registerRealization(Class<? extends GlobalArtifactIdentifier> realization)
	{
		try
		{
			int classModifiers = realization.getModifiers();
			if(! Modifier.isPublic(classModifiers) )
				throw new Exception("The class '" + realization.getName() + "' must be declared public and it is not.");

			Method requiredCreate = realization.getMethod(REQUIRED_CREATE_METHOD_NAME, REQUIRED_CREATE_METHOD_PARAMETERS);
			if( !GlobalArtifactIdentifier.class.isAssignableFrom(requiredCreate.getReturnType()) )
				throw new Exception("The method '" + REQUIRED_CREATE_METHOD_NAME + "' must return type GlobalArtifactIdentifier and it does not.");
			int createModifiers = requiredCreate.getModifiers();
			if(! Modifier.isStatic(createModifiers) )
				throw new Exception("The method '" + REQUIRED_CREATE_METHOD_NAME + "' is not declared static and it must be.");
			if(! Modifier.isPublic(createModifiers) )
				throw new Exception("The method '" + REQUIRED_CREATE_METHOD_NAME + "' is not declared public and it must be.");

			Method requiredApplicable = realization.getMethod(REQUIRED_APPLICABLE_METHOD_NAME, REQUIRED_APPLICABLE_METHOD_PARAMETERS);
			if( boolean.class != requiredApplicable.getReturnType() && Boolean.class != requiredApplicable.getReturnType() )
				throw new Exception("The method '" + REQUIRED_APPLICABLE_METHOD_NAME + "' must return type boolean and it does not.");
			int requiredApplicableModifiers = requiredApplicable.getModifiers();
			if(! Modifier.isStatic(requiredApplicableModifiers) )
				throw new Exception("The method '" + REQUIRED_APPLICABLE_METHOD_NAME + "' is not declared static and it must be.");
			if(! Modifier.isPublic(requiredApplicableModifiers) )
				throw new Exception("The method '" + REQUIRED_APPLICABLE_METHOD_NAME + "' is not declared public and it must be.");
			
			Logger.getLogger(GlobalArtifactIdentifierFactory.class).info("Registering GlobalArtifactIdentifier class '" + realization.getSimpleName() + "' with factory.");
			registeredRealizations.add(realization);
		}
		catch (Throwable x)
		{
			Logger.getLogger(GlobalArtifactIdentifierFactory.class).error("Unable to register '" + realization.getName() + "' as a GlobalArtifactIdentifier.", x);
		}		
	}
	
	/**
	 * @param studyUrn
	 * @param class1
	 * @return
	 */
	public static GlobalArtifactIdentifier create(String stringified, Class<? extends GlobalArtifactIdentifier>... coerceToClass)
	throws GlobalArtifactIdentifierFormatException
	{
		return create(stringified, SERIALIZATION_FORMAT.RFC2141, coerceToClass);
	}
	
	/**
	 * 
	 * @param stringified
	 * @param coerceToClasses
	 * @return
	 * @throws ConfigurationException
	 * @throws GlobalArtifactIdentifierFormatException
	 */
	public static GlobalArtifactIdentifier create(
		String stringified,
		SERIALIZATION_FORMAT serializationFormat, 
		Class<? extends GlobalArtifactIdentifier>... coerceToClasses) 
	throws GlobalArtifactIdentifierFormatException
	{
		if(stringified == null || stringified.length() == 0)
			return null;
		
		// first, use the URNFactory to create a URN
		// the URN classes are usually realizations of GlobalArtifactIdentifier and
		// 
		try
		{
			URN urn = URNFactory.create(stringified, serializationFormat);
			if(urn instanceof GlobalArtifactIdentifier)
			{
				if(coerceToClasses == null || coerceToClasses.length == 0)
					return (GlobalArtifactIdentifier)urn;
				for(Class<? extends GlobalArtifactIdentifier> coerceToClass : coerceToClasses)
					if( coerceToClass.isInstance(urn) )
						return (GlobalArtifactIdentifier)urn;
			}
		}
		catch (URNFormatException x)
		{
			// ignore the error, we'll just try to make a GAI from the identifier triad
		}
		
		return createGlobalArtifactIdentifier(stringified, serializationFormat, coerceToClasses);

	}

	/**
	 * 
	 * @param stringified
	 * @param coerceToClasses
	 * @return
	 * @throws GlobalArtifactIdentifierFormatException
	 */
	private static GlobalArtifactIdentifier createGlobalArtifactIdentifier(
		String stringified,
		SERIALIZATION_FORMAT serializationFormat, 
		Class<? extends GlobalArtifactIdentifier>... coerceToClasses) 
	throws GlobalArtifactIdentifierFormatException
	{
		Matcher parserMatcher = GlobalArtifactIdentifierImpl.namespaceSpecificStringPattern.matcher(stringified);
		if(!parserMatcher.matches())
			throw new GlobalArtifactIdentifierFormatException("'" + stringified + "' cannot be parsed into a generic global artifact identifier form.");

		return create(
			parserMatcher.group(GlobalArtifactIdentifierImpl.homeCommunityIdGroup),
			parserMatcher.group(GlobalArtifactIdentifierImpl.repositoryIdGroup),
			parserMatcher.group(GlobalArtifactIdentifierImpl.documentIdGroup),
			coerceToClasses
		);
	}

	/**
	 * 
	 * @param homeCommunityId
	 * @param repositoryId
	 * @param documentId
	 * @param coerceToClass
	 * @return
	 * @throws GlobalArtifactIdentifierFormatException 
	 */
	@SuppressWarnings("unchecked")
	public static <G extends GlobalArtifactIdentifier> G create(
		String homeCommunityId, 
		String repositoryId, 
		String documentId, 
		Class<G> coerceToClass) 
	throws GlobalArtifactIdentifierFormatException
	{
		return (G)create(
			homeCommunityId, 
			repositoryId, 
			documentId, 
			(Class<? extends GlobalArtifactIdentifier>[])new Class<?>[]{coerceToClass});
	}
	
	public static GlobalArtifactIdentifier create(
		String homeCommunityId, 
		String repositoryId, 
		String documentId)
	throws GlobalArtifactIdentifierFormatException
	{
		return create(homeCommunityId, repositoryId, documentId, (Class<? extends GlobalArtifactIdentifier>[])null);
	}
	
	/**
	 * @param homeCommunityId
	 * @param repositoryId
	 * @param documentId
	 * @param coerceToClasses
	 * @return
	 * @throws GlobalArtifactIdentifierFormatException 
	 */
	public static GlobalArtifactIdentifier create(
		String homeCommunityId, 
		String repositoryId, 
		String documentId, 
		Class<? extends GlobalArtifactIdentifier>... coerceToClasses) 
	throws GlobalArtifactIdentifierFormatException
	{
		List<Class<? extends GlobalArtifactIdentifier>> candidateRealizations;
		
		if(coerceToClasses != null && coerceToClasses.length != 0)
		{
			candidateRealizations = new ArrayList<Class<? extends GlobalArtifactIdentifier>>();
			for(Class<? extends GlobalArtifactIdentifier> coerceToClass : coerceToClasses)
				candidateRealizations.add(coerceToClass);
		}
		else
			candidateRealizations = registeredRealizations;
		
		for(Class<? extends GlobalArtifactIdentifier> coerceToClass : candidateRealizations)
		{
			try
			{
				if( isRegisteredRealization(coerceToClass) && isApplicable(coerceToClass, homeCommunityId, repositoryId, documentId))
					return createInstance(coerceToClass, homeCommunityId, repositoryId, documentId);
			}
			catch (Exception x)
			{
				String msg = 
					"Exception creating an identifier with the home community ID '" + homeCommunityId + 
					"', repository ID '" + repositoryId + "', document ID '" + documentId +
					"' was when this arose.  \n" + 
					"Possibly a configuration exception, assure that the required static methods are implemented.";
				Logger.getLogger(GlobalArtifactIdentifierFactory.class).error(msg);
				throw new GlobalArtifactIdentifierFormatException(msg, x);
			}
		}
		
		return null;
	}
	
	/**
	 * @param coerceToClass
	 * @return
	 */
	public static boolean isRegisteredRealization(Class<? extends GlobalArtifactIdentifier> coerceToClass)
	{
		if(coerceToClass == null)
			return false;
		
		for(Class<? extends GlobalArtifactIdentifier> registeredRealization : registeredRealizations)
			if( coerceToClass == registeredRealization )
			{
				Logger.getLogger(GlobalArtifactIdentifierFactory.class).info("'" + coerceToClass.getSimpleName() + "' is a registered class.");
				return true;
			}
		
		Logger.getLogger(GlobalArtifactIdentifierFactory.class).info("'" + coerceToClass.getSimpleName() + "' is NOT a registered class.");
		return false;
	}

	/**
	 * @param registeredRealization
	 * @param homeCommunityId
	 * @param documentId 
	 * @param repositoryId 
	 * @return
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private static boolean isApplicable(
		Class<? extends GlobalArtifactIdentifier> registeredRealization, 
		String homeCommunityId, 
		String repositoryId, 
		String documentId) 
	throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		Method requiredApplicable = registeredRealization.getMethod(REQUIRED_APPLICABLE_METHOD_NAME, REQUIRED_APPLICABLE_METHOD_PARAMETERS);
		Boolean result = (Boolean)requiredApplicable.invoke(null, new Object[]{homeCommunityId, repositoryId, documentId});
		return result.booleanValue();
	}
	
	/**
	 * @param registeredRealization
	 * @param homeCommunityId
	 * @param repositoryId
	 * @param documentId
	 * @return
	 */
	private static GlobalArtifactIdentifier createInstance(
		Class<? extends GlobalArtifactIdentifier> registeredRealization,
		String homeCommunityId, 
		String repositoryId, 
		String documentId)
	throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		Method requiredCreate = registeredRealization.getMethod(REQUIRED_CREATE_METHOD_NAME, REQUIRED_CREATE_METHOD_PARAMETERS);
		
		return (GlobalArtifactIdentifier)requiredCreate.invoke(null, new Object[]{homeCommunityId, repositoryId, documentId, null});
	}

	/**
	 * 
	 * @author vhaiswbeckec
	 *
	 */
	public static class ConfigurationException 
	extends Throwable
	{
		private static final long serialVersionUID = 2639402351183835155L;

		/**
		 * 
		 */
		public ConfigurationException()
		{
			super();
		}

		/**
		 * @param message
		 * @param cause
		 */
		public ConfigurationException(String message, Throwable cause)
		{
			super(message, cause);
		}

		/**
		 * @param message
		 */
		public ConfigurationException(String message)
		{
			super(message);
		}

		/**
		 * @param cause
		 */
		public ConfigurationException(Throwable cause)
		{
			super(cause);
		}
		
	}
}
