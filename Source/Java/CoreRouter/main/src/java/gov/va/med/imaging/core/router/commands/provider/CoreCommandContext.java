/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 4, 2011
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
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
package gov.va.med.imaging.core.router.commands.provider;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.Router;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.router.CommandContext;
import gov.va.med.imaging.core.interfaces.router.CommandFactory;
import gov.va.med.imaging.datasource.DataSourceProvider;
import gov.va.med.imaging.datasource.DicomApplicationEntityDataSourceSpi;
import gov.va.med.imaging.datasource.DicomDataSourceSpi;
import gov.va.med.imaging.datasource.DicomImporterDataSourceSpi;
import gov.va.med.imaging.datasource.DicomQueryRetrieveDataSourceSpi;
import gov.va.med.imaging.datasource.DicomStorageDataSourceSpi;
import gov.va.med.imaging.datasource.SiteResolutionDataSourceSpi;
import gov.va.med.imaging.datasource.TransactionLoggerDataSourceSpi;
import gov.va.med.imaging.datasource.VersionableDataSourceSpi;
import gov.va.med.imaging.exchange.business.ResolvedSite;

/**
 * @author VHAISWWERFEJ
 *
 */
public class CoreCommandContext 
implements CommandContext
{
	private final CommandContext commandContext;
	private final DicomQueryRetrieveDataSourceSpi dicomQueryRetrieveService;
	private final DicomStorageDataSourceSpi dicomStorageService;
	private final DicomApplicationEntityDataSourceSpi dicomApplicationEntityService;
	private final DicomDataSourceSpi dicomService;
	private final DicomImporterDataSourceSpi dicomImporterService;
	
	public CoreCommandContext(CommandContext commandContext)
	{
		Logger.getLogger(CoreCommandContext.class).info("Creating new CoreCommandContext instance");
		this.commandContext = commandContext;
		ResolvedArtifactSource resolvedArtifactSource = null;
		if(getLocalSite() != null)
		{
			try
			{
				RoutingToken routingToken = getLocalSite().getArtifactSource().createRoutingToken();
					Logger.getLogger(CoreCommandContext.class).debug("Getting resolvedArtifactSource using routing token '" + routingToken.toRoutingTokenString() + "'.");
				resolvedArtifactSource = 
					getSiteResolver().resolveArtifactSource(routingToken);
			}
			catch (MethodException e)
			{
				String msg = "Failed to create resolvedArtifactSource to create local services during CoreCommandContext initialization, " + e.getMessage();
				Logger.getLogger(CoreCommandContext.class).error(msg, e);
				throw new ExceptionInInitializerError(msg);
			}
			catch (ConnectionException e)
			{
				String msg = "Failed to create resolvedArtifactSource to create local services during CoreCommandContext initialization, " + e.getMessage();
				Logger.getLogger(CoreCommandContext.class).error(msg, e);
				throw new ExceptionInInitializerError(msg);
			}
		}
		
		if(resolvedArtifactSource != null)
		{
			try
			{
				this.dicomQueryRetrieveService = getProvider().createLocalDicomQueryRetrieveDataSource(resolvedArtifactSource);
			}
			catch(ConnectionException x)
			{
				String msg = "Failed to get DICOM Query/Retrieve services during CoreCommandContext initialization, " + x.getMessage();
				Logger.getLogger(CoreCommandContext.class).error(msg);
				throw new ExceptionInInitializerError(msg);
			}
			
			try
			{
				this.dicomStorageService = getProvider().createLocalDicomStorageDataSource(resolvedArtifactSource);
			}
			catch(ConnectionException x)
			{
				String msg = "Failed to get DICOM Query/Retrieve services during CoreCommandContext initialization, " + x.getMessage();
				Logger.getLogger(CoreCommandContext.class).error(msg);
				throw new ExceptionInInitializerError(msg);
			}
	
			try
			{		
				this.dicomApplicationEntityService = getProvider().createLocalDicomApplicationEntityDataSource(resolvedArtifactSource);
			}
			catch(ConnectionException x)
			{
				String msg = "Failed to get DICOM Application Entity services during CoreCommandContext initialization, " + x.getMessage();
				Logger.getLogger(CoreCommandContext.class).error(msg);
				throw new ExceptionInInitializerError(msg);
			}
			
			try
			{
				this.dicomService = getProvider().createLocalDicomDataSource(resolvedArtifactSource);
			}
			catch(ConnectionException x)
			{
				String msg = "Failed to get DICOM services during CoreCommandContext initialization, " + x.getMessage();
				Logger.getLogger(CoreCommandContext.class).error(msg);
				throw new ExceptionInInitializerError(msg);
			}
			
			try
			{
				this.dicomImporterService = getProvider().createLocalDicomImporterDataSource(resolvedArtifactSource);
			}
			catch(ConnectionException x)
			{
				String msg = "Failed to get DICOM Importer services during CoreCommandContext initialization, " + x.getMessage();
				Logger.getLogger(CoreCommandContext.class).error(msg);
				throw new ExceptionInInitializerError(msg);
			}
		}
		else
		{
			// if everything is configured properly this should not happen. If this does happen verify VixConfig.xml is available - this is
			// the site number used to get the local site
			Logger.getLogger(CoreCommandContext.class).error("Could not initialize local services in CoreCommandContext, likely cannot find local site");
			dicomApplicationEntityService = null;
			dicomImporterService = null;
			dicomQueryRetrieveService = null;
			dicomService = null;
			dicomStorageService = null;
		}
	}

	@Override
	public Router getRouter() 
	{
		return commandContext.getRouter();
	}

	@Override
	public DataSourceProvider getProvider() 
	{
		return commandContext.getProvider();
	}

	@Override
	public SiteResolutionDataSourceSpi getSiteResolver() 
	{
		return commandContext.getSiteResolver();
	}

	@Override
	public CommandFactory getCommandFactory() 
	{
		return commandContext.getCommandFactory();
	}

	@Override
	public TransactionLoggerDataSourceSpi getTransactionLoggerService() 
	{
		return commandContext.getTransactionLoggerService();
	}

	@Override
	public boolean isCachingEnabled() 
	{
		return commandContext.isCachingEnabled();
	}

	@Override
	public ResolvedSite getLocalSite() 
	{
		return commandContext.getLocalSite();
	}

	@Override
	public ResolvedArtifactSource getResolvedArtifactSource(
			RoutingToken routingToken) 
	throws MethodException 
	{
		return commandContext.getResolvedArtifactSource(routingToken);
	}

	@Override
	public ResolvedArtifactSource getResolvedArtifactSource(
			RoutingToken routingToken,
			Class<? extends VersionableDataSourceSpi> spi, Method method,
			Object[] parameters) 
	throws MethodException 
	{
		return commandContext.getResolvedArtifactSource(routingToken, spi, method, parameters);
	}

	public DicomQueryRetrieveDataSourceSpi getDicomQueryRetrieveService() 
	{
		return dicomQueryRetrieveService;
	}

	public DicomStorageDataSourceSpi getDicomStorageService() {
		return dicomStorageService;
	}

	public DicomApplicationEntityDataSourceSpi getDicomApplicationEntityService() {
		return dicomApplicationEntityService;
	}

	public DicomDataSourceSpi getDicomService() {
		return dicomService;
	}

	public DicomImporterDataSourceSpi getDicomImporterService() {
		return dicomImporterService;
	}

}
