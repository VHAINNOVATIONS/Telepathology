/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 13, 2012
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

import gov.va.med.imaging.exchange.enums.ImagingSecurityContextType;
import gov.va.med.imaging.pathology.PathologyFacadeContext;
import gov.va.med.imaging.pathology.PathologyFacadeRouter;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.web.commands.AbstractWebserviceCommand;

/**
 * @author VHAISWWERFEJ
 *
 */
public abstract class AbstractPathologyCommand<D, E extends Object>
extends AbstractWebserviceCommand<D, E>
{

	/**
	 * @param methodName
	 */
	public AbstractPathologyCommand(String methodName)
	{
		super(methodName);
		TransactionContextFactory.get().setImagingSecurityContextType(ImagingSecurityContextType.MAGTP_WORKLIST_MGR.name());
	}
	
	@Override
	protected PathologyFacadeRouter getRouter()
	{
		return PathologyFacadeContext.getPathologyFacadeRouter();
	}

	@Override
	protected String getWepAppName()
	{
		return "Pathology WebApp";
	}

	@Override
	public String getInterfaceVersion()
	{
		return "V1";
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
