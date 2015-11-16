package gov.va.med.imaging.access.je.taglib;

import gov.va.med.imaging.access.TransactionLogEntry;

public interface TransactionLogEnumerationParent
{

	public abstract TransactionLogEntry getCurrentTransactionLogEntry();
}