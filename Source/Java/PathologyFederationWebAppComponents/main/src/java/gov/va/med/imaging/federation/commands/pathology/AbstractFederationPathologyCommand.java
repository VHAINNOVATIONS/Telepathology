/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 22, 2012
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
package gov.va.med.imaging.federation.commands.pathology;

import gov.va.med.imaging.federation.pathology.FederationPathologyContext;
import gov.va.med.imaging.federation.pathology.FederationPathologyRouter;
import gov.va.med.imaging.web.commands.AbstractWebserviceCommand;

/**
 * @author VHAISWWERFEJ
 *
 */
public abstract class AbstractFederationPathologyCommand<D, E extends Object>
extends AbstractWebserviceCommand<D, E>
{

	public AbstractFederationPathologyCommand(String methodName)
	{
		super(methodName);
	}
	
	/**
	 * 
	 * @return
	 */
	@Override
	protected FederationPathologyRouter getRouter()
	{
		return FederationPathologyContext.getFederationRouter();
	}
		
	@Override
	protected String getWepAppName() 
	{
		return "Federation WebApp";
	}
	
	@Override
	protected String getRequestTypeAdditionalDetails()
	{
		return null;
	}

	@Override
	public void setAdditionalTransactionContextFields()
	{
		
	}

}
