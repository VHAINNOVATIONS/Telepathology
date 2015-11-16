/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date May 7, 2010
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

package gov.va.med.imaging.exchange.translation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author vhaiswbeckec
 *
 */
@Retention(RetentionPolicy.RUNTIME) 
public @interface TranslationMethod
{
	/**
	 * By default, every translate() method must have another translate() method
	 * that is is reflection (i.e. int translate(String) <-> String translate(int))
	 * This allows for automated testing of the translator class.
	 * When a translate() method must have more than one arg or the reflective method
	 * does not exist then the method must be marked as an unmatchedMethod(), else a unit
	 * test will fail.
	 * 
	 * @return
	 */
	public boolean unmatchedMethod() default false;
	
}
