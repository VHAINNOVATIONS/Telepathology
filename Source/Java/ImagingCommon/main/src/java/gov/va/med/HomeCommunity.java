/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Jan 18, 2011
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author vhaiswbeckec
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

package gov.va.med;

import gov.va.med.imaging.exceptions.OIDFormatException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A class of static fields and methods to determine whether an OID is within the 
 * home community.
 * 
 * @author vhaiswbeckec
 *
 */
public class HomeCommunity
{
	private HomeCommunity(){}	// prevent construction
	
	private static char commentChar = '#';
	private static List<OID> homeCommunityOIDs;
	
	static
	{
		homeCommunityOIDs = new ArrayList<OID>();
		
		InputStream in = HomeCommunity.class.getClassLoader().getResourceAsStream("gov/va/med/HomeCommunityOIDs.txt");
		LineNumberReader reader = new LineNumberReader( new InputStreamReader(in) );
		try
		{
			for( String line = reader.readLine(); line != null; line = reader.readLine() )
			{
				int commentStart = line.indexOf(commentChar);
				if(commentStart >= 0)
					line = line.substring(0, commentStart);
				line = line.trim();
				if(line.length() > 0)
					homeCommunityOIDs.add( OID.create(line) );
			}
		}
		catch (IOException x)
		{
			x.printStackTrace();
			throw new java.lang.ExceptionInInitializerError(x);
		}
		catch (OIDFormatException x)
		{
			x.printStackTrace();
			throw new java.lang.ExceptionInInitializerError(x);
		}
	}
	
	/**
	 * Returns TRUE if the OID is within the defined home community
	 * or FALSE otherwise.
	 * 
	 * @param oid
	 * @return
	 */
	public static boolean isWithinHomeCommunity(OID oid)
	{
		return homeCommunityOIDs.contains(oid);
	}

	/**
	 * Returns TRUE if the home community in the routing token is within the defined home community
	 * or FALSE otherwise.
	 * 
	 * @param routingToken
	 * @return
	 * @throws OIDFormatException
	 */
	public static boolean isWithinHomeCommunity(RoutingToken routingToken) 
	throws OIDFormatException
	{
		return homeCommunityOIDs.contains( OID.create(routingToken.getHomeCommunityId()) );
	}
	
	/**
	 * Returns TRUE if the OID (represented as a String) is within the defined home community
	 * or FALSE otherwise.
	 * 
	 * @param routingToken
	 * @return
	 * @throws OIDFormatException
	 */
	public static boolean isWithinHomeCommunity(String oidAsString) 
	throws OIDFormatException
	{
		return homeCommunityOIDs.contains( OID.create(oidAsString) );
	}
}
