package gov.va.med.imaging.exchange.business.dicom;

public class InstrumentConfig
{
	public static final String ConsultServiceCode = "CON";
	public static final String RadiologyServiceCode = "RAD";
	
	private String hostName;
	private String configTimestamp;
	private String nickName;
	private String description;
	private String service;
	private int port;
	private String siteId = null;
	private String site;
	private String machineId;
	
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
	public String getNickName()
	{
		return nickName;
	}
	public void setNickName(String nickName)
	{
		this.nickName = nickName;
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	public String getService()
	{
		return service;
	}
	public void setService(String service)
	{
		this.service = service;
	}
	public int getPort()
	{
		return port;
	}
	public void setPort(int port)
	{
		this.port = port;
	}
	public String getSiteId()
	{
		return siteId;
	}
	public void setSiteId(String siteId)
	{
		this.siteId = siteId;
	}
	public String getSite()
	{
		return site;
	}
	public void setSite(String site)
	{
		this.site = site;
	}
	public String getMachineId()
	{
		return machineId;
	}
	public void setMachineId(String machineId)
	{
		this.machineId = machineId;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((configTimestamp == null) ? 0 : configTimestamp.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((hostName == null) ? 0 : hostName.hashCode());
		result = prime * result + ((machineId == null) ? 0 : machineId.hashCode());
		result = prime * result + ((nickName == null) ? 0 : nickName.hashCode());
		result = prime * result + port;
		result = prime * result + ((service == null) ? 0 : service.hashCode());
		result = prime * result + ((site == null) ? 0 : site.hashCode());
		result = prime * result + ((siteId == null) ? 0 : siteId.hashCode());
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
		final InstrumentConfig other = (InstrumentConfig) obj;
		if (configTimestamp == null)
		{
			if (other.configTimestamp != null)
				return false;
		}
		else if (!configTimestamp.equals(other.configTimestamp))
			return false;
		if (description == null)
		{
			if (other.description != null)
				return false;
		}
		else if (!description.equals(other.description))
			return false;
		if (hostName == null)
		{
			if (other.hostName != null)
				return false;
		}
		else if (!hostName.equals(other.hostName))
			return false;
		if (machineId == null)
		{
			if (other.machineId != null)
				return false;
		}
		else if (!machineId.equals(other.machineId))
			return false;
		if (nickName == null)
		{
			if (other.nickName != null)
				return false;
		}
		else if (!nickName.equals(other.nickName))
			return false;
		if (port != other.port)
			return false;
		if (service == null)
		{
			if (other.service != null)
				return false;
		}
		else if (!service.equals(other.service))
			return false;
		if (site == null)
		{
			if (other.site != null)
				return false;
		}
		else if (!site.equals(other.site))
			return false;
		if (siteId == null)
		{
			if (other.siteId != null)
				return false;
		}
		else if (!siteId.equals(other.siteId))
			return false;
		return true;
	}

	public boolean isRadiologyInstrument()
	{
		return this.service.toUpperCase().equals("RAD");
	}
	
	public boolean isConsultInstrument()
	{
		return this.service.toUpperCase().equals("CON");
	}
	

}
