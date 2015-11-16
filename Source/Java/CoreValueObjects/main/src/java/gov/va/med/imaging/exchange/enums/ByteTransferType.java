/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Jun 5, 2008
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
package gov.va.med.imaging.exchange.enums;

/**
 * @author VHAISWBECKEC - vhaiswbatesl1
 * This is a list of all of the known types of byte transfers recorded in the Transaction Log. 
 * NOTE: If types of byte transfers change in the Transaction Log, then so does this.
 * This class should only be used where the content of the list is not critical.
 */
public enum ByteTransferType
{
	FACADE_BYTES_SENT("FacadeBytesSent"),
	FACADE_BYTES_RECEIVED("FacadeBytesReceived"),
	DATASOURCE_BYTES_SENT("DataSourceBytesSent"),
	DATASOURCE_BYTES_RECEIVED("DataSourceBytesReceived");
	
	private final String description;
	
	ByteTransferType(String description)
	{
		this.description = description;
	}

	public String getDescription()
    {
    	return description;
    }
	
	public static ByteTransferType valueOf(int ordinal)
	{
		for(ByteTransferType btt : ByteTransferType.values())
			if(btt.ordinal() == ordinal)
				return btt;
		
		return null;
	}
}
