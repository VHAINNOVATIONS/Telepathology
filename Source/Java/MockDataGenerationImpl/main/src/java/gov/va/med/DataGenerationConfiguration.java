/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date May 4, 2010
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

import gov.va.med.GenericDataGenerator.Mode;
import gov.va.med.NetworkSimulatingInputStream.DELAY_MODE;
import gov.va.med.NetworkSimulatingInputStream.EXCEPTION_MODE;
import java.io.Serializable;

/**
 * 
 * @author vhaiswbeckec
 *
 */
public class DataGenerationConfiguration
extends ObservableMap<String, Object>
implements Serializable
{
	private static final String EXCEPTION_MODE_PROP = "EXCEPTION_MODE";
	private static final String DELAY_MODE_PROP = "DELAY_MODE";
	private static final String MODE_PROP = "MODE";
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Default configuration specifies randomized data generation with
	 * no IO delays or exceptions.
	 */
	public DataGenerationConfiguration()
	{
		this(Mode.RANDOMIZE, EXCEPTION_MODE.RELIABLE, DELAY_MODE.NONE);
	}
	
	/**
	 * @param mode
	 * @param delayMode
	 * @param exceptionMode
	 */
	public DataGenerationConfiguration(Mode mode, EXCEPTION_MODE exceptionMode, DELAY_MODE delayMode)
	{
		super();
		setMode(mode);
		setDelayMode(delayMode);
		setExceptionMode(exceptionMode);
	}
	
	protected GenericDataGenerator.Mode getMode()
	{
		return (GenericDataGenerator.Mode)this.get(MODE_PROP);
	}
	protected NetworkSimulatingInputStream.DELAY_MODE getDelayMode()
	{
		return (NetworkSimulatingInputStream.DELAY_MODE)this.get(DELAY_MODE_PROP);
	}
	protected NetworkSimulatingInputStream.EXCEPTION_MODE getExceptionMode()
	{
		return (NetworkSimulatingInputStream.EXCEPTION_MODE)this.get(EXCEPTION_MODE_PROP);
	}
	protected void setMode(GenericDataGenerator.Mode mode)
	{
		this.put(MODE_PROP, mode);
	}
	protected void setDelayMode(NetworkSimulatingInputStream.DELAY_MODE delayMode)
	{
		this.put(DELAY_MODE_PROP, delayMode);
	}
	protected void setExceptionMode(NetworkSimulatingInputStream.EXCEPTION_MODE exceptionMode)
	{
		this.put(EXCEPTION_MODE_PROP, exceptionMode);
	}
}