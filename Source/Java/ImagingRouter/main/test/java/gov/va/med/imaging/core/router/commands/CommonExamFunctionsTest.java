/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 1, 2011
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
package gov.va.med.imaging.core.router.commands;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.business.TestSite;
import gov.va.med.imaging.core.interfaces.Router;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.router.CommandContext;
import gov.va.med.imaging.core.interfaces.router.CommandContextTestImpl;
import gov.va.med.imaging.core.interfaces.router.CommandFactory;
import gov.va.med.imaging.datasource.DataSourceProvider;
import gov.va.med.imaging.datasource.SiteResolutionDataSourceSpi;
import gov.va.med.imaging.datasource.SiteResolutionDataSourceTestImpl;
import gov.va.med.imaging.datasource.TransactionLoggerDataSourceSpi;
import gov.va.med.imaging.datasource.VersionableDataSourceSpi;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.vistarad.ExamImage;
import gov.va.med.imaging.exchange.business.vistarad.ExamImages;
import gov.va.med.imaging.exchange.business.vistarad.test.VistaRadBusinessObjectBuilder;
import gov.va.med.imaging.exchange.storage.cache.DODSourcedCache;
import gov.va.med.imaging.exchange.storage.cache.VASourcedCache;
import gov.va.med.imaging.router.commands.provider.ImagingCommandContext;
import gov.va.med.imaging.router.commands.vistarad.AbstractExamCommandImpl;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This is a unit test to test the code to update the ExamImage for a consolidated site. This unit test is
 * strange because it extends AbstractExamCommandImpl. This is necessary to access the method needed to test
 * the consolidated site update.  This is very strange and ugly but should work fine.  This object should NEVER
 * be run as a command since it has no command implementation. 
 * 
 * @author vhaiswwerfej
 *
 */
public class CommonExamFunctionsTest
extends AbstractExamCommandImpl<java.lang.Void>
{

	private static final long serialVersionUID = -977236705311119828L;

	@Override
	protected boolean areClassSpecificFieldsEqual(Object obj)
	{
		return false;
	}

	@Override
	public Void callSynchronouslyInTransactionContext() 
	throws MethodException, ConnectionException
	{
		throw new MethodException("This command should never be called as a command, its a unit test!");
	}

	@Override
	protected String parameterToString()
	{
		return null;
	}

	@Test
	public void testUpdateConsolidatedSiteInExams()
	throws Exception
	{
		Site consolidatedSite = getConsolidatedSite();
		Site site = getLocalSite();
		ExamImages examImages = 
			VistaRadBusinessObjectBuilder.createExamImages(site.getSiteNumber(), 
					consolidatedSite.getSiteNumber());
		assertEquals(consolidatedSite.getSiteNumber(), examImages.getConsolidatedSiteNumber());
		for(ExamImage examImage : examImages)
		{
			assertEquals(site.getSiteNumber(), examImage.getImageUrn().getRepositoryUniqueId());
		}
		updateExamImagesConsolidatedSite(examImages);
		
		for(ExamImage examImage : examImages)
		{
			assertEquals(consolidatedSite.getSiteNumber(), examImage.getImageUrn().getRepositoryUniqueId());
		}
	}

	@Override
	protected ImagingCommandContext getCommandContext()
	{
		SiteResolutionDataSourceSpi siteResolution = 
			new SiteResolutionDataSourceTestImpl(getTestSites());
		CommandContext commandContext = new CommandContextTestImpl(siteResolution);
		return new TestImagingCommandContext(commandContext);
	}
	
	protected List<Site> getTestSites()
	{
		List<Site> sites = new ArrayList<Site>();
		sites.add(getConsolidatedSite());
		sites.add(getLocalSite());
		
		return sites;
	}
	
	protected Site getConsolidatedSite()
	{		
		String consolidatedSiteNumber = "589AF";
		return new TestSite("ConsolidatedSite", consolidatedSiteNumber, "CON");
	}
	
	protected Site getLocalSite()
	{
		String siteNumber = "660";
		return new TestSite("LocalSite", siteNumber, "LOCAL");
	}
	
	class TestImagingCommandContext
	implements ImagingCommandContext
	{
		CommandContext commandContext;
		TestImagingCommandContext(CommandContext commandContext)
		{
			this.commandContext = commandContext;
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

		@Override
		public DODSourcedCache getExtraEnterpriseCache() 
		{
			return null;
		}

		@Override
		public VASourcedCache getIntraEnterpriseCacheCache() 
		{
			return null;
		}
		
	}
}
