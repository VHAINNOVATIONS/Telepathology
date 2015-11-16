/**
 * 
 */
package gov.va.med.imaging.core.annotations.routerfacade;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation on any method in a FacadeRouterInterface which the
 * code generator should build a router call, which should be every
 * method in the interface.
 * 
 * @author vhaiswbeckec
 *
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface FacadeRouterMethod
{
	/**
	 * If the command should be executed asynchronously then set this
	 * property to true and declare the annotated method as returning void.
	 * If this property is true and the annotated method does not return void 
	 * then a compile error will result (during annotation processing).
	 * If this property is true then the annotated method may also include a 
	 * parameter of type AsynchronousCommandResultListener, which is a reference
	 * to a listener that will be notified when the command is complete.  If such a
	 * parameter is NOT included or the value of the parameter is null then there
	 * will be no notification of completion (a reasonable scenario for prefetch).
	 * The name of the parameter containing the AsynchronousCommandResultListener must be
	 * specified using the asynchronousCommandResultListenerParameterName parameter to this
	 * annotation.
	 * If the asynchronous property is false (or not specified) then the annotated method MUST NOT
	 * include a parameter of type AsynchronousCommandResultListener, else a compile
	 * error (during annotation processing) will be generated.
	 * 
	 * If this property is true then the delay and priority properties may also be specified.
	 * The delay property is the number of milliseconds to wait until the
	 * command is eligible for execution.  The priority property is the execution priority,
	 * the queue ordering, of the command.  The priority property is an integer with a value
	 * of 0, 1, 2.  High numeric priority will be executed before lower numeric priority.  
	 */
	public boolean asynchronous() default false;

	/**
	 * For an asynchronous command to have a listener, the name of the listener
	 * parameter must be specified here.  This parameter should not be specified
	 * for synchronous commands.
	 * The parameter, in the facade, must specify a type of 
	 * AsynchronousCommandResultListener<R> where R is the result type of
	 * the called command.
	 */
	public String asynchronousCommandResultListenerParameterName() default "";
	
	/**
	 * @see #asynchronous()
	 */
	public long delay() default 0L;
	
	/**
	 * @see #asynchronous()
	 */
	public int priority() default 2;
	
	/**
	 * By default the annotation processor will use naming conventions to determine what
	 * router command is required.  The interface method name is parsed and the command name
	 * is derived from the content.  If this convention is not followed then this property
	 * must specify the desired command interface.
	 * In any case the declared return type of the annotated method must be castable from
	 * the return type of the mapped command's process() method.  Reasonable effort is made to 
	 * generate a compile error when this is not the case but a runtime error may still occur. 
	 */
	public String commandClassName() default "";
	
	/**
	 * If this is a command being called by another command, the annotation should have the value
	 * of true. This allows the Router to correctly handle saving and restoring the parent
	 * context, and creating and establishing the child command's context for the duration
	 * of the command.
	 * 
	 * @return
	 */
	public boolean isChildCommand() default false;
	
	/**
	 * Specify the package to find the command in. This property is optional, if no package is specified then the CommandFactory
	 * will brute force to find the command in all possible packages. If a package is specified the CommandFactory will only
	 * look in the specified package to find the command and no other locations.
	 * 
	 * @return
	 */
	public String commandPackage() default "";
	
	/**
	 * Specifies if this command should be a periodic command
	 * @return
	 */
	public boolean isPeriodic() default false;
	
	/**
	 * Specifies the periodic command interval (in ms), this value does not nothing if isPeriodic() is false.
	 * @return
	 */
	public int periodicExecutionDelay() default 60000;
}
