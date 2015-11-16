/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 24, 2009
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
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * Retrieve the exam requisition report. If the requisition report is in the cache then
 * it will retrieve from there, otherwise the report will come from the data source.
 * 
 * @author vhaiswwerfej
 *
 */
public class GetExamRequisitionReportCommandImpl
extends AbstractExamCommandImpl<String>
{
	private static final long serialVersionUID = 4374910127413845287L;
	
	private final StudyURN studyUrn;
	
	public GetExamRequisitionReportCommandImpl(StudyURN studyUrn)
	{
		this.studyUrn = studyUrn;
	}

	/**
	 * @return the studyUrn
	 */
	public StudyURN getStudyUrn() {
		return studyUrn;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.commands.vistarad.AbstractExamCommandImpl#areClassSpecificFieldsEqual(java.lang.Object)
	 */
	@Override
	protected boolean areClassSpecificFieldsEqual(Object obj) 
	{
		// Perform cast for subsequent tests
		final GetExamRequisitionReportCommandImpl other = (GetExamRequisitionReportCommandImpl) obj;
		
		// Check the studyUrn
		return areFieldsEqual(this.studyUrn, other.studyUrn);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#callSynchronouslyInTransactionContext()
	 */
	@Override
	public String callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException 
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		getLogger().info("Getting exam requisition report '" + getStudyUrn() + ", transaction (" + transactionContext.getTransactionId() + ").");
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
		
		if((exam != null) && (exam.isExamRequisitionReportLoaded()))
		{
			getLogger().info("Got requisition report for exam '" + getStudyUrn().toString() + "' from cache");
			transactionContext.setItemCached(Boolean.TRUE);
			return exam.getExamRequisitionReport();
		}
		// do asynch command to populate exam site while getting report?
		getLogger().info("DID NOT get requisition report for exam '" + getStudyUrn().toString() + "' from cache, requesting from data source");
		transactionContext.setItemCached(Boolean.FALSE);
		return getExamRequisitionReport(getStudyUrn());
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((studyUrn == null) ? 0 : studyUrn.hashCode());
		return result;
	}
}
