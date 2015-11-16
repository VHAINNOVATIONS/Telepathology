/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: 
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswpeterb
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

package gov.va.med.imaging.dicom.common.stats;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class InboundDicomAssociationStatistics implements InboundDicomAssociationStatisticsMBean{

	private String aeTitle;
	private String ipAddress;
	
	private int totalAcceptedAssociations;
	private int totalRejectedAssociations;
	private String timeStampOfLastActivity;
	
	public InboundDicomAssociationStatistics(String aet, String ipAddr){
		this.aeTitle = aet;
		this.ipAddress = ipAddr;
		
		this.totalAcceptedAssociations = 0;
		this.totalRejectedAssociations = 0;
	}

	/**
	 * @return the aeTitle
	 */
	public String getAeTitle() {
		return aeTitle;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.InboundDicomAssociationStatisticsMBean#getIpAddress()
	 */
	@Override
	public String getIpAddress() {
		// TODO Auto-generated method stub
		return ipAddress;
	}

	/**
	 * @param aeTitle the aeTitle to set
	 */
	public void setAeTitle(String aeTitle) {
		this.aeTitle = aeTitle;
	}

	/**
	 * @return the timeStampOfLastActivity
	 */
	public String getTimeStampOfLastActivity() {
		return timeStampOfLastActivity;
	}

	/**
	 * @return the totalAcceptedAssociations
	 */
	public int getTotalAcceptedAssociations() {
		return totalAcceptedAssociations;
	}
	
	public synchronized void incrementAcceptedAssociationsCount(){
		this.totalAcceptedAssociations++;
		Calendar cal = Calendar.getInstance();
	    String DATE_FORMAT = "yyyyMMdd";
	    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	    this.timeStampOfLastActivity = sdf.format(cal.getTime());

	}

	/**
	 * @return the totalRejectedAssociations
	 */
	public int getTotalRejectedAssociations() {
		return totalRejectedAssociations;
	}
	
	public synchronized void incrementRejectedAssociationsCount(){
		this.totalRejectedAssociations++;
		Calendar cal = Calendar.getInstance();
	    String DATE_FORMAT = "yyyyMMdd";
	    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	    this.timeStampOfLastActivity = sdf.format(cal.getTime());
	}

}
