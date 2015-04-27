package gov.va.med.imaging.tomcat.vistarealm;

/**
 * The required interface for a Site in the VistaRealm.
 * This is NOT the same as a Site in the Vix application, which has a lot more
 * information.
 * 
 * @author VHAISWBECKEC
 *
 */
public interface VistaRealmSite
{

	/**
	 * @return
	 */
	public abstract String getSiteAbbreviation();
	public abstract String getSiteName();
	public abstract String getSiteNumber();
	public abstract Integer getVistaPort();
	public abstract String getVistaServer();
}