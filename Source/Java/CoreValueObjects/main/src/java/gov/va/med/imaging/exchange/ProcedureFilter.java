/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Oct 19, 2010
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
package gov.va.med.imaging.exchange;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import gov.va.med.GlobalArtifactIdentifier;
import gov.va.med.imaging.exchange.business.StudyFilter;
import gov.va.med.imaging.exchange.business.StudyFilterFilterable;
import gov.va.med.imaging.exchange.configuration.DicomProcedureFilterTermsConfiguration;
import gov.va.med.imaging.exchange.enums.ProcedureFilterMatchMode;

/**
 * @author vhaiswwerfej
 *
 */
public class ProcedureFilter
extends StudyFilter
{

	private static final long serialVersionUID = 1715699710129910623L;
	
	/**
	 * boolean determines if the study is included or excluded based on being in the filter list
	 */
	private final ProcedureFilterMatchMode procedureFilterMatchMode;
	
	public ProcedureFilter(ProcedureFilterMatchMode procedureFilterMatchMode)
	{
		super();
		this.procedureFilterMatchMode = procedureFilterMatchMode;
	}
	
	public ProcedureFilter(GlobalArtifactIdentifier studyId, 
			ProcedureFilterMatchMode procedureFilterMatchMode)
	{
		super(studyId);
		this.procedureFilterMatchMode = procedureFilterMatchMode;
	}

	@Override
	public void preFilter(Collection<? extends StudyFilterFilterable> studies)
	{
		for(Iterator<? extends StudyFilterFilterable> iter = studies.iterator(); iter.hasNext();)
		{
			StudyFilterFilterable study = iter.next();
			if(!isAllowableStudyType(study.getProcedure()))
				iter.remove();
		}
	}
	
	/**
	 * @param studyType
	 * @return returns true if the given study type is on the allowed list
	 * or if the list is null or empty
	 */
	private boolean isAllowableStudyType(String procedure)
	{
		if(procedureFilterMatchMode == ProcedureFilterMatchMode.all)
			return true;
		
		if(procedure == null)
			return false;
		
		List<String> allowableTypes = getAllowableStudyTypes();
		if(allowableTypes == null || allowableTypes.size() == 0)
			return true;
		
		boolean foundInList = false;
		
		for(String allowableStudyType : getAllowableStudyTypes())
		{
			if(procedure.equalsIgnoreCase(allowableStudyType))
			{
				foundInList = true;
				break;
			}
		}
		if(foundInList)
		{
			if(procedureFilterMatchMode == ProcedureFilterMatchMode.existInProcedureList)
				return true;
			else
				return false;
		}
		else
		{
			if(procedureFilterMatchMode == ProcedureFilterMatchMode.excludedInProcedureList)
				return true;
			else
				return false;
		}
	}
	
	private List<String> getAllowableStudyTypes()
	{
		DicomProcedureFilterTermsConfiguration configuration = 
			DicomProcedureFilterTermsConfiguration.getConfiguration();
		return configuration.getFilterTerms();
	}

	public ProcedureFilterMatchMode getProcedureFilterMatchMode()
	{
		return procedureFilterMatchMode;
	}

}
