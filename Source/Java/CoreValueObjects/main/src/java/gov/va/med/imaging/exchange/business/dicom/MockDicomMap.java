/*
 * Created on Mar 18, 2005
// Per VHA Directive 2004-038, this routine should not be modified.
//+---------------------------------------------------------------+
//| Property of the US Government.                                |
//| No permission to copy or redistribute this software is given. |
//| Use of unreleased versions of this software requires the user |
//| to execute a written test agreement with the VistA Imaging    |
//| Development Office of the Department of Veterans Affairs,     |
//| telephone (301) 734-0100.                                     |
//|                                                               |
//| The Food and Drug Administration classifies this software as  |
//| a medical device.  As such, it may not be changed in any way. |
//| Modifications to this software may result in an adulterated   |
//| medical device under 21CFR820, the use of which is considered |
//| to be a violation of US Federal Statutes.                     |
//+---------------------------------------------------------------+
 *
 */
package gov.va.med.imaging.exchange.business.dicom;


/**
 * @author Csaba Titton
 *
 * This is a support class for holding one DICOM map entry at a time
 * for legacy as well as testing purposes in the ImagingDicomDCF project
 * (see DicomDataSetImpl and DicomConfigurationFacade there);
 * also can be used for test feeding (see MockDicomConfigurationFacade)
 * instead of having the ConfigurationFacade mapping persistence hooked
 */
public class MockDicomMap extends DicomMap {

	/**
	 * 
	 */
	public MockDicomMap( String tagName, String tag2Name, String className,
			                 String fieldName, int fieldMultiplicity) {
		
		this.setId(1);
		this.setMessageType("");//("C-STORE");
		this.setSOPClass("");
		this.setTag(tagName);
		this.setTag2(tag2Name);
		this.setImagingEntity(className);
		this.setEntityField(fieldName);
		this.setFieldMultiplicity(fieldMultiplicity);
		this.setDirection("");
	}
}
