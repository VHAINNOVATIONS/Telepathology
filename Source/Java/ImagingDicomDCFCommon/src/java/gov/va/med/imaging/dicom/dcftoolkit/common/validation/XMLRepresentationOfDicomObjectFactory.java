/* Copyright (c) 2001-2006, David A. Clunie DBA Pixelmed Publishing. All rights reserved. */

package gov.va.med.imaging.dicom.dcftoolkit.common.validation;

import gov.va.med.imaging.HexDump;
import gov.va.med.imaging.dicom.dcftoolkit.common.impl.DicomDataSetImpl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.lbs.DCS.AttributeTag;
import com.lbs.DCS.DCSException;
import com.lbs.DCS.DicomDataDictionary;
import com.lbs.DCS.DicomDataSet;
import com.lbs.DCS.DicomElement;
import com.lbs.DCS.DicomFileInput;
import com.lbs.DCS.DicomSQElement;

/**
 * <p>A class to encode a representation of a DICOM object in an XML form,
 * suitable for analysis as human-readable text, or for feeding into an
 * XSLT-based validator, and to convert them back again.</p>
 *
 * <p>An example of the type of output produced by this class is as follows:</p>
 * <pre>
&lt;?xml version="1.0" encoding="UTF-8"?&gt;
  &lt;DicomObject&gt;
    &lt;FileMetaInformationGroupLength element="0000" group="0002" vr="UL"&gt;
      &lt;value number="1"&gt;222&lt;/value&gt;
    &lt;/FileMetaInformationGroupLength&gt;
    ...
    &lt;ImageType element="0008" group="0008" vr="CS"&gt;
      &lt;value number="1"&gt;ORIGINAL&lt;/value&gt;
      &lt;value number="2"&gt;PRIMARY&lt;/value&gt;
      &lt;value number="3"&gt;CINE&lt;/value&gt;
      &lt;value number="4"&gt;NONE&lt;/value&gt;
    &lt;/ImageType&gt;
    ...
    &lt;ContrastBolusAgentSequence element="0012" group="0018" vr="SQ"&gt;
      &lt;Item number="1"&gt;
        &lt;CodeValue element="0100" group="0008" vr="SH"&gt;
          &lt;value number="1"&gt;C-17800&lt;/value&gt;
        &lt;/CodeValue&gt;
      ...
      &lt;/Item&gt;
    &lt;/ContrastBolusAgentSequence&gt;
    ...
    &lt;PixelData element="0010" group="7fe0" vr="OW"/&gt;
&lt;/DicomObject&gt;
 * </pre>
 *
 * <p>There are a number of characteristics of this form of output:</p>
 *
 * <ul>
 * <li>Rather than a generic name for all DICOM data elements, like "element", with an attribute to provide the human-readable name,
 *     the name of the XML element itself is a human-readable keyword, as used in the DICOM Data Dictionary for the toolkit; the
 *     group and element tags are available as attributes of each such element; this makes construction of XPath accessors more straightforward.</li>
 * <li>The value representation of the DICOM source element is conveyed explicitly in an attribute; this facilitates validation of the XML result
 *     (e.g., that the correct VR has been used, and that the values are compatible with that VR).</li>
 * <li>Individual values of a DICOM data element are expressed as separate XML elements (named "value"), each with an attribute ("number") to specify their order, starting from 1 increasing by 1;
 *     this prevents users of the XML form from needing to parse multiple string values and separate out the DICOM value delimiter (backslash), and allows
 *     XPath accessors to obtain specific values; it also allows for access to separate values of binary, rather than string, DICOM data elements, which
 *     are represented the same way. Within each "value" element, the XML plain character data contains a string representation of the value.</li>
 * <li>Sequence items are encoded in a similar manner to multi-valued attributes, i.e., there is a nested XML data element (called "Item") with an
 *     explicit numeric attribute ("number") to specify their order, starting from 1 increasing by 1.</li>
 * </ul>
 *
 * <p> E.g., to test if an image is original, which is determined by a specific value of <code>ImageType (0008,0008)</code>, one
 * could write in XPath <code>"/DicomObject/ImageType/value[@number=1] = 'ORIGINAL'"</code>. To get the code value of the contrast
 * agent in use, one could write <code>"/DicomObject/ContrastBolusAgentSequence/Item[@number=1]/CodeValue/value[@number=1]"</code>,
 * or making some assumptions about cardinality and depth of nesting and removing the predicates, simply <code>"//ContrastBolusAgentSequence/Item/CodeValue/value"</code>. One could do this from the command
 * line with a utility such as {@link com.pixelmed.utils.XPathQuery XPathQuery}.</p>
 *
 * <p>Note that a round trip from DICOM to XML and back again does not
 * result in full fidelity, since:</p>
 *
 * <ul>
 * <li>Binary floating point values will lose precision when converted to string representation and back again</li>
 * <li>Leading and trailing white space and control characters in strings will be discarded</li>
 * <li>Meta information header elements will be changed</li>
 * <li>Structural elements such as group lengths will be removed and may or may not be replaced</li>
 * <li>Physical offsets such as in the DICOMDIR will be invalidated</li>
 * <li>Attributes with OB and OW value representations have their values discarded so as not to encode the bulk pixel data (probably should be added as an option)</li>
 * </ul>
 *
 * <p>A typical example of how to invoke this class to convert DICOM to XML would be:</p>
 * <pre>
try {
    AttributeList list = new AttributeList();
    list.read("dicomfile",null,true,true);
    Document document = new XMLRepresentationOfDicomObjectFactory().getDocument(list);
    XMLRepresentationOfDicomObjectFactory.write(System.out,document);
} catch (Exception e) {
    e.printStackTrace(System.err);
 }
 * </pre>
 *
 * <p>or even simpler, if there is no further use for the XML document:</p>
 * <pre>
try {
    AttributeList list = new AttributeList();
    list.read("dicomfile",null,true,true);
    XMLRepresentationOfDicomObjectFactory.createDocumentAndWriteIt(list,System.out);
} catch (Exception e) {
    e.printStackTrace(System.err);
 }
 * </pre>
 *
 * <p>A typical example of converting XML back to DICOM would be:</p>
 * <pre>
try {
    AttributeList list = new XMLRepresentationOfDicomObjectFactory().getAttributeList("xmlfile");
    list.write(System.out,TransferSyntax.ExplicitVRLittleEndian,true,true);
} catch (Exception e) {
    e.printStackTrace(System.err);
 }
 * </pre>
 *
 * <p>or if you need to handle the meta information properly:</p>
 * <pre>
try {
    AttributeList list = new XMLRepresentationOfDicomObjectFactory().getAttributeList("xmlfile");
    String sourceApplicationEntityTitle = Attribute.getSingleStringValueOrEmptyString(list,TagFromName.SourceApplicationEntityTitle);
    list.removeMetaInformationHeaderAttributes();
    FileMetaInformation.addFileMetaInformation(list,TransferSyntax.ExplicitVRLittleEndian,sourceApplicationEntityTitle);
    list.write(System.out,TransferSyntax.ExplicitVRLittleEndian,true,true);
} catch (Exception e) {
    e.printStackTrace(System.err);
 }
 * </pre>
 *
 * @see com.pixelmed.dicom.XMLRepresentationOfStructuredReportObjectFactory
 * @see com.pixelmed.utils.XPathQuery
 * @see org.w3c.dom.Document
 *
 * @author	dclunie
 */
public class XMLRepresentationOfDicomObjectFactory {

	//private static final String identString = "@(#) $Header: /cvs/ImagingDicomDCFCommon/src/java/gov/va/med/imaging/dicom/dcftoolkit/common/validation/XMLRepresentationOfDicomObjectFactory.java,v 1.5 2010/11/02 14:18:20 vhaiswpeterb Exp $";

	/***/
	private DocumentBuilder db;
	
    private static Logger logger = Logger.getLogger(XMLRepresentationOfDicomObjectFactory.class);
	
	/**
	 * @param	tag
	 */
	private String makeElementNameFromHexadecimalGroupElementValues(AttributeTag tag) {
		StringBuffer str = new StringBuffer();
		str.append("HEX");		// XML element names not allowed to start with a number
		String groupString = Integer.toHexString(tag.group());
		for (int i=groupString.length(); i<4; ++i) str.append("0");
		str.append(groupString);
		String elementString = Integer.toHexString(tag.element());
		for (int i=elementString.length(); i<4; ++i) str.append("0");
		str.append(elementString);
		return str.toString();
	}


	/**
	 * @param	list
	 * @param	document
	 * @param	parent
	 */
	void addDicomElementsFromListToNode(DicomDataSet list,Document document,Node parent) {
		//DicomDataDictionary dictionary = DicomDataDictionary.instance();
		//Iterator i = list.values().iterator();
		//while (i.hasNext()) {
		int total = list.count();
		for(int i=0; i<total; i++){
			DicomElement element = list.getElementAt(i);
			AttributeTag tag = element.tag();
			if(!((tag.group() == 0x7FE0) && (tag.element() == 0x0010))){
				String elementName;
				try{
					elementName = DicomDataDictionary.getElementShortName(tag);
				}
				catch(DCSException dcsX){
					elementName=makeElementNameFromHexadecimalGroupElementValues(tag);				
				}
				if ((elementName == null) || (elementName.equals("???"))){
					elementName=makeElementNameFromHexadecimalGroupElementValues(tag);
				}
				Node node = document.createElement(elementName);
				parent.appendChild(node);
			
				{
					Attr attr = document.createAttribute("group");
					attr.setValue(HexDump.shortToPaddedHexString(tag.group()));
					node.getAttributes().setNamedItem(attr);
				}
				{
					Attr attr = document.createAttribute("element");
					attr.setValue(HexDump.shortToPaddedHexString(tag.element()));
					node.getAttributes().setNamedItem(attr);
				}
				{
					Attr attr = document.createAttribute("vr");
					try{
						attr.setValue(DicomDataDictionary.getVRString(DicomDataDictionary.getElementVR(tag)));
						node.getAttributes().setNamedItem(attr);
					}
					catch(DCSException dcsX){
					
					}
				}	
				if (element instanceof DicomSQElement) {
				
					//Iterator si = ((DicomSQElement)element).iterator();
					DicomSQElement sqElement = (DicomSQElement)element;
					DicomDataSet[] sqDDSs = sqElement.getSQData();
					int seqTotal = sqDDSs.length;
					//while (si.hasNext()) {
					for(int j=0; j<seqTotal; j++){
						DicomDataSet item = sqDDSs[j];
						Node itemNode = document.createElement("Item");
						Attr numberAttr = document.createAttribute("number");
						numberAttr.setValue(Integer.toString(j+1));
						itemNode.getAttributes().setNamedItem(numberAttr);
						node.appendChild(itemNode);
						//addDicomElementsFromListToNode(item.getDicomDataSet(),document,itemNode);
						addDicomElementsFromListToNode(item,document,itemNode);
					}
				}
				else {
				
					ArrayList<String> values = new ArrayList<String>();
					for(int k=0; k<element.vm(); k++){
						try {
							values.add(element.getStringValue(k));
						}
						catch (DCSException e) {
							e.printStackTrace(System.err);
						}
					}
				
					if (!values.isEmpty()) {
						for (int l=0; l<values.size(); ++l) {
							Node valueNode = document.createElement("value");
							Attr numberAttr = document.createAttribute("number");
							numberAttr.setValue(Integer.toString(l+1));
							valueNode.getAttributes().setNamedItem(numberAttr);
							valueNode.appendChild(document.createTextNode(values.get(l).trim()));
							node.appendChild(valueNode);
						}
					}
				}
			}
		}
	}

	/**
	 * <p>Construct a factory object, which can be used to get XML documents from DICOM objects.</p>
	 *
	 * @exception	ParserConfigurationException
	 */
	public XMLRepresentationOfDicomObjectFactory() throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		db = dbf.newDocumentBuilder();
	}
	
	/**
	 * <p>Given a DICOM object encoded as a list of DicomElements, get an XML document
	 * as a DOM tree.</p>
	 *
	 * @param	list	the list of DICOM DicomElements
	 */
	public Document getDocument(DicomDataSet list) {
		Document document = db.newDocument();
		org.w3c.dom.Node element = document.createElement("DicomObject");
		document.appendChild(element);
		addDicomElementsFromListToNode(list,document,element);
		return document;
	}
	
	
	/**
	 * @param	node
	 * @param	indent
	 */
	public static String toString(Node node,int indent) {
		StringBuffer str = new StringBuffer();
		for (int i=0; i<indent; ++i) str.append("    ");
		str.append(node);
		if (node.hasAttributes()) {
			NamedNodeMap attrs = node.getAttributes();
			for (int j=0; j<attrs.getLength(); ++j) {
				Node attr = attrs.item(j);
				//str.append(toString(attr,indent+2));
				str.append(" ");
				str.append(attr);
			}
		}
		str.append("\n");
		++indent;
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
			str.append(toString(child,indent));
			//str.append("\n");
		}
		return str.toString();
	}
	
	/**
	 * @param	node
	 */
	public static String toString(Node node) {
		return toString(node,0);
	}
	
	/**
	 * <p>Serialize an XML document (DOM tree).</p>
	 *
	 * @param	out		the output stream to write to
	 * @param	document	the XML document
	 * @exception	IOException
	 */
	public static void write(OutputStream out,Document document) throws IOException, TransformerConfigurationException, TransformerException {
		
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(out);
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		Properties outputProperties = new Properties();
		outputProperties.setProperty(OutputKeys.METHOD,"xml");
		outputProperties.setProperty(OutputKeys.INDENT,"yes");
		outputProperties.setProperty(OutputKeys.ENCODING,"UTF-8");	// the default anyway
		transformer.setOutputProperties(outputProperties);
		transformer.transform(source, result);
	}
	
	/**
	 * <p>Serialize an XML document (DOM tree) created from a DICOM DicomElement list.</p>
	 *
	 * @param	list		the list of DICOM DicomElements
	 * @param	out		the output stream to write to
	 * @exception	IOException
	 * @exception	DCSException
	 */
	public static void createDocumentAndWriteIt(DicomDataSet list,OutputStream out) throws IOException, DCSException {
		try {
			Document document = new XMLRepresentationOfDicomObjectFactory().getDocument(list);
			write(out,document);
		}
		catch (ParserConfigurationException e) {
			throw new DCSException("Could not create XML document - problem creating object model from DICOM"+e);
		}
		catch (TransformerConfigurationException e) {
			throw new DCSException("Could not create XML document - could not instantiate transformer"+e);
		}
		catch (TransformerException e) {
			throw new DCSException("Could not create XML document - could not transform to XML"+e);
		}
	}
		
	/**
	 * <p>Read a DICOM dataset and write an XML representation of it to the standard output, or vice versa.</p>
	 *
	 * @param	arg	either one filename of the file containing the DICOM dataset, or a direction argument (toDICOM or toXML, case insensitive) and an input filename
	 */
	public static void main(String arg[]) {
		try {
			boolean bad = true;
			boolean toXML = true;
			String filename = null;
			if (arg.length == 1) {
				bad = false;
				toXML = true;
				filename = arg[0];
			}
			else if (arg.length == 2) {
				filename = arg[1];
				if (arg[0].toLowerCase().equals("toxml")) {
					bad = false;
					toXML = true;
				}
				else if (arg[0].toLowerCase().equals("todicom") || arg[0].toLowerCase().equals("todcm")) {
					bad = false;
					toXML = false;
				}
			}
			if (bad) {
				System.err.println("usage: XMLRepresentationOfDicomObjectFactory [toDICOM|toXML] inputfile");
			}
			else {
				if (toXML) {
					
					DicomDataSet list;
					DicomFileInput input = new DicomFileInput(arg[1]);
					input.open();
					list = input.readDataSet();

					//System.err.println("making document");
					Document document = new XMLRepresentationOfDicomObjectFactory().getDocument(list);
					//System.err.println(toString(document));
					FileOutputStream fos = new FileOutputStream("DCMObject.xml");
					write(fos,document);
					fos.close();
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}
}

