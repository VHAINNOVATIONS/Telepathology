/**
 * Package: MAG - VistA Imaging
 * WARNING: Per VHA Directive 2004-038, this routine should not be modified.
 * @date May 2, 2013
 * Site Name:  Washington OI Field Office, Silver Spring, MD
 * @author vhaiswlouthj
 * @version 1.0
 *
 * ----------------------------------------------------------------
 * Property of the US Government.
 * No permission to copy or redistribute this software is given.
 * Use of unreleased versions of this software requires the user
 * to execute a written test agreement with the VistA Imaging
 * Development Office of the Department of Veterans Affairs,
 * telephone (301) 734-0100.
 * 
 * The Food and Drug Administration classifies this software as
 * a Class II medical device.  As such, it may not be changed
 * in any way.  Modifications to this software may result in an
 * adulterated medical device under 21CFR820, the use of which
 * is considered to be a violation of US Federal Statutes.
 * ----------------------------------------------------------------
 */
package gov.va.med.imaging.exchange.business.dicom.rdsr;

public class CTDose extends Dose
{
	private String meanCTDIvol = "";
	private String dlp = "";
	private String phantomType = "";
	private String anatomicTargetRegion = "";
	
	public String getType()
	{
		return "CT";
	}

	public String getMeanCTDIvol()
	{
		return meanCTDIvol;
	}
	public void setMeanCTDIvol(String meanCTDIvol)
	{
		this.meanCTDIvol = meanCTDIvol;
	}
	public String getDlp()
	{
		return dlp;
	}
	public void setDlp(String dlp)
	{
		this.dlp = dlp;
	}
	public String getPhantomType()
	{
		return phantomType;
	}
	public void setPhantomType(String phantomType)
	{
		this.phantomType = phantomType;
	}
	public String getAnatomicTargetRegion()
	{
		return anatomicTargetRegion;
	}
	public void setAnatomicTargetRegion(String anatomicTargetRegion)
	{
		this.anatomicTargetRegion = anatomicTargetRegion;
	}
	
	@Override
	public int hashCode()
	{
		return irradiationEventUid.hashCode();
	}
	@Override
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("  Irradiation Event Uid: " + irradiationEventUid + System.getProperty("line.separator"));
		buffer.append("  Mean CTDIvol: " + meanCTDIvol + System.getProperty("line.separator"));
		buffer.append("  DLP: " + dlp + System.getProperty("line.separator"));
		buffer.append("  Phantom Type: " + phantomType + System.getProperty("line.separator"));
		buffer.append("  Anatomic Target Region: " + anatomicTargetRegion + System.getProperty("line.separator"));
		return buffer.toString();
	}
	
}
