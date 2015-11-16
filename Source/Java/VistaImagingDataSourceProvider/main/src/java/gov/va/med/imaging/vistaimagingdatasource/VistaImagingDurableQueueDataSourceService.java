package gov.va.med.imaging.vistaimagingdatasource;

import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityException;
import gov.va.med.imaging.datasource.AbstractVersionableDataSource;
import gov.va.med.imaging.datasource.DurableQueueDataSourceSpi;
import gov.va.med.imaging.exchange.business.DurableQueue;
import gov.va.med.imaging.exchange.business.DurableQueueMessage;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.enums.ImagingSecurityContextType;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.vistadatasource.session.VistaSession;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;
import java.io.IOException;
import java.util.List;
import org.apache.log4j.Logger;

public class VistaImagingDurableQueueDataSourceService 
extends AbstractVersionableDataSource 
implements VistaSessionFactory,DurableQueueDataSourceSpi 
{
	// The required version of VistA Imaging needed to execute the RPC calls for
	// this operation

	// TODO CHANGE TO 3.0P34
	public final static String MAG_REQUIRED_VERSION = "3.0P83";
	public final static int DEFAULT_PATIENT_SENSITIVITY_LEVEL = 2;
	public final static String SUPPORTED_PROTOCOL = "vistaimaging";

	/*
	 * =====================================================================
	 * Instance fields and methods
	 * =====================================================================
	 */
	private Logger logger = Logger.getLogger(this.getClass());

	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public VistaImagingDurableQueueDataSourceService(
			ResolvedArtifactSource resolvedArtifactSource, String protocol) {
		super(resolvedArtifactSource, protocol);
	}

	@Override
	public DurableQueueMessage dequeue(int queueId) throws MethodException,
			ConnectionException {
		return dequeue(queueId, null);
	}

	@Override
	public DurableQueueMessage dequeue(int queueId, String messageGroupId)
			throws MethodException, ConnectionException {
		DurableQueueDAO dao = new DurableQueueDAO(this);
		return dao.dequeue(queueId, messageGroupId);
	}

	@Override
	public DurableQueueMessage enqueue(DurableQueueMessage message)
			throws MethodException, ConnectionException {
		DurableQueueDAO dao = new DurableQueueDAO(this);
		return dao.enqueue(message);
	}

	@Override
	public List<DurableQueue> getAll() throws MethodException,
			ConnectionException {
		DurableQueueDAO dao = new DurableQueueDAO(this);
		return dao.findAll();
	}

	@Override
	public DurableQueue getByName(String name) throws MethodException,
			ConnectionException {
		DurableQueueDAO dao = new DurableQueueDAO(this);
		List<DurableQueue> queues = dao.findAll();
		for (DurableQueue queue : queues) {
			if (queue.getName().equals(name))
				return queue;
		}
		return null;
	}

	@Override
	public int getMessageCount(int queueId) throws MethodException,
			ConnectionException {
		DurableQueueDAO dao = new DurableQueueDAO(this);
		return dao.getMessageCount(queueId);

	}

	@Override
	public int getMessageCount(int queueId, String messageGroupId)
			throws MethodException, ConnectionException {
		DurableQueueDAO dao = new DurableQueueDAO(this);
		return dao.getMessageCount(queueId, messageGroupId);

	}

	@Override
	public List<DurableQueueMessage> getMessages(int queueId, int startIndex,
			int numRecords) throws MethodException, ConnectionException {
		DurableQueueDAO dao = new DurableQueueDAO(this);
		return dao.getMessages(queueId, null, startIndex, numRecords);
	}

	@Override
	public List<DurableQueueMessage> getMessages(int queueId,
			String messageGroupId, int startIndex, int numRecords)
			throws MethodException, ConnectionException {
		DurableQueueDAO dao = new DurableQueueDAO(this);
		return dao.getMessages(queueId, messageGroupId, startIndex, numRecords);
	}

	protected ResolvedSite getResolvedSite() {
		return (ResolvedSite) getResolvedArtifactSource();
	}

	protected Site getSite() {
		return getResolvedSite().getSite();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gov.va.med.imaging.vistaimagingdatasource.dicom.storage.SessionFactory
	 *      #getVistaSession()
	 */
	@Override
	public VistaSession getVistaSession() throws IOException,
			ConnectionException, MethodException {
		TransactionContextFactory.get().setImagingSecurityContextType(
				ImagingSecurityContextType.DICOM_QR_CONTEXT.name());
		return VistaSession.getOrCreate(getMetadataUrl(), getSite());
	}

	@Override
	public boolean isVersionCompatible() throws SecurityException {
		return true;
	}

	@Override
	public void moveMessage(int messageId, int targetQueueId)
			throws MethodException, ConnectionException {
		DurableQueueDAO dao = new DurableQueueDAO(this);
		dao.moveMessage(messageId, targetQueueId);
	}

	@Override
	public DurableQueueMessage peek(int queueId) throws MethodException,
			ConnectionException {
		return this.peek(queueId, null);
	}

	@Override
	public DurableQueueMessage peek(int queueId, String messageGroupId)
			throws MethodException, ConnectionException {
		DurableQueueDAO dao = new DurableQueueDAO(this);
		return dao.peek(queueId, messageGroupId);
	}

	public DurableQueue updateQueue(DurableQueue queue) throws MethodException,
			ConnectionException {
		DurableQueueDAO dao = new DurableQueueDAO(this);
		return dao.update(queue);
	}
}
