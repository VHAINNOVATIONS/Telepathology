/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 23, 2011
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
  Description: 

        ;; +--------------------------------------------------------------------+
        ;; Property of the US Government.
        ;; No permission to copy or redistribute this software is given.
        ;; Use of unreleased versions of this software requires the user
        ;;  to execute a written test agreement with the VistA Imaging
        ;;  Development Office of the Department of Veterans Affairs,
        ;;  telephone (301) 734-0100.
        ;;
        ;; The Food and Drug Administration classifies this software as
        ;; a Class II medical device.  As such, it may not be changed
        ;; in any way.  Modifications to this software may result in an
        ;; adulterated medical device under 21CFR820, the use of which
        ;; is considered to be a violation of US Federal Statutes.
        ;; +--------------------------------------------------------------------+

 */
package gov.va.med.imaging.exchange.business;

import gov.va.med.OID;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ArtifactSourceMemento;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.* ;

/**
 * @author vhaiswwerfej
 *
 */
public abstract class AbstractCloneTest
{

	protected <T extends Object> void compareObjects(T obj1, T obj2, String [] ignoreMethods)
	throws InvocationTargetException, IllegalAccessException
	{
		
		Method [] methods = obj1.getClass().getMethods();
		for(Method method : methods)
		{
			String methodName = method.getName();
			if((methodName.startsWith("get")) || (methodName.startsWith("is")))
			{
				boolean isIgnoreMethod = false;
				for(String ignoreMethod : ignoreMethods)
				{
					if(ignoreMethod.equalsIgnoreCase(methodName))
					{
						isIgnoreMethod = true;
						break;
					}
				}
				if(!isIgnoreMethod)
				{
					System.out.println("Invoking method '" + methodName + "'.");
					Object study1Value = method.invoke(obj1, null);
					Object study2Value = method.invoke(obj2, null);
					assertEquals("Method '" + methodName + "' values do not match", study1Value, study2Value);
				}
				else
				{
					System.out.println("Skipping method '" + methodName + "'");
				}
			}
		}
	}
	
	class TestSite 
	implements Site
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = 367478391755009092L;

		@Override
		public Iterator<URL> artifactIterator()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public RoutingToken createRoutingToken()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getArtifactServerCount()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public List<URL> getArtifactUrls()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public URL getAvailableArtifactServer(String protocol)
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public URL getAvailableMetadataServer(String protocol)
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public OID getHomeCommunityId()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getIdentifier()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ArtifactSourceMemento getMemento()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getMetadataServerCount()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public List<URL> getMetadataUrls()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getName()
		{
			return "Test Site Name";
		}

		@Override
		public String getRepositoryId()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isDodDocument()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isDodRadiology()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isRepresents(OID homeCommunityId, String repositoryId)
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isVaDocument()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isVaRadiology()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Iterator<URL> metadataIterator()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getAcceleratorPort()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String getAcceleratorServer()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getRegionId()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getSiteAbbr()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getSiteName()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getSiteNumber()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getVistaPort()
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public String getVistaServer()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean hasAcceleratorServer()
		{
			// TODO Auto-generated method stub
			return false;
		}

		/* (non-Javadoc)
		 * @see gov.va.med.imaging.exchange.business.Site#isSiteUserAuthenticatable()
		 */
		@Override
		public boolean isSiteUserAuthenticatable()
		{
			// TODO Auto-generated method stub
			return false;
		}

		/* (non-Javadoc)
		 * @see gov.va.med.imaging.exchange.business.Site#isSitePatientLookupable()
		 */
		@Override
		public boolean isSitePatientLookupable()
		{
			// TODO Auto-generated method stub
			return false;
		}

		/* (non-Javadoc)
		 * @see gov.va.med.imaging.exchange.business.Site#getSiteConnections()
		 */
		@Override
		public Map<String, SiteConnection> getSiteConnections()
		{
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
