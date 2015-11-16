package gov.va.med.imaging.datasource;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.annotations.SPI;
import gov.va.med.imaging.exchange.business.DurableQueue;
import gov.va.med.imaging.exchange.business.DurableQueueMessage;

import java.util.List;

/**
 * This class defines the Service Provider Interface (SPI) for the
 * DurableQueueDataSource. All the abstract methods in this class must be
 * implemented by each data source service provider who wishes to supply the
 * implementation of a DurableQueueDataSource for a particular datasource type.
 * 
 * @since 1.0
 * @author VHAISWGRAVER
 * 
 */
@SPI(description="The service provider interface for persistent queues.")
public interface DurableQueueDataSourceSpi 
extends VersionableDataSourceSpi 
{
	DurableQueueMessage dequeue(int queueId) throws MethodException,
			ConnectionException;

	DurableQueueMessage dequeue(int queueId, String messageGroupId)
			throws MethodException, ConnectionException;

	DurableQueueMessage enqueue(DurableQueueMessage message)
			throws MethodException, ConnectionException;

	List<DurableQueue> getAll() throws MethodException, ConnectionException;

	DurableQueue getByName(String name) throws MethodException,
			ConnectionException;

	int getMessageCount(int queueId) throws MethodException,
			ConnectionException;

	int getMessageCount(int queueId, String messageGroupId)
			throws MethodException, ConnectionException;

	List<DurableQueueMessage> getMessages(int queueId, int startIndex,
			int numRecords) throws MethodException, ConnectionException;

	List<DurableQueueMessage> getMessages(int queueId, String messageGroupId,
			int previousRecordId, int numRecords) throws MethodException,
			ConnectionException;

	void moveMessage(int messageId, int targetQueueId) throws MethodException,
			ConnectionException;

	DurableQueueMessage peek(int queueId) throws MethodException,
			ConnectionException;

	DurableQueueMessage peek(int queueId, String messageGroupId)
			throws MethodException, ConnectionException;

	DurableQueue updateQueue(DurableQueue queue) throws MethodException,
			ConnectionException;
}
