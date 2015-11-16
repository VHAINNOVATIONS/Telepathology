/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Oct 15, 2013
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWTITTOC
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
import gov.va.med.exceptions.RoutingTokenFormatException;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.exchange.RoutingTokenHelper;
import gov.va.med.imaging.pathology.PathologyCaseURN;
import gov.va.med.imaging.pathology.PathologyPatientInfoItem;
import gov.va.med.imaging.pathology.commands.facade.PathologyDataSourceContext;

/**
 * @author VHAISWTITTOC
 *
 */
public class PostPathologyTransferPatientCommandImpl
extends AbstractCommandImpl<String>
{
	private static final long serialVersionUID = 5402880145101437825L;
	
	private final PathologyCaseURN pathologyCaseUrn;
	private final String targetSiteId;
	
	public PostPathologyTransferPatientCommandImpl(PathologyCaseURN pathologyCaseUrn, String targetSiteId)
	{
		super();
		this.pathologyCaseUrn = pathologyCaseUrn;
		this.targetSiteId = targetSiteId;
	}
	
	public PathologyCaseURN getPathologyCaseUrn()
	{
		return pathologyCaseUrn;
	}

	public String getTargetSiteId() {
		return targetSiteId;
	}

	@Override
	public String callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException 
	{
		getLogger().info("Transferring patient with case [" + getPathologyCaseUrn() + "] to site [" + getTargetSiteId() + "]");
		
		List<PathologyPatientInfoItem> patientInfo = 
			PathologyDataSourceContext.getRouter().getPathologyPatientInfo(getPathologyCaseUrn());
		
		try
		{
			RoutingToken routingToken =
				RoutingTokenHelper.createSiteAppropriateRoutingToken(getTargetSiteId());
			return PathologyDataSourceContext.getRouter().addNewPatient(routingToken, patientInfo, getPathologyCaseUrn().getOriginatingSiteId());
		}
		catch(RoutingTokenFormatException rtfX)
		{
			throw new MethodException(rtfX);
		}
	}

	@Override
	public boolean equals(Object obj) {
		return false;
	}

	@Override
	protected String parameterToString() {
		return getPathologyCaseUrn().toString() + " creating user at site " + getTargetSiteId();
	}

}
