package gov.va.med.imaging.dicom.common;

import org.springframework.context.ApplicationContext;

/**
 * The Spring context is initialized in the DicomEngineAdapter when the server starts up.
 * However, the application context needs to be available throughout the DICOM code. Adding a
 * reference here allows it to be accessed from any project.
 * 
 * @author vhaiswlouthj
 *
 */
public class SpringContext {
	private static ApplicationContext context;
	
	public static ApplicationContext getContext()
	{
		return context;
	}
	public static void setContext(ApplicationContext context)
	{
		SpringContext.context = context;
	}

}
