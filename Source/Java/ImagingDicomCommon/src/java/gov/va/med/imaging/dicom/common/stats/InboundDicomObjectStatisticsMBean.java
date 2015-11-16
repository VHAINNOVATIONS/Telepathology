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
public interface InboundDicomObjectStatisticsMBean {

	/**
	 * 
	 * @return AETitle
	 */
	public String getAeTitle();
	
	/**
	 * 
	 * @return Total number of objects processed.
	 */
	public int getTotalObjectsProcessed();
	
	/**
	 * 
	 * @return  Total number of objects rejected.
	 */
	public int getTotalObjectsRejected();
	
	/**
	 * 
	 * @return Total number of objects passed to the legacy DGW.
	 */
	public int getTotalObjectsPassedToLegacyGW();
	
	/**
	 * 
	 * @return Total number of objects passed to the 2005.6x Data Structure.
	 */
	public int getTotalObjectsPassedToHDIGDataStructure();
	
	/**
	 * 
	 * @return  Total number of duplicate objects
	 */
	public int getTotalDuplicateObjects();
		
}
