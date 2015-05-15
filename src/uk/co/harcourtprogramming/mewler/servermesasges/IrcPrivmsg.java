package uk.co.harcourtprogramming.mewler.servermesasges;

import uk.co.harcourtprogramming.mewler.MessageTokeniser;

public class IrcPrivmsg extends AbstractIrcMessage
{
	/**
	 * <p>Name of the User or Channel this message came from</p>
	 *
	 * @todo Refactor to be "origin" or similar to make it clearer this may be a nick
	 */
	public final String channel;
	/**
	 * <p>String content of the message</p>
	 */
	public final String message;
	/**
	 * <p>The Client-to-Client protocol 'verb', if this is a CTCP message,
	 * NONE otherwise</p>
	 * @link PrivmsgExtensionCommands#NONE
	 */
	public final PrivmsgExtensionCommands command;
	/**
	 * <p>If this message is a CTCP command with 'verb' "ACTION"</p>
	 *
	 * @link PrivmsgExtensionCommands#ACTION
	 * @todo Deprecate this.
	 */
	public final boolean action;

	public IrcPrivmsg(String origin, MessageTokeniser payload, String selfNick)
	{
		super(origin);
		payload.consumeWhitespace();

		String target = payload.nextToken(' ');
		channel = (target.equalsIgnoreCase(selfNick) ? null : target);

		payload.consume(":");
		if (payload.startsWith("\u0001"))
		{
			payload.consume("\u0001");

			PrivmsgExtensionCommands commandTemp;
			final String commandString = payload.nextToken(' ');

			try
			{
				commandTemp = PrivmsgExtensionCommands.valueOf(commandString);
			}
			catch (IllegalArgumentException ignore)
			{
				commandTemp = PrivmsgExtensionCommands.INVALID;
			}
			command = commandTemp;

			// Set legacy flag for IRC 'Action'
			action = (command == PrivmsgExtensionCommands.ACTION);
			// Extract the message
			// TODO: This might need unescaping?
			message = payload.nextToken('\u0001');
		}
		else
		{
			command = PrivmsgExtensionCommands.NONE;
			action = false;
			message = payload.toString();
		}
	}

	@Override
	public String toString()
	{
		return origin + (channel != null ? " via " + channel : "") + " said " + message;
	}

}
