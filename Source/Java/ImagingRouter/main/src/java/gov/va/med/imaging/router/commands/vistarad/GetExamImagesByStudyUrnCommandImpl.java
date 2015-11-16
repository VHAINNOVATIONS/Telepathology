/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jan 7, 2010
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
package gov.va.med.imaging.router.commands.vistarad;

import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.vistarad.Exam;
import gov.va.med.imaging.exchange.business.vistarad.ExamImages;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author vhaiswwerfej
 *
 */
public class GetExamImagesByStudyUrnCommandImpl 
extends AbstractExamCommandImpl<ExamImages>
{
	private static final long serialVersionUID = 4244995392009977745L;
	private final StudyURN studyUrn;
	
	public GetExamImagesByStudyUrnCommandImpl(StudyURN studyUrn)
	{
		this.studyUrn = studyUrn;
	}
	
	/**
	 * @return the studyUrn
	 */
	public StudyURN getStudyUrn() 
	{
		return studyUrn;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.commands.vistarad.AbstractExamCommandImpl#areClassSpecificFieldsEqual(java.lang.Object)
	 */
	@Override
	protected boolean areClassSpecificFieldsEqual(Object obj) 
	{
		// Perform cast for subsequent tests
		final GetExamImagesByStudyUrnCommandImpl other = 
			(GetExamImagesByStudyUrnCommandImpl) obj;
		
		// Check the studyUrn
		return areFieldsEqual(this.studyUrn, other.studyUrn);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#callSynchronouslyInTransactionContext()
	 */
	@Override
	public ExamImages callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException 
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		getLogger().info("Getting exam images for study '" + getStudyUrn() + ", transaction (" + transactionContext.getTransactionId() + ").");
		transactionContext.setServicedSource(getStudyUrn().toRoutingTokenString());
		
		Exam exam = null;
		try
		{
			exam = getExamFromCache(getStudyUrn());
		}
		catch (URNFormatException x)
		{
			throw new MethodException(x);
		}
		
		if((exam != null) &&  (exam.isImagesIncluded()))
		{
			getLogger().info("Got images for exam '" + getStudyUrn().toString() + "' from cache");
			transactionContext.setItemCached(Boolean.TRUE);
			return exam.getImages();
		}
		getLogger().info("DID NOT get exam images for exam '" + getStudyUrn().toString() + "' from cache, requesting from data source");
		transactionContext.setItemCached(Boolean.FALSE);
		return getExamImagesFromDataSource(getStudyUrn());
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#parameterToString()
	 */
	@Override
	protected String parameterToString() 
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.getStudyUrn());
		
		return sb.toString();
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
}
