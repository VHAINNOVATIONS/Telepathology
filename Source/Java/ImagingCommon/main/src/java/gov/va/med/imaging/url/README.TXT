The subpackages of this package are the URL handlers for the Vix.  All of the URL handlers should be in a commonly accessible
location because they are referenced from the common packages as well as the provider packages.
The Vix uses the protocol (the scheme of a URL) as the differentiator in selecting a data source provider.
Each type of remote server (data source) should have a unique protocol associated to it.  As of now the known
protocols are:
exchange: - for communication with the BIA
vftp: - for communication with another Vix (Vix Federation Transfer Protocol)
vista: - for communication with a VistA server
stsvc: - for communication with a site service server

Note that the actual communication protocol is up to the service provider itself.  For example communication to the BIA (the exchange
protocol) is over webservices (SOAP) over http.