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
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
import gov.va.med.imaging.datasource.VistaRadImageDataSourceSpi;
import gov.va.med.imaging.exchange.business.vistarad.ExamImage;
import gov.va.med.imaging.exchange.storage.DataSourceInputStream;

/**
 * @author vhaiswwerfej
 *
 */
public class GetExamTextFileByExamImageFromDataSourceCommandImpl 
extends AbstractDataSourceCommandImpl<DataSourceInputStream, VistaRadImageDataSourceSpi>
{
	private static final long serialVersionUID = 3755419517260603609L;
	
	private final ExamImage examImage;
	
	private static final String SPI_METHOD_NAME = "getImageTXTFile";
	
	public GetExamTextFileByExamImageFromDataSourceCommandImpl(ExamImage examImage)
	{
		this.examImage = examImage;
	}

	/**
	 * @return the examImage
	 */
	public ExamImage getExamImage() {
		return examImage;
	}
	
	@Override
	public RoutingToken getRoutingToken()
	{
		return getExamImage();
	}

	@Override
	protected Object[] getSpiMethodParameters()
	{
		return new Object[]{getExamImage()};
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes()
	{
		return new Class<?>[]{ExamImage.class};
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getCommandResult(gov.va.med.imaging.datasource.VersionableDataSourceSpi)
	 */
	@Override
	protected DataSourceInputStream getCommandResult(VistaRadImageDataSourceSpi spi)
	throws ConnectionException, MethodException 
	{		
		// image not found and image nearline exceptions are thrown as method exceptions, no special handling needed.
		return spi.getImageTXTFile(getExamImage());		
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
		
		sb.append(getExamImage().toString());
		
		return sb.toString();
	}
}
