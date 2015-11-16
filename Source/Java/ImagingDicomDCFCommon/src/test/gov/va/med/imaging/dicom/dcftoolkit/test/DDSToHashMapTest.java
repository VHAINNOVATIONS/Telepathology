/*
 * Created on Oct 28, 2005
 *
 */
package gov.va.med.imaging.dicom.dcftoolkit.test;

import gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet;
import gov.va.med.imaging.dicom.dcftoolkit.common.impl.DicomDataSetImpl;
import gov.va.med.imaging.exchange.business.dicom.exceptions.IllegalQueryDataException;

import java.util.HashMap;

import com.lbs.DCS.AttributeTag;
import com.lbs.DCS.DCSException;
import com.lbs.DCS.DicomDataSet;

/**
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 *
 *
 * @author William Peterson
 *
 */
public class DDSToHashMapTest extends DicomDCFCommonTestBase {

    /**
     * Constructor
     *
     * @param arg0
     */
    public DDSToHashMapTest(String arg0) {
        super(arg0);
    }

    
    protected void setUp()throws Exception{
    	super.setUp();
    }
    
    protected void tearDown() throws Exception{
    	super.tearDown();
    }
    
    public void testCFindOne(){
        DicomDataSet dds = new DicomDataSet();
        
        try{
            AttributeTag StudyDate = new AttributeTag("0008,0020");
            dds.insert(StudyDate, "");
            AttributeTag StudyTime = new AttributeTag("0008,0030");
            dds.insert(StudyTime, "");
            AttributeTag Accession = new AttributeTag("0008,0050");
            dds.insert(Accession, "022102-105");
            AttributeTag QueryLevel = new AttributeTag("0008,0052");
            dds.insert(QueryLevel, "STUDY");
            AttributeTag InstanceAvail = new AttributeTag("0008,0056");
            dds.insert(InstanceAvail, "");
            AttributeTag Modalities = new AttributeTag("0008,0061");
            dds.insert(Modalities, "");
            AttributeTag Institution = new AttributeTag("0008,0080");
            dds.insert(Institution, "");
            AttributeTag ReferringPhysician = new AttributeTag("0008,0090");
            dds.insert(ReferringPhysician, "");
            AttributeTag StudyDescription = new AttributeTag("0008,1030");
            dds.insert(StudyDescription, "");
            AttributeTag Department = new AttributeTag("0008,1040");
            dds.insert(Department, "");
            AttributeTag PatientName = new AttributeTag("0010,0010");
            dds.insert(PatientName, "");
            AttributeTag PatientID = new AttributeTag("0010,0020");
            dds.insert(PatientID, "");
            AttributeTag BirthDate = new AttributeTag("0010,0030");
            dds.insert(BirthDate, "");
            AttributeTag Sex = new AttributeTag("0010,0040");
            dds.insert(Sex, "");
            AttributeTag StudyInstanceUID = new AttributeTag("0020,000d");
            dds.insert(StudyInstanceUID, "");
            AttributeTag StudyID = new AttributeTag("0020,0010");
            dds.insert(StudyID, "");
            
            HashMap<String, String> queryResponse = this.CFindDDSToHashMap(dds);

            if(queryResponse == null){
            	fail();
            }
            if(queryResponse.isEmpty()){
            	fail();
            }
            TESTLOGGER.info("HashMap: " + queryResponse.toString());
        }
        catch(DCSException dcs){
            fail("DCSException thrown.");
        }
    }

    public void testCFindTwo(){
        DicomDataSet dds = new DicomDataSet();
        
        try{
            AttributeTag StudyDate = new AttributeTag("0008,0020");
            dds.insert(StudyDate, "");
            AttributeTag StudyTime = new AttributeTag("0008,0030");
            dds.insert(StudyTime, "");
            AttributeTag Accession = new AttributeTag("0008,0050");
            dds.insert(Accession, "050207-1867");
            AttributeTag QueryLevel = new AttributeTag("0008,0052");
            dds.insert(QueryLevel, "STUDY");
            AttributeTag Modalities = new AttributeTag("0008,0061");
            dds.insert(Modalities, "");
            AttributeTag ReferringPhysician = new AttributeTag("0008,0090");
            dds.insert(ReferringPhysician, "");
            AttributeTag StudyDescription = new AttributeTag("0008,1030");
            dds.insert(StudyDescription, "");
            AttributeTag AdmittingDiagnosisDescription = new AttributeTag("0008,1080");
            dds.insert(AdmittingDiagnosisDescription, "");
            AttributeTag PatientName = new AttributeTag("0010,0010");
            dds.insert(PatientName, "FRED, FDFD");
            AttributeTag PatientID = new AttributeTag("0010,0020");
            dds.insert(PatientID, "");
            AttributeTag BirthDate = new AttributeTag("0010,0030");
            dds.insert(BirthDate, "");
            AttributeTag BirthTime = new AttributeTag("0010,0032");
            dds.insert(BirthTime, "");
            AttributeTag Sex = new AttributeTag("0010,0040");
            dds.insert(Sex, "");
            AttributeTag weight = new AttributeTag("0010,1030");
            dds.insert(weight, "");
            AttributeTag ethnic = new AttributeTag("0010,2160");
            dds.insert(ethnic, "");
            AttributeTag occupation = new AttributeTag("0010,2180");
            dds.insert(occupation, "");
            AttributeTag additionalPatientHistory = new AttributeTag("0010,21b0");
            dds.insert(additionalPatientHistory, "");
            AttributeTag patientComments = new AttributeTag("0010,4000");
            dds.insert(patientComments, "");
            AttributeTag StudyInstanceUID = new AttributeTag("0020,000d");
            dds.insert(StudyInstanceUID, "");
            AttributeTag StudyID = new AttributeTag("0020,0010");
            dds.insert(StudyID, "");
            AttributeTag otherStudyNumbers = new AttributeTag("0020,1070");
            dds.insert(otherStudyNumbers, "");
            AttributeTag numberofRelatedSeries = new AttributeTag("0020,1206");
            dds.insert(numberofRelatedSeries, "");
            AttributeTag numberofRelatedInstances = new AttributeTag("0020,1208");
            dds.insert(numberofRelatedInstances, "");
            
            HashMap<String, String> queryResponse = this.CFindDDSToHashMap(dds);
            
            if(queryResponse == null){
            	fail();
            }
            if(queryResponse.isEmpty()){
            	fail();
            }
            TESTLOGGER.info("HashMap: " + queryResponse.toString());
        }
        catch(DCSException dcs){
            fail("DCSException thrown.");
        }
    }

    private HashMap<String, String> CFindDDSToHashMap(DicomDataSet dds){
        try{
            IDicomDataSet encapDDS = new DicomDataSetImpl(dds);
            
            HashMap<String, String> QueryResponse = encapDDS.createStudyQueryRequestParameters();
            
            return QueryResponse;
            
        }
        catch(IllegalQueryDataException b){
            TESTLOGGER.error("IllegalQueryDataException");
            return null;
        }
        
    }

}
