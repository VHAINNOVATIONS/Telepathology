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
public @interface MockDataGenerationSetter
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
	 * The data that will be generated when randomize is false.
	 * This value is ignored when randomize is true.
	 * 
	 * @return
	 */
	public String defaultValue() default "";
	
	/**
	 * The meaning of minimum and maximum differ depending on the field type
	 * being annotated.  For numeric fields these specify the range of values
	 * that will be generated.  For string fields these specify the minimum and
	 * maximum length of generated data.
	 * Minimum and maximum are ignored if pattern is specified. 
	 * 
	 * @return
	 */
	public double minimum() default 1;
	
	/**
	 * The meaning of minimum and maximum differ depending on the field type
	 * being annotated.  For numeric fields these specify the range of values
	 * that will be generated.  For string fields these specify the minimum and
	 * maximum length of generated data.
	 * Minimum and maximum are ignored if pattern is specified. 
	 * @return
	 */
	public double maximum() default 32;
	
}
