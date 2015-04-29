/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 25, 2012
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
package gov.va.med.imaging.pathology.commands;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.pathology.PathologyCase;
import gov.va.med.imaging.pathology.PathologyCaseURN;
import gov.va.med.imaging.pathology.commands.facade.PathologyDataSourceContext;

import java.util.List;

/**
 * Get specific cases, this command verifies all cases requested are from the same source before calling the data source
 * 
 * @author vhaiswwerfej
 *
 */
public class GetPathologySpecificCasesCommandImpl
extends AbstractCommandImpl<List<PathologyCase>>
{
	private static final long serialVersionUID = -7547751246768944957L;
	
	private final List<PathologyCaseURN> cases;
	
	public GetPathologySpecificCasesCommandImpl(List<PathologyCaseURN> cases)
	{
		super();
		this.cases = cases;
	}

	public List<PathologyCaseURN> getCases()
	{
		return cases;
	}

	@Override
	public List<PathologyCase> callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException
	{
		if(cases == null || cases.size() <= 0)
			throw new MethodException("Must specify at least one PathologyCaseURN");
		
		RoutingToken routingToken = null;
		for(PathologyCaseURN caseUrn : cases)
		{
			if(routingToken == null)
			{
				routingToken = caseUrn;
			}
			else
			{
				if(!routingToken.isEquivalent(caseUrn))
				{
					throw new MethodException("All cases specified must be from the same source");
				}
			}
		}
		
		return PathologyDataSourceContext.getRouter().getPathologySpecificCases(routingToken, getCases());
	}

	@Override
	public boolean equals(Object obj)
	{
		return false;
	}

	@Override
	protected String parameterToString()
	{
		return "";
	}
}
