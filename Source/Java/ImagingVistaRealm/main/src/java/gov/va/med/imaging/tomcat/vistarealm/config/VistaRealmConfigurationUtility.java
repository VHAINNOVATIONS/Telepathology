/**
 * 
 */
package gov.va.med.imaging.tomcat.vistarealm.config;

import gov.va.med.imaging.tomcat.vistarealm.VistaAccessVerifyRealm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author VHAISWBECKEC
 *
 */
public class VistaRealmConfigurationUtility
{
	// define the command line
	private static final String VISTA_CONNECT_DELAY_KLUDGE = "VistaConnectDelayKludge";
	private static final String SECURITY_CONSTRAINT_CACHE_LIFESPAN = "SecurityConstraintCacheLifespan";
	private static final String REFRESH_SECURITY_CONSTRAINT_CACHE_ENTRY_ON_USE = "RefreshSecurityConstraintCacheEntryOnUse";
	private static final String USING_SECURITY_CONSTRAINT_CACHE = "UsingSecurityConstraintCache";
	private static final String PRINCIPAL_CACHE_LIFESPAN = "PrincipalCacheLifespan";
	private static final String REFRESH_PRINCIPAL_CACHE_ENTRY_ON_USE = "RefreshPrincipalCacheEntryOnUse";
	private static final String USING_PRINCIPAL_CACHE = "UsingPrincipalCache";
	private static final String LOCAL_VISTA_PORT = "LocalVistaPort";
	private static final String LOCAL_VISTA_SERVER = "LocalVistaServer";
	private static final String LOCAL_SITE_NUMBER = "LocalSiteNumber";
	private static final String LOCAL_SITE_ABBREVIATION = "LocalSiteAbbreviation";
	private static final String LOCAL_SITE_NAME = "LocalSiteName";
	
	private static Collection<CommandLineOption> commandLineDefinition = new ArrayList<CommandLineOption>();
	static
	{
		commandLineDefinition.add( new CommandLineOptionImpl(LOCAL_SITE_NAME, java.lang.String.class, true) );
		commandLineDefinition.add( new CommandLineOptionImpl(LOCAL_SITE_ABBREVIATION, java.lang.String.class, true) );
		commandLineDefinition.add( new CommandLineOptionImpl(LOCAL_SITE_NUMBER, java.lang.String.class, true) );
		commandLineDefinition.add( new CommandLineOptionImpl(LOCAL_VISTA_SERVER, java.lang.String.class, true) );
		commandLineDefinition.add( new CommandLineOptionImpl(LOCAL_VISTA_PORT, java.lang.Integer.class, true) );
		
		commandLineDefinition.add( new CommandLineOptionImpl(USING_PRINCIPAL_CACHE, java.lang.Boolean.class, false) );
		commandLineDefinition.add( new CommandLineOptionImpl(REFRESH_PRINCIPAL_CACHE_ENTRY_ON_USE, java.lang.Boolean.class, false) );
		commandLineDefinition.add( new CommandLineOptionImpl(PRINCIPAL_CACHE_LIFESPAN, java.lang.Long.class, false) );
		
		commandLineDefinition.add( new CommandLineOptionImpl(USING_SECURITY_CONSTRAINT_CACHE, java.lang.Boolean.class, false) );
		commandLineDefinition.add( new CommandLineOptionImpl(REFRESH_SECURITY_CONSTRAINT_CACHE_ENTRY_ON_USE, java.lang.Boolean.class, false) );
		commandLineDefinition.add( new CommandLineOptionImpl(SECURITY_CONSTRAINT_CACHE_LIFESPAN, java.lang.Long.class, false) );
	
		commandLineDefinition.add( new CommandLineOptionImpl(VISTA_CONNECT_DELAY_KLUDGE, java.lang.Integer.class, false) );
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		CommandLineParser commandLineParser = new CommandLineParser(commandLineDefinition);
		commandLineParser.parse(args, true);
		List<String> commandLineMessages = commandLineParser.getParseErrorMessages();
		
		// if the command line parser found errors then they will be in the
		// command line messages.  If the command line messages are null or
		// zero length - then no errors were found
		if(commandLineMessages != null && commandLineMessages.size() > 0)
		{
			for(String commandLineMessage : commandLineMessages)
				System.err.println(commandLineMessage);
		
			System.exit(-1);
		}
		
		// create a realm strictly to use the configuration storage mechanism
		// within it
		VistaAccessVerifyRealm realm = new VistaAccessVerifyRealm();
	
		// set all of the required parameters
		realm.setSiteName( (String)commandLineParser.getValue(LOCAL_SITE_NAME) );
		realm.setSiteAbbreviation( (String)commandLineParser.getValue(LOCAL_SITE_ABBREVIATION) );
		realm.setSiteNumber( (String)commandLineParser.getValue(LOCAL_SITE_NUMBER) );
		realm.setVistaServer( (String)commandLineParser.getValue(LOCAL_VISTA_SERVER) );
		realm.setVistaPort( (Integer)commandLineParser.getValue(LOCAL_VISTA_PORT) );
		
		// optional args
		if(commandLineParser.isExists(USING_PRINCIPAL_CACHE))
			realm.setUsingPrincipalCache( (Boolean)commandLineParser.getValue(USING_PRINCIPAL_CACHE));
		if(commandLineParser.isExists(REFRESH_PRINCIPAL_CACHE_ENTRY_ON_USE))
			realm.setUsingPrincipalCache( (Boolean)commandLineParser.getValue(REFRESH_PRINCIPAL_CACHE_ENTRY_ON_USE));
		if(commandLineParser.isExists(PRINCIPAL_CACHE_LIFESPAN))
			realm.setPrincipalCacheLifespan( (Long)commandLineParser.getValue(PRINCIPAL_CACHE_LIFESPAN) );
		
		// optional args
		if(commandLineParser.isExists(USING_SECURITY_CONSTRAINT_CACHE))
			realm.setUsingPrincipalCache( (Boolean)commandLineParser.getValue(USING_SECURITY_CONSTRAINT_CACHE));
		if(commandLineParser.isExists(REFRESH_SECURITY_CONSTRAINT_CACHE_ENTRY_ON_USE))
			realm.setUsingPrincipalCache( (Boolean)commandLineParser.getValue(REFRESH_SECURITY_CONSTRAINT_CACHE_ENTRY_ON_USE));
		if(commandLineParser.isExists(SECURITY_CONSTRAINT_CACHE_LIFESPAN))
			realm.setPrincipalCacheLifespan( (Long)commandLineParser.getValue(SECURITY_CONSTRAINT_CACHE_LIFESPAN) );
		
		// optional args
		if(commandLineParser.isExists(VISTA_CONNECT_DELAY_KLUDGE))
			realm.setVistaConnectDelayKludge( (Integer)commandLineParser.getValue(VISTA_CONNECT_DELAY_KLUDGE) );
		
		// set the exit value to the agreed on successful
		System.exit(0);
	}
}
