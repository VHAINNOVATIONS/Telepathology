/**
 * 
 */
package gov.va.med.imaging.exchange.business.dicom;

/**
 * @author vhaiswlouthj
 *
 */
public class CFindElement {
	
	private static final String CONTROL_CHARACTER_REGEX = "\\p{Cntrl}";
	private String tag;
	private String value;
	
	public String getTag() { return tag;}
	public String getValue() {return value; }

	public CFindElement(String tag, String value)
	{
		this.tag = cleanControlCharacters(tag);
		this.value = cleanControlCharacters(value);
	}

	// This code replaces the loop over each char found in the original code...
	private String cleanControlCharacters(String stringToClean)
	{
        return stringToClean.replaceAll(CONTROL_CHARACTER_REGEX, "");
	}

}
