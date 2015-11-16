/**
 * 
 */
package gov.va.med.imaging.terminology;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * An (extensible) enumeration of well known classification schemes.
 * 
 * @author vhaiswbeckec
 *
 */
public class CodingScheme
{
	public static final CodingScheme LOINC;
	public static final CodingScheme SNOMED; 
	public static final CodingScheme HL7;
	public static final CodingScheme MHS;
	public static final CodingScheme CONFIDENTIALITY_CODE; 
	public static final CodingScheme VAPROCEDURE; 
	public static final CodingScheme VASPECIALTY;
	public static final CodingScheme VACONFIDENTIALITY; 
	public static final CodingScheme VADOCUMENTCLASS; 
	public static final CodingScheme VAFACILITYTYPE;
	public static final CodingScheme VAPRACTICESETTING; 
	public static final CodingScheme MIME;

	public final static List<CodingScheme> knownCodingSchemes;
	
	static
	{
		knownCodingSchemes = new ArrayList<CodingScheme>();
		
		try
		{
			LOINC = new CodingScheme( "LOINC", new URI("urn:oid:2.16.840.1.113883.6.1"), new URI("2.16.840.1.113883.6.1"), new URI("LOINC") ); 
			knownCodingSchemes.add(LOINC);
			SNOMED = new CodingScheme( "SNOMED", new URI("urn:oid:2.16.840.1.113883.6.96"), new URI("2.16.840.1.113883.6.96"), new URI("SNOMED") );
			knownCodingSchemes.add(SNOMED);
			HL7 = new CodingScheme( "HL7", new URI("urn:oid:2.16.840.1.113883.11.19465"), new URI("2.16.840.1.113883.11.19465"), new URI("HL7") );
			knownCodingSchemes.add(HL7);
			MHS = new CodingScheme( "MHS", new URI("urn:oid:2.16.840.1.113883.3.42.10012.100001.205"), new URI("2.16.840.1.113883.3.42.10012.100001.205"), new URI("MHS") );
			knownCodingSchemes.add(MHS);
			// HL7 defined classification scheme
			CONFIDENTIALITY_CODE = new CodingScheme( "CONFIDENTIALITY_CODE", new URI("urn:oid:2.16.840.1.113883.5.25"), new URI("2.16.840.1.113883.5.25"), new URI("CONFIDENTIALITY_CODE") );
			knownCodingSchemes.add(CONFIDENTIALITY_CODE);
			VAPROCEDURE = new CodingScheme( "VAPROCEDURE", new URI("VAPROCEDURE") );
			knownCodingSchemes.add(VAPROCEDURE);
			VASPECIALTY = new CodingScheme( "VASPECIALTY", new URI("VASPECIALTY") ); 
			knownCodingSchemes.add(VASPECIALTY); 
			VACONFIDENTIALITY = new CodingScheme( "VACONFIDENTIALITY", new URI("VACONFIDENTIALITY") ); 
			knownCodingSchemes.add(VACONFIDENTIALITY); 
			VADOCUMENTCLASS = new CodingScheme( "VADOCUMENTCLASS", new URI("VADOCUMENTCLASS") );
			knownCodingSchemes.add(VADOCUMENTCLASS);
			VAFACILITYTYPE = new CodingScheme( "VAFACILITYTYPE", new URI("VAFACILITYTYPE") );
			knownCodingSchemes.add(VAFACILITYTYPE);
			VAPRACTICESETTING = new CodingScheme( "VAPRACTICESETTING", new URI("VAPRACTICESETTING") );
			knownCodingSchemes.add(VAPRACTICESETTING);
			MIME = new CodingScheme( "MIME", new URI("MIME") );
			knownCodingSchemes.add(MIME);
		}
		catch (URISyntaxException x)
		{
			throw new java.lang.ExceptionInInitializerError("Unable to initialize a known coding scheme [" + x.getMessage() + "]");
		}
	}
	
	/**
	 * Find a known CodingScheme by its name.  The matching is
	 * case-sensitive.
	 * 
	 * @param identifier
	 * @return
	 * @throws URISyntaxException 
	 */
	public static CodingScheme valueOf(String name) 
	{
		return valueOf(name, true);
	}
	
	/**
	 * Find a known CodingScheme by its name, specifying whether the
	 * matching is case sensitive or not.
	 * 
	 * @param name
	 * @param caseSensitive
	 * @return
	 */
	public static CodingScheme valueOf(String name, boolean caseSensitive) 
	{
		if(name == null)
			return null;
		
		for( Iterator<CodingScheme> iter = iterator(); iter.hasNext(); )
		{
			CodingScheme codingScheme = iter.next();
			if( caseSensitive && name.equals(codingScheme.getName()) ||
				!caseSensitive && name.equalsIgnoreCase(codingScheme.getName()) )
					return codingScheme;
		}
		
		return null;
	}

	/**
	 * Find a known coding scheme by an identifying URI 
	 * @param identifier
	 * @return
	 * @throws URISyntaxException
	 */
	public static CodingScheme valueOf(URI identifier) 
	{
		if(identifier == null)
			return null;
		
		for( Iterator<CodingScheme> iter = iterator(); iter.hasNext(); )
		{
			CodingScheme codingScheme = iter.next();
			if( codingScheme.identifierMatches(identifier) )
				return codingScheme;
		}
		
		return null;
	}
	
	/**
	 * Create an iterator that does not allow any modification to the underlying list or elements
	 */
	public static Iterator<CodingScheme> iterator()
	{
		return new Iterator<CodingScheme>()
		{
			private Iterator<CodingScheme> wrappedIterator = knownCodingSchemes.iterator();
			
			@Override
			public boolean hasNext(){return wrappedIterator.hasNext();}

			@Override
			public CodingScheme next(){return wrappedIterator.next();}

			@Override
			public void remove(){}
		};
	}
	
	/**
	 * Add a new CodingScheme if it is completely unique, meaning that the name
	 * and the canonical URI and the aliases are not in the known list already.
	 * 
	 * @param name
	 * @param canonicalURI
	 * @param aliases
	 */
	public static void add(String name, URI canonicalURI, URI ... aliases)
	{
		if(name == null || canonicalURI == null)
			return;
		
		synchronized(knownCodingSchemes)
		{
			if( valueOf(name) != null)
			{
				Logger.getLogger(CodingScheme.class).warn("Unable to add new CodingScheme because the name '" + name + "' is already known.");
				return;
			}
			if(valueOf(canonicalURI) != null)
			{
				Logger.getLogger(CodingScheme.class).warn("Unable to add new CodingScheme because the canonical URI '" + canonicalURI + "' is already known.");
				return;
			}
			if(aliases != null)
				for(URI alias : aliases)
					if(valueOf(alias) != null)
					{
						Logger.getLogger(CodingScheme.class).warn("Unable to add new CodingScheme because the alias URI '" + alias + "' is already known.");
						return;
					}
			
			knownCodingSchemes.add(new CodingScheme(name, canonicalURI, aliases));
		}
	}
	
	/**
	 * @param sourceCodeSchemeIdentifier
	 * @return
	 * @throws URISyntaxException 
	 */
	public static CodingScheme addUnknownCodingScheme(String sourceCodeSchemeIdentifier) throws URISyntaxException
	{
		synchronized(knownCodingSchemes)
		{
			CodingScheme codingScheme = new CodingScheme(sourceCodeSchemeIdentifier, new URI(sourceCodeSchemeIdentifier));
			
			knownCodingSchemes.add(codingScheme);
			return codingScheme;
		}
	}

	// ==============================================================================================
	// Instance Members
	// ==============================================================================================
	private final String name;
	private final URI canonicalIdentifier;
	private final URI[] aliasIdentifiers;
	
	/**
	 * 
	 * @param name
	 * @param additionalIdentifiers
	 */
	private CodingScheme(String name, URI canonicalIdentifier, URI ... additionalIdentifiers) 
	{
		this.name = name;
		this.canonicalIdentifier = canonicalIdentifier;
		this.aliasIdentifiers = new URI[additionalIdentifiers.length];
		int index = 0;
		for(URI identifier : additionalIdentifiers)
			this.aliasIdentifiers[index++] = identifier;
	}
	
	public String getName()
	{
		return this.name;
	}

	public URI getCanonicalIdentifier()
	{
		return this.canonicalIdentifier;
	}
	
	public URI[] getAliasIdentifiers()
	{
		return this.aliasIdentifiers;
	}

	/**
	 * If the given identifier is either the canonical identifier
	 * or is an alias then return true.
	 * 
	 * @param identifier
	 * @return
	 */
	public boolean identifierMatches(URI identifier)
	{
		if(identifier == null)
			return false;
		
		if( identifier.equals(this.getCanonicalIdentifier()) )
			return true;
		
		for(URI schemeIdentifier : this.getAliasIdentifiers())
			if(identifier.equals(schemeIdentifier))
				return true;
		
		return false;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final CodingScheme other = (CodingScheme) obj;
		if (this.name == null)
		{
			if (other.name != null)
				return false;
		}
		else if (!this.name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.getClass().getSimpleName());
		sb.append('(');
		sb.append(this.getName());
		sb.append(',');
		sb.append(this.getCanonicalIdentifier().toString());
		if(this.getAliasIdentifiers() != null && this.getAliasIdentifiers().length > 0)
		{
			sb.append('[');
			boolean firstAlias = true;
			for(URI alias : getAliasIdentifiers())
			{
				if(!firstAlias)
					sb.append(',');
				sb.append(alias.toString());
				firstAlias = false;
			}
			sb.append(']');
		}
		sb.append(')');
		
		return sb.toString();
	}

}
