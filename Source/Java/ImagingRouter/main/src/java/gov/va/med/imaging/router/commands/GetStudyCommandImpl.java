/**
 * 
 */
package gov.va.med.imaging.router.commands;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * A command to get a List of Study instances:
 * 1.) from a single Site
 * 2.) related to a single patient
 * 3.) meeting the criteria of the given StudyFilter instance
 * 
 * @author vhaiswbeckec
 *
 */
public class GetStudyCommandImpl 
extends AbstractStudyCommandImpl<Study> 
{
	private static final long serialVersionUID = -4963797794965394068L;
	private final GlobalArtifactIdentifier studyIdentifier;
	private final boolean includeDeletedImages;
	
	/**
	 * @param studyIdentifier
	 */
	public GetStudyCommandImpl(GlobalArtifactIdentifier studyIdentifier)
	{
		this(studyIdentifier, false);
	}
	
	/**
	 * @param studyIdentifier
	 */
	public GetStudyCommandImpl(GlobalArtifactIdentifier studyIdentifier, boolean includeDeletedImages)
	{
		super();
		this.studyIdentifier = studyIdentifier;
		this.includeDeletedImages = includeDeletedImages;
	}

	public GlobalArtifactIdentifier getStudyIdentifier()
	{
		return this.studyIdentifier;
	}

	public boolean isIncludeDeletedImages()
	{
		return includeDeletedImages;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AsynchronousCommandProcessor#parameterToString()
	 */
	@Override
	protected String parameterToString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.getStudyIdentifier());
		sb.append(this.isIncludeDeletedImages());
		
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AsynchronousCommandProcessor#callInTransactionContext()
	 */
	@Override
	public Study callSynchronouslyInTransactionContext()
	throws MethodException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setServicedSource(getStudyIdentifier().toRoutingTokenString());
		
		Study study = this.getPatientStudy(getStudyIdentifier(), isIncludeDeletedImages());
		return study;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.studyIdentifier == null) ? 0 : this.studyIdentifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		final GetStudyCommandImpl other = (GetStudyCommandImpl) obj;
		if (this.studyIdentifier == null)
		{
			if (other.studyIdentifier != null)
				return false;
		}
		else if (!this.studyIdentifier.equals(other.studyIdentifier))
			return false;
		return true;
	}
	
}
