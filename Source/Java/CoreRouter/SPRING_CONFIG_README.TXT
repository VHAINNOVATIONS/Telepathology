Spring Configuration Notes:
1.) The Spring configuration in the CoreRouter project is unrelated to the Spring configuration in the facades.
2.) Core components are made available as JNDI registered objects through the CoreRouterFactory class.
3.) The CoreFactory class is registered as the object factory in the Tomcat server.xml configuration. 
4.) Facades acquire references to core components using JNDI naming.
    a.) Facades may use Spring and may use Spring JNDI lookup to get core component references but the Spring contexts (core and facade) are NOT connected.