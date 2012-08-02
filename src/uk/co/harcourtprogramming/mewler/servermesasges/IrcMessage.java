package uk.co.harcourtprogramming.mewler.servermesasges;

import uk.co.harcourtprogramming.mewler.MessageTokeniser;

public class IrcMessage extends AbstractIrcMessage
{
	private final String messageType;
	private final String payload;

	public IrcMessage(String origin, String messageType, MessageTokeniser payload)
	{
		super(origin);
		this.messageType = messageType;
		this.payload = payload.toString();
	}

	@Override
	public String toString()
	{
		if (origin != null)
		{
			return getMessageType() + " from " + origin + ": " + getPayload();
		}
		else
		{
			return getMessageType() + ": " + getPayload();
		}
	}

	/**
	 * @return the messageType
	 */
	public String getMessageType()
	{
		return messageType;
	}

	/**
	 * @return the payload
	 */
	public String getPayload()
	{
		return payload;
	}

}
