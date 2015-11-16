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

import gov.va.med.imaging.core.interfaces.StorageCredentials;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.router.storage.StorageBusinessRouter;
import gov.va.med.imaging.core.router.storage.StorageContext;
import gov.va.med.imaging.core.router.storage.StorageDataSourceRouter;
import gov.va.med.imaging.exchange.business.storage.ArtifactSourceInfo;
import gov.va.med.imaging.exchange.business.storage.StorageServerConfiguration;
import gov.va.med.imaging.exchange.business.storage.StorageServerDatabaseConfiguration;
import gov.va.med.imaging.transactioncontext.TransactionContext;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.vista.storage.SmbStorageUtility;

import java.io.InputStream;

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
public class GetResolvedArtifactStreamCommandImpl extends AbstractStorageCommandImpl<InputStream>
{
	private static final long serialVersionUID = -4963797794965394068L;
    private static Logger logger = Logger.getLogger(GetResolvedArtifactStreamCommandImpl.class);
    private static final StorageBusinessRouter businessRouter = StorageContext.getBusinessRouter();
    private static final StorageDataSourceRouter dataSourcerouter = StorageContext.getDataSourceRouter();



    private final ArtifactSourceInfo sourceInfo;

	/**
	 * @param businessRouter
	 * @param asynchronousMethodProcessor
	 */
	public GetResolvedArtifactStreamCommandImpl(ArtifactSourceInfo info) {
		super();
		this.sourceInfo = info;
	}

    
	
	@Override
	public InputStream callSynchronouslyInTransactionContext() throws MethodException, ConnectionException
	{

		StorageServerDatabaseConfiguration dbConfig = StorageServerDatabaseConfiguration.getConfiguration();
		
		TransactionContext transactionContext = TransactionContextFactory.get();
		transactionContext.setServicedSource(getLocalSiteId());

		InputStream artifactStream = null;
		
		if (sourceInfo.getType().equals("NEW"))
		{
			// This artifact is stored in the new storage subsystem.
			// Pass the token to the Storage subsystem and return the stream
			artifactStream = businessRouter.getArtifactStream(sourceInfo.getIdentifier());
		}
		else
		{
			// This artifact is not stored in the old storage subsystem. Just treat retrieve a stream
			// using JCIFS
			SmbStorageUtility util = new SmbStorageUtility();
			artifactStream = util.openFileInputStream(sourceInfo.getIdentifier(), (StorageCredentials)this.sourceInfo).getInputStream();
		}

		// If we have a stream, return it. Otherwise, throw a method exception.
		if (artifactStream != null)
		{
			return artifactStream;
		}
		else
		{
			throw new MethodException("Couldn't retrieve a stream for the requested object: " + sourceInfo.toString());
		}
	}

	@Override
	protected boolean areClassSpecificFieldsEqual(Object obj) {
		// Perform cast for subsequent tests
		final GetResolvedArtifactStreamCommandImpl other = (GetResolvedArtifactStreamCommandImpl) obj;

		// Check the studyUrn
		boolean areFieldsEqual = areFieldsEqual(this.sourceInfo, other.sourceInfo);

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

		sb.append(this.sourceInfo.toString());

		return sb.toString();
	}

}
