/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Oct 16, 2009
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
package gov.va.med.imaging.core.router.commands.datasource;

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.datasource.PatientDataSourceSpi;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import java.util.List;

/**
 * @author vhaiswwerfej
 *
 */
public class GetTreatingSitesFromDataSourceCommandImpl 
extends AbstractDataSourceCommandImpl<List<String>, PatientDataSourceSpi>
{
	private static final long serialVersionUID = 4725241764963127500L;
	
	private final RoutingToken routingToken;
	private final PatientIdentifier patientIdentifier;
	private final boolean includeTrailingCharactersForSite200;
	
	private static final String SPI_METHOD_NAME = "getTreatingSites";
	
	public GetTreatingSitesFromDataSourceCommandImpl(RoutingToken routingToken, PatientIdentifier patientIdentifier,
			boolean includeTrailingCharactersForSite200)
	{
		this.routingToken = routingToken;
		this.patientIdentifier = patientIdentifier;
		this.includeTrailingCharactersForSite200 = includeTrailingCharactersForSite200;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getCommandResult(gov.va.med.imaging.datasource.VersionableDataSourceSpi)
	 */
	@Override
	protected List<String> getCommandResult(PatientDataSourceSpi spi)
	throws ConnectionException, MethodException 
	{
		return spi.getTreatingSites(getRoutingToken(), getPatientIdentifier(), isIncludeTrailingCharactersForSite200());
	}

	public RoutingToken getRoutingToken()
	{
		return this.routingToken;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSiteNumber()
	 */
	@Override
	protected String getSiteNumber() 
	{
		return getRoutingToken().getRepositoryUniqueId();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiClass()
	 */
	@Override
	protected Class<PatientDataSourceSpi> getSpiClass() 
	{
		return PatientDataSourceSpi.class;
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
	protected Object[] getSpiMethodParameters()
	{
		return new Object[]{getRoutingToken(), getPatientIdentifier(), isIncludeTrailingCharactersForSite200()};
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes()
	{
		return new Class<?>[]{RoutingToken.class, PatientIdentifier.class, boolean.class};
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#parameterToString()
	 */
	@Override
	protected String parameterToString() 
	{
		return getSiteNumber() + ", " + getPatientIdentifier();
	}

	public PatientIdentifier getPatientIdentifier()
	{
		return patientIdentifier;
	}

	public boolean isIncludeTrailingCharactersForSite200()
	{
		return includeTrailingCharactersForSite200;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#postProcessResult(java.lang.Object)
	 */
	@Override
	protected List<String> postProcessResult(List<String> result) 
	{
		TransactionContextFactory.get().setDataSourceEntriesReturned(result == null ? 0 : result.size());	
		return result;
	}
}
