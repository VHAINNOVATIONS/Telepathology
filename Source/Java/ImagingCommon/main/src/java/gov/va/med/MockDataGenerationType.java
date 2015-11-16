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
@Target(ElementType.TYPE)
public @interface MockDataGenerationType
{
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
}
