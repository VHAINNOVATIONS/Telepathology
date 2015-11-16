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
package gov.va.med.imaging.dicom.dcftoolkit.common.mediainterchange.media;

import gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet;
import gov.va.med.imaging.dicom.common.interfaces.IDicomMedia;
import gov.va.med.imaging.dicom.dcftoolkit.common.exceptions.DCFDicomException;
import gov.va.med.imaging.dicom.dcftoolkit.common.exceptions.DicomDIRFactoryException;
import gov.va.med.imaging.dicom.dcftoolkit.common.mediainterchange.media.factory.DicomDirectoryRecordFactory;
import gov.va.med.imaging.exchange.business.dicom.DicomDIRRecord;
import gov.va.med.imaging.exchange.business.dicom.ImageDIRRecord;
import gov.va.med.imaging.exchange.business.dicom.PatientDIRRecord;
import gov.va.med.imaging.exchange.business.dicom.SeriesDIRRecord;
import gov.va.med.imaging.exchange.business.dicom.StudyDIRRecord;
import gov.va.med.imaging.exchange.business.dicom.exceptions.DicomMediaException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import org.w3c.dom.Document;

import com.lbs.DCS.DCSException;
import com.lbs.DCS.DicomDataSet;
import com.lbs.DCS.DicomFileInput;
import com.lbs.DCS.DicomSessionSettings;
import com.lbs.DCS.DicomStreamReader;
import com.lbs.DSS.DicomDir;
import com.lbs.DSS.DicomDirectoryRecord;
import com.thoughtworks.xstream.XStream;

/**
 * @author vhaiswpeterb
 *
 */
public class DicomMediaImpl implements IDicomMedia {
	
	
	public DicomMediaImpl(){
		super();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IDicomMedia#readDicomDIRToDocument(java.io.InputStream)
	 */
	@Override
	public Document readDicomDIRToDocument(InputStream stream)
				throws DicomMediaException{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IDicomMedia#readDicomDIRToDocument(java.lang.String)
	 */
	@Override
	public Document readDicomDIRToDocument(String filename) 
				throws DicomMediaException{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IDicomMedia#readDicomDIRToDocument(gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet)
	 */
	@Override
	public Document readDicomDIRToDocument(IDicomDataSet dds) 
				throws DicomMediaException{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IDicomMedia#readDicomDIRToStream(java.io.InputStream)
	 */
	@Override
	public OutputStream readDicomDIRToStream(InputStream stream) 
				throws DicomMediaException{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IDicomMedia#readDicomDIRToXStream(java.io.InputStream)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String readDicomDIRToXMLString(InputStream stream) 
				throws DicomMediaException{

		DicomDir dir = null;
		//Create a DICOMDIR dataset from stream.
		try{
			dir = getDicomDIRFromStream(stream);
		}
		catch(DCFDicomException dcfdX){
			throw new DicomMediaException("Failed to create XStream object.");
		}
		
		//Parse the DICOMDIR into a hierarchical nonDCF object.
		DicomDirectoryRecord root = dir.RootDir();
		Vector<DicomDirectoryRecord> rootRecords = root.DirEntries();
		DicomDIRRecord rootRecord = new DicomDIRRecord();
		rootRecord.setDirectoryRecordType(DicomDIRRecord.ROOT);
		try{
			rootRecord.setFileSetID(dir.filesetId());
		}
		catch(DCSException dcsX){
			rootRecord.setFileSetID("UNKNOWN");
		}
		try{
			for (DicomDirectoryRecord record: rootRecords)
			{
				DicomDIRRecord recordObj = DicomDirectoryRecordFactory.createDicomDIRRecord(record);
				
				buildDicomDIRRecord(record, recordObj);
				
				DicomDirectoryRecordFactory.addDicomDIRRecordToParent(rootRecord, recordObj);
			}
		}
		catch(DicomDIRFactoryException ddirfX){
			throw new DicomMediaException("Failed to create XStream object.");
		}
		
		String xmlString = xmlStringUsingXStream(rootRecord);
		
		return xmlString;
	}

	@SuppressWarnings("unchecked")
	@Override
	public DicomDIRRecord readDicomDIR(InputStream stream) 
				throws DicomMediaException{

		DicomDir dir = null;
		//Create a DICOMDIR dataset from stream.
		try{
			dir = getDicomDIRFromStream(stream);
		}
		catch(DCFDicomException dcfdX){
			throw new DicomMediaException("Failed to create XStream object.");
		}
		
		//Parse the DICOMDIR into a hierarchical nonDCF object.
		DicomDirectoryRecord root = dir.RootDir();
		Vector<DicomDirectoryRecord> rootRecords = root.DirEntries();
		DicomDIRRecord rootRecord = new DicomDIRRecord();
		rootRecord.setDirectoryRecordType(DicomDIRRecord.ROOT);
		try{
			rootRecord.setFileSetID(dir.filesetId());
		}
		catch(DCSException dcsX){
			rootRecord.setFileSetID("UNKNOWN");
		}
		try{
			for (DicomDirectoryRecord record: rootRecords)
			{
				DicomDIRRecord recordObj = DicomDirectoryRecordFactory.createDicomDIRRecord(record);
				
				buildDicomDIRRecord(record, recordObj);
				
				DicomDirectoryRecordFactory.addDicomDIRRecordToParent(rootRecord, recordObj);
			}
		}
		catch(DicomDIRFactoryException ddirfX){
			throw new DicomMediaException("Failed to create XStream object.");
		}
		
		return rootRecord;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IDicomMedia#readDicomDIRToXStream(java.lang.String)
	 */
	@Override
	public String readDicomDIRToXMLString(String filename) 
				throws DicomMediaException{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.common.interfaces.IDicomMedia#readDicomDIRToXStream(gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet)
	 */
	@Override
	public String readDicomDIRToXMLString(IDicomDataSet dds) 
				throws DicomMediaException{
		// TODO Auto-generated method stub
		return null;
	}
	
	private DicomDir getDicomDIRFromStream(InputStream stream)
						throws DCFDicomException{

		DicomDir dir = null;
		try{
			DicomSessionSettings sessionSettings = new DicomSessionSettings(); 
			DicomStreamReader reader = new DicomStreamReader(stream, sessionSettings);
		    DicomFileInput dfi = new DicomFileInput(reader, null, sessionSettings );
		    dfi.setTrackElementOffsets(true);
		    
			DicomDataSet dataset = dfi.readDataSet();
			dir = new DicomDir(dataset);
		}
		catch(DCSException dcsX){
			throw new DCFDicomException("Failed to get DICOMDIR from Input Stream.");
		}
		return dir;
	}
	
	@SuppressWarnings("unchecked")
	private void buildDicomDIRRecord(DicomDirectoryRecord record, DicomDIRRecord recordObj) 
					throws DicomDIRFactoryException{	 
		
		
		Vector<DicomDirectoryRecord> lowerRecords = record.DirEntries();
		
		for (DicomDirectoryRecord child:lowerRecords){
			
			DicomDIRRecord childObj = DicomDirectoryRecordFactory.createDicomDIRRecord(child);
			
			buildDicomDIRRecord(child, childObj);

			DicomDirectoryRecordFactory.addDicomDIRRecordToParent(recordObj, childObj);
	    }
	 }
	
	private String xmlStringUsingXStream(DicomDIRRecord record){
		XStream xstream = new XStream();
		xstream.alias("Root", DicomDIRRecord.class);
		xstream.alias("Patient", PatientDIRRecord.class);
		xstream.alias("Study", StudyDIRRecord.class);
		xstream.alias("Series", SeriesDIRRecord.class);
		xstream.alias("Image", ImageDIRRecord.class);
		xstream.alias("Collection", Vector.class);
		
		String xml = xstream.toXML(record);
		return xml;
		
	}
}
