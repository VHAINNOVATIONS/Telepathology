/**
 * 
 */
package gov.va.med.imaging.core.router.commands;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.core.router.facade.InternalContext;
import gov.va.med.imaging.exchange.business.Patient;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A command to get a List of Patient instances:
 * 1.) from a single Site
 * 2.) with the name (partially) matching the given name
 * 
 * @author vhaiswbeckec
 *
 */
public class GetPatientListCommandImpl 
extends AbstractCommandImpl<List<Patient>> 
{
	private static final long serialVersionUID = 1559200704980670613L;
	private final RoutingToken routingToken;
	private final String patientName; 

	/**
	 * 
	 * @param patientName
	 * @param siteNumber
	 */
	public GetPatientListCommandImpl(
		String patientName,
		RoutingToken routingToken)
	{
		super();
		this.routingToken = routingToken;
		this.patientName = patientName;
	}


	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AsynchronousCommandProcessor#parameterToString()
	 */
	@Override
	protected String parameterToString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.getSiteNumber());
		sb.append(',');
		sb.append(this.getPatientName());
		
		return sb.toString();
	}

	public String getPatientName()
	{
		return this.patientName;
	}

	public RoutingToken getRoutingToken()
	{
		return this.routingToken;
	}

	public String getSiteNumber()
	{
		return getRoutingToken().getRepositoryUniqueId();
	}
	

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.patientName == null) ? 0 : this.patientName.hashCode());
		result = prime * result + ((this.routingToken == null) ? 0 : this.routingToken.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		final GetPatientListCommandImpl other = (GetPatientListCommandImpl) obj;
		if (this.patientName == null)
		{
			if (other.patientName != null)
				return false;
		}
		else if (!this.patientName.equals(other.patientName))
			return false;
		if (this.routingToken == null)
		{
			if (other.routingToken != null)
				return false;
		}
		else if (!this.routingToken.equals(other.routingToken))
			return false;
		return true;
	}


	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AsynchronousCommandProcessor#callInTransactionContext()
	 */
	@Override
	public List<Patient> callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setServicedSource(getRoutingToken().toRoutingTokenString());
		
		getLogger().info( "findPatients - Transaction ID [" + transactionContext.getTransactionId() + "] from site [" + getRoutingToken().toString() + "] for search [" + getPatientName() + "].");
		
		Set<Patient> patients = InternalContext.getRouter().getPatientsByName(
			getRoutingToken(), 
			getPatientName());

		getLogger().info("Got " + ((patients == null || patients.size() == 0) ? "no" : patients.size()) + 
				" patients for search '" + getPatientName() + "'.");
		
		// a null Study Set indicates no studies meet the search criteria
		if(patients != null)
		{
			List<Patient> patientList = new ArrayList<Patient>();
			patientList.addAll(patients);						
			return patientList;
		}
		else
		{
			return new ArrayList<Patient>(0);
		}
	}
}
