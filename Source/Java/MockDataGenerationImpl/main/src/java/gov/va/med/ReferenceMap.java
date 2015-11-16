/**
 * 
 */
package gov.va.med;

import java.util.HashMap;
import java.util.Map;

/**
 * A map of references to their values.
 * This map is passed along with the instance loading parameters,
 * as instances of business objects are created they may need to 
 * 
 * @author vhaiswbeckec
 *
 */
public class ReferenceMap
{
	private static final long serialVersionUID = 1L;
	private final ReferenceMap parent;
	private final Map<ReferenceKey, Object> referenceMap = new HashMap<ReferenceKey, Object>();
	
	// a list of strings to be used as reference keys
	public enum ReferenceKey
	{
		OBJECT_ORIGIN, 
		SITE_NUMBER, 
		ROUTING_TOKEN, 
		PATIENT, PATIENT_NAME, PATIENT_ICN, PATIENT_DFN, PATIENT_SSN,
		STUDY, STUDY_URN, STUDY_IEN, STUDY_FILTER, 
		SERIES, SERIES_IEN, SERIES_NUMBER, 
		IMAGE, IMAGE_URN, IMAGE_IEN, 
		EXAM, EXAM_ID, EXAM_IMAGE, EXAM_SITE, EXAM_IMAGES, 
		RAW_HEADER1, RAW_HEADER2, 
		ACTIVE_EXAM, ACTIVE_EXAMS,   
		MODALITY, 
		IMAGE_ACCESS_LOG_EVENT, 
		PASSTHROUGH_PARAMETER, PASSTHROUGH_PARAMETER_TYPE
	}
	
	public static ReferenceMap createRoot()
	{
		return new ReferenceMap(null);
	}
	
	public ReferenceMap createChild()
	{
		return new ReferenceMap(this);
	}

	/**
	 * @param parent
	 */
	private ReferenceMap(ReferenceMap parent)
	{
		super();
		this.parent = parent;
	}
	
	public void putReference(ReferenceKey key, Object value)
	{
		referenceMap.put(key, value);
	}
	
	/**
	 * Recursively call up to the first ancestor that has the key value
	 * 
	 * @param key
	 * @return
	 */
	public Object getReference(ReferenceKey key)
	{
		return referenceMap.get(key) == null ? 
			(parent == null ? null : parent.getReference(key)) : 
			referenceMap.get(key);
	}
}
