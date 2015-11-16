package gov.va.med.imaging.federation.codegenerator.annotation.processor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Messager;

import freemarker.cache.TemplateLoader;

/**
 * 
 * @author vhaiswbeckec
 *
 */
class ClassLoaderTemplateLoader
implements TemplateLoader
{
	private final ClassLoader classLoader;
	private final Messager messager;
	
	private Map<URL, Reader> openTemplates;
	
	ClassLoaderTemplateLoader(ClassLoader classLoader, Messager messager)
	{
		this.classLoader = classLoader;
		this.messager = messager;
		
		openTemplates = new HashMap<URL, Reader>();
	}
	
	ClassLoader getClassLoader()
	{
		return this.classLoader;
	}

	/**
	 * @return the messager
	 */
	Messager getMessager()
	{
		return this.messager;
	}

	/* (non-Javadoc)
	 * @see freemarker.cache.TemplateLoader#findTemplateSource(java.lang.String)
	 */
	@Override
	public Object findTemplateSource(String name) 
	throws IOException
	{
		// dump the classpath to debug template loading problems
		//for(URL searchUrl : ((java.net.URLClassLoader)getClassLoader()).getURLs() )
		//	getMessager().printMessage(javax.tools.Diagnostic.Kind.NOTE, "ClassLoader '" + getClassLoader().getClass().getName() + 
		//			"', path '" + searchUrl.toString() + "'.");
		
		//name = "/" + name;
		URL resourceUrl = getClassLoader().getResource(name);

		if(resourceUrl == null)
			getMessager().printMessage(javax.tools.Diagnostic.Kind.WARNING, "Template '" + name + "' not found.");
		else
			getMessager().printMessage(javax.tools.Diagnostic.Kind.NOTE, "Using template '" + name + "' for router implementation template.");
		
		return resourceUrl;
	}

	/* (non-Javadoc)
	 * @see freemarker.cache.TemplateLoader#getLastModified(java.lang.Object)
	 */
	@Override
	public long getLastModified(Object templateSource)
	{
		URL resourceUrl = (URL)templateSource;
		
		return -1;
	}

	/* (non-Javadoc)
	 * @see freemarker.cache.TemplateLoader#getReader(java.lang.Object, java.lang.String)
	 */
	@Override
	public Reader getReader(Object templateSource, String encoding)
	throws IOException
	{
		URL resourceUrl = (URL)templateSource;
		URLConnection urlConnection = resourceUrl.openConnection();
		Reader reader = new InputStreamReader(urlConnection.getInputStream());
		
		synchronized (openTemplates)
		{
			openTemplates.put(resourceUrl, reader);
		}
		
		return reader;
	}
	
	/* (non-Javadoc)
	 * @see freemarker.cache.TemplateLoader#closeTemplateSource(java.lang.Object)
	 */
	@Override
	public void closeTemplateSource(Object templateSource)
	throws IOException
	{
		URL resourceUrl = (URL)templateSource;
		synchronized(openTemplates)
		{
			Reader reader = openTemplates.get(resourceUrl);
			if(reader != null)
			{
				openTemplates.remove(resourceUrl);
				reader.close();
			}
		}
	}
}