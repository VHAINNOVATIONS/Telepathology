/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 27, 2012
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
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
package gov.va.med.imaging.pathology;

import java.util.Date;

/**
 * @author VHAISWWERFEJ
 *
 */
public class PathologyCaseSupplementalReport
{
	private final Date supplementalReportDate;
	private final boolean verified;
	private final String verifiedProvider;
	private final String [] values;
	
	public PathologyCaseSupplementalReport(Date supplementalReportDate,
			boolean verified, String verifiedProvider, String[] values)
	{
		super();
		this.supplementalReportDate = supplementalReportDate;
		this.verified = verified;
		this.verifiedProvider = verifiedProvider;
		this.values = values;
	}

	public Date getSupplementalReportDate()
	{
		return supplementalReportDate;
	}

	public boolean isVerified()
	{
		return verified;
	}

	public String getVerifiedProvider()
	{
		return verifiedProvider;
	}

	public String[] getValues()
	{
		return values;
	}	
}
