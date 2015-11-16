/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Oct 1, 2008
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWBECKEC
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */
package gov.va.med.imaging.core.interfaces.router;

import java.util.Collection;
import java.util.Map;

/**
 * This interface defined the factory class for asynchronous command to a Router implementation.
 * Every Router implementation is required to provide an instance of this class to build
 * asynchronous commands which the Router implementations doAsynchronously() method can process.
 * 
 * NOTE: The naming of all methods within this interface is strictly controlled by convention.  The method
 * names MUST follow the format:
 * "create" + <action> + <item> + "Command"
 * where:
 * <action> is the verb describing the activity to perform.  The case is significant, and must
 *   follow camel casing convention, with the first letter in upper case. 
 *   ex: "Log", "Prefetch", "Get"
 *   
 * <item> is a noun (possibly concatenated in camel case) of the type of thing to act on.
 *   Again, the case is significant, and must follow camel casing convention, with the first 
 *   letter in upper case. 
 *   ex: "PatientStudyList", "ImageAccessEvent", "PatientIdentificationImage"
 * 
 * For basic CRUD operations, the action should use "Post", "Get", "Put", "Delete".
 * For commands with a Boolean result, "Is" may be substituted for "Get".
 * 
 * These conventions are checked at compile time by the CommandFactoryMethod annotation,
 * which is also required for all methods used by the facade-specific router proxy handling.
 * 
 * @author VHAISWBECKEC
 *
 */
public interface CommandFactory
{

	/**
	 * This form of the createCommand method must be used
	 * if any of the parameters are of unambiguous type.
	 * This is the preferred form to create commands because it
	 * is much safer than relying on initArg types.
	 * 
	 * @param commandClassName
	 * @param initArgTypes
	 * @param initArgs
	 * @return
	 */
	public <R extends Object> Command<R> createCommand(
		Class<R> resultClass,
		String commandClassName, 
		String commandPackage,
		Class<?>[] initArgTypes,
		Object[] initArgs);
	
	public <R extends Object> boolean isCommandSupported(
			Class<R> resultClass,
			String commandClassName, 
			String commandPackage,
			Class<?>[] initArgTypes,
			Object[] initArgs);

	public <C extends Collection<R>, R extends Object> boolean isCollectionCommandSupported(
			Class<C> collectionClass,
			Class<R> resultClass,
			String commandClassName, 
			String commandPackage,
			Class<?>[] initArgTypes,
			Object[] initArgs)
		throws IllegalArgumentException;
	
	/**
	 * 
	 * @param <C>
	 * @param <R>
	 * @param collectionClass
	 * @param resultClass
	 * @param commandClassName
	 * @param initArgTypes
	 * @param initArgs
	 * @return
	 * @throws IllegalArgumentException
	 */
	public <C extends Collection<R>, R extends Object> Command<C> createCollectionCommand(
		Class<C> collectionClass,
		Class<R> resultClass,
		String commandClassName, 
		String commandPackage,
		Class<?>[] initArgTypes,
		Object[] initArgs)
	throws IllegalArgumentException;

	/**
	 * 
	 * @param <C>
	 * @param <R>
	 * @param collectionClass
	 * @param resultClass
	 * @param commandClassName
	 * @param initArgTypes
	 * @param initArgs
	 * @return
	 * @throws IllegalArgumentException
	 */
	public <C extends Map<K,V>, K extends Object, V extends Object> Command<C> createMapCollectionCommand(
		Class<C> collectionClass,
		Class<K> mapKeyClass,
		Class<V> mapValueClass,
		String commandClassName, 
		String commandPackage,
		Class<?>[] initArgTypes,
		Object[] initArgs)
	throws IllegalArgumentException;
	
	public <C extends Map<K,V>, K extends Object, V extends Object> boolean isMapCollectionCommandSupported(
			Class<C> collectionClass,
			Class<K> mapKeyClass,
			Class<V> mapValueClass,
			String commandClassName, 
			String commandPackage,
			Class<?>[] initArgTypes,
			Object[] initArgs)
		throws IllegalArgumentException;
}
