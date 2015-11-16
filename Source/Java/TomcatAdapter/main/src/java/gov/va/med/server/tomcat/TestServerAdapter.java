/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date May 12, 2010
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author vhaiswbeckec
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */

package gov.va.med.server.tomcat;

import java.security.Principal;
import org.apache.log4j.Logger;
import gov.va.med.server.ServerAgnosticEngine;
import gov.va.med.server.ServerAgnosticEngineAdapter;
import gov.va.med.server.ServerLifecycleEvent;

/**
 * This is a test implementation of a ServerAgnosticEngine, useful only for testing
 * the ServerAgnosticEngineAdapter.
 * 
 * @author vhaiswbeckec
 *
 */
public class TestServerAdapter
implements ServerAgnosticEngine
{
	private Logger logger = Logger.getLogger(TestServerAdapter.class);
	private ServerAgnosticEngineAdapter engineAdapter;
	
	@Override
	public void serverEvent(ServerLifecycleEvent event)
	{
		logger.info("Just got a " + event.toString() + " message.");
		
		Principal principal = this.engineAdapter.authenticate("boating1", "boating1.".getBytes());
		logger.info("Authenticated as '" + principal.toString() + "'.");
	}

	@Override
	public void setServerAgnosticEngineAdapter(ServerAgnosticEngineAdapter engineAdapter)
	{
		this.engineAdapter = engineAdapter;		
	}

}
