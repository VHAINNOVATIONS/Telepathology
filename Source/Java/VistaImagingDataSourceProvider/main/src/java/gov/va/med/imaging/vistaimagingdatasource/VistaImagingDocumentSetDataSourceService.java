/**
 * 
 */
package gov.va.med.imaging.vistaimagingdatasource;

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.DocumentSetDataSourceSpi;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.DocumentFilter;
import gov.va.med.imaging.exchange.business.Study;
import gov.va.med.imaging.exchange.business.StudySetResult;
import gov.va.med.imaging.exchange.business.documents.DocumentSet;
import gov.va.med.imaging.exchange.business.documents.DocumentSetResult;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;

import java.util.SortedSet;

import org.apache.log4j.Logger;

/**
 * This is an implementation of the DocumentDataSourceSpi with VistA as the backing
 * data storage.  This class is a derivation of the StudyGraphDataSourceSpi over VistA
 * with additional function to translate into Document semantics.
 * 
 * @author vhaiswbeckec
 *
 */
public class VistaImagingDocumentSetDataSourceService
extends VistaImagingStudyGraphDataSourceService
implements DocumentSetDataSourceSpi
{
	private Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public VistaImagingDocumentSetDataSourceService(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	{
		super(resolvedArtifactSource, protocol);
	}

	@Override
	public DocumentSetResult getPatientDocumentSets(RoutingToken globalRoutingToken, DocumentFilter filter) 
	throws MethodException, ConnectionException
	{
		// JMW 12/27/2012 assuming filter.getPatientId() is a patient ICN
		PatientIdentifier patientIdentifier = PatientIdentifier.icnPatientIdentifier(filter.getPatientId());
		StudySetResult studySet = this.getPatientStudies(globalRoutingToken, patientIdentifier, 
				filter, StudyLoadLevel.STUDY_AND_IMAGES);
		if(studySet == null)
			throw new MethodException("StudySetResult from getting patient studies is null - this should NEVER happen!");
		SortedSet<Study> studies = studySet.getArtifacts(); 
		SortedSet<DocumentSet> documentSets;
		try
		{
			documentSets = DocumentSet.translate(studies);
			return DocumentSetResult.create(documentSets, studySet.getArtifactResultStatus(), 
					studySet.getArtifactResultErrors());
		}
		catch (URNFormatException x)
		{
			logger.error("Unable to transform studies into document sets.", x);
			throw new MethodException(x);
		}
	}
}
