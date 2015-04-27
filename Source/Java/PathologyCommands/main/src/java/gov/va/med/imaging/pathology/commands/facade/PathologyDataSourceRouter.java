/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 7, 2011
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
package gov.va.med.imaging.pathology.commands.facade;

import java.util.List;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterDataSourceMethod;
import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterInterface;
import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterInterfaceCommandTester;
import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterMethod;
import gov.va.med.imaging.core.interfaces.FacadeRouter;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.pathology.PathologyCase;
import gov.va.med.imaging.pathology.PathologyCaseURN;

/**
 * @author VHAISWWERFEJ
 *
 */
@FacadeRouterInterface
@FacadeRouterInterfaceCommandTester
public interface PathologyDataSourceRouter
extends FacadeRouter
{
	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="GetPathologySpecificCasesDataSourceCommand")
	@FacadeRouterDataSourceMethod(commandClassName="GetPathologySpecificCasesDataSourceCommand", 
			commandPackage="gov.va.med.imaging.pathology.commands.datasource",
			dataSourceSpi="gov.va.med.imaging.pathology.datasource.PathologyDataSourceSpi", methodName="getSpecificCases", 
			routingTokenParameterName="routingToken", 
			spiParameterNames="cases",
			postProcessResultMethodName="PathologyDataSourceHelper.postProcessCasesResult")
	public abstract List<PathologyCase> getPathologySpecificCases(RoutingToken routingToken, List<PathologyCaseURN> cases)
	throws MethodException, ConnectionException;

}
