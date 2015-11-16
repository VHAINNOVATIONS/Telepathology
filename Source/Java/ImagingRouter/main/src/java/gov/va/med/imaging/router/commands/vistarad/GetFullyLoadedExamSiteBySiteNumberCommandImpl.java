/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 18, 2009
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

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.annotations.routerfacade.RouterCommandExecution;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.vistarad.ExamSite;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author vhaiswwerfej
 *
 */
@RouterCommandExecution(asynchronous=true, distributable=false)
public class GetFullyLoadedExamSiteBySiteNumberCommandImpl 
extends AbstractExamCommandImpl<ExamSite> 
{
	private static final long serialVersionUID = 8247309543323116975L;
	
	private final RoutingToken routingToken;
	private final String patientIcn; 
	private final boolean forceRefresh;
	private final boolean forceImagesFromJb;	
	
	/**
	 * 
	 * @param site
	 * @param patientIcn
	 * @param fullyLoadExams
	 * @param forceRefresh True to force the data to come from the datasource and not the cache. False to allow the data to come from the cache 
	 */
	public GetFullyLoadedExamSiteBySiteNumberCommandImpl(
		RoutingToken routingToken, 
		String patientIcn, 
		boolean forceRefresh,
		boolean forceImagesFromJb)
	{
		super();
		this.routingToken = routingToken;
		this.patientIcn = patientIcn;
		this.forceRefresh = forceRefresh;
		this.forceImagesFromJb = forceImagesFromJb;
	}

//	public Site getSite()
//	{
//		if((this.site == null) && (this.siteNumber != null))
//		{
//			try
//			{
//				CommandContext context = getCommandContext();
//				this.site = context.getSite(this.siteNumber.getSiteNumber()).getSite();
//			}
//			catch(MethodException mX)
//			{
//				getLogger().error("Error getting site from site number '" + siteNumber + "'", mX);
//				this.site = null;
//				// set site number to null to prevent trying again (since it won't work!)
//				this.siteNumber = null;
//			}
//		}
//		return this.site;
//	}
//
//	public String getSiteNumber()
//	{
//		return getSite().getSiteNumber();
//	}

	
	public RoutingToken getRoutingToken()
	{
		return this.routingToken;
	}
	
	/**
	 * @return the patientIcn
	 */
	public String getPatientIcn() {
		return patientIcn;
	}

	/**
	 * @return the forceRefresh
	 */
	public boolean isForceRefresh() {
		return forceRefresh;
	}

	public boolean isForceImagesFromJb()
	{
		return forceImagesFromJb;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.commands.vistarad.AbstractExamCommandImpl#areClassSpecificFieldsEqual(java.lang.Object)
	 */
	@Override
	protected boolean areClassSpecificFieldsEqual(Object obj) 
	{
		// Perform cast for subsequent tests
		final GetFullyLoadedExamSiteBySiteNumberCommandImpl other = (GetFullyLoadedExamSiteBySiteNumberCommandImpl) obj;
		
		// Check the patient id and siteNumber
		boolean allEqual = true;
		allEqual = allEqual && areFieldsEqual(this.getPatientIcn(), other.getPatientIcn());
		allEqual = allEqual && areFieldsEqual(this.getRoutingToken(), other.getRoutingToken());
		allEqual = allEqual && areFieldsEqual(this.forceRefresh, other.forceRefresh);
		allEqual = allEqual && areFieldsEqual(this.forceImagesFromJb, other.forceImagesFromJb);
		
		return allEqual;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#callSynchronouslyInTransactionContext()
	 */
	@Override
	public ExamSite callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException 
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		getLogger().info("Getting fully loaded exam site for site '" + getRoutingToken()
				 + "', patient '" + getPatientIcn() + "', transaction (" + transactionContext.getTransactionId() + ").");
		transactionContext.setServicedSource(this.getRoutingToken().toRoutingTokenString());
		
		ExamSite examSite = this.getExamSite(this.getRoutingToken(), this.getPatientIcn(), 
				true, this.isForceRefresh(), isForceImagesFromJb());
		return examSite;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#parameterToString()
	 */
	@Override
	protected String parameterToString() 
	{		
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.getRoutingToken());
		sb.append(',');
		sb.append(this.getPatientIcn());
		sb.append(',');
		sb.append(this.isForceRefresh());
		sb.append(',');
		sb.append(this.isForceImagesFromJb());
		
		return sb.toString();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (this.forceRefresh ? 1231 : 1237);
		result = prime * result + ((this.patientIcn == null) ? 0 : this.patientIcn.hashCode());
		result = prime * result + ((this.routingToken == null) ? 0 : this.routingToken.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final GetFullyLoadedExamSiteBySiteNumberCommandImpl other = (GetFullyLoadedExamSiteBySiteNumberCommandImpl) obj;
		if (this.forceRefresh != other.forceRefresh)
			return false;
		if (this.patientIcn == null)
		{
			if (other.patientIcn != null)
				return false;
		}
		else if (!this.patientIcn.equals(other.patientIcn))
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
