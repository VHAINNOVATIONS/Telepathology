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

import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.WorkItem;
import gov.va.med.imaging.exchange.business.dicom.importer.DiagnosticCode;
import gov.va.med.imaging.exchange.business.dicom.importer.ImagingLocation;
import gov.va.med.imaging.exchange.business.dicom.importer.ImporterUtils;
import gov.va.med.imaging.exchange.business.dicom.importer.Procedure;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

public class ImagingLocationDAO extends BaseImporterDAO<ImagingLocation>
{
	private static String FIND_ALL = "MAGV GET RAD IMAGING LOCATIONS";
	//
	// Constructor
	//
	public ImagingLocationDAO(){}
	public ImagingLocationDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}
	
	@Override
	public VistaQuery generateFindAllQuery() throws MethodException
	{
		VistaQuery vm = new VistaQuery(FIND_ALL);
		return vm;
	}
	
	@Override
	public List<ImagingLocation> translateFindAll(String result) 
	{
		XStream xstream = ImporterUtils.getXStream();
		xstream.alias("ArrayOfImagingLocation", List.class);
		List<ImagingLocation> locations = (List<ImagingLocation>) xstream.fromXML(result);
		return locations;	
		
		// Add fake data
//		List<ImagingLocation> locations = new ArrayList<ImagingLocation>();
//		locations.add(new ImagingLocation(1, "Location1", "NO CREDIT"));
//		locations.add(new ImagingLocation(2, "Location2", "PARTIAL CREDIT"));
//		locations.add(new ImagingLocation(3, "Location3", "FULL CREDIT"));
//		return locations;	
	}
	
}
