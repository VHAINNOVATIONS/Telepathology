/**
 * 
 */
package gov.va.med.imaging.core.interfaces;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import gov.va.med.DataGenerationConfiguration;
import gov.va.med.GenericDataGenerator;
import gov.va.med.NetworkSimulatingInputStream;
import gov.va.med.MasterDataGenerator;
import gov.va.med.GenericDataGenerator.Mode;
import gov.va.med.NetworkSimulatingInputStream.DELAY_MODE;
import gov.va.med.NetworkSimulatingInputStream.EXCEPTION_MODE;
import gov.va.med.imaging.core.interfaces.FacadeRouter;
import gov.va.med.imaging.core.interfaces.router.AsynchronousCommandResult;
import gov.va.med.imaging.core.interfaces.router.AsynchronousCommandResultListener;
import gov.va.med.imaging.core.interfaces.router.Command;
import gov.va.med.imaging.core.interfaces.router.NullCommand;
import org.apache.log4j.Logger;

/**
 * @author vhaiswbeckec
 *
 */
public abstract class AbstractMockFacadeRouterImpl
implements FacadeRouter
{
	private static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
	private static GenericDataGenerator.Mode mode = GenericDataGenerator.Mode.RANDOMIZE;
	private static NetworkSimulatingInputStream.EXCEPTION_MODE ioExceptionMode = NetworkSimulatingInputStream.EXCEPTION_MODE.RELIABLE; 
	private static NetworkSimulatingInputStream.DELAY_MODE delayMode = NetworkSimulatingInputStream.DELAY_MODE.NONE; 
	private final GenericDataGenerator dataGenerator;

	public static GenericDataGenerator.Mode getMode()
	{
		return mode;
	}

	public static NetworkSimulatingInputStream.EXCEPTION_MODE getIoExceptionMode()
	{
		return ioExceptionMode;
	}

	public static NetworkSimulatingInputStream.DELAY_MODE getDelayMode()
	{
		return delayMode;
	}

	public static void setIoExceptionMode(NetworkSimulatingInputStream.EXCEPTION_MODE ioExceptionMode)
	{
		AbstractMockFacadeRouterImpl.ioExceptionMode = ioExceptionMode;
	}

	public static void setDelayMode(NetworkSimulatingInputStream.DELAY_MODE delayMode)
	{
		AbstractMockFacadeRouterImpl.delayMode = delayMode;
	}

	public static void setMode(GenericDataGenerator.Mode mode)
	{
		AbstractMockFacadeRouterImpl.mode = mode;
	}

	public AbstractMockFacadeRouterImpl()
	{
		dataGenerator = new MasterDataGenerator(
			new DataGenerationConfiguration(mode, ioExceptionMode, delayMode)
		);
	}
	
	private GenericDataGenerator getDataGenerator()
	{
		return this.dataGenerator;
	}

	/**
	 * 
	 * @param <T>
	 * @param type
	 * @return
	 */
	protected <T extends Object> T createInstance(Class<T> type)
	{
		return getDataGenerator().createInstance(type);
	}
	
	protected <T extends Collection<E>, E extends Object> T createCollectionInstance(Class<T> collectionType, Class<E> elementType)
	{
		return (T)getDataGenerator().createCollectionInstance(collectionType, elementType);
	}
	
	protected <T extends Map<K, V>, K extends Object, V extends Object> T createMapInstance(
		Class<T> mapType, 
		Class<K> keyType, 
		Class<V> valueType)
	{
		return (T)getDataGenerator().createMapInstance(mapType, keyType, valueType);
	}
	
	private void asynchronouslyNotifyListener(Object o, AsynchronousCommandResultListener listener)
	{
		executor.submit( new NotableNotifier(o, listener) );
	}
	
	class NotableNotifier
	implements Callable<Void>
	{
		private final Object o;
		private final AsynchronousCommandResultListener listener;
		
		NotableNotifier(Object o, AsynchronousCommandResultListener listener)
		{
			this.o = o;
			this.listener = listener;
		}
		
		@Override
		public Void call() 
		throws Exception
		{
			listener.commandComplete( new AsynchronousCommandResult(new NullCommand(), o));
			return null;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	private Logger getMyLogger()
	{
		return Logger.getLogger(AbstractMockFacadeRouterImpl.class);
	}
	
	
}
