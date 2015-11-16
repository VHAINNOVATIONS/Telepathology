package gov.va.med.imaging.exchange.business;

public class WelcomeMessage 
{
	private String messageText;

	
	public WelcomeMessage(String messageText) {
		this.messageText = messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	public String getMessageText() {
		return messageText;
	}
}
