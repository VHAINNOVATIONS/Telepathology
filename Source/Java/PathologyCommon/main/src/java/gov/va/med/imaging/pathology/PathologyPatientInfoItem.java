/**
*
Package: MAG - VistA Imaging
WARNING: Per VHA Directive 2004-038, this routine should not be modified.
Date Created: Oct 16, 2015
Site Name:  Washington OI Field Office, Silver Spring, MD
Developer:  VHAISWTITTOC
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

package gov.va.med.imaging.pathology;

/**
* @author VHAISWTITTOC
*
*/
public class PathologyPatientInfoItem
{
	private final String fieldNumber; // M patient file (#2) filed number
	private final String fieldValue;  // actual patient data in that field

	public PathologyPatientInfoItem(String fieldNum, String fieldVal) {
		super();
		this.fieldNumber = fieldNum;
		this.fieldValue = fieldVal;
	}

	public String getFieldNumber() {
		return fieldNumber;
	}

	public String getFieldValue() {
		return fieldValue;
	}
}
