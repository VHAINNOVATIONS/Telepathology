package gov.va.med.imaging.core.router.commands;

import gov.va.med.imaging.access.TransactionLogWriter;

public class GetTransactionLogEntriesByTransactionIdCommandImpl
extends GetTransactionLogEntriesCommandImpl
{
	private static final long serialVersionUID = 4935235087321711848L;

	public GetTransactionLogEntriesByTransactionIdCommandImpl(TransactionLogWriter transactionLogWriter, String transactionId)
	{
		super(transactionLogWriter, "transactionId", transactionId);
	}

}
