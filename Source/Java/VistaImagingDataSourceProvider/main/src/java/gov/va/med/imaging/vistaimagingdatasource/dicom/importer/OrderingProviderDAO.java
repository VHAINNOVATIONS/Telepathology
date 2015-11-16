/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Nov, 2009
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

package gov.va.med.imaging.vistaimagingdatasource.dicom.importer;

import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.dicom.importer.OrderingLocation;
import gov.va.med.imaging.exchange.business.dicom.importer.OrderingProvider;
import gov.va.med.imaging.exchange.business.dicom.importer.Procedure;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderingProviderDAO extends BaseImporterDAO<OrderingProvider>
{
	private static String FIND_ALL = "PSB GETPROVIDER";
	//
	// Constructor
	//
	public OrderingProviderDAO(){}
	public OrderingProviderDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}


	public List<OrderingProvider> findProviders(String searchString) 
	throws MethodException, ConnectionException 
	{
		VistaQuery vm = generateFindProvidersQuery(searchString);
		String results = executeRPC(vm);
		return translateFindProviders(results);
		
	}

	public VistaQuery generateFindProvidersQuery(String searchString) throws MethodException
	{
		VistaQuery vm = new VistaQuery(FIND_ALL);
		vm.addParameter(VistaQuery.LITERAL, searchString.trim().toUpperCase());
		return vm;
	}

	public List<OrderingProvider> translateFindProviders(String result) 
	{
		// Create the OrderingLocation list
		List<OrderingProvider> providers = new ArrayList<OrderingProvider>();
		HashMap<Integer, String> providerMap = new HashMap<Integer, String>();
		
		// Split the result into lines
		String[] lines = StringUtils.Split(result, LINE_SEPARATOR);

		// If results were found, populate the list
		if (resultsFound(lines))
		{
			// Populate the dictionary, starting with line 1 (if it exists). Line 0 gives the record count.
			// Replace value if we find a longer one for a given IEN
			for (int i=1; i<lines.length; i++)
			{
				String[] fields = StringUtils.Split(lines[i], StringUtils.CARET);
				int ien = Integer.parseInt(fields[0]);
				String name = fields[1];
				
				if (!providerMap.containsKey(ien))
				{
					// Don't have a provider for this ien yet. Add it.
					providerMap.put(ien, name);
				}
				else if (providerMap.get(ien) != null && (name.length() > providerMap.get(ien).length()))
				{
					// We do have a value for this key, but the new one is longer, so replace it with the new one
					providerMap.put(ien, name);
				}
			}
	
			// Now that we have filtered down the results, create the list of providers
			for (int i : providerMap.keySet())
			{
				providers.add(new OrderingProvider(i, providerMap.get(i)));
			}
		}
		return providers;	
	}
	private boolean resultsFound(String[] lines) 
	{
		// if we get one result line back, and the ien is negative, no results were found
		if (lines[0].equals("1"))
		{
			String[] fields = StringUtils.Split(lines[1], StringUtils.CARET);
			int ien = Integer.parseInt(fields[0]);
			if (ien < 0)
			{
				return false;
			}
		}
		
		// If we made it to here, we have actual results
		return true;
	}
	
}
