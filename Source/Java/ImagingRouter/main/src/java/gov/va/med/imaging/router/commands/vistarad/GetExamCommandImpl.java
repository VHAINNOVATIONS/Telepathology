/**
 * 
 */
package gov.va.med.imaging.router.commands.vistarad;

import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.vistarad.Exam;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * A command to get a List of Study instances:
 * 1.) from a single Site
 * 2.) related to a single patient
 * 3.) meeting the criteria of the given StudyFilter instance
 * 
 * @author vhaiswlouthj
 *
 */
public class GetExamCommandImpl 
extends AbstractExamCommandImpl<Exam> 
{
	private static final long serialVersionUID = -4963797794965394068L;
	private final StudyURN studyUrn;
	
	/**
	 * @param router
	 * @param asynchronousMethodProcessor
	 */
	public GetExamCommandImpl(StudyURN studyUrn)
	{
		super();
		this.studyUrn = studyUrn;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.studyUrn == null) ? 0 : this.studyUrn.hashCode());
		return result;
	}

	@Override
	protected boolean areClassSpecificFieldsEqual(Object obj)
	{
		// Perform cast for subsequent tests
		final GetExamCommandImpl other = (GetExamCommandImpl) obj;
		
		// Check the studyUrn
		return areFieldsEqual(this.studyUrn, other.studyUrn);
	}

	public StudyURN getStudyUrn()
	{
		return this.studyUrn;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AsynchronousCommandProcessor#parameterToString()
	 */
	@Override
	protected String parameterToString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.getStudyUrn());
		
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AsynchronousCommandProcessor#callInTransactionContext()
	 */
	@Override
	public Exam callSynchronouslyInTransactionContext()
	throws MethodException
	{		
		TransactionContext transactionContext = TransactionContextFactory.get();
		getLogger().info("Getting exam '" + getStudyUrn() + ", transaction (" + transactionContext.getTransactionId() + ").");
		transactionContext.setServicedSource(getStudyUrn().toRoutingTokenString());
		
		Exam exam = this.getFullyLoadedExam(getStudyUrn());
		return exam;
	}
}
