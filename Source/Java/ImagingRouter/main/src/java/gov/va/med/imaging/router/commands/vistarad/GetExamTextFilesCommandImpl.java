/**
 * 
  Package: MAG - VistA Imaging
  WARNING: Per VHA Directive 2004-038, this routine should not be modified.
  Date Created: May 6, 2009
  Site Name:  Washington OI Field Office, Silver Spring, MD
  Developer:  vhaiswwerfej
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
package gov.va.med.imaging.router.commands.vistarad;

import gov.va.med.imaging.ImageURN;
import gov.va.med.imaging.StudyURN;
import gov.va.med.imaging.channels.ByteStreamPump;
import gov.va.med.imaging.core.interfaces.exceptions.ConnectionException;
import gov.va.med.imaging.core.interfaces.exceptions.MethodException;
import gov.va.med.imaging.exchange.business.vistarad.Exam;
import gov.va.med.imaging.exchange.business.vistarad.ExamImage;
import gov.va.med.imaging.router.facade.ImagingContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This command requests all of the text files for a given exam and returns them in a single stream with a 
 * delimiter.
 *  
 * If cacheOnly is true then the command will not write any data and will return the number of bytes that 
 * were retrieved from a data source (not cache).
 * 
 * @author vhaiswwerfej
 *
 */
public class GetExamTextFilesCommandImpl 
extends AbstractExamImageCommandImpl<Long> 
{
	private static final long serialVersionUID = 5222417191513646899L;
	
	private final static String txtFileStreamDelimiter = "-------------------------";
	
	private final StudyURN studyUrn;
	private final OutputStream outStream;
	private final boolean cacheOnly;
	
	public GetExamTextFilesCommandImpl(StudyURN studyUrn, OutputStream outStream)
	{
		this.studyUrn = studyUrn;
		this.outStream = outStream;
		this.cacheOnly = false;
	}
	
	public GetExamTextFilesCommandImpl(StudyURN studyUrn)
	{
		this.studyUrn = studyUrn;
		this.outStream = null;
		this.cacheOnly = true;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.commands.vistarad.AbstractExamCommandImpl#areClassSpecificFieldsEqual(java.lang.Object)
	 */
	@Override
	protected boolean areClassSpecificFieldsEqual(Object obj) 
	{
		// Perform cast for subsequent tests
		final GetExamTextFilesCommandImpl other = (GetExamTextFilesCommandImpl) obj;
		
		boolean allEqual = true;
		allEqual = allEqual && areFieldsEqual(this.studyUrn, other.studyUrn);
		allEqual = allEqual && areFieldsEqual(this.outStream, other.outStream);
		
		return allEqual;
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#callSynchronouslyInTransactionContext()
	 */
	@Override
	public Long callSynchronouslyInTransactionContext() 
	throws MethodException, ConnectionException 
	{
		getLogger().info("RouterImpl.GetExamTextFilesCommandImpl(" + studyUrn.toString() + ")");
		Exam exam = getFullyLoadedExam(getStudyUrn());
		// exam should be fully loaded
		long totalBytes = 0;
		int successfulTxtFiles = 0;
		int failedTxtFiles = 0;
		for(ExamImage examImage : exam.getImages())
		{
			//ExamImage examImage = exam.getImages().get(imageUrnString);
			try
			{
				String imageUrnString = examImage.getImageUrn().toString();
				ImageURN imageUrn = examImage.getImageUrn();
				getLogger().info("Retrieving text file for image '" + imageUrnString + "'.");
				
				if(cacheOnly)
				{
					totalBytes += ImagingContext.getRouter().getExamTextFileByImageUrn(imageUrn);
					getLogger().info("Finished retrieving txt file for image '" + imageUrnString + "'.");
					successfulTxtFiles++;
				}
				else
				{
					ByteArrayOutputStream output = new ByteArrayOutputStream();					
					totalBytes += ImagingContext.getRouter().getExamTextFileByImageUrn(imageUrn, null, output);
					
					ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
					ByteStreamPump pump = ByteStreamPump.getByteStreamPump();
					try
					{
						pump.xfer(input, outStream);
						outputDelimiter(imageUrnString);
						successfulTxtFiles++;
					}
					catch(IOException ioX)
					{
						getLogger().error(ioX);
						throw new MethodException("Error writing TXT file to output stream", ioX);
					}					
				}						
			}
			catch(MethodException mX)
			{
				String msg = "Exception retrieving txt file";
				getLogger().error(msg, mX);
				failedTxtFiles++;
			}
			catch(ConnectionException cX)
			{
				String msg = "Exception retrieving txt file";
				getLogger().error(msg, cX);
				failedTxtFiles++;
			}
		}
		getLogger().info("Completed processing exam '" + studyUrn.toString() + 
				"' text files, processed '" + successfulTxtFiles + "' files successfully, failed to process '" + 
				failedTxtFiles + "' text files. Wrote '" + totalBytes + "' total bytes");
		return totalBytes;
	}
	
	private void outputDelimiter(String imageUrnString)
	throws IOException
	{
		if(outStream != null)
		{
			StringBuilder sb = new StringBuilder();
			sb.append(imageUrnString);
			sb.append("\n");
			sb.append(txtFileStreamDelimiter);
			sb.append("\n");
			
			
			outStream.write(sb.toString().getBytes());
		}
	}

	/* (non-Javadoc)
	 * @see gov.va.med.imaging.core.router.AbstractCommandImpl#parameterToString()
	 */
	@Override
	protected String parameterToString() 
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append(this.getStudyUrn());
		
		return sb.toString();
	}

	/**
	 * @return the studyUrn
	 */
	public StudyURN getStudyUrn() 
	{
		return studyUrn;
	}

	/**
	 * @return the outStream
	 */
	public OutputStream getOutStream() 
	{
		return outStream;
	}

	/**
	 * @return the cacheOnly
	 */
	public boolean isCacheOnly() {
		return cacheOnly;
	}
}
