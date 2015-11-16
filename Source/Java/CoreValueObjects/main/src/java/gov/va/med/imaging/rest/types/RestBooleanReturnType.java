/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 29, 2010
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
package gov.va.med.imaging.rest.types;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Primitive types cannot be returned as results using REST, so this is a wrapper for a boolean type
 * It is in CoreValueObjects because it is common to many projects
 * 
 * @author vhaiswwerfej
 *
 */
@XmlRootElement
public class RestBooleanReturnType
{
	private boolean result;
	
	public RestBooleanReturnType()
	{
		this.result = true;
	}
	
	public RestBooleanReturnType(boolean result)
	{
		this.result = result;
	}

	public boolean isResult()
	{
		return result;
	}

	public void setResult(boolean result)
	{
		this.result = result;
	}

	@Override
	public String toString()
	{
		return result + "";
	}

}
