/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 3, 2010
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */
package gov.va.med.imaging.core.router.commands.configuration;

import gov.va.med.imaging.facade.configuration.AbstractBaseFacadeConfiguration;
import gov.va.med.imaging.facade.configuration.FacadeConfigurationFactory;
import gov.va.med.imaging.facade.configuration.exceptions.CannotLoadConfigurationException;

/**
 * @author vhaiswwerfej
 *
 */
public class CommandConfiguration 
extends AbstractBaseFacadeConfiguration
{
	private boolean returnDiagnosticUncompressedForDiagnosticRequests;
	private boolean useCprsContextToGetPatientTreatingFacilitiyList;
	private boolean callArtifactSpi;
	private long asynchronousCommandWaiterTimeoutMs; 
	private int minimumThreadCount;
	private boolean ensurePatientSeenAtDoD = true;
	
	public CommandConfiguration()
	{
		super();	
	}

	/**
	 * @return the returnDiagnosticUncompressedForDiagnosticRequests
	 */
	public boolean isReturnDiagnosticUncompressedForDiagnosticRequests()
	{
		return returnDiagnosticUncompressedForDiagnosticRequests;
	}

	/**
	 * @param returnDiagnosticUncompressedForDiagnosticRequests the returnDiagnosticUncompressedForDiagnosticRequests to set
	 */
	public void setReturnDiagnosticUncompressedForDiagnosticRequests(
		boolean returnDiagnosticUncompressedForDiagnosticRequests)
	{
		this.returnDiagnosticUncompressedForDiagnosticRequests = returnDiagnosticUncompressedForDiagnosticRequests;
	}

	public boolean isUseCprsContextToGetPatientTreatingFacilitiyList()
	{
		return useCprsContextToGetPatientTreatingFacilitiyList;
	}

	public void setUseCprsContextToGetPatientTreatingFacilitiyList(
			boolean useCprsContextToGetPatientTreatingFacilitiyList)
	{
		this.useCprsContextToGetPatientTreatingFacilitiyList = useCprsContextToGetPatientTreatingFacilitiyList;
	}

	public boolean isCallArtifactSpi()
	{
		return callArtifactSpi;
	}

	public void setCallArtifactSpi(boolean callArtifactSpi)
	{
		this.callArtifactSpi = callArtifactSpi;
	}

	public long getAsynchronousCommandWaiterTimeoutMs()
	{
		return asynchronousCommandWaiterTimeoutMs;
	}

	public void setAsynchronousCommandWaiterTimeoutMs(
			long asynchronousCommandWaiterTimeoutMs)
	{
		this.asynchronousCommandWaiterTimeoutMs = asynchronousCommandWaiterTimeoutMs;
	}

	public int getMinimumThreadCount()
	{
		return minimumThreadCount;
	}

	public void setMinimumThreadCount(int minimumThreadCount)
	{
		this.minimumThreadCount = minimumThreadCount;
	}

	public boolean isEnsurePatientSeenAtDoD() {
		return ensurePatientSeenAtDoD;
	}

	public void setEnsurePatientSeenAtDoD(boolean ensurePatientSeenAtDoD) {
		this.ensurePatientSeenAtDoD = ensurePatientSeenAtDoD;
	}

	public synchronized static CommandConfiguration getCommandConfiguration()
	{
		try
		{
			return FacadeConfigurationFactory.getConfigurationFactory().getConfiguration(CommandConfiguration.class);
		}
		catch(CannotLoadConfigurationException clcX)
		{
			// no need to log, already logged
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.facade.configuration.AbstractBaseFacadeConfiguration#loadDefaultConfiguration()
	 */
	@Override
	public AbstractBaseFacadeConfiguration loadDefaultConfiguration()
	{
		this.returnDiagnosticUncompressedForDiagnosticRequests = true;
		this.useCprsContextToGetPatientTreatingFacilitiyList = true;
		this.callArtifactSpi = true;
		this.asynchronousCommandWaiterTimeoutMs = 120000L;
		this.minimumThreadCount = 100;
		this.ensurePatientSeenAtDoD = true;
		return this;
	}
	
	public static void main(String [] args)
	{
		boolean returnDiagnosticUncompressedForDiagnosticRequests = true;
		boolean useCprsContextToGetPatientTreatingFacilitiyList = true;
		boolean callArtifactsSpi = true;
		long asynchronousCommandWaiterTimeoutMs = 120000L;
		int minimumThreadCount = 100;
		boolean ensurePatientSeenAtDoD = true;
		if(args.length == 1)
		{
			returnDiagnosticUncompressedForDiagnosticRequests = Boolean.parseBoolean(args[0]);
		}
		else if(args.length == 2)
		{
			returnDiagnosticUncompressedForDiagnosticRequests = Boolean.parseBoolean(args[0]);
			useCprsContextToGetPatientTreatingFacilitiyList = Boolean.parseBoolean(args[1]);
		}
		else if(args.length == 3)
		{
			returnDiagnosticUncompressedForDiagnosticRequests = Boolean.parseBoolean(args[0]);
			useCprsContextToGetPatientTreatingFacilitiyList = Boolean.parseBoolean(args[1]);
			callArtifactsSpi = Boolean.parseBoolean(args[2]);
		}
		else if(args.length == 4)
		{
			returnDiagnosticUncompressedForDiagnosticRequests = Boolean.parseBoolean(args[0]);
			useCprsContextToGetPatientTreatingFacilitiyList = Boolean.parseBoolean(args[1]);
			callArtifactsSpi = Boolean.parseBoolean(args[2]);
			ensurePatientSeenAtDoD = Boolean.parseBoolean(args[3]);
		}
		
		CommandConfiguration config = getCommandConfiguration();
		config.setReturnDiagnosticUncompressedForDiagnosticRequests(returnDiagnosticUncompressedForDiagnosticRequests);
		config.setUseCprsContextToGetPatientTreatingFacilitiyList(useCprsContextToGetPatientTreatingFacilitiyList);
		config.setCallArtifactSpi(callArtifactsSpi);
		config.setAsynchronousCommandWaiterTimeoutMs(asynchronousCommandWaiterTimeoutMs);
		config.setMinimumThreadCount(minimumThreadCount);
		config.setEnsurePatientSeenAtDoD(ensurePatientSeenAtDoD);
		config.storeConfiguration();
	}
}
