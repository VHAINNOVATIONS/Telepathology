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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import gov.va.med.imaging.dicom.dcftoolkit.common.impl.DicomDataSetImpl;
import gov.va.med.imaging.exchange.business.dicom.SOPInstance;
import gov.va.med.imaging.exchange.business.dicom.rdsr.CTDose;
import gov.va.med.imaging.exchange.business.dicom.rdsr.Dose;
import gov.va.med.imaging.exchange.business.dicom.rdsr.FluoroDose;

import com.lbs.DCS.DicomDataSet;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;

public class RDSRProcessor
{
    private static Logger logger = Logger.getLogger(RDSRProcessor.class);
	private static Logger radDosageLogger = Logger.getLogger("RadDosage");

    private TinkerGraph graph;
    private DicomDataSetImpl dataSetImpl;
    
	public RDSRProcessor(DicomDataSetImpl dataSetImpl)
	{
		super();
		graph = new TinkerGraph();
		this.dataSetImpl = dataSetImpl;
	}
	
	public List<Dose> getDose(SOPInstance sopInstance)
	{
		RDSRParser parser = new RDSRParser(graph, dataSetImpl);
		
		radDosageLogger.info("Parsing the DICOM header and building the object graph...");
		parser.parseRDSR();

		// Create the doseList, and append CTDose and XRayDose objects to it.
		List<Dose> doseList = new ArrayList<Dose>();
		extractCTDoses(doseList);
		extractFluoroDoses(doseList, sopInstance);

		return doseList;
	}


	private void extractCTDoses(List<Dose> doseList)
	{
		radDosageLogger.info("Checking for CT Dose using the template identifier...");

		// Get the list of CT dose template root nodes. First try to find the root nodes 
		// using the template identifier.
		Iterable<Vertex> doseTemplates = graph.getVertices("TemplateIdentifier", "10013");

		// If we can't find the template identifier, look for the "CT Acquisition" node directly, to
		// see if we can find some dose data anyway.
		if (doseTemplates == null || !doseTemplates.iterator().hasNext())
		{
			radDosageLogger.info("No data found using the template identifer. Looking for CT Acquisition nodes instead.");
			doseTemplates = graph.getVertices("ConceptNameCodeSequence_CodeValue", "113819");
		}
		

		if (doseTemplates.iterator().hasNext())
		{
			int counter = 1;
			radDosageLogger.info("Found CT dose data. Details to follow...");
			// Iterate over each of the root vertices to extract the dose data
			for (Vertex doseTemplate : doseTemplates)
			{
				CTDoseTemplate ctDoseTemplate = new CTDoseTemplate(doseTemplate);
				
				// Create the CTDose business object, and populate it via the dose template.
				CTDose ctDose = new CTDose();
				ctDose.setIrradiationEventUid(ctDoseTemplate.getIrradiationEventUid());
				ctDose.setMeanCTDIvol(ctDoseTemplate.getMeanCTDIvol());
				ctDose.setDlp(ctDoseTemplate.getDlp());
				ctDose.setPhantomType(ctDoseTemplate.getPhantomType());
				ctDose.setAnatomicTargetRegion(ctDoseTemplate.getAnatomicTargetRegion());
				
				// Add the CTDose object to the list
				doseList.add(ctDose);
				radDosageLogger.info("\n* CT dose object " + counter + ":\n" + ctDose.toString());
				counter++;
			}
		}
		else
		{
			radDosageLogger.info("No CT dose data found in this DICOM object.");
		}
	}

	private void extractFluoroDoses(List<Dose> doseList, SOPInstance sopInstance)
	{
		radDosageLogger.info("Checking for X-Ray (Fluoro) Dose data using the template identifier...");

		// Get the list of fluoro dose template root nodes. First try to find the root nodes 
		// using the template identifier.
		Iterable<Vertex> doseTemplates = graph.getVertices("TemplateIdentifier", "10004");
		
		// If we can't find the template identifier, look for the "Accumulated X-Ray Dose Data" node directly, to
		// see if we can find some dose data anyway.
		if (doseTemplates == null || !doseTemplates.iterator().hasNext())
		{
			radDosageLogger.info("No data found using the template identifer. Looking for Accumulated X-Ray Dose Data nodes instead.");
			doseTemplates = graph.getVertices("ConceptNameCodeSequence_CodeValue", "113702");
		}
		
		// Iterate over each of the root vertices to extract the dose data
		if (doseTemplates.iterator().hasNext())
		{
			int counter = 1;
			radDosageLogger.info("Found X-Ray (Fluoro) dose data. Details to follow...");
			for (Vertex doseTemplate : doseTemplates)
			{
				FluoroDoseTemplate fluoroDoseTemplate = new FluoroDoseTemplate(doseTemplate);
				
				// Create the CTDose business object, and populate it via the dose template.
				FluoroDose fluoroDose = new FluoroDose();
				fluoroDose.setIrradiationEventUid(sopInstance.getSOPInstanceUID());
				fluoroDose.setDoseRpTotal(fluoroDoseTemplate.getDoseRpTotal());
				fluoroDose.setDoseAreaProductTotal(fluoroDoseTemplate.getDoseAreaProductTotal());
				fluoroDose.setFluoroTimeTotal(fluoroDoseTemplate.getFluoroTimeTotal());
				fluoroDose.setFluoroDoseRpTotal(fluoroDoseTemplate.getFluoroDoseRpTotal());
				fluoroDose.setFluoroDoseAreaProductTotal(fluoroDoseTemplate.getFluoroDoseAreaProductTotal());
				fluoroDose.setCineDoseRpTotal(fluoroDoseTemplate.getCineDoseRpTotal());
				fluoroDose.setCineDoseAreaProductTotal(fluoroDoseTemplate.getCineDoseAreaProductTotal());
				fluoroDose.setCineTime(fluoroDoseTemplate.getCineTime());
				
				// Add the CTDose object to the list
				doseList.add(fluoroDose);
				radDosageLogger.info("\n* X-Ray (Fluoro) dose object " + counter + ":\n" + fluoroDose.toString());
				counter++;
			}
		}
		else
		{
			radDosageLogger.info("No X-Ray (Fluoro) dose data found in this DICOM object.");
		}
	}
}
