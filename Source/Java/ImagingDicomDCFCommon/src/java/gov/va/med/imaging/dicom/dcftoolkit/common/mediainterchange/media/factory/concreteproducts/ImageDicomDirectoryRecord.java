/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 6, 2011
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
package gov.va.med.imaging.dicom.dcftoolkit.common.mediainterchange.media.factory.concreteproducts;

import org.apache.log4j.Logger;

import gov.va.med.imaging.dicom.dcftoolkit.common.exceptions.DicomDIRFactoryException;
import gov.va.med.imaging.dicom.dcftoolkit.common.impl.DicomDataSetImpl;
import gov.va.med.imaging.dicom.dcftoolkit.common.mediainterchange.media.factory.products.AbstractDicomDirectoryRecord;
import gov.va.med.imaging.exchange.business.dicom.DicomDIRRecord;
import gov.va.med.imaging.exchange.business.dicom.ImageDIRRecord;
import gov.va.med.imaging.exchange.business.dicom.SOPInstance;

import com.lbs.DCS.DCSException;
import com.lbs.DSS.DicomDirectoryRecord;
import com.lbs.DSS.ImageDirectoryRecord;

/**
 * @author vhaiswpeterb
 *
 */
public class ImageDicomDirectoryRecord extends AbstractDicomDirectoryRecord {

    private static Logger logger = Logger.getLogger(ImageDicomDirectoryRecord.class);

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.dcftoolkit.common.mediainterchange.media.factory.products.AbstractDicomDirectoryRecord#createDicomDIRRecord(com.lbs.DSS.DicomDirectoryRecord)
	 */
	@Override
	public DicomDIRRecord createDicomDIRRecord(DicomDirectoryRecord record)
			throws DicomDIRFactoryException {

		ImageDIRRecord imageDIRRecord = new ImageDIRRecord();
		

		try
		{
			String referencedFileId = record.referencedFileId();
			if (referencedFileId == null || referencedFileId.equals(""))
			{
				logger.warn("DICOM object has no file id: " + record.directoryRecordType());
			}

			if(record instanceof ImageDirectoryRecord)
			{
				ImageDirectoryRecord imageRecord = (ImageDirectoryRecord)record;
					DicomDataSetImpl dataSet = new DicomDataSetImpl(record.data_set());
					SOPInstance instance = dataSet.getSOPInstance();
					imageDIRRecord.setDirectoryRecordType(DicomDIRRecord.IMAGE);
					imageDIRRecord.setFileID(imageRecord.referencedFileId());
					imageDIRRecord.setImageInstanceUID(imageRecord.refdSopinstanceUidFile());
					imageDIRRecord.setInstanceNumber(imageRecord.instanceNumber());
					imageDIRRecord.setSopClassUid(instance.getSOPClassUID());
					imageDIRRecord.setTransferSyntaxUid(instance.getTransferSyntaxUid());
					imageDIRRecord.setImageNumber(instance.getInstanceNumber());
					imageDIRRecord.setNumberOfFrames(instance.getNumberOfFrames());
			}
			else
			{
				imageDIRRecord.setFileID(record.referencedFileId());
				imageDIRRecord.setImageInstanceUID(record.refdSopinstanceUidFile());
			}
		}			
		catch(DCSException dcsX){
			throw new DicomDIRFactoryException("Failed to create DICOMDIR Image Directory Record");
		}
		return imageDIRRecord;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.dcftoolkit.common.mediainterchange.media.factory.products.AbstractDicomDirectoryRecord#addDicomDIRRecordToParent(gov.va.med.imaging.exchange.business.dicom.DicomDIRRecord, gov.va.med.imaging.exchange.business.dicom.DicomDIRRecord)
	 */
	@Override
	public void addDicomDIRRecordToParent(DicomDIRRecord parent,
			DicomDIRRecord child) throws DicomDIRFactoryException {

		throw new DicomDIRFactoryException(this.getClass().getName()+": This method should never be called");
	}

}
