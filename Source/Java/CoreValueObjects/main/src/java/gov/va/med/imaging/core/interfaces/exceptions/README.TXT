All of the exceptions referenced in the Router interface (the Vix) derive from the following base Exception derivations:
1.) ConfigurationError (unchecked exceptions)
  1.1.) InvalidComponentError
  1.2.) ComponentNotFoundError
  1.3.) ApplicationConfigurationError
2.) ConnectionException
  2.1.) ConnectionUnavailableException
  2.2.) CredentialsDeniedException
  2.3.) CompositeConnectionException - a composite of 1..n ConnectionException
3.) MethodException
  3.1.) LocalMethodException
  3.2.) RemoteMethodException
  3.3.) CompositeMethodException - a composite of 1..n MethodException

An exception may be marked as a TransientMethodException (by implementing that interface).
