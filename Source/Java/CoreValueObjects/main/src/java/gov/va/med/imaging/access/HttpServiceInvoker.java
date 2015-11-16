package gov.va.med.imaging.access;


/**
 * This Interface lets you concoct your own Http Service accessors.
 * 
 * @author VHAISWBATESL1
 *
 */
public interface HttpServiceInvoker
{

   /**
    * Invoke an Http Service request.
    * @param methodName the name of the method to invoke.
    * @param clazzes the classes of the parameters.
    * @param params the method parameters.
    * @return a generic Java Object for the user to puzzle over.
    * @throws Exception if something goes wrong.
    */
   public Object invoke (String     methodName,
                         Class<?>[] clazzes,
                         Object[]   params) throws Exception;
   
} // interface HttpServiceInvoker
