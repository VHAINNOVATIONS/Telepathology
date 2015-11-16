Much of the testing of cache implementation is done in the ImagingExchangeCacheTest project.
This is because of dependencies between projects required for testing that should not exist in deployment.  
Specifically ImagingExchangeCacheImpl is deployed as a context resource, not as part of the web application.  This is
somewhat like a database connection, which is defined in the application server and then referenced from a web application.
The ImagingCache project defines the API for the cache, the ImagingExchangeCache project basically provides proxies to the real cache.