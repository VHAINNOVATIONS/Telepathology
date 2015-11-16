/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Dec 14, 2010
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

package gov.va.med.imaging.core;

/**
 * @author vhaiswbeckec
 * 
 * Contains information about a command factory.  This class is used to
 * communicate the provider and the implementing class of a command factory
 * to the CommandFactoryProvider base implementation, which uses it to create
 * command factories.
 *
 */
public class CommandFactoryInfo
implements Comparable<CommandFactoryInfo>
{
	private final CommandFactoryProvider commandFactoryProvider;
	private final byte ordinal;
	private final Class<?> implementingClass;
	/**
	 * @param commandFactoryProvider
	 * @param ordinal
	 * @param implementingClass
	 */
	public CommandFactoryInfo(CommandFactoryProvider commandFactoryProvider, byte ordinal, Class<?> implementingClass)
	{
		super();
		this.commandFactoryProvider = commandFactoryProvider;
		this.ordinal = ordinal;
		this.implementingClass = implementingClass;
	}
	/**
	 * @return the commandFactoryProvider
	 */
	protected CommandFactoryProvider getCommandFactoryProvider()
	{
		return this.commandFactoryProvider;
	}
	/**
	 * @return the ordinal
	 */
	protected byte getOrdinal()
	{
		return this.ordinal;
	}
	/**
	 * @return the implementingClass
	 */
	protected Class<?> getImplementingClass()
	{
		return this.implementingClass;
	}
	
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(CommandFactoryInfo that)
	{
		return (new Byte(this.getOrdinal())).compareTo(new Byte(that.getOrdinal()));
	}
}
