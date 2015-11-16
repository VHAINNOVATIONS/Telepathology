package gov.va.med.imaging.exchange.business.dicom;

/**
 * This class holds the purge delays for each of the Importer work item statuses
 * 
 * A value of 0 indicates immediate purge availablity.
 * 
 * A value of -1 indicates never purge
 * 
 * @author vhaiswlouthj
 *
 */
public class ImporterPurgeDelays 
{
	private int StagingPurgeDelayInDays = 7;
	private int NewPurgeDelayInDays = -1;
	private int FailedStagingPurgeDelayInDays = 0;
	private int FailedDirectImportStagingPurgeDelayInDays = 0;
	private int CancelledStagingPurgeDelayInDays = 0;
	private int CancelledDirectImportStagingPurgeDelayInDays = 0;
	private int InReconciliationPurgeDelayInDays = -1;
	private int ReadyForImportPurgeDelayInDays = -1;
	private int ImportingPurgeDelayInDays = -1;
	private int ImportCompletePurgeDelayInDays = 14;
	private int FailedImportPurgeDelayInDays = 30;

	public int getStagingPurgeDelayInDays() {
		return StagingPurgeDelayInDays;
	}
	public void setStagingPurgeDelayInDays(int stagingPurgeDelayInDays) {
		StagingPurgeDelayInDays = stagingPurgeDelayInDays;
	}
	public int getNewPurgeDelayInDays() {
		return NewPurgeDelayInDays;
	}
	public void setNewPurgeDelayInDays(int newPurgeDelayInDays) {
		NewPurgeDelayInDays = newPurgeDelayInDays;
	}
	public int getFailedStagingPurgeDelayInDays() {
		return FailedStagingPurgeDelayInDays;
	}
	public void setFailedStagingPurgeDelayInDays(int failedStagingPurgeDelayInDays) {
		FailedStagingPurgeDelayInDays = failedStagingPurgeDelayInDays;
	}
	public int getFailedDirectImportStagingPurgeDelayInDays() {
		return FailedDirectImportStagingPurgeDelayInDays;
	}
	public void setFailedDirectImportStagingPurgeDelayInDays(
			int failedDirectImportStagingPurgeDelayInDays) {
		FailedDirectImportStagingPurgeDelayInDays = failedDirectImportStagingPurgeDelayInDays;
	}
	public int getCancelledStagingPurgeDelayInDays() {
		return CancelledStagingPurgeDelayInDays;
	}
	public void setCancelledStagingPurgeDelayInDays(
			int cancelledStagingPurgeDelayInDays) {
		CancelledStagingPurgeDelayInDays = cancelledStagingPurgeDelayInDays;
	}
	public int getCancelledDirectImportStagingPurgeDelayInDays() {
		return CancelledDirectImportStagingPurgeDelayInDays;
	}
	public void setCancelledDirectImportStagingPurgeDelayInDays(
			int cancelledDirectImportStagingPurgeDelayInDays) {
		CancelledDirectImportStagingPurgeDelayInDays = cancelledDirectImportStagingPurgeDelayInDays;
	}
	public int getInReconciliationPurgeDelayInDays() {
		return InReconciliationPurgeDelayInDays;
	}
	public void setInReconciliationPurgeDelayInDays(
			int inReconciliationPurgeDelayInDays) {
		InReconciliationPurgeDelayInDays = inReconciliationPurgeDelayInDays;
	}
	public int getReadyForImportPurgeDelayInDays() {
		return ReadyForImportPurgeDelayInDays;
	}
	public void setReadyForImportPurgeDelayInDays(int readyForImportPurgeDelayInDays) {
		ReadyForImportPurgeDelayInDays = readyForImportPurgeDelayInDays;
	}
	public int getImportingPurgeDelayInDays() {
		return ImportingPurgeDelayInDays;
	}
	public void setImportingPurgeDelayInDays(int importingPurgeDelayInDays) {
		ImportingPurgeDelayInDays = importingPurgeDelayInDays;
	}
	public int getImportCompletePurgeDelayInDays() {
		return ImportCompletePurgeDelayInDays;
	}
	public void setImportCompletePurgeDelayInDays(int importCompletePurgeDelayInDays) {
		ImportCompletePurgeDelayInDays = importCompletePurgeDelayInDays;
	}
	public int getFailedImportPurgeDelayInDays() {
		return FailedImportPurgeDelayInDays;
	}
	public void setFailedImportPurgeDelayInDays(int failedImportPurgeDelayInDays) {
		FailedImportPurgeDelayInDays = failedImportPurgeDelayInDays;
	}
}
