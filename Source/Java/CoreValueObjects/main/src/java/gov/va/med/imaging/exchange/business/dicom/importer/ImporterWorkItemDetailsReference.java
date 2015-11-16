package gov.va.med.imaging.exchange.business.dicom.importer;

import gov.va.med.imaging.exchange.business.Patient;

public class ImporterWorkItemDetailsReference 
{
    private int networkLocationIen;
    private String mediaBundleStagingRootDirectory;
    private Patient vaPatientFromStaging;

    
	public void setNetworkLocationIen(int networkLocationIen) 
	{
		this.networkLocationIen = networkLocationIen;
	}
	public int getNetworkLocationIen() 
	{
		return networkLocationIen;
	}
	public void setMediaBundleStagingRootDirectory(
			String mediaBundleStagingRootDirectory) 
	{
		this.mediaBundleStagingRootDirectory = mediaBundleStagingRootDirectory;
	}
	public String getMediaBundleStagingRootDirectory() 
	{
		return mediaBundleStagingRootDirectory;
	}
	public void setVaPatientFromStaging(Patient vaPatientFromStaging) {
		this.vaPatientFromStaging = vaPatientFromStaging;
	}
	public Patient getVaPatientFromStaging() {
		return vaPatientFromStaging;
	}
}
