/*
 * Created on Mar 29, 2006
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


import java.util.Comparator;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public class CTParameterComparator implements Comparator<ParameterDeviceInfo> {

    /**
     * Constructor
     *
     * 
     */
    public CTParameterComparator() {
        super();
    }

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(ParameterDeviceInfo o1, ParameterDeviceInfo o2) {
        
        ParameterDeviceInfo device1 = o1;
        ParameterDeviceInfo device2 = o2;
        
        int objcomp = device1.getSiteID().compareToIgnoreCase(device2.getSiteID());
        if(objcomp == 0){
            objcomp = device1.getManufacturer().compareToIgnoreCase(device2.getManufacturer());
        }
        if(objcomp == 0){
            objcomp = device1.getModel().compareToIgnoreCase(device2.getModel());
        }
        if(objcomp == 0){
            objcomp = device2.getChangeDate().compareTo(device1.getChangeDate());
        }

        return objcomp;
    }

}
