/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr, 2010
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswlouthj
  Description: DICOM Study cache manager. Maintains the cache of study instances
  			   and expires old studies after 15 minutes. 

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
package gov.va.med.imaging.core.router.commands.storage.datasource;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.commands.storage.AbstractStorageDataSourceCommandImpl;
import gov.va.med.imaging.datasource.StorageDataSourceSpi;
import gov.va.med.imaging.exchange.business.storage.Provider;
import gov.va.med.imaging.exchange.business.storage.NetworkLocationInfo;

public class GetCurrentWriteLocationCommandImpl 
extends AbstractStorageDataSourceCommandImpl<NetworkLocationInfo>
{

	private static final long serialVersionUID = 1L;

	private static final String SPI_METHOD_NAME = "getCurrentWriteLocation";

	private final Provider provider;
	
	public GetCurrentWriteLocationCommandImpl(Provider provider) 
	{
		super();
		this.provider = provider;
	}

	@Override
	protected Class<?>[] getSpiMethodParameterTypes() {
		return new Class<?>[]{Provider.class};
	}

	@Override
	protected Object[] getSpiMethodParameters() {
		return new Object[]{provider} ;
	}

	@Override
	protected String parameterToString()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getCommandResult(gov.va.med.imaging.datasource.VersionableDataSourceSpi)
	 */
	@Override
	protected NetworkLocationInfo getCommandResult(StorageDataSourceSpi spi)
	throws ConnectionException, MethodException 
	{
		return spi.getCurrentWriteLocation(provider);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodName()
	 */
	@Override
	protected String getSpiMethodName() 
	{
		return SPI_METHOD_NAME;
	}
	
	

}
