package gov.va.med.imaging.router.commands.vistarad;

import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.core.annotations.routerfacade.RouterCommandExecution;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.router.facade.ImagingContext;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * 
 * @author vhaiswlouthj
 *
 */
@RouterCommandExecution(asynchronous=false, distributable=false)
public class PrefetchExamImagesCommandImpl 
extends AbstractExamCommandImpl<Boolean> 
{
	private static final long serialVersionUID = 5473568262532356886L;
	
	private final StudyURN studyUrn; 
	
	/**
	 * @param router
	 * @param asynchronousMethodProcessor
	 */
	public PrefetchExamImagesCommandImpl(StudyURN studyUrn)
	{
		super();
		this.studyUrn = studyUrn;
	}

	public StudyURN getStudyUrn()
	{
		return this.studyUrn;
	}

	public String getSiteNumber()
	{
		return this.getStudyUrn().getOriginatingSiteId();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AsynchronousCommandProcessor#callInTransactionContext()
	 */
	@Override
	public Boolean callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException
	{
		getLogger().info("Prefetching exam images for exam '" + getStudyUrn() + "'.");
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setServicedSource(getStudyUrn().toRoutingTokenString());
		
		ImagingContext.getRouter().prefetchExamImages(getStudyUrn());
		
		
		return true;
	}

	@Override
	protected boolean areClassSpecificFieldsEqual(Object obj)
	{
		// Perform cast for subsequent tests
		final PrefetchExamImagesCommandImpl other = (PrefetchExamImagesCommandImpl) obj;
		
		// Check the patient id and siteNumber
		boolean allEqual = true;
		allEqual = allEqual && areFieldsEqual(getStudyUrn(), other.getStudyUrn());
		allEqual = allEqual && areFieldsEqual(getSiteNumber(), other.getSiteNumber());
		
		return allEqual;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		String siteNumber = this.getSiteNumber();
		result = prime * result + ((getStudyUrn() == null) ? 0 : getStudyUrn().hashCode());
		result = prime * result + ((siteNumber == null) ? 0 : siteNumber.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AsynchronousCommandProcessor#parameterToString()
	 */
	@Override
	protected String parameterToString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(getSiteNumber());
		sb.append(',');
		sb.append(getStudyUrn());
		
		return sb.toString();
	}

}
