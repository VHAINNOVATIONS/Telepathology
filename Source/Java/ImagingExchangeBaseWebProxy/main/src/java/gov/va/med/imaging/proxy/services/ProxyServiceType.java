/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 28, 2009
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
package gov.va.med.imaging.proxy.services;

import gov.va.med.imaging.datasource.ImageAccessLoggingSpi;
import gov.va.med.imaging.datasource.PatientDataSourceSpi;
import gov.va.med.imaging.datasource.UserDataSourceSpi;
import gov.va.med.imaging.proxy.ids.IDSOperation;

/**
 * This enumeration defines the types of service operations available to a proxy
 * 
 * @author vhaiswwerfej
 *
 */
public class ProxyServiceType 
{
	
	public static ProxyServiceType image = new ProxyServiceType("Standard image retrieval using a URN", IDSOperation.IDS_OPERATION_IMAGE); 
	public static ProxyServiceType metadata = new ProxyServiceType("Any form of metadata retrieval usually pointing to a web service endpoint", IDSOperation.IDS_OPERATION_METADATA);
	public static ProxyServiceType photo = new ProxyServiceType("Retrieval of a photo Id, the interface for this method may be different than an image retrieval", IDSOperation.IDS_OPERATION_PHOTO);
	public static ProxyServiceType text = new ProxyServiceType("Retrieval of an image TXT file, the interface for this method may be different than an image retrieval", IDSOperation.IDS_OPERATION_TEXT);
	public static ProxyServiceType examImage = new ProxyServiceType("VistARad exam image retrieval using a URN", IDSOperation.IDS_OPERATION_EXAM_IMAGE);
	public static ProxyServiceType examImageText = new ProxyServiceType("VistARad exam image text file retrieval using a URN", IDSOperation.IDS_OPERATION_EXAM_IMAGE_TEXT);
	public static ProxyServiceType vistaRadMetadata = new ProxyServiceType("Metadata retrieval for VistARad methods", IDSOperation.IDS_OPERATION_VISTARAD_METADATA);
	public static ProxyServiceType document = new ProxyServiceType("Document retrieval", IDSOperation.IDS_OPERATION_DOCUMENT);
	//public static ProxyServiceType pathology = new ProxyServiceType("Pathology", IDSOperation.IDS_OPERATION_PATHOLOGY);
	// JMW 9/10/2012 going forward Federation functionality will be split based on the SPI so the SPI name will be in IDS
	public static ProxyServiceType user = new ProxyServiceType("User Methods", UserDataSourceSpi.class.getSimpleName());
	public static ProxyServiceType patient = new ProxyServiceType("Patient Methods", PatientDataSourceSpi.class.getSimpleName());
	public static ProxyServiceType imageAccessLogging = new ProxyServiceType("Image Access Logging Methods", ImageAccessLoggingSpi.class.getSimpleName());
	
	private final String description;
	private final String idsOperationType;
	
	public ProxyServiceType(String description, String idsOperationType)
	{
		this.description = description;		
		this.idsOperationType = idsOperationType;
	}
	
	public ProxyServiceType(String idsOperationType)
	{
		this(null, idsOperationType);
	}	

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the idsOperationType
	 */
	public String getIdsOperationType() {
		return idsOperationType;
	}
	
	/**
	 * Retrieve the ProxyServiceType from a given IDS operation name
	 * @param idsOperationType
	 * @return the specified ProxyServiceType or null if none matches
	 */
	public static ProxyServiceType getProxyServiceTypeFromIDSOperationType(String idsOperationType)
	{
		return new ProxyServiceType(idsOperationType);
	}
	
	/**
	 * Retrieve the ProxyServiceType from a given IDS operation name
	 * @param idsOperation
	 * @return the specified ProxyServiceType or null if none matches
	 */
	public static ProxyServiceType getProxyServiceTypeFromIDSOperation(IDSOperation idsOperation)
	{
		return getProxyServiceTypeFromIDSOperationType(idsOperation.getOperationType());
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return idsOperationType;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof ProxyServiceType)
		{
			ProxyServiceType that = (ProxyServiceType)obj;
			return this.idsOperationType.equals(that.idsOperationType);
		}
		return false;
	}
}
