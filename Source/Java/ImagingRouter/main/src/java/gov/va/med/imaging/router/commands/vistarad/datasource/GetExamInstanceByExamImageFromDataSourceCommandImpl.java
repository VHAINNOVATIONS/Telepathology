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
import gov.va.med.RoutingTokenImpl;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.datasource.VistaRadImageDataSourceSpi;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.exchange.business.ImageStreamResponse;
import gov.va.med.imaging.exchange.business.vistarad.ExamImage;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author vhaiswwerfej
 *
 */
public class GetExamInstanceByExamImageFromDataSourceCommandImpl 
extends AbstractDataSourceCommandImpl<ImageStreamResponse, VistaRadImageDataSourceSpi>
{
	private static final long serialVersionUID = -1840552099065209535L;
	
	private final ExamImage examImage;
	private final ImageFormatQualityList requestFormatQuality;
	
	private static final String SPI_METHOD_NAME = "getImage";

	public GetExamInstanceByExamImageFromDataSourceCommandImpl(ExamImage examImage, ImageFormatQualityList requestFormatQuality)
	{
		this.requestFormatQuality = requestFormatQuality;
		this.examImage = examImage;
	}

	/**
	 * @return the examImage
	 */
	public ExamImage getExamImage() {
		return examImage;
	}

	/**
	 * @return the requestFormatQuality
	 */
	public ImageFormatQualityList getRequestFormatQuality() {
		return requestFormatQuality;
	}

	/*
	@Override
	public List<RoutingToken> getPossibleRoutingTokens()
	{
		List<RoutingToken> routingTokens = new ArrayList<RoutingToken>();
		if(examImage.getAlienSiteNumber() != null && examImage.getAlienSiteNumber().length() > 0)
		{
			try
			{
				routingTokens.add(RoutingTokenImpl.createVARadiologySite(examImage.getAlienSiteNumber()));
			}
			catch(RoutingTokenFormatException rtfX)
			{
				getLogger().warn("Error creating routingToken for alien site '" + examImage.getAlienSiteNumber() + "', " + rtfX.getMessage());
			}
		}
		routingTokens.add(getExamImage());
		return routingTokens;
	}*/

	@Override
	public RoutingToken getRoutingToken()
	{
		if(examImage.getAlienSiteNumber() != null && examImage.getAlienSiteNumber().length() > 0)
		{
			try
			{
				return RoutingTokenImpl.createVARadiologySite(examImage.getAlienSiteNumber());
			}
			catch(RoutingTokenFormatException rtfX)
			{
				getLogger().warn("Error creating routingToken for alien site '" + examImage.getAlienSiteNumber() + "', " + rtfX.getMessage());
			}
		}
		return getExamImage();
	}

	@Override
	protected Object[] getSpiMethodParameters()
	{
		return new Object[]{getExamImage(), getRequestFormatQuality()};
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes()
	{
		return new Class<?>[]{ExamImage.class, ImageFormatQualityList.class};
	}

	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getCommandResult(gov.va.med.imaging.datasource.VersionableDataSourceSpi)
	 */
	@Override
	protected ImageStreamResponse getCommandResult(
			VistaRadImageDataSourceSpi spi) 
	throws ConnectionException, MethodException 
	{
		// image not found and image nearline exceptions are thrown as method exceptions, no special handling needed.
		return spi.getImage(getExamImage(), getRequestFormatQuality());		
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiClass()
	 */
	@Override
	protected Class<VistaRadImageDataSourceSpi> getSpiClass() 
	{
		return VistaRadImageDataSourceSpi.class;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSiteNumber()
	 */
	@Override
	protected String getSiteNumber() 
	{
		return getExamImage().getSiteNumber();
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
		
		sb.append(getSiteNumber());
		sb.append(", ");
		sb.append(getExamImage().getImageId());		
		sb.append(", ");
		sb.append(getRequestFormatQuality().toString());
		
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#postProcessResult(java.lang.Object)
	 */
	@Override
	protected ImageStreamResponse postProcessResult(ImageStreamResponse result) 
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setDataSourceEntriesReturned(result == null ? 0 : 1);
		return result;
	}
}
