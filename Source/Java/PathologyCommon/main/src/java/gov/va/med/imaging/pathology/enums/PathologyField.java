/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 6, 2012
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
package gov.va.med.imaging.pathology.enums;

/**
 * Defines the fields that can be retrieved from the pathology package
 * 
 * @author VHAISWWERFEJ
 *
 */
public enum PathologyField
{
	/** 
	 * Comes from file #61 TOPOGRAPHY FIELD
	 */
	topography,
	/** 
	 * Comes from file #61.1 MORPHOLOGY
	 */
	morphology,
	/**
	 * Comes from #61.2 ETIOLOGY
	 */
	etiology,
	/**
	 * Comes from #61.3 FUNCTION
	 */
	function,
	/**
	 * Comes from #61.4 DISEASE
	 */
	disease,
	/**
	 * Comes from #61.5 PROCEDURE
	 */
	procedure,
	/**
	 * Comes from #200 NEW PERSON
	 */
	users,
	/**
	 * Comes from #44 LOCATION
	 */
	location,
	/**
	 * Comes from #64 WKLD CODE
	 */
	workload,
	/**
	 * Comes from #81 CPT
	 */
	cpt

}
