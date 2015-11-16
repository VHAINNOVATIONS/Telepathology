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
import java.util.Date;

/**
 * Represents a Java log file stored on the VIX
 * 
 * @author vhaiswwerfej
 *
 */
public class JavaLogFile 
implements Comparable<JavaLogFile>
{
	private final String filename;
	private final Date dateModified;
	private final long fileSize;
	
	public JavaLogFile(String filename, long lastModified, long fileSize) 
	{
		super();
		this.filename = filename;
		this.dateModified = new Date(lastModified);
		this.fileSize = fileSize;
	}
	
	public JavaLogFile(File file)
	{
		this(file.getName(), file.lastModified(), file.length());
	}
	
	public JavaLogFile(File file, File rootDirectory)
	{
		String rootPath = rootDirectory.getPath();
		if (rootPath != null && file.getPath().indexOf(rootPath) == 0)
		{
			this.filename = file.getPath().substring(rootPath.length() + 1);
		}
		else
		{
			this.filename = file.getName();
		}
		this.dateModified = new Date(file.lastModified());
		this.fileSize = file.length();
	}

	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @return the dateModified
	 */
	public Date getDateModified() {
		return dateModified;
	}

	/**
	 * @return the fileSize
	 */
	public long getFileSize() {
		return fileSize;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() 
	{
		return filename;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(JavaLogFile that) 
	{
		return this.filename.compareToIgnoreCase(that.filename);
	}	
}
