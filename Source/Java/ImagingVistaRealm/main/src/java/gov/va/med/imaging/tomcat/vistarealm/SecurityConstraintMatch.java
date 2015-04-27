/**
 * 
 */
package gov.va.med.imaging.tomcat.vistarealm;

import org.apache.catalina.deploy.SecurityConstraint;

/**
 * A wrapper for SecurityConstraint (security-constraint elements in web.xml)
 * that maintains the matching url pattern and the type of match made to allow for
 * sorting by applicability to a URL.
 * 
 * @author VHAISWBECKEC
 *
 */
class SecurityConstraintMatch
implements Comparable
{
	private SecurityConstraint securityConstraint = null;
	private String urlPattern = null;
	private boolean exactMatch = false;
	private boolean pathMatch = false;
	private boolean extensionMatch = false;
	
	private SecurityConstraintMatch(
			SecurityConstraint securityConstraint, 
			String urlPattern, 
			boolean exactMatch, 
			boolean pathMatch, 
			boolean extensionMatch)
	{
		super();
		this.securityConstraint = securityConstraint;
		this.urlPattern = urlPattern;
		this.exactMatch = exactMatch;
		this.pathMatch = pathMatch;
		this.extensionMatch = extensionMatch;
	}

	static SecurityConstraintMatch createDefaultSecurityConstraintMatch(SecurityConstraint securityConstraint, String urlPattern)
	{
		return new SecurityConstraintMatch(securityConstraint, urlPattern, false, false, false);
	}
	static SecurityConstraintMatch createExactSecurityConstraintMatch(SecurityConstraint securityConstraint, String urlPattern)
	{
		return new SecurityConstraintMatch(securityConstraint, urlPattern, true, false, false);
	}
	static SecurityConstraintMatch createPathSecurityConstraintMatch(SecurityConstraint securityConstraint, String urlPattern)
	{
		return new SecurityConstraintMatch(securityConstraint, urlPattern, false, true, false);
	}
	static SecurityConstraintMatch createExtensionSecurityConstraintMatch(SecurityConstraint securityConstraint, String urlPattern)
	{
		return new SecurityConstraintMatch(securityConstraint, urlPattern, false, false, true);
	}
	
	public SecurityConstraint getSecurityConstraint()
	{
		return this.securityConstraint;
	}

	public String getUrlPattern()
	{
		return this.urlPattern;
	}

	public boolean isExactMatch()
	{
		return this.exactMatch;
	}

	public boolean isExtensionMatch()
	{
		return this.extensionMatch;
	}

	public boolean isPathMatch()
	{
		return this.pathMatch;
	}

	/*
	 * Compares this object with the specified object for order. 
	 * Returns a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
	 * 
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o)
	{
		if(o instanceof SecurityConstraintMatch)
		{
			SecurityConstraintMatch that = (SecurityConstraintMatch)o;
			// exact match always wins
			if(this.isExactMatch())
				return -1;
			if(that.isExactMatch())
				return 1;
			
			// path match takes precedence over extension match
			if(this.isPathMatch() && that.isExtensionMatch())
				return -1;
			if(this.isExtensionMatch() && that.isPathMatch())
				return 1;
			
			// if the patterns are both pattern match then the longer one wins
			if(this.isPathMatch() && that.isPathMatch())
				return that.getUrlPattern().length() - this.getUrlPattern().length();

			// if the patterns are both extension match then, again, the longer one wins
			// offhand I can't see how this will happen but might as well handle it
			if(this.isExtensionMatch() && that.isExtensionMatch())
				return that.getUrlPattern().length() - this.getUrlPattern().length();
			
			return 0;
		}
		return -1;
	}

	@Override
	public boolean equals(Object o)
	{
		if(o instanceof SecurityConstraintMatch)
		{
			SecurityConstraintMatch that = (SecurityConstraintMatch)o;
			return 
				this.isExactMatch() == that.isExactMatch() &&
				this.isPathMatch() == that.isPathMatch() &&
				this.isExtensionMatch() == that.isExtensionMatch() &&
				this.getSecurityConstraint().equals(that.getSecurityConstraint()) &&
				this.getUrlPattern().equals(that.getUrlPattern());
		}
		
		return false;
	}
	
}