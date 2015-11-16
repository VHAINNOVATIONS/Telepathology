/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Dec 01, 2007
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWLOUTHJ
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
package gov.va.med.imaging.exchange.business.storage;

import gov.va.med.RoutingToken;
import gov.va.med.RoutingTokenImpl;
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.facade.configuration.AbstractBaseFacadeConfiguration;

import java.io.File;

import org.apache.log4j.Logger;

public class StorageServerConfiguration 
extends AbstractBaseFacadeConfiguration 
{
	private static StorageServerConfiguration storageServerConfiguration = null;
	private static final Logger logger = Logger.getLogger(StorageServerConfiguration.class);

    private String iconImageFolder;
	
	
	/**
	 * @return the iconImageFolder
	 */
	public String getIconImageFolder() {
		return iconImageFolder;
	}

	/**
	 * @param iconImageFolder the iconImageFolder to set
	 */
	public void setIconImageFolder(String iconImageFolder) {
		this.iconImageFolder = iconImageFolder;
	}

	@Override
	public AbstractBaseFacadeConfiguration loadDefaultConfiguration()
	{
		return this;
	}
	
	public static synchronized StorageServerConfiguration getConfiguration()
	{
		if(storageServerConfiguration == null)
		{
			StorageServerConfiguration config = new StorageServerConfiguration();
			storageServerConfiguration = (StorageServerConfiguration)config.loadConfiguration();			
		}
		return storageServerConfiguration;
	}


    public static void main(String[] args) {

        StorageServerConfiguration config = getConfiguration();

        String path= System.getenv("vixcache");			// <x:/vixcache>
        if (path.length() < 4){
        	path = "c:/temp/";
        }
        if (!(path.endsWith("/") || path.endsWith("\\"))){
        	path += "/";
        }
        config.setIconImageFolder(path + "IconImage");
		checkAndMakeDirs(config.getIconImageFolder());

        // Store the configuration
        config.storeConfiguration();
    }

    private static void printUsage()
    {
        System.out.println("This program requires no arguments.");
    }

    private static void checkAndMakeDirs(String folderPath)
    {
		File folder = new File(folderPath);
		if (!folder.exists())
		{
			folder.mkdirs();
		}   	
    }
}
