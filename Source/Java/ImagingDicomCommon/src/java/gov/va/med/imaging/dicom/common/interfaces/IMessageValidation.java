/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: March 25, 2005
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWPETRB
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
package gov.va.med.imaging.dicom.common.interfaces;

import java.util.ArrayList;

/**
 *
 * Interface.  Add each violation recieved to an Array List.  The List stores Objects representing
 * the offending Dicom Element.  Generally, the Object may contain the
 * offending group/element, the value, and the violation reason. However, there may be
 * additional information as determined by the implementation.  It is up to the implementation
 * to determine the fields within the Objects stored in the List.</p>
 *  
 * Keeps track of the highest violation level set while creating the List.
 * The user can receive the List for their own descretion.</p>
 * 
 * This is in the Generic Dicom layer for use by any Dicom toolkit. The Implementation should
 * take place at the Dicom Toolkit layer.</p>
 * 
 *
 * @author William Peterson
 *
 */
public interface IMessageValidation {
    /**
     * Get the current validation violation level.  This represents the highest level set.
     * 
     * @return violation level of type integer.
     */
    public abstract int getViolationLevel();

    /**
     * Add Violation information to the List.
     * 
     * @param group Represents the violating Dicom Group.
     * @param element Represents the violating Dicom Element.
     * @param level Represents the violation level.
     * @param reason Represents the reason given for the violation.
     */
    public abstract void addViolation(int group, int element, int level,
            String reason);

    /**
     * Add Violation information to the List.  
     * 
     * @param group Represents the violating Dicom Group.
     * @param element Represents the violating Dicom Element.
     * @param level Represents the violation level.
     * @param reason Represents the reason given for the violation.
     */
    public abstract void AddViolation(String group, String element, int level,
            String reason);

    /**
     * Add Violation information to the List.
     * 
     * @param dicomTag Represents the violating Dicom Group/Element.
     * @param level Represents the violation level.
     * @param reason Represents the reason given for the violation.
     */
    public abstract void AddViolation(String dicomTag, int level, String reason);

    /**
     * Returns ArrayList containing all Violations.
     * 
     * @return ArrayList
     */
    public abstract ArrayList getViolations();

    public static final int NORMAL = 1;

    public static final int WARNING = 2;

    public static final int VIOLATION = 3;
}