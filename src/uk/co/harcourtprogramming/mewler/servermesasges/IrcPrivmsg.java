package uk.co.harcourtprogramming.mewler.servermesasges;
import uk.co.harcourtprogramming.mewler.MessageTokeniser;
public class IrcPrivmsg extends AbstractIrcMessage
{
	public final String channel;
	public final String message;
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
			switch (PrivmsgExtensionCommands.valueOf(payload.nextToken(' ')))
			{
				case ACTION:
					action = true;
					break;
				default:
					action = false;
			}
			message = payload.nextToken('\u0001');
		}
		else
		{
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
