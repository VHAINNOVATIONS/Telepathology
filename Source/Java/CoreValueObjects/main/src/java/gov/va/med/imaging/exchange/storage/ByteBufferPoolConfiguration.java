/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 4, 2010
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import gov.va.med.imaging.facade.configuration.AbstractBaseFacadeConfiguration;
import gov.va.med.imaging.facade.configuration.FacadeConfigurationFactory;
import gov.va.med.imaging.facade.configuration.exceptions.CannotLoadConfigurationException;

/**
 * Configuration for all byte buffer pool objects.
 * 
 * @author vhaiswwerfej
 *
 */
public class ByteBufferPoolConfiguration
extends AbstractBaseFacadeConfiguration
{
	private List<BufferConfiguration> buffers = null;
	
	private final static Logger logger = Logger.getLogger(ByteBufferPoolConfiguration.class);

	public ByteBufferPoolConfiguration()
	{
		super();
	}
	
	public static synchronized ByteBufferPoolConfiguration getByteBufferPoolConfiguration()
	{
		try
		{
			return FacadeConfigurationFactory.getConfigurationFactory().getConfiguration(
					ByteBufferPoolConfiguration.class);			
		}
		catch(CannotLoadConfigurationException clcX)
		{
			return null;
		}
	}
	
	/**
	 * Special case for a mini VIX where the only major difference is the default number of buffers created
	 * is more tuned for a mini VIX
	 * @return
	 */
	private static ByteBufferPoolConfiguration createMiniVixConfiguration()
	{
		ByteBufferPoolConfiguration config = new ByteBufferPoolConfiguration();
		List<BufferConfiguration> buffers = new ArrayList<BufferConfiguration>();
		// JMW 3/8/2010 - set default VIX pool configuration to 0 buffers to prevent buffer pool image corruption issue
		buffers.add(new BufferConfiguration("50k buffers", 51200, 0, 0, 100));
		buffers.add(new BufferConfiguration("100k buffers", 102400, 0, 0, 100));
		buffers.add(new BufferConfiguration("500k buffers", 512000, 0, 0, 100));
		buffers.add(new BufferConfiguration("1M buffers", 1048576, 0, 0, 100));
		buffers.add(new BufferConfiguration("2M buffers", 2097152, 0, 0, 100));
		buffers.add(new BufferConfiguration("5M buffers", 5242880, 0, 0, 100));
		buffers.add(new BufferConfiguration("10M buffers", 10485760, 0, 0, 100));
		buffers.add(new BufferConfiguration("12M buffers", 12582912, 0, 0, 100));
		buffers.add(new BufferConfiguration("18M buffers", 18874368, 0, 0, 100));
		buffers.add(new BufferConfiguration("25M buffers", 26214400, 0, 0, 100));
		buffers.add(new BufferConfiguration("30M buffers", 31457280, 0, 0, 100));
		config.setBuffers(buffers);
		
		return config;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.facade.configuration.AbstractBaseFacadeConfiguration#loadDefaultConfiguration()
	 */
	@Override
	public AbstractBaseFacadeConfiguration loadDefaultConfiguration()
	{
		buffers = new ArrayList<BufferConfiguration>();
		// JMW 3/8/2010 - set default VIX pool configuration to 0 buffers to prevent buffer pool image corruption issue
		buffers.add(new BufferConfiguration("50k buffers", 51200, 0, 0, 100));
		buffers.add(new BufferConfiguration("100k buffers", 102400, 0, 0, 100));
		buffers.add(new BufferConfiguration("500k buffers", 512000, 0, 0, 100));
		buffers.add(new BufferConfiguration("1M buffers", 1048576, 0, 0, 100));
		buffers.add(new BufferConfiguration("2M buffers", 2097152, 0, 0, 100));
		buffers.add(new BufferConfiguration("5M buffers", 5242880, 0, 0, 100));
		buffers.add(new BufferConfiguration("10M buffers", 10485760, 0, 0, 100));
		buffers.add(new BufferConfiguration("12M buffers", 12582912, 0, 0, 100));
		buffers.add(new BufferConfiguration("18M buffers", 18874368, 0, 0, 100));
		buffers.add(new BufferConfiguration("25M buffers", 26214400, 0, 0, 100));
		buffers.add(new BufferConfiguration("30M buffers", 31457280, 0, 0, 100));
		return this;
	}

	/**
	 * @return the buffers
	 */
	public List<BufferConfiguration> getBuffers()
	{
		return buffers;
	}

	/**
	 * @param buffers the buffers to set
	 */
	public void setBuffers(List<BufferConfiguration> buffers)
	{
		this.buffers = buffers;
	}
	
	public static void main(String [] args)
	{
		ByteBufferPoolConfiguration config = null;
		if(args.length == 1)
		{
			if("-m".equalsIgnoreCase(args[0]))
			{
				logger.info("Creating configuration for mini VIX");
				config = ByteBufferPoolConfiguration.createMiniVixConfiguration();
			}
			else
			{
				logger.info("Loading/Creating default configuration");
				config = getByteBufferPoolConfiguration();
			}
		}
		else
		{
			logger.info("Loading/Creating default configuration");
			config = getByteBufferPoolConfiguration();
		}
		config.storeConfiguration();
	}

}
