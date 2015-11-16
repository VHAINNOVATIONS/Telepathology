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
 * This is a list of the possible byte transfer pathways in Vix.  One is the number of raw DataSource bytes received,
 * followed by the number of processed bytes returned to the client by the Facade.  The other is the number of bytes
 * input to a Facade,  followed by the number of bytes a DataSource sends out.
 * NOTE: If types of byte transfers change in the Transaction Log, then so does this.
 * This class should only be used where the content of the list is not critical.
 */
public enum ByteTransferPath
{
	DS_IN_FACADE_OUT("DataSourceBytesReceived, FacadeBytesSent"),
	FACADE_IN_DS_OUT("FacadeBytesReceived, DataSourceBytesSent");

	private final String description;
	
	ByteTransferPath(String description)
	{
		this.description = description;
	}

	public String getDescription()
    {
    	return description;
    }
	
	public static ByteTransferPath valueOf(int ordinal)
	{
		for(ByteTransferPath btp : ByteTransferPath.values())
			if(btp.ordinal() == ordinal)
				return btp;
		
		return null;
	}
}
