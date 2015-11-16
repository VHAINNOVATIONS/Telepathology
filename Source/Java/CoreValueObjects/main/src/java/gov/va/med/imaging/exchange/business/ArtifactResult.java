/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 24, 2010
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
package gov.va.med.imaging.exchange.business;

import gov.va.med.imaging.exchange.enums.ArtifactResultStatus;

import java.util.Collection;
import java.util.List;

/**
 * Abstract implementation of the holder object which holds collection of a specific type
 * 
 * @author vhaiswwerfej
 *
 */
public abstract class ArtifactResult<D extends Object, E extends Collection<D>>
{
	private final ArtifactResultStatus artifactResultStatus;
	private final List<ArtifactResultError> artifactResultErrors;
	
	private final E artifacts;
	
	protected ArtifactResult(E artifacts, ArtifactResultStatus artifactResultStatus, 
			List<ArtifactResultError> artifactResultErrors)
	{
		this.artifacts = artifacts;
		this.artifactResultErrors = artifactResultErrors;
		this.artifactResultStatus = artifactResultStatus;
	}

	public ArtifactResultStatus getArtifactResultStatus()
	{
		return artifactResultStatus;
	}

	public List<ArtifactResultError> getArtifactResultErrors()
	{
		return artifactResultErrors;
	}
	
	/**
	 * Returns true if the result is a partial result, false otherwise
	 * @return
	 */
	public boolean isPartialResult()
	{
		return artifactResultStatus == ArtifactResultStatus.partialResult;
	}
	
	/**
	 * Returns true if the result is full, false otherwise
	 * @return
	 */
	public boolean isFullResult()
	{
		return artifactResultStatus == ArtifactResultStatus.fullResult;
	}
	
	public boolean isErrorResult()
	{
		return artifactResultStatus == ArtifactResultStatus.errorResult;
	}

	/**
	 * Return the collection of artifacts
	 * @return
	 */
	public E getArtifacts()
	{
		return artifacts;
	}
	
	/**
	 * Return the number of artifacts included in the result. This method checks for the artifacts
	 * to be null and returns 0 in that case, otherwise it returns the size of the collection
	 * @return
	 */
	public int getArtifactSize()
	{
		if(artifacts == null)
		{
			return 0;
		}
		return artifacts.size();
	}

	@Override
	public String toString()
	{
		return toString(false);
	}
	
	public String toString(boolean showErrors)
	{
		if(showErrors)
		{
			StringBuilder sb = new StringBuilder();
			sb.append(getArtifactSize() + " artifacts, '" + getArtifactResultStatus() + "' status, and '" + (getArtifactResultErrors() == null ? "0" : getArtifactResultErrors().size()) + "' errors");
			if(artifactResultErrors != null && artifactResultErrors.size() > 0)
			{
				sb.append("\nErrors:");
				for(ArtifactResultError error : artifactResultErrors)
				{
					sb.append("\n");
					sb.append(error.getErrorCode() + ": " + error.getCodeContext());
				}
			}
			return sb.toString();
		}
		else
		{
			return getArtifactSize() + " artifact collection items, '" + getArtifactResultStatus() + "' status, and '" + (getArtifactResultErrors() == null ? "0" : getArtifactResultErrors().size()) + "' errors";
		}
	}
}
