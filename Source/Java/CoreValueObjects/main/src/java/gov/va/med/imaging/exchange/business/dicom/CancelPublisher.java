/**
 * 
 */
package gov.va.med.imaging.exchange.business.dicom;

import java.util.Observable;

/**
 * @author vhaiswpeterb
 *
 */
@Deprecated
public class CancelPublisher extends Observable {

	/**
	 * 
	 */
	public CancelPublisher() {
		// TODO Auto-generated constructor stub
	}

	
	public void publish(Object obj){
        this.setChanged();
        this.notifyObservers(obj);
        //this.clearChanged();
    }

}
