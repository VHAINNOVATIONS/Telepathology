/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Feb 22, 2008
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
package gov.va.med.imaging.versions;

import java.util.ArrayList;
import java.util.List;

/**
 * @author VHAISWWERFEJ
 *
 */
public class ImagingService 
implements Comparable<ImagingService>
{
	private String version;
	private String applicationType;
	private String applicationPath;
	private List<ImagingOperation> operations = new ArrayList<ImagingOperation>();
	
	public ImagingService()
	{
		applicationType = version = applicationPath = "";
	}

	public ImagingService(String version, String applicationType,
			String applicationPath) {
		super();
		this.version = version;
		this.applicationType = applicationType;
		this.applicationPath = applicationPath;
	}		

	public List<ImagingOperation> getOperations() {
		return operations;
	}

	public void setOperations(List<ImagingOperation> operations) {
		this.operations = operations;
	}

	public String getApplicationType() {
		return applicationType;
	}

	public void setApplicationType(String applicationType) {
		this.applicationType = applicationType;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getApplicationPath() {
		return applicationPath;
	}

	public void setApplicationPath(String applicationPath) {
		this.applicationPath = applicationPath;
	}

	@Override
	public String toString() {
		return applicationType + " [" + version + "]";
	}

	@Override
	public int compareTo(ImagingService o) 
	{	/*
		int diff = this.applicationPath.compareTo(o.applicationPath);
		if(diff != 0)
			return diff;
		*/
		int diff = this.applicationType.compareTo(o.applicationType);
		if(diff != 0)
			return diff;
		try
		{
			double thisV = Double.parseDouble(this.version);
			double thatV = Double.parseDouble(o.version);
			if(thisV < thatV)
				diff = -1;
			if(thatV < thisV)
				diff = 1;
		}
		catch(Exception ex) { diff = 0;}
		if(diff != 0)
			return diff;
		return this.version.compareTo(o.version);
	}

}
