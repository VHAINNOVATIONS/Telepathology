package gov.va.med.imaging.access.je.taglib;

import gov.va.med.imaging.access.TransactionLogEntry;

public interface TransactionLogEntryParent
{

	public abstract TransactionLogEntry getTransactionLogEntry();
}