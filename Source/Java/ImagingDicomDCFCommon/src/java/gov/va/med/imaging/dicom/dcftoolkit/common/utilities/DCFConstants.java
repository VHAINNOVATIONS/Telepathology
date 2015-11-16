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

package gov.va.med.imaging.dicom.dcftoolkit.common.utilities;

import com.lbs.DCS.QRDimseStatus;
import com.lbs.DCS.UID;

public class DCFConstants {

    public static final String PATIENTROOT_FIND = UID.SOPPATIENTQUERY_FIND;
    public static final String STUDYROOT_FIND = UID.SOPSTUDYQUERY_FIND;
    public static final String PATIENTSTUDYONLYROOT_FIND = UID.SOPPATIENTQUERY_FIND;
    public static final String PATIENTROOT_MOVE = UID.SOPPATIENTQUERY_MOVE;
    public static final String STUDYROOT_MOVE = UID.SOPSTUDYQUERY_MOVE;
    public static final String PATIENTSTUDYONLYROOT_MOVE = UID.SOPPATIENTSTUDYQUERY_MOVE;
    
    public static final int DIMSE_STATUS_CANCEL = QRDimseStatus.CANCEL;
    public static final int DIMSE_STATUS_PENDING = QRDimseStatus.PENDING;
    public static final int DIMSE_STATUS_SUCCESS = QRDimseStatus.DIMSE_SUCCESS;
    public static final int DIMSE_STATUS_OUT_OF_RESOURCES = QRDimseStatus.OUT_OF_RESOURCES_REFUSED;
	
}
