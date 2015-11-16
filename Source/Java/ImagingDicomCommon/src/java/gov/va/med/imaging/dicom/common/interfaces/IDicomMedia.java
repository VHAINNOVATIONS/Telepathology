/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 4, 2011
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
package gov.va.med.imaging.dicom.common.interfaces;


import gov.va.med.imaging.exchange.business.dicom.DicomDIRRecord;
import gov.va.med.imaging.exchange.business.dicom.exceptions.DicomMediaException;

import java.io.InputStream;
import java.io.OutputStream;

import org.w3c.dom.Document;

/**
 * @author vhaiswpeterb
 *
 */
public interface IDicomMedia {

	public abstract OutputStream readDicomDIRToStream(InputStream stream)
	throws DicomMediaException;

	public abstract DicomDIRRecord readDicomDIR(InputStream stream)
	throws DicomMediaException;

	public abstract Document readDicomDIRToDocument(InputStream stream)
						throws DicomMediaException;
	
	public abstract String readDicomDIRToXMLString(InputStream stream)
						throws DicomMediaException;
	
	public abstract Document readDicomDIRToDocument(String filename)
						throws DicomMediaException;
	
	public abstract String readDicomDIRToXMLString(String filename)
						throws DicomMediaException;
	
	public abstract Document readDicomDIRToDocument(IDicomDataSet dds)
						throws DicomMediaException;
	
	public abstract String readDicomDIRToXMLString(IDicomDataSet dds)
						throws DicomMediaException;
}
