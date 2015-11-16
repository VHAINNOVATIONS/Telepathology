/**
 * 
 */
package gov.va.med.imaging.exchange.business.dicom;

import java.util.Observable;
import java.util.Observer;

/**
 * @author vhaiswpeterb
 *
 */
public class MoveCommandObserver implements Observer {

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	
	private boolean cancelMoveOperation = false;
	
	private boolean getDicomDataSetsDone = false;
	
	private boolean sendDicomDataSetsDone = false;
	

	/**
	 * @return the cancelMoveOperation
	 */
	public boolean isCancelMoveOperation() {
		return cancelMoveOperation;
	}


	/**
	 * @param cancelMoveOperation the cancelMoveOperation to set
	 */
	public void setCancelMoveOperation(boolean cancelMoveOperation) {
		this.cancelMoveOperation = cancelMoveOperation;
	}


	/**
	 * @return the getDicomDataSetsDone
	 */
	public boolean isGetDicomDataSetsDone() {
		return getDicomDataSetsDone;
	}


	/**
	 * @param getDicomDataSetsDone the getDicomDataSetsDone to set
	 */
	public void setGetDicomDataSetsDone(boolean getDicomDataSetsDone) {
		this.getDicomDataSetsDone = getDicomDataSetsDone;
	}


	/**
	 * @return the sendDicomDataSetsDone
	 */
	public boolean isSendDicomDataSetsDone() {
		return sendDicomDataSetsDone;
	}


	/**
	 * @param sendDicomDataSetsDone the sendDicomDataSetsDone to set
	 */
	public void setSendDicomDataSetsDone(boolean sendDicomDataSetsDone) {
		this.sendDicomDataSetsDone = sendDicomDataSetsDone;
	}


	@Override
	public void update(Observable o, Object arg) {
		this.cancelMoveOperation = true;
	}

}
