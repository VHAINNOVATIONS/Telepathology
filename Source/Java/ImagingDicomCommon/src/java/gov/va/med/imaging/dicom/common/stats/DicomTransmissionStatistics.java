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
public class DicomTransmissionStatistics implements
		DicomTransmissionStatisticsMBean {

	private String aeTitle = null;
	private int totalAssociationsAborted = 0;
	private int totalAssociationsRejected = 0;
	private int totalAssocationsAccepted = 0;
	private int totalObjectsRejected = 0;
	private int totalObjectsSuccessful = 0;
	
	public DicomTransmissionStatistics(String aet){
		this.aeTitle = aet;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.DicomTransmissionStatisticsMBean#getAeTitle()
	 */
	@Override
	public String getAeTitle() {
		return this.aeTitle;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.DicomTransmissionStatisticsMBean#getTotalTransmissionAssociationAborted()
	 */
	@Override
	public int getTotalTransmissionAssociationsAborted() {
		return this.totalAssociationsAborted;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.DicomTransmissionStatisticsMBean#getTotalTransmissionAssociationRejected()
	 */
	@Override
	public int getTotalTransmissionAssociationsRejected() {
		return this.totalAssociationsRejected;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.DicomTransmissionStatisticsMBean#getTotalTransmissionAssociationsAccepted()
	 */
	@Override
	public int getTotalTransmissionAssociationsAccepted() {
		return this.totalAssocationsAccepted;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.DicomTransmissionStatisticsMBean#getTotalTransmittedObjectsRejected()
	 */
	@Override
	public int getTotalTransmittedObjectsRejected() {
		return this.totalObjectsRejected;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.stats.DicomTransmissionStatisticsMBean#getTotalTransmittedObjectsSuccessful()
	 */
	@Override
	public int getTotalTransmittedObjectsSuccessful() {
		return this.totalObjectsSuccessful;
	}

	public synchronized void incrementTotalTransmissionAssociationAbortedCount(){
		this.totalAssociationsAborted++;
	}
	
	public synchronized void incrementTotalTransmissionAssociationRejectedCount(){
		this.totalAssociationsRejected++;
	}
	
	public synchronized void incrementTotalTransmissionAssociationAcceptedCount(){
		this.totalAssocationsAccepted++;
	}
	
	public synchronized void incrementTotalTransmittedObjectRejectedCount(){
		this.totalObjectsRejected++;
	}
	
	public synchronized void incrementTotalTransmittedObjectSuccessfulCount(){
		this.totalObjectsSuccessful++;
	}
}
