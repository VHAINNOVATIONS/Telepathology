/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Aug 29, 2011
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWPETERB
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
 * @author VHAISWPETERB
 *
 */
public class InboundDicomObjectStatistics implements
		InboundDicomObjectStatisticsMBean {

	public String aeTitle;
	
	public int totalObjectsProcessed;
	public int totalObjectsRejected;
	public int totalObjectsPassedToLegacyGW;
	public int totalObjectsPassedToHDIGDataStructure;
	public int totalDuplicateObjects;
	
	
	/**
	 * @param aeTitle
	 */
	public InboundDicomObjectStatistics(String aeTitle) {
		super();
		this.aeTitle = aeTitle;
		this.totalObjectsProcessed = 0;
		this.totalObjectsRejected = 0;
		this.totalObjectsPassedToLegacyGW = 0;
		this.totalDuplicateObjects = 0;
		this.totalObjectsPassedToHDIGDataStructure = 0;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.InboundDicomObjectStatisticsMBean#getAetitle()
	 */
	@Override
	public String getAeTitle() {
		return aeTitle;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.InboundDicomObjectStatisticsMBean#getTotalObjectsProcessed()
	 */
	@Override
	public int getTotalObjectsProcessed() {
		
		return totalObjectsProcessed;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.InboundDicomObjectStatisticsMBean#getTotalObjectsRejected()
	 */
	@Override
	public int getTotalObjectsRejected() {
		
		return totalObjectsRejected;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.InboundDicomObjectStatisticsMBean#getTotalObjectsPassedToLegacyGW()
	 */
	@Override
	public int getTotalObjectsPassedToLegacyGW() {
		return totalObjectsPassedToLegacyGW;
	}
	
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.InboundDicomObjectStatisticsMBean#getTotalObjectsPassedToHDIGDataStructure()
	 */
	@Override
	public int getTotalObjectsPassedToHDIGDataStructure() {
		return this.totalObjectsPassedToHDIGDataStructure;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.InboundDicomObjectStatisticsMBean#getTotalDuplicateObjects()
	 */
	@Override
	public int getTotalDuplicateObjects() {
		return this.totalDuplicateObjects;
	}

	/**
	 * @param aeTitle the aeTitle to set
	 */
	public void setAeTitle(String aeTitle) {
		this.aeTitle = aeTitle;
	}
	
	public synchronized void incrementObjectsProcessedCount(){
		this.totalObjectsProcessed++;
	}
	
	public synchronized void incrementObjectsRejectedCount(){
		this.totalObjectsRejected++;
	}
	
	public synchronized void incrementObjectsPassedToLegacyGWCount(){
		this.totalObjectsPassedToLegacyGW++;
	}
	
	public synchronized void incrementObjectsPassedToHDIGDataStructureCount(){
		this.totalObjectsPassedToHDIGDataStructure++;
	}
	
	public synchronized void incrementDuplicateObjectsCount(){
		this.totalDuplicateObjects++;
	}
	
	
}
