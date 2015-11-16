/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Aug 3, 2012
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
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
 * If you want to make a POST call with Jersey but not to actually post any data, Jersey gets confused if that one value is null.
 * 
 * The failure mode is the xxx headers are not passed to the facade (very strange)
 * 
 * This object is a holder that can be used but doesn't actually hold any value or meaning.  The receiving side doesn't need to use 
 * this type at all.
 * 
 * 
 * @author VHAISWWERFEJ
 *
 */
@XmlRootElement
public class RestNullPostType
{	

}
