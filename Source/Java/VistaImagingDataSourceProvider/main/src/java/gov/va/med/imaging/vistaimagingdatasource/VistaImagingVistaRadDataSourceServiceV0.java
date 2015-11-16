package gov.va.med.imaging.vistaimagingdatasource;

import gov.va.med.RoutingToken;
import gov.va.med.imaging.artifactsource.ResolvedArtifactSource;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.transactioncontext.TransactionContextFactory;
import gov.va.med.imaging.vistadatasource.common.VistaCommonUtilities;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaImagingCommonUtilities;

/**
 * An implementation of a VistaRadDataSourceSpi that talks to VistA.
 * 
 * NOTE: 1.) public methods that do Vista access (particularly anything defined
 * in the VistaRadDataSourceSpi interface) must acquire a VistaSession instance
 * using getVistaSession(). 2.) private methods which are only called from
 * public methods that do Vista access must include a VistaSession parameter,
 * they should not acquire their own VistaSession 3.) Where a method is both
 * public and called from within this class, there should be a public version
 * following rule 1, calling a private version following rule 2.
 * 
 * @author vhaiswlouthj
 * @deprecated This version is no longer supported - Patch 101 is too old
 * 
 */
@Deprecated
public class VistaImagingVistaRadDataSourceServiceV0 
extends AbstractBaseVistaImagingVistaRadService
{
	/*
	 * =====================================================================
	 * Instance fields and methods
	 * =====================================================================
	 */
	// The required version of VistA Imaging needed to execute the RPC calls for
	// this operation
	public final static String MAG_REQUIRED_VERSION = "3.0.101|VIX";

	
    /**
	 * @param resolvedArtifactSource
	 * @param protocol
	 */
	public VistaImagingVistaRadDataSourceServiceV0(ResolvedArtifactSource resolvedArtifactSource, String protocol)
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
	public boolean postExamAccessEvent(RoutingToken globalRoutingToken, String inputParameter)
	throws MethodException, ConnectionException 
	{
		VistaCommonUtilities.setDataSourceMethodAndVersion("postExamAccessEvent", getDataSourceVersion());
		getLogger().info("postExamAccessEvent(" + inputParameter + ") TransactionContext (" + TransactionContextFactory.get().getDisplayIdentity() + ").");
		
		getLogger().warn("This version of the interface does not support logging, not doing anything.");
		return false;
	}

	@Override
	protected String getDataSourceVersion()
	{
		return "0";
	}
}
