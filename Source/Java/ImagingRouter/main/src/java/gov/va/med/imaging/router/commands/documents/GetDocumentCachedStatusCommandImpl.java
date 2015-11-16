/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Mar 31, 2011
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
package gov.va.med.imaging.router.commands.documents;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.SERIALIZATION_FORMAT;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.storage.cache.ImmutableInstance;
import gov.va.med.imaging.exchange.storage.cache.RealizedCache;
import gov.va.med.imaging.storage.cache.exceptions.CacheException;

/**
 * @author VHAISWWERFEJ
 *
 */
public class GetDocumentCachedStatusCommandImpl
extends AbstractDocumentCommandImpl<Boolean>
{
	private static final long serialVersionUID = 1414559260046745039L;

	public GetDocumentCachedStatusCommandImpl(GlobalArtifactIdentifier documentIdentifier)
	{
		super(documentIdentifier);
	}

	@Override
	public Boolean callSynchronouslyInTransactionContext()
	throws MethodException, ConnectionException
	{
		getLogger().info("Determining if document '" + getDocumentIdentifier().toString(SERIALIZATION_FORMAT.RAW) + "' is cached");
		// if caching disabled, don't bother doing anything
		if(!getCommandContext().isCachingEnabled())
			return false;
		RealizedCache cache = getCache();
		if(cache != null)
		{
			try
			{
				ImmutableInstance instance = 
					cache.getDocumentContent(getDocumentIdentifier());
				if(instance != null)
				{
					// getting an instance does not open it, don't need to close it
					getLogger().info("Document '" + getDocumentIdentifier().toString() + "' found in cache.");
					return true;
				}
			}
			catch(CacheException cX)
			{
				getLogger().error("CacheException reading '" + getDocumentIdentifier().toString() + "' from cache, " + cX.getMessage());
			}
		}
		return false;
	}


	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((documentIdentifier == null) ? 0 : documentIdentifier
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		GetDocumentCachedStatusCommandImpl other = (GetDocumentCachedStatusCommandImpl) obj;
		if (documentIdentifier == null)
		{
			if (other.documentIdentifier != null)
				return false;
		}
		else if (!documentIdentifier.equals(other.documentIdentifier))
			return false;
		return true;
	}

	@Override
	protected String parameterToString()
	{
		return getDocumentIdentifier().toString();
	}

}
