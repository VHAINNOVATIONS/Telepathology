/**
 * 
 */
package gov.va.med.imaging.exchange.business.documents;

import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.log4j.Logger;
import gov.va.med.OID;
import gov.va.med.WellKnownOID;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.Image;
import gov.va.med.imaging.exchange.business.Series;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.enums.ObjectOrigin;

/**
 * @author vhaiswbeckec
 * 
 * A DocumentSet is a Set of related documents.  In VistA a document set is a
 * document group, according to the grouping in file 2005.
 * In IHE parlance, a DocumentSet is a document set as stored, retrieved and retained
 * in an IHE registry/repository.
 * A DocumentSet is a composition of Document instances.  A DocumentSet also has
 * information comprising a summary of the document set members.
 */
public class DocumentSet
extends java.util.HashSet<Document>
implements Comparable<DocumentSet>
{
	private static final long serialVersionUID = 1L;

	//private ObjectOrigin objectOrigin;	// the originating enterprise, domain of the siteNumber if from an external origin
	//private String siteNumber;			// the site within the enterprise
	private final OID homeCommunityId;		// the owning community ID
	private final String repositoryId;		// the owning repository ID
	private final String identifier;			// internal entry number in VistA or a unique String within the 
	private Date acquisitionDate;
	
	private String patientIcn;
	private String patientName;
	private String firstImageIen;
	private String siteName;
	private String siteAbbr;
	private String rpcResponseMsg;
	private Date procedureDate;
	private String errorMessage;
	private String alienSiteNumber;
	private String clinicalType;			// a text description of the clinical significance of the document
											// in the Study object (and VI vernacular), this is imageType
	private String consolidatedSiteNumber = null;
	
	//NOTE: If you add more fields to this class, be sure to add the new fields to cloneWithConsolidatedSiteNumber() 
	// to ensure the new fields are copied correctly
	
	private static Logger logger = Logger.getLogger(DocumentSet.class);
	
	/**
	 * Create a DocumentSet instance from a Study.
	 * The Instance instances in the Study become Document instances
	 * in this collection.
	 *  
	 * @param study
	 * @throws URNFormatException 
	 */
	public static DocumentSet translate(Study study) 
	throws URNFormatException
	{
		if(study.hasErrorMessage())
		{
			logger.warn("Study '" + study.getStudyUrn().toString() + "' contains error message, excluding from document result.");
			return null;
		}
		if(study.isDeleted())
		{
			logger.warn("Study '" + study.getStudyUrn().toString() + "' is deleted, excluding from document result.");
			return null;
		}
		DocumentSet documentSet = new DocumentSet(
			study.getObjectOrigin() == ObjectOrigin.DOD ? 
				WellKnownOID.HAIMS_DOCUMENT.getCanonicalValue() : WellKnownOID.VA_DOCUMENT.getCanonicalValue(), 
			study.getSiteNumber(), 
			study.getStudyIen());

		documentSet.setPatientIcn( study.getPatientId() );
		documentSet.setPatientName( study.getPatientName() );
		documentSet.setFirstImageIen( study.getFirstImageIen() );
		documentSet.setSiteName( study.getSiteName() );
		documentSet.setSiteAbbr( study.getSiteAbbr() );
		documentSet.setRpcResponseMsg( study.getRpcResponseMsg() );
		documentSet.setProcedureDate( study.getProcedureDate() );
		documentSet.setErrorMessage( study.getErrorMessage() );
		documentSet.setAlienSiteNumber( study.getAlienSiteNumber() );
		documentSet.setClinicalType(study.getImageType());
		documentSet.setConsolidatedSiteNumber(study.getConsolidatedSiteNumber());
		
		for( Series series : study )
			for(Image image : series )
			{
				// JMW 9/3/2010 - if the image is deleted or has an error
				// , it won't be included in the result
				Document document = Document.translate(image, documentSet);
				if(document != null)
					documentSet.add( document );
			}
		
		return documentSet;
	}

	/**
	 * Translate a SortedSet of Study instances into a SortedSet of DocumentSet instances.
	 * 
	 * @param homeCommunityId
	 * @param studies
	 * @return
	 * @throws URNFormatException 
	 */
	public static SortedSet<DocumentSet> translate(SortedSet<Study> studies) 
	throws URNFormatException
	{
		if(studies == null)
			return null;
		
		SortedSet<DocumentSet> documentSets = new TreeSet<DocumentSet>();
		
		for(Study study : studies)
		{
			DocumentSet documentSet = DocumentSet.translate(study);
			if(documentSet != null)
			{
				logger.info("Translated " + documentSet.toString());
				documentSets.add( documentSet );
			}
		}
		
		return documentSets;
	}
	

	/**
	 * 
	 * @param objectOrigin
	 * @param siteNumber
	 * @param groupIen
	 */
	public DocumentSet(OID homeCommunityId, String repositoryId, String groupIen)
	{
		super();

		if(homeCommunityId == null)
			throw new NullPointerException("Home Community ID cannot be a null value.");
		if(repositoryId == null)
			throw new NullPointerException("Repository ID cannot be a null value.");
		if(groupIen == null)
			throw new NullPointerException("Group IEN cannot be a null value.");
		
		// set the immutable primary keys
		this.homeCommunityId = homeCommunityId;
		this.repositoryId = repositoryId;
		this.identifier = groupIen;
	}

	public String getPatientIcn()
	{
		return this.patientIcn;
	}

	public void setPatientIcn(String patientIcn)
	{
		if(patientIcn == null)
			throw new NullPointerException("Patient ICN cannot be a null value.");
		this.patientIcn = patientIcn;
	}

	public OID getHomeCommunityId()
	{
		return this.homeCommunityId;
	}

	public String getRepositoryId()
	{
		return this.repositoryId;
	}

	public String getGroupIen()
	{
		return this.identifier;
	}

	public String getIdentifier()
	{
		return this.identifier;
	}

	public Date getAcquisitionDate()
	{
		return this.acquisitionDate;
	}

	public void setAcquisitionDate(Date acquisitionDate)
	{
		this.acquisitionDate = acquisitionDate;
	}

	public String getPatientName()
	{
		return this.patientName;
	}

	public void setPatientName(String patientName)
	{
		this.patientName = patientName;
	}

	public String getFirstImageIen()
	{
		return this.firstImageIen;
	}

	public void setFirstImageIen(String firstImageIen)
	{
		this.firstImageIen = firstImageIen;
	}

	public String getSiteName()
	{
		return this.siteName;
	}

	public void setSiteName(String siteName)
	{
		this.siteName = siteName;
	}

	public String getSiteAbbr()
	{
		return this.siteAbbr;
	}

	public void setSiteAbbr(String siteAbbr)
	{
		this.siteAbbr = siteAbbr;
	}

	public String getRpcResponseMsg()
	{
		return this.rpcResponseMsg;
	}

	public void setRpcResponseMsg(String rpcResponseMsg)
	{
		this.rpcResponseMsg = rpcResponseMsg;
	}

	public Date getProcedureDate()
	{
		return this.procedureDate;
	}

	public void setProcedureDate(Date procedureDate)
	{
		this.procedureDate = procedureDate;
	}

	public String getErrorMessage()
	{
		return this.errorMessage;
	}

	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	public String getAlienSiteNumber()
	{
		return this.alienSiteNumber;
	}

	public void setAlienSiteNumber(String alienSiteNumber)
	{
		this.alienSiteNumber = alienSiteNumber;
	}

	/**
	 * @return the clinicalType
	 */
	public String getClinicalType()
	{
		return this.clinicalType;
	}

	public void setClinicalType(String clinicalType)
	{
		this.clinicalType = clinicalType;
	}

	@Override
	public int compareTo(DocumentSet that)
	{
		if(this.getAcquisitionDate() != null)
			return this.getAcquisitionDate().compareTo(that.getAcquisitionDate());
		else
			return 1;
	}
	
	public String getConsolidatedSiteNumber()
	{
		return consolidatedSiteNumber;
	}

	public void setConsolidatedSiteNumber(String consolidatedSiteNumber)
	{
		this.consolidatedSiteNumber = consolidatedSiteNumber;
	}
	
	public boolean containsConsolidatedSiteNumber()
	{
		if(this.consolidatedSiteNumber == null)
			return false;
		if(this.consolidatedSiteNumber.length() <= 0)
			return false;
		// if the consolidated site number value is the same as the repository ID, no need to update the value
		// just return false to avoid unnecessary effort
		if(this.consolidatedSiteNumber.equals(this.getRepositoryId()))
			return false;
		return true;
	}

	public DocumentSet cloneWithConsolidatedSiteNumber(Site site)
	{
		if(containsConsolidatedSiteNumber())
		{			
			if(!this.getRepositoryId().equals(this.consolidatedSiteNumber))
			{
				logger.info("Updating documentSet '" + this.identifier  + "' with new repository ID '" + this.consolidatedSiteNumber + "'.");
				
				DocumentSet documentSet = new DocumentSet(this.homeCommunityId, 
						this.consolidatedSiteNumber, this.identifier);
				documentSet.acquisitionDate = this.acquisitionDate;
				documentSet.alienSiteNumber = this.alienSiteNumber;
				documentSet.clinicalType = this.clinicalType;
				documentSet.consolidatedSiteNumber = this.consolidatedSiteNumber;
				documentSet.errorMessage = this.errorMessage;
				documentSet.firstImageIen = this.firstImageIen;
				documentSet.patientIcn = this.patientIcn;
				documentSet.patientName = this.patientName;
				documentSet.procedureDate = this.procedureDate;
				documentSet.rpcResponseMsg = this.rpcResponseMsg;
				documentSet.siteAbbr = this.siteAbbr;
				documentSet.siteName = this.siteName;
				
				// JMW 5/5/2011 P104 do not include documents - this is done later
				//documentSet.addAll(this);
				
				return documentSet;
			}
		}
		// if we get here then either something went wrong or the conversion was not necessary
		return this;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.getClass().getSimpleName());
		
		sb.append("{");
		sb.append(getHomeCommunityId().toString());
		sb.append("-");
		sb.append(getRepositoryId());
		sb.append("} ");

		sb.append("{");
		sb.append(getPatientName());
		sb.append('(');
		sb.append(getPatientIcn());
		sb.append(')');
		sb.append("} ");
		
		sb.append("{");
		sb.append(this.size());
		sb.append(" documents");
		sb.append("} ");
		
		return sb.toString();
	}
	
	
}
