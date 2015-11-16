/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Jul 8, 2008
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
package gov.va.med.imaging.vistaimagingdatasource.configuration;

import gov.va.med.imaging.vistaimagingdatasource.VistaImageDataSourceService;
import gov.va.med.imaging.vistaimagingdatasource.VistaImageDataSourceServiceV0;
import gov.va.med.imaging.vistaimagingdatasource.VistaImageDataSourceServiceV2;
import gov.va.med.imaging.vistaimagingdatasource.VistaImagingDicomDataSourceService;
import gov.va.med.imaging.vistaimagingdatasource.VistaImagingExternalPackageDataSourceService;
import gov.va.med.imaging.vistaimagingdatasource.VistaImagingExternalPackageDataSourceServiceV0;
import gov.va.med.imaging.vistaimagingdatasource.VistaImagingExternalPackageDataSourceServiceV2;
import gov.va.med.imaging.vistaimagingdatasource.VistaImagingImageAccessLoggingDataSourceService;
import gov.va.med.imaging.vistaimagingdatasource.VistaImagingPatientDataSourceService;
import gov.va.med.imaging.vistaimagingdatasource.VistaImagingStudyGraphDataSourceService;
import gov.va.med.imaging.vistaimagingdatasource.VistaImagingStudyGraphDataSourceServiceV0;
import gov.va.med.imaging.vistaimagingdatasource.VistaImagingStudyGraphDataSourceServiceV1;
import gov.va.med.imaging.vistaimagingdatasource.VistaImagingStudyGraphDataSourceServiceV2;
import gov.va.med.imaging.vistaimagingdatasource.VistaImagingVistaRadDataSourceService;
import gov.va.med.imaging.vistaimagingdatasource.VistaImagingVistaRadDataSourceServiceV2;
import gov.va.med.imaging.vistaimagingdatasource.VistaImagingVistaRadDataSourceServiceV3;
import gov.va.med.imaging.vistaimagingdatasource.VistaImagingVistaRadImageDataSourceServiceV1;
import gov.va.med.imaging.vistaimagingdatasource.VistaImagingVistaRadImageDataSourceServiceV2;
import gov.va.med.imaging.vistaimagingdatasource.VistaImagingVistaRadImageDataSourceServiceV3;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

/**
 * @author VHAISWWERFEJ
 *
 */
public class VistaImagingConfiguration 
implements Serializable 
{	
	private final static long serialVersionUID = 1L;
	
	private HashMap<Integer, String> imageTypeAbstracts;
	private HashMap<String, String> imageFilenameAbstracts;
	private HashMap<String, String> dataSouceImagingVersions;
	
	public VistaImagingConfiguration()
	{
		super();
		imageTypeAbstracts = new HashMap<Integer, String>();
		imageFilenameAbstracts = new HashMap<String, String>();
		dataSouceImagingVersions = new HashMap<String, String>();
	}
	
	public String getDataSourceImagingVersion(Class<?> dataSourceClass)
	{
		String className = dataSourceClass.getSimpleName();
		return dataSouceImagingVersions.get(className);
	}
	
	/**
	 * @return the imageTypeAbstracts
	 */
	public HashMap<Integer, String> getImageTypeAbstracts() {
		return imageTypeAbstracts;
	}

	/**
	 * @param imageTypeAbstracts the imageTypeAbstracts to set
	 */
	public void setImageTypeAbstracts(HashMap<Integer, String> imageTypeAbstracts) {
		this.imageTypeAbstracts = imageTypeAbstracts;
	}

	/**
	 * @return the imageFilenameAbstracts
	 */
	public HashMap<String, String> getImageFilenameAbstracts() {
		return imageFilenameAbstracts;
	}

	/**
	 * @param imageFilenameAbstracts the imageFilenameAbstracts to set
	 */
	public void setImageFilenameAbstracts(
			HashMap<String, String> imageFilenameAbstracts) {
		this.imageFilenameAbstracts = imageFilenameAbstracts;
	}

	/**
	 * Retrieve the necessary canned filename for the specified image typex
	 * @param imageType
	 * @return
	 */
	public String getImageAbstract(int imageType)
	{			
		return imageTypeAbstracts.get(new Integer(imageType));
	}
	
	/**
	 * Retrieve the necessary canned filename for the specified filename
	 * @param filename
	 * @return
	 */
	public String getImageAbstract(String filename)
	{	
		String name = extractNameFromFilename(filename);		
		return imageFilenameAbstracts.get(name.toLowerCase());
	}
	
	private String extractNameFromFilename(String filename)
	{
		File file = new File(filename);
		return file.getName();
	}
	
	/**
	 * @return the dataSouceImagingVersions
	 */
	public HashMap<String, String> getDataSouceImagingVersions() {
		return dataSouceImagingVersions;
	}

	/**
	 * @param dataSouceImagingVersions the dataSouceImagingVersions to set
	 */
	public void setDataSouceImagingVersions(
			HashMap<String, String> dataSouceImagingVersions) {
		this.dataSouceImagingVersions = dataSouceImagingVersions;
	}

	public static VistaImagingConfiguration createDefaultConfiguration()
	{
		System.out.println("Creating default VistaConfiguration");
		VistaImagingConfiguration vistaConfiguration = new VistaImagingConfiguration();		
		vistaConfiguration.imageTypeAbstracts.put(10, "ABSPACI.jpg");
		vistaConfiguration.imageTypeAbstracts.put(12, "ABSPACG.jpg");
		vistaConfiguration.imageTypeAbstracts.put(13, "absekg.jpg");
		vistaConfiguration.imageTypeAbstracts.put(14, "abscine.jpg");
		vistaConfiguration.imageTypeAbstracts.put(21, "magavi.jpg");
		vistaConfiguration.imageTypeAbstracts.put(101, "maghtml.jpg");
		vistaConfiguration.imageTypeAbstracts.put(102, "magdoc.jpg");
		vistaConfiguration.imageTypeAbstracts.put(103, "magtext.jpg");
		vistaConfiguration.imageTypeAbstracts.put(104, "magpdf.jpg");
		vistaConfiguration.imageTypeAbstracts.put(105, "magrtf.jpg");
		vistaConfiguration.imageTypeAbstracts.put(106, "magwav.jpg");
		vistaConfiguration.imageTypeAbstracts.put(107, "maghtml.jpg"); // XML
		// be sure to put filenames here in lowercase (for the key)
		vistaConfiguration.imageFilenameAbstracts.put("magsensitive.bmp", "magsensitive.JPG");
		
		vistaConfiguration.dataSouceImagingVersions.put(
				VistaImagingStudyGraphDataSourceService.class.getSimpleName(), 
				VistaImagingStudyGraphDataSourceService.MAG_REQUIRED_VERSION);
		vistaConfiguration.dataSouceImagingVersions.put(
				VistaImagingStudyGraphDataSourceServiceV0.class.getSimpleName(), 
				VistaImagingStudyGraphDataSourceServiceV0.MAG_REQUIRED_VERSION);
		vistaConfiguration.dataSouceImagingVersions.put(
				VistaImagingStudyGraphDataSourceServiceV1.class.getSimpleName(), 
				VistaImagingStudyGraphDataSourceServiceV1.MAG_REQUIRED_VERSION);
		vistaConfiguration.dataSouceImagingVersions.put(
				VistaImagingStudyGraphDataSourceServiceV2.class.getSimpleName(), 
				VistaImagingStudyGraphDataSourceServiceV2.MAG_REQUIRED_VERSION);
		vistaConfiguration.dataSouceImagingVersions.put(
				VistaImagingPatientDataSourceService.class.getSimpleName(), 
				VistaImagingPatientDataSourceService.MAG_REQUIRED_VERSION);
		vistaConfiguration.dataSouceImagingVersions.put(
				VistaImageDataSourceService.class.getSimpleName(), 
				VistaImageDataSourceService.MAG_REQUIRED_VERSION);
		vistaConfiguration.dataSouceImagingVersions.put(
				VistaImageDataSourceServiceV0.class.getSimpleName(), 
				VistaImageDataSourceServiceV0.MAG_REQUIRED_VERSION);
		vistaConfiguration.dataSouceImagingVersions.put(
				VistaImageDataSourceServiceV2.class.getSimpleName(), 
				VistaImageDataSourceServiceV2.MAG_REQUIRED_VERSION);
		vistaConfiguration.dataSouceImagingVersions.put(
				VistaImagingImageAccessLoggingDataSourceService.class.getSimpleName(), 
				VistaImagingImageAccessLoggingDataSourceService.MAG_REQUIRED_VERSION);
		vistaConfiguration.dataSouceImagingVersions.put(
				VistaImagingExternalPackageDataSourceService.class.getSimpleName(), 
				VistaImagingExternalPackageDataSourceService.MAG_REQUIRED_VERSION);
		vistaConfiguration.dataSouceImagingVersions.put(
				VistaImagingExternalPackageDataSourceServiceV0.class.getSimpleName(), 
				VistaImagingExternalPackageDataSourceServiceV0.MAG_REQUIRED_VERSION);
		/*
		vistaConfiguration.dataSouceImagingVersions.put(
				VistaImagingVistaRadImageDataSourceServiceV0.class.getSimpleName(),
				VistaImagingVistaRadImageDataSourceServiceV0.MAG_REQUIRED_VERSION);
		*/
		vistaConfiguration.dataSouceImagingVersions.put(
				VistaImagingVistaRadImageDataSourceServiceV1.class.getSimpleName(),
				VistaImagingVistaRadImageDataSourceServiceV1.MAG_REQUIRED_VERSION);
		vistaConfiguration.dataSouceImagingVersions.put(
				VistaImagingVistaRadImageDataSourceServiceV2.class.getSimpleName(),
				VistaImagingVistaRadImageDataSourceServiceV2.MAG_REQUIRED_VERSION);
		vistaConfiguration.dataSouceImagingVersions.put(
				VistaImagingVistaRadImageDataSourceServiceV3.class.getSimpleName(),
				VistaImagingVistaRadImageDataSourceServiceV3.MAG_REQUIRED_VERSION);
		vistaConfiguration.dataSouceImagingVersions.put(
				VistaImagingVistaRadDataSourceService.class.getSimpleName(),
				VistaImagingVistaRadDataSourceService.MAG_REQUIRED_VERSION);
		/*
		vistaConfiguration.dataSouceImagingVersions.put(
				VistaImagingVistaRadDataSourceServiceV0.class.getSimpleName(),
				VistaImagingVistaRadDataSourceServiceV0.MAG_REQUIRED_VERSION);
		*/
		vistaConfiguration.dataSouceImagingVersions.put(
				VistaImagingVistaRadDataSourceServiceV2.class.getSimpleName(),
				VistaImagingVistaRadDataSourceServiceV2.MAG_REQUIRED_VERSION);
		vistaConfiguration.dataSouceImagingVersions.put(
				VistaImagingVistaRadDataSourceServiceV3.class.getSimpleName(),
				VistaImagingVistaRadDataSourceServiceV3.MAG_REQUIRED_VERSION);
		vistaConfiguration.dataSouceImagingVersions.put(
				VistaImagingDicomDataSourceService.class.getSimpleName(),
				VistaImagingDicomDataSourceService.MAG_REQUIRED_VERSION);
		vistaConfiguration.dataSouceImagingVersions.put(
				VistaImagingExternalPackageDataSourceServiceV2.class.getSimpleName(),
				VistaImagingExternalPackageDataSourceServiceV2.MAG_REQUIRED_VERSION);
		
		// no need to have Document or DocumentSet here since they extend other SPI implementations
		
		return vistaConfiguration;
	}
}
