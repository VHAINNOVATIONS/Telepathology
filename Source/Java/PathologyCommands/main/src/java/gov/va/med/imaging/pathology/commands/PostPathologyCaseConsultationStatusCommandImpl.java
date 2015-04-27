/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 9, 2012
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
import gov.va.med.imaging.pathology.PathologyCaseConsultationURN;
import gov.va.med.imaging.pathology.datasource.PathologyDataSourceSpi;
import gov.va.med.imaging.pathology.enums.PathologyCaseConsultationUpdateStatus;

/**
 * Update a case consultation status
 * 
 * @author VHAISWWERFEJ
 *
 */
public class PostPathologyCaseConsultationStatusCommandImpl
extends AbstractPathologyDataSourceCommandImpl<java.lang.Void>
{
	private static final long serialVersionUID = 6464797794027874922L;
	
	private final PathologyCaseConsultationURN pathologyCaseConsultationUrn;
	private final PathologyCaseConsultationUpdateStatus pathologyCaseConsultationUpdateStatus;
	
	public PostPathologyCaseConsultationStatusCommandImpl(PathologyCaseConsultationURN pathologyCaseConsultationUrn,
			PathologyCaseConsultationUpdateStatus pathologyCaseConsultationUpdateStatus)
	{
		super();
		this.pathologyCaseConsultationUpdateStatus = pathologyCaseConsultationUpdateStatus;
		this.pathologyCaseConsultationUrn = pathologyCaseConsultationUrn;
		
	}

	public PathologyCaseConsultationURN getPathologyCaseConsultationUrn()
	{
		return pathologyCaseConsultationUrn;
	}

	public PathologyCaseConsultationUpdateStatus getPathologyCaseConsultationUpdateStatus()
	{
		return pathologyCaseConsultationUpdateStatus;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getRoutingToken()
	 */
	@Override
	public RoutingToken getRoutingToken()
	{
		return getPathologyCaseConsultationUrn();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodName()
	 */
	@Override
	protected String getSpiMethodName()
	{
		return "updateConsultationStatus";
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodParameterTypes()
	 */
	@Override
	protected Class<?>[] getSpiMethodParameterTypes()
	{
		return new Class<?>[] {PathologyCaseConsultationURN.class, PathologyCaseConsultationUpdateStatus.class};
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getSpiMethodParameters()
	 */
	@Override
	protected Object[] getSpiMethodParameters()
	{
		return new Object[] {getPathologyCaseConsultationUrn(), getPathologyCaseConsultationUpdateStatus()};
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl#getCommandResult(gov.va.med.imaging.datasource.VersionableDataSourceSpi)
	 */
	@Override
	protected java.lang.Void getCommandResult(PathologyDataSourceSpi spi)
	throws ConnectionException, MethodException
	{
		spi.updateConsultationStatus(getPathologyCaseConsultationUrn(), getPathologyCaseConsultationUpdateStatus());
		return (java.lang.Void)null;
	}

}
