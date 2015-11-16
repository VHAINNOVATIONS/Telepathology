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
package gov.va.med.imaging.dicom.dcftoolkit.common.mediainterchange.media.factory;

import gov.va.med.imaging.dicom.dcftoolkit.common.exceptions.DicomDIRFactoryException;
import gov.va.med.imaging.dicom.dcftoolkit.common.mediainterchange.media.factory.concreteproducts.ImageDicomDirectoryRecord;
import gov.va.med.imaging.dicom.dcftoolkit.common.mediainterchange.media.factory.concreteproducts.PatientDicomDirectoryRecord;
import gov.va.med.imaging.dicom.dcftoolkit.common.mediainterchange.media.factory.concreteproducts.RootDicomDirectoryRecord;
import gov.va.med.imaging.dicom.dcftoolkit.common.mediainterchange.media.factory.concreteproducts.SeriesDicomDirectoryRecord;
import gov.va.med.imaging.dicom.dcftoolkit.common.mediainterchange.media.factory.concreteproducts.StudyDicomDirectoryRecord;
import gov.va.med.imaging.exchange.business.dicom.DicomDIRRecord;

import com.lbs.DSS.DicomDirectoryRecord;
import com.lbs.DSS.ImageDirectoryRecord;
import com.lbs.DSS.PatientDirectoryRecord;
import com.lbs.DSS.SeriesDirectoryRecord;
import com.lbs.DSS.StudyDirectoryRecord;

/**
 * @author vhaiswpeterb
 *
 */
public class DicomDirectoryRecordFactory {

	/**
	 * 
	 */
	public DicomDirectoryRecordFactory() {
		super();
	}

    public static DicomDIRRecord createDicomDIRRecord(DicomDirectoryRecord record)
    			throws DicomDIRFactoryException{

		if(record instanceof PatientDirectoryRecord){
			return new PatientDicomDirectoryRecord().createDicomDIRRecord(record);
		}
		if(record instanceof StudyDirectoryRecord){
			return new StudyDicomDirectoryRecord().createDicomDIRRecord(record);
		}
		if(record instanceof SeriesDirectoryRecord){
			return new SeriesDicomDirectoryRecord().createDicomDIRRecord(record);
		}
		if(record instanceof ImageDirectoryRecord){
			return new ImageDicomDirectoryRecord().createDicomDIRRecord(record);
		}
    	if(record instanceof DicomDirectoryRecord){
    		return new ImageDicomDirectoryRecord().createDicomDIRRecord(record);
    	}
    	return null;
    }

	
    public static void addDicomDIRRecordToParent(DicomDIRRecord parent, DicomDIRRecord child)
    				throws DicomDIRFactoryException{

    	if(parent.getDirectoryRecordType() == null){
    		throw new DicomDIRFactoryException("Record Type is Null.");
    	}
    	if(parent.getDirectoryRecordType().equals(DicomDIRRecord.ROOT)){
    		new RootDicomDirectoryRecord().addDicomDIRRecordToParent(parent, child);
    	}
		if(parent.getDirectoryRecordType().equals(DicomDIRRecord.PATIENT)){
			new PatientDicomDirectoryRecord().addDicomDIRRecordToParent(parent, child);
		}
		if(parent.getDirectoryRecordType().equals(DicomDIRRecord.STUDY)){
			new StudyDicomDirectoryRecord().addDicomDIRRecordToParent(parent, child);
		}
		if(parent.getDirectoryRecordType().equals(DicomDIRRecord.SERIES)){
			new SeriesDicomDirectoryRecord().addDicomDIRRecordToParent(parent, child);
		}
		if(parent.getDirectoryRecordType().equals(DicomDIRRecord.IMAGE)){
			new ImageDicomDirectoryRecord().addDicomDIRRecordToParent(parent, child);
		}    	
    }
}
