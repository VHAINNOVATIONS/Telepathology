/**
 * 
 */
package gov.va.med;

import gov.va.med.imaging.exceptions.OIDFormatException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * An object identifier implementation consistent with RFC3061
 * 
 * @see http://www.ietf.org/rfc/rfc3061.txt
 * @author vhaiswbeckec
 *
 * From RFC3061:
 * The NSS portion of the identifier is based on the string encoding
 * rules found in RFC 1778 Section 2.15 [4] which specifies a series
 * of digits separated by a period with the most significant digit
 * being at the left and the least significant being at the right.
 * At no time shall the NSS portion of the URN contain the human
 * readable description of a particular node in the OID tree.  The
 * NSS portion of the name is strictly limited to the digits 0-9 and
 * the '.' character with no leading zeros. No other characters are
 * permitted. This is all expressed in the following ABNF:
 *
 * oid             = number *( DOT number )
 * number          = DIGIT / ( LEADDIGIT 1*DIGIT )
 * LEADDIGIT       = %x31-39 ; 1-9
 * DIGIT           = %x30 / LEADDIGIT ; 0-9
 * DOT             = %x2E ; period
 */
public class OID
implements Comparable<OID>, Serializable
{
	private static final long serialVersionUID = 1L;
	//private final static String groupDelimiterRegex = "\\.";
	public final static String groupRegex = "[1-9][0-9]*";
	public final static Pattern groupPattern = Pattern.compile(groupRegex);
	public final static String OID_REGEX = "([1-9][0-9]*)((?:(?:0x2E)[1-9][0-9]*)*)";
	public final static Pattern OID_PATTERN = Pattern.compile(OID_REGEX);

	/**
	 * 
	 * @param oidAsString
	 * @return
	 * @throws OIDFormatException
	 */
	public static OID create(String oidAsString)
	throws OIDFormatException
	{
		return create(oidAsString, null);
	}
	
	/**
	 * 
	 * @param oidAsString
	 * @param description
	 * @return
	 * @throws OIDFormatException
	 */
	public static OID create(String oidAsString, String description)
	throws OIDFormatException
	{
		String[] groups = parseOIDString(oidAsString);
		
		OID newOid = new OID( groups, description );
		
		return newOid;
	}

	/**
	 * Parse a String in the format n[.n] where n is a base-10
	 * integer number.  The resulting String will have each group
	 * in an element of the returned array.
	 * 
	 * e.g. 1.2.33.444 returns {"1","2","33","444"}
	 * 
	 * @param oidAsString
	 * @return
	 * @throws OIDFormatException
	 */
	public static String[] parseOIDString(String oidAsString) 
	throws OIDFormatException
	{
		String groups[] = oidAsString.split("\\x2e");
		
		if(groups == null || groups.length < 1)
			throw new OIDFormatException("The string '" + oidAsString + "' is not in valid OID format, e.g. 1.2.3.4.56" );
		
		for(String group : groups)
			if( !groupPattern.matcher(group).matches() )
				throw new OIDFormatException("The group '" + group + "' of the string '" + oidAsString + "' is not in valid OID format, e.g. 1.2.3.4.56" );
		return groups;
	}
	
	/**
	 * 
	 * @param oidAsString
	 * @return
	 */
	public static boolean isValidOIDString(String oidAsString)
	{
		try
		{
			parseOIDString(oidAsString);
			return true;
		}
		catch (OIDFormatException x)
		{
			return false;
		}
	}
	
	// ========================================================================================================
	// Instance Members 
	// ========================================================================================================
	private final String[] groups;
	private final String description;
	
	private OID(String[] groups)
	{
		this(groups, null);
	}
	private OID(String[] groups, String description)
	{
		this.groups = new String[groups.length];
		// make a copy so that the array can't be changed after this instance is created
		// the String array members, are implicitly final
		System.arraycopy(groups, 0, this.groups, 0, groups.length);
		
		this.description = description;
	}
	
	public String getDescription()
	{
		return this.description;
	}

	/**
	 * If this OID is an ancestor of the given OID then return true.
	 * An ancestor is an OID where all groups in the ancestor match the
	 * corresponding group in the descendant and the descendant group
	 * has more groups than the ancestor.
	 * 
	 * @return
	 */
	public boolean isAncestorOf(OID descendant)
	{
		if(this.groups.length >= descendant.groups.length)
			return false;
		
		for(int index=0; index < this.groups.length; ++index)
			if(! this.groups[index].equals(descendant.groups[index]))
				return false;
			
		return true;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		for(String group : this.groups)
		{
			if(sb.length() > 0)
				sb.append('.');
			sb.append(group);
		}
		
		return sb.toString();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(this.groups);
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
		final OID other = (OID) obj;
		if (!Arrays.equals(this.groups, other.groups))
			return false;
		return true;
	}

	/**
	 * Compares this object with the specified object for order. 
	 * Returns a negative integer, zero, or a positive integer as this object is 
	 * less than, equal to, or greater than the specified object.
	 * 
	 * The natural ordering of the OID class is defined as:
	 * 1.) parent OIDs are less than offspring OID
	 * 2.) groups to the left have precedence over groups to the right
	 * 3.) groups of lower numeric value are less than those of a higher value
	 */
	@Override
	public int compareTo(OID that)
	{
		if(this==that)
			return 0;
		if(this.equals(that))
			return 0;

		for(int index=0; index<this.groups.length; ++index)
		{
			// all groups match up until now and that group has no more groups
			// so we must be an descendant of that group
			if(index >= that.groups.length)
				return 1;
			int groupCompare = this.groups[index].compareTo(that.groups[index]);
			if(groupCompare != 0)
				return groupCompare;
		}
		
		if(this.groups.length < that.groups.length)
			return -1;
		
		return 0;
	}
}
