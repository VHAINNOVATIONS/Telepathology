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

package gov.va.med.server;

/**
 * This interface defines the required methods for a server-agnostic
 * engine.  In Tomcat, an engine is declared in the server's configuration 
 * and lives as a top level element within the host.
 * 
 * @author vhaiswbeckec
 *
 */
public interface ServerAgnosticEngine
{
	/**
	 * Sets a reference to the server-specific engine implementation that serves as a
	 * proxy to this server-agnostic engine.  This method will be called before any
	 * lifecycle events are sent.
	 * 
	 * @param engineAdapter
	 */
	public void setServerAgnosticEngineAdapter(ServerAgnosticEngineAdapter engineAdapter);
	
	/**
	 * Notifies the server agnostic engine of server lifecycle events.
	 * 
	 * @param event
	 */
	public void serverEvent(ServerLifecycleEvent event);
}
