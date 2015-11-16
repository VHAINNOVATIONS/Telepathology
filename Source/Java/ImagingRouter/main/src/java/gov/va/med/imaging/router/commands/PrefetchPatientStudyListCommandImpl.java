/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Oct 3, 2008
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWBECKEC
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */
package gov.va.med.imaging.router.commands;

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.annotations.routerfacade.RouterCommandExecution;
import gov.va.med.imaging.core.interfaces.ImageMetadataNotification;
import gov.va.med.imaging.core.interfaces.exceptions.CompositeMethodException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.*;
import gov.va.med.imaging.exchange.enums.ImageFormat;
import gov.va.med.imaging.exchange.enums.ImageQuality;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.router.facade.ImagingContext;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import java.io.OutputStream;
import java.util.List;

/**
 * A Command implementation for initiating a prefetch of a patient identification image.
 * The result of successful processing is a void, represented by the
 * "Object" class used as the generic type.  The result will always
 * be a null. 
 * 
 * @author VHAISWBECKEC
 *
 */
@RouterCommandExecution(asynchronous=true, distributable=true)
public class PrefetchPatientStudyListCommandImpl 
extends AbstractStudyCommandImpl<java.lang.Void>
{
	private static final long serialVersionUID = 1L;
	
	private final RoutingToken routingToken;
	private final PatientIdentifier patientIdentifier;
	private final StudyFilter filter;
	private final ImageFormatQualityList format;
	
	/**
	 * @param command
	 * @param router
	 * @param asynchronousMethodProcessor
	 */
	public PrefetchPatientStudyListCommandImpl(
		RoutingToken routingToken,
		PatientIdentifier patientIdentifier,
		StudyFilter filter,
		ImageFormatQualityList format)
	{
		super();
		this.routingToken = routingToken;
		this.patientIdentifier = patientIdentifier;
		this.filter = filter;
		this.format = format;
	}

	public RoutingToken getRoutingToken()
	{
		return this.routingToken;
	}

	public String getSiteNumber()
    {
    	return getRoutingToken().getRepositoryUniqueId();
    }
	
	public PatientIdentifier getPatientIdentifier()
	{
		return patientIdentifier;
	}

	public StudyFilter getFilter()
    {
    	return filter;
    }

	public ImageFormatQualityList getFormat()
    {
    	return format;
    }
	
	/**
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#callSynchronouslyInTransactionContext()
	 */
	@Override
	public java.lang.Void callSynchronouslyInTransactionContext() 
	throws MethodException
	{
		TransactionContext xactionCtx = TransactionContextFactory.get();
		
		getLogger().info("Asynchronous Command [" + this.getClass().getSimpleName() + "] is being processed.");
		
		xactionCtx.setChildRequestType("getPatientStudyList");
		List<Study> studyList = getPatientStudyList(getRoutingToken(), getPatientIdentifier(), getFilter(), StudyLoadLevel.FULL);
		
		try		
		{
			getCommandContext().getTransactionLoggerService().writeLogEntry(new TransactionContextLogEntrySnapshot(xactionCtx));
		}
		catch(Exception x)
		{
			throw new MethodException(x);
		}
      
		getLogger().info("Asynchronous Command [" + this.getClass().getSimpleName() + "] - study list obtained, getting images");
		
		CompositeMethodException compositeException = new CompositeMethodException();
		
		for(Study study : studyList)
			if( study.getSeries() != null )
				for(Series series : study.getSeries())
					for(Image image : series )
					{
						ImagingContext.getRouter().prefetchInstanceByImageUrn(image.getImageUrn(), getFormat());						
						getLogger().info("Image command " + image.getImageUrn() + " has been submitted for asynchronous execution.");
					}		
		getLogger().info("Asynchronous Command [" + this.getClass().getSimpleName() + "] - completed.");
		
		if(compositeException.size() > 0)
			throw compositeException;
		
		return null;
	}

	@Override
    protected String parameterToString()
    {
		StringBuffer sb = new StringBuffer();
		sb.append(getSiteNumber() == null ? "<null site number>" : getSiteNumber());
		sb.append(getPatientIdentifier() == null ? "<null patient id>" : getPatientIdentifier());
		sb.append(getFilter() == null ? "<null filter>" : getFilter().toString());
		sb.append(getFormat() == null ? "<null format list>" : getFormat().toString());

		return sb.toString();
    }

	
	/**
	 * This is an idempotent method, therefore the .equals() must make a good-faith determination that the
	 * result of equal instance would be equals.
	 *  
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#equals(java.lang.Object)
	 */
	
	
	private class NullOutputStream 
	extends OutputStream
	{
		@Override
		 public void write(int b) {}
	}
	
	private class NullImageMetadataNotification implements ImageMetadataNotification
	{
		@Override
        public void imageMetadata(String checksumValue, ImageFormat imageFormat, int fileSize, ImageQuality imageQuality){}
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.filter == null) ? 0 : this.filter.hashCode());
		result = prime * result + ((this.format == null) ? 0 : this.format.hashCode());
		result = prime * result + ((this.patientIdentifier == null) ? 0 : this.patientIdentifier.hashCode());
		result = prime * result + ((this.routingToken == null) ? 0 : this.routingToken.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		final PrefetchPatientStudyListCommandImpl other = (PrefetchPatientStudyListCommandImpl) obj;
		if (this.filter == null)
		{
			if (other.filter != null)
				return false;
		}
		else if (!this.filter.equals(other.filter))
			return false;
		if (this.format == null)
		{
			if (other.format != null)
				return false;
		}
		else if (!this.format.equals(other.format))
			return false;
		if (this.patientIdentifier == null)
		{
			if (other.patientIdentifier != null)
				return false;
		}
		else if (!this.patientIdentifier.equals(other.patientIdentifier))
			return false;
		if (this.routingToken == null)
		{
			if (other.routingToken != null)
				return false;
		}
		else if (!this.routingToken.equals(other.routingToken))
			return false;
		return true;
	}


}
