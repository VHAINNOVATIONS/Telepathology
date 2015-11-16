/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: April 2, 2013 
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswlouthj 
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
package gov.va.med.imaging.dicom.dcftoolkit.common.mediainterchange;

import org.apache.log4j.Logger;

import com.lbs.DCS.DicomFileInput;

import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet;
import gov.va.med.imaging.exchange.business.dicom.exceptions.DicomException;
import gov.va.med.imaging.exchange.business.storage.NetworkLocationInfo;
import gov.va.med.imaging.retry.RetryableWorkUnitCommand;
import gov.va.med.imaging.retry.WorkUnit;

/**
 * This class encapsulates a call to read a part 10 file from a network 
 * share. It implements WorkUnit<T> for use within a RetryableWorkUnitCommand<T>
 * 
 * @author vhaiswlouthj
 *
 */
public class ReadPart10FileWorkUnit implements WorkUnit<IDicomDataSet> 
{

	private Logger logger = Logger.getLogger(this.getClass());

	NetworkLocationInfo networkLocationInfo;
	String filename;
	DicomFileInput fileIn;
	
	public ReadPart10FileWorkUnit(NetworkLocationInfo networkLocationInfo, String filename, DicomFileInput fileIn) {
		super();
		this.networkLocationInfo = networkLocationInfo;
		this.filename = filename;
		this.fileIn = fileIn;
	}


	@Override
	public IDicomDataSet doWork() throws MethodException 
	{
		try
		{
			return Part10Files.readDicomFile(networkLocationInfo, filename, fileIn);
		}
		catch (DicomException e)
		{
			// The DicomException is not the root cause. The root cause will be an SMB exception 
			// or some other IO problem, most likely. Get the root cause and display the message from the root
			// when possible, instead of the generic DicomException
			Throwable rootCause = e.getCause() != null ? e.getCause() : e;
			String message = "Exception reading file " + filename + ": " +  rootCause.getMessage(); 
			
			// Log the error and throw the wrapped exception up to the caller
			logger.error(message);
			throw new MethodException(message, e);
		}
	}

}
