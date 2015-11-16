/**
 * 
 */
package gov.va.med.imaging.core.annotations.routerfacade;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that indicates to the router whether a command can be executed asynchronously
 * and whether it may be safely executed on a remote node (in a cluster).
 * By default, commands will not be executed asynchronously and will always be
 * executed on the node that created them.  Commands that may be executed asynchronously
 * and may be executed on another node must be explicitly marked so. 
 * 
 * @author vhaiswbeckec
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RouterCommandExecution
{
	/**
	 * Set to true if this command may be executed asynchronously.  The command should not
	 * depend on its listeners getting called to free system resources (i.e. no streams).
	 *
	 * @return
	 */
	public boolean asynchronous() default false;

	/**
	 * Set to true if this command is safe to run on any node in the cluster.
	 * Set to false and this command must execute on the node on which it was created.
	 * 
	 * @return
	 */
	public boolean distributable() default false;
}
