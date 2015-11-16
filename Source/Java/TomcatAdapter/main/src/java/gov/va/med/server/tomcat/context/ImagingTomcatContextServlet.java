/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: February 21, 2006
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+
  */
package gov.va.med.server.tomcat.context;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;

import org.apache.catalina.ContainerServlet;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.Wrapper;


/**
 * Base Servlet that receives Tomcat context information when created.  This servlet must be
 * put into a web app with privileged access in order for this servlet to recieve context.
 * Also this servlet piece must be put into tomcat/server/lib while the actual web app can exist in webapps
 * 
 * @author VHAISWWERFEJ
 *
 */
public class ImagingTomcatContextServlet
extends HttpServlet
implements ContainerServlet
{
	private static final long serialVersionUID = 2605539246114859007L;
	
	private Wrapper wrapper = null;
	private Context context = null;
	private Host host = null;
	private Engine engine = null;
	
	@Override
	public Wrapper getWrapper() {
		return wrapper;
	}
	
	@Override
	public void setWrapper(Wrapper arg0) {
		wrapper = arg0;
		context = (Context) wrapper.getParent();
        host = (Host) context.getParent();
        engine = (Engine) host.getParent();
	}
	
	public void init() 
	throws ServletException
	{
		super.init();
		// Ensure that our ContainerServlet properties have been set
        if ((wrapper == null) || (context == null))
            throw new UnavailableException("Servlet not properly initialized");
	}
	
	public Context getContext() {
		return context;
	}
	
	public Host getHost() {
		return host;
	}
	
	public Engine getEngine() {
		return engine;
	}
}