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


public class ModalityDeviceStatistics implements ModalityDeviceStatisticsMBean {
	
	private String manufacturer;
	private String model;
	private int totalDicomObjectsProcessed;
	private int totalDicomObjectRejected;
	private int totalStudiesProcessed;
	private int totalDuplicateInstanceUIDs;
	private int totalDicomObjectsWithIODViolations;
	
	public ModalityDeviceStatistics(String manufacturer, String model){
		this.manufacturer = manufacturer;
		this.model = model;
		this.totalDicomObjectsProcessed = 0;
		this.totalDicomObjectRejected = 0;
		this.totalDuplicateInstanceUIDs = 0;
		this.totalDicomObjectsWithIODViolations = 0;
	}
	

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.ModalityDeviceStatisticsMBean#getManufacturer()
	 */
	public String getManufacturer() {
		return manufacturer;
	}

	/**
	 * @param manufacturer the manufacturer to set
	 */
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.ModalityDeviceStatisticsMBean#getModel()
	 */
	public String getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(String model) {
		this.model = model;
	}

	public synchronized void incrementDicomObjectsProcessedCount(){
		this.totalDicomObjectsProcessed++;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.ModalityDeviceStatisticsMBean#getTotalDicomObjectsProcessed()
	 */
	public int getTotalDicomObjectsProcessed() {
		return totalDicomObjectsProcessed;
	}

	public synchronized void incrementStudiesProcessedCount(){
		this.totalStudiesProcessed++;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.ModalityDeviceStatisticsMBean#getTotalStudiesProcessed()
	 */
	public int getTotalStudiesProcessed() {
		return totalStudiesProcessed;
	}

	public synchronized void incrementDuplicateInstanceUIDsCount(){
		this.totalDuplicateInstanceUIDs++;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.ModalityDeviceStatisticsMBean#getTotalDuplicateInstanceUIDs()
	 */
	public int getTotalDuplicateInstanceUIDs() {
		return totalDuplicateInstanceUIDs;
	}

	public synchronized void incrementDicomObjectsWithIODViolationsCount(){
		this.totalDicomObjectsWithIODViolations++;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.ModalityDeviceStatisticsMBean#getTotalDicomObjectsWithIODViolations()
	 */
	public int getTotalDicomObjectsWithIODViolations() {
		return totalDicomObjectsWithIODViolations;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.ModalityDeviceStatisticsMBean#getTotalDicomObjectRejected()
	 */
	public int getTotalDicomObjectRejected() {
		return totalDicomObjectRejected;
	}

	public synchronized void incrementDicomObjectsRejectedCount() {
		this.totalDicomObjectRejected++;
	}

	
}
