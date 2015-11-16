/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 13, 2011
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

/**
 * @author vhaiswpeterb
 *
 */
public class InboundDicomDimseStatistics implements InboundDicomDimseStatisticsMBean {

	private String aeTitle = null;
	private String dimseServiceName = null;
	private int totalProcessedDimseMessages;
	private int totalRejectedDimseMessages;
	
	public InboundDicomDimseStatistics(String aet, String dimseService){
		this.aeTitle = aet;
		this.dimseServiceName = dimseService;
		this.totalProcessedDimseMessages = 0;
		this.totalRejectedDimseMessages = 0;
	}
	
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.InboundDicomDimseStatisticsMBean#getAeTitle()
	 */
	@Override
	public String getAeTitle() {
		return aeTitle;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.InboundDicomDimseStatisticsMBean#getDimseServiceName()
	 */
	@Override
	public String getDimseServiceName() {
		return dimseServiceName;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.InboundDicomDimseStatisticsMBean#getTotalSuccessfulDimseMessages()
	 */
	@Override
	public int getTotalProcessedDimseMessages() {
		return totalProcessedDimseMessages;
	}

	/**
	 * @return the totalFailedDimseMessages
	 */
	public int getTotalRejectedDimseMessages() {
		return totalRejectedDimseMessages;
	}


	/**
	 * @param aeTitle the aeTitle to set
	 */
	public void setAeTitle(String aeTitle) {
		this.aeTitle = aeTitle;
	}

	/**
	 * @param dimseServiceName the dimseServiceName to set
	 */
	public void setDimseServiceName(String dimseServiceName) {
		this.dimseServiceName = dimseServiceName;
	}

	/**
	 * 
	 */
	public synchronized void incrementProcessedDimseMessageCount() {
		this.totalProcessedDimseMessages++;
	}

	/**
	 * 
	 */
	public synchronized void incrementRejectedDimseMessageCount() {
		this.totalRejectedDimseMessages++;
	}
	
}
