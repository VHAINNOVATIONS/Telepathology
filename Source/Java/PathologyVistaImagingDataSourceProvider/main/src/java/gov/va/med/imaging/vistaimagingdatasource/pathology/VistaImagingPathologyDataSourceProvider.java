/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jun 5, 2012
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
package gov.va.med.imaging.vistaimagingdatasource.pathology;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import gov.va.med.imaging.datasource.Provider;
import gov.va.med.imaging.datasource.ProviderService;
import gov.va.med.imaging.pathology.datasource.PathologyDataSourceSpi;

/**
 * @author VHAISWWERFEJ
 *
 */
public class VistaImagingPathologyDataSourceProvider
extends Provider
{
	
	private static final String PROVIDER_NAME = "VistaImagingPathologyDataSource";
	private static final double PROVIDER_VERSION = 1.0d;
	private static final String PROVIDER_INFO = 
		"Implements: \n" + 
		"PathologyDataSource SPI \n" + 
		"backed by a VistA Imaging data store.";

	private static final long serialVersionUID = 1L;
	
	private final SortedSet<ProviderService> services;

	/**
	 * The public "nullary" constructor that is used by the ServiceLoader class
	 * to create instances.
	 */
	public VistaImagingPathologyDataSourceProvider()
	{
		this(PROVIDER_NAME, PROVIDER_VERSION, PROVIDER_INFO);
	}

	/**
	 * @param name
	 * @param version
	 * @param info
	 */
	private VistaImagingPathologyDataSourceProvider(String name, double version, String info)
	{
		super(name, version, info);

		services = new TreeSet<ProviderService>();
		
		/*
		services.add(
				new ProviderService(
					this,
					PathologyDataSourceSpi.class,
					VistaImagingPathologyDataSourceService.SUPPORTED_PROTOCOL,
					1.0F,
					VistaImagingPathologyDataSourceService.class)
				);
		*/
		services.add(
				new ProviderService(
					this,
					PathologyDataSourceSpi.class,
					VistaImagingPathologyDataSourceServiceImpl.SUPPORTED_PROTOCOL,
					1.0F,
					VistaImagingPathologyDataSourceServiceImpl.class)
				);
	}
	

	@Override
	public SortedSet<ProviderService> getServices()
	{
		return Collections.unmodifiableSortedSet(services);
	}

}
