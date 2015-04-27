/**
 * 
 */
package gov.va.med.imaging.tomcat.vistarealm;

/**
 * @author vhaiswbeckec
 *
 */
public interface CertificateRealmMBean
extends AbstractVistaRealm
{
	public abstract String getServiceAccountUID();
	public abstract String getServiceAccountRoles();

}
