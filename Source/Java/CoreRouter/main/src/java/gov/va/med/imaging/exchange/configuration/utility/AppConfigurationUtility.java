/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 12, 2007
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWBUCKD
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
package gov.va.med.imaging.exchange.configuration.utility;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import gov.va.med.imaging.core.interfaces.exceptions.ApplicationConfigurationException;
import gov.va.med.imaging.exchange.configuration.AppConfiguration;

/**
 * @author VHAISWBUCKD
 * Command line support for initializing and persisting an AppConfiguration object.
 * Used by the ViX Installer.
 */
public class AppConfigurationUtility
{
	// define the command line
	private static final String LOCAL_SITE_NUMBER = "LocalSiteNumber";
	private static final String SMTP_SERVER_URI = "SmtpServerUri";
	private static final String VIX_SOFTWARE_VERSION = "VixSoftwareVersion";
	private static final Logger logger = Logger.getLogger(AppConfigurationUtility.class);

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		HashMap<String, String> configParameters = parseCommandLine(args);
		boolean paramError = false;
		String localSiteNumber = null;
		String smtpServerUri = null;
		String vixSoftwareVersion = null;
		
		localSiteNumber = configParameters.get(LOCAL_SITE_NUMBER);
		if (localSiteNumber == null)
		{
			System.err.println("Missing required command line parameter : " + LOCAL_SITE_NUMBER);
			paramError = true;
		}
		smtpServerUri = configParameters.get(SMTP_SERVER_URI);
		if (smtpServerUri == null)
		{
			System.err.println("Missing required command line parameter : " + SMTP_SERVER_URI);
			paramError = true;
		}
		vixSoftwareVersion = configParameters.get(VIX_SOFTWARE_VERSION);
		if (vixSoftwareVersion == null)
		{
			System.err.println("Missing required command line parameter : " + VIX_SOFTWARE_VERSION);
			paramError = true;
		}
		
		// exit if we are missing any required parameters
		if (paramError)
		{
			System.exit(-1);
		}
		
		AppConfiguration appConfig = new AppConfiguration(); 
		try
		{
			appConfig.init();
			appConfig.setLocalSiteNumber(localSiteNumber);
			appConfig.setSmtpServerUri(new URI(smtpServerUri));
			appConfig.setVixSoftwareVersion(vixSoftwareVersion);
			appConfig.setCachingEnabled(true); // caching enabled by default
			if (!appConfig.saveAppConfigurationToFile())
			{
				System.err.println("Error: could not write the configuration file.");
				System.exit(-1);
			}
			logger.info("ViX configuration file written to: " + appConfig.getAppConfigurationFilespec());
		}
		catch (ApplicationConfigurationException ex)
		{
			System.err.println(ex.getMessage());
			System.exit(-1);
		}
		catch (URISyntaxException ex)
		{
			System.err.println(ex.getMessage());
			System.exit(-1);
		} 

		System.exit(0);
	}

	private static HashMap<String, String> parseCommandLine(String[] args)
	{
		HashMap<String, String> configParameters = new HashMap<String, String>();
		
		for (int i=0 ; i < args.length ; i++)
		{
			String[] nameValue = args[i].split("=");
			if (nameValue.length != 2)
			{
				System.err.println("Command line parameter is not in the form key=value: " + args[i]);
				System.exit(-1);
			}
			configParameters.put(nameValue[0], nameValue[1]);
		}

		return configParameters;
	}

}
