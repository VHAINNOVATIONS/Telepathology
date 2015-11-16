package gov.va.med.imaging.datasource;

/**
 * 
 * @author VHAISWBECKEC
 * 
 */
public class ProviderService
implements Comparable<ProviderService>
{
	private final Provider provider;
	private final Class<?> spiType;
	private final String protocol;
	private final float protocolVersion;
	private final byte ordinal;
	private final Class<?> implementingClass;

	/**
	 * Versionable services must include a protocol and a protocol version
	 * 
	 * @param type
	 * @param protocol
	 * @param protocolVersion
	 * @param implementingClass
	 */
	public ProviderService(Provider provider, Class<?> spiType, String protocol, float protocolVersion, Class<?> implementingClass)
	{
		this.provider = provider;
		this.spiType = spiType;
		this.protocol = protocol;
		this.protocolVersion = protocolVersion;
		this.ordinal = 0;
		this.implementingClass = implementingClass;
	}

	/**
	 * Local services must specify an ordinal number, lower numbers are
	 * executed first.
	 * 
	 * @param type
	 * @param ordinal
	 * @param implementingClass
	 */
	public ProviderService(Provider provider, Class<?> spiType, byte ordinal, Class<?> implementingClass)
	{
		this.provider = provider;
		this.spiType = spiType;
		this.protocol = null;
		this.protocolVersion = 0.0f;
		this.ordinal = ordinal;
		this.implementingClass = implementingClass;
	}

	/**
	 * @return the provider
	 */
	public Provider getProvider()
	{
		return this.provider;
	}

	public Class<?> getSpiType()
	{
		return this.spiType;
	}

	public String getProtocol()
	{
		return protocol;
	}

	public float getProtocolVersion()
	{
		return protocolVersion;
	}

	/**
	 * Return the implementing service class
	 * 
	 * @return
	 */
	public Class<?> getImplementingClass()
	{
		return implementingClass;
	}

	/**
	 * Implement natural ordering as type, protocol and then protocol
	 * version using: 1.) ascending by type (DataSourceServices.compare),
	 * 2.) ascending by protocol (String.compare) 3.) descending by
	 * protoclVersion (mathematical comparison)
	 * 
	 * Sort descending by protocol version so that the newest versions come
	 * first.
	 */
	@Override
	public int compareTo(ProviderService that)
	{
		// if the classes are not the same then sort on the simple class name
		boolean spiTypeCompare = this.getSpiType().equals(that.getSpiType());
		if (!spiTypeCompare)
			return this.getSpiType().getSimpleName().compareTo(that.getSpiType().getSimpleName());

		if (this.protocol != null)
		{
			// JMW 4/7/2011 P104
			// if that has a null protocol then that is local, put this one first - they are not equal. 
			// This prevents NPE
			if(that.protocol == null)
				return 1;
			int protocolCompare = this.protocol.compareTo(that.protocol);
			if (protocolCompare != 0)
				return protocolCompare;

			float protocolVersionCompare = this.protocolVersion - that.protocolVersion;
			// reverse order by version so the highest version comes first
			// and the lowest version comes last
			// causes the ordering below
			return protocolVersionCompare < 0.0 ? 1 : protocolVersionCompare > 0.0 ? -1 : 0;
		}
		else
		{
			return (int) (this.ordinal - that.ordinal);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.getProtocol());
		sb.append('.');
		sb.append(this.getProtocolVersion());
		sb.append(':');
		sb.append(this.getSpiType().getSimpleName());
		sb.append('-');
		sb.append(this.getImplementingClass().getName());
		sb.append('(');
		sb.append(this.getProvider().toString());
		sb.append(')');
		
		return sb.toString();
	}
}