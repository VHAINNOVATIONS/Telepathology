package gov.va.med.imaging.storage.cache;


public interface InstanceByteChannelListener
{
	/**
	 * Notification that a writable byte channel has been closed.  This is 
	 * the 'happy' path.  The listeners may allow read channels to open
	 * on the source (file).
	 * Note that this event will NOT be sent to the listeners after a timeout signal
	 * has been sent.  The responsibility for closing the channel relies on the
	 * listener(s).
	 * 
	 * @param writable
	 */
	public abstract void writeChannelClose(InstanceWritableByteChannel writable);
	
	/**
	 * Notification that a readable byte channel has been closed.  This is 
	 * the 'happy' path.
	 * Note that this event will NOT be sent to the listeners after a timeout signal
	 * has been sent.  The responsibility for closing the channel relies on the
	 * listener(s).
	 * 
	 * @param readable
	 */
	public abstract void readChannelClose(InstanceReadableByteChannel readable);

	/**
	 * Notification that a writable byte channel has been open without activity for longer
	 * than the allowable time.  This is a one time notification, the channel is closed
	 * when this message is received.  This message will be received AFTER a writeChannelClose
	 * message on the same channel has been received.
	 * 
	 * @param writable
	 */
	public abstract void writeChannelIdleTimeout(InstanceWritableByteChannel writable);
	
	/**
	 * Notification that a readable byte channel has been open without activity for longer
	 * than the allowable time.  This is a one time notification, the channel is closed
	 * when this message is received.  This message will be received AFTER a readChannelClose
	 * message on the same channel has been received.
	 * 
	 * @param readable
	 */
	public abstract void readChannelIdleTimeout(InstanceReadableByteChannel readable);

}