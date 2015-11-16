/**
 * 
 */
package gov.va.med.imaging.core;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;

import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class CollectionTypeVocabularyTest 
extends TestCase
{

	/**
	 * Test method for {@link gov.va.med.imaging.core.CollectionTypeVocabulary#getSimpleName()}.
	 */
	public void testGetSimpleName()
	{
		assertEquals("List", CollectionTypeVocabulary.LIST.getSimpleName() );
		assertEquals("Set", CollectionTypeVocabulary.SET.getSimpleName() );
		assertEquals("Map", CollectionTypeVocabulary.MAP.getSimpleName() );
	}

	/**
	 * Test method for {@link gov.va.med.imaging.core.CollectionTypeVocabulary#findByObjectSuffix(java.lang.String)}.
	 */
	public void testFindByObjectSuffix()
	{
		assertEquals(CollectionTypeVocabulary.findByObjectSuffix("List"), CollectionTypeVocabulary.LIST );
		assertEquals(CollectionTypeVocabulary.findByObjectSuffix("Set"), CollectionTypeVocabulary.SET );
		assertEquals(CollectionTypeVocabulary.findByObjectSuffix("Map"), CollectionTypeVocabulary.MAP );
	}

	/**
	 * Test method for {@link gov.va.med.imaging.core.CollectionTypeVocabulary#find(javax.lang.model.element.Element)}.
	 */
	public void testFind()
	{
		assertEquals(CollectionTypeVocabulary.MAP, CollectionTypeVocabulary.find(new MockElement(java.util.Map.class.getName())) );
		assertEquals(CollectionTypeVocabulary.SET, CollectionTypeVocabulary.find(new MockElement(java.util.Set.class.getName())) );
		assertEquals(CollectionTypeVocabulary.LIST, CollectionTypeVocabulary.find(new MockElement(java.util.List.class.getName())) );
	}

	class MockElement 
	implements Element
	{
		private String elementText;
		
		MockElement(String elementText)
		{
			this.elementText = elementText;
		}
		
		@Override
		public <R, P> R accept(ElementVisitor<R, P> v, P p)
		{
			return null;
		}

		@Override
		public TypeMirror asType()
		{
			return null;
		}

		@Override
		public <A extends Annotation> A getAnnotation(
				Class<A> annotationType)
		{
			return null;
		}

		@Override
		public List<? extends AnnotationMirror> getAnnotationMirrors()
		{
			return null;
		}

		@Override
		public List<? extends Element> getEnclosedElements()
		{
			return null;
		}

		@Override
		public Element getEnclosingElement()
		{
			return null;
		}

		@Override
		public ElementKind getKind()
		{
			return null;
		}

		@Override
		public Set<Modifier> getModifiers()
		{
			return null;
		}

		@Override
		public Name getSimpleName()
		{
			int lastDot = elementText.lastIndexOf('.');
			if(lastDot >= 0)
				return new MockName(elementText.substring(lastDot));
			return new MockName(elementText);
		}

		public String toString()
		{
			return elementText;
		}
	};
	
	class MockName
	implements javax.lang.model.element.Name
	{
		private final CharSequence seq;
		MockName(CharSequence seq)
		{
			this.seq = seq;
		}
		@Override
		public boolean contentEquals(CharSequence cs)
		{
			return seq.equals(cs);
		}
		@Override
		public String toString()
		{
			return seq.toString();
		}
		@Override
		public char charAt(int index)
		{
			return seq.charAt(index);
		}
		@Override
		public int length()
		{
			return seq.length();
		}
		@Override
		public CharSequence subSequence(int start, int end)
		{
			return seq.subSequence(start, end);
		}
	}
}
