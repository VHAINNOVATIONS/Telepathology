/**
 * 
 */
package gov.va.med.imaging.pathology.rest.types;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Julian
 *
 */
@XmlRootElement
public class PathologyCaseSlidesType {

	private PathologyCaseSlideType [] pathologyCaseSlide;
	
	public PathologyCaseSlidesType()
	{
		super();
	}

	public PathologyCaseSlidesType(PathologyCaseSlideType[] pathologyCaseSlide) {
		super();
		this.pathologyCaseSlide = pathologyCaseSlide;
	}

	public PathologyCaseSlideType[] getPathologyCaseSlide() {
		return pathologyCaseSlide;
	}

	public void setPathologyCaseSlide(PathologyCaseSlideType[] pathologyCaseSlide) {
		this.pathologyCaseSlide = pathologyCaseSlide;
	}
}
