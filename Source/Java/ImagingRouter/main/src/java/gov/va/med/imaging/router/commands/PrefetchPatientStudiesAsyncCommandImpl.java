/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jan 21, 2011
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
package gov.va.med.imaging.router.commands;

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.core.annotations.routerfacade.RouterCommandExecution;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * @author vhaiswwerfej
 *
 */
@RouterCommandExecution(asynchronous=true, distributable=false)
public class PrefetchPatientStudiesAsyncCommandImpl
extends AbstractStudyCommandImpl<Boolean>
{
	private static final long serialVersionUID = 2054228497793560861L;
	
	private final RoutingToken routingToken;
	private final PatientIdentifier patientIdentifier; 
	private final StudyFilter filter;
	private final StudyLoadLevel studyLoadLevel;
	
	public PrefetchPatientStudiesAsyncCommandImpl(RoutingToken routingToken, PatientIdentifier patientIdentifier, 
			StudyFilter filter, StudyLoadLevel studyLoadLevel)
	{
		this.routingToken = routingToken;
		this.patientIdentifier = patientIdentifier;
		this.filter = filter;
		this.studyLoadLevel = studyLoadLevel;		 
	}

	public RoutingToken getRoutingToken()
	{
		return routingToken;
	}

	public PatientIdentifier getPatientIdentifier()
	{
		return patientIdentifier;
	}

	public StudyFilter getFilter()
	{
		return filter;
	}

	public StudyLoadLevel getStudyLoadLevel()
	{
		return studyLoadLevel;
	}

	@Override
	public Boolean callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException
	{		
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setServicedSource(getRoutingToken().toRoutingTokenString());
		
		// for now we need to ignore the study load level provided because if it isn't full then
		// it won't get cached.  IF we make the cache support non full studies then the provided study load
		// level can be used
		// 
		// call the existing method to consolidate code, need to be sure it does actually cache the local metadata
		// and that it doesn't call an async command to get the full loaded data in any case (Even if study load level is not full)
		getPatientStudySetResult(getRoutingToken(), getPatientIdentifier(), getFilter(), StudyLoadLevel.FULL);
		
		return true;
	}

	@Override
	public boolean equals(Object obj)
	{
		return false;
	}

	@Override
	protected String parameterToString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.getRoutingToken().toRoutingTokenString());
		sb.append(',');
		sb.append(this.getPatientIdentifier());
		sb.append(',');
		sb.append(this.getStudyLoadLevel());
		sb.append(',');
		sb.append(this.getFilter() == null ? "<null>" : this.getFilter().toString());
		
		return sb.toString();
	}

}
