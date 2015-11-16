/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Oct 15, 2010
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
package gov.va.med.imaging.router.commands.artifacts.datasource;

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.datasource.PatientArtifactDataSourceSpi;
import gov.va.med.imaging.exchange.business.ArtifactResults;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author vhaiswwerfej
 *
 */
public abstract class AbstractArtifactResultsBySiteNumberDataSourceCommandImpl
extends AbstractDataSourceCommandImpl<ArtifactResults, PatientArtifactDataSourceSpi>
{
	private static final long serialVersionUID = 7021967343413372547L;
	
	private final RoutingToken routingToken;
	private final PatientIdentifier patientIdentifier; 
	private final StudyFilter filter;
	private final StudyLoadLevel studyLoadLevel;
	private final boolean includeRadiology;
	private final boolean includeDocuments;
	
	private static final Class<PatientArtifactDataSourceSpi> SPI_CLASS = PatientArtifactDataSourceSpi.class; 
	private static final String SPI_METHOD_NAME = "getPatientArtifacts";
	private static final Class<?>[] SPI_METHOD_PARAMETER_TYPES = 
		new Class<?>[]{RoutingToken.class, PatientIdentifier.class, StudyFilter.class, StudyLoadLevel.class, 
			boolean.class, boolean.class};
	
	public AbstractArtifactResultsBySiteNumberDataSourceCommandImpl(
		RoutingToken routingToken, 
		PatientIdentifier patientIdentifier, 
		StudyFilter filter, 
		StudyLoadLevel studyLoadLevel,
		boolean includeRadiology,
		boolean includeDocuments)
	{
		this.routingToken = routingToken;
		this.patientIdentifier = patientIdentifier;
		this.filter = filter;
		this.studyLoadLevel = studyLoadLevel;
		this.includeDocuments = includeDocuments;
		this.includeRadiology = includeRadiology;
		
	}

	@Override
	protected ArtifactResults getCommandResult(PatientArtifactDataSourceSpi spi)
	throws ConnectionException, MethodException
	{
		return spi.getPatientArtifacts(getRoutingToken(),
				getPatientIdentifier(), getFilter(), getStudyLoadLevel(), isIncludeRadiology(), isIncludeDocuments());
	}

	@Override
	public RoutingToken getRoutingToken()
	{
		return routingToken;
	}

	@Override
	protected String getSiteNumber()
	{
		return routingToken.getRepositoryUniqueId();
	}

	@Override
	protected Class<PatientArtifactDataSourceSpi> getSpiClass()
	{
		return SPI_CLASS;
	}

	@Override
	protected String getSpiMethodName()
	{
		return SPI_METHOD_NAME;
	}

	@Override
	protected Object[] getSpiMethodParameters()
	{
		return new Object[]{getRoutingToken(), getPatientIdentifier(), getFilter(), getStudyLoadLevel(), 
				isIncludeRadiology(), isIncludeDocuments()};	
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes()
	{
		return SPI_METHOD_PARAMETER_TYPES;
	}

	public PatientIdentifier getPatientIdentifier()
	{
		return patientIdentifier;
	}

	public StudyFilter getFilter()
	{
		return filter;
	}

	public StudyLoadLevel getStudyLoadLevel()
	{
		return studyLoadLevel;
	}

	public boolean isIncludeRadiology()
	{
		return includeRadiology;
	}

	public boolean isIncludeDocuments()
	{
		return includeDocuments;
	}

	@Override
	protected ArtifactResults postProcessResult(ArtifactResults result)
	{
		TransactionContextFactory.get().setDataSourceEntriesReturned(result == null ? 0 : result.getArtifactSize());
		return result;
	}

}
