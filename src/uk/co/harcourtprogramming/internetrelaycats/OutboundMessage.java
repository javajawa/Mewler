package uk.co.harcourtprogramming.internetrelaycats;

public class OutboundMessage
{
	private final String target;
	private final String message;
	private final boolean isAction;

	OutboundMessage(String target, String message, boolean isAction)
	{
		this.target = target;
		this.message = message;
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

	public boolean isAction()
	{
		return isAction;
	}

	public OutboundMessage dervive(String message)
	{
		return new OutboundMessage(target, message, isAction);
	}

	public OutboundMessage dervive(String message, String traget)
	{
		return new OutboundMessage(target, message, isAction);
	}
}
