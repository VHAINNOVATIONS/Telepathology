package gov.va.med.imaging.exchange.business.storage;

public class ArtifactInstanceProviderPair 
{
	private Provider Provider;
	private ArtifactInstance artifactInstance;

	public ArtifactInstanceProviderPair(Provider provider, ArtifactInstance artifactInstance) 
	{
		Provider = provider;
		this.artifactInstance = artifactInstance;
	}

	public Provider getProvider() {
		return Provider;
	}

	public void setProvider(Provider provider) {
		Provider = provider;
	}

	public ArtifactInstance getArtifactInstance() {
		return artifactInstance;
	}

	public void setArtifactInstance(ArtifactInstance artifactInstance) {
		this.artifactInstance = artifactInstance;
	}
}
