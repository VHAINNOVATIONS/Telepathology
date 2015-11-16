/**
 * 
 */
package gov.va.med.imaging.core;

import javax.lang.model.element.Element;

/**
 * This class defines the limited set of collection types understood as part of a command name.
 * 
 * @author vhaiswbeckec
 *
 */
public enum CollectionTypeVocabulary
{
	LIST(java.util.List.class, 1),
	MAP(java.util.Map.class, 2),
	SET(java.util.Set.class, 1);
	
	private final Class<?> collectionClass;
	private final int typeArgumentsCount;
	CollectionTypeVocabulary(Class<?> collectionClass, int typeArgumentsCount)
	{
		this.collectionClass = collectionClass;
		this.typeArgumentsCount = typeArgumentsCount;
	}
	
	/**
	 * @return the collectionClass
	 */
	public Class<?> getCollectionClass()
	{
		return this.collectionClass;
	}
	
	public String getSimpleName()
	{
		return getCollectionClass().getSimpleName();
	}
	
	public int getTypeArgumentsCount()
	{
		return this.typeArgumentsCount;
	}

	/**
	 * 
	 * @param objectSuffix
	 * @return
	 */
	public static CollectionTypeVocabulary findByObjectSuffix(String objectSuffix)
	{
		if( objectSuffix == null || objectSuffix.length() == 0 )
			return null;
		
		for(CollectionTypeVocabulary collectionType: values())
			if(collectionType.getSimpleName().equals(objectSuffix))
				return collectionType;
		
		return null;
	}
	
	/**
	 * 
	 * @param modelElement
	 * @return
	 */
	public static CollectionTypeVocabulary find(Element modelElement)
	{
		if( modelElement == null )
			return null;
		
		String modelElementText = modelElement.toString();
		for(CollectionTypeVocabulary collectionType : values())
		{
			String collectionClassName = collectionType.getCollectionClass().getName();
			if(modelElementText.equals(collectionClassName) || modelElementText.equals(collectionType.getSimpleName()))
				return collectionType;
		}
		
		return null;
	}

}
