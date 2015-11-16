package gov.va.med.imaging.core.router;

import static org.junit.Assert.fail;
import junit.framework.Assert;
import gov.va.med.GeneratedCodeValidationUtility;
import gov.va.med.exceptions.ValidationException;
import gov.va.med.imaging.core.interfaces.router.Command;
import gov.va.med.imaging.core.router.commands.GetUserKeysCommandImpl;
import gov.va.med.imaging.core.router.commands.GetWelcomeMessageCommandImpl;
import gov.va.med.imaging.core.router.commands.PostAuditEventCommandImpl;
import gov.va.med.imaging.core.router.commands.PostDurableQueueCommandImpl;
import gov.va.med.imaging.core.router.facade.InternalRouterTest;

import org.hamcrest.core.IsAnything;
import org.junit.Test;

public class TestPeriodicCommandList 
{
	@Test
	public void testIsAnotherInstancePresent()
	{
		// Create a list of periodic commands
		PeriodicCommandList list = PeriodicCommandList.get();
		
		// Verify that if nothing is in the list, return false
		Command sharedCommand = new GetUserKeysCommandImpl(null);
		Assert.assertEquals(false, list.isAnotherInstancePresent(sharedCommand));
		
		Command originalAuditEventCommand = new PostAuditEventCommandImpl(null, null);
		// Add some items to the list
		list.addScheduledPeriodicCommand(sharedCommand);
		list.addScheduledPeriodicCommand(originalAuditEventCommand);
		list.addScheduledPeriodicCommand(new PostDurableQueueCommandImpl(null, null));
		
		// Verify you're in the list, return false
		Assert.assertEquals(false, list.isAnotherInstancePresent(sharedCommand));
		
		// Verify that if no instances of the command is in the list, return false
		Command welcomeMessageCommand = new GetWelcomeMessageCommandImpl();
		Assert.assertEquals(false, list.isAnotherInstancePresent(welcomeMessageCommand ));

		// Verify that if a different instance of your class is in the list, return true
		Command newAuditEventCommand = new PostAuditEventCommandImpl(null, null);
		Assert.assertEquals(true, list.isAnotherInstancePresent(newAuditEventCommand));
		
		// Have two instances in the list
		list.addScheduledPeriodicCommand(newAuditEventCommand);

		Assert.assertEquals(true, list.isAnotherInstancePresent(originalAuditEventCommand));
		Assert.assertEquals(true, list.isAnotherInstancePresent(newAuditEventCommand));
		
	}
}
