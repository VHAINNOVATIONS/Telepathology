/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: March 12, 2013
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswlouthj
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

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.core.router.commands.dicom.importer.AbstractDicomImporterDataSourceCommandImpl;
import gov.va.med.imaging.datasource.DicomImporterDataSourceSpi;
import gov.va.med.imaging.datasource.UserDataSourceSpi;
import gov.va.med.imaging.exchange.business.ApplicationTimeoutParameters;
import gov.va.med.imaging.exchange.business.dicom.importer.Order;
import gov.va.med.imaging.exchange.business.dicom.importer.OrderingLocation;
import gov.va.med.imaging.exchange.business.dicom.importer.OrderingProvider;
import gov.va.med.imaging.exchange.business.dicom.importer.Procedure;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

import java.util.List;

public class GetApplicationTimeoutParametersCommandImpl 
extends AbstractDataSourceCommandImpl<ApplicationTimeoutParameters, UserDataSourceSpi>
{
	private final String siteId;
	private final String applicationName;
	
	private static final long serialVersionUID = 1L;

	private static final String SPI_METHOD_NAME = "getApplicationTimeoutParameters";

	public GetApplicationTimeoutParametersCommandImpl(String siteId, String applicationName)
	{
		this.siteId = siteId;
		this.applicationName = applicationName;
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes() {
		return new Class<?>[]{String.class, String.class};
	}

	@Override
	protected Object[] getSpiMethodParameters() {
		return new Object[]{getSiteId(), getApplicationName()} ;
	}

	@Override
	protected String parameterToString()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getCommandResult(gov.va.med.imaging.datasource.VersionableDataSourceSpi)
	 */
	@Override
	protected ApplicationTimeoutParameters getCommandResult(
			UserDataSourceSpi spi) 
	throws ConnectionException, MethodException, SecurityCredentialsExpiredException 
	{
		return spi.getApplicationTimeoutParameters(getSiteId(), getApplicationName());
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodName()
	 */
	@Override
	protected String getSpiMethodName() {
		return SPI_METHOD_NAME;
	}
	
	public String getSiteId() {
		return siteId;
	}

	public String getApplicationName() {
		return applicationName;
	}
	
	@Override
	protected Class<UserDataSourceSpi> getSpiClass() 
	{
		return UserDataSourceSpi.class;
	}

	protected String getSiteNumber() 
	{
		return TransactionContextFactory.get().getSiteNumber();
	}

	public RoutingToken getRoutingToken() 
	{
		return getCommandContext().getLocalSite().getArtifactSource().createRoutingToken();
	}


}
