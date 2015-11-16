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

package gov.va.med.imaging.vistaimagingdatasource.dicom.storage;

import gov.va.med.imaging.exchange.business.dicom.InstanceFile;
import gov.va.med.imaging.exchange.business.storage.exceptions.CreationException;
import gov.va.med.imaging.url.vista.StringUtils;
import gov.va.med.imaging.url.vista.VistaQuery;
import gov.va.med.imaging.vistaimagingdatasource.common.EntityDAO;
import gov.va.med.imaging.vistaimagingdatasource.common.VistaSessionFactory;

import java.util.HashMap;

public class InstanceFileDAO extends EntityDAO<InstanceFile>
{
	private String RPC_ATTACH_FILE = "MAGV ATTACH IMAGE INSTANCE";
	
	private String DB_PARENT_IEN = "SOP INSTANCE REFERENCE"; // was "PARENT IEN"; // the immediate parent of the child entity
	private String DB_CHILD_IEN = "ARTIFACT REFERENCE"; // IEN to storage DB Artifact record (ER is 1:1)

//	private String IFI_IEN = "IFIEN";
	private String IFI_ARTIFACT_TOKEN = "ARTIFACT TOKEN";
	private String IFI_ORIGINAL = "ORIGINAL SOP INSTANCE"; // was "IS ORIGINAL";
	private String IFI_CONFIDENTIAL = "CONFIDENTIAL"; // was "IS CONFIDENTIAL";
	private String IFI_DELETE_DATIME = "DELETION DATE/TIME"; // was "DELETION DATE-TIME"
	private String IFI_DELETED_BY = "DELETION BY";
	private String IFI_DELETE_REASON = "DELETION REASON";
	private String IFI_IMAGE_TYPE = "IMAGE TYPE";
	private String IFI_DERIVATION_DESCRIPTION = "DERIVATION DESCRIPTION";
	private String IFI_COMPRESION_RATIO = "COMPRESSION RATIO";
	private String IFI_COMPRESSION_METHOD = "COMPRESSION METHOD";

	// Constructor
	public InstanceFileDAO(VistaSessionFactory sessionFactory)
	{
		this.setSessionFactory(sessionFactory);
	}

	@Override
	public VistaQuery generateCreateQuery(InstanceFile instanceFile) {
		VistaQuery vm = new VistaQuery(RPC_ATTACH_FILE);
		HashMap <String, String> hm = new HashMap <String, String>();
		hm.put("1", DB_PARENT_IEN + dbSeparator + instanceFile.getSOPInstanceIEN());
		hm.put("2", IFI_ARTIFACT_TOKEN + dbSeparator + instanceFile.getArtifactToken());
		hm.put("3", IFI_CONFIDENTIAL + dbSeparator + instanceFile.getIsConfidential());
		hm.put("4", IFI_ORIGINAL + dbSeparator + instanceFile.getIsOriginal());
		hm.put("5", IFI_DELETE_DATIME + dbSeparator + instanceFile.getDeleteDateTime());
		hm.put("6", IFI_DELETED_BY + dbSeparator + instanceFile.getDeletedBy());
		hm.put("7", IFI_DELETE_REASON + dbSeparator + instanceFile.getDeleteReason());
		hm.put("8", IFI_IMAGE_TYPE + dbSeparator + instanceFile.getImageType());
		hm.put("9", IFI_DERIVATION_DESCRIPTION + dbSeparator + instanceFile.getDerivationDesc());
		hm.put("10", IFI_COMPRESION_RATIO + dbSeparator + instanceFile.getCompressionRatio());
		hm.put("11", IFI_COMPRESSION_METHOD + dbSeparator + instanceFile.getCompressionMethod());
		hm.put("12", DB_CHILD_IEN + dbSeparator + instanceFile.getArtifactFileId());
		vm.addParameter(VistaQuery.LIST, hm);
		return vm;
	}

	@Override
	public InstanceFile translateCreate(InstanceFile instanceFile, String returnValue)  throws CreationException
	{
		instanceFile.setIEN(translateNewEntityIEN(returnValue, true));
		return instanceFile;
	}
	
	
}
