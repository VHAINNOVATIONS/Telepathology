/**
 * 
 */
package gov.va.med;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author vhaiswbeckec
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface MockDataGenerationField
{
	/**
	 * A pattern that specifies the data that will be generated when 
	 * randomize is true.
	 * This value is ignored when randomize is false.
	 * 
	 * The format of the pattern follows a very simple regex specification
	 * using only square brackets "[]" for character classes and curly
	 * brackets "{}" for repetition.
	 * 
	 * This property may also be used when annotating a numeric field if the pattern
	 * builds a valid string representation of the numeric type.
	 * 
	 * String Examples: 
	 * "[a-z]{1,32}" - generates a string from 1 to 32 characters long of lower case characters
	 * "[1-9][0-9]{2}-[0-9]{2}-[0-9]{4}" - generates strings that look like social security numbers
	 * "([1-9][0-9]{2})[1-9][0-9]{2}-[0-9]{4}" - generates strings that look like US telephone numbers
	 * 
	 * Numeric Examples:
	 * "[0-9]{1,2}" - generates an integer from 0 to 99
	 * "[+|-][0-9]{1,2}" - generates an integer from -99 to 99
	 * "[+|-][0-9]{1,2}.[0-9]{0,2}" - generates an floating point number from -99.99 to 99.99
	 * 
	 * @return
	 */
	public String pattern() default "";

	/**
	 * A comma separated list of media types (MIME types).
	 * This parameter is only valid when the annotated field
	 * is of type InputStream.
	 *  
	 * @return
	 */
	public String mediaTypes() default "";
	
	/**
	 * The data that will be generated when randomize is false.
	 * This value is ignored when randomize is true.
	 * 
	 * @return
	 */
	public String defaultValue() default "";

	/**
	 * The default media type of an InputStream field type.
	 * This parameter is only valid when the annotated field
	 * is of type InputStream.
	 * @return
	 */
	public String defaultMediaType() default "";
	
	/**
	 * The meaning of minimum and maximum differ depending on the field type
	 * being annotated.  For numeric fields these specify the range of values
	 * that will be generated.  For string fields these specify the minimum and
	 * maximum length of generated data.
	 * Minimum and maximum are ignored if pattern is specified. 
	 * 
	 * @return
	 */
	public double minimum() default 1.0;
	public int minimumStringLength() default 0;
	/**
	 * The minimum value of a date/time field in the format: dd-MM-yyyy:hh:mm:ss
	 * 
	 * @return
	 */
	public String minimumDate() default "01-01-1970:00:00:00";
	
	/**
	 * The meaning of minimum and maximum differ depending on the field type
	 * being annotated.  For numeric fields these specify the range of values
	 * that will be generated.  For string fields these specify the minimum and
	 * maximum length of generated data.
	 * Minimum and maximum are ignored if pattern is specified. 
	 * @return
	 */
	public double maximum() default 32.0;
	public int maximumStringLength() default 32;
	/**
	 * The maximum value of a date/time field in the format: dd-MM-yyyy:hh:mm:ss
	 * 
	 * @return
	 */
	public String maximumDate() default "12-31-2100:23:59:59";
	
	/**
	 * Specifies whether the field should be populated when doing a FULL
	 * or a REQUIRED population.
	 * A field marked as required() will always be populated when the
	 * child population is FULL or REQUIRED.
	 * A field not marked as required() will be populated when the
	 * child population is FULL.
	 * 
	 * @return
	 */
	public boolean required() default false;
	
	/**
	 * The fully qualified type of the component members of a Map derived type.
	 * This property is only used when the annotated field is a Map derivation,
	 * otherwise it is ignored.
	 * @see #componentValueType()
	 * @return
	 */
	public String componentKeyType() default "";
	
	
	/**
	 * The fully qualified type of the component members of a Collection derived type
	 * or the component values of a Map derived type.
	 * This property is only used when the annotated field is a Map or Collection 
	 * derivation, otherwise it is ignored.
	 * @see #componentKeyType()
	 * @return
	 */
	public String componentValueType() default "";
	
	public static final String NAME_PATTERN = "[A-Z][a-z]{2,16} [A-Z] [A-Z][a-z]{0,32}";
	public static final String OID_PATTERN = "[1-9].[1-9].[1-9][0-9]{1,2}[1-9].[1-9].[1-9][0-9]{1,4}.[1-9][0-9]{1,4}.[1-9][0-9]{1,4}.[1-9][0-9]{1,4}";
	public static final String UID_PATTERN = OID_PATTERN;
	public static final String ICN_PATTERN = "[0-9]{10}V[0-9]{4}";
	public static final String CPT_PATTERN = "[1-9][0-9]{4}";
}
