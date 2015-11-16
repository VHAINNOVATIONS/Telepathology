/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: Mar 11, 2008
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
package gov.va.med.imaging.core.interfaces.exceptions;

/**
 * A TransientCondition may be implemented if the condition that caused
 * an Exception to be thrown could be resolved after a delay.  The 
 * instigating example is a NearLineException, which is a MethodExcption
 * that may be resolved by waiting for a platter to be manually mounted.
 * The TransientCondition should only be implemented if the re-issued 
 * request has a reasonable chance of success.  Re-issuing a request in
 * the hope that a Vix will be installed by then is not reasonable. 
 * 
 * @author VHAISWBECKEC
 *
 */
public interface TransientCondition
{
	/**
	 * Return the suggested delay, in milliseconds, before re-issuing the request.
	 * Return a value less than zero if the suggested delay is undefined.
	 * A time of zero means that the request can be re-issued immediately.
	 */
	public abstract long getSuggestedDelay();
}
