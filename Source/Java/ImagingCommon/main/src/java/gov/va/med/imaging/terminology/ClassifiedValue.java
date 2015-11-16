/**
 * 
 */
package gov.va.med.imaging.terminology;


/**
 * Just a small value object to pass translated values with their
 * classification scheme.
 * 
 * @author vhaiswbeckec
 *
 */
public class ClassifiedValue
{
	private final CodingScheme codingScheme;
	private final String codeValue;
	
	public ClassifiedValue(CodingScheme codingScheme, String codeValue) 
	{
		super();
		this.codingScheme = codingScheme;
		this.codeValue = codeValue;
	}
	public CodingScheme getCodingScheme()
	{
		return this.codingScheme;
	}
	
	public String getCodeValue()
	{
		return this.codeValue;
	}
}
