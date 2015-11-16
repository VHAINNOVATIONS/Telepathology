/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date Sep 29, 2010
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

import gov.va.med.imaging.exceptions.URNFormatException;

/**
 * @author vhaiswbeckec
 *
 */
public class ImageURNFactory
extends URNFactory
{
	private static final Class<?>[] IMAGE_CREATE_METHOD_PARAMETERS = 
		new Class<?>[]{String.class, String.class, String.class, String.class};
	private static final Class<?>[] IMAGE_CREATE_WITH_MODALITY_METHOD_PARAMETERS = 
		new Class<?>[]{String.class, String.class, String.class, String.class, String.class};
	
	/**
	 * This is a "special" create method that (currently) works with ImageURN and its derivatives only.
	 * 
	 * @param <T>
	 * @param originatingSiteId
	 * @param assignedId
	 * @param studyId
	 * @param patientIcn
	 * @param imageModality
	 * @return
	 * @throws URNFormatException
	 */
	@SuppressWarnings("unchecked")
	public static <T extends URN> T create(
		String originatingSiteId, 
		String assignedId, 
		String studyId,
		String patientId, 
		String imageModality, 
		Class<T> expectedClass) 
	throws URNFormatException
	{
		String msgIdentifier = 
			"site ID[" + originatingSiteId + "], assignedId [" + assignedId + 
			"], studyId [" + studyId + "], patientId [" + patientId + "], imageModality [" + imageModality + "]"; 
		
		if(! isRegisteredUrnClass(expectedClass) )
			throw new URNFormatException("Unable to create URN from " + msgIdentifier + 
				" because the expected class '" + expectedClass.getName() + " is not registered with the URNFactory");
		
		if(imageModality == null)
		{
			String[] vaUrnComponents = new String[]{originatingSiteId, assignedId, studyId, patientId};
			return (T)create(expectedClass, URNFactory.FACTORY_METHOD_NAME, IMAGE_CREATE_METHOD_PARAMETERS, vaUrnComponents);
		}
		else
		{
			String[] vaUrnComponents = new String[]{originatingSiteId, assignedId, studyId, patientId, imageModality};
			return (T)create(expectedClass, URNFactory.FACTORY_METHOD_NAME, IMAGE_CREATE_WITH_MODALITY_METHOD_PARAMETERS, vaUrnComponents);
			
		}
	}
}
