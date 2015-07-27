/**
 * 
 */
package gov.va.med.imaging.federation.pathology.rest.types;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Julian
 *
 */
@XmlRootElement
public class PathologyFederationCaseSlideType 
{
	private String slideNumber;
	private String dateTimeScanned;
	private String url;
	private String zoomFactor;
	private String scanApplication;
	private String slideStatus;
	private String viewApplication;
	private String description;
	
	public PathologyFederationCaseSlideType(){
		super();
	}

	public PathologyFederationCaseSlideType(String slideNumber,
			String dateTimeScanned, String url, String zoomFactor,
			String scanApplication, String slideStatus, String viewApplication,
			String description) {
		super();
		this.slideNumber = slideNumber;
		this.dateTimeScanned = dateTimeScanned;
		this.url = url;
		this.zoomFactor = zoomFactor;
		this.scanApplication = scanApplication;
		this.slideStatus = slideStatus;
		this.viewApplication = viewApplication;
		this.description = description;
	}

	public String getSlideNumber() {
		return slideNumber;
	}

	public void setSlideNumber(String slideNumber) {
		this.slideNumber = slideNumber;
	}

	public String getDateTimeScanned() {
		return dateTimeScanned;
	}

	public void setDateTimeScanned(String dateTimeScanned) {
		this.dateTimeScanned = dateTimeScanned;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getZoomFactor() {
		return zoomFactor;
	}

	public void setZoomFactor(String zoomFactor) {
		this.zoomFactor = zoomFactor;
	}

	public String getScanApplication() {
		return scanApplication;
	}

	public void setScanApplication(String scanApplication) {
		this.scanApplication = scanApplication;
	}

	public String getSlideStatus() {
		return slideStatus;
	}

	public void setSlideStatus(String slideStatus) {
		this.slideStatus = slideStatus;
	}

	public String getViewApplication() {
		return viewApplication;
	}

	public void setViewApplication(String viewApplication) {
		this.viewApplication = viewApplication;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
