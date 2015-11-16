/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jan 20, 2010
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
package gov.va.med.imaging.health.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Parser to convert a VIX Server health XML response into the name/value properties of a VIX server health.
 * 
 * @author vhaiswwerfej
 *
 */
public class VixServerHealthXmlParser 
extends DefaultHandler 
{
	private final static String PROPERTY_TAG = "Property";
	
	private Map<String, String> properties = new HashMap<String, String>();
	
	private final static Logger logger = Logger.getLogger(VixServerHealthXmlParser.class);
	
	public VixServerHealthXmlParser()
	{
		super();
	}
	
	public Map<String, String> parse(InputStream inputStream)
	throws IOException
	{		
		try
		{
			XMLReader xr = XMLReaderFactory.createXMLReader();
			xr.setContentHandler(this);
			xr.setErrorHandler(this);
			
			xr.parse(new InputSource(inputStream));
		}
		catch(SAXException saxX)
		{
			logger.error("Error parsing VIX Server Health XML response", saxX);
			throw new IOException(saxX);
		}
		
		return properties;
	}	
	
	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException 
	{		
		if(PROPERTY_TAG.equals(name))
		{
			String propertyName = attributes.getValue("name");
			String propertyValue = attributes.getValue("value");
			this.properties.put(propertyName, propertyValue);
		}
	}
}
