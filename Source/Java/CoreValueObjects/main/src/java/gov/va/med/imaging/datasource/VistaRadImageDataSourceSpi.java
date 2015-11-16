/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 15, 2009
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
package gov.va.med.imaging.datasource;

import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNearLineException;
import gov.va.med.imaging.core.interfaces.exceptions.ImageNotFoundException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.annotations.SPI;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.exchange.business.ImageStreamResponse;
import gov.va.med.imaging.exchange.business.vistarad.ExamImage;
import gov.va.med.imaging.exchange.storage.DataSourceInputStream;

/**
 * 
 * This class defines the Service Provider Interface (SPI) for the VistaRadSource class. 
 * All the abstract methods in this class must be implemented by each 
 * data source service provider who wishes to supply the implementation of a 
 * VistaRadImageDataSource for a particular datasource type.
 * 
 * @author vhaiswwerfej
 *
 */
@SPI(description="Defines the interface for radiology workstation image (binary) data.")
public interface VistaRadImageDataSourceSpi 
extends VersionableDataSourceSpi 
{

	public abstract ImageStreamResponse getImage(ExamImage image, ImageFormatQualityList requestFormatQuality)
	throws MethodException, ConnectionException;
	
	public abstract ImageStreamResponse getImage(ImageURN imageUrn, ImageFormatQualityList requestFormatQuality)
	throws MethodException, ConnectionException;
	
	public abstract DataSourceInputStream getImageTXTFile(ExamImage image)
	throws MethodException, ConnectionException, ImageNotFoundException, ImageNearLineException;
	
	public abstract DataSourceInputStream getImageTXTFile(ImageURN imageUrn)
	throws MethodException, ConnectionException, ImageNotFoundException, ImageNearLineException;
	
}
