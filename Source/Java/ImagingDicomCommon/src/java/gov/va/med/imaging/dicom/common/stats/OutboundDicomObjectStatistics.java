/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Aug 31, 2011
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
public class OutboundDicomObjectStatistics implements
		OutboundDicomObjectStatisticsMBean {

	private String aeTitle;
	private int totalObjectsTransmittedToSCP;
	private int totalObjetsRejectedBySCP;
	
	
	/**
	 * @param aeTitle
	 */
	public OutboundDicomObjectStatistics(String aeTitle) {
		super();
		this.aeTitle = aeTitle;
		this.totalObjectsTransmittedToSCP = 0;
		this.totalObjetsRejectedBySCP = 0;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.OutboundDicomObjectStatisticsMBean#getAeTitle()
	 */
	@Override
	public String getAeTitle() {
		
		return this.aeTitle;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.OutboundDicomObjectStatisticsMBean#getTotalObjectsTransmittedToSCP()
	 */
	@Override
	public int getTotalObjectsTransmittedToSCP() {
		
		return this.totalObjectsTransmittedToSCP;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.OutboundDicomObjectStatisticsMBean#getTotalObjectsRejectedBySCP()
	 */
	@Override
	public int getTotalObjectsRejectedBySCP() {
		
		return this.totalObjetsRejectedBySCP;
	}

	public synchronized void incrementObjectsTransmittedToSCPCount(){
		this.totalObjectsTransmittedToSCP++;
	}
	
	public synchronized void incrementObjectsRejectedBySCPCount(){
		this.totalObjetsRejectedBySCP++;
	}
}
