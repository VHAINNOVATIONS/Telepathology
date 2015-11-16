/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 24, 2010
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
public class FederationActiveExamsType 
{
	private String rawHeader1;
	private String rawHeader2;
	private String siteNumber;
	private FederationActiveExamType [] activeExams;

	public FederationActiveExamsType()
	{
		super();
	}

	public String getRawHeader1() {
		return rawHeader1;
	}

	public void setRawHeader1(String rawHeader1) {
		this.rawHeader1 = rawHeader1;
	}

	public String getRawHeader2() {
		return rawHeader2;
	}

	public void setRawHeader2(String rawHeader2) {
		this.rawHeader2 = rawHeader2;
	}

	public String getSiteNumber() {
		return siteNumber;
	}

	public void setSiteNumber(String siteNumber) {
		this.siteNumber = siteNumber;
	}

	public FederationActiveExamType[] getActiveExams() {
		return activeExams;
	}

	public void setActiveExams(FederationActiveExamType[] activeExams) {
		this.activeExams = activeExams;
	}
}
