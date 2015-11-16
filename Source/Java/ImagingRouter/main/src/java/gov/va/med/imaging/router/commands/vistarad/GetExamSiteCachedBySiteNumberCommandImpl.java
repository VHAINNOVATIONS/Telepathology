/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Oct 12, 2009
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
import gov.va.med.RoutingTokenImpl;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.vistarad.ExamSite;
import gov.va.med.imaging.exchange.business.vistarad.ExamSiteCachedStatus;
import gov.va.med.imaging.exchange.enums.ArtifactResultStatus;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vhaiswwerfej
 *
 */
public class GetExamSiteCachedBySiteNumberCommandImpl 
extends AbstractExamCommandImpl<List<ExamSiteCachedStatus>> 
{
	private static final long serialVersionUID = 5574516810169344593L;
	
	private final String patientIcn;
	private final RoutingToken[] routingTokens;
	
	public GetExamSiteCachedBySiteNumberCommandImpl(String patientIcn, RoutingToken[] routingTokens)
	{
		this.routingTokens = routingTokens;
		this.patientIcn = patientIcn;
	}

	public RoutingToken[] getRoutingTokens()
	{
		return this.routingTokens;
	}

	/**
	 * @return the patientIcn
	 */
	public String getPatientIcn() {
		return patientIcn;
	}

	@Override
	public List<ExamSiteCachedStatus> callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException 
	{
		getLogger().info("Finding cached exam site status for '" + getRoutingTokens().length + "' site numbers for patient '" + getPatientIcn() + "'.");
		List<ExamSiteCachedStatus> result = new ArrayList<ExamSiteCachedStatus>();
		for(RoutingToken routingToken : this.getRoutingTokens())
		{
			ExamSite examSite = this.getExamSiteFromCache(routingToken, getPatientIcn());
			boolean cached = false;
			
			if((examSite != null) && (examSite.getArtifactResultStatus() == ArtifactResultStatus.fullResult))
				cached = true;
												
			result.add(new ExamSiteCachedStatus(getPatientIcn(), routingToken, cached));
		}
		TransactionContext transactionContext = TransactionContextFactory.get();
		
		String localSiteNumber = getCommandContext().getLocalSite().getSite().getSiteNumber();
		try
		{
			RoutingToken routingToken = RoutingTokenImpl.createVARadiologySite(localSiteNumber);
			transactionContext.setServicedSource(routingToken.toRoutingTokenString());
		}
		catch(RoutingTokenFormatException rtfX)
		{
			getLogger().warn("Error creating routing token from local site number '" + localSiteNumber + "' in GetExamSiteCachedBySiteNumberCommandImpl, " + rtfX.getMessage());
		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.commands.vistarad.AbstractExamCommandImpl#areClassSpecificFieldsEqual(java.lang.Object)
	 */
	@Override
	protected boolean areClassSpecificFieldsEqual(Object obj) 
	{
		// Perform cast for subsequent tests
		final GetExamSiteCachedBySiteNumberCommandImpl other = (GetExamSiteCachedBySiteNumberCommandImpl) obj;
		
		boolean allEqual = true;
		allEqual = allEqual && areFieldsEqual(this.getRoutingTokens(), other.getRoutingTokens());
		allEqual = allEqual && areFieldsEqual(this.getPatientIcn(), other.getPatientIcn());
		
		return allEqual;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#parameterToString()
	 */
	@Override
	protected String parameterToString() 
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.getPatientIcn());
		sb.append(',');
		sb.append(this.getRoutingTokens());
		
		return sb.toString();
	}
}
