package gov.va.med.imaging.tomcat.vistarealm;

/**
 * 
 * @author VHAISWBECKEC
 *
 */
public interface VistaRealm
extends AbstractVistaRealm
{

	/**
	 * @see gov.va.med.imaging.tomcat.vistarealm.VistaRealmSite#getVistaPort()
	 */
	public abstract Integer getVistaPort();

	/**
	 * @see gov.va.med.imaging.tomcat.vistarealm.VistaRealmSite#getVistaServer()
	 */
	public abstract String getVistaServer();

	/**
	 * Set the vista connect delay kludge to something between 0 and 3000
	 * milliseconds. Values outside that range will be forced into that range.
	 * 
	 * @param vistaConnectDelayKludge
	 */
	public abstract int getVistaConnectDelayKludge();
	public abstract void setVistaConnectDelayKludge(int kludge);
}