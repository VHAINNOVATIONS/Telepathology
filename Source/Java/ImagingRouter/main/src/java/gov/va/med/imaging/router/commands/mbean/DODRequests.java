/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: Apr 26, 2012
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
package gov.va.med.imaging.router.commands.mbean;

import gov.va.med.imaging.ImagingMBean;

import java.lang.management.ManagementFactory;
import java.util.Hashtable;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Logger;

/**
 * @author VHAISWWERFEJ
 *
 */
public class DODRequests 
implements DODRequestsMBean
{
	private final static Logger logger = Logger.getLogger(DODRequests.class);
	
	private long totalDodPatientArtifactRequests;
	private long totalDodExamRequests;
	private long nonCorrelatedDodPatientArtifactRequests;
	private long nonCorrelatedDodExamRequests;

	public DODRequests()
	{
		super();
		totalDodExamRequests = 0L;
		totalDodPatientArtifactRequests = 0L;
		nonCorrelatedDodExamRequests = 0L;
		nonCorrelatedDodPatientArtifactRequests = 0L;
	}
	
	/* (non-Javadoc)
	 * @see gov.va.med.imaging.router.commands.mbean.DODRequestsMBean#getTotalDodPatientArtifactRequests()
	 */
	@Override
	public long getTotalDodPatientArtifactRequests()
	{
		return totalDodPatientArtifactRequests;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.router.commands.mbean.DODRequestsMBean#getTotalDodExamRequests()
	 */
	@Override
	public long getTotalDodExamRequests()
	{
		return totalDodExamRequests;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.router.commands.mbean.DODRequestsMBean#getNonCorrelatedDodPatientArtifactRequests()
	 */
	@Override
	public long getNonCorrelatedDodPatientArtifactRequests()
	{
		return nonCorrelatedDodPatientArtifactRequests;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.router.commands.mbean.DODRequestsMBean#getDodNonCorrelatedDodExamRequests()
	 */
	@Override
	public long getNonCorrelatedDodExamRequests()
	{
		return nonCorrelatedDodExamRequests;
	}

	public void setTotalDodPatientArtifactRequests(
			long totalDodPatientArtifactRequests)
	{
		this.totalDodPatientArtifactRequests = totalDodPatientArtifactRequests;
	}

	public void setTotalDodExamRequests(long totalDodExamRequests)
	{
		this.totalDodExamRequests = totalDodExamRequests;
	}

	public void setNonCorrelatedDodPatientArtifactRequests(
			long nonCorrelatedDodPatientArtifactRequests)
	{
		this.nonCorrelatedDodPatientArtifactRequests = nonCorrelatedDodPatientArtifactRequests;
	}

	public void setNonCorrelatedDodExamRequests(long nonCorrelatedDodExamRequests)
	{
		this.nonCorrelatedDodExamRequests = nonCorrelatedDodExamRequests;
	}
	
	public void incrementNonCorrelatedDodExamRequests()
	{
		this.nonCorrelatedDodExamRequests++;
	}
	
	public void incrementNonCorrelatedDodPatientArtifactRequests()
	{
		this.nonCorrelatedDodPatientArtifactRequests++;
	}
	
	public void incrementTotalDodExamRequests()
	{
		this.totalDodExamRequests++;
	}
	
	public void incrementTotalDodPatientArtifactRequests()
	{
		this.totalDodPatientArtifactRequests++;
	}

	private static ObjectName dodRequestsMBeanName = null;
	
	public static DODRequests dodRequests = null;
	public synchronized static DODRequests getRoiCommandsStatistics()
	{
		if(dodRequests == null)
		{
			dodRequests = new DODRequests();
			registerMBeanServer();
		}
		return dodRequests;
	}
	
	private static synchronized void registerMBeanServer()
	{
		if(dodRequestsMBeanName == null)
		{
			logger.info("Registering DOD Requests with JMX");
			try
			{
	            // add statistics
				MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
				Hashtable<String, String> mBeanProperties = new Hashtable<String, String>();
				mBeanProperties.put( "type", "DODRequests" );
				mBeanProperties.put( "name", "Statistics");
				dodRequestsMBeanName = new ObjectName(ImagingMBean.VIX_MBEAN_DOMAIN_NAME, mBeanProperties);
				mBeanServer.registerMBean(dodRequests, dodRequestsMBeanName);
			}
			catch(Exception ex)
			{
				logger.error("Error registering DOD Requests with JMX", ex);
			}
		}
	}
}
