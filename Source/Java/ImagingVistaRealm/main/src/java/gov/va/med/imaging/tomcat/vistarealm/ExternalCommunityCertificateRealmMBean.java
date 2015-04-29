package gov.va.med.imaging.tomcat.vistarealm;

public interface ExternalCommunityCertificateRealmMBean
extends CertificateRealmMBean
{
	public abstract Integer getVistaPort();
	public abstract String getVistaServer();
	public abstract int getVistaConnectDelayKludge();

}
