/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * Date Created: May 10, 2010
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author VHAISWWERFEJ
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */
 package gov.va.med.imaging.core.router.commands.dicom.importer;
 
 import java.util.Date;
 import org.apache.log4j.*;
 import gov.va.med.imaging.*;
 import gov.va.med.imaging.core.interfaces.exceptions.*;
 import gov.va.med.imaging.datasource.*;
 import gov.va.med.imaging.core.interfaces.router.Command;
 import gov.va.med.imaging.core.interfaces.router.AsynchronousCommandResultListener;
 import gov.va.med.imaging.core.router.AbstractDataSourceCommandImpl;
 import javax.annotation.Generated;
 
 @SuppressWarnings("unused")
 public class GetOriginIndexListCommandImpl
 extends AbstractDataSourceCommandImpl<java.util.List<gov.va.med.imaging.exchange.business.dicom.OriginIndex>,DicomImporterDataSourceSpi>
 {
 	private final static long serialVersionUID = 1L;
 
 	private final gov.va.med.RoutingToken routingToken;
 
 	public GetOriginIndexListCommandImpl(
 			gov.va.med.RoutingToken routingToken
 		)
 	{
	 	this.routingToken = routingToken;	 	
 	}
 	
 	@Override
	protected java.util.List<gov.va.med.imaging.exchange.business.dicom.OriginIndex> getCommandResult(DicomImporterDataSourceSpi spi)
	throws ConnectionException, MethodException
	{
			return spi.getOriginIndexList(
		 			routingToken
		 		);
	}
	
	@Override
	public gov.va.med.RoutingToken getRoutingToken()
	{
		return routingToken;	
	}

	@Override
	protected String getSiteNumber()
	{
		return getRoutingToken().getRepositoryUniqueId();
	}

	@Override
	protected Class<DicomImporterDataSourceSpi> getSpiClass()
	{
		return DicomImporterDataSourceSpi.class;
	}
 
 	@Override
	protected String getSpiMethodName()
	{
		return "getOriginIndexList";
	}
	
	@Override
	protected Class<?>[] getSpiMethodParameterTypes()
	{			
		return new Class<?> [] {
	 			gov.va.med.RoutingToken.class
	 	};
	}
	
	@Override
	protected Object[] getSpiMethodParameters()
	{
		return new Object [] {
	 			routingToken
	 	};	
	}
	
	@Override
	protected java.util.List<gov.va.med.imaging.exchange.business.dicom.OriginIndex> postProcessResult(java.util.List<gov.va.med.imaging.exchange.business.dicom.OriginIndex> result)
	{
		return result;
	}
 }
