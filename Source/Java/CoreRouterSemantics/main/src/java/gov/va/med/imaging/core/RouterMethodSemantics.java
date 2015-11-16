/**
 * 
 */
package gov.va.med.imaging.core;

import java.util.regex.Pattern;

/**
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
 * The action MUST be one of POST, GET, PUT, DELETE, READ, UPDATE
 * The object MUST be the simple name of a business object (a collection type may be concatenated)
 * The preposition MUST be one of BY, LIKE
 * The related_object MUST be either the simple name of a business object or the simple name of a core Java class
 * 
 * Router methods that pre-date the semantic router are mapped using a hard-coded mapping to 
 * the "correct" names outside of this package.
 * 
 * @author vhaiswbeckec
 *
 */
public class RouterMethodSemantics
extends CoreRouterSemantics
{
	private static Pattern methodNamePattern = Pattern.compile(CoreRouterSemantics.getRouterMethodNameRegex()); 
	
	/**
	 * A factory method only because the constructor has to throw exceptions.
	 * 
	 * @param methodName
	 * @return
	 * @throws CoreRouterSemanticsException
	 */
	public static RouterMethodSemantics create(String methodName) 
	throws CoreRouterSemanticsException
	{
		return new RouterMethodSemantics(methodName);
	}
	/**
	 * 
	 * @param elementName
	 */
	private RouterMethodSemantics(String elementName)
	throws CoreRouterSemanticsException
	{
		parseElement( elementName );
	}
	
	public static RouterMethodSemantics transformFrom(CoreRouterSemantics routerMethod)
	{
		return new RouterMethodSemantics(routerMethod);
	}
	
	private RouterMethodSemantics(CoreRouterSemantics coreRouterElement)
	{
		setAll(coreRouterElement);
	}

	public Pattern getPattern()
	{
		return methodNamePattern;
	}

	protected int getActionIndex(){return CoreRouterSemantics.routerMethodActionIndex;}
	protected int getObjectIndex(){return CoreRouterSemantics.routerMethodObjectIndex;}
	protected int getObjectSimpleTypeIndex(){return CoreRouterSemantics.routerMethodObjectSimpleTypeIndex;}
	protected int getObjectCollectionIndex(){return CoreRouterSemantics.routerMethodObjectCollectionIndex;}
	protected int getPrepositionIndex(){return CoreRouterSemantics.routerMethodPrepositionIndex;}
	protected int getObjectOfPrepositionIndex(){return CoreRouterSemantics.routerMethodObjectOfPrepositionIndex;}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.getAction().toString().toLowerCase());
		sb.append(this.getObject());
		if(this.getPreposition() != null)
			sb.append(this.getPreposition().toStringFirstCharUpperCase());
		if(this.getObjectOfPreposition() != null)
			sb.append(this.getObjectOfPreposition());
		
		if(sb.length() > 0)
			return RouterSemanticsUtility.standardizeToMethodName(sb.toString());
		else
			return "";
	}
}
