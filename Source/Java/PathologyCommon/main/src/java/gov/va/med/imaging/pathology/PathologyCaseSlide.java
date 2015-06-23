/**
 * 
 */
package gov.va.med.imaging.pathology;

/**
 * @author Julian
 *
 */
public class PathologyCaseSlide 
{
	private final String slideNumber;
	private final String dateTimeScanned;
	private final String url;
	private final String zoomFactor;
	private final String scanApplication;
	private final String slideStatus;
	private final String viewApplication;
	private final String description;
	
	public PathologyCaseSlide(String slideNumber, String dateTimeScanned,
			String url, String zoomFactor, String scanApplication,
			String slideStatus, String viewApplication, String description) {
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

	public String getDateTimeScanned() {
		return dateTimeScanned;
	}

	public String getUrl() {
		return url;
	}

	public String getZoomFactor() {
		return zoomFactor;
	}

	public String getScanApplication() {
		return scanApplication;
	}

	public String getSlideStatus() {
		return slideStatus;
	}

	public String getViewApplication() {
		return viewApplication;
	}

	public String getDescription() {
		return description;
	}
}
