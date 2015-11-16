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
 * @author VHAISWBECKEC
 * This is a list of all of the known protocols that a datasource may implement.
 * NOTE: a datasource may register and implement any protocol so this list CANNOT be
 * relied on to be complete.
 * This class should only be used where the content of the list is not critical.
 */
public enum DatasourceProtocol
implements java.io.Serializable
{
	EXCHANGE("exchange", "DoD to VA Transport Protocol"), 
	VFTP("vftp", "ViX Federation Transport Protocol (Vix to Vix"), 
	CDTP("cdtp", "Clinical Display Transport Protocol"),
	VISTA("vista", "Vista Datasource Protocol"),
	XCA("xca", "Cross Community Access Protocol"),
	VISTAIMAGING("vistaimaging", "VistA Imaging Datasource Protocol");
	
	private final String protocol;
	private final String description;
	
	DatasourceProtocol(String protocol, String description)
	{
		this.protocol = protocol;
		this.description = description;
	}

	public String getProtocol()
    {
    	return protocol;
    }

	public String getDescription()
    {
    	return description;
    }
	
	public static DatasourceProtocol valueOf(int ordinal)
	{
		for(DatasourceProtocol dp : DatasourceProtocol.values())
			if(dp.ordinal() == ordinal)
				return dp;
		
		return null;
	}
}
