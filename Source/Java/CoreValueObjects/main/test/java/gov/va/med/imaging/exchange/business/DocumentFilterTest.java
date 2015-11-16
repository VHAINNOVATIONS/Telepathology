/**
 * 
 */
package gov.va.med.imaging.exchange.business;

import gov.va.med.PatientIdentifier;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.enums.ObjectOrigin;
import gov.va.med.imaging.exchange.enums.StudyDeletedImageState;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.exchange.enums.VistaImageType;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import org.junit.Test;

import junit.framework.TestCase;

/**
 * @author vhaiswbeckec
 *
 */
public class DocumentFilterTest
extends TestCase
{
	private DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

	/**
	 * Test method for {@link gov.va.med.imaging.exchange.business.DocumentFilter#postFilter(java.util.Collection)}.
	 * @throws ParseException 
	 * @throws URNFormatException 
	 */
	public void testPostFilter() 
	throws ParseException, URNFormatException
	{
		Collection<Study> studies = createStudyCollection();
		
		DocumentFilter documentFilter = new DocumentFilter("pat1234");
		documentFilter.postFilter(studies);
		assertEquals(1, studies.size());
		
		studies = createStudyCollection();
		documentFilter = new DocumentFilter("pat1234", df.parse("01/01/2000"), df.parse("31/12/2000"));
		documentFilter.postFilter(studies);
		assertEquals(1, studies.size());
		
		studies = createStudyCollection();
		documentFilter = new DocumentFilter("pat1234", df.parse("01/01/2001"), df.parse("31/12/2001"));
		documentFilter.postFilter(studies);
		assertEquals(0, studies.size());
		
		studies = createStudyCollection();
		documentFilter = new DocumentFilter("pat1234", "0");
		documentFilter.postFilter(studies);
		assertEquals(1, studies.size());
		
		studies = createStudyCollection();
		documentFilter = new DocumentFilter("pat1234", "1");
		documentFilter.postFilter(studies);
		assertEquals(0, studies.size());
	}

	private Collection<Study> createStudyCollection() 
	throws ParseException, URNFormatException
	{
		Collection<Study> studies = new ArrayList<Study>();
		
		Study study = Study.create(ObjectOrigin.VA, "660", "STUDY01", PatientIdentifier.icnPatientIdentifier("655321"), 
				StudyLoadLevel.FULL, StudyDeletedImageState.cannotIncludeDeletedImages);
		study.setProcedureDate(df.parse("28/02/2000"));
		Series series = new Series();
		
		Image image = Image.create(ImageURN.create("660", "IMAGE01", study.getStudyIen(), "pat1234" ));
		//image.setGroupIen(study.getStudyIen());
		image.setImgType(VistaImageType.HTML.getImageType());
		image.setImageClass("0");
		
		series.addImage(image);
		studies.add(study);
		study.setFirstImage(image);
		study.setFirstImageIen(image.getIen());
		
		return studies;
	}

	@Test
	public void testCreatingDocumentFiltersWithDateRange()
	{
		Calendar fromDate = Calendar.getInstance();
		fromDate.set(Calendar.YEAR, 2000);
		fromDate.set(Calendar.MONTH, 0);
		fromDate.set(Calendar.DAY_OF_MONTH, 1);
		Calendar toDate = Calendar.getInstance();
		toDate.set(Calendar.YEAR, 2000);
		toDate.set(Calendar.MONTH, 11);
		toDate.set(Calendar.DAY_OF_MONTH, 31);
		
		DocumentFilter documentFilter = new DocumentFilter("patId", fromDate.getTime(), toDate.getTime());
		validateDateFilterSet(documentFilter, fromDate, toDate);
		
		documentFilter = new DocumentFilter("patId");
		documentFilter.setFromDate(fromDate.getTime());
		documentFilter.setToDate(toDate.getTime());
		validateDateFilterSet(documentFilter, fromDate, toDate);
		
		documentFilter = new DocumentFilter("patId", fromDate.getTime(), toDate.getTime(), "classCode");
		validateDateFilterSet(documentFilter, fromDate, toDate);
		
		documentFilter = new DocumentFilter("patId", fromDate.getTime(), toDate.getTime(), 
				new String [] {"classCode1", "classCode2"});
		validateDateFilterSet(documentFilter, fromDate, toDate);
	}
	
	private void validateDateFilterSet(DocumentFilter filter, Calendar fromDate, Calendar toDate)
	{
		assertNotNull(filter.getFromDate());
		assertNotNull(filter.getToDate());
		assertNotNull(filter.getCreationTimeFrom());
		assertNotNull(filter.getCreationTimeTo());
		
		assertEquals(fromDate.getTime(), filter.getFromDate());
		assertEquals(filter.getFromDate(), filter.getCreationTimeFrom());
		
		assertEquals(toDate.getTime(), filter.getToDate());
		assertEquals(filter.getToDate(), filter.getCreationTimeTo());
	}
	
	@Test
	public void testDocumentFilterWithoutDateRange()
	{
		DocumentFilter documentFilter = new DocumentFilter("patId");
		assertNull(documentFilter.getFromDate());
		assertNull(documentFilter.getToDate());
		assertNull(documentFilter.getCreationTimeFrom());
		assertNull(documentFilter.getCreationTimeTo());
	}
}
