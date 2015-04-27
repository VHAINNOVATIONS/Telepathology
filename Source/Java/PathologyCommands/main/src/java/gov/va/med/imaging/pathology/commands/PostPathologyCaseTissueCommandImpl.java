/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 31, 2012
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

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.pathology.PathologyCaseURN;
import gov.va.med.imaging.pathology.PathologyFieldURN;
import gov.va.med.imaging.pathology.datasource.PathologyDataSourceSpi;

/**
 * Add tissues to a case
 * 
 * @author VHAISWWERFEJ
 *
 */
public class PostPathologyCaseTissueCommandImpl
extends AbstractPathologyDataSourceCommandImpl<String>
{

	private static final long serialVersionUID = 5756992315361913L;
	
	private final PathologyCaseURN pathologyCaseUrn;
	private final PathologyFieldURN tissueFieldUrn;
	
	public PostPathologyCaseTissueCommandImpl(PathologyCaseURN pathologyCaseUrn, 
			PathologyFieldURN tissueFieldUrn)
	{
		this.pathologyCaseUrn = pathologyCaseUrn;
		this.tissueFieldUrn = tissueFieldUrn;
	}

	public PathologyCaseURN getPathologyCaseUrn()
	{
		return pathologyCaseUrn;
	}

	public PathologyFieldURN getTissueFieldUrn()
	{
		return tissueFieldUrn;
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
		return "saveCaseTissue";
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodParameterTypes()
	 */
	@Override
	protected Class<?>[] getSpiMethodParameterTypes()
	{
		return new Class<?>[] {PathologyCaseURN.class, PathologyFieldURN.class};
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodParameters()
	 */
	@Override
	protected Object[] getSpiMethodParameters()
	{
		return new Object[] {getPathologyCaseUrn(), getTissueFieldUrn()};
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getCommandResult(gov.va.med.imaging.datasource.VersionableDataSourceSpi)
	 */
	@Override
	protected String getCommandResult(PathologyDataSourceSpi spi)
	throws ConnectionException, MethodException
	{
		return spi.saveCaseTissue(getPathologyCaseUrn(), getTissueFieldUrn());
	}

}
