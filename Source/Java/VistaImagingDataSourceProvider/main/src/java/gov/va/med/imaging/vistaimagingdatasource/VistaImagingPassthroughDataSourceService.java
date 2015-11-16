/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Sep 30, 2009
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
package gov.va.med.imaging.vistaimagingdatasource;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.core.interfaces.exceptions.SecurityCredentialsExpiredException;
import gov.va.med.imaging.datasource.AbstractVersionableDataSource;
import gov.va.med.imaging.datasource.PassthroughDataSourceSpi;
import gov.va.med.imaging.datasource.exceptions.InvalidCredentialsException;
import gov.va.med.imaging.exchange.business.PassthroughInputMethod;
import gov.va.med.imaging.exchange.business.ResolvedSite;
import gov.va.med.imaging.exchange.business.Site;
import gov.va.med.imaging.exchange.enums.ImagingSecurityContextType;
import gov.va.med.imaging.protocol.vista.exceptions.InvalidVistaVistaRadVersionException;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.url.vista.exceptions.InvalidVistaCredentialsException;
import gov.va.med.imaging.url.vista.exceptions.VistaMethodException;
import gov.va.med.imaging.vistadatasource.VistaCommonQueryFactory;
import gov.va.med.imaging.vistadatasource.common.VistaCommonUtilities;
import gov.va.med.imaging.vistadatasource.session.VistaSession;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaImagingVistaRadCommonUtilities;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 * @author vhaiswwerfej
 *
 */
public class VistaImagingPassthroughDataSourceService
extends AbstractVersionableDataSource
implements PassthroughDataSourceSpi 
{
	/* =====================================================================
	 * Instance fields and methods
	 * ===================================================================== */
	private final static Logger logger = Logger.getLogger(VistaImagingPassthroughDataSourceService.class);
	
	public final static String SUPPORTED_PROTOCOL = "vistaimaging";
	
	public final static String MAG_VRAD_REQUIRED_VERSION = "3.0.115|VIX";


	
	/**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public VistaImagingPassthroughDataSourceService(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	{
		super(resolvedArtifactSource, protocol);
		if(! (resolvedArtifactSource instanceof ResolvedSite) )
			throw new UnsupportedOperationException("The artifact source must be an instance of ResolvedSite and it is a '" + resolvedArtifactSource.getClass().getSimpleName() + "'.");
	}
	/**
	 * The artifact source must be checked in the constructor to assure that it is an instance
	 * of ResolvedSite.
	 * 
	 * @return
	 */
	protected ResolvedSite getResolvedSite()
	{
		return (ResolvedSite)getResolvedArtifactSource();
	}
	
	protected Site getSite()
	{
		return getResolvedSite().getSite();
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.PassthroughDataSource#executePassthroughMethod(gov.va.med.imaging.exchange.business.PassthroughInputMethod)
	 */
	@Override
	public String executePassthroughMethod(RoutingToken globalRoutingToken, PassthroughInputMethod method)
	throws MethodException, ConnectionException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("executePassthroughMethod", getDataSourceVersion());
		VistaSession vistaSession = null;
		logger.info("executePassthroughMethod(" + method + 
				") TransactionContext (" + TransactionContextFactory.get().getTransactionId() + ").");
		try
		{
			vistaSession = getVistaSession();
			
			VistaQuery query = VistaCommonQueryFactory.createPassthroughQuery(method);
			logger.info("Executing query '" + query.getRpcName() + "'.");
			String rtn = vistaSession.call(query);
			logger.info("Received result of '" + (rtn == null ? "null" : rtn.length()) + "' bytes.");
			TransactionContextFactory.get().setDataSourceBytesReceived(rtn == null ? 0L : rtn.length());
			return rtn;			
		}
		catch(VistaMethodException vmX)
		{
			throw new MethodException(vmX);
		}
		catch(InvalidVistaCredentialsException icX)
		{
			throw new InvalidCredentialsException(icX);
		}
		catch(IOException ioX)
		{
			throw new ConnectionException(ioX);
		}
		finally
		{
			try{vistaSession.close();}
			catch(Throwable t){}
		}		
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.PassthroughDataSource#isVersionCompatible()
	 */
	@Override
	public boolean isVersionCompatible() 
	{
		logger.info("isVersionCompatible does not check the database for this data source, returning true.");
		return true;
	}

	protected VistaSession getVistaSession() 
    throws IOException, ConnectionException, MethodException, SecurityCredentialsExpiredException
    {
		try
		{
			// somewhat kludgy - need to determine if work on behalf of VRad or not since VRad has a 
			// different login mechanism
			ImagingSecurityContextType contextType = VistaSession.getImagingSecurityContextType();
			if(contextType == ImagingSecurityContextType.MAGJ_VISTARAD)
			{
				// version only done if VRad connection, not otherwise
				return VistaImagingVistaRadCommonUtilities.getVistaSession(getMetadataUrl(), 
					getSite(), MAG_VRAD_REQUIRED_VERSION);
			}
			else
			{
				return VistaSession.getOrCreate(getMetadataUrl(), getSite());
			}			
		}
		catch(InvalidVistaVistaRadVersionException ivvrvX)
		{
			// exception already displayed
			throw new ConnectionException(ivvrvX);
		}		
    }
	
	protected String getDataSourceVersion()
	{
		return "1";
	}
}
