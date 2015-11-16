package gov.va.med.imaging.exchange.utility;

/**
 * Static utility methods for Base32 coding and decoding.
 * 
 * @author VHAISWBECKEC
 *
 */
public class Base32ConversionUtility 
{
	// prevent instance creation
	private Base32ConversionUtility(){}
	
	public static String base32Encode(String input) {
		if(input == null)
			return "";
		return Base32.encode(input.getBytes());
	}
	
	public static String base32Decode(String input) {
		if(input == null)
			return "";
		return new String(Base32.decode(input));
		
	}
}
