/**
 * 
 */
package gov.va.med.interactive;

import java.lang.reflect.InvocationTargetException;

/**
 * A CommandFactory takes String values of the command name and command argumants and creates
 * Command-derived instances from them.
 * 
 * @author vhaiswbeckec
 *
 */
public interface CommandFactory<M>
{
	/**
	 * 
	 * @param command
	 * @param commandArgs
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassCastException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public Command<M> createCommand( String command, String[] commandArgs) 
	throws InstantiationException, IllegalAccessException, ClassCastException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException;

	/**
	 * 
	 * @param string
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassCastException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public Command<M> createCommand(String string)
	throws InstantiationException, IllegalAccessException, ClassCastException, SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException;

	/**
	 * 
	 * @return
	 */
	public String getHelpMessage();
}
