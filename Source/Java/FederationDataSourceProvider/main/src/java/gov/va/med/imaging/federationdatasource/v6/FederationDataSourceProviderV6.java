/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 26, 2012
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  VHAISWWERFEJ
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
package gov.va.med.imaging.federationdatasource.v6;

import gov.va.med.imaging.datasource.PatientDataSourceSpi;
import gov.va.med.imaging.datasource.Provider;
import gov.va.med.imaging.datasource.ProviderService;
import gov.va.med.imaging.datasource.UserDataSourceSpi;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author VHAISWWERFEJ
 *
 */
public class FederationDataSourceProviderV6
extends Provider
{
	private static final String PROVIDER_NAME = "FederationDataSourceProviderV6";
	private static final double PROVIDER_VERSION = 1.0d;
	private static final String PROVIDER_INFO = "Implements: \nPathologyDataSource \n backed by a Federation data store.";

	private static final long serialVersionUID = 1L;
	private final SortedSet<ProviderService> services;

	/**
	 * The public "nullary" constructor that is used by the ServiceLoader class
	 * to create instances.
	 */
	public FederationDataSourceProviderV6()
	{
		this(PROVIDER_NAME, PROVIDER_VERSION, PROVIDER_INFO);
	}
	
	/**
	 * A special constructor that is only used for creating a configuration
	 * file.
	 * 
	 * @param exchangeConfiguration
	 */
	/*
	private FederationPathologyDataSourceProvider(FederationConfiguration federationConfiguration) 
	{
		this();
		FederationPathologyDataSourceProvider.federationConfiguration = federationConfiguration;
	}*

	/**
	 * @param name
	 * @param version
	 * @param info
	 */
	private FederationDataSourceProviderV6(String name, double version, String info)
	{
		super(name, version, info);

		services = new TreeSet<ProviderService>();
		
		// version 6 services
		
		services.add(
				new ProviderService(
					this,
					PatientDataSourceSpi.class,
					FederationPatientDataSourceServiceV6.SUPPORTED_PROTOCOL,
					6.0F,
					FederationPatientDataSourceServiceV6.class)
				);
		services.add(
				new ProviderService(
					this,
					UserDataSourceSpi.class,
					FederationUserDataSourceServiceV6.SUPPORTED_PROTOCOL,
					6.0F,
					FederationUserDataSourceServiceV6.class)
				);

			
	}

	@Override
	public SortedSet<ProviderService> getServices()
	{
		return Collections.unmodifiableSortedSet(services);
	}

}
