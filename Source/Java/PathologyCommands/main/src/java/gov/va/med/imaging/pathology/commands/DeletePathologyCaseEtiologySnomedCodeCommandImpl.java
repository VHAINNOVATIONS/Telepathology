/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Aug 22, 2012
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
package gov.va.med.imaging.pathology.commands;

import gov.va.med.imaging.pathology.PathologyCaseURN;

/**
 * @author VHAISWWERFEJ
 *
 */
public class DeletePathologyCaseEtiologySnomedCodeCommandImpl
extends AbstractDeletePathologyCaseSnomedCodeCommandImpl
{
	private static final long serialVersionUID = 7741985848607938100L;

	/**
	 * @param pathologyCaseUrn
	 * @param tissueId
	 * @param snomedId
	 * @param etiologyId
	 */
	public DeletePathologyCaseEtiologySnomedCodeCommandImpl(
			PathologyCaseURN pathologyCaseUrn, String tissueId,
			String snomedId, String etiologyId)
	{
		super(pathologyCaseUrn, tissueId, snomedId, etiologyId);
	}

}
