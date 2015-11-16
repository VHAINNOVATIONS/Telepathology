/**
 * 
 */
package gov.va.med.imaging.core;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The semantics of the commands, command factory methods and the router interface methods
 * are strictly specified in this package (and mostly in this class).  This strictness
 * is required to allow the reliable generation of the facade router implementations.
 * 
 * Router methods, except for methods pre-dating the semantic router, MUST follow the naming
 * convention prescribed below.
 * Methods are named as follows:
 * action + object + optional(preposition + related_object)
 * - action describes what is to be done with the object
 * - object describes the object type being acted on
 * - the optional preposition and object describe the relationship of additional objects to the actio
 *   being taken
 * - the optional preposition and related_object are only required when it is necessary to differentiate
 *   between commands with similar action/object pairs
 * - method names use camel-casing, where the first char of each word is capitalized, except that the
 *   first character of the first word is always lower-case
 * Neither the action name, the object name, nor the related_object name may include any of the words
 * reserved for the action or the preposition.
 * examples: getStudyList(), getStudyListByStudyFilter()
 * The action MUST be one of POST, GET, PUT, DELETE, READ, UPDATE (see Action enumeration)
 * The object MUST be the simple name of a business object (a member of the gov.va.med.imaging.business package),
 *   with an optional collection type (Set, List, Map) concatenated.
 * The preposition MUST be one of BY, LIKE (see Preposition enum)
 * The related_object MUST be either the simple name of a business object or the simple name of a core Java class
 * 
 * Router methods that pre-date the semantic router are mapped using a hard-coded mapping to 
 * the "correct" names.
 * 
 * Command names follow the same semantics as the router names with the String "Command" suffixed.
 * - command names are camel-cased with teh first letter always capitalized (as in a class name).
 * Command factory methods follow the same semantics as the Command names with the String "create" prefixed.
 * - command factory method names are camel-cased with the first character lower-case (as in a method name).
 * 
 * @author vhaiswbeckec
 * 
 */
public abstract class CoreRouterSemantics
{
	private static String actionRegex;
	private static String prepositionRegex;

	static
	{
		StringBuilder actionRegexSb = new StringBuilder();
		for (Action action : Action.values())
		{
			for (String actionSynonym : action.getExpression())
			{
				if (actionRegexSb.length() > 0)
					actionRegexSb.append('|');
				actionRegexSb.append(actionSynonym);
			}
		}

		actionRegex = actionRegexSb.toString();

		StringBuilder prepositionRegexSb = new StringBuilder();
		for (Preposition preposition : Preposition.values())
		{
			for (String prepositionSynonym : preposition.getExpression())
			{
				if (prepositionRegexSb.length() > 0)
					prepositionRegexSb.append('|');
				prepositionRegexSb.append(Character
						.toUpperCase(prepositionSynonym.charAt(0)));
				prepositionRegexSb.append(prepositionSynonym.substring(1));
			}
		}

		prepositionRegex = prepositionRegexSb.toString();
		
		StringBuilder collectionRegexSb = new StringBuilder();
		for(CollectionTypeVocabulary collectionType : CollectionTypeVocabulary.values())
		{
			if(collectionRegexSb.length() > 0)
				collectionRegexSb.append('|');
			collectionRegexSb.append(collectionType.getSimpleName());
		}
		
		collectionTypeRegex = collectionRegexSb.toString();
	}

	public static final String commandFactoryMethodPrefix = "create";
	private static final String commandFactoryMethodPrefixRegex = "create";
	private static final String collectionTypeRegex;
	
	public static final String commandSuffix = "Command"; 
	private static final String commandSuffixRegex = "Command";
	
	public static final String implementationSuffix = "Impl";
	
	private static String objectRegex = "([A-Z][a-z0-9]*[[A-Z][a-z0-9]*]*?)" + "(" + collectionTypeRegex + ")?";
	private static String relatedObjectRegex = "[A-Z][a-z0-9]*[[A-Z][a-z0-9]*]*";

	// ex: getStudyByStudyId
	private static String routerFacadeMethodNameRegex = "^" +
		"(" + actionRegex + ")" + 
		"(" + objectRegex + ")" + 
		"(" + "(" + prepositionRegex + ")(" + relatedObjectRegex + "))?" +
		"$";
	protected static final int routerMethodActionIndex = 1;
	protected static final int routerMethodObjectIndex = 2;
	protected static final int routerMethodObjectSimpleTypeIndex = 3;
	protected static final int routerMethodObjectCollectionIndex = 4;
	protected static final int routerMethodPrepositionIndex = 6;
	protected static final int routerMethodObjectOfPrepositionIndex = 7;
	
	// ex: GetStudyByStudyIdCommand
	private static String commandClassNameRegex = "^" + 
		"(" + actionRegex + ")" + 
		"(" + objectRegex + ")" + 
		"(" + "(" + prepositionRegex + ")(" + relatedObjectRegex + "))?" + 
		commandSuffixRegex +
		"$";
	protected static final int commandClassNameIndex = 0;
	protected static final int commandClassActionIndex = 1;
	protected static final int commandClassObjectIndex = 2;
	protected static final int commandClassObjectSimpleTypeIndex = 3;
	protected static final int commandClassObjectCollectionIndex = 4;
	protected static final int commandClassPrepositionIndex = 6;
	protected static final int commandClassObjectOfPrepositionIndex = 7;

	// ex: createGetStudyByStudyIdCommand
	private static String commandFactoryMethodNameRegex = "^" + 
		commandFactoryMethodPrefixRegex +
		"(" + actionRegex + ")" + 
		"(" + objectRegex + ")" + 
		"(" + "(" + prepositionRegex + ")(" + relatedObjectRegex + "))?" + 
		commandSuffixRegex + 
		"$";
	
	protected static final int commandFactoryMethodActionIndex = 1;
	protected static final int commandFactoryMethodObjectIndex = 2;
	protected static final int commandFactoryMethodObjectSimpleTypeIndex = 3;
	protected static final int commandFactoryMethodObjectCollectionIndex = 4;
	protected static final int commandFactoryMethodPrepositionIndex = 6;
	protected static final int commandFactoryMethodObjectOfPrepositionIndex = 7;

	/**
	 * @return the regular expression that defines how a facade router method must be named
	 */
	protected static String getRouterMethodNameRegex()
	{
		return routerFacadeMethodNameRegex;
	}
	
	/**
	 * @return the regular expression that defines how command classes must be named
	 */
	protected static String getCommandClassNameRegex()
	{
		return commandClassNameRegex;
	}

	/**
	 * @return the regular expression that defines how command factory method must
	 * be named
	 */
	protected static String getCommandFactoryMethodNameRegex()
	{
		return commandFactoryMethodNameRegex;
	}
	
	// ===========================================================================================================
	// 
	// ===========================================================================================================
	private Action action;
	private String object;
	private String objectSimpleName;
	private String objectCollectionType;
	private Preposition preposition;
	private String objectOfPreposition;
	
	protected CoreRouterSemantics()
	{
		
	}
	
	/**
	 * @param action
	 * @param objectName
	 * @param preposition
	 * @param objectOfPreposition
	 */
	protected CoreRouterSemantics(
		Action action, 
		String objectName,
		Preposition preposition, 
		String objectOfPreposition)
	{
		setAction(action);
		setObject(objectName);
		setPreposition(preposition);
		setObjectOfPreposition( objectOfPreposition );
	}

	public Action getAction()
	{
		return action;
	}
	
	public String getObject()
	{
		return object;
	}
	
	protected String getObjectSimpleName()
	{
		return this.objectSimpleName;
	}
	
	protected String getObjectCollectionType()
	{
		return this.objectCollectionType;
	}
	
	public Preposition getPreposition()
	{
		return preposition;
	}
	
	public String getObjectOfPreposition()
	{
		return objectOfPreposition;
	}

	/**
	 * @param action the action to set
	 */
	protected void setAction(Action action)
	{
		this.action = action;
	}

	/**
	 * @param object the object to set
	 */
	protected void setObject(String object)
	{
		this.object = object;
	}

	/**
	 * @param objectSimpleName the objectSimpleName to set
	 */
	protected void setObjectSimpleName(String objectSimpleName)
	{
		this.objectSimpleName = objectSimpleName;
	}

	/**
	 * @param objectCollectionType the objectCollectionType to set
	 */
	protected void setObjectCollectionType(String objectCollectionType)
	{
		this.objectCollectionType = objectCollectionType;
	}

	/**
	 * @param preposition the preposition to set
	 */
	protected void setPreposition(Preposition preposition)
	{
		this.preposition = preposition;
	}

	/**
	 * @param objectOfPreposition the relatedObject to set
	 */
	protected void setObjectOfPreposition(String objectOfPreposition)
	{
		this.objectOfPreposition = objectOfPreposition;
	}

	/**
	 * Set all shared properties between core router semantic elements
	 * 
	 * @param coreRouterElement
	 */
	protected void setAll(CoreRouterSemantics coreRouterElement)
	{
		setAction(coreRouterElement.getAction());
		setObject(coreRouterElement.getObject());
		setObjectSimpleName(coreRouterElement.getObjectSimpleName());
		setPreposition(coreRouterElement.getPreposition());
		setObjectOfPreposition(coreRouterElement.getObjectOfPreposition());
	}
	
	/**
	 * 
	 * @param elementName
	 * @param matcher
	 * @throws CoreRouterSemanticsException
	 */
	protected void parseElement(String elementName)
	throws CoreRouterSemanticsException
	{
		Matcher matcher = getPattern().matcher(elementName);
		if(matcher.matches())
		{
			setAction( Action.getAction(matcher.group(getActionIndex())) );
			setObject( matcher.group(getObjectIndex()) );
			setObjectSimpleName( matcher.group(getObjectSimpleTypeIndex()) );
			setObjectCollectionType( matcher.group(getObjectCollectionIndex()) );
			
			if(getAction() == null)
				throw new CoreRouterSemanticsException("Element '" + elementName + "' did not include an action.");
			if(getObject() == null)
				throw new CoreRouterSemanticsException("Element '" + elementName + "' did not include an object.");
			
			setPreposition( Preposition.getPreposition(matcher.group(getPrepositionIndex())) );
			setObjectOfPreposition( matcher.group(getObjectOfPrepositionIndex()) );
			if(getPreposition() != null && getObjectOfPreposition() == null)
				throw new CoreRouterSemanticsException("Element '" + elementName + "' included a preposition clause with no related object.");
		}
		else
			throw new CoreRouterSemanticsException("'" + elementName + "' is not a valid router semantic element." + 
				"Element must match regular expression pattern '" + getPattern().pattern() + "'.");
	}

	protected abstract Pattern getPattern();
	protected abstract int getActionIndex();
	protected abstract int getObjectIndex();
	protected abstract int getObjectSimpleTypeIndex();
	protected abstract int getObjectCollectionIndex();
	protected abstract int getPrepositionIndex();
	protected abstract int getObjectOfPrepositionIndex();

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.action == null) ? 0 : this.action.hashCode());
		result = prime * result
				+ ((this.object == null) ? 0 : this.object.hashCode());
		result = prime
				* result
				+ ((this.objectCollectionType == null) ? 0
						: this.objectCollectionType.hashCode());
		result = prime
				* result
				+ ((this.objectSimpleName == null) ? 0 : this.objectSimpleName
						.hashCode());
		result = prime
				* result
				+ ((this.preposition == null) ? 0 : this.preposition.hashCode());
		result = prime
				* result
				+ ((this.objectOfPreposition == null) ? 0 : this.objectOfPreposition
						.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final CoreRouterSemantics other = (CoreRouterSemantics) obj;
		if (this.action == null)
		{
			if (other.action != null)
				return false;
		} else if (!this.action.equals(other.action))
			return false;
		if (this.object == null)
		{
			if (other.object != null)
				return false;
		} else if (!this.object.equals(other.object))
			return false;
		if (this.objectCollectionType == null)
		{
			if (other.objectCollectionType != null)
				return false;
		} else if (!this.objectCollectionType
				.equals(other.objectCollectionType))
			return false;
		if (this.objectSimpleName == null)
		{
			if (other.objectSimpleName != null)
				return false;
		} else if (!this.objectSimpleName.equals(other.objectSimpleName))
			return false;
		if (this.preposition == null)
		{
			if (other.preposition != null)
				return false;
		} else if (!this.preposition.equals(other.preposition))
			return false;
		if (this.objectOfPreposition == null)
		{
			if (other.objectOfPreposition != null)
				return false;
		} else if (!this.objectOfPreposition.equals(other.objectOfPreposition))
			return false;
		return true;
	}

	/**
	 * This method compares to instances of CoreRouterSemantics (or derivations)
	 * for equivalency in mapping.  
	 * Essentially this means that a router command can be mapped to a command type,
	 * which can be mapped to a command factory method.
	 * 
	 * examples:
	 * "getStudy" directly maps to "GetStudyCommand" and "createGetStudyCommand"
	 * "getStudyListByStudyFilter" directly maps to "GetStudyListByStudyFilterCommand" and "createGetStudyListByStudyFilterCommand"
	 * 
	 * @param other
	 * @return
	 */
	public boolean directMapping(final CoreRouterSemantics other)
	{
		if (this.action == null)
		{
			if (other.action != null)
				return false;
		} else if (!this.action.equals(other.action))
			return false;
		if (this.object == null)
		{
			if (other.object != null)
				return false;
		} else if (!this.object.equals(other.object))
			return false;
		if (this.objectCollectionType == null)
		{
			if (other.objectCollectionType != null)
				return false;
		} else if (!this.objectCollectionType
				.equals(other.objectCollectionType))
			return false;
		if (this.objectSimpleName == null)
		{
			if (other.objectSimpleName != null)
				return false;
		} else if (!this.objectSimpleName.equals(other.objectSimpleName))
			return false;
		if (this.preposition == null)
		{
			if (other.preposition != null)
				return false;
		} else if (!this.preposition.equals(other.preposition))
			return false;
		if (this.objectOfPreposition == null)
		{
			if (other.objectOfPreposition != null)
				return false;
		} else if (!this.objectOfPreposition.equals(other.objectOfPreposition))
			return false;
		return true;
	}
}
