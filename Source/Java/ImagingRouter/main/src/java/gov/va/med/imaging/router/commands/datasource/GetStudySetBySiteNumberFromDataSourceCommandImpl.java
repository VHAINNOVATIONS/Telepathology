/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Oct 15, 2009
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
package gov.va.med.imaging.router.commands.datasource;

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.router.exceptions.IncompatibleRoutingTokenException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.datasource.StudyGraphDataSourceSpi;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.business.StudySetResult;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author vhaiswwerfej
 *
 */
public class GetStudySetBySiteNumberFromDataSourceCommandImpl 
extends AbstractDataSourceCommandImpl<StudySetResult, StudyGraphDataSourceSpi> 
{
	private static final long serialVersionUID = -7994262458178015486L;
	private static final Class<StudyGraphDataSourceSpi> SPI_CLASS = StudyGraphDataSourceSpi.class; 
	private static final String SPI_METHOD_NAME = "getPatientStudies";
	private static final Class<?>[] SPI_METHOD_PARAMETER_TYPES = 
		new Class<?>[]{RoutingToken.class, PatientIdentifier.class, StudyFilter.class, StudyLoadLevel.class};
	
	private final RoutingToken routingToken;
	private final PatientIdentifier patientIdentifier; 
	private final StudyFilter filter;
	private final StudyLoadLevel studyLoadLevel;
	
	/**
	 * 
	 * @param routingToken
	 * @param patientId
	 * @param filter
	 * @param studyLoadLevel
	 * @throws IncompatibleRoutingTokenException 
	 */
	public GetStudySetBySiteNumberFromDataSourceCommandImpl(
		RoutingToken routingToken, 
		PatientIdentifier patientIdentifier, 
		StudyFilter filter, 
		StudyLoadLevel studyLoadLevel) 
	throws IncompatibleRoutingTokenException
	{
		validateRoutingToken(routingToken);
		this.routingToken = routingToken;
		this.patientIdentifier = patientIdentifier;
		this.filter = filter;
		this.studyLoadLevel = studyLoadLevel;
	}

	/**
	 * @param routingToken
	 */
	private void validateRoutingToken(RoutingToken routingToken)
	throws IncompatibleRoutingTokenException
	{
		if( routingToken == null )
			throw IncompatibleRoutingTokenException.createInvalidNull(this.getClass());
		if( RoutingToken.ROUTING_WILDCARD.equals(routingToken.getHomeCommunityId()) )
			throw IncompatibleRoutingTokenException.createInvalidHomeCommunityWildcard(this.getClass());
		if( RoutingToken.ROUTING_WILDCARD.equals(routingToken.getRepositoryUniqueId()) )
			throw IncompatibleRoutingTokenException.createInvalidRepositoryWildcard(this.getClass());
	}

	@Override
	public RoutingToken getRoutingToken()
	{
		return this.routingToken;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getDataSourceSpi(java.net.URL)
	 */
	@Override
	protected StudyGraphDataSourceSpi getDataSourceSpi(String protocol) 
	throws ConnectionException
	{
		return getProvider().createStudyGraphDataSource(resolvedArtifactSource, protocol);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getCommandResult(gov.va.med.imaging.datasource.VersionableDataSourceSpi)
	 */
	@Override
	protected StudySetResult getCommandResult(StudyGraphDataSourceSpi spi)
	throws ConnectionException, MethodException 
	{						
		return spi.getPatientStudies(getRoutingToken(), getPatientIdentifier(), getFilter(), getStudyLoadLevel());
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSiteNumber()
	 */
	@Override
	protected String getSiteNumber() 
	{
		return routingToken.getRepositoryUniqueId();
	}

	/**
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiClass()
	 */
	@Override
	protected Class<StudyGraphDataSourceSpi> getSpiClass() 
	{
		return SPI_CLASS;
	}

	/**
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodName()
	 */
	@Override
	protected String getSpiMethodName() {
		return SPI_METHOD_NAME;
	}
	
	/**
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodParameterTypes()
	 */
	@Override
	protected Class<?>[] getSpiMethodParameterTypes()
	{
		return SPI_METHOD_PARAMETER_TYPES;
	}
	
	/**
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodParameters()
	 */
	@Override
	protected Object[] getSpiMethodParameters()
	{
		return new Object[]{getRoutingToken(), getPatientIdentifier(), getFilter(), getStudyLoadLevel()};
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

	/**
	 * @return the studyLoadLevel
	 */
	public StudyLoadLevel getStudyLoadLevel() {
		return studyLoadLevel;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#postProcessResult(java.lang.Object)
	 */
	@Override
	protected StudySetResult postProcessResult(StudySetResult result) 
	{
		TransactionContextFactory.get().setDataSourceEntriesReturned(result == null ? 0 : result.getArtifactSize());
		return result;
	}
}
