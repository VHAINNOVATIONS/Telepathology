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

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.datasource.VistaRadDataSourceSpi;
import gov.va.med.imaging.exchange.business.vistarad.ActiveExams;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * Command to get the active worklist from a specified site. This will return an array
 * of strings which is the raw result from the data source with no translation on the data.
 * This result of this command is never cached.
 * 
 * @author vhaiswwerfej
 *
 */
public class GetActiveWorklistCommandImpl
extends AbstractDataSourceCommandImpl<ActiveExams, VistaRadDataSourceSpi>
{

	private final RoutingToken routingToken;
	private final String listDescriptor;
	
	private static final String SPI_METHOD_NAME = "getActiveExams";	
	
	public GetActiveWorklistCommandImpl(RoutingToken routingToken, String listDescriptor)
	{
		this.listDescriptor = listDescriptor;
		this.routingToken = routingToken;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4127158209988714171L;
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getCommandResult(gov.va.med.imaging.datasource.VersionableDataSourceSpi)
	 */
	@Override
	protected ActiveExams getCommandResult(VistaRadDataSourceSpi spi)
	throws ConnectionException, MethodException 
	{		
		ActiveExams activeExamsResult = spi.getActiveExams(getRoutingToken(), getListDescriptor());
		return activeExamsResult;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiClass()
	 */
	@Override
	protected Class<VistaRadDataSourceSpi> getSpiClass() 
	{
		return VistaRadDataSourceSpi.class;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#postProcessResult(java.lang.Object)
	 */
	@Override
	protected ActiveExams postProcessResult(ActiveExams result) 
	{
		getLogger().info("Got " + ((result == null || result.size() == 0) ? "0" : result.size()) + " active exams.");
		TransactionContextFactory.get().setDataSourceEntriesReturned(result == null ? 0 : result.size());
		return result;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodName()
	 */
	@Override
	protected String getSpiMethodName() 
	{
		return SPI_METHOD_NAME;
	}

	@Override
	public RoutingToken getRoutingToken()
	{
		return this.routingToken;
	}

	@Override
	protected Object[] getSpiMethodParameters()
	{
		return new Object[]{getRoutingToken(), getListDescriptor()};
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes()
	{
		return new Class<?>[]{RoutingToken.class, String.class};
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) 
	{
		// Check objectEquivalence
		if (this == obj)
		{
			return true;
		}
		
		// Check that classes match
		if (getClass() != obj.getClass())
		{
			return false;
		}
		GetActiveWorklistCommandImpl that = (GetActiveWorklistCommandImpl)obj;
		return getSiteNumber().equals(that.getSiteNumber());
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#parameterToString()
	 */
	@Override
	protected String parameterToString() 
	{
		StringBuffer sb = new StringBuffer();		
		sb.append(this.getSiteNumber());		
		sb.append(this.getListDescriptor());
		return sb.toString();
	}

	/**
	 * @return the siteNumber
	 */
	public String getSiteNumber() 
	{
		return getRoutingToken().getRepositoryUniqueId();
	}

	/**
	 * @return the listDescriptor
	 */
	public String getListDescriptor() {
		return listDescriptor;
	}
}
