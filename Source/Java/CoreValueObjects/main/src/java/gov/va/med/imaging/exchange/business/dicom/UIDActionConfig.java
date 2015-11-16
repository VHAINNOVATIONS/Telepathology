package gov.va.med.imaging.exchange.business.dicom;

public class UIDActionConfig
{
	private String uid;
	private String description;
	private String actionCode;
	private String actionComment;
	private String iconFilename;
	public String getUid()
	{
		return uid;
	}
	public void setUid(String uid)
	{
		this.uid = uid;
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	public String getActionCode()
	{
		return actionCode;
	}
	public void setActionCode(String actionCode)
	{
		this.actionCode = actionCode;
	}
	public String getActionComment()
	{
		return actionComment;
	}
	public void setActionComment(String actionComment)
	{
		this.actionComment = actionComment;
	}
	public String getIconFilename() {
		return iconFilename;
	}
	public void setIconFilename(String iconFilename) {
		this.iconFilename = iconFilename;
	}
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actionCode == null) ? 0 : actionCode.hashCode());
		result = prime * result + ((actionComment == null) ? 0 : actionComment.hashCode());
		result = prime * result + ((iconFilename == null) ? 0 : iconFilename.hashCode());		
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((uid == null) ? 0 : uid.hashCode());
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
		final UIDActionConfig other = (UIDActionConfig) obj;
		if (actionCode == null)
		{
			if (other.actionCode != null)
				return false;
		}
		else if (!actionCode.equals(other.actionCode))
			return false;
		if (actionComment == null)
		{
			if (other.actionComment != null)
				return false;
		}
		else if (!actionComment.equals(other.actionComment))
			return false;
		if (iconFilename == null)
		{
			if (other.iconFilename != null)
				return false;
		}
		else if (!iconFilename.equals(other.iconFilename))
			return false;
		if (description == null)
		{
			if (other.description != null)
				return false;
		}
		else if (!description.equals(other.description))
			return false;
		if (uid == null)
		{
			if (other.uid != null)
				return false;
		}
		else if (!uid.equals(other.uid))
			return false;
		return true;
	}
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(uid + "^");
		sb.append(description + "^");
		sb.append(actionCode + "^");
		sb.append(actionComment);
		
		// TODO Auto-generated method stub
		return sb.toString();
	}
}
