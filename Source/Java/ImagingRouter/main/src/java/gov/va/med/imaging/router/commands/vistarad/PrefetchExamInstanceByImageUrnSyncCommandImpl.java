/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 21, 2011
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
package gov.va.med.imaging.router.commands.vistarad;

import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.core.annotations.routerfacade.RouterCommandExecution;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.router.facade.ImagingContext;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * The purpose of this command is to have a Sync command that can be called from the facade which then
 * calls an async command to get an exam image and optionally an exam image text file
 * 
 * @author vhaiswwerfej
 *
 */
@RouterCommandExecution(asynchronous=false, distributable=false)
public class PrefetchExamInstanceByImageUrnSyncCommandImpl
extends AbstractExamCommandImpl<Boolean>
{
	private static final long serialVersionUID = -1977882353542654600L;
	
	private final ImageURN imageUrn;
	private final ImageFormatQualityList imageFormatQualityList;
	private final boolean includeTextFile;
	
	public PrefetchExamInstanceByImageUrnSyncCommandImpl(ImageURN imageUrn, 
			ImageFormatQualityList imageFormatQualityList,
			boolean includeTextFile)
	{
		super();
		this.imageUrn = imageUrn;
		this.imageFormatQualityList = imageFormatQualityList;
		this.includeTextFile = includeTextFile;
	}
	
	public PrefetchExamInstanceByImageUrnSyncCommandImpl(ImageURN imageUrn, 
			ImageFormatQualityList imageFormatQualityList)
	{
		this(imageUrn, imageFormatQualityList, true);
	}

	@Override
	public Boolean callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		getLogger().info("Prefetching exam image '" + getImageUrn() + "', transaction '" + transactionContext.getTransactionId() + "'.");		
		transactionContext.setServicedSource(getImageUrn().toRoutingTokenString());
		
		// kick off async operations
		ImagingContext.getRouter().getExamInstanceByImageUrnAsync(getImageUrn(), getImageFormatQualityList());
		if(isIncludeTextFile())
		{
			ImagingContext.getRouter().getExamTextFileByImageUrnAsync(getImageUrn());
		}
		return true;
	}

	@Override
	protected boolean areClassSpecificFieldsEqual(Object obj)
	{
		// Perform cast for subsequent tests
		final PrefetchExamInstanceByImageUrnSyncCommandImpl other = (PrefetchExamInstanceByImageUrnSyncCommandImpl) obj;
		
		// Check the patient id and siteNumber
		boolean allEqual = true;
		allEqual = allEqual && areFieldsEqual(getImageUrn(), other.getImageUrn());
		allEqual = allEqual && areFieldsEqual(getImageFormatQualityList(), other.getImageFormatQualityList());
		allEqual = allEqual && areFieldsEqual(isIncludeTextFile(), other.isIncludeTextFile());
		
		return allEqual;
	}

	@Override
	protected String parameterToString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(getImageUrn());
		sb.append(',');
		sb.append(isIncludeTextFile());
		sb.append(',');
		sb.append(getImageFormatQualityList());
		
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

	public boolean isIncludeTextFile()
	{
		return includeTextFile;
	}

}
