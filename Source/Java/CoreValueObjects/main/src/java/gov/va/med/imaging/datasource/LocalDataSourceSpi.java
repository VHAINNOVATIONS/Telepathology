/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Apr 30, 2008
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
package gov.va.med.imaging.datasource;

/**
 * The root abstract class of all local service provider interfaces.
 * A local service provider has no protocol or protocol version and is
 * required to implement only a no-arg constructor and/or a no-arg factory
 * method (i.e. public static T create(){...})
 * 
 * The Provider.Service description of the implemented local service
 * must identify the protocol as null and the protocolVersion as 0.0f.
 * 
 * @author VHAISWBECKEC
 *
 */
public interface LocalDataSourceSpi 
extends DataSourceSpi
{
	public final static String protocol = null;
	public final static float protocolVersion = 0.0f;
	
}
