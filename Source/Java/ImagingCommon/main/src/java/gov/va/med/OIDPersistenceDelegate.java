/**
 * 
 */
package gov.va.med;

import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;
import java.lang.reflect.Method;

/**
 * @author vhaiswbeckec
 * 
 */
public class OIDPersistenceDelegate
	extends PersistenceDelegate
{
	/**
	 * Returns an expression whose value is oldInstance. 
	 * This method is used to characterize the constructor or factory method that should be used to 
	 * create the given object. 
	 * For example, the instantiate method of the persistence delegate for the Field class could be defined as follows:
	 * Field f = (Field)oldInstance;
	 * return new Expression(f, f.getDeclaringClass(), "getField", new Object[]{f.getName()});
	 * 
	 * Note that we declare the value of the returned expression so that the value of the expression 
	 * (as returned by getValue) will be identical to oldInstance.
	 * 
	 * Parameters:
	 * oldInstance - The instance that will be created by this expression.
	 * out - The stream to which this expression will be written.
	 * 
	 *  Returns:
	 *  An expression whose value is oldInstance.
	 */
	@Override
	protected Expression instantiate(Object oldInstance, Encoder out)
	{
		Method m = (Method) oldInstance;
		
		// An Expression object represents a primitive expression in which a single method is applied to a 
		// target and a set of arguments to return a result - as in "a.getFoo()".
		// In addition to the properties of the super class, the Expression object provides a value which 
		// is the object returned when this expression is evaluated. The return value is typically not 
		// provided by the caller and is instead computed by dynamically finding the method and invoking 
		// it when the first call to getValue is made.
		
		// Creates a new Expression object for a method that returns a result. 
		// The result will never be calculated however, since this constructor uses the value parameter to 
		// set the value property by calling the setValue method.
		// Parameters:
		//  value - The value of this expression.
		//  target - The target of this expression.
		//  methodName - The methodName of this expression.
		//  arguments - The arguments of this expression. If null then an empty array will be used.
		return new Expression(
			oldInstance, 
			m.getDeclaringClass(), 
			"create", 
			new Object[]{ m.getName(), m.getParameterTypes() });
	}
}
