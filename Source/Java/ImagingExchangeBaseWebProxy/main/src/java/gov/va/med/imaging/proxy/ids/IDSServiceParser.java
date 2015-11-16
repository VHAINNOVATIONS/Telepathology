/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 29, 2008
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
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
package gov.va.med.imaging.proxy.ids;

import java.io.IOException;
import java.io.InputStream;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.XMLReader;

/**
 * @author VHAISWWERFEJ
 *
 */
public class IDSServiceParser 
extends DefaultHandler 
{
//	private final static String IDSSERVICE_TAG_SERVICES = "services";
	private final static String IDSSERVICE_TAG_SERVICE = "Service";
	private final static String IDSSERVICE_TAG_APPLICATION_PATH = "ApplicationPath";
//	private final static String IDSSERVICE_TAG_METADATA_PATH = "MetadataPath";
//	private final static String IDSSERVICE_TAG_IMAGE_PATH = "ImagePath";
	private final static String IDSSERVICE_TAG_OPERATION_PATH = "OperationPath";
	private final static String IDSSERVICE_TAG_OPERATION = "Operation";
	
	private final static Logger logger = Logger.getLogger(IDSServiceParser.class);
	
	private String currentTag = "";
	private SortedSet<IDSService> services = null;
	private IDSService currentService = null;
	private IDSOperation currentOperation = null;	
	
	public IDSServiceParser()
	{
		super();
	}

	public SortedSet<IDSService> parse(InputStream inputStream)
	throws IOException
	{
		this.currentTag = "";
		services = new TreeSet<IDSService>();
		try
		{
			XMLReader xr = XMLReaderFactory.createXMLReader();
			xr.setContentHandler(this);
			xr.setErrorHandler(this);
			
			xr.parse(new InputSource(inputStream));
		}
		catch(SAXException saxX)
		{
			logger.error("Error parsing IDS service response", saxX);
			throw new IOException(saxX);
		}

		return services;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if(currentService != null)
		{
			if(IDSSERVICE_TAG_APPLICATION_PATH.equalsIgnoreCase(currentTag))
			{				
				currentService.setApplicationPath(new String(ch, start,length));
			}
			else if(IDSSERVICE_TAG_OPERATION_PATH.equalsIgnoreCase(currentTag))
			{
				currentOperation.setOperationPath(new String(ch, start, length));
			}
			/*
			else if(IDSSERVICE_TAG_IMAGE_PATH.equalsIgnoreCase(currentTag))
			{
				currentService.setImagePath(new String(ch, start,length));
			}
			else if(IDSSERVICE_TAG_METADATA_PATH.equalsIgnoreCase(currentTag))
			{
				currentService.setMetadataPath(new String(ch, start,length));
			}
			*/
		}
		//super.characters(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		currentTag = "";
		//super.endElement(uri, localName, name);
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException 
	{
		currentTag = name;
		
		
		if(IDSSERVICE_TAG_SERVICE.equals(name))
		{
			currentService = new IDSService();
			services.add(currentService);			
			String type = attributes.getValue("type");
			currentService.setApplicationType(type);
			String version = attributes.getValue("version");
			currentService.setVersion(version);			
		}		
		else if(IDSSERVICE_TAG_OPERATION.equals(name))
		{
			String operationType = attributes.getValue("type");
			
			currentOperation = new IDSOperation(operationType);
			currentService.addOperation(currentOperation);
		}
		//super.startElement(uri, localName, name, attributes);
	}

}
