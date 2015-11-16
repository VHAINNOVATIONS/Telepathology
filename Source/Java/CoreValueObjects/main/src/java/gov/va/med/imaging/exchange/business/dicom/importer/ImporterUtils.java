package gov.va.med.imaging.exchange.business.dicom.importer;

import gov.va.med.imaging.exchange.business.ApplicationTimeoutParameters;
import gov.va.med.imaging.exchange.business.Patient;
import gov.va.med.imaging.exchange.business.dicom.OriginIndex;
import gov.va.med.imaging.exchange.business.dicom.UIDActionConfig;
import gov.va.med.imaging.xstream.FieldUpperCaseMapper;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;
import com.thoughtworks.xstream.mapper.MapperWrapper;

public class ImporterUtils
{
    public static XStream getXStream() 
    {
    	XStream xstream = new XStream() {
            protected MapperWrapper wrapMapper(MapperWrapper next) {
                return new FieldUpperCaseMapper(next);
            }
    	};

    	// Add all the standard aliases
    	xstream.alias("ImporterWorkItem", ImporterWorkItem.class);
    	xstream.alias("ImporterWorkItemDetails", ImporterWorkItemDetails.class);
    	xstream.alias("ImporterWorkItemDetailsReference", ImporterWorkItemDetailsReference.class);
    	xstream.alias("ImporterWorkListFilter", ImporterWorkItemFilter.class);
    	xstream.alias("Order", Order.class);
    	xstream.alias("OrderingLocation", OrderingLocation.class);
    	xstream.alias("ImagingLocation", ImagingLocation.class);
    	xstream.alias("OrderingProvider", OrderingProvider.class);
    	xstream.alias("Patient", Patient.class);
    	xstream.alias("Procedure", Procedure.class);
    	xstream.alias("ProcedureModifier", ProcedureModifier.class);
    	xstream.alias("Reconciliation", Reconciliation.class);
    	xstream.alias("Series", Series.class);
    	xstream.alias("SopInstance", SopInstance.class);
    	xstream.alias("Study", Study.class);
    	xstream.alias("ReportParameters", ReportParameters.class);
    	xstream.alias("Report", Report.class);
    	xstream.alias("OriginIndex", OriginIndex.class);
    	xstream.alias("UIDActionConfig", UIDActionConfig.class);
    	xstream.alias("StandardReport", StandardReport.class);
    	xstream.alias("DiagnosticCode", DiagnosticCode.class);
    	xstream.alias("StatusChangeDetails", StatusChangeDetails.class);
    	xstream.alias("SecondaryDiagnosticCode", SecondaryDiagnosticCode.class);
    	xstream.alias("ApplicationTimeoutParameters", ApplicationTimeoutParameters.class);
    	xstream.alias("NonDicomFile", NonDicomFile.class);
    	xstream.alias("MediaCategory", MediaCategory.class);
    	xstream.alias("MediaCategories", MediaCategories.class);
    	
    	String[] acceptableDateFormats = new String[]
    	{
    		"MM-dd-yyyy",
    		"yyyyMMdd"
    	};
    	
    	DateConverter dateConverter = new DateConverter("MM/dd/yyyy", acceptableDateFormats);
    	xstream.registerConverter(dateConverter);
    	return xstream;

    }
}
