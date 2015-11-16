/**
 * 
 */
package gov.va.med.imaging.channels;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author VHAISWBECKEC
 *
 */
public abstract class AbstractBytePump
{
	static Map<PumpClass, BUFFER_SIZE> nameToBufferSizeMap = new HashMap<PumpClass, BUFFER_SIZE>();
	
	static int avgThresholdPercent = 75;
	static int maxThresholdPercent = 90;

	private String name;
	protected AbstractBytePump(String name)
	{
		this.name = name;
	}
	public String getName()
	{
		return this.name;
	}
	
	public enum MEDIUM
	{
		ChannelToChannel,
		ChannelToStream,
		StreamToChannel,
		StreamToStream;
	}
	
	public enum TRANSFER_TYPE
	{
		ByteArrayToByteArray,
		ByteArrayToFile,
		ByteArrayToNetwork,
		FileToByteArray,
		FileToFile,
		FileToNetwork,
		NetworkToByteArray,
		NetworkToFile,
		NetworkToNetwork;
	}

	public enum BUFFER_SIZE
	{
		OneK(1024),
		TwoK(2 * 1024),
		FourK(4 * 1024),
		EightK(8 * 1024),
		SixteenK(16 * 1024),
		ThirtyTwoK(32 * 1024),
		SixtyFourK(64 * 1024),
		OneTwentyEightK(128 * 1024),
		TwoFiftySixK(256 * 1024);
		
		public static BUFFER_SIZE defaultBufferSize = EightK;
		
		private int size;
		BUFFER_SIZE(int size)
		{
			this.size = size;
		}
		
		public int getSize()
		{
			return this.size;
		}
	
		/**
		 * @return the next smallest buffer size of null if
		 * this is the smallest
		 */
		public BUFFER_SIZE nextSmaller()
		{
			BUFFER_SIZE nextSmaller = null;
			for(BUFFER_SIZE indexBufferSize: BUFFER_SIZE.values())
			{
				if(this.equals(indexBufferSize))
					break;
				nextSmaller = indexBufferSize;
			}
			return nextSmaller;
		}
		
		/**
		 * 
		 * @return - the next largest buffer size or null if this is the largest
		 */
		public BUFFER_SIZE nextLarger()
		{
			boolean breakNext = false;
			
			for(BUFFER_SIZE indexBufferSize: BUFFER_SIZE.values())
			{
				if(breakNext)
					return indexBufferSize;
				if(this.equals(indexBufferSize))
					breakNext = true;
			}
			
			return null;
		}
	}

	protected static BUFFER_SIZE getBufferSize(String name, MEDIUM medium)
	{
		PumpClass pumpClass = new PumpClass(name, medium);

		return getBufferSize(pumpClass);
	}
	
	protected static BUFFER_SIZE getBufferSize(PumpClass pumpClass)
	{
		BUFFER_SIZE bufferSize;
		synchronized(nameToBufferSizeMap)
		{
			bufferSize = nameToBufferSizeMap.get(pumpClass);
			if(bufferSize == null)
			{
				nameToBufferSizeMap.put(pumpClass, BUFFER_SIZE.defaultBufferSize);
				bufferSize = BUFFER_SIZE.defaultBufferSize;
			}
		}
		return bufferSize;
	}

	/**
	 * Stream the name to buffer size map
	 * This could be used to save the 'tuned' map or simply as a debug tool
	 * 
	 * @param outStream
	 * @throws IOException 
	 */
	public static String getNameToBufferSizeMapString() throws IOException
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("Start => Name to Buffer Size Map ====================================================== \n");
		for(PumpClass pumpClass:nameToBufferSizeMap.keySet())
		{
			String line = pumpClass + ":" + nameToBufferSizeMap.get(pumpClass).toString() + "\n";
			sb.append(line);
		}
		sb.append("End => Name to Buffer Size Map ======================================================== \n");
		
		return sb.toString();
	}

	/**
	 * 
	 * @param name
	 * @param smallestChunkSize
	 * @param largestChunkSize
	 * @param totalBytesRead
	 * @param readCount
	 * @param elapsedTime
	 */
	protected static void adjustBufferUsage(
			String name, MEDIUM medium, 
			int smallestChunkSize, int largestChunkSize, 
			int totalBytesRead, int readCount,
			long elapsedTime)
	{
		PumpClass pumpClass = new PumpClass(name, medium);
		
		if(totalBytesRead > 0 || readCount > 0)
		{
			int avgChunkSize = totalBytesRead/readCount;
			if(name == null)
				return;
			
			BUFFER_SIZE currentBufferSize = getBufferSize(name, medium);
			BUFFER_SIZE smallerBufferSize = currentBufferSize.nextSmaller();
			
			int currentBufferSizeInt = currentBufferSize.getSize();
			int currentBufferMaxThreshold = (currentBufferSizeInt * maxThresholdPercent) / 100;
			int currentBufferAvgThreshold = (currentBufferSizeInt * avgThresholdPercent) / 100;
			
			BUFFER_SIZE targetBufferSize = null;
		
			if(smallerBufferSize != null && largestChunkSize < smallerBufferSize.getSize())
				targetBufferSize = smallerBufferSize;
			
			else if( largestChunkSize > currentBufferMaxThreshold || avgChunkSize > currentBufferAvgThreshold)
				targetBufferSize = currentBufferSize.nextLarger();
			
			if(targetBufferSize != null)
				synchronized(nameToBufferSizeMap)
				{
					nameToBufferSizeMap.put(pumpClass, targetBufferSize);
				}
		}
	}
}
