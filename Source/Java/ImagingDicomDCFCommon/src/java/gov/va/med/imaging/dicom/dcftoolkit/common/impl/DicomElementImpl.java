/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: March 23, 2005
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWLOUTHJ
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
package gov.va.med.imaging.dicom.dcftoolkit.common.impl;


import gov.va.med.imaging.dicom.common.Constants;
import gov.va.med.imaging.dicom.common.interfaces.IDicomElement;

import org.apache.log4j.Logger;

import com.lbs.DCS.AttributeTag;
import com.lbs.DCS.DCM;
import com.lbs.DCS.DCSException;
import com.lbs.DCS.DicomDataSet;
import com.lbs.DCS.DicomElement;
import com.lbs.DCS.DicomSQElement;
import com.lbs.DCS.DicomStringElement;

/**
 * @author jon
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DicomElementImpl implements IDicomElement {

	/**************************************************************************
	 * Static fields and initializer
	 *************************************************************************/
	private static Logger logger = Logger.getLogger(DicomElementImpl.class);
	
	/**************************************************************************
	 * Member variables
	 *************************************************************************/
	private DicomElement element = null;
	
	/**************************************************************************
	 * Constructors
	 *************************************************************************/
	/**
	 * @param element
	 */
	public DicomElementImpl(DicomElement element) {
		super();
		this.element = element;
	}
	
	/**************************************************************************
	 * Interface methods
	 *************************************************************************/

	/* (non-Javadoc)
	 * @see com.lbs.DCS.DicomElement#getValue()
	 */
	public Object getValue() throws DCSException {
		// Delegate to the contained DCF DicomElement
		return element.getValue();
	}

	/* (non-Javadoc)
	 * @see com.lbs.DCS.DicomElement#getValue(int)
	 */
	public Object getValue(int index) throws DCSException {
		// Delegate to the contained DCF DicomElement
		return element.getValue(index);
	}

	/* (non-Javadoc)
	 * @see com.lbs.DCS.DicomElement#getStringValue(java.lang.String, boolean)
	 */
	public String getStringValue(String arg0, boolean arg1) throws DCSException {
		// Delegate to the contained DCF DicomElement

	    //It was discovered control and misc. characters were being passed into the 
        //  HashMap.  This was causing problems when sending RPC calls in the Legacy 
        //  busines layer.  I added the .trim() method for the meantime.  It might help.
        DicomStringElement stringElement = (DicomStringElement)this.element;
        String stringValue = "";
//        try
//       {
            stringValue  = stringElement.getStringValue(arg0, arg1).trim();
//        }
//        catch (DCSException dcse){
//            // log exception
//            logger.error(dcse.getMessage());
//            logger.error(this.getClass().getName()+": Failure to get String value.");
//        }
        return stringValue;
	}

	/* (non-Javadoc)
	 * @see com.lbs.DCS.DicomElement#getDecimalValue()
	 */
	public double getDecimalValue() {
		// Delegate to the contained DCF DicomElement
		double decimalValue = 0;
		try
		{
			decimalValue = element.getDecimalValue();
		}
		catch (DCSException dcse){
            logger.error(dcse.getMessage());
            logger.error(this.getClass().getName()+": Failure to get decimal value.");
			// log exception
            logger.error("Failure to get Decimal value.");
		}
		return decimalValue;
	}

	/* (non-Javadoc)
	 * @see com.lbs.DCS.DicomElement#getDecimalValue(int)
	 */
	public double getDecimalValue(int index) {
		// Delegate to the contained DCF DicomElement
		double decimalValue = 0;
		try
		{
			decimalValue = element.getDecimalValue(index);
		}
		catch (DCSException dcse){
            logger.error("Error :"+dcse.getMessage());
            logger.error(this.getClass().getName()+": Failure to get decimal value.");
			// log exception
            logger.error("Failure to get Decimal value.");
		}
		return decimalValue;
	}

	/* (non-Javadoc)
	 * @see com.lbs.DCS.DicomElement#getIntValue()
	 */
	public int getIntValue() {
		// Delegate to the contained DCF DicomElement
		int intValue = 0;
		try
		{
			intValue  = element.getIntValue();
		}
		catch (DCSException dcse)
		{
			// log exception
            logger.error(dcse.getMessage());
            logger.error(this.getClass().getName()+": Failure to get Int value.");
		}
		return intValue;
	}

	/* (non-Javadoc)
	 * @see com.lbs.DCS.DicomElement#getIntValue(int)
	 */
	public int getIntValue(int index) {
		// Delegate to the contained DCF DicomElement
		int intValue = 0;
		try
		{
			intValue  = element.getIntValue(index);
		}
		catch (DCSException dcse)
		{
			// log exception
            logger.error(dcse.getMessage());
            logger.error(this.getClass().getName()+": Failure to get Int value");
		}
		return intValue;
	}

	/* (non-Javadoc)
	 * @see com.lbs.DCS.DicomElement#getStringValue()
	 */
	public String getStringValue() {
		// Delegate to the contained DCF DicomElement
        
        DicomStringElement stringElement = (DicomStringElement)this.element;

		String stringValue = "";
		try
		{
			stringValue  = stringElement.getStrippedValue();
		}
		catch (DCSException dcse)
		{
			// log exception
            logger.error(dcse.getMessage());
            logger.error(this.getClass().getName()+": Failure to get String value");
		}
		return stringValue;
	}

	/* (non-Javadoc)
	 * @see com.lbs.DCS.DicomElement#getStringValue(int)
	 */
	public String getStringValue(int index) {
		// Delegate to the contained DCF DicomElement
        
        //Cast to DicomStringElement to use methods that an remove erronous characters from
        //  from the String variable.  Not doing this was causing problems with the Legacy 
        //  business layer.
        DicomStringElement stringElement = (DicomStringElement)this.element;

        String stringValue = "";
        try
        {
            stringValue  = stringElement.getStrippedValue(index);
        }
        catch (DCSException dcse)
        {
            // log exception
            logger.error(dcse.getMessage());
            logger.error(this.getClass().getName()+": Failure to get String value.");
        }
        return stringValue;
	}
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.interfaces.IDicomElement#getSequenceElementStringValue(IDicomElement)
	 */
	public String getSequenceElementStringValue(String seqtag) {
		String stringValue = "";
		try
		{
			DicomSQElement sq_e = (DicomSQElement)element; // at this point it is assumed that element is of VR_SQ
			DicomDataSet  sq_ds = sq_e.SQ(); // get the sequence DataSet
			stringValue = sq_ds.findElement(new AttributeTag(seqtag)).getStringValue(); // fetch the relevant tag's string value
		}
		catch (DCSException dcse)
		{
			// log exception
            logger.error(dcse.getMessage());
            logger.error(this.getClass().getName()+": Failure to get Sequence Element String value.");
		}
		return stringValue;		
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.interfaces.IDicomElement#getSequenceElementStringValue(IDicomElement, int)
	 */
	public String getSequenceElementStringValue(String seqtag, int index) {
		String stringValue = "";
		try
		{
			DicomSQElement sq_e = (DicomSQElement)element; // at this point it is assumed that element is of VR_SQ
			DicomDataSet  sq_ds = sq_e.SQ(index); // get the proper sequence DataSet item
			stringValue = sq_ds.getElementStringValue(new AttributeTag(seqtag)); // fetch the relevant tag's string value
		}
		catch (DCSException dcse)
		{
			// log exception
            logger.error(dcse.getMessage());
            logger.error(this.getClass().getName()+": Failure to get Sequence Element String value.");
		}
		return stringValue;	
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.interfaces.IDicomElement#vm()
	 */
	public int vm() {
		// Delegate to the contained DCF DicomElement
		return element.vm();
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.dicom.interfaces.IDicomElement#vr()
	 */
	public short vr() {
		// Delegate to the contained DCF DicomElement
		return translateVR(element.vr());
	}
	
	public String getTagName()
	{
		return this.element.tag().toString();
	}

	/**
	 * Translates the vr constants from the DCF specific representation into our generic dicom 
	 * representation of the same vr.
	 * 
	 * @param vr
	 * @return
	 */
	private short translateVR(short vr) {

		switch (vr)
		{
			case DCM.VR_AE: 
				return Constants.DICOM_VR_AE;
			case DCM.VR_AS: 
				return Constants.DICOM_VR_AS;
			case DCM.VR_AT: 
				return Constants.DICOM_VR_AT;
			case DCM.VR_CS: 
				return Constants.DICOM_VR_CS;
			case DCM.VR_DA: 
				return Constants.DICOM_VR_DA;
			case DCM.VR_DS: 
				return Constants.DICOM_VR_DS;
			case DCM.VR_DT: 
				return Constants.DICOM_VR_DT;
			case DCM.VR_FD: 
				return Constants.DICOM_VR_FD;
			case DCM.VR_FL: 
				return Constants.DICOM_VR_FL;
			case DCM.VR_INVALID: 
				return Constants.DICOM_VR_INVALID;
			case DCM.VR_IS: 
				return Constants.DICOM_VR_IS;
			case DCM.VR_LO: 
				return Constants.DICOM_VR_LO;
			case DCM.VR_LT: 
				return Constants.DICOM_VR_LT;
			case DCM.VR_OB: 
				return Constants.DICOM_VR_OB;
			case DCM.VR_OW: 
				return Constants.DICOM_VR_OW;
			case DCM.VR_PN: 
				return Constants.DICOM_VR_PN;
			case DCM.VR_SH: 
				return Constants.DICOM_VR_SH;
			case DCM.VR_SL: 
				return Constants.DICOM_VR_SL;
			case DCM.VR_SQ: 
				return Constants.DICOM_VR_SQ;
			case DCM.VR_SQ_DELIM: 
				return Constants.DICOM_VR_SQ_DELIM;
			case DCM.VR_SS: 
				return Constants.DICOM_VR_SS;
			case DCM.VR_ST: 
				return Constants.DICOM_VR_ST;
			case DCM.VR_TM: 
				return Constants.DICOM_VR_TM;
			case DCM.VR_UI: 
				return Constants.DICOM_VR_UI;
			case DCM.VR_UL: 
				return Constants.DICOM_VR_UL;
			case DCM.VR_UN: 
				return Constants.DICOM_VR_UN;
			case DCM.VR_US: 
				return Constants.DICOM_VR_US;
			case DCM.VR_UT: 
				return Constants.DICOM_VR_UT;
			default: 
				return Constants.DICOM_VR_UN;
		}
	}
	/**
	 * Converts our generic dicom representation of the same vr constants to the DCF specific representation	 * 
	 * @param vr
	 * @return
	 */
	private short convertVR(short vr) {

		switch (vr)
		{
			case Constants.DICOM_VR_AE: 
				return DCM.VR_AE;
			case Constants.DICOM_VR_AS: 
				return DCM.VR_AS;
			case Constants.DICOM_VR_AT: 
				return DCM.VR_AT;
			case Constants.DICOM_VR_CS: 
				return DCM.VR_CS;
			case Constants.DICOM_VR_DA: 
				return DCM.VR_DA;
			case Constants.DICOM_VR_DS: 
				return DCM.VR_DS;
			case Constants.DICOM_VR_DT: 
				return DCM.VR_DT;
			case Constants.DICOM_VR_FD: 
				return DCM.VR_FD;
			case Constants.DICOM_VR_FL: 
				return DCM.VR_FL;
			case Constants.DICOM_VR_INVALID: 
				return DCM.VR_INVALID;
			case Constants.DICOM_VR_IS: 
				return DCM.VR_IS;
			case Constants.DICOM_VR_LO: 
				return DCM.VR_LO;
			case Constants.DICOM_VR_LT: 
				return DCM.VR_LT;
			case Constants.DICOM_VR_OB: 
				return DCM.VR_OB;
			case Constants.DICOM_VR_OW: 
				return DCM.VR_OW;
			case Constants.DICOM_VR_PN: 
				return DCM.VR_PN;
			case Constants.DICOM_VR_SH: 
				return DCM.VR_SH;
			case Constants.DICOM_VR_SL: 
				return DCM.VR_SL;
			case Constants.DICOM_VR_SQ: 
				return DCM.VR_SQ;
			case Constants.DICOM_VR_SQ_DELIM: 
				return DCM.VR_SQ_DELIM;
			case Constants.DICOM_VR_SS: 
				return DCM.VR_SS;
			case Constants.DICOM_VR_ST: 
				return DCM.VR_ST;
			case Constants.DICOM_VR_TM: 
				return DCM.VR_TM;
			case Constants.DICOM_VR_UI: 
				return DCM.VR_UI;
			case Constants.DICOM_VR_UL: 
				return DCM.VR_UL;
			case Constants.DICOM_VR_UN: 
				return DCM.VR_UN;
			case Constants.DICOM_VR_US: 
				return DCM.VR_US;
			case Constants.DICOM_VR_UT: 
				return DCM.VR_UT;
			default: 
				return DCM.VR_UN;
		}
	}    
}
