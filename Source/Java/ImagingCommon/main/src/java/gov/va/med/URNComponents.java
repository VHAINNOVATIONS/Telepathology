package gov.va.med;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.utility.Base32ConversionUtility;

/**
 * A simple value object containing the namespace identifier and the
 * namespace specific string of a URN after parsing.
 * 
 */
public class URNComponents
{
	private final static OctetSequenceEscaping rfc2141EscapeEngine = 
		OctetSequenceEscaping.createRFC2141EscapeEngine();
	private final static OctetSequenceEscaping rfc2141ToFilenameEscapeEngine = 
		OctetSequenceEscaping.createRFC2141toFilenameLegalEscapeEngine();
	private final String schema;
	private final NamespaceIdentifier namespaceIdentifier;
	private String namespaceSpecificString;
	private String[] additionalIdentifers;
	
	public static URNComponents create(
		NamespaceIdentifier namespaceIdentifier,
		String namespaceSpecificString) 
	throws URNFormatException
	{
		return create(URN.urnSchemaIdentifier, namespaceIdentifier, namespaceSpecificString);
	}
	
	public static URNComponents create(
		String schema,
		NamespaceIdentifier namespaceIdentifier,
		String namespaceSpecificString)
	{
		return create(schema, namespaceIdentifier, namespaceSpecificString);
	}
	
	public static URNComponents create(	
		String schema,
		NamespaceIdentifier namespaceIdentifier,
		String namespaceSpecificString,
		String... additionalIdentifiers) 
	throws URNFormatException
	{
		if( URN.urnSchemaIdentifier.equalsIgnoreCase(namespaceIdentifier.toString()) )
			throw new URNFormatException("The namespace identifier '" + namespaceIdentifier.toString() + "' is not permitted.");

		// restore any filename illegal characters that are RFC2141 legal
		namespaceSpecificString = rfc2141ToFilenameEscapeEngine.unescapeIllegalCharacters(namespaceSpecificString);
		// unescape any characters that are not RFC2141 legal
		namespaceSpecificString = rfc2141EscapeEngine.escapeIllegalCharacters(namespaceSpecificString);
		
		if(additionalIdentifiers != null && additionalIdentifiers.length > 0)
			for(int index=0; index < additionalIdentifiers.length; ++index)
				additionalIdentifiers[index] = rfc2141EscapeEngine.escapeIllegalCharacters(additionalIdentifiers[index]);
		
		return new URNComponents(schema, namespaceIdentifier, namespaceSpecificString, additionalIdentifiers);
	}
	
	/**
	 * 
	 * @param urnAsString
	 * @return
	 * @throws URNFormatException
	 */
	public static URNComponents parse(String urnAsString)
	throws URNFormatException
	{
		return parse(urnAsString, null);
	}

	/**
	 * If the serializationFormat parameter is not null then unescaping will be applied
	 * after parsing the URN components but before parsing the namespace specific string.
	 * The namespace identifier and the scheme are always assumed to be unescaped and the 
	 * delimiters (':') are similarly not escaped .
	 *   
	 * @param urnAsString
	 * @param serializationFormat
	 * @return
	 * @throws URNFormatException
	 */
	public static URNComponents parse(String urnAsString, SERIALIZATION_FORMAT serializationFormat)
	throws URNFormatException
	{
		try
		{
			Matcher matcher = URN.urnPattern.matcher(urnAsString);
			
			if(! matcher.matches() )
				throw new URNFormatException(
					"'" + urnAsString + "' is not a valid URN, does not match required pattern.  " + 
					"Valid format is 'urn:<namespace>:<namespace-specific-string>[additional-identifiers]...'.");

			// if the URN components are serialized using VFTP then unescape the NSS before continuing 
			List<String> additionalIdentifierList = new ArrayList<String>();
			String nss = null;
			// If the serialization format has been specified and if it is flagged as "atomically escaped"
			// then it encodes the ENTIRE NSS, which must be decoded before further parsing.
			// RAW format is an exception in that its encoding is null, so the form presented here is
			// parsed already.
			if(serializationFormat != null && serializationFormat.isNssAtomicallyEscaped())
			{
				nss = serializationFormat.deserialize( matcher.group(URN.urnNssComponentGroup) );
				Matcher nssMatcher = URN.EXTENDED_NSS_PATTERN.matcher(nss);
				
				if(! nssMatcher.matches() )
					throw new URNFormatException(
						"'" + nss + "' is not a valid namespace specific string, does not match required pattern.");
				
				for(int groupIndex=URN.urnXnssAdditionalIdentifierComponentGroup1; 
					groupIndex <= nssMatcher.groupCount() && groupIndex<URN.urnXnssAdditionalIdentifierComponentGroup5;
					++groupIndex)
				{
					String additionalIdentifier = nssMatcher.group(groupIndex);
					if(additionalIdentifier != null && additionalIdentifier.length() > 0)
						additionalIdentifierList.add(additionalIdentifier);
				}
				nss = nssMatcher.group(URN.urnXnssComponentGroup);
			}
			else
			{
				nss = matcher.group(URN.urnNssComponentGroup); 
				for(int groupIndex=URN.urnAdditionalIdentifierComponentGroup1; 
					groupIndex <= matcher.groupCount() && groupIndex<URN.urnAdditionalIdentifierComponentGroup5;
					++groupIndex)
				{
					String additionalIdentifier = matcher.group(groupIndex);
					if(additionalIdentifier != null && additionalIdentifier.length() > 0)
						additionalIdentifierList.add(additionalIdentifier);
				}
			}
			
			String[] additionalIdentifiers = null;
			if(additionalIdentifierList.size() > 0)
			{
				additionalIdentifiers = new String[additionalIdentifierList.size()];
				additionalIdentifiers = additionalIdentifierList.toArray(additionalIdentifiers);
			}
			
			return URNComponents.create(
				URN.urnSchemaIdentifier, 
				new NamespaceIdentifier(matcher.group(URN.urnNamespaceComponentGroup)), 
				nss,
				additionalIdentifiers
			); 
		}
		catch (Throwable x)
		{
			throw new URNFormatException(
				"'" + urnAsString + "' is not a valid URN.  " +
				"Valid format is 'urn:namespace:namespace-specific-string'.  " + 
				x.getMessage());
		}
	}
	/**
	 * 
	 * @param schema
	 * @param namespaceIdentifier
	 * @param namespaceSpecificString
	 * @param additionalIdentifiers
	 */
	private URNComponents(
		String schema,
		NamespaceIdentifier namespaceIdentifier,
		String namespaceSpecificString,
		String... additionalIdentifiers)
	{
		super();
		this.schema = schema;
		this.namespaceIdentifier = namespaceIdentifier;
		this.namespaceSpecificString = namespaceSpecificString;
		this.additionalIdentifers = additionalIdentifiers;
	}
	
	/**
	 * If the entire NSS is encoded as Base32 then this method may be used to
	 * decode the NSS and re-parse the additional identifiers from it.
	 * The BHIE Image and Study URNs encode the entire NSS so that the additional
	 * identifiers are kept.
	 * 
	 * @throws URNFormatException 
	 */
	public void decodeNamespaceSpecificStringAsBase32() 
	throws URNFormatException
	{
		String decodedNss = Base32ConversionUtility.base32Decode(this.namespaceSpecificString);
		Matcher matcher = URN.EXTENDED_NSS_PATTERN.matcher(decodedNss);
		if(! matcher.matches())
			throw new URNFormatException("The namespace specific string '" + this.namespaceSpecificString + "' is not in the format '" + URN.EXTENDED_NSS_PATTERN.pattern() + "'.");
		
		this.namespaceSpecificString = matcher.group(URN.urnXnssComponentGroup);
		List<String> additionalIdentifierList = new ArrayList<String>();
		for(int groupIndex=URN.urnXnssAdditionalIdentifierComponentGroup1; 
			groupIndex <= matcher.groupCount() && groupIndex<URN.urnXnssAdditionalIdentifierComponentGroup5;
			++groupIndex)
		{
			String additionalIdentifier = matcher.group(groupIndex);
			if(additionalIdentifier != null && additionalIdentifier.length() > 0)
				additionalIdentifierList.add(additionalIdentifier);
		}
		
		this.additionalIdentifers = null;
		if(additionalIdentifierList.size() > 0)
			this.additionalIdentifers = additionalIdentifierList.toArray(new String[additionalIdentifierList.size()]);
	}
	
	public String getSchema()
	{
		return this.schema;
	}

	public NamespaceIdentifier getNamespaceIdentifier()
	{
		return this.namespaceIdentifier;
	}
	
	public String getNamespaceSpecificString()
	{
		return this.namespaceSpecificString;
	}

	public String[] getAdditionalIdentifers()
	{
		return this.additionalIdentifers;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(getSchema());
		sb.append(":");
		sb.append(getNamespaceIdentifier());
		sb.append(":");
		sb.append(getNamespaceSpecificString());
		
		if(getAdditionalIdentifers() != null)
			for(String additionalIdentifier : getAdditionalIdentifers())
			{
				sb.append(URN.appendedIdentifierStartDelimiter);
				sb.append(additionalIdentifier);
				sb.append(URN.appendedIdentifierEndDelimiter);
			}
		return sb.toString();
	}
}