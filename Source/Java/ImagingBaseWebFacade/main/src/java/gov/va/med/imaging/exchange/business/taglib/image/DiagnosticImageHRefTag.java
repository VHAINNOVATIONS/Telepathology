/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Feb 4, 2008
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWBECKEC
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
package gov.va.med.imaging.exchange.business.taglib.image;


/**
 * This tag will generate a String that can be used as an href to an image.
 * It needs, by default, only the application path (servlet mapping ) of the WAI servlet.
 * The image specification is determined by the surrounding AbstractImageTag.
 * The quality and accept type may be set using tag properties, or if left blank
 * will be defaulted.
 * 
 * @author VHAISWBECKEC
 */
public class DiagnosticImageHRefTag 
extends AbstractImageHRefTag
{
	private static final long serialVersionUID = 1L;
	
	protected String getDefaultPathInfoPattern()
	{
		return "diagnostic/" + imageUrnParameterKey;
	}
}
