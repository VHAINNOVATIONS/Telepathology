/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 15, 2011
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
package gov.va.med.imaging.router.commands.documents;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.router.CommandContext;
import gov.va.med.imaging.exchange.business.ArtifactResultError;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.business.documents.Document;
import gov.va.med.imaging.exchange.business.documents.DocumentSetResult;
import gov.va.med.imaging.exchange.enums.ArtifactResultStatus;
import gov.va.med.imaging.exchange.business.documents.DocumentSet;

/**
 * @author vhaiswwerfej
 *
 */
public class CommonDocumentFunctions
{
	private final static Logger logger = Logger.getLogger(CommonDocumentFunctions.class);
	
	protected static Logger getLogger()
	{
		return logger;
	}

	public static DocumentSetResult updateConsolidatedSitesInDocumentSetResult(DocumentSetResult documentSetResult,
			CommandContext commandContext)
	{
		if(documentSetResult == null)
			return null;
		List<ArtifactResultError> errors = documentSetResult.getArtifactResultErrors();
		ArtifactResultStatus status = documentSetResult.getArtifactResultStatus();
		SortedSet<DocumentSet> updatedDocumentSets = null;
		SortedSet<DocumentSet> documentSets = documentSetResult.getArtifacts();
		if(documentSets != null)
		{
			updatedDocumentSets = new TreeSet<DocumentSet>();
			for(DocumentSet documentSet : documentSets)
			{
				DocumentSet updatedDocumentSet = null;
				if(documentSet.containsConsolidatedSiteNumber())						
				{						
					Site site = getConsolidatedSite(documentSet.getConsolidatedSiteNumber(), commandContext);
					if(site != null)
					{
						updatedDocumentSet = documentSet.cloneWithConsolidatedSiteNumber(site);
					}
					else
					{
						updatedDocumentSet = documentSet;
					}
				}
				else
				{
					updatedDocumentSet = documentSet;
				}
				updatedDocumentSets.add(updatedDocumentSet);
				// changed so at this point updatedDocumentSet has the consolidated site value updated but does not 
				// includes all documents 
				for(Document document : documentSet)
				{
					Document updatedDocument = null;
					if(document.containsConsolidatedSiteNumber())
					{
						Site site = getConsolidatedSite(document.getConsolidatedSiteNumber(), commandContext);
						if(site != null)
						{
							updatedDocument = document.cloneWithConsolidatedSiteNumber();
						}							
					}
					// no change to be made
					if(updatedDocument == null)
						updatedDocument = document;
					updatedDocumentSet.add(updatedDocument);
				}
			}
		}
		return DocumentSetResult.create(updatedDocumentSets, status, errors);
	}
	
	private static Site getConsolidatedSite(String consolidatedSiteNumber, CommandContext commandContext)
	{
		try
		{
			return commandContext.getSiteResolver().getSite(consolidatedSiteNumber);					
		}
		catch(ConnectionException cX)
		{
			getLogger().warn("ConnectionException finding resolved site for consolidated site '" + consolidatedSiteNumber + ", " + cX.getMessage(), cX);
		}
		catch(MethodException mX)
		{
			getLogger().warn("MethodException finding resolved site for consolidated site '" + consolidatedSiteNumber + ", " + mX.getMessage(), mX);			
		}
		return null;
	}
}
