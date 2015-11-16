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

public class FluoroDose extends Dose
{
//  Dose (RP) Total (Air Kerma equivalent) (EV [113725, DCM, “Dose ( RP) Total”])
//  Dose Area Product Total (AKAP equivalent) (EV [113722, DCM, “Dose Area Product Total”])
//  Fluoro Time Total (EV [113730, DCM, “Total Fluoro Time”])
//  Fluoro Dose (RP) Total (EV [113728, DCM, “Fluoro Dose ( RP) Total”])
//  Fluoro Dose Area Product Total (EV [113726, DCM, “Fluoro Dose Area Product Total”])
//  Cine (Acquisition) Dose (RP) Total (EV [113729, DCM, “Acquisition Dose ( RP) Total”])
//  Cine (Acquisition) Dose Area Product Total (EV [113727, DCM, “Acquisition Dose Area Product Total”])
//  Cine (Acquisition) Time (EV [113855, DCM, “Total Acquisition Time”])

	private String doseRpTotal = "";
	private String doseAreaProductTotal = "";
	private String fluoroTimeTotal = "";
	private String fluoroDoseRpTotal = "";
	private String fluoroDoseAreaProductTotal = "";
	private String cineDoseRpTotal = "";
	private String cineDoseAreaProductTotal = "";
	private String cineTime = "";
	
	public String getType()
	{
		return "FLUORO";
	}
	
	public String getDoseRpTotal()
	{
		return doseRpTotal;
	}
	public void setDoseRpTotal(String doseRpTotal)
	{
		this.doseRpTotal = doseRpTotal;
	}
	public String getDoseAreaProductTotal()
	{
		return doseAreaProductTotal;
	}
	public void setDoseAreaProductTotal(String doseAreaProductTotal)
	{
		this.doseAreaProductTotal = doseAreaProductTotal;
	}
	public String getFluoroTimeTotal()
	{
		return fluoroTimeTotal;
	}
	public void setFluoroTimeTotal(String fluoroTimeTotal)
	{
		this.fluoroTimeTotal = fluoroTimeTotal;
	}
	public String getFluoroDoseRpTotal()
	{
		return fluoroDoseRpTotal;
	}
	public void setFluoroDoseRpTotal(String fluoroDoseRpTotal)
	{
		this.fluoroDoseRpTotal = fluoroDoseRpTotal;
	}
	public String getFluoroDoseAreaProductTotal()
	{
		return fluoroDoseAreaProductTotal;
	}
	public void setFluoroDoseAreaProductTotal(String fluoroDoseAreaProductTotal)
	{
		this.fluoroDoseAreaProductTotal = fluoroDoseAreaProductTotal;
	}
	public String getCineDoseRpTotal()
	{
		return cineDoseRpTotal;
	}
	public void setCineDoseRpTotal(String cineDoseRpTotal)
	{
		this.cineDoseRpTotal = cineDoseRpTotal;
	}
	public String getCineDoseAreaProductTotal()
	{
		return cineDoseAreaProductTotal;
	}
	public void setCineDoseAreaProductTotal(String cineDoseAreaProductTotal)
	{
		this.cineDoseAreaProductTotal = cineDoseAreaProductTotal;
	}
	public String getCineTime()
	{
		return cineTime;
	}
	public void setCineTime(String cineTime)
	{
		this.cineTime = cineTime;
	}

	@Override
	public int hashCode()
	{
		String fullString = doseRpTotal + doseAreaProductTotal + fluoroTimeTotal +
				fluoroDoseRpTotal + fluoroDoseAreaProductTotal + cineDoseRpTotal +
				cineDoseAreaProductTotal + cineTime;
		return fullString.hashCode();
	}
	
//	private String doseRpTotal;
//	private String doseAreaProductTotal;
//	private String fluoroTimeTotal;
//	private String fluoroDoseRpTotal;
//	private String fluoroDoseAreaProductTotal;
//	private String cineDoseRpTotal;
//	private String cineDoseAreaProductTotal;
//	private String cineTime;

	@Override
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("  Dose (RP) Total: " + doseRpTotal + System.getProperty("line.separator"));
		buffer.append("  Dose Area Product Total: " + doseAreaProductTotal + System.getProperty("line.separator"));
		buffer.append("  Fluoro Time Total: " + fluoroTimeTotal + System.getProperty("line.separator"));
		buffer.append("  Fluoro Dose (RP) Total: " + fluoroDoseRpTotal + System.getProperty("line.separator"));
		buffer.append("  Fluoro Dose Area Product Total: " + fluoroDoseAreaProductTotal + System.getProperty("line.separator"));
		buffer.append("  Cine Dose (RP) Total: " + cineDoseRpTotal + System.getProperty("line.separator"));
		buffer.append("  Cine Dose Area Product Total: " + cineDoseAreaProductTotal + System.getProperty("line.separator"));
		buffer.append("  Cine Time: " + cineTime + System.getProperty("line.separator"));
		return buffer.toString();
	}

}
