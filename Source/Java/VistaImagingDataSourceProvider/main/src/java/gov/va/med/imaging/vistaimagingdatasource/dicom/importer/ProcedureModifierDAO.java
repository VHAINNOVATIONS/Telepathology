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
import gov.va.med.imaging.exchange.business.dicom.importer.OrderingLocation;
import gov.va.med.imaging.exchange.business.dicom.importer.ProcedureModifier;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.util.ArrayList;
import java.util.List;

public class ProcedureModifierDAO extends BaseImporterDAO<ProcedureModifier>
{
	private static String FIND_ALL = "MAG DICOM RADIOLOGY MODIFIERS";
	
	//
	// Constructor
	//
	public ProcedureModifierDAO(){}
	public ProcedureModifierDAO(VistaSessionFactory sessionFactory)
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
	public List<ProcedureModifier> translateFindAll(String result) 
	{
		// Create the OrderingLocation list
		List<ProcedureModifier> modifiers = new ArrayList<ProcedureModifier>();
		
		// Split the result into lines
		String[] lines = StringUtils.Split(result, LINE_SEPARATOR);

		// Populate the list, starting with line 1 (if it exists). Line 0 gives the record count.
		for (int i=1; i<lines.length; i++)
		{
			
			// Split the line into fields
			String[] fields = StringUtils.Split(lines[i], StringUtils.CARET);

			// Create the procedure modifier
			ProcedureModifier modifier = new ProcedureModifier();
			modifier.setId(Integer.parseInt(fields[1]));
			modifier.setImagingTypeId(Integer.parseInt(fields[2]));
			modifier.setName(fields[0]);
			
			// Add it to the list
			modifiers.add(modifier);
		}
		
		return modifiers;	
	}
	
}
