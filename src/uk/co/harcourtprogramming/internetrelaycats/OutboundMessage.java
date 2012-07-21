package uk.co.harcourtprogramming.internetrelaycats;

public class OutboundMessage
{
	private final String target;
	private final String message;
	private final String nick;
	private final boolean isAction;

	OutboundMessage(String target, String message, String nick, boolean isAction)
	{
		this.target = target;
		this.message = message;
		this.nick = nick;
		this.isAction = isAction;
	}

	public String getMessage()
	{
		return message;
	}

	public String getTarget()
	{
		return target;
	}

	public String getNick()
	{
		return nick;
	}

	public boolean isAction()
	{
		return isAction;
	}

	public OutboundMessage dervive(String message)
	{
		return new OutboundMessage(target, message, nick, isAction);
	}

	public OutboundMessage dervive(String message, String traget)
	{
		return new OutboundMessage(target, message, nick, isAction);
	}
}
