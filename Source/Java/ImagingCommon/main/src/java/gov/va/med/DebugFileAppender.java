/**
 * 
 */
package gov.va.med;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;

/**
 * A cheap little appender that is convenient for debugging.
 * Each time it is started it will create a new file based on the 
 * time.  Other than that, it is just a derivation of FileAppender.
 * This appender makes no attempt to clean up old files so its use in
 * a production system is strongly discouraged.
 * 
 * @author vhaiswbeckec
 *
 */
public class DebugFileAppender
extends FileAppender
implements org.apache.log4j.Appender
{
	private static String fileExtension = ".log";
	private static String dateFormatSpecification = "ddMMyyyy-hhmm"; 
	private static DateFormat df = new SimpleDateFormat(dateFormatSpecification);
	
	private static String generateFilename(String filenameRoot)
	{
		return filenameRoot == null ? "appender" : filenameRoot + df.format(new Date()) + fileExtension;
	}
	
	/**
	 * 
	 */
	public DebugFileAppender()
	{
	}

	/**
	 * @param layout
	 * @param filename
	 * @throws IOException
	 */
	public DebugFileAppender(Layout layout, String filenameRoot) 
	throws IOException
	{
		super(layout, generateFilename(filenameRoot));
	}

	/**
	 * @param layout
	 * @param filename
	 * @param append
	 * @throws IOException
	 */
	public DebugFileAppender(Layout layout, String filenameRoot, boolean append)
			throws IOException
	{
		super(layout, generateFilename(filenameRoot), append);
	}

	/**
	 * @param layout
	 * @param filename
	 * @param append
	 * @param bufferedIO
	 * @param bufferSize
	 * @throws IOException
	 */
	public DebugFileAppender(Layout layout, String filenameRoot, boolean append,
			boolean bufferedIO, int bufferSize) throws IOException
	{
		super(layout, generateFilename(filenameRoot), append, bufferedIO, bufferSize);
	}

	public void setFilenameRoot(String filenameRoot)
	{
		setFile(generateFilename(filenameRoot));
	}
}
