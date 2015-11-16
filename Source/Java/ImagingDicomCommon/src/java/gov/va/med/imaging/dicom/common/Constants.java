/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: April 13, 2005
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWLOUTHIJ
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

package gov.va.med.imaging.dicom.common;

/**
 * @author Jon
 *
 */
public class Constants {
	public static final short DICOM_VR_AE = 1;
	public static final short DICOM_VR_AS = 2;
	public static final short DICOM_VR_AT = 3;
	public static final short DICOM_VR_CS = 4;
	public static final short DICOM_VR_DA = 5;
	public static final short DICOM_VR_DS = 6;
	public static final short DICOM_VR_DT = 7;
	public static final short DICOM_VR_FD = 8;
	public static final short DICOM_VR_FL = 9;
	public static final short DICOM_VR_INVALID = 10;
	public static final short DICOM_VR_IS = 11;
	public static final short DICOM_VR_LO = 12;
	public static final short DICOM_VR_LT = 13;
	public static final short DICOM_VR_OB = 14;
	public static final short DICOM_VR_OW = 15;
	public static final short DICOM_VR_PN = 16;
	public static final short DICOM_VR_SH = 17;
	public static final short DICOM_VR_SL = 18;
	public static final short DICOM_VR_SQ = 19;
	public static final short DICOM_VR_SQ_DELIM = 20;
	public static final short DICOM_VR_SS = 21;
	public static final short DICOM_VR_ST = 22;
	public static final short DICOM_VR_TM = 23;
	public static final short DICOM_VR_UI = 24;
	public static final short DICOM_VR_UL = 25;
	public static final short DICOM_VR_UN = 26; // unknown
	public static final short DICOM_VR_US = 27;
	public static final short DICOM_VR_UT = 28;
//	public static final short DICOM_VR_UNKNOWN = 29;
	
	
	public static final String PATIENTROOT_FIND = "1.2.840.10008.5.1.4.1.2.1.1";
	public static final String STUDYROOT_FIND = "1.2.840.10008.5.1.4.1.2.2.1";
	public static final String PATIENTSTUDYONLYROOT_FIND = "1.2.840.10008.5.1.4.1.2.3.1"; 
	public static final String PATIENTROOT_MOVE = "1.2.840.10008.5.1.4.1.2.1.2";
	public static final String STUDYROOT_MOVE = "1.2.840.10008.5.1.4.1.2.2.2";
	public static final String PATIENTSTUDYONLYROOT_MOVE = "1.2.840.10008.5.1.4.1.2.3.2"; 

	public static final String PATIENT_LEVEL = "PATIENT";
    public static final String STUDY_LEVEL = "STUDY";
    public static final String SERIES_LEVEL = "SERIES";
    public static final String IMAGE_LEVEL = "IMAGE";
    
    public static final int SUCCESS = 0;
    public static final int CANCELLED = 1;
    public static final int FAILURE = 2;
    public static final int WARNING = 3;
    public static final int ABORT = 4;
    public static final int REJECT = 5;
    
    public static final int IOD_VALID = 0;
    public static final int IOD_NOT_VALID = 1;
    public static final int IOD_NOT_CHECKED = 2;
    public static final int IOD_UNKNOWN = 3;
  
}
