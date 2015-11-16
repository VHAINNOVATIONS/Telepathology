/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 22, 2011
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
package gov.va.med.imaging.router.commands;

import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.core.annotations.routerfacade.RouterCommandExecution;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.router.facade.ImagingContext;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * This is a synchronous command which should be called by a facade to initiate a prefetch of a single image
 * 
 * @author vhaiswwerfej
 *
 */
@RouterCommandExecution(asynchronous=false, distributable=false)
public class PrefetchInstanceByImageUrnSyncCommandImpl
extends AbstractCommandImpl<Boolean>
{
	private static final long serialVersionUID = -1346951767989747939L;
	
	private final ImageURN imageUrn;
	private final ImageFormatQualityList imageFormatQualityList;
	
	public PrefetchInstanceByImageUrnSyncCommandImpl(ImageURN imageUrn, 
			ImageFormatQualityList imageFormatQualityList)
	{
		super();
		this.imageUrn = imageUrn;
		this.imageFormatQualityList = imageFormatQualityList;
	}

	@Override
	public Boolean callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException
	{
		getLogger().info("Prefetching image '" + getImageUrn() + "'.");
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setServicedSource(getImageUrn().toRoutingTokenString());
		
		// kick off async operations
		ImagingContext.getRouter().prefetchInstanceByImageUrn(getImageUrn(), getImageFormatQualityList());
		return true;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		final PrefetchInstanceByImageUrnSyncCommandImpl other = (PrefetchInstanceByImageUrnSyncCommandImpl) obj;
		if (this.imageFormatQualityList == null)
		{
			if (other.imageFormatQualityList != null)
				return false;
		} else if (!this.imageFormatQualityList
				.equals(other.imageFormatQualityList))
			return false;
		
		if (this.imageUrn == null)
		{
			if (other.imageUrn != null)
				return false;
		} else if (!this.imageUrn.equals(other.imageUrn))
			return false;
		return true;
	}

	@Override
	protected String parameterToString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(getImageUrn());
		sb.append(',');
		sb.append(getImageFormatQualityList() == null ? "<null image format>" : getImageFormatQualityList().toString());
		
		return sb.toString();
	}

	public ImageURN getImageUrn()
	{
		return imageUrn;
	}

	public ImageFormatQualityList getImageFormatQualityList()
	{
		return imageFormatQualityList;
	}

}
