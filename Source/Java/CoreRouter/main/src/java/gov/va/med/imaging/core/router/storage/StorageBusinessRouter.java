/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr, 2010
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswlouthj
  Description: DICOM Study cache manager. Maintains the cache of study instances
  			   and expires old studies after 15 minutes. 

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
package gov.va.med.imaging.core.router.storage;

import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterInterface;
import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterInterfaceCommandTester;
import gov.va.med.imaging.core.annotations.routerfacade.FacadeRouterMethod;
import gov.va.med.imaging.core.interfaces.FacadeRouter;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.storage.Artifact;
import gov.va.med.imaging.exchange.business.storage.ArtifactSourceInfo;
import gov.va.med.imaging.exchange.business.storage.AsyncStorageRequest;
import gov.va.med.imaging.exchange.business.storage.ArtifactDescriptor;
import gov.va.med.imaging.exchange.business.storage.KeyList;

import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;

/**
 * 
 * @author vhaiswlouthj
 *
 */
@FacadeRouterInterface
@FacadeRouterInterfaceCommandTester
public interface StorageBusinessRouter extends FacadeRouter
{
	@FacadeRouterMethod(asynchronous=false)
	InputStream getArtifactStream(String artifactToken) throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false)
	InputStream getResolvedArtifactStream(ArtifactSourceInfo sourceInfo) throws MethodException, ConnectionException;
	
	@FacadeRouterMethod(asynchronous=false)
	String postArtifactByStream(InputStream artifactStream, ReadableByteChannel artifactChannel, ArtifactDescriptor artifactDescriptor, String place, KeyList keyList, String createdBy) throws MethodException, ConnectionException;

	@FacadeRouterMethod(asynchronous=false, isChildCommand=true, commandClassName="ProcessAsyncStorageRequestCommand")
	Boolean processAsyncStorageRequest(AsyncStorageRequest request) throws MethodException, ConnectionException;
}
