/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 8, 2009
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
package gov.va.med.imaging.exchange.business.vistarad;

import java.io.Serializable;

import org.apache.log4j.Logger;

import gov.va.med.ImageURNFactory;
import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.exceptions.URNFormatException;

/**
 * Represents a VistARad image. 
 * 
 * @author vhaiswwerfej
 *
 */
public class ExamImage 
implements Serializable, RoutingToken
{
	private static final long serialVersionUID = 8423447288101448578L;	
	private final static Logger logger = Logger.getLogger(ExamImage.class);
	
	private final ImageURN imageUrn;
	
	private String diagnosticFilePath;
	private String patientName;	
	private boolean imageInCache;
	private String alienSiteNumber; // the alien site number is the site number known to the external entity (smells a bit like repository Id but not used in the same way). Necessary to support wormhole
	
	public static ExamImage create(String siteNumber, String imageId, String examId, String patientIcn)
	throws URNFormatException
	{
		ImageURN imageUrn = ImageURNFactory.create(siteNumber, imageId, examId, patientIcn, null, ImageURN.class);
		return new ExamImage(imageUrn);
	}

	private ExamImage(ImageURN imageUrn)
	throws URNFormatException
	{
		super();
		this.imageUrn = imageUrn;
		imageInCache = false;
	}
	
	public ImageURN getImageUrn()
	{
		return this.imageUrn;
	}

	@Override
	public String getHomeCommunityId(){return getImageUrn().getHomeCommunityId();}

	@Override
	public String getRepositoryUniqueId(){return getImageUrn().getRepositoryUniqueId();}

	/**
	 * Determines if the image exists in the cache for a VistARad user.
	 * 
	 * @return the imageInCache
	 */
	public boolean isImageInCache() {
		return imageInCache;
	}

	/**
	 * @param imageInCache the imageInCache to set
	 */
	public void setImageInCache(boolean imageInCache) {
		this.imageInCache = imageInCache;
	}

	/**
	 * @return the imageId
	 */
	public String getImageId() 
	{
		return getImageUrn().getImageId();
	}

	/**
	 * @return the examId
	 */
	public String getExamId() 
	{
		return getImageUrn().getStudyId();
	}

	/**
	 * @return the patientIcn
	 */
	public String getPatientIcn() 
	{
		return getImageUrn().getPatientId();
	}

	/**
	 * @return the siteNumber
	 */
	public String getSiteNumber() 
	{
		return getImageUrn().getOriginatingSiteId();
	}

	/**
	 * @return the patientName
	 */
	public String getPatientName() {
		return patientName;
	}

	/**
	 * @param patientName the patientName to set
	 */
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	/**
	 * @return the diagnosticFilePath
	 */
	public String getDiagnosticFilePath() {
		return diagnosticFilePath;
	}

	/**
	 * @param diagnosticFilePath the diagnosticFilePath to set
	 */
	public void setDiagnosticFilePath(String diagnosticFilePath) {
		this.diagnosticFilePath = diagnosticFilePath;
	}

	@Override
	public boolean isEquivalent(RoutingToken that)
	{
		return RoutingTokenImpl.isEquivalent(this, that);
	}

	@Override
	public boolean isIncluding(RoutingToken that)
	{
		return RoutingTokenImpl.isIncluding(this, that);
	}

	/**
	 * @see gov.va.med.RoutingToken#toRoutingTokenString()
	 */
	@Override
	public String toRoutingTokenString()
	{
		return getImageUrn() == null ? null : getImageUrn().toRoutingTokenString();
	}
	
	public String getAlienSiteNumber()
	{
		return alienSiteNumber;
	}

	public void setAlienSiteNumber(String alienSiteNumber)
	{
		this.alienSiteNumber = alienSiteNumber;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() 
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Image (" + getImageId() + ") from site '" + getSiteNumber() + "'\n");
		sb.append("\tHas Path '" + diagnosticFilePath + "'");
		return sb.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object that) 
	{
		if (this == that)
			return true;
		if (that == null)
			return false;
		if (getClass() != that.getClass())
			return false;
		final ExamImage other = (ExamImage) that;
		if (this.imageUrn == null)
		{
			if (other.imageUrn != null)
				return false;
		}
		else if (!this.imageUrn.equalsGlobalArtifactIdentifier(other.imageUrn))
			return false;
		return true;
	}
	
	public ExamImage cloneWithConsolidatedSiteNumber(String consolidatedSiteNumber)
	{
		// only do the update if it is necessary
		if(!consolidatedSiteNumber.equals(this.getSiteNumber()))
		{
			try
			{
				ExamImage examImage = ExamImage.create(consolidatedSiteNumber, this.getImageId(), 
						this.getExamId(), this.getPatientIcn());
				examImage.setAlienSiteNumber(this.alienSiteNumber);
				examImage.setDiagnosticFilePath(this.diagnosticFilePath);
				examImage.setImageInCache(this.imageInCache);
				examImage.setPatientName(this.patientName);
				return examImage;
			}
			catch(URNFormatException urnfX)
			{
				logger.warn("Error creating new ImageURN from consolidated site number, " + urnfX.getMessage(), urnfX);
			}
		}
		// something went wrong or the conversion was not necessary, don't do anything
		return this;
	}
	
}
