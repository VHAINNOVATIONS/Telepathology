/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 28, 2011
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

import org.junit.Test;

import gov.va.med.MediaType;
import gov.va.med.imaging.exchange.ImageAccessLogEvent.ImageAccessLogEventType;
import gov.va.med.imaging.exchange.business.PassthroughParameterType;
import gov.va.med.imaging.exchange.business.PatientSensitiveValue;
import gov.va.med.imaging.exchange.business.Patient.PatientSex;
import gov.va.med.imaging.exchange.enums.ArtifactResultErrorCode;
import gov.va.med.imaging.exchange.enums.ArtifactResultErrorSeverity;
import gov.va.med.imaging.exchange.enums.ArtifactResultStatus;
import gov.va.med.imaging.exchange.enums.ObjectStatus;
import gov.va.med.imaging.exchange.enums.PatientSensitivityLevel;
import gov.va.med.imaging.exchange.enums.StudyDeletedImageState;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.exchange.enums.vistarad.ExamStatus;
import gov.va.med.imaging.federation.rest.types.FederationArtifactResultErrorCodeType;
import gov.va.med.imaging.federation.rest.types.FederationArtifactResultErrorSeverityType;
import gov.va.med.imaging.federation.rest.types.FederationArtifactResultStatusType;
import gov.va.med.imaging.federation.rest.types.FederationExamStatusType;
import gov.va.med.imaging.federation.rest.types.FederationImageAccessLogEventTypeType;
import gov.va.med.imaging.federation.rest.types.FederationMediaType;
import gov.va.med.imaging.federation.rest.types.FederationObjectStatusType;
import gov.va.med.imaging.federation.rest.types.FederationPatientSensitiveType;
import gov.va.med.imaging.federation.rest.types.FederationPatientSensitivityLevelType;
import gov.va.med.imaging.federation.rest.types.FederationPatientSexType;
import gov.va.med.imaging.federation.rest.types.FederationRemoteMethodParameterTypeType;
import gov.va.med.imaging.federation.rest.types.FederationStudyDeletedImageStateType;
import gov.va.med.imaging.federation.rest.types.FederationStudyLoadLevelType;

import static org.junit.Assert.*;

/**
 * @author vhaiswwerfej
 *
 */
public class FederationRestEnumerationTranslatorTest
{

	@Test
	public void testEnumerations()
	{
		new FederationPatientSexEnumeration().testValues();
		new FederationImageAccessLogEventTypeEnumeration().testValues();
		new FederationStudyLoadLevelEnumeration().testValues();
		new FederationPassthroughParameterTypeEnumeration().testValues();
		new FederationExamStatusTypeEnumeration().testValues();
		new FederationPatientSensitivityLevelEnumeration().testValues();
		new FederationObjectStatusTypeEnumeration().testValues();
		new FederationStudyDeletedImageStateEnumeration().testValues();
		new FederationArtifactResultStatusTypeEnumeration().testValues();
		new FederationMediaTypeEnumeration().testValues();		
		new FederationArtifactResultErrorCodeTypeEnumeration().testValues();
		new FederationArtifactResultSeverityTypeEnumeration().testValues();

	}
	
	abstract class AbstractFederationEnumerationTester<E extends Enum<E>, F extends Enum<F>>
	{
		protected abstract E [] getValues();
		
		protected abstract F translateToFederation(E e);
		
		protected abstract E translateToEnum(F f);
		
		public void testValues()
		{
			int compareCount = 0;			
			for(E e : getValues())
			{
				F f = translateToFederation(e);
				E translatedE = translateToEnum(f);
				assertEquals(e, translatedE);
				compareCount++;
			}
			assertNotSame(0, compareCount);
			//System.out.println("Made '" + compareCount + "' comparisons for enumeration.");
		}
	}
	
	class FederationPatientSexEnumeration 
	extends AbstractFederationEnumerationTester<PatientSex, FederationPatientSexType>
	{

		@Override
		protected PatientSex[] getValues()
		{
			return PatientSex.values();
		}

		@Override
		protected PatientSex translateToEnum(FederationPatientSexType f)
		{
			return FederationRestTranslator.translate(f);
		}

		@Override
		protected FederationPatientSexType translateToFederation(PatientSex e)
		{
			return FederationRestTranslator.translate(e);
		}		
	}
	
	class FederationImageAccessLogEventTypeEnumeration
	extends AbstractFederationEnumerationTester<ImageAccessLogEventType, FederationImageAccessLogEventTypeType>
	{

		@Override
		protected ImageAccessLogEventType[] getValues()
		{
			return ImageAccessLogEventType.values();
		}

		@Override
		protected ImageAccessLogEventType translateToEnum(
				FederationImageAccessLogEventTypeType f)
		{
			return FederationRestTranslator.translate(f);
		}

		@Override
		protected FederationImageAccessLogEventTypeType translateToFederation(
				ImageAccessLogEventType e)
		{
			return FederationRestTranslator.translate(e);
		}		
	}
	
	class FederationStudyLoadLevelEnumeration
	extends AbstractFederationEnumerationTester<StudyLoadLevel, FederationStudyLoadLevelType>
	{

		@Override
		protected StudyLoadLevel[] getValues()
		{
			return StudyLoadLevel.values();
		}

		@Override
		protected StudyLoadLevel translateToEnum(FederationStudyLoadLevelType f)
		{
			return FederationRestTranslator.translate(f);
		}

		@Override
		protected FederationStudyLoadLevelType translateToFederation(
				StudyLoadLevel e)
		{
			return FederationRestTranslator.translate(e);
		}		
	}
	
	class FederationPassthroughParameterTypeEnumeration
	extends AbstractFederationEnumerationTester<PassthroughParameterType, FederationRemoteMethodParameterTypeType>
	{

		@Override
		protected PassthroughParameterType[] getValues()
		{
			return PassthroughParameterType.values();
		}

		@Override
		protected PassthroughParameterType translateToEnum(
				FederationRemoteMethodParameterTypeType f)
		{
			return FederationRestTranslator.translate(f);
		}

		@Override
		protected FederationRemoteMethodParameterTypeType translateToFederation(
				PassthroughParameterType e)
		{
			return FederationRestTranslator.translate(e);
		}		
	}
	
	class FederationExamStatusTypeEnumeration
	extends AbstractFederationEnumerationTester<ExamStatus, FederationExamStatusType>
	{

		@Override
		protected ExamStatus[] getValues()
		{
			return ExamStatus.values();
		}

		@Override
		protected ExamStatus translateToEnum(FederationExamStatusType f)
		{
			return FederationRestTranslator.translate(f);
		}

		@Override
		protected FederationExamStatusType translateToFederation(ExamStatus e)
		{
			return FederationRestTranslator.translate(e);
		}
		
	}
	
	class FederationPatientSensitivityLevelEnumeration
	extends AbstractFederationEnumerationTester<PatientSensitivityLevel, FederationPatientSensitivityLevelType>
	{

		@Override
		protected PatientSensitivityLevel[] getValues()
		{
			return PatientSensitivityLevel.values();
		}

		@Override
		protected PatientSensitivityLevel translateToEnum(
				FederationPatientSensitivityLevelType f)
		{
			FederationPatientSensitiveType fpst = 
				new FederationPatientSensitiveType();
			fpst.setSensitiveLevel(f);
			return FederationRestTranslator.translate(fpst).getSensitiveLevel();
		}

		@Override
		protected FederationPatientSensitivityLevelType translateToFederation(
				PatientSensitivityLevel e)
		{
			PatientSensitiveValue psv = new PatientSensitiveValue(e, null);
			FederationPatientSensitiveType fpst = FederationRestTranslator.translate(psv);
			return fpst.getSensitiveLevel();
		}		
	}
	
	class FederationObjectStatusTypeEnumeration
	extends AbstractFederationEnumerationTester<ObjectStatus, FederationObjectStatusType>
	{

		@Override
		protected ObjectStatus[] getValues()
		{
			return ObjectStatus.values();
		}

		@Override
		protected ObjectStatus translateToEnum(FederationObjectStatusType f)
		{
			return FederationRestTranslator.translate(f);
		}

		@Override
		protected FederationObjectStatusType translateToFederation(
				ObjectStatus e)
		{
			return FederationRestTranslator.translate(e);
		}
	}
	
	class FederationStudyDeletedImageStateEnumeration
	extends AbstractFederationEnumerationTester<StudyDeletedImageState, FederationStudyDeletedImageStateType>
	{

		@Override
		protected StudyDeletedImageState[] getValues()
		{
			return StudyDeletedImageState.values();
		}

		@Override
		protected StudyDeletedImageState translateToEnum(
				FederationStudyDeletedImageStateType f)
		{
			return FederationRestTranslator.translate(f);
		}

		@Override
		protected FederationStudyDeletedImageStateType translateToFederation(
				StudyDeletedImageState e)
		{
			return FederationRestTranslator.translate(e);
		}		
	}
	
	class FederationArtifactResultStatusTypeEnumeration
	extends AbstractFederationEnumerationTester<ArtifactResultStatus, FederationArtifactResultStatusType>
	{

		@Override
		protected ArtifactResultStatus[] getValues()
		{
			return ArtifactResultStatus.values();
		}

		@Override
		protected ArtifactResultStatus translateToEnum(
				FederationArtifactResultStatusType f)
		{
			return FederationRestTranslator.translate(f);
		}

		@Override
		protected FederationArtifactResultStatusType translateToFederation(
				ArtifactResultStatus e)
		{
			return FederationRestTranslator.translate(e);
		}		
	}
	
	class FederationMediaTypeEnumeration
	extends AbstractFederationEnumerationTester<MediaType, FederationMediaType>
	{

		@Override
		protected MediaType[] getValues()
		{
			return MediaType.values();
		}

		@Override
		protected MediaType translateToEnum(FederationMediaType f)
		{
			return FederationRestTranslator.translate(f);
		}

		@Override
		protected FederationMediaType translateToFederation(MediaType e)
		{
			return FederationRestTranslator.translate(e);
		}		
	}
	
	class FederationArtifactResultErrorCodeTypeEnumeration
	extends AbstractFederationEnumerationTester<ArtifactResultErrorCode, FederationArtifactResultErrorCodeType>
	{

		@Override
		protected ArtifactResultErrorCode[] getValues()
		{
			return ArtifactResultErrorCode.values();
		}

		@Override
		protected ArtifactResultErrorCode translateToEnum(
				FederationArtifactResultErrorCodeType f)
		{
			return FederationRestTranslator.translate(f);
		}

		@Override
		protected FederationArtifactResultErrorCodeType translateToFederation(
				ArtifactResultErrorCode e)
		{
			return FederationRestTranslator.translate(e);
		}
	}
	
	class FederationArtifactResultSeverityTypeEnumeration
	extends AbstractFederationEnumerationTester<ArtifactResultErrorSeverity, FederationArtifactResultErrorSeverityType>
	{

		@Override
		protected ArtifactResultErrorSeverity[] getValues()
		{
			return ArtifactResultErrorSeverity.values();
		}

		@Override
		protected ArtifactResultErrorSeverity translateToEnum(
				FederationArtifactResultErrorSeverityType f)
		{
			return FederationRestTranslator.translate(f);
		}

		@Override
		protected FederationArtifactResultErrorSeverityType translateToFederation(
				ArtifactResultErrorSeverity e)
		{
			return FederationRestTranslator.translate(e);
		}
		
	}
	
	
}
