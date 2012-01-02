package uk.co.harcourtprogramming.mewler.servermesasges;

import uk.co.harcourtprogramming.internetrelaycats.MessageTokeniser;
import uk.co.harcourtprogramming.mewler.IrcCommands;
/**
 *
 * @author Benedict
 */
public class IrcPingMessage extends AbstractIrcMessage
{
	private final String nonce;

	public IrcPingMessage(String origin, MessageTokeniser payload)
	{
		super(origin);
		payload.consumeWhitespace();

		if (!payload.startsWith(":"))
			throw new IllegalArgumentException("Invalid data for PING message");

		payload.consume(":");
		nonce = payload.toString();
	}

	public String reply()
	{
		return IrcCommands.createCommandString(IrcCommands.PONG, nonce);
	}

	@Override
	public String toString()
	{
		return "PING: " + nonce;
	}

}
