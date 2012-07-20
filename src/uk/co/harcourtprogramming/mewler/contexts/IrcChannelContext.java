package uk.co.harcourtprogramming.mewler.contexts;

import java.util.SortedSet;
import java.util.TreeSet;
import uk.co.harcourtprogramming.mewler.IrcCommands;
import uk.co.harcourtprogramming.mewler.IrcConnection;

public class IrcChannelContext extends IrcContext
{
	private final String name;
	private final SortedSet<String> users = new TreeSet<String>();

	private String topic = null;

	public IrcChannelContext(IrcConnection connection, String name)
	{
		super(connection);
		this.name = name;
	}

	public void part()
	{
		getConnection().part(name);
	}

	public void setTopic(String topic)
	{
		sendCommand(IrcCommands.TOPIC, name, topic);
	}

	public void message(String message)
	{
		getConnection().message(name, message);
	}

	public void act(String action)
	{
		getConnection().act(name, action);
	}
}
