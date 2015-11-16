/**
 * 
 */
package gov.va.med.imaging.storage.cache.interactive;

import gov.va.med.imaging.storage.cache.impl.CacheManagerImpl;
import gov.va.med.interactive.Command;
import java.lang.reflect.InvocationTargetException;
import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class TestCommandFactory extends TestCase
{
	private InteractiveCacheCommandFactoryImpl commandFactory;

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		commandFactory = InteractiveCacheCommandFactoryImpl.getSingleton();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		commandFactory = null;
		super.tearDown();
	}

	/**
	 * @return the commandFactory
	 */
	public InteractiveCacheCommandFactoryImpl getCommandFactory()
	{
		return this.commandFactory;
	}

	/**
	 * Test method for {@link gov.va.med.interactive.CommandListCommandFactory#createCommand(java.lang.String, java.lang.String[])}.
	 * @throws InvocationTargetException 
	 * @throws NoSuchMethodException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws IllegalArgumentException 
	 * @throws SecurityException 
	 * @throws ClassCastException 
	 */
	public void testCreateCommand() 
	throws ClassCastException, SecurityException, IllegalArgumentException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException
	{
		Command<CacheManagerImpl> vcp = getCommandFactory().createCommand("");
		assertNull(vcp);
		
		vcp = getCommandFactory().createCommand("initialize");
		assertNotNull(vcp);
		
		vcp = getCommandFactory().createCommand("start");
		assertNotNull(vcp);
		
		vcp = getCommandFactory().createCommand("stop");
		assertNotNull(vcp);
	}

	/**
	 * Test method for {@link gov.va.med.interactive.CommandListCommandFactory#getHelpString()}.
	 */
	public void testGetHelpString()
	{
		System.out.println( getCommandFactory().getHelpMessage() );
	}

}
