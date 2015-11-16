/**
 * 
 */
package gov.va.med.imaging.core;

import java.io.IOException;

import gov.va.med.imaging.core.interfaces.router.CommandContext;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.core.router.AsynchronousCommandExecutor;
import gov.va.med.imaging.core.router.queue.MockCommandImpl;
import gov.va.med.imaging.transactioncontext.ClientPrincipal;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class AsynchronousCommandExecutorTest 
extends TestCase
{
	private AsynchronousCommandExecutor executor;
	private CommandContext commandContext = new MockCommandContext();
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		executor = new AsynchronousCommandExecutor();
	}

	/**
	 * Test method for {@link gov.va.med.imaging.core.router.AsynchronousCommandExecutor#execute(gov.va.med.imaging.core.router.AbstractCommandImpl)}.
	 * @throws InterruptedException 
	 */
	public void testSingleExecute() 
	throws InterruptedException
	{
		MockCommandImpl cmd = MockCoreRouterUtility.createMockCommand(commandContext);
		
		executor.execute(cmd);
		
		Thread.sleep(3000L);
		
		assertTrue(cmd.isExecuted());
	}

	/*
	private void testMultipleExecute() 
	throws InterruptedException, IOException
	{
		MockCommandImpl[] cmds = new MockCommandImpl[100];
		for(int n=0; n<cmds.length; ++n)
		{
			TransactionContextFactory.setTestSecurityContext();

			cmds[n] = MockCoreRouterUtility.createMockCommand(commandContext);
			executor.execute(cmds[n]);
		}
		
		Thread.sleep(10000L);
		
		for(int n=0; n<cmds.length; ++n)
			assertTrue(cmds[n].isExecuted());
	}*/
}
