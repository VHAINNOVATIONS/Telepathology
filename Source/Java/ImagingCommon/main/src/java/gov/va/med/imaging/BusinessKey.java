package gov.va.med.imaging;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @author vhaiswgraver
 * An annotation identifying properties of a class which are business keys used for
 * generating the hashCode, comparing equality, and converting to a string
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD, ElementType.METHOD })
public @interface BusinessKey {
    BusinessKeyMethod[] include() default BusinessKeyMethod.ALL;
    BusinessKeyMethod[] exclude() default BusinessKeyMethod.NONE;
}
