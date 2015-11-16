/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Dec 16, 2009
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
package gov.va.med.imaging.javalogs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Tools to read the Java Log files on the VIX
 * 
 * @author vhaiswwerfej
 *
 */
public class JavaLogReader 
{
	private final static Logger logger = Logger.getLogger(JavaLogReader.class);
	
	private final static String catalina_home_env = "catalina_home";
	
	/**
	 * Get the list of log files in the directory.
	 * @return
	 */
	public static List<JavaLogFile> getJavaLogList()
	{	
		List<JavaLogFile> files = new ArrayList<JavaLogFile>();
		File logsDir = getLogsDir();
		logger.info("Logs Dir: " + logsDir.getAbsolutePath());
		try{
			List<File> fileList = getFileListing(logsDir);
			for(File file : fileList)
			{
				if(file != null){
					files.add(new JavaLogFile(file, logsDir));
				}
			}
		}
		catch (FileNotFoundException fnfe)
		{
			logger.error("Error reading log directory", fnfe);
		}
		Collections.sort(files);
		return files;
	}
	
	/**
	 * Return a stream to the specified log.
	 * 
	 * @param filename The name of the file requested (not the full path).
	 * @return InputStream to the log file.
	 * @throws FileNotFoundException Thrown if the file specified is not found
	 */
	public static InputStream getJavaLogFile(String filename)
	throws FileNotFoundException
	{
		File logsDir = getLogsDir();
		if(logsDir != null)
		{
			String fName = logsDir.getAbsolutePath() + File.separatorChar + filename;
			FileInputStream fis = new FileInputStream(fName);
			return fis;
		}
		return null;
	}
	
	private static File logsDir = null;	
	public static File getLogsDir()
	{
		if(logsDir == null)
		{
			String tomcatDir = System.getenv(catalina_home_env);
			if(tomcatDir == null)
			{
				logger.warn("Did not find '" + catalina_home_env + "' environment variable.");
				return null;
			}
			logsDir = new File(tomcatDir + File.separatorChar + "logs");
			logger.info("Found logs directory '" + logsDir.getAbsolutePath() + "'.");
		}
		return logsDir;
	}
	
	/**
	* Recursively walk a directory tree and return a List of all
	* Files found; the List is sorted using File.compareTo().
	*
	* @param aStartingDir is a valid directory, which can be read.
	*/
	private static List<File> getFileListing(File aStartingDir)
    throws FileNotFoundException
    {
	    validateDirectory(aStartingDir);
	    List<File> result = getFileListingNoSort(aStartingDir);
	    Collections.sort(result);
	    return result;
	}

	  
	private static List<File> getFileListingNoSort(File aStartingDir)
	throws FileNotFoundException
	{
		List<File> result = new ArrayList<File>();
		File[] filesAndDirs = aStartingDir.listFiles();
		List<File> filesDirs = Arrays.asList(filesAndDirs);
		for(File file : filesDirs)
		{
			if (file.isFile())
			{
				result.add(file);
			}
			else
			{
				//must be a directory
				//recursive call!
				List<File> deeperList = getFileListingNoSort(file);
				result.addAll(deeperList);
			}
		}
	    return result;
	}
	  
	/**
	 * Directory is valid if it exists, does not represent a file, and can be read.
	 */
	private static void validateDirectory ( File aDirectory)
	throws FileNotFoundException
	{
		if (aDirectory == null)
		{
			throw new IllegalArgumentException("Directory should not be null.");
		}
		if (!aDirectory.exists())
		{
			throw new FileNotFoundException("Directory does not exist: " + aDirectory);
		}
		if (!aDirectory.isDirectory())
		{
			throw new IllegalArgumentException("Is not a directory: " + aDirectory);
		}
		if (!aDirectory.canRead())
		{
			throw new IllegalArgumentException("Directory cannot be read: " + aDirectory);
		}
	}
}
