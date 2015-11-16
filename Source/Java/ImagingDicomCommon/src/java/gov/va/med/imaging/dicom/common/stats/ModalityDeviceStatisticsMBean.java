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

public interface ModalityDeviceStatisticsMBean {

	/**
	 * @return the manufacturer
	 */
	public String getManufacturer();

	/**
	 * @return the model
	 */
	public String getModel();

	/**
	 * @return the totalDicomObjectsProcessed
	 */
	public int getTotalDicomObjectsProcessed();

	/**
	 * @return the totalDicomObjectsProcessed
	 */
	public int getTotalStudiesProcessed();

	/**
	 * @return the totalDuplicateInstanceUIDs
	 */
	public int getTotalDuplicateInstanceUIDs();

	/**
	 * @return the totalDicomObjectsWithIODViolations
	 */
	public int getTotalDicomObjectsWithIODViolations();

	/**
	 * @return the totalDicomObjectRejected
	 */
	public int getTotalDicomObjectRejected();

}