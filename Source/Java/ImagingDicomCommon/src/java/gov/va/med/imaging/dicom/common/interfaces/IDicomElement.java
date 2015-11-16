/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: March 21, 2005
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWLOUTHJ
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
package gov.va.med.imaging.dicom.common.interfaces;

/**
 * @author Jon
 *
 */
public interface IDicomElement {
	
	/**
	 * Get the decimal representation of the value of this DicomElement if possible.
	 * @return
	 */
	public double getDecimalValue();
	
	/**
	 * Get the decimal representation of one value from a multi-valued DicomElement if possible.
	 * @return
	 */
	public double getDecimalValue(int i);
	
	/**
	 * Get the int representation of the value of this DicomElement if possible.
	 * @return
	 */
	public int getIntValue();
	
	/**
	 * Get the int representation of one value from a multi-valued DicomElement if possible.
	 * @return
	 */
	public int getIntValue(int i);
	
	/**
	 * Get the String representation of the value of this DicomElement.
	 * @return
	 */
	public String getStringValue();
	
	/**
	 * Get a String representation of a single value from a multi-valued DicomElement.
	 * @return
	 */
	public String getStringValue(int i);
	
	/**
	 * Get a String representation of value from a DicomElement of a sequence element (dataset).
	 * @return
	 */
	public String getSequenceElementStringValue(String seqtag);

	/**
	 * Get a String representation of a single value from a multi-valued DicomElement of a sequence element (dataset).
	 * @return
	 */
	public String getSequenceElementStringValue(String seqtag, int i);

	/**
	 * Get the value multiplicity.
	 * @return
	 */
	public int vm();
	
	/**
	 * Get the value representation.
	 * @return
	 */
	public short vr();
	

	/**
	 * Get the DICOM tag name as a string.
	 * @return
	 */
	public String getTagName();
	
}
