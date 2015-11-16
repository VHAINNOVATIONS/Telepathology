/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 24, 2010
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
package gov.va.med.imaging.exchange.enums;

/**
 * Status of the result of getting a collection of artifacts from a source
 * 
 * @author vhaiswwerfej
 *
 */
public enum ArtifactResultStatus
{
	fullResult("The collection of results is the full result. There should not be any errors in a full result (although this is not a requirement)"), 
	partialResult("The collection of results is not the full result but a subset, errors might indicate the reason for the subset"),
	// note - errorResult does not conform to XCA spec but is necessary to hold data 
	errorResult("There was an error retrieving data, this is not a full or a partial result. There should one or more ArtifactResultError to describe the problem");
	
	final String description;
	
	ArtifactResultStatus(String description)
	{
		this.description = description;
	}

	public String getDescription()
	{
		return description;
	}
}
