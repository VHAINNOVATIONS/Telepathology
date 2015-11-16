/**
 * 
 */
package gov.va.med.imaging.router.commands;

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.annotations.routerfacade.RouterCommandExecution;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import java.util.List;

/**
 * A command to get a List of Study instances:
 * 1.) from a single Site
 * 2.) related to a single patient
 * 3.) meeting the criteria of the given StudyFilter instance
 * 
 * @author vhaiswbeckec
 *
 */
@RouterCommandExecution(asynchronous=true, distributable=false)
public class GetStudyListBySiteCommandImpl 
extends AbstractStudyCommandImpl<List<Study>> 
{
	private static final long serialVersionUID = 5473568262532356886L;
	private final static StudyLoadLevel studyLoadLevel = StudyLoadLevel.FULL;
	
	private final RoutingToken routingToken;
	private final PatientIdentifier patientIdentifier; 
	private final StudyFilter filter;
	
	/**
	 * @param router
	 * @param asynchronousMethodProcessor
	 */
	public GetStudyListBySiteCommandImpl(
		RoutingToken routingToken,
		PatientIdentifier patientIdentifier, 
		StudyFilter filter)
	{
		super();
		this.routingToken = routingToken;
		this.patientIdentifier = patientIdentifier;
		this.filter = filter;
	}

	public RoutingToken getRoutingToken()
	{
		return this.routingToken;
	}

	public StudyFilter getFilter()
	{
		return this.filter;
	}

	public PatientIdentifier getPatientIdentifier()
	{
		return patientIdentifier;
	}

	public String getSiteNumber()
	{
		return this.getRoutingToken().getRepositoryUniqueId();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AsynchronousCommandProcessor#callInTransactionContext()
	 */
	@Override
	public List<Study> callSynchronouslyInTransactionContext()
	throws MethodException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setServicedSource(getRoutingToken().toRoutingTokenString());
		
		List<Study> studies = this.getPatientStudyList(getRoutingToken(), patientIdentifier, filter, studyLoadLevel);
		return studies;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AsynchronousCommandProcessor#parameterToString()
	 */
	@Override
	protected String parameterToString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.getRoutingToken());
		sb.append(',');
		sb.append(this.getPatientIdentifier());
		sb.append(',');
		sb.append(this.getFilter() == null ? "<null>" : this.getFilter().toString());
		
		return sb.toString();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.filter == null) ? 0 : this.filter.hashCode());
		result = prime * result + ((this.patientIdentifier == null) ? 0 : this.patientIdentifier.hashCode());
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
		final GetStudyListBySiteCommandImpl other = (GetStudyListBySiteCommandImpl) obj;
		if (this.filter == null)
		{
			if (other.filter != null)
				return false;
		}
		else if (!this.filter.equals(other.filter))
			return false;
		if (this.patientIdentifier == null)
		{
			if (other.patientIdentifier != null)
				return false;
		}
		else if (!this.patientIdentifier.equals(other.patientIdentifier))
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


}
