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
package gov.va.med.imaging.router.commands.annotations;

import java.util.List;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.AbstractImagingURN;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.datasource.ImageAnnotationDataSourceSpi;
import gov.va.med.imaging.exchange.business.annotations.ImageAnnotation;

/**
 * @author VHAISWWERFEJ
 *
 */
public class GetImageAnnotationListByImageUrnCommandImpl
extends AbstractDataSourceCommandImpl<List<ImageAnnotation>, ImageAnnotationDataSourceSpi>
{
	private static final long serialVersionUID = 4399020915461375538L;

	private final AbstractImagingURN imagingUrn;
	
	private static final String SPI_METHOD_NAME = "getImageAnnotations";
	private static final Class<?>[] SPI_METHOD_PARAMETER_TYPES = 
		new Class<?>[]{AbstractImagingURN.class};
	
	public GetImageAnnotationListByImageUrnCommandImpl(AbstractImagingURN imagingUrn)
	{
		this.imagingUrn = imagingUrn;
	}

	public AbstractImagingURN getImagingUrn()
	{
		return imagingUrn;
	}

	@Override
	public RoutingToken getRoutingToken()
	{
		return getImagingUrn();
	}

	@Override
	protected Class<ImageAnnotationDataSourceSpi> getSpiClass()
	{
		return ImageAnnotationDataSourceSpi.class;
	}

	@Override
	protected String getSpiMethodName()
	{
		return SPI_METHOD_NAME;
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes()
	{
		return SPI_METHOD_PARAMETER_TYPES;
	}

	@Override
	protected Object[] getSpiMethodParameters()
	{
		return new Object[]{getImagingUrn()};
	}

	@Override
	protected String getSiteNumber()
	{
		return getRoutingToken().getRepositoryUniqueId();
	}

	@Override
	protected List<ImageAnnotation> getCommandResult(
			ImageAnnotationDataSourceSpi spi) throws ConnectionException,
			MethodException
	{
		return spi.getImageAnnotations(getImagingUrn());
	}

}
