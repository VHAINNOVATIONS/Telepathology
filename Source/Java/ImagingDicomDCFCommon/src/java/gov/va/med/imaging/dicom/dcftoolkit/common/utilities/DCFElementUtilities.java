/**
 * 
 */
package gov.va.med.imaging.dicom.dcftoolkit.common.utilities;

import gov.va.med.imaging.dicom.common.interfaces.IDicomDataSet;

import java.util.Iterator;
import java.util.Vector;

import com.lbs.DCS.AttributeTag;
import com.lbs.DCS.DicomDataSet;
import com.lbs.DCS.DicomElement;

/**
 * @author vhaiswpeterb
 *
 */
public class DCFElementUtilities {

	
	public static void removeAllElementsInGroup(int group, IDicomDataSet dds){
		
		Vector<AttributeTag> list = new Vector<AttributeTag>();
		DicomDataSet dataset = (DicomDataSet)dds.getDicomDataSet();
		int count = dataset.count();
		for(int i=0; i<count; i++){
			DicomElement e = dataset.getElementAt(i);
			if(e.tag().group() == group){
				list.add(e.tag());
			}
			else{
				if(e.tag().group() > group){
					break;
				}
			}
		}
		
		Iterator<AttributeTag> iter = list.iterator();
		while(iter.hasNext()){
			AttributeTag tag = (AttributeTag)iter.next();
			dataset.removeElement(tag);
		}
		
		dds.setDicomDataSet(dataset);
	}

}
