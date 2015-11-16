/**
 * 
 */
package gov.va.med.interactive;

import java.lang.reflect.Method;
import org.apache.log4j.Logger;

/**
 * @author VHAISWBECKEC
 *
 */
public abstract class Command<M>
{
	private final Logger logger = Logger.getLogger(this.getClass());
	
	private CommandFactory<M> commandFactory;
	private String[] commandParameterValues;
	
	/**
	 * A CommandProcessor that takes no parameters may call this
	 * (the default no-arg) constructor.
	 *  
	 * @return
	 */
	public Command()
	{
		this.commandParameterValues = null;
	}
	
	/**
	 * A CommandProcessor that takes any parameters must implement a constructor
	 * overriding this form.
	 * 
	 * @param commandParameterValues
	 */
	public Command (String[] commandParameterValues)
	{
		this.commandParameterValues = commandParameterValues;
	}

	protected String[] getCommandParameterValues()
	{
		return commandParameterValues;
	}
	
	/**
	 * 
	 * @param commandParameterValues
	 * @return
	 */
	protected void validateParameters(String[] commandParameterValues)
	throws CommandValidationException
	{
		CommandParametersDescription<?>[] parametersDescs = getInstanceCommandParametersDescription();
		if(parametersDescs == null)
			return;		// can't really validate without the description

		CommandValidationExceptions exceptions = new CommandValidationExceptions();
		int parameterIndex = 0;
		for(CommandParametersDescription<?> parameterDesc : parametersDescs)
		{
			if(parameterDesc.isRequired() && parameterIndex >= commandParameterValues.length)
				exceptions.add(new CommandValidationException("Required field '" + parameterDesc.getParameterName() + "' is not present."));
			
			
			++parameterIndex;
		}
		
		if(exceptions.size() > 0)
			throw exceptions;
	}
	
	private Object[] typecheckedParameters;
	protected synchronized Object[] getTypecheckedParameters() 
	throws CommandValidationException
	{
		if(this.typecheckedParameters == null)
			createTypeCheckedParameters(getCommandParameterValues());
		
		return this.typecheckedParameters;
	}
	
	/**
	 * Get the CommandParametersDescription from the command that has the given index.
	 * 
	 * @param name
	 * @return
	 */
	protected CommandParametersDescription<?> getCommandParametersDescription(int index)
	{
		try
		{
			Class<?> clazz = this.getClass();
			Method parameterDescriptionsMethod = clazz.getMethod("getCommandParametersDescription", (Class<?>[])null);
			CommandParametersDescription<?>[] commandParametersDescription = 
				(CommandParametersDescription[])( parameterDescriptionsMethod.invoke(null, (Object[])null) );
			
			return commandParametersDescription[index];
		}
		catch (Exception x)
		{
			return null;
		}
	}
	
	/**
	 * Get the CommandParametersDescription from the command that has the given name.
	 * 
	 * @param name
	 * @return
	 */
	protected CommandParametersDescription<?> getCommandParametersDescription(String name)
	{
		CommandParametersDescription<?>[] commandParametersDescription = getCommandsParametersDescriptions();

		if(commandParametersDescription != null)
			for( CommandParametersDescription<?> commandParameterDescription : commandParametersDescription )
				if( name.equals(commandParameterDescription.getParameterName()) )
					return commandParameterDescription;

		return null;
	}

	private CommandParametersDescription<?>[] cpdxs = null;
	private synchronized CommandParametersDescription<?>[] getCommandsParametersDescriptions() 
	{
		if(cpdxs == null)
			try
			{
				Class<?> clazz = this.getClass();
				Method parameterDescriptionsMethod = clazz.getMethod("getCommandParametersDescription", (Class<?>[])null);
				CommandParametersDescription<?>[] commandParametersDescription = 
					(CommandParametersDescription[])( parameterDescriptionsMethod.invoke(null, (Object[])null) );
				cpdxs = commandParametersDescription;
			}
			catch (Exception x)
			{
				cpdxs = null;
			}
			
		return cpdxs;
	}
	
	/**
	 * 
	 * @param cpx
	 * @return
	 */
	protected int getCommandParametersDescriptionIndex(final CommandParametersDescription<?> cpx)
	{
		CommandParametersDescription<?>[] commandParametersDescription = getCommandsParametersDescriptions();

		int index = 0;
		if(commandParametersDescription != null)
			for( CommandParametersDescription<?> commandParameterDescription : commandParametersDescription )
			{
				if( commandParameterDescription.equals(cpx) )
					return index;
				++index;
			}
		return -1;
	}
 	/**
 	 * 
 	 * @param commandParameterValues
 	 * @return
 	 * @throws CommandValidationException 
 	 */
	private void createTypeCheckedParameters(final String[] commandParameterValues) 
	throws CommandValidationException
	{
		CommandParametersDescription<?>[] parametersDescs = getInstanceCommandParametersDescription();
		if(parametersDescs == null)
			return;
		
		this.typecheckedParameters = new Object[parametersDescs.length];
		int parameterIndex = 0;
		for( CommandParametersDescription<?> parametersDesc : parametersDescs)
		{
			String source = (parameterIndex < commandParameterValues.length) ? commandParameterValues[parameterIndex] : null;
			
			this.typecheckedParameters[parameterIndex] = parametersDesc.getValue(source); 
			++parameterIndex;
		}
		
		return;
	}

	/**
	 * If the command has a static getCommandParametersDescription method then call it
	 * and return the result else return null.
	 * 
	 * @return
	 */
 	public CommandParametersDescription<?>[] getInstanceCommandParametersDescription()
 	{
 		try
		{
			Method staticParametersDescriptionAccessor = this.getClass().getMethod("getCommandParametersDescription", (Class[])null);
			return (CommandParametersDescription[]) staticParametersDescriptionAccessor.invoke(null, (Object[])null);
		} 
 		catch (Exception x)
		{
 			return null;
		}
 	}

 	public boolean isParameterExists(int index)
 	{
 		return index < this.getCommandParameterValues().length;
 	}
 	
 	public boolean isParameterExists(String name)
 	{
 		CommandParametersDescription<?> cpx = this.getCommandParametersDescription(name);
 		int index = this.getCommandParametersDescriptionIndex(cpx);
 		
 		return isParameterExists(index);
 	}
 	
 	/**
 	 * 
 	 * @param <T>
 	 * @param index
 	 * @param expectedClass
 	 * @return
 	 * @throws CommandTypeValidationException
 	 */
 	@SuppressWarnings("unchecked")
	public <T extends Object> T getParameterValue(int index, Class<T> expectedClass) 
 	throws CommandTypeValidationException
 	{
 		CommandParametersDescription<?> cpx = this.getCommandParametersDescription(index);
 		if( cpx != null && expectedClass.isAssignableFrom(cpx.getParameterClass()) && index < this.getCommandParameterValues().length)
 			return (T)cpx.getValue(this.getCommandParameterValues()[index]);
 		else
 			return null;
 	}
 	
 	/**
 	 * 
 	 * @param <T>
 	 * @param name
 	 * @param expectedClass
 	 * @return
 	 * @throws CommandTypeValidationException
 	 */
 	@SuppressWarnings("unchecked")
 	public <T extends Object> T getParameterValue(String name, Class<T> expectedClass)
 	throws CommandTypeValidationException
 	{
 		CommandParametersDescription<?> cpx = this.getCommandParametersDescription(name);
 		if(cpx == null)
 			return (T)( Boolean.class == expectedClass ? Boolean.FALSE : null );
 		
 		int index = getCommandParametersDescriptionIndex(cpx);
 		if(index >= 0)
 			return
 				this.getCommandParameterValues() != null &&
 				this.getCommandParameterValues().length > index ?
 					(T)cpx.getValue(this.getCommandParameterValues()[index]) :
 					( Boolean.class.equals(cpx.getParameterClass()) ? (T)Boolean.FALSE : null );
 		else
 			return null;
 	}
 	
	/**
	 * @return the commandFactory
	 */
	public CommandFactory<M> getCommandFactory()
	{
		return this.commandFactory;
	}

	/**
	 * @param commandFactory the commandFactory to set
	 */
	public void setCommandFactory(CommandFactory<M> commandFactory)
	{
		this.commandFactory = commandFactory;
	}

	protected Logger getLogger()
	{
		return this.logger;
	}

	public abstract void processCommand(CommandProcessor<M> processor, M managedObject)
	throws Exception;
	
	public boolean exitAfterProcessing(){return false;}
}
