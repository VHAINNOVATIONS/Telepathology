/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date May 2, 2013
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author vhaiswlouthj
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */
package gov.va.med.imaging.dicom.dcftoolkit.common.impl.rdsr;

import org.apache.log4j.Logger;

import gov.va.med.imaging.dicom.dcftoolkit.common.impl.DicomDataSetImpl;

import com.lbs.DCS.AttributeTag;
import com.lbs.DCS.DCSException;
import com.lbs.DCS.DicomDataDictionary;
import com.lbs.DCS.DicomDataSet;
import com.lbs.DCS.DicomElement;
import com.lbs.DCS.DicomSQElement;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

public class RDSRParser
{
    private static Logger logger = Logger.getLogger(RDSRParser.class);

    public static final String CODE_VALUE_TAG = "0008,0100";
    public static final String CODING_SCHEME_DESIGNATOR_TAG = "0008,0102";
    public static final String CODE_MEANING_TAG = "0008,0104";
    public static final String RELATIONSHIP_TAG = "0040,a010";
    public static final String CONCEPT_NAME_CODE_SEQ_TAG = "0040,a043";
    public static final String CONCEPT_CODE_SEQ_TAG = "0040,a168";
    public static final String MEASURED_VALUE_SEQ_TAG = "0040,a300";
    public static final String MEASUREMENT_UNITS_SEQ_TAG = "0040,08ea";
    public static final String CONTENT_TEMPLATE_SEQ_TAG = "0040,a504";

	private TinkerGraph graph;
	private DicomDataSet dataSet;
	
	private int vertexId = 1;

	public RDSRParser(TinkerGraph graph, DicomDataSetImpl dataSetImpl)
	{
		super();
		this.graph = graph;
		this.dataSet = (DicomDataSet)dataSetImpl.getDicomDataSet();
	}
	
	public Vertex parseRDSR()
	{
		// Create a new Root vertex for the SR object
		Vertex rootVertex = graph.addVertex(vertexId++);
		
		try
		{

			// Add properties to the root vertex of the SR object
			rootVertex.setProperty("Key", "1");

			// Find the element containing the detailed report data
			DicomSQElement reportSequence = (DicomSQElement) dataSet.findElement(new AttributeTag("0040, a730"));

			// Recursively process this report sequence to build the graph
			processSequence(rootVertex, reportSequence, "1", 0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return rootVertex;
		
	}
	
	private void processSequence(Vertex parentVertex, DicomSQElement sequenceElement, String parentVertexKey, int level) throws Exception
	{
		DicomDataSet[] dataSets = sequenceElement.getSQData();
		int vertexCountAtThisLevel = 1;

		String sequenceTag = getTag(sequenceElement);
		String sequenceName = getTagKeyword(sequenceElement);
		
		for (DicomDataSet dataSet : dataSets)
		{
			if (sequenceTag.equals(CONCEPT_NAME_CODE_SEQ_TAG))
			{
				processEmbeddedSequence(parentVertex, sequenceElement);
			}
			else if (sequenceTag.equals(CONCEPT_CODE_SEQ_TAG))
			{
				processEmbeddedSequence(parentVertex, sequenceElement);
			}
			else if (sequenceTag.equals(MEASURED_VALUE_SEQ_TAG))
			{
				processMeasuredValueSequence(parentVertex, sequenceElement);
			}
			else if (sequenceTag.equals(CONTENT_TEMPLATE_SEQ_TAG))
			{
				processContentTemplateSequence(parentVertex, sequenceElement);
			}
			else
			{
				// Create a vertex for this data set
				Vertex currentVertex = graph.addVertex(vertexId++);
				String currentVertexKey = "";

				// Loop over the elements in the data set.
				for (int i = 0; i < dataSet.count(); i++)
				{
					DicomElement element = dataSet.getElementAt(i);

					String elementName = getTagKeyword(element);

					// If this is the first element in the dataset, create the
					// relationship based on whether or not an explicit
					// relationship
					// tag is defined
					if (i == 0)
					{
						// This is the first element of the sequence. If it's a
						// relationship type, create the specified relationship
						// to the parent. If it's NOT a relationship, then
						// create an IMPLICIT relationship to the parent.
						if (getTag(element).equals(RELATIONSHIP_TAG))
						{
							// Since the first element is an explicit
							// relationship, relate it to its parent with the
							// given relationship type.
							parentVertex.addEdge(SRRelationshipTypes.getType(element.getStringValue()), currentVertex);
							currentVertexKey = parentVertexKey + "." + vertexCountAtThisLevel++;
							currentVertex.setProperty("key", currentVertexKey);

						}
						else
						{
							// Since the first element is not a relationship,
							// relate it to its parent with an implicit
							// relationship, and process the element as normal.
							parentVertex.addEdge(SRRelationshipTypes.Implicit, currentVertex);
							processElement(currentVertex, element, currentVertexKey, level, elementName);
						}
					}
					else
					{
						processElement(currentVertex, element, currentVertexKey, level, elementName);
					}
				}
			}
		}
	}

		private void processEmbeddedSequence(Vertex vertex, DicomSQElement sequenceElement) throws Exception
	{
		DicomDataSet[] dataSets = sequenceElement.getSQData();
		if (dataSets.length == 1)
		{
			DicomDataSet dataSet = dataSets[0];
			for (int i = 0; i < dataSet.count(); i++)
			{
				DicomElement element = dataSet.getElementAt(i);
				vertex.setProperty(getTagKeyword(sequenceElement, element), element.getStringValue().trim());
			}
		}
		else
		{
			throw new Exception("Multiple datasets detected in sequence: " + getTagKeyword(sequenceElement));
		}
	}

	private void processMeasuredValueSequence(Vertex vertex, DicomSQElement sequenceElement) throws Exception
	{
		DicomDataSet[] dataSets = sequenceElement.getSQData();
		if (dataSets.length == 1)
		{
			DicomDataSet dataSet = dataSets[0];
			for (int i = 0; i < dataSet.count(); i++)
			{
				DicomElement element = dataSet.getElementAt(i);
				if (getTag(element).equals(MEASUREMENT_UNITS_SEQ_TAG))
				{
					processMeasurementUnitsSequence(vertex, (DicomSQElement)element);
				}
				else if (element instanceof DicomSQElement)
				{
					throw new Exception ("Unexpected Sequence in Measured Value Sequence: " + getTag(element));
				}
				else
				{
					String canonicalElementName = getTagKeyword(element);
					vertex.setProperty(canonicalElementName, element.getStringValue().trim());
				}
			}
		}
		else
		{
			throw new Exception("Multiple datasets detected in a Measured Value Sequence");
		}
	}

	private void processMeasurementUnitsSequence(Vertex vertex, DicomSQElement sequenceElement) throws Exception
	{
		DicomDataSet[] dataSets = sequenceElement.getSQData();
		if (dataSets.length == 1)
		{
			DicomDataSet dataSet = dataSets[0];
			for (int i = 0; i < dataSet.count(); i++)
			{
				DicomElement element = dataSet.getElementAt(i);
				
				if (element instanceof DicomSQElement)
				{
					throw new Exception ("Unexpected Sequence in MeasurementUnitsSequence: " + getTag(element));
				}
				else
				{
					String elementName = getTagKeyword(element);
					vertex.setProperty(elementName, element.getStringValue().trim());
				}
			}
		}
		else
		{
			throw new Exception("Multiple datasets detected in a Measurement Units Sequence");
		}
	}

	private void processContentTemplateSequence(Vertex vertex, DicomSQElement sequenceElement) throws Exception
	{
		DicomDataSet[] dataSets = sequenceElement.getSQData();
		if (dataSets.length == 1)
		{
			DicomDataSet dataSet = dataSets[0];
			for (int i = 0; i < dataSet.count(); i++)
			{
				DicomElement element = dataSet.getElementAt(i);
				
				if (element instanceof DicomSQElement)
				{
					throw new Exception ("Unexpected Sequence in Content Template Sequence: " + getTag(element));
				}
				else
				{
					String elementName = getTagKeyword(element);
					vertex.setProperty(elementName, element.getStringValue().trim());
				}
			}
		}
		else
		{
			throw new Exception("Multiple datasets detected in a Content Template Sequence");
		}
	}

	private void processElement(Vertex currentVertex, DicomElement element, String currentVertexKey, int level, String elementName) throws Exception
	{
		if (element instanceof DicomSQElement)
		{
			processSequence(currentVertex, (DicomSQElement) element, currentVertexKey, level + 1);
		}
		else
		{
			currentVertex.setProperty(elementName, element.getStringValue().trim());
		}
	}
	
	protected String getIndentation(int level) 
	{
		String indentationString = "";

		for (int i=0; i<level; i++)
		{
			indentationString += "..";
		}
		
		return indentationString;
	}
	
	protected String getTag(DicomElement element)
	{
		return element.tag().toString().replace(" ", "").trim();
	}
	
	private String getTagKeyword(DicomElement sequenceElement, DicomElement element) throws DCSException
	{
		String tagKeyword = DicomDataDictionary.getElementName(sequenceElement.tag());
		if (element != null)
		{
			tagKeyword = tagKeyword + "_" + DicomDataDictionary.getElementName(element.tag());
		}

		tagKeyword = tagKeyword.replace(" ", "");
		return tagKeyword;
	}

	private String getTagKeyword(DicomElement sequenceElement) throws DCSException
	{
		return getTagKeyword(sequenceElement, null);
	}

	
}
