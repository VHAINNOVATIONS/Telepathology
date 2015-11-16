/**
 * 
 */
package gov.va.med.imaging.core;

import gov.va.med.imaging.core.interfaces.router.CommandContext;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.core.router.queue.MockCommandImpl;
import gov.va.med.imaging.core.router.queue.ScheduledPriorityQueueElement;

import java.util.Date;

/**
 * @author vhaiswbeckec
 *
 */
public class MockCoreRouterUtility
{
	private static int mockCommandSerialNumber = 0;
	public static synchronized MockCommandImpl createMockCommand(CommandContext commandContext)
	{
		long now = System.currentTimeMillis();
		
		return new MockCommandImpl(
				"MockCommand" + (mockCommandSerialNumber++), 
				ScheduledPriorityQueueElement.Priority.NORMAL, 
				new Date(now), 	// immediately accessible
				new Date(now), 	// immediate processing
				-1L				// explicit, no estimate provided
			);
		
	}

}
