/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 27, 2009
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

import gov.va.med.RoutingToken;
import gov.va.med.imaging.CprsIdentifier;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.router.facade.ImagingContext;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import java.util.List;

/**
 * Command to retrieve a study from a given CPRS identifier from a specific site.
 * 
 * @author vhaiswwerfej
 *
 */
public class GetStudiesByCprsIdentifierCommandImpl 
extends AbstractStudyCommandImpl<List<Study>>
{
	private static final long serialVersionUID = -6605009044618808407L;
	private final RoutingToken routingToken;
	private final CprsIdentifier cprsIdentifier;
	private final String patientIcn;
	
	public GetStudiesByCprsIdentifierCommandImpl(String patientIcn, 
			RoutingToken routingToken, CprsIdentifier cprsIdentifier)
	{
		super();
		this.cprsIdentifier = cprsIdentifier;
		this.routingToken = routingToken;
		this.patientIcn = patientIcn;
	}

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
	 * @return the cprsIdentifier
	 */
	public CprsIdentifier getCprsIdentifier() 
	{
		return cprsIdentifier;
	}

	/**
	 * @return the siteNumber
	 */
	public String getSiteNumber() {
		return getRoutingToken().getRepositoryUniqueId();
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
		transactionContext.setPatientID(patientIcn);
		transactionContext.setItemCached(Boolean.FALSE);
		return getStudyByCprsIdentifier();
	}
	
	protected List<Study> getStudyByCprsIdentifier()
	throws MethodException
	{
		
		if((getPatientIcn() == null) || (getPatientIcn().length() <= 0))
		{
			throw new MethodException("Missing required patient Icn parameter");
		}
		
		if((getSiteNumber() == null) || (getSiteNumber().length() <= 0))
		{
			throw new MethodException("Missing required site number parameter");
		}
		
		try
		{
			List<Study> studies = ImagingContext.getRouter().getStudyFromCprsIdentifier(
				getRoutingToken(), 
				getPatientIcn(), 
				getCprsIdentifier() );			
			getLogger().info("Got " + (studies == null ? "null" : studies.size()) + 
					" patient '" + patientIcn + "' studies.");
			
			// a null Study Set indicates no studies meet the search criteria
			if(studies != null)
			{
				CommonStudyCacheFunctions.cacheStudyList(getCommandContext(), getSiteNumber(), studies);
				return studies;
			}
			else
				return null;	
		}
		catch(ConnectionException cX)
		{
			throw new MethodConnectionException(cX);
		}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#parameterToString()
	 */
	@Override
	protected String parameterToString() 
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.getPatientIcn());		
		sb.append(this.getRoutingToken());
		sb.append(this.getCprsIdentifier());		
		
		return sb.toString();
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.cprsIdentifier == null) ? 0 : this.cprsIdentifier.hashCode());
		result = prime * result + ((this.patientIcn == null) ? 0 : this.patientIcn.hashCode());
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
		final GetStudiesByCprsIdentifierCommandImpl other = (GetStudiesByCprsIdentifierCommandImpl) obj;
		if (this.cprsIdentifier == null)
		{
			if (other.cprsIdentifier != null)
				return false;
		}
		else if (!this.cprsIdentifier.equals(other.cprsIdentifier))
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
