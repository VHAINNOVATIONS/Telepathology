/**
 * 
 */
package gov.va.med.imaging.wado.query;

enum ComplianceType
{
	WADO("Enforces strict WADO compliance."), 
	VA("Enforces VA-WADO compliance checking."), 
	XCHANGE("Enforces VA-XChange compliance checking."),
	FEDERATION("Enforced VA ViX to ViX Federation compliance checking"),
	ACCELERATOR("Enforce VA ViX to ViX image only compliance checking for retrieving images from a Jukebox"),
	NONE("No compliance checking or default value implementation."),
	CDTP("Compliance for request from Clinical Display"),
	VRTP("Compliance for request from VistARad"),
	PATCH83_VFTP("Patch 83 Federation");
	
	private String description;
	ComplianceType(String description)
	{
		this.description = description;
	}
	
	public String getDescription()
	{
		return description;
	}
}