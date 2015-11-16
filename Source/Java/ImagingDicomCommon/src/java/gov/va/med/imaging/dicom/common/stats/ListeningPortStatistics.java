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


public class ListeningPortStatistics implements ListeningPortStatisticsMBean {
	
	private int port;
	
	private String currentStatus;
	
	private String listeningSince;

	
	public ListeningPortStatistics(){
		this.port = 0;
		this.currentStatus = DicomServiceStats.DOWN;
		this.listeningSince = null;
	}
	
	public ListeningPortStatistics(int port){
		this.port = port;
		this.currentStatus = DicomServiceStats.DOWN;
		this.listeningSince = null;
	}
	
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.ListeningPortStatisticsMBean#getPort()
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.ListeningPortStatisticsMBean#getPortName()
	 */
	@Override
	public String getPortName() {
		return Integer.toString(port);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.ListeningPortStatisticsMBean#getCurrentStatus()
	 */
	public String getCurrentStatus() {
		return currentStatus;
	}

	/**
	 * @param currentStatus the currentStatus to set
	 */
	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
		if(this.currentStatus.equals(DicomServiceStats.UP)){
			Calendar alive = Calendar.getInstance();
		    String DATE_FORMAT = "yyyyMMdd";
		    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
			this.listeningSince = sdf.format(alive.getTime());
		}
		else{
			this.listeningSince = null;
		}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.ListeningPortStatisticsMBean#getListeningSince()
	 */
	public String getListeningSince() {
		return this.listeningSince;
	}
	
}
