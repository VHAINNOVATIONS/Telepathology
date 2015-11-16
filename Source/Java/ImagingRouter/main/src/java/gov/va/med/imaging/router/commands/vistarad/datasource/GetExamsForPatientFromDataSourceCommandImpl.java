/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Oct 13, 2009
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
package gov.va.med.imaging.router.commands.vistarad.datasource;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.datasource.VistaRadDataSourceSpi;
import gov.va.med.imaging.exchange.business.vistarad.ExamListResult;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author vhaiswwerfej
 *
 */
public class GetExamsForPatientFromDataSourceCommandImpl 
extends AbstractDataSourceCommandImpl<ExamListResult, VistaRadDataSourceSpi> 
{
	private static final long serialVersionUID = 3380207008296059580L;
	
	private static final String SPI_METHOD_NAME = "getExamsForPatient";
	
	private final RoutingToken routingToken;
	private final String patientIcn;
	private final boolean fullyLoadExams;
	private final boolean forceRefresh;
	private final boolean forceImagesFromJb;
	
	public GetExamsForPatientFromDataSourceCommandImpl(RoutingToken routingToken, String patientIcn, 
			boolean fullyLoadExams, boolean forceRefresh, boolean forceImagesFromJb)
	{
		this.routingToken = routingToken;
		this.patientIcn = patientIcn;
		this.fullyLoadExams = fullyLoadExams;
		this.forceRefresh = forceRefresh;
		this.forceImagesFromJb = forceImagesFromJb;
	}

	/**
	 * @return the patientIcn
	 */
	public String getPatientIcn() {
		return patientIcn;
	}

	/**
	 * @return the fullyLoadExams
	 */
	public boolean isFullyLoadExams() {
		return fullyLoadExams;
	}

	@Override
	public RoutingToken getRoutingToken()
	{
		return routingToken;
	}

	public boolean isForceRefresh()
	{
		return forceRefresh;
	}

	public boolean isForceImagesFromJb()
	{
		return forceImagesFromJb;
	}

	@Override
	protected Object[] getSpiMethodParameters()
	{
		return new Object[]{getRoutingToken(), getPatientIcn(), isFullyLoadExams(), 
				isForceRefresh(), isForceImagesFromJb()};
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes()
	{
		return new Class<?>[]{RoutingToken.class, String.class, boolean.class, boolean.class, boolean.class};
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getCommandResult(gov.va.med.imaging.datasource.VersionableDataSourceSpi)
	 */
	@Override
	protected ExamListResult getCommandResult(VistaRadDataSourceSpi spi)
	throws ConnectionException, MethodException 
	{
		return spi.getExamsForPatient(getRoutingToken(), getPatientIcn(), isFullyLoadExams(), 
				isForceRefresh(), isForceImagesFromJb());
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
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSiteNumber()
	 */
	@Override
	protected String getSiteNumber() 
	{
		return routingToken.getRepositoryUniqueId();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodName()
	 */
	@Override
	protected String getSpiMethodName() 
	{
		return SPI_METHOD_NAME;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#parameterToString()
	 */
	@Override
	protected String parameterToString() 
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(getRoutingToken().toString());
		sb.append(", ");
		sb.append(getPatientIcn());
		sb.append(", ");
		sb.append(isFullyLoadExams());
		sb.append(", ");
		sb.append(isForceRefresh());
		sb.append(", ");
		sb.append(isForceImagesFromJb());
		
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#postProcessResult(java.lang.Object)
	 */
	@Override
	protected ExamListResult postProcessResult(ExamListResult result) 
	{
		TransactionContextFactory.get().setDataSourceEntriesReturned(result == null ? 0 : result.getArtifactSize());
		return result;
	}

}
