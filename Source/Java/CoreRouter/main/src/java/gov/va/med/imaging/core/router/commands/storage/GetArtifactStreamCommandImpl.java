/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr, 2010
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswlouthj
  Description: DICOM Study cache manager. Maintains the cache of study instances
  			   and expires old studies after 15 minutes. 

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
package gov.va.med.imaging.core.router.commands.storage;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.storage.Artifact;
import gov.va.med.imaging.exchange.business.storage.ArtifactInstanceProviderPair;
import gov.va.med.imaging.exchange.business.storage.StorageServerConfiguration;
import gov.va.med.imaging.exchange.business.storage.StorageServerDatabaseConfiguration;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * This command is the driver for storing a DICOM instance. It performs validation
 * of the patient and imaging service request, check UIDs and coerces them if necessary,
 * determines whether the instance is an "old" or "new" SOP class, and stores the
 * instance appropriately.
 * 
 * @author vhaiswlouthj
 * 
 */
public class GetArtifactStreamCommandImpl extends AbstractStorageCommandImpl<InputStream>
{
	private static final long serialVersionUID = -4963797794965394068L;
    private static Logger logger = Logger.getLogger(GetArtifactStreamCommandImpl.class);

    private final String artifactToken;

	/**
	 * @param router
	 * @param asynchronousMethodProcessor
	 */
	public GetArtifactStreamCommandImpl(String artifactToken) {
		super();
		this.artifactToken = artifactToken;
	}

    
	
	@Override
	public InputStream callSynchronouslyInTransactionContext() throws MethodException, ConnectionException
	{

		StorageServerDatabaseConfiguration dbConfig = StorageServerDatabaseConfiguration.getConfiguration();
		
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setServicedSource(getLocalSiteId());

		InputStream artifactStream = null;

		Artifact artifact = dataSourceRouter.getArtifactAndInstancesByToken(artifactToken);
		List<ArtifactInstanceProviderPair> artifactInstanceProviderPairs = dbConfig.getProvidersForRetrieval(artifact);

		//
		// Try to get the stream from providers until successful, or until we run out of providers...
		//
		FileNotFoundException fnfe = null;
		
		for (ArtifactInstanceProviderPair artifactInstanceProviderPair : artifactInstanceProviderPairs)
		{
			try
			{
				// Get the stream			
				artifactStream = artifactInstanceProviderPair.getProvider().getArtifactStream(artifactInstanceProviderPair.getArtifactInstance());

				// Mark the artifactInstance as "touched" - currently the "update" method only updates last
				// accessed date and time...
				dataSourceRouter.putArtifactInstanceLastAccessed(artifactInstanceProviderPair.getArtifactInstance());
				break;
			}
			catch (FileNotFoundException e)
			{
				fnfe = e;
			}
		}

		// If we have a stream, return it. Otherwise, throw a method exception.
		if (artifactStream != null)
		{
			return artifactStream;
		}
		else
		{
			if (fnfe != null)
				throw new MethodException(fnfe);
			else
				throw new MethodException();
		}
	}

	@Override
	protected boolean areClassSpecificFieldsEqual(Object obj) {
		// Perform cast for subsequent tests
		final GetArtifactStreamCommandImpl other = (GetArtifactStreamCommandImpl) obj;

		// Check the studyUrn
		boolean areFieldsEqual = areFieldsEqual(this.artifactToken, other.artifactToken);

		return areFieldsEqual;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.core.router.AsynchronousCommandProcessor#parameterToString()
	 */
	@Override
	protected String parameterToString()
	{
		StringBuffer sb = new StringBuffer();

		sb.append(this.artifactToken.toString());

		return sb.toString();
	}

}
