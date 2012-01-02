package uk.co.harcourtprogramming.mewler.servermesasges;

import uk.co.harcourtprogramming.internetrelaycats.MessageTokeniser;

abstract public class AbstractIrcMessage
{
	public final User origin;

	protected AbstractIrcMessage(String origin)
	{
		this.origin = origin == null ? null : new User(origin);
	}

	public static AbstractIrcMessage parse(String input, String asNick)
	{
		MessageTokeniser tokeniser = new MessageTokeniser(input);

		final String origin;
		if (tokeniser.startsWith(":"))
		{
			tokeniser.consume(":");
			origin = tokeniser.nextToken(' ');
		}
		else
		{
			origin = null;
		}

		final String type = tokeniser.nextToken(' ');

		try
		{
			final int replyNumber = Integer.parseInt(type);
			return new IrcResponseCode(origin, replyNumber, tokeniser);
		}
		catch (NumberFormatException ex)
		{
			final IrcMessageTypeEnum typeEnum;
			try
			{
				typeEnum = IrcMessageTypeEnum.valueOf(type);
			}
			catch (IllegalArgumentException ex2)
			{
				return new IrcMessage(origin, type, tokeniser);
			}
			switch (typeEnum)
			{
				case PING:
					return new IrcPingMessage(origin, tokeniser);
				case PRIVMSG:
					return new IrcPrivmsg(origin, tokeniser, asNick);
				default:
					return new IrcMessage(origin, type, tokeniser);
			}
		}
	}
}
