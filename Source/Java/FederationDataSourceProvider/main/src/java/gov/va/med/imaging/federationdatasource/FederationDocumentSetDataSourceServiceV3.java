/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 22, 2009
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
package gov.va.med.imaging.federationdatasource;

import java.util.SortedSet;
import org.apache.log4j.Logger;

import gov.va.med.PatientIdentifier;
import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.DocumentSetDataSourceSpi;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.exchange.business.DocumentFilter;
import gov.va.med.imaging.exchange.business.StudySetResult;
import gov.va.med.imaging.exchange.business.documents.DocumentSet;
import gov.va.med.imaging.exchange.business.documents.DocumentSetResult;
import gov.va.med.imaging.exchange.enums.StudyLoadLevel;
import gov.va.med.imaging.federationdatasource.document.DocumentCommon;

/**
 * @author vhaiswwerfej
 *
 */
public class FederationDocumentSetDataSourceServiceV3 
extends FederationStudyGraphDataSourceServiceV3 
implements DocumentSetDataSourceSpi 
{

	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 * @throws UnsupportedOperationException
	 */
	public FederationDocumentSetDataSourceServiceV3(ResolvedArtifactSource resolvedArtifactSource, String protocol)
		throws UnsupportedOperationException
	{
		super(resolvedArtifactSource, protocol);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.DocumentSetDataSourceSpi#getPatientDocumentSets(java.lang.String, gov.va.med.imaging.exchange.business.DocumentFilter)
	 */
	@Override
	public DocumentSetResult getPatientDocumentSets(RoutingToken globalRoutingToken, DocumentFilter filter) 
	throws MethodException, ConnectionException 
	{
		// assuming patientId in the filter is an ICN
		PatientIdentifier patientIdentifier = PatientIdentifier.icnPatientIdentifier(filter.getPatientId());
		StudySetResult studySet = this.getPatientStudies(globalRoutingToken, patientIdentifier, 
				filter, StudyLoadLevel.STUDY_AND_IMAGES);
		if(studySet == null)
		{
			getLogger().error("Got null StudySetResult, cannot convert that to DocumentSetResult");
			return null;
		}
		SortedSet<DocumentSet> documentSets;
		try
		{
			documentSets = DocumentCommon.translate(studySet.getArtifacts());
			return DocumentSetResult.create(documentSets, studySet.getArtifactResultStatus(), 
					studySet.getArtifactResultErrors());
		}
		catch (URNFormatException x)
		{
			Logger.getLogger(this.getClass()).error("Unable to transform studies into document sets.", x);
			throw new MethodException(x);
		}
	}

}
