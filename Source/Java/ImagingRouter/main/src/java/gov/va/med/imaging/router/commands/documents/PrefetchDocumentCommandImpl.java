/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 24, 2011
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
package gov.va.med.imaging.router.commands.documents;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.imaging.core.annotations.routerfacade.RouterCommandExecution;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.AbstractCommandImpl;
import gov.va.med.imaging.router.facade.ImagingContext;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

/**
 * This is the synchronous command called by the parent that just kicks off an async command
 * 
 * @author vhaiswwerfej
 *
 */
@RouterCommandExecution(asynchronous=false, distributable=false)
public class PrefetchDocumentCommandImpl
extends AbstractCommandImpl<Boolean>
{	
	private static final long serialVersionUID = -2497481010814472001L;
	
	private final GlobalArtifactIdentifier gai;
	
	public PrefetchDocumentCommandImpl(GlobalArtifactIdentifier gai)
	{
		this.gai = gai;
	}

	@Override
	public Boolean callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException
	{
		TransactionContext transactionContext = TransactionContextFactory.get();
		getLogger().info("Prefetching document '" + getGai().toString() + "', transaction '" + transactionContext.getTransactionId() + "'.");		
		transactionContext.setServicedSource(getGai().toRoutingTokenString());		
		// kick off async operation
		ImagingContext.getRouter().prefetchDocument(getGai());
		return true;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gai == null) ? 0 : gai.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		final PrefetchDocumentCommandImpl other = (PrefetchDocumentCommandImpl) obj;
		if (gai == null)
		{
			if (other.gai != null)
				return false;
		}
		else if (!gai.equals(other.gai))
			return false;
		return true;
	}

	@Override
	protected String parameterToString()
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.getGai());
		
		return sb.toString();
	}

	public GlobalArtifactIdentifier getGai()
	{
		return gai;
	}

}
