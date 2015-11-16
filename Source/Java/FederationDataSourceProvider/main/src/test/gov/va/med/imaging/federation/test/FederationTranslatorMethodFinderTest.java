/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 25, 2010
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
package gov.va.med.imaging.federation.test;

import java.util.List;
import java.util.SortedSet;

import gov.va.med.imaging.exchange.business.PatientSensitiveValue;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.vistarad.ActiveExams;
import gov.va.med.imaging.exchange.business.vistarad.Exam;
import gov.va.med.imaging.exchange.business.vistarad.ExamImages;
import gov.va.med.imaging.exchange.business.vistarad.PatientRegistration;
import gov.va.med.imaging.exchange.translation.AbstractTranslator;
import gov.va.med.imaging.exchange.translation.exceptions.TranslationException;

/**
 * @author vhaiswwerfej
 *
 */
public class FederationTranslatorMethodFinderTest 
extends FederationTestBase 
{
	static
	{
		AbstractTranslator.registerTranslatorClass(gov.va.med.imaging.federation.webservices.translation.v3.Translator.class);
	}
	
	public static int count = 0;
	
	public void testFindingTranslatorMethods()
	{
	
		/*
		findTranslator(ActiveExams.class, new gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadActiveExamsType());
		findTranslator(ExamImages.class, new gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamImagesType());
		findTranslator(Exam.class, new gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamType());
		*/
		
		findTranslator(PatientRegistration.class, new gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadPatientRegistrationType());
		//findTranslator(Study.class, new gov.va.med.imaging.federation.webservices.types.v3.FederationStudyType()); // breaks because object origin not set
		findTranslator(SortedSet.class, new gov.va.med.imaging.federation.webservices.types.v3.PatientType[1]);
		findTranslator(PatientSensitiveValue.class, new gov.va.med.imaging.federation.webservices.types.v3.PatientSensitiveCheckResponseType());
		
		//findTranslator(List.class, new gov.va.med.imaging.federation.webservices.types.v3.FederationVistaRadExamType [1]);
		
		System.out.println("Found '" + count + "' translations correctly.");
	}
	
	private <D extends Object> void  findTranslator(Class<D> destinationClass, Object ... source)
	{
		try
		{
			D result = AbstractTranslator.translate(destinationClass, source);
			if(result == null)
				fail("Got null result converting to type " + destinationClass + ".");
		}
		catch(TranslationException tX)
		{
			fail(tX.getMessage());
		}
		count++;
	}
	
	
	

}
