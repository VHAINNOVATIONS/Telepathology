package gov.va.med.imaging.exchange.business;

import gov.va.med.MockDataGenerationField;
import gov.va.med.imaging.GUID;
import gov.va.med.imaging.exchange.enums.ObjectOrigin;

import java.io.Serializable;
import java.util.*;

public class Series
implements Serializable, Iterable<Image>
{
	private static final long serialVersionUID = -7467740571635450273L;
	@MockDataGenerationField(pattern=MockDataGenerationField.UID_PATTERN)
	private String seriesUid;
	@MockDataGenerationField(pattern="[a-z0-9]{6,6}")
	private String seriesIen;
	@MockDataGenerationField(pattern="[0-9]{1,3}")
	private String seriesNumber;
	@MockDataGenerationField(pattern="[A-Z]{2,2}")
	private String modality; // string representation of a modality (CT, CR, MR, etc)
	@MockDataGenerationField(componentValueType="gov.va.med.imaging.exchange.business.Image")
	private SortedSet<Image> images = new TreeSet<Image>(new ImageComparator());
	@MockDataGenerationField()
	private ObjectOrigin objectOrigin;
	
	/**
	 * @param studyIen
	 * @param defaultSeriesNumber
	 * @return
	 */
	public static Series create(ObjectOrigin objectOrigin, String seriesIen, String seriesNumber)
	{
		Series series = new Series();
		
		series.setObjectOrigin(objectOrigin);
		series.setSeriesIen(seriesIen);
		series.setSeriesNumber(seriesNumber);
		
		return series;
	}
	
	public Series()
	{
		//images = new TreeSet<Image>();
		seriesUid = seriesIen = seriesNumber = modality = "";
		objectOrigin = ObjectOrigin.VA;
	}
	
	public int getImageCount()
	{
		return this.images.size();
	}
	
	@Deprecated
	public Set<Image> getImages() 
	{
		return Collections.unmodifiableSet(images);
	}
	
	public void replaceImage(Image oldImage, Image newImage)
	{
		images.remove(oldImage);
		images.add(newImage);
	}
	
	/**
	 * Add image to the Series children.
	 * @param image
	 */
	public void addImage(Image image) 
	{
		synchronized(images)
		{
			images.add(image);
		}
	}

	/**
	 * Add all of the images in the given Set to the Series children.
	 * @param images
	 */
	public void addImages(SortedSet<Image> images) 
	{
		synchronized(this.images)
		{
			this.images.addAll(images);
		}
	}

	@Override
	public Iterator<Image> iterator()
	{
		return images.iterator();
	}

	/**
	 * @return the seriesIen
	 */
	public String getSeriesIen() {
		return seriesIen;
	}

	/**
	 * @param seriesIen the seriesIen to set
	 */
	public void setSeriesIen(String seriesIen) {
		this.seriesIen = seriesIen;
	}

	/**
	 * @return the seriesNumber
	 */
	public String getSeriesNumber() {
		return seriesNumber;
	}

	/**
	 * @param seriesNumber the seriesNumber to set
	 */
	public void setSeriesNumber(String seriesNumber) {
		this.seriesNumber = seriesNumber;
	}

	/**
	 * @return the seriesUid
	 */
	public String getSeriesUid() {
		return seriesUid;
	}

	/**
	 * @param seriesUid the seriesUid to set
	 */
	public void setSeriesUid(String seriesUid) {
		this.seriesUid = seriesUid;
	}
	
	public static Series createMockSeries(String seriesNumber) {
		Series series = new  Series();
		series.seriesIen = new GUID().toShortString();
		series.seriesUid = new GUID().toShortString();
		series.seriesNumber = seriesNumber;
		return series;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String output = "Series Details [" + this.seriesIen + "] \t Series UID [" + this.seriesUid + "]\t Series Number [" + this.seriesNumber + "]\n" + 
			"Contains " + images.size() + " images\n";
		Iterator<Image> imageIter = images.iterator();
		while(imageIter.hasNext()) {
			Image image = imageIter.next();
			output += "\t[" + (image == null ? "<null image>" : image.getIen()) + 
				"] [" + (image == null ? "<null image>" : image.getImageUid()) + 
				"] \t Image Number [" + (image == null ? "<null image>" : image.getImageNumber()) + 
				"] \t Display Seq Number [" + (image == null ? "<null image>" : image.getDicomSequenceNumberForDisplay()) + 
				"] \n";
		}
		
		return output;
	}

	/**
	 * @return the objectOrigin
	 */
	public ObjectOrigin getObjectOrigin() {
		return objectOrigin;
	}

	/**
	 * @param objectOrigin the objectOrigin to set
	 */
	public void setObjectOrigin(ObjectOrigin objectOrigin) {
		this.objectOrigin = objectOrigin;
	}

	/**
	 * Returns the string representation of a modality (CT, CR, MR, etc)
	 */
	public String getModality() {
		return modality;
	}

	/**
	 * @param modality Sets the string representation of a modality (CT, CR, MR, etc)
	 */
	public void setModality(String modality) {
		this.modality = modality;
	}

}
