/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 6, 2010
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
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
package gov.va.med.imaging.exchange.storage;

import gov.va.med.imaging.channels.ChecksumValue;
import gov.va.med.imaging.exchange.storage.exceptions.CannotCalculateChecksumException;
import gov.va.med.imaging.exchange.storage.exceptions.ChecksumNotProvidedException;

import java.io.InputStream;

/**
 * Response from data sources when requesting objects
 * 
 * @author vhaiswwerfej
 *
 */
public interface DataSourceInputStream 
{
	/**
	 * Returns the size (in bytes) of the stored object
	 * @return
	 */
	public int getSize();
	
	/**
	 * Returns an input stream to the stored object
	 * @return
	 */
	public InputStream getInputStream();
	
	/**
	 * Returns the checksum expected for the stored object 
	 * @return
	 */
	public ChecksumValue getProvidedChecksum();
	
	/**
	 * Returns the calculated checksum for the stored object
	 * @return
	 * @throws CannotCalculateChecksumException
	 */
	public ChecksumValue getCalculatedChecksum() 
	throws CannotCalculateChecksumException;
	
	/**
	 * Returns true if the checksum was provided, false if it was not provided. This should be called before
	 * attempting to compare the checksum in order to ensure a ChecksumNotProvidedException is not thrown.
	 * @return
	 */
	public boolean isChecksumProvided();
	
	/**
	 * Determines if the input stream is readable. If the input stream has been read or is null, this 
	 * will return false.
	 * @return
	 */
	public boolean isReadable();
	
	/**
	 * Compares the provided checksum to the calculated checksum. If the checksum has not already
	 * been calculated, it will be calculated
	 */
	public boolean validateChecksum()
	throws ChecksumNotProvidedException, CannotCalculateChecksumException;	

}
