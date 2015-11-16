/*
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

import java.io.Serializable;

/**
 * @author Csaba Titton
 *
 *
 */
public class DicomMap implements Serializable
{
	private static final long serialVersionUID = 4278178819381588795L;
	// Fields
	private int id;					// Entity id and DB Visit table record ID
	private String messageType;		// max 16 char.
	private String SOPClass;		// max 64 chars, DICOM SOP Class UID
	private String tag;				// max 10 char. hex string
	private String tag2;			// max 10 char. hex string
	private String imagingEntity;	// max 32 entity name
	private String entityField;		// max 32 entity field name
	private int fieldMultiplicity;	// 1 normal conversion to from one DB field, 2.. split conv.
	private String direction;		// max 6 ('IN', 'OUT', 'INOUT' or NULL)

	//
	// Relationships: None
	//

	//
	// Fields
	//
	/**
	 * 
	 * @return Returns the id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id The id to set.
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * 
	 * 
	 * @return Returns the messageType.
	 */
	public String getMessageType() {
		return messageType;
	}
	/**
	 * @param messageType The messageType to set.
	 */
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	/**
	 * 
	 * 
	 * @return Returns the theSOPClass.
	 */
	public String getSOPClass() {
		return SOPClass;
	}
	/**
	 * @param theSOPClass The theSOPClass to set.
	 */
	public void setSOPClass(String SOPClass) {
		this.SOPClass = SOPClass;
	}

	/**
	 * 
	 * 
	 * @return Returns the tag.
	 */
	public String getTag() {
		return tag;
	}
	/**
	 * @param tag The tag to set.
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * 
	 * 
	 * @return Returns the tag.
	 */
	public String getTag2() {
		return tag2;
	}
	/**
	 * @param tag The tag to set.
	 */
	public void setTag2(String tag2) {
		this.tag2 = tag2;
	}

	/**
	 * 
	 * 
	 * @return Returns the imagingEntity.
	 */
	public String getImagingEntity() {
		return imagingEntity;
	}
	/**
	 * @param imagingEntity The imagingEntity to set.
	 */
	public void setImagingEntity(String imagingEntity) {
		this.imagingEntity = imagingEntity;
	}

	/**
	 * 
	 * 
	 * @return Returns the entityField.
	 */
	public String getEntityField() {
		return entityField;
	}
	/**
	 * @param entityField The entityField to set.
	 */
	public void setEntityField(String entityField) {
		this.entityField = entityField;
	}

	/**
	 * 
	 * 
	 * @return Returns the fieldMultplicity.
	 */
	public int getFieldMultiplicity() {
		return fieldMultiplicity;
	}
	/**
	 * @param entityField The entityField to set.
	 */
	public void setFieldMultiplicity(int fieldMultiplicity) {
		this.fieldMultiplicity = fieldMultiplicity;
	}

	/**
	 * 
	 * 
	 * @return Returns the direction.
	 */
	public String getDirection() {
		return direction;
	}
	/**
	 * @param direction The direction to set.
	 */
	public void setDirection(String direction) {
		this.direction = direction;
	}

}
