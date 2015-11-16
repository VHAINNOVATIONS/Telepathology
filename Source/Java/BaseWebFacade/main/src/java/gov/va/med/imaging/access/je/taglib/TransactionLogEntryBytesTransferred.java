/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: May 28, 2008
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWBECKEC
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */
package gov.va.med.imaging.access.je.taglib;

import gov.va.med.imaging.BinaryOrdersOfMagnitude;
import gov.va.med.imaging.exchange.enums.ByteTransferType;


/**
 * @author VHAISWBECKEC
 *
 */
public class TransactionLogEntryBytesTransferred 
extends TransactionLogEntryElement
{
	private static final long serialVersionUID = 1L;
	private int precision = 2;
	private ByteTransferType byteTransferType = ByteTransferType.DATASOURCE_BYTES_RECEIVED;
	
	public int getPrecision()
    {
    	return precision;
    }

	public void setPrecision(int precision)
    {
    	this.precision = precision;
    }

	public ByteTransferType getByteTransferType ()
	{
		return byteTransferType;
	}
	
	public void setByteTransferType (ByteTransferType byteTransferType)
	{
		this.byteTransferType = byteTransferType;
	}
	
	/**
	 * @see gov.va.med.imaging.access.je.taglib.TransactionLogEntryElement#getElementValue()
	 */
	@Override
	protected String getElementValue()
	{
		Long bytesXferred = null;
		
		switch (getByteTransferType ())
		{
		   case FACADE_BYTES_SENT:
			   bytesXferred = getTransactionLogEntry().getFacadeBytesSent ();
			   break;
			   
		   case FACADE_BYTES_RECEIVED:
			   bytesXferred = getTransactionLogEntry().getFacadeBytesReceived ();
			   break;
			   
		   case DATASOURCE_BYTES_SENT:
			   bytesXferred = getTransactionLogEntry().getDataSourceBytesSent ();
			   break;
		
		   case DATASOURCE_BYTES_RECEIVED:
			   bytesXferred = getTransactionLogEntry().getDataSourceBytesReceived ();
			   break;
		
		   default:
			   bytesXferred = null;
		}
		
		return bytesXferred == null ? 
			"" :
			BinaryOrdersOfMagnitude.format( bytesXferred, getPrecision() );
	}

}
