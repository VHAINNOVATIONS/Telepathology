/**
 * 
 */
package gov.va.med.imaging.core.annotations.routerfacade;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to mark an interface as a facade-specific router interface.
 * The annotation processor will generate a proxy, implementing the annotated
 * interface, that calls the "real" router.
 * 
 * @author vhaiswbeckec
 *
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface FacadeRouterInterface
{
	/**
	 * The name of the class to extend.  The named class MUST BE derived from
	 * gov.va.med.imaging.core.interfaces.router.AbstractFacadeRouterImpl
	 * 
	 * @return
	 */
	public String extendsClassName() default "";
	
	/**
	 * A comma separated list of package names where the commands
	 * can be located.
	 *  
	 * @return
	 */
	public String commandPackageNames() default "";
}
