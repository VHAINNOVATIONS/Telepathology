/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 19, 2012
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

import java.util.List;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.pathology.PathologyCaseURN;
import gov.va.med.imaging.pathology.PathologyCptCodeResult;
import gov.va.med.imaging.pathology.PathologyFieldURN;
import gov.va.med.imaging.pathology.datasource.PathologyDataSourceSpi;

/**
 * Add CPT codes to a case
 * 
 * @author VHAISWWERFEJ
 *
 */
public class PostPathologyCaseCptCodesCommandImpl
extends AbstractPathologyDataSourceCommandImpl<List<PathologyCptCodeResult>>
{
	private static final long serialVersionUID = -2036895722248347992L;
	
	private final PathologyCaseURN pathologyCaseUrn;
	private final PathologyFieldURN locationFieldUrn;
	private final List<String> cptCodes;
	
	public PostPathologyCaseCptCodesCommandImpl(PathologyCaseURN pathologyCaseUrn, 
			PathologyFieldURN locationFieldUrn, List<String> cptCodes)
	{
		super();
		this.pathologyCaseUrn = pathologyCaseUrn;
		this.locationFieldUrn = locationFieldUrn;
		this.cptCodes = cptCodes;
	}

	public PathologyCaseURN getPathologyCaseUrn()
	{
		return pathologyCaseUrn;
	}

	public PathologyFieldURN getLocationFieldUrn()
	{
		return locationFieldUrn;
	}

	public List<String> getCptCodes()
	{
		return cptCodes;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getRoutingToken()
	 */
	@Override
	public RoutingToken getRoutingToken()
	{
		return getPathologyCaseUrn();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodName()
	 */
	@Override
	protected String getSpiMethodName()
	{
		return "saveCaseCptCodes";
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodParameterTypes()
	 */
	@Override
	protected Class<?>[] getSpiMethodParameterTypes()
	{
		return new Class<?>[] {PathologyCaseURN.class, PathologyFieldURN.class, List.class};
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodParameters()
	 */
	@Override
	protected Object[] getSpiMethodParameters()
	{
		return new Object[] {getPathologyCaseUrn(), getLocationFieldUrn(), getCptCodes()};
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getCommandResult(gov.va.med.imaging.datasource.VersionableDataSourceSpi)
	 */
	@Override
	protected List<PathologyCptCodeResult> getCommandResult(PathologyDataSourceSpi spi)
	throws ConnectionException, MethodException
	{
		return spi.saveCaseCptCodes(getPathologyCaseUrn(), getLocationFieldUrn(), getCptCodes());
	}

}
