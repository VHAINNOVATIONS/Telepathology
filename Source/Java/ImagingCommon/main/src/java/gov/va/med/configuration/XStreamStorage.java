/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Aug 4, 2010
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

package gov.va.med.configuration;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import javax.naming.CommunicationException;
import javax.naming.NamingException;
import org.apache.log4j.Logger;
import com.thoughtworks.xstream.XStream;

/**
 * @author vhaiswbeckec
 *
 */
public class XStreamStorage
implements ConfigurationStorage
{
	Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * 
	 * @see gov.va.med.configuration.ConfigurationStorage#load(java.net.URL)
	 */
	@SuppressWarnings("null")
	@Override
	public ConfigurationContext load(URL location) 
	throws NamingException
	{
		if(location == null)
			throw new CommunicationException("Unable to load from a null location.");
		
		InputStream inStream = null;
		
		try
		{
			logger.info("Loading configuration from '" + location + "'.");
			inStream = location.openStream();
			assert inStream != null;
			
			logger.info("Loading configuration using XStream");
			XStream xstream = new XStream();
			ConfigurationContext config = (ConfigurationContext)xstream.fromXML(inStream);
			logger.info("Configuration loaded using XStream and is " + (config == null ? "NULL" : "NOT NULL") );
			
			return config;
		}
		catch (IOException ioX)
		{
			throw new NamingException("IOException loading from '" + location.toExternalForm() + "'.");
		}
		finally
		{
			try{inStream.close();}
			catch(Throwable t){}
		}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.configuration.ConfigurationStorage#store(java.net.URL, gov.va.med.configuration.ConfigurationContext)
	 */
	@SuppressWarnings("null")
	@Override
	public void store(URL location, ConfigurationContext context)
	throws NamingException
	{
		if(context == null)
			throw new CommunicationException("Unable to store a null ConfigurationContext.");
		if(location == null)
			throw new CommunicationException("Unable to store a null location.");

		String filename = filenameFromURL(location);
		FileOutputStream outStream = null;
		try
		{
			File configFile = new File(filename);
			configFile.createNewFile();		// create the file if it does not exist
			
			outStream = new FileOutputStream(configFile);
			logger.info("Saving configuration '" + location.toExternalForm() + "' using XStream.");
			XStream xstream = new XStream();
			xstream.toXML(context, outStream);
			logger.info("Configuration saved using XStream.");
		}
		catch (IOException x)
		{
			throw new CommunicationException("Unable to write configuration to '" + filename + "'.");
		}
		finally
		{
			try{outStream.close();}catch(Throwable t){}
		}
	}

	private static final String FILE_PROTOCOL = "file:";
	@SuppressWarnings("deprecation")
	private String filenameFromURL(URL location)
	{
		String filename = URLDecoder.decode( location.toExternalForm() );
		if(filename.startsWith(FILE_PROTOCOL))
			filename = filename.substring(FILE_PROTOCOL.length());
		
		return filename;
	}
}
