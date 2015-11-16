/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 30, 2008
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
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

import java.nio.ByteBuffer;

/**
 * This is an object that contains a ByteBuffer and the known size of the image. This is necessary
 * to know how many bytes in the buffer actually need to be ready (not the full size of the ByteBuffer).
 * 
 * @author VHAISWWERFEJ
 *
 */
public class KnownSizeByteBuffer 
{
	private final ByteBuffer buffer;
	private final int knownSize;
	private final String identifier;
	
	/**
	 * Create a known size byte buffer
	 * @param identifier
	 * @param buffer
	 * @param knownSize
	 */
	public KnownSizeByteBuffer(String identifier, ByteBuffer buffer, int knownSize)
	{
		this.identifier = identifier;
		this.buffer = buffer;
		this.knownSize = knownSize;
	}

	/**
	 * @return the buffer
	 */
	public ByteBuffer getBuffer() {
		return buffer;
	}

	/**
	 * @return the knownSize
	 */
	public int getKnownSize() {
		return knownSize;
	}

	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) 
	{
		if(obj.getClass() == KnownSizeByteBuffer.class)
		{
			KnownSizeByteBuffer that = (KnownSizeByteBuffer)obj;
			if(this.identifier.equals(that.identifier))
				return true;
		}
		return false;
	}
}
