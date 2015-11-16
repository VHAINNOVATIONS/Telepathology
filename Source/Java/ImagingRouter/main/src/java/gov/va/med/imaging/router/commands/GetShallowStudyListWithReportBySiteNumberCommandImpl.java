/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 18, 2009
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
package gov.va.med.imaging.router.commands;

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import java.util.List;

/**
 * Command to get the list of studies for a patient including the reports but NOT including the images.
 * 
 * @author vhaiswwerfej
 *
 */
public class GetShallowStudyListWithReportBySiteNumberCommandImpl 
extends AbstractStudyCommandImpl<List<Study>>
{
	
	private static final long serialVersionUID = -8280863334578738906L;
	
	private final RoutingToken routingToken;
	private final PatientIdentifier patientIdentifier; 
	private final StudyFilter filter;
	
	private final static StudyLoadLevel studyLoadLevel = StudyLoadLevel.STUDY_AND_REPORT;
	
	public GetShallowStudyListWithReportBySiteNumberCommandImpl(
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

	/**
	 * @return the siteNumber
	 */
	public String getSiteNumber() {
		return getRoutingToken().getRepositoryUniqueId();
	}

	public PatientIdentifier getPatientIdentifier()
	{
		return patientIdentifier;
	}

	/**
	 * @return the filter
	 */
	public StudyFilter getFilter() {
		return filter;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#callSynchronouslyInTransactionContext()
	 */
	@Override
	public List<Study> callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException 
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setServicedSource(getRoutingToken().toRoutingTokenString());
		getLogger().info("Getting study list with report to site '" + getRoutingToken().toRoutingTokenString() + "'.");
		
		List<Study> studies = this.getPatientStudyList(getRoutingToken(), patientIdentifier, filter, studyLoadLevel);
		return studies;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) 
	{
		// command equivalence not allowed here since this data is never cached because StudyLoadLevel is study only
		return false;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#parameterToString()
	 */
	@Override
	protected String parameterToString() 
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.getSiteNumber());
		sb.append(',');
		sb.append(this.getPatientIdentifier());
		sb.append(',');
		sb.append(this.getFilter() == null ? "<null>" : this.getFilter().toString());
		
		return sb.toString();
	}
	

}
