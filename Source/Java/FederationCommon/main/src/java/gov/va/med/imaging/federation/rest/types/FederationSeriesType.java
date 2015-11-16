/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 18, 2010
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
package gov.va.med.imaging.federation.rest.types;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author vhaiswwerfej
 *
 */
@XmlRootElement
public class FederationSeriesType 
{
	private String seriesUid;
	private String seriesIen;
	private String seriesNumber;
	private String modality;
	private FederationImageType [] images;
	
	public FederationSeriesType()
	{
		super();
	}

	public String getSeriesUid() {
		return seriesUid;
	}

	public void setSeriesUid(String seriesUid) {
		this.seriesUid = seriesUid;
	}

	public String getSeriesIen() {
		return seriesIen;
	}

	public void setSeriesIen(String seriesIen) {
		this.seriesIen = seriesIen;
	}

	public String getSeriesNumber() {
		return seriesNumber;
	}

	public void setSeriesNumber(String seriesNumber) {
		this.seriesNumber = seriesNumber;
	}

	public String getModality() {
		return modality;
	}

	public void setModality(String modality) {
		this.modality = modality;
	}

	public FederationImageType[] getImages() {
		return images;
	}

	public void setImages(FederationImageType[] images) {
		this.images = images;
	}
}
