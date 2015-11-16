/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: December 6, 2005
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWPETERB
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
package gov.va.med.imaging.dicom.dcftoolkit.utilities.reconstitution;

import java.util.HashMap;

import com.lbs.DCS.UID;

/**
 *
 * @author William Peterson
 *
 */
public class SOPClassModalityCodeMapping {

    
    HashMap<String, String> sop_mcMapping = null;
    /**
     * Constructor
     *
     * 
     */
    public SOPClassModalityCodeMapping() {
        super();
        //
        this.makeMappingSet(); 
        
    }

    //This only is used to populate the Modality Code, if necessary, in case there is more 
    //  than one Modality Code in the field.  This is only called when converting a Legacy
    //  TGA file.  I do not currently see the need to implement it in the Listen file.
    private void makeMappingSet(){
        this.sop_mcMapping = new HashMap<String, String>();
        
        this.sop_mcMapping.put(UID.SOPCLASSCOMPUTEDRADIOGRAPHY, "CR");
        this.sop_mcMapping.put(UID.SOPCLASSCT, "CT");
        this.sop_mcMapping.put(UID.SOPCLASSMR, "MR");
        this.sop_mcMapping.put(UID.SOPCLASSUS, "US");
        this.sop_mcMapping.put(UID.SOPCLASSUSMULTIFRAMEIMAGE, "US");
        this.sop_mcMapping.put(UID.SOPCLASSNM, "NM");
        //this.sop_mcMapping.put(UID.SOPCLASSSECONDARYCAPTURE, "OT");
        this.sop_mcMapping.put(UID.SOPCLASSXRAYANGIO, "XA");
        this.sop_mcMapping.put(UID.SOPCLASSXRAYFLUORO, "RF");
        this.sop_mcMapping.put(UID.SOPCLASSXRAYPRESENTATION, "XA");
        this.sop_mcMapping.put(UID.SOPCLASSXRAYPROCESSING, "XA");
        //this.sop_mcMapping.put(UID.SOPCLASSDMXPRESENTATION, "MX");
        //this.sop_mcMapping.put(UID.SOPCLASSDMXPROCESSING, "MX");
        this.sop_mcMapping.put(UID.SOPCLASSVLENDOSCOPIC, "ES");
        this.sop_mcMapping.put(UID.SOPCLASSVLMICROSCOPIC, "GM");
        this.sop_mcMapping.put(UID.SOPCLASSVLSLIDECOORDINATESMICROSCOPIC, "SM");
        this.sop_mcMapping.put(UID.SOPCLASSVLPHOTOGRAPHIC, "XC");
        this.sop_mcMapping.put(UID.SOPCLASSVIDEOENDOSCOPIC, "ES");
        this.sop_mcMapping.put(UID.SOPCLASSVIDEOMICROSCOPIC, "GM");
        this.sop_mcMapping.put(UID.SOPCLASSVIDEOPHOTOGRAPHIC, "XC");
        this.sop_mcMapping.put(UID.SOPCLASSBASICVOICEAUDIOWAVEFORM, "AU");
        this.sop_mcMapping.put(UID.SOPCLASS12LEADECGWAVEFORM, "ECG");
        this.sop_mcMapping.put(UID.SOPCLASSGENERALECGWAVEFORM, "ECG");
        this.sop_mcMapping.put(UID.SOPCLASSAMBULATORYECGWAVEFORM, "ECG");
        this.sop_mcMapping.put(UID.SOPCLASSHEMODYNAMICWAVEFORM, "HD");
        this.sop_mcMapping.put(UID.SOPCLASSCARDIACELECTROPHYSIOLOGYWAVEFORM, "EPS");
        this.sop_mcMapping.put(UID.SOPCLASSENHANCEDXA, "XA");
        this.sop_mcMapping.put(UID.SOPCLASSENHANCEDXRF, "RF");
        this.sop_mcMapping.put(UID.SOPCLASSRTIMAGE, "RTIMAGE");
        this.sop_mcMapping.put(UID.SOPCLASSRTDOSE, "RTDOSE");
        this.sop_mcMapping.put(UID.SOPCLASSRTSTRUCTURESET, "RTSTRUCT");
        this.sop_mcMapping.put(UID.SOPCLASSRTPLAN, "RTPLAN");
        this.sop_mcMapping.put(UID.SOPCLASSRTTREATMENTSUMMARYRECORD, "RTRECORD");
        this.sop_mcMapping.put(UID.SOPCLASSRTBEAMSTREATMENTRECORD, "RTRECORD");
        this.sop_mcMapping.put(UID.SOPCLASSRTBRACHYTREATMENTRECORD, "RTRECORD");
        this.sop_mcMapping.put(UID.SOPCLASSMAMMOXRAYPRESENTATION, "MG");
        this.sop_mcMapping.put(UID.SOPCLASSMAMMOXRAYPROCESSING, "MG");
        this.sop_mcMapping.put(UID.SOPCLASSENHANCEDMR, "MR");
        this.sop_mcMapping.put(UID.SOPCLASSENHANCEDCT, "CT");
        this.sop_mcMapping.put(UID.SOPCLASSOPHTHALMICPHOTOGRAPHY16, "OP");
        this.sop_mcMapping.put(UID.SOPCLASSOPHTHALMICPHOTOGRAPHY8, "OP");
        this.sop_mcMapping.put(UID.SOPCLASSSTEREOMETRICRELATIONSHIP, "SMR");
        this.sop_mcMapping.put(UID.SOPCLASSPRESENTATIONLUT, "PR");
    }
    
    public String getModalityCode(String sopClassUID){
        
        return this.sop_mcMapping.get(sopClassUID);
    }
}
