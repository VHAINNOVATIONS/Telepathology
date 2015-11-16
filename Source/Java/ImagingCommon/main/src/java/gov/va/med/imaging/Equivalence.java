/**
 * 
 */
package gov.va.med.imaging;

/**
 * @author VHAISWBECKEC
 * 
 * The Equivalence interface was intended as a testing assist.  
 * The intent is to allow determination of the equivalence of two
 * objects with respect to everything but artificial keys, in 
 * that respect it is an opposite to .equals().
 * 
 * The contract for equivalent is as follows:
 * For object A, object B is equivalent if for all fields other than artificial
 * keys A.field.equals(B.field) returns true.
 * 
 * The originating usage was in testing results of queries.  For the same
 * parameters the results should be equivalent.  For the ViX the results include
 * a transaction ID which is globally unique.  Comparing the results of two
 * requests is done on everything but the transaction ID.
 * 
 */
public interface Equivalence
{
	/**
	 * Returns true if the given instance is equivalent to this instance.
	 * Equivalent means that all fields but artificial keys are equal.
	 * 
	 * @param obj
	 * @return
	 */
	public boolean equivalent(Object obj);
}
