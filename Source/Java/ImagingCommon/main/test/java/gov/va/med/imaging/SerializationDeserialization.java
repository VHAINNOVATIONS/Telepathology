/**
 * 
 */
package gov.va.med.imaging;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;

/**
 * @author VHAISWBECKEC
 *
 */
public class SerializationDeserialization<T>
{
	public SerializationDeserialization()
	{
		
	}
	
	/**
	 * @param original
	 * @return
	 */
	public T serializeAndDeserialize(T original)
	{
		java.io.ByteArrayOutputStream out = new DebuggingByteArrayOutputStream(4096);
		XMLEncoder encoder = new XMLEncoder(out);
		encoder.writeObject(original);
		encoder.close();
		
		dumpByteArray(out.toByteArray());
		
		java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(out.toByteArray());
		XMLDecoder decoder = new XMLDecoder(in);
		T deserialized = (T)decoder.readObject();
		decoder.close();
		
		return deserialized;
	}

	private void dumpByteArray(byte[] byteArray)
	{
		for(int index=0; index<byteArray.length; ++ index)
		{
			System.out.print(byteArray[index]);
			if((index % 80) == 0)
				System.out.print("\n");
		}
	}
	
	class DebuggingByteArrayOutputStream
	extends java.io.ByteArrayOutputStream
	{
		public DebuggingByteArrayOutputStream()
		{
			super();
		}

		public DebuggingByteArrayOutputStream(int size)
		{
			super(size);
		}

		@Override
		public synchronized void write(int b)
		{
			System.out.print(b);
			super.write(b);
		}
	}
}
