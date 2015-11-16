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
package gov.va.med.imaging.exchange.business.documents.test;

import java.util.Date;

import gov.va.med.ImageURNFactory;
import gov.va.med.MediaType;
import gov.va.med.WellKnownOID;
import gov.va.med.imaging.DocumentURN;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.documents.Document;
import gov.va.med.imaging.exchange.business.documents.DocumentSet;

/**
 * @author vhaiswwerfej
 *
 */
public class DocumentBusinessObjectBuilder
{
	public static DocumentSet createDocumentSet()
	throws URNFormatException
	{
		return createDocumentSet(null);
	}
	
	public static DocumentSet createDocumentSet(String consolidatedSiteNumber)
	throws URNFormatException
	{
		DocumentSet documentSet = 
			new DocumentSet(WellKnownOID.VA_DOCUMENT.getCanonicalValue(), "660", "documentSet123");
		documentSet.setAcquisitionDate(new Date());
		documentSet.setAlienSiteNumber("alien123");
		documentSet.setClinicalType("clinType");
		if(consolidatedSiteNumber != null)
			documentSet.setConsolidatedSiteNumber(consolidatedSiteNumber);
		else
			documentSet.setConsolidatedSiteNumber("conSiteNumber");
		documentSet.setErrorMessage("errMsg");
		documentSet.setFirstImageIen("firstIen");
		documentSet.setPatientIcn("patIcn");
		documentSet.setPatientName("patName");
		documentSet.setProcedureDate(new Date());
		documentSet.setRpcResponseMsg("rpcREsponse");
		documentSet.setSiteAbbr("siteAbbr");
		documentSet.setSiteName("siteName");
		documentSet.add(createDocument(consolidatedSiteNumber));
		
		return documentSet;		
	}
	
	public static Document createDocument()
	throws URNFormatException
	{
		return createDocument(null);
	}
	
	public static Document createDocument(String consolidatedSiteNumber)
	throws URNFormatException
	{
		String documentSetIen = "documentSet123";
		String repositoryId = "660";
		String documentId = "456";
		String patientIcn = "pat123";
		ImageURN imageUrn = ImageURNFactory.create(repositoryId, documentId, 
				documentSetIen, patientIcn, null, ImageURN.class);
		DocumentURN documentUrn = DocumentURN.create(imageUrn);
		Document document = new Document(documentSetIen, documentUrn, new Date(), 123, "clinType", 123456, null);
		document.setConfidentialityCode(987);
		if(consolidatedSiteNumber != null)
			document.setConsolidatedSiteNumber(consolidatedSiteNumber);
		else
			document.setConsolidatedSiteNumber("con123");
		document.setDescription("description");
		document.setLanguageCode("language123");
		document.setMediaType(MediaType.APPLICATION_DOCX);
		document.setName("name");
		return document;
	}

}
