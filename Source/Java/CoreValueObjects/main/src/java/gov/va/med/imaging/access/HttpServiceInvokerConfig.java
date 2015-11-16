package gov.va.med.imaging.access;


/**
 * This class contains the dope needed to access a Spring Http Web Service.
 * 
 * @author VHAISWBATESL1
 *
 */
public class HttpServiceInvokerConfig
{

   private String serviceUrl = null;
   private Class<?> serviceInterface = null;
   
   
   /**
    * MT Constructor.
    */
   public HttpServiceInvokerConfig ()
   {
   
   } // HttpServiceInvokerConfig

   
   /**
    * Set the String URL of the Spring Http service.
    * @param serviceUrl The String URL of the Spring Http service.
    */
   public void setServiceUrl (String serviceUrl) {this.serviceUrl = serviceUrl;}

   
   /**
    * Set the Class of the Spring Http service interface.
    * @param serviceInterface The Class of the Spring Http service interface.
    */
   public void setServiceInterface (Class<?> serviceInterface) {this.serviceInterface = serviceInterface;}

   
   /**
    * Get the String URL of the Spring Http service.
    * @return the String URL of the Spring Http service.
    */
   public String getServiceUrl () {return serviceUrl;}

   
   /**
    * Get the Class of the Spring Http service interface.
    * @return the Class of the Spring Http service interface.
    */
   public Class<?> getServiceInterface () {return serviceInterface;}

   
   /**
    * Display Yourself.
    */
   public String toString ()
   {
   
      StringBuilder sbTxt = new StringBuilder ();
      
      sbTxt.append (getClass ().getName ());
      sbTxt.append (": Http Service Url: ");
      sbTxt.append (getServiceUrl ());
      sbTxt.append (", Http Service Interface: ");
      sbTxt.append (getServiceInterface ().getName ());
      
      return sbTxt.toString ();
      
   } // toString
   
} // class HttpServiceInvokerConfig
