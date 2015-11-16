package gov.va.med.imaging.dicom.dcftoolkit.common.license.info;


import gov.va.med.imaging.ImagingMBean;

import java.lang.management.ManagementFactory;
import java.util.Hashtable;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.MalformedObjectNameException;
import javax.management.InstanceAlreadyExistsException;

// import com.lbs.DCF.DCFVersion; -- DCF3.3.x
// import com.lbs.DCFUtil.OSUtil; -- DCF3.2.x
import com.lbs.DCF.OSUtil;

public class DCFLicenseInfo implements DCFLicenseInfoMBean {

	private static DCFLicenseInfo instance_ = null;
	
	private static ObjectName DCFLicenseInfoMBeanName = null;

	
	private DCFLicenseInfo(){
	
	}
	
	public synchronized static DCFLicenseInfo getInstance(){
		if(instance_ == null){
			instance_ = new DCFLicenseInfo();
			registerResourceMBeans();
		}
		return instance_;
	}
	
	
	@Override
	public String getDCFLicenseExpirationDate() {
		String expirationDate = ""; // "99991231" means permanent license;
		//Calendar date = null;
		try{
			expirationDate = OSUtil.getLicenseCfgAttrValue("expiration_date");
		}
		catch(Exception X){
		}
		
		//if(expirationDate != null){
		//	String yearString = expirationDate.substring(0, 3);
		//	String monthString = expirationDate.substring(4, 5);
		//	String dayString = expirationDate.substring(6, 7);
		
		//	int year = Integer.valueOf(yearString);
		//	int month = Integer.valueOf(monthString);
		//	int day = Integer.valueOf(dayString);
			
		//	date = Calendar.getInstance();
		//	date.set(year, month, day);
		//}
		return expirationDate;
		//return date.toString();
	}
	

	@Override
	public String getDCFVersion() {
		String version = "Unknown";
		try{
			version = OSUtil.getLicenseCfgAttrValue("prod_version");
//			version = DCFVersion.version();
		}
		catch(Exception X){
		}
		return version;
	}
	
	/**
	 * This method should only be called once, else MBean exceptions will occur.
	 */
	private static void registerResourceMBeans()
    {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();	
		
		if(DCFLicenseInfoMBeanName == null){
			try
	        {
				Hashtable<String, String> mBeanProperties = new Hashtable<String, String>();
				mBeanProperties.put( "type", "DCFLicenseInfo" );
				mBeanProperties.put( "name", "License");
				ObjectName name = new ObjectName(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, mBeanProperties);
	            mbs.registerMBean(DCFLicenseInfo.getInstance(), name);
	        } 
			catch (MalformedObjectNameException mioe){ 
				mioe.printStackTrace();
			}
			catch (InstanceAlreadyExistsException iaee){ 
				iaee.printStackTrace();
			}
			catch (Exception e){ 
				e.printStackTrace();
			}
		}
    }	
}
