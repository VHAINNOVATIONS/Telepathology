/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 17, 2011
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
package gov.va.med.imaging.router.commands;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.commands.AbstractSpiServiceAvailabilityCommandImpl;
import gov.va.med.imaging.datasource.ImageAnnotationDataSourceSpi;
import gov.va.med.imaging.exchange.business.util.ExchangeUtil;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author VHAISWWERFEJ
 *
 */
public class GetSiteAnnotationSupportedStatusCommandImpl
extends AbstractSpiServiceAvailabilityCommandImpl<ImageAnnotationDataSourceSpi>
{
	private static final long serialVersionUID = 3057252617059822500L;

	public GetSiteAnnotationSupportedStatusCommandImpl(RoutingToken routingToken)
	{
		super(routingToken, ImageAnnotationDataSourceSpi.class);
	}

	@Override
	public Boolean callSynchronouslyInTransactionContext()
	throws MethodException
	{
		// JMW 4/17/2012 - this is a bit ugly but necessary. I originally planned to have
		// the VIX try to create a data source using the annotation SPI and have that fail
		// for the DoD but because the VIX goes through the CVIX (which can support annotations)
		// the test failed
		if(ExchangeUtil.isSiteDOD(getRoutingToken().getRepositoryUniqueId()))
		{
			TransactionContext transactionContext = TransactionContextFactory.get();
			transactionContext.setServicedSource( getRoutingToken().toRoutingTokenString() );
			getLogger().info("Annotations at site '" +  getRoutingToken().toRoutingTokenString() + "' are not supported.");
			transactionContext.addDebugInformation("Annotations at site '" +  getRoutingToken().toRoutingTokenString() + "' are not supported.");
			return false;
		}
		// if not site 200 then call the parent to determine if available
		return super.callSynchronouslyInTransactionContext();
	}
}
