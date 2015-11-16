/**
 * 
 */
package gov.va.med.imaging.transactioncontext;

import java.io.*;

import gov.va.med.imaging.tomcat.vistarealm.VistaRealmPrincipal;

/**
 * @author VHAISWBECKEC
 * 
 * A wrapper around a byte array, with a fancy name.
 */
public class TransactionContextMemento
{
	private byte[] buffer;
	
	static TransactionContextMemento create(VistaRealmPrincipal principal)
	throws IOException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(out);
		
		// writeObject iterates through any collection instances in the principal
		// this means that any modifications to the principal while the serialization
		// is taking place will result in a ConcurrentModificationException
		oos.writeObject(principal);
		
		TransactionContextMemento memento = new TransactionContextMemento(out.toByteArray());
		oos.close();
		
		return memento;
	}
	
	static VistaRealmPrincipal create(TransactionContextMemento memento)
	throws IOException, ClassNotFoundException
	{
		ByteArrayInputStream in = new ByteArrayInputStream( memento.getBuffer() );
		ObjectInputStream ois = new ObjectInputStream(in);
		VistaRealmPrincipal principal = (VistaRealmPrincipal)ois.readObject();
		ois.close();
		
		return principal;
	}
	
	TransactionContextMemento(byte[] buffer)
	{
		this.buffer = buffer;
	}
	
	/**
	 * @return the buffer
	 */
	byte[] getBuffer()
	{
		return this.buffer;
	}
}
