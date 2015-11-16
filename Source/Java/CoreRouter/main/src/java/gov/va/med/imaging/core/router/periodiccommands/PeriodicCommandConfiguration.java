package gov.va.med.imaging.core.router.periodiccommands;

import gov.va.med.imaging.core.router.queue.ScheduledPriorityQueueElement;
import gov.va.med.imaging.exchange.business.vistarad.PatientEnterpriseExams;
import gov.va.med.imaging.facade.configuration.AbstractBaseFacadeConfiguration;

import java.util.ArrayList;
import java.util.List;

public class PeriodicCommandConfiguration extends
		AbstractBaseFacadeConfiguration {
	private static PeriodicCommandConfiguration periodicCommandConfiguration = null;
	private List<PeriodicCommandDefinition> commandDefinitions;

	public PeriodicCommandConfiguration() {
		commandDefinitions = new ArrayList<PeriodicCommandDefinition>();
	}

	@Override
	public AbstractBaseFacadeConfiguration loadDefaultConfiguration() {
		return this;
	}

	public synchronized static PeriodicCommandConfiguration getConfiguration() {
		if (periodicCommandConfiguration == null) {
			PeriodicCommandConfiguration config = new PeriodicCommandConfiguration();
			periodicCommandConfiguration = (PeriodicCommandConfiguration) config
					.loadConfiguration();
		}
		return periodicCommandConfiguration;
	}

	public static void main(String[] args) {
		if (args.length < 2 || args.length%2 > 0) {
			printUsage();
			System.exit(0);
		}
		
		PeriodicCommandConfiguration config = PeriodicCommandConfiguration.getConfiguration();
		PeriodicCommandDefinition commandDefinition;

		for (int i=0; i < args.length; i = i + 2){
			commandDefinition = new PeriodicCommandDefinition();
			commandDefinition.setReturnClass(Object.class);
			commandDefinition
			.setCommandClassName(args[i]);
			commandDefinition.setCommandParameters(new Object[] {});
			commandDefinition.setPeriodicDelayInterval(args[i + 1]);
			commandDefinition
					.setPriority(ScheduledPriorityQueueElement.Priority.NORMAL);
			config.commandDefinitions.add(commandDefinition);
		}
		config.storeConfiguration();
	}

	public List<PeriodicCommandDefinition> getCommandDefinitions() {
		return commandDefinitions;
	}

	public void setCommandDefinitions(
			List<PeriodicCommandDefinition> commandDefinitions) {
		this.commandDefinitions = commandDefinitions;
	}

    private static void printUsage() {
        System.out.println("This program requires two arguments:");
        System.out.println("  * The name of the periodic command");
        System.out.println("  * The period in seconds");
    }
}
