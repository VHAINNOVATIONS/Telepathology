/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 26, 2012
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
package gov.va.med.imaging.federation.pathology.ids;

import java.util.ArrayList;
import java.util.List;

import gov.va.med.imaging.versions.IDSOperationProvider;
import gov.va.med.imaging.versions.ImagingOperation;

/**
 * @author vhaiswwerfej
 *
 */
public class PathologyFederationIDSOperationProvider
extends IDSOperationProvider
{

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.versions.IDSOperationProvider#getApplicationType()
	 */
	@Override
	public String getApplicationType()
	{
		return "Federation";
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.versions.IDSOperationProvider#getVersion()
	 */
	@Override
	public String getVersion()
	{
		return "6";
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.versions.IDSOperationProvider#getImagingOperations()
	 */
	@Override
	public List<ImagingOperation> getImagingOperations()
	{
		List<ImagingOperation> operations = new ArrayList<ImagingOperation>();
		operations.add(new ImagingOperation("Pathology", "/restservices/V6/"));
		return operations;
	}

}
