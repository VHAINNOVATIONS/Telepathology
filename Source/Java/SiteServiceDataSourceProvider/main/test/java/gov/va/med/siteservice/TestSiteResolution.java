/**
 * 
 */
package gov.va.med.siteservice;

import gov.va.med.ProtocolHandlerUtility;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import java.lang.reflect.InvocationTargetException;
import org.junit.Ignore;

/**
 * @author vhaiswbeckec
 *
 */
@Ignore
public class TestSiteResolution
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		ProtocolHandlerUtility.initialize(true);
		SiteResolutionProvider provider = new SiteResolutionProvider();
		
		try
		{
			SiteResolver siteResolver = SiteResolver.create(provider.getInstanceConfiguration());
			
			ResolvedSite site200 = siteResolver.resolveSite("200");
			System.out.println("Site 200 - " + site200.toString());
			assert( site200.getArtifactSource() != null );
			assert( site200.getMetadataUrl("vista") != null );
			assert( site200.getSite() != null );
			
			ResolvedSite site660 = siteResolver.resolveSite("660");
			System.out.println("Site 660 - " + site660.toString());
			
			ResolvedSite site661 = siteResolver.resolveSite("661");
			System.out.println("Site 661 - " + site661.toString());
		}
		catch (IllegalArgumentException x)
		{
			x.printStackTrace();
		}
		catch (SecurityException x)
		{
			x.printStackTrace();
		}
		catch (InstantiationException x)
		{
			x.printStackTrace();
		}
		catch (IllegalAccessException x)
		{
			x.printStackTrace();
		}
		catch (InvocationTargetException x)
		{
			x.printStackTrace();
		}
		catch (NoSuchMethodException x)
		{
			x.printStackTrace();
		}
	}
}
