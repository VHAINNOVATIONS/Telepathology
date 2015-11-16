package gov.va.med.imaging.vistaimagingdatasource;

import gov.va.med.RoutingToken;
import gov.va.med.URNFactory;
import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.datasource.exceptions.InvalidCredentialsException;
import gov.va.med.imaging.exceptions.URNFormatException;
import gov.va.med.imaging.protocol.vista.exceptions.InvalidVistaVistaRadVersionException;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.url.vista.exceptions.InvalidVistaCredentialsException;
import gov.va.med.imaging.url.vista.exceptions.VistaMethodException;
import gov.va.med.imaging.vistadatasource.common.VistaCommonUtilities;
import gov.va.med.imaging.vistadatasource.session.VistaSession;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaImagingCommonUtilities;

import java.io.IOException;

/**
 * This version is functionally the same as V0 but it indicates it is Patch 90, required since VRad only allows 2 oldest versions to work
 * 
 * @author vhaiswlouthj
 * 
 */
public class VistaImagingVistaRadDataSourceService 
extends AbstractBaseVistaImagingVistaRadService
{
	/*
	 * =====================================================================
	 * Instance fields and methods
	 * =====================================================================
	 */
	// The required version of VistA Imaging needed to execute the RPC calls for
	// this operation
	public final static String MAG_REQUIRED_VERSION = "3.0.90|VIX";

    /**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public VistaImagingVistaRadDataSourceService(ResolvedArtifactSource resolvedArtifactSource, String protocol)
	{
		super(resolvedArtifactSource, protocol);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.vistadatasource.AbstractBaseVistaStudyGraphService#getRequiredVistaImagingVersion()
	 */
	@Override
	protected String getRequiredVistaImagingVersion() 
	{
		return VistaImagingCommonUtilities.getVistaDataSourceImagingVersion(
				VistaImagingDataSourceProvider.getVistaConfiguration(), this.getClass(), 
				MAG_REQUIRED_VERSION);
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.datasource.VistaRadDataSource#postExamAccessEvent(java.lang.String)
	 */
	@Override
	public boolean postExamAccessEvent(RoutingToken destinationRoutingToken, String inputParameter)
	throws MethodException, ConnectionException 
	{		
		VistaCommonUtilities.setDataSourceMethodAndVersion("postExamAccessEvent", getDataSourceVersion());
		getLogger().info("postExamAccessEvent(" + inputParameter + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		VistaSession vistaSession = null;
		try
		{
			vistaSession = getVistaSession();
			// VistARad passes a string that contains an ImageURN in the 3rd piece.
			// the VIX needs to extract that URN and convert it into an image IEN to call the RPC
			String decodedInputParameter = "";
			String prefix = "";
			String pieces [] = StringUtils.Split(inputParameter, StringUtils.CARET);
			for(int i = 0; i < pieces.length; i++)
			{
				if(i == 2)
				{
					try
					{
						ImageURN imageUrn = URNFactory.create(pieces[i], ImageURN.class);
						// CTB 29Nov2009
						//decodedInputParameter += prefix + Base32ConversionUtility.base32Decode(imageUrn.getImageId());
						decodedInputParameter += prefix + imageUrn.getImageId();
					}
					catch(URNFormatException urnfX)
					{
						getLogger().warn("Error converting string '" + pieces[i] + "' into ImageURN, cannot continue", urnfX);
						return false;
					}
				}
				else
				{
					decodedInputParameter += prefix + pieces[i];
				}
				prefix = StringUtils.CARET;
			}
			
			getLogger().info("converted input parameter into value '" + decodedInputParameter + "'.");			
			VistaQuery query = VistaImagingVistaRadQueryFactory.createMagJLogRemoteImgAccess(decodedInputParameter);
			String rtn = vistaSession.call(query);
			if((rtn != null) && (rtn.startsWith("1")))
			{
				getLogger().info("Successfully logged exam access.");
				return true;
			}
			else
			{
				getLogger().info("Error logging exam access, " + rtn);
				return false;
			}
		}
		catch(VistaMethodException mX)
		{
			throw new MethodException(mX); 
		}
		catch(InvalidVistaCredentialsException ivcX)
		{
			throw new InvalidCredentialsException(ivcX);
		}		
		catch(InvalidVistaVistaRadVersionException vvrvX)
		{
			throw new ConnectionException(vvrvX);
		}
		catch(IOException ioX)
		{
			throw new ConnectionException(ioX);
		}		
		finally
		{
			// note - vistaSession might be null if getVistaSession throws exception (bad version), shouldn't happen since isVersionCompatible should have failed already
			try{vistaSession.close();}
			catch(Throwable t){}
		}
	}

	@Override
	protected String getDataSourceVersion()
	{
		return "1";
	}
}
