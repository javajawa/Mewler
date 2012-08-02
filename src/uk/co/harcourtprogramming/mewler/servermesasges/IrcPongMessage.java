package uk.co.harcourtprogramming.mewler.servermesasges;

import uk.co.harcourtprogramming.mewler.MessageTokeniser;

public class IrcPongMessage extends AbstractIrcMessage
{
	private final String nonce;

	public IrcPongMessage(String origin, MessageTokeniser payload)
	{
		super(origin);
		payload.consumeWhitespace();

		payload.consume(payload.nextToken(':'));

		nonce = payload.toString();
	}

	public String getNonce()
	{
		return nonce;
	}

	@Override
	public String toString()
	{
		return "PONG: " + nonce;
	}

}