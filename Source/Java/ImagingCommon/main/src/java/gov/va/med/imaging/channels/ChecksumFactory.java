/**
 * 
 */
package gov.va.med.imaging.channels;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.Checksum;

/**
 * This class maintains a mapping from:
 *   string name to a Checksum implementing class
 *   
 * Usually this is just mapped by class simple name but this allows
 * for arbitrary mapping.
 * 
 * @author VHAISWBECKEC
 *
 */
public class ChecksumFactory
{
	private static ChecksumFactory singleton;
	
	public static synchronized ChecksumFactory getFactory()
	{
		if(singleton == null)
			singleton = new ChecksumFactory();
		
		return singleton;
	}
	
	private Map<String, Class<? extends Checksum>> nameAlgorithmMap = new HashMap<String, Class<? extends Checksum>>();
	
	/**
	 * 
	 *
	 */
	private ChecksumFactory()
	{
		// the two checksum classes don't really have to be there because we'll search
		// for them in java.util.zip anyway.  Having them here just makes it clearer to read.
		addNameChecksumMapping("Adler32", java.util.zip.Adler32.class);
		addNameChecksumMapping("CRC32", java.util.zip.CRC32.class);
	}
	
	/**
	 * 
	 * @param algorithmName
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public Checksum get(String algorithmName) 
	throws InstantiationException, IllegalAccessException
	{
		if(algorithmName == null)
			return null;
		
		// try to find the checksum class in the nameAlgorithmMap first
		Class<? extends Checksum> algorithmClass = nameAlgorithmMap.get(algorithmName);
		
		// if the class was not found yet then try to load it by name
		if(algorithmClass == null)
		{
			try{algorithmClass = (Class<? extends Checksum>)Class.forName(algorithmName);} 
			catch (ClassNotFoundException x){}		// don't do anything, we just can't find the class
			catch (ClassCastException x){}			// don't do anything, we found the class but it does not implement Checksum
		}
		
		// if the class was not found yet then try to load it by name, assuming it is in the java.util.zip package
		if(algorithmClass == null)
		{
			try{algorithmClass = (Class<? extends Checksum>)Class.forName("java.util.zip." + algorithmName);} 
			catch (ClassNotFoundException x){}		// don't do anything, we just can't find the class
			catch (ClassCastException x){}			// don't do anything, we found the class but it does not implement Checksum
		}
		
		// give up, we can't find it
		if(algorithmClass == null)
			return null;
		
		return algorithmClass.newInstance();
	}
	
	public void addNameChecksumMapping(String algorithmName, Class<? extends Checksum> algorithmClass)
	{
		nameAlgorithmMap.put(algorithmName, algorithmClass);
	}
	
	public Iterator<String> checksumAlgorithms()
	{
		return nameAlgorithmMap.keySet().iterator();
	}
}
