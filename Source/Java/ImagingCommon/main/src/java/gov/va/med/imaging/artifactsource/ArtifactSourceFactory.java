/**
 * 
 */
package gov.va.med.imaging.artifactsource;

import java.lang.reflect.Constructor;
import org.apache.log4j.Logger;

/**
 * @author vhaiswbeckec
 *
 */
public class ArtifactSourceFactory
{
	private static Logger logger = Logger.getLogger(ArtifactSourceFactory.class);
	
	/**
	 * 
	 * @param memento
	 * @return
	 */
	public static ArtifactSource create(ArtifactSourceMemento memento)
	{
		if(memento == null || memento.getArtifactSourceClassName() == null)
			return null;
		
		try
		{
			Class<ArtifactSource> artifactSourceClass = 
				(Class<ArtifactSource>)Class.forName( memento.getArtifactSourceClassName() );
			Constructor constructor = artifactSourceClass.getConstructor(ArtifactSourceMemento.class);
			return (ArtifactSource)constructor.newInstance(memento);
		}
		catch (Exception x)
		{
			logger.error("Unable to create an ArtifactSource instance of type '" + memento.getArtifactSourceClassName() + "'.", x);
			return null;
		}
	}
}
