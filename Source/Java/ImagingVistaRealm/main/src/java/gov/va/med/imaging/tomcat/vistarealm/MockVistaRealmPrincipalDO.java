package gov.va.med.imaging.tomcat.vistarealm;

/**
* A simple class neede for XML serialization, the VistaRealmPrincipal class will
* not serialize and include its roles 
*/
public class MockVistaRealmPrincipalDO
{
	private String realm;
	private String access;
	private String verify;
	private String duz;
	private String fullname;
	private String ssn;
	private String siteNumber;
	private String sitename;
	private String[] roles;

	public MockVistaRealmPrincipalDO()
	{
	}
	
	public MockVistaRealmPrincipalDO(String realm, String access,
			String verify, String duz, String fullname, String ssn,
			String siteNumber, String sitename, String[] roles)
	{
		super();
		this.realm = realm;
		this.access = access;
		this.verify = verify;
		this.duz = duz;
		this.fullname = fullname;
		this.ssn = ssn;
		this.siteNumber = siteNumber;
		this.sitename = sitename;
		this.roles = roles;
	}

	public String getAccess()
	{
		return this.access;
	}

	public void setAccess(String access)
	{
		this.access = access;
	}

	public String getDuz()
	{
		return this.duz;
	}

	public void setDuz(String duz)
	{
		this.duz = duz;
	}

	public String getFullname()
	{
		return this.fullname;
	}

	public void setFullname(String fullname)
	{
		this.fullname = fullname;
	}

	public String getRealm()
	{
		return this.realm;
	}

	public void setRealm(String realm)
	{
		this.realm = realm;
	}

	public String[] getRoles()
	{
		return this.roles;
	}

	public void setRoles(String[] roles)
	{
		this.roles = roles;
	}

	public String getSitename()
	{
		return this.sitename;
	}

	public void setSitename(String sitename)
	{
		this.sitename = sitename;
	}

	public String getSiteNumber()
	{
		return this.siteNumber;
	}

	public void setSiteNumber(String siteNumber)
	{
		this.siteNumber = siteNumber;
	}

	public String getSsn()
	{
		return this.ssn;
	}

	public void setSsn(String ssn)
	{
		this.ssn = ssn;
	}

	public String getVerify()
	{
		return this.verify;
	}

	public void setVerify(String verify)
	{
		this.verify = verify;
	}
}
