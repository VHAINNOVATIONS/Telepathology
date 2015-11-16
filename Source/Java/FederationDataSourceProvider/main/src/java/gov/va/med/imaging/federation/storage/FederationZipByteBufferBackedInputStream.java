/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 11, 2010
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
package gov.va.med.imaging.federation.storage;

import java.io.IOException;
import java.io.InputStream;

import gov.va.med.imaging.exchange.storage.ByteBufferBackedInputStream;

/**
 * Extends ByteBufferBackedInputStream and returns false for isCloseInputStreamWhenReadComplete() so the input
 * stream is not closed when the read is complete. This is necessary because the input stream is the zip stream
 * and there may be more files in the zip stream to read. This should only be used when the input stream is a
 * zip stream
 * 
 * @author vhaiswwerfej
 *
 */
public class FederationZipByteBufferBackedInputStream 
extends ByteBufferBackedInputStream 
{
	public FederationZipByteBufferBackedInputStream(InputStream inputStream, int size, boolean readIntoBuffer, 
			String providedChecksum)
	throws IOException
	{
		super(inputStream, size, readIntoBuffer, providedChecksum);
	}

	@Override
	protected boolean isCloseInputStreamWhenReadComplete() 
	{
		return false;
	}

}
