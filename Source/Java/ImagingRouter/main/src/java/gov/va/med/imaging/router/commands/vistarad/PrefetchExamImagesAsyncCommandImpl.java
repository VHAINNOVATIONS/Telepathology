/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Aug 27, 2010
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

import gov.va.med.WellKnownOID;
import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.core.annotations.routerfacade.RouterCommandExecution;
import gov.va.med.imaging.core.interfaces.exceptions.CompositeMethodException;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.ImageFormatQualityList;
import gov.va.med.imaging.exchange.business.vistarad.Exam;
import gov.va.med.imaging.exchange.business.vistarad.ExamImage;
import gov.va.med.imaging.router.commands.vistarad.configuration.VistaRadCommandConfiguration;
import gov.va.med.imaging.router.facade.ImagingContext;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author vhaiswwerfej
 *
 */
@RouterCommandExecution(asynchronous=true, distributable=false)
public class PrefetchExamImagesAsyncCommandImpl
extends AbstractExamCommandImpl<Boolean> 
{

	private static final long serialVersionUID = -4518171326077499819L;
	
	private final StudyURN studyUrn; 
	
	/**
	 * @param router
	 * @param asynchronousMethodProcessor
	 */
	public PrefetchExamImagesAsyncCommandImpl(StudyURN studyUrn)
	{
		super();
		this.studyUrn = studyUrn;
	}

	public StudyURN getStudyUrn()
	{
		return this.studyUrn;
	}

	public String getSiteNumber()
	{
		return this.getStudyUrn().getOriginatingSiteId();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AsynchronousCommandProcessor#callInTransactionContext()
	 */
	@Override
	public Boolean callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException
	{
		getLogger().info("Prefetching exam images for exam '" + getStudyUrn() + "'.");
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setServicedSource(getStudyUrn().toRoutingTokenString());
		
		Exam exam = this.getFullyLoadedExam(getStudyUrn());
		
		getLogger().info("Found '" + exam.getImageCount() + "' images in exam.");
		int successful = 0;
		CompositeMethodException compositeException = null;
		
		for (ExamImage examImage : exam.getImages())
		{
			// JMW 1/29/10 - changed logic a bit so if 1 image fails, the others will continue.
			try
			{
				ImagingContext.getRouter().getExamInstanceByImageUrn(examImage.getImageUrn(), getImageFormatQualityList());
				if(WellKnownOID.VA_RADIOLOGY_IMAGE.isApplicable(examImage.getImageUrn().getHomeCommunityId()))
				{
					// only if this is a VA radiology image do you want the text file
					ImagingContext.getRouter().getExamTextFileByImageUrn(examImage.getImageUrn());
				}
				successful++;
			}
			catch(ConnectionException cX)
			{
				getLogger().warn("ConnectionException when prefetching exam image '" + examImage.getImageId() + "'.", cX);
				if(compositeException == null)
					compositeException = new CompositeMethodException();
				compositeException.addException(new MethodConnectionException(cX));
			}
			catch(MethodException mX)			
			{
				getLogger().warn("MethodException when prefetching exam image '" + examImage.getImageId() + "'.", mX);
				if(compositeException == null)
					compositeException = new CompositeMethodException();
				compositeException.addException(mX);
			}
		}
		
		
		return true;
	}

	private ImageFormatQualityList getImageFormatQualityList()
	{
		ImageFormatQualityList imageFormatQualityList = new ImageFormatQualityList();
		
		VistaRadCommandConfiguration configuration = VistaRadCommandConfiguration.getVistaRadCommandConfiguration();
		imageFormatQualityList.addAll(configuration.getExamImagePrefetchImageFormatQualities());
		
		return imageFormatQualityList;
	}

	@Override
	protected boolean areClassSpecificFieldsEqual(Object obj)
	{
		// Perform cast for subsequent tests
		final PrefetchExamImagesAsyncCommandImpl other = (PrefetchExamImagesAsyncCommandImpl) obj;
		
		// Check the patient id and siteNumber
		boolean allEqual = true;
		allEqual = allEqual && areFieldsEqual(getStudyUrn(), other.getStudyUrn());
		allEqual = allEqual && areFieldsEqual(getSiteNumber(), other.getSiteNumber());
		
		return allEqual;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		String siteNumber = this.getSiteNumber();
		result = prime * result + ((getStudyUrn() == null) ? 0 : getStudyUrn().hashCode());
		result = prime * result + ((siteNumber == null) ? 0 : siteNumber.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AsynchronousCommandProcessor#parameterToString()
	 */
	@Override
	protected String parameterToString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(getSiteNumber());
		sb.append(',');
		sb.append(getStudyUrn());
		
		return sb.toString();
	}
}
