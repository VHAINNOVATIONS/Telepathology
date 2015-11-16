/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Aug 20, 2008
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWTITTOC
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
package gov.va.med.imaging.exchange.business;

import java.io.Serializable;


/**
 * Represents a region containing one or more Site objects.
 * 
 * @author VHAISWTITTOC
 *
 */
public class ModalityLossyCompressionParameters
implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String  modality;		// DICOM modality code (always upper)
	private float   j2kLossyRatio;	// value greater than 3.0
	private int		jpegQuality;	// worst=1..100=best/lossless; 0 undefined -> compute from j2k Lossy ratio

	public ModalityLossyCompressionParameters() {
		modality = "";
		j2kLossyRatio = 0.0f;
		jpegQuality = 0;	
	}
	public ModalityLossyCompressionParameters(String mty, float lossyRatio) {
		modality = mty;
		j2kLossyRatio = lossyRatio;
		jpegQuality = 0;	
	}
	public ModalityLossyCompressionParameters(String mty, float lossyRatio, int lossyQuality) {
		modality = mty;
		j2kLossyRatio = lossyRatio;
		jpegQuality = lossyQuality;	
	}
	public String getModality() {
		return modality;
	}
	public void setModality(String mty) {
		modality = mty;
	}
	public Float getJ2kLossyRatio() {
		return j2kLossyRatio;
	}
	public void setJ2kLossyRatio(Float lossyRatio) {
		j2kLossyRatio = lossyRatio;
	}
	public Integer getJpegQuality() {
		return jpegQuality;
	}
	public void setJpegQuality(Integer lossyQuality) {
		jpegQuality = lossyQuality;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(j2kLossyRatio);
		result = prime * result + jpegQuality;
		result = prime * result
				+ ((modality == null) ? 0 : modality.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ModalityLossyCompressionParameters other = (ModalityLossyCompressionParameters) obj;
		if (Float.floatToIntBits(j2kLossyRatio) != Float
				.floatToIntBits(other.j2kLossyRatio))
			return false;
		if (jpegQuality != other.jpegQuality)
			return false;
		if (modality == null) {
			if (other.modality != null)
				return false;
		} else if (!modality.equals(other.modality))
			return false;
		return true;
	}
}