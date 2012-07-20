package uk.co.harcourtprogramming.mewler.contexts;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import uk.co.harcourtprogramming.mewler.IrcCommands;
import uk.co.harcourtprogramming.mewler.IrcConnection;

abstract class IrcContext
{
	private final static IrcContextApiToken token = new IrcContextApiToken();

	private final IrcConnection connection;
	private final Set<IrcContextHandler> handlers;

	protected IrcContext(IrcConnection connection)
	{
		this.connection = connection;
		this.handlers = new HashSet<IrcContextHandler>();
	}

	public Set<? extends IrcContextHandler> getHandlers()
	{
		synchronized (handlers)
		{
			return Collections.unmodifiableSet(handlers);
		}
	}

	public void addHandler(IrcContextHandler handler)
	{
		synchronized (handlers)
		{
			handlers.add(handler);
		}
	}

	public boolean removeHandler(IrcContextHandler handler)
	{
		synchronized (handlers)
		{
			return handlers.remove(handler);
		}
	}

	public final IrcConnection getConnection()
	{
		return connection;
	}

	final protected void sendCommand(IrcCommands cmd, Object... params)
	{
		String mess = IrcCommands.createCommandString(cmd, params);
		connection.sendString(token, mess);
	}
}
