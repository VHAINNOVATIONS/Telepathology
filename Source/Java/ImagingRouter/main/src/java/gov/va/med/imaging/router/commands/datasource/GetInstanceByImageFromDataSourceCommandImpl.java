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

import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.datasource.ImageDataSourceSpi;
import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.exchange.business.ImageStreamResponse;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author vhaiswwerfej
 *
 */
public class GetInstanceByImageFromDataSourceCommandImpl 
extends AbstractDataSourceCommandImpl<ImageStreamResponse, ImageDataSourceSpi> 
{
	private static final long serialVersionUID = -5516200468848720826L;
	
	private final Image image;
	private final ImageFormatQualityList requestFormatQualityList;
	
	private static final String SPI_METHOD_NAME = "getImage";
	
	public GetInstanceByImageFromDataSourceCommandImpl(Image image, ImageFormatQualityList requestFormatQualityList)
	{
		this.image = image;
		this.requestFormatQualityList = requestFormatQualityList;
	}

	@Override
	public RoutingToken getRoutingToken()
	{
		if(image.getAlienSiteNumber() != null && image.getAlienSiteNumber().length() > 0)
		{
			try
			{
				return RoutingTokenImpl.create(image.getGlobalArtifactIdentifier().getHomeCommunityId(), image.getAlienSiteNumber());
			}
			catch(RoutingTokenFormatException rtfX)
			{
				getLogger().warn("Error creating routingToken for alien site '" + image.getAlienSiteNumber() + "', " + rtfX.getMessage());
			}
		}
		return image;
	}

	/**
	 * @return the image
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * @return the requestFormatQualityList
	 */
	public ImageFormatQualityList getRequestFormatQualityList() {
		return requestFormatQualityList;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getCommandResult(gov.va.med.imaging.datasource.VersionableDataSourceSpi)
	 */
	@Override
	protected ImageStreamResponse getCommandResult(ImageDataSourceSpi spi)
	throws ConnectionException, MethodException 
	{			
		// image not found and image nearline exceptions are thrown as method exceptions, no special handling needed.			
		return spi.getImage(getImage(), getRequestFormatQualityList());
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiClass()
	 */
	@Override
	protected Class<ImageDataSourceSpi> getSpiClass() {
		return ImageDataSourceSpi.class;
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
		return new Object[]{getImage(), getRequestFormatQualityList()};
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes()
	{
		return new Class<?>[]{Image.class, ImageFormatQualityList.class};
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSiteNumber()
	 */
	@Override
	protected String getSiteNumber() 
	{
		return getImage().getSiteNumber();
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
		sb.append(getImage().getIen());
		sb.append(", ");
		sb.append(getRequestFormatQualityList().toString());
		
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#postProcessResult(java.lang.Object)
	 */
	@Override
	protected ImageStreamResponse postProcessResult(ImageStreamResponse result) 
	{
		TransactionContextFactory.get().setDataSourceEntriesReturned(result == null ? 0 : 1);
		return result;
	}
}
