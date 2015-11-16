package gov.va.med.imaging.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtilities 
{
	public static String convertExceptionToString(Exception e)
	{
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String stacktrace = sw.toString();
		
		String formattedException = "Exception Type: " + e.getClass().getName() + "\n";
		formattedException += "Details Message: " + e.getMessage() + "\n";
		formattedException += "Stack Trace:\n";
		formattedException += stacktrace;
		
		return formattedException;
	}
}
