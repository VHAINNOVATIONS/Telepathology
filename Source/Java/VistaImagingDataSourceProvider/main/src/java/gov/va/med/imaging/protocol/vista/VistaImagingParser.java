/**
 * 
 */
package gov.va.med.imaging.protocol.vista;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Parse the lines read from a Vista into a hierarchy as defined by the tag
 * delimiter keys. The first line is skipped. Example: lines = 8 NEXT_STUDY||712
 * STUDY_IEN|712 STUDY_PAT|1011|9217103663V710366|IMAGPATIENT1011,1011
 * NEXT_SERIES SERIES_IEN|712 NEXT_IMAGE IMAGE_IEN|713
 * IMAGE_INFO|B2^713^\\isw-werfelj-lt\image1$\DM\00\07\DM000713.TGA^\\isw-werfelj-lt\image1$\DM\00\07\DM000713.ABS^040600-28
 * CHEST SINGLE
 * VIEW^3000406.1349^3^CR^04/06/2000^^M^A^^^1^1^SLC^^^1011^IMAGPATIENT1011,1011^CLIN^^^^
 * and ontologyDelimiterKeys = NEXT_STUDY, NEXT_SERIES, NEXT_IMAGE
 * 
 * would return a list: ParsedVistaLine with key="NEXT_STUDY" and values="",
 * "712" and properties: ParsedVistaLine with key="STUDY_IEN" and values="712"
 * ParsedVistaLine with key="STUDY_PAT" and values="1011", "9217103663V710366",
 * IMAGPATIENT1011,1011 and children: ParsedVistaLine with key="NEXT_SERIES" and
 * values= and properties: ParsedVistaLine with key="SERIES_IEN" and
 * values="712" and children: ParsedVistaLine with key="NEXT_IMAGE" and values=
 * and properties: ParsedVistaLine with key="IMAGE_IEN" and values="713"
 * ParsedVistaLine with key="IMAGE_INFO" and
 * values="B2^713^\\isw-werfelj-lt\image1$\DM\00\07\DM000713.TGA^\\isw-werfelj-lt\image1$\DM\00\07\DM000713.ABS^040600-28
 * CHEST SINGLE
 * VIEW^3000406.1349^3^CR^04/06/2000^^M^A^^^1^1^SLC^^^1011^IMAGPATIENT1011,1011^CLIN^^^^"
 * 
 * 
 * 
 * @param lines
 * @param delimiterKeys -
 *            A list of key values that define the start of each serialized
 *            object. The order of the delimiter keys IS SIGNIFICANT. Later,
 *            numerically higher index, delimiter keys are considered children
 *            of earlier keys. The returned List will consist entirely of
 *            ParsedVistaLine where the key is the first delimiterKeys member.
 *            Each of those ParsedVistaLine will have children with a key value
 *            of the second delimiterKeys member, etc ...
 * 
 * @return
 * @author vhaiswbeckec
 * 
 */
public class VistaImagingParser
{
	private OntologyDelimiterKey[] ontologyDelimiterKeys;
	private Logger logger = Logger.getLogger(this.getClass());
	
	VistaImagingParser(OntologyDelimiterKey[] ontologyDelimiterKeys)
	{
		this.ontologyDelimiterKeys = new OntologyDelimiterKey[ontologyDelimiterKeys.length];
		System.arraycopy(ontologyDelimiterKeys, 0, this.ontologyDelimiterKeys, 0, ontologyDelimiterKeys.length);
	}

	/**
	 * 
	 * @param lines
	 * @param discardUnknownRootLines
	 * @return
	 */
	public List<ParsedVistaLine> parse(String[] lines, boolean discardUnknownRootLines)
	{

		// parameter checking, the ontology delimiter keys must be provided
		if (ontologyDelimiterKeys == null || ontologyDelimiterKeys.length <= 0)
			throw new IllegalArgumentException("The delimiter keys must not be null or an empty array.");

		for (OntologyDelimiterKey ontologyDelimiterKey : ontologyDelimiterKeys)
			if (ontologyDelimiterKey == null || ontologyDelimiterKey.getKey() == null
				|| ontologyDelimiterKey.getKey().length() <= 0)
				throw new IllegalArgumentException(
					"All keys in the delimiter keys must not be null or an empty String.");

		List<ParsedVistaLine> parsedLines = new ArrayList<ParsedVistaLine>();
		// if no lines given then return an empty list
		if (lines == null || lines.length <= 1)
			return parsedLines;

		logger.info("Parsing " + lines.length + " lines using " + ontologyDelimiterKeys.length + " keys.");

		// An array of ParsedVistaLine instances that define the current path
		// in the resulting hierarchy. New instances of ParsedVistaLine, as they
		// are parsed, are added to the current path, unless they are delimiter
		// keys, in which case they change the current path.
		ParsedVistaLine[] currentHierarchyPath = new ParsedVistaLine[ontologyDelimiterKeys.length];
		for (int index = 0; index < currentHierarchyPath.length; ++index)
			currentHierarchyPath[index] = null;
		// the depth into the hierarchy that we are currently adding to
		// this is also the index into currentHierarchyPath of the "active"
		// element
		int currentHierarchyDepth = 0;

		for (String line : lines)
		{
			if (line == null) // should never happen, but ...
				continue;
			line = line.trim(); // remove trailing whitespace

			logger.debug("Parsing line '" + line + "'.");

			// given a line like "NEXT_STUDY||712" get the parts "NEXT_STUDY",
			// "", "712"
			String parts[] = line.split("\\|", -1); // StringUtils.Split(line,
													// StringUtils.STICK);
			String key = parts[0].trim();
			String[] values = parts.length > 1 ? new String[parts.length - 1] : null;
			if (values != null)
				System.arraycopy(parts, 1, values, 0, parts.length - 1);
			// at this point the example line should end up like:
			// key = "NEXT_STUDY"
			// values = "", "712"

			// simply see if the key is an ontology delimiter, its he index value we'll be looking at later
			int ontologyKeyIndex;
			for (ontologyKeyIndex = 0; ontologyKeyIndex < ontologyDelimiterKeys.length; ++ontologyKeyIndex)
				if (ontologyDelimiterKeys[ontologyKeyIndex].getKey().equals(key))
					break;
			
			int parentKeyIndex = -1;
			if (ontologyKeyIndex >= ontologyDelimiterKeys.length)
			{
				ontologyKeyIndex = -1;
				// if the current key is not a ontology key, check to see if the
				// current key
				// is a exceptional key for an ontology. if so we will add this
				// property to that key
				for (parentKeyIndex = ontologyDelimiterKeys.length - 1; parentKeyIndex >= 0; --parentKeyIndex)
				{
					if (ontologyDelimiterKeys[parentKeyIndex].containsExceptionalKey(key))
					{
						break;
					}
				}
			}

			// if the new line is a root line
			if (ontologyKeyIndex == 0)
			{
				logger.debug("Root line detected '" + line + "'.");

				ParsedVistaLine root = new ParsedVistaLine(key, values);
				parsedLines.add(root);
				currentHierarchyDepth = 0;
				currentHierarchyPath[currentHierarchyDepth] = root;
			}

			// if the new line is just a property
			else if (ontologyKeyIndex == -1)
			{
				logger.debug("Property line detected '" + line + "'.");
				ParsedVistaLine parent = null;
				// if the parent key index is not -1, then there is a parent key
				// index to use, not the current one
				if (parentKeyIndex >= 0)
				{
					parent = currentHierarchyPath[parentKeyIndex];
				}
				else
				{
					parent = currentHierarchyPath[currentHierarchyDepth];
				}
				if (parent != null)
					parent.addProperty(key, values);
				else
				{
					if (discardUnknownRootLines)
						logger.warn("There is no current line to add unknown line '" + line + "' to, discarding.");
					else
						parsedLines.add(new ParsedVistaLine(key, values));
				}
			}

			// if the key is a delimiter at the current hierarchy level
			// and it is not the root level (which we addressed in the
			// preceeding 'if' block)
			// create a new ParsedVistaLine as a sibling of the current
			// and make the new element the currently active element
			else if (ontologyKeyIndex == currentHierarchyDepth)
			{
				logger.debug("Sibling delimiter line detected '" + line + "'.");

				ParsedVistaLine parent = currentHierarchyPath[ontologyKeyIndex - 1];
				currentHierarchyPath[ontologyKeyIndex] = parent.addChild(key, values);
			}

			// if the key is a delimiter at the child hiererchy level
			// create a new ParsedVistaLine as a child of the current
			// make the new element the currently active element
			// set the current depth to the new deeper level
			else if (ontologyKeyIndex > currentHierarchyDepth)
			{
				logger.debug("Descendent delimiter line detected '" + line + "'.");

				ParsedVistaLine parent = currentHierarchyPath[currentHierarchyDepth];
				currentHierarchyPath[ontologyKeyIndex] = parent.addChild(key, values);
				currentHierarchyDepth = ontologyKeyIndex;
			}

			// if the key is a delimiter at an ancestor hiererchy level
			// create a new ParsedVistaLine
			// make the new element the currently active element
			// set the current depth to the new shallower level
			else if (ontologyKeyIndex < currentHierarchyDepth)
			{
				logger.debug("Ancestor delimiter line detected '" + line + "'.");

				ParsedVistaLine parent = currentHierarchyPath[ontologyKeyIndex - 1];
				currentHierarchyPath[ontologyKeyIndex] = parent.addChild(key, values);
				currentHierarchyDepth = ontologyKeyIndex;
			}
		}

		return parsedLines;
	}

	/**
	 * A class that contains parsed lines from Vista with no semantics applied,
	 * other than recognizing "NEXT_XXX" as a start delimiter.
	 * 
	 * @author VHAISWBECKEC
	 * 
	 */
	public static class ParsedVistaLine
	{
		private Logger logger = Logger.getLogger(this.getClass());
		
		private final String key;
		private final List<String> values;
		private final List<ParsedVistaLine> properties;
		private final List<ParsedVistaLine> children;

		ParsedVistaLine(String key, String[] values)
		{
			this.key = key;
			this.values = new ArrayList<String>();
			if (values != null)
				for (String value : values)
					this.values.add(value);
			this.properties = new ArrayList<ParsedVistaLine>();
			this.children = new ArrayList<ParsedVistaLine>();
		}

		/**
		 * A Child is a line whose type indicates an inferior position in the
		 * defined hierarchy.
		 * 
		 * @param key
		 * @param values
		 * @return
		 */
		ParsedVistaLine addChild(String key, String[] values)
		{
			ParsedVistaLine child = new ParsedVistaLine(key, values);
			children.add(child);

			return child;
		}

		/**
		 * A Property is a line whose type indicates an equivalent position in
		 * the defined hierarchy.
		 * 
		 * @param key
		 * @param values
		 * @return
		 */
		ParsedVistaLine addProperty(String key, String[] values)
		{
			ParsedVistaLine property = new ParsedVistaLine(key, values);
			properties.add(property);

			return property;
		}

		String getKey()
		{
			return key;
		}

		Iterator<String> valuesIterator()
		{
			return values.iterator();
		}

		Iterator<ParsedVistaLine> propertyIterator()
		{
			return properties.iterator();
		}

		Iterator<ParsedVistaLine> childIterator()
		{
			return children.iterator();
		}

		String getValueAtIndex(int index)
		{
			return values.get(index);
		}

		boolean isValueAtIndexExists(int index)
		{
			if (index < 0)
				return false;
			if (index >= values.size())
				return false;
			return true;
		}

		ParsedVistaLine getPropertyAtIndex(int index)
		{
			return properties.get(index);
		}

		ParsedVistaLine getChildAtIndex(int index)
		{
			return children.get(index);
		}

		ParsedVistaLine getProperty(String key)
		{
			for (Iterator<ParsedVistaLine> iter = propertyIterator(); iter.hasNext();)
			{
				ParsedVistaLine property = iter.next();
				if (key.equals(property.getKey()))
					return property;
			}

			return null;
		}

		/**
		 * The keys must match and the properties and children must also be
		 * equals and in the same order.
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			if (obj instanceof ParsedVistaLine)
			{
				ParsedVistaLine that = (ParsedVistaLine) obj;

				if (this.getKey() == null && that.getKey() != null || this.getKey() != null && that.getKey() == null
					|| !this.getKey().equals(that.getKey()))
				{
					logger.info("this.key '" + this.getKey() + "' is not equal to that.key '" + that.getKey() + "'.");
					return false;
				}

				if (this.values.size() != that.values.size())
				{
					logger.info("this.values.size '" + this.values.size() + "' is not equal to that.values.size '"
						+ that.values.size() + "'.");
					return false;
				}

				Iterator<String> thisValueIterator = this.valuesIterator();
				Iterator<String> thatValueIterator = that.valuesIterator();
				for (; thisValueIterator.hasNext() && thatValueIterator.hasNext();)
				{
					String thisValue = thisValueIterator.next();
					String thatValue = thatValueIterator.next();
					if (!thisValue.equals(thatValue))
					{
						logger.info("this(" + this.getKey() + ").value '" + thisValue + "' is not equal to that("
							+ that.getKey() + ").value '" + thatValue + "'.");
						return false;
					}
				}

				if (this.properties.size() != that.properties.size())
				{
					logger.info("this.properties.size '" + this.properties.size()
						+ "' is not equal to that.properties.size '" + that.properties.size() + "'.");
					return false;
				}

				Iterator<ParsedVistaLine> thisPropertyIterator = this.propertyIterator();
				Iterator<ParsedVistaLine> thatPropertyIterator = that.propertyIterator();
				for (; thisPropertyIterator.hasNext() && thatPropertyIterator.hasNext();)
				{
					ParsedVistaLine thisProperty = thisPropertyIterator.next();
					ParsedVistaLine thatProperty = thatPropertyIterator.next();
					if (!thisProperty.equals(thatProperty))
						return false;
				}

				if (this.children.size() != that.children.size())
				{
					logger.info("this.children.size '" + this.children.size()
						+ "' is not equal to that.children.size '" + that.children.size() + "'.");
					return false;
				}

				Iterator<ParsedVistaLine> thisChildIterator = this.childIterator();
				Iterator<ParsedVistaLine> thatChildIterator = that.childIterator();
				for (; thisChildIterator.hasNext() && thatChildIterator.hasNext();)
				{
					ParsedVistaLine thisChild = thisChildIterator.next();
					ParsedVistaLine thatChild = thatChildIterator.next();
					if (!thisChild.equals(thatChild))
						return false;
				}

				return true;
			}

			return false;
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();

			sb.append("ParsedVistaLine \r");

			sb.append("\tKey :'");
			sb.append(key);
			sb.append("'\r");

			sb.append("\tValues :'");
			for (String value : values)
				sb.append(value + " ");
			sb.append("'\r");

			return sb.toString();
		}
	}

	/**
	 * Represents an ontology key in the study, series, instance hierearchy
	 * 
	 * @author vhaiswwerfej
	 * 
	 */
	static class OntologyDelimiterKey
	{
		private final String key;
		private final String[] exceptionalKeys;

		/**
		 * Create an ontology delimiter key with no exceptional keys
		 * 
		 * @param key
		 */
		OntologyDelimiterKey(String key)
		{
			this.key = key;
			exceptionalKeys = null;
		}

		/**
		 * Create an ontology delimiter key with exceptional keys
		 * 
		 * @param key
		 * @param exceptionalKeys
		 */
		OntologyDelimiterKey(String key, String[] exceptionalKeys)
		{
			this.key = key;
			this.exceptionalKeys = exceptionalKeys;
		}

		/**
		 * @return the key
		 */
		public String getKey()
		{
			return key;
		}

		/**
		 * @return the exceptionalKeys
		 */
		public String[] getExceptionalKeys()
		{
			return exceptionalKeys;
		}

		public boolean containsExceptionalKey(String searchExceptionalKey)
		{
			if (exceptionalKeys == null)
				return false;
			if (searchExceptionalKey == null)
				return false;
			for (String exceptionalKey : exceptionalKeys)
			{
				if (exceptionalKey.equals(searchExceptionalKey))
					return true;
			}
			return false;
		}
	}
}
