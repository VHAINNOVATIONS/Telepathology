package gov.va.med.imaging.exchange.business.dicom;

public class ModalityConfig
{
	private String hostName;
	private String configTimestamp;
	private String manufacturer;
	private String model;
	private String modality;
	private String imagingService;
	private boolean isActive;
	public String getHostName()
	{
		return hostName;
	}
	public void setHostName(String hostName)
	{
		this.hostName = hostName;
	}
	public String getConfigTimestamp()
	{
		return configTimestamp;
	}
	public void setConfigTimestamp(String configTimestamp)
	{
		this.configTimestamp = configTimestamp;
	}
	public String getManufacturer()
	{
		return manufacturer;
	}
	public void setManufacturer(String manufacturer)
	{
		this.manufacturer = manufacturer;
	}
	public String getModel()
	{
		return model;
	}
	public void setModel(String model)
	{
		this.model = model;
	}
	public String getModality()
	{
		return modality;
	}
	public void setModality(String modality)
	{
		this.modality = modality;
	}
	public String getImagingService()
	{
		return imagingService;
	}
	public void setImagingService(String imagingService)
	{
		this.imagingService = imagingService;
	}
	public boolean isActive()
	{
		return isActive;
	}
	public void setActive(boolean isActive)
	{
		this.isActive = isActive;
	}
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((configTimestamp == null) ? 0 : configTimestamp.hashCode());
		result = prime * result + ((hostName == null) ? 0 : hostName.hashCode());
		result = prime * result + ((imagingService == null) ? 0 : imagingService.hashCode());
		result = prime * result + (isActive ? 1231 : 1237);
		result = prime * result + ((manufacturer == null) ? 0 : manufacturer.hashCode());
		result = prime * result + ((modality == null) ? 0 : modality.hashCode());
		result = prime * result + ((model == null) ? 0 : model.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ModalityConfig other = (ModalityConfig) obj;
		if (configTimestamp == null)
		{
			if (other.configTimestamp != null)
				return false;
		}
		else if (!configTimestamp.equals(other.configTimestamp))
			return false;
		if (hostName == null)
		{
			if (other.hostName != null)
				return false;
		}
		else if (!hostName.equals(other.hostName))
			return false;
		if (imagingService == null)
		{
			if (other.imagingService != null)
				return false;
		}
		else if (!imagingService.equals(other.imagingService))
			return false;
		if (isActive != other.isActive)
			return false;
		if (manufacturer == null)
		{
			if (other.manufacturer != null)
				return false;
		}
		else if (!manufacturer.equals(other.manufacturer))
			return false;
		if (modality == null)
		{
			if (other.modality != null)
				return false;
		}
		else if (!modality.equals(other.modality))
			return false;
		if (model == null)
		{
			if (other.model != null)
				return false;
		}
		else if (!model.equals(other.model))
			return false;
		return true;
	}
	
	

}
