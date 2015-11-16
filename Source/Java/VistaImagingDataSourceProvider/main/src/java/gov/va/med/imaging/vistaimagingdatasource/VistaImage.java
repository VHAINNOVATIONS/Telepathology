/**
 * 
 */
package gov.va.med.imaging.vistaimagingdatasource;

import gov.va.med.imaging.exchange.business.ComparableUtil;
import gov.va.med.imaging.exchange.enums.ObjectOrigin;
import gov.va.med.imaging.exchange.enums.ObjectStatus;
import gov.va.med.imaging.protocol.vista.VistaTranslatorUtility;
import gov.va.med.imaging.protocol.vista.exceptions.VistaParsingException;
import gov.va.med.imaging.url.vista.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @author vhaiswbeckec
 * 
 * A VistaImage is a VistA Imaging only construct that represents an image in
 * the VistA Imaging database.
 * 
 * NOTE: the VistaImage class is not a VIX core value object and should never
 * find its way into that package, it should also never be used outside of the
 * Vista Data Source.
 */
public class VistaImage
implements Serializable, Comparable<VistaImage>
{
	private static final long serialVersionUID = 1L;

	private String ien;
	private String absFilename; // UNC/HTTP path of image (full path)
	private String fullFilename; // UNC/HTTP path of image (full path)
	private String bigFilename; // UNC/HTTP path of image (full path)
	private String description;
	private Date procedureDate;
	private String procedure;
	private int imgType; // image type (100 = study, 3 = radiology?)
	private String absLocation; // location of abstract (M = magnetic)
	private String fullLocation; // location of full image (m = magnetic)
	private String dicomSequenceNumberForDisplay;
	private String dicomImageNumberForDisplay;
	private String patientDFN; // not sure if we will be able to get this from
								// DOD - may not matter
	private String patientName;
	private String imageClass;
	private String siteAbbr;
	private String studyIen;
	private String groupIen;
	private String imageUid; // DICOM Image UID
	private String imageNumber; // DICOM Image sequence number
	private String qaMessage;
	private ObjectOrigin objectOrigin;
	private String imageModality;
	private String errorMessage;
	private String alienSiteNumber;
	private ObjectStatus imageStatus = ObjectStatus.NO_STATUS;
	private ObjectStatus imageViewStatus = ObjectStatus.NO_STATUS;
	private boolean sensitive = false;
	private Date documentDate = null;
	private Date captureDate = null;
	private boolean imageHasAnnotations = false;
	// if the image is associated with a progress note, indicates if it is resulted
	private String associatedNoteResulted = "";
	private int imageAnnotationStatus = 0;
	private String imageAnnotationStatusDescription = "";
	private String imagePackage = "";

	/**
	 * Create a VistaImage instance from a line from an RPC response
	 * 
     * An image string (line) looks something like:
     * 1752^\\ISW-IMGGOLDBACK\image1$\DM\00\17\DM001753.JPG^\\ISW-IMGGOLDBACK\image1$\DM\00\17\DM001753.ABS^CONSULT NURSE MEDICAL WOUND SPEC INPT^3010928^11^NOTE^09/28/2001^36^M^A^^^2^1^WAS^^^711^IMAGPATIENT1055,1055^CLIN^^^
     * 
	 * @param line
	 * @return
	 */
	public static VistaImage create(String line)
	throws VistaParsingException
	{
		if (line == null || line.trim().equals(""))
			return null;

		VistaImage image = new VistaImage();
		String[] pieces = StringUtils.Split(line, StringUtils.CARET);

		if(pieces.length < 22)
			throw new VistaParsingException("Expected at least 22 'pieces' when parsing an image line and only found " + pieces.length + ".");
		
    	// CTB 27Nov2009
		//image.ien = Base32ConversionUtility.base32Encode(pieces[1]);
		image.ien = pieces[1];
		image.fullFilename = pieces[2];
		image.absFilename = pieces[3];
		image.description = pieces[4];
		image.imgType = Integer.parseInt(pieces[6]);
		image.procedure = pieces[7];
		image.procedureDate = VistaTranslatorUtility.convertVistaDatetoDate(pieces[8]);
		image.absLocation = pieces[10];
		image.fullLocation = pieces[11];
		image.dicomSequenceNumberForDisplay = pieces[12];
		image.dicomImageNumberForDisplay = pieces[13];
		image.siteAbbr = pieces[16];
		image.qaMessage = pieces[17];
		image.bigFilename = pieces[18];
		image.patientDFN = pieces[19];
		image.patientName = pieces[20];
		image.imageClass = pieces[21];
		if(pieces.length >= 31)
		{
			image.captureDate = VistaTranslatorUtility.convertVistaDatetoDate(pieces[22]);
			image.documentDate = VistaTranslatorUtility.convertVistaDatetoDate(pieces[23]);
			// pieces[24] is the IEN of the group for the image
			// pieces[25] is the IEN of the first image in a group
			// pieces[26] is the Image type of the first image in the group
			image.sensitive = VistaTranslatorUtility.convertVistaBooleanToBoolean(pieces[28]);
			
			String viewStatusValue = pieces[29];
			if(viewStatusValue.length() > 0)
			{
				try
				{
					image.imageViewStatus = ObjectStatus.valueOf(Integer.parseInt(viewStatusValue));
				}
				catch(Exception ex)
				{}
			}
			String statusValue = pieces[30];
			if(statusValue.length() > 0)
			{
				try
				{
					image.imageStatus = ObjectStatus.valueOf(Integer.parseInt(statusValue));
				}
				catch(Exception ex)
				{}
			}
		}
		if(pieces.length >= 35)
		{
			String imageHasAnnotationsValue = pieces[31];
			if(imageHasAnnotationsValue.length() > 0)
			{
				try
				{
					image.imageHasAnnotations = (!("0".equals(imageHasAnnotationsValue)));
				}
				catch(Exception ex) {}
			}
			image.associatedNoteResulted = pieces[32].trim();
			String imageAnnotationStatusValue = pieces[33];
			if(imageAnnotationStatusValue.length() > 0)
			{
				try
				{
					image.imageAnnotationStatus = Integer.parseInt(imageAnnotationStatusValue);
				}
				catch(Exception ex) {}
			}
			image.imageAnnotationStatusDescription = pieces[34].trim();
			image.imagePackage = pieces[35].trim();
		}
		
		

		return image;
	}
	
	private VistaImage()
	{
	}

	public String getIen()
	{
		return this.ien;
	}
	
	/**
	 * This method is necessary because the IEN of the first image in a group might come from VistA as the group IEN, 
	 * not the actual image IEN. Setting this property here can be done so the proper Image object can be created 
	 * from this VistaImage object
	 * @param ien
	 */
	public void setIen(String ien)
	{
		this.ien = ien;
	}

	public String getAbsFilename()
	{
		return this.absFilename;
	}

	public String getFullFilename()
	{
		return this.fullFilename;
	}

	public String getBigFilename()
	{
		return this.bigFilename;
	}

	public String getDescription()
	{
		return this.description;
	}

	public Date getProcedureDate()
	{
		return this.procedureDate;
	}

	public String getProcedure()
	{
		return this.procedure;
	}

	public int getImgType()
	{
		return this.imgType;
	}

	public String getAbsLocation()
	{
		return this.absLocation;
	}

	public String getFullLocation()
	{
		return this.fullLocation;
	}

	public String getDicomSequenceNumberForDisplay()
	{
		return this.dicomSequenceNumberForDisplay;
	}

	public String getDicomImageNumberForDisplay()
	{
		return this.dicomImageNumberForDisplay;
	}

	public String getPatientDFN()
	{
		return this.patientDFN;
	}

	public String getPatientName()
	{
		return this.patientName;
	}

	public String getImageClass()
	{
		return this.imageClass;
	}

	public String getSiteAbbr()
	{
		return this.siteAbbr;
	}

	public String getStudyIen()
	{
		return this.studyIen;
	}

	public String getGroupIen()
	{
		return this.groupIen;
	}

	public String getImageUid()
	{
		return this.imageUid;
	}

	public String getImageNumber()
	{
		return this.imageNumber;
	}

	public String getQaMessage()
	{
		return this.qaMessage;
	}

	public ObjectOrigin getObjectOrigin()
	{
		return this.objectOrigin;
	}

	public String getImageModality()
	{
		return this.imageModality;
	}

	public String getErrorMessage()
	{
		return this.errorMessage;
	}

	public String getAlienSiteNumber()
	{
		return this.alienSiteNumber;
	}

	public static long getSerialVersionUID()
	{
		return serialVersionUID;
	}

	public ObjectStatus getImageStatus()
	{
		return imageStatus;
	}

	public ObjectStatus getImageViewStatus()
	{
		return imageViewStatus;
	}

	public boolean isSensitive()
	{
		return sensitive;
	}

	public Date getDocumentDate()
	{
		return documentDate;
	}

	public Date getCaptureDate()
	{
		return captureDate;
	}

	public boolean isImageHasAnnotations()
	{
		return imageHasAnnotations;
	}

	public String getAssociatedNoteResulted()
	{
		return associatedNoteResulted;
	}

	public int getImageAnnotationStatus()
	{
		return imageAnnotationStatus;
	}

	public String getImageAnnotationStatusDescription()
	{
		return imageAnnotationStatusDescription;
	}

	public String getImagePackage()
	{
		return imagePackage;
	}

	@Override
	public int compareTo(VistaImage that) 
	{
		int cumulativeCompare = 0;
		
		cumulativeCompare = ComparableUtil.compare(this.procedureDate, that.procedureDate, false);
		if(cumulativeCompare != 0)
			return cumulativeCompare;		
			
		cumulativeCompare = ComparableUtil.compare(this.studyIen, that.studyIen, true);
		if(cumulativeCompare != 0)
			return cumulativeCompare;
		
		cumulativeCompare = ComparableUtil.compare(this.groupIen, that.groupIen, true);
		if(cumulativeCompare != 0)
			return cumulativeCompare;
		
		return this.ien == null ? -1 : this.ien.compareTo(that.ien);
	}
}
