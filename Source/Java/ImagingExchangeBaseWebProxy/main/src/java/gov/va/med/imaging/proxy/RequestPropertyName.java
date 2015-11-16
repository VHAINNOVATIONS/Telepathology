/**
 * 
 */
package gov.va.med.imaging.proxy;

import gov.va.med.imaging.EnumPersistenceDelegate;

/**
 * @author VHAISWBECKEC
 *
 * This is an enumeration of the "well-known" property names that are copied from
 * request to response and are transfered across thread switches (in asynchrounous
 * proxies).
 * 
 * Additional (caller specific) properties may be added to the request properties, which
 * will also survive thread switches and be available in the asynch results.  The properties
 * listed here are for convenience and also to publicize which are immutable and which
 * are not.
 * Immutable properties may be set once, but cannot be changed once set. 
 */
public enum RequestPropertyName
{
	TransactionId(false),					// the transaction ID of this transaction, an immutable value
	ReferenceTransactionId(true),			// the transaction ID of a reference transaction
	RequiredResponseDate(false);			// the date that the response is required (used for priority scheduling)

	// makes this enum work with XML encoder/decoder
	// delete this when we switch to Java 1.6
	static { EnumPersistenceDelegate.installFor(values()[0].getClass()); }
	
	private boolean mutable;
	RequestPropertyName(boolean mutable)
	{
		this.mutable = mutable;
	}
	
	public boolean isMutable()
	{
		return mutable;
	}
}