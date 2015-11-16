/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 6, 2012
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
package gov.va.med.imaging.health;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author VHAISWWERFEJ
 *
 */
public class VisaConfigurationProperty
{
	private final String name;
	private final Date modified;
	private final long size;
	private final String checksum;
	
	public VisaConfigurationProperty(String name, Date modified, long size, String checksum)
	{
		super();
		this.name = name;
		this.modified = modified;
		this.size = size;
		this.checksum = checksum;
	}

	public String getChecksum()
	{
		return checksum;
	}

	public String getName()
	{
		return name;
	}

	public Date getModified()
	{
		return modified;
	}

	public long getSize()
	{
		return size;
	}
	
	private final static String dateFormat = "MMM d, yyyy h:mm:ss a";
	
	public String getModifiedFormatted()
	{
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		return format.format(modified);
	}
}
