/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 5, 2011
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
package gov.va.med.imaging.exchange.business.dicom;

import java.io.Serializable;
import java.util.Vector;

/**
 * @author vhaiswpeterb
 *
 */
public class DicomDIRRecord implements Serializable {

	public static final String ROOT = "ROOT";
	public static final String PATIENT = "PATIENT";
	public static final String STUDY = "STUDY";
	public static final String SERIES = "SERIES";
	public static final String IMAGE = "IMAGE";
	
	private String directoryRecordType = null;
	private String fileSetID = null;
	private Vector<DicomDIRRecord> rootRecords= null;

	/**
	 * @return the directoryRecordType
	 */
	public String getDirectoryRecordType() {
		return directoryRecordType;
	}

	/**
	 * @param directoryRecordType the directoryRecordType to set
	 */
	public void setDirectoryRecordType(String directoryRecordType) {
		this.directoryRecordType = directoryRecordType;
	}

	/**
	 * @return the fileSetID
	 */
	public String getFileSetID() {
		return fileSetID;
	}

	/**
	 * @param fileSetID the fileSetID to set
	 */
	public void setFileSetID(String fileSetID) {
		this.fileSetID = fileSetID;
	}

	/**
	 * @return the rootRecords
	 */
	public Vector<DicomDIRRecord> getRootRecords() {
		return rootRecords;
	}

	/**
	 * @param rootRecords the rootRecords to set
	 */
	public void setRootRecords(Vector<DicomDIRRecord> rootRecords) {
		this.rootRecords = rootRecords;
	}
	
	public void addRootRecord(DicomDIRRecord record){
		if(this.rootRecords == null){
			this.rootRecords = new Vector<DicomDIRRecord>();
		}
		this.rootRecords.add(record);
	}

	
}
