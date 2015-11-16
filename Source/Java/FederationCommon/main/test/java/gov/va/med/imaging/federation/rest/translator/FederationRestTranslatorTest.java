/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 25, 2011
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
package gov.va.med.imaging.federation.rest.translator;

import java.util.Iterator;
import java.util.TreeSet;

import gov.va.med.imaging.exchange.ImageAccessLogEvent;
import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.exchange.business.Patient;
import gov.va.med.imaging.exchange.business.Series;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.documents.Document;
import gov.va.med.imaging.exchange.business.documents.DocumentSet;
import gov.va.med.imaging.exchange.business.documents.DocumentSetResult;
import gov.va.med.imaging.exchange.business.documents.test.DocumentBusinessObjectBuilder;
import gov.va.med.imaging.exchange.business.vistarad.Exam;
import gov.va.med.imaging.exchange.business.vistarad.ExamImage;
import gov.va.med.imaging.exchange.business.vistarad.test.VistaRadBusinessObjectBuilder;
import gov.va.med.imaging.federation.rest.types.FederationDocumentSetResultType;
import gov.va.med.imaging.federation.rest.types.FederationExamImageType;
import gov.va.med.imaging.federation.rest.types.FederationExamType;
import gov.va.med.imaging.federation.rest.types.FederationImageAccessLogEventType;
import gov.va.med.imaging.federation.rest.types.FederationImageType;
import gov.va.med.imaging.federation.rest.types.FederationPatientType;
import gov.va.med.imaging.federation.rest.types.FederationStudyType;
import gov.va.med.imaging.test.ObjectComparer;
import gov.va.med.imaging.translator.test.TranslatorTestBusinessObjectBuilder;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author vhaiswwerfej
 *
 */
public class FederationRestTranslatorTest
{
	@Test
	public void testStudyTranslation()
	{
		try
		{
			Site site = TranslatorTestBusinessObjectBuilder.createSite();
			Study study = TranslatorTestBusinessObjectBuilder.createStudy(site);
			FederationStudyType studyType = FederationRestTranslator.translate(study);
			Study translatedStudy = FederationRestTranslator.translate(studyType);
			String [] ignoreMethods = {"getKeys"};
			ObjectComparer.compareObjects(study, translatedStudy, ignoreMethods);		
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			fail(ex.getMessage());
		}
	}
	
	@Test
	public void testImageTranslation()
	throws Exception
	{
		Site site = TranslatorTestBusinessObjectBuilder.createSite();
		Study study = TranslatorTestBusinessObjectBuilder.createStudy(site);
		Series series = TranslatorTestBusinessObjectBuilder.createSeries(study, site);		
		Image image = TranslatorTestBusinessObjectBuilder.createImage(study, site, series);
		FederationImageType imageType = FederationRestTranslator.translate(image);
		Image translatedImage = FederationRestTranslator.translate(imageType);
		String [] ignoreMethods = {"getObjectOrigin", "getPatientDFN"}; // these methods are not passed across Federation
		ObjectComparer.compareObjects(image, translatedImage, ignoreMethods);
	}
	
	@Test
	public void testExamTranslation()
	throws Exception
	{
		Exam exam = VistaRadBusinessObjectBuilder.createExam();
		FederationExamType examType = FederationRestTranslator.translate(exam);
		Exam translatedExam = FederationRestTranslator.translate(examType);
		String [] ignoreMethods = {};
		ObjectComparer.compareObjects(exam, translatedExam, ignoreMethods);		
	}
	
	@Test
	public void testExamImageTranslation()
	throws Exception
	{
		ExamImage examImage = VistaRadBusinessObjectBuilder.createExamImage();
		FederationExamImageType examImageType = FederationRestTranslator.translate(examImage);
		ExamImage translatedExamImage = FederationRestTranslator.translate(examImageType);
		String [] ignoreMethods = {"getPatientName"};
		ObjectComparer.compareObjects(examImage, translatedExamImage, ignoreMethods);		
	}
	
	@Test	
	public void testDocumentSetTranslation()
	throws Exception
	{		
		DocumentSet documentSet = DocumentBusinessObjectBuilder.createDocumentSet();
		java.util.SortedSet<DocumentSet> documentSets = new TreeSet<DocumentSet>();
		documentSets.add(documentSet);
		DocumentSetResult documentSetResult = DocumentSetResult.createFullResult(documentSets);
		FederationDocumentSetResultType documentSetResultType = 
			FederationRestTranslator.translate(documentSetResult);
		DocumentSetResult translatedDocumentSetResult = FederationRestTranslator.translate(documentSetResultType);
		Iterator<DocumentSet> translatedDocumentSetIterator = translatedDocumentSetResult.getArtifacts().iterator();
		Iterator<DocumentSet> documentSetIterator = documentSetResult.getArtifacts().iterator();
		while(documentSetIterator.hasNext())
		{
			DocumentSet ds = documentSetIterator.next();
			DocumentSet translatedDs = translatedDocumentSetIterator.next();
			compareDocumentSets(ds, translatedDs);			
		}		
	}
	
	private void compareDocumentSets(DocumentSet ds1, DocumentSet ds2)
	throws Exception
	{
		String [] ignoreMethods = {""};
		ObjectComparer.compareObjects(ds1, ds2, ignoreMethods);
		Iterator<Document> documentIter1 = ds1.iterator();
		Iterator<Document> documentIter2 = ds2.iterator();
		while(documentIter1.hasNext())
		{
			Document doc1 = documentIter1.next();
			Document doc2 = documentIter2.next();
			String [] documentIgnoreMethods = {""};
			ObjectComparer.compareObjects(doc1, doc2, documentIgnoreMethods);
		}
	}
	
	@Test
	public void testPatientTranslation()
	throws Exception
	{
		Patient patient = TranslatorTestBusinessObjectBuilder.createPatient();
		FederationPatientType patientType = FederationRestTranslator.translate(patient);
		Patient translatedPatient = FederationRestTranslator.translate(patientType);
		String [] ignoreMethods = {""};
		ObjectComparer.compareObjects(patient, translatedPatient, ignoreMethods);
	}
	
	@Test
	public void testImageAccessLogEvent()
	throws Exception
	{
		ImageAccessLogEvent imageAccessLogEvent = 
			TranslatorTestBusinessObjectBuilder.createImageAccessLogEvent();
		FederationImageAccessLogEventType imageAccessLogEventType = 
			FederationRestTranslator.translate(imageAccessLogEvent);
		ImageAccessLogEvent translatedImageAccessLogEvent = 
			FederationRestTranslator.translate(imageAccessLogEventType);
		String [] ignoreMethods = {"getPatientDfn", "getImageAccessEventTime"}; // not passed across Federation
		ObjectComparer.compareObjects(imageAccessLogEvent, translatedImageAccessLogEvent, 
				ignoreMethods);		
	}
	
	
}
