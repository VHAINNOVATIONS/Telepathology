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
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.datasource.ImageDataSourceSpi;
import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.exchange.storage.DataSourceInputStream;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author vhaiswwerfej
 *
 */
public class GetInstanceTextFileByImageFromDataSourceCommandImpl 
extends AbstractDataSourceCommandImpl<DataSourceInputStream, ImageDataSourceSpi> 
{
	private static final long serialVersionUID = -4147951638115855610L;
	private static final String SPI_METHOD_NAME = "getImageTXTFile";
	
	private final Image image;
	
	public GetInstanceTextFileByImageFromDataSourceCommandImpl(Image image)
	{
		this.image = image;
	}
	
	/**
	 * @return the image
	 */
	public Image getImage() {
		return image;
	}

	@Override
	public RoutingToken getRoutingToken()
	{
		return image;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getCommandResult(gov.va.med.imaging.datasource.VersionableDataSourceSpi)
	 */
	@Override
	protected DataSourceInputStream getCommandResult(ImageDataSourceSpi spi)
	throws ConnectionException, MethodException 
	{			
		// image not found and image nearline exceptions are thrown as method exceptions, no special handling needed.			
		return spi.getImageTXTFile(getImage());
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
		return new Object[]{getImage()};
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes()
	{
		return new Class<?>[]{Image.class};
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
		return getImage().getIen();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#postProcessResult(java.lang.Object)
	 */
	@Override
	protected DataSourceInputStream postProcessResult(DataSourceInputStream result) 
	{
		TransactionContextFactory.get().setDataSourceEntriesReturned(result == null ? 0 : 1);
		return result;
	}

}
